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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import model.bean.PipeGraph;
import model.bean.SprinklerShape;
import model.bean.Zone;

public class CanvasPane extends Pane {

	SprinklerController controller = new SprinklerControllerImpl();

	KeyCode pressedKey;

	public enum Use {
		NONE, BORDERDRAWING, SPRINKLERDRAWING, PREPAREFORDRAWINGSEVERALSPRINKLERS, ZONEEDITING, PREPAREFORPIPEDRAWING,
		PIPEDRAWING, TEXTWRITING, PREPAREFORTEXTEDITING
	}

	Use stateOfCanvasUse = Use.NONE;
	boolean sprinklerAttributesSet = false;
	boolean drawingSeveralSprinklers = false;

	static int strokeWidth = (int) (Common.pixelPerMeter / 12);

	static Color tempLineColor = Color.DARKTURQUOISE;

	Color selectionColor = Color.CYAN;
	static Color pipeLineColor = Color.RED;

	Group bordersLayer = new Group();
	Group irrigationLayer = new Group();
	Group sprinklerArcLayer = new Group();
	Group gridLayer = new Group();
	Group tempLineLayer = new Group();
	Group pipeLineLayer = new Group();
	Group sprinklerTextLayer = new Group();
	Group pipeTextLayer = new Group();
	Group textLayer = new Group();
	
	static private Circle focusCircle = new Circle(Common.pixelPerMeter / 3);
	boolean showingFocusCircle = false;
	private Line measuringIntersectionsLine = new Line();

	boolean cursorNearLineEnd = false;
	double lineEndX, lineEndY;

	boolean cursorNearSprinklerHead = false;
	static double sprinklerHeadX, sprinklerHeadY;
	SprinklerShape sprinklerShapeNearCursor;

	boolean cursorOnPipeLine = false;
	boolean lineSelected = false;
	int indexOfSelectedLine;
	List<Circle> tempSprinklerCirclesInALine = new ArrayList<>();
	List<Circle> tempSprinklerCentersInALine = new ArrayList<>();

	Set<SprinklerShape> selectedSprinklerShapes = new HashSet<>();
	double flowRateOfSelected = 0;

	PipeGraph pipeGraphUnderEditing;

	private ContextMenu rightClickMenu = new ContextMenu();
	private MenuItem delMenuItem = new MenuItem("Törlés");

	public CanvasPane() {

		setWidth(Common.primaryScreenBounds.getWidth() * 6);
		setHeight(Common.primaryScreenBounds.getHeight() * 6);

		// Grid
		// TODO: javítani azt, hogy ha késõbb a meglévõ területen kívülre rajzolok, azon
		// már nem lesz rajta ez a rács
		for (int i = 0; i < (int) getWidth(); i += Common.pixelPerMeter) {
			Line line = new Line(0, i, getHeight(), i);
			line.setStroke(Color.SILVER);
			getChildren().add(line);
			gridLayer.getChildren().add(line);
		}
		for (int i = 0; i < (int) getHeight(); i += Common.pixelPerMeter) {
			Line line = new Line(i, 0, i, getWidth());
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
				sprinklerTextLayer, pipeTextLayer, textLayer, SprinklerDrawing.angleInput, BorderDrawing.lengthInput, TextEditing.textField);

	}

	// TODO most ha több element van egymáson, nem tudja a felhasználó, melyiket
	// fogja törölni
	// delmenu sorolja fel õket? vagy a kijelölt elem színe változzon és akkor tudja
	// mit jelölt ki?
	protected void selectElement(MouseEvent e) {
		for (SprinklerShape s : controller.listSprinklerShapes()) {
			if (s.getCircle().contains(e.getX(), e.getY())) {
				rightClickMenu.show(s.getCircle(), Side.RIGHT, 5, 5);
				delMenuItem.setOnAction(ev -> {
					controller.deleteSprinklerShape(s);
					irrigationLayer.getChildren().remove(s.getCircle());
					sprinklerArcLayer.getChildren().remove(s.getArc());
					sprinklerTextLayer.getChildren().remove(s.getLabel());
					ev.consume();
				});
			}
		}
		for (Shape border : controller.listBorderShapes()) {
			if (border.contains(e.getX(), e.getY())) {
				rightClickMenu.show(border, e.getScreenX(), e.getScreenY());
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
	}

	public void endLineDrawing() {
		BorderDrawing.lengthInput.setVisible(false);
		BorderDrawing.tempBorderLine.setVisible(false);
		if (stateOfCanvasUse == Use.PIPEDRAWING)
			stateOfCanvasUse = Use.PREPAREFORPIPEDRAWING;

		else
			stateOfCanvasUse = Use.NONE;
	}

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

	protected void setSprinklerAttributes() {
		SetSprinklerAttributesStage stage = new SetSprinklerAttributesStage(this);
		stage.show();
	}

	protected void selectHeadsForZone(MouseEvent e, boolean adding, boolean selectIndividual) {
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

	public void createZone(String name, double durationInHours) {
		Zone zone = new Zone();
		zone.setName(name);
		zone.setSprinklers(FXCollections.observableArrayList(selectedSprinklerShapes));
		zone.setDurationOfWatering(durationInHours);
		controller.addZone(zone);
		for (SprinklerShape s : zone.getSprinklers()) {
			s.getArc().setFill(Color.LAWNGREEN);
			// TODO ha mgoldható, csak a fill opacityje változzon, a strokeé nem, nem biztos
			// hogy lehet
			// vagy nem opacityt állítgatni hanem hogy mennyire világos zöld? vagy
			// zöld-sárga színek közt valami
			s.getArc().setOpacity((s.getFlowRate() * durationInHours) / 3);
		}
		deselectAll();
	}

	public void deselectAll() {
		for (SprinklerShape s : selectedSprinklerShapes) {
			s.getCircle().setFill(SprinklerDrawing.sprinklerColor);
		}
		selectedSprinklerShapes.clear();
	}
	

	public String showInfos(MouseEvent e) {

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

						}
						else counts = true;
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


}
