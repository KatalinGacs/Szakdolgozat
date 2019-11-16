package application;

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
import model.bean.PipeGraph.Vertex;
import model.bean.Zone;

public class PipeStage extends Stage {

	SprinklerController controller = new SprinklerControllerImpl();

	private GridPane root = new GridPane();
	private Scene scene = new Scene(root);
	private Text zoneText = new Text("Zóna");
	private ComboBox<Zone> zonePicker = new ComboBox<>();
	private Text colorText = new Text("Szín");
	private ColorPicker colorPicker = new ColorPicker(nextColor());
	private Button startDrawingpipesBtn = new Button("Csövek behúzása");
	private Button okBtn = new Button("OK");
	private Text beginningPressureText = new Text("Kezdeti nyomás");
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
			PipeDrawing.completePipeDrawing(canvasPane, zonePicker.getValue(), controller.getPipeGraph(zonePicker.getValue()).getRoot());

			/*for (PipeGraph pg : controller.listPipeGraphs()) {
				for (Vertex parent : pg.getVertices()) {
					System.out.println(parent + " parent: " + parent.getParent());
					System.out.println(parent + " children: " + parent.getChildren());
				}
			}*/

		});

	}

	private static int colorCounter = 0;

	private Color nextColor() {
		int r = (0 + colorCounter * 75) % 256;
		int g = (150 + colorCounter * 50) % 256;
		int b = (255 + colorCounter * 30) % 256;
		Color color = Color.rgb(r, g, b);
		// TODO vmi számítás, ami a color rgb értékeit a colorcountertõl teszi függõvé
		// úgy, hogy kb 50 különbözõ színt kiadjon és az egymást követõ színek eléggé
		// eltérjenek
		// lehet hogy ennél a random is értelmesebb
		// vagy felsorolni kb 20 színt egy listában, és ha ezek nem elfogynak, azután
		// random;
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
