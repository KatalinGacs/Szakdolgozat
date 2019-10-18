package application;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import application.common.Common;
import application.common.DecimalCellFactory;
import controller.SprinklerController;
import controller.SprinklerControllerImpl;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.bean.SprinklerGroup;
import model.bean.SprinklerShape;
import model.bean.SprinklerType;

public class CanvasPane extends Pane {

	SprinklerController controller = new SprinklerControllerImpl();

	protected KeyCode pressedKey;

	private double borderX, borderY;

	private double centerX = 0, centerY = 0, firstX = 0, firstY = 0, secondX = 0, secondY = 0;
	double startAngle, arcExtent;
	private Color sprinklerColor = Color.BLUE;
	private int strokeWidth = (int) (Common.pixelPerMeter / 12);

	protected SprinklerType sprinklerType;
	protected double sprinklerRadius;

	private TextField angleInput = new TextField();
	private TextField lengthInput = new TextField();

	Group bordersLayer = new Group();
	Group irrigationLayer = new Group();
	Group sprinklerArcLayer = new Group();
	Group gridLayer = new Group();

	private Line tempFirstSprinklerLine = new Line();
	private Line tempSecondSprinklerLine = new Line();

	private Circle focusCircle = new Circle(Common.pixelPerMeter / 3);
	private boolean showingFocusCircle = false;
	private Color tempLineColor = Color.DARKTURQUOISE;

	private Line tempBorderLine = new Line();
	private Circle tempSprinklerCircle = new Circle(Common.pixelPerMeter / 4);
	private Rectangle tempRectangle = new Rectangle();
	private Circle tempCircle = new Circle();

	protected boolean borderDrawingOn = false;

	protected boolean sprinklerAttributesSet = false;

	protected boolean cursorNearLineEnd = false;
	protected double lineEndX;
	protected double lineEndY;

	protected boolean lineSelected = false;
	protected boolean preparingForDrawingSeveralSprinklers = false;
	protected boolean drawingSeveralSprinklers = false;
	protected int indexOfSelectedLine;
	private List<Circle> tempSprinklerCirclesInALine = new ArrayList<>();
	private List<Circle> tempSprinklerCentersInALine = new ArrayList<>();

	// TODO esetleg a tervezés fázisait jelzõ egyre több bool helyett egy enum?
	protected boolean zoneEditingOn = false;
	protected Set<SprinklerShape> selectedSprinklerShapes = new HashSet<>();
	protected double flowRateOfSelected = 0;

	private enum sprinklerDrawingState {
		CENTER, FIRSTSIDE, SECONDSIDE
	}

	private sprinklerDrawingState drawingState = sprinklerDrawingState.CENTER;

	protected List<Shape> borderShapes = new ArrayList<Shape>();

	private ContextMenu rightClickMenu = new ContextMenu();
	private MenuItem delMenuItem = new MenuItem("Törlés");

	public CanvasPane() {

		setWidth(Common.primaryScreenBounds.getWidth() * 6);
		setHeight(Common.primaryScreenBounds.getHeight() * 6);

		// Grid
		// TODO: javítani azt, hogy ha késõbb a meglévõ területen kívülre rajzolok, azon
		// már nem lesz rajta ez a rács
		for (int i = 0; i < (int) getWidth(); i += Common.pixelPerMeter) {
			Line line = new Line(0, i, getHeight(), i);
			line.setStroke(Color.SILVER);
			getChildren().add(line);
			gridLayer.getChildren().add(line);
		}
		for (int i = 0; i < (int) getHeight(); i += Common.pixelPerMeter) {
			Line line = new Line(i, 0, i, getWidth());
			line.setStroke(Color.SILVER);
			getChildren().add(line);
			gridLayer.getChildren().add(line);
		}
		tempFirstSprinklerLine.setVisible(false);
		tempSprinklerCircle.setVisible(false);
		tempRectangle.setVisible(false);
		tempCircle.setVisible(false);
		focusCircle.setVisible(false);
		focusCircle.setStroke(tempLineColor);
		focusCircle.setStrokeWidth(strokeWidth);
		focusCircle.setFill(Color.TRANSPARENT);

		angleInput.setVisible(false);
		angleInput.setMaxWidth(70);
		angleInput.setFont(Font.font(20));
		angleInput.setPromptText("Szög");

		lengthInput.setVisible(false);
		lengthInput.setMaxWidth(130);
		lengthInput.setFont(Font.font(20));
		lengthInput.setPromptText("Hossz (m)");

		rightClickMenu.getItems().add(delMenuItem);

		getChildren().addAll(bordersLayer, irrigationLayer, sprinklerArcLayer, gridLayer, angleInput, lengthInput,
				tempFirstSprinklerLine, tempSecondSprinklerLine, tempBorderLine, tempSprinklerCircle, tempRectangle,
				tempCircle, focusCircle);

	}

	// Elemek kijelölése, most csak törlésre használható, de ezzel lehetne az adott
	// elemrõl infókat kilistázni
	protected void selectElement(MouseEvent e, boolean onlySprinklerHeads) {

		for (SprinklerShape s : controller.listSprinklerShapes()) {
			if (s.getCircle().contains(e.getX(), e.getY())) {
				if (onlySprinklerHeads) {
					selectedSprinklerShapes.add(s);
					flowRateOfSelected = 0;
					for (SprinklerShape sh : selectedSprinklerShapes) 
						flowRateOfSelected += sh.getSprinkler().getWaterConsumption();
				} else {
					rightClickMenu.show(s.getCircle(), Side.RIGHT, 5, 5);
					delMenuItem.setOnAction(ev -> {
						controller.deleteSprinklerShape(s);
						irrigationLayer.getChildren().remove(s.getCircle());
						sprinklerArcLayer.getChildren().remove(s.getArc());
						ev.consume();
					});
				}
			}
		}
		if (!onlySprinklerHeads) {
			for (Shape border : borderShapes) {
				if (border.contains(e.getX(), e.getY())) {
					rightClickMenu.show(border, e.getScreenX(), e.getScreenY());
					delMenuItem.setOnAction(ev -> {
						borderShapes.remove(border);
						bordersLayer.getChildren().remove(border);
						tempBorderLine.setVisible(false);
						tempRectangle.setVisible(false);
						tempCircle.setVisible(false);
						ev.consume();
					});

				}
			}
		}

	}

	protected void drawNewSprinkler(MouseEvent mouseEvent) {
		borderDrawingOn = false;
		SprinklerShape sprinkler = new SprinklerShape();
		Circle circle = new Circle();
		Arc arc = new Arc();
		arc.setType(ArcType.ROUND);
		arc.setStroke(sprinklerColor);
		arc.setStrokeWidth(strokeWidth);
		arc.setFill(Color.TRANSPARENT);

		if (!preparingForDrawingSeveralSprinklers) {
			if (drawingState == sprinklerDrawingState.CENTER && !drawingSeveralSprinklers) {

				centerX = mouseEvent.getX();
				centerY = mouseEvent.getY();

				if (pressedKey == KeyCode.CONTROL) {
					if (showingFocusCircle) {
						Point2D center = Common.snapToGrid(centerX, centerY);
						centerX = center.getX();
						centerY = center.getY();
					}
				}

				tempSprinklerCircle.setCenterX(centerX);
				tempSprinklerCircle.setCenterY(centerY);
				tempSprinklerCircle.setStroke(sprinklerColor);
				tempSprinklerCircle.setFill(sprinklerColor);
				tempSprinklerCircle.setVisible(true);
				drawingState = sprinklerDrawingState.FIRSTSIDE;
			} else if (drawingState == sprinklerDrawingState.FIRSTSIDE) {
				firstX = mouseEvent.getX();
				firstY = mouseEvent.getY();

				if (pressedKey == KeyCode.CONTROL) {
					Point2D firstPoint = Common.snapToHorizontalOrVertival(centerX, centerY, firstX, firstY);
					firstX = firstPoint.getX();
					firstY = firstPoint.getY();
				}

				startAngle = -Math.toDegrees(Math.atan((firstY - centerY) / (firstX - centerX))) - 180;
				if (centerX <= firstX)
					startAngle -= 180;
				if (startAngle < 0)
					startAngle += 360;
				angleInput.setVisible(true);

				angleInput.setLayoutX(mouseEvent.getX());
				angleInput.setLayoutY(mouseEvent.getY());
				angleInput.relocate(centerX, centerY);
				angleInput.setOnKeyPressed(ke -> {
					if (ke.getCode().equals(KeyCode.ENTER)) {
						if (angleInput.getText() == null && angleInput.getText().trim().isEmpty()) {
							Common.showAlert("Add meg a szórófej szögét!");
						} else
							try {

								if (Double.parseDouble(angleInput.getText()) > sprinklerType.getMaxAngle()
										|| Double.parseDouble(angleInput.getText()) < sprinklerType.getMinAngle()) {
									Common.showAlert(
											"A megadott szög nem esik az ennél a szórófejnél lehetséges intervallumba! Min. szög: "
													+ sprinklerType.getMinAngle() + ", max. szög: "
													+ sprinklerType.getMaxAngle());
								} else {
									arcExtent = -Double.parseDouble(angleInput.getText());
									arc.setCenterX(centerX);
									arc.setCenterY(centerY);
									arc.setRadiusX(sprinklerRadius);
									arc.setRadiusY(sprinklerRadius);
									arc.setStartAngle(startAngle);
									arc.setLength(-arcExtent);

									circle.setCenterX(centerX);
									circle.setCenterY(centerY);
									circle.setRadius(Common.pixelPerMeter / 4);
									circle.setStroke(sprinklerColor);
									circle.setFill(sprinklerColor);
									sprinkler.setCircle(circle);
									irrigationLayer.getChildren().add(sprinkler.getCircle());
									tempSprinklerCircle.setVisible(false);

									sprinkler.setArc(arc);

									sprinklerArcLayer.getChildren().add(sprinkler.getArc());
									controller.addSprinklerShape(sprinkler);

									angleInput.setText("");

									drawingState = sprinklerDrawingState.SECONDSIDE;
								}
							} catch (NumberFormatException ex) {
								Common.showAlert("Számokban add meg a szórófej sugarát!");

							}
					}
				});

				drawingState = sprinklerDrawingState.SECONDSIDE;
			} else if (drawingState == sprinklerDrawingState.SECONDSIDE) {

				angleInput.setVisible(false);

				secondX = mouseEvent.getX();
				secondY = mouseEvent.getY();

				if (pressedKey == KeyCode.CONTROL) {
					Point2D firstPoint = Common.snapToHorizontalOrVertival(centerX, centerY, secondX, secondY);
					secondX = firstPoint.getX();
					secondY = firstPoint.getY();
				}
				double endAngle = -Math.toDegrees(Math.atan((secondY - centerY) / (secondX - centerX))) - 180;
				if (centerX <= secondX)
					endAngle -= 180;
				if (endAngle < -360)
					endAngle += 360;
				if (endAngle < 0)
					endAngle += 360;
				arcExtent = 360 - (360 - endAngle) - startAngle;
				if (arcExtent <= 0)
					arcExtent += 360;
				if (arcExtent > sprinklerType.getMaxAngle() || arcExtent < sprinklerType.getMinAngle()) {
					Common.showAlert(
							"A megadott szög nem esik az ennél a szórófejnél lehetséges intervallumba! Min. szög: "
									+ sprinklerType.getMinAngle() + ", max. szög: " + sprinklerType.getMaxAngle());
				} else {

					arc.setCenterY(centerY);
					arc.setCenterX(centerX);
					arc.setCenterY(centerY);
					arc.setRadiusX(sprinklerRadius);
					arc.setRadiusY(sprinklerRadius);
					arc.setStartAngle(startAngle);
					arc.setLength(arcExtent);
					sprinkler.setArc(arc);
					sprinklerArcLayer.getChildren().add(sprinkler.getArc());

					circle.setCenterX(centerX);
					circle.setCenterY(centerY);
					circle.setRadius(Common.pixelPerMeter / 4);
					circle.setStroke(sprinklerColor);
					circle.setFill(sprinklerColor);
					sprinkler.setCircle(circle);
					sprinkler.setSprinkler(sprinklerType);
					irrigationLayer.getChildren().add(sprinkler.getCircle());
					tempSprinklerCircle.setVisible(false);

					controller.addSprinklerShape(sprinkler);
					drawingState = sprinklerDrawingState.CENTER;
					if (drawingSeveralSprinklers)
						drawSeveralSprinklers();
				}
			}
		}
	}

	public void endSprinklerDrawing() {
		drawingState = sprinklerDrawingState.CENTER;
		tempFirstSprinklerLine.setVisible(false);
		tempSprinklerCircle.setVisible(false);
		angleInput.setVisible(false);
		angleInput.setText("");
		sprinklerAttributesSet = false;
	}

	/**
	 * Shows a temporary line while drawing the sprinkler arc, between the center of
	 * the arc and the two endpoints
	 * 
	 * @param mouseEvent
	 */
	public void showTempLine(MouseEvent mouseEvent) {
		if (sprinklerAttributesSet && !borderDrawingOn) {

			tempFirstSprinklerLine.setStroke(tempLineColor);
			tempFirstSprinklerLine.setStrokeWidth(strokeWidth);
			tempSecondSprinklerLine.setStroke(tempLineColor);
			tempSecondSprinklerLine.setStrokeWidth(strokeWidth);

			if (drawingState == sprinklerDrawingState.CENTER) {
				tempFirstSprinklerLine.setVisible(false);
				tempSecondSprinklerLine.setVisible(false);
			} else if (drawingState == sprinklerDrawingState.FIRSTSIDE) {
				tempFirstSprinklerLine.setStartX(centerX);
				tempFirstSprinklerLine.setStartY(centerY);
				if (pressedKey == KeyCode.CONTROL) {
					Point2D firstPoint = Common.snapToHorizontalOrVertival(centerX, centerY, mouseEvent.getX(),
							mouseEvent.getY());
					tempFirstSprinklerLine.setEndX(firstPoint.getX());
					tempFirstSprinklerLine.setEndY(firstPoint.getY());
				} else {
					tempFirstSprinklerLine.setEndX(mouseEvent.getX());
					tempFirstSprinklerLine.setEndY(mouseEvent.getY());
				}
				tempFirstSprinklerLine.setVisible(true);
				tempSecondSprinklerLine.setVisible(false);
			} else if (drawingState == sprinklerDrawingState.SECONDSIDE) {
				tempSecondSprinklerLine.setStartX(centerX);
				tempSecondSprinklerLine.setStartY(centerY);

				tempSecondSprinklerLine.setEndX(firstX);
				tempSecondSprinklerLine.setEndY(firstY);
				tempSecondSprinklerLine.setVisible(true);
				tempFirstSprinklerLine.setStartX(centerX);
				tempFirstSprinklerLine.setStartY(centerY);

				if (pressedKey == KeyCode.CONTROL) {
					Point2D secondPoint = Common.snapToHorizontalOrVertival(centerX, centerY, mouseEvent.getX(),
							mouseEvent.getY());
					tempFirstSprinklerLine.setEndX(secondPoint.getX());
					tempFirstSprinklerLine.setEndY(secondPoint.getY());
				} else {
					tempFirstSprinklerLine.setEndX(mouseEvent.getX());
					tempFirstSprinklerLine.setEndY(mouseEvent.getY());
				}

				tempFirstSprinklerLine.setVisible(true);
			}

		}
	}

	public void showTempBorderLine(MouseEvent e, Color color) {
		lengthInput.setVisible(true);
		lengthInput.relocate(borderX, borderY);
		tempBorderLine.setStartX(borderX);
		tempBorderLine.setStartY(borderY);
		tempBorderLine.setStroke(color);
		tempBorderLine.setVisible(true);
		if (pressedKey == KeyCode.CONTROL) {
			Point2D point = Common.snapToHorizontalOrVertival(borderX, borderY, e.getX(), e.getY());
			tempBorderLine.setEndX(point.getX());
			tempBorderLine.setEndY(point.getY());
		} else {
			tempBorderLine.setEndX(e.getX());
			tempBorderLine.setEndY(e.getY());
		}
	}

	// TODO ide is kell a rácshoz igazodás, mint a szórófej rajzolásnál
	// TODO kell arra mód, hogy az elõzõ vonalhoz képest derékszögû vonalat tudjon
	// rajzolni (nem feltétlen esik a rácsra), de nem tudom, milyen felhasználói
	// inputra csinálja ezt
	public void drawBorderLine(MouseEvent e, Color color, int width) {
		tempBorderLine.setVisible(false);
		Line line = new Line();
		line.setStartX(borderX);
		line.setStartY(borderY);
		line.setStrokeWidth(width);
		line.setStroke(color);
		if (pressedKey == KeyCode.CONTROL) {
			Point2D point = Common.snapToHorizontalOrVertival(borderX, borderY, e.getX(), e.getY());
			line.setEndX(point.getX());
			line.setEndY(point.getY());
			borderX = point.getX();
			borderY = point.getY();
			borderShapes.add(line);
			bordersLayer.getChildren().add(line);
		} else if (lengthInput.getText().trim().isEmpty() || lengthInput.getText() == null) {
			if (cursorNearLineEnd) {
				line.setEndX(lineEndX);
				line.setEndY(lineEndY);
				borderX = lineEndX;
				borderY = lineEndY;
				borderShapes.add(line);
				bordersLayer.getChildren().add(line);
			} else {
				line.setEndX(e.getX());
				line.setEndY(e.getY());
				borderX = e.getX();
				borderY = e.getY();
				borderShapes.add(line);
				bordersLayer.getChildren().add(line);
			}
		} else {
			try {
				double requiredLength = Double.parseDouble(lengthInput.getText()) * Common.pixelPerMeter;
				double drawnLength = Math.sqrt(
						(borderX - e.getX()) * (borderX - e.getX()) + (borderY - e.getY()) * (borderY - e.getY()));
				double ratio = requiredLength / drawnLength;
				double X = borderX + (e.getX() - borderX) * ratio;
				double Y = borderY + (e.getY() - borderY) * ratio;
				line.setEndX(X);
				line.setEndY(Y);
				borderX = X;
				borderY = Y;
				borderShapes.add(line);
				bordersLayer.getChildren().add(line);
				lengthInput.setText("");
				lengthInput.setVisible(false);

			} catch (NumberFormatException ex) {
				Common.showAlert("Számokban add meg a vonal hosszát vagy hagyd üresen a mezõt!");
			}

		}
	}

	public void endLineDrawing() {
		lengthInput.setVisible(false);
		tempBorderLine.setVisible(false);
		borderDrawingOn = false;
	}

	public void showtempBorderRectanlge(MouseEvent e, Color color) {
		if (e.getButton() == MouseButton.PRIMARY) {
			double width = Math.abs(borderX - e.getX());
			double height = Math.abs(borderY - e.getY());
			double x = 0, y = 0;
			if (borderX > e.getX())
				x = e.getX();
			else
				x = borderX;
			if (borderY > e.getY())
				y = e.getY();
			else
				y = borderY;
			tempRectangle.setX(x);
			tempRectangle.setY(y);
			tempRectangle.setWidth(width);
			tempRectangle.setHeight(height);

			tempRectangle.setStroke(color);
			tempRectangle.setFill(null);

			tempRectangle.setVisible(true);
		}
	}

	public void drawBorderRectanlge(MouseEvent e, Color strokeColor, Color fillColor, int width) {
		Rectangle rect = Common.drawRectangle(strokeColor, borderX, borderY, e.getX(), e.getY());
		rect.setFill(fillColor);
		rect.setStrokeWidth(width);
		bordersLayer.getChildren().add(rect);
		borderShapes.add(rect);
	}

	public void showTempBorderCircle(MouseEvent e, Color color) {
		if (e.getButton() == MouseButton.PRIMARY) {
			double r = Math
					.sqrt((borderX - e.getX()) * (borderX - e.getX()) + (borderY - e.getY()) * (borderY - e.getY()));
			tempCircle.setCenterX(borderX);
			tempCircle.setCenterY(borderY);
			tempCircle.setRadius(r);
			tempCircle.setFill(Color.TRANSPARENT);
			tempCircle.setStroke(color);
			tempCircle.setVisible(true);
		}
	}

	public void drawBorderCircle(MouseEvent e, Color strokeColor, Color fillColor, int width) {

		double r = Math.sqrt((borderX - e.getX()) * (borderX - e.getX()) + (borderY - e.getY()) * (borderY - e.getY()));
		Circle circle = new Circle(borderX, borderY, r, null);
		circle.setStroke(strokeColor);
		circle.setFill(fillColor);
		circle.setStrokeWidth(width);
		bordersLayer.getChildren().add(circle);
		borderShapes.add(circle);

	}

	public void startDrawingBorder(MouseEvent e) {
		borderX = e.getX();
		borderY = e.getY();
	}

	public void showFocusCircle(MouseEvent e) {
		if (pressedKey != null && pressedKey.equals(KeyCode.CONTROL) && sprinklerAttributesSet && !borderDrawingOn
				&& drawingState == sprinklerDrawingState.CENTER) {
			if ((e.getX() % Common.pixelPerMeter < Common.pixelPerMeter / 4
					|| e.getX() % Common.pixelPerMeter > Common.pixelPerMeter * 3 / 4)
					&& (e.getY() % Common.pixelPerMeter < Common.pixelPerMeter / 4
							|| e.getY() % Common.pixelPerMeter > Common.pixelPerMeter * 3 / 4)) {

				setCursor(Cursor.CROSSHAIR);
				Point2D focusCenter = Common.snapToGrid(e.getX(), e.getY());
				focusCircle.setCenterX(focusCenter.getX());
				focusCircle.setCenterY(focusCenter.getY());
				focusCircle.setVisible(true);
				showingFocusCircle = true;
			} else {
				focusCircle.setVisible(false);
				showingFocusCircle = false;
			}
		} else {
			focusCircle.setVisible(false);
			showingFocusCircle = false;
		}
	}

	protected void setSprinklerAttributes() {
		Stage sprinklerInfoStage = new Stage();
		VBox sprinklerInfoRoot = new VBox();
		Scene sprinklerInfoScene = new Scene(sprinklerInfoRoot, 800, 400);
		HBox sprinklerGroupPane = new HBox();
		Text sprinklerGroupText = new Text("Szórófej csoport");
		ChoiceBox<SprinklerGroup> sprinklerGroupChoiceBox = new ChoiceBox<SprinklerGroup>();
		Button ok = new Button("OK");
		sprinklerInfoStage.setAlwaysOnTop(true);
		TableView<SprinklerType> tableView = new TableView<SprinklerType>();
		TableColumn<SprinklerType, String> nameCol = new TableColumn<>("Név");
		TableColumn<SprinklerType, Double> minRadiusCol = new TableColumn<>("Min. sugár (m)");
		TableColumn<SprinklerType, Double> maxRadiusCol = new TableColumn<>("Max. sugár (m)");
		TableColumn<SprinklerType, Double> minAngleCol = new TableColumn<>("Min. szög (fok)");
		TableColumn<SprinklerType, Double> maxAngleCol = new TableColumn<>("Max. szög (fok)");
		TableColumn<SprinklerType, Boolean> fixWaterConsumptionCol = new TableColumn<>("Fix vízfogyasztás");
		TableColumn<SprinklerType, Double> waterConsumptionCol = new TableColumn<>("Vízfogyasztás (l/min)");
		TableColumn<SprinklerType, Double> minPressureCol = new TableColumn<>("Min. víznyomás (bar)");
		Text radiusText = new Text("Sugár: ");
		TextField radiusField = new TextField();
		Text meterText = new Text("méter");

		sprinklerGroupChoiceBox.setItems(controller.listSprinklerGroups());
		sprinklerGroupChoiceBox.getSelectionModel().selectFirst();
		sprinklerGroupPane.getChildren().addAll(sprinklerGroupText, sprinklerGroupChoiceBox);
		sprinklerInfoStage.setScene(sprinklerInfoScene);
		sprinklerInfoRoot.getChildren().addAll(sprinklerGroupPane, tableView, radiusText, radiusField, meterText, ok);

		tableView.getColumns().addAll(nameCol, minRadiusCol, maxRadiusCol, minAngleCol, maxAngleCol,
				fixWaterConsumptionCol, waterConsumptionCol, minPressureCol);
		nameCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, String>("name"));
		minRadiusCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, Double>("minRadius"));
		minRadiusCol.setCellFactory(new DecimalCellFactory<SprinklerType, Double>());
		maxRadiusCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, Double>("maxRadius"));
		maxRadiusCol.setCellFactory(new DecimalCellFactory<SprinklerType, Double>());
		minAngleCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, Double>("minAngle"));
		minAngleCol.setCellFactory(new DecimalCellFactory<SprinklerType, Double>());
		maxAngleCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, Double>("maxAngle"));
		maxAngleCol.setCellFactory(new DecimalCellFactory<SprinklerType, Double>());
		fixWaterConsumptionCol
				.setCellValueFactory(new PropertyValueFactory<SprinklerType, Boolean>("fixWaterConsumption"));
		waterConsumptionCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, Double>("waterConsumption"));
		waterConsumptionCol.setCellFactory(new DecimalCellFactory<SprinklerType, Double>());
		minPressureCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, Double>("minPressure"));
		minPressureCol.setCellFactory(new DecimalCellFactory<SprinklerType, Double>());

		tableView.setItems(
				controller.listSprinklerTypeByGroup(sprinklerGroupChoiceBox.getSelectionModel().getSelectedItem()));

		sprinklerGroupChoiceBox.setOnAction(e -> {
			if (sprinklerGroupChoiceBox.getSelectionModel().getSelectedItem() != null) {
				tableView.getItems().clear();
				tableView.setItems(controller
						.listSprinklerTypeByGroup(sprinklerGroupChoiceBox.getSelectionModel().getSelectedItem()));
			}
		});

		ok.setOnAction(e -> {
			if (radiusField.getText() == null && radiusField.getText().trim().isEmpty()) {
				Common.showAlert("Add meg a szórófej sugarát!");
			} else {
				try {
					double radius = Double.parseDouble(radiusField.getText());
					SprinklerType type = tableView.getSelectionModel().getSelectedItem();
					// a megrendelõ kérésére csak azt ellenõrzi, hogyha a megengedettnél nagyobb
					// sugárra próbálja állítani, fizikailag lehetséges kisebb a gyártó által
					// megadottnál kisebb szögre állítani és néha erre van szükség
					// TODO ha nem választ ki elemet és rányom az ok-ra, ez nullpointerexceptiont
					// dob
					if (radius > tableView.getSelectionModel().getSelectedItem().getMaxRadius()) {

						Common.showAlert("A sugár nagyobb, mint az ennél a típusnál megengedett legnagyobb sugár");
					} else {
						sprinklerRadius = radius * Common.pixelPerMeter;
						sprinklerAttributesSet = true;
						sprinklerType = type;
					}

					requestFocus();
				} catch (NumberFormatException ex) {
					Common.showAlert("Számokban add meg a szórófej sugarát!");
				}
			}
			sprinklerInfoStage.close();
		});
		sprinklerInfoStage.show();
	}

	public void selectLineForSprinklerDrawing(MouseEvent e) {
		for (Shape border : borderShapes) {
			if (border instanceof Line) {
				if (border.contains(e.getX(), e.getY())) {
					lineSelected = true;
					indexOfSelectedLine = borderShapes.indexOf(border);
					preparingForDrawingSeveralSprinklers = false; // TODO ezt nem itt kell átállítani, hanem amikor már
																	// kész a berajzolás
				}
			}
		}
	}

	public void showSprinklersInALine(int numberOfSprinklersInALine) {
		if (!tempSprinklerCentersInALine.isEmpty())
			for (Circle c : tempSprinklerCentersInALine) {
				sprinklerArcLayer.getChildren().remove(c);
			}
		if (!tempSprinklerCirclesInALine.isEmpty())
			for (Circle c : tempSprinklerCirclesInALine) {
				sprinklerArcLayer.getChildren().remove(c);
			}
		tempSprinklerCirclesInALine.clear();
		tempSprinklerCentersInALine.clear();
		if (lineSelected) {
			// TODO valahol még ellenõrizni kéne, hogy tényleg beállított-e értékeket a
			// szórófejnek - vagy ezt már megcsináltam?
			double startX = ((Line) borderShapes.get(indexOfSelectedLine)).getStartX();
			double endX = ((Line) borderShapes.get(indexOfSelectedLine)).getEndX();
			double diffX = startX - endX;
			double startY = ((Line) borderShapes.get(indexOfSelectedLine)).getStartY();
			double endY = ((Line) borderShapes.get(indexOfSelectedLine)).getEndY();
			double diffY = startY - endY;

			for (int i = 0; i < numberOfSprinklersInALine; i++) {
				Circle center = new Circle(Common.pixelPerMeter / 4);
				center.setStroke(tempLineColor);
				center.setFill(tempLineColor);
				center.setCenterX(startX - i * (diffX / (numberOfSprinklersInALine - 1)));
				center.setCenterY(startY - i * (diffY / (numberOfSprinklersInALine - 1)));
				sprinklerArcLayer.getChildren().add(center);
				Circle c = new Circle();
				c.setStroke(tempLineColor);
				c.setStrokeWidth(strokeWidth);
				c.setRadius(sprinklerRadius);
				c.setFill(null);
				c.setCenterX(startX - i * (diffX / (numberOfSprinklersInALine - 1)));
				c.setCenterY(startY - i * (diffY / (numberOfSprinklersInALine - 1)));
				sprinklerArcLayer.getChildren().add(c);

				tempSprinklerCirclesInALine.add(c);
				tempSprinklerCentersInALine.add(center);
			}
		} else
			Common.showAlert("A vonal nincs kiválasztva!");
	}

	public void drawSeveralSprinklers() {
		lineSelected = false;
		if (tempSprinklerCentersInALine.isEmpty()) {
			drawingSeveralSprinklers = false;
		} else {
			drawingSeveralSprinklers = true;
			sprinklerArcLayer.getChildren().remove(tempSprinklerCirclesInALine.get(0));
			sprinklerArcLayer.getChildren().remove(tempSprinklerCentersInALine.get(0));
			centerX = tempSprinklerCentersInALine.get(0).getCenterX();
			centerY = tempSprinklerCentersInALine.get(0).getCenterY();
			tempSprinklerCentersInALine.remove(0);
			tempSprinklerCirclesInALine.remove(0);
			tempSprinklerCircle.setCenterX(centerX);
			tempSprinklerCircle.setCenterY(centerY);
			tempSprinklerCircle.setStroke(sprinklerColor);
			tempSprinklerCircle.setFill(sprinklerColor);
			tempSprinklerCircle.setVisible(true);
			drawingState = sprinklerDrawingState.FIRSTSIDE;
		}
	}

}
