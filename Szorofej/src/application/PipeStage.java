package application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import application.CanvasPane.Use;
import application.common.Common;
import controller.PressureException;
import controller.SprinklerController;
import controller.SprinklerControllerImpl;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.bean.PipeGraph;
import model.bean.Zone;

/**
 * Stage for setting infos of pipes and draw pipes
 * 
 * @author Gacs Katalin
 *
 */
public class PipeStage extends Stage {

	/**
	 * Root container in the stage
	 */
	private GridPane root = new GridPane();

	/**
	 * Scene of the stage
	 */
	private Scene scene = new Scene(root);

	/**
	 * ComboBox to choose the zone for which pipes are drawn
	 */
	private ComboBox<Zone> zonePicker = new ComboBox<>();

	/**
	 * Text for zonePicker
	 */
	private Text zoneText = new Text("Zóna");

	/**
	 * Control for setting the color of the currently drawn pipe. Once set for a
	 * zone, cannot be changed.
	 */
	private ColorPicker colorPicker = new ColorPicker(nextColor());

	/**
	 * Text for colorPicker
	 */
	private Text colorText = new Text("Szín");

	/**
	 * When this ToggleButton is selected, the user can draw the pipes on the
	 * canvaspane
	 */
	private ToggleButton startDrawingpipesBtn = new ToggleButton("Csövek behúzása");

	/**
	 * TextField for setting the beginning pressure in the zone
	 */
	private TextField beginningPressureField = new TextField();

	/**
	 * Text for beginningPressureField
	 */
	private Text beginningPressureText = new Text("Kezdeti nyomás");

	/**
	 * By clicking this button the user finishes the pipe drawing and the
	 * application calculates the pipe diameters
	 */
	private Button okBtn = new Button("OK");

	/**
	 * Create the stage, set its controls and add event handling to the controls
	 * 
	 * @param canvasPane the CanvasPane on which the pipes are to be drawn
	 */
	public PipeStage(CanvasPane canvasPane) {
		setX(Common.primaryScreenBounds.getWidth() - 500);
		setY(100);
		root.setVgap(10);
		root.setHgap(10);
		root.setPadding(new Insets(10, 10, 10, 10));

		setScene(scene);
		setAlwaysOnTop(true);
		setTitle("Csövezés");

		root.add(zoneText, 0, 0);
		root.add(zonePicker, 1, 0);
		root.add(colorText, 0, 1);
		root.add(colorPicker, 1, 1);
		root.add(startDrawingpipesBtn, 0, 2);
		root.add(beginningPressureText, 0, 3);
		root.add(beginningPressureField, 1, 3);
		root.add(okBtn, 0, 4);

		zonePicker.setItems(canvasPane.controller.listZones());
		zonePicker.getSelectionModel().select(0);
		setColor(canvasPane);
		zonePicker.setOnAction(e -> {
			setColor(canvasPane);

			canvasPane.pipeGraphUnderEditing = canvasPane.controller.getPipeGraph(zonePicker.getValue());
		});

		startDrawingpipesBtn.setOnAction(e -> {
			if (startDrawingpipesBtn.isSelected()) {
				CanvasPane.setPipeLineColor(colorPicker.getValue());
				canvasPane.setStateOfCanvasUse(Use.PREPAREFORPIPEDRAWING);
				colorPicker.setDisable(false);
				if (canvasPane.pipeGraphUnderEditing == null
						|| canvasPane.pipeGraphUnderEditing.getZone() != zonePicker.getValue()) {
					canvasPane.pipeGraphUnderEditing = new PipeGraph(zonePicker.getValue(), colorPicker.getValue());
					canvasPane.controller.addPipeGraph(canvasPane.pipeGraphUnderEditing);
				}
			} else {
				startDrawingpipesBtn.setSelected(false);
				canvasPane.setStateOfCanvasUse(Use.NONE);
			}
		});

		beginningPressureField.setOnKeyPressed(e -> {
			if (e.getCode().equals(KeyCode.ENTER)) {
				finalizePipeGraph(canvasPane);
				e.consume();
			}
		});

		okBtn.setOnAction(e -> {
			finalizePipeGraph(canvasPane);
		});
	}

	/**
	 * A list of colors for pipes. The user can set it manually but does not have to
	 * because for a new zone a new color is given from this list. It is not
	 * expected that there are more zones in a plan than colors in this list.
	 */
	private static ArrayList<Color> colors = new ArrayList<>(Arrays.asList(Color.BLUEVIOLET, Color.BROWN, Color.CORAL,
			Color.GOLD, Color.CRIMSON, Color.DEEPPINK, Color.CHOCOLATE, Color.DARKCYAN, Color.DARKGOLDENROD,
			Color.DARKGREEN, Color.DARKMAGENTA, Color.DARKORANGE, Color.DARKORCHID, Color.FORESTGREEN, Color.FUCHSIA,
			Color.GOLDENROD, Color.LIGHTCORAL));

	/**
	 * Count which color was already used in the list colors
	 */
	private static int colorCounter = 0;

	/**
	 * Get the next not used color from the list colors
	 * 
	 * @return a color which was not used before
	 */
	private Color nextColor() {
		Color color;
		if (colorCounter < colors.size()) {
			color = colors.get(colorCounter);
		} else {
			Random random = new Random();
			int r = random.nextInt(256);
			int g = random.nextInt(256);
			int b = random.nextInt(256);
			color = Color.rgb(r, g, b);
		}
		colorCounter++;
		return color;
	}

	/**
	 * Set the color of the colorpicker to a color which was not used before. If a
	 * zone is selected that already has pipes drawn with one color, then setting a
	 * new color is disabled.
	 */
	private void setColor(CanvasPane canvasPane) {
		for (PipeGraph pipeGraph : canvasPane.controller.listPipeGraphs()) {
			if (pipeGraph.getZone() == zonePicker.getValue()) {
				colorPicker.setValue(pipeGraph.getColor());
				colorPicker.setDisable(true);
				break;
			} else {
				colorPicker.setDisable(false);
				colorPicker.setValue(nextColor());
			}
		}
	}

	/**
	 * End drawing pipes, calculate their diameters.
	 * 
	 * @param canvasPane CanvasPane on which the pipes were drawn
	 */
	private void finalizePipeGraph(CanvasPane canvasPane) {
		if (zonePicker.getValue() == null) {
			Common.showAlert("Nincs kiválasztott zóna");
		} else if (canvasPane.pipeGraphUnderEditing == null) {
			Common.showAlert("Nincs megadott csövezés");
		} else if (beginningPressureField.getText() == null || beginningPressureField.getText().trim().isEmpty()) {
			Common.showAlert("Add meg a kezdeti nyomást!");
		} else
			try {
				canvasPane.pipeGraphUnderEditing
						.setBeginningPressure(Double.parseDouble(beginningPressureField.getText()));
			} catch (NumberFormatException ex) {
				Common.showAlert("Számokban add meg a kezdeti nyomást!");
			}
		try {
			PipeDrawing.completePipeDrawing(canvasPane, zonePicker.getValue(),
					canvasPane.controller.getPipeGraph(zonePicker.getValue()).getRoot());
			close();
		} catch (PressureException e) {
			Common.showAlert(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			utilities.Error.HandleException(e);
		}

	}

}
