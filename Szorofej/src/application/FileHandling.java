package application;

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
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import model.Canvas;
import model.bean.SprinklerShape;

public class FileHandling {

	private static SprinklerController controller = new SprinklerControllerImpl();	
	
	public static void saveCanvas() {
		// TODO file helyét paraméterben kéne megkapja
		try (FileOutputStream file = new FileOutputStream("file.xml", true)) {
			Canvas canvas = new Canvas();
			JAXBContext context = JAXBContext.newInstance(Canvas.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			m.marshal(canvas, System.out);

			m.marshal(canvas, file);

		} catch (IOException | JAXBException ex) {
			ex.printStackTrace();
		}

	}

	public static void loadCanvas(CanvasPane canvasPane) {

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
			Canvas canvas2 = (Canvas) um.unmarshal(new FileReader("file.xml"));
			Group sprinklerArcLayer = new Group();
			Group sprinklerTextLayer = new Group();
			Group irrigationLayer = new Group();
			for (SprinklerShape s : canvas2.sprinklerShapes) {
				Color strokeColor = Color.web(s.getStrokeColor());
				System.out.println(s.getFillColor());
				Color fillColor = s.getFillColor().equals("0x000000ff") ? Color.TRANSPARENT : Color.web(s.getFillColor());
				s.setArc(new Arc(s.getCenterX(), s.getCenterY(), s.getRadius()*Common.pixelPerMeter, s.getRadius()*Common.pixelPerMeter, s.getStartAngle(), s.getLength()));
				s.getArc().setFill(fillColor);
				s.getArc().setStroke(strokeColor);
				s.getArc().setType(ArcType.ROUND);
				s.setCircle(new Circle(s.getCenterX(), s.getCenterY(), s.getCircleRadius(), strokeColor));
				controller.listSprinklerShapes().add(s);
				canvasPane.irrigationLayer.getChildren().add(s.getCircle());
				canvasPane.sprinklerArcLayer.getChildren().add(s.getArc());
				canvasPane.sprinklerTextLayer.getChildren().add(s.getLabel());
	
			}
			
		} catch (JAXBException | FileNotFoundException e) {

			e.printStackTrace();
		}

		

		/*
		 * try( FileOutputStream file = new FileOutputStream("file.xml", true)){ Canvas
		 * canvas = new Canvas(); JAXBContext context =
		 * JAXBContext.newInstance(Canvas.class); Marshaller m =
		 * context.createMarshaller(); m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
		 * Boolean.TRUE);
		 * 
		 * m.marshal(canvas, System.out);
		 * 
		 * m.marshal(canvas, file);
		 * 
		 * } catch (IOException | JAXBException ex) { ex.printStackTrace(); }
		 */
	}
}
