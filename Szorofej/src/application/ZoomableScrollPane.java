package application;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.transform.Scale;

/**
 * A custom scrollpane whose content can be zoomed by scrolling. Scrolling can
 * be done with the scrollbars
 * 
 * @author Gacs Katalin
 *
 */
public class ZoomableScrollPane extends ScrollPane {

	Scale scale;

	double scaleValue = 1;
	double zoomIntensity = 0.03;

	public ZoomableScrollPane(Node content) {
		super();
		try {
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
		} catch (Exception ex) {
			utilities.Error.HandleException(ex);
		}
	}

	public void zoomTo(double scaleValue) {
		try {
			this.scaleValue = scaleValue;
			scale.setX(scaleValue);
			scale.setY(scaleValue);
		} catch (Exception ex) {
			utilities.Error.HandleException(ex);
		}
	}

}
