package application;

import java.util.Random;

import application.CanvasPane.Use;
import application.common.Common;
import controller.SprinklerController;
import controller.SprinklerControllerImpl;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
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
	private Spinner<Zone> zonePicker = new Spinner<>();
	private Text colorText = new Text("Szín");
	private ColorPicker colorPicker = new ColorPicker();
	private Button startDrawingpipesBtn = new Button("Csövek behúzása");
	private Button okBtn = new Button("OK");

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
		root.add(okBtn, 0, 3);

		colorPicker.setValue(nextColor());
		zonePicker.setValueFactory(new SpinnerValueFactory.ListSpinnerValueFactory<Zone>(controller.listZones()));

		startDrawingpipesBtn.setOnAction(e -> {
			canvasPane.pipeLineColor = colorPicker.getValue();
			canvasPane.stateOfCanvasUse = Use.PREPAREFORPIPEDRAWING;
			canvasPane.pipeGraph = new PipeGraph(zonePicker.getValue());
		});

	}

	private static int colorCounter = 0;

	// TODO el kéne tárolni zónákhoz a színüket, mert ha ezt az ablakot becsukja és
	// utána egy megkezdett zónát még szerkesztene, sose találja meg a korábbi
	// színt
	private Color nextColor() {
		int r = (0 + colorCounter * 75) % 256;
		int g = (150 + colorCounter * 50) % 256;
		int b = (255 + colorCounter * 30) % 256;
		Color color = Color.rgb(r, g, b);
		// TODO vmi számítás, ami a color rgb értékeit a colorcountertõl teszi függõvé
		// úgy, hogy kb 50 különbözõ színt kiadjon és az egymást követõ színek eléggé
		// eltérjenek
		colorCounter++;
		return color;
	}
}
