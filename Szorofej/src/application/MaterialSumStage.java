package application;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Optional;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import controller.SprinklerController;
import controller.SprinklerControllerImpl;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.bean.SprinklerShape;
import model.bean.UsedMaterial;

public class MaterialSumStage extends Stage {

	private SprinklerController controller = new SprinklerControllerImpl();

	private VBox root = new VBox();
	private Scene scene = new Scene(root);

	private TableView<UsedMaterial> tableView = new TableView<>();
	private TableColumn<UsedMaterial, String> nameCol = new TableColumn<>("N�v");
	private TableColumn<UsedMaterial, Integer> quantityCol = new TableColumn<>("Mennyis�g");

	private Button saveBtn = new Button("Ment�s");

	public MaterialSumStage() {
		initModality(Modality.APPLICATION_MODAL);
		setTitle("�sszegz�s");
		setScene(scene);
		tableView.getColumns().addAll(nameCol, quantityCol);
		nameCol.setCellValueFactory(new PropertyValueFactory<UsedMaterial, String>("material"));
		quantityCol.setCellValueFactory(new PropertyValueFactory<UsedMaterial, Integer>("quantity"));

		tableView.setItems(controller.summarizeMaterials());

		saveBtn.setOnAction(e -> {
			Optional<ButtonType> result = null;
			if (!controller.listSprinklerShapesNotInZones().isEmpty() || !controller.listSprinklerShapesNotConnectedToPipes().isEmpty()) {
				Alert confirmContinue = new Alert(AlertType.CONFIRMATION);
				confirmContinue.setTitle("�sszegz�s");
				confirmContinue.setHeaderText("Vannak z�n�ba nem sorolt vagy cs�vez�ssel be nem k�t�tt sz�r�fejek.");
				confirmContinue.setContentText("Folytatja?");

				result = confirmContinue.showAndWait();
			}
			else if (result == null || result.get() == ButtonType.OK){
					
					
					Workbook workbook = new HSSFWorkbook();
					Sheet spreadsheet = workbook.createSheet("sample");

					Row row = spreadsheet.createRow(0);

					for (int j = 0; j < tableView.getColumns().size(); j++) {
						row.createCell(j).setCellValue(tableView.getColumns().get(j).getText());
					}

					for (int i = 0; i < tableView.getItems().size(); i++) {
						row = spreadsheet.createRow(i + 1);
						for (int j = 0; j < tableView.getColumns().size(); j++) {
							if (tableView.getColumns().get(j).getCellData(i) != null) {
								row.createCell(j).setCellValue(tableView.getColumns().get(j).getCellData(i).toString());
							} else {
								row.createCell(j).setCellValue("");
							}
						}
					}
					FileChooser fileChooser = new FileChooser();
					fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XLS File (*.xls)", "*.xls"));
					File file = fileChooser.showSaveDialog(null);
					if (file != null) {
						try (FileOutputStream outputStream = new FileOutputStream(file.getAbsolutePath())) {
							workbook.write(outputStream);
							workbook.close();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
					close();
				} else {
					close();
				
			}

		});

		root.getChildren().addAll(tableView, saveBtn);
	}

}
