package model.bean;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class PipeGraph {

	private Zone zone;

	private Color color;
	
	private Set<Vertex> vertices;
	private Set<Edge> edges;
	private Map<Vertex, Set<Edge>> adjList;

	public PipeGraph(Zone zone, Color color) {
		vertices = new HashSet<>();
		edges = new HashSet<>();
		adjList = new HashMap<>();
		this.zone = zone;
		this.color = color;
	}

	public boolean addVertex(double x, double y) {
		return vertices.add(new Vertex(x, y));
	}

	public boolean addVertex(Vertex v) {
		return vertices.add(v);
	}

	public void addVertex(double x, double y, Vertex parent, SprinklerShape sprinklerShape) {
		vertices.add(new Vertex(x, y, parent, sprinklerShape));
	}

	public boolean removeVertex(double x, double y) {
		return vertices.remove(new Vertex(x, y));
	}

	public boolean addEdge(Edge e) {
		if (!edges.add(e))
			return false;

		adjList.putIfAbsent(e.vParent, new HashSet<>());
		adjList.putIfAbsent(e.vChild, new HashSet<>());

		adjList.get(e.vParent).add(e);
		adjList.get(e.vChild).add(e);

		return true;
	}

	public boolean addEdge(double vertex1X, double vertex1Y, double vertex2X, double vertex2Y) {
		return addEdge(new Edge(new Vertex(vertex1X, vertex1Y), new Vertex(vertex2X, vertex2Y)));
	}

	public boolean removeEdge(Edge e) {
		if (!edges.remove(e))
			return false;
		Set<Edge> edgesOfV1 = adjList.get(e.vParent);
		Set<Edge> edgesOfV2 = adjList.get(e.vChild);

		if (edgesOfV1 != null)
			edgesOfV1.remove(e);
		if (edgesOfV2 != null)
			edgesOfV2.remove(e);

		return true;
	}

	public boolean removeEdge(double vertex1X, double vertex1Y, double vertex2X, double vertex2Y) {
		return removeEdge(new Edge(new Vertex(vertex1X, vertex1Y), new Vertex(vertex2X, vertex2Y)));
	}

	public Set<Vertex> getAdjVertices(Vertex v) {
		return adjList.get(v).stream().map(e -> e.vParent.equals(v) ? e.vChild : e.vParent).collect(Collectors.toSet());
	}

	public Set<Vertex> getVertices() {
		return Collections.unmodifiableSet(vertices);
	}

	public Set<Edge> getEdges() {
		return Collections.unmodifiableSet(edges);
	}

	public Map<Vertex, Set<Edge>> getAdjList() {
		return Collections.unmodifiableMap(adjList);
		
	}

	public Zone getZone() {
		return zone;
	}

	public void setZone(Zone zone) {
		this.zone = zone;
	}
	
	

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}



	public static class Vertex extends Point2D {
		private Vertex parent;
		private Set<Vertex> children = new HashSet<>();
		private SprinklerShape sprinklerShape;

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

		public void addChild(Vertex child) {
			children.add(child);
		}

		
	}

	public static class Edge extends Line {

		Vertex vParent, vChild;
		// double weight;
		// TODO itt kéne eltárolni a csõátmérõt?

		public Edge(Vertex vParent, Vertex vChild) {
			super();
			this.setStartX(vParent.getX());
			this.setStartY(vParent.getY());
			this.setEndX(vChild.getX());
			this.setEndY(vChild.getY());
			this.vParent = vParent;
			this.vChild = vChild;
			vParent.addChild(vChild);
		}

		public Edge() {
			super();
		}

		public Vertex getvParent() {
			return vParent;
		}

		public void setvParent(Vertex vParent) {
			this.vParent = vParent;
			this.setStartX(vParent.getX());
			this.setStartY(vParent.getY());
		}

		public Vertex getvChild() {
			return vChild;
		}

		public void setvChild(Vertex vChild) {
			this.vChild = vChild;
			this.setEndX(vChild.getX());
			this.setEndY(vChild.getY());
		}
		
	}

}
