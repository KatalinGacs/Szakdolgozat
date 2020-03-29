package application;

import application.dbviews.AddSprinklerView;
import application.dbviews.ConnectMaterialsDbView;
import application.dbviews.MaterialListDBView;
import application.dbviews.SprinklerDBView;
import application.dbviews.SprinklerGroupDBView;
import controller.SprinklerController;
import controller.SprinklerControllerImpl;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
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

	private String filePath = FileHandler.currentPath.equals("") ? "[nem mentett]" : FileHandler.currentPath;

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
	private MenuItem printMenuItem = new MenuItem("Mentés képként");
	private MenuItem exitMenuItem = new MenuItem("Kilépés");
	/*
	 * private Menu editMenu = new Menu("Szerkesztés"); private MenuItem
	 * undoMenuItem = new MenuItem("Visszavonás"); private MenuItem redoMenuItem =
	 * new MenuItem("Ismét");
	 */
	private Menu dbMenu = new Menu("Adatbázis");
	private MenuItem sprinklerDbMenuItem = new MenuItem("Szórófej adatbázis");
	private MenuItem newSprinklerMenuItem = new MenuItem("Szórófej hozzáadása");
	private MenuItem sprinklerGroupDbMenuItem = new MenuItem("Szórófej csoportok");
	private MenuItem materialDbMenuItem = new MenuItem("Szórófej-anyag összekapcsolás");
	private MenuItem newMaterialMenuItem = new MenuItem("Anyag adatbázis");
	
	private ContextMenu rightClickMenu = new ContextMenu();
	private MenuItem delMenuItem = new MenuItem("Törlés");

	@Override
	public void start(Stage primaryStage) {
		try {
			primaryStage.setTitle("Öntözõ programka - " + filePath);
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root, 800, 600);
			// scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			root.setCenter(drawingPanel);

			menuBar.getMenus().addAll(fileMenu, dbMenu);
			fileMenu.getItems().addAll(newMenuItem, openMenuItem, saveMenuItem, saveAsMenuItem, printMenuItem,
					exitMenuItem);
			// editMenu.getItems().addAll(undoMenuItem, redoMenuItem);
			dbMenu.getItems().addAll(sprinklerGroupDbMenuItem, sprinklerDbMenuItem, newSprinklerMenuItem,
					materialDbMenuItem, newMaterialMenuItem);

			newMenuItem.setOnAction(e -> {
				FileHandler.newCanvas(drawingPanel.getCanvasPane(), primaryStage);
			});
			openMenuItem.setOnAction(e -> {
				FileHandler.loadCanvas(drawingPanel.getCanvasPane(), primaryStage);
				zoneTable.setItems(controller.listZones());
			});
			saveMenuItem.setOnAction(e -> {
				FileHandler.saveCanvas(primaryStage, drawingPanel.getCanvasPane(), false);
			});
			saveAsMenuItem.setOnAction(e -> {
				FileHandler.saveCanvas(primaryStage, drawingPanel.getCanvasPane(), true);
			});
			printMenuItem.setOnAction(e -> {
				PrintHandler.print(primaryStage, drawingPanel.getCanvasPane());
			});
			exitMenuItem.setOnAction(e -> {
				if (drawingPanel.getCanvasPane().isModifiedSinceLastSave()) {
					SaveModificationsStage s = new SaveModificationsStage(true, drawingPanel.getCanvasPane(),
							primaryStage);
				} else {
					Platform.exit();
				}
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
				ConnectMaterialsDbView connectMaterialsDbView = new ConnectMaterialsDbView();
			});
			newMaterialMenuItem.setOnAction(e -> {
				MaterialListDBView materialView = new MaterialListDBView();
			});
			root.setTop(menuBar);

			Platform.setImplicitExit(false);

			primaryStage.setOnCloseRequest(e -> {
				e.consume();
				if (drawingPanel.getCanvasPane().isModifiedSinceLastSave()) {
					SaveModificationsStage s = new SaveModificationsStage(true, drawingPanel.getCanvasPane(),
							primaryStage);
				} else {
					Platform.exit();
				}
			});

			// TODO innen jobbkattintásra legyenek zónák törölhetõk
			zoneTable.setOnMouseClicked(e -> {
				selectedZone = zoneTable.getSelectionModel().getSelectedItem();
				if (selectedZone != null) {
					if (e.getButton() == MouseButton.PRIMARY) {
						sprinklerListTable.setItems(controller.listSprinklerShapes(selectedZone));
					} else if (e.getButton() == MouseButton.SECONDARY) {
						rightClickMenu.show(zoneTable, Side.RIGHT, 5, 5);
						rightClickMenu.show(primaryStage, e.getX(), e.getY());
						delMenuItem.setOnAction(ev -> {
							//controller.deleteZone(selectedZone);
							// TODO controllerben megírni
							ev.consume();
						});
					}
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

	public static void run() {
		launch("");
	}

	public static void exit() {
		Platform.exit();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
