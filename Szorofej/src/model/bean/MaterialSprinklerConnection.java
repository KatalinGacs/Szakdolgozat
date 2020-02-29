package model.bean;

import javafx.beans.property.SimpleIntegerProperty;

/**
 * Class to describe what amount of which materials needed for certain sprinkler types
 */
public class MaterialSprinklerConnection {
	
	private Material material = new Material();
	private SprinklerType sprinklerType = new SprinklerType();
	private SimpleIntegerProperty quantity = new SimpleIntegerProperty();

	public MaterialSprinklerConnection() {
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public SprinklerType getSprinklerType() {
		return sprinklerType;
	}

	public void setSprinklerType(SprinklerType sprinklerType) {
		this.sprinklerType = sprinklerType;
	}

	public int getQuantity() {
		return quantity.get();
	}

	public void setQuantity(int quantity) {
		this.quantity.set(quantity);
	}

	
}
