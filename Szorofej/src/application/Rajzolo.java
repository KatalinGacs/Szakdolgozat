package application;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Rajzolo extends VBox {
	
	private TabPane tabPane = new TabPane();
	private Tab alaprajzTab = new Tab("Alaprajz");
	private Tab szorofejTab = new Tab("Szorofej");
	private HBox alaprajzTabElements = new HBox();
	private HBox szorofejTabElements = new HBox();

	private Button szorofejbtn = new Button("Szórófej kiválasztása");

	private CanvasPane canvasPane = new CanvasPane();
	private ZoomableScrollPane scrollPane = new ZoomableScrollPane(canvasPane);

	public Rajzolo() {

		getChildren().add(tabPane);
		tabPane.getTabs().addAll(alaprajzTab, szorofejTab);
		szorofejTab.setContent(szorofejTabElements);
		szorofejTabElements.getChildren().add(szorofejbtn);

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
		Scene scene = new Scene(root, 420, 50);
		Text colorText = new Text("Szín: ");
		ColorPicker colorPicker = new ColorPicker();
		Text radiusText = new Text("Sugár: ");
		TextField radiusField = new TextField();
		Text meterText = new Text("méter");
		sprinklerInfoStage.setScene(scene);
		Button ok = new Button("OK");
		root.getChildren().addAll(colorText, colorPicker, radiusText, radiusField, meterText, ok);

		ok.setOnAction(e -> {
			if (radiusField.getText() == null && radiusField.getText().trim().isEmpty()) {
				Common.showAlert("Add meg a szórófej sugarát!");
			} else {
				try {
					canvasPane.setSprinklerRadius(Double.parseDouble(radiusField.getText()) * 50);
					canvasPane.setSprinklerColor(colorPicker.getValue());
					canvasPane.setSprinklerAttributesSet(true);
					canvasPane.requestFocus();
				} catch (NumberFormatException ex) {
					Common.showAlert("Számokban add meg a szórófej sugarát!");
				}
			}
			sprinklerInfoStage.close();
		});
		sprinklerInfoStage.show();

	}

}
