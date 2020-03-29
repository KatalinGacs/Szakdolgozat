package application;

import application.common.Common;
import controller.SprinklerController;
import controller.SprinklerControllerImpl;
import javafx.geometry.Point2D;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import model.bean.SprinklerShape;
import model.bean.SprinklerType;

public class SprinklerDrawing {

	static SprinklerController controller = new SprinklerControllerImpl();

	static TextField angleInput = new TextField();

	static double arcExtent;

	static double centerX = 0;
	static double centerY = 0;
	static double firstX = 0;
	static double firstY = 0;
	static double secondX = 0;
	static double secondY = 0;

	static Color sprinklerColor = Color.BLUE;
	protected static double sprinklerRadius;
	protected static SprinklerType sprinklerType;
	static double startAngle;

	static Line tempFirstSprinklerLine = new Line();
	static Line tempSecondSprinklerLine = new Line();
	static Circle tempSprinklerCircle = new Circle(Common.pixelPerMeter / 4);

	enum SprinklerDrawingState {
		CENTER, FIRSTSIDE, SECONDSIDE
	}

	static SprinklerDrawingState drawingState = SprinklerDrawingState.CENTER;

	private static SprinklerShape sprinkler;
	private static Circle circle;
	private static Arc arc;
	private static Text label;

	static void drawNewSprinkler(MouseEvent mouseEvent, CanvasPane canvasPane) {

		canvasPane.stateOfCanvasUse = CanvasPane.Use.SPRINKLERDRAWING;
		circle = new Circle();
		arc = new Arc();
		arc.setType(ArcType.ROUND);
		arc.setStroke(sprinklerColor);
		arc.setStrokeWidth(CanvasPane.strokeWidth);
		arc.setFill(Color.TRANSPARENT);

		
		if (SprinklerDrawing.drawingState == SprinklerDrawingState.CENTER
				&& !canvasPane.drawingSeveralSprinklers) {

			boolean validPoint = true;
			for (Shape s : controller.listObstacles()) {
				if (s.contains(mouseEvent.getX(), mouseEvent.getY()) ) {
					Common.showAlert("Ezen a ponton tereptárgy található");
					validPoint = false;
					break;
				}
			}
			if (validPoint) {
				sprinkler = new SprinklerShape();

				label = new Text(sprinkler.getSprinkler().getName());

				sprinkler.setSprinkler(sprinklerType);
				sprinkler.setRadius(sprinklerRadius / Common.pixelPerMeter);

				centerX = mouseEvent.getX();
				centerY = mouseEvent.getY();

				if (canvasPane.pressedKey == KeyCode.CONTROL) {
					if (canvasPane.showingFocusCircle) {
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
				drawingState = SprinklerDrawingState.FIRSTSIDE;
			}
		} else if (SprinklerDrawing.drawingState == SprinklerDrawingState.FIRSTSIDE) {
			firstX = mouseEvent.getX();
			firstY = mouseEvent.getY();

			if (canvasPane.pressedKey == KeyCode.CONTROL) {
				Point2D firstPoint = Common.snapToHorizontalOrVertival(centerX, centerY, firstX, firstY);
				firstX = firstPoint.getX();
				firstY = firstPoint.getY();
			}

			if (canvasPane.drawingSeveralSprinklers) {
				sprinkler = new SprinklerShape();

				label = new Text(sprinkler.getSprinkler().getName());

				sprinkler.setSprinkler(sprinklerType);
				sprinkler.setRadius(sprinklerRadius / Common.pixelPerMeter);

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
					if (angleInput.getText() == null
							|| angleInput.getText().trim().isEmpty()) {
						Common.showAlert("Add meg a szórófej szögét!");
					} else
						try {
							if (Double.parseDouble(angleInput
									.getText()) > sprinklerType.getMaxAngle()
									|| Double.parseDouble(angleInput
											.getText()) < sprinklerType.getMinAngle()) {
								Common.showAlert("A megadott szög (" + angleInput.getText()
										+ ") nem esik az ennél a szórófejnél lehetséges intervallumba! Min. szög: "
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
								canvasPane.irrigationLayer.getChildren().add(sprinkler.getCircle());

								tempSprinklerCircle.setVisible(false);

								sprinkler.setArc(arc);

								canvasPane.sprinklerArcLayer.getChildren().add(sprinkler.getArc());
								controller.addSprinklerShape(sprinkler);

								angleInput.setText("");
								angleInput.setVisible(false);
								drawingState = SprinklerDrawingState.CENTER;
							}
						} catch (NumberFormatException ex) {
							Common.showAlert("Számokban add meg a szórófej sugarát!");

						}
				}
			});

			drawingState = SprinklerDrawingState.SECONDSIDE;
		} else if (drawingState == SprinklerDrawingState.SECONDSIDE) {
			canvasPane.setModifiedSinceLastSave(true);

			angleInput.setVisible(false);

			secondX = mouseEvent.getX();
			secondY = mouseEvent.getY();

			if (canvasPane.pressedKey == KeyCode.CONTROL) {
				Point2D firstPoint = Common.snapToHorizontalOrVertival(centerX, centerY, secondX,
						secondY);
				secondX = firstPoint.getX();
				secondY = firstPoint.getY();
			}
			double endAngle = -Math
					.toDegrees(Math.atan((secondY - centerY) / (secondX - centerX)))
					- 180;
			if (centerX <= secondX)
				endAngle -= 180;
			if (endAngle < -360)
				endAngle += 360;
			if (endAngle < 0)
				endAngle += 360;
			arcExtent = 360 - (360 - endAngle) - startAngle;
			if (arcExtent <= 0)
				arcExtent += 360;
			else if (arcExtent > 360)
				arcExtent -= 360;
			if (arcExtent > sprinklerType.getMaxAngle()
					|| arcExtent < sprinklerType.getMinAngle()) {
				Common.showAlert("A megadott szög (" + angleInput.getText()
						+ ") nem esik az ennél a szórófejnél lehetséges intervallumba! Min. szög: "
						+ sprinklerType.getMinAngle() + ", max. szög: "
						+ sprinklerType.getMaxAngle());
			} else {

				arc.setCenterY(centerY);
				arc.setCenterX(centerX);
				arc.setCenterY(centerY);
				arc.setRadiusX(sprinklerRadius);
				arc.setRadiusY(sprinklerRadius);
				arc.setStartAngle(startAngle);
				arc.setLength(arcExtent);
				sprinkler.setArc(arc);
				canvasPane.sprinklerArcLayer.getChildren().add(sprinkler.getArc());

				circle.setCenterX(centerX);
				circle.setCenterY(centerY);
				circle.setRadius(Common.pixelPerMeter / 4);
				circle.setStroke(sprinklerColor);
				circle.setFill(sprinklerColor);
				sprinkler.setCircle(circle);
				canvasPane.irrigationLayer.getChildren().add(sprinkler.getCircle());

				tempSprinklerCircle.setVisible(false);

				label.setX(centerX);
				label.setY(centerY - (Common.pixelPerMeter / 2));
				label.setStyle(Common.textstyle);
				sprinkler.setLabel(label);
				canvasPane.sprinklerTextLayer.getChildren().add(sprinkler.getLabel());

				controller.addSprinklerShape(sprinkler);
				drawingState = SprinklerDrawingState.CENTER;
				if (canvasPane.drawingSeveralSprinklers)
					drawSeveralSprinklers(canvasPane);
			}
		}
	}

	public static void endSprinklerDrawing(CanvasPane canvasPane) {
		drawingState = SprinklerDrawingState.CENTER;
		tempFirstSprinklerLine.setVisible(false);
		tempSprinklerCircle.setVisible(false);
		angleInput.setVisible(false);
		angleInput.setText("");
		canvasPane.sprinklerAttributesSet = false;
		clearTempSprinklersInALine(canvasPane);
	}

	public static void selectLineForSprinklerDrawing(MouseEvent e, CanvasPane canvasPane) {
		for (Shape border : controller.listBorderShapes()) {
			if (border instanceof Line) {
				if (border.contains(e.getX(), e.getY())) {
					canvasPane.lineSelected = true;
					canvasPane.indexOfSelectedLine = controller.listBorderShapes().indexOf(border);
					canvasPane.stateOfCanvasUse = CanvasPane.Use.SPRINKLERDRAWING;
					
				}
			}
		}
	}

	/**
	 * Shows a temporary line while drawing the sprinkler arc, between the center of
	 * the arc and the two endpoints
	 * 
	 * @param mouseEvent
	 */
	public static void showTempLine(MouseEvent mouseEvent, CanvasPane canvasPane) {
		if (canvasPane.sprinklerAttributesSet && canvasPane.stateOfCanvasUse == CanvasPane.Use.SPRINKLERDRAWING) {
			tempFirstSprinklerLine.setStroke(CanvasPane.tempLineColor);
			tempFirstSprinklerLine.setStrokeWidth(CanvasPane.strokeWidth);
			tempSecondSprinklerLine.setStroke(CanvasPane.tempLineColor);
			tempSecondSprinklerLine.setStrokeWidth(CanvasPane.strokeWidth);

			if (drawingState == SprinklerDrawingState.CENTER) {
				tempFirstSprinklerLine.setVisible(false);
				tempSecondSprinklerLine.setVisible(false);
			} else if (drawingState == SprinklerDrawingState.FIRSTSIDE) {
				tempFirstSprinklerLine.setStartX(centerX);
				tempFirstSprinklerLine.setStartY(centerY);
				if (canvasPane.pressedKey == KeyCode.CONTROL) {
					Point2D firstPoint = Common.snapToHorizontalOrVertival(centerX, centerY,
							mouseEvent.getX(), mouseEvent.getY());
					tempFirstSprinklerLine.setEndX(firstPoint.getX());
					tempFirstSprinklerLine.setEndY(firstPoint.getY());
				} else {
					tempFirstSprinklerLine.setEndX(mouseEvent.getX());
					tempFirstSprinklerLine.setEndY(mouseEvent.getY());
				}
				tempFirstSprinklerLine.setVisible(true);
				tempSecondSprinklerLine.setVisible(false);
			} else if (drawingState == SprinklerDrawingState.SECONDSIDE) {
				tempSecondSprinklerLine.setStartX(centerX);
				tempSecondSprinklerLine.setStartY(centerY);

				tempSecondSprinklerLine.setEndX(firstX);
				tempSecondSprinklerLine.setEndY(firstY);
				tempSecondSprinklerLine.setVisible(true);
				tempFirstSprinklerLine.setStartX(centerX);
				tempFirstSprinklerLine.setStartY(centerY);

				if (canvasPane.pressedKey == KeyCode.CONTROL) {
					Point2D secondPoint = Common.snapToHorizontalOrVertival(centerX, centerY,
							mouseEvent.getX(), mouseEvent.getY());
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

	public static void showSprinklersInALine(int numberOfSprinklersInALine, CanvasPane canvasPane) {
		clearTempSprinklersInALine(canvasPane);
		if (canvasPane.lineSelected) {
			double startX = ((Line) controller.listBorderShapes().get(canvasPane.indexOfSelectedLine)).getStartX();
			double endX = ((Line) controller.listBorderShapes().get(canvasPane.indexOfSelectedLine)).getEndX();
			double diffX = startX - endX;
			double startY = ((Line) controller.listBorderShapes().get(canvasPane.indexOfSelectedLine)).getStartY();
			double endY = ((Line) controller.listBorderShapes().get(canvasPane.indexOfSelectedLine)).getEndY();
			double diffY = startY - endY;

			for (int i = 0; i < numberOfSprinklersInALine; i++) {
				Circle center = new Circle(Common.pixelPerMeter / 4);
				center.setStroke(CanvasPane.tempLineColor);
				center.setFill(CanvasPane.tempLineColor);
				center.setCenterX(startX - i * (diffX / (numberOfSprinklersInALine - 1)));
				center.setCenterY(startY - i * (diffY / (numberOfSprinklersInALine - 1)));
				canvasPane.tempLineLayer.getChildren().add(center);
				Circle c = new Circle();
				c.setStroke(CanvasPane.tempLineColor);
				c.setStrokeWidth(CanvasPane.strokeWidth);
				c.setRadius(sprinklerRadius);
				c.setFill(null);
				c.setCenterX(startX - i * (diffX / (numberOfSprinklersInALine - 1)));
				c.setCenterY(startY - i * (diffY / (numberOfSprinklersInALine - 1)));
				canvasPane.tempLineLayer.getChildren().add(c);

				canvasPane.tempSprinklerCirclesInALine.add(c);
				canvasPane.tempSprinklerCentersInALine.add(center);
	
			}
		} else
			Common.showAlert("A vonal nincs kiválasztva!");
	}

	// TODO ha az elsõnél nyomok escapet, rosszul mûködik - mert az event filter targetje nem a canvaspane
	// https://stackoverflow.com/questions/25740103/javafx-what-is-the-difference-between-eventhandler-and-eventfilter
	public static void drawSeveralSprinklers(CanvasPane canvasPane) {
		canvasPane.lineSelected = false;
		if (canvasPane.tempSprinklerCentersInALine.isEmpty()) {
			canvasPane.drawingSeveralSprinklers = false;
		} else {
			canvasPane.drawingSeveralSprinklers = true;
			canvasPane.tempLineLayer.getChildren().remove(canvasPane.tempSprinklerCirclesInALine.get(0));
			canvasPane.tempLineLayer.getChildren().remove(canvasPane.tempSprinklerCentersInALine.get(0));
			centerX = canvasPane.tempSprinklerCentersInALine.get(0).getCenterX();
			centerY = canvasPane.tempSprinklerCentersInALine.get(0).getCenterY();
			canvasPane.tempSprinklerCentersInALine.remove(0);
			canvasPane.tempSprinklerCirclesInALine.remove(0);
			tempSprinklerCircle.setCenterX(centerX);
			tempSprinklerCircle.setCenterY(centerY);
			tempSprinklerCircle.setStroke(sprinklerColor);
			tempSprinklerCircle.setFill(sprinklerColor);
			tempSprinklerCircle.setVisible(true);
			drawingState = SprinklerDrawingState.FIRSTSIDE;
		}
	}

	private static void clearTempSprinklersInALine(CanvasPane canvasPane) {
		if (!canvasPane.tempSprinklerCentersInALine.isEmpty())
			for (Circle c : canvasPane.tempSprinklerCentersInALine) {
				canvasPane.tempLineLayer.getChildren().remove(c);
			}
		if (!canvasPane.tempSprinklerCirclesInALine.isEmpty())
			for (Circle c : canvasPane.tempSprinklerCirclesInALine) {
				canvasPane.tempLineLayer.getChildren().remove(c);
			}
		canvasPane.tempSprinklerCirclesInALine.clear();
		canvasPane.tempSprinklerCentersInALine.clear();
	}
	
	
}
