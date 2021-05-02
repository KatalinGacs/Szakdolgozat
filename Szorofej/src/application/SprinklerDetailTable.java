package application;

import controller.SprinklerController;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import model.bean.SprinklerShape;
import model.bean.Zone;

/**
 * A Layout which lists informations about a selected sprinklershape
 * 
 * @author Gacs Katalin
 *
 */
public class SprinklerDetailTable extends GridPane {

	private SprinklerController controller;

	private Text sprinklerTypeText = new Text("Szórófej típus");
	private TextField sprinklerTypeField = new TextField();
	private Text flowRateText = new Text("Vízmennyiség (l/min)");
	private TextField flowRateField = new TextField();
	private Text waterCoverageText = new Text("Csapadék (mm)");
	private TextField waterCoverageField = new TextField();
	private Text minPressureText = new Text("Min. víznyomás");
	private TextField minPressureField = new TextField();
	private Text zoneText = new Text("Zóna");
	private TextField zoneField = new TextField();

	public SprinklerDetailTable(SprinklerShape s, SprinklerController dataController) {
		try {
			controller = dataController;
			
			Zone zone = null;
			for (Zone z : controller.listZones()) {
				if (z.getSprinklers().contains(s)) {
					zone = z;
					break;
				}
			}

			add(sprinklerTypeText, 0, 0);
			add(sprinklerTypeField, 1, 0);
			add(flowRateText, 0, 1);
			add(flowRateField, 1, 1);
			add(waterCoverageText, 0, 2);
			add(waterCoverageField, 1, 2);
			add(minPressureText, 0, 3);
			add(minPressureField, 1, 3);
			add(zoneText, 0, 4);
			add(zoneField, 1, 4);

			sprinklerTypeField.setText(s.getSprinkler().getName());
			sprinklerTypeField.setEditable(false);
			flowRateField.setText(String.format("%.2f", s.getFlowRate()));
			flowRateField.setEditable(false);
			waterCoverageField.setText(String.format("%.2f", s.getWaterCoverageInMmPerHour()));
			waterCoverageField.setEditable(false);
			minPressureField.setText(String.format("%.2f", s.getSprinkler().getMinPressure()));
			minPressureField.setEditable(false);
			zoneField.setText(zone.getName());
			zoneField.setEditable(false);
		} catch (Exception ex) {
			utilities.Error.HandleException(ex);
		}	
	}
}
