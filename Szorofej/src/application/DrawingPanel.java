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

	private Button setSprinklerBtn = new Button("Sz�r�fej kiv�laszt�sa");

	private Spinner<Integer> borderLineWidth = new Spinner<Integer>(1, 5, 3);
	private ToggleButton borderLineBtn = new ToggleButton("Hat�rvonal");
	private ToggleButton borderRectangleBtn = new ToggleButton("T�glalap");
	private ToggleButton borderCircleBtn = new ToggleButton("K�r");
	private ToggleGroup borderButtons = new ToggleGroup();
	protected ColorPicker borderColor = new ColorPicker();

	private CanvasPane canvasPane = new CanvasPane();
	private ZoomableScrollPane scrollPane = new ZoomableScrollPane(canvasPane);

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

		scrollPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		scrollPane.setFitToWidth(true);
		scrollPane.setPrefHeight(600);
		scrollPane.setMinHeight(USE_COMPUTED_SIZE);

		getChildren().add(scrollPane);

		borderTab.setOnSelectionChanged(e -> {
			borderButtons.selectToggle(null);
		});

		borderLineBtn.setOnMouseClicked(e -> {
			Polyline line = new Polyline();
			line.setStroke(borderColor.getValue());
			line.setStrokeWidth(borderLineWidth.getValue());
			canvasPane.borderLines.add(line);
			canvasPane.borderShape.add(line);
			canvasPane.group.getChildren().add(line);

			if (canvasPane.borderDrawingOn)
				canvasPane.borderDrawingOn = false;
		});

		setSprinklerBtn.setOnAction(e -> {
			setSprinklerAttributes();
		});

		canvasPane.setOnMouseClicked(e -> {
			canvasPane.requestFocus();
			if (borderButtons.getSelectedToggle() == null && canvasPane.sprinklerAttributesSet
					&& e.getButton() == MouseButton.PRIMARY) {
				canvasPane.drawNewSprinkler(e);
			} else if (borderButtons.getSelectedToggle() == borderLineBtn) {
				canvasPane.borderLines.get(canvasPane.borderLines.size() - 1).getPoints().addAll(e.getX(), e.getY());
			} else if (e.getButton() == MouseButton.SECONDARY)
				canvasPane.selectElement(e);
			e.consume();
			canvasPane.requestFocus();
		});

		canvasPane.setOnMousePressed(e -> {
			canvasPane.requestFocus();
			if (borderButtons.getSelectedToggle() != null && e.getButton() == MouseButton.PRIMARY) {
				canvasPane.borderDrawingOn = true;
				canvasPane.startDrawingBorder(e);

			}
		});

		canvasPane.setOnMouseReleased(e -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				if (borderButtons.getSelectedToggle() == borderRectangleBtn) {
					canvasPane.drawBorderRectanlge(e, borderColor.getValue(), borderLineWidth.getValue());
				} else if (borderButtons.getSelectedToggle() == borderCircleBtn) {
					canvasPane.drawBorderCircle(e, borderColor.getValue(), borderLineWidth.getValue());
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
			}
		});

		canvasPane.setOnMouseMoved(e -> {
			canvasPane.showFocusCircle(e);
			canvasPane.showTempLine(e);
			Point2D mousePoint = new Point2D(e.getX(), e.getY());

			if (canvasPane.pressedKey == KeyCode.SHIFT) {
				for (Shape border : canvasPane.borderShape) {
					if (border.contains(mousePoint)) {
						canvasPane.setCursor(Cursor.CROSSHAIR);
						break;
					} else
						canvasPane.setCursor(Cursor.DEFAULT);
				}
			}
		});

		canvasPane.setOnMouseDragged(e -> {
			if (canvasPane.borderDrawingOn && borderButtons.getSelectedToggle() == borderRectangleBtn)
				canvasPane.showtempRectanlge(e, borderColor.getValue());
			else if (canvasPane.borderDrawingOn && borderButtons.getSelectedToggle() == borderCircleBtn)
				canvasPane.showTempCircle(e, borderColor.getValue());
			else if (canvasPane.borderDrawingOn && borderButtons.getSelectedToggle() == borderLineBtn
					&& canvasPane.borderLines.size() > 0)
				canvasPane.drawBorderline(e, canvasPane.borderLines.get(canvasPane.borderLines.size() - 1),
						borderColor.getValue());
		});
	}

	private void setSprinklerAttributes() {
		Stage sprinklerInfoStage = new Stage();
		VBox root = new VBox();
		Scene scene = new Scene(root, 800, 400);

		HBox groupPane = new HBox();
		Text sprinklerGroupText = new Text("Sz�r�fej csoport");
		ChoiceBox<SprinklerGroup> sprinklerGroupChoiceBox = new ChoiceBox<SprinklerGroup>();
		sprinklerGroupChoiceBox.setValue(controller.listSprinklerGroups().get(0));
		groupPane.getChildren().addAll(sprinklerGroupText, sprinklerGroupChoiceBox);
		
		sprinklerGroupChoiceBox.setItems(controller.listSprinklerGroups());

		TableView<SprinklerType> tableView = new TableView<SprinklerType>();
		TableColumn<SprinklerType, String> nameCol = new TableColumn<>("N�v");
		TableColumn<SprinklerType, Double> minRadiusCol = new TableColumn<>("Min. sug�r (m)");
		TableColumn<SprinklerType, Double> maxRadiusCol = new TableColumn<>("Max. sug�r (m)");
		TableColumn<SprinklerType, Double> minAngleCol = new TableColumn<>("Min. sz�g (fok)");
		TableColumn<SprinklerType, Double> maxAngleCol = new TableColumn<>("Max. sz�g (fok)");
		TableColumn<SprinklerType, Boolean> fixWaterConsumptionCol = new TableColumn<>("Fix v�zfogyaszt�s");
		TableColumn<SprinklerType, Double> waterConsumptionCol = new TableColumn<>("V�zfogyaszt�s (l/min)");
		TableColumn<SprinklerType, Double> minPressureCol = new TableColumn<>("Min. v�znyom�s (bar)");

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
		fixWaterConsumptionCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, Boolean>("fixWaterConsumption"));
		waterConsumptionCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, Double>("waterConsumption"));
		waterConsumptionCol.setCellFactory(new DecimalCellFactory<SprinklerType, Double>());
		minPressureCol.setCellValueFactory(new PropertyValueFactory<SprinklerType, Double>("minPressure"));
		minPressureCol.setCellFactory(new DecimalCellFactory<SprinklerType, Double>());
		
		sprinklerGroupChoiceBox.getSelectionModel().selectedIndexProperty().addListener(l -> {
			tableView.setItems(controller.listSprinklerTypeByGroup(sprinklerGroupChoiceBox.getValue()));
		});
		
		Text radiusText = new Text("Sug�r: ");
		TextField radiusField = new TextField();
		Text meterText = new Text("m�ter");
		sprinklerInfoStage.setScene(scene);
		Button ok = new Button("OK");
		root.getChildren().addAll(groupPane, tableView, radiusText, radiusField, meterText, ok);

		ok.setOnAction(e -> {
			if (radiusField.getText() == null && radiusField.getText().trim().isEmpty()) {
				Common.showAlert("Add meg a sz�r�fej sugar�t!");
			} 
			//TODO ellen�rizni, hogy a be�ll�tott sug�r �s sz�g megfelel-e a t�pusnak
			else {
				try {
					canvasPane.sprinklerRadius = Double.parseDouble(radiusField.getText()) * 50;
					canvasPane.sprinklerAttributesSet = true;
					canvasPane.sprinklerType = tableView.getSelectionModel().getSelectedItem();
					canvasPane.requestFocus();
				} catch (NumberFormatException ex) {
					Common.showAlert("Sz�mokban add meg a sz�r�fej sugar�t!");
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