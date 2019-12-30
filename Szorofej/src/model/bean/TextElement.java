package model.bean;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "TextElement")
public class TextElement {
	private String text;
	private String style;
	private double x;
	private double y;
	private String fillColor;
	private double fontSize;
	private String font;
	
	public TextElement() {
	}

	public TextElement(String text, String style, double x, double y, String fillColor, double fontSize, String font) {
		this.text = text;
		this.style = style;
		this.x = x;
		this.y = y;
		this.fillColor = fillColor;
		this.fontSize = fontSize;
		this.font = font;
	}

	@XmlElement(name = "Text")
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@XmlElement(name = "Style")
	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	@XmlElement(name = "X")
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	@XmlElement(name = "Y")
	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	@XmlElement(name = "FillColor")
	public String getFillColor() {
		return fillColor;
	}

	public void setFillColor(String fillColor) {
		this.fillColor = fillColor;
	}
	
	@XmlElement(name = "FontSize")
	public double getFontSize() {
		return fontSize;
	}

	public void setFontSize(double fontSize) {
		this.fontSize = fontSize;
	}

	@XmlElement(name = "Font")
	public String getFont() {
		return font;
	}

	public void setFont(String font) {
		this.font = font;
	}
	
	
}
