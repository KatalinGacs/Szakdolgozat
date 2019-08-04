package application;

import controller.SprinklerController;
import controller.SprinklerControllerImpl;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.bean.Sprinkler;

public class Rajzolo extends VBox {

	SprinklerController controller = new SprinklerControllerImpl();

	private Button szorofejbtn = new Button("Szórófej kiválasztása");

	private Pane canvasPane = new Pane();
	private ScrollPane scrollPane = new ScrollPane(canvasPane);
	protected static Group group = new Group();

	private TextField angleInput = new TextField();

	private Color sprinklerColor;
	private double sprinklerRadius;

	// segédvonalként
	private Line line = new Line();
	private Circle tempCircle = new Circle(5);

	public Rajzolo() {
		
		getChildren().add(szorofejbtn);

		canvasPane.setMinWidth(1500);
		canvasPane.setMinHeight(1500);
		scrollPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		scrollPane.setFitToWidth(true);
		scrollPane.setMinHeight(800);
		line.setVisible(false);
		tempCircle.setVisible(false);
		canvasPane.getChildren().addAll(group, angleInput, line, tempCircle);

		angleInput.setVisible(false);
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
	double startAngle, radius, arcExtent;

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
			sprinklerColor = colorPicker.getValue();
			sprinklerRadius = Double.parseDouble(radiusField.getText());
			sprinklerInfoStage.close();
		});

		sprinklerInfoStage.show();
	}

	private void drawNewSprinkler(MouseEvent mouseEvent) {

		Sprinkler sprinkler = new Sprinkler();

		Circle circle = new Circle();
		Arc arc = new Arc();

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

			startAngle = -Math.toDegrees(Math.atan((firstY - centerY) / (firstX - centerX)));

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
					arcExtent = Double.parseDouble(angleInput.getText());
					startAngle = -Math.toDegrees(Math.atan((firstY - centerY) / (firstX - centerX)));
					if (firstX > centerX)
						;
					else
						startAngle -= 180;
					radius = sprinklerRadius;

					arc.setCenterX(centerX);
					arc.setCenterY(centerY);
					arc.setRadiusX(radius);
					arc.setRadiusY(radius);
					arc.setStartAngle(startAngle);
					arc.setLength(arcExtent);
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
					
					sprinkler.setArc(arc);
					sprinkler.setCenterX(centerX);
					sprinkler.setCenterY(centerY);
					sprinkler.setRadius(radius);
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

			if (firstX > centerX)
				System.out.println("jó?");
			else
				startAngle -= 180;

			radius = sprinklerRadius;
			arcExtent = -Math.toDegrees(Math.atan((secondY - centerY) / (secondX - centerX)));
			if (secondX > centerX) {
				arcExtent -= startAngle;

			} else if (firstX < centerX && secondX > centerX) {
				arcExtent = 360 - arcExtent;
			}

			else {
				arcExtent = arcExtent - startAngle - 180;

			}
			arc.setCenterY(centerY);
			arc.setCenterX(centerX);
			arc.setCenterY(centerY);
			arc.setRadiusX(radius);
			arc.setRadiusY(radius);
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
			sprinkler.setRadius(radius);
			sprinkler.setAngle(arcExtent);
			sprinkler.setColor(sprinklerColor);

			controller.addSprinkler(sprinkler);
			i++;
		}
	}

}
