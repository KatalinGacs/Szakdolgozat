package application;

import java.util.Optional;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class SaveModificationsStage extends Alert {

	private String windowTitle = "Mentés";
	private String saveQuestionPart1 = "Menti a ";
	private String saveQuestionPart2 = " dokumentum módosításait?";

	public SaveModificationsStage(boolean exit, CanvasPane canvasPane, Stage stage) {
		super(AlertType.CONFIRMATION);

		setTitle(windowTitle);
		setHeaderText(saveQuestionPart1 + FileHandling.currentFileName + saveQuestionPart2);
		
		ButtonType yesButton = new ButtonType("Igen");
		ButtonType noButton = new ButtonType("Nem");
		ButtonType cancelButton = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

		getButtonTypes().setAll(yesButton, noButton, cancelButton);

		Optional<ButtonType> result = showAndWait();
		if (result.get() == yesButton){
			FileHandling.saveCanvas(stage, canvasPane, false);
			if (exit) {
				Platform.exit();
			} else {
				canvasPane.clear();
				canvasPane.hideTempLayer();
			}
		} else if (result.get() == noButton) {
			if (exit) {
				Platform.exit();
			} else {
				close();
				canvasPane.clear();
				canvasPane.hideTempLayer();
			}	
		} else {
			close();
		}
		
	}
}
