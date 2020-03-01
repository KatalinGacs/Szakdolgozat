package application;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.PrintResolution;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

public class PrintHandler {

	// private static Rectangle printArea = new Rectangle();
	// TODO rajzterület kijelölése nyomtatáshoz

	public static void printSettings(CanvasPane canvasPane) {
		/*
		 * canvasPane.tempLineLayer.getChildren().add(printArea);
		 * printArea.setStrokeDashOffset(5); printArea.setStroke(Color.RED);
		 * printArea.setFill(Color.TRANSPARENT); printArea.setVisible(false);
		 * 
		 * settingsStage.setScene(scene); settingsStage.setAlwaysOnTop(true);
		 * settingsStage.setTitle("Nyomtatás"); root.getChildren().addAll(gridCheckBox,
		 * textsCheckBox, printBtn);
		 * 
		 * printBtn.setOnAction(e -> { foo(settingsStage, canvasPane,
		 * gridCheckBox.isSelected(), textsCheckBox.isSelected());
		 * settingsStage.close(); });
		 * 
		 * settingsStage.show();
		 */
	}

	public static void print(Window owner, CanvasPane canvasPane) {
		FileChooser fileChooser = new FileChooser();

		// Set extension filter
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("png files (*.png)", "*.png"));

		// Prompt user to select a file
		File file = fileChooser.showSaveDialog(null);

		Rectangle nonEmptyArea = getNonEmptyArea(canvasPane);
		Node oldClip = canvasPane.getClip();

		canvasPane.setClip(nonEmptyArea);

		if (file != null) {
			try {
				WritableImage writableImage = new WritableImage((int) nonEmptyArea.getWidth() + 20,
						(int) nonEmptyArea.getHeight() + 20);
				canvasPane.snapshot(null, writableImage);
				RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);

				ImageIO.write(renderedImage, "png", file);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		canvasPane.setClip(oldClip);

		// TODO would be nicer to be able to print/save as pdf but this solution needs more work
		/*
		 * PrinterJob job = PrinterJob.createPrinterJob(); Printer printer =
		 * job.getPrinter(); PageLayout pageLayout = printer.createPageLayout(Paper.A3,
		 * PageOrientation.LANDSCAPE, Printer.MarginType.HARDWARE_MINIMUM);
		 * 
		 * Rectangle nonEmptyArea = getNonEmptyArea(canvasPane);
		 * 
		 * 
		 * double width = nonEmptyArea.getWidth(); double height =
		 * nonEmptyArea.getHeight();
		 * 
		 * PrintResolution resolution = job.getJobSettings().getPrintResolution(); width
		 * /= resolution.getFeedResolution(); height /=
		 * resolution.getCrossFeedResolution(); double scaleX =
		 * pageLayout.getPrintableWidth() / width / 600; double scaleY =
		 * pageLayout.getPrintableHeight() / height / 600;
		 * 
		 * System.out.println("nonempty.." + nonEmptyArea);
		 * System.out.println("bounds "+ nonEmptyArea.getBoundsInParent().toString());
		 * System.out.println(scaleX + " " + scaleY);
		 * 
		 * if (scaleX > scaleY) scaleX = scaleY; else scaleY = scaleX; Scale scale = new
		 * Scale(scaleX, scaleY);
		 * 
		 * canvasPane.getTransforms().add(scale);
		 * 
		 * Node oldClip = canvasPane.getClip();
		 * 
		 * canvasPane.setClip(nonEmptyArea);
		 * 
		 * if (job != null) { job.showPrintDialog(owner);
		 * 
		 * job.printPage(canvasPane); }
		 * 
		 * canvasPane.getTransforms().remove(scale); canvasPane.setClip(oldClip);
		 */
	}

	private static Rectangle getNonEmptyArea(CanvasPane canvasPane) {
		Bounds borderBounds = canvasPane.bordersLayer.getBoundsInParent();
		Rectangle borderBoundingRect = new Rectangle(borderBounds.getMinX(), borderBounds.getMinY(),
				borderBounds.getWidth(), borderBounds.getHeight());

		Bounds irrigationBounds = canvasPane.irrigationLayer.getBoundsInParent();
		Rectangle irrigationBoundingRect = new Rectangle(irrigationBounds.getMinX(), irrigationBounds.getMinY(),
				irrigationBounds.getWidth(), irrigationBounds.getHeight());

		Bounds pipeLineBounds = canvasPane.pipeLineLayer.getBoundsInParent();
		Rectangle pipeLineBoundingRect = new Rectangle(pipeLineBounds.getMinX(), pipeLineBounds.getMinY(),
				pipeLineBounds.getWidth(), pipeLineBounds.getHeight());

		Bounds sprinklerArcBounds = canvasPane.sprinklerArcLayer.getBoundsInParent();
		Rectangle sprinklerArcBoundingRect = new Rectangle(sprinklerArcBounds.getMinX(), sprinklerArcBounds.getMinY(),
				sprinklerArcBounds.getWidth(), sprinklerArcBounds.getHeight());

		Bounds pipeTextBounds = canvasPane.pipeTextLayer.getBoundsInParent();
		Rectangle pipeTextRect = new Rectangle(pipeTextBounds.getMinX(), pipeTextBounds.getMinY(),
				pipeTextBounds.getWidth(), pipeTextBounds.getHeight());

		Bounds sprinklerTextBounds = canvasPane.sprinklerTextLayer.getBoundsInParent();
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
