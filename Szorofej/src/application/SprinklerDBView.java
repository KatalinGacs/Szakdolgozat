package application;

import application.common.DecimalCellFactory;
import controller.SprinklerController;
import controller.SprinklerControllerImpl;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.bean.SprinklerShape;
import model.bean.SprinklerType;

public class SprinklerDBView {

	private SprinklerController controller = new SprinklerControllerImpl();

	private Stage sprinklerDbStage = new Stage();
	private VBox root = new VBox();
	private Scene scene = new Scene(root, 800, 400);

	private TableView<SprinklerType> tableView = new TableView<>();
	private TableColumn<SprinklerType, String> nameCol = new TableColumn<>("N�v");
	private TableColumn<SprinklerType, Double> minRadiusCol = new TableColumn<>("Min. sug�r (m)");
	private TableColumn<SprinklerType, Double> maxRadiusCol = new TableColumn<>("Max. sug�r (m)");
	private TableColumn<SprinklerType, Double> minAngleCol = new TableColumn<>("Min. sz�g (fok)");
	private TableColumn<SprinklerType, Double> maxAngleCol = new TableColumn<>("Max. sz�g (fok)");
	private TableColumn<SprinklerType, Boolean> fixWaterConsumptionCol = new TableColumn<>("Fix v�zfogyaszt�s");
	private TableColumn<SprinklerType, Double> waterConsumptionCol = new TableColumn<>("V�zfogyaszt�s (l/min)");
	private TableColumn<SprinklerType, Double> minPressureCol = new TableColumn<>("Min. v�znyom�s (bar)");
	private TableColumn<SprinklerType, String> sprinklerGroupCol = new TableColumn<>("Csoport");

	public SprinklerDBView() {
		sprinklerDbStage.setScene(scene);
		root.getChildren().add(tableView);
		tableView.getColumns().addAll(nameCol, minRadiusCol, maxRadiusCol, minAngleCol, maxAngleCol,
				fixWaterConsumptionCol, waterConsumptionCol, minPressureCol, sprinklerGroupCol);
		nameCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, String>("name"));
		minRadiusCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, Double>("minRadius"));
		minRadiusCol.setCellFactory(new DecimalCellFactory<SprinklerType, Double>());
		maxRadiusCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, Double>("maxRadius"));
		maxRadiusCol.setCellFactory(new DecimalCellFactory<SprinklerType, Double>());
		minAngleCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, Double>("minAngle"));
		minAngleCol.setCellFactory(new DecimalCellFactory<SprinklerType, Double>());
		maxAngleCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, Double>("maxAngle"));
		maxAngleCol.setCellFactory(new DecimalCellFactory<SprinklerType, Double>());
		fixWaterConsumptionCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, Boolean>("fixWaterConsumption"));
		waterConsumptionCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, Double>("waterConsumption"));
		waterConsumptionCol.setCellFactory(new DecimalCellFactory<SprinklerType, Double>());
		minPressureCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, Double>("minPressure"));
		minPressureCol.setCellFactory(new DecimalCellFactory<SprinklerType, Double>());
		sprinklerGroupCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, String>("sprinklerGroup"));
	
		tableView.setItems(controller.listSprinklerTypes());
		
		sprinklerDbStage.show();
	}

}
