package application;

import application.common.Common;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import model.bean.PipeGraph.Edge;
import model.bean.PipeGraph.Vertex;

public class PipeDrawing {

	static Vertex startVertex;

	public static void startDrawingPipeLine(MouseEvent e, CanvasPane canvasPane) {
		BorderDrawing.startX = e.getX();
		BorderDrawing.startY = e.getY();
		startVertex = new Vertex(e.getX(), e.getY(), null);
		canvasPane.pipeGraph.addVertex(startVertex);
	}
	
	public static void drawPipeLine(MouseEvent e, CanvasPane canvasPane) {
	
		BorderDrawing.tempBorderLine.setVisible(false);
		Edge line = new Edge();
		line.setvParent(PipeDrawing.startVertex);
		line.setStrokeWidth(CanvasPane.strokeWidth);
		line.setStroke(CanvasPane.pipeLineColor);
		if (canvasPane.pressedKey == KeyCode.CONTROL) {
			Vertex endVertex = new Vertex(Common.snapToHorizontalOrVertival(BorderDrawing.startX, BorderDrawing.startY, e.getX(), e.getY()));
			endVertex.setParent(PipeDrawing.startVertex);
			canvasPane.pipeGraph.addVertex(endVertex);
			line.setvChild(endVertex);
			PipeDrawing.startVertex = endVertex;
			BorderDrawing.startX = endVertex.getX();
			BorderDrawing.startY = endVertex.getY();
			canvasPane.pipeLineLayer.getChildren().add(line);
			canvasPane.pipeGraph.addEdge(line);
			canvasPane.controller.addPipeGraph(canvasPane.pipeGraph);
		} else {
			if (canvasPane.cursorNearSprinklerHead) {
				if (!canvasPane.pipeGraph.getZone().getSprinklers().contains(canvasPane.sprinklerShapeNearCursor)) {
					Common.showAlert("Nem a zónába tartozó szórófej!");
				} else {
					Vertex endVertex = new Vertex(CanvasPane.sprinklerHeadX, CanvasPane.sprinklerHeadY, PipeDrawing.startVertex,
							canvasPane.sprinklerShapeNearCursor);
					canvasPane.pipeGraph.addVertex(endVertex);
					line.setvChild(endVertex);
					PipeDrawing.startVertex = endVertex;
					BorderDrawing.startX = endVertex.getX();
					BorderDrawing.startY = endVertex.getY();
					canvasPane.pipeLineLayer.getChildren().add(line);
					canvasPane.pipeGraph.addEdge(line);
					canvasPane.controller.addPipeGraph(canvasPane.pipeGraph);
				}
			} else {
				Vertex endVertex = new Vertex(e.getX(), e.getY());
				endVertex.setParent(PipeDrawing.startVertex);
				canvasPane.pipeGraph.addVertex(endVertex);
				line.setvChild(endVertex);
				PipeDrawing.startVertex = endVertex;
				BorderDrawing.startX = endVertex.getX();
				BorderDrawing.startY = endVertex.getY();
				canvasPane.pipeLineLayer.getChildren().add(line);
				canvasPane.pipeGraph.addEdge(line);
				canvasPane.controller.addPipeGraph(canvasPane.pipeGraph);
			}
		}
	
	
	}



}
