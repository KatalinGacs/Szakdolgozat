package model.bean;

import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "VertexElement")
public  class VertexElement{
	private double X;
	private double Y;
	private VertexElement parent;
	private Set<VertexElement> children;
	private boolean root;
	private boolean breakpoint;
	private String sprinklerShapeID;
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
    @XmlIDREF
	public VertexElement getParent() {
		return parent;
	}
	public void setParent(VertexElement parent) {
		this.parent = parent;
	}
	@XmlElement(name="Child")
    @XmlIDREF
	public Set<VertexElement> getChildren() {
		return children;
	}
	public void setChildren(Set<VertexElement> children) {
		this.children = children;
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
	@XmlElement(name="SprinklerId")
	public String getSprinklerShapeID() {
		return sprinklerShapeID;
	}
	
	public void setSprinklerShapeID(String sprinklerShapeID) {
		this.sprinklerShapeID = sprinklerShapeID;
	}
	
}
