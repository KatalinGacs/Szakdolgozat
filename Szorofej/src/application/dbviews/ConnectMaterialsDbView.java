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
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import model.bean.Material;
import model.bean.MaterialSprinklerConnection;
import model.bean.SprinklerType;

public class ConnectMaterialsDbView {
	private SprinklerController controller = new SprinklerControllerImpl();

	private Stage stage = new Stage();
	private HBox root = new HBox();
	private Scene scene = new Scene(root);

	private TableView<SprinklerType> sprinklerTypes = new TableView<>();
	private TableColumn<SprinklerType, String> nameCol = new TableColumn<>("Név");

	private VBox addedMaterialPane = new VBox();
	private Text addedTitle = new Text("Hozzárendelt anyagok");
	private TableView<MaterialSprinklerConnection> alreadyAddedMaterials = new TableView<>();
	private TableColumn<MaterialSprinklerConnection, Material> materialCol = new TableColumn<>("Név");
	private TableColumn<MaterialSprinklerConnection, Integer> quantityCol = new TableColumn<>("Mennyiség");
	private Button deleteButton = new Button("Anyag hozzárendelés törlése");

	private GridPane addConnectionPane = new GridPane();
	private Text addTitle = new Text("Anyag hozzárendelése");
	private ListView<Material> materials = new ListView<>();
	private Text quantityText = new Text("Mennyiség");
	private Spinner<Integer> quantityPicker = new Spinner<>(1, 20, 1);
	private Button addButton = new Button("Hozzáadás");

	public ConnectMaterialsDbView() {

		stage.setTitle("Szórófej-anyag összekapcsolás");
		stage.setScene(scene);

		// the first panel: a list of the sprinkler types to which materials can be
		// connected
		sprinklerTypes.getColumns().add(nameCol);
		nameCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, String>("name"));
		sprinklerTypes.setItems(controller.listSprinklerTypes());
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
			controller.deleteMaterialConnection(s, m);
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
			SprinklerType s = sprinklerTypes.getSelectionModel().getSelectedItem();
			Material m = materials.getSelectionModel().getSelectedItem();
			int quantity = quantityPicker.getValue();
			controller.addMaterialConnection(s, m, quantity);
			refreshTables();
		});

		refreshTables();
		
		root.getChildren().addAll(sprinklerTypes, addedMaterialPane, addConnectionPane);
		root.setPadding(new Insets(10));
		root.setSpacing(10);

		stage.show();
	}

	private void refreshTables() {
		alreadyAddedMaterials.setItems(controller.listMaterials(sprinklerTypes.getSelectionModel().getSelectedItem()));
		materials.setItems(controller.listNotAddedMaterials(sprinklerTypes.getSelectionModel().getSelectedItem()));
	}
}
