package application;

import application.common.Common;
import application.common.DecimalCellFactory;
import controller.SprinklerController;
import controller.SprinklerControllerImpl;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.bean.SprinklerGroup;
import model.bean.SprinklerType;

public class DrawingPanel extends VBox {

	private SprinklerController controller = new SprinklerControllerImpl();

	private TabPane tabPane = new TabPane();
	private Tab borderTab = new Tab("Alaprajz");
	private Tab sprinklerTab = new Tab("Szorofej");
	private HBox borderTabElements = new HBox();
	private HBox sprinklerTabElements = new HBox();

	private Text borderColorText = new Text(" Vonalszín ");
	protected ColorPicker borderColor = new ColorPicker();
	private Text borderLineWidthText = new Text(" Vonalvastagság ");
	private Spinner<Integer> borderLineWidth = new Spinner<Integer>(1, 10, 5);
	private ToggleButton borderLineBtn = new ToggleButton("Határvonal");
	private ToggleButton borderRectangleBtn = new ToggleButton("Tereptárgy téglalap");
	private ToggleButton borderCircleBtn = new ToggleButton("Tereptárgy kör");
	private Text borderFillText = new Text("Tereptárgy kitöltés szín ");
	protected ColorPicker borderFillColor = new ColorPicker(Color.TRANSPARENT);
	private ToggleGroup borderButtons = new ToggleGroup();

	private Button setSprinklerBtn = new Button("Szórófej kiválasztása");
	private ToggleButton drawSeveralSprinklerOptions = new ToggleButton("Több szórófej rajzolása egy vonalra");
	private Text numberOfSprinklerText = new Text(" Darab ");
	private Spinner<Integer> numberOfSprinklers = new Spinner<Integer>(1, 40, 3);
	private ToggleButton selectLine = new ToggleButton("Vonal kiválasztása");
	private Button showSprinklers = new Button("Mutasd");
	private Button drawSeveralSprinklers = new Button("OK, szögek beállítása");

	private CanvasPane canvasPane = new CanvasPane();
	private ZoomableScrollPane scrollPane = new ZoomableScrollPane(canvasPane);

	private HBox viewElements = new HBox();
	private ToggleButton showGrid = new ToggleButton("Rács");
	private ToggleButton showArcs = new ToggleButton("Szóráskép");

	public DrawingPanel() {

		getChildren().add(tabPane);
		tabPane.getTabs().addAll(borderTab, sprinklerTab);
		tabPane.setMinHeight(50);

		borderTab.setContent(borderTabElements);
		borderButtons.getToggles().addAll(borderLineBtn, borderRectangleBtn, borderCircleBtn);
		borderColor.setValue(Color.LIMEGREEN);
		borderTabElements.getChildren().addAll(borderColorText, borderColor, borderLineWidthText, borderLineWidth,
				borderLineBtn, borderRectangleBtn, borderCircleBtn, borderFillText, borderFillColor);
		borderTabElements.setAlignment(Pos.CENTER_LEFT);

		sprinklerTab.setContent(sprinklerTabElements);
		sprinklerTabElements.getChildren().addAll(setSprinklerBtn, drawSeveralSprinklerOptions, selectLine,
				numberOfSprinklerText, numberOfSprinklers, showSprinklers, drawSeveralSprinklers);
		sprinklerTabElements.setAlignment(Pos.CENTER_LEFT);
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

		scrollPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		scrollPane.setFitToWidth(true);
		scrollPane.setPrefHeight(Common.primaryScreenBounds.getHeight() / 4 * 3);
		scrollPane.setMinHeight(USE_COMPUTED_SIZE);
		getChildren().add(scrollPane);

		viewElements.setMinHeight(25);
		showGrid.setSelected(true);
		showArcs.setSelected(true);
		viewElements.getChildren().addAll(showGrid, showArcs);
		getChildren().add(viewElements);

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

			} else if (e.getButton() == MouseButton.SECONDARY) {
				canvasPane.selectElement(e);
			}

			e.consume();
			canvasPane.requestFocus();
		});

		canvasPane.setOnMousePressed(e -> {
			canvasPane.requestFocus();
			if ((borderButtons.getSelectedToggle() == borderRectangleBtn
					|| borderButtons.getSelectedToggle() == borderCircleBtn) && e.getButton() == MouseButton.PRIMARY) {
				canvasPane.borderDrawingOn = true;
				canvasPane.startDrawingBorder(e);
			}
		});

		canvasPane.setOnMouseReleased(e -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				if (borderButtons.getSelectedToggle() == borderRectangleBtn) {
					canvasPane.drawBorderRectanlge(e, borderColor.getValue(), borderFillColor.getValue(),
							borderLineWidth.getValue());
					canvasPane.borderDrawingOn = false;
				} else if (borderButtons.getSelectedToggle() == borderCircleBtn) {
					canvasPane.drawBorderCircle(e, borderColor.getValue(), borderFillColor.getValue(),
							borderLineWidth.getValue());
					canvasPane.borderDrawingOn = false;
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

		canvasPane.setOnMouseDragged(e -> {
			if (canvasPane.borderDrawingOn && borderButtons.getSelectedToggle() == borderRectangleBtn)
				canvasPane.showtempBorderRectanlge(e, borderColor.getValue());
			else if (canvasPane.borderDrawingOn && borderButtons.getSelectedToggle() == borderCircleBtn)
				canvasPane.showTempBorderCircle(e, borderColor.getValue());
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

	public CanvasPane getCanvasPane() {
		return canvasPane;
	}

}
