package application;

import controller.SprinklerController;
import controller.SprinklerControllerImpl;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.bean.SprinklerShape;
import model.bean.SprinklerType;

public class SprinklerTable extends TableView<SprinklerShape> {

	SprinklerController controller = new SprinklerControllerImpl();
	

	TableColumn<SprinklerShape, SprinklerType> nameCol = new TableColumn<>("Szórófej");

	public SprinklerTable() {

		nameCol.setCellValueFactory(new PropertyValueFactory<SprinklerShape, SprinklerType>("sprinkler"));

		getColumns().addAll(nameCol);
		
		setItems(controller.listSprinklerShapes());
		
		

	}
	
}
