package application;

import application.common.Common;
import application.common.DecimalCellFactory;
import controller.SprinklerController;
import controller.SprinklerControllerImpl;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.bean.SprinklerGroup;
import model.bean.SprinklerType;

public class DrawingPanel extends VBox {

	private SprinklerController controller = new SprinklerControllerImpl();

	private TabPane tabPane = new TabPane();
	private Tab borderTab = new Tab("Alaprajz");
	private Tab sprinklerTab = new Tab("Szorofej");
	private HBox borderTabElements = new HBox();
	private HBox sprinklerTabElements = new HBox();

	private Button setSprinklerBtn = new Button("Szórófej kiválasztása");

	private Spinner<Integer> borderLineWidth = new Spinner<Integer>(1, 5, 3);
	private ToggleButton borderLineBtn = new ToggleButton("Határvonal");
	private ToggleButton borderRectangleBtn = new ToggleButton("Téglalap");
	private ToggleButton borderCircleBtn = new ToggleButton("Kör");
	private ToggleGroup borderButtons = new ToggleGroup();
	protected ColorPicker borderColor = new ColorPicker();

	private CanvasPane canvasPane = new CanvasPane();
	private ZoomableScrollPane scrollPane = new ZoomableScrollPane(canvasPane);

	private HBox viewElements = new HBox();
	private ToggleButton showGrid = new ToggleButton("Rács");
	private ToggleButton showArcs = new ToggleButton("Szóráskép");

	public DrawingPanel() {

		getChildren().add(tabPane);
		tabPane.getTabs().addAll(borderTab, sprinklerTab);
		sprinklerTab.setContent(sprinklerTabElements);
		sprinklerTabElements.getChildren().add(setSprinklerBtn);

		borderTab.setContent(borderTabElements);
		borderButtons.getToggles().addAll(borderLineBtn, borderRectangleBtn, borderCircleBtn);
		borderColor.setValue(Color.LIMEGREEN);
		borderTabElements.getChildren().addAll(borderColor, borderLineWidth, borderLineBtn, borderRectangleBtn,
				borderCircleBtn);

		tabPane.setMinHeight(50);

		scrollPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		scrollPane.setFitToWidth(true);
		scrollPane.setPrefHeight(600);
		scrollPane.setMinHeight(USE_COMPUTED_SIZE);
		getChildren().add(scrollPane);

		viewElements.setMinHeight(25);
		showGrid.setSelected(true);
		showArcs.setSelected(true);
		viewElements.getChildren().addAll(showGrid, showArcs);
		getChildren().add(viewElements);

		borderTab.setOnSelectionChanged(e -> {
			borderButtons.selectToggle(null);
			canvasPane.endLineDrawing();
		});

		setSprinklerBtn.setOnAction(e -> {
			setSprinklerAttributes();
		});

		canvasPane.setOnMouseClicked(e -> {
			canvasPane.requestFocus();
			if (borderButtons.getSelectedToggle() == null && canvasPane.sprinklerAttributesSet
					&& e.getButton() == MouseButton.PRIMARY) {
				canvasPane.drawNewSprinkler(e);
			} else if (e.getButton() == MouseButton.PRIMARY && borderButtons.getSelectedToggle() == borderLineBtn
					&& canvasPane.borderDrawingOn == false) {
				canvasPane.borderDrawingOn = true;
				canvasPane.startDrawingBorder(e);
			} else if (e.getButton() == MouseButton.PRIMARY && borderButtons.getSelectedToggle() == borderLineBtn
					&& canvasPane.borderDrawingOn) {
				canvasPane.drawBorderLine(e, borderColor.getValue(), borderLineWidth.getValue());

			} else if (e.getButton() == MouseButton.SECONDARY) {
				canvasPane.selectElement(e);
			}
			e.consume();
			canvasPane.requestFocus();
		});

		canvasPane.setOnMousePressed(e -> {
			canvasPane.requestFocus();
			if ((borderButtons.getSelectedToggle() == borderRectangleBtn
					|| borderButtons.getSelectedToggle() == borderCircleBtn) && e.getButton() == MouseButton.PRIMARY) {
				canvasPane.borderDrawingOn = true;
				canvasPane.startDrawingBorder(e);
			}
		});

		canvasPane.setOnMouseReleased(e -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				if (borderButtons.getSelectedToggle() == borderRectangleBtn) {
					canvasPane.drawBorderRectanlge(e, borderColor.getValue(), borderLineWidth.getValue());
					canvasPane.borderDrawingOn = false;
				} else if (borderButtons.getSelectedToggle() == borderCircleBtn) {
					canvasPane.drawBorderCircle(e, borderColor.getValue(), borderLineWidth.getValue());
					canvasPane.borderDrawingOn = false;
				}
			}
		});

		canvasPane.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
			canvasPane.pressedKey = null;
			canvasPane.setCursor(Cursor.DEFAULT);
		});

		canvasPane.addEventFilter(KeyEvent.KEY_PRESSED, e -> {

			canvasPane.pressedKey = e.getCode();
			if (canvasPane.pressedKey.equals(KeyCode.ESCAPE)) {
				canvasPane.endSprinklerDrawing();
				canvasPane.endLineDrawing();
				borderButtons.selectToggle(null);
			}
		});

		canvasPane.setOnMouseMoved(e -> {
			canvasPane.showFocusCircle(e);
			canvasPane.showTempLine(e);
			Point2D mousePoint = new Point2D(e.getX(), e.getY());

			if (canvasPane.pressedKey == KeyCode.SHIFT) {
				for (Shape border : canvasPane.borderShapes) {
					if (border.contains(mousePoint)) {
						canvasPane.setCursor(Cursor.CROSSHAIR);
						break;
					} else
						canvasPane.setCursor(Cursor.DEFAULT);
				}
			}

			if (borderButtons.getSelectedToggle() == borderLineBtn && canvasPane.borderDrawingOn)
				canvasPane.showTempBorderLine(e, borderColor.getValue());
		});

		canvasPane.setOnMouseDragged(e -> {
			if (canvasPane.borderDrawingOn && borderButtons.getSelectedToggle() == borderRectangleBtn)
				canvasPane.showtempBorderRectanlge(e, borderColor.getValue());
			else if (canvasPane.borderDrawingOn && borderButtons.getSelectedToggle() == borderCircleBtn)
				canvasPane.showTempBorderCircle(e, borderColor.getValue());
		});

		showGrid.setOnAction(e -> {
			if (showGrid.isSelected())
				Common.showLayer(canvasPane.gridLayer);
			else
				Common.hideLayer(canvasPane.gridLayer);
		});
		showArcs.setOnAction(e -> {
			if (showArcs.isSelected())
				Common.showLayer(canvasPane.sprinklerArcLayer);
			else
				Common.hideLayer(canvasPane.sprinklerArcLayer);
		});
	}

	private void setSprinklerAttributes() {
		Stage sprinklerInfoStage = new Stage();
		VBox root = new VBox();
		Scene scene = new Scene(root, 800, 400);

		HBox groupPane = new HBox();
		Text sprinklerGroupText = new Text("Szórófej csoport");
		ChoiceBox<SprinklerGroup> sprinklerGroupChoiceBox = new ChoiceBox<SprinklerGroup>();
		sprinklerGroupChoiceBox.setItems(controller.listSprinklerGroups());
		sprinklerGroupChoiceBox.setValue(controller.listSprinklerGroups().get(0));
		groupPane.getChildren().addAll(sprinklerGroupText, sprinklerGroupChoiceBox);

		TableView<SprinklerType> tableView = new TableView<SprinklerType>();
		TableColumn<SprinklerType, String> nameCol = new TableColumn<>("Név");
		TableColumn<SprinklerType, Double> minRadiusCol = new TableColumn<>("Min. sugár (m)");
		TableColumn<SprinklerType, Double> maxRadiusCol = new TableColumn<>("Max. sugár (m)");
		TableColumn<SprinklerType, Double> minAngleCol = new TableColumn<>("Min. szög (fok)");
		TableColumn<SprinklerType, Double> maxAngleCol = new TableColumn<>("Max. szög (fok)");
		TableColumn<SprinklerType, Boolean> fixWaterConsumptionCol = new TableColumn<>("Fix vízfogyasztás");
		TableColumn<SprinklerType, Double> waterConsumptionCol = new TableColumn<>("Vízfogyasztás (l/min)");
		TableColumn<SprinklerType, Double> minPressureCol = new TableColumn<>("Min. víznyomás (bar)");

		tableView.getColumns().addAll(nameCol, minRadiusCol, maxRadiusCol, minAngleCol, maxAngleCol,
				fixWaterConsumptionCol, waterConsumptionCol, minPressureCol);
		nameCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, String>("name"));
		minRadiusCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, Double>("minRadius"));
		minRadiusCol.setCellFactory(new DecimalCellFactory<SprinklerType, Double>());
		maxRadiusCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, Double>("maxRadius"));
		maxRadiusCol.setCellFactory(new DecimalCellFactory<SprinklerType, Double>());
		minAngleCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, Double>("minAngle"));
		minAngleCol.setCellFactory(new DecimalCellFactory<SprinklerType, Double>());
		maxAngleCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, Double>("maxAngle"));
		maxAngleCol.setCellFactory(new DecimalCellFactory<SprinklerType, Double>());
		fixWaterConsumptionCol
				.setCellValueFactory(new PropertyValueFactory<SprinklerType, Boolean>("fixWaterConsumption"));
		waterConsumptionCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, Double>("waterConsumption"));
		waterConsumptionCol.setCellFactory(new DecimalCellFactory<SprinklerType, Double>());
		minPressureCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, Double>("minPressure"));
		minPressureCol.setCellFactory(new DecimalCellFactory<SprinklerType, Double>());

		sprinklerGroupChoiceBox.setOnAction(e -> {
			tableView.getItems().clear();
			tableView.setItems(
					//TODO ez vmiért nullpointerexceptiont dob, de miért, kijavítani
					controller.listSprinklerTypeByGroup(sprinklerGroupChoiceBox.getSelectionModel().getSelectedItem()));
		});

		Text radiusText = new Text("Sugár: ");
		TextField radiusField = new TextField();
		Text meterText = new Text("méter");
		sprinklerInfoStage.setScene(scene);
		Button ok = new Button("OK");
		root.getChildren().addAll(groupPane, tableView, radiusText, radiusField, meterText, ok);

		ok.setOnAction(e -> {
			if (radiusField.getText() == null && radiusField.getText().trim().isEmpty()) {
				Common.showAlert("Add meg a szórófej sugarát!");
			} else {
				try {
					double radius = Double.parseDouble(radiusField.getText());
					SprinklerType type = tableView.getSelectionModel().getSelectedItem();
					// a megrendelõ kérésére csak azt ellenõrzi, hogyha a megengedettnél nagyobb
					// sugárra próbálja állítani, fizikailag lehetséges kisebb a gyártó által
					// megadottnál kisebb szögre állítani és néha erre van szükség
					if (radius > tableView.getSelectionModel().getSelectedItem().getMaxRadius()) {

						Common.showAlert("A sugár nagyobb, mint az ennél a típusnál megengedett legnagyobb sugár");
					} else {
						canvasPane.sprinklerRadius = radius * Common.pixelPerMeter;
						canvasPane.sprinklerAttributesSet = true;
						canvasPane.sprinklerType = type;
					}

					canvasPane.requestFocus();
				} catch (NumberFormatException ex) {
					Common.showAlert("Számokban add meg a szórófej sugarát!");
				}
			}
			sprinklerInfoStage.close();
		});
		sprinklerInfoStage.show();
	}

	public CanvasPane getCanvasPane() {
		return canvasPane;
	}

}
