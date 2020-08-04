package application;

import java.util.Optional;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

/**
 * Pop up alert that asks the user to confirm if the unsaved modificatoins
 * shouldd be saved
 * 
 * @author Gacs Katalin
 *
 */
public class SaveModificationsAlert extends Alert {

	/**
	 * Title of the alert window
	 */
	private String windowTitle = "Mentés";

	/**
	 * Part 1 of the header text
	 */
	private String saveQuestionPart1 = "Menti a ";

	/**
	 * Part 2 of the header text
	 */
	private String saveQuestionPart2 = " dokumentum módosításait?";

	/**
	 * Create the alert window. Set its controls. Add event handling to the buttons.
	 * 
	 * @param exit       True if the alert is called because the application is
	 *                   about to be closed
	 * @param canvasPane the CanvasPane which has to be saved if the users chooses
	 *                   to save
	 * @param stage      owner window of the filechooser for saving
	 */
	public SaveModificationsAlert(boolean exit, CanvasPane canvasPane, Stage stage ) {
		super(AlertType.CONFIRMATION);

		setTitle(windowTitle);
		setHeaderText(saveQuestionPart1 + FileHandler.currentFileName + saveQuestionPart2);

		ButtonType yesButton = new ButtonType("Igen");
		ButtonType noButton = new ButtonType("Nem");
		ButtonType cancelButton = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

		getButtonTypes().setAll(yesButton, noButton, cancelButton);

		Optional<ButtonType> result = showAndWait();
		if (result.get() == yesButton) {
			FileHandler.saveCanvas(stage, canvasPane, false);
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
	public SaveModificationsAlert(boolean exit, CanvasPane canvasPane ) {
		this(exit, canvasPane, null);
	}
}
