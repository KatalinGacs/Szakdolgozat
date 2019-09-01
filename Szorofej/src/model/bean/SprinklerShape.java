package model.bean;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;

public class SprinklerShape {


	private Circle circle = new Circle();
	private Arc arc = new Arc();

	private SprinklerType sprinkler = new SprinklerType();

	public SprinklerShape() {
	}

	public SprinklerShape(Circle circle, Arc arc, SprinklerType sprinkler) {
		super();
		this.circle = circle;
		this.arc = arc;
		this.sprinkler = sprinkler;
	}

	public Circle getCircle() {
		return circle;
	}

	public void setCircle(Circle circle) {
		this.circle = circle; 
	}

	public Arc getArc() {
		return arc;
	}

	public void setArc(Arc arc) {
		this.arc = arc;
	}

	public SprinklerType getSprinkler() {
		return sprinkler;
	}

	public void setSprinkler(SprinklerType sprinkler) {
		this.sprinkler = sprinkler;
	}
	
}
