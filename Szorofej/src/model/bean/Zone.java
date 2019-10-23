package model.bean;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Zone {
	
	//TODO tov�bbi adattagok: hozz� tartoz� cs�vek (?)
	
	private String name;
	private ObservableList<SprinklerShape> sprinklers = FXCollections.observableArrayList();
	private double sumOfFlowRate = 0;
	private double durationOfWatering;
	private int numberOfHeads;
	
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
		numberOfHeads = sprinklers.size();
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

	public int getNumberOfHeads() {
		return numberOfHeads;
	}
	
	
	
}
