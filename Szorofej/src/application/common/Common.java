package application.common;

import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Common {

	public static void showAlert(String contentText) {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setContentText(contentText);
		alert.setTitle("Hiba");
		alert.show();
	}
	
	public static Point2D snapToGrid(double mouseX, double mouseY) {
		double x = 50*(Math.round(mouseX/50));
		double y = 50*(Math.round(mouseY/50));	
		return new Point2D(x, y);
		
	}
	
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
	
	
}


