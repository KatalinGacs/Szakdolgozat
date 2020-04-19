package application;

import java.util.ArrayList;

import application.CanvasPane.Use;
import application.common.Common;
import controller.GraphException;
import controller.PipeDiameterOptimizer;
import controller.PressureException;
import controller.SprinklerController;
import controller.SprinklerControllerImpl;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import model.DbException;
import model.bean.PipeGraph;
import model.bean.PipeGraph.Edge;
import model.bean.PipeGraph.Vertex;
import model.bean.SprinklerShape;
import model.bean.Zone;

/**
 * Helper class for drawing pipes on the CanvasPane
 * 
 * @author Gacs Katalin
 *
 */
public class PipeDrawing {

	/**
	 * Controller to access data from the database
	 */
	private static SprinklerController controller = new SprinklerControllerImpl();

	/**
	 * The point where the current pipe polyline is started
	 */
	static Vertex startVertex;

	/**
	 * The point from where another branch of the pipeline is starting
	 */
	static Vertex breakPointVertex;

	/**
	 * The X coordinate of the breakpoint
	 */
	static double lineBreakPointX;

	/**
	 * The Y coordinate of the breakpoint
	 */
	static double lineBreakPointY;

	/**
	 * The line from which another branch of the pipeline is starting
	 */
	static Edge pipeLineToSplit;

	/**
	 * True if calculating pipe diameters arrived to a leaf in the pipegraph
	 */
	static boolean leaf = false;

	/**
	 * Begin drawing the pipe polyline, or begin drawing a new branch on is
	 * 
	 * @param e          Mousevent, mouse clicked on the canvasPane, from where the
	 *                   pipeline is starting
	 * @param canvasPane CanvasPane on which the line is drawn
	 */
	public static void startDrawingPipeLine(MouseEvent e, CanvasPane canvasPane) {

		if (!canvasPane.pipeGraphUnderEditing.getVertices().isEmpty() && !canvasPane.cursorOnPipeLine) {
			Common.showAlert("A zóna megkezdett csövezésére kattintva folytasd a rajzolást!");
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
						.setValve(ValveIcon.valveIcon(e.getX(), e.getY(), CanvasPane.getPipeLineColor()));
				canvasPane.getPipeLineLayer().getChildren().add(canvasPane.pipeGraphUnderEditing.getValve());
			}
			canvasPane.pipeGraphUnderEditing.addVertex(startVertex);
		}
		canvasPane.setStateOfCanvasUse(Use.PIPEDRAWING);
	}

	/**
	 * When a new branch of the pipeline is added, the pipeGraph has to be modified,
	 * a new vertex added and its parent and child vertices set
	 * 
	 * @param canvasPane CanvasPane on which the line is drawn
	 */
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
		canvasPane.setModifiedSinceLastSave(true);
	}

	/**
	 * Draw the pipe polyline. Connect sprinklershapes to it.
	 * 
	 * @param e          MouseEvent mouse clicked, the end point of the current line
	 *                   and the start point of the next line
	 * @param canvasPane CanvasPane on which the line is drawn
	 */
	public static void drawPipeLine(MouseEvent e, CanvasPane canvasPane) {
		canvasPane.setModifiedSinceLastSave(true);
		BorderDrawing.tempBorderLine.setVisible(false);
		Edge line = new Edge();
		Vertex endVertex = null;
		line.setvParent(startVertex);
		line.setStrokeWidth(CanvasPane.getStrokeWidth() * 2);
		line.setStroke(CanvasPane.getPipeLineColor());
		if (canvasPane.getPressedKey() == KeyCode.CONTROL) {
			endVertex = new Vertex(
					Common.snapToHorizontalOrVertival(BorderDrawing.startX, BorderDrawing.startY, e.getX(), e.getY()));
		} else {
			if (canvasPane.cursorNearSprinklerHead) {
				if (!canvasPane.pipeGraphUnderEditing.getZone().getSprinklers()
						.contains(canvasPane.sprinklerShapeNearCursor)) {
					Common.showAlert("Nem a zónába tartozó szórófej!");
				} else {
					endVertex = new Vertex(CanvasPane.sprinklerHeadX, CanvasPane.sprinklerHeadY, startVertex,
							canvasPane.sprinklerShapeNearCursor);
					canvasPane.sprinklerShapeNearCursor.setConnectedToPipe(true);
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
		canvasPane.getPipeLineLayer().getChildren().add(line);
		canvasPane.pipeGraphUnderEditing.addEdge(line);
	}

	/**
	 * Finish the pipe drawing and calculate the pipe diameters for the pipes
	 * 
	 * @param canvasPane CanvasPane on which the line is drawn
	 * @param zone       Zone for which the pipes are drawn
	 * @param root       root Vertex of the pipeGraph for this zone
	 * @throws PressureException
	 */
	public static void completePipeDrawing(CanvasPane canvasPane, Zone zone, Vertex root) throws PressureException {
		canvasPane.setModifiedSinceLastSave(true);
		PipeGraph pg = controller.getPipeGraph(zone);
		int leafes = pg.getNumberOfLeaves();
		Vertex startingVertex = root;
		double beginningPressure = pg.getBeginningPressure();

		do {
			for (Vertex child : startingVertex.getChildren()) {
				calculatePipeDiameters(canvasPane, pg, startingVertex, child, beginningPressure);
				beginningPressure = PipeDiameterOptimizer.remainingPressure;

				if (leaf)
					leafes--;
			}
			startingVertex = breakPointVertex;
		} while (leafes != 0);

	}

	/**
	 * Calculate the pipe diameters for a part of the pipeGraph where there are not
	 * branches. Put the calculated diameters in text on the canvas and save them
	 * for material summarizing.
	 * 
	 * @param canvasPane        CanvasPane on which the pipe graph is drawn
	 * @param pg                PipeGraph whose pipe diameters are being calculated
	 * @param startingVertex    starting Vertex of the subgraph
	 * @param nextVertex        child Vertex of the subgraph
	 * @param beginningPressure the water pressure at the starting Vertex
	 * @throws PressureException
	 */
	public static void calculatePipeDiameters(CanvasPane canvasPane, PipeGraph pg, Vertex startingVertex,
			Vertex nextVertex, double beginningPressure) throws PressureException {
		try {
			ArrayList<Double> pipeLengths = new ArrayList<>();
			ArrayList<SprinklerShape> sprinklers = new ArrayList<>();

			double totalWaterFlow = calculateSubGraphWaterFlow(pg, startingVertex, nextVertex);

			Vertex current = nextVertex;
			double currentLength = 0;

			outerloop: while (true) {
				currentLength += pg.getEdgeByChildVertex(current).getLength() / Common.pixelPerMeter;
				if (current.getSprinklerShape() != null) {
					pipeLengths.add(currentLength);
					currentLength = 0;
					sprinklers.add(current.getSprinklerShape());
				}
				if (current.getChildren().isEmpty()) {
					leaf = true;
					break;
				}
				for (Vertex bp : pg.getBreakpoints()) {
					if (current == bp) {
						breakPointVertex = current;
						leaf = false;
						pipeLengths.add(currentLength);
						currentLength = 0;
						break outerloop;
					}
				}
				current = current.getChild();
			}
			ArrayList<String> diameters = PipeDiameterOptimizer.optimalPipes(beginningPressure, pipeLengths, sprinklers,
					totalWaterFlow);

			String currentDiameter = null;
			double posStartX = startingVertex.getX();
			double posStartY = startingVertex.getY();
			double posEndX = nextVertex.getX();
			double posEndY = nextVertex.getY();
			Point2D position = new Point2D((posStartX + posEndX) / 2, (posStartY + posEndY) / 2);

			Text diameterText;
			if (sprinklers.isEmpty()) {
				if (diameters.get(0) != currentDiameter) {
					currentDiameter = diameters.get(0);
					diameterText = new Text(currentDiameter);
					pg.getPipeTextes().add(diameterText);

					diameterText.setX(position.getX() + (Common.pixelPerMeter / 2));
					diameterText.setY(position.getY() + (Common.pixelPerMeter / 2));
					diameterText.setStyle(Common.textstyle);
					canvasPane.getPipeTextLayer().getChildren().add(diameterText);
				}
			} else {
				SprinklerShape s = null;
				Vertex sprinklerVertex = null;
				for (int i = 0; i < pipeLengths.size(); i++) {

					if (i < sprinklers.size()) {
						s = sprinklers.get(i);
						sprinklerVertex = pg.getVertexBySprinklerShape(s);
					}
					if (diameters.get(i) != currentDiameter) {

						currentDiameter = diameters.get(i);
						diameterText = new Text(currentDiameter);
						pg.getPipeTextes().add(diameterText);

						diameterText.setX(position.getX() + (Common.pixelPerMeter / 2));
						diameterText.setY(position.getY() + (Common.pixelPerMeter / 2));
						diameterText.setStyle(Common.textstyle);
						canvasPane.getPipeTextLayer().getChildren().add(diameterText);
						if (!sprinklerVertex.getChildren().isEmpty()) {
							posStartX = sprinklerVertex.getX();
							posStartY = sprinklerVertex.getY();
							posEndX = sprinklerVertex.getChild().getX();
							posEndY = sprinklerVertex.getChild().getY();
						}
					}
					position = new Point2D((posStartX + posEndX) / 2, (posStartY + posEndY) / 2);
				}
			}
			for (Double length : pipeLengths) {
				controller.addPipeMaterial(diameters.get(pipeLengths.indexOf(length)), length);
			}
		} catch (GraphException ex) {
			ex.printStackTrace();
		} catch (DbException ex) {
			Common.showAlert(ex.getMessage());
		}
	}

	/**
	 * The Vertex from which pipe diameters are calculated on separate subgraphs
	 */
	private static Vertex breakPoint2 = null;

	/**
	 * Calculate the total waterflow for a part of the pipeGraph where there are not
	 * branches.
	 * 
	 * @param pg             PipeGraph whose pipe diameters are being calculated
	 * @param startingVertex starting Vertex of the subgraph
	 * @param nextVertex     child Vertex of the subgraph
	 * @return the total waterflow on this subgraph in l/min
	 */
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

	/**
	 * Calculate the total waterflow for a part of the pipeGraph where there are not
	 * branches.
	 * 
	 * @param pg             PipeGraph whose pipe diameters are being calculated
	 * @param startingVertex starting Vertex of the subgraph
	 * @param child          child Vertex of the subgraph
	 * @return the total waterflow on this subgraph
	 */
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
}