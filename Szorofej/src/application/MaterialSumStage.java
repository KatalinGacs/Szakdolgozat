package application;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Optional;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import application.common.Common;
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
import model.DbException;
import model.bean.UsedMaterial;

/**
 * A stage to show the summary of the materials used in the current plan. The
 * listed materials can be exported from this stage as XLS.
 * 
 * @author Gacs Katalin
 *
 */
public class MaterialSumStage extends Stage {

	/**
	 * Controller to access data from the database
	 */
	private SprinklerController controller = new SprinklerControllerImpl();

	/**
	 * Root container in the stage
	 */
	private VBox root = new VBox();

	/**
	 * Scene of the stage
	 */
	private Scene scene = new Scene(root);

	/**
	 * Table with the used materials and their quantities
	 */
	private TableView<UsedMaterial> tableView = new TableView<>();

	/**
	 * Table column for the names of the materials
	 */
	private TableColumn<UsedMaterial, String> nameCol = new TableColumn<>("Név");

	/**
	 * Table column for the quantities of the materials
	 */
	private TableColumn<UsedMaterial, Integer> quantityCol = new TableColumn<>("Mennyiség");

	/**
	 * Button for exporting the materials to XLS
	 */
	private Button saveBtn = new Button("Mentés");

	/**
	 * Create the stage, set its controls, populate the table with the materials
	 */
	public MaterialSumStage() {
		initModality(Modality.APPLICATION_MODAL);
		setTitle("Összegzés");
		setScene(scene);
		tableView.getColumns().addAll(nameCol, quantityCol);
		nameCol.setCellValueFactory(new PropertyValueFactory<UsedMaterial, String>("material"));
		quantityCol.setCellValueFactory(new PropertyValueFactory<UsedMaterial, Integer>("quantity"));

		try {
			tableView.setItems(controller.summarizeMaterials());
		} catch (DbException ex) {
			Common.showAlert(ex.getMessage());
		}

		saveBtn.setOnAction(e -> {
			exportToExcel();
		});

		root.getChildren().addAll(tableView, saveBtn);
	}

	/**
	 * Export the listed materials to XLS. If there are sprinklers not in a zone or
	 * not connected with pipes, it is assumed that the plan is not completed so in
	 * this case before saving the user has to confirm.
	 */
	private void exportToExcel() {
		Optional<ButtonType> result = null;
		if (!controller.listSprinklerShapesNotInZones().isEmpty()
				|| !controller.listSprinklerShapesNotConnectedToPipes().isEmpty()) {
			Alert confirmContinue = new Alert(AlertType.CONFIRMATION);
			confirmContinue.setTitle("Összegzés");
			confirmContinue.setHeaderText("Vannak zónába nem sorolt vagy csövezéssel be nem kötött szórófejek.");
			confirmContinue.setContentText("Folytatja?");

			result = confirmContinue.showAndWait();
		} 
		if (result == null || result.get() == ButtonType.OK) {

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
	}
}
