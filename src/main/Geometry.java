package main;

import core.MapFile;
import processing.core.PImage;
import processing.core.PVector;

public class Geometry {

	public static class HexCoords {
		int x;
		int y;
	}

	public static HexCoords pointToHex(MapFile<PImage> map, float x, float y, float mapOffsetX, float mapOffsetY) {
		return pointToHex(map, x - mapOffsetX, y - mapOffsetY);
	}

	public static HexCoords pointToHex(MapFile<PImage> map, float x, float y) {
		HexCoords ret = new HexCoords();
		float tileWidth = map.tileWidth;
		float tileHeight = map.tileHeight;
		float sideLen = map.getHexSideLength();
		// The height of the pointy bits on top and bottom of each hex.
		float pointLen = (tileHeight - sideLen) / 2;

		// Narrow the search down to 2 possible rows.
		int upperRowNum = (int) Math.floor(y / (sideLen + pointLen));
		int lowerRowNum = upperRowNum - 1;
		float remainingHeight = y - upperRowNum * (sideLen + pointLen);

		int halfColumn = (int) Math.floor(x / (tileWidth / 2));

		// Are we in the easy area or the zig-zag area?
		if (remainingHeight > pointLen) {
			// We're in the easy area. There are no slanted lines.
			ret.y = upperRowNum;
			if (Math.abs(ret.y) % 2 == 0) {
				ret.x = halfColumn / 2;
			} else {
				ret.x = (halfColumn - 1) / 2;
			}
		} else {
			// We're in the difficult zig-zag area.
			float remainingWidth = x - halfColumn * (tileWidth / 2);

			boolean parity = (Math.abs(halfColumn + upperRowNum) % 2 == 1);

			// y = m*x + b, origin is upper-left.
			float m;
			float b;
			if (parity) {
				// Diagonal upper-left to lower-right.
				m = pointLen / (tileWidth / 2);
				b = 0;
			} else {
				// Diagonal upper-right to lower-left.
				m = -1 * pointLen / (tileWidth / 2);
				b = pointLen;
			}

			float lineY = m * remainingWidth + b;
			if (parity) {
				if (lineY > remainingHeight) {
					// We're above the line. (origin is upper-left!)
					ret.y = lowerRowNum;
					ret.x = (halfColumn) / 2;
				} else {
					ret.y = upperRowNum;
					ret.x = (halfColumn - 1) / 2;
				}
			} else {
				if (lineY > remainingHeight) {
					// We're above the line.
					ret.x = (halfColumn - 1) / 2;
					ret.y = lowerRowNum;
				} else {
					ret.x = (halfColumn / 2);
					ret.y = upperRowNum;
				}
			}
		}

		return ret;
	}

	public static PVector hexToCornerPoint(MapFile<PImage> map, HexCoords coords, float mapOffsetX, float mapOffsetY) {
		PVector ret = hexToCornerPoint(map, coords);
		ret.x += mapOffsetX;
		ret.y += mapOffsetY;
		return ret;
	}

	public static PVector hexToCornerPoint(MapFile<PImage> map, HexCoords coords) {
		int pixelX = coords.x * map.tileWidth;
		if (coords.y % 2 == 1) {
			pixelX += ((float) map.tileWidth) / 2;
		}
		float combinedHeight = map.tileHeight + map.getHexSideLength();
		int rowPairs = (int) Math.floor((float) coords.y / 2.0);
		float pixelY = combinedHeight * rowPairs;
		if (coords.y % 2 == 1) {
			pixelY += map.getHexSideLength() + (map.tileHeight - map.getHexSideLength()) / 2;
		}

		// Tile images are relative to the bottom-left corner of the image.
		pixelY += map.tileHeight;
		return new PVector(pixelX, pixelY);
	}

	public static PVector hexToCenterPoint(MapFile<PImage> map, HexCoords coords, float mapOffsetX, float mapOffsetY) {
		PVector ret = hexToCenterPoint(map, coords);
		ret.x += mapOffsetX;
		ret.y += mapOffsetY;
		return ret;
	}

	public static PVector hexToCenterPoint(MapFile<PImage> map, HexCoords coords) {
		PVector ret = hexToCornerPoint(map, coords);
		ret.x += ((float) map.tileWidth) / 2;
		ret.y += ((float) map.tileHeight) / 2;
		return ret;
	}
}
