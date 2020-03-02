package model.bean;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Class for the materials needed to install different parts of the irrigation
 * system
 */
public class Material {
	private SimpleStringProperty name = new SimpleStringProperty();

	private SimpleStringProperty unit = new SimpleStringProperty();
	
	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
	}

	public String getUnit() {
		return unit.get();
	}

	public void setUnit(String unit) {
		this.unit.set(unit);
	}

	public Material() {
	}

	@Override
	public String toString() {
		return name.get() + " (" + unit.get() + ")";
	}	
}
