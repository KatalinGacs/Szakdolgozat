package application;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;

public class SprinklerShape extends Arc {
	//private Circle center;

	public SprinklerShape(double centerX, double centerY, double radius, double startAngle,
			double length) {
		super(centerX, centerY, radius, radius, startAngle, length);
		setType(ArcType.ROUND);
		//this.center = new Circle(centerX, centerY, 5, Paint.valueOf("blue"));
	}

	public void drawSprinkler(GraphicsContext gc) {
		gc.setStroke(Color.BLUE);
		// gc.arc(this.getCenterX(), this.getCenterY(), this.getRadiusX(),
		// this.getRadiusY(), this.getStartAngle(), this.getLength());
		gc.strokeArc(this.getCenterX(), this.getCenterY(), this.getRadiusX(), this.getRadiusY(), this.getStartAngle(),
				this.getLength(), this.getType());
		gc.strokeOval(this.getCenterX()+(this.getRadiusX())/2 - 5, this.getCenterY()+(this.getRadiusY())/2 - 5,  10,  10);
		
	}
}
