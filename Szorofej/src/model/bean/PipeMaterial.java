package model.bean;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Class that represents the materials needed to install a pipe
 */
public class PipeMaterial extends Material {
	private SimpleIntegerProperty radius = new SimpleIntegerProperty();
	private SimpleDoubleProperty length = new SimpleDoubleProperty();

	public int getRadius() {
		return radius.get();
	}

	public void setRadius(int radius) {
		this.radius.set(radius);
	}

	public double getLength() {
		return length.get();
	}

	public void setLength(double length) {
		this.length.set(length);
	}

	public PipeMaterial(String name, int radius, double length) {
		super.setName(name);
		this.radius.set(radius);
		this.length.set(length);
	}

	public PipeMaterial() {
	}
	
}
