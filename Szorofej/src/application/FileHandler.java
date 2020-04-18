package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import application.common.Common;
import controller.PressureException;
import controller.SprinklerController;
import controller.SprinklerControllerImpl;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.bean.BorderLine;
import model.bean.Canvas;
import model.bean.CircleObstacle;
import model.bean.PipeGraph;
import model.bean.PipeGraph.Edge;
import model.bean.PipeGraph.Vertex;
import model.bean.RectangleObstacle;
import model.bean.SprinklerShape;
import model.bean.TextElement;
import model.bean.VertexElement;
import model.bean.Zone;

public class FileHandler {

	private static SprinklerController controller = new SprinklerControllerImpl();

	/**
	 * Path of the XML file if the plan is saved.
	 */
	static String currentPath = "";

	/**
	 * Name of the XML file if the plan is saved.
	 */
	static String currentFileName = "";

	/**
	 * Clear everything off from the CanvasPane and start a new plan. If called
	 * while there are unsaved modifications on the opened plan then asks for
	 * confirmation and the user can save the modifications before starting a new
	 * plan.
	 * 
	 * @param canvasPane the CanvasPane to be cleared
	 * @param stage      if the previous plan is changed and the user chooses to
	 *                   save it first, this is the owner window of the Save dialog
	 */
	public static void newCanvas(CanvasPane canvasPane, Stage stage) {
		if (canvasPane.isModifiedSinceLastSave()) {
			SaveModificationsStage s = new SaveModificationsStage(false, canvasPane, stage);
		} else {
			canvasPane.clear();
			canvasPane.hideTempLayer();
			currentPath = "";
			stage.setTitle(Common.programName + " - " + currentPath);
		}
		canvasPane.setModifiedSinceLastSave(false);
	}

	/**
	 * Save the current file in XML. If it is a previously saved plan and saveAs is
	 * false, overwrite it. Otherwise asks for the save location and filename.
	 * 
	 * @param stage      the owner window of the Save dialog
	 * @param canvasPane the CanvasPane to be saved
	 * @param saveAs     true if the user chose "Save as" instead of "Save"
	 */
	public static void saveCanvas(Stage stage, CanvasPane canvasPane, boolean saveAs) {
		File file;
		if (currentPath == "" || saveAs == true) {
			FileChooser fileChooser = new FileChooser();
			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
			fileChooser.getExtensionFilters().add(extFilter);
			file = fileChooser.showSaveDialog(stage);
		} else {
			file = new File(currentPath);
		}
		if (file != null) {
			currentPath = file.getAbsolutePath();
			currentFileName = file.getName();
			try (FileOutputStream fileOS = new FileOutputStream(currentPath, false)) {
				for (Zone z : controller.listZones()) {
					z.updateVertices();
				}
				Canvas canvas = new Canvas();
				JAXBContext context = JAXBContext.newInstance(Canvas.class);
				Marshaller m = context.createMarshaller();
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

				m.marshal(canvas, fileOS);

				canvasPane.setModifiedSinceLastSave(false);

			} catch (IOException | JAXBException ex) {
				ex.printStackTrace();
			}

		}
		stage.setTitle(Common.programName + " - " + currentPath);
	}

	/**
	 * Open a previously saved plan from XML. Load the XML, unmarshall it and put
	 * the shapes on a cleared CanvasPane. If called while there are unsaved
	 * modifications on the opened plan then asks for confirmation and the user can
	 * save the modifications before starting a new plan.
	 * 
	 * @param canvasPane the CanvasPane to be cleared and loaded with the saved plan
	 * @param stage      if the previous plan is changed and the user chooses to
	 *                   save it first, this is the owner window of the Save dialog
	 */
	public static void loadCanvas(CanvasPane canvasPane, Stage stage) {
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
		fileChooser.getExtensionFilters().add(extFilter);
		File file = fileChooser.showOpenDialog(stage);
		if (file != null) {
			currentPath = file.getAbsolutePath();
			stage.setTitle(Common.programName + " - " + currentPath);
			newCanvas(canvasPane, stage);
			JAXBContext context;
			try {
				context = JAXBContext.newInstance(Canvas.class);
				Unmarshaller um = context.createUnmarshaller();
				Canvas canvas = (Canvas) um.unmarshal(new FileInputStream(file));
				loadSprinklerShapes(canvasPane, canvas);
				loadBorderLines(canvasPane, canvas);
				loadCircleObstacles(canvasPane, canvas);
				loadRectangleObstacles(canvasPane, canvas);
				loadZones(canvasPane, canvas);
				loadTexts(canvasPane, canvas);
				canvasPane.setModifiedSinceLastSave(false);
			} catch (JAXBException | FileNotFoundException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Add the Sprinklershapes from the XML unmarshalling to the CanvasPane
	 * 
	 * @param canvasPane the CanvasPane where the sprinklers will be loaded
	 * @param canvas     the result of XML unmarshalling containing a list of
	 *                   sprinklershapes
	 */
	private static void loadSprinklerShapes(CanvasPane canvasPane, Canvas canvas) {

		for (SprinklerShape s : controller.listSprinklerShapes()) {
			canvasPane.getIrrigationLayer().getChildren().remove(s.getCircle());
			canvasPane.getSprinklerArcLayer().getChildren().remove(s.getArc());
			canvasPane.getSprinklerTextLayer().getChildren().remove(s.getLabel());
		}
		controller.listSprinklerShapes().clear();

		for (SprinklerShape s : canvas.sprinklerShapesNotInZone) {
			loadSprinkler(s, canvasPane);
		}
	}

	/**
	 * Add the Sprinklershape from the XML unmarshalling to the CanvasPane
	 * 
	 * @param s          the SprinklerShape to be added to the CanvasPane
	 * @param canvasPane the CanvasPane where the sprinklerShape will be loaded
	 */
	private static void loadSprinkler(SprinklerShape s, CanvasPane canvasPane) {
		Color strokeColor = Color.web(s.getStrokeColor());
		Color fillColor = s.getFillColor().equals("0x000000ff") ? Color.TRANSPARENT : Color.web(s.getFillColor());
		s.setArc(new Arc(s.getCenterX(), s.getCenterY(), s.getRadius() * Common.pixelPerMeter,
				s.getRadius() * Common.pixelPerMeter, s.getStartAngle(), s.getLength()));
		s.getArc().setFill(fillColor);
		s.getArc().setStroke(strokeColor);
		s.getArc().setStrokeWidth(s.getStrokeWidth());
		s.getArc().setType(ArcType.ROUND);
		s.getArc().setOpacity(s.getFillOpacity());
		s.setCircle(new Circle(s.getCenterX(), s.getCenterY(), s.getCircleRadius(), strokeColor));
		s.setLabel(new Text(s.getLabelText()));
		s.getLabel().setX(s.getLabelX());
		s.getLabel().setY(s.getLabelY());
		s.getLabel().setStyle(s.getLabelStyle());
		s.setSprinkler(controller.getSprinklerType(s.getSprinklerType()));
		s.setFlowRate(s.getFlowRate());
		s.setWaterCoverageInMmPerHour(s.getWaterCoverageInMmPerHour());

		controller.addSprinklerShape(s);
		canvasPane.getIrrigationLayer().getChildren().add(s.getCircle());
		canvasPane.getSprinklerArcLayer().getChildren().add(s.getArc());
		canvasPane.getSprinklerTextLayer().getChildren().add(s.getLabel());
	}

	/**
	 * Add the borderLines from the XML unmarshalling to the CanvasPane
	 * 
	 * @param canvasPane the CanvasPane where the borderLines will be loaded
	 * @param canvas     the result of XML unmarshalling containing a list of
	 *                   borderLines
	 */
	private static void loadBorderLines(CanvasPane canvasPane, Canvas canvas) {
		for (Shape s : controller.listBorderShapes()) {
			canvasPane.getBordersLayer().getChildren().remove(s);
		}
		controller.listBorderShapes().clear();

		for (BorderLine b : canvas.borderLines) {
			Color strokeColor = Color.web(b.getColor());
			Line line = new Line(b.getStartX(), b.getStartY(), b.getEndX(), b.getEndY());
			line.setStrokeWidth(b.getWidth());
			line.setStroke(strokeColor);
			controller.addBorderShape(line);
			canvasPane.getBordersLayer().getChildren().add(line);
		}
	}

	/**
	 * Add the circleObstacles from the XML unmarshalling to the CanvasPane
	 * 
	 * @param canvasPane the CanvasPane where the circleObstacles will be loaded
	 * @param canvas     the result of XML unmarshalling containing a list of
	 *                   circleObstacles
	 */
	private static void loadCircleObstacles(CanvasPane canvasPane, Canvas canvas) {
		for (Shape s : controller.listObstacles()) {
			canvasPane.getBordersLayer().getChildren().remove(s);
		}
		controller.listObstacles().clear();

		for (CircleObstacle c : canvas.circleObstacles) {
			Color strokeColor = Color.web(c.getStrokeColor());
			Color fillColor = Color.web(c.getFillColor());
			Circle circle = new Circle(c.getCenterX(), c.getCenterY(), c.getRadius(), fillColor);
			circle.setStroke(strokeColor);
			circle.setStrokeWidth(c.getStrokeWidth());

			controller.addBorderShape(circle);
			controller.addObstacle(circle);
			canvasPane.getBordersLayer().getChildren().add(circle);
		}
	}

	/**
	 * Add the rectangleObstacles from the XML unmarshalling to the CanvasPane
	 * 
	 * @param canvasPane the CanvasPane where the rectangleObstacles will be loaded
	 * @param canvas     the result of XML unmarshalling containing a list of
	 *                   rectangleObstacles
	 */
	private static void loadRectangleObstacles(CanvasPane canvasPane, Canvas canvas) {
		for (RectangleObstacle r : canvas.rectangleObstacles) {
			Color strokeColor = Color.web(r.getStrokeColor());
			Color fillColor = Color.web(r.getFillColor());
			Rectangle rectangle = new Rectangle(r.getX(), r.getY(), r.getWidth(), r.getHeight());
			rectangle.setStroke(strokeColor);
			rectangle.setFill(fillColor);
			rectangle.setStrokeWidth(r.getStrokeWidth());

			controller.addBorderShape(rectangle);
			controller.addObstacle(rectangle);
			canvasPane.getBordersLayer().getChildren().add(rectangle);
		}
	}

	/**
	 * Load the zones from the XML unmarshalling to the CanvasPane. Recreate their
	 * piping, recalculate the pipe diameters.
	 * 
	 * @param canvasPane the CanvasPane where the zones and pipes will be loaded
	 * @param canvas     the result of XML unmarshalling containing a list of zones
	 *                   with informations of their pipes
	 * @throws PressureException when calculating the pipe diameters this exception
	 *                           shows if the beginning pressure of the pipeline can
	 *                           not be enough for the zone
	 */
	private static void loadZones(CanvasPane canvasPane, Canvas canvas) throws PressureException {
		controller.clearZones();
		for (Zone zone : canvas.zones) {
			PipeGraph pg = new PipeGraph();
			pg.setZone(zone);
			pg.setBeginningPressure(zone.getBeginningPressure());
			for (VertexElement vE : zone.getVertices()) {
				Vertex vertex = new Vertex(vE.getX(), vE.getY());
				if (vE.getSprinklerShape() != null) {
					loadSprinkler(vE.getSprinklerShape(), canvasPane);
					vertex.setSprinklerShape(vE.getSprinklerShape());
					zone.addSprinkler(vE.getSprinklerShape());
				}

				vertex.setBreakPoint(vE.isBreakpoint());
				if (vE.isRoot()) {
					pg.setRoot(vertex);
				}
				vertex.setVertexElement(vE);
				pg.addVertex(vertex);
			}
			for (Vertex vertex : pg.getVertices()) {
				for (Vertex other : pg.getVertices()) {
					if (vertex.getVertexElement().getParentID() == other.getVertexElement().getID()) {
						vertex.setParent(other);
					}
					if (vertex.getVertexElement().getChildrenID().contains(other.getVertexElement().getID())) {
						vertex.addChild(other);
					}
				}
			}
			for (Vertex vertex : pg.getVertices()) {
				for (Vertex child : vertex.getChildren()) {
					Edge edge = new Edge();
					edge.setStartX(vertex.getX());
					edge.setStartY(vertex.getY());
					edge.setEndX(child.getX());
					edge.setEndY(child.getY());
					edge.setvParent(vertex);
					edge.setStrokeWidth(CanvasPane.getStrokeWidth() * 2);
					edge.setStroke(Color.valueOf(zone.getColor()));
					edge.setvChild(child);
					pg.addEdge(edge);
					canvasPane.getPipeLineLayer().getChildren().add(edge);
				}
			}
			pg.setValve(ValveIcon.valveIcon(pg.getRoot().getX(), pg.getRoot().getY(), Color.valueOf(zone.getColor())));
			canvasPane.getPipeLineLayer().getChildren().add(pg.getValve());
			controller.addZone(zone);
			controller.addPipeGraph(pg);
			PipeDrawing.completePipeDrawing(canvasPane, zone, pg.getRoot());
		}
	}

	/**
	 * Add texts from the XML unmarshalling to the CanvasPane
	 * 
	 * @param canvasPane the CanvasPane where the texts will be loaded
	 * @param canvas     the result of XML unmarshalling containing a list of texts
	 */
	private static void loadTexts(CanvasPane canvasPane, Canvas canvas) {
		for (Text t : controller.listTexts()) {
			canvasPane.getTextLayer().getChildren().remove(t);
		}
		for (Text t : controller.listTexts()) {
			controller.removeText(t);
		}
		for (TextElement t : canvas.texts) {
			Text text = new Text(t.getX(), t.getY(), t.getText());
			text.setStyle(t.getStyle());
			text.setFont(Font.font(t.getFont(), FontWeight.SEMI_BOLD, t.getFontSize()));
			text.setFill(Color.web(t.getFillColor()));
			controller.addText(text);
			canvasPane.getTextLayer().getChildren().add(text);
		}
	}

	public static String getCurrentPath() {
		return currentPath;
	}

	public static String getCurrentFileName() {
		return currentFileName;
	}
}
