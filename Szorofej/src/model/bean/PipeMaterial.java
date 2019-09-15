package model.bean;

import javafx.beans.property.SimpleDoubleProperty;

/**
 * Class that represents the materials needed to install a pipe
 */
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
