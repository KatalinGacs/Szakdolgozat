package controller;

import javafx.collections.ObservableList;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import model.DbException;
import model.SprinklerDAO;
import model.SprinklerDAOImpl;
import model.bean.PipeGraph;
import model.bean.SprinklerGroup;
import model.bean.SprinklerShape;
import model.bean.SprinklerType;
import model.bean.Zone;

public class SprinklerControllerImpl implements SprinklerController {

	private SprinklerDAO dao = new SprinklerDAOImpl();
	
	@Override
	public void addSprinklerShape(SprinklerShape s) {
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
	public void addSprinklerType(SprinklerType s) {
		dao.addSprinklerType(s);
		
	}

	@Override
	public ObservableList<SprinklerType> listSprinklerTypes() {
		return dao.listSprinklerTypes();
	}

	@Override
	public void deleteSprinklerType(SprinklerType s) {
		dao.deleteSprinklerType(s);
	}

	@Override
	public ObservableList<SprinklerGroup> listSprinklerGroups() {
		return dao.listSprinklerGroups();
	}

	@Override
	public void addSprinklerGroup(SprinklerGroup s) throws DbException {
		dao.addSprinklerGroup(s);
		
	}

	@Override
	public void deleteSprinklerGroup(SprinklerGroup s) {
		dao.deleteSprinklerGroup(s);
		
	}

	@Override
	public ObservableList<SprinklerType> listSprinklerTypeByGroup(SprinklerGroup s) {
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
	public void updateSprinklerData(String column, double newValue, String name) {
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
	public SprinklerType getSprinklerType(String sprinklerType) {
		return dao.getSprinklerType(sprinklerType);
	}

}
