package application;

import application.CanvasPane.Use;
import application.common.Common;
import controller.SprinklerController;
import controller.SprinklerControllerImpl;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.bean.Zone;

public class ZoneStage extends Stage{

	static SprinklerController controller = new SprinklerControllerImpl();
	
	private GridPane root = new GridPane();
	private Scene scene = new Scene(root);
	private Text zoneNameText = new Text("Zóna elnevezése");
	private TextField zoneNameTextField = new TextField();
	private Text numberOfSelectedHeadsText = new Text("Kijelölt szórófejek száma");
	private Text flowRateOfSelectedHeadsText = new Text("Kijelölt fejek vízfogyasztása (l/min)");
	private Text durationOfWatering = new Text("Zóna mûködésének napi idõtartama");
	private HBox timePicker = new HBox();
	private Spinner<Integer> hourPicker = new Spinner<Integer>(0, 5, 1);
	private Text colon = new Text(" : ");
	private Spinner<Integer> minutePicker = new Spinner<Integer>(0, 59, 0);
	private Button createZoneBtn = new Button("Zóna létrehozása");

	public ZoneStage (CanvasPane canvasPane, ToggleButton addHeads, ToggleButton removeHeads, Text numberOfSelectedHeadsField, Text flowRateOfSelectedHeadsField) {

		setX(Common.primaryScreenBounds.getWidth() - 500);
		setY(100);
		root.setVgap(10);
		root.setHgap(10);
		root.setPadding(new Insets(10, 10, 10, 10));
		setTitle("Zóna megadása");
		setScene(scene);
		setAlwaysOnTop(true);
		
		hourPicker.setPrefWidth(60);
		minutePicker.setPrefWidth(60);
		timePicker.getChildren().addAll(hourPicker, colon, minutePicker);
		timePicker.setAlignment(Pos.CENTER_LEFT);

		HBox addOrRemoveContainer = new HBox(addHeads, removeHeads);
		addHeads.setSelected(true);

		root.add(zoneNameText, 0, 0);
		root.add(zoneNameTextField, 1, 0);
		root.add(numberOfSelectedHeadsText, 0, 1);
		root.add(numberOfSelectedHeadsField, 1, 1);
		root.add(flowRateOfSelectedHeadsText, 0, 2);
		root.add(flowRateOfSelectedHeadsField, 1, 2);
		root.add(addOrRemoveContainer, 0, 3);
		root.add(durationOfWatering, 0, 4);
		root.add(timePicker, 1, 4);
		root.add(createZoneBtn, 0, 5);

		createZoneBtn.setOnAction(e -> {
			boolean zoneNameInUse = false;
			double durationInHours = hourPicker.getValue() + ((double) minutePicker.getValue() / 60);
			for (Zone z : controller.listZones()) {
				if (z.getName().contentEquals(zoneNameTextField.getText().trim())) {
					zoneNameInUse = true;
					break;
				}
			}
			
			if (zoneNameTextField.getText() == null || zoneNameTextField.getText().trim().isEmpty()) {
				Common.showAlert("Add meg a zóna nevét!");
			} else if (canvasPane.selectedSprinklerShapes.isEmpty()) {
				Common.showAlert("Nincsenek kiválasztott szórófejek!");
			} else if (zoneNameInUse) {
				Common.showAlert("Ilyen nevû zóna már létezik!");
			}else {
				canvasPane.createZone(zoneNameTextField.getText().trim(), durationInHours);
				canvasPane.stateOfCanvasUse = Use.NONE;
				close();
			}
		});
		
		setOnCloseRequest(e -> {
			canvasPane.deselectAll();
			canvasPane.stateOfCanvasUse = Use.NONE;
		});
	}
	
}
