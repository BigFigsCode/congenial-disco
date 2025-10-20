

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class ProfilesView {
    private final BorderPane root = new BorderPane();
    private final TableView<Profile> table = new TableView<>();
    private final TextField tfName = new TextField();
    private final ComboBox<String> cbLevel = new ComboBox<>();
    private final ComboBox<String> cbGoal  = new ComboBox<>();
    private final Spinner<Integer> spDays  = new Spinner<>(1, 7, 3);

    private final ProfileDao dao = new ProfileDao();

    // Callback the MainApp can register to tell ExercisesView to reload profile list
    private Runnable onAnyProfileChange = () -> {};

    public ProfilesView() {
        // Table columns
        TableColumn<Profile, Integer> cId    = new TableColumn<>("ID");
        cId.setCellValueFactory(p -> new javafx.beans.property.SimpleIntegerProperty(p.getValue().getId()).asObject());
        cId.setPrefWidth(60);

        TableColumn<Profile, String> cName  = new TableColumn<>("Name");
        cName.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().getName()));
        cName.setPrefWidth(180);

        TableColumn<Profile, String> cLevel = new TableColumn<>("Level");
        cLevel.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().getLevel()));
        cLevel.setPrefWidth(140);

        TableColumn<Profile, String> cGoal  = new TableColumn<>("Goal");
        cGoal.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().getGoal()));
        cGoal.setPrefWidth(160);

        TableColumn<Profile, Integer> cDays = new TableColumn<>("Days/Week");
        cDays.setCellValueFactory(p -> new javafx.beans.property.SimpleIntegerProperty(p.getValue().getDaysPerWeek()).asObject());
        cDays.setPrefWidth(100);

        table.getColumns().addAll(cId, cName, cLevel, cGoal, cDays);

        // Form
        cbLevel.getItems().addAll("Beginner", "Intermediate", "Advanced");
        cbLevel.getSelectionModel().selectFirst();
        cbGoal.getItems().addAll("Strength", "Fat Loss", "Hypertrophy", "Endurance");
        cbGoal.getSelectionModel().selectFirst();

        GridPane form = new GridPane();
        form.setHgap(10); form.setVgap(8); form.setPadding(new Insets(10));
        form.addRow(0, new Label("Name:"), tfName, new Label("Level:"), cbLevel);
        form.addRow(1, new Label("Goal:"), cbGoal, new Label("Days/Week:"), spDays);

        Button btnAdd = new Button("Add");
        Button btnUpdate = new Button("Update");
        Button btnDelete = new Button("Delete");
        HBox buttons = new HBox(8, btnAdd, btnUpdate, btnDelete);
        buttons.setPadding(new Insets(0,10,10,10));

        // Layout
        root.setCenter(table);
        BorderPane bottom = new BorderPane();
        bottom.setCenter(form);
        bottom.setBottom(buttons);
        root.setBottom(bottom);

        // Selection to form
        table.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                tfName.setText(sel.getName());
                cbLevel.getSelectionModel().select(sel.getLevel());
                cbGoal.getSelectionModel().select(sel.getGoal());
                spDays.getValueFactory().setValue(sel.getDaysPerWeek());
            }
        });

        // Actions
        btnAdd.setOnAction(e -> {
            String name = tfName.getText().trim();
            if (name.isEmpty()) { show("Name required"); return; }
            Profile p = new Profile(name, cbLevel.getValue(), cbGoal.getValue(), spDays.getValue());
            int id = dao.insert(p);
            if (id > 0) { refresh(); clearForm(); onAnyProfileChange.run(); }
            else show("Insert failed");
        });

        btnUpdate.setOnAction(e -> {
            Profile sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { show("Select a profile to update"); return; }
            sel.setName(tfName.getText().trim());
            sel.setLevel(cbLevel.getValue());
            sel.setGoal(cbGoal.getValue());
            sel.setDaysPerWeek(spDays.getValue());
            if (dao.update(sel)) { refresh(); onAnyProfileChange.run(); }
            else show("Update failed");
        });

        btnDelete.setOnAction(e -> {
            Profile sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { show("Select a profile to delete"); return; }
            if (dao.delete(sel.getId())) { refresh(); clearForm(); onAnyProfileChange.run(); }
            else show("Delete failed");
        });
    }

    public Node getRoot() { return root; }

    public void refresh() {
        table.getItems().setAll(dao.getAll());
    }

    public void setOnAnyProfileChange(Runnable r) {
        this.onAnyProfileChange = (r != null ? r : () -> {});
    }

    private void clearForm() {
        tfName.clear();
        cbLevel.getSelectionModel().selectFirst();
        cbGoal.getSelectionModel().selectFirst();
        spDays.getValueFactory().setValue(3);
        table.getSelectionModel().clearSelection();
    }

    private static void show(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
}
