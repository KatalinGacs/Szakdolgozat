package application;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import application.common.Common;
import controller.SprinklerController;
import controller.SprinklerControllerImpl;
import javafx.collections.FXCollections;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import model.bean.PipeGraph;
import model.bean.PipeGraph.Edge;
import model.bean.SprinklerShape;
import model.bean.Zone;

/**
 * A custom layout that extends javafx.scene.layout.Pane. This is the canvas on
 * which the user does the drawing.
 * 
 * @author Gacs Katalin
 *
 */
/**
 * @author Gacs Katalin
 *
 */
/**
 * @author Gacs Katalin
 *
 */
public class CanvasPane extends Pane {

	SprinklerController controller = new SprinklerControllerImpl();

	/**
	 * Represents different phases of drawing where the user interaction with the
	 * CanvasPane has different meanings.
	 * 
	 * @author Gacs Katalin
	 *
	 */
	public enum Use {
		NONE, BORDERDRAWING, SPRINKLERDRAWING, PREPAREFORDRAWINGSEVERALSPRINKLERS, ZONEEDITING, PREPAREFORPIPEDRAWING,
		PIPEDRAWING, TEXTWRITING, PREPAREFORTEXTEDITING
	}

	/**
	 * Current phase of drawing.
	 */
	private Use stateOfCanvasUse = Use.NONE;

	/**
	 * A stage that lists the sprinkler types and the user can choose which to draw.
	 */
	private SetSprinklerAttributesStage sprinklerAttributeStage;

	/**
	 * True if there is a sprinkler type selected and a radius set.
	 */
	private boolean sprinklerAttributesSet = false;

	/**
	 * True if the user is drawing several sprinklers in a line.
	 */
	private boolean drawingSeveralSprinklers = false;

	/**
	 * Stroke width of the sprinkler arcs.
	 */
	private static int strokeWidth = (int) (Common.pixelPerMeter / 12);

	/**
	 * Color of helper lines.
	 */
	private static Color tempLineColor = Color.DARKTURQUOISE;

	/**
	 * Color of the pipe that is currently edited. At beginning set to to red.
	 */
	private static Color pipeLineColor = Color.RED;

	/**
	 * Highlight color of selected shapes.
	 */
	private Color selectionColor = Color.CYAN;

	/**
	 * Layer that contains border lines and circle and rectangle obstacles.
	 */
	private Group bordersLayer = new Group();

	/**
	 * Layer that contains the sprinklerheads (only the circles).
	 */
	private Group irrigationLayer = new Group();

	/**
	 * Layer that contains the sprinklerarcs without the heads.
	 */
	private Group sprinklerArcLayer = new Group();

	/**
	 * Layer that contains the grid.
	 */
	private Group gridLayer = new Group();

	/**
	 * Layer that contains helper lines and shapes.
	 */
	private Group tempLineLayer = new Group();

	/**
	 * Layer that contains pipe lines.
	 */
	private Group pipeLineLayer = new Group();

	/**
	 * Layer that contains the texts showing the type of sprinklers.
	 */
	private Group sprinklerTextLayer = new Group();

	/**
	 * Layer that contains the texts showing the diameter of pipes.
	 */
	private Group pipeTextLayer = new Group();

	/**
	 * Layer that contains texts that the user puts on the CanvasPane.
	 */
	private Group textLayer = new Group();

	/**
	 * Helper circle showing where a snap-to-grid would lead.
	 */
	static private Circle focusCircle = new Circle(Common.pixelPerMeter / 3);

	/**
	 * True if focusCircle is shown.
	 */
	boolean showingFocusCircle = false;

	/**
	 * The key held down during different user interactions.
	 */
	private KeyCode pressedKey;

	/**
	 * Invisible line that helps counting water coverage in a point.
	 */
	private Line measuringIntersectionsLine = new Line();

	/**
	 * True if the mouse is near a line end. Used in line drawing to connect lines.
	 */
	boolean cursorNearLineEnd = false;

	/**
	 * True if the mouse is near a sprinklerhead. Used in pipe drawing to connect.
	 * sprinklerheads to pipes.
	 */
	boolean cursorNearSprinklerHead = false;

	/**
	 * The X position of the sprinklerhead when cursorNearSprinklerHead is true.
	 */
	static double sprinklerHeadX;

	/**
	 * The Y position of the sprinklerhead when cursorNearSprinklerHead is true.
	 */
	static double sprinklerHeadY;

	/**
	 * The SprinklerShape to which the mouse is close to when
	 * cursorNearSprinklerHead is true.
	 */
	SprinklerShape sprinklerShapeNearCursor;

	/**
	 * True if the mouse is moved over a pipeline.
	 */
	boolean cursorOnPipeLine = false;

	/**
	 * True if the user is drawing several sprinklers in a line and there is a line
	 * selected for this.
	 */
	boolean lineSelected = false;

	/**
	 * The selected line when the user is drawing several sprinklers in a line.
	 */
	Line selectedLine;

	/**
	 * Helper circles showing the sprinklerheads positions when the user is drawing
	 * several sprinklers in a line.
	 */
	List<Circle> tempSprinklerCirclesInALine = new ArrayList<>();

	/**
	 * Helper circles showing the arc positions when the user is drawing several
	 * sprinklers in a line.
	 */
	List<Circle> tempSprinklerCentersInALine = new ArrayList<>();

	/**
	 * In zone editing the selected sprinklers for the zone to be created.
	 */
	Set<SprinklerShape> selectedSprinklerShapes = new HashSet<>();

	/**
	 * In zone editing the sum of flow rate of the selected sprinklers for the zone.
	 * to be created.
	 */
	double flowRateOfSelected = 0;

	/**
	 * A shape selected be right clicking it. Used to show a contextmenu by it.
	 */
	private Shape selectedShape = null;

	/**
	 * If a shape is selected, its stroke color is changed to show what is selected.
	 * The original color is saved here to be able to restore after the selection is
	 * done.
	 */
	private Paint originalStrokeColorOfSelectedShape = null;

	/**
	 * The PipeGraph whose pipes are currently drawn.
	 */
	PipeGraph pipeGraphUnderEditing;

	/**
	 * Context menu for any selected item.
	 */
	private ContextMenu rightClickMenu = new ContextMenu();

	/**
	 * Menu item of the rightClickMenu. Choosing it deletes the selected shape from
	 * the CanvasPane.
	 */
	private MenuItem delMenuItem = new MenuItem("Törlés");

	/**
	 * True if there was any change on the CanvasPane since last saving it in a
	 * file. Used to confirm before starting a new drawing, closing or opening a
	 * drawing.
	 */
	private boolean modifiedSinceLastSave = false;

	/**
	 * Create the CanvasPane. Add the layers and other child items to it. Draw a
	 * grid on it. Set the helper shapes, input fields etc. to be invisible.
	 */
	public CanvasPane() {

		setWidth(Common.canvasWidth);
		setHeight(Common.canvasHeight);

		// Grid - horizontal lines
		for (int i = 0; i < (int) getHeight(); i += Common.pixelPerMeter) {

			Line line = new Line(0, i, getWidth(), i);
			line.setStroke(Color.SILVER);
			getChildren().add(line);
			gridLayer.getChildren().add(line);
		}
		// Grid - vertical lines
		for (int i = 0; i < (int) getWidth(); i += Common.pixelPerMeter) {
			Line line = new Line(i, 0, i, getHeight());
			line.setStroke(Color.SILVER);
			getChildren().add(line);
			gridLayer.getChildren().add(line);
		}

		tempLineLayer.getChildren().addAll(SprinklerDrawing.tempFirstSprinklerLine,
				SprinklerDrawing.tempSecondSprinklerLine, SprinklerDrawing.tempSprinklerCircle,
				BorderDrawing.tempBorderLine, BorderDrawing.tempCircle, BorderDrawing.tempRectangle, focusCircle,
				measuringIntersectionsLine);

		SprinklerDrawing.tempFirstSprinklerLine.setVisible(false);
		SprinklerDrawing.tempSprinklerCircle.setVisible(false);
		BorderDrawing.tempRectangle.setVisible(false);
		BorderDrawing.tempCircle.setVisible(false);

		focusCircle.setVisible(false);
		focusCircle.setStroke(tempLineColor);
		focusCircle.setStrokeWidth(strokeWidth);
		focusCircle.setFill(Color.TRANSPARENT);

		SprinklerDrawing.angleInput.setVisible(false);
		SprinklerDrawing.angleInput.setMaxWidth(70);
		SprinklerDrawing.angleInput.setFont(Font.font(20));
		SprinklerDrawing.angleInput.setPromptText("Szög");

		BorderDrawing.lengthInput.setVisible(false);
		BorderDrawing.lengthInput.setMaxWidth(130);
		BorderDrawing.lengthInput.setFont(Font.font(20));
		BorderDrawing.lengthInput.setPromptText("Hossz (m)");

		TextEditing.textField.setVisible(false);

		rightClickMenu.getItems().add(delMenuItem);

		getChildren().addAll(bordersLayer, sprinklerArcLayer, irrigationLayer, gridLayer, tempLineLayer, pipeLineLayer,
				sprinklerTextLayer, pipeTextLayer, textLayer, SprinklerDrawing.angleInput, BorderDrawing.lengthInput,
				TextEditing.textField);
	}

	/**
	 * Selects a shape on the CanvasPane that was right clicked and shows a context
	 * menu for it. The selected shape can be deleted via the contextmenu
	 * 
	 * @param e MouseEvent where the canvasPane was right clicked
	 */
	public void selectElement(MouseEvent e) {

		// check if a sprinklerhead was right clicked
		for (SprinklerShape s : controller.listSprinklerShapes()) {
			if (s.getCircle().contains(e.getX(), e.getY())) {
				selectedShape = s.getCircle();
				rightClickMenu.show(s.getCircle(), Side.RIGHT, 5, 5);
				delMenuItem.setOnAction(ev -> {
					controller.deleteSprinklerShape(s);
					irrigationLayer.getChildren().remove(s.getCircle());
					sprinklerArcLayer.getChildren().remove(s.getArc());
					sprinklerTextLayer.getChildren().remove(s.getLabel());
					modifiedSinceLastSave = true;
					ev.consume();
				});
			}
		}
		// check if a border line or obstacle was right clicked
		for (Shape border : controller.listBorderShapes()) {
			if (border.contains(e.getX(), e.getY())) {
				rightClickMenu.show(border, e.getScreenX(), e.getScreenY());
				selectedShape = border;
				delMenuItem.setOnAction(ev -> {
					controller.removeBorderShape(border);
					bordersLayer.getChildren().remove(border);
					if (controller.listObstacles().contains(border))
						controller.removeObstacle(border);
					BorderDrawing.tempBorderLine.setVisible(false);
					BorderDrawing.tempRectangle.setVisible(false);
					BorderDrawing.tempCircle.setVisible(false);
					ev.consume();
				});
			}
		}

		// set the stroke color of the selected shape to highlight it and save the
		// original color
		originalStrokeColorOfSelectedShape = selectedShape.getStroke();
		selectedShape.setStroke(selectionColor);

	}

	/**
	 * End the drawing of a polyline. Set the stateOfCanvasUse to
	 * PREPAREFORPIPEDRAWING if a pipeline was drawn, to NONE if a borderline was
	 * drawn
	 */
	public void endLineDrawing() {
		BorderDrawing.lengthInput.setVisible(false);
		BorderDrawing.tempBorderLine.setVisible(false);
		if (stateOfCanvasUse == Use.PIPEDRAWING)
			stateOfCanvasUse = Use.PREPAREFORPIPEDRAWING;

		else
			stateOfCanvasUse = Use.NONE;
	}

	/**
	 * Show a helper circle for snap-to-grid when Control is pressed down to
	 * indicate during SprinklerDrawing where the sprinklerhead would be.
	 * 
	 * @param e MouseEvent, mouse moved
	 */
	public void showFocusCircle(MouseEvent e) {
		if (pressedKey != null && pressedKey.equals(KeyCode.CONTROL) && sprinklerAttributesSet
				&& stateOfCanvasUse == Use.SPRINKLERDRAWING
				&& SprinklerDrawing.drawingState == SprinklerDrawing.SprinklerDrawingState.CENTER) {
			if ((e.getX() % Common.pixelPerMeter < Common.pixelPerMeter / 4
					|| e.getX() % Common.pixelPerMeter > Common.pixelPerMeter * 3 / 4)
					&& (e.getY() % Common.pixelPerMeter < Common.pixelPerMeter / 4
							|| e.getY() % Common.pixelPerMeter > Common.pixelPerMeter * 3 / 4)) {

				setCursor(Cursor.CROSSHAIR);
				Point2D focusCenter = Common.snapToGrid(e.getX(), e.getY());
				focusCircle.setCenterX(focusCenter.getX());
				focusCircle.setCenterY(focusCenter.getY());
				focusCircle.setVisible(true);
				showingFocusCircle = true;
			} else {
				focusCircle.setVisible(false);
				showingFocusCircle = false;
			}
		} else {
			focusCircle.setVisible(false);
			showingFocusCircle = false;
		}
	}

	/**
	 * Show a stage that lists the sprinkler types and the user can choose which to
	 * draw.
	 */
	public void setSprinklerAttributes() {
		sprinklerAttributeStage = new SetSprinklerAttributesStage(this);
		sprinklerAttributeStage.show();
	}

	/**
	 * Select or delete from selection sprinklerheads for a zone to be created. Can
	 * be done by selecting an area on the CanvasPane and selecting each head within
	 * the area or by selecting individual heads.
	 * 
	 * @param e                MouseEvent, clicked when selecting individual heads,
	 *                         released when selecting an area
	 * @param adding           true if the selection adds heads to the zone, false
	 *                         if deletes
	 * @param selectIndividual true if individual heads are selected for the zone,
	 *                         false if an area is selected
	 */
	public void selectHeadsForZone(MouseEvent e, boolean adding, boolean selectIndividual) {
		if (stateOfCanvasUse == Use.ZONEEDITING) {
			for (SprinklerShape s : controller.listSprinklerShapes()) {
				boolean selected = selectIndividual ? s.getCircle().contains(e.getX(), e.getY())
						: BorderDrawing.tempRectangle.intersects(s.getCircle().getBoundsInLocal());
				if (selected) {
					if (adding) {
						s.getCircle().setFill(selectionColor);
						selectedSprinklerShapes.add(s);
						flowRateOfSelected = 0;
						for (SprinklerShape sh : selectedSprinklerShapes)
							flowRateOfSelected += sh.getFlowRate();
						for (Zone zone : controller.listZones()) {
							for (SprinklerShape sInZone : zone.getSprinklers()) {
								if (s == sInZone) {
									s.getCircle().setFill(SprinklerDrawing.sprinklerColor);
									selectedSprinklerShapes.remove(s);
									flowRateOfSelected = 0;
									for (SprinklerShape sh : selectedSprinklerShapes)
										flowRateOfSelected += sh.getFlowRate();
								}
							}
						}

					} else {
						s.getCircle().setFill(SprinklerDrawing.sprinklerColor);
						selectedSprinklerShapes.remove(s);
						flowRateOfSelected = 0;
						for (SprinklerShape sh : selectedSprinklerShapes)
							flowRateOfSelected += sh.getFlowRate();
					}
				}
			}
			BorderDrawing.tempRectangle.setVisible(false);
		}
	}

	/**
	 * Create a zone, save its attributes
	 * 
	 * @param name            Name of the zone, set by the user
	 * @param durationInHours duration of watering (hours per day), set by the user
	 */
	public void createZone(String name, double durationInHours) {
		Zone zone = new Zone();
		zone.setName(name);
		zone.setSprinklers(FXCollections.observableArrayList(selectedSprinklerShapes));
		zone.setDurationOfWatering(durationInHours);
		controller.addZone(zone);
		deselectAll();
		modifiedSinceLastSave = true;
	}

	/**
	 * Deselect every selected shape
	 */
	public void deselectAll() {
		for (SprinklerShape s : selectedSprinklerShapes) {
			s.getCircle().setFill(SprinklerDrawing.sprinklerColor);
		}
		selectedSprinklerShapes.clear();
	}

	/**
	 * The type and the attributes currently set in sprinklerdrawing
	 * 
	 * @return A string with the type and the radius currently set in
	 *         sprinklerdrawing
	 */
	public String sprinklerInfos() {
		return "Kiválasztott szórófej: " + SprinklerDrawing.sprinklerType + " "
				+ SprinklerDrawing.sprinklerRadius / Common.pixelPerMeter + " m";
	}

	/**
	 * General informations for when the mouse is moved over the canvasPane.
	 * Contains information about the scale of the drawing, the (X,Y) position of
	 * the mouse and of the water coverage in the position.
	 * 
	 * @param e MouseEvent, mouse moved, to get current position of the mouse over
	 *          the CanvasPane
	 * @return A string containing information about the scale of the drawing, the
	 *         (X,Y) position of the mouse and of the water coverage in the
	 *         position.
	 */
	public String generalInfos(MouseEvent e) {

		double sumOfWaterCoverageInMmPerHour = 0;
		for (SprinklerShape s : controller.listSprinklerShapes()) {
			boolean counts = false;
			if (s.getArc().contains(e.getX(), e.getY())) {
				measuringIntersectionsLine.setStartX(s.getCircle().getCenterX());
				measuringIntersectionsLine.setStartY(s.getCircle().getCenterY());
				measuringIntersectionsLine.setEndX(e.getX());
				measuringIntersectionsLine.setEndY(e.getY());
				measuringIntersectionsLine.setStroke(Color.TRANSPARENT);
				List<Shape> intersects = new ArrayList<>();
				if (!controller.listObstacles().isEmpty()) {
					for (Shape obstacle : controller.listObstacles()) {
						if (Shape.intersect(s.getArc(), obstacle).getBoundsInLocal().getMinX() != 0) {
							intersects.add(Shape.intersect(s.getArc(), obstacle));
						}
					}
				}
				if (!intersects.isEmpty()) {
					for (Shape obstacle : intersects) {
						Shape intersect = Shape.intersect(measuringIntersectionsLine, obstacle);
						if (intersect.getBoundsInLocal().getMinX() != 0
								|| intersect.getBoundsInLocal().getMinY() != 0) {
							counts = false;
							break;

						} else
							counts = true;
					}
				} else {
					counts = true;
				}
			}
			if (counts)
				sumOfWaterCoverageInMmPerHour += s.getWaterCoverageInMmPerHour();
		}
		return ("Egy négyzetrács mérete: 1x1 m \r\n" + "Csapadék: "
				+ String.format("%.2f", sumOfWaterCoverageInMmPerHour) + " mm/óra" + "\r\n" + "X: "
				+ String.format("%10.2f", e.getX()) + " Y: " + String.format("%10.2f", e.getY()));
	}

	/**
	 * Clear everything that was drawn on the CanvasPane.
	 */
	public void clear() {
		bordersLayer.getChildren().clear();
		irrigationLayer.getChildren().clear();
		pipeLineLayer.getChildren().clear();
		pipeTextLayer.getChildren().clear();
		sprinklerArcLayer.getChildren().clear();
		sprinklerTextLayer.getChildren().clear();
		textLayer.getChildren().clear();
		controller.clearAll();
	}

	/**
	 * Hide every helper shape in the tempLineLayer.
	 */
	public void hideTempLayer() {
		for (Node n : tempLineLayer.getChildren()) {
			n.setVisible(false);
		}
	}

	/**
	 * Delete the lines of a PipeGraph of a given Zone from the CanvasPane
	 * 
	 * @param zone the Zone whose pipelines are to be deleted.
	 */
	public void deletePipes(Zone zone) {
		PipeGraph pg = controller.getPipeGraph(zone);
		for (SprinklerShape s : zone.getSprinklers()) {
			s.setConnectedToPipe(false);
		}
		for (Edge e : pg.getEdges()) {
			pipeLineLayer.getChildren().remove(e);
		}
		pipeLineLayer.getChildren().remove(pg.getValve());
		for (Text t : pg.getPipeTextes()) {
			pipeTextLayer.getChildren().remove(t);
		}
	}

	public Set<SprinklerShape> getSelectedSprinklerShapes() {
		return selectedSprinklerShapes;
	}

	public SetSprinklerAttributesStage getSprinklerAttributeStage() {
		return sprinklerAttributeStage;
	}

	public Use getStateOfCanvasUse() {
		return stateOfCanvasUse;
	}

	public void setStateOfCanvasUse(Use stateOfCanvasUse) {
		this.stateOfCanvasUse = stateOfCanvasUse;
	}

	public boolean isModifiedSinceLastSave() {
		return modifiedSinceLastSave;
	}

	public void setModifiedSinceLastSave(boolean modifiedSinceLastSave) {
		this.modifiedSinceLastSave = modifiedSinceLastSave;
	}

	public Shape getSelectedShape() {
		return selectedShape;
	}

	public void setSelectedShape(Shape selectedShape) {
		this.selectedShape = selectedShape;
	}

	public Paint getOriginalStrokeColorOfSelectedShape() {
		return originalStrokeColorOfSelectedShape;
	}

	public boolean isSprinklerAttributesSet() {
		return sprinklerAttributesSet;
	}

	public void setSprinklerAttributesSet(boolean sprinklerAttributesSet) {
		this.sprinklerAttributesSet = sprinklerAttributesSet;
	}

	public boolean isDrawingSeveralSprinklers() {
		return drawingSeveralSprinklers;
	}

	public void setDrawingSeveralSprinklers(boolean drawingSeveralSprinklers) {
		this.drawingSeveralSprinklers = drawingSeveralSprinklers;
	}

	public static int getStrokeWidth() {
		return strokeWidth;
	}

	public static void setStrokeWidth(int strokeWidth) {
		CanvasPane.strokeWidth = strokeWidth;
	}

	public SprinklerController getController() {
		return controller;
	}

	public void setController(SprinklerController controller) {
		this.controller = controller;
	}

	public static Color getTempLineColor() {
		return tempLineColor;
	}

	public static void setTempLineColor(Color tempLineColor) {
		CanvasPane.tempLineColor = tempLineColor;
	}

	public static Color getPipeLineColor() {
		return pipeLineColor;
	}

	public static void setPipeLineColor(Color pipeLineColor) {
		CanvasPane.pipeLineColor = pipeLineColor;
	}

	public Color getSelectionColor() {
		return selectionColor;
	}

	public void setSelectionColor(Color selectionColor) {
		this.selectionColor = selectionColor;
	}

	public Group getBordersLayer() {
		return bordersLayer;
	}

	public void setBordersLayer(Group bordersLayer) {
		this.bordersLayer = bordersLayer;
	}

	public Group getIrrigationLayer() {
		return irrigationLayer;
	}

	public void setIrrigationLayer(Group irrigationLayer) {
		this.irrigationLayer = irrigationLayer;
	}

	public Group getSprinklerArcLayer() {
		return sprinklerArcLayer;
	}

	public void setSprinklerArcLayer(Group sprinklerArcLayer) {
		this.sprinklerArcLayer = sprinklerArcLayer;
	}

	public Group getGridLayer() {
		return gridLayer;
	}

	public void setGridLayer(Group gridLayer) {
		this.gridLayer = gridLayer;
	}

	public Group getTempLineLayer() {
		return tempLineLayer;
	}

	public void setTempLineLayer(Group tempLineLayer) {
		this.tempLineLayer = tempLineLayer;
	}

	public Group getPipeLineLayer() {
		return pipeLineLayer;
	}

	public void setPipeLineLayer(Group pipeLineLayer) {
		this.pipeLineLayer = pipeLineLayer;
	}

	public Group getSprinklerTextLayer() {
		return sprinklerTextLayer;
	}

	public void setSprinklerTextLayer(Group sprinklerTextLayer) {
		this.sprinklerTextLayer = sprinklerTextLayer;
	}

	public Group getPipeTextLayer() {
		return pipeTextLayer;
	}

	public void setPipeTextLayer(Group pipeTextLayer) {
		this.pipeTextLayer = pipeTextLayer;
	}

	public Group getTextLayer() {
		return textLayer;
	}

	public void setTextLayer(Group textLayer) {
		this.textLayer = textLayer;
	}

	public KeyCode getPressedKey() {
		return pressedKey;
	}

	public void setPressedKey(KeyCode pressedKey) {
		this.pressedKey = pressedKey;
	}

}
