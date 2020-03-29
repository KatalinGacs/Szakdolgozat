package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import application.common.Common;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import model.bean.Material;
import model.bean.MaterialSprinklerConnection;
import model.bean.PipeGraph;
import model.bean.SprinklerGroup;
import model.bean.SprinklerShape;
import model.bean.SprinklerType;
import model.bean.UsedMaterial;
import model.bean.Zone;

public class SprinklerDAOImpl implements SprinklerDAO {

	private static final String DBFILE = "db/sprinkler.db";

	private ObservableList<SprinklerType> sprinklertypes = FXCollections.observableArrayList();

	private ObservableList<SprinklerGroup> sprinklergroups = FXCollections.observableArrayList();

	private ObservableList<Material> materials = FXCollections.observableArrayList();

	private static ObservableList<SprinklerShape> sprinklerShapes = FXCollections.observableArrayList();

	private static ObservableList<Zone> zones = FXCollections.observableArrayList();

	private static ObservableList<PipeGraph> pipeGraphs = FXCollections.observableArrayList();

	private static ObservableList<Shape> borderShapes = FXCollections.observableArrayList();

	private static ObservableList<Shape> obstacles = FXCollections.observableArrayList();

	private static ObservableList<Text> texts = FXCollections.observableArrayList();

	private static ObservableList<UsedMaterial> materialSum = FXCollections.observableArrayList();
	private static ObservableList<UsedMaterial> pipeMaterialSum = FXCollections.observableArrayList();
	
	public SprinklerDAOImpl() {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			System.out.println("Failed to load SQLite JDBC driver.");
		}
	}

	@Override
	public void addSprinklerShapes(SprinklerShape s) {
		sprinklerShapes.add(s);
	}

	@Override
	public ObservableList<SprinklerShape> listSprinklerShapes() {
		return sprinklerShapes;
	}

	@Override
	public void deleteSprinklerShape(SprinklerShape s) {
		sprinklerShapes.remove(s);
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
		for (SprinklerShape s : sprinklerShapes) {
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
		if (z == null) {
			Common.showAlert("Érvénytelen zóna");
			return;
		}
		for (Zone zone : listZones()) {
			if (zone.getName() == z.getName()) {
				Common.showAlert("Már létezik ilyen nevû zóna");
				return;
			}
		}
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
		for (PipeGraph pg : listPipeGraphs()) {
			if (p.getZone() == pg.getZone()) {
				Common.showAlert("Ennek a zónának már létezik megkezdett csövezése!");
				return;
			}
		}
		pipeGraphs.add(p);
	}

	@Override
	public void removePipeGraph(PipeGraph p) {
		pipeGraphs.remove(p);
	}

	@Override
	public void addBorderShape(Shape border) {
		borderShapes.add(border);
	}

	@Override
	public ObservableList<Shape> listBorderShapes() {
		return borderShapes;
	}

	@Override
	public void removeBorderShape(Shape border) {
		borderShapes.remove(border);
	}

	@Override
	public void addObstacle(Shape obstacle) {
		obstacles.add(obstacle);
	}

	@Override
	public ObservableList<Shape> listObstacles() {
		return obstacles;
	}

	@Override
	public void removeObstacle(Shape obstacle) {
		obstacles.remove(obstacle);
	}

	@Override
	public PipeGraph getPipeGraph(Zone zone) {
		for (PipeGraph pg : pipeGraphs) {
			if (pg.getZone() == zone) {
				return pg;
			}
		}
		return null;
	}

	@Override
	public void updateSprinklerData(String column, double newValue, String name) {
		try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DBFILE);
				PreparedStatement pst = conn
						.prepareStatement("UPDATE Sprinklertype SET " + column + " = ? WHERE name = ?");) {
			pst.setDouble(1, newValue);
			pst.setString(2, name);
			pst.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public ObservableList<Text> listTexts() {
		return texts;
	}

	@Override
	public void addText(Text t) {
		texts.add(t);
	}

	@Override
	public void removeText(Text t) {
		texts.remove(t);
	}

	@Override
	public SprinklerType getSprinklerType(String sprinklerType) {
		for (SprinklerType s : listSprinklerTypes()) {
			if (s.getName().equals(sprinklerType)) {
				return s;
			}
		}
		return null;
	}

	@Override
	public void clearBorderShapes() {
		borderShapes.clear();
	}

	@Override
	public void clearObstacles() {
		obstacles.clear();
	}

	@Override
	public void clearSprinklerShapes() {
		sprinklerShapes.clear();
	}

	@Override
	public void clearZones() {
		zones.clear();
	}

	@Override
	public void clearPipeGraphs() {
		pipeGraphs.clear();
	}

	@Override
	public void clearTexts() {
		texts.clear();
	}

	@Override
	public void clearMaterials() {
		materialSum.clear();
	}

	@Override
	public void clearAll() {
		clearBorderShapes();
		clearObstacles();
		clearPipeGraphs();
		clearSprinklerShapes();
		clearTexts();
		clearZones();
		clearMaterials();
	}

	@Override
	public void addMaterial(Material m) {
		try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DBFILE);
				PreparedStatement pst = conn
						.prepareStatement("INSERT INTO Material (name, unit) " + "VALUES (?, ?)");) {
			pst.setString(1, m.getName());
			pst.setString(2, m.getUnit());
			pst.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deleteMaterial(Material m) {
		try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DBFILE);
				PreparedStatement pst = conn.prepareStatement("DELETE FROM Material WHERE name = ?");) {
			pst.setString(1, m.getName());
			pst.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public ObservableList<Material> listMaterials() {
		materials.clear();
		try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DBFILE);
				Statement st = conn.createStatement();
				ResultSet rs = st.executeQuery("SELECT * FROM Material");) {
			while (rs.next()) {
				Material m = new Material();
				m.setName(rs.getString("Name"));
				m.setUnit(rs.getString("Unit"));
				materials.add(m);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return materials;
	}

	@Override
	public ObservableList<MaterialSprinklerConnection> listMaterials(SprinklerType selectedItem) {
		ObservableList<MaterialSprinklerConnection> materials = FXCollections.observableArrayList();
		try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DBFILE);
				PreparedStatement pst = conn
						.prepareStatement("SELECT * FROM Sprinklermaterial WHERE Sprinklertype = ?");) {
			pst.setString(1, selectedItem.getName());
			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				MaterialSprinklerConnection item = new MaterialSprinklerConnection();
				Material m = getMaterial(rs.getString("Materialname"));
				item.setMaterial(m);
				item.setQuantity(rs.getInt("Quantity"));
				materials.add(item);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return materials;
	}

	@Override
	public void addMaterialConnection(SprinklerType s, Material m, int quantity) {
		try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DBFILE);
				PreparedStatement pst = conn
						.prepareStatement("INSERT INTO Sprinklermaterial (sprinklertype, materialname, quantity) "
								+ "VALUES (?, ?, ?)");) {
			pst.setString(1, s.getName());
			pst.setString(2, m.getName());
			pst.setInt(3, quantity);
			pst.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deleteMaterialConnection(SprinklerType s, Material m) {
		try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DBFILE);
				PreparedStatement pst = conn.prepareStatement(
						"DELETE FROM Sprinklermaterial WHERE sprinklertype = ? AND materialname = ?");) {
			pst.setString(1, s.getName());
			pst.setString(2, m.getName());
			pst.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public ObservableList<Material> listNotAddedMaterials(SprinklerType selectedItem) {
		ObservableList<Material> materials = FXCollections.observableArrayList();

		try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DBFILE);
				PreparedStatement pst = conn.prepareStatement("SELECT * FROM Material WHERE name NOT IN "
						+ "(SELECT Materialname FROM Sprinklermaterial WHERE Sprinklertype = ?)");) {
			pst.setString(1, selectedItem.getName());
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				Material m = new Material();
				m.setName(rs.getString("Name"));
				m.setUnit(rs.getString("Unit"));
				materials.add(m);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return materials;
	}

	@Override
	public ObservableList<UsedMaterial> summarizeMaterials() {
		materialSum.clear();
		for (SprinklerShape s : sprinklerShapes) {
			for (MaterialSprinklerConnection mConn : listMaterials(getSprinklerType(s.getSprinklerType()))) {
				UsedMaterial m = new UsedMaterial();
				m.setMaterial(mConn.getMaterial());
				m.setQuantity(mConn.getQuantity());
				materialSum.add(m);
			}
		}
		for (UsedMaterial m : pipeMaterialSum) {
			materialSum.add(m);
		}
		if (listZones().size() > 0) {
			UsedMaterial solenoid = new UsedMaterial();
			solenoid.setMaterial(getMaterial("Mágnesszelep"));
			solenoid.setQuantity(listZones().size());
			materialSum.add(solenoid);
		}

		return materialSum;
	}

	@Override
	public Material getMaterial(String name) {
		for (Material m : listMaterials()) {
			if (m.getName().equals(name)) {
				return m;
			}
		}
		return null;
	}

	@Override
	public void addPipeMaterial(String pipename, Double length) {

		UsedMaterial m = new UsedMaterial();
		String name = null;
		switch (pipename) {
		case "20":
			name = "20-as csõ";
			break;
		case "25":
			name = "25-ös csõ";
			break;
		case "32":
			name = "32-es csõ";
			break;
		case "40":
			name = "40-es csõ";
			break;
		case "50":
			name = "50-es csõ";
			break;
		case "63":
			name = "63-as csõ";
			break;
		case "75":
			name = "75-ös csõ";
			break;
		case "90":
			name = "90-es csõ";
			break;
		case "110":
			name = "110-es csõ";
			break;
		}
		m.setMaterial(getMaterial(name));
		m.setQuantity((int) Math.round(length));
		pipeMaterialSum.add(m);
	}

	@Override
	public ObservableList<SprinklerShape> listSprinklerShapesNotInZones() {
		ObservableList<SprinklerShape> result = FXCollections.observableArrayList();
		ArrayList<SprinklerShape> inZone = new ArrayList<>();
		for (Zone z : listZones()) {
			inZone.addAll(z.getSprinklers());
		}
		for (SprinklerShape s : sprinklerShapes) {
			if (!inZone.contains(s)) {
				result.add(s);
			}
		}
		return result;
	}

}
