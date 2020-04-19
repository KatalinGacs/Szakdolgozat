package controller;

import javafx.collections.ObservableList;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import model.DbException;
import model.SprinklerDAO;
import model.SprinklerDAOImpl;
import model.bean.Material;
import model.bean.MaterialSprinklerConnection;
import model.bean.PipeGraph;
import model.bean.SprinklerGroup;
import model.bean.SprinklerShape;
import model.bean.SprinklerType;
import model.bean.UsedMaterial;
import model.bean.Zone;

public class SprinklerControllerImpl implements SprinklerController {

	private SprinklerDAO dao = new SprinklerDAOImpl();
	
	@Override
	public void addSprinklerShape(SprinklerShape s){
		dao.addSprinklerShapes(s);
	}
 
	@Override
	public ObservableList<SprinklerShape> listSprinklerShapes() {
		return dao.listSprinklerShapes();
	}

	@Override
	public void deleteSprinklerShape(SprinklerShape s) {
		dao.deleteSprinklerShape(s);
	}

	@Override
	public void addSprinklerType(SprinklerType s)  throws DbException{
		dao.addSprinklerType(s);
		
	}

	@Override
	public ObservableList<SprinklerType> listSprinklerTypes()  throws DbException{
		return dao.listSprinklerTypes();
	}

	@Override
	public void deleteSprinklerType(SprinklerType s)  throws DbException{
		dao.deleteSprinklerType(s);
	}

	@Override
	public ObservableList<SprinklerGroup> listSprinklerGroups()  throws DbException{
		return dao.listSprinklerGroups();
	}

	@Override
	public void addSprinklerGroup(SprinklerGroup s) throws DbException {
		dao.addSprinklerGroup(s);
		
	}

	@Override
	public void deleteSprinklerGroup(SprinklerGroup s)  throws DbException{
		dao.deleteSprinklerGroup(s);
		
	}

	@Override
	public ObservableList<SprinklerType> listSprinklerTypeByGroup(SprinklerGroup s) throws DbException {
		return dao.listSprinklerTypeByGroup(s);
	}

	@Override
	public ObservableList<SprinklerShape> listSprinklerShapes(Zone zone) {
		return dao.listSprinklerShapes(zone);
	}

	@Override
	public ObservableList<Zone> listZones() {
		return dao.listZones();
	}

	@Override
	public void addZone(Zone z) {
		dao.addZone(z);
	}

	@Override
	public void removeZone(Zone z) {
		dao.removeZone(z);
	}

	@Override
	public ObservableList<PipeGraph> listPipeGraphs() {
		return dao.listPipeGraphs();
	}

	@Override
	public void addPipeGraph(PipeGraph p) {
		dao.addPipeGraph(p);
	}

	@Override
	public void removePipeGraph(PipeGraph p) {
		dao.removePipeGraph(p);
	}

	@Override
	public void addBorderShape(Shape border) {
		dao.addBorderShape(border);
	}

	@Override
	public ObservableList<Shape> listBorderShapes() {
		return dao.listBorderShapes();
	}

	@Override
	public void removeBorderShape(Shape border) {
		dao.removeBorderShape(border);
	}

	@Override
	public void addObstacle(Shape obstacle) {
		dao.addObstacle(obstacle);
	}

	@Override
	public ObservableList<Shape> listObstacles() {
		return dao.listObstacles();
	}

	@Override
	public void removeObstacle(Shape obstacle) {
		dao.removeObstacle(obstacle);
	}

	@Override
	public PipeGraph getPipeGraph(Zone zone) {
		return dao.getPipeGraph(zone);
	}

	@Override
	public void updateSprinklerData(String column, double newValue, String name) throws DbException {
		dao.updateSprinklerData(column, newValue, name);
	}

	@Override
	public ObservableList<Text> listTexts() {
		return dao.listTexts();
	}

	@Override
	public void addText(Text t) {
		dao.addText(t);
	}

	@Override
	public void removeText(Text t) {
		dao.removeText(t);
	}

	@Override
	public SprinklerType getSprinklerType(String sprinklerType)  throws DbException{
		return dao.getSprinklerType(sprinklerType);
	}

	@Override
	public void clearBorderShapes() {
		dao.clearBorderShapes();
	}

	@Override
	public void clearObstacles() {
		dao.clearObstacles();
	}

	@Override
	public void clearSprinklerShapes() {
		dao.clearSprinklerShapes();
	}

	@Override
	public void clearZones() {
		dao.clearZones();
	}

	@Override
	public void clearPipeGraphs() {
		dao.clearPipeGraphs();
	}

	@Override
	public void clearTexts() {
		dao.clearTexts();
	}

	@Override
	public void clearAll() {
		dao.clearAll();
	}

	@Override
	public void addMaterial(Material material)  throws DbException{
		dao.addMaterial(material);
		
	}

	@Override
	public void deleteMaterial(Material material) throws DbException {
		dao.deleteMaterial(material);
	}

	@Override
	public ObservableList<Material> listMaterials() throws DbException {
		return dao.listMaterials();
	}

	@Override
	public ObservableList<MaterialSprinklerConnection> listMaterials(SprinklerType selectedItem)  throws DbException{
		return dao.listMaterials(selectedItem);
	}

	@Override
	public void addMaterialConnection(SprinklerType s, Material m, int quantity) throws DbException {
		dao.addMaterialConnection(s, m, quantity);
	}

	@Override
	public void deleteMaterialConnection(SprinklerType s, Material m)  throws DbException{
		dao.deleteMaterialConnection(s, m);
	}

	@Override
	public ObservableList<Material> listNotAddedMaterials(SprinklerType selectedItem)  throws DbException{
		return dao.listNotAddedMaterials(selectedItem);
	}

	@Override
	public void addPipeMaterial(String pipename, Double length)  throws DbException{
		dao.addPipeMaterial(pipename, length);
	}

	@Override
	public ObservableList<UsedMaterial> summarizeMaterials()  throws DbException{
		return dao.summarizeMaterials();
	}

	@Override
	public ObservableList<SprinklerShape> listSprinklerShapesNotInZones() {
		return dao.listSprinklerShapesNotInZones();
	}

	@Override
	public ObservableList<SprinklerShape> listSprinklerShapesNotConnectedToPipes() {
		return dao.listSprinklerShapesNotConnectedToPipes();
	}

}
