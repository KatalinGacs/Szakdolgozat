package application;

import application.common.Common;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class SaveModificationsStage extends Stage {

	private String saveQuestionPart1 = "Menti a ";
	private String saveQuestionPart2 = " dokumentum módosításait?";

	private boolean cancelled = false;

	public SaveModificationsStage(boolean exit, CanvasPane canvasPane) {
		cancelled = false;
		setTitle(Common.programName + " " + Common.version);
		setAlwaysOnTop(true);
		VBox root = new VBox();
		GridPane buttonPane = new GridPane();
		Scene scene = new Scene(root);
		setScene(scene);
		Text questionText = new Text(saveQuestionPart1 + FileHandling.currentFileName + saveQuestionPart2);

		Button yes = new Button("Igen");
		Button no = new Button("Nem");
		Button cancel = new Button("Mégse");

		buttonPane.add(yes, 1, 0);
		buttonPane.add(no, 2, 0);
		buttonPane.add(cancel, 3, 0);
		buttonPane.setPadding(new Insets(20));
		buttonPane.setHgap(20);
		root.setPrefSize(300, 100);
		root.getChildren().addAll(questionText, buttonPane);

		this.show();

		yes.setOnAction(e -> {
			FileHandling.saveCanvas(this, canvasPane, false);
			if (exit) {
				Platform.exit();
			} else {
				canvasPane.clear();
				canvasPane.hideTempLayer();
			}
		});

		no.setOnAction(e -> {
			if (exit) {
				Platform.exit();
			} else {
				close();
				canvasPane.clear();
				canvasPane.hideTempLayer();
			}	
		});

		cancel.setOnAction(e -> {
			cancelled = true;
			close();

		});

		setOnCloseRequest(event -> {
			cancelled = true;
		});
	}

	public boolean isCancelled() {
		return cancelled;
	}

}
