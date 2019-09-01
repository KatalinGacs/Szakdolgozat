package model.bean;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class SprinklerType {

	private SimpleStringProperty name = new SimpleStringProperty();
	private SimpleDoubleProperty minRadius = new SimpleDoubleProperty();
	private SimpleDoubleProperty maxRadius = new SimpleDoubleProperty();
	private SimpleDoubleProperty minAngle = new SimpleDoubleProperty();
	private SimpleDoubleProperty maxAngle = new SimpleDoubleProperty();
	private SimpleBooleanProperty fixWaterConsumption = new SimpleBooleanProperty();
	private SimpleDoubleProperty waterCounsumption = new SimpleDoubleProperty();
	private SimpleDoubleProperty minPressure = new SimpleDoubleProperty();
	private SimpleStringProperty sprinklerGroup = new SimpleStringProperty();

	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
	}

	public double getMinRadius() {
		return minRadius.get();
	}

	public void setMinRadius(double minRadius) {
		this.minRadius.set(minRadius);
	}

	public double getMaxRadius() {
		return maxRadius.get();
	}

	public void setMaxRadius(double maxRadius) {
		this.maxRadius.get();
	}

	public double getMinAngle() {
		return minAngle.get();
	}

	public void setMinAngle(double minAngle) {
		this.minAngle.set(minAngle);
	}

	public double getMaxAngle() {
		return maxAngle.get();
	}

	public void setMaxAngle(double maxAngle) {
		this.maxAngle.set(maxAngle);
	}

	public boolean getFixWaterConsumption() {
		return fixWaterConsumption.get();
	}

	public void setFixWaterConsumption(boolean fixWaterConsumption) {
		this.fixWaterConsumption.set(fixWaterConsumption);
	}

	public double getWaterCounsumption() {
		return waterCounsumption.get();
	}

	public void setWaterCounsumption(double waterCounsumption) {
		this.waterCounsumption.set(waterCounsumption);
	}

	public double getMinPressure() {
		return minPressure.get();
	}

	public void setMinPressure(double minPressure) {
		this.minPressure.set(minPressure);
	}

	public String getSprinklerGroup() {
		return sprinklerGroup.get();
	}

	public void setSprinklerGroup(String sprinklerGroup) {
		this.sprinklerGroup.set(sprinklerGroup);
	}

	public SprinklerType() {
		super();
	}

	public SprinklerType(String name, double minRadius, double maxRadius, double minAngle, double maxAngle,
			boolean fixWaterConsumption, double waterCounsumption, double minPressure, String sprinklerGroup) {
		this.name.set(name);
		this.minRadius.set(minRadius);
		this.maxRadius.set(maxRadius);
		this.minAngle.set(minAngle);
		this.maxAngle.set(maxAngle);
		this.fixWaterConsumption.set(fixWaterConsumption);
		this.waterCounsumption.set(waterCounsumption);
		this.minPressure.set(minPressure);
		this.sprinklerGroup.set(sprinklerGroup);
	}

}
