package model.bean;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.paint.Color;

public class Sprinkler {
	
	private SimpleDoubleProperty radius;
	private SimpleDoubleProperty angle;
	private SimpleDoubleProperty centerX;
	private SimpleDoubleProperty centerY;
	private SimpleStringProperty color;
	
	public double getRadius() {
		return radius.get();
	}

	public void setRadius(double radius) {
		this.radius.set(radius);
	}

	public double getAngle() {
		return angle.get();
	}

	public void setAngle(double angle) {
		this.angle.set(angle);
	}

	public double getCenterX() {
		return centerX.get();
	}

	public void setCenterX(double centerX) {
		this.centerX.set(centerX);
	}

	public double getCenterY() {
		return centerY.get();
	}

	public void setCenterY(double centerY) {
		this.centerY.set(centerY);
	}

	public Color getColor() {
		return Color.valueOf(color.get());
	}

	public void setColor(Color color) {
		this.color.set(color.toString());
	}

	public Sprinkler() {
	}

	public Sprinkler(double radius, double angle, double centerX,
			double centerY, Color color) {
		this.radius = new SimpleDoubleProperty(radius);
		this.angle = new SimpleDoubleProperty(angle);
		this.centerX = new SimpleDoubleProperty(centerX);
		this.centerY = new SimpleDoubleProperty(centerY);
		this.color = new SimpleStringProperty(color.toString());
	}
	
	
	
	
}
