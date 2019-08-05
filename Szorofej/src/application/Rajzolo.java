package application;

import controller.SprinklerController;
import controller.SprinklerControllerImpl;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.bean.Sprinkler;

public class Rajzolo extends VBox {

	SprinklerController controller = new SprinklerControllerImpl();

	private Button szorofejbtn = new Button("Sz�r�fej kiv�laszt�sa");

	private Pane canvasPane = new Pane();
	private ScrollPane scrollPane = new ScrollPane(canvasPane);
	protected static Group group = new Group();

	private TextField angleInput = new TextField();

	private Color sprinklerColor;
	private double sprinklerRadius;
	private boolean sprinklerAttributesSet = false;

	private Alert alert = new Alert(AlertType.WARNING);
	
	// seg�dvonalk�nt
	// TODO: az k�ne, hogy a line k�veti az egeret az els� kattint�s ut�n
	private Line line = new Line();
	private Circle tempCircle = new Circle(5);

	public Rajzolo() {

		getChildren().add(szorofejbtn);

		canvasPane.setMinWidth(1500);
		canvasPane.setMinHeight(1500);
		scrollPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		scrollPane.setFitToWidth(true);
		scrollPane.setPrefHeight(600);
		scrollPane.setMinHeight(USE_COMPUTED_SIZE);

		line.setVisible(false);
		tempCircle.setVisible(false);
		canvasPane.getChildren().addAll(group, angleInput, line, tempCircle);

		angleInput.setVisible(false);
		angleInput.setMinWidth(40);
		angleInput.setPromptText("Sz�g");

		getChildren().add(scrollPane);

		canvasPane.setOnMouseClicked(e -> {
			drawNewSprinkler(e);
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
		Text colorText = new Text("Sz�n: ");
		ColorPicker colorPicker = new ColorPicker();
		Text radiusText = new Text("Sug�r: ");
		TextField radiusField = new TextField();
		sprinklerInfoStage.setScene(scene);
		Button ok = new Button("OK");
		root.getChildren().addAll(colorText, colorPicker, radiusText, radiusField, ok);

		ok.setOnAction(e -> {
			if (radiusField.getText().equals("")) {
				alert.setTitle("Hiba");
				alert.setContentText("Add meg a sz�r�fej sugar�t!");
				alert.show();
			} else {
				try {
					sprinklerRadius = Double.parseDouble(radiusField.getText());
					sprinklerAttributesSet = true;
					sprinklerColor = colorPicker.getValue();
				} catch (NumberFormatException ex) {
					alert.setTitle("Hiba");
					alert.setContentText("Sz�mokban add meg a sz�r�fej sugar�t!");
					alert.show();
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

		if (sprinklerAttributesSet) {

			if (mouseEvent.getButton() == MouseButton.PRIMARY && i % 3 == 0) {

				centerX = mouseEvent.getX();
				centerY = mouseEvent.getY();

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

				line.setStroke(Color.GAINSBORO);
				line.setStartX(centerX);
				line.setStartY(centerY);
				line.setEndX(firstX);
				line.setEndY(firstY);
				line.setVisible(true);

				angleInput.setVisible(true);
				angleInput.setLayoutX(mouseEvent.getX());
				angleInput.setLayoutY(mouseEvent.getY());
				angleInput.setMaxWidth(30);
				angleInput.relocate(centerX, centerY);
				angleInput.setOnKeyPressed(ke -> {
					if (ke.getCode().equals(KeyCode.ENTER)) {
						try {
							arcExtent = -Double.parseDouble(angleInput.getText());
						} catch (NumberFormatException ex) {
							alert.setTitle("Hiba");
							alert.setContentText("Sz�mokban add meg a sz�r�fej sugar�t!");
							alert.show();
						}
						arc.setCenterX(centerX);
						arc.setCenterY(centerY);
						arc.setRadiusX(sprinklerRadius);
						arc.setRadiusY(sprinklerRadius);
						arc.setStartAngle(startAngle);
						arc.setLength(-arcExtent);
						arc.setType(ArcType.ROUND);
						arc.setStroke(sprinklerColor);
						arc.setFill(Color.TRANSPARENT);

						circle.setCenterX(centerX);
						circle.setCenterY(centerY);
						circle.setRadius(5);
						circle.setStroke(sprinklerColor);
						circle.setFill(sprinklerColor);
						sprinkler.setCircle(circle);
						group.getChildren().add(sprinkler.getCircle());
						tempCircle.setVisible(false);
						line.setVisible(false);

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
					}
				});

				i++;
			} else if (mouseEvent.getButton() == MouseButton.PRIMARY && i % 3 == 2) {
				line.setVisible(false);
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
				arc.setType(ArcType.ROUND);
				arc.setStroke(sprinklerColor);
				arc.setFill(Color.TRANSPARENT);
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
