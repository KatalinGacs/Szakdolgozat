package model.bean;

import javafx.beans.property.SimpleDoubleProperty;

public class PipeMaterial extends Material {
	private SimpleDoubleProperty radius = new SimpleDoubleProperty();

	public SimpleDoubleProperty getRadius() {
		return radius;
	}

	public void setRadius(SimpleDoubleProperty radius) {
		this.radius = radius;
	}

	public PipeMaterial(String name, double radius) {
		super.setName(name);
		this.radius.set(radius);
	}
	
}
