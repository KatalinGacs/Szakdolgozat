package application;

import java.util.ArrayList;
import java.util.List;

import application.CanvasPane.Use;
import application.common.Common;
import controller.SprinklerController;
import controller.SprinklerControllerImpl;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import model.GraphException;
import model.PipeDiameterOptimizer;
import model.bean.PipeGraph;
import model.bean.PipeMaterial;
import model.bean.PipeGraph.Edge;
import model.bean.PipeGraph.Vertex;
import model.bean.SprinklerShape;
import model.bean.Zone;

public class PipeDrawing {

	static SprinklerController controller = new SprinklerControllerImpl();

	static Vertex startVertex;
	static Vertex breakPointVertex;

	static double lineBreakPointX;
	static double lineBreakPointY;
	static Edge pipeLineToSplit;

	public static void startDrawingPipeLine(MouseEvent e, CanvasPane canvasPane) {
		if (!canvasPane.pipeGraphUnderEditing.getVertices().isEmpty() && !canvasPane.cursorOnPipeLine) {
			Common.showAlert("A z�na megkezdett cs�vez�s�re kattintva folytasd a rajzol�st!");
			return;
		}
		if (canvasPane.cursorOnPipeLine) {
			breakLine(canvasPane);
		} else {
			BorderDrawing.startX = e.getX();
			BorderDrawing.startY = e.getY();
			startVertex = new Vertex(e.getX(), e.getY(), null);
			if (canvasPane.pipeGraphUnderEditing.getVertices().isEmpty()) {
				canvasPane.pipeGraphUnderEditing
						.setValve(ValveIcon.valveIcon(e.getX(), e.getY(), CanvasPane.pipeLineColor));
				canvasPane.pipeLineLayer.getChildren().add(canvasPane.pipeGraphUnderEditing.getValve());
			}
			canvasPane.pipeGraphUnderEditing.addVertex(startVertex);

		}
		canvasPane.stateOfCanvasUse = Use.PIPEDRAWING;

	}

	public static void breakLine(CanvasPane canvasPane) {
		Vertex breakPointVertex = new Vertex(lineBreakPointX, lineBreakPointY);
		breakPointVertex.setBreakPoint(true);
		breakPointVertex.setParent(pipeLineToSplit.getvParent());
		breakPointVertex.addChild(pipeLineToSplit.getvChild());
		pipeLineToSplit.getvChild().setParent(breakPointVertex);
		pipeLineToSplit.getvParent().getChildren().clear();
		pipeLineToSplit.getvParent().addChild(breakPointVertex);
		canvasPane.pipeGraphUnderEditing.addVertex(breakPointVertex);
		canvasPane.pipeGraphUnderEditing.removeEdge(pipeLineToSplit);
		canvasPane.pipeGraphUnderEditing.addEdge(new Edge(pipeLineToSplit.getvParent(), breakPointVertex));
		canvasPane.pipeGraphUnderEditing.addEdge(new Edge(breakPointVertex, pipeLineToSplit.getvChild()));
		startVertex = breakPointVertex;
		BorderDrawing.startX = breakPointVertex.getX();
		BorderDrawing.startY = breakPointVertex.getY();
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

	public static void completePipeDrawing(CanvasPane canvasPane, Zone zone, Vertex startingVertex) {
		System.out.println("completePipeDrawing called");
		System.out.println("startingV: " + startingVertex);
		PipeGraph pg = controller.getPipeGraph(zone);
		double beginningPressure = pg.getBeginningPressure();

		do {
			for (Vertex child : startingVertex.getChildren()) {
				calculatePipeDiameters(canvasPane, pg, startingVertex, child, beginningPressure);
				beginningPressure = PipeDiameterOptimizer.remainingPressure;
				if (breakPointVertex != null)
					completePipeDrawing(canvasPane, zone, breakPointVertex);
			}
		} while (breakPointVertex != null);

	}

	public static void calculatePipeDiameters(CanvasPane canvasPane, PipeGraph pg, Vertex startingVertex,
			Vertex nextVertex, double beginningPressure) {

		try {
			ArrayList<Double> pipeLengths = new ArrayList<>();
			ArrayList<SprinklerShape> sprinklers = new ArrayList<>();
			PipeMaterial pipe = new PipeMaterial();

			double totalWaterFlow = calculateSubGraphWaterFlow(pg, startingVertex, nextVertex);

			Vertex current = nextVertex;
			double currentLength = 0;

			outerloop: while (true) {
				currentLength += pg.getEdgeByChildVertex(current).getLength() / Common.pixelPerMeter;
				if (current.getSprinklerShape() != null) {
					pipe.setLength(currentLength);
					pipeLengths.add(currentLength);
					currentLength = 0;
					sprinklers.add(current.getSprinklerShape());
				}
				if (current.getChildren().isEmpty()) {
					breakPointVertex = null;
					break;
				}
				for (Vertex bp : pg.getBreakpoints()) {
					if (current == bp) {
						breakPointVertex = current;
						pipe.setLength(currentLength);
						pipeLengths.add(currentLength);
						currentLength = 0;
						break outerloop;
					}
				}
				current = current.getChild();
			}
			ArrayList<String> diameters = PipeDiameterOptimizer.optimalPipes(beginningPressure, pipeLengths, sprinklers,
					totalWaterFlow);

			System.out.println(diameters);
			// TODO eddig kisz�molja az els� szakaszig a megfelel� cs��tm�r�ket, ezut�n
			// 1. ugyanezt minden el�gaz�s ut�n a k�vetkez� szakaszra is kell rekurz�van
			// 2. a k�pre ki kell �rni szakaszonk�nt az �tm�r�ket
			// 3. el kell t�rolni hogy h�ny m�ter milyen �tm�r�j� cs� kell
			// 4. el kell t�rolni minden el�gaz�sn�l, hogy milyen T-idom kell

			String currentDiameter = null;
			Point2D position = new Point2D(startingVertex.getX(), startingVertex.getY());

			Text diameterText;
			if (sprinklers.isEmpty()) {
				if (diameters.get(0) != currentDiameter) {
					currentDiameter = diameters.get(0);
					diameterText = new Text(currentDiameter);
					diameterText.setX(position.getX() + (Common.pixelPerMeter / 2));
					diameterText.setY(position.getY() + (Common.pixelPerMeter / 2));
					diameterText.setStyle(Common.textstyle);
					canvasPane.pipeTextLayer.getChildren().add(diameterText);
				}
			} else {
				for (SprinklerShape s : sprinklers) {
					if (diameters.get(sprinklers.indexOf(s)) != currentDiameter) {
						currentDiameter = diameters.get(sprinklers.indexOf(s));
						diameterText = new Text(currentDiameter);
						// TODO: az el�gaz�sn�l egym�s f�l� ker�lnek a feliratok, nem �gy k�ne, hanem
						// aez els� cs�szakasz k�zepe f�l� pl
						diameterText.setX(position.getX() + (Common.pixelPerMeter / 2));
						diameterText.setY(position.getY() + (Common.pixelPerMeter / 2));
						diameterText.setStyle(Common.textstyle);
						canvasPane.pipeTextLayer.getChildren().add(diameterText);
						position = new Point2D(s.getCircle().getCenterX(), s.getCircle().getCenterY());
					}
				}
			}

		} catch (GraphException ex) {
			ex.printStackTrace();
		}

	}

	private static Vertex breakPoint2 = null;

	private static double calculateSubGraphWaterFlow(PipeGraph pg, Vertex startingVertex, Vertex nextVertex) {
		Vertex current = startingVertex;
		double totalWaterFlow = waterFlowUntilBreakpoint(pg, current, nextVertex);
		if (breakPoint2 != null) {
			for (Vertex child : breakPoint2.getChildren()) {
				totalWaterFlow += calculateSubGraphWaterFlow(pg, breakPoint2, child);
			}
		}
		return totalWaterFlow;
	}

	private static double waterFlowUntilBreakpoint(PipeGraph pg, Vertex startingVertex, Vertex child) {
		double totalWaterFlow = 0;
		try {
			Vertex current = child;

			outerloop: while (true) {

				if (current.getSprinklerShape() != null) {
					totalWaterFlow += current.getSprinklerShape().getFlowRate();
				}

				if (current.getChildren().isEmpty()) {
					breakPoint2 = null;
					break;

				}
				for (Vertex bp : pg.getBreakpoints()) {
					if (current == bp) {
						breakPoint2 = current;
						break outerloop;
					}
				}
				current = current.getChild();
			}

		} catch (GraphException e) {
			e.printStackTrace();
		}

		return totalWaterFlow;
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
