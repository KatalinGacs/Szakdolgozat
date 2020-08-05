package application;

import application.common.DecimalCellFactory;
import controller.SprinklerController;
import controller.SprinklerControllerImpl;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.bean.SprinklerShape;
import model.bean.SprinklerType;
import model.bean.Zone;

/**
 * A table which lists informations about sprinklershapes in a selected zone
 * 
 * @author Gacs Katalin
 *
 */
public class SprinklerListTable extends TableView<SprinklerShape> {

	SprinklerController controller;
	
	TableColumn<SprinklerShape, SprinklerType> nameCol = new TableColumn<>("Szórófej");
	TableColumn<SprinklerShape, String> groupCol = new TableColumn<>("Csoport");
	TableColumn<SprinklerShape, Double> flowRateCol = new TableColumn<>("Vízfogyasztás (l/min)");
	TableColumn<SprinklerShape, Double> waterCol = new TableColumn<>("Csapadék (mm)");
	

	public SprinklerListTable(Zone zone, SprinklerController dataController) {

		controller = dataController;
		
		nameCol.setCellValueFactory(new PropertyValueFactory<SprinklerShape, SprinklerType>("sprinkler"));
		groupCol.setCellValueFactory(new PropertyValueFactory<SprinklerShape, String>("group"));
		flowRateCol.setCellValueFactory(new PropertyValueFactory<SprinklerShape, Double>("flowRate"));
		flowRateCol.setCellFactory(new DecimalCellFactory<SprinklerShape, Double>());
		waterCol.setCellValueFactory(new PropertyValueFactory<SprinklerShape, Double>("waterCoverageInMmPerHour"));
		waterCol.setCellFactory(new DecimalCellFactory<SprinklerShape, Double>());

		getColumns().addAll(nameCol, groupCol, flowRateCol, waterCol);
		
		setItems(controller.listSprinklerShapes(zone));
	}
}