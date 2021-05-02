package application;

import java.util.Stack;

import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.util.Pair;
import model.bean.SprinklerShape;

public class UndoManager {

	private Stack<Pair<DrawingAction, Object>> modifications = new Stack<Pair<DrawingAction, Object>>();
	private Stack<Pair<DrawingAction, Object>> undone = new Stack<Pair<DrawingAction, Object>>();

	private final CanvasPane canvasPane;

	private static UndoManager singleton = null;
	
	private UndoManager(CanvasPane canvasPane) {
		this.canvasPane = canvasPane;
	}
	
	public static UndoManager init(CanvasPane canvasPane) {
		if (singleton != null) {
			throw new AssertionError("Instance already initialized");
		}
		singleton = new UndoManager(canvasPane);
		return singleton;
	}

	public static UndoManager getInstance() 
    { 
        if (singleton == null) {
        	throw new AssertionError("Init has to be called first");
        }
        return singleton; 
    } 
	
	public enum DrawingAction {
		SPRINKLER, BORDERLINE, OBSTACLE, TEXT, PIPELINE
	}

	public void draw(DrawingAction action, Object drawnObject) {
		try {
			Pair<DrawingAction, Object> modification = new Pair<DrawingAction, Object>(action, drawnObject);
			modifications.push(modification);
		} catch (Exception ex) {
			utilities.Error.HandleException(ex);
		}
	}

	public void undo() {
		try {
			if (modifications.isEmpty()) {
				return;
			}
			Pair<DrawingAction, Object> modification = modifications.pop();
			DrawingAction action = modification.getKey();

			switch (action) {
			case SPRINKLER:
				SprinklerShape sprinkler = (SprinklerShape) modification.getValue();
				canvasPane.getSprinklerArcLayer().getChildren().remove(sprinkler.getArc());
				canvasPane.getIrrigationLayer().getChildren().remove(sprinkler.getCircle());
				canvasPane.getSprinklerTextLayer().getChildren().remove(sprinkler.getLabel());
				canvasPane.controller.deleteSprinklerShape(sprinkler);
				break;
			case BORDERLINE:
				Line borderLine = (Line) modification.getValue();
				canvasPane.controller.removeBorderShape(borderLine);
				canvasPane.getBordersLayer().getChildren().remove(borderLine);
				break;
			case OBSTACLE:
				Shape shape = (Shape) modification.getValue();
				canvasPane.controller.removeObstacle(shape);
				canvasPane.controller.removeBorderShape(shape);
				canvasPane.getBordersLayer().getChildren().remove(shape);
				break;
			case TEXT:
				Text text = (Text) modification.getValue();
				canvasPane.controller.removeText(text);
				canvasPane.getTextLayer().getChildren().remove(text);
				break;
			case PIPELINE:
				// TODO
				break;
				
			}

			undone.push(modification);
			canvasPane.setDirty(true);
		} catch (Exception ex) {
			utilities.Error.HandleException(ex);
		}
	}

	public void redo() {
		try {
			if (undone.isEmpty()) {
				return;
			}
			Pair<DrawingAction, Object> modification = undone.pop();
			DrawingAction action = modification.getKey();

			switch (action) {
			case SPRINKLER:
				SprinklerShape sprinkler = (SprinklerShape) modification.getValue();
				canvasPane.getSprinklerArcLayer().getChildren().add(sprinkler.getArc());
				canvasPane.getIrrigationLayer().getChildren().add(sprinkler.getCircle());
				canvasPane.getSprinklerTextLayer().getChildren().add(sprinkler.getLabel());
				canvasPane.controller.addSprinklerShape(sprinkler);
				break;
			case BORDERLINE:
				Line borderLine = (Line) modification.getValue();
				canvasPane.controller.addBorderShape(borderLine);
				canvasPane.getBordersLayer().getChildren().add(borderLine);
				break;
			case OBSTACLE:
				Shape shape = (Shape) modification.getValue();
				canvasPane.controller.addObstacle(shape);
				canvasPane.controller.addBorderShape(shape);
				canvasPane.getBordersLayer().getChildren().add(shape);
				break;
			case TEXT:
				Text text = (Text) modification.getValue();
				canvasPane.controller.addText(text);
				canvasPane.getTextLayer().getChildren().add(text);
				break;
			case PIPELINE:
				// TODO
				break;
			}

			draw(action, modification.getValue());
			canvasPane.setDirty(true);
		} catch (Exception ex) {
			utilities.Error.HandleException(ex);
		}
	}
}
