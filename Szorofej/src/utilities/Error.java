package utilities;

import org.apache.commons.lang3.exception.ExceptionUtils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Error {

	/**
	 * Show an error alert with the infos of an Exception.
	 * To be used in cathc blocks
	 * 
	 * @param e The Exception whose message and stack trace are shown
	 * @throws Exception
	 */
	// TODO: log the exceptions
	public static void HandleException(Exception e) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setContentText(ExceptionUtils.getStackTrace(e));
		alert.setHeaderText(e.getMessage());
		alert.setTitle("Hiba");
		alert.show();
	}
	
}
