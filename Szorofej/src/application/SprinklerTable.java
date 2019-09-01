package application;

import application.common.DecimalCellFactory;
import controller.SprinklerController;
import controller.SprinklerControllerImpl;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.bean.SprinklerShape;

public class SprinklerTable extends TableView<SprinklerShape> {

	SprinklerController controller = new SprinklerControllerImpl();
	
	TableColumn<SprinklerShape, Double> radiusCol = new TableColumn<>("Sugár");
	TableColumn<SprinklerShape, Double> angleCol = new TableColumn<>("Szög");
	TableColumn<SprinklerShape, Double> xCol = new TableColumn<>("X");
	TableColumn<SprinklerShape, Double> yCol = new TableColumn<>("Y");
	TableColumn<SprinklerShape, String> colorCol = new TableColumn<>("Szín");

	public SprinklerTable() {

		radiusCol.setCellValueFactory(new PropertyValueFactory<SprinklerShape, Double>("radius"));
		radiusCol.setCellFactory(new DecimalCellFactory<SprinklerShape, Double>());
		angleCol.setCellValueFactory(new PropertyValueFactory<SprinklerShape, Double>("angle"));
		angleCol.setCellFactory(new DecimalCellFactory<SprinklerShape, Double>());
		xCol.setCellValueFactory(new PropertyValueFactory<SprinklerShape, Double>("centerX"));
		xCol.setCellFactory(new DecimalCellFactory<SprinklerShape, Double>());
		yCol.setCellValueFactory(new PropertyValueFactory<SprinklerShape, Double>("centerY"));
		yCol.setCellFactory(new DecimalCellFactory<SprinklerShape, Double>());
		colorCol.setCellValueFactory(new PropertyValueFactory<SprinklerShape, String>("color"));

		getColumns().addAll(radiusCol, angleCol, xCol, yCol, colorCol);
		
		setItems(controller.listSprinklerShapes());
		
		

	}
	
}
