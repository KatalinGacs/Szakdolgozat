package controller;

import javafx.collections.ObservableList;
import model.SprinklerDAO;
import model.SprinklerDAOImpl;
import model.bean.Sprinkler;

public class SprinklerControllerImpl implements SprinklerController {

	private SprinklerDAO dao = new SprinklerDAOImpl();
	
	@Override
	public void addSprinkler(Sprinkler s) {
		dao.addSprinkler(s);
	}

	@Override
	public ObservableList<Sprinkler> listSprinklers() {
		return dao.listSprinklers();
	}

}
