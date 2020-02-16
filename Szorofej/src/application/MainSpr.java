package application;

import application.dbviews.AddSprinklerView;
import application.dbviews.SprinklerDBView;
import application.dbviews.SprinklerGroupDBView;
import controller.SprinklerController;
import controller.SprinklerControllerImpl;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.bean.Zone;

public class MainSpr extends Application {

	private SprinklerController controller = new SprinklerControllerImpl();

	private String filePath = FileHandling.currentPath.equals("") ? "[nem mentett]" : FileHandling.currentPath;

	private DrawingPanel drawingPanel = new DrawingPanel();

	private VBox left = new VBox();
	private SprinklerListTable sprinklerListTable;
	private ZoneTable zoneTable = new ZoneTable(drawingPanel.getCanvasPane());
	private Zone selectedZone;
	private SprinklerDetailTable sprinklerDetailTable;

	private MenuBar menuBar = new MenuBar();
	private Menu fileMenu = new Menu("Fájl");
	private MenuItem newMenuItem = new MenuItem("Új");
	private MenuItem openMenuItem = new MenuItem("Megnyitás");
	private MenuItem saveMenuItem = new MenuItem("Mentés");
	private MenuItem saveAsMenuItem = new MenuItem("Mentés másként");
	private MenuItem printMenuItem = new MenuItem("Nyomtatás");
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
			primaryStage.setTitle("Öntözõ programka - " + filePath);
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root, 800, 600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			root.setCenter(drawingPanel);

			menuBar.getMenus().addAll(fileMenu, editMenu, dbMenu);
			fileMenu.getItems().addAll(newMenuItem, openMenuItem, saveMenuItem, saveAsMenuItem, printMenuItem,
					exitMenuItem);
			editMenu.getItems().addAll(undoMenuItem, redoMenuItem);
			dbMenu.getItems().addAll(sprinklerGroupDbMenuItem, sprinklerDbMenuItem, newSprinklerMenuItem,
					materialDbMenuItem, newMaterialMenuItem);

			newMenuItem.setOnAction(e -> {
				// TODO
			});
			openMenuItem.setOnAction(e -> {
				FileHandling.loadCanvas(drawingPanel.getCanvasPane(), primaryStage);
				zoneTable.setItems(controller.listZones());
			});
			saveMenuItem.setOnAction(e -> {
				FileHandling.saveCanvas(primaryStage, false);
			});
			saveAsMenuItem.setOnAction(e -> {
				FileHandling.saveCanvas(primaryStage, true);
			});
			printMenuItem.setOnAction(e -> {
				PrintHandler.printSettings(drawingPanel.getCanvasPane());
			});
			exitMenuItem.setOnAction(e -> {
				// TODO rákérdezzen, hogy menti-e
				primaryStage.close();
			});
			undoMenuItem.setOnAction(e -> {
				// TODO
			});
			redoMenuItem.setOnAction(e -> {
				// TODO
			});
			sprinklerDbMenuItem.setOnAction(e -> {
				SprinklerDBView sprinklerDBView = new SprinklerDBView();
			});
			sprinklerGroupDbMenuItem.setOnAction(e -> {
				SprinklerGroupDBView sprinklerGroupDbView = new SprinklerGroupDBView();
			});
			newSprinklerMenuItem.setOnAction(e -> {
				AddSprinklerView addNewSprinklerView = new AddSprinklerView();
			});
			materialDbMenuItem.setOnAction(e -> {
				// TODO
			});
			newMaterialMenuItem.setOnAction(e -> {
				// TODO
			});
			root.setTop(menuBar);

			// TODO innen jobbkattintásra legyenek zónák törölhetõk, különösen ha az undo/redo nem lesz meg
			zoneTable.setOnMouseClicked(e -> {
				if (e.getButton() == MouseButton.PRIMARY) {
					selectedZone = zoneTable.getSelectionModel().getSelectedItem();
					if (selectedZone != null)
						sprinklerListTable.setItems(controller.listSprinklerShapes(selectedZone));
				}
			});
			sprinklerListTable = new SprinklerListTable(selectedZone);

			sprinklerListTable.setOnMouseClicked(e -> {
				if (e.getButton() == MouseButton.PRIMARY) {
					sprinklerDetailTable = new SprinklerDetailTable(
							sprinklerListTable.getSelectionModel().getSelectedItem());
					root.setRight(sprinklerDetailTable);

				}
			});

			left.getChildren().addAll(zoneTable, sprinklerListTable);

			root.setLeft(left);
			primaryStage.setMaximized(true);
			
			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
    public static void run()
    {
        launch("");
    }
    
    public static void exit()
    {
        Platform.exit();
    }

	public static void main(String[] args) {
		launch(args);
	}

}
