package controller;

import javafx.collections.ObservableList;
import model.bean.Sprinkler;

public interface SprinklerController {

	public void addSprinkler(Sprinkler s);
	
	public ObservableList<Sprinkler> listSprinklers();
	
	public void deleteSprinkler(Sprinkler s);
}
