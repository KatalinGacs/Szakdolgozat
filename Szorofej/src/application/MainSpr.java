package application;

import application.dbviews.AddSprinklerView;
import application.dbviews.SprinklerDBView;
import application.dbviews.SprinklerGroupDBView;
import controller.SprinklerController;
import controller.SprinklerControllerImpl;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.bean.SprinklerShape;

public class MainSpr extends Application {

	private SprinklerController controller = new SprinklerControllerImpl();
	SprinklerTable table = new SprinklerTable();
	VBox left = new VBox();
	Button del = new Button("Törlés");
	DrawingPanel view = new DrawingPanel();

	private MenuBar menuBar = new MenuBar();
	private Menu fileMenu = new Menu("Fájl");
	private MenuItem newMenuItem = new MenuItem("Új");
	private MenuItem openMenuItem = new MenuItem("Megnyitás");
	private MenuItem saveMenuItem = new MenuItem("Mentés");
	private MenuItem saveAsMenuItem = new MenuItem("Mentés másként");
	private MenuItem exportMenuItem = new MenuItem("Exportálás pdf-be");
	private MenuItem exitMenuItem = new MenuItem("Kilépés");
	private Menu editMenu = new Menu("Szerkesztés");
	private MenuItem undoMenuItem = new MenuItem("Visszavonás");
	private MenuItem redoMenuItem = new MenuItem("Ismét");
	private Menu dbMenu = new Menu("Adatbázis");
	private MenuItem sprinklerDbMenuItem = new MenuItem("Szórófej adatbázis");
	private MenuItem newSprinklerMenuItem = new MenuItem("Szórófej hozzáadása");
	private MenuItem sprinklerGroupDbMenuItem = new MenuItem("Szórófej csoportok");
	private MenuItem materialDbMenuItem = new MenuItem("Anyag adatbázis");
	private MenuItem newMaterialMenuItem = new MenuItem("Anyag hozzáadása");
	
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root, 800, 600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			root.setCenter(view);

			menuBar.getMenus().addAll(fileMenu, editMenu, dbMenu);
			fileMenu.getItems().addAll(newMenuItem, openMenuItem, saveMenuItem, saveAsMenuItem, exportMenuItem, exitMenuItem);
			editMenu.getItems().addAll(undoMenuItem, redoMenuItem);
			dbMenu.getItems().addAll(sprinklerGroupDbMenuItem, sprinklerDbMenuItem, newSprinklerMenuItem, materialDbMenuItem, newMaterialMenuItem);
			
			sprinklerDbMenuItem.setOnAction(e -> {
				SprinklerDBView sprinklerDBView = new SprinklerDBView(); 
			});
			sprinklerGroupDbMenuItem.setOnAction( e-> {
				SprinklerGroupDBView sprinklerGroupDbView = new SprinklerGroupDBView();
			});
			newSprinklerMenuItem.setOnAction(e -> {
				AddSprinklerView addNewSprinklerView = new AddSprinklerView();
			});
			
			root.setTop(menuBar);
			
			left.getChildren().addAll(del, table);
			del.setOnAction(e -> {
				ObservableList<SprinklerShape> toBeDeleted = table.getSelectionModel().getSelectedItems();
				for (SprinklerShape s : toBeDeleted) {
					view.getCanvasPane().bordersLayer.getChildren().remove(s.getArc());
					view.getCanvasPane().bordersLayer.getChildren().remove(s.getCircle());
					controller.deleteSprinklerShape(s);
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
