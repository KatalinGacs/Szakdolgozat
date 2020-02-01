package applicationTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import application.BorderDrawing;
import application.CanvasPane;
import application.MainSpr;
import controller.SprinklerController;
import controller.SprinklerControllerImpl;
import model.bean.SprinklerShape;
import model.bean.Zone;

public class CanvasPaneTest {


	@Test
	public void testCreateZone() {
		CanvasPane canvasPane = new CanvasPane();
		canvasPane.createZone("zone", 1.5);
		SprinklerController controller = new SprinklerControllerImpl();
		Zone zone = new Zone();
		for (Zone z : controller.listZones()) {
			if (z.getName() == "zone") {
				zone = z;
				break;
			}
		}
		assertTrue(zone.getName() == "zone");
		assertTrue(zone.getDurationOfWatering() == 1.5);
	}

	@Test
	public void testDeselectAll() {
		CanvasPane canvasPane = new CanvasPane();
		canvasPane.getSelectedSprinklerShapes().add(new SprinklerShape());
		canvasPane.getSelectedSprinklerShapes().add(new SprinklerShape());
		canvasPane.deselectAll();
		assertTrue(canvasPane.getSelectedSprinklerShapes().isEmpty());
	}

	@Test
	public void testEndLineDrawing() {
		CanvasPane canvasPane = new CanvasPane();
		canvasPane.endLineDrawing();
		assertFalse(BorderDrawing.getLengthInput().isVisible());
		assertFalse(BorderDrawing.getTempBorderLine().isVisible());
		assertTrue(canvasPane.getStateOfCanvasUse() == CanvasPane.Use.NONE);
		canvasPane.setStateOfCanvasUse(CanvasPane.Use.PIPEDRAWING);
		canvasPane.endLineDrawing();
		assertTrue(canvasPane.getStateOfCanvasUse() == CanvasPane.Use.PREPAREFORPIPEDRAWING);
	}

}
