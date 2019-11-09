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
	private Text zoneText = new Text("Z�na");
	private Spinner<Zone> zonePicker = new Spinner<>();
	private Text colorText = new Text("Sz�n");
	private ColorPicker colorPicker = new ColorPicker();
	private Button startDrawingpipesBtn = new Button("Cs�vek beh�z�sa");
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

	// TODO el k�ne t�rolni z�n�khoz a sz�n�ket, mert ha ezt az ablakot becsukja �s
	// ut�na egy megkezdett z�n�t m�g szerkesztene, sose tal�lja meg a kor�bbi
	// sz�nt
	private Color nextColor() {
		int r = (0 + colorCounter * 75) % 256;
		int g = (150 + colorCounter * 50) % 256;
		int b = (255 + colorCounter * 30) % 256;
		Color color = Color.rgb(r, g, b);
		// TODO vmi sz�m�t�s, ami a color rgb �rt�keit a colorcountert�l teszi f�gg�v�
		// �gy, hogy kb 50 k�l�nb�z� sz�nt kiadjon �s az egym�st k�vet� sz�nek el�gg�
		// elt�rjenek
		colorCounter++;
		return color;
	}
}
