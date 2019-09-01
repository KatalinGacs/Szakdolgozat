package controller;

import javafx.collections.ObservableList;
import model.bean.SprinklerShape;
import model.bean.SprinklerType;

public interface SprinklerController {

	public void addSprinklerShape(SprinklerShape s);
	
	public ObservableList<SprinklerShape> listSprinklerShapes();
	
	public void deleteSprinklerShape(SprinklerShape s);
	
	public void addSprinklerType(SprinklerType s);
	
	public ObservableList<SprinklerType> listSprinklerTypes();
	
	public void deleteSprinklerType (SprinklerType s);
}
