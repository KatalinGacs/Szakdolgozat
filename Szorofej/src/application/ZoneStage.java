package application;

import application.common.Common;
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

public class ZoneStage extends Stage{
	
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
		GridPane zoneRoot = new GridPane();
		zoneRoot.setVgap(10);
		zoneRoot.setHgap(10);
		zoneRoot.setPadding(new Insets(10, 10, 10, 10));
		Scene zoneScene = new Scene(zoneRoot, 400, 200);
		setScene(zoneScene);
		setAlwaysOnTop(true);
		
		hourPicker.setPrefWidth(60);
		minutePicker.setPrefWidth(60);
		timePicker.getChildren().addAll(hourPicker, colon, minutePicker);
		timePicker.setAlignment(Pos.CENTER_LEFT);

		HBox addOrRemoveContainer = new HBox(addHeads, removeHeads);
		addHeads.setSelected(true);

		zoneRoot.add(zoneNameText, 0, 0);
		zoneRoot.add(zoneNameTextField, 1, 0);
		zoneRoot.add(numberOfSelectedHeadsText, 0, 1);
		zoneRoot.add(numberOfSelectedHeadsField, 1, 1);
		zoneRoot.add(flowRateOfSelectedHeadsText, 0, 2);
		zoneRoot.add(flowRateOfSelectedHeadsField, 1, 2);
		zoneRoot.add(addOrRemoveContainer, 0, 3);
		zoneRoot.add(durationOfWatering, 0, 4);
		zoneRoot.add(timePicker, 1, 4);
		zoneRoot.add(createZoneBtn, 0, 5);

		createZoneBtn.setOnAction(e -> {
			double durationInHours = hourPicker.getValue() + ((double) minutePicker.getValue() / 60);
			if (zoneNameTextField.getText() == null || zoneNameTextField.getText().trim().isEmpty()) {
				Common.showAlert("Add meg a zóna nevét");
			} else if (canvasPane.selectedSprinklerShapes.isEmpty()) {
				Common.showAlert("Nincsenek kiválasztott szórófejek");
			} else {
				canvasPane.createZone(zoneNameTextField.getText(), durationInHours);
				close();
			}
		});
		
		setOnCloseRequest(e -> {
			canvasPane.deselectAll();
		});
	}
	
}
