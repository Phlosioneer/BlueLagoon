package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import core.TextObject;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PVector;

public class TextRendering {

	private static HashMap<TextTriple, PImage> renderedTextCache;
	private static Graphics2D dummyGraphicsObj;
	private static HashMap<String, Font> loadedFonts;

	public static class TextTriple {
		int maxWidth;
		String text;
		Font font;

		TextTriple(int maxWidth, String text, Font font) {
			this.maxWidth = maxWidth;
			this.text = text;
			this.font = font;
		}

		@Override
		public int hashCode() {
			return (int) maxWidth * text.hashCode() * font.hashCode();
		}

		@Override
		public boolean equals(Object other) {
			if (other == this) {
				return true;
			}
			if (other == null) {
				return false;
			}
			if (this.getClass() != other.getClass()) {
				return false;
			}
			TextTriple castOther = (TextTriple) other;
			return maxWidth == castOther.maxWidth && text.equals(castOther.text) && font.equals(castOther.font);
		}
	}

	public static void drawText(PApplet app, TextObject object) {
		drawText(app, object, 0, 0);
	}

	public static void drawText(PApplet app, TextObject object, int offsetX, int offsetY) {
		// Load the font.
		Font font = null;
		String fontName = object.fontFamily + "-" + (int) object.getPointHeight();
		if (loadedFonts == null) {
			loadedFonts = new HashMap<String, Font>();
		}
		if (loadedFonts.containsKey(fontName)) {
			font = loadedFonts.get(fontName);
		} else {
			try {
				font = Font.createFont(Font.TRUETYPE_FONT, new File(app.sketchPath("data\\" + object.fontFamily + ".ttf")));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			font = font.deriveFont(font.getStyle(), object.pixelHeight);
			loadedFonts.put(fontName, font);
		}

		PImage rendered = renderTextToImageCached(object.text, font, (int) object.size.x);
		PVector imagePos = new PVector();

		// Calculate horizontal alignment.
		if (object.horizontalAlign == TextObject.HorizontalAlign.CENTER) {
			imagePos.x = (object.size.x - rendered.width) / 2;
		} else if (object.horizontalAlign == TextObject.HorizontalAlign.RIGHT) {
			imagePos.x = object.size.x - rendered.width;
		} else if (object.horizontalAlign == TextObject.HorizontalAlign.LEFT) {
			imagePos.x = 0;
		} else {
			throw new RuntimeException();
		}

		// Calculate vertical alignment.
		if (object.verticalAlign == TextObject.VerticalAlign.CENTER) {
			imagePos.y = (object.size.y - rendered.height) / 2;
		} else if (object.verticalAlign == TextObject.VerticalAlign.TOP) {
			imagePos.y = 0;
		} else if (object.verticalAlign == TextObject.VerticalAlign.BOTTOM) {
			imagePos.y = object.size.y - rendered.height;
		} else {
			throw new RuntimeException();
		}

		imagePos.x += object.position.x + offsetX;
		imagePos.y += object.position.y + offsetY;

		// Draw the rendered text.
		app.image(rendered, imagePos.x, imagePos.y);
	}

	public static PImage renderTextToImageCached(String text, Font font, int maxWidth) {
		if (renderedTextCache == null) {
			renderedTextCache = new HashMap<TextTriple, PImage>();
		}

		TextTriple triple = new TextTriple(maxWidth, text, font);

		PImage renderedText = renderedTextCache.get(triple);
		if (renderedText == null) {
			renderedText = renderTextToImage(text, font, maxWidth);
			renderedTextCache.put(triple, renderedText);
		}
		return renderedText;
	}

	// Code based on this answer: https://stackoverflow.com/a/18800845
	public static PImage renderTextToImage(String text, Font font, int maxWidth) {

		/*
		 * Because font metrics is based on a graphics context, we need to create
		 * a small, temporary image so we can ascertain the width and height
		 * of the final image
		 */
		if (dummyGraphicsObj == null) {
			BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
			dummyGraphicsObj = img.createGraphics();
		}
		dummyGraphicsObj.setFont(font);
		FontMetrics metrics = dummyGraphicsObj.getFontMetrics();
		int width = metrics.stringWidth(text);
		int height = metrics.getHeight();
		ArrayList<String> lines;
		if (width >= maxWidth) {
			String[] words = text.split(" ");
			lines = new ArrayList<String>();
			String currentString = "";
			for (String nextWord : words) {
				int newWidth = metrics.stringWidth(currentString + " " + nextWord);
				if (newWidth >= maxWidth) {
					// If this is a one-word line, we just have to use it.
					if (currentString.equals("")) {
						lines.add(nextWord);
					} else {
						lines.add(currentString);
						currentString = nextWord;
					}
				} else {
					currentString += " " + nextWord;
				}
			}
			if (!currentString.equals("")) {
				lines.add(currentString);
			}

			// Update the width and height.
			height = metrics.getHeight() * lines.size();
			width = -1;
			for (String line : lines) {
				int lineWidth = metrics.stringWidth(line);
				if (lineWidth > width) {
					width = lineWidth;
				}
			}
		} else {
			lines = new ArrayList<String>(1);
			lines.add(text);
		}

		// Render the text.
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = img.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		g2d.setFont(font);
		metrics = g2d.getFontMetrics();
		g2d.setColor(Color.BLACK);
		for (int i = 0; i < lines.size(); i++) {
			g2d.drawString(lines.get(i), 0, metrics.getAscent() + i * metrics.getHeight());
		}
		g2d.dispose();

		// Convert to PImage
		PImage ret = new PImage(width, height, PConstants.ARGB);
		img.getRGB(0, 0, width, height, ret.pixels, 0, width);
		ret.updatePixels();
		return ret;
	}
}
