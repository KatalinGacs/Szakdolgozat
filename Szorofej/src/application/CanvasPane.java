package application;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import application.common.Common;
import application.common.DecimalCellFactory;
import controller.SprinklerController;
import controller.SprinklerControllerImpl;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.bean.PipeGraph;
import model.bean.SprinklerGroup;
import model.bean.SprinklerShape;
import model.bean.Zone;

//TODO t�l nagyra n�tt oszt�ly, sz�tszedni?...
public class CanvasPane extends Pane {

	SprinklerController controller = new SprinklerControllerImpl();

	protected KeyCode pressedKey;

	public enum Use {
		NONE, BORDERDRAWING, SPRINKLERDRAWING, PREPAREFORDRAWINGSEVERALSPRINKLERS, ZONEEDITING, PREPAREFORPIPEDRAWING,
		PIPEDRAWING
	}

	protected Use stateOfCanvasUse = Use.NONE;
	protected boolean sprinklerAttributesSet = false;
	protected boolean drawingSeveralSprinklers = false;

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
	Group textLayer = new Group();

	static private Circle focusCircle = new Circle(Common.pixelPerMeter / 3);
	boolean showingFocusCircle = false;
	private Line measuringIntersectionsLine = new Line();

	protected boolean cursorNearLineEnd = false;
	protected double lineEndX;
	protected double lineEndY;

	protected boolean cursorNearSprinklerHead = false;
	protected static double sprinklerHeadX;
	protected static double sprinklerHeadY;
	SprinklerShape sprinklerShapeNearCursor;

	protected boolean lineSelected = false;
	protected int indexOfSelectedLine;
	List<Circle> tempSprinklerCirclesInALine = new ArrayList<>();
	List<Circle> tempSprinklerCentersInALine = new ArrayList<>();

	protected Set<SprinklerShape> selectedSprinklerShapes = new HashSet<>();
	protected double flowRateOfSelected = 0;

	PipeGraph pipeGraph;
	// TODO ezeket is modelben elt�rolni, azon kereszt�l el�rni
	protected List<Shape> borderShapes = new ArrayList<Shape>();
	protected List<Shape> obstacles = new ArrayList<>();

	private ContextMenu rightClickMenu = new ContextMenu();
	private MenuItem delMenuItem = new MenuItem("T�rl�s");

	public CanvasPane() {

		setWidth(Common.primaryScreenBounds.getWidth() * 6);
		setHeight(Common.primaryScreenBounds.getHeight() * 6);

		// Grid
		// TODO: jav�tani azt, hogy ha k�s�bb a megl�v� ter�leten k�v�lre rajzolok, azon
		// m�r nem lesz rajta ez a r�cs
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
		SprinklerDrawing.angleInput.setPromptText("Sz�g");

		BorderDrawing.lengthInput.setVisible(false);
		BorderDrawing.lengthInput.setMaxWidth(130);
		BorderDrawing.lengthInput.setFont(Font.font(20));
		BorderDrawing.lengthInput.setPromptText("Hossz (m)");

		rightClickMenu.getItems().add(delMenuItem);

		getChildren().addAll(bordersLayer, sprinklerArcLayer, irrigationLayer, gridLayer, tempLineLayer, pipeLineLayer,
				textLayer, SprinklerDrawing.angleInput, BorderDrawing.lengthInput);

	}

	// TODO most ha t�bb element van egym�son, nem tudja a felhaszn�l�, melyiket
	// fogja t�r�lni
	// delmenu sorolja fel �ket? vagy a kijel�lt elem sz�ne v�ltozzon �s akkor tudja
	// mint jel�lt ki?
	protected void selectElement(MouseEvent e) {
		for (SprinklerShape s : controller.listSprinklerShapes()) {
			if (s.getCircle().contains(e.getX(), e.getY())) {
				rightClickMenu.show(s.getCircle(), Side.RIGHT, 5, 5);
				delMenuItem.setOnAction(ev -> {
					controller.deleteSprinklerShape(s);
					irrigationLayer.getChildren().remove(s.getCircle());
					sprinklerArcLayer.getChildren().remove(s.getArc());
					ev.consume();
				});
			}
		}
		for (Shape border : borderShapes) {
			if (border.contains(e.getX(), e.getY())) {
				rightClickMenu.show(border, e.getScreenX(), e.getScreenY());
				delMenuItem.setOnAction(ev -> {
					borderShapes.remove(border);
					bordersLayer.getChildren().remove(border);
					if (obstacles.contains(border))
						obstacles.remove(border);
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

	public void createZone(String name, double durationInHours) {
		Zone zone = new Zone();
		zone.setName(name);
		zone.setSprinklers(FXCollections.observableArrayList(selectedSprinklerShapes));
		zone.setDurationOfWatering(durationInHours);
		controller.addZone(zone);
		for (SprinklerShape s : zone.getSprinklers()) {
			s.getArc().setFill(Color.LAWNGREEN);
			// TODO ha mgoldhat�, csak a fill opacityje v�ltozzon, a stroke� nem, nem biztos
			// hogy lehet
			// vagy nem opacityt �ll�tgatni hanem hogy mennyire vil�gos z�ld? vagy
			// z�ld-s�rga sz�nek k�zt valami
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
			if (s.getArc().contains(e.getX(), e.getY())) {
				measuringIntersectionsLine.setStartX(s.getCircle().getCenterX());
				measuringIntersectionsLine.setStartY(s.getCircle().getCenterY());
				measuringIntersectionsLine.setEndX(e.getX());
				measuringIntersectionsLine.setEndY(e.getY());
				measuringIntersectionsLine.setStroke(Color.TRANSPARENT);
				for (Shape obstacle : obstacles) {
					Shape intersect = Shape.intersect(measuringIntersectionsLine, obstacle);
					if (intersect.getBoundsInLocal().getMinX() == 0 && intersect.getBoundsInLocal().getMinY() == 0) {
						sumOfWaterCoverageInMmPerHour += s.getWaterCoverageInMmPerHour();
					}
				}
				if (obstacles.isEmpty())
					sumOfWaterCoverageInMmPerHour += s.getWaterCoverageInMmPerHour();
			}
		}
		return ("Egy n�gyzetr�cs m�rete: 1x1 m \r\n" + "Csapad�k: "
				+ String.format("%.2f", sumOfWaterCoverageInMmPerHour) + " mm/�ra" + "\r\n" + "X: "
				+ String.format("%10.2f", e.getX()) + " Y: " + String.format("%10.2f", e.getY()));
	}

}
