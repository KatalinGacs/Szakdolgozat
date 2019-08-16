package application;

import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Common {

	public static void showAlert(String contentText) {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setContentText(contentText);
		alert.setTitle("Hiba");
		alert.show();
	}
	
	public static Point2D snapToGrid(Point2D mousePoisition) {
		double x = 50*(Math.round(mousePoisition.getX()/50));
		double y = 50*(Math.round(mousePoisition.getY()/50));	
		return new Point2D(x, y);
		
	}
}
