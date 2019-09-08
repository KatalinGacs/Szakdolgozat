package application.dbviews;

import application.common.Common;
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
import javafx.stage.Stage;
import model.DbException;
import model.bean.SprinklerGroup;

public class SprinklerGroupDBView {

	private SprinklerController controller = new SprinklerControllerImpl();

	private Stage sprinklerGroupDbStage = new Stage();
	private VBox root = new VBox();
	private Scene scene = new Scene(root, 400, 400);

	private TableView<SprinklerGroup> tableView = new TableView<>();
	private TableColumn<SprinklerGroup, String> nameCol = new TableColumn<>("Csoport név");
	
	private Button deleteBtn = new Button("Kijelöltek törlése");
	private Text addSprinklerGroupText = new Text("Új csoport név:");
	private TextField nameField = new TextField();
	private Button addBtn = new Button("Hozzáad");
	
	public SprinklerGroupDBView() {
		sprinklerGroupDbStage.setScene(scene);
		root.getChildren().addAll(tableView, deleteBtn, addSprinklerGroupText, nameField, addBtn);
		tableView.getColumns().add(nameCol);
		nameCol.setCellValueFactory(new PropertyValueFactory<SprinklerGroup, String>("name"));
		tableView.setItems(controller.listSprinklerGroups());
		addBtn.setOnAction(e -> {
			SprinklerGroup s = new SprinklerGroup(nameField.getText());
			try {
			controller.addSprinklerGroup(s);
			} catch (DbException ex) {
				Common.showAlert(ex.getMessage());
			}
			tableView.setItems(controller.listSprinklerGroups());
			nameField.setText("");
		});
		deleteBtn.setOnAction(e -> {
			ObservableList<SprinklerGroup> selectedGroups = tableView.getSelectionModel().getSelectedItems();
			for (SprinklerGroup s : selectedGroups) {
				controller.deleteSprinklerGroup(s);
			}
			tableView.setItems(controller.listSprinklerGroups());
		});
		
		sprinklerGroupDbStage.show();

	}

}
