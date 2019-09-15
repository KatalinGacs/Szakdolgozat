package application;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.transform.Scale;

public class ZoomableScrollPane extends ScrollPane {

	// TODO: k�ne egy hat�r, hogy mennyire lehet kicsiny�teni, mert most
	// el�fordulhat, hogy olyan kicsire kizoomol, hogy nem lehet vissza�ll�tani
	// vagy: egy gomb valahol, amivel vissza lehet �llni a "gy�ri" be�ll�t�sra (ez egyszer�bb)
	Group zoomGroup;
	Scale scale;
	Node content;
	double scaleValue = 0.5;
	double zoomIntensity = 0.01;

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

	public Group getZoomGroup() {
		return zoomGroup;
	}

	public void setZoomGroup(Group zoomGroup) {
		this.zoomGroup = zoomGroup;
	}

}
