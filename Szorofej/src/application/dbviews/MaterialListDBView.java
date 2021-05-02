package application.dbviews;

import controller.SprinklerController;
import controller.SprinklerControllerImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.DbException;
import model.bean.Material;
import utilities.Common;

public class MaterialListDBView {
	private SprinklerController controller = new SprinklerControllerImpl();

	private Stage stage = new Stage();
	private VBox root = new VBox();
	private Scene scene = new Scene(root);

	private TableView<Material> tableView = new TableView<>();
	private TableColumn<Material, String> nameCol = new TableColumn<>("Név");
	private TableColumn<Material, String> unitCol = new TableColumn<>("");

	private Button delBtn = new Button("Törlés");
	
	private Text addText = new Text("Új anyag hozzáadása");
	private GridPane addPane = new GridPane();
	private Text nameText = new Text("Név");
	private TextField nameField = new TextField();
	private Text unitText = new Text("Mértékegység");
	private ChoiceBox<String> unitBox = new ChoiceBox<String>();
	private Button addBtn = new Button("Hozzáadás");
	

	public void ShowMaterialListDBView() {
		
		try {
			stage.setTitle("Anyag adatbázis");
			stage.initModality(Modality.APPLICATION_MODAL);
			
			tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
			tableView.getColumns().addAll(nameCol, unitCol);
			stage.setScene(scene);
			nameCol.setCellValueFactory(new PropertyValueFactory<Material, String>("name"));
			unitCol.setCellValueFactory(new PropertyValueFactory<Material, String>("unit"));
			root.setPadding(new Insets(10));
			
			ObservableList<String> units =  FXCollections.observableArrayList();
			units.addAll("méter", "darab");
			unitBox.setItems(units);
			unitBox.getSelectionModel().select(1);
			
			addPane.add(addText, 0, 0, 2, 1);
			addPane.add(nameText, 0, 1);
			addPane.add(nameField, 1, 1);
			addPane.add(unitText, 0, 2);
			addPane.add(unitBox, 1, 2);
			addPane.add(addBtn, 0, 3, 2 ,1);
			addPane.setHgap(10);
			GridPane.setHalignment(addBtn, HPos.CENTER);
			
			root.getChildren().addAll(tableView, delBtn, addPane);
			
			addBtn.setOnAction(e -> {
				Material m  = new Material();
				m.setName(nameField.getText().trim());
				m.setUnit(unitBox.getSelectionModel().getSelectedItem());
				
				try {
					controller.addMaterial(m);
					tableView.setItems(controller.listMaterials());
				} catch (DbException ex) {
					Common.showAlert(ex.getMessage());
				}
				nameField.setText("");
			});
			
			delBtn.setOnAction(e -> {
				try {
					controller.deleteMaterial(tableView.getSelectionModel().getSelectedItem());
					tableView.setItems(controller.listMaterials());
				} catch (DbException ex) {
					Common.showAlert(ex.getMessage());
				}
			});
			
			try {
				tableView.setItems(controller.listMaterials());
			} catch (DbException ex) {
				Common.showAlert(ex.getMessage());
			}

			tableView.setEditable(true);
			stage.show();
		} catch (Exception ex) {
			utilities.Error.HandleException(ex);
		}
	}
}
