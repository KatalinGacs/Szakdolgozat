package model.bean;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import javafx.scene.shape.Circle;

@XmlRootElement(name = "CircleObstacle")
public class CircleObstacle {
	
	private double centerX;
	private double centerY;
	private double radius;
	private String strokeColor;
	private String fillColor;
	private double strokeWidth;
	private Circle circle;
	
	public CircleObstacle() {
	}

	public CircleObstacle(Circle circle) {
		this.circle = circle;
		centerX = circle.getCenterX();
		centerY = circle.getCenterY();
		radius = circle.getRadius();
		strokeColor = circle.getStroke().toString();
		fillColor = circle.getFill().toString();
		strokeWidth = circle.getStrokeWidth();
	}

	@XmlElement(name = "CenterX")
	public double getCenterX() {
		return centerX;
	}

	public void setCenterX(double centerX) {
		this.centerX = centerX;
	}
	
	@XmlElement(name = "CenterY")
	public double getCenterY() {
		return centerY;
	}

	public void setCenterY(double centerY) {
		this.centerY = centerY;
	}

	@XmlElement(name = "Radius")
	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	@XmlElement(name = "StrokeColor")
	public String getStrokeColor() {
		return strokeColor;
	}

	public void setStrokeColor(String strokeColor) {
		this.strokeColor = strokeColor;
	}
	
	@XmlElement(name = "FillColor")
	public String getFillColor() {
		return fillColor;
	}

	public void setFillColor(String fillColor) {
		this.fillColor = fillColor;
	}

	@XmlElement(name = "StrokeWidth")
	public double getStrokeWidth() {
		return strokeWidth;
	}

	public void setStrokeWidth(double strokeWidth) {
		this.strokeWidth = strokeWidth;
	}

	@XmlTransient
	public Circle getCircle() {
		return circle;
	}

	public void setCircle(Circle circle) {
		this.circle = circle;
	}
	
	
}
