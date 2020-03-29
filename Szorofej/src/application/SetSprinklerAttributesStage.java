package application;

import application.CanvasPane.Use;
import application.common.Common;
import application.common.DecimalCellFactory;
import controller.SprinklerController;
import controller.SprinklerControllerImpl;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.bean.SprinklerGroup;
import model.bean.SprinklerType;

public class SetSprinklerAttributesStage extends Stage {

	private SprinklerController controller = new SprinklerControllerImpl();

	private VBox sprinklerInfoRoot = new VBox();
	private Scene sprinklerInfoScene = new Scene(sprinklerInfoRoot);
	private HBox sprinklerGroupPane = new HBox();
	private Text sprinklerGroupText = new Text("Szórófej csoport");
	private ChoiceBox<SprinklerGroup> sprinklerGroupChoiceBox = new ChoiceBox<SprinklerGroup>();
	private Button ok = new Button("OK");
	private TableView<SprinklerType> tableView = new TableView<SprinklerType>();
	private TableColumn<SprinklerType, String> nameCol = new TableColumn<>("Név");
	private TableColumn<SprinklerType, Double> minRadiusCol = new TableColumn<>("Min. sugár (m)");
	private TableColumn<SprinklerType, Double> maxRadiusCol = new TableColumn<>("Max. sugár (m)");
	private TableColumn<SprinklerType, Double> minAngleCol = new TableColumn<>("Min. szög (fok)");
	private TableColumn<SprinklerType, Double> maxAngleCol = new TableColumn<>("Max. szög (fok)");
	private TableColumn<SprinklerType, Double> waterConsumptionCol = new TableColumn<>("Vízfogyasztás (l/min)");
	private Text radiusText = new Text("Sugár: ");
	private TextField radiusField = new TextField();
	private Text meterText = new Text("méter");
	private HBox radiusBox = new HBox();

	public SetSprinklerAttributesStage(CanvasPane canvasPane) {
		initModality(Modality.APPLICATION_MODAL);
		setAlwaysOnTop(true);
		setTitle("Szórófej kiválasztása");
		
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		
		radiusBox.getChildren().addAll(radiusText, radiusField, meterText);
		radiusBox.setAlignment(Pos.CENTER_LEFT);
		radiusBox.setPadding(new Insets(5));
		radiusBox.setSpacing(5);
		sprinklerGroupChoiceBox.setItems(controller.listSprinklerGroups());
		sprinklerGroupChoiceBox.getSelectionModel().selectFirst();
		sprinklerGroupPane.getChildren().addAll(sprinklerGroupText, sprinklerGroupChoiceBox);
		sprinklerGroupPane.setAlignment(Pos.CENTER_LEFT);
		setScene(sprinklerInfoScene);
		sprinklerInfoRoot.getChildren().addAll(sprinklerGroupPane, tableView, radiusBox, ok);
		sprinklerInfoRoot.setPadding(new Insets(10));

		tableView.getColumns().addAll(nameCol, minRadiusCol, maxRadiusCol, minAngleCol, maxAngleCol,
				 waterConsumptionCol);
		nameCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, String>("name"));
		minRadiusCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, Double>("minRadius"));
		minRadiusCol.setCellFactory(new DecimalCellFactory<SprinklerType, Double>());
		maxRadiusCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, Double>("maxRadius"));
		maxRadiusCol.setCellFactory(new DecimalCellFactory<SprinklerType, Double>());
		minAngleCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, Double>("minAngle"));
		minAngleCol.setCellFactory(new DecimalCellFactory<SprinklerType, Double>());
		maxAngleCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, Double>("maxAngle"));
		maxAngleCol.setCellFactory(new DecimalCellFactory<SprinklerType, Double>());
		waterConsumptionCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, Double>("waterConsumption"));
		waterConsumptionCol.setCellFactory(new DecimalCellFactory<SprinklerType, Double>());

		tableView.setItems(
				controller.listSprinklerTypeByGroup(sprinklerGroupChoiceBox.getSelectionModel().getSelectedItem()));

		sprinklerGroupChoiceBox.setOnAction(e -> {
			if (sprinklerGroupChoiceBox.getSelectionModel().getSelectedItem() != null) {
				tableView.getItems().clear();
				tableView.setItems(controller
						.listSprinklerTypeByGroup(sprinklerGroupChoiceBox.getSelectionModel().getSelectedItem()));
			}
		});
		
		tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (sprinklerGroupChoiceBox.getSelectionModel().getSelectedItem() != null) {
				radiusField.setText(String.valueOf(newSelection.getMaxRadius()));	
			}
		});

		ok.setOnAction(e -> {
			setAttributes(canvasPane);
		});
		
		tableView.setOnMousePressed(e -> {
			if ( e.isPrimaryButtonDown() && e.getClickCount() == 2) {
				setAttributes(canvasPane);
			}
		});
	}
	
	private void setAttributes(CanvasPane canvasPane) {
		if (radiusField.getText() == null || radiusField.getText().trim().isEmpty()) {
			Common.showAlert("Add meg a szórófej sugarát!");
		} else {
			try {
				double radius = Double.parseDouble(radiusField.getText());
				SprinklerType type;
				if (tableView.getSelectionModel().isEmpty()) {
					Common.showAlert("Nincs szórófej kijelölve");
				} else {
					type = tableView.getSelectionModel().getSelectedItem();
					if (radius > tableView.getSelectionModel().getSelectedItem().getMaxRadius()) {
						Common.showAlert("A sugár nagyobb, mint az ennél a típusnál megengedett legnagyobb sugár");
					} else {
						SprinklerDrawing.sprinklerRadius = radius * Common.pixelPerMeter;
						canvasPane.sprinklerAttributesSet = true;
						SprinklerDrawing.sprinklerType = type;
						canvasPane.stateOfCanvasUse = Use.SPRINKLERDRAWING;
					}
				}
				requestFocus();
			} catch (NumberFormatException ex) {
				Common.showAlert("Számokban add meg a szórófej sugarát!");
			}
		}
		close();
	}
}
