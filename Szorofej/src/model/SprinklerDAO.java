package model;

import javafx.collections.ObservableList;
import model.bean.SprinklerType;
import model.bean.SprinklerShape;

public interface SprinklerDAO {

	public void addSprinklerShapes(SprinklerShape s);
	
	public ObservableList<SprinklerShape> listSprinklerShapes();
	
	public void deleteSprinklerShape(SprinklerShape s);
	
	public void addSprinklerType(SprinklerType s);
	
	public ObservableList<SprinklerType> listSprinklerTypes();
	
	public void deleteSprinklerType (SprinklerType s);
}
