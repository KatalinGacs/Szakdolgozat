package application;

import application.dbviews.AddSprinklerView;
import application.dbviews.SprinklerDBView;
import application.dbviews.SprinklerGroupDBView;
import controller.SprinklerController;
import controller.SprinklerControllerImpl;
import javafx.application.Application;
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
	
	private DrawingPanel drawingPanel = new DrawingPanel();
	
	private VBox left = new VBox();
	private SprinklerListTable sprinklerListTable;
	private ZoneTable zoneTable = new ZoneTable(drawingPanel.getCanvasPane());
	private Zone selectedZone;
	private SprinklerDetailTable sprinklerDetailTable;

	private MenuBar menuBar = new MenuBar();
	private Menu fileMenu = new Menu("F�jl");
	private MenuItem newMenuItem = new MenuItem("�j");
	private MenuItem openMenuItem = new MenuItem("Megnyit�s");
	private MenuItem saveMenuItem = new MenuItem("Ment�s");
	private MenuItem saveAsMenuItem = new MenuItem("Ment�s m�sk�nt");
	private MenuItem exportMenuItem = new MenuItem("Export�l�s pdf-be");
	private MenuItem exitMenuItem = new MenuItem("Kil�p�s");
	private Menu editMenu = new Menu("Szerkeszt�s");
	private MenuItem undoMenuItem = new MenuItem("Visszavon�s");
	private MenuItem redoMenuItem = new MenuItem("Ism�t");
	private Menu dbMenu = new Menu("Adatb�zis");
	private MenuItem sprinklerDbMenuItem = new MenuItem("Sz�r�fej adatb�zis");
	private MenuItem newSprinklerMenuItem = new MenuItem("Sz�r�fej hozz�ad�sa");
	private MenuItem sprinklerGroupDbMenuItem = new MenuItem("Sz�r�fej csoportok");
	private MenuItem materialDbMenuItem = new MenuItem("Anyag adatb�zis");
	private MenuItem newMaterialMenuItem = new MenuItem("Anyag hozz�ad�sa");
	
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root, 800, 600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			root.setCenter(drawingPanel);

			menuBar.getMenus().addAll(fileMenu, editMenu, dbMenu);
			fileMenu.getItems().addAll(newMenuItem, openMenuItem, saveMenuItem, saveAsMenuItem, exportMenuItem, exitMenuItem);
			editMenu.getItems().addAll(undoMenuItem, redoMenuItem);
			dbMenu.getItems().addAll(sprinklerGroupDbMenuItem, sprinklerDbMenuItem, newSprinklerMenuItem, materialDbMenuItem, newMaterialMenuItem);
			
			newMenuItem.setOnAction(e ->{
				//TODO ha volt v�ltoz�s, r�k�rdezzen, menti-e (vagy  ha nem tudom megoldani mindenk�pp r�k�rdezzen)
				// a canvas groupjait lepucolni
			});
			openMenuItem.setOnAction(e->{
				FileHandling.loadCanvas(drawingPanel.getCanvasPane(), primaryStage);
			});
			saveMenuItem.setOnAction(e->{
				FileHandling.saveCanvas(primaryStage, false);
			});
			saveAsMenuItem.setOnAction(e->{
				FileHandling.saveCanvas(primaryStage, true);
			});
			exportMenuItem.setOnAction(e->{
				//TODO
			});
			exitMenuItem.setOnAction(e->{
				//TODO r�k�rdezzen, hogy menti-e
				primaryStage.close();
			});
			undoMenuItem.setOnAction(e->{
				//TODO
			});
			redoMenuItem.setOnAction(e->{
				//TODO
			});
			sprinklerDbMenuItem.setOnAction(e -> {
				SprinklerDBView sprinklerDBView = new SprinklerDBView(); 
			});
			sprinklerGroupDbMenuItem.setOnAction( e-> {
				SprinklerGroupDBView sprinklerGroupDbView = new SprinklerGroupDBView();
			});
			newSprinklerMenuItem.setOnAction(e -> {
				AddSprinklerView addNewSprinklerView = new AddSprinklerView();
			});
			materialDbMenuItem.setOnAction(e->{
				//TODO
			});
			newMaterialMenuItem.setOnAction(e->{
				//TODO
			});
			root.setTop(menuBar);
			
			zoneTable.setOnMouseClicked(e -> {
				if (e.getButton() == MouseButton.PRIMARY) {
					selectedZone = zoneTable.getSelectionModel().getSelectedItem();
					sprinklerListTable.setItems(controller.listSprinklerShapes(selectedZone));
				}
			});
			sprinklerListTable = new SprinklerListTable(selectedZone);
			
			sprinklerListTable.setOnMouseClicked(e -> {
				if (e.getButton() == MouseButton.PRIMARY) {
					sprinklerDetailTable = new SprinklerDetailTable(sprinklerListTable.getSelectionModel().getSelectedItem());
					root.setRight(sprinklerDetailTable);
					
				}
			});

			
			
			left.getChildren().addAll(zoneTable,sprinklerListTable);
			
			
			
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
