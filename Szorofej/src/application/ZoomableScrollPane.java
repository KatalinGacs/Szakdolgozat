package application;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.transform.Scale;

public class ZoomableScrollPane extends ScrollPane {

	Scale scale;

	double scaleValue = 1;
	double zoomIntensity = 0.03;

	public ZoomableScrollPane(Node content) {
		super();
		setContent(content);
		
		scale = new Scale(scaleValue, scaleValue, 0, 0);
		content.getTransforms().add(scale);

		content.setOnScroll(e -> {
			if (e.getDeltaY() < 0)
				scaleValue -= zoomIntensity;
			else
				scaleValue += zoomIntensity;
			zoomTo(scaleValue);
			e.consume();
		});
	}

	public void zoomTo(double scaleValue) {
		this.scaleValue = scaleValue;
		scale.setX(scaleValue);
		scale.setY(scaleValue);
	}

}
