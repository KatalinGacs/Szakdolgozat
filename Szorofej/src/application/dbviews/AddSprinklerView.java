package application.dbviews;

import java.util.Arrays;
import java.util.List;

import application.common.Common;
import controller.SprinklerController;
import controller.SprinklerControllerImpl;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.DbException;
import model.bean.SprinklerGroup;
import model.bean.SprinklerType;

public class AddSprinklerView {

	private SprinklerController controller = new SprinklerControllerImpl();

	private Stage addSprinklerDbStage = new Stage();
	private GridPane root = new GridPane();
	private Scene scene = new Scene(root);

	private ObservableList<SprinklerGroup> sprinklerGroups;

	private Text nameText = new Text("N�v");
	private TextField nameField = new TextField();
	private Text minRadiusText = new Text("Min. sug�r (m)");
	private TextField minRadiusField = new TextField();
	private Text maxRadiusText = new Text("Max. sug�r (m)");
	private TextField maxRadiusField = new TextField();
	private Text minAngleText = new Text("Min. sz�g (fok)");
	private TextField minAngleField = new TextField();
	private Text maxAngleText = new Text("Max. sz�g (fok)");
	private TextField maxAngleField = new TextField();
	private Text fixWaterConsumptionText = new Text("Rotoros? ");
	private CheckBox fixWaterConsumptionCheckBox = new CheckBox();
	private Text waterConsumptionText = new Text("V�zfogyaszt�s (l/min)");
	private TextField waterConsumptionfField = new TextField();
	private Text minPressureText = new Text("Min. sz�ks�ges v�znyom�s (bar)");
	private TextField minPressureField = new TextField();
	private Text sprinklerGroupText = new Text("Sz�r�fej csoport");
	private ChoiceBox<SprinklerGroup> sprinklerGroupChoiceBox;

	private Button addBtn = new Button("Hozz�ad");

	public AddSprinklerView() {
		try {
			sprinklerGroups = controller.listSprinklerGroups();
			sprinklerGroupChoiceBox = new ChoiceBox<SprinklerGroup>(sprinklerGroups);
		} catch (DbException ex) {
			Common.showAlert(ex.getMessage());
		}
		addSprinklerDbStage.setScene(scene);
		addSprinklerDbStage.setTitle("Sz�r�fej hozz�ad�sa");
		root.addColumn(0, nameText, minRadiusText, maxRadiusText, minAngleText, maxAngleText, fixWaterConsumptionText,
				waterConsumptionText, minPressureText, sprinklerGroupText);
		root.addColumn(1, nameField, minRadiusField, maxRadiusField, minAngleField, maxAngleField,
				fixWaterConsumptionCheckBox, waterConsumptionfField, minPressureField, sprinklerGroupChoiceBox);
		root.add(addBtn, 1, 9);
		root.setHgap(10);
		root.setVgap(10);
		root.setPadding(new Insets(10, 10, 10, 25));

		List<TextField> fields = Arrays.asList(nameField, minRadiusField, maxRadiusField, minAngleField, maxAngleField,
				waterConsumptionfField, minPressureField);

		addBtn.setOnAction(e -> {
			boolean filled = false;
			for (TextField f : fields) {
				if (f.getText() == null && f.getText().trim().isEmpty())
					Common.showAlert("T�ltsd ki az �sszes mez�t!");
				else
					filled = true;
			}
			if (filled) {
				try {
					if (Double.parseDouble(minRadiusField.getText()) > Double.parseDouble(maxRadiusField.getText())) {
						Common.showAlert("A min. sug�r nem lehet nagyobb a max. sug�rn�l!");
					} else if (Double.parseDouble(minAngleField.getText()) > Double
							.parseDouble(maxAngleField.getText())) {
						Common.showAlert("A min. sz�g nem lehet nagyobb a max. sz�gn�l!");
					} else {
						SprinklerType s = new SprinklerType();
						s.setName(nameField.getText());
						s.setMinRadius(Double.parseDouble(minRadiusField.getText()));
						s.setMaxRadius(Double.parseDouble(maxRadiusField.getText()));
						s.setMinAngle(Double.parseDouble(minAngleField.getText()));
						s.setMaxAngle(Double.parseDouble(maxAngleField.getText()));
						s.setFixWaterConsumption(!fixWaterConsumptionCheckBox.isSelected());
						s.setWaterCounsumption(Double.parseDouble(waterConsumptionfField.getText()));
						s.setMinPressure(Double.parseDouble(minPressureField.getText()));
						s.setSprinklerGroup(sprinklerGroupChoiceBox.getValue());
						controller.addSprinklerType(s);
						for (TextField f : fields) {
							f.setText("");
						}
					}
				} catch (NumberFormatException ex) {
					Common.showAlert("Sz�mokban add meg az �rt�keket!");
				} catch (DbException ex) {
					Common.showAlert(ex.getMessage());
				}
			}

		});

		addSprinklerDbStage.show();
	}
}
