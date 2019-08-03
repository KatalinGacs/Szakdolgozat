package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
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

}
