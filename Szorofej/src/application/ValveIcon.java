package application;

import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Shape;
import utilities.Common;

/**
 * Class for creating a custom shape representing the solenoid valve
 * 
 * @author Gacs Katalin
 *
 */
public class ValveIcon {

	/**
	 * Create a custom shape representing the solenoid valve
	 * 
	 * @param centerX The X coordinate of the center of the solenoid valve
	 * @param centerY The X coordinate of the center of the solenoid valve
	 * @param color   The fill color of the solenoid valve shape
	 * @return custom solenoid valve shape
	 */
	public static Shape valveIcon(double centerX, double centerY, Color color) {
		Shape valveShape = null;
		try {
			double radius = Common.pixelPerMeter / 2;

			Arc upperRightArc = new Arc(centerX, centerY, radius, radius, 0, 90);
			Arc upperLeftArc = new Arc(centerX, centerY, radius, radius, 90, 90);
			Arc lowerLeftArc = new Arc(centerX, centerY, radius, radius, 180, 90);
			Arc lowerRightArc = new Arc(centerX, centerY, radius, radius, 270, 90);

			upperLeftArc.setFill(null);
			upperLeftArc.setStroke(color);
			lowerRightArc.setFill(null);
			lowerRightArc.setStroke(color);

			upperLeftArc.setType(ArcType.ROUND);
			upperRightArc.setType(ArcType.ROUND);
			lowerLeftArc.setType(ArcType.ROUND);
			lowerRightArc.setType(ArcType.ROUND);

			valveShape = Shape.union(Shape.union(lowerRightArc, lowerLeftArc), Shape.union(upperLeftArc, upperRightArc));

			valveShape.setFill(color);
			valveShape.setStroke(color);
		} catch (Exception ex) {
			utilities.Error.HandleException(ex);
		}

		return valveShape;
	}
}