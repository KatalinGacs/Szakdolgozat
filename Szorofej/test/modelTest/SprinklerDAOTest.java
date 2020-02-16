package modelTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import model.SprinklerDAO;
import model.SprinklerDAOImpl;
import model.bean.SprinklerShape;

public class SprinklerDAOTest {

	@Test
	void testAddSprinklerShapes() {
		SprinklerDAO dao = new SprinklerDAOImpl();
		SprinklerShape testShape = new SprinklerShape();
		dao.addSprinklerShapes(testShape);
		assertTrue(dao.listSprinklerShapes().contains(testShape));
	}

	@Test
	void testListSprinklerShapes() {
		SprinklerDAO dao = new SprinklerDAOImpl();
		SprinklerShape testShape1 = new SprinklerShape();
		SprinklerShape testShape2 = new SprinklerShape();
		SprinklerShape testShape3 = new SprinklerShape();
		dao.addSprinklerShapes(testShape1);
		dao.addSprinklerShapes(testShape2);
		dao.addSprinklerShapes(testShape3);
		assertTrue(dao.listSprinklerShapes().contains(testShape1));
		assertTrue(dao.listSprinklerShapes().contains(testShape2));
		assertTrue(dao.listSprinklerShapes().contains(testShape3));
	}

	@Test
	void testDeleteSprinklerShape() {
		SprinklerDAO dao = new SprinklerDAOImpl();
		SprinklerShape testShape = new SprinklerShape();
		dao.addSprinklerShapes(testShape);
		dao.deleteSprinklerShape(testShape);
		assertFalse(dao.listSprinklerShapes().contains(testShape));
	}
/*
	@Test
	void testAddSprinklerType() {
		fail("Not yet implemented");
	}

	@Test
	void testListSprinklerTypes() {
		fail("Not yet implemented");
	}

	@Test
	void testDeleteSprinklerType() {
		fail("Not yet implemented");
	}

	@Test
	void testListSprinklerGroups() {
		fail("Not yet implemented");
	}

	@Test
	void testAddSprinklerGroup() {
		fail("Not yet implemented");
	}

	@Test
	void testDeleteSprinklerGroup() {
		fail("Not yet implemented");
	}

	@Test
	void testListSprinklerTypeByGroup() {
		fail("Not yet implemented");
	}

	@Test
	void testListSprinklerShapesZone() {
		fail("Not yet implemented");
	}

	@Test
	void testListZones() {
		fail("Not yet implemented");
	}

	@Test
	void testAddZone() {
		fail("Not yet implemented");
	}

	@Test
	void testRemoveZone() {
		fail("Not yet implemented");
	}

	@Test
	void testListPipeGraphs() {
		fail("Not yet implemented");
	}

	@Test
	void testAddPipeGraph() {
		fail("Not yet implemented");
	}

	@Test
	void testRemovePipeGraph() {
		fail("Not yet implemented");
	}

	@Test
	void testAddBorderShape() {
		fail("Not yet implemented");
	}

	@Test
	void testListBorderShapes() {
		fail("Not yet implemented");
	}

	@Test
	void testRemoveBorderShape() {
		fail("Not yet implemented");
	}

	@Test
	void testAddObstacle() {
		fail("Not yet implemented");
	}

	@Test
	void testListObstacles() {
		fail("Not yet implemented");
	}

	@Test
	void testRemoveObstacle() {
		fail("Not yet implemented");
	}

	@Test
	void testGetPipeGraph() {
		fail("Not yet implemented");
	}

	@Test
	void testUpdateSprinklerData() {
		fail("Not yet implemented");
	}

	@Test
	void testListTexts() {
		fail("Not yet implemented");
	}

	@Test
	void testAddText() {
		fail("Not yet implemented");
	}

	@Test
	void testRemoveText() {
		fail("Not yet implemented");
	}

	@Test
	void testGetSprinklerType() {
		fail("Not yet implemented");
	}
*/
}
