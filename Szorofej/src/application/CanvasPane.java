package application;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class CanvasPane extends Pane {

	public CanvasPane() {

		setWidth(1500);
		setHeight(1500);

		for (int i = 0; i < (int) getWidth(); i += 50) {
			Line line = new Line(0, i, getHeight(), i);
			line.setStroke(Color.SILVER);
			getChildren().add(line);
		}
		for (int i = 0; i < (int) getHeight(); i += 50) {
			Line line = new Line(i, 0, i, getWidth());
			line.setStroke(Color.SILVER);

			getChildren().add(line);
		}
		
		
		

	}

	
}
