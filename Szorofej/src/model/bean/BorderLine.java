package model.bean;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import javafx.scene.shape.Line;

@XmlRootElement(name = "BorderLine")
public class BorderLine {
	
	private double startX;
	private double startY;
	private double endX;
	private double endY;
	private String color;
	private double width;
	
	private Line line;
	
	@XmlElement(name = "StartX")
	public double getStartX() {
		if (line == null) {
			return startX;
		}
		else return line.getStartX();
	}

	public void setStartX(double startX) {
		this.startX = startX;
	}

	@XmlElement(name = "StartY")
	public double getStartY() {
		if (line == null) {
			return startY;
		}
		else return line.getStartY();
	}

	public void setStartY(double startY) {
		this.startY = startY;
	}

	@XmlElement(name = "EndX")
	public double getEndX() {
		if (line == null) {
			return endX;
		}
		else return line.getEndX();
	}

	public void setEndX(double endX) {
		this.endX = endX;
	}

	@XmlElement(name = "EndY")
	public double getEndY() {
		if (line == null) {
			return endY;
		}
		else return line.getEndY();
	}

	public void setEndY(double endY) {
		this.endY = endY;
	}

	@XmlElement(name = "Color")
	public String getColor() {
		if (line == null) {
			return color;
		}
		else return line.getStroke().toString();
	}

	public void setColor(String color) {
		this.color = color;
	}

	@XmlElement(name = "Width")
	public double getWidth() {
		if (line == null) {
			return width;
		}
		else return line.getStrokeWidth();
	}

	public void setWidth(double width) {
		this.width = width;
	}

	@XmlTransient
	public Line getLine() {
		return line;
	}

	public void setLine(Line line) {
		this.line = line;
	}
	public BorderLine() {
	}
	
	public BorderLine(Line line) {
		this.line = line;
		startX = line.getStartX();
		startY = line.getStartY();
		endX = line.getEndX();
		endY = line.getEndY();
		color = line.getStroke().toString();
		width = line.getStrokeWidth();
	}
	
	
}
