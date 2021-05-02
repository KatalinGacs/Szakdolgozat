package application.dbviews;

import controller.SprinklerController;
import controller.SprinklerControllerImpl;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import model.DbException;
import model.bean.SprinklerType;
import utilities.Common;

public class SprinklerDBView {

	private SprinklerController controller = new SprinklerControllerImpl();

	private Stage stage = new Stage();
	private VBox root = new VBox();
	private Scene scene = new Scene(root);

	private TableView<SprinklerType> tableView = new TableView<>();
	private TableColumn<SprinklerType, String> nameCol = new TableColumn<>("Név");
	private TableColumn<SprinklerType, Double> minRadiusCol = new TableColumn<>("Min. sugár (m)");
	private TableColumn<SprinklerType, Double> maxRadiusCol = new TableColumn<>("Max. sugár (m)");
	private TableColumn<SprinklerType, Double> minAngleCol = new TableColumn<>("Min. szög (fok)");
	private TableColumn<SprinklerType, Double> maxAngleCol = new TableColumn<>("Max. szög (fok)");
	private TableColumn<SprinklerType, Boolean> fixWaterConsumptionCol = new TableColumn<>("Rotoros");
	private TableColumn<SprinklerType, Double> waterConsumptionCol = new TableColumn<>("Vízfogyasztás (l/min)");
	private TableColumn<SprinklerType, Double> minPressureCol = new TableColumn<>("Min. víznyomás (bar)");
	private TableColumn<SprinklerType, String> sprinklerGroupCol = new TableColumn<>("Csoport");

	private Button delBtn = new Button("Törlés");

	public SprinklerDBView() {

		try {
			root.getChildren().addAll(tableView, delBtn);

			tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
			tableView.getColumns().addAll(nameCol, minRadiusCol, maxRadiusCol, minAngleCol, maxAngleCol,
					fixWaterConsumptionCol, waterConsumptionCol, minPressureCol, sprinklerGroupCol);
			stage.setScene(scene);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("Szórófej adatbázis");
			nameCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, String>("name"));
			minRadiusCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, Double>("minRadius"));
			maxRadiusCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, Double>("maxRadius"));
			minAngleCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, Double>("minAngle"));
			maxAngleCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, Double>("maxAngle"));
			fixWaterConsumptionCol
					.setCellValueFactory(new PropertyValueFactory<SprinklerType, Boolean>("fixWaterConsumption"));
			waterConsumptionCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, Double>("waterConsumption"));
			minPressureCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, Double>("minPressure"));
			sprinklerGroupCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, String>("sprinklerGroup"));
			try {
				tableView.setItems(controller.listSprinklerTypes());
			} catch (DbException ex) {
				Common.showAlert(ex.getMessage());
			}

			delBtn.setOnAction(e -> {
				ObservableList<SprinklerType> selected = tableView.getSelectionModel().getSelectedItems();
				try {
					for (SprinklerType s : selected) {
						controller.deleteSprinklerType(s);
					}
					tableView.setItems(controller.listSprinklerTypes());
				} catch (DbException ex) {
					Common.showAlert(ex.getMessage());
				}
			});
			
			tableView.setEditable(true);
			minRadiusCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
			minRadiusCol.setOnEditCommit(event -> {
			    SprinklerType s = event.getRowValue();
			    s.setWaterCounsumption(event.getNewValue());
			    try {
					controller.updateSprinklerData("minRadius", event.getNewValue(), s.getName());
				} catch (DbException ex) {
					Common.showAlert(ex.getMessage());
				}
			});
			maxRadiusCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
			maxRadiusCol.setOnEditCommit(event -> {
			    SprinklerType s = event.getRowValue();
			    s.setWaterCounsumption(event.getNewValue());
			    try {
					controller.updateSprinklerData("maxRadius", event.getNewValue(), s.getName());
				} catch (DbException ex) {
					Common.showAlert(ex.getMessage());
				}
			});
			minAngleCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
			minAngleCol.setOnEditCommit(event -> {
			    SprinklerType s = event.getRowValue();
			    s.setWaterCounsumption(event.getNewValue());
			    try {
					controller.updateSprinklerData("minAngle", event.getNewValue(), s.getName());
				} catch (DbException ex) {
					Common.showAlert(ex.getMessage());
				}
			});
			maxAngleCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
			maxAngleCol.setOnEditCommit(event -> {
			    SprinklerType s = event.getRowValue();
			    s.setWaterCounsumption(event.getNewValue());
			    try {
					controller.updateSprinklerData("maxAngle", event.getNewValue(), s.getName());
				} catch (DbException ex) {
					Common.showAlert(ex.getMessage());
				}
			});
			waterConsumptionCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
			waterConsumptionCol.setOnEditCommit(event -> {
			    SprinklerType s = event.getRowValue();
			    s.setWaterCounsumption(event.getNewValue());
			    try {
					controller.updateSprinklerData("waterConsumption", event.getNewValue(), s.getName());
				} catch (DbException ex) {
					Common.showAlert(ex.getMessage());
				}
			});
			minPressureCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
			minPressureCol.setOnEditCommit(event -> {
			    SprinklerType s = event.getRowValue();
			    s.setWaterCounsumption(event.getNewValue());
			    try {
					controller.updateSprinklerData("minPressure", event.getNewValue(), s.getName());
				} catch (DbException ex) {
					Common.showAlert(ex.getMessage());
				}
			});

			fixWaterConsumptionCol.setCellFactory(col -> new TableCell<SprinklerType, Boolean>() {
			    @Override
			    protected void updateItem(Boolean item, boolean empty) {
			        super.updateItem(item, empty) ;
			        setText(empty ? null : item ? "✓" : "✗" );
			    }
			});
			
			stage.show();
		} catch (Exception ex) {
			utilities.Error.HandleException(ex);
		}
	}

}
