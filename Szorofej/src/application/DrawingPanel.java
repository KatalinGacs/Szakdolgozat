package application;

import java.time.Duration;
import java.time.LocalTime;

import application.common.Common;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class DrawingPanel extends VBox {

	private TabPane tabPane = new TabPane();
	private Tab borderTab = new Tab("Alaprajz");
	private Tab sprinklerTab = new Tab("Szórófej");
	private Tab zoneTab = new Tab("Zónák");
	private HBox borderTabElements = new HBox();
	private HBox sprinklerTabElements = new HBox();
	private HBox zoneTabElements = new HBox();

	private Text borderColorText = new Text(" Vonalszín ");
	protected ColorPicker borderColor = new ColorPicker();
	private Text borderLineWidthText = new Text(" Vonalvastagság ");
	private Spinner<Integer> borderLineWidth = new Spinner<Integer>(1, 20, 5);
	private ToggleButton borderLineBtn = new ToggleButton("Határvonal");
	private ToggleButton obstacleRectangleBtn = new ToggleButton("Tereptárgy téglalap");
	private ToggleButton obstacleCircleBtn = new ToggleButton("Tereptárgy kör");
	private Text obstacleFillText = new Text("Tereptárgy kitöltés szín ");
	protected ColorPicker obstacleFillColor = new ColorPicker(Color.DARKGRAY);
	private Text obstacleStrokeText = new Text("Tereptárgy körvonal szín ");
	protected ColorPicker obstacleStrokeColor = new ColorPicker(Color.DARKGRAY);
	private ToggleGroup borderButtons = new ToggleGroup();

	private Button setSprinklerBtn = new Button("Szórófej kiválasztása");
	private ToggleButton drawSeveralSprinklerOptions = new ToggleButton("Több szórófej rajzolása egy vonalra");
	private Text numberOfSprinklerText = new Text(" Darab ");
	private Spinner<Integer> numberOfSprinklers = new Spinner<Integer>(1, 40, 3);
	private ToggleButton selectLine = new ToggleButton("Vonal kiválasztása");
	private Button showSprinklers = new Button("Mutasd");
	private Button drawSeveralSprinklers = new Button("OK, szögek beállítása");

	private Button zoneBtn = new Button("Zóna megadása");

	private CanvasPane canvasPane = new CanvasPane();
	private ZoomableScrollPane scrollPane = new ZoomableScrollPane(canvasPane);

	private BorderPane footer = new BorderPane();
	private HBox viewElements = new HBox();
	private ToggleButton showGrid = new ToggleButton("Rács");
	private ToggleButton showArcs = new ToggleButton("Szóráskép");
	private HBox infoBox = new HBox();
	private Text infoText = new Text("");

	private ToggleButton addHeads = new ToggleButton("Hozzáadás");
	private ToggleButton removeHeads = new ToggleButton("Törlés");
	private ToggleGroup addOrRemoveGroup = new ToggleGroup();
	private RadioButton selectIndividualHeads = new RadioButton("Egyes fejek kijelölése");
	private RadioButton selectTerritory = new RadioButton("Területbe esõ fejek kijelölése");
	private ToggleGroup selectionMethodGroup = new ToggleGroup();
	private Text numberOfSelectedHeadsField = new Text();
	private Text flowRateOfSelectedHeadsField = new Text();

	public DrawingPanel() {

		getChildren().add(tabPane);
		tabPane.getTabs().addAll(borderTab, sprinklerTab, zoneTab);
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		tabPane.setMinHeight(Common.pixelPerMeter * 2);
		borderLineWidth.setPrefWidth(60);

		borderTab.setContent(borderTabElements);
		borderButtons.getToggles().addAll(borderLineBtn, obstacleRectangleBtn, obstacleCircleBtn);
		borderTabElements.setAlignment(Pos.CENTER_LEFT);
		borderTabElements.setSpacing(10);
		borderColor.setValue(Color.LIMEGREEN);
		borderTabElements.getChildren().addAll(borderColorText, borderColor, borderLineWidthText, borderLineWidth,
				borderLineBtn, obstacleRectangleBtn, obstacleCircleBtn, obstacleStrokeText, obstacleStrokeColor,
				obstacleFillText, obstacleFillColor);

		sprinklerTab.setContent(sprinklerTabElements);
		sprinklerTabElements.getChildren().addAll(setSprinklerBtn, drawSeveralSprinklerOptions, selectLine,
				numberOfSprinklerText, numberOfSprinklers, showSprinklers, drawSeveralSprinklers);
		sprinklerTabElements.setAlignment(Pos.CENTER_LEFT);
		sprinklerTabElements.setSpacing(10);
		numberOfSprinklerText.setVisible(false);
		numberOfSprinklers.setVisible(false);
		showSprinklers.setVisible(false);
		selectLine.setVisible(false);
		drawSeveralSprinklers.setVisible(false);
		drawSeveralSprinklerOptions.setOnAction(e -> {
			if (drawSeveralSprinklerOptions.isSelected()) {
				numberOfSprinklers.setVisible(true);
				showSprinklers.setVisible(true);
				selectLine.setVisible(true);
				numberOfSprinklerText.setVisible(true);
				drawSeveralSprinklers.setVisible(true);
			} else {
				numberOfSprinklers.setVisible(false);
				showSprinklers.setVisible(false);
				selectLine.setVisible(false);
				numberOfSprinklerText.setVisible(false);
				drawSeveralSprinklers.setVisible(false);
			}
		});

		zoneTab.setContent(zoneTabElements);
		zoneTabElements.getChildren().addAll(zoneBtn);
		zoneTabElements.setAlignment(Pos.CENTER_LEFT);
		zoneTabElements.setSpacing(10);
		selectIndividualHeads.setToggleGroup(selectionMethodGroup);
		selectTerritory.setToggleGroup(selectionMethodGroup);
		addHeads.setToggleGroup(addOrRemoveGroup);
		removeHeads.setToggleGroup(addOrRemoveGroup);
		zoneBtn.setOnAction(e -> {
			setZones();
		});

		scrollPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		scrollPane.setFitToWidth(true);
		scrollPane.setPrefHeight(Common.primaryScreenBounds.getHeight() / 4 * 3);
		scrollPane.setMinHeight(USE_COMPUTED_SIZE);
		getChildren().add(scrollPane);

		footer.setLeft(viewElements);
		footer.setRight(infoBox);
		footer.setPadding(new Insets(10));
		viewElements.setMinHeight(Common.pixelPerMeter * 2);
		viewElements.setAlignment(Pos.CENTER_LEFT);
		viewElements.setSpacing(10);
		showGrid.setSelected(true);
		showArcs.setSelected(true);
		viewElements.getChildren().addAll(showGrid, showArcs);
		infoBox.getChildren().addAll(infoText);
		infoBox.setAlignment(Pos.CENTER_RIGHT);
		getChildren().add(footer);

		borderTab.setOnSelectionChanged(e -> {
			borderButtons.selectToggle(null);
			canvasPane.endLineDrawing();
		});

		setSprinklerBtn.setOnAction(e -> {
			canvasPane.setSprinklerAttributes();
		});

		drawSeveralSprinklers.setOnAction(e -> {
			if (!canvasPane.sprinklerAttributesSet)
				Common.showAlert("A szórófej típusa nincs kiválasztva!");
			else if (!canvasPane.lineSelected)
				Common.showAlert("A vonal nincs kiválasztva!");
			else
				canvasPane.drawSeveralSprinklers();
		});

		canvasPane.setOnMouseClicked(e -> {
			canvasPane.requestFocus();
			if (e.getButton() == MouseButton.PRIMARY && selectLine.isSelected()) {
				canvasPane.selectLineForSprinklerDrawing(e);
				if (canvasPane.lineSelected)
					selectLine.setSelected(false);
			} else if (borderButtons.getSelectedToggle() == null && canvasPane.sprinklerAttributesSet
					&& e.getButton() == MouseButton.PRIMARY) {
				canvasPane.drawNewSprinkler(e);
			} else if (e.getButton() == MouseButton.PRIMARY && borderButtons.getSelectedToggle() == borderLineBtn
					&& canvasPane.borderDrawingOn == false) {
				canvasPane.borderDrawingOn = true;
				canvasPane.startDrawingBorder(e);
			} else if (e.getButton() == MouseButton.PRIMARY && borderButtons.getSelectedToggle() == borderLineBtn
					&& canvasPane.borderDrawingOn) {
				canvasPane.drawBorderLine(e, borderColor.getValue(), borderLineWidth.getValue());
			} else if (e.getButton() == MouseButton.PRIMARY && canvasPane.zoneEditingOn && addHeads.isSelected()
					&& selectIndividualHeads.isSelected()) {
				canvasPane.selectIndiviualHeadsForZone(e, true, true);
				numberOfSelectedHeadsField.setText(canvasPane.selectedSprinklerShapes.size() + "");
				flowRateOfSelectedHeadsField.setText(String.format("%.2f", canvasPane.flowRateOfSelected));
			} else if (e.getButton() == MouseButton.PRIMARY && canvasPane.zoneEditingOn && removeHeads.isSelected()
					&& selectIndividualHeads.isSelected()) {
				canvasPane.selectIndiviualHeadsForZone(e, false, true);
				numberOfSelectedHeadsField.setText(canvasPane.selectedSprinklerShapes.size() + "");
				flowRateOfSelectedHeadsField.setText(String.format("%.2f", canvasPane.flowRateOfSelected));
			} else if (e.getButton() == MouseButton.SECONDARY) {
				canvasPane.selectElement(e);
			}
			e.consume();
			canvasPane.requestFocus();
		});

		canvasPane.setOnMousePressed(e -> {
			canvasPane.requestFocus();
			if ((borderButtons.getSelectedToggle() == obstacleRectangleBtn
					|| borderButtons.getSelectedToggle() == obstacleCircleBtn)
					&& e.getButton() == MouseButton.PRIMARY) {
				canvasPane.borderDrawingOn = true;
				canvasPane.startDrawingBorder(e);
			} else if (canvasPane.zoneEditingOn && selectTerritory.isSelected()) {
				canvasPane.startDrawingBorder(e);
			}
		});

		canvasPane.setOnMouseDragged(e -> {
			if ((canvasPane.borderDrawingOn && borderButtons.getSelectedToggle() == obstacleRectangleBtn)
					|| (canvasPane.zoneEditingOn && selectTerritory.isSelected()))
				canvasPane.showtempBorderRectanlge(e, obstacleStrokeColor.getValue(), obstacleFillColor.getValue());
			else if (canvasPane.borderDrawingOn && borderButtons.getSelectedToggle() == obstacleCircleBtn)
				canvasPane.showTempBorderCircle(e, obstacleStrokeColor.getValue(), obstacleFillColor.getValue());
		});

		canvasPane.setOnMouseReleased(e -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				if (borderButtons.getSelectedToggle() == obstacleRectangleBtn) {
					canvasPane.drawBorderRectanlge(e, obstacleStrokeColor.getValue(), obstacleFillColor.getValue(),
							borderLineWidth.getValue());
					canvasPane.borderDrawingOn = false;
				} else if (borderButtons.getSelectedToggle() == obstacleCircleBtn) {
					canvasPane.drawBorderCircle(e, obstacleStrokeColor.getValue(), obstacleFillColor.getValue(),
							borderLineWidth.getValue());
					canvasPane.borderDrawingOn = false;
				} else if (canvasPane.zoneEditingOn && selectTerritory.isSelected()) {
					if (addHeads.isSelected())
						canvasPane.selectIndiviualHeadsForZone(e, true, false);
					else
						canvasPane.selectIndiviualHeadsForZone(e, false, false);
				}
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
				canvasPane.endLineDrawing();
				borderButtons.selectToggle(null);
			}
		});

		canvasPane.setOnMouseMoved(e -> {
			infoText.setText(canvasPane.showInfos(e));
			canvasPane.showFocusCircle(e);
			canvasPane.showTempLine(e);
			Point2D mousePoint = new Point2D(e.getX(), e.getY());

			if (canvasPane.pressedKey == KeyCode.SHIFT) {
				for (Shape border : canvasPane.borderShapes) {
					if (border.contains(mousePoint)) {
						canvasPane.setCursor(Cursor.CROSSHAIR);
						break;
					} else
						canvasPane.setCursor(Cursor.DEFAULT);
				}
			}

			if (borderButtons.getSelectedToggle() == borderLineBtn && canvasPane.borderDrawingOn) {
				canvasPane.showTempBorderLine(e, borderColor.getValue());
				for (Shape border : canvasPane.borderShapes) {
					if (border instanceof Line) {
						if ((Math.abs(e.getX() - ((Line) border).getStartX()) < Common.pixelPerMeter / 2
								&& Math.abs(e.getY() - ((Line) border).getStartY()) < Common.pixelPerMeter / 2)) {
							canvasPane.setCursor(Cursor.CROSSHAIR);
							canvasPane.lineEndX = ((Line) border).getStartX();
							canvasPane.lineEndY = ((Line) border).getStartY();
							canvasPane.cursorNearLineEnd = true;
							break;
						} else if (Math.abs(e.getX() - ((Line) border).getEndX()) < Common.pixelPerMeter / 2
								&& Math.abs(e.getY() - ((Line) border).getEndY()) < Common.pixelPerMeter / 2) {
							canvasPane.setCursor(Cursor.CROSSHAIR);
							canvasPane.lineEndX = ((Line) border).getEndX();
							canvasPane.lineEndY = ((Line) border).getEndY();
							canvasPane.cursorNearLineEnd = true;
							break;
						} else {
							canvasPane.setCursor(Cursor.DEFAULT);
							canvasPane.cursorNearLineEnd = false;
						}
					}
				}
			}
			if (selectLine.isSelected()) {
				canvasPane.preparingForDrawingSeveralSprinklers = true;
				for (Shape border : canvasPane.borderShapes) {
					if (border instanceof Line && border.contains(e.getX(), e.getY())) {
						canvasPane.setCursor(Cursor.CROSSHAIR);
						break;
					} else
						canvasPane.setCursor(Cursor.DEFAULT);
				}
			}
		});

		showSprinklers.setOnAction(e -> {
			if (canvasPane.sprinklerAttributesSet) {
				canvasPane.showSprinklersInALine(numberOfSprinklers.getValue());
				selectLine.setSelected(false);
			} else
				Common.showAlert("Válaszd ki a szórófej típusát!");
		});

		showGrid.setOnAction(e -> {
			if (showGrid.isSelected())
				Common.showLayer(canvasPane.gridLayer);
			else
				Common.hideLayer(canvasPane.gridLayer);
		});
		showArcs.setOnAction(e -> {
			if (showArcs.isSelected())
				Common.showLayer(canvasPane.sprinklerArcLayer);
			else
				Common.hideLayer(canvasPane.sprinklerArcLayer);
		});
	}

	public void setZones() {
		canvasPane.sprinklerAttributesSet = false;
		canvasPane.zoneEditingOn = true;
		canvasPane.selectedSprinklerShapes.clear();
		numberOfSelectedHeadsField.setText("0");
		flowRateOfSelectedHeadsField.setText("0");

		Stage zoneStage = new Stage();
		zoneStage.setX(Common.primaryScreenBounds.getWidth() - 500);
		zoneStage.setY(100);
		GridPane zoneRoot = new GridPane();
		zoneRoot.setVgap(10);
		zoneRoot.setHgap(10);
		zoneRoot.setPadding(new Insets(10, 10, 10, 10));
		Scene zoneScene = new Scene(zoneRoot, 400, 300);
		zoneStage.setScene(zoneScene);
		zoneStage.setAlwaysOnTop(true);

		Text zoneNameText = new Text("Zóna elnevezése");
		TextField zoneNameTextField = new TextField();
		Text selectionMethodText = new Text("Kijelölés módja");
		Text numberOfSelectedHeadsText = new Text("Kijelölt szórófejek száma");
		Text flowRateOfSelectedHeadsText = new Text("Kijelölt fejek vízfogyasztása (liter/perc)");
		Text durationOfWatering = new Text("Zóna mûködésének napi idõtartama");
		HBox timePicker = new HBox();
		Spinner<Integer> hourPicker = new Spinner<Integer>(0, 5, 1);
		hourPicker.setPrefWidth(60);
		Text colon = new Text(" : ");
		Spinner<Integer> minutePicker = new Spinner<Integer>(0, 59, 0);
		minutePicker.setPrefWidth(60);
		timePicker.getChildren().addAll(hourPicker, colon, minutePicker);
		timePicker.setAlignment(Pos.CENTER_LEFT);

		HBox addOrRemoveContainer = new HBox(addHeads, removeHeads);
		Button createZoneBtn = new Button("Zóna létrehozása");

		selectIndividualHeads.setSelected(true);
		addHeads.setSelected(true);

		zoneRoot.add(zoneNameText, 0, 0);
		zoneRoot.add(zoneNameTextField, 1, 0);
		zoneRoot.add(selectionMethodText, 0, 1);
		zoneRoot.add(selectIndividualHeads, 0, 2);
		zoneRoot.add(selectTerritory, 0, 3);
		zoneRoot.add(numberOfSelectedHeadsText, 0, 4);
		zoneRoot.add(numberOfSelectedHeadsField, 1, 4);
		zoneRoot.add(flowRateOfSelectedHeadsText, 0, 5);
		zoneRoot.add(flowRateOfSelectedHeadsField, 1, 5);
		zoneRoot.add(addOrRemoveContainer, 0, 6);
		zoneRoot.add(durationOfWatering, 0, 7);
		zoneRoot.add(timePicker, 1, 7);
		zoneRoot.add(createZoneBtn, 0, 8);

		createZoneBtn.setOnAction(e -> {
			double durationInHours = hourPicker.getValue() + (minutePicker.getValue() / 60);
			if (zoneNameTextField.getText() == null || zoneNameTextField.getText().trim().isEmpty()) {
				Common.showAlert("Add meg a zóna nevét");
			} else if (canvasPane.selectedSprinklerShapes.isEmpty()) {
				Common.showAlert("Nincsenek kiválasztott szórófejek");
			} else {
				canvasPane.createZone(zoneNameTextField.getText(), durationInHours);
				zoneStage.close();
				// TODO: és a csövezéshez új stage-t nyitni itt?
			}
		});

		canvasPane.requestFocus();

		zoneStage.show();

	}

	public CanvasPane getCanvasPane() {
		return canvasPane;
	}

}
