package model.bean;

import java.util.ArrayList;

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

@XmlRootElement
public class Canvas {
	@XmlTransient
	private SprinklerController controller = new SprinklerControllerImpl();
	
	@XmlElementWrapper(name="BorderLines")
    @XmlElement(name="borderLine")
	public ArrayList<BorderLine> borderLines = borderLines();
	
	@XmlElementWrapper(name="CircleObstacles")
    @XmlElement(name="CircleObstacle")
	public ArrayList<CircleObstacle> circleObstacles = circleObstacles();
	
	@XmlElementWrapper(name="RectangleObstacles")
    @XmlElement(name="RectangleObstacle")
	public ArrayList<RectangleObstacle> rectangleObstacles = rectangleObstacles();
	
	@XmlElementWrapper(name="SprinklerShapes")
    @XmlElement(name="sprinklerShape")
	public ArrayList<SprinklerShape> sprinklerShapes = new ArrayList<>(controller.listSprinklerShapes());
	
	@XmlElementWrapper(name="Zones")
    @XmlElement(name="Zone")
	public ArrayList<Zone> zones = new ArrayList<>(controller.listZones());
	
	@XmlTransient
	public ArrayList<PipeGraph> pipeGraphs = new ArrayList<>(controller.listPipeGraphs());
	
	@XmlTransient
	public ArrayList<Text> texts = new ArrayList<>(controller.listTexts());
	
	private ArrayList<CircleObstacle> circleObstacles(){
		ArrayList<CircleObstacle> result = new ArrayList<>();
		for(Shape s: controller.listObstacles()) {
			if(s instanceof Circle) 
				result.add(new CircleObstacle((Circle) s));
		}
		return result;
	}

	private ArrayList<RectangleObstacle> rectangleObstacles(){
		ArrayList<RectangleObstacle> result = new ArrayList<>();
		for(Shape s: controller.listObstacles()) {
			if(s instanceof Rectangle) 
				result.add(new RectangleObstacle((Rectangle) s));
		}
		return result;
	}
	
	private ArrayList<BorderLine> borderLines() {
		ArrayList<BorderLine> result = new ArrayList<>();
		for(Shape s: controller.listBorderShapes()) {
			if(s instanceof Line) 
				result.add(new BorderLine((Line) s));
		}
		return result;
	}
}
