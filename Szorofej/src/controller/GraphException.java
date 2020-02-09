package controller;

import model.bean.PipeGraph.Vertex;

public class GraphException extends Exception {
	public GraphException(String msg) {
		super(msg);
	}
	
	public GraphException(String msg, Vertex v) {
		super(v + " " + msg);
	}
}
