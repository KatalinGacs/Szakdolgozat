package application.dbviews;

import application.common.DecimalCellFactory;
import controller.SprinklerController;
import controller.SprinklerControllerImpl;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.bean.SprinklerType;

public class SprinklerDBView {

	private SprinklerController controller = new SprinklerControllerImpl();

	private Stage stage = new Stage();
	private VBox root = new VBox();
	private Scene scene = new Scene(root);

	private TableView<SprinklerType> tableView = new TableView<>();
	private TableColumn<SprinklerType, String> nameCol = new TableColumn<>("Név");
	private TableColumn<SprinklerType, Double> minRadiusCol = new TableColumn<>("Min. sugár (m)");
	private TableColumn<SprinklerType, Double> maxRadiusCol = new TableColumn<>("Max. sugár (m)");
	private TableColumn<SprinklerType, Double> minAngleCol = new TableColumn<>("Min. szög (fok)");
	private TableColumn<SprinklerType, Double> maxAngleCol = new TableColumn<>("Max. szög (fok)");
	private TableColumn<SprinklerType, Boolean> fixWaterConsumptionCol = new TableColumn<>("Rotoros");
	private TableColumn<SprinklerType, Double> waterConsumptionCol = new TableColumn<>("Vízfogyasztás (l/min)");
	private TableColumn<SprinklerType, Double> minPressureCol = new TableColumn<>("Min. víznyomás (bar)");
	private TableColumn<SprinklerType, String> sprinklerGroupCol = new TableColumn<>("Csoport");

	private Button delBtn = new Button("Törlés");

	public SprinklerDBView() {

		root.getChildren().addAll(tableView, delBtn);

		// TODO ha be van állítva a resize policy, látszik az összes oszlop, de a
		// tartalmukat levágja, ha nincs beállítva, akkor a tableview kilóg a stagebõl
		// és görgetni kell, hogy a jobb szélsõ oszlopok látszanak
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		tableView.getColumns().addAll(nameCol, minRadiusCol, maxRadiusCol, minAngleCol, maxAngleCol,
				fixWaterConsumptionCol, waterConsumptionCol, minPressureCol, sprinklerGroupCol);
		stage.setScene(scene);
		nameCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, String>("name"));
		minRadiusCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, Double>("minRadius"));
		minRadiusCol.setCellFactory(new DecimalCellFactory<SprinklerType, Double>());
		maxRadiusCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, Double>("maxRadius"));
		maxRadiusCol.setCellFactory(new DecimalCellFactory<SprinklerType, Double>());
		minAngleCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, Double>("minAngle"));
		minAngleCol.setCellFactory(new DecimalCellFactory<SprinklerType, Double>());
		maxAngleCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, Double>("maxAngle"));
		maxAngleCol.setCellFactory(new DecimalCellFactory<SprinklerType, Double>());
		fixWaterConsumptionCol
				.setCellValueFactory(new PropertyValueFactory<SprinklerType, Boolean>("fixWaterConsumption"));
		waterConsumptionCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, Double>("waterConsumption"));
		waterConsumptionCol.setCellFactory(new DecimalCellFactory<SprinklerType, Double>());
		minPressureCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, Double>("minPressure"));
		minPressureCol.setCellFactory(new DecimalCellFactory<SprinklerType, Double>());
		sprinklerGroupCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, String>("sprinklerGroup"));
		tableView.setItems(controller.listSprinklerTypes());

		delBtn.setOnAction(e -> {
			ObservableList<SprinklerType> selected = tableView.getSelectionModel().getSelectedItems();
			for (SprinklerType s : selected) {
				controller.deleteSprinklerType(s);
			}
			tableView.setItems(controller.listSprinklerTypes());
		});
		
		stage.show();

	}

}
