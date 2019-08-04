package model;

import javafx.collections.ObservableList;
import model.bean.Sprinkler;

public interface SprinklerDAO {

	public void addSprinkler(Sprinkler s);
	
	public ObservableList<Sprinkler> listSprinklers();
	
	public void deleteSprinkler(Sprinkler s);
	
}
