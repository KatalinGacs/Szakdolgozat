package application;

import java.util.ArrayList;
import java.util.List;

import controller.SprinklerController;
import controller.SprinklerControllerImpl;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import model.bean.Sprinkler;

public class CanvasPane extends Pane {

	SprinklerController controller = new SprinklerControllerImpl();

	protected KeyCode pressedKey;

	private double borderX, borderY;

	private double centerX = 0, centerY = 0, firstX = 0, firstY = 0, secondX = 0, secondY = 0;
	double startAngle, arcExtent;
	protected Color sprinklerColor;
	protected double sprinklerRadius;
	protected boolean sprinklerAttributesSet = false;
	private TextField angleInput = new TextField();
	protected static Group group = new Group();

	// seg�dvonalk�nt
	private Line line = new Line();
	private Line tempLine = new Line();
	private Line tempBorderLine = new Line();
	private Circle tempSprinklerCircle = new Circle(5);
	private Circle focusCircle = new Circle(7);

	private Rectangle tempRectangle = new Rectangle();
	private Circle tempCircle = new Circle();
	protected boolean borderDrawingOn = false;

	private static int i = 0;

	protected List<Polyline> borderLines = new ArrayList<Polyline>();
	protected List<Rectangle> borderRectangles = new ArrayList<Rectangle>();
	protected List<Circle> borderCircles = new ArrayList<Circle>();

	public CanvasPane() {

		setWidth(1500);
		setHeight(1500);

		for (int i = 0; i < (int) getWidth(); i += 50) {
			Line line = new Line(0, i, getHeight(), i);
			line.setStroke(Color.SILVER);
			getChildren().add(line);
		}
		for (int i = 0; i < (int) getHeight(); i += 50) {
			Line line = new Line(i, 0, i, getWidth());
			line.setStroke(Color.SILVER);

			getChildren().add(line);
		}

		line.setVisible(false);
		tempSprinklerCircle.setVisible(false);
		tempRectangle.setVisible(false);
		tempCircle.setVisible(false);
		focusCircle.setVisible(false);
		focusCircle.setStroke(Color.SILVER);
		focusCircle.setFill(Color.TRANSPARENT);

		angleInput.setVisible(false);
		angleInput.setMinWidth(40);
		angleInput.setPromptText("Sz�g");

		getChildren().addAll(group, angleInput, line, tempLine, tempBorderLine, tempSprinklerCircle, tempRectangle, tempCircle,
				focusCircle);

	}

	protected void selectElement(MouseEvent e) {
		
		for (Sprinkler s : controller.listSprinklers()) {

				if (s.getCircle().contains(e.getX(), e.getY())) {
				
							ContextMenu delMenu = new ContextMenu();
							MenuItem delMenuItem = new MenuItem("T�rl�s");
							delMenu.getItems().add(delMenuItem);
							delMenu.show(s.getCircle(), Side.RIGHT, 5, 5);
							delMenuItem.setOnAction(ev-> {
								controller.deleteSprinkler(s);
								group.getChildren().removeAll(s.getArc(), s.getCircle());
								ev.consume();
							});
						}
					
				
			
		}
	}

	protected void drawNewSprinkler(MouseEvent mouseEvent) {
		borderDrawingOn = false;
		Sprinkler sprinkler = new Sprinkler();

		Circle circle = new Circle();
		Arc arc = new Arc();
		arc.setType(ArcType.ROUND);
		arc.setStroke(sprinklerColor);
		arc.setFill(Color.TRANSPARENT);

		if (i % 3 == 0) {

			centerX = mouseEvent.getX();
			centerY = mouseEvent.getY();

			if (pressedKey == KeyCode.CONTROL) {
				if ((mouseEvent.getX() % 50 < 10 || mouseEvent.getX() > 40)
						&& (mouseEvent.getY() % 50 < 10 || mouseEvent.getY() > 40)) {
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
			i++;
		} else if (i % 3 == 1) {
			firstX = mouseEvent.getX();
			firstY = mouseEvent.getY();

			startAngle = -Math.toDegrees(Math.atan((firstY - centerY) / (firstX - centerX))) - 180;
			if (centerX < firstX)
				startAngle -= 180;
			angleInput.setVisible(true);

			angleInput.setLayoutX(mouseEvent.getX());
			angleInput.setLayoutY(mouseEvent.getY());
			angleInput.setMaxWidth(30);
			angleInput.relocate(centerX, centerY);
			angleInput.setOnKeyPressed(ke -> {
				if (ke.getCode().equals(KeyCode.ENTER)) {
					if (angleInput.getText() == null && angleInput.getText().trim().isEmpty()) {
						Common.showAlert("Add meg a sz�r�fej sz�g�t!");
					} else
						try {
							arcExtent = -Double.parseDouble(angleInput.getText());

							arc.setCenterX(centerX);
							arc.setCenterY(centerY);
							arc.setRadiusX(sprinklerRadius);
							arc.setRadiusY(sprinklerRadius);
							arc.setStartAngle(startAngle);
							arc.setLength(-arcExtent);

							circle.setCenterX(centerX);
							circle.setCenterY(centerY);
							circle.setRadius(5);
							circle.setStroke(sprinklerColor);
							circle.setFill(sprinklerColor);
							sprinkler.setCircle(circle);
							group.getChildren().add(sprinkler.getCircle());
							tempSprinklerCircle.setVisible(false);

							sprinkler.setArc(arc);
							sprinkler.setCenterX(centerX);
							sprinkler.setCenterY(centerY);
							sprinkler.setRadius(sprinklerRadius);
							sprinkler.setAngle(arcExtent);
							sprinkler.setColor(sprinklerColor);

							group.getChildren().add(sprinkler.getArc());
							controller.addSprinkler(sprinkler);

							i++;

							angleInput.setText("");

						} catch (NumberFormatException ex) {
							Common.showAlert("Sz�mokban add meg a sz�r�fej sugar�t!");

						}

				}
			});

			i++;
		} else if (i % 3 == 2) {

			angleInput.setVisible(false);

			secondX = mouseEvent.getX();
			secondY = mouseEvent.getY();

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

			arc.setCenterY(centerY);
			arc.setCenterX(centerX);
			arc.setCenterY(centerY);
			arc.setRadiusX(sprinklerRadius);
			arc.setRadiusY(sprinklerRadius);
			arc.setStartAngle(startAngle);
			arc.setLength(arcExtent);
			sprinkler.setArc(arc);

			group.getChildren().add(sprinkler.getArc());

			circle.setCenterX(centerX);
			circle.setCenterY(centerY);
			circle.setRadius(5);
			circle.setStroke(sprinklerColor);
			circle.setFill(sprinklerColor);
			sprinkler.setCircle(circle);
			group.getChildren().add(sprinkler.getCircle());
			tempSprinklerCircle.setVisible(false);

			sprinkler.setCenterX(centerX);
			sprinkler.setCenterY(centerY);
			sprinkler.setRadius(sprinklerRadius);
			sprinkler.setAngle(arcExtent);
			sprinkler.setColor(sprinklerColor);

			controller.addSprinkler(sprinkler);
			i++;
		}
	}

	public void showTempLine(MouseEvent mouseEvent) {
		if (sprinklerAttributesSet && !borderDrawingOn) {

			line.setStroke(Color.SILVER);
			tempLine.setStroke(Color.SILVER);

			if (i % 3 == 0) {
				line.setVisible(false);
				tempLine.setVisible(false);
			} else if (i % 3 == 1) {
				line.setStartX(centerX);
				line.setStartY(centerY);
				line.setEndX(mouseEvent.getX());
				line.setEndY(mouseEvent.getY());
				line.setVisible(true);
				tempLine.setVisible(false);
			} else if (i % 3 == 2) {
				tempLine.setStartX(centerX);
				tempLine.setStartY(centerY);
				tempLine.setEndX(firstX);
				tempLine.setEndY(firstY);
				tempLine.setVisible(true);
				line.setStartX(centerX);
				line.setStartY(centerY);
				line.setEndX(mouseEvent.getX());
				line.setEndY(mouseEvent.getY());
				line.setVisible(true);
			}

		}
	}

	public void drawBorderline(MouseEvent e, Polyline line, Color color) {
		if (!line.getPoints().isEmpty()) {
			
			tempBorderLine.setStartX(line.getPoints().get(line.getPoints().size() - 2));
			tempBorderLine.setStartY(line.getPoints().get(line.getPoints().size() - 1));
			tempBorderLine.setEndX(e.getX());
			tempBorderLine.setEndY(e.getY());
			tempBorderLine.setStroke(color);
			tempBorderLine.setVisible(true);
		}
	}

	public void showtempRectanlge(MouseEvent e, Color color) {

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
		tempRectangle.setFill(Color.TRANSPARENT);

		tempRectangle.setVisible(true);

	}

	public void drawBorderRectanlge(MouseEvent e, Color color, int width) {
		Rectangle rect = Common.drawRectangle(color, borderX, borderY, e.getX(), e.getY());
		rect.setStrokeWidth(width);
		group.getChildren().add(rect);
		borderRectangles.add(rect);

	}

	public void showTempCircle(MouseEvent e, Color color) {
		double r = Math.sqrt((borderX - e.getX()) * (borderX - e.getX()) + (borderY - e.getY()) * (borderY - e.getY()));
		tempCircle.setCenterX(borderX);
		tempCircle.setCenterY(borderY);
		tempCircle.setRadius(r);
		tempCircle.setFill(Color.TRANSPARENT);
		tempCircle.setStroke(color);
		tempCircle.setVisible(true);
	}

	public void drawBorderCircle(MouseEvent e, Color color, int width) {
		double r = Math.sqrt((borderX - e.getX()) * (borderX - e.getX()) + (borderY - e.getY()) * (borderY - e.getY()));
		Circle circle = new Circle(borderX, borderY, r, Color.TRANSPARENT);
		circle.setStroke(color);
		circle.setStrokeWidth(width);
		group.getChildren().add(circle);
		borderCircles.add(circle);
	}

	public void startDrawingBorder(MouseEvent e) {
		borderX = e.getX();
		borderY = e.getY();
	}

	public void endSprinklerDrawing() {
		i = 0;
		line.setVisible(false);
		tempSprinklerCircle.setVisible(false);
		angleInput.setVisible(false);
		angleInput.setText("");
		sprinklerAttributesSet = false;
	}

	public void showFocusCircle(MouseEvent e) {
		if (pressedKey != null && pressedKey.equals(KeyCode.CONTROL)) {
			if ((e.getX() % 50 < 10 || e.getX() > 40) && (e.getY() % 50 < 10 || e.getY() > 40)) {
				setCursor(Cursor.CROSSHAIR);
				Point2D focusCenter = Common.snapToGrid(e.getX(), e.getY());
				focusCircle.setCenterX(focusCenter.getX());
				focusCircle.setCenterY(focusCenter.getY());
				focusCircle.setVisible(true);
			} else
				focusCircle.setVisible(false);
		} else
			focusCircle.setVisible(false);
	}

}
