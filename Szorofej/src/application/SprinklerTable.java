package application;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import controller.SprinklerController;
import controller.SprinklerControllerImpl;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
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
		radiusCol.setCellFactory(new DecimalCellFactory<Sprinkler, Double>());
		angleCol.setCellValueFactory(new PropertyValueFactory<Sprinkler, Double>("angle"));
		angleCol.setCellFactory(new DecimalCellFactory<Sprinkler, Double>());
		xCol.setCellValueFactory(new PropertyValueFactory<Sprinkler, Double>("centerX"));
		xCol.setCellFactory(new DecimalCellFactory<Sprinkler, Double>());
		yCol.setCellValueFactory(new PropertyValueFactory<Sprinkler, Double>("centerY"));
		yCol.setCellFactory(new DecimalCellFactory<Sprinkler, Double>());
		colorCol.setCellValueFactory(new PropertyValueFactory<Sprinkler, String>("color"));

		getColumns().addAll(radiusCol, angleCol, xCol, yCol, colorCol);
		
		setItems(controller.listSprinklers());
		


	}

	public class DecimalCellFactory<S, T extends Number> implements Callback<TableColumn<S, T>, TableCell<S, T>> {

	    @Override
	    public TableCell<S, T> call(TableColumn<S, T> param) {
	        return new TableCell<S, T>() {

	        	@Override
			    protected void updateItem(T value, boolean empty) {
					super.updateItem(value, empty) ;
			        if (empty) {
			            setText("");
			        } else {
			            setText(String.format(" %.2f", value.doubleValue()));
			        }
				}
	        };
	    }
	}
	
}
