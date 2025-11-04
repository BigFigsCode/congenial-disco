import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class EquipmentView {
    private final TableView<Equipment> table = new TableView<>();
    private final ObservableList<Equipment> data = FXCollections.observableArrayList();
    private final TextField nameField = new TextField();
    private final TextField typeField = new TextField();
    private final TextField notesField = new TextField();
    private final EquipmentDao dao = new EquipmentDao();
    private final VBox root = new VBox(8);

    public EquipmentView() {
        // --- table columns ---
        TableColumn<Equipment, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()));
        idCol.setMinWidth(60);

        TableColumn<Equipment, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));

        TableColumn<Equipment, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getType()));

        TableColumn<Equipment, String> notesCol = new TableColumn<>("Notes");
        notesCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNotes()));

        table.getColumns().addAll(idCol, nameCol, typeCol, notesCol);
        table.setItems(data);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        // when a row is selected, load its values into the form
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, sel) -> {
            if (sel == null) { clearForm(); return; }
            nameField.setText(sel.getName());
            typeField.setText(sel.getType());
            notesField.setText(sel.getNotes());
        });

        // --- form ---
        GridPane form = new GridPane();
        form.setHgap(8); form.setVgap(8); form.setPadding(new Insets(8));
        form.addRow(0, new Label("Name:"), nameField, new Label("Type:"), typeField);
        form.addRow(1, new Label("Notes:"), notesField);

        // --- buttons ---
        Button addBtn = new Button("Add");
        addBtn.setOnAction(e -> {
            String nm = nameField.getText();
            if (nm == null || nm.isBlank()) return;       // simple guard
            dao.insert(new Equipment(nm, typeField.getText(), notesField.getText()));
            refresh(); clearForm();
        });

        Button updateBtn = new Button("Update");
        updateBtn.setOnAction(e -> {
            Equipment sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) return;
            sel.setName(nameField.getText());
            sel.setType(typeField.getText());
            sel.setNotes(notesField.getText());
            dao.update(sel);
            refresh();
        });

        Button deleteBtn = new Button("Delete");
        deleteBtn.setOnAction(e -> {
            Equipment sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) return;
            dao.delete(sel.getId());
            refresh(); clearForm();
        });

        HBox buttons = new HBox(8, addBtn, updateBtn, deleteBtn);
        buttons.setPadding(new Insets(0, 8, 8, 8));

        // layout
        root.setPadding(new Insets(8));
        root.getChildren().addAll(new Label("Equipment"), table, form, buttons);

        // initial load
        refresh();
    }

    public Node getRoot() { return root; }

    public void refresh() {
        data.setAll(dao.findAll());
    }

    private void clearForm() {
        nameField.clear();
        typeField.clear();
        notesField.clear();
        table.getSelectionModel().clearSelection();
    }
}
