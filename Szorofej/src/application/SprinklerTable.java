package application;

import controller.SprinklerController;
import controller.SprinklerControllerImpl;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.bean.Sprinkler;

public class SprinklerTable extends TableView<Sprinkler> {

	SprinklerController controller = new SprinklerControllerImpl();

	TableColumn<Sprinkler, Double> radiusCol = new TableColumn<>("Sugár");
	TableColumn<Sprinkler, Double> angleCol = new TableColumn<>("Szög");
	TableColumn<Sprinkler, Double> xCol = new TableColumn<>("X");
	TableColumn<Sprinkler, Double> yCol = new TableColumn<>("Y");
	TableColumn<Sprinkler, String> colorCol = new TableColumn<>("Szín");

	public SprinklerTable() {

		radiusCol.setCellValueFactory(new PropertyValueFactory<Sprinkler, Double>("radius"));
		angleCol.setCellValueFactory(new PropertyValueFactory<Sprinkler, Double>("angle"));
		xCol.setCellValueFactory(new PropertyValueFactory<Sprinkler, Double>("centerX"));
		yCol.setCellValueFactory(new PropertyValueFactory<Sprinkler, Double>("centerY"));
		colorCol.setCellValueFactory(new PropertyValueFactory<Sprinkler, String>("color"));

		getColumns().addAll(radiusCol, angleCol, xCol, yCol, colorCol);
		
		setItems(controller.listSprinklers());
		


	}

}
