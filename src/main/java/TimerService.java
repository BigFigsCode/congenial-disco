import javafx.application.Platform;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * TimerService
 *
 * Purpose:
 * A simple, predictable countdown engine that runs off the Java executor framework.
 * I wanted something that:
 *   1) Ticks once per second without freezing the JavaFX UI,
 *   2) Calls my UI update on the JavaFX Application Thread (safe for labels, etc.),
 *   3) Can be started, paused, reset, and adjusted cleanly,
 *   4) Shuts down safely when the app closes (no lingering background threads).
 *
 * How it works in plain English:
 * - When I call start(N), it stores N seconds and flips a flag that says “we’re running.”
 * - A single-threaded scheduler ticks every 1 second and reduces the remaining seconds.
 * - Each second, it posts the new value back to the UI using Platform.runLater.
 * - When the counter reaches 0, it stops and triggers onFinish (also on the FX thread).
 *
 * Threading notes:
 * - The scheduler is created once. I stop it for good in stop(), which I call from MainApp.stop().
 * - The AtomicBoolean 'running' is how I pause/resume without re-creating the scheduler task.
 */
public class TimerService {

    /** Called once per second with the current seconds remaining (on JavaFX thread). */
    private final Consumer<Integer> onTick;

    /** Called when the timer hits 0 (on JavaFX thread). */
    private final Runnable onFinish;

    /** A single background thread that does the timing. */
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    /** True if the timer is currently counting down. This lets me pause without killing the scheduler. */
    private final AtomicBoolean running = new AtomicBoolean(false);

    /** How many seconds are left right now. */
    private int secondsRemaining = 0;

    /** The value we last started from. Reset uses this. */
    private int initialSeconds = 0;

    public TimerService(Consumer<Integer> onTick, Runnable onFinish) {
        this.onTick = onTick;
        this.onFinish = onFinish;

        // On construction I want the UI to be in a known state (00:00).
        // I send 0 to whoever owns the label, but I do it on the FX thread to stay safe.
        fireTickOnFx(0);

        // Start a fixed-rate 1s ticker that will call tick(). It’s cheap and always ready.
        // Note: tick() checks 'running', so nothing “counts down” until start() flips the flag.
        scheduler.scheduleAtFixedRate(this::tick, 0, 1, TimeUnit.SECONDS);
    }

    /**
     * Start (or restart) a countdown from 'seconds'. If seconds <= 0, I treat it as a reset to 0.
     * I also remember the value for reset().
     */
    public void start(int seconds) {
        if (seconds <= 0) {
            resetTo(0);
            running.set(false);
            return;
        }
        initialSeconds = seconds;
        secondsRemaining = seconds;
        running.set(true);
        fireTickOnFx(secondsRemaining);
    }

    /**
     * Pause the countdown without clearing the current remaining value.
     * Useful when I want to resume later with the same time.
     */
    public void pause() {
        running.set(false);
    }

    /**
     * Reset back to the last started value. Does not automatically start running.
     * If I haven’t started yet, this simply shows 0.
     */
    public void reset() {
        running.set(false);
        resetTo(initialSeconds);
    }

    /**
     * Adjust the remaining time by delta seconds (negative allowed). Floors at 0.
     * I use this for quick “+10s” or “-10s” buttons while the timer is running or paused.
     */
    public void adjust(int delta) {
        secondsRemaining = Math.max(0, secondsRemaining + delta);
        fireTickOnFx(secondsRemaining);
    }

    /**
     * Permanently stop the scheduler thread. Call this once when the app closes.
     * If I forget to call this, the JVM might hang on exit because the executor is still alive.
     */
    public void stop() {
        running.set(false);
        scheduler.shutdownNow();
    }

    // ===== Internals below =====

    /** One logical “tick.” Runs on the scheduler thread every 1 second. */
    private void tick() {
        if (!running.get()) return;

        // Decrement, clamp at 0, and notify UI.
        secondsRemaining = Math.max(0, secondsRemaining - 1);
        fireTickOnFx(secondsRemaining);

        // If we hit zero, stop and fire the finish callback.
        if (secondsRemaining == 0) {
            running.set(false);
            if (onFinish != null) {
                Platform.runLater(onFinish);
            }
        }
    }

    /** Reset the display to a specific value and push it to the UI. */
    private void resetTo(int secs) {
        secondsRemaining = Math.max(0, secs);
        fireTickOnFx(secondsRemaining);
    }

    /** Safely notify the UI owner (label/spinner/etc.) from the JavaFX thread. */
    private void fireTickOnFx(int secs) {
        if (onTick != null) {
            Platform.runLater(() -> onTick.accept(secs));
        }
    }
}
