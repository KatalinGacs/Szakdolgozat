package application;

import controller.SprinklerController;
import controller.SprinklerControllerImpl;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.bean.Sprinkler;

public class Rajzolo extends VBox {
	
	SprinklerController controller = new SprinklerControllerImpl();

	private ToggleButton btn = new ToggleButton("szórófej");

	private Button szorofejbtn = new Button("Szórófej kiválasztása");

	private Pane canvasPane = new Pane();

	private ScrollPane scrollPane = new ScrollPane(canvasPane);

	private Canvas canvas = new Canvas(1500, 1500);

	private TextField angleInput = new TextField();

	private GraphicsContext gc;

	//private List<SprinklerShape> lista = new ArrayList<SprinklerShape>();

	private Color sprinklerColor;
	private double sprinklerRadius;
	private Line line = new Line();

	public Rajzolo() {
		getChildren().addAll(btn, szorofejbtn);

		canvasPane.setMinWidth(1500);
		canvasPane.setMinHeight(1500);
		scrollPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		scrollPane.setFitToWidth(true);
		scrollPane.setFitToHeight(true);
		line.setVisible(false);

		canvasPane.getChildren().addAll(canvas, angleInput, line);
		

		angleInput.setVisible(false);
		getChildren().add(scrollPane);
		gc = canvas.getGraphicsContext2D();

		gc.setFill(Color.WHITE);
		gc.fill();

		canvas.setOnMouseClicked(e -> {
			drawNewSprinkler(e);
		});

		szorofejbtn.setOnAction(e -> {
			setSprinklerAttributes();
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
		gc.setStroke(sprinklerColor);
		gc.setLineWidth(1);
		

		if (i % 3 == 0) {
		
			centerX = mouseEvent.getX();
			centerY = mouseEvent.getY();
			System.out.println("centerX " + centerX);
			System.out.println("centerY " + centerY);
			gc.strokeOval(centerX - 5, centerY - 5, 10, 10);

			
			i++;
		} else if (i % 3 == 1) {
			firstX = mouseEvent.getX();
			firstY = mouseEvent.getY();
			
			startAngle = -Math.toDegrees(Math.atan((firstY - centerY) / (firstX - centerX)));
			
			//gc.setStroke(Color.GREY);
			//gc.strokeLine(centerX, centerY, firstX, firstY);
			
	
			line.setStroke(Color.GAINSBORO);
			line.setStartX(centerX);
			line.setStartY(centerY);
			line.setEndX(firstX);
			line.setEndY(firstY);
			line.setVisible(true);
			
			
			//gc.setStroke(sprinklerColor);
			
			
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
					gc.strokeArc(centerX - radius, centerY - radius, radius * 2, radius * 2, startAngle, arcExtent,
							ArcType.ROUND);
					i++;

					angleInput.setVisible(false);
					angleInput.setText("");
				}
			});
			//gc.setStroke(sprinklerColor);
			//gc.strokeLine(centerX, centerY, firstX, firstY);

			i++;
		} else if (i % 3 == 2) {
			line.setVisible(false);
			angleInput.setVisible(false);
			secondX = mouseEvent.getX();
			secondY = mouseEvent.getY();
			gc.setStroke(sprinklerColor);
			//startAngle = -Math.toDegrees(Math.atan((firstY - centerY) / (firstX - centerX)));
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

			System.out.println("arcextent" + arcExtent);

			gc.strokeArc(centerX - radius, centerY - radius, radius * 2, radius * 2, startAngle, arcExtent,
					ArcType.ROUND);
			controller.addSprinkler(new Sprinkler(radius, arcExtent, centerX, centerY, sprinklerColor));
			i++;
		}

	}
}
