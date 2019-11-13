package model;

import javafx.collections.ObservableList;
import javafx.scene.shape.Shape;
import model.bean.PipeGraph;
import model.bean.SprinklerGroup;
import model.bean.SprinklerShape;
import model.bean.SprinklerType;
import model.bean.Zone;

public interface SprinklerDAO {
	
	// database
	public void addSprinklerType(SprinklerType s);	
	public ObservableList<SprinklerType> listSprinklerTypes();	
	public void deleteSprinklerType (SprinklerType s);
	public ObservableList<SprinklerGroup> listSprinklerGroups();	
	public void addSprinklerGroup (SprinklerGroup s) throws DbException;	
	public void deleteSprinklerGroup (SprinklerGroup s);
	public ObservableList<SprinklerType> listSprinklerTypeByGroup(SprinklerGroup s);
	
	//memory
	public void addBorderShape(Shape border);
	public ObservableList<Shape> listBorderShapes();
	public void removeBorderShape(Shape border);
	public void addObstacle(Shape obstacle);
	public ObservableList<Shape> listObstacles();
	public void removeObstacle(Shape obstacle);
	public void addSprinklerShapes(SprinklerShape s);
	public ObservableList<SprinklerShape> listSprinklerShapes();
	public ObservableList<SprinklerShape> listSprinklerShapes(Zone zone);
	public void deleteSprinklerShape(SprinklerShape s);
	public ObservableList<Zone> listZones();
	public void addZone(Zone z);
	public void removeZone(Zone z);
	public ObservableList<PipeGraph> listPipeGraphs();
	public void addPipeGraph(PipeGraph p);
	public void removePipeGraph(PipeGraph p);
	public PipeGraph getPipeGraph(Zone zone);
}
