package szakdolgozat.view;

import org.eclipse.gef.fx.nodes.InfiniteCanvas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
	}
}