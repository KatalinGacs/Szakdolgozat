package application;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.transform.Scale;

public class ZoomableScrollPane extends ScrollPane {

	Group zoomGroup;
	Scale scale;
	Node content;
	double scaleValue = 1.0;
	double zoomIntensity = 0.05;

	public ZoomableScrollPane(Node content) {
		super();
		this.content = content;
		zoomGroup = new Group(content);
		setContent(zoomGroup);
		scale = new Scale(scaleValue, scaleValue, 0, 0);
		zoomGroup.getTransforms().add(scale);

		zoomGroup.setOnScroll(e -> {			
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
