package main;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import core.MapFile;
import core.ObjectLayer;
import core.TMXColor;
import core.TMXObject;
import core.TextObject;
import core.Tile;
import core.TileLayer;
import core.TileObject;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import util.FileLocatorDelegate;
import util.ImageDelegate;
import util.Rect;

public class Boilerplate {

	static MapFile<PImage> openMap(PApplet app, String filename) {
		ProcessingFileOpener fileOpener = new ProcessingFileOpener();
		ProcessingImageHandler imageHandler = new ProcessingImageHandler(app, fileOpener);
		return new MapFile<PImage>(filename, fileOpener, imageHandler);
	}

	private static class ProcessingFileOpener implements FileLocatorDelegate {
		@Override
		public InputStream openFile(String filename) {
			String correctedName = filename;
			if (filename.startsWith("data/")) {
				correctedName = filename.substring(5);
			}
			return getClass().getClassLoader().getResourceAsStream(correctedName);
		}
	}

	private static class ProcessingImageHandler implements ImageDelegate<PImage> {
		private PApplet app;
		private FileLocatorDelegate fileLocator;

		public ProcessingImageHandler(PApplet app, FileLocatorDelegate fileLocator) {
			this.app = app;
			this.fileLocator = fileLocator;
		}

		@Override
		public PImage loadImage(String filename, TMXColor transparentColor) {
			PImage ret;
			try {
				Image rawImage = ImageIO.read(fileLocator.openFile(filename));
				ret = new PImage(rawImage);
			} catch (IOException e) {
				throw new RuntimeException("Exception handler not yet implemented", e);
			}
			ret.loadPixels();

			if (transparentColor != null) {
				int convertedColor = tmxColorConvert(app, transparentColor);
				for (int i = 0; i < ret.pixels.length; i++) {
					if (ret.pixels[i] == convertedColor) {
						ret.pixels[i] = 0;
					}
				}
			}
			return ret;
		}

		@Override
		public PImage sliceImage(PImage tilesetImage, Rect bounds) {
			PImage ret = app.createImage(bounds.width, bounds.height, PConstants.ARGB);
			ret.loadPixels();
			for (int x = 0; x < bounds.width; x++) {
				for (int y = 0; y < bounds.height; y++) {
					int tilesetX = x + bounds.x;
					int tilesetY = y + bounds.y;
					int tilesetIndex = tilesetX + tilesetY * tilesetImage.width;
					ret.pixels[x + y * bounds.width] = tilesetImage.pixels[tilesetIndex];
				}
			}
			ret.updatePixels();
			return ret;
		}
	}

	public static void drawObjectLayer(PApplet app, ObjectLayer<PImage> layer) {
		drawObjectLayer(app, layer, 0, 0);
	}

	public static void drawObjectLayer(PApplet app, ObjectLayer<PImage> layer, int offsetX, int offsetY) {
		for (TMXObject object : layer.objects) {
			if (!object.isVisible) {
				continue;
			}

			if (object instanceof TileObject) {
				@SuppressWarnings("unchecked")
				TileObject<PImage> castedObject = (TileObject<PImage>) object;
				PImage tile = castedObject.tile.image;
				app.image(tile, castedObject.position.x + offsetX, castedObject.position.y - tile.height + offsetY);
			} else if (object instanceof TextObject) {
				TextRendering.drawText(app, (TextObject) object, offsetX, offsetY);
			}
		}
	}

	public static void drawOrthoStaticLayer(PApplet app, MapFile<PImage> map, TileLayer<PImage> layer) {
		drawOrthoStaticLayer(app, map, layer, 0, 0);
	}

	public static void drawOrthoStaticLayer(PApplet app, MapFile<PImage> map, TileLayer<PImage> layer, int offsetX, int offsetY) {
		app.tint(255, layer.opacity * 255);
		for (int x = 0; x < layer.width; x++) {
			for (int y = 0; y < layer.height; y++) {
				Tile<PImage> tile = layer.tiles[x][y];
				if (tile == null) {
					continue;
				}
				int pixelX = offsetX + x * map.tileWidth + (int) layer.offset.x;
				int pixelY = offsetY + y * map.tileHeight + (int) layer.offset.y;
				app.image(tile.image, pixelX, pixelY);
			}
		}
		app.noTint();
	}

	public static void drawHexStaticLayer(PApplet app, MapFile<PImage> map, TileLayer<PImage> layer) {
		drawHexStaticLayer(app, map, layer, 0, 0);
	}

	// Assumes staggerIdex = odd, and staggerAxis = Y
	public static void drawHexStaticLayer(PApplet app, MapFile<PImage> map, TileLayer<PImage> layer, int offsetX, int offsetY) {
		int hexSideLength = (int) map.getHexSideLength();
		app.tint(255, layer.opacity * 255);
		for (int x = 0; x < layer.width; x++) {
			for (int y = 0; y < layer.height; y++) {
				Tile<PImage> tile = layer.tiles[x][y];
				if (tile == null) {
					continue;
				}
				int pixelX = offsetX + x * map.tileWidth;
				if (y % 2 == 1) {
					pixelX += tile.pixelRect.width / 2;
				}
				int combinedHeight = map.tileHeight + hexSideLength;
				int rowPairs = (int) Math.floor((float) y / 2.0);
				int pixelY = offsetY + combinedHeight * rowPairs;
				if (y % 2 == 1) {
					pixelY += hexSideLength + (map.tileHeight - hexSideLength) / 2;
				}
				app.image(tile.image, pixelX, pixelY);
			}
		}
		app.noTint();
	}

	public static int tmxColorConvert(PApplet app, TMXColor otherColor) {
		return app.color(otherColor.red, otherColor.green, otherColor.blue, otherColor.alpha);
	}
}
