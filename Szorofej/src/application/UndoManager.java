package application;

import java.util.Stack;

import javafx.scene.shape.Shape;
import javafx.util.Pair;
import model.bean.SprinklerShape;

public class UndoManager {

	// TODO singletont csinálni belõle

	private Stack<Pair<DrawingAction, Object>> modifications = new Stack<Pair<DrawingAction, Object>>();
	private Stack<Pair<DrawingAction, Object>> undone = new Stack<Pair<DrawingAction, Object>>();

	private CanvasPane canvasPane;

	public UndoManager(CanvasPane canvasPane) {
		this.canvasPane = canvasPane;
	}

	public enum DrawingAction {
		SPRINKLER, BORDERLINE, OBSTACLE, TEXT
	}

	public void draw(DrawingAction action, Object drawnObject) {
		Pair<DrawingAction, Object> modification = new Pair<DrawingAction, Object>(action, drawnObject);
		modifications.push(modification);
	}

	public void undo() {
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
			break;
		case BORDERLINE:
			// TODO
			break;
		case OBSTACLE:
			// TODO
			break;
		case TEXT:
			// TODO
			break;
		}

		undone.push(modification);
		canvasPane.setDirty(true);

	}

	public void redo() {
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
			break;
		case BORDERLINE:
			// TODO
			break;
		case OBSTACLE:
			// TODO
			break;
		case TEXT:
			// TODO
			break;
		}

		draw(action, modification.getValue());
		canvasPane.setDirty(true);
	}

}
