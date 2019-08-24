package application;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Rajzolo extends VBox {

	private TabPane tabPane = new TabPane();
	private Tab alaprajzTab = new Tab("Alaprajz");
	private Tab szorofejTab = new Tab("Szorofej");
	private HBox alaprajzTabElements = new HBox();
	private HBox szorofejTabElements = new HBox();

	private Button szorofejbtn = new Button("Szórófej kiválasztása");

	private ToggleButton hatarvonalbtn = new ToggleButton("Határvonal");
	private ToggleButton teglalapTereptargybtn = new ToggleButton("Téglalap");
	private ToggleButton korTereptargybtn = new ToggleButton("Kör");
	private ToggleGroup alaprajzButtons = new ToggleGroup();
	protected ColorPicker alaprajzColor = new ColorPicker();

	private CanvasPane canvasPane = new CanvasPane();
	private ZoomableScrollPane scrollPane = new ZoomableScrollPane(canvasPane);

	public Rajzolo() {

		getChildren().add(tabPane);
		tabPane.getTabs().addAll(alaprajzTab, szorofejTab);
		szorofejTab.setContent(szorofejTabElements);
		szorofejTabElements.getChildren().add(szorofejbtn);

		alaprajzTab.setContent(alaprajzTabElements);
		alaprajzButtons.getToggles().addAll(hatarvonalbtn, teglalapTereptargybtn, korTereptargybtn);
		alaprajzColor.setValue(Color.DARKGREEN);
		alaprajzTabElements.getChildren().addAll(alaprajzColor, hatarvonalbtn, teglalapTereptargybtn, korTereptargybtn);

		scrollPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		scrollPane.setFitToWidth(true);
		scrollPane.setPrefHeight(600);
		scrollPane.setMinHeight(USE_COMPUTED_SIZE);

		getChildren().add(scrollPane);

		alaprajzTab.setOnSelectionChanged(e -> {
			alaprajzButtons.selectToggle(null);
		});

		hatarvonalbtn.setOnMouseClicked(e -> {
			Polyline line = new Polyline();
			line.setStroke(alaprajzColor.getValue());
			canvasPane.lines.add(line);
			canvasPane.group.getChildren().add(line);
		});

		szorofejbtn.setOnAction(e -> {
			setSprinklerAttributes();
		});

		canvasPane.setOnMouseClicked(e -> {
			canvasPane.requestFocus();
			if (alaprajzButtons.getSelectedToggle() == null && canvasPane.sprinklerAttributesSet
					&& e.getButton() == MouseButton.PRIMARY) {
				canvasPane.drawNewSprinkler(e);
			} else if (alaprajzButtons.getSelectedToggle() == hatarvonalbtn) {
				canvasPane.lines.get(canvasPane.lines.size() - 1).getPoints().addAll(e.getX(), e.getY());

			} else
				canvasPane.selectElement(e);
			canvasPane.requestFocus();
		});

		canvasPane.setOnMousePressed(e -> {
			canvasPane.requestFocus();
			if (alaprajzButtons.getSelectedToggle() != null) {
				canvasPane.borderDrawingOn = true;
				canvasPane.startDrawingBorder(e);

			}
		});

		canvasPane.setOnMouseReleased(e -> {

			if (alaprajzButtons.getSelectedToggle() == teglalapTereptargybtn) {
				canvasPane.drawBorderRectanlge(e, alaprajzColor.getValue());
			} else if (alaprajzButtons.getSelectedToggle() == korTereptargybtn) {
				canvasPane.drawBorderCircle(e, alaprajzColor.getValue());
			}

		});

		canvasPane.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
			canvasPane.pressedKey = null;
			canvasPane.setCursor(Cursor.DEFAULT);
		});

		canvasPane.addEventFilter(KeyEvent.KEY_PRESSED, e -> {

			canvasPane.pressedKey = e.getCode();
			if (canvasPane.pressedKey.equals(KeyCode.ESCAPE)) {
				canvasPane.endSprinklerDrawing();
			}
		});

		canvasPane.setOnMouseMoved(e -> {
			canvasPane.showFocusCircle(e);

		});

		canvasPane.setOnMouseDragged(e -> {
			if (canvasPane.borderDrawingOn && alaprajzButtons.getSelectedToggle() == teglalapTereptargybtn)
				canvasPane.showtempRectanlge(e, alaprajzColor.getValue());
			else if (canvasPane.borderDrawingOn && alaprajzButtons.getSelectedToggle() == korTereptargybtn)
				canvasPane.showTempCircle(e, alaprajzColor.getValue());
			else if (canvasPane.borderDrawingOn && alaprajzButtons.getSelectedToggle() == hatarvonalbtn)
				canvasPane.drawBorderline(e, canvasPane.lines.get(canvasPane.lines.size() - 1),
						alaprajzColor.getValue());
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
					canvasPane.sprinklerRadius = Double.parseDouble(radiusField.getText()) * 50;
					canvasPane.sprinklerColor = colorPicker.getValue();
					canvasPane.sprinklerAttributesSet = true;
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
