package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import application.common.Common;
import controller.SprinklerController;
import controller.SprinklerControllerImpl;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Canvas;
import model.bean.BorderLine;
import model.bean.SprinklerShape;

public class FileHandling {

	private static SprinklerController controller = new SprinklerControllerImpl();

	private static String currentPath = "";

	public static void saveCanvas(Stage stage, boolean saveAs) {

		if (currentPath == "" || saveAs == true) {
			FileChooser fileChooser = new FileChooser();
			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
			fileChooser.getExtensionFilters().add(extFilter);
			File file = fileChooser.showSaveDialog(stage);
			currentPath = file.getAbsolutePath();
		}

		try (FileOutputStream fileOS = new FileOutputStream(currentPath, false)) {

			Canvas canvas = new Canvas();
			JAXBContext context = JAXBContext.newInstance(Canvas.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			m.marshal(canvas, System.out);

			m.marshal(canvas, fileOS);

		} catch (IOException | JAXBException ex) {
			ex.printStackTrace();
		}

	}

	// TODO file helyét paraméterben kéne megkapja
	public static void loadCanvas(CanvasPane canvasPane, Stage stage) {

		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
		fileChooser.getExtensionFilters().add(extFilter);
		File file = fileChooser.showOpenDialog(stage);

		for (SprinklerShape s : controller.listSprinklerShapes()) {
			canvasPane.irrigationLayer.getChildren().remove(s.getCircle());
			canvasPane.sprinklerArcLayer.getChildren().remove(s.getArc());
			canvasPane.sprinklerTextLayer.getChildren().remove(s.getLabel());
		}
		controller.listSprinklerShapes().clear();

		JAXBContext context;
		try {
			context = JAXBContext.newInstance(Canvas.class);
			Unmarshaller um = context.createUnmarshaller();
			Canvas canvas = (Canvas) um.unmarshal(new FileReader(file));
			loadSprinklerShapes(canvasPane, canvas);
			loadBorderLines(canvasPane, canvas);
		} catch (JAXBException | FileNotFoundException e) {

			e.printStackTrace();
		}

	}

	private static void loadSprinklerShapes(CanvasPane canvasPane, Canvas canvas) {
		for (SprinklerShape s : canvas.sprinklerShapes) {
			Color strokeColor = Color.web(s.getStrokeColor());
			Color fillColor = s.getFillColor().equals("0x000000ff") ? Color.TRANSPARENT : Color.web(s.getFillColor());
			s.setArc(new Arc(s.getCenterX(), s.getCenterY(), s.getRadius() * Common.pixelPerMeter,
					s.getRadius() * Common.pixelPerMeter, s.getStartAngle(), s.getLength()));
			s.getArc().setFill(fillColor);
			s.getArc().setStroke(strokeColor);
			s.getArc().setStrokeWidth(s.getStrokeWidth());
			s.getArc().setType(ArcType.ROUND);
			s.setCircle(new Circle(s.getCenterX(), s.getCenterY(), s.getCircleRadius(), strokeColor));
			s.setLabel(new Text(s.getLabelText()));
			s.getLabel().setX(s.getLabelX());
			s.getLabel().setY(s.getLabelY());
			s.getLabel().setStyle(s.getLabelStyle());
			s.setSprinkler(controller.getSprinklerType(s.getSprinklerType()));
			controller.listSprinklerShapes().add(s);
			canvasPane.irrigationLayer.getChildren().add(s.getCircle());
			canvasPane.sprinklerArcLayer.getChildren().add(s.getArc());
			canvasPane.sprinklerTextLayer.getChildren().add(s.getLabel());
		}
	}
	
	private static void loadBorderLines(CanvasPane canvasPane, Canvas canvas) {
		for (BorderLine b : canvas.borderLines) {
			Color strokeColor = Color.web(b.getColor());
			Line line = new Line(b.getStartX(), b.getStartY(), b.getEndX(), b.getEndY());
			line.setStrokeWidth(b.getWidth());
			line.setStroke(strokeColor);
			controller.addBorderShape(line);
			canvasPane.bordersLayer.getChildren().add(line);
		}
	}
}
