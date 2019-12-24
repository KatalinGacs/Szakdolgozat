package model.bean;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.sun.prism.paint.Color;

import javafx.scene.control.Label;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

/**
 * Class that represents the shape of a sprinkler that can be drawn on the
 * canvas
 */
@XmlRootElement(name = "SprinklerShape")
public class SprinklerShape {

	private Circle circle = new Circle();
	private Arc arc = new Arc();
	private double flowRate;
	private double radius;
	private double waterCoverageInMmPerHour;
	private SprinklerType sprinkler = new SprinklerType();
	private Text label = new Text();

	// needed for XML unmarshalling
	private String fillColor;
	private String strokeColor;
	private double strokeWidth;
	private double centerX;
	private double centerY;
	private double circleRadius;
	private String sprinklerType;
	private double length;
	private double startAngle;
	private String labelText;
	private String labelStyle;
	private double labelX;
	private double labelY;

	public SprinklerShape() {
	}

	public SprinklerShape(Circle circle, Arc arc, SprinklerType sprinkler, double radius) {

		this.circle = circle;
		this.arc = arc;
		this.sprinkler = sprinkler;
		this.radius = radius;
		flowRate = calculateFlowRate();
		waterCoverageInMmPerHour = calculateWaterCoverage();
	}

	@XmlTransient
	public Circle getCircle() {
		return circle;
	}

	public void setCircle(Circle circle) {
		this.circle = circle;
	}

	@XmlTransient
	public Arc getArc() {
		return arc;
	}

	public void setArc(Arc arc) {
		this.arc = arc;
		flowRate = calculateFlowRate();
		waterCoverageInMmPerHour = calculateWaterCoverage();
	}

	@XmlTransient
	public SprinklerType getSprinkler() {
		return sprinkler;
	}

	public void setSprinkler(SprinklerType sprinkler) {
		this.sprinkler = sprinkler;
	}

	@XmlElement(name = "FlowRate")
	public double getFlowRate() {
		return flowRate;
	}

	@XmlElement(name = "Radius")
	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	@XmlElement(name = "WaterCoverageInMmPerHour")
	public double getWaterCoverageInMmPerHour() {
		return waterCoverageInMmPerHour;
	}

	public void setWaterCoverageInMmPerHour(double waterCoverageInMmPerHour) {
		this.waterCoverageInMmPerHour = waterCoverageInMmPerHour;
	}

	@XmlTransient
	public String getGroup() {
		return sprinkler.getSprinklerGroup().getName();
	}

	@XmlTransient
	public Text getLabel() {
		return label;
	}

	public void setLabel(Text label) {
		this.label = label;
	}

	private double calculateWaterCoverage() {
		double areaInM2 = 0;
		if (sprinkler.getFixWaterConsumption()) {
			areaInM2 = (arc.getLength() / 360) * radius * radius * Math.PI;
		} else {
			areaInM2 = radius * radius * Math.PI;
		}
		// return (flowRate * 0.06 * 1000) / areaInM2;
		return (flowRate * 60) / areaInM2;
	}

	private double calculateFlowRate() {
		if (!sprinkler.getFixWaterConsumption())
			return sprinkler.getWaterConsumption() * (arc.getLength() / 360);
		else
			return sprinkler.getWaterConsumption();
	}

	@XmlElement(name = "StrokeColor")
	public String getStrokeColor() {
		if (circle.getStroke() != null)
			return circle.getStroke().toString();
		else
			return strokeColor;
	}

	@XmlElement(name = "FillColor")
	public String getFillColor() {
		if (arc.getFill() != null)
			return arc.getFill().toString();
		else 
			return fillColor;
	}

	@XmlElement(name = "StrokeWidth")
	public double getStrokeWidth() {
		if (strokeWidth == 0) {
			return arc.getStrokeWidth();
		} else
			return strokeWidth;
	}

	@XmlElement(name = "CenterX")
	public double getCenterX() {
		if (centerX == 0) {
			return circle.getCenterX();
		}
		else return centerX;
	}

	@XmlElement(name = "CenterY")
	public double getCenterY() {
		if (centerY == 0) {
			return circle.getCenterY();
		} else return centerY;
	}

	@XmlElement(name = "CircleRadius")
	public double getCircleRadius() {
		if (circleRadius == 0) {
			return circle.getRadius();
		} else return circleRadius;
	}

	@XmlElement(name = "Length")
	public double getLength() {
		if (length == 0) {
			return arc.getLength();
		} else return length;
	}

	@XmlElement(name = "StartAngle")
	public double getStartAngle() {
		if (startAngle == 0) {
			return arc.getStartAngle();
		} else return startAngle;
	}

	@XmlElement(name = "SprinklerType")
	public String getSprinklerType() {
		if (sprinkler != null) {
			return sprinkler.toString();
		} else return sprinklerType;
	}

	@XmlElement(name = "LabelText")
	public String getLabelText() {
		if (labelText == "") {
			return label.getText();
		} else return labelText;
	}

	@XmlElement(name = "LabelStyle")
	public String getLabelStyle() {
		if (labelStyle == "") {
			return label.getStyle();
		} else return labelStyle;
	}

	@XmlElement(name = "LabelX")
	public double getLabelX() {
		if (labelX == 0) {
			return label.getX();
		} else return labelX;
	}

	@XmlElement(name = "LabelY")
	public double getLabelY() {
		if (labelY == 0) {
			return label.getY();
		} else return labelY;
	}

	public void setFlowRate(double flowRate) {
		this.flowRate = calculateFlowRate();
	}

	public void setFillColor(String fillColor) {
		this.fillColor = fillColor;
	}

	public void setStrokeColor(String strokeColor) {
		this.strokeColor = strokeColor;
	}

	public void setStrokeWidth(double strokeWidth) {
		this.strokeWidth = strokeWidth;
	}

	public void setCenterX(double centerX) {
		this.centerX = centerX;
	}

	public void setCenterY(double centerY) {
		this.centerY = centerY;
	}

	public void setCircleRadius(double circleRadius) {
		this.circleRadius = circleRadius;
	}

	public void setSprinklerType(String sprinklerType) {
		this.sprinklerType = sprinklerType;
	}

	public void setLength(double length) {
		this.length = length;
	}

	public void setStartAngle(double startAngle) {
		this.startAngle = startAngle;
	}

	public void setLabelText(String labelText) {
		this.labelText = labelText;
	}

	public void setLabelStyle(String labelStyle) {
		this.labelStyle = labelStyle;
	}

	public void setLabelX(double labelX) {
		this.labelX = labelX;
	}

	public void setLabelY(double labelY) {
		this.labelY = labelY;
	}

	@Override
	public String toString() {
		return "SprinklerShape [circle=" + circle + ", arc=" + arc + ", flowRate=" + flowRate + ", radius=" + radius
				+ ", waterCoverageInMmPerHour=" + waterCoverageInMmPerHour + ", sprinkler=" + sprinkler + ", label="
				+ label + ", fillColor=" + fillColor + ", strokeColor=" + strokeColor + ", strokeWidth=" + strokeWidth
				+ ", centerX=" + centerX + ", centerY=" + centerY + ", circleRadius=" + circleRadius
				+ ", sprinklerType=" + sprinklerType + ", length=" + length + ", startAngle=" + startAngle
				+ ", labelText=" + labelText + ", labelStyle=" + labelStyle + ", labelX=" + labelX + ", labelY="
				+ labelY + "]";
	}

	

}
