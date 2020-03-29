package application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import com.sun.glass.events.KeyEvent;

import application.CanvasPane.Use;
import application.common.Common;
import controller.SprinklerController;
import controller.SprinklerControllerImpl;
import javafx.event.EventType;
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

public class PipeStage extends Stage {

	SprinklerController controller = new SprinklerControllerImpl();

	private GridPane root = new GridPane();
	private Scene scene = new Scene(root);
	private Text zoneText = new Text("Zóna");
	private ComboBox<Zone> zonePicker = new ComboBox<>();
	private Text colorText = new Text("Szín");
	private ColorPicker colorPicker = new ColorPicker(nextColor());
	private ToggleButton startDrawingpipesBtn = new ToggleButton("Csövek behúzása");

	private Text beginningPressureText = new Text("Kezdeti nyomás");
	private TextField beginningPressureField = new TextField();

	private Button okBtn = new Button("OK");

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
		
		zonePicker.setItems(controller.listZones());
		zonePicker.getSelectionModel().select(0);	
		setColor();
		zonePicker.setOnAction(e -> {
			setColor();

			canvasPane.pipeGraphUnderEditing = controller.getPipeGraph(zonePicker.getValue());
		});

		startDrawingpipesBtn.setOnAction(e -> {
			if (startDrawingpipesBtn.isSelected()) {
				CanvasPane.pipeLineColor = colorPicker.getValue();
				canvasPane.stateOfCanvasUse = Use.PREPAREFORPIPEDRAWING;
				colorPicker.setDisable(false);
				if (canvasPane.pipeGraphUnderEditing == null
						|| canvasPane.pipeGraphUnderEditing.getZone() != zonePicker.getValue()) {
					canvasPane.pipeGraphUnderEditing = new PipeGraph(zonePicker.getValue(), colorPicker.getValue());
					controller.addPipeGraph(canvasPane.pipeGraphUnderEditing);
				}
			} else {
				startDrawingpipesBtn.setSelected(false);
				canvasPane.stateOfCanvasUse = Use.NONE;
			}
		});

		beginningPressureField.setOnKeyPressed(e -> {
			if (e.getCode().equals(KeyCode.ENTER)) {
				finalizePipeGraph(canvasPane);
				e.consume();
			}
		});
		
		okBtn.setOnAction(e-> {
			finalizePipeGraph(canvasPane);
		});
	}

	private static int colorCounter = 0;
	private static ArrayList<Color> colors = new ArrayList<>(Arrays.asList(Color.BLUEVIOLET, Color.BROWN, Color.CORAL,
			Color.GOLD, Color.CRIMSON, Color.DEEPPINK, Color.CHOCOLATE, Color.DARKCYAN, Color.DARKGOLDENROD,
			Color.DARKGREEN, Color.DARKMAGENTA, Color.DARKORANGE, Color.DARKORCHID, Color.FORESTGREEN, Color.FUCHSIA,
			Color.GOLDENROD, Color.LIGHTCORAL));

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

	private void setColor() {
		for (PipeGraph pipeGraph : controller.listPipeGraphs()) {
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
				controller.getPipeGraph(zonePicker.getValue()).getRoot());
		close();
		} catch (Exception e) {
			Common.showAlert(e.getMessage());
		}
		
	}

}
