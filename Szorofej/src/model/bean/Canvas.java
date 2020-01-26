package model.bean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import controller.SprinklerController;
import controller.SprinklerControllerImpl;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import model.bean.PipeGraph.Vertex;

/**
 * This class is serving as a wrapper for saving and loading the project. It
 * contains the elements drawn on the CanvasPane
 */
@XmlRootElement
public class Canvas {
	@XmlTransient
	private SprinklerController controller = new SprinklerControllerImpl();

	@XmlElementWrapper(name = "BorderLines")
	@XmlElement(name = "borderLine")
	public ArrayList<BorderLine> borderLines = borderLines();

	@XmlElementWrapper(name = "CircleObstacles")
	@XmlElement(name = "CircleObstacle")
	public ArrayList<CircleObstacle> circleObstacles = circleObstacles();

	@XmlElementWrapper(name = "RectangleObstacles")
	@XmlElement(name = "RectangleObstacle")
	public ArrayList<RectangleObstacle> rectangleObstacles = rectangleObstacles();

	@XmlElementWrapper(name = "SprinklerShapes")
	@XmlElement(name = "sprinklerShape")
	public ArrayList<SprinklerShape> sprinklerShapes = new ArrayList<>(controller.listSprinklerShapes());

	@XmlElementWrapper(name = "Zones")
	@XmlElement(name = "Zone")
	public ArrayList<Zone> zones = new ArrayList<>(controller.listZones());

	@XmlElementWrapper(name = "PipeGraphs")
	@XmlElement(name = "PipeGraph")
	public ArrayList<PipeGraph> pipeGraphs = new ArrayList<>(controller.listPipeGraphs());
	
	@XmlElementWrapper(name = "VertexElements")
	@XmlElement(name = "VertexElement")
	public ArrayList<VertexElement> vertexElements = listVertexElements();
	
	@XmlElementWrapper(name = "Texts")
	@XmlElement(name = "TextElement")
	public ArrayList<TextElement> texts = listTexts();

	private ArrayList<CircleObstacle> circleObstacles() {
		ArrayList<CircleObstacle> result = new ArrayList<>();
		for (Shape s : controller.listObstacles()) {
			if (s instanceof Circle)
				result.add(new CircleObstacle((Circle) s));
		}
		return result;
	}

	private ArrayList<RectangleObstacle> rectangleObstacles() {
		ArrayList<RectangleObstacle> result = new ArrayList<>();
		for (Shape s : controller.listObstacles()) {
			if (s instanceof Rectangle)
				result.add(new RectangleObstacle((Rectangle) s));
		}
		return result;
	}

	private ArrayList<BorderLine> borderLines() {
		ArrayList<BorderLine> result = new ArrayList<>();
		for (Shape s : controller.listBorderShapes()) {
			if (s instanceof Line)
				result.add(new BorderLine((Line) s));
		}
		return result;
	}

	private ArrayList<TextElement> listTexts() {
		ArrayList<TextElement> result = new ArrayList<>();
		for (Text t : controller.listTexts()) {
			TextElement text = new TextElement(t.getText(), t.getStyle(), t.getX(), t.getY(), t.getFill().toString(),
					t.getFont().getSize(), t.getFont().toString());
			result.add(text);
		}
		return result;
	}


	private ArrayList<VertexElement> listVertexElements() {
		ArrayList<VertexElement> result = new ArrayList<>();
		for(PipeGraph pg : controller.listPipeGraphs()) {
			for(Vertex v : pg.getVertices()) {
				VertexElement ve = new VertexElement();
				ve.setBreakpoint(v.isBreakPoint());
				ve.setRoot(v.getParent()==null);
				ve.setX(v.getX());
				ve.setY(v.getY());
				//TODO :(
				//ve.setParent(parent);
				//Set<VertexElement> children = new HashSet<>();
				//ve.setChildren(children);
			}
		}
		return result;
	}
}


