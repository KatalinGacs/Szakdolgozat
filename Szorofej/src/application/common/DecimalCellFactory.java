package application.common;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

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
