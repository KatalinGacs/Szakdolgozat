package model;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import controller.SprinklerController;
import controller.SprinklerControllerImpl;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import model.bean.PipeGraph;
import model.bean.SprinklerShape;
import model.bean.Zone;

@XmlRootElement
public class Canvas {
	@XmlTransient
	private SprinklerController controller = new SprinklerControllerImpl();
	
	@XmlTransient
	public ArrayList<Shape> borderShapes = new ArrayList<>(controller.listBorderShapes());
	@XmlTransient
	public ArrayList<Shape> obstacles = new ArrayList<>(controller.listObstacles());
	
	@XmlElementWrapper(name="sprinklerShapes")
    @XmlElement(name="sprinklerShape")
	public ArrayList<SprinklerShape> sprinklerShapes = new ArrayList<>(controller.listSprinklerShapes());
	@XmlTransient
	public ArrayList<Zone> zones = new ArrayList<>(controller.listZones());
	@XmlTransient
	public ArrayList<PipeGraph> pipeGraphs = new ArrayList<>(controller.listPipeGraphs());
	@XmlTransient
	public ArrayList<Text> texts = new ArrayList<>(controller.listTexts());
}
