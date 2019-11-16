package model.bean;

import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;

/**
 * Class that represents the shape of a sprinkler that can be drawn on the canvas
 */
public class SprinklerShape {


	private Circle circle = new Circle();
	private Arc arc = new Arc();
	private double flowRate;
	private double radius;
	private double waterCoverageInMmPerHour;
	private SprinklerType sprinkler = new SprinklerType();

	public SprinklerShape() {
	}

	public SprinklerShape(Circle circle, Arc arc, SprinklerType sprinkler, double radius) {
		super();
		this.circle = circle;
		this.arc = arc;
		this.sprinkler = sprinkler;
		this.radius = radius;
		flowRate = calculateFlowRate();
		waterCoverageInMmPerHour = calculateWaterCoverage();
	}

	public Circle getCircle() {
		return circle;
	}

	public void setCircle(Circle circle) {
		this.circle = circle; 
	}

	public Arc getArc() {
		return arc;
	}

	public void setArc(Arc arc) {
		this.arc = arc;
		flowRate = calculateFlowRate();
		waterCoverageInMmPerHour = calculateWaterCoverage();
	}

	public SprinklerType getSprinkler() {
		return sprinkler;
	}

	public void setSprinkler(SprinklerType sprinkler) {
		this.sprinkler = sprinkler;
	}

	public double getFlowRate() {
		return flowRate;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public double getWaterCoverageInMmPerHour() {
		return waterCoverageInMmPerHour;
	}
	
	public String getGroup() {
		return sprinkler.getSprinklerGroup().getName();
	}
	
	private double calculateWaterCoverage() {
		double areaInM2= 0;
		if (sprinkler.getFixWaterConsumption()) {
			areaInM2 = (arc.getLength() / 360) * radius * radius * Math.PI;
		}
		else {
			areaInM2 = radius * radius * Math.PI;
		}
		//return (flowRate  * 0.06 * 1000) / areaInM2;
		return (flowRate*60)/ areaInM2;
	}
	
	private double calculateFlowRate() {
		if (!sprinkler.getFixWaterConsumption())
			return sprinkler.getWaterConsumption() * (arc.getLength()/360);
		else
			return sprinkler.getWaterConsumption();
	}

}

