package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.bean.Sprinkler;

public class SprinklerDAOImpl implements SprinklerDAO {

	private static ObservableList<Sprinkler> sprinklers = FXCollections.observableArrayList();

	@Override
	public void addSprinkler(Sprinkler s) {
		sprinklers.add(s);
	}

	@Override
	public ObservableList<Sprinkler> listSprinklers() {
		return sprinklers;
	}

	@Override
	public void deleteSprinkler(Sprinkler s) {
		sprinklers.remove(s);
	}

}
