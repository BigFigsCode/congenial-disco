import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

/**
 * MainApp
 *
 * What this file does:
 * - Creates a TabPane-based UI so everything lives in one window with simple navigation.
 * - Builds the three original tabs (Profiles, Exercises, Equipment) exactly as before.
 * - Adds a fourth tab called "Timer" that hosts my new TimerPane.
 * - Ensures that when the application closes, the timer’s background thread stops cleanly.
 *
 * Notes I’m keeping from the team:
 * - The “20251103” comments below were left by teammates to mark Sprint 3 work.
 *   I left those intact and added my own comments around them so it’s clear what I changed.
 */
public class MainApp extends Application {

    // I keep a reference to the timer tab’s root so I can shut it down in stop().
    private TimerPane timerPane;

    @Override
    public void start(Stage stage) {
        // Tab container for the whole app. This matches the design we already have.
        TabPane tabs = new TabPane();

        // Original tabs
        ProfilesView profilesView = new ProfilesView();      // Tab 1
        ExercisesView exercisesView = new ExercisesView();   // Tab 2

        // 20251103 Added EquipmentView integration (new feature for Sprint 3)
        EquipmentView equipmentView = new EquipmentView();   // Tab 3

        // Create tabs for the first three views.
        Tab t1 = new Tab("Profiles", profilesView.getRoot());
        t1.setClosable(false);

        Tab t2 = new Tab("Exercises", exercisesView.getRoot());
        t2.setClosable(false);

        // 20251103 Added new tab for EquipmentView
        Tab t3 = new Tab("Equipment", equipmentView.getRoot());
        t3.setClosable(false);

        // My addition: Timer tab.
        // Reasoning: The workout timer is part of our MVP. Putting it in the same TabPane
        // keeps the UX consistent and avoids extra windows.
        timerPane = new TimerPane();                         // Tab 4 content
        Tab t4 = new Tab("Timer", timerPane);                // TimerPane is a Node (VBox)
        t4.setClosable(false);

        // 20251103 Added Equipment tab to the list
        // I’m appending mine right after it so we end up with: Profiles | Exercises | Equipment | Timer
        tabs.getTabs().addAll(t1, t2, t3, t4);

        // When profiles change, refresh the Exercises profile list
        // (This coupling was already part of our UI flow and I’m keeping it.)
        profilesView.setOnAnyProfileChange(exercisesView::reloadProfiles);

        // Window title and initial size. The width comfortably fits the table-based tabs and the timer.
        stage.setTitle("Fitness Planner – Sprint 3 GUI");
        stage.setScene(new Scene(tabs, 900, 520));
        stage.show();

        // Initial load for each view so the tables/spinners have data right away.
        profilesView.refresh();
        exercisesView.reloadProfiles();

        // 20251103 Initial load for EquipmentView
        equipmentView.refresh();

        // TimerPane shows 00:00 at start by design; no explicit refresh needed here.
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        // Important: stop background threads before exit to prevent the app from hanging.
        // If the timer exists, ask it to shut down its scheduler thread.
        if (timerPane != null) {
            timerPane.shutdown();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
