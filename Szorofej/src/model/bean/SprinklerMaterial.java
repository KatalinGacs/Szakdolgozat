package model.bean;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class SprinklerMaterial extends Material {

	private SimpleStringProperty sprinklerName = new SimpleStringProperty();
	
	private SimpleDoubleProperty quantity = new SimpleDoubleProperty();

	public SprinklerMaterial(String sprinklerName, double quantity, String name) {
		super.setName(name);
		this.quantity.set(quantity);
		this.sprinklerName.set(sprinklerName);
	}
	
}
