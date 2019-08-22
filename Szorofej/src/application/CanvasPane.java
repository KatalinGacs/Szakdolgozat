package application;

import controller.SprinklerController;
import controller.SprinklerControllerImpl;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import model.bean.Sprinkler;

public class CanvasPane extends Pane {

	SprinklerController controller = new SprinklerControllerImpl();

	private KeyCode pressedKey;

	private double centerX = 0, centerY = 0, firstX = 0, firstY = 0, secondX = 0, secondY = 0;
	double startAngle, arcExtent;
	private Color sprinklerColor;
	private double sprinklerRadius;
	private boolean sprinklerAttributesSet = false;
	private TextField angleInput = new TextField();
	protected static Group group = new Group();

	// segédvonalként
	private Line line = new Line();
	private Line tempLine = new Line();
	private Circle tempCircle = new Circle(5);
	private Circle focusCircle = new Circle(7);

	private static int i = 0;

	public Color getSprinklerColor() {
		return sprinklerColor;
	}

	public void setSprinklerColor(Color sprinklerColor) {
		this.sprinklerColor = sprinklerColor;
	}

	public double getSprinklerRadius() {
		return sprinklerRadius;
	}

	public void setSprinklerRadius(double sprinklerRadius) {
		this.sprinklerRadius = sprinklerRadius;
	}

	public boolean isSprinklerAttributesSet() {
		return sprinklerAttributesSet;
	}

	public void setSprinklerAttributesSet(boolean sprinklerAttributesSet) {
		this.sprinklerAttributesSet = sprinklerAttributesSet;
	}

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
		tempCircle.setVisible(false);
		focusCircle.setVisible(false);
		focusCircle.setStroke(Color.SILVER);
		focusCircle.setFill(Color.TRANSPARENT);

		angleInput.setVisible(false);
		angleInput.setMinWidth(40);
		angleInput.setPromptText("Szög");

		getChildren().addAll(group, angleInput, line, tempLine, tempCircle, focusCircle);

		setOnMouseClicked(e -> {
			if (sprinklerAttributesSet && e.getButton() == MouseButton.PRIMARY) {
				drawNewSprinkler(e);
			} else
				selectElement(e);
			requestFocus();
		});

		setOnMouseMoved(e -> {
			if (pressedKey != null && pressedKey.equals(KeyCode.CONTROL)) {

				if ((e.getX() % 50 < 10 || e.getX() > 40) && (e.getY() % 50 < 10 || e.getY() > 40)) {
					setCursor(Cursor.CROSSHAIR);
					Point2D focusCenter = Common.snapToGrid(new Point2D(e.getX(), e.getY()));
					focusCircle.setCenterX(focusCenter.getX());
					focusCircle.setCenterY(focusCenter.getY());
					focusCircle.setVisible(true);
				} else
					focusCircle.setVisible(false);
			} else
				focusCircle.setVisible(false);
			showTempLine(e);
		});

		addEventFilter(KeyEvent.KEY_PRESSED, e -> {
			pressedKey = e.getCode();
			if (pressedKey.equals(KeyCode.CONTROL)) {

			} else if (pressedKey.equals(KeyCode.ESCAPE)) {
				i = 0;
				line.setVisible(false);
				tempCircle.setVisible(false);
				angleInput.setVisible(false);
				angleInput.setText("");
				sprinklerAttributesSet = false;
			}
		});

		addEventFilter(KeyEvent.KEY_RELEASED, e -> {
			pressedKey = null;
			setCursor(Cursor.DEFAULT);
		});

	}

	private void selectElement(MouseEvent e) {
		for (Node node : getChildren()) {
			if (node instanceof javafx.scene.shape.Circle) {
				if (node.contains(e.getX(), e.getY())) {
					System.out.println("kijelöltük");
					double centerx = ((javafx.scene.shape.Circle) node).getCenterX();
					double centery = ((javafx.scene.shape.Circle) node).getCenterY();
					
					for (Sprinkler s : controller.listSprinklers()) {
						if (s.getCenterX() == centerx && s.getCenterY() == centery) {
							s.getCircle().setFill(Color.LAWNGREEN);
						}
					}
					
				}
			}
		}
	}

	private void drawNewSprinkler(MouseEvent mouseEvent) {

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
					Point2D center = Common.snapToGrid(new Point2D(centerX, centerY));
					centerX = center.getX();
					centerY = center.getY();
				}

			}

			tempCircle.setCenterX(centerX);
			tempCircle.setCenterY(centerY);
			tempCircle.setStroke(sprinklerColor);
			tempCircle.setFill(sprinklerColor);
			tempCircle.setVisible(true);
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
						Common.showAlert("Add meg a szórófej szögét!");
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
							tempCircle.setVisible(false);

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
							Common.showAlert("Számokban add meg a szórófej sugarát!");

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
			tempCircle.setVisible(false);

			sprinkler.setCenterX(centerX);
			sprinkler.setCenterY(centerY);
			sprinkler.setRadius(sprinklerRadius);
			sprinkler.setAngle(arcExtent);
			sprinkler.setColor(sprinklerColor);

			controller.addSprinkler(sprinkler);
			i++;
		}
	}

	private void showTempLine(MouseEvent mouseEvent) {
		if (sprinklerAttributesSet) {

			line.setStroke(Color.GAINSBORO);
			tempLine.setStroke(Color.GAINSBORO);

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

}
