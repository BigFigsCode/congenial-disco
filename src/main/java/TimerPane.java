import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

/**
 * TimerPane
 *
 * Purpose:
 * A self-contained UI for work/rest style timing. I wanted a clean, compact layout
 * that fits as a tab in my main app without extra FXML or controllers.
 *
 * What the user sees:
 * - A large clock readout like "00:00" that always shows the remaining time.
 * - Two editable spinners for Work (sec) and Rest (sec). These accept direct typing
 *   and quick stepping. I clamp them in code to safe values.
 * - A toggle “Auto Rest.” If on, when Work hits 0, it automatically runs Rest.
 * - Start, Pause, Reset, and quick -10s / +10s to make adjustments on the fly.
 *
 * Wiring notes:
 * - TimerService does the background ticks. I pass it:
 *     (1) how to update the label each second, and
 *     (2) what to do when the timer finishes (optionally trigger rest).
 * - shutdown() is here so MainApp can cleanly stop the timer thread on exit.
 */
public class TimerPane extends VBox {

    /** Large label for the clock readout (mm:ss). */
    private final Label clock = new Label("00:00");

    /** Work duration (seconds). Range chosen to be practical for workouts. */
    private final Spinner<Integer> workSecs = new Spinner<>(5, 3600, 60, 5);

    /** Rest duration (seconds). Defaults longer than work for demo; easily changed. */
    private final Spinner<Integer> restSecs = new Spinner<>(5, 3600, 90, 5);

    /** If selected, the timer auto-starts rest when work hits zero. */
    private final ToggleButton useRest = new ToggleButton("Auto Rest");

    /** Transport + adjustment buttons. */
    private final Button start = new Button("Start");
    private final Button pause = new Button("Pause");
    private final Button reset = new Button("Reset");
    private final Button plus10 = new Button("+10s");
    private final Button minus10 = new Button("-10s");

    /** The timer engine (background thread + FX callbacks). */
    private final TimerService timer;

    public TimerPane() {
        // Layout spacing/padding keeps everything readable and aligned.
        setSpacing(16);
        setPadding(new Insets(16));
        setAlignment(Pos.TOP_LEFT);

        // Make the clock easy to read during a workout.
        clock.setFont(Font.font(48));

        // Top row: the big clock display.
        HBox clockRow = new HBox(clock);
        clockRow.setAlignment(Pos.CENTER_LEFT);

        // Middle row: work/rest inputs and the Auto Rest toggle.
        workSecs.setEditable(true);
        restSecs.setEditable(true);
        HBox settings = new HBox(12,
                new Label("Work (sec):"), workSecs,
                new Label("Rest (sec):"), restSecs,
                useRest
        );
        settings.setAlignment(Pos.CENTER_LEFT);

        // Bottom row: transport controls and the quick +/- 10s adjusters.
        HBox controls = new HBox(12, start, pause, reset, minus10, plus10);
        controls.setAlignment(Pos.CENTER_LEFT);

        // Connect the timer engine:
        // onTick -> update the label,
        // onFinish -> call a method (handleFinish) so we don't self-reference 'timer' in the initializer.
        timer = new TimerService(
                secs -> clock.setText(format(secs)),
                this::handleFinish
        );

        // Button behaviors in simple terms:
        start.setOnAction(e -> timer.start(safe(workSecs.getValue()))); // start from Work value
        pause.setOnAction(e -> timer.pause());                          // freeze where it is
        reset.setOnAction(e -> timer.reset());                          // go back to last start value
        plus10.setOnAction(e -> timer.adjust(+10));                     // add 10 seconds
        minus10.setOnAction(e -> timer.adjust(-10));                    // remove 10 seconds (floors at 0)

        // Assemble the rows in order.
        getChildren().addAll(clockRow, settings, controls);

        // Keep button sizes consistent so it looks clean.
        start.setPrefWidth(80);
        pause.setPrefWidth(80);
        reset.setPrefWidth(80);
        minus10.setPrefWidth(80);
        plus10.setPrefWidth(80);
    }

    /** Called by TimerService when a countdown hits zero. */
    private void handleFinish() {
        if (useRest.isSelected()) {
            int r = safe(restSecs.getValue());
            timer.start(r);
        }
    }

    /** Format seconds as mm:ss. I keep it simple and readable. */
    private static String format(int totalSeconds) {
        int m = totalSeconds / 60;
        int s = totalSeconds % 60;
        return String.format("%02d:%02d", m, s);
    }

    /** Defensive helper to avoid null spinner values and negative inputs. */
    private static int safe(Integer v) {
        return v == null ? 0 : Math.max(0, v);
    }

    /**
     * Called by MainApp.stop(). This is important to prevent the scheduler thread
     * from lingering after the window closes. One line, but necessary.
     */
    public void shutdown() {
        timer.stop();
    }
}
