import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.List;

public class WorkoutTableApp extends Application {

    @Override
    public void start(Stage stage) {
        TableView<WorkoutWithProfile> table = new TableView<>();

        TableColumn<WorkoutWithProfile, String> colProfile = new TableColumn<>("Profile Name");
        colProfile.setCellValueFactory(new PropertyValueFactory<>("profileName"));

        TableColumn<WorkoutWithProfile, String> colWorkout = new TableColumn<>("Workout Name");
        colWorkout.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<WorkoutWithProfile, Integer> colDay = new TableColumn<>("Day");
        colDay.setCellValueFactory(new PropertyValueFactory<>("dayOfWeek"));

        TableColumn<WorkoutWithProfile, Boolean> colActive = new TableColumn<>("Active");
        colActive.setCellValueFactory(new PropertyValueFactory<>("active"));

        table.getColumns().addAll(colProfile, colWorkout, colDay, colActive);

        // Load from DAO
        WorkoutDao dao = new WorkoutDao();
        List<WorkoutWithProfile> workouts = dao.listWithProfileByProfile(1); // Brandon’s ID
        table.getItems().addAll(workouts);

        VBox layout = new VBox(table);
        Scene scene = new Scene(layout, 600, 400);

        stage.setScene(scene);
        stage.setTitle("Workout List");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
