package application;

import application.CanvasPane.Use;
import controller.SprinklerController;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.FileHandler;
import model.bean.PipeGraph.Edge;
import model.bean.SprinklerShape;
import utilities.Common;

/**
 * Layout containing the CanvasPane, a tabmenu with different options for
 * drawing on the CanvasPane and a footer with actual informations of the
 * drawing process.
 * 
 * @author Gacs Katalin
 *
 */
public class DrawingPanel extends VBox {

	SprinklerController controller;

	private HBox toolbar = new HBox();
	private Button newCanvas = new Button();
	private Button openCanvas = new Button();
	private Button saveCanvas = new Button();
	private Button undoButton = new Button();
	private Button redoButton = new Button();

	/**
	 * TabPane with tabs for different phases of drawing.
	 */
	private TabPane tabPane = new TabPane();

	/**
	 * Tab for the controls of border and obstacle drawing.
	 */
	private Tab borderTab = new Tab("Alaprajz");

	/**
	 * Container for controls under borderTab.
	 */
	private HBox borderTabElements = new HBox();

	/**
	 * Tab for the controls of sprinkler drawing.
	 */
	private Tab sprinklerTab = new Tab("Szórófej");

	/**
	 * Container for controls under sprinklerTab.
	 */
	private HBox sprinklerTabElements = new HBox();

	/**
	 * Tab for the controls of zone and pipe drawing.
	 */
	private Tab zoneTab = new Tab("Zónák");

	/**
	 * Container for controls under zoneTab.
	 */
	private HBox zoneTabElements = new HBox();

	/**
	 * Tab for other miscellanous controls.
	 */
	private Tab miscTab = new Tab("Egyéb");

	/**
	 * Container for controls under miscTab.
	 */
	private HBox miscTabElements = new HBox();

	/**
	 * ColorPicker with which the user can set the color of the border lines.
	 */
	ColorPicker borderColor = new ColorPicker();

	/**
	 * Text for borderColor ColorPicker.
	 */
	private Text borderColorText = new Text(" Vonalszín ");

	/**
	 * Spinner with which the user can set the width of the border lines.
	 */
	private Spinner<Integer> borderLineWidth = new Spinner<Integer>(1, 20, 5);

	/**
	 * Text for borderLineWidth Spinner
	 */
	private Text borderLineWidthText = new Text(" Vonalvastagság ");

	/**
	 * Group of togglebuttons borderLineBtn, obstacleRectangleBtn,
	 * obstacleCircleBtn.
	 */
	private ToggleGroup borderButtons = new ToggleGroup();

	/**
	 * By choosing this togglebutton, the user begins drawing lines and keeps
	 * drawing lines until 1. Escape is pressed, 2. another togglebutton of
	 * borderButtons is chosen, 3. another tab is chosen of tabPane.
	 */
	private ToggleButton borderLineBtn = new ToggleButton("Határvonal");

	/**
	 * By choosing this togglebutton, the user begins drawing rectangles and keeps
	 * drawing rectangles until 1. Escape is pressed, 2. another togglebutton of
	 * borderButtons is chosen, 3. another tab is chosen of tabPane.
	 */
	private ToggleButton obstacleRectangleBtn = new ToggleButton("Tereptárgy téglalap");

	/**
	 * By choosing this togglebutton, the user begins drawing circles and keeps
	 * drawing circles until 1. Escape is pressed, 2. another togglebutton of
	 * borderButtons is chosen, 3. another tab is chosen of tabPane.
	 */
	private ToggleButton obstacleCircleBtn = new ToggleButton("Tereptárgy kör");

	/**
	 * ColorPicker with which the user can set the fill color of the circle and
	 * rectanlge obstacles.
	 */
	ColorPicker obstacleFillColor = new ColorPicker(Color.DARKGRAY);

	/**
	 * Text for obstacleFillColor ColorPicker.
	 */
	private Text obstacleFillText = new Text("Tereptárgy kitöltés szín ");

	/**
	 * ColorPicker with which the user can set the stroke color of the circle and
	 * rectanlge obstacles.
	 */
	ColorPicker obstacleStrokeColor = new ColorPicker(Color.DARKGRAY);

	/**
	 * Text for obstacleStrokeColor ColorPicker.
	 */
	private Text obstacleStrokeText = new Text("Tereptárgy körvonal szín ");

	/**
	 * By choosing this button the user can add a custom text to the CanvasPane
	 */
	private Button textButton = new Button("Szöveg hozzáadása");

	/**
	 * By choosing this button a new stage opens where the user can choose the
	 * sprinklertype to draw and set its attributes.
	 */
	private Button setSprinklerBtn = new Button("Szórófej kiválasztása");

	/**
	 * By choosing this button the controls to draw several sprinklers are shown in
	 * sprinklerTab.
	 */
	private ToggleButton drawSeveralSprinklerToggleBtn = new ToggleButton("Több szórófej rajzolása egy vonalra");

	/**
	 * When the users chooses to draw several sprinklers in a line, with this
	 * Spinner the number os sprinklers can be set.
	 */
	private Spinner<Integer> numberOfSprinklers = new Spinner<Integer>(1, 40, 3);

	/**
	 * Text for numberOfSprinklers Spinner.
	 */
	private Text numberOfSprinklerText = new Text("Darab");

	/**
	 * When the users chooses to draw several sprinklers in a line, when this button
	 * is selected, the user can choose on which line the sprinklers will be drawn
	 * by clicking on a line on the canvasPane.
	 */
	private ToggleButton selectLine = new ToggleButton("Vonal kiválasztása");

	/**
	 * When the users chooses to draw several sprinklers in a line, and a line is
	 * selected and the number of sprinklers is set, clicking this button shows a
	 * preview of the spinklers.
	 */
	private Button showSprinklers = new Button("Mutasd");

	/**
	 * When the users chooses to draw several sprinklers in a line, and a line is
	 * selected and the number of sprinklers is set, by clicking this button the
	 * user can begin setting the sprinklers angles.
	 */
	private Button drawSeveralSprinklers = new Button("OK, szögek beállítása");

	/**
	 * Clicking this button opens a new stage where a user can create a new zone.
	 */
	private Button zoneBtn = new Button("Zóna megadása");

	/**
	 * Clicking this button opens a new stage with the help of which the user can
	 * draw pipes.
	 */
	private Button pipeBtn = new Button("Csövezés");

	/**
	 * Clicking this button opens a new stage where the materials for the plan drawn
	 * so far are summarized and can be exported to XLS.
	 */
	private Button summarizeBtn = new Button("Terv összegzés");

	/**
	 * The CanvasPane on which the user draws.
	 */
	private CanvasPane canvasPane;

	/**
	 * An extension of ScrollPane that can be zoomed by scrolling the mouse. In this
	 * the CanvasPane is put so the drawing is zoomable.
	 */
	private ZoomableScrollPane scrollPane;

	/**
	 * A container for showing actual informations about the drawing and for buttons
	 * where the user can set which layer is visible.
	 */
	private BorderPane footer = new BorderPane();

	/**
	 * Container for buttons where the user can set which layer is visible.
	 */
	private HBox viewElements = new HBox();

	/**
	 * ToggleButton to show/hide the grid on the drawing.
	 */
	private ToggleButton showGrid = new ToggleButton("Rács");

	/**
	 * ToggleButton to show/hide the arcs of sprinklers on the drawing.
	 */
	private ToggleButton showArcs = new ToggleButton("Szóráskép");

	/**
	 * ToggleButton to show/hide the texts on the drawing.
	 */
	private ToggleButton showTexts = new ToggleButton("Szövegek");

	/**
	 * Textfield for inputing different parameters while drawing e.g. length of a
	 * line, extent of an arc etc.
	 */
	private TextField drawingInputField = new TextField();

	/**
	 * Container for general informations about the drawing e.g. scale, mouse
	 * coordinates.
	 */
	private HBox generalInfoBox = new HBox();

	/**
	 * Text for general informations about the drawing e.g. scale, mouse
	 * coordinates.
	 */
	private Text generalInfoText = new Text("");

	/**
	 * Container for informations about the set sprinkler type and its attributes in
	 * sprinkler drawing.
	 */
	private HBox sprinklerInfoBox = new HBox();

	/**
	 * Text for informations about the set sprinkler type and its attributes in
	 * sprinkler drawing.
	 */
	private Text sprinklerInfoText = new Text("");

	/**
	 * When a zone is created, after selecting this button selected sprinkler heads
	 * are added to the zone.
	 */
	private ToggleButton addHeads = new ToggleButton("Hozzáadás");

	/**
	 * When a zone is created, after selecting this button selected sprinkler heads
	 * are deleted from the zone.
	 */
	private ToggleButton removeHeads = new ToggleButton("Törlés");

	/**
	 * ToggleGroup for ToggleButtons addHeads and removeHeads.
	 */
	private ToggleGroup addOrRemoveGroup = new ToggleGroup();

	/**
	 * When a zone is created this text shows the number of selected sprinklerheads.
	 */
	private Text numberOfSelectedHeadsText = new Text();

	/**
	 * When a zone is created this text shows the total flow rate of selected
	 * sprinklerheads.
	 */
	private Text flowRateOfSelectedHeadsText = new Text();

	/**
	 * Create a DrawingPanel. Set the controls under the tabs and in the footer. Set
	 * the canvasPane's action handlers.
	 * 
	 * @param dataController
	 */
	public DrawingPanel(SprinklerController dataController) {
		try {
			controller = dataController;
			canvasPane = new CanvasPane(controller);
			scrollPane = new ZoomableScrollPane(canvasPane);

			canvasPane.setDrawingInputField(drawingInputField);

			// set the toolbar and its buttons
			getChildren().add(toolbar);
			toolbar.getChildren().addAll(newCanvas, openCanvas, saveCanvas, undoButton, redoButton);

			ImageView newImage = new ImageView(new Image(Common.getSourceFolder() + "/img/new.png"));
			newCanvas.setGraphic(newImage);
			newCanvas.setTooltip(new Tooltip("Új (Ctrl + N)"));
			newCanvas.setOnAction(e -> {
				FileHandler.newCanvas(canvasPane);
			});

			ImageView openImage = new ImageView(new Image(Common.getSourceFolder() + "/img/open.png"));
			openCanvas.setGraphic(openImage);
			openCanvas.setTooltip(new Tooltip("Megnyitás"));
			openCanvas.setOnAction(e -> {
				FileHandler.loadCanvas(canvasPane, null);
			});

			ImageView saveImage = new ImageView(new Image(Common.getSourceFolder() + "/img/save.png"));
			saveCanvas.setGraphic(saveImage);
			saveCanvas.setTooltip(new Tooltip("Mentés (Ctrl + S)"));
			saveCanvas.setOnAction(e -> {
				FileHandler.saveCanvas(null, canvasPane, false);
			});

			ImageView undoImage = new ImageView(new Image(Common.getSourceFolder() + "/img/undo.png"));
			undoButton.setGraphic(undoImage);
			undoButton.setTooltip(new Tooltip("Visszavonás (Ctrl + Z)"));
			undoButton.setOnAction(e -> {
				UndoManager.getInstance().undo();
			});

			ImageView redoImage = new ImageView(new Image(Common.getSourceFolder() + "/img/redo.png"));
			redoButton.setGraphic(redoImage);
			redoButton.setTooltip(new Tooltip("Újra (Ctrl + Y)"));
			redoButton.setOnAction(e -> {
				UndoManager.getInstance().redo();
			});

			// order the tabs under the tabpane
			getChildren().add(tabPane);
			tabPane.getTabs().addAll(borderTab, sprinklerTab, zoneTab, miscTab);
			tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
			tabPane.setMinHeight(Common.pixelPerMeter * 2);
			// by changing the selected tab in the tabpane the current drawing activity is
			// disrupted
			tabPane.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> {
				canvasPane.setStateOfCanvasUse(Use.NONE);
				canvasPane.setSprinklerAttributesSet(false);
				SprinklerDrawing.endSprinklerDrawing(canvasPane);
				sprinklerInfoText.setText("");
			});

			// set the elements of borderTab
			borderTab.setContent(borderTabElements);
			borderButtons.getToggles().addAll(borderLineBtn, obstacleRectangleBtn, obstacleCircleBtn);
			borderLineBtn.setTooltip(new Tooltip("Határvonal"));
			obstacleCircleBtn.setTooltip(new Tooltip("Tereptárgy kör"));
			obstacleCircleBtn.setTooltip(new Tooltip("Tereptárgy téglalap"));
			borderTabElements.setAlignment(Pos.CENTER_LEFT);
			borderTabElements.setSpacing(10);
			borderColor.setValue(Color.LIMEGREEN);
			borderLineWidth.setPrefWidth(70);
			borderTabElements.getChildren().addAll(borderColorText, borderColor, borderLineWidthText, borderLineWidth,
					borderLineBtn, obstacleRectangleBtn, obstacleCircleBtn, obstacleStrokeText, obstacleStrokeColor,
					obstacleFillText, obstacleFillColor, textButton);
			borderTab.setOnSelectionChanged(e -> {
				borderButtons.selectToggle(null);
				canvasPane.endLineDrawing();
			});

			// set the elements of srpinklerTab
			sprinklerTab.setContent(sprinklerTabElements);
			sprinklerTabElements.getChildren().addAll(setSprinklerBtn, drawSeveralSprinklerToggleBtn, selectLine,
					numberOfSprinklerText, numberOfSprinklers, showSprinklers, drawSeveralSprinklers);
			sprinklerTabElements.setAlignment(Pos.CENTER_LEFT);
			setSprinklerBtn.setTooltip(new Tooltip("Szórófej kiválasztása"));
			drawSeveralSprinklerToggleBtn.setTooltip(new Tooltip("Több szórófej rajzolása egy vonalra"));
			sprinklerTabElements.setSpacing(10);
			numberOfSprinklerText.setVisible(false);
			numberOfSprinklers.setVisible(false);
			showSprinklers.setVisible(false);
			selectLine.setVisible(false);
			drawSeveralSprinklers.setVisible(false);
			drawSeveralSprinklerToggleBtn.setOnAction(e -> {
				if (drawSeveralSprinklerToggleBtn.isSelected()) {
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

			setSprinklerBtn.setOnAction(e -> {
				canvasPane.setSprinklerAttributes();
			});

			showSprinklers.setOnAction(e -> {
				showSprinklersInALine();
			});

			drawSeveralSprinklers.setOnAction(e -> {
				if (!canvasPane.isSprinklerAttributesSet()) {
					Common.showAlert("A szórófej típusa nincs kiválasztva!");
				} else if (!canvasPane.lineSelected) {
					Common.showAlert("A vonal nincs kiválasztva!");
				} else {
					showSprinklersInALine();
					SprinklerDrawing.drawSeveralSprinklers(canvasPane);
				}
			});

			// set the elements of zonetab
			zoneTab.setContent(zoneTabElements);
			zoneTabElements.getChildren().addAll(zoneBtn, pipeBtn, summarizeBtn);
			zoneTabElements.setAlignment(Pos.CENTER_LEFT);
			zoneTabElements.setSpacing(10);
			addHeads.setToggleGroup(addOrRemoveGroup);
			removeHeads.setToggleGroup(addOrRemoveGroup);
			zoneBtn.setOnAction(e -> {
				setZones();
			});
			pipeBtn.setOnAction(e -> {
				setPipes();
			});
			summarizeBtn.setOnAction(e -> {
				MaterialSumStage materialSumStage = new MaterialSumStage(canvasPane.controller);
				materialSumStage.show();
			});

			// set the elements of miscTab
			miscTab.setContent(miscTabElements);
			miscTabElements.getChildren().addAll(textButton);
			miscTabElements.setAlignment(Pos.CENTER_LEFT);
			miscTabElements.setSpacing(10);
			textButton.setOnAction(e -> {
				TextEditing.openTextFormatStage(canvasPane);
			});

			// set the scrollpane with the canvaspane
			getChildren().add(scrollPane);

			// set the footer and handle showing/hiding layers
			footer.setLeft(viewElements);
			footer.setRight(generalInfoBox);
			footer.setCenter(sprinklerInfoBox);
			footer.setPadding(new Insets(10));
			viewElements.setMinHeight(Common.pixelPerMeter * 2);
			viewElements.setAlignment(Pos.CENTER_LEFT);
			viewElements.setSpacing(10);
			showGrid.setSelected(true);
			showArcs.setSelected(true);
			showTexts.setSelected(true);
			drawingInputField.setVisible(false);
			viewElements.getChildren().addAll(showGrid, showArcs, showTexts, drawingInputField);
			sprinklerInfoBox.getChildren().addAll(sprinklerInfoText);
			sprinklerInfoBox.setAlignment(Pos.CENTER);
			generalInfoBox.getChildren().addAll(generalInfoText);
			generalInfoBox.setAlignment(Pos.CENTER_RIGHT);
			getChildren().add(footer);

			// handle user interactions on the canvasPane
			canvasPane.setOnMouseClicked(e -> {
				canvasPane.requestFocus();

				// delete selection
				if (canvasPane.getSelectedShape() != null) {
					canvasPane.getSelectedShape().setStroke(canvasPane.getOriginalStrokeColorOfSelectedShape());
					canvasPane.setSelectedShape(null);
				}

				if (e.getButton() == MouseButton.PRIMARY) {
					if (selectLine.isSelected()) {
						SprinklerDrawing.selectLineForSprinklerDrawing(e, canvasPane);
						if (canvasPane.lineSelected)
							selectLine.setSelected(false);
					} else if (canvasPane.getStateOfCanvasUse() == Use.SPRINKLERDRAWING) {
						SprinklerDrawing.drawNewSprinkler(e, canvasPane);
					} else if (borderButtons.getSelectedToggle() == borderLineBtn
							&& canvasPane.getStateOfCanvasUse() != Use.BORDERDRAWING) {
						canvasPane.setStateOfCanvasUse(Use.BORDERDRAWING);
						BorderDrawing.startDrawingBorder(e);
					} else if (borderButtons.getSelectedToggle() == borderLineBtn
							&& canvasPane.getStateOfCanvasUse() == Use.BORDERDRAWING) {
						BorderDrawing.drawBorderLine(new Point2D(e.getX(), e.getY()), borderColor.getValue(),
								borderLineWidth.getValue(), canvasPane);
					} else if (canvasPane.getStateOfCanvasUse() == Use.ZONEEDITING) {
						if (addHeads.isSelected()) {
							canvasPane.selectHeadsForZone(e, true, true);
						} else if (removeHeads.isSelected()) {
							canvasPane.selectHeadsForZone(e, false, true);
						}
						updateZoneInfos();
					} else if (canvasPane.getStateOfCanvasUse() == Use.PREPAREFORPIPEDRAWING) {
						PipeDrawing.startDrawingPipeLine(e, canvasPane);
					} else if (canvasPane.getStateOfCanvasUse() == Use.PIPEDRAWING) {
						PipeDrawing.drawPipeLine(e, canvasPane);
					} else if (canvasPane.getStateOfCanvasUse() == Use.PREPAREFORTEXTEDITING) {
						TextEditing.startWritingText(e, canvasPane);
					}
				} else if (e.getButton() == MouseButton.SECONDARY) {
					canvasPane.selectElement(e);
				}
				e.consume();
				if (canvasPane.getStateOfCanvasUse() != Use.PREPAREFORTEXTEDITING)
					canvasPane.requestFocus();
			});

			canvasPane.setOnMousePressed(e -> {
				canvasPane.requestFocus();
				if ((borderButtons.getSelectedToggle() == obstacleRectangleBtn
						|| borderButtons.getSelectedToggle() == obstacleCircleBtn)
						&& e.getButton() == MouseButton.PRIMARY) {
					canvasPane.setStateOfCanvasUse(Use.BORDERDRAWING);
					BorderDrawing.startDrawingBorder(e);
				} else if (canvasPane.getStateOfCanvasUse() == Use.ZONEEDITING) {
					BorderDrawing.startDrawingBorder(e);
				}
			});

			canvasPane.setOnMouseDragged(e -> {
				if (canvasPane.getStateOfCanvasUse() == Use.NONE) {
					scrollPane.setPannable(true);
				} else {
					scrollPane.setPannable(false);
				}

				if ((canvasPane.getStateOfCanvasUse() == Use.BORDERDRAWING
						&& borderButtons.getSelectedToggle() == obstacleRectangleBtn)
						|| (canvasPane.getStateOfCanvasUse() == Use.ZONEEDITING))
					BorderDrawing.showtempBorderRectanlge(e, obstacleStrokeColor.getValue(),
							obstacleFillColor.getValue(), canvasPane);
				else if (canvasPane.getStateOfCanvasUse() == Use.BORDERDRAWING
						&& borderButtons.getSelectedToggle() == obstacleCircleBtn)
					BorderDrawing.showTempBorderCircle(e, obstacleStrokeColor.getValue(), obstacleFillColor.getValue(),
							canvasPane);
			});

			canvasPane.setOnMouseReleased(e -> {
				if (e.getButton() == MouseButton.PRIMARY) {
					if (borderButtons.getSelectedToggle() == obstacleRectangleBtn) {
						BorderDrawing.drawBorderRectanlge(e, obstacleStrokeColor.getValue(),
								obstacleFillColor.getValue(), borderLineWidth.getValue(), canvasPane);
						canvasPane.setStateOfCanvasUse(Use.NONE);
					} else if (borderButtons.getSelectedToggle() == obstacleCircleBtn) {
						BorderDrawing.drawBorderCircle(e, obstacleStrokeColor.getValue(), obstacleFillColor.getValue(),
								borderLineWidth.getValue(), canvasPane);
						canvasPane.setStateOfCanvasUse(Use.NONE);
					} else if (canvasPane.getStateOfCanvasUse() == Use.ZONEEDITING) {
						if (addHeads.isSelected()) {
							canvasPane.selectHeadsForZone(e, true, false);
							updateZoneInfos();
						} else {
							canvasPane.selectHeadsForZone(e, false, false);
							updateZoneInfos();
						}
					}
				}
			});

			canvasPane.setOnMouseMoved(e -> {
				generalInfoText.setText(canvasPane.generalInfos(e));

				if (canvasPane.isSprinklerAttributesSet()
						&& tabPane.getSelectionModel().getSelectedItem() == sprinklerTab) {
					sprinklerInfoText.setText(canvasPane.sprinklerInfos());
				}

				canvasPane.showFocusCircle(e);
				SprinklerDrawing.showTempLine(e, canvasPane);
				Point2D mousePoint = new Point2D(e.getX(), e.getY());

				if (canvasPane.getStateOfCanvasUse() == Use.SPRINKLERDRAWING) {
					SprinklerDrawing.showTempSprinklingCircle(e, canvasPane);
				}

				if (canvasPane.getStateOfCanvasUse() == Use.PREPAREFORTEXTEDITING) {
					canvasPane.setCursor(Cursor.TEXT);
				}

				if (canvasPane.getPressedKey() == KeyCode.SHIFT) {
					for (Shape border : controller.listBorderShapes()) {
						if (border.contains(mousePoint)) {
							canvasPane.setCursor(Cursor.CROSSHAIR);
							break;
						} else
							canvasPane.setCursor(Cursor.DEFAULT);
					}
				}

				if (borderButtons.getSelectedToggle() == borderLineBtn
						&& canvasPane.getStateOfCanvasUse() == Use.BORDERDRAWING) {
					BorderDrawing.showTempBorderLine(e, borderColor.getValue(), canvasPane);
					for (Shape border : controller.listBorderShapes()) {
						if (border instanceof Line) {
							if ((Math.abs(e.getX() - ((Line) border).getStartX()) < Common.pixelPerMeter / 2
									&& Math.abs(e.getY() - ((Line) border).getStartY()) < Common.pixelPerMeter / 2)) {
								canvasPane.setCursor(Cursor.CROSSHAIR);
								BorderDrawing.lineEndX = ((Line) border).getStartX();
								BorderDrawing.lineEndY = ((Line) border).getStartY();
								canvasPane.cursorNearLineEnd = true;
								break;
							} else if (Math.abs(e.getX() - ((Line) border).getEndX()) < Common.pixelPerMeter / 2
									&& Math.abs(e.getY() - ((Line) border).getEndY()) < Common.pixelPerMeter / 2) {
								canvasPane.setCursor(Cursor.CROSSHAIR);
								BorderDrawing.lineEndX = ((Line) border).getEndX();
								BorderDrawing.lineEndY = ((Line) border).getEndY();
								canvasPane.cursorNearLineEnd = true;
								break;
							} else {
								canvasPane.setCursor(Cursor.DEFAULT);
								canvasPane.cursorNearLineEnd = false;
							}
						}
					}
				} else if (canvasPane.getStateOfCanvasUse() == Use.PIPEDRAWING
						|| canvasPane.getStateOfCanvasUse() == Use.PREPAREFORPIPEDRAWING) {
					if (canvasPane.getStateOfCanvasUse() == Use.PIPEDRAWING) {
						BorderDrawing.showTempBorderLine(e, CanvasPane.getPipeLineColor(), canvasPane);
						for (SprinklerShape s : controller.listSprinklerShapes()) {
							if ((Math.abs(e.getX() - s.getCircle().getCenterX()) < Common.pixelPerMeter / 2
									&& Math.abs(e.getY() - (s.getCircle().getCenterY())) < Common.pixelPerMeter / 2)) {
								canvasPane.setCursor(Cursor.CROSSHAIR);
								CanvasPane.sprinklerHeadX = s.getCircle().getCenterX();
								CanvasPane.sprinklerHeadY = s.getCircle().getCenterY();
								canvasPane.cursorNearSprinklerHead = true;
								canvasPane.sprinklerShapeNearCursor = s;
								break;
							} else {
								canvasPane.setCursor(Cursor.DEFAULT);
								canvasPane.cursorNearSprinklerHead = false;
							}
						}
					}
					if (!canvasPane.cursorNearSprinklerHead) {
						for (Edge line : canvasPane.pipeGraphUnderEditing.getEdges()) {
							if (line.contains(e.getX(), e.getY())) {
								canvasPane.setCursor(Cursor.CROSSHAIR);
								PipeDrawing.lineBreakPointX = e.getX();
								PipeDrawing.lineBreakPointY = e.getY();
								canvasPane.cursorOnPipeLine = true;
								PipeDrawing.pipeLineToSplit = line;
								break;
							} else {
								canvasPane.setCursor(Cursor.DEFAULT);
								canvasPane.cursorOnPipeLine = false;
							}
						}
					}
				}

				if (selectLine.isSelected()) {
					canvasPane.setStateOfCanvasUse(Use.PREPAREFORDRAWINGSEVERALSPRINKLERS);
					for (Shape border : controller.listBorderShapes()) {
						if (border instanceof Line && border.contains(e.getX(), e.getY())) {
							canvasPane.setCursor(Cursor.CROSSHAIR);
							break;
						} else
							canvasPane.setCursor(Cursor.DEFAULT);
					}
				}
			});

			canvasPane.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
				canvasPane.setPressedKey(null);
				canvasPane.setCursor(Cursor.DEFAULT);
			});

			canvasPane.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
				canvasPane.setPressedKey(e.getCode());
				if (canvasPane.getPressedKey().equals(KeyCode.ESCAPE)) {
					SprinklerDrawing.endSprinklerDrawing(canvasPane);
					sprinklerInfoText.setText("");
					canvasPane.endLineDrawing();
					borderButtons.selectToggle(null);
					if (canvasPane.cursorNearSprinklerHead) {
						canvasPane.cursorNearSprinklerHead = false;
					}
				}
			});

			canvasPane.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
				if (drawingInputField.isVisible() && canvasPane.getPressedKey().isDigitKey()) {
					drawingInputField.requestFocus();
				}
			});

			
			drawingInputField.setOnKeyPressed(e -> {
				if (e.getCode() == KeyCode.ENTER) {
					canvasPane.requestFocus();
					BorderDrawing.drawBorderLine(
							new Point2D(BorderDrawing.tempBorderLine.getEndX(),
									BorderDrawing.tempBorderLine.getEndY()),
							borderColor.getValue(), borderLineWidth.getValue(), canvasPane);
				}
			});
			
			// show or hide grid layer
			showGrid.setOnAction(e -> {
				if (showGrid.isSelected())
					Common.showLayer(canvasPane.getGridLayer());
				else
					Common.hideLayer(canvasPane.getGridLayer());
			});

			// show or hide arcs of sprinklers
			showArcs.setOnAction(e -> {
				if (showArcs.isSelected())
					Common.showLayer(canvasPane.getSprinklerArcLayer());
				else
					Common.hideLayer(canvasPane.getSprinklerArcLayer());
			});

			// show or hide texts
			showTexts.setOnAction(e -> {
				if (showTexts.isSelected()) {
					Common.showLayer(canvasPane.getTextLayer());
					Common.showLayer(canvasPane.getPipeTextLayer());
				} else {
					Common.hideLayer(canvasPane.getTextLayer());
					Common.hideLayer(canvasPane.getPipeTextLayer());
				}
			});
		} catch (Exception ex) {
			utilities.Error.HandleException(ex);
		}
	}

	/**
	 * Show the stage for creating zones.
	 */
	private void setZones() {
		try {
			canvasPane.setSprinklerAttributesSet(false);
			canvasPane.setStateOfCanvasUse(Use.ZONEEDITING);
			canvasPane.selectedSprinklerShapes.clear();
			numberOfSelectedHeadsText.setText("0");
			flowRateOfSelectedHeadsText.setText("0");
			Stage zoneStage = new ZoneStage(canvasPane, addHeads, removeHeads, numberOfSelectedHeadsText,
					flowRateOfSelectedHeadsText);
			canvasPane.requestFocus();
			zoneStage.show();
		} catch (Exception ex) {
			utilities.Error.HandleException(ex);
		}
	}

	/**
	 * Update the actual informations on the stage for creating zones according to
	 * the selected sprinklerheads.
	 */
	private void updateZoneInfos() {
		try {
			numberOfSelectedHeadsText.setText(canvasPane.selectedSprinklerShapes.size() + "");
			flowRateOfSelectedHeadsText.setText(String.format("%.2f", canvasPane.flowRateOfSelected));
		} catch (Exception ex) {
			utilities.Error.HandleException(ex);
		}
	}

	/**
	 * Show the stage for drawing pipes.
	 */
	private void setPipes() {
		try {
			canvasPane.setSprinklerAttributesSet(false);
			canvasPane.setStateOfCanvasUse(Use.NONE);
			Stage pipeStage = new PipeStage(canvasPane);
			canvasPane.requestFocus();
			pipeStage.show();
		} catch (Exception ex) {
			utilities.Error.HandleException(ex);
		}
	}

	/**
	 * When drawing several sprinklers in one line, show a preview of the
	 * sprinklers.
	 */
	private void showSprinklersInALine() {
		try {
			if (canvasPane.isSprinklerAttributesSet()) {
				SprinklerDrawing.showSprinklersInALine(numberOfSprinklers.getValue(), canvasPane);
				selectLine.setSelected(false);
			} else
				Common.showAlert("Válaszd ki a szórófej típusát!");
		} catch (Exception ex) {
			utilities.Error.HandleException(ex);
		}
	}

	public CanvasPane getCanvasPane() {
		return canvasPane;
	}

}
