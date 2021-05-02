package application.dbviews;

import controller.SprinklerController;
import controller.SprinklerControllerImpl;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.DbException;
import model.bean.Material;
import model.bean.MaterialSprinklerConnection;
import model.bean.SprinklerType;
import utilities.Common;

public class ConnectMaterialsDbView {
	private SprinklerController controller = new SprinklerControllerImpl();

	private Stage stage = new Stage();
	private HBox root = new HBox();
	private Scene scene = new Scene(root);

	private TableView<SprinklerType> sprinklerTypes = new TableView<>();
	private TableColumn<SprinklerType, String> nameCol = new TableColumn<>("N�v");

	private VBox addedMaterialPane = new VBox();
	private Text addedTitle = new Text("Hozz�rendelt anyagok");
	private TableView<MaterialSprinklerConnection> alreadyAddedMaterials = new TableView<>();
	private TableColumn<MaterialSprinklerConnection, Material> materialCol = new TableColumn<>("N�v");
	private TableColumn<MaterialSprinklerConnection, Integer> quantityCol = new TableColumn<>("Mennyis�g");
	private Button deleteButton = new Button("Anyag hozz�rendel�s t�rl�se");

	private GridPane addConnectionPane = new GridPane();
	private Text addTitle = new Text("Anyag hozz�rendel�se");
	private ListView<Material> materials = new ListView<>();
	private Text quantityText = new Text("Mennyis�g");
	private Spinner<Integer> quantityPicker = new Spinner<>(1, 20, 1);
	private Button addButton = new Button("Hozz�ad�s");

	public ConnectMaterialsDbView() {

		try {
			stage.setTitle("Sz�r�fej-anyag �sszekapcsol�s");
			stage.setScene(scene);
			stage.initModality(Modality.APPLICATION_MODAL);

			// the first panel: a list of the sprinkler types to which materials can be
			// connected
			sprinklerTypes.getColumns().add(nameCol);
			nameCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, String>("name"));
			try {
				sprinklerTypes.setItems(controller.listSprinklerTypes());
			} catch (DbException ex) {
				Common.showAlert(ex.getMessage());
			}
			sprinklerTypes.getSelectionModel().selectFirst();

			// the second panel: a list of the materials already set to the sprinkler type
			// selected in the first panel
			addedMaterialPane.getChildren().addAll(addedTitle, alreadyAddedMaterials, deleteButton);
			alreadyAddedMaterials.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
			alreadyAddedMaterials.getColumns().addAll(materialCol, quantityCol);
			materialCol.setCellValueFactory(new PropertyValueFactory<MaterialSprinklerConnection, Material>("material"));
			quantityCol.setCellValueFactory(new PropertyValueFactory<MaterialSprinklerConnection, Integer>("quantity"));
			addedMaterialPane.setSpacing(10);
			addedMaterialPane.setAlignment(Pos.TOP_CENTER);

			sprinklerTypes.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
				if (newSelection != null)
					refreshTables();
			});

			deleteButton.setOnAction(e -> {
				MaterialSprinklerConnection mConn = alreadyAddedMaterials.getSelectionModel().getSelectedItem();
				Material m = mConn.getMaterial();
				SprinklerType s = mConn.getSprinklerType();
				try {
					controller.deleteMaterialConnection(s, m);
				} catch (DbException ex) {
					Common.showAlert(ex.getMessage());
				}
			});

			// the third panel: adding further materials to the sprinkler type selected in
			// the first panel
			addConnectionPane.add(addTitle, 0, 0, 2, 1);
			addConnectionPane.add(materials, 0, 1, 2, 1);
			addConnectionPane.add(quantityText, 0, 2);
			addConnectionPane.add(quantityPicker, 1, 2);
			addConnectionPane.add(addButton, 0, 3, 2, 1);
			addConnectionPane.setVgap(10);
			addConnectionPane.setHgap(10);
			addConnectionPane.setAlignment(Pos.TOP_CENTER);

			addButton.setOnAction(e -> {
				if (sprinklerTypes.getSelectionModel().getSelectedItem() != null) {
					SprinklerType s = sprinklerTypes.getSelectionModel().getSelectedItem();
					Material m = materials.getSelectionModel().getSelectedItem();
					int quantity = quantityPicker.getValue();
					try {
						controller.addMaterialConnection(s, m, quantity);
					} catch (DbException ex) {
						Common.showAlert(ex.getMessage());
					}
					refreshTables();
				}

			});

			refreshTables();

			root.getChildren().addAll(sprinklerTypes, addedMaterialPane, addConnectionPane);
			root.setPadding(new Insets(10));
			root.setSpacing(10);

			stage.show();
		} catch (Exception ex) {
			utilities.Error.HandleException(ex);
		}
	}

	private void refreshTables() {
		try {
			alreadyAddedMaterials.setItems(controller.listMaterials(sprinklerTypes.getSelectionModel().getSelectedItem()));
			materials.setItems(controller.listNotAddedMaterials(sprinklerTypes.getSelectionModel().getSelectedItem()));
		} catch (DbException e) {
			Common.showAlert(e.getMessage());
		} catch (Exception ex) {
			utilities.Error.HandleException(ex);
		}	 
	}
}
