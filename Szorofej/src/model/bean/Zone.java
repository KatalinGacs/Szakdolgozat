package model.bean;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@XmlRootElement(name = "Zone")
public class Zone {

	private String name;
	private ObservableList<SprinklerShape> sprinklers = FXCollections.observableArrayList();
	private double sumOfFlowRate = 0;
	private double durationOfWatering;
	private int numberOfHeads;

	private ArrayList<String> sprinklerIDs = new ArrayList<String>();

	public Zone(String name, ObservableList<SprinklerShape> sprinklers, double durationOfWatering) {
		// TODO kéne ellenõrizni hogy létezik-e már ilyen nevû zóna, de lehet hogy nem
		// itt hanem a név megadásánál
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

	@XmlElement(name = "SprinklerIDs")
	public ArrayList<String> getSprinklerIDs() {
		if(sprinklers.isEmpty())
		return sprinklerIDs;
		else {
			ArrayList<String> result = new ArrayList<String>();
			for (SprinklerShape s : sprinklers) {
				result.add(s.getID());
			}
			return result;
		}
	}

	public void setSprinklerIDs( ArrayList<String>  sprinklerIDs) {
		this.sprinklerIDs = sprinklerIDs;
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


}
