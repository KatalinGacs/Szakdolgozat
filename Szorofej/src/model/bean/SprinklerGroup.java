package model.bean;

import javafx.beans.property.SimpleStringProperty;

public class SprinklerGroup {
	private SimpleStringProperty name = new SimpleStringProperty();

	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
	}

	public SprinklerGroup(String name) {
		this.name.set(name);
	}

	public SprinklerGroup() {
	}

	@Override
	public String toString() {
		return name.get();
	}
	
}
