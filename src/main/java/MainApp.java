import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        TabPane tabs = new TabPane();

        // Original tabs
        ProfilesView profilesView = new ProfilesView();      // Tab 1
        ExercisesView exercisesView = new ExercisesView();   // Tab 2

        // 20251103 Added EquipmentView integration (new feature for Sprint 3)
        EquipmentView equipmentView = new EquipmentView();   // Tab 3

        // Create tabs
        Tab t1 = new Tab("Profiles", profilesView.getRoot());
        t1.setClosable(false);

        Tab t2 = new Tab("Exercises", exercisesView.getRoot());
        t2.setClosable(false);

        // 20251103 Added new tab for EquipmentView
        Tab t3 = new Tab("Equipment", equipmentView.getRoot());
        t3.setClosable(false);

        // 20251103 Added Equipment tab to the list
        tabs.getTabs().addAll(t1, t2, t3);

        // When profiles change, refresh the Exercises profile list
        profilesView.setOnAnyProfileChange(exercisesView::reloadProfiles);

        stage.setTitle("Fitness Planner – Sprint 3 GUI");
        stage.setScene(new Scene(tabs, 900, 520));
        stage.show();

        // Initial load for each view
        profilesView.refresh();
        exercisesView.reloadProfiles();

        // 20251103 Initial load for EquipmentView
        equipmentView.refresh();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
