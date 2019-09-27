package startScreen;

import java.util.ArrayList;
import core.Layer;
import core.LayerGroup;
import core.ObjectLayer;
import core.TileLayer;
import processing.core.PImage;

public class HostTab {

	ObjectLayer<PImage> staticObjects;
	ObjectLayer<PImage> spawns;
	ArrayList<TileLayer<PImage>> staticTiles;

	public HostTab(LayerGroup<PImage> group) {
		ArrayList<Layer> simpleStaticTiles = group.getLayersByName("Static Tiles");
		staticTiles = new ArrayList<>(simpleStaticTiles.size());
		for (Layer layer : simpleStaticTiles) {
			staticTiles.add(layer.asTiles());
		}

		ObjectLayer<PImage> areas = group.getLayerByName("Areas").asObjects();
		spawns = group.getLayerByName("Spawns").asObjects();
		staticObjects = group.getLayerByName("Static Objects").asObjects();
	}
}
