package application;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Rajzolo extends VBox {

	private Button szorofejbtn = new Button("Sz�r�fej kiv�laszt�sa");

	private CanvasPane canvasPane = new CanvasPane();
	private ZoomableScrollPane scrollPane = new ZoomableScrollPane(canvasPane);

	public Rajzolo() {

		getChildren().add(szorofejbtn);

		scrollPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		scrollPane.setFitToWidth(true);
		scrollPane.setPrefHeight(600);
		scrollPane.setMinHeight(USE_COMPUTED_SIZE);

		getChildren().add(scrollPane);

		szorofejbtn.setOnAction(e -> {
			setSprinklerAttributes();
		});
	}

	private void setSprinklerAttributes() {
		Stage sprinklerInfoStage = new Stage();
		HBox root = new HBox();
		Scene scene = new Scene(root, 400, 50);
		Text colorText = new Text("Sz�n: ");
		ColorPicker colorPicker = new ColorPicker();
		Text radiusText = new Text("Sug�r: ");
		TextField radiusField = new TextField();
		sprinklerInfoStage.setScene(scene);
		Button ok = new Button("OK");
		root.getChildren().addAll(colorText, colorPicker, radiusText, radiusField, ok);

		ok.setOnAction(e -> {
			if (radiusField.getText() == null && radiusField.getText().trim().isEmpty()) {
				Common.showAlert("Add meg a sz�r�fej sugar�t!");
			} else {
				try {
					canvasPane.setSprinklerRadius(Double.parseDouble(radiusField.getText()));
					canvasPane.setSprinklerColor(colorPicker.getValue());
					canvasPane.setSprinklerAttributesSet(true);
					canvasPane.requestFocus();
				} catch (NumberFormatException ex) {
					Common.showAlert("Sz�mokban add meg a sz�r�fej sugar�t!");
				}
			}
			sprinklerInfoStage.close();
		});
		sprinklerInfoStage.show();

	}

}
