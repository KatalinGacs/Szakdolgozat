package application;

import application.common.DecimalCellFactory;
import controller.SprinklerController;
import controller.SprinklerControllerImpl;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.bean.Zone;

/**
 * A table which lists informations about the zones on the play
 * 
 * @author Gacs Katalin
 *
 */
public class ZoneTable extends TableView<Zone> {

	TableColumn<Zone, String> nameCol = new TableColumn<>("N�v");
	TableColumn<Zone, Integer> numberOfHeadsCol = new TableColumn<>("Fejek sz�ma");
	TableColumn<Zone, Double> durationCol = new TableColumn<>("Id�tartam");
	TableColumn<Zone, Double> flowRateCol = new TableColumn<>("V�zfogyaszt�s (l/min)");

	public ZoneTable(SprinklerController controller) {

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
