package application;

import controller.SprinklerController;
import controller.SprinklerControllerImpl;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.bean.Sprinkler;

public class MainSpr extends Application {

	private SprinklerController controller = new SprinklerControllerImpl();

	SprinklerTable table = new SprinklerTable();
	VBox left = new VBox();
	Button del = new Button("Törlés");

	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root, 800, 600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			root.setCenter(new Rajzolo());

			left.getChildren().addAll(del, table);
			del.setOnAction(e -> {
				ObservableList<Sprinkler> toBeDeleted = table.getSelectionModel().getSelectedItems();
				for (Sprinkler s : toBeDeleted) {
					Rajzolo.group.getChildren().remove(s.getArc());
					Rajzolo.group.getChildren().remove(s.getCircle());
					controller.deleteSprinkler(s);
				}
			});
			root.setLeft(left);
			primaryStage.setMaximized(true);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);

	}

}
