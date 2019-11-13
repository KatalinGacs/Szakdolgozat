package application;

import application.common.Common;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Shape;

public class ValveIcon {

	public static Shape valveIcon(double centerX, double centerY, Color color) {
		Shape valveShape;
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
		
		return valveShape;
	}

}
