package controller;

import javafx.collections.ObservableList;
import model.SprinklerDAO;
import model.SprinklerDAOImpl;
import model.bean.SprinklerShape;
import model.bean.SprinklerType;

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
}
