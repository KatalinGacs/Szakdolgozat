package application;

import application.common.Common;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.PrintResolution;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.stage.Window;

public class PrintHandler {

	public static void printSettings(CanvasPane canvasPane) {
		Stage settingsStage = new Stage();
		VBox root = new VBox();
		Scene scene = new Scene(root);
		CheckBox gridCheckBox = new CheckBox("Rácsvonalak");
		CheckBox textsCheckBox = new CheckBox("Feliratok");
		Button printBtn = new Button("Nyomtatás");

		settingsStage.setScene(scene);
		settingsStage.setAlwaysOnTop(true);
		root.getChildren().addAll(gridCheckBox, textsCheckBox, printBtn);

		printBtn.setOnAction(e -> {
			foo(settingsStage, canvasPane, gridCheckBox.isSelected(), textsCheckBox.isSelected());
			settingsStage.close();
		});

		settingsStage.show();
	}

	private static void foo(Window owner, CanvasPane canvasPane, boolean grid, boolean texts) {

		PrinterJob job = PrinterJob.createPrinterJob();
		Printer printer = job.getPrinter();
		PageLayout pageLayout = printer.createPageLayout(Paper.A3, PageOrientation.PORTRAIT,
				Printer.MarginType.HARDWARE_MINIMUM);

		double scaleX = pageLayout.getPrintableWidth() / canvasPane.getBoundsInParent().getWidth();
		double scaleY = pageLayout.getPrintableHeight() / canvasPane.getBoundsInParent().getHeight();
		if (grid)
			Common.showLayer(canvasPane.gridLayer);
		else
			Common.hideLayer(canvasPane.gridLayer);

		if (texts)
			Common.showLayer(canvasPane.textLayer);
		else
			Common.hideLayer(canvasPane.textLayer);
		
		if (scaleX < scaleY)
			scaleX = scaleY;
		else
			scaleY = scaleX;
		Scale scale = new Scale(scaleX, scaleY);

		canvasPane.getTransforms().add(scale);

		if (job != null) {
			job.showPrintDialog(owner);
			job.printPage(canvasPane);
			job.endJob();
			
			if (!grid)
				Common.showLayer(canvasPane.gridLayer);
			else
				Common.hideLayer(canvasPane.gridLayer);

			if (!texts)
				Common.showLayer(canvasPane.textLayer);
			else
				Common.hideLayer(canvasPane.textLayer);
		}

		canvasPane.getTransforms().remove(scale);
	}
}
