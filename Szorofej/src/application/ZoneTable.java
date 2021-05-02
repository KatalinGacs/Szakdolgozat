package application;

import controller.SprinklerController;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.bean.Zone;
import utilities.DecimalCellFactory;

/**
 * A table which lists informations about the zones on the play
 * 
 * @author Gacs Katalin
 *
 */
public class ZoneTable extends TableView<Zone> {

	TableColumn<Zone, String> nameCol = new TableColumn<>("Név");
	TableColumn<Zone, Integer> numberOfHeadsCol = new TableColumn<>("Fejek száma");
	TableColumn<Zone, Double> durationCol = new TableColumn<>("Idõtartam");
	TableColumn<Zone, Double> flowRateCol = new TableColumn<>("Vízfogyasztás (l/min)");

	public ZoneTable(SprinklerController controller) {
		try {
			nameCol.setCellValueFactory(new PropertyValueFactory<Zone, String>("name"));
			numberOfHeadsCol.setCellValueFactory(new PropertyValueFactory<Zone, Integer>("numberOfHeads"));
			durationCol.setCellValueFactory(new PropertyValueFactory<Zone, Double>("durationOfWatering"));
			durationCol.setCellFactory(new DecimalCellFactory<Zone, Double>());
			flowRateCol.setCellValueFactory(new PropertyValueFactory<Zone, Double>("sumOfFlowRate"));
			flowRateCol.setCellFactory(new DecimalCellFactory<Zone, Double>());

			getColumns().addAll(nameCol, numberOfHeadsCol, durationCol, flowRateCol);

			setItems(controller.listZones());
		} catch (Exception ex) {
			utilities.Error.HandleException(ex);
		}
	}
}
