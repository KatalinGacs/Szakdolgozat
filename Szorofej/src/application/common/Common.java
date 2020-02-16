package application.common;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;

public class Common {

	public static Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
	public static int pixelPerMeter = (int) (primaryScreenBounds.getWidth() / 50);
	public static String textstyle = "-fx-font: 24 arial;";
	public static String programName = "Öntözéstervezezõ";
	public static String version = "1.0";
	
	public static void showAlert(String contentText) {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setContentText(contentText);
		alert.setTitle("Hiba");
		alert.show();
	}

	public static Point2D snapToGrid(double mouseX, double mouseY) {
		double x = pixelPerMeter * (Math.round(mouseX / pixelPerMeter));
		double y = pixelPerMeter * (Math.round(mouseY / pixelPerMeter));
		return new Point2D(x, y);
	}

	public static Point2D snapToHorizontalOrVertival(double referenceX, double referenceY, double mouseX, double mouseY) {
		double diffX = Math.abs(referenceX - mouseX);
		double diffY = Math.abs(referenceY - mouseY);
		if (diffX < diffY)
			return new Point2D(referenceX, mouseY);
		else
			return new Point2D(mouseX, referenceY);
		//TODO kipróbálni
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

	public static void showLayer(Group layer) {
		for (Node node : layer.getChildren()) {
			node.setVisible(true);
		}
	}

	public static void hideLayer(Group layer) {
		for (Node node : layer.getChildren()) {
			node.setVisible(false);
		}
	}
}
