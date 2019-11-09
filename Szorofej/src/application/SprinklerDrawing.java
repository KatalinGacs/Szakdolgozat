package application;

import application.CanvasPane.Use;
import application.SprinklerDrawing.SprinklerDrawingState;
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
import javafx.scene.shape.Shape;
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

	static void drawNewSprinkler(MouseEvent mouseEvent, CanvasPane canvasPane) {
		canvasPane.stateOfCanvasUse = CanvasPane.Use.SPRINKLERDRAWING;
		SprinklerShape sprinkler = new SprinklerShape();
		Circle circle = new Circle();
		Arc arc = new Arc();
		arc.setType(ArcType.ROUND);
		arc.setStroke(SprinklerDrawing.sprinklerColor);
		arc.setStrokeWidth(CanvasPane.strokeWidth);
		arc.setFill(Color.TRANSPARENT);
		sprinkler.setSprinkler(SprinklerDrawing.sprinklerType);
		sprinkler.setRadius(SprinklerDrawing.sprinklerRadius / Common.pixelPerMeter);

		if (SprinklerDrawing.drawingState == SprinklerDrawing.SprinklerDrawingState.CENTER
				&& !canvasPane.drawingSeveralSprinklers) {

			centerX = mouseEvent.getX();
			centerY = mouseEvent.getY();

			if (canvasPane.pressedKey == KeyCode.CONTROL) {
				if (canvasPane.showingFocusCircle) {
					Point2D center = Common.snapToGrid(centerX, centerY);
					centerX = center.getX();
					centerY = center.getY();
				}
			}

			SprinklerDrawing.tempSprinklerCircle.setCenterX(centerX);
			SprinklerDrawing.tempSprinklerCircle.setCenterY(centerY);
			SprinklerDrawing.tempSprinklerCircle.setStroke(SprinklerDrawing.sprinklerColor);
			SprinklerDrawing.tempSprinklerCircle.setFill(SprinklerDrawing.sprinklerColor);
			SprinklerDrawing.tempSprinklerCircle.setVisible(true);
			SprinklerDrawing.drawingState = SprinklerDrawing.SprinklerDrawingState.FIRSTSIDE;
		} else if (SprinklerDrawing.drawingState == SprinklerDrawing.SprinklerDrawingState.FIRSTSIDE) {
			firstX = mouseEvent.getX();
			firstY = mouseEvent.getY();

			if (canvasPane.pressedKey == KeyCode.CONTROL) {
				Point2D firstPoint = Common.snapToHorizontalOrVertival(centerX, centerY, firstX, firstY);
				firstX = firstPoint.getX();
				firstY = firstPoint.getY();
			}

			SprinklerDrawing.startAngle = -Math.toDegrees(Math.atan((firstY - centerY) / (firstX - centerX))) - 180;
			if (centerX <= firstX)
				SprinklerDrawing.startAngle -= 180;
			if (SprinklerDrawing.startAngle < 0)
				SprinklerDrawing.startAngle += 360;
			SprinklerDrawing.angleInput.setVisible(true);

			SprinklerDrawing.angleInput.setLayoutX(mouseEvent.getX());
			SprinklerDrawing.angleInput.setLayoutY(mouseEvent.getY());
			SprinklerDrawing.angleInput.relocate(centerX, centerY);
			SprinklerDrawing.angleInput.setOnKeyPressed(ke -> {
				if (ke.getCode().equals(KeyCode.ENTER)) {
					if (SprinklerDrawing.angleInput.getText() == null
							|| SprinklerDrawing.angleInput.getText().trim().isEmpty()) {
						Common.showAlert("Add meg a szórófej szögét!");
					} else
						try {

							if (Double.parseDouble(SprinklerDrawing.angleInput
									.getText()) > SprinklerDrawing.sprinklerType.getMaxAngle()
									|| Double.parseDouble(SprinklerDrawing.angleInput
											.getText()) < SprinklerDrawing.sprinklerType.getMinAngle()) {
								Common.showAlert("A megadott szög (" + SprinklerDrawing.angleInput.getText()
										+ ") nem esik az ennél a szórófejnél lehetséges intervallumba! Min. szög: "
										+ SprinklerDrawing.sprinklerType.getMinAngle() + ", max. szög: "
										+ SprinklerDrawing.sprinklerType.getMaxAngle());
							} else {
								SprinklerDrawing.arcExtent = -Double.parseDouble(SprinklerDrawing.angleInput.getText());
								arc.setCenterX(centerX);
								arc.setCenterY(centerY);
								arc.setRadiusX(SprinklerDrawing.sprinklerRadius);
								arc.setRadiusY(SprinklerDrawing.sprinklerRadius);
								arc.setStartAngle(SprinklerDrawing.startAngle);
								arc.setLength(-SprinklerDrawing.arcExtent);

								circle.setCenterX(centerX);
								circle.setCenterY(centerY);
								circle.setRadius(Common.pixelPerMeter / 4);
								circle.setStroke(SprinklerDrawing.sprinklerColor);
								circle.setFill(SprinklerDrawing.sprinklerColor);
								sprinkler.setCircle(circle);
								canvasPane.irrigationLayer.getChildren().add(sprinkler.getCircle());
								SprinklerDrawing.tempSprinklerCircle.setVisible(false);

								sprinkler.setArc(arc);

								canvasPane.sprinklerArcLayer.getChildren().add(sprinkler.getArc());
								controller.addSprinklerShape(sprinkler); // TODO !

								SprinklerDrawing.angleInput.setText("");
								SprinklerDrawing.angleInput.setVisible(false);
								SprinklerDrawing.drawingState = SprinklerDrawing.SprinklerDrawingState.CENTER;
							}
						} catch (NumberFormatException ex) {
							Common.showAlert("Számokban add meg a szórófej sugarát!");

						}
				}
			});

			SprinklerDrawing.drawingState = SprinklerDrawing.SprinklerDrawingState.SECONDSIDE;
		} else if (SprinklerDrawing.drawingState == SprinklerDrawing.SprinklerDrawingState.SECONDSIDE) {

			SprinklerDrawing.angleInput.setVisible(false);

			SprinklerDrawing.secondX = mouseEvent.getX();
			SprinklerDrawing.secondY = mouseEvent.getY();

			if (canvasPane.pressedKey == KeyCode.CONTROL) {
				Point2D firstPoint = Common.snapToHorizontalOrVertival(centerX, centerY, SprinklerDrawing.secondX,
						SprinklerDrawing.secondY);
				SprinklerDrawing.secondX = firstPoint.getX();
				SprinklerDrawing.secondY = firstPoint.getY();
			}
			double endAngle = -Math
					.toDegrees(Math.atan((SprinklerDrawing.secondY - centerY) / (SprinklerDrawing.secondX - centerX)))
					- 180;
			if (centerX <= SprinklerDrawing.secondX)
				endAngle -= 180;
			if (endAngle < -360)
				endAngle += 360;
			if (endAngle < 0)
				endAngle += 360;
			SprinklerDrawing.arcExtent = 360 - (360 - endAngle) - SprinklerDrawing.startAngle;
			if (SprinklerDrawing.arcExtent <= 0)
				SprinklerDrawing.arcExtent += 360;
			else if (SprinklerDrawing.arcExtent > 360)
				SprinklerDrawing.arcExtent -= 360;
			if (SprinklerDrawing.arcExtent > SprinklerDrawing.sprinklerType.getMaxAngle()
					|| SprinklerDrawing.arcExtent < SprinklerDrawing.sprinklerType.getMinAngle()) {
				Common.showAlert("A megadott szög (" + SprinklerDrawing.angleInput.getText()
						+ ") nem esik az ennél a szórófejnél lehetséges intervallumba! Min. szög: "
						+ SprinklerDrawing.sprinklerType.getMinAngle() + ", max. szög: "
						+ SprinklerDrawing.sprinklerType.getMaxAngle());
			} else {

				arc.setCenterY(centerY);
				arc.setCenterX(centerX);
				arc.setCenterY(centerY);
				arc.setRadiusX(SprinklerDrawing.sprinklerRadius);
				arc.setRadiusY(SprinklerDrawing.sprinklerRadius);
				arc.setStartAngle(SprinklerDrawing.startAngle);
				arc.setLength(SprinklerDrawing.arcExtent);
				sprinkler.setArc(arc);
				canvasPane.sprinklerArcLayer.getChildren().add(sprinkler.getArc());

				circle.setCenterX(centerX);
				circle.setCenterY(centerY);
				circle.setRadius(Common.pixelPerMeter / 4);
				circle.setStroke(SprinklerDrawing.sprinklerColor);
				circle.setFill(SprinklerDrawing.sprinklerColor);
				sprinkler.setCircle(circle);

				canvasPane.irrigationLayer.getChildren().add(sprinkler.getCircle());
				SprinklerDrawing.tempSprinklerCircle.setVisible(false);

				controller.addSprinklerShape(sprinkler);
				SprinklerDrawing.drawingState = SprinklerDrawing.SprinklerDrawingState.CENTER;
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
	}

	public static void selectLineForSprinklerDrawing(MouseEvent e, CanvasPane canvasPane) {
		for (Shape border : canvasPane.borderShapes) {
			if (border instanceof Line) {
				if (border.contains(e.getX(), e.getY())) {
					canvasPane.lineSelected = true;
					canvasPane.indexOfSelectedLine = canvasPane.borderShapes.indexOf(border);
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

			SprinklerDrawing.tempFirstSprinklerLine.setStroke(CanvasPane.tempLineColor);
			SprinklerDrawing.tempFirstSprinklerLine.setStrokeWidth(CanvasPane.strokeWidth);
			SprinklerDrawing.tempSecondSprinklerLine.setStroke(CanvasPane.tempLineColor);
			SprinklerDrawing.tempSecondSprinklerLine.setStrokeWidth(CanvasPane.strokeWidth);

			if (SprinklerDrawing.drawingState == SprinklerDrawing.SprinklerDrawingState.CENTER) {
				SprinklerDrawing.tempFirstSprinklerLine.setVisible(false);
				SprinklerDrawing.tempSecondSprinklerLine.setVisible(false);
			} else if (SprinklerDrawing.drawingState == SprinklerDrawing.SprinklerDrawingState.FIRSTSIDE) {
				SprinklerDrawing.tempFirstSprinklerLine.setStartX(centerX);
				SprinklerDrawing.tempFirstSprinklerLine.setStartY(SprinklerDrawing.centerY);
				if (canvasPane.pressedKey == KeyCode.CONTROL) {
					Point2D firstPoint = Common.snapToHorizontalOrVertival(centerX, SprinklerDrawing.centerY,
							mouseEvent.getX(), mouseEvent.getY());
					SprinklerDrawing.tempFirstSprinklerLine.setEndX(firstPoint.getX());
					SprinklerDrawing.tempFirstSprinklerLine.setEndY(firstPoint.getY());
				} else {
					SprinklerDrawing.tempFirstSprinklerLine.setEndX(mouseEvent.getX());
					SprinklerDrawing.tempFirstSprinklerLine.setEndY(mouseEvent.getY());
				}
				SprinklerDrawing.tempFirstSprinklerLine.setVisible(true);
				SprinklerDrawing.tempSecondSprinklerLine.setVisible(false);
			} else if (SprinklerDrawing.drawingState == SprinklerDrawing.SprinklerDrawingState.SECONDSIDE) {
				SprinklerDrawing.tempSecondSprinklerLine.setStartX(centerX);
				SprinklerDrawing.tempSecondSprinklerLine.setStartY(SprinklerDrawing.centerY);

				SprinklerDrawing.tempSecondSprinklerLine.setEndX(SprinklerDrawing.firstX);
				SprinklerDrawing.tempSecondSprinklerLine.setEndY(SprinklerDrawing.firstY);
				SprinklerDrawing.tempSecondSprinklerLine.setVisible(true);
				SprinklerDrawing.tempFirstSprinklerLine.setStartX(centerX);
				SprinklerDrawing.tempFirstSprinklerLine.setStartY(SprinklerDrawing.centerY);

				if (canvasPane.pressedKey == KeyCode.CONTROL) {
					Point2D secondPoint = Common.snapToHorizontalOrVertival(centerX, SprinklerDrawing.centerY,
							mouseEvent.getX(), mouseEvent.getY());
					SprinklerDrawing.tempFirstSprinklerLine.setEndX(secondPoint.getX());
					SprinklerDrawing.tempFirstSprinklerLine.setEndY(secondPoint.getY());
				} else {
					SprinklerDrawing.tempFirstSprinklerLine.setEndX(mouseEvent.getX());
					SprinklerDrawing.tempFirstSprinklerLine.setEndY(mouseEvent.getY());
				}

				SprinklerDrawing.tempFirstSprinklerLine.setVisible(true);
			}

		}
	}

	public static void showSprinklersInALine(int numberOfSprinklersInALine, CanvasPane canvasPane) {
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
		if (canvasPane.lineSelected) {
			double startX = ((Line) canvasPane.borderShapes.get(canvasPane.indexOfSelectedLine)).getStartX();
			double endX = ((Line) canvasPane.borderShapes.get(canvasPane.indexOfSelectedLine)).getEndX();
			double diffX = startX - endX;
			double startY = ((Line) canvasPane.borderShapes.get(canvasPane.indexOfSelectedLine)).getStartY();
			double endY = ((Line) canvasPane.borderShapes.get(canvasPane.indexOfSelectedLine)).getEndY();
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

}
