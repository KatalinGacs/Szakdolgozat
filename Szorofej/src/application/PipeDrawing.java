package application;

import java.util.ArrayList;
import java.util.List;

import application.CanvasPane.Use;
import application.common.Common;
import controller.SprinklerController;
import controller.SprinklerControllerImpl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import model.GraphException;
import model.PipeDiameterOptimizer;
import model.bean.PipeGraph;
import model.bean.PipeGraph.Edge;
import model.bean.PipeGraph.Vertex;
import model.bean.SprinklerShape;
import model.bean.Zone;

public class PipeDrawing {

	static SprinklerController controller = new SprinklerControllerImpl();

	static Vertex startVertex;

	static double lineBreakPointX;
	static double lineBreakPointY;
	static Edge pipeLineToSplit;

	public static void startDrawingPipeLine(MouseEvent e, CanvasPane canvasPane) {
		if (!canvasPane.pipeGraphUnderEditing.getVertices().isEmpty() && !canvasPane.cursorOnPipeLine) {
			Common.showAlert("A z�na megkezdett cs�vez�s�re kattintva folytasd a rajzol�st!");
			return;
		}
		BorderDrawing.startX = e.getX();
		BorderDrawing.startY = e.getY();
		startVertex = new Vertex(e.getX(), e.getY(), null);
		if (canvasPane.pipeGraphUnderEditing.getVertices().isEmpty()) {
			canvasPane.pipeGraphUnderEditing
					.setValve(ValveIcon.valveIcon(e.getX(), e.getY(), CanvasPane.pipeLineColor));
			canvasPane.pipeLineLayer.getChildren().add(canvasPane.pipeGraphUnderEditing.getValve());
		}
		canvasPane.pipeGraphUnderEditing.addVertex(startVertex);
		canvasPane.stateOfCanvasUse = Use.PIPEDRAWING;

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
				if (!canvasPane.pipeGraphUnderEditing.getZone().getSprinklers()
						.contains(canvasPane.sprinklerShapeNearCursor)) {
					Common.showAlert("Nem a z�n�ba tartoz� sz�r�fej!");
				} else {
					endVertex = new Vertex(CanvasPane.sprinklerHeadX, CanvasPane.sprinklerHeadY, startVertex,
							canvasPane.sprinklerShapeNearCursor);
				}
			} else {
				endVertex = new Vertex(e.getX(), e.getY());
			}
		}
		startVertex.addChild(endVertex);
		endVertex.setParent(startVertex);
		canvasPane.pipeGraphUnderEditing.addVertex(endVertex);
		line.setvChild(endVertex);
		startVertex = endVertex;
		BorderDrawing.startX = endVertex.getX();
		BorderDrawing.startY = endVertex.getY();
		canvasPane.pipeLineLayer.getChildren().add(line);
		canvasPane.pipeGraphUnderEditing.addEdge(line);
	}

	public static void breakLine(CanvasPane canvasPane) {
		Vertex breakPointVertex = new Vertex(lineBreakPointX, lineBreakPointY);
		breakPointVertex.setBreakPoint(true);
		breakPointVertex.setParent(pipeLineToSplit.getvParent());
		pipeLineToSplit.getvChild().setParent(breakPointVertex);
		canvasPane.pipeGraphUnderEditing.addVertex(breakPointVertex);
		canvasPane.pipeGraphUnderEditing.removeEdge(pipeLineToSplit);
		canvasPane.pipeGraphUnderEditing.addEdge(new Edge(pipeLineToSplit.getvParent(), breakPointVertex));
		canvasPane.pipeGraphUnderEditing.addEdge(new Edge(breakPointVertex, pipeLineToSplit.getvChild()));

	}

	public static void calculatePipeDiameters(Zone zone) {
		PipeGraph pg = controller.getPipeGraph(zone);
		Vertex startingVertex = pg.getRoot();
		double beginningPressure = pg.getBeginningPressure();
		try {
			// TODO ezt nem is biztos hogy haszn�lom
			List<Vertex> currentLine = getVerticesUntilNextBreakPoint(startingVertex, startingVertex.getChild());
			ArrayList<Double> pipeLengths = new ArrayList<>();
			ArrayList<SprinklerShape> sprinklers = new ArrayList<>();
			Vertex current = startingVertex.getChild();
			double currentLength = 0;

			outerloop: while (true) {
				currentLength += pg.getEdgeByChildVertex(current).getLength() / Common.pixelPerMeter;
				if (current.getSprinklerShape() != null) {
					pipeLengths.add(currentLength);
					currentLength = 0;
					sprinklers.add(current.getSprinklerShape());
				}
				if (current.getChildren().isEmpty())
					break;
				for (Vertex bp : pg.getBreakpoints()) {
					if (current == bp)
						break outerloop;
				}
				current = current.getChild();
			}
			ArrayList<String> diameters = PipeDiameterOptimizer.optimalPipes(beginningPressure, pipeLengths,
					sprinklers);
			System.out.println(diameters);
			// TODO eddig kisz�molja az els� szakaszig a megfelel� cs��tm�r�ket, ezut�n
			// 1. ugyanezt minden el�gaz�s ut�n a k�vetkez� szakaszra is kell rekurz�van
			// 2. a k�pre ki kell �rni szakaszonk�nt az �tm�r�ket
			// 3. el kell t�rolni hogy h�ny m�ter milyen �tm�r�j� cs� kell
			// 4. el kell t�rolni minden el�gaz�sn�l, hogy milyen T-idom kell
			
		} catch (GraphException ex) {
			ex.printStackTrace();
		}
	
	}

	private static List<Vertex> getVerticesUntilNextBreakPoint(Vertex startingBreakPoint, Vertex direction) {
		List<Vertex> result = new ArrayList<>();
		Vertex child = direction;
		result.add(startingBreakPoint);
		result.add(child);
		while (true) {
			if (child.isBreakPoint() || child.getChildren().isEmpty()) {
				break;
			} else {
				try {
					child = child.getChild();
					result.add(child);
				} catch (GraphException ex) {
					ex.printStackTrace();
				}
			}
		}
		return result;
	}

}
