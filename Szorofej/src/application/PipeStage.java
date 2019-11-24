package application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import application.CanvasPane.Use;
import application.common.Common;
import controller.SprinklerController;
import controller.SprinklerControllerImpl;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.bean.PipeGraph;
import model.bean.PipeGraph.Edge;
import model.bean.Zone;

public class PipeStage extends Stage {

	SprinklerController controller = new SprinklerControllerImpl();

	private GridPane root = new GridPane();
	private Scene scene = new Scene(root);
	private Text zoneText = new Text("Z�na");
	private ComboBox<Zone> zonePicker = new ComboBox<>();
	private Text colorText = new Text("Sz�n");
	private ColorPicker colorPicker = new ColorPicker(nextColor());
	private Button startDrawingpipesBtn = new Button("Cs�vek beh�z�sa");
	private Button okBtn = new Button("OK");
	private Text beginningPressureText = new Text("Kezdeti nyom�s");
	private TextField beginningPressureField = new TextField();

	public PipeStage(CanvasPane canvasPane) {
		setX(Common.primaryScreenBounds.getWidth() - 500);
		setY(100);
		root.setVgap(10);
		root.setHgap(10);
		root.setPadding(new Insets(10, 10, 10, 10));

		setScene(scene);
		setAlwaysOnTop(true);

		root.add(zoneText, 0, 0);
		root.add(zonePicker, 1, 0);
		root.add(colorText, 0, 1);
		root.add(colorPicker, 1, 1);
		root.add(startDrawingpipesBtn, 0, 2);
		root.add(beginningPressureText, 0, 3);
		root.add(beginningPressureField, 1, 3);
		root.add(okBtn, 0, 4);
		zonePicker.setItems(controller.listZones());

		setColor();

		zonePicker.setOnAction(e -> {
			setColor();

			canvasPane.pipeGraphUnderEditing = controller.getPipeGraph(zonePicker.getValue());
		});

		startDrawingpipesBtn.setOnAction(e -> {
			CanvasPane.pipeLineColor = colorPicker.getValue();
			canvasPane.stateOfCanvasUse = Use.PREPAREFORPIPEDRAWING;
			colorPicker.setDisable(false);
			if (canvasPane.pipeGraphUnderEditing == null
					|| canvasPane.pipeGraphUnderEditing.getZone() != zonePicker.getValue()) {
				canvasPane.pipeGraphUnderEditing = new PipeGraph(zonePicker.getValue(), colorPicker.getValue());
				controller.addPipeGraph(canvasPane.pipeGraphUnderEditing);

			}
		});

		okBtn.setOnAction(e -> {
			if (zonePicker.getValue() == null) {
				Common.showAlert("Nincs kiv�lasztott z�na");
			} else if (canvasPane.pipeGraphUnderEditing == null) {
				Common.showAlert("Nincs megadott cs�vez�s");
			} else if (beginningPressureField.getText() == null || beginningPressureField.getText().trim().isEmpty()) {
				Common.showAlert("Add meg a kezdeti nyom�st!");
			} else
				try {
					canvasPane.pipeGraphUnderEditing
							.setBeginningPressure(Double.parseDouble(beginningPressureField.getText()));
				} catch (NumberFormatException ex) {
					Common.showAlert("Sz�mokban add meg a kezdeti nyom�st!");
				}
			PipeDrawing.completePipeDrawing(canvasPane, zonePicker.getValue(),
					controller.getPipeGraph(zonePicker.getValue()).getRoot());
			/*for (Vertex v : controller.getPipeGraph(zonePicker.getValue()).getVertices()) {
				System.out.println("vertex: "  + v);
				System.out.println("parent: " + v.getParent());
				for (Vertex child: v.getChildren()) {
					System.out.println("child: " + child);
				}
			}*/
			
			/*for (Edge edge :controller.getPipeGraph(zonePicker.getValue()).getEdges()) { 
				System.out.println("edge: " + edge + " st" + edge.getStartX() + " "+ edge.getStartY() + " end" 
			+ edge.getEndX() + " " + edge.getEndY());
				System.out.println("parentv: " + edge.getvParent());
				System.out.println("childv " + edge.getvChild());
			}*/

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

	
}
