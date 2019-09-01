package model.bean;

import javafx.beans.property.SimpleStringProperty;

public abstract class Material {
	private SimpleStringProperty name = new SimpleStringProperty();

	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
	}
}
