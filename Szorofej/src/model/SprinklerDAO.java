package model;

import javafx.collections.ObservableList;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import model.bean.Material;
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
	public void updateSprinklerData(String column, double newValue, String name);
	public SprinklerType getSprinklerType(String sprinklerType);
	public void addMaterial(Material material);
	public void deleteMaterial(Material material);
	public ObservableList<Material> listMaterials();
	
	//memory
	public void addBorderShape(Shape border);
	public ObservableList<Shape> listBorderShapes();
	public void removeBorderShape(Shape border);
	public void clearBorderShapes();
	
	public void addObstacle(Shape obstacle);
	public ObservableList<Shape> listObstacles();
	public void removeObstacle(Shape obstacle);
	public void clearObstacles();
	
	public void addSprinklerShapes(SprinklerShape s);
	public ObservableList<SprinklerShape> listSprinklerShapes();
	public ObservableList<SprinklerShape> listSprinklerShapes(Zone zone);
	public void deleteSprinklerShape(SprinklerShape s);
	public void clearSprinklerShapes();
	
	public ObservableList<Zone> listZones();
	public void addZone(Zone z);
	public void removeZone(Zone z);
	public void clearZones();
	
	public ObservableList<PipeGraph> listPipeGraphs();
	public void addPipeGraph(PipeGraph p);
	public void removePipeGraph(PipeGraph p);
	public PipeGraph getPipeGraph(Zone zone);
	public void clearPipeGraphs();
	
	public ObservableList<Text> listTexts();
	public void addText(Text t);
	public void removeText(Text t);
	public void clearTexts();	
	
	public void clearAll();
}
