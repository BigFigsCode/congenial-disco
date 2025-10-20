

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class ExercisesView {
    private final BorderPane root = new BorderPane();

    private final ComboBox<Profile> cbProfiles = new ComboBox<>();
    private final TableView<Exercise> table = new TableView<>();

    private final TextField tfName = new TextField();
    private final TextField tfMuscle = new TextField();
    private final TextField tfEquip = new TextField();
    private final ComboBox<String> cbDiff = new ComboBox<>();
    private final ComboBox<String> cbType = new ComboBox<>();

    private final ProfileDao profileDao = new ProfileDao();
    private final ExerciseDao exerciseDao = new ExerciseDao();

    public ExercisesView() {
        // Profile selector display
        cbProfiles.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Profile item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName() + " (id=" + item.getId() + ")");
            }
        });
        cbProfiles.setCellFactory(list -> new ListCell<>() {
            @Override protected void updateItem(Profile item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName() + " (id=" + item.getId() + ")");
            }
        });

        // Table columns
        TableColumn<Exercise, Integer> cId = new TableColumn<>("ID");
        cId.setCellValueFactory(p -> new javafx.beans.property.SimpleIntegerProperty(p.getValue().getId()).asObject());
        cId.setPrefWidth(60);
        TableColumn<Exercise, String> cName = new TableColumn<>("Name");
        cName.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().getName()));
        cName.setPrefWidth(180);
        TableColumn<Exercise, String> cMuscle = new TableColumn<>("Muscle");
        cMuscle.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().getMuscle()));
        cMuscle.setPrefWidth(140);
        TableColumn<Exercise, String> cEquip = new TableColumn<>("Equipment");
        cEquip.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().getEquipment()));
        cEquip.setPrefWidth(140);
        TableColumn<Exercise, String> cDiff = new TableColumn<>("Difficulty");
        cDiff.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().getDifficulty()));
        cDiff.setPrefWidth(100);
        TableColumn<Exercise, String> cType = new TableColumn<>("Type");
        cType.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().getType()));
        cType.setPrefWidth(80);

        table.getColumns().addAll(cId, cName, cMuscle, cEquip, cDiff, cType);

        // Form
        cbDiff.getItems().addAll("Easy", "Moderate", "Hard");
        cbDiff.getSelectionModel().selectFirst();
        cbType.getItems().addAll("REPS", "SECS");
        cbType.getSelectionModel().selectFirst();

        GridPane form = new GridPane();
        form.setHgap(10); form.setVgap(8); form.setPadding(new Insets(10));
        form.addRow(0, new Label("Profile:"), cbProfiles);
        form.addRow(1, new Label("Name:"), tfName, new Label("Muscle:"), tfMuscle);
        form.addRow(2, new Label("Equipment:"), tfEquip, new Label("Difficulty:"), cbDiff);
        form.addRow(3, new Label("Type:"), cbType);

        Button btnAdd = new Button("Add");
        Button btnUpdate = new Button("Update");
        Button btnDelete = new Button("Delete");
        HBox buttons = new HBox(8, btnAdd, btnUpdate, btnDelete);
        buttons.setPadding(new Insets(0,10,10,10));

        root.setTop(form);
        root.setCenter(table);
        root.setBottom(buttons);

        // Handlers
        cbProfiles.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> loadExercises(sel));
        table.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                tfName.setText(sel.getName());
                tfMuscle.setText(sel.getMuscle());
                tfEquip.setText(sel.getEquipment());
                cbDiff.getSelectionModel().select(sel.getDifficulty());
                cbType.getSelectionModel().select(sel.getType());
            }
        });

        btnAdd.setOnAction(e -> {
            Profile p = cbProfiles.getValue();
            if (p == null) { show("Select a profile first"); return; }
            String name = tfName.getText().trim();
            if (name.isEmpty()) { show("Exercise name required"); return; }
            Exercise ex = new Exercise(p.getId(), name, tfMuscle.getText().trim(), tfEquip.getText().trim(),
                    cbDiff.getValue(), cbType.getValue());
            int id = exerciseDao.insert(ex);
            if (id > 0) { loadExercises(p); clearForm(); }
            else show("Insert failed (duplicate name for this profile?)");
        });

        btnUpdate.setOnAction(e -> {
            Exercise sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { show("Select an exercise"); return; }
            Profile p = cbProfiles.getValue();
            if (p == null) { show("Select a profile first"); return; }

            sel.setProfileId(p.getId());
            sel.setName(tfName.getText().trim());
            sel.setMuscle(tfMuscle.getText().trim());
            sel.setEquipment(tfEquip.getText().trim());
            sel.setDifficulty(cbDiff.getValue());
            sel.setType(cbType.getValue());
            if (exerciseDao.update(sel)) { loadExercises(p); }
            else show("Update failed");
        });

        btnDelete.setOnAction(e -> {
            Exercise sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { show("Select an exercise"); return; }
            if (exerciseDao.delete(sel.getId())) {
                Profile p = cbProfiles.getValue();
                if (p != null) loadExercises(p);
                clearForm();
            } else show("Delete failed");
        });
    }

    public Node getRoot() { return root; }

    public void reloadProfiles() {
        cbProfiles.getItems().setAll(profileDao.getAll());
        if (!cbProfiles.getItems().isEmpty()) {
            cbProfiles.getSelectionModel().selectFirst();
        } else {
            table.getItems().clear();
        }
    }

    private void loadExercises(Profile p) {
        if (p == null) { table.getItems().clear(); return; }
        table.getItems().setAll(exerciseDao.findByProfile(p.getId())); // add this finder if you don’t have it
    }

    private void clearForm() {
        tfName.clear(); tfMuscle.clear(); tfEquip.clear();
        cbDiff.getSelectionModel().selectFirst();
        cbType.getSelectionModel().selectFirst();
        table.getSelectionModel().clearSelection();
    }

    private static void show(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
}
