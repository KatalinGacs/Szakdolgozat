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
	private SimpleDoubleProperty waterConsumption = new SimpleDoubleProperty();
	private SimpleDoubleProperty minPressure = new SimpleDoubleProperty();
	private SprinklerGroup sprinklerGroup = new SprinklerGroup();

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

	public double getWaterConsumption() {
		return waterConsumption.get();
	}

	public void setWaterCounsumption(double waterCounsumption) {
		this.waterConsumption.set(waterCounsumption);
	}

	public double getMinPressure() {
		return minPressure.get();
	}

	public void setMinPressure(double minPressure) {
		this.minPressure.set(minPressure);
	}

	public SprinklerGroup getSprinklerGroup() {
		return sprinklerGroup;
	}

	public void setSprinklerGroup(SprinklerGroup sprinklerGroup) {
		this.sprinklerGroup = sprinklerGroup;
	}

	public SprinklerType() {
		super();
	}

	public SprinklerType(String name, double minRadius, double maxRadius, double minAngle, double maxAngle,
			boolean fixWaterConsumption, double waterConsumption, double minPressure, SprinklerGroup sprinklerGroup) {
		this.name.set(name);
		this.minRadius.set(minRadius);
		this.maxRadius.set(maxRadius);
		this.minAngle.set(minAngle);
		this.maxAngle.set(maxAngle);
		this.fixWaterConsumption.set(fixWaterConsumption);
		this.waterConsumption.set(waterConsumption);
		this.minPressure.set(minPressure);
		this.sprinklerGroup = sprinklerGroup;
	}
	
	@Override
	public String toString() {
		return name.get();
	}

}
