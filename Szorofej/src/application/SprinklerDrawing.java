package application;

import application.CanvasPane.Use;
import application.UndoManager.DrawingAction;
import application.common.Common;
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
import javafx.scene.text.Text;
import model.bean.SprinklerShape;
import model.bean.SprinklerType;

/**
 * Helper class for sprinkler drawing
 * 
 * @author Gacs Katalin
 *
 */
public class SprinklerDrawing {

	/**
	 * Controller to access infos from the database
	 */
	//static SprinklerController controller = new SprinklerControllerImpl();

	/**
	 * Input field where the user can set the angle of the currently drawn
	 * sprinklershape
	 */
	static TextField angleInput = new TextField();

	/**
	 * Angle of the arc of the sprinklershape in degrees
	 */
	static double arcExtent;

	/**
	 * The X coordinate of the center of the sprinklerhead
	 */
	static double centerX = 0;

	/**
	 * The Y coordinate of the center of the sprinklerhead
	 */
	static double centerY = 0;

	/**
	 * The X coordinate of the first end of the arc of the sprinklershape (the
	 * second end is counterclockwise from it)
	 */
	static double firstX = 0;

	/**
	 * The Y coordinate of the first end of the arc of the sprinklershape (the
	 * second end is counterclockwise from it)
	 */
	static double firstY = 0;

	/**
	 * The X coordinate of the second end of the arc of the sprinklershape
	 */
	static double secondX = 0;
	/**
	 * The Y coordinate of the second end of the arc of the sprinklershape
	 */
	static double secondY = 0;

	/**
	 * The stroke color of the sprinkler arc and the stroke and fill color of the
	 * sprinklerhead circle
	 */
	static Color sprinklerColor = Color.BLUE;

	/**
	 * Radius of the sprinkler arc in pixels
	 */
	protected static double sprinklerRadius;

	/**
	 * The sprinkler type set to be drawn
	 */
	protected static SprinklerType sprinklerType;

	/**
	 * Start angle of the arc of the sprinklershape
	 */
	static double startAngle;

	/**
	 * Helper line showing where the first side of the arc will be (the second is
	 * counterclockwise from it)
	 */
	static Line tempFirstSprinklerLine = new Line();

	/**
	 * Helper line showing where the second side of the arc will be
	 */
	static Line tempSecondSprinklerLine = new Line();
	static Circle tempSprinklerCircle = new Circle(Common.pixelPerMeter / 8);

	/**
	 * Possible states of drawing a sprinklershape
	 * 
	 * @author Gacs Katalin
	 *
	 */
	enum SprinklerDrawingState {
		CENTER, FIRSTSIDE, SECONDSIDE
	}

	/**
	 * Which side of a sprinklershape is being drawn
	 */
	static SprinklerDrawingState drawingState = SprinklerDrawingState.CENTER;

	/**
	 * The sprinklershape that is being drawn
	 */
	private static SprinklerShape sprinkler;

	/**
	 * The circle representing the spriknlerhead
	 */
	private static Circle circle;

	/**
	 * The arc representing the water coverage of the sprinkler head
	 */
	private static Arc arc;

	/**
	 * A label near the sprinklerhead showing its type
	 */
	private static Text label;

	/**
	 * Drawing a sprinklershape
	 * 
	 * @param mouseEvent mouse clicked on the canvas, if the center is drawn, it is
	 *                   center of the circle, if the first or the second side is
	 *                   being drawn, it is the line on which the first or the
	 *                   second side is located,
	 * @param canvasPane CanvasPane on which the sprinklershape is being drawn
	 */
	static void drawNewSprinkler(MouseEvent mouseEvent, CanvasPane canvasPane) {

		canvasPane.setStateOfCanvasUse(Use.SPRINKLERDRAWING);
		circle = new Circle();
		arc = new Arc();
		arc.setType(ArcType.ROUND);
		arc.setStroke(sprinklerColor);
		arc.setStrokeWidth(CanvasPane.getStrokeWidth());
		arc.setFill(Color.TRANSPARENT);

		// draw the center of the sprinklershape
		if (drawingState == SprinklerDrawingState.CENTER && !canvasPane.isDrawingSeveralSprinklers()) {

			// check that the user is not trying to draw on top of an obstacle
			boolean validPoint = true;
			for (Shape s : canvasPane.controller.listObstacles()) {
				if (s.contains(mouseEvent.getX(), mouseEvent.getY())) {
					Common.showAlert("Ezen a ponton tereptárgy található");
					validPoint = false;
					break;
				}
			}

			// if the user is not trying to draw on top of an obstacle, create the circle
			// for the sprinkler head
			if (validPoint) {
				sprinkler = new SprinklerShape();

				sprinkler.setSprinkler(sprinklerType);
				label = new Text(sprinkler.getSprinkler().getName());
				sprinkler.setRadius(sprinklerRadius / Common.pixelPerMeter);

				centerX = mouseEvent.getX();
				centerY = mouseEvent.getY();
				// if Control is pressed down, snap the center of the circle to the grid
				if (canvasPane.getPressedKey() == KeyCode.CONTROL) {
					if (canvasPane.showingFocusCircle) {
						Point2D center = Common.snapToGrid(centerX, centerY);
						centerX = center.getX();
						centerY = center.getY();
					}
				}
				setSprinklerCircleAttributes(tempSprinklerCircle, centerX, centerY);
	
				tempSprinklerCircle.setVisible(true);
				drawingState = SprinklerDrawingState.FIRSTSIDE;
			}

			// draw the first side of the sprinkler
		} else if (drawingState == SprinklerDrawingState.FIRSTSIDE) {
			firstX = mouseEvent.getX();
			firstY = mouseEvent.getY();

			// if Control is held down, the side can be only a vertical or horizontal line
			if (canvasPane.getPressedKey() == KeyCode.CONTROL) {
				Point2D firstPoint = Common.snapToHorizontalOrVertival(centerX, centerY, firstX, firstY);
				firstX = firstPoint.getX();
				firstY = firstPoint.getY();
			}

			// if the user is drawing several sprinklers in a line, their centers are
			// already set but the instantiation of the sprinklershape has to be done here
			if (canvasPane.isDrawingSeveralSprinklers()) {
				sprinkler = new SprinklerShape();
				sprinkler.setSprinkler(sprinklerType);
				label = new Text(sprinkler.getSprinkler().getName());
				sprinkler.setRadius(sprinklerRadius / Common.pixelPerMeter);
			}

			// calculate the start angle of the arc from the positions of the center and the first side
			startAngle = -Math.toDegrees(Math.atan((firstY - centerY) / (firstX - centerX))) - 180;
			if (centerX <= firstX)
				startAngle -= 180;
			if (startAngle < 0)
				startAngle += 360;

			// the user can set in an input field the arc extent
			angleInput.setVisible(true);
			angleInput.setLayoutX(mouseEvent.getX());
			angleInput.setLayoutY(mouseEvent.getY());
			angleInput.relocate(centerX, centerY);
			angleInput.setOnKeyPressed(ke -> {
				if (ke.getCode().equals(KeyCode.ENTER)) {
					if (angleInput.getText() == null || angleInput.getText().trim().isEmpty()) {
						Common.showAlert("Add meg a szórófej szögét!");
					} else
						try {
							if (Double.parseDouble(angleInput.getText()) > sprinklerType.getMaxAngle()
									|| Double.parseDouble(angleInput.getText()) < sprinklerType.getMinAngle()) {
								Common.showAlert("A megadott szög (" + angleInput.getText()
										+ ") nem esik az ennél a szórófejnél lehetséges intervallumba! Min. szög: "
										+ sprinklerType.getMinAngle() + ", max. szög: " + sprinklerType.getMaxAngle());
							}
							// if the user sets the arc extent in the input field then finish drawing the
							// sprinkler shape using this angle
							else {
								arcExtent = -Double.parseDouble(angleInput.getText());
								arc.setCenterX(centerX);
								arc.setCenterY(centerY);
								arc.setRadiusX(sprinklerRadius);
								arc.setRadiusY(sprinklerRadius);
								arc.setStartAngle(startAngle);
								arc.setLength(-arcExtent);

								setSprinklerCircleAttributes(circle, centerX, centerY);
								
								sprinkler.setCircle(circle);
								canvasPane.getIrrigationLayer().getChildren().add(sprinkler.getCircle());

								tempSprinklerCircle.setVisible(false);

								sprinkler.setArc(arc);

								canvasPane.getSprinklerArcLayer().getChildren().add(sprinkler.getArc());
								canvasPane.controller.addSprinklerShape(sprinkler);

								label.setX(centerX);
								label.setY(centerY - (Common.pixelPerMeter / 2));
								label.setStyle(Common.textstyle);
								sprinkler.setLabel(label);
								canvasPane.getSprinklerTextLayer().getChildren().add(sprinkler.getLabel());
								
								canvasPane.setDirty(true);

								angleInput.setText("");
								angleInput.setVisible(false);

								drawingState = SprinklerDrawingState.CENTER;
								
								UndoManager.getInstance().draw(DrawingAction.SPRINKLER, sprinkler);
							}
						} catch (NumberFormatException ex) {
							Common.showAlert("Számokban add meg a szórófej sugarát!");
						}
				}
			});
			drawingState = SprinklerDrawingState.SECONDSIDE;

			// draw the second side of the sprinkler shape and finish drawing it
		} else if (drawingState == SprinklerDrawingState.SECONDSIDE) {

			canvasPane.setDirty(true);

			angleInput.setVisible(false);

			secondX = mouseEvent.getX();
			secondY = mouseEvent.getY();

			// if Control is held down, the side can be only a vertical or horizontal line
			if (canvasPane.getPressedKey() == KeyCode.CONTROL) {
				Point2D firstPoint = Common.snapToHorizontalOrVertival(centerX, centerY, secondX, secondY);
				secondX = firstPoint.getX();
				secondY = firstPoint.getY();
			}

			// calculate the end angle of the arc from the positions of the center and the
			// second side and from this and the startangle calculate the arcextent
			double endAngle = -Math.toDegrees(Math.atan((secondY - centerY) / (secondX - centerX))) - 180;
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

			// check if the set arc extent is within the allowed range for this sprinkler
			// type
			if (arcExtent > sprinklerType.getMaxAngle() || arcExtent < sprinklerType.getMinAngle()) {
				Common.showAlert("A megadott szög (" + angleInput.getText()
						+ ") nem esik az ennél a szórófejnél lehetséges intervallumba! Min. szög: "
						+ sprinklerType.getMinAngle() + ", max. szög: " + sprinklerType.getMaxAngle());
			}
			// finish the sprinkler drawing if the set arc extent is within the allowed
			// range for this sprinkler type
			else {
				arc.setCenterY(centerY);
				arc.setCenterX(centerX);
				arc.setCenterY(centerY);
				arc.setRadiusX(sprinklerRadius);
				arc.setRadiusY(sprinklerRadius);
				arc.setStartAngle(startAngle);
				arc.setLength(arcExtent);
				sprinkler.setArc(arc);
				canvasPane.getSprinklerArcLayer().getChildren().add(sprinkler.getArc());

				setSprinklerCircleAttributes(circle, centerX, centerY);
			
				sprinkler.setCircle(circle);
				canvasPane.getIrrigationLayer().getChildren().add(sprinkler.getCircle());

				tempSprinklerCircle.setVisible(false);

				label.setX(centerX);
				label.setY(centerY - (Common.pixelPerMeter / 2));
				label.setStyle(Common.textstyle);
				sprinkler.setLabel(label);
				canvasPane.getSprinklerTextLayer().getChildren().add(sprinkler.getLabel());
				canvasPane.controller.addSprinklerShape(sprinkler);
				drawingState = SprinklerDrawingState.CENTER;
				UndoManager.getInstance().draw(DrawingAction.SPRINKLER, sprinkler);

				if (canvasPane.isDrawingSeveralSprinklers())
					drawSeveralSprinklers(canvasPane);
			}
		}
	}

	/**
	 * When sprinkler drawing is disrupted, finish it, delete half-drawn sprinkler
	 * shapes and hide helper lines and controls
	 * 
	 * @param canvasPane CanvasPane on which the sprinklershape is being drawn
	 */
	public static void endSprinklerDrawing(CanvasPane canvasPane) {
		drawingState = SprinklerDrawingState.CENTER;
		tempFirstSprinklerLine.setVisible(false);
		tempSecondSprinklerLine.setVisible(false);
		tempSprinklerCircle.setVisible(false);
		angleInput.setVisible(false);
		angleInput.setText("");
		canvasPane.setSprinklerAttributesSet(false);
		clearTempSprinklersInALine(canvasPane);
	}

	/**
	 * Select a line from the plan by clicking on it on which several sprinklers are
	 * to be drawn
	 * 
	 * @param e          Mouseevent, click on the canvasPane
	 * @param canvasPane CanvasPane on which the line can be found
	 */
	public static void selectLineForSprinklerDrawing(MouseEvent e, CanvasPane canvasPane) {
		for (Shape border : canvasPane.controller.listBorderShapes()) {
			if (border instanceof Line) {
				if (border.contains(e.getX(), e.getY())) {
					canvasPane.lineSelected = true;
					canvasPane.selectedLine = (Line) border;
					canvasPane.setStateOfCanvasUse(Use.SPRINKLERDRAWING);
				}
			}
		}
	}

	/**
	 * Show a temporary line while drawing the sprinkler arc, between the center of
	 * the arc and the one of the two endpoints
	 * 
	 * @param mouseEvent mouse moved where the endpoint could be
	 * @param canvasPane CanvasPane on which the sprinklershape is being drawn
	 */
	public static void showTempLine(MouseEvent mouseEvent, CanvasPane canvasPane) {
		if (canvasPane.isSprinklerAttributesSet() && canvasPane.getStateOfCanvasUse() == Use.SPRINKLERDRAWING) {
			tempFirstSprinklerLine.setStroke(CanvasPane.getTempLineColor());
			tempFirstSprinklerLine.setStrokeWidth(CanvasPane.getStrokeWidth());
			tempSecondSprinklerLine.setStroke(CanvasPane.getTempLineColor());
			tempSecondSprinklerLine.setStrokeWidth(CanvasPane.getStrokeWidth());

			if (drawingState == SprinklerDrawingState.CENTER) {
				tempFirstSprinklerLine.setVisible(false);
				tempSecondSprinklerLine.setVisible(false);
			} else if (drawingState == SprinklerDrawingState.FIRSTSIDE) {
				tempFirstSprinklerLine.setStartX(centerX);
				tempFirstSprinklerLine.setStartY(centerY);
				if (canvasPane.getPressedKey() == KeyCode.CONTROL) {
					Point2D firstPoint = Common.snapToHorizontalOrVertival(centerX, centerY, mouseEvent.getX(),
							mouseEvent.getY());
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

				if (canvasPane.getPressedKey() == KeyCode.CONTROL) {
					Point2D secondPoint = Common.snapToHorizontalOrVertival(centerX, centerY, mouseEvent.getX(),
							mouseEvent.getY());
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

	/**
	 * Show helper circles that show sprinkler positions and arcs when the user
	 * chooses to draw several sprinklers in a line with equal distance from each
	 * other
	 * 
	 * @param numberOfSprinklersInALine how many sprinkler shapes are on the line
	 *                                  with equal distance from each other
	 * @param canvasPane                CanvasPane on which the sprinklershapes are
	 *                                  to be being drawn
	 */
	public static void showSprinklersInALine(int numberOfSprinklersInALine, CanvasPane canvasPane) {
		clearTempSprinklersInALine(canvasPane);
		if (canvasPane.lineSelected) {
			double startX = canvasPane.selectedLine.getStartX();
			double endX = canvasPane.selectedLine.getEndX();
			double diffX = startX - endX;
			double startY = canvasPane.selectedLine.getStartY();
			double endY = canvasPane.selectedLine.getEndY();
			double diffY = startY - endY;

			for (int i = 0; i < numberOfSprinklersInALine; i++) {
				Circle center = new Circle(Common.pixelPerMeter / 8);
				center.setStroke(CanvasPane.getTempLineColor());
				center.setFill(CanvasPane.getTempLineColor());
				center.setCenterX(startX - i * (diffX / (numberOfSprinklersInALine - 1)));
				center.setCenterY(startY - i * (diffY / (numberOfSprinklersInALine - 1)));
				canvasPane.getTempLineLayer().getChildren().add(center);
				Circle c = new Circle();
				c.setStroke(CanvasPane.getTempLineColor());
				c.setStrokeWidth(CanvasPane.getStrokeWidth());
				c.setRadius(sprinklerRadius);
				c.setFill(null);
				c.setCenterX(startX - i * (diffX / (numberOfSprinklersInALine - 1)));
				c.setCenterY(startY - i * (diffY / (numberOfSprinklersInALine - 1)));
				canvasPane.getTempLineLayer().getChildren().add(c);

				canvasPane.tempSprinklerCirclesInALine.add(c);
				canvasPane.tempSprinklerCentersInALine.add(center);
			}
		} else
			Common.showAlert("A vonal nincs kiválasztva!");
	}

	/**
	 * Draw sprinklershapes on a line with equal distance from each other
	 * 
	 * @param canvasPane CanvasPane on which the sprinklershape is being drawn
	 */
	public static void drawSeveralSprinklers(CanvasPane canvasPane) {
		canvasPane.lineSelected = false;
		if (canvasPane.tempSprinklerCentersInALine.isEmpty()) {
			canvasPane.setDrawingSeveralSprinklers(false);
		} else {
			canvasPane.setDrawingSeveralSprinklers(true);
			canvasPane.getTempLineLayer().getChildren().remove(canvasPane.tempSprinklerCirclesInALine.get(0));
			canvasPane.getTempLineLayer().getChildren().remove(canvasPane.tempSprinklerCentersInALine.get(0));
			centerX = canvasPane.tempSprinklerCentersInALine.get(0).getCenterX();
			centerY = canvasPane.tempSprinklerCentersInALine.get(0).getCenterY();
			canvasPane.tempSprinklerCentersInALine.remove(0);
			canvasPane.tempSprinklerCirclesInALine.remove(0);
			setSprinklerCircleAttributes(tempSprinklerCircle, centerX, centerY);	
			tempSprinklerCircle.setVisible(true);
			drawingState = SprinklerDrawingState.FIRSTSIDE;
			canvasPane.requestFocus();
		}
	}

	/**
	 * Remove the helper circles from the canvasPane when there were several
	 * sprinklers drawn on a line with equal distance from each other and this
	 * drawing process was disrupted
	 * 
	 * @param canvasPane CanvasPane on which the sprinklershape were being drawn
	 */
	private static void clearTempSprinklersInALine(CanvasPane canvasPane) {
		if (!canvasPane.tempSprinklerCentersInALine.isEmpty())
			for (Circle c : canvasPane.tempSprinklerCentersInALine) {
				canvasPane.getTempLineLayer().getChildren().remove(c);
			}
		if (!canvasPane.tempSprinklerCirclesInALine.isEmpty())
			for (Circle c : canvasPane.tempSprinklerCirclesInALine) {
				canvasPane.getTempLineLayer().getChildren().remove(c);
			}
		canvasPane.tempSprinklerCirclesInALine.clear();
		canvasPane.tempSprinklerCentersInALine.clear();
	}
	
	private static void setSprinklerCircleAttributes(Circle circle, double centerX, double centerY) {
		circle.setCenterX(centerX);
		circle.setCenterY(centerY);
		circle.setRadius(Common.pixelPerMeter / 8);
		circle.setStroke(sprinklerColor);
		circle.setStrokeWidth(Common.pixelPerMeter / 10);
		circle.setFill(Color.TRANSPARENT);
	}

}
