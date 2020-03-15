package model.bean;

import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "VertexElement")
public  class VertexElement{
	private double X;
	private double Y;
	private String parentID;
	private Set<String> childrenID;
	private boolean root;
	private boolean breakpoint;
	private SprinklerShape sprinklerShape;
	private String ID = createID();
	private static AtomicLong idCounter = new AtomicLong();
	
	public VertexElement() {
		super();
	}
	@XmlElement(name = "X")
	public double getX() {
		return X;
	}
	public void setX(double x) {
		X = x;
	}
	@XmlElement(name = "Y")
	public double getY() {
		return Y;
	}
	public void setY(double y) {
		Y = y;
	}
	@XmlAttribute
	public String getParentID() {
		return parentID;
	}
	public void setParentID(String parentID) {
		this.parentID = parentID;
	}
	@XmlElementWrapper(name = "ChildrenIDs")
	@XmlElement(name = "ID")
	public Set<String> getChildrenID() {
		return childrenID;
	}
	public void setChildrenID(Set<String> childrenID) {
		this.childrenID = childrenID;
	}
	@XmlAttribute
	public boolean isRoot() {
		return root;
	}
	public void setRoot(boolean root) {
		this.root = root;
	}
	@XmlAttribute
	public boolean isBreakpoint() {
		return breakpoint;
	}
	public void setBreakpoint(boolean breakpoint) {
		this.breakpoint = breakpoint;
	}
	@XmlElement(name="Sprinkler")
	public SprinklerShape getSprinklerShape() {
		return sprinklerShape;
	}
	
	public void setSprinklerShape(SprinklerShape sprinklerShape) {
		this.sprinklerShape = sprinklerShape;
	}
	@XmlElement(name="ID")
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public static String createID() {
		return String.valueOf(idCounter.getAndIncrement());
	}
}
