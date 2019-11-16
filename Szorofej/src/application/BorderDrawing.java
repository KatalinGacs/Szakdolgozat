package application;

import application.common.Common;
import javafx.geometry.Point2D;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class BorderDrawing {

	public static void showTempBorderLine(MouseEvent e, Color color, CanvasPane canvasPane) {
		BorderDrawing.lengthInput.setVisible(true);
		BorderDrawing.lengthInput.relocate(BorderDrawing.startX, BorderDrawing.startY);
		BorderDrawing.tempBorderLine.setStartX(BorderDrawing.startX);
		BorderDrawing.tempBorderLine.setStartY(BorderDrawing.startY);
		BorderDrawing.tempBorderLine.setStroke(color);
		BorderDrawing.tempBorderLine.setVisible(true);
		if (canvasPane.pressedKey == KeyCode.CONTROL) {
			Point2D point = Common.snapToHorizontalOrVertival(BorderDrawing.startX, BorderDrawing.startY, e.getX(),
					e.getY());
			BorderDrawing.tempBorderLine.setEndX(point.getX());
			BorderDrawing.tempBorderLine.setEndY(point.getY());
		} else {
			BorderDrawing.tempBorderLine.setEndX(e.getX());
			BorderDrawing.tempBorderLine.setEndY(e.getY());
		}
	}

	// TODO ide is kell a rácshoz igazodás, mint a szórófej rajzolásnál
	// TODO kell arra mód, hogy az elõzõ vonalhoz képest derékszögû vonalat tudjon
	// rajzolni (nem feltétlen esik a rácsra), de nem tudom, milyen felhasználói
	// inputra csinálja ezt
	public static void drawBorderLine(MouseEvent e, Color color, int width, CanvasPane canvasPane) {
		BorderDrawing.tempBorderLine.setVisible(false);
		Line line = new Line();
		line.setStartX(BorderDrawing.startX);
		line.setStartY(BorderDrawing.startY);
		line.setStrokeWidth(width);
		line.setStroke(color);
		double endX, endY;

		if (canvasPane.cursorNearLineEnd) {
			endX = canvasPane.lineEndX;
			endY = canvasPane.lineEndY;
		} else {
			endX = e.getX();
			endY = e.getY();
		}
		if (!BorderDrawing.lengthInput.getText().trim().isEmpty() && BorderDrawing.lengthInput.getText() != null)
			try {
				double requiredLength = Double.parseDouble(BorderDrawing.lengthInput.getText()) * Common.pixelPerMeter;
				double drawnLength = Math.sqrt((BorderDrawing.startX - e.getX()) * (BorderDrawing.startX - e.getX())
						+ (BorderDrawing.startY - e.getY()) * (BorderDrawing.startY - e.getY()));
				double ratio = requiredLength / drawnLength;

				endX = BorderDrawing.startX + (e.getX() - BorderDrawing.startX) * ratio;
				endY = BorderDrawing.startY + (e.getY() - BorderDrawing.startY) * ratio;
				
				BorderDrawing.lengthInput.setText("");
				BorderDrawing.lengthInput.setVisible(false);

			} catch (NumberFormatException ex) {
				Common.showAlert("Számokban add meg a vonal hosszát vagy hagyd üresen a mezõt!");
			}
		if (canvasPane.pressedKey == KeyCode.CONTROL) {
			Point2D point = Common.snapToHorizontalOrVertival(BorderDrawing.startX, BorderDrawing.startY, endX,
					endY);
			endX = point.getX();
			endY = point.getY();
		}
		line.setEndX(endX);
		line.setEndY(endY);
		BorderDrawing.startX = endX;
		BorderDrawing.startY = endY;

		canvasPane.controller.addBorderShape(line);
		canvasPane.bordersLayer.getChildren().add(line);
	}

	public static void drawBorderRectanlge(MouseEvent e, Color strokeColor, Color fillColor, int width,
			CanvasPane canvasPane) {
		Rectangle rect = Common.drawRectangle(strokeColor, BorderDrawing.startX, BorderDrawing.startY, e.getX(),
				e.getY());
		rect.setFill(fillColor);
		rect.setStrokeWidth(width);
		canvasPane.bordersLayer.getChildren().add(rect);
		canvasPane.controller.addBorderShape(rect);
		canvasPane.controller.addObstacle(rect);
	}

	public static void showTempBorderCircle(MouseEvent e, Color stroke, Color fill, CanvasPane canvasPane) {
		if (e.getButton() == MouseButton.PRIMARY) {
			double r = Math.sqrt((BorderDrawing.startX - e.getX()) * (BorderDrawing.startX - e.getX())
					+ (BorderDrawing.startY - e.getY()) * (BorderDrawing.startY - e.getY()));
			BorderDrawing.tempCircle.setCenterX(BorderDrawing.startX);
			BorderDrawing.tempCircle.setCenterY(BorderDrawing.startY);
			BorderDrawing.tempCircle.setRadius(r);
			BorderDrawing.tempCircle.setFill(fill);
			BorderDrawing.tempCircle.setStroke(stroke);
			BorderDrawing.tempCircle.setVisible(true);
		}
	}

	static double startX;
	static double startY;
	static TextField lengthInput = new TextField();
	static Line tempBorderLine = new Line();
	static Circle tempCircle = new Circle();

	public static void showtempBorderRectanlge(MouseEvent e, Color stroke, Color fill, CanvasPane canvasPane) {

		if (e.getButton() == MouseButton.PRIMARY) {
			double width = Math.abs(startX - e.getX());
			double height = Math.abs(startY - e.getY());
			double x = 0, y = 0;
			if (startX > e.getX())
				x = e.getX();
			else
				x = startX;
			if (startY > e.getY())
				y = e.getY();
			else
				y = startY;
			BorderDrawing.tempRectangle.setX(x);
			BorderDrawing.tempRectangle.setY(y);
			BorderDrawing.tempRectangle.setWidth(width);
			BorderDrawing.tempRectangle.setHeight(height);
			if (canvasPane.stateOfCanvasUse == CanvasPane.Use.ZONEEDITING) {
				BorderDrawing.tempRectangle.setStroke(canvasPane.selectionColor);
				BorderDrawing.tempRectangle.setFill(null);
			} else {
				BorderDrawing.tempRectangle.setStroke(stroke);
				BorderDrawing.tempRectangle.setFill(fill);
			}
			BorderDrawing.tempRectangle.setVisible(true);
		}
	}

	static Rectangle tempRectangle = new Rectangle();

	public static void startDrawingBorder(MouseEvent e) {
		startX = e.getX();
		startY = e.getY();
	}

	public static void drawBorderCircle(MouseEvent e, Color strokeColor, Color fillColor, int width,
			CanvasPane canvasPane) {

		double r = Math.sqrt((startX - e.getX()) * (startX - e.getX()) + (startY - e.getY()) * (startY - e.getY()));
		Circle circle = new Circle(startX, startY, r, null);
		circle.setStroke(strokeColor);
		circle.setFill(fillColor);
		circle.setStrokeWidth(width);
		canvasPane.bordersLayer.getChildren().add(circle);
		canvasPane.controller.addBorderShape(circle);
		canvasPane.controller.addObstacle(circle);
	}

}
