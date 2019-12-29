package model;

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
import model.bean.BorderLine;
import model.bean.PipeGraph;
import model.bean.SprinklerShape;
import model.bean.Zone;

@XmlRootElement
public class Canvas {
	@XmlTransient
	private SprinklerController controller = new SprinklerControllerImpl();
	
	@XmlElementWrapper(name="borderLines")
    @XmlElement(name="borderLine")
	public ArrayList<BorderLine> borderLines = borderLines();
	@XmlTransient
	public ArrayList<Circle> circleObstacles = circleObstacles();
	@XmlTransient
	public ArrayList<Rectangle> rectangleObstacles = rectangleObstacles();
	
	@XmlElementWrapper(name="sprinklerShapes")
    @XmlElement(name="sprinklerShape")
	public ArrayList<SprinklerShape> sprinklerShapes = new ArrayList<>(controller.listSprinklerShapes());
	@XmlTransient
	public ArrayList<Zone> zones = new ArrayList<>(controller.listZones());
	@XmlTransient
	public ArrayList<PipeGraph> pipeGraphs = new ArrayList<>(controller.listPipeGraphs());
	@XmlTransient
	public ArrayList<Text> texts = new ArrayList<>(controller.listTexts());
	
	private ArrayList<Circle> circleObstacles(){
		ArrayList<Circle> result = new ArrayList<Circle>();
		for(Shape s: controller.listObstacles()) {
			if(s instanceof Circle) 
				result.add((Circle) s);
		}
		return result;
	}

	private ArrayList<Rectangle> rectangleObstacles(){
		ArrayList<Rectangle> result = new ArrayList<>();
		for(Shape s: controller.listObstacles()) {
			if(s instanceof Rectangle) 
				result.add((Rectangle) s);
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
