package application;

import application.common.DecimalCellFactory;
import controller.SprinklerController;
import controller.SprinklerControllerImpl;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.bean.Zone;

public class ZoneTable extends TableView<Zone> {
	
	SprinklerController controller = new SprinklerControllerImpl();
	
	TableColumn<Zone, String> nameCol = new TableColumn<>("N�v");
	TableColumn<Zone, Integer> numberOfHeadsCol = new TableColumn<>("Fejek sz�ma");
	TableColumn<Zone, Double> durationCol = new TableColumn<>("Id�tartam");
	TableColumn<Zone, Double> flowRateCol  = new TableColumn<>("V�zfogyaszt�s (l/min)");
	
	
	public ZoneTable(CanvasPane canvasPane) {

		nameCol.setCellValueFactory(new PropertyValueFactory<Zone, String>("name"));
		numberOfHeadsCol.setCellValueFactory(new PropertyValueFactory<Zone, Integer>("numberOfHeads"));
		durationCol.setCellValueFactory(new PropertyValueFactory<Zone, Double>("durationOfWatering"));
		durationCol.setCellFactory(new DecimalCellFactory<Zone, Double>());
		flowRateCol.setCellValueFactory(new PropertyValueFactory<Zone, Double>("sumOfFlowRate"));
		flowRateCol.setCellFactory(new DecimalCellFactory<Zone, Double>());

		getColumns().addAll(nameCol, numberOfHeadsCol, durationCol, flowRateCol);
		
		setItems(controller.listZones());

	}
}
