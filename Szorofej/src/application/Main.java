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
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.FileHandler;
import model.bean.Zone;

/**
 * Main class to set the application up and run it.
 * 
 * @author Gacs Katalin
 *
 */
public class Main extends Application {

	/**
	 * Controller for database actions
	 */
	private SprinklerController controller = new SprinklerControllerImpl();

	/**
	 * Path of the currently saved file
	 */
	private String filePath = FileHandler.currentPath.equals("") ? "" : FileHandler.currentPath;

	/**
	 * Container with the drawing area and its controls
	 */
	private DrawingPanel drawingPanel = new DrawingPanel(controller);

	/**
	 * Container for the layouts and controls left from the drawing panel
	 */
	private VBox left = new VBox();
	
	/**
	 * Table listing the zones on the current plan and their infos
	 */
	private ZoneTable zoneTable;
	
	/**
	 * Selected zone in zoneTable
	 */
	private Zone selectedZone;
	
	/**
	 * Table listing the sprinklers and their informations in selectedZone
	 */
	private SprinklerListTable sprinklerListTable;
	
//	/**
//	 * Table listing the details of a srpinkler selected in sprinklerListTable
//	 */
//	private SprinklerDetailTable sprinklerDetailTable;

	/**
	 * Main menu bar
	 */
	private MenuBar menuBar = new MenuBar();
	
	/**
	 * File menu under main menu bar
	 */
	private Menu fileMenu = new Menu("F�jl");
	
	/**
	 * Menu item for starting a new plan
	 */
	private MenuItem newMenuItem = new MenuItem("�j");
	
	/**
	 * Menu item for opening a saved plan
	 */
	private MenuItem openMenuItem = new MenuItem("Megnyit�s");
	
	/**
	 * Menu item for saving the current plan
	 */
	private MenuItem saveMenuItem = new MenuItem("Ment�s");
	
	/**
	 * Menu item for saving the current plan as a new file
	 */
	private MenuItem saveAsMenuItem = new MenuItem("Ment�s m�sk�nt");
	
	/**
	 * Menu item for exporting the current plan to png
	 */
	private MenuItem exportMenuItem = new MenuItem("Ment�s k�pk�nt");

	/**
	 * Menu item for closing the application
	 */
	private MenuItem exitMenuItem = new MenuItem("Kil�p�s");

	/**
	 * Menu for editing the plan
	 */
	private Menu editMenu = new Menu("Szerkeszt�s");
	
	/**
	 * Menu item for undoing a drawing action on the canvas
	 */
	private MenuItem undoMenu = new MenuItem("Visszavon�s");
	
	/**
	 * Menu item for redoing a drawing action on the canvas
	 */
	private MenuItem redoMenu = new MenuItem("�jra");	
	
	/**
	 * Menu for actions related to the database
	 */
	private Menu dbMenu = new Menu("Adatb�zis");
	
	/**
	 * Menu item for listing the sprinkler types in the database and editing them
	 */
	private MenuItem sprinklerDbMenuItem = new MenuItem("Sz�r�fej adatb�zis");
	
	/**
	 * Menu item for adding a sprinkler types to the database
	 */
	private MenuItem newSprinklerMenuItem = new MenuItem("Sz�r�fej hozz�ad�sa");
	
	/**
	 * Menu item for listing the sprinkler groups in the database and editing them
	 */
	private MenuItem sprinklerGroupDbMenuItem = new MenuItem("Sz�r�fej csoportok");
	
	/**
	 *  Menu item for connecting the materials and the sprinkler types in the database
	 */
	private MenuItem materialDbMenuItem = new MenuItem("Sz�r�fej-anyag �sszekapcsol�s");
	
	/**
	 *  Menu item for listing the materials in the database and editing them
	 */
	private MenuItem newMaterialMenuItem = new MenuItem("Anyag adatb�zis");
	
	/**
	 * Context menu for the zoneTable
	 */
	private ContextMenu rightClickMenu = new ContextMenu();
	
	/**
	 * Menu item for the rightClickMenu for deleting the selected zone
	 */
	private MenuItem delMenuItem = new MenuItem("T�rl�s");

	/**
	 * Run the application. 
	 */
	@Override
	public void start(Stage primaryStage) {
		
		try {
			primaryStage.setTitle("�nt�z� programka - " + filePath);
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root, 800, 600);
			primaryStage.setScene(scene);
			root.setCenter(drawingPanel);
			UndoManager.init(drawingPanel.getCanvasPane());
			
			menuBar.getMenus().addAll(fileMenu, editMenu, dbMenu);
			
			fileMenu.getItems().addAll(newMenuItem, openMenuItem, saveMenuItem, saveAsMenuItem, exportMenuItem,
					exitMenuItem);
			
			editMenu.getItems().addAll(undoMenu, redoMenu);
			
			dbMenu.getItems().addAll(sprinklerGroupDbMenuItem, sprinklerDbMenuItem, newSprinklerMenuItem,
					materialDbMenuItem, newMaterialMenuItem);
			
			newMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
			newMenuItem.setOnAction(e -> {
				FileHandler.newCanvas(drawingPanel.getCanvasPane(), primaryStage);
			});
			
			openMenuItem.setOnAction(e -> {
				FileHandler.loadCanvas(drawingPanel.getCanvasPane(), primaryStage);
				zoneTable.setItems(controller.listZones());
			});
			
			saveMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
			saveMenuItem.setOnAction(e -> {
				FileHandler.saveCanvas(primaryStage, drawingPanel.getCanvasPane(), false);
			});
			
			saveAsMenuItem.setOnAction(e -> {
				FileHandler.saveCanvas(primaryStage, drawingPanel.getCanvasPane(), true);
			});
			
			exportMenuItem.setOnAction(e -> {
				PrintHandler.print(primaryStage, drawingPanel.getCanvasPane());
			});
			
			exitMenuItem.setOnAction(e -> {
				if (drawingPanel.getCanvasPane().isDirty()) {
					SaveModificationsAlert s = new SaveModificationsAlert(true, drawingPanel.getCanvasPane(),
							primaryStage);
				} else {
					Platform.exit();
				}
			});
			
			undoMenu.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));
			undoMenu.setOnAction(e -> {
				UndoManager.getInstance().undo();
			});
			
			redoMenu.setAccelerator(new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN));
			redoMenu.setOnAction(e -> {
				UndoManager.getInstance().redo();
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
				MaterialListDBView materialListDBView = new MaterialListDBView();
			});
			root.setTop(menuBar);

			Platform.setImplicitExit(false);

			primaryStage.setOnCloseRequest(e -> {
				e.consume();
				if (drawingPanel.getCanvasPane().isDirty()) {
					SaveModificationsAlert s = new SaveModificationsAlert(true, drawingPanel.getCanvasPane(),
							primaryStage);
				} else {
					Platform.exit();
				}
			});

			rightClickMenu.getItems().add(delMenuItem);
			zoneTable = new ZoneTable(drawingPanel.controller);
			zoneTable.setOnMouseClicked(e -> {
				selectedZone = zoneTable.getSelectionModel().getSelectedItem();
				if (selectedZone != null) {
					if (e.getButton() == MouseButton.PRIMARY) {
						sprinklerListTable.setItems(controller.listSprinklerShapes(selectedZone));
					} else if (e.getButton() == MouseButton.SECONDARY) {
						rightClickMenu.show(primaryStage, e.getSceneX()+5, e.getSceneY()+5);
						
						delMenuItem.setOnAction(ev -> {
							drawingPanel.getCanvasPane().deletePipes(selectedZone);
							controller.removeZone(selectedZone);
							drawingPanel.getCanvasPane().setDirty(true);
							ev.consume();
						});
					}
				}
			});
			
			sprinklerListTable = new SprinklerListTable(selectedZone, drawingPanel.controller);
//			sprinklerListTable.setOnMouseClicked(e -> {
//				if (e.getButton() == MouseButton.PRIMARY) {
//					sprinklerDetailTable = new SprinklerDetailTable(
//							sprinklerListTable.getSelectionModel().getSelectedItem(), drawingPanel.controller);
//					root.setRight(sprinklerDetailTable);
//
//				}
//			});

			left.getChildren().addAll(zoneTable, sprinklerListTable);

			root.setLeft(left);
			primaryStage.setMaximized(true);

			primaryStage.show();

		} catch (Exception e) {
			utilities.Error.HandleException(e);
		}
	}

	public static void run() {
		launch("");
	}

	public static void exit() {
		Platform.exit();
	}

	public static void main(String[] args) {
	
		try {
			launch(args);
		} catch (Exception ex) {
			utilities.Error.HandleException(ex);
		}
	}

}
