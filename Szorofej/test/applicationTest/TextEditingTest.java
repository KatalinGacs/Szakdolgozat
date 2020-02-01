package applicationTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import application.CanvasPane;
import application.CanvasPane.Use;
import application.MainSpr;
import application.TextEditing;
import javafx.application.Platform;
import junit.framework.TestCase;

public class TextEditingTest {



	@Test
	public void testOpenTextFormatStage() {
		
		CanvasPane canvasPane = new CanvasPane();
		Platform.runLater(new Runnable(){
			@Override
			public void run() {
				TextEditing.openTextFormatStage(canvasPane);
				assertTrue(canvasPane.getStateOfCanvasUse() == Use.PREPAREFORTEXTEDITING);
			}
			});
		
		

	}

}
