package application;

import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;

import application.common.Common;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import model.bean.PipeGraph;
import model.bean.PipeGraph.Edge;
import model.bean.PipeGraph.Vertex;
import model.bean.Zone;

public class PipeDrawing {

	static Vertex startVertex;

	static double lineBreakPointX;
	static double lineBreakPointY;
	static Edge pipeLineToSplit;

	public static void startDrawingPipeLine(MouseEvent e, CanvasPane canvasPane) {
		BorderDrawing.startX = e.getX();
		BorderDrawing.startY = e.getY();
		startVertex = new Vertex(e.getX(), e.getY(), null);
		canvasPane.pipeGraph.addVertex(startVertex);
	}

	public static void drawPipeLine(MouseEvent e, CanvasPane canvasPane) {
		BorderDrawing.tempBorderLine.setVisible(false);
		Edge line = new Edge();
		Vertex endVertex = null;
		line.setvParent(startVertex);
		line.setStrokeWidth(CanvasPane.strokeWidth);
		line.setStroke(CanvasPane.pipeLineColor);
		if (canvasPane.pressedKey == KeyCode.CONTROL) {
			endVertex = new Vertex(
					Common.snapToHorizontalOrVertival(BorderDrawing.startX, BorderDrawing.startY, e.getX(), e.getY()));
		} else {
			if (canvasPane.cursorNearSprinklerHead) {
				if (!canvasPane.pipeGraph.getZone().getSprinklers().contains(canvasPane.sprinklerShapeNearCursor)) {
					Common.showAlert("Nem a zónába tartozó szórófej!");
				} else {
					endVertex = new Vertex(CanvasPane.sprinklerHeadX, CanvasPane.sprinklerHeadY,
							startVertex, canvasPane.sprinklerShapeNearCursor);
				}
			} else {
				endVertex = new Vertex(e.getX(), e.getY());
			}
		}
		startVertex.addChild(endVertex);
		endVertex.setParent(startVertex);
		canvasPane.pipeGraph.addVertex(endVertex);
		line.setvChild(endVertex);
		startVertex = endVertex;
		BorderDrawing.startX = endVertex.getX();
		BorderDrawing.startY = endVertex.getY();
		canvasPane.pipeLineLayer.getChildren().add(line);
		canvasPane.pipeGraph.addEdge(line);
	}

	public static void breakLine(CanvasPane canvasPane) {
		System.out.println("itt");
		Vertex breakPointVertex = new Vertex(lineBreakPointX, lineBreakPointY);
		breakPointVertex.setParent(pipeLineToSplit.getvParent());
		pipeLineToSplit.getvChild().setParent(breakPointVertex);
		canvasPane.pipeGraph.addVertex(breakPointVertex);
		canvasPane.pipeGraph.removeEdge(pipeLineToSplit);
		canvasPane.pipeGraph.addEdge(new Edge(pipeLineToSplit.getvParent(), breakPointVertex));
		canvasPane.pipeGraph.addEdge(new Edge(breakPointVertex, pipeLineToSplit.getvChild()));

	}

}
