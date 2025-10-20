

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) {
        TabPane tabs = new TabPane();

        ProfilesView profilesView = new ProfilesView();                  // Tab 1
        ExercisesView exercisesView = new ExercisesView();               // Tab 2

        Tab t1 = new Tab("Profiles", profilesView.getRoot());
        t1.setClosable(false);

        Tab t2 = new Tab("Exercises", exercisesView.getRoot());
        t2.setClosable(false);

        tabs.getTabs().addAll(t1, t2);

        // When profiles change, refresh the Exercises profile list
        profilesView.setOnAnyProfileChange(exercisesView::reloadProfiles);

        stage.setTitle("Fitness Planner – Sprint 2 GUI");
        stage.setScene(new Scene(tabs, 900, 520));
        stage.show();

        // initial load
        profilesView.refresh();
        exercisesView.reloadProfiles();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
