package application;

import application.common.Common;
import javafx.geometry.Point2D;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

/**
 * Helper class for drawing borders and obstacles on the CanvasPane
 * 
 * @author Gacs Katalin
 *
 */
public class BorderDrawing {

	/**
	 * The X coordinate from where a line starts, or a corner of a rectangle, or the
	 * middle of a circle.
	 */
	static double startX;

	/**
	 * The X coordinate from where a line starts, or a corner of a rectangle, or the
	 * middle of a circle.
	 */
	static double startY;
	
	/**
	 * The X coordinate from where a line ends.
	 */
	static double lineEndX;

	/**
	 * The Y coordinate from where a line ends.
	 */
	static double lineEndY;

	/**
	 * Input field when the user sets an exact length for a line.
	 */
	static TextField lengthInput = new TextField();

	/**
	 * Helper line showing where a borderline will be drawn. Begins at (startX,
	 * startY), ends where the mouse currently is. Shown only after (startX, startY)
	 * is set and before the end of the borderline is set.
	 */
	static Line tempBorderLine = new Line();

	/**
	 * Helper circle showing where a circle obstacle will be drawn. Center is at
	 * (startX, startY), radius is where the mouse currently is. Shown only after
	 * (startX, startY) is set and before the radius of the circle obstacle is set.
	 */
	static Circle tempCircle = new Circle();

	/**
	 * Helper rectangle showing where a rectangle obstacle will be drawn. One corner
	 * is at (startX, startY), the opposite corner is where the mouse currently is.
	 * Shown only after (startX, startY) is set and before the opposite corner of
	 * the rectangle obstacle is set.
	 */
	static Rectangle tempRectangle = new Rectangle();

	/**
	 * MShows the tempBorderLine during drawing. Called after (startX, startY) is
	 * set and before the end of the borderline is set.
	 * 
	 * @param e          MouseEvent, mouse moved, the position is the end of the
	 *                   tempBorderLine
	 * @param color      stroke color of the tempBorderLine
	 * @param canvasPane the CanvasPane on which the borderline is drawn
	 */
	public static void showTempBorderLine(MouseEvent e, Color color, CanvasPane canvasPane) {

		// ensure that the user does not draw outside the canvasPane
		double mouseX = Common.mouseEventWithinBounds(e).getX();
		double mouseY = Common.mouseEventWithinBounds(e).getY();

		lengthInput.setVisible(true);
		lengthInput.relocate(startX, startY);
		tempBorderLine.setStartX(startX);
		tempBorderLine.setStartY(startY);
		tempBorderLine.setStroke(color);
		tempBorderLine.setVisible(true);

		if (canvasPane.getPressedKey() == KeyCode.CONTROL) {
			Point2D point = Common.snapToHorizontalOrVertival(startX, startY, mouseX, mouseY);
			tempBorderLine.setEndX(point.getX());
			tempBorderLine.setEndY(point.getY());
		} else {
			tempBorderLine.setEndX(mouseX);
			tempBorderLine.setEndY(mouseY);
		}
	}

	/**
	 * Draws a borderline. Called after startDrawingBorder(). Drawing is done like a
	 * polyline, the end of a line is set to the beginning of the next line. But
	 * after drawing each part of the polyline can be only managed separately.
	 * Also used in pipe drawing.
	 * 
	 * @param e          MouseEvent, mouse clicked, the position is the end of the
	 *                   current line and the beginning of the next one
	 * @param color      stroke color of the BorderLine, set by the user
	 * @param width      width of the BorderLine, set by the user
	 * @param canvasPane the CanvasPane on which the borderline is drawn
	 */
	public static void drawBorderLine(MouseEvent e, Color color, int width, CanvasPane canvasPane) {

		// ensure that the user does not draw outside the canvasPane
		double clickX = Common.mouseEventWithinBounds(e).getX();
		double clickY = Common.mouseEventWithinBounds(e).getY();

		tempBorderLine.setVisible(false);

		Line line = new Line();
		line.setStartX(startX);
		line.setStartY(startY);
		line.setStrokeWidth(width);
		line.setStroke(color);
		double endX, endY;

		// make line ends be connectable with each other
		if (canvasPane.cursorNearLineEnd) {
			endX = lineEndX;
			endY = lineEndY;
		} else {
			endX = clickX;
			endY = clickY;
		}

		// handle when the user sets the length of the line in lengthInput inputfield
		if (!lengthInput.getText().trim().isEmpty() && lengthInput.getText() != null)
			try {
				double requiredLength = Double.parseDouble(lengthInput.getText()) * Common.pixelPerMeter;
				double drawnLength = Math
						.sqrt((startX - clickX) * (startX - clickX) + (startY - clickY) * (startY - clickY));
				double ratio = requiredLength / drawnLength;

				endX = startX + (clickX - startX) * ratio;
				endY = startY + (clickY - startY) * ratio;

				lengthInput.setText("");
				lengthInput.setVisible(false);

			} catch (NumberFormatException ex) {
				Common.showAlert("Számokban add meg a vonal hosszát vagy hagyd üresen a mezõt!");
			}

		// when Control is held down, only horizontal or vertical lines can be drawn
		if (canvasPane.getPressedKey() == KeyCode.CONTROL) {
			Point2D point = Common.snapToHorizontalOrVertival(startX, startY, endX, endY);
			endX = point.getX();
			endY = point.getY();
		}

		line.setEndX(endX);
		line.setEndY(endY);
		startX = endX;
		startY = endY;

		canvasPane.controller.addBorderShape(line);
		canvasPane.getBordersLayer().getChildren().add(line);
		canvasPane.setModifiedSinceLastSave(true);
	}

	/**
	 * Draws a rectangular obstacle. Called after startDrawingBorder().
	 * 
	 * @param e           MouseEvent, mouse released, the position is one corner of
	 *                    the rectangle, the other is (startX, startY)
	 * @param strokeColor stroke color of the rectangle, set by the user
	 * @param fillColor   fill color of the rectangle, set by the user
	 * @param width       width of the stroke of the rectangle, set by the user
	 * @param canvasPane  the CanvasPane on which the rectangle is drawn
	 */
	public static void drawBorderRectanlge(MouseEvent e, Color strokeColor, Color fillColor, int width,
			CanvasPane canvasPane) {

		// ensure that the user does not draw outside the canvasPane
		double clickX = Common.mouseEventWithinBounds(e).getX();
		double clickY = Common.mouseEventWithinBounds(e).getY();

		Rectangle rect = Common.drawRectangle(strokeColor, startX, startY, clickX, clickY);
		rect.setFill(fillColor);
		rect.setStrokeWidth(width);
		canvasPane.getBordersLayer().getChildren().add(rect);
		canvasPane.controller.addBorderShape(rect);
		canvasPane.controller.addObstacle(rect);
		canvasPane.setModifiedSinceLastSave(true);
	}

	/**
	 * Shows tempCircle where a circle obstacle will be drawn. Center is at (startX,
	 * startY), radius is where the mouse currently is. Called only after (startX,
	 * startY) is set and the mouse is moved over the CanvasPane.
	 * 
	 * @param e          MouseEvent, mouse dragged, the position indicates the
	 *                   radius
	 * @param stroke     stroke color of the tempCircle, set by the user
	 * @param fill       fill color of the tempCircle, set by the user
	 * @param canvasPane the CanvasPane on which the circle is drawn
	 */
	public static void showTempBorderCircle(MouseEvent e, Color stroke, Color fill, CanvasPane canvasPane) {

		// ensure that the user does not draw outside the canvasPane
		double mouseX = Common.mouseEventWithinBounds(e).getX();
		double mouseY = Common.mouseEventWithinBounds(e).getY();

		if (e.getButton() == MouseButton.PRIMARY) {
			double r = Math.sqrt((startX - mouseX) * (startX - mouseX) + (startY - mouseY) * (startY - mouseY));
			tempCircle.setCenterX(startX);
			tempCircle.setCenterY(startY);
			tempCircle.setRadius(r);
			tempCircle.setFill(fill);
			tempCircle.setStroke(stroke);
			tempCircle.setVisible(true);
		}
	}

	/**
	 * Shows tempRectangle where a rectanlge obstacle will be drawn. One corner is
	 * at (startX, startY), the opposite corner is where the mouse currently is.
	 * Called only after (startX, startY) is set and the mouse is moved over the
	 * CanvasPane.
	 * 
	 * @param e          MouseEvent, mouse dragged, the position indicates a corner
	 *                   of the rectangle
	 * @param stroke     stroke color of the tempRectangle, set by the user
	 * @param fill       fill color of the tempRectangle, set by the user
	 * @param canvasPane the CanvasPane on which the rectangle is drawn
	 */
	public static void showtempBorderRectanlge(MouseEvent e, Color stroke, Color fill, CanvasPane canvasPane) {

		// ensure that the user does not draw outside the canvasPane
		double mouseX = Common.mouseEventWithinBounds(e).getX();
		double mouseY = Common.mouseEventWithinBounds(e).getY();

		if (e.getButton() == MouseButton.PRIMARY) {
			double width = Math.abs(startX - mouseX);
			double height = Math.abs(startY - mouseY);
			double x = 0, y = 0;
			if (startX > mouseX)
				x = mouseX;
			else
				x = startX;
			if (startY > mouseY)
				y = mouseY;
			else
				y = startY;
			tempRectangle.setX(x);
			tempRectangle.setY(y);
			tempRectangle.setWidth(width);
			tempRectangle.setHeight(height);

			// this method is also used in zone editing, when selecting sprinkler heads for
			// the zone
			// the selection is a rectangle
			if (canvasPane.getStateOfCanvasUse() == CanvasPane.Use.ZONEEDITING) {
				tempRectangle.setStroke(canvasPane.getSelectionColor());
				tempRectangle.setFill(null);
			} else {
				tempRectangle.setStroke(stroke);
				tempRectangle.setFill(fill);
			}
			tempRectangle.setVisible(true);
		}
	}

	/**
	 * Begin drawing the borderline, circle or rectangle obstacle by clicking on the
	 * canvasPane. Sets the startX and startY.
	 * 
	 * @param e MouseEvent, click in case of line drawing, press in case of
	 *          rectangle or circle drawing
	 */
	public static void startDrawingBorder(MouseEvent e) {
		startX = e.getX();
		startY = e.getY();
	}

	/**
	 * Draws a circle obstacle. Called after startDrawingBorder().
	 * 
	 * @param e           MouseEvent, mouse released, the position is one end of the
	 *                    radius, the other is (startX, startY)
	 * @param strokeColor stroke color of the circle, set by the user
	 * @param fillColor   fill color of the circle, set by the user
	 * @param width       width of the stroke of the circle, set by the user
	 * @param canvasPane  the CanvasPane on which the circle is drawn
	 */
	public static void drawBorderCircle(MouseEvent e, Color strokeColor, Color fillColor, int width,
			CanvasPane canvasPane) {

		// ensure that the user does not draw outside the canvasPane
		double mouseX = Common.mouseEventWithinBounds(e).getX();
		double mouseY = Common.mouseEventWithinBounds(e).getY();

		double r = Math.sqrt((startX - mouseX) * (startX - mouseX) + (startY - mouseY) * (startY - mouseY));
		Circle circle = new Circle(startX, startY, r, null);
		circle.setStroke(strokeColor);
		circle.setFill(fillColor);
		circle.setStrokeWidth(width);
		canvasPane.getBordersLayer().getChildren().add(circle);
		canvasPane.controller.addBorderShape(circle);
		canvasPane.controller.addObstacle(circle);
		canvasPane.setModifiedSinceLastSave(true);
	}
	
}
