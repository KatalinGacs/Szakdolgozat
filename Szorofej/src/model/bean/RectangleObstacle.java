package model.bean;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import javafx.scene.shape.Rectangle;

@XmlRootElement(name = "RectangleObstacle")
public class RectangleObstacle {
	
	private double X;
	private double Y;
	private double width;
	private double height;
	private String strokeColor;
	private String fillColor;
	private double strokeWidth;
	private Rectangle rectangle;
	
	public RectangleObstacle() {
	}

	public RectangleObstacle(Rectangle rectangle) {
		this.rectangle = rectangle;
		X = rectangle.getX();
		Y = rectangle.getY();
		width = rectangle.getWidth();
		height = rectangle.getHeight();
		strokeColor = rectangle.getStroke().toString();
		fillColor = rectangle.getFill().toString();
		strokeWidth = rectangle.getStrokeWidth();
	}

	@XmlElement(name = "X")
	public double getX() {
		return X;
	}

	public void setX(double x) {
		X = x;
	}
	
	@XmlElement(name = "Y")
	public double getY() {
		return Y;
	}

	public void setY(double y) {
		Y = y;
	}

	@XmlElement(name = "Width")
	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	@XmlElement(name = "Height")
	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
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
	public Rectangle getRectangle() {
		return rectangle;
	}

	public void setRectangle(Rectangle rectangle) {
		this.rectangle = rectangle;
	}
	
	
}
