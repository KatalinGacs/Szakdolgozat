package application.common;

import java.net.URL;

import application.Main;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;

/**
 * Helper class for miscellanous common problems
 * 
 * @author Gacs Katalin
 *
 */
public class Common {

	public static Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
	public static int pixelPerMeter = (int) (primaryScreenBounds.getWidth() / 50);
	public static String textstyle = "-fx-font: 24 arial;";
	public static String programName = "Öntözéstervezezõ";
	public static String version = "1.0";

	public static double canvasWidth = primaryScreenBounds.getWidth() * 5;
	public static double canvasHeight = primaryScreenBounds.getHeight() * 5;

	/**
	 * Show a warning alert with the given text
	 * 
	 * @param contentText the contentText of the alert
	 */
	public static void showAlert(String contentText) {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setContentText(contentText);
		alert.setTitle("Hiba");
		alert.show();
	}

	/**
	 * Ensure that the user does not draw outside the drawing area. If the user
	 * moves the mouse outside the drawing area while drawing, with this method the
	 * drawn shape can be cut off at the end of the drawing area.
	 * 
	 * @param e MouseEvent which could be outside the drawing area
	 * @return if the mouseeent coordinates where within the drawing area, return
	 *         its original position, otherwise return the closest positioin at the
	 *         border of the drawing area.
	 */
	public static Point2D mouseEventWithinBounds(MouseEvent e) {

		double x;
		double y;

		if (e.getX() < 0) {
			x = 0;
		} else if (e.getX() > canvasWidth) {
			x = canvasWidth;
		} else {
			x = e.getX();
		}

		if (e.getY() < 0) {
			y = 0;
		} else if (e.getY() > canvasHeight) {
			y = canvasHeight;
		} else {
			y = e.getY();
		}

		return new Point2D(x, y);
	}

	/**
	 * Calculate the closest grid point to a mouseevent to use it for "snap-to-grid"
	 * function
	 * 
	 * @param mouseX The X coordinate of the mouseevent
	 * @param mouseY The y coordinate of the mouseevent
	 * @return the closest grid point to the mouse coordinates
	 */
	public static Point2D snapToGrid(double mouseX, double mouseY) {
		double x = pixelPerMeter * (Math.round(mouseX / pixelPerMeter));
		double y = pixelPerMeter * (Math.round(mouseY / pixelPerMeter));
		return new Point2D(x, y);
	}

	/**
	 * Calculate a point which is on a horizontal or a vertical line with a
	 * reference point and is closest to the position of the mouse. Used for drawing
	 * horizontal or vertical lines.
	 * 
	 * @param referenceX X coordinate of the reference point
	 * @param referenceY Y coordinate of the reference point
	 * @param mouseX     X coordinate of the mouse
	 * @param mouseY     Y coordinate of the mouse
	 * @return a point that is horizontally or vertically aligned with the reference
	 *         point and is the possible closest to the mouse position
	 */
	public static Point2D snapToHorizontalOrVertival(double referenceX, double referenceY, double mouseX,
			double mouseY) {
		double diffX = Math.abs(referenceX - mouseX);
		double diffY = Math.abs(referenceY - mouseY);
		if (diffX < diffY)
			return new Point2D(referenceX, mouseY);
		else
			return new Point2D(mouseX, referenceY);
	}

	/**
	 * Help for method drawing rectangles because it is used in several different
	 * parts of the plan drawing
	 * 
	 * @param color   stroke color of the rectangle
	 * @param firstX  X coordinate of one corner of the rectangle
	 * @param firstY  Y coordinate of one corner of the rectangle
	 * @param secondX X coordinate of the opposite corner of the rectangle
	 * @param secondY Y coordinate of the opposite corner of the rectangle
	 * @return a Rectangle with the given attributes
	 */
	public static Rectangle drawRectangle(Color color, double firstX, double firstY, double secondX, double secondY) {
		double width = Math.abs(firstX - secondX);
		double height = Math.abs(firstY - secondY);
		double x = 0, y = 0;
		if (firstX > secondX)
			x = secondX;
		else
			x = firstX;
		if (firstY > secondY)
			y = secondY;
		else
			y = firstY;
		Rectangle rectangle = new Rectangle(x, y, width, height);
		rectangle.setStroke(color);
		rectangle.setFill(Color.TRANSPARENT);
		return rectangle;
	}

	/**
	 * Set every node in a layer visible
	 * 
	 * @param layer
	 */
	public static void showLayer(Group layer) {
		for (Node node : layer.getChildren()) {
			node.setVisible(true);
		}
	}

	/**
	 * Set every node in a layer invisible
	 * 
	 * @param layer
	 */
	public static void hideLayer(Group layer) {
		for (Node node : layer.getChildren()) {
			node.setVisible(false);
		}
	}
	
	//
	/**
	 * @return the path of the program in the following format file:/C:/Users/Gacs%20Katalin/git/Szakdolgozat/Szorofej/
	 */
	public static String getSourceFolder() {
		ClassLoader loader = Main.class.getClassLoader();
		String temp = loader.getResource("").toString();
		int index = temp.indexOf("target");
		String result = temp.substring(0, index);
		return result;
	}
}
