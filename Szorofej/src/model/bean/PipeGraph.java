package model.bean;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import model.GraphException;

@XmlRootElement(name = "PipeGraph")
public class PipeGraph {

	private Zone zone;
	private Color color;
	private double beginningPressure;
	private Shape valve;

	private Set<Vertex> vertices;
	private Set<Edge> edges;
	private Set<Vertex> breakpoints = new HashSet<>();
	private Vertex root;

	private String zoneName;
	private String Color;
	private double valveX;
	private double valveY;
	
	public PipeGraph(Zone zone, Color color) {
		vertices = new HashSet<>();
		edges = new HashSet<>();
		this.zone = zone;
		this.color = color;
	}

	public PipeGraph() {
		super();
	}

	public boolean addVertex(double x, double y) {
		return vertices.add(new Vertex(x, y));
	}

	public boolean addVertex(Vertex v) {
		if (v.breakPoint)
			breakpoints.add(v);
		if (vertices.isEmpty())
			root = v;
		return vertices.add(v);
	}

	public boolean addVertex(double x, double y, Vertex parent, SprinklerShape sprinklerShape) {
		return addVertex(new Vertex(x, y, parent, sprinklerShape));
	}

	public boolean removeVertex(double x, double y) {
		return vertices.remove(new Vertex(x, y));
	}

	public boolean addEdge(Edge e) {
		if (!edges.add(e))
			return false;
		return true;
	}

	public boolean removeEdge(Edge e) {
		if (!edges.remove(e))
			return false;
		return true;
	}

	public boolean removeEdge(double vertex1X, double vertex1Y, double vertex2X, double vertex2Y) {
		return removeEdge(new Edge(new Vertex(vertex1X, vertex1Y), new Vertex(vertex2X, vertex2Y)));
	}
	@XmlTransient //TODO 
	public Set<Vertex> getVertices() {
		return Collections.unmodifiableSet(vertices);
	}
	
	@XmlTransient
	public Set<Edge> getEdges() {
		return Collections.unmodifiableSet(edges);
	}
	@XmlTransient
	public Zone getZone() {
		return zone;
	}

	public void setZone(Zone zone) {
		this.zone = zone;
	}
	@XmlTransient
	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
	@XmlTransient
	public Shape getValve() {
		return valve;
	}

	public void setValve(Shape valve) {
		this.valve = valve;
	}
	
	@XmlElement(name = "BeginningPressure")
	public double getBeginningPressure() {
		return beginningPressure;
	}

	public void setBeginningPressure(double beginningPressure) {
		this.beginningPressure = beginningPressure;
	}

	@XmlTransient
	public Set<Vertex> getBreakpoints() {
		return breakpoints;
	}

	public void setBreakpoints(Set<Vertex> breakpoints) {
		this.breakpoints = breakpoints;
	}
	
	@XmlTransient //TODO 
	public Vertex getRoot() {
		return root;
	}

	public void setRoot(Vertex root) {
		this.root = root;
	}
	
	public Edge getEdgeByChildVertex(Vertex child) {
		Edge e = null;

		for (Edge edge : edges) {
			if (edge.getvChild() == child) {
				e = edge;
				// break;
			}
		}
		return e;
	}

	public Vertex getVertexBySprinklerShape(SprinklerShape s) {
		Vertex result = null;
		for (Vertex v : vertices) {
			if (v.getSprinklerShape() == s) {
				result = v;
				break;
			}
		}
		return result;
	}

	public int getNumberOfLeaves() {
		int result = 0;
		for (Vertex v : vertices) {
			if (v.getChildren().isEmpty()) {
				result++;
			}
		}
		return result;
	}

	public static class Vertex extends Point2D {
		private Vertex parent;
		private Set<Vertex> children = new HashSet<>();
		private SprinklerShape sprinklerShape;
		private boolean breakPoint = false;

		public Vertex(Point2D point) {
			super(point.getX(), point.getY());
		}

		public Vertex(double x, double y) {
			super(x, y);
		}

		public Vertex(double x, double y, Vertex parent) {
			super(x, y);
			this.parent = parent;
			if (parent != null) {
				parent.children.add(this);
			}
		}

		public Vertex(double x, double y, Vertex parent, SprinklerShape sprinklerShape) {
			super(x, y);
			this.sprinklerShape = sprinklerShape;
			this.parent = parent;
		}

		public Vertex getParent() {
			return parent;
		}

		public void setParent(Vertex parent) {
			this.parent = parent;
		}

		public Set<Vertex> getChildren() {
			return children;
		}

		public Vertex getChild() throws GraphException {
			if (children.size() == 1) {
				for (Vertex v : children) {
					return v;
				}
			} else if (children.size() == 0) {
				throw new GraphException("This Vertex has no children", this);
			} else
				throw new GraphException("This Vertex has more than one child", this);
			return null;
		}

		public void addChild(Vertex child) {
			children.add(child);
		}

		public boolean isBreakPoint() {
			return breakPoint;
		}

		public void setBreakPoint(boolean breakPoint) {
			this.breakPoint = breakPoint;
		}

		public SprinklerShape getSprinklerShape() {
			return sprinklerShape;
		}

		public void setSprinklerShape(SprinklerShape sprinklerShape) {
			this.sprinklerShape = sprinklerShape;
		}

	}
	
	public static class Edge extends Line {

		Vertex vParent, vChild;

		public Edge(Vertex vParent, Vertex vChild) {
			super();
			this.setStartX(vParent.getX());
			this.setStartY(vParent.getY());
			this.setEndX(vChild.getX());
			this.setEndY(vChild.getY());
			this.vParent = vParent;
			this.vChild = vChild;
		}

		public Edge() {
			super();
		}
		@XmlTransient
		public Vertex getvParent() {
			return vParent;
		}

		public void setvParent(Vertex vParent) {
			this.vParent = vParent;
			this.setStartX(vParent.getX());
			this.setStartY(vParent.getY());
		}
		@XmlTransient
		public Vertex getvChild() {
			return vChild;
		}

		public void setvChild(Vertex vChild) {
			this.vChild = vChild;
			this.setEndX(vChild.getX());
			this.setEndY(vChild.getY());
		}

		@XmlTransient
		public double getLength() {
			return Math.sqrt(((getStartX() - getEndX()) * (getStartX() - getEndX()))
					+ ((getStartY() - getEndY()) * (getStartY() - getEndY())));
		}

	}


}
