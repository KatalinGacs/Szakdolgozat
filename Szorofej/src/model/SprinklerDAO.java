package model;

import javafx.collections.ObservableList;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import model.bean.Material;
import model.bean.MaterialSprinklerConnection;
import model.bean.PipeGraph;
import model.bean.SprinklerGroup;
import model.bean.SprinklerShape;
import model.bean.SprinklerType;
import model.bean.UsedMaterial;
import model.bean.Zone;

public interface SprinklerDAO {
	
	// database
	public void addSprinklerType(SprinklerType s) throws DbException;	
	public ObservableList<SprinklerType> listSprinklerTypes() throws DbException;	
	public void deleteSprinklerType (SprinklerType s) throws DbException;
	public void updateSprinklerData(String column, double newValue, String name) throws DbException;
	public ObservableList<SprinklerType> listSprinklerTypeByGroup(SprinklerGroup s) throws DbException;

	public ObservableList<SprinklerGroup> listSprinklerGroups() throws DbException;	
	public void addSprinklerGroup (SprinklerGroup s) throws DbException;	
	public void deleteSprinklerGroup (SprinklerGroup s) throws DbException;
	public SprinklerType getSprinklerType(String sprinklerType) throws DbException;
	
	public void addMaterial(Material material) throws DbException;
	public void deleteMaterial(Material material) throws DbException;
	public ObservableList<Material> listMaterials() throws DbException;
	public ObservableList<Material> listNotAddedMaterials(SprinklerType selectedItem) throws DbException;
	public Material getMaterial(String name) throws DbException;
	
	public ObservableList<MaterialSprinklerConnection> listMaterials(SprinklerType selectedItem) throws DbException;
	public void addMaterialConnection(SprinklerType s, Material m, int quantity) throws DbException;
	public void deleteMaterialConnection(SprinklerType s, Material m) throws DbException;
	
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
	public ObservableList<SprinklerShape> listSprinklerShapesNotInZones();
	public ObservableList<SprinklerShape> listSprinklerShapesNotConnectedToPipes();
 	
	public ObservableList<Zone> listZones();
	public void addZone(Zone z);
	public void removeZone(Zone z);
	public void clearZones();
	
	public ObservableList<PipeGraph> listPipeGraphs();
	public void addPipeGraph(PipeGraph p);
	public void removePipeGraph(PipeGraph p);
	public PipeGraph getPipeGraph(Zone zone);
	public void clearPipeGraphs();
	
	public ObservableList<UsedMaterial> summarizeMaterials() throws DbException;
	public void addPipeMaterial(String pipename, Double length) throws DbException;
	
	public ObservableList<Text> listTexts();
	public void addText(Text t);
	public void removeText(Text t);
	public void clearTexts();	
	void clearMaterials();

	public void clearAll();
}
