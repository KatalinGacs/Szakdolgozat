package model.bean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import controller.SprinklerController;
import controller.SprinklerControllerImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.bean.PipeGraph.Vertex;

@XmlRootElement(name = "Zone")
public class Zone {

	private String name;
	private ObservableList<SprinklerShape> sprinklers = FXCollections.observableArrayList();
	private double sumOfFlowRate = 0;
	private double durationOfWatering;
	private int numberOfHeads;

	private ArrayList<VertexElement> vertices = new ArrayList<>();
	private String color;
	private double beginningPressure = 0;

	public Zone(String name, ObservableList<SprinklerShape> sprinklers, double durationOfWatering) {
		this.name = name;
		this.sprinklers = sprinklers;
		for (SprinklerShape s : sprinklers) {
			sumOfFlowRate += s.getFlowRate();
		}
		this.durationOfWatering = durationOfWatering;
		numberOfHeads = sprinklers.size();
	}

	public Zone() {
	}

	@XmlElement(name = "Name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlTransient
	public ObservableList<SprinklerShape> getSprinklers() {
		return sprinklers;
	}

	public void setSprinklers(ObservableList<SprinklerShape> sprinklers) {
		this.sprinklers = sprinklers;
		for (SprinklerShape s : sprinklers) {
			sumOfFlowRate += s.getFlowRate();
		}
		numberOfHeads = sprinklers.size();
	}

	@XmlTransient
	public double getSumOfFlowRate() {
		return sumOfFlowRate;
	}

	@XmlElement(name = "DurationOfWatering")
	public double getDurationOfWatering() {
		return durationOfWatering;
	}

	public void setDurationOfWatering(double durationOfWatering) {
		this.durationOfWatering = durationOfWatering;
	}

	@XmlTransient
	public int getNumberOfHeads() {
		return numberOfHeads;
	}

	@Override
	public String toString() {
		return name;
	}

	@XmlElementWrapper(name = "Vertices")
	@XmlElement(name = "VertexElement")
	public ArrayList<VertexElement> getVertices() {
		return vertices;
	}

	public void setVertices(ArrayList<VertexElement> vertices) {
		this.vertices = vertices;
	}

	public void setSumOfFlowRate(double sumOfFlowRate) {
		this.sumOfFlowRate = sumOfFlowRate;
	}

	public void setNumberOfHeads(int numberOfHeads) {
		this.numberOfHeads = numberOfHeads;
	}

	public void addSprinkler(SprinklerShape sprinkler) {
		sprinklers.add(sprinkler);
		sumOfFlowRate += sprinkler.getFlowRate();
		numberOfHeads++;
	}

	@XmlElement(name = "Color")
	public String getColor() {
		if (color != null) {
			return color;
		} else {
			SprinklerController controller = new SprinklerControllerImpl();
			return controller.getPipeGraph(this).getColor().toString();
		}
	}

	public void setColor(String color) {
		this.color = color;
	}

	@XmlElement(name = "BeginningPressure")
	public double getBeginningPressure() {
		if (beginningPressure != 0) {
			return beginningPressure;
		} else {
			SprinklerController controller = new SprinklerControllerImpl();
			return controller.getPipeGraph(this).getBeginningPressure();
		}
	}

	public void setBeginningPressure(double beginningPressure) {
		this.beginningPressure = beginningPressure;
	}

	public void updateVertices() {
		SprinklerController controller = new SprinklerControllerImpl();

		ArrayList<VertexElement> result = new ArrayList<>();
		for (Vertex v : controller.getPipeGraph(this).getVertices()) { 
			VertexElement vE = new VertexElement();
			vE.setRoot(v.getParent() == null);
			vE.setBreakpoint(v.isBreakPoint());
			vE.setX(v.getX());
			vE.setY(v.getY());
			vE.setSprinklerShape(v.getSprinklerShape());
			v.setVertexElement(vE);
			result.add(vE);
		}

		for (Vertex v : controller.getPipeGraph(this).getVertices()) {
			VertexElement vE = v.getVertexElement();
			Set<String> childrenIDs = new HashSet<>();
			for (Vertex child : v.getChildren()) {
				childrenIDs.add(child.getVertexElement().getID());
			}
			vE.setChildrenID(childrenIDs);
			if (!vE.isRoot()) {
				vE.setParentID(v.getParent().getVertexElement().getID());
			}
		}

		vertices = result;
	}


}
