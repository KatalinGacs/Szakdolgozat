package model.bean;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Zone {
	
	//TODO további adattagok: hozzá tartozó csövek (?)
	
	private String name;
	private ObservableList<SprinklerShape> sprinklers = FXCollections.observableArrayList();
	private double sumOfFlowRate = 0;
	private double durationOfWatering;
	
	public Zone(String name, ObservableList<SprinklerShape> sprinklers, double durationOfWatering) {
		this.name = name;
		this.sprinklers = sprinklers;
		for (SprinklerShape s : sprinklers) {
			sumOfFlowRate += s.getFlowRate();
		}
		this.durationOfWatering = durationOfWatering;
	}

	public Zone() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ObservableList<SprinklerShape> getSprinklers() {
		return sprinklers;
	}

	public void setSprinklers(ObservableList<SprinklerShape> sprinklers) {
		this.sprinklers = sprinklers;
		for (SprinklerShape s : sprinklers) {
			sumOfFlowRate += s.getFlowRate();
		}
	}

	public double getSumOfFlowRate() {
		return sumOfFlowRate;
	}

	public double getDurationOfWatering() {
		return durationOfWatering;
	}

	public void setDurationOfWatering(double durationOfWatering) {
		this.durationOfWatering = durationOfWatering;
	}
	
	
	
}
