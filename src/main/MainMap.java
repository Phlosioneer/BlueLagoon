package main;

import java.util.ArrayList;
import core.MapFile;
import core.ObjectLayer;
import core.TMXObject;
import core.TileLayer;
import core.TileObject;
import main.Geometry.HexCoords;
import processing.core.PImage;
import processing.core.PVector;

public class MainMap {
	BlueLagoon app;
	MapFile<PImage> map;
	PImage background;

	TileLayer<PImage> backgroundOcean;
	TileLayer<PImage> backgroundTerrain;
	ObjectLayer<PImage> backgroundTowns;
	TileLayer<PImage> backgroundGrid;
	ObjectLayer<PImage> spawns;
	TileLayer<PImage> pieces;

	ArrayList<TMXObject> gemObjects;
	ArrayList<TMXObject> breadObjects;
	ArrayList<TMXObject> rockObjects;
	ArrayList<TMXObject> woodObjects;
	ArrayList<TMXObject> goldObjects;
	ArrayList<TMXObject> resourceSpawnPoints;

	ArrayList<TMXObject> allResources;

	TMXObject mouseCursorOutline;
	int offsetX;
	int offsetY;

	final int hexSideLength;

	@SuppressWarnings("unchecked")
	MainMap(BlueLagoon app, String filePath) {
		this.app = app;
		map = Boilerplate.openMap(app, filePath);
		assert (map.orientation == MapFile.Orientation.HEXAGONAL);
		assert (map.staggerAxis == MapFile.StaggerAxis.Y);
		assert (map.staggerIndex == MapFile.StaggerIndex.ODD);
		assert (map.renderOrder == MapFile.RenderOrder.RIGHT_DOWN);

		hexSideLength = (int) map.getHexSideLength();

		backgroundOcean = (TileLayer<PImage>) map.getLayerByName("Background Ocean");
		backgroundTerrain = (TileLayer<PImage>) map.getLayerByName("Map");
		backgroundTowns = (ObjectLayer<PImage>) map.getLayerByName("Towns");
		backgroundGrid = (TileLayer<PImage>) map.getLayerByName("Hex Grid");
		spawns = (ObjectLayer<PImage>) map.getLayerByName("Spawns");
		pieces = (TileLayer<PImage>) map.getLayerByName("Player Pieces");
		ObjectLayer<PImage> areas = (ObjectLayer<PImage>) map.getLayerByName("Areas");

		// Make everything invisible unless we tell it otherwise.
		for (TMXObject obj : spawns.objects) {
			obj.isVisible = false;
		}

		gemObjects = spawns.getObjectsByName("Gems");
		breadObjects = spawns.getObjectsByName("Bread");
		rockObjects = spawns.getObjectsByName("Rock");
		woodObjects = spawns.getObjectsByName("Wood");
		goldObjects = spawns.getObjectsByName("Gold");
		mouseCursorOutline = spawns.getObjectByName("Cursor");
		resourceSpawnPoints = spawns.getObjectsByName("Resource Spawn");
		TMXObject visibleMapBounds = areas.getObjectByName("Visible Area");

		offsetX = -1 * (int) visibleMapBounds.position.x;
		offsetY = -1 * (int) visibleMapBounds.position.y;

		allResources = new ArrayList<TMXObject>();
		allResources.addAll(gemObjects);
		allResources.addAll(breadObjects);
		allResources.addAll(rockObjects);
		allResources.addAll(woodObjects);
		allResources.addAll(goldObjects);

		// Correct the positions for all the resource spawn points. They're placed by hand
		// in the tiled file, but we need them in the exact center of the hex.
		for (TMXObject point : resourceSpawnPoints) {
			HexCoords hexCoords = Geometry.pointToHex(map, point.position.x, point.position.y);
			PVector centerPoint = Geometry.hexToCenterPoint(map, hexCoords);
			point.position.x = centerPoint.x;
			point.position.y = centerPoint.y;
		}

		// Pick spawn points randomly.
		shuffleArray(resourceSpawnPoints);
		int spawnIndex = 0;
		for (TMXObject obj : allResources) {
			TileObject<PImage> castedObj = (TileObject<PImage>) obj;
			TMXObject point = resourceSpawnPoints.get(spawnIndex);
			spawnIndex += 1;
			float tileWidth = castedObj.tile.pixelRect.width;
			float tileHeight = castedObj.tile.pixelRect.height;
			castedObj.position.x = point.position.x - tileWidth / 2;
			castedObj.position.y = point.position.y - tileHeight;

			// Make all of them visible while we're iterating.
			castedObj.isVisible = true;
		}
	}

	void draw() {
		Boilerplate.drawHexStaticLayer(app, map, backgroundOcean, offsetX, offsetY);
		Boilerplate.drawHexStaticLayer(app, map, backgroundTerrain, offsetX, offsetY);
		Boilerplate.drawObjectLayer(app, backgroundTowns, offsetX, offsetY);
		Boilerplate.drawHexStaticLayer(app, map, backgroundGrid, offsetX, offsetY);
		Boilerplate.drawHexStaticLayer(app, map, pieces, offsetX, offsetY);
		Boilerplate.drawObjectLayer(app, spawns, offsetX, offsetY);

		HexCoords mouseOver = Geometry.pointToHex(map, app.mouseX, app.mouseY, offsetX, offsetY);
		mouseCursorOutline.isVisible = false;
		if (mouseOver.x >= 0 && mouseOver.y >= 0) {
			if (mouseOver.x < map.mapWidth && mouseOver.y < map.mapHeight) {
				PVector cursorPos = Geometry.hexToCornerPoint(map, mouseOver);
				mouseCursorOutline.position.x = cursorPos.x;
				mouseCursorOutline.position.y = cursorPos.y;
				mouseCursorOutline.isVisible = true;
			}
		}
	}

	<T> void shuffleArray(ArrayList<T> array) {
		for (int i = 0; i < array.size(); i++) {
			int j = (int) Math.floor(app.random(i, array.size()));
			T temp = array.get(i);
			array.set(i, array.get(j));
			array.set(j, temp);
		}
	}
}
