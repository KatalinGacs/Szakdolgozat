package controller;

import javafx.collections.ObservableList;
import model.DbException;
import model.SprinklerDAO;
import model.SprinklerDAOImpl;
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
}
