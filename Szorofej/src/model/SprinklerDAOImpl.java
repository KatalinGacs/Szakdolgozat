package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.bean.PipeGraph;
import model.bean.SprinklerGroup;
import model.bean.SprinklerShape;
import model.bean.SprinklerType;
import model.bean.Zone;

public class SprinklerDAOImpl implements SprinklerDAO {

	private static final String DBFILE = "db/sprinkler.db";

	private ObservableList<SprinklerType> sprinklertypes = FXCollections.observableArrayList();

	private ObservableList<SprinklerGroup> sprinklergroups = FXCollections.observableArrayList();

	private static ObservableList<SprinklerShape> sprinklers = FXCollections.observableArrayList();

	private static ObservableList<Zone> zones = FXCollections.observableArrayList();

	private static ObservableList<PipeGraph> pipeGraphs = FXCollections.observableArrayList();

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
				PreparedStatement pst = conn.prepareStatement("INSERT INTO Sprinklertype (name, minradius, "
						+ "maxradius, minangle, maxangle, fixwaterconsumption, waterconsumption, "
						+ "minpressure, sprinklergroup) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");) {
			pst.setString(1, s.getName());
			pst.setDouble(2, s.getMinRadius());
			pst.setDouble(3, s.getMaxRadius());
			pst.setDouble(4, s.getMinAngle());
			pst.setDouble(5, s.getMaxAngle());
			pst.setInt(6, s.getFixWaterConsumption() ? 1 : 0);
			pst.setDouble(7, s.getWaterConsumption());
			pst.setDouble(8, s.getMinPressure());
			pst.setString(9, s.getSprinklerGroup().toString());
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
				s.setSprinklerGroup(new SprinklerGroup(rs.getString("sprinklergroup")));
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
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public ObservableList<SprinklerGroup> listSprinklerGroups() {
		sprinklergroups.clear();
		try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DBFILE);
				Statement st = conn.createStatement();
				ResultSet rs = st.executeQuery("SELECT * FROM Sprinklergroup");) {
			while (rs.next()) {
				SprinklerGroup s = new SprinklerGroup();
				s.setName(rs.getString("Name"));
				sprinklergroups.add(s);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sprinklergroups;
	}

	@Override
	public void addSprinklerGroup(SprinklerGroup s) throws DbException {
		try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DBFILE);
				PreparedStatement pst = conn.prepareStatement("INSERT INTO Sprinklergroup (name) " + "VALUES (?)");) {
			pst.setString(1, s.getName());
			pst.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DbException("Nem sikerült a hozzáadás");
		}
	}

	@Override
	public void deleteSprinklerGroup(SprinklerGroup s) {
		try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DBFILE);
				PreparedStatement pst = conn.prepareStatement("DELETE FROM Sprinklergroup WHERE name = ?");) {
			pst.setString(1, s.getName());
			pst.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public ObservableList<SprinklerType> listSprinklerTypeByGroup(SprinklerGroup sg) {
		ObservableList<SprinklerType> sprinklertypes = FXCollections.observableArrayList();
		try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DBFILE);
				PreparedStatement pst = conn
						.prepareStatement("SELECT * FROM Sprinklertype " + "WHERE Sprinklergroup = ?");) {
			pst.setString(1, sg.getName());
			ResultSet rs = pst.executeQuery();
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
				s.setSprinklerGroup(new SprinklerGroup(rs.getString("sprinklergroup")));
				sprinklertypes.add(s);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sprinklertypes;
	}

	@Override
	public ObservableList<SprinklerShape> listSprinklerShapes(Zone zone) {
		ObservableList<SprinklerShape> list = FXCollections.observableArrayList();
		for (SprinklerShape s : sprinklers) {
			if (zone.getSprinklers().contains(s)) {
				list.add(s);
			}
		}
		return list;
	}

	@Override
	public ObservableList<Zone> listZones() {
		return zones;
	}

	@Override
	public void addZone(Zone z) {
		zones.add(z);
	}

	@Override
	public void removeZone(Zone z) {
		zones.remove(z);
	}

	@Override
	public ObservableList<PipeGraph> listPipeGraphs() {
		return pipeGraphs;
	}

	@Override
	public void addPipeGraph(PipeGraph p) {
		pipeGraphs.add(p);
	}

	@Override
	public void removePipeGraph(PipeGraph p) {
		pipeGraphs.remove(p);
	}

}
