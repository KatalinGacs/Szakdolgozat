package szakdolgozat.view;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.fx.nodes.InfiniteCanvas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import szakdolgozat.controller.CsoatmeroOptimalizalo;

public class MainView extends Application {
	InfiniteCanvas canvas = new InfiniteCanvas();

	@Override
	public void start(Stage primaryStage)  {

		try {
			VBox root = new VBox();
			Scene scene = new Scene(root, 800, 600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			root.getChildren().add(FXMLLoader.load(getClass().getResource("TabMenu.fxml")));
			canvas.setGridCellHeight(30);
			canvas.setGridCellWidth(30);
			canvas.setMinHeight(400);
			root.getChildren().add(canvas);
			primaryStage.show();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
		
		/*
		 * optimalizalo teszt
		CsoatmeroOptimalizalo op = new CsoatmeroOptimalizalo();
		ArrayList<Double> csoSzakaszok = new ArrayList<Double>();
		ArrayList<Double> kumulaltVizfogyasztas = new ArrayList<Double>();
		ArrayList<Double> minimalisNyomas = new ArrayList<Double>();
		
		csoSzakaszok.add(0, 100.0);
		csoSzakaszok.add(1, 100.0);
		csoSzakaszok.add(2, 100.0);
		
		kumulaltVizfogyasztas.add(0, 100.0);
		kumulaltVizfogyasztas.add(1, 50.0);
		kumulaltVizfogyasztas.add(2, 25.0);
		
		minimalisNyomas.add(0, 2.0);
		minimalisNyomas.add(1, 2.0);
		minimalisNyomas.add(2, 2.0);
		
		List<String> lista = op.optimalisCsovek(5.0, csoSzakaszok, kumulaltVizfogyasztas, minimalisNyomas);
		*/
	
	}
}