package application;

import controller.SprinklerController;
import controller.SprinklerControllerImpl;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.bean.Sprinkler;

public class Rajzolo extends VBox {

	SprinklerController controller = new SprinklerControllerImpl();

	private Button szorofejbtn = new Button("Szórófej kiválasztása");

	private CanvasPane canvasPane = new CanvasPane();
	private ZoomableScrollPane scrollPane = new ZoomableScrollPane(canvasPane);
	protected static Group group = new Group();

	private TextField angleInput = new TextField();

	private Color sprinklerColor;
	private double sprinklerRadius;
	private boolean sprinklerAttributesSet = false;

	// segédvonalként
	private Line line = new Line();
	private Line tempLine = new Line();
	private Circle tempCircle = new Circle(5);

	private KeyCode pressedKey;

	public Rajzolo() {

		getChildren().add(szorofejbtn);

		scrollPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		scrollPane.setFitToWidth(true);
		scrollPane.setPrefHeight(600);
		scrollPane.setMinHeight(USE_COMPUTED_SIZE);

		line.setVisible(false);
		tempCircle.setVisible(false);
		canvasPane.getChildren().addAll(group, angleInput, line, tempLine, tempCircle);

		angleInput.setVisible(false);
		angleInput.setMinWidth(40);
		angleInput.setPromptText("Szög");

		getChildren().add(scrollPane);

		canvasPane.setOnMouseClicked(e -> {
			drawNewSprinkler(e);
		});

		canvasPane.setOnMouseMoved(e -> {
			showTempLine(e);
		});

		canvasPane.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
			pressedKey = e.getCode();
			if (pressedKey == KeyCode.CONTROL) {
				setCursor(Cursor.CROSSHAIR);
			}
		});

		canvasPane.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
			pressedKey = null;
			setCursor(Cursor.DEFAULT);
		});

		szorofejbtn.setOnAction(e -> {
			setSprinklerAttributes();
		});
	}

	private static int i = 0;
	private double centerX = 0, centerY = 0, firstX = 0, firstY = 0, secondX = 0, secondY = 0;
	double startAngle, arcExtent;

	private void setSprinklerAttributes() {
		Stage sprinklerInfoStage = new Stage();
		HBox root = new HBox();
		Scene scene = new Scene(root, 400, 50);
		Text colorText = new Text("Szín: ");
		ColorPicker colorPicker = new ColorPicker();
		Text radiusText = new Text("Sugár: ");
		TextField radiusField = new TextField();
		sprinklerInfoStage.setScene(scene);
		Button ok = new Button("OK");
		root.getChildren().addAll(colorText, colorPicker, radiusText, radiusField, ok);

		ok.setOnAction(e -> {
			if (radiusField.getText() == null && radiusField.getText().trim().isEmpty()) {
				Common.showAlert("Add meg a szórófej sugarát!");
			} else {
				try {
					sprinklerRadius = Double.parseDouble(radiusField.getText());
					sprinklerAttributesSet = true;
					sprinklerColor = colorPicker.getValue();
					canvasPane.requestFocus();
				} catch (NumberFormatException ex) {
					Common.showAlert("Számokban add meg a szórófej sugarát!");
				}
			}
			sprinklerInfoStage.close();
		});
		sprinklerInfoStage.show();

	}

	private void drawNewSprinkler(MouseEvent mouseEvent) {

		Sprinkler sprinkler = new Sprinkler();

		Circle circle = new Circle();
		Arc arc = new Arc();
		arc.setType(ArcType.ROUND);
		arc.setStroke(sprinklerColor);
		arc.setFill(Color.TRANSPARENT);

		if (sprinklerAttributesSet) {

			if (mouseEvent.getButton() == MouseButton.PRIMARY && i % 3 == 0) {

				centerX = mouseEvent.getX();
				centerY = mouseEvent.getY();

				if (pressedKey == KeyCode.CONTROL) {
					Point2D center = Common.snapToGrid(new Point2D(centerX, centerY));
					centerX = center.getX();
					centerY = center.getY();

				}

				tempCircle.setCenterX(centerX);
				tempCircle.setCenterY(centerY);
				tempCircle.setStroke(sprinklerColor);
				tempCircle.setFill(sprinklerColor);
				tempCircle.setVisible(true);
				i++;
			} else if (mouseEvent.getButton() == MouseButton.PRIMARY && i % 3 == 1) {
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

								angleInput.setVisible(false);
								angleInput.setText("");
								canvasPane.requestFocus();

							} catch (NumberFormatException ex) {
								Common.showAlert("Számokban add meg a szórófej sugarát!");

							}

					}
				});

				i++;
			} else if (mouseEvent.getButton() == MouseButton.PRIMARY && i % 3 == 2) {

				angleInput.setVisible(false);
				secondX = mouseEvent.getX();
				secondY = mouseEvent.getY();

				arcExtent = -Math.toDegrees(Math.atan((secondY - centerY) / (secondX - centerX))) - startAngle;

				if ((secondX > centerX && firstY < centerY && secondY > centerY)
						|| (firstX < centerX && secondX > centerX)
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

	public void escapeHandler(KeyEvent ke) {
		if (ke.getCode().equals(KeyCode.ESCAPE)) {
			i = 0;
			line.setVisible(false);
			tempCircle.setVisible(false);
			angleInput.setVisible(false);
			angleInput.setText("");
			sprinklerAttributesSet = false;
		}
	}

}
