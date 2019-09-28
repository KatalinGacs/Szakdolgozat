package application;

import java.util.ArrayList;
import java.util.List;

import application.common.Common;
import controller.SprinklerController;
import controller.SprinklerControllerImpl;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import model.bean.SprinklerShape;
import model.bean.SprinklerType;

public class CanvasPane extends Pane {

	SprinklerController controller = new SprinklerControllerImpl();

	protected KeyCode pressedKey;

	private double borderX, borderY;

	private double centerX = 0, centerY = 0, firstX = 0, firstY = 0, secondX = 0, secondY = 0;
	double startAngle, arcExtent;
	private Color sprinklerColor = Color.CORNFLOWERBLUE;
	private int strokeWidth = (int) (Common.pixelPerMeter / 12);

	protected SprinklerType sprinklerType;
	protected double sprinklerRadius;

	private TextField angleInput = new TextField();
	private TextField lengthInput = new TextField();

	Group bordersLayer = new Group();
	Group irrigationLayer = new Group();
	Group sprinklerArcLayer = new Group();
	Group gridLayer = new Group();

	// segédvonalak
	private Line line = new Line();
	private Line tempLine = new Line();

	private Circle focusCircle = new Circle(Common.pixelPerMeter / 3);
	private boolean showingFocusCircle = false;
	Color tempLineColor = Color.DARKTURQUOISE;

	private Line tempBorderLine = new Line();
	private Circle tempSprinklerCircle = new Circle(Common.pixelPerMeter / 4);

	private Rectangle tempRectangle = new Rectangle();
	private Circle tempCircle = new Circle();

	protected boolean borderDrawingOn = false;
	protected boolean sprinklerAttributesSet = false;

	private enum sprinklerDrawingState {
		CENTER, FIRSTSIDE, SECONDSIDE
	}

	private sprinklerDrawingState drawingState = sprinklerDrawingState.CENTER;

	protected List<Shape> borderShapes = new ArrayList<Shape>();

	private ContextMenu delMenu = new ContextMenu();
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
		line.setVisible(false);
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

		delMenu.getItems().add(delMenuItem);

		getChildren().addAll(bordersLayer, irrigationLayer, sprinklerArcLayer, gridLayer, angleInput, lengthInput, line,
				tempLine, tempBorderLine, tempSprinklerCircle, tempRectangle, tempCircle, focusCircle);

	}

	// Elemek kijelölése, most csak törlésre használható, de ezzel lehetne az adott
	// elemrõl infókat kilistázni
	protected void selectElement(MouseEvent e) {
		for (SprinklerShape s : controller.listSprinklerShapes()) {
			if (s.getCircle().contains(e.getX(), e.getY())) {
				delMenu.show(s.getCircle(), Side.RIGHT, 5, 5);
				delMenuItem.setOnAction(ev -> {
					controller.deleteSprinklerShape(s);
					irrigationLayer.getChildren().remove(s.getCircle());
					sprinklerArcLayer.getChildren().remove(s.getArc());
					ev.consume();
				});
			}
		}
		for (Shape border : borderShapes) {
			if (border.contains(e.getX(), e.getY())) {
				delMenu.show(border, e.getScreenX(), e.getScreenY());
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

	protected void drawNewSprinkler(MouseEvent mouseEvent) {
		borderDrawingOn = false;
		SprinklerShape sprinkler = new SprinklerShape();
		Circle circle = new Circle();
		Arc arc = new Arc();
		arc.setType(ArcType.ROUND);
		arc.setStroke(sprinklerColor);
		arc.setStrokeWidth(strokeWidth);
		arc.setFill(Color.TRANSPARENT);

		if (drawingState == sprinklerDrawingState.CENTER) {

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
			if (centerX < firstX)
				startAngle -= 180;
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

			arcExtent = -Math.toDegrees(Math.atan((secondY - centerY) / (secondX - centerX))) - startAngle;

			if ((secondX > centerX && firstY < centerY && secondY > centerY) || (firstX < centerX && secondX > centerX)
					|| (firstX > centerX && secondX > centerX && firstY < secondY)) {
			} else if (firstX < centerX && secondX < centerX && firstY > secondY) {
				arcExtent += 180;
			} else if (firstX > centerX && secondX > centerX && firstY > secondY) {
				arcExtent -= 360;
			} else {
				arcExtent -= 180;
			}

			if (arcExtent > sprinklerType.getMaxAngle() || arcExtent < sprinklerType.getMinAngle()) {
				Common.showAlert("A megadott szög nem esik az ennél a szórófejnél lehetséges intervallumba! Min. szög: "
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
				circle.setRadius(5);
				circle.setStroke(sprinklerColor);
				circle.setFill(sprinklerColor);
				sprinkler.setCircle(circle);
				sprinkler.setSprinkler(sprinklerType);
				irrigationLayer.getChildren().add(sprinkler.getCircle());
				tempSprinklerCircle.setVisible(false);

				controller.addSprinklerShape(sprinkler);
				drawingState = sprinklerDrawingState.CENTER;
			}
		}
	}

	public void endSprinklerDrawing() {
		drawingState = sprinklerDrawingState.CENTER;
		line.setVisible(false);
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

			line.setStroke(tempLineColor);
			line.setStrokeWidth(strokeWidth);
			tempLine.setStroke(tempLineColor);
			tempLine.setStrokeWidth(strokeWidth);

			if (drawingState == sprinklerDrawingState.CENTER) {
				line.setVisible(false);
				tempLine.setVisible(false);
			} else if (drawingState == sprinklerDrawingState.FIRSTSIDE) {
				line.setStartX(centerX);
				line.setStartY(centerY);
				if (pressedKey == KeyCode.CONTROL) {
					Point2D firstPoint = Common.snapToHorizontalOrVertival(centerX, centerY, mouseEvent.getX(),
							mouseEvent.getY());
					line.setEndX(firstPoint.getX());
					line.setEndY(firstPoint.getY());
				} else {
					line.setEndX(mouseEvent.getX());
					line.setEndY(mouseEvent.getY());
				}
				line.setVisible(true);
				tempLine.setVisible(false);
			} else if (drawingState == sprinklerDrawingState.SECONDSIDE) {
				tempLine.setStartX(centerX);
				tempLine.setStartY(centerY);

				tempLine.setEndX(firstX);
				tempLine.setEndY(firstY);
				tempLine.setVisible(true);
				line.setStartX(centerX);
				line.setStartY(centerY);

				if (pressedKey == KeyCode.CONTROL) {
					Point2D secondPoint = Common.snapToHorizontalOrVertival(centerX, centerY, mouseEvent.getX(),
							mouseEvent.getY());
					line.setEndX(secondPoint.getX());
					line.setEndY(secondPoint.getY());
				} else {
					line.setEndX(mouseEvent.getX());
					line.setEndY(mouseEvent.getY());
				}

				line.setVisible(true);
			}

		}
	}

	public void showTempBorderLine(MouseEvent e, Color color) {
		lengthInput.setVisible(true);
		lengthInput.relocate(borderX, borderY);
		tempBorderLine.setStartX(borderX);
		tempBorderLine.setStartY(borderY);
		tempBorderLine.setEndX(e.getX());
		tempBorderLine.setEndY(e.getY());
		tempBorderLine.setStroke(color);
		tempBorderLine.setVisible(true);
	}

	//TODO ezeknél valami mód kéne arra, hogy a végével vagy más berajzolt elemmel össze tudjam kötni
	public void drawBorderLine(MouseEvent e, Color color, int width) {
		tempBorderLine.setVisible(false);
		if (lengthInput.getText().trim().isEmpty() || lengthInput.getText() == null) {
			Line line = new Line(borderX, borderY, e.getX(), e.getY());
			line.setStrokeWidth(width);
			line.setStroke(color);
			borderShapes.add(line);
			bordersLayer.getChildren().add(line);
			borderX = e.getX();
			borderY = e.getY();
		} else {
			try {
				double requiredLength = Double.parseDouble(lengthInput.getText()) * Common.pixelPerMeter;
				double drawnLength = Math.sqrt(
						(borderX - e.getX()) * (borderX - e.getX()) + (borderY - e.getY()) * (borderY - e.getY()));
				double ratio = requiredLength / drawnLength;
				double X = borderX + (e.getX() - borderX) * ratio;
				double Y = borderY + (e.getY() - borderY) * ratio;
				Line line = new Line(borderX, borderY, X, Y);
				line.setStrokeWidth(width);
				line.setStroke(color);
				borderShapes.add(line);
				bordersLayer.getChildren().add(line);
				borderX = X;
				borderY = Y;
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

	public void drawBorderRectanlge(MouseEvent e, Color color, int width) {
		Rectangle rect = Common.drawRectangle(color, borderX, borderY, e.getX(), e.getY());
		rect.setFill(null);
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

	public void drawBorderCircle(MouseEvent e, Color color, int width) {

		double r = Math.sqrt((borderX - e.getX()) * (borderX - e.getX()) + (borderY - e.getY()) * (borderY - e.getY()));
		Circle circle = new Circle(borderX, borderY, r, null);
		circle.setStroke(color);
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

}
