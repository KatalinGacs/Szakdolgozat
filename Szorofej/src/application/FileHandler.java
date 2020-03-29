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

	static String currentPath = "";

	static String currentFileName = "";

	public static void newCanvas(CanvasPane canvasPane, Stage stage) {
		if (canvasPane.isModifiedSinceLastSave()) {
			SaveModificationsStage s = new SaveModificationsStage(false, canvasPane, stage);
		} else {
			canvasPane.clear();
			canvasPane.hideTempLayer();
		}
		canvasPane.setModifiedSinceLastSave(false);
	}

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
				updateZoneInfos();
				Canvas canvas = new Canvas();
				JAXBContext context = JAXBContext.newInstance(Canvas.class);
				Marshaller m = context.createMarshaller();
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

				m.marshal(canvas, System.out);

				m.marshal(canvas, fileOS);

				canvasPane.setModifiedSinceLastSave(false);

			} catch (IOException | JAXBException ex) {
				ex.printStackTrace();
			}

		}
		stage.setTitle(Common.programName + " - " + currentPath);

	}

	public static void loadCanvas(CanvasPane canvasPane, Stage stage) {

		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
		fileChooser.getExtensionFilters().add(extFilter);
		File file = fileChooser.showOpenDialog(stage);
		currentPath = file.getAbsolutePath();
		stage.setTitle(Common.programName + " - " + currentPath);

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

	private static void loadSprinklerShapes(CanvasPane canvasPane, Canvas canvas) {

		for (SprinklerShape s : controller.listSprinklerShapes()) {
			canvasPane.irrigationLayer.getChildren().remove(s.getCircle());
			canvasPane.sprinklerArcLayer.getChildren().remove(s.getArc());
			canvasPane.sprinklerTextLayer.getChildren().remove(s.getLabel());
		}
		controller.listSprinklerShapes().clear();

		for (SprinklerShape s : canvas.sprinklerShapesNotInZone) {
			loadSprinkler(s, canvasPane);
		}
	}

	private static void loadBorderLines(CanvasPane canvasPane, Canvas canvas) {
		for (Shape s : controller.listBorderShapes()) {
			canvasPane.bordersLayer.getChildren().remove(s);
		}
		controller.listBorderShapes().clear();

		for (BorderLine b : canvas.borderLines) {
			Color strokeColor = Color.web(b.getColor());
			Line line = new Line(b.getStartX(), b.getStartY(), b.getEndX(), b.getEndY());
			line.setStrokeWidth(b.getWidth());
			line.setStroke(strokeColor);
			controller.addBorderShape(line);
			canvasPane.bordersLayer.getChildren().add(line);
		}
	}

	private static void loadCircleObstacles(CanvasPane canvasPane, Canvas canvas) {
		for (Shape s : controller.listObstacles()) {
			canvasPane.bordersLayer.getChildren().remove(s);
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
			canvasPane.bordersLayer.getChildren().add(circle);
		}
	}

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
			canvasPane.bordersLayer.getChildren().add(rectangle);
		}
	}

	private static void loadZones(CanvasPane canvasPane, Canvas canvas) throws PressureException {
		for (Zone zone : controller.listZones()) {
			controller.removeZone(zone);
		}
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
					edge.setStrokeWidth(CanvasPane.strokeWidth * 2);
					edge.setStroke(Color.valueOf(zone.getColor()));
					edge.setvChild(child);
					pg.addEdge(edge);
					canvasPane.pipeLineLayer.getChildren().add(edge);
				}
			}
			pg.setValve(ValveIcon.valveIcon(pg.getRoot().getX(), pg.getRoot().getY(), Color.valueOf(zone.getColor())));
			canvasPane.pipeLineLayer.getChildren().add(pg.getValve());
			controller.addZone(zone);
			controller.addPipeGraph(pg);
			PipeDrawing.completePipeDrawing(canvasPane, zone, pg.getRoot());
		}
	}

	private static void loadTexts(CanvasPane canvasPane, Canvas canvas) {
		for (Text t : controller.listTexts()) {
			canvasPane.textLayer.getChildren().remove(t);
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
			canvasPane.textLayer.getChildren().add(text);
		}
	}

	public static String getCurrentPath() {
		return currentPath;
	}

	public static String getCurrentFileName() {
		return currentFileName;
	}

	private static void updateZoneInfos() {
		for (Zone z : controller.listZones()) {
			z.updateVertices();
		}
	}

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
		canvasPane.irrigationLayer.getChildren().add(s.getCircle());
		canvasPane.sprinklerArcLayer.getChildren().add(s.getArc());
		canvasPane.sprinklerTextLayer.getChildren().add(s.getLabel());
	}
}
