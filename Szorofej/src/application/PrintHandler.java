package application;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.image.WritableImage;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import utilities.Common;

/**
 * Helper class for exporting the plan to an image
 * 
 * @author Gacs Katalin
 *
 */
public class PrintHandler {

	/**
	 * Export the parts of the plan which contain drawn shapes to PNG.
	 * 
	 * @param owner      owner window of the file chooser
	 * @param canvasPane CanvasPane on which the plan is drawn
	 */
	public static void print(Window owner, CanvasPane canvasPane) {

		try {
			Common.hideLayer(canvasPane.getTempLineLayer());
			FileChooser fileChooser = new FileChooser();
			fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("png files (*.png)", "*.png"));
			File file = fileChooser.showSaveDialog(null);

			Rectangle nonEmptyArea = getNonEmptyArea(canvasPane);
			Node oldClip = canvasPane.getClip();

			canvasPane.setClip(nonEmptyArea);

			if (file != null) {
				WritableImage writableImage = new WritableImage((int) nonEmptyArea.getWidth() + 20,
						(int) nonEmptyArea.getHeight() + 20);
				canvasPane.snapshot(null, writableImage);
				RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);

				ImageIO.write(renderedImage, "png", file);
			}
			canvasPane.setClip(oldClip);
			
			String[] commands = { "cmd.exe", "/c", "start", "\"DummyTitle\"", "\"" + file.getAbsolutePath() + "\""};
			Process p = Runtime.getRuntime().exec(commands);
			p.waitFor();
			
		} catch (Exception ex) {
			utilities.Error.HandleException(ex);
		}

	}

	/**
	 * Calculate the area on the CanvasPane which has shapes drawn on
	 * 
	 * @param canvasPane CanvasPane on which the plan was drawn
	 * @return Rectangle that covers exactly that area on which there was drawn
	 */
	private static Rectangle getNonEmptyArea(CanvasPane canvasPane) {
		Bounds borderBounds = canvasPane.getBordersLayer().getBoundsInParent();
		Rectangle borderBoundingRect = new Rectangle(borderBounds.getMinX(), borderBounds.getMinY(),
				borderBounds.getWidth(), borderBounds.getHeight());

		Bounds irrigationBounds = canvasPane.getIrrigationLayer().getBoundsInParent();
		Rectangle irrigationBoundingRect = new Rectangle(irrigationBounds.getMinX(), irrigationBounds.getMinY(),
				irrigationBounds.getWidth(), irrigationBounds.getHeight());

		Bounds pipeLineBounds = canvasPane.getPipeLineLayer().getBoundsInParent();
		Rectangle pipeLineBoundingRect = new Rectangle(pipeLineBounds.getMinX(), pipeLineBounds.getMinY(),
				pipeLineBounds.getWidth(), pipeLineBounds.getHeight());

		Bounds sprinklerArcBounds = canvasPane.getSprinklerArcLayer().getBoundsInParent();
		Rectangle sprinklerArcBoundingRect = new Rectangle(sprinklerArcBounds.getMinX(), sprinklerArcBounds.getMinY(),
				sprinklerArcBounds.getWidth(), sprinklerArcBounds.getHeight());

		Bounds pipeTextBounds = canvasPane.getPipeTextLayer().getBoundsInParent();
		Rectangle pipeTextRect = new Rectangle(pipeTextBounds.getMinX(), pipeTextBounds.getMinY(),
				pipeTextBounds.getWidth(), pipeTextBounds.getHeight());

		Bounds sprinklerTextBounds = canvasPane.getSprinklerTextLayer().getBoundsInParent();
		Rectangle sprinklerTextBoundingRect = new Rectangle(sprinklerTextBounds.getMinX(),
				sprinklerTextBounds.getMinY(), sprinklerTextBounds.getWidth(), sprinklerTextBounds.getHeight());

		Shape tempUnion1 = Shape.union(borderBoundingRect, irrigationBoundingRect);
		Shape tempUnion2 = Shape.union(pipeLineBoundingRect, sprinklerArcBoundingRect);
		Shape tempUnion3 = Shape.union(pipeTextRect, sprinklerTextBoundingRect);
		Shape union = Shape.union(tempUnion1, Shape.union(tempUnion2, tempUnion3));

		Bounds unionBounds = union.getBoundsInParent();

		Rectangle result = new Rectangle(unionBounds.getMinX(), unionBounds.getMinY(), unionBounds.getWidth(),
				unionBounds.getHeight());

		return result;
	}
}
