package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.bean.SprinklerShape;
import model.bean.SprinklerType;

public class SprinklerDAOImpl implements SprinklerDAO {

	private static final String DBFILE = "db/sprinkler.db";

	private ObservableList<SprinklerType> sprinklertypes = FXCollections.observableArrayList();

	private static ObservableList<SprinklerShape> sprinklers = FXCollections.observableArrayList();

	public SprinklerDAOImpl() {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			System.out.println("Failed to load SQLite JDBC driver.");
		}
	}

	@Override
	public void addSprinklerShapes(SprinklerShape s) {
		sprinklers.add(s);
	}

	@Override
	public ObservableList<SprinklerShape> listSprinklerShapes() {
		return sprinklers;
	}

	@Override
	public void deleteSprinklerShape(SprinklerShape s) {
		sprinklers.remove(s);
	}

	@Override
	public void addSprinklerType(SprinklerType s) {
		try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DBFILE);
				PreparedStatement pst = conn.prepareStatement("INSERT INTO Sprinklertype (name, minradius"
						+ "maxradius, minangle, maxangle, fixwaterconsumption, waterconsumption"
						+ "minpressure, sprinklergroup) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");) {
			pst.setString(1, s.getName());
			pst.setDouble(2, s.getMinRadius());
			pst.setDouble(3, s.getMaxRadius());
			pst.setDouble(4, s.getMinAngle());
			pst.setDouble(5, s.getMaxAngle());
			pst.setInt(6, s.getFixWaterConsumption() ? 1 : 0);
			pst.setDouble(7, s.getWaterCounsumption());
			pst.setDouble(8, s.getMinPressure());
			pst.setString(9, s.getSprinklerGroup());
			pst.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public ObservableList<SprinklerType> listSprinklerTypes() {
		sprinklertypes.clear();
		try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DBFILE);
				Statement st = conn.createStatement();
				ResultSet rs = st.executeQuery("SELECT * FROM Sprinklertype");) {
			while (rs.next()) {
				SprinklerType s = new SprinklerType();
				s.setName(rs.getString("Name"));
				s.setMinRadius(rs.getDouble("minradius"));
				s.setMaxRadius(rs.getDouble("maxradius"));
				s.setMinAngle(rs.getDouble("minangle"));
				s.setMaxAngle(rs.getDouble("maxangle"));
				s.setFixWaterConsumption(rs.getInt("fixwaterconsumption") == 1 ? true : false);
				s.setWaterCounsumption(rs.getDouble("waterconsumption"));
				s.setMinPressure(rs.getDouble("minpressure"));
				s.setSprinklerGroup(rs.getString("sprinklergroup"));
				sprinklertypes.add(s);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sprinklertypes;
	}

	@Override
	public void deleteSprinklerType(SprinklerType s) {
		try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DBFILE);
				PreparedStatement pst = conn.prepareStatement("DELETE FROM Sprinklertype WHERE name = ?");) {
			pst.setString(1, s.getName());
			pst.execute();
			sprinklertypes.remove(s);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
