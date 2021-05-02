package application.dbviews;

import controller.SprinklerController;
import controller.SprinklerControllerImpl;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.DbException;
import model.bean.SprinklerGroup;
import utilities.Common;

public class SprinklerGroupDBView {

	private SprinklerController controller = new SprinklerControllerImpl();

	private Stage stage = new Stage();
	private VBox root = new VBox();
	private Scene scene = new Scene(root);

	private TableView<SprinklerGroup> tableView = new TableView<>();
	private TableColumn<SprinklerGroup, String> nameCol = new TableColumn<>("Csoport név");

	private Button deleteBtn = new Button("Kijelöltek törlése");
	private Text addSprinklerGroupText = new Text("Új csoport név:");
	private TextField nameField = new TextField();
	private Button addBtn = new Button("Hozzáad");

	public SprinklerGroupDBView() {
		try {
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setScene(scene);
			stage.setTitle("Anyag adatbázis");
			root.getChildren().addAll(tableView, deleteBtn, addSprinklerGroupText, nameField, addBtn);
			tableView.getColumns().add(nameCol);
			tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
			nameCol.setCellValueFactory(new PropertyValueFactory<SprinklerGroup, String>("name"));
			try {
				tableView.setItems(controller.listSprinklerGroups());
			} catch (DbException ex) {
				Common.showAlert(ex.getMessage());
			}
			addBtn.setOnAction(e -> {
				SprinklerGroup s = new SprinklerGroup(nameField.getText());
				try {
					controller.addSprinklerGroup(s);
					tableView.setItems(controller.listSprinklerGroups());
				} catch (DbException ex) {
					Common.showAlert(ex.getMessage());
				}
				nameField.setText("");
			});
			deleteBtn.setOnAction(e -> {
				ObservableList<SprinklerGroup> selectedGroups = tableView.getSelectionModel().getSelectedItems();
				try {
					for (SprinklerGroup s : selectedGroups) {
						controller.deleteSprinklerGroup(s);
					}
					tableView.setItems(controller.listSprinklerGroups());
				} catch (DbException ex) {
					Common.showAlert(ex.getMessage());
				}
			});

			stage.show();
		} catch (Exception ex) {
			utilities.Error.HandleException(ex);
		}

	}

}
