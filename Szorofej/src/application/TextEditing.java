package application;

import java.util.Arrays;
import java.util.List;

import com.sun.javafx.collections.ObservableListWrapper;

import application.CanvasPane.Use;
import application.common.Common;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Helper class for adding custom texts to the plan on the canvasPane
 * 
 * @author Gacs Katalin
 *
 */
public class TextEditing {

	static Integer fontSize = 24;
	static Color fontColor = Color.BLACK;
	static TextField textField = new TextField();
	static Stage stage = new Stage();

	/**
	 * Show a stage where the user can set the format of the text to be written on
	 * the canvasPane
	 * 
	 * @param canvasPane CanvasPane on which the text will be written
	 */
	public static void openTextFormatStage(CanvasPane canvasPane) {
		canvasPane.setStateOfCanvasUse(Use.PREPAREFORTEXTEDITING);

		GridPane root = new GridPane();
		Scene scene = new Scene(root);
		stage.setX(Common.primaryScreenBounds.getWidth() - 500);
		stage.setY(100);
		root.setVgap(10);
		root.setHgap(10);
		root.setPadding(new Insets(10, 10, 10, 10));
		stage.setScene(scene);

		Text colorText = new Text("Betûszín");
		ColorPicker colorPicker = new ColorPicker(fontColor);
		Text fontSizeText = new Text("Betûméret");
		ListView<Integer> fontSizeList = new ListView<>();
		List<Integer> fontSizes = Arrays.asList(8, 10, 12, 14, 16, 20, 24, 28, 32, 36, 48, 72);
		fontSizeList.setItems(new ObservableListWrapper<Integer>(fontSizes));
		fontSizeList.setPrefHeight(100);
		fontSizeList.getSelectionModel().select(fontSize);
		fontSizeList.scrollTo(fontSize);

		root.add(fontSizeText, 0, 0);
		root.add(fontSizeList, 0, 1);
		root.add(colorText, 2, 0);
		root.add(colorPicker, 2, 1);

		fontSizeList.setOnMouseClicked(e -> {
			fontSize = fontSizeList.getSelectionModel().getSelectedItem();
		});

		colorPicker.setOnAction(a -> {
			fontColor = colorPicker.getValue();
		});

		stage.setAlwaysOnTop(true);
		canvasPane.requestFocus();
		stage.show();
	}

	/**
	 * Add the text the user types to the canvasPane. The user can type until Enter is pressed.
	 * 
	 * @param e MouseEvent mouse clicked on the canvasPane, here starts the text
	 * @param canvasPane CanvasPane on which the text is written
	 */
	public static void startWritingText(MouseEvent e, CanvasPane canvasPane) {
		canvasPane.setModifiedSinceLastSave(true);
		stage.close();
		textField.setVisible(true);
		textField.requestFocus();
		String style = "-fx-text-fill: #" + fontColor.toString().substring(2) + "; -fx-font-size: " + fontSize + "px;";
		textField.setBackground(null);
		textField.setLayoutX(e.getX() - fontSize / 2);
		textField.setLayoutY(e.getY() - fontSize);
		textField.setFont(Font.font("arial", FontWeight.SEMI_BOLD, fontSize));
		textField.setStyle(style);
		textField.setOnKeyPressed(ke -> {
			if (ke.getCode() == KeyCode.ENTER) {
				textField.setVisible(false);
				Text text = new Text(textField.getText());
				text.setX(e.getX() + fontSize * 0.1);
				text.setY(e.getY() + fontSize * 0.3);
				text.setFont(Font.font("arial", FontWeight.SEMI_BOLD, fontSize));
				text.setStyle(style);
				text.setFill(fontColor);
				canvasPane.controller.addText(text);
				canvasPane.setStateOfCanvasUse(Use.NONE);
				canvasPane.getTextLayer().getChildren().add(text);
				textField.setVisible(false);
				textField.setText("");
				canvasPane.setCursor(Cursor.DEFAULT);
			}
		});
	}
}
