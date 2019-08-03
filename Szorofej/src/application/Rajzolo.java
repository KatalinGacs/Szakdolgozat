package application;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

public class Rajzolo extends VBox {

	private ToggleButton btn = new ToggleButton("szórófej");

	private Pane canvasPane = new Pane();
	
	private ScrollPane scrollPane = new ScrollPane(canvasPane);

	private Canvas canvas = new Canvas(1500, 1500);

	private TextField angleInput = new TextField();

	private GraphicsContext gc;

	private List<SprinklerShape> lista = new ArrayList<SprinklerShape>();

	public Rajzolo() {
		getChildren().add(btn);

		canvasPane.setMinWidth(1500);
		canvasPane.setMinHeight(1500);
		scrollPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		scrollPane.setFitToWidth(true);
		scrollPane.setFitToHeight(true);

		
		canvasPane.getChildren().add(canvas);
		canvasPane.getChildren().add(angleInput);

		
		angleInput.setVisible(false);
		getChildren().add(scrollPane);
		gc = canvas.getGraphicsContext2D();

		gc.setFill(Color.WHITE);
		gc.fill();

		canvas.setOnMouseClicked(e -> {
			drawSprinkler(e);
		});
	}

	private static int i = 0;
	private double centerX = 0, centerY = 0, firstX = 0, firstY = 0, secondX = 0, secondY = 0;
	double startAngle, radius, arcExtent;

	private void drawSprinkler(MouseEvent mouseEvent) {
		gc.setStroke(Color.BLUE);
		gc.setLineWidth(1);

		if (btn.isSelected()) {

			if (i % 3 == 0) {
				centerX = mouseEvent.getX();
				centerY = mouseEvent.getY();
				gc.strokeOval(centerX - 5, centerY - 5, 10, 10);
				i++;
			} else if (i % 3 == 1) {
				firstX = mouseEvent.getX();
				firstY = mouseEvent.getY();

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
						radius = Math.sqrt(
								(firstX - centerX) * (firstX - centerX) + (firstY - centerY) * (firstY - centerY));
						gc.strokeArc(centerX - radius, centerY - radius, radius * 2, radius * 2, startAngle, arcExtent,
								ArcType.ROUND);
						i++;
						
						angleInput.setVisible(false);
						angleInput.setText("");
					}
				});
				gc.setStroke(Color.GREEN);
				gc.strokeLine(centerX, centerY, firstX, firstY);

				i++;
			} else if (i % 3 == 2) {
				angleInput.setVisible(false);
				secondX = mouseEvent.getX();
				secondY = mouseEvent.getY();
				gc.setStroke(Color.GREEN);
				startAngle = -Math.toDegrees(Math.atan((firstY - centerY) / (firstX - centerX)));
				if (firstX > centerX)
					System.out.println("jó?");
				else
					startAngle -= 180;

				radius = Math.sqrt((firstX - centerX) * (firstX - centerX) + (firstY - centerY) * (firstY - centerY));

				arcExtent = -Math.toDegrees(Math.atan((secondY - centerY) / (secondX - centerX)));
				if (secondX > centerX) {
					arcExtent -= startAngle;

				} else if (firstX < centerX && secondX > centerX) {
					arcExtent = 360 - arcExtent;
				}

				else {
					arcExtent = arcExtent - startAngle - 180;

				}

				System.out.println("arcextent" + arcExtent);

				gc.strokeArc(centerX - radius, centerY - radius, radius * 2, radius * 2, startAngle, arcExtent,
						ArcType.ROUND);
				i++;
			}

		}
	}
// gc.fillArc(10, 10, 50, 50, 30, 30, ArcType.ROUND);
// SprinklerShape s = new SprinklerShape(centerX, centerY, radiusX, radiusY,
// startAngle, length);
}
