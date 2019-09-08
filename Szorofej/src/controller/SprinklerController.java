package controller;

import javafx.collections.ObservableList;
import model.DbException;
import model.bean.SprinklerGroup;
import model.bean.SprinklerShape;
import model.bean.SprinklerType;

public interface SprinklerController {

	public void addSprinklerShape(SprinklerShape s);

	public ObservableList<SprinklerShape> listSprinklerShapes();

	public void deleteSprinklerShape(SprinklerShape s);

	public void addSprinklerType(SprinklerType s);

	public ObservableList<SprinklerType> listSprinklerTypes();

	public void deleteSprinklerType(SprinklerType s);

	public ObservableList<SprinklerGroup> listSprinklerGroups();

	public void addSprinklerGroup(SprinklerGroup s) throws DbException;

	public void deleteSprinklerGroup(SprinklerGroup s);
	
	public ObservableList<SprinklerType> listSprinklerTypeByGroup(SprinklerGroup s);

}
