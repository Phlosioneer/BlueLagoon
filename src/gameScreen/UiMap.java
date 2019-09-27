package gameScreen;

import java.util.ArrayList;
import core.LayerGroup;
import core.MapFile;
import core.ObjectLayer;
import core.TMXObject;
import core.TextObject;
import core.Tile;
import core.TileLayer;
import core.TileObject;
import main.Boilerplate;
import processing.core.PApplet;
import processing.core.PImage;

public class UiMap {
	GameScreen parent;

	MapFile<PImage> map;

	TileLayer<PImage> staticTiles;
	ObjectLayer<PImage> staticText;
	ObjectLayer<PImage> spawns;

	TextObject connectionStatusMessage;
	TextObject turnCountMessage;
	TileObject<PImage> hostColorIndicator;

	ArrayList<TextObject> rankNames;
	ArrayList<TileObject<PImage>> rankColorIndicators;

	PlayerInfoPanel staticInfoPanel;
	PlayerInfoPanel floatingInfoPanel;
	boolean floatingInfoPanelIsVisible;

	ArrayList<ToolTip> tooltips;

	@SuppressWarnings("unchecked")
	public UiMap(GameScreen parent, PApplet app, String filePath) {
		this.parent = parent;
		map = Boilerplate.openMap(app, filePath);

		LayerGroup<PImage> root = map.root;

		staticTiles = root.getLayerByName("Static Tiles").asTiles();
		staticText = root.getLayerByName("Static Text").asObjects();
		spawns = root.getLayerByName("Spawns").asObjects();
		ObjectLayer<PImage> areas = root.getLayerByName("Areas").asObjects();
		LayerGroup<PImage> infoPanelGroup = root.getLayerByName("Player Info Panel").asGroup();

		staticInfoPanel = new PlayerInfoPanel(parent, map, 1, infoPanelGroup);

		connectionStatusMessage = spawns.getObjectByName("Connection Status").asText();
		hostColorIndicator = spawns.getObjectByName("Host Color").asTile();
		turnCountMessage = spawns.getObjectByName("Turn Count").asText();

		// Get all the rank and player objects.
		rankNames = new ArrayList<>(4);
		rankColorIndicators = new ArrayList<>(4);
		for (int i = 1; i <= 4; i++) {
			rankNames.add(spawns.getObjectByName("Rank " + i + " Name").asText());
			rankColorIndicators.add(spawns.getObjectByName("Rank " + i + " Color").asTile());
			parent.players[i - 1].nameObject = spawns.getObjectByName("Player " + i + " Name").asText();
			parent.players[i - 1].colorIndicator = spawns.getObjectByName("Player " + i + " Color").asTile();
		}

		// Disable all the indicator tiles. They only exist for metadata.
		for (TMXObject obj : spawns.getObjectsByType("Indicator Tile")) {
			obj.isVisible = false;
		}

		TMXObject boardArea = areas.getObjectByName("Board Area");
		parent.board.offsetX += boardArea.position.x;
		parent.board.offsetY += boardArea.position.y;

		// Create tooltips.
		tooltips = new ArrayList<ToolTip>();
		LayerGroup<PImage> resourceTooltipTemplate = root.getLayerByName("Resource Tooltip").asGroup();
		ArrayList<LayerGroup<PImage>> tooltipTemplates = new ArrayList<>(4);
		tooltipTemplates.add(resourceTooltipTemplate);
		try {
			tooltipTemplates.add((LayerGroup<PImage>) resourceTooltipTemplate.clone());
			tooltipTemplates.add((LayerGroup<PImage>) resourceTooltipTemplate.clone());
			tooltipTemplates.add((LayerGroup<PImage>) resourceTooltipTemplate.clone());
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
		String[] tooltipNames = new String[]{"Wood", "Rock", "Gems", "Bread", "Gold", "Set Bonus", "Islands Visited", "Islands Controlled", "Longest Chain"};
		for (int i = 0; i < tooltipNames.length; i++) {
			String name = tooltipNames[i];
			LayerGroup<PImage> template = null;
			TMXObject tooltipArea = null;
			Tile<PImage> iconTile = null;
			if (i < 4) {
				// The 4 basic resources have dynamically placed tooltips.
				template = tooltipTemplates.get(i);
				tooltipArea = areas.getObjectByName(name + " Tooltip Area");
				iconTile = ((TileObject<PImage>) spawns.getObjectByName(name)).tile;

			} else {
				// The rest of the tooltips are placed in the correct spot already.
				template = (LayerGroup<PImage>) root.getLayerByName(name + " Tooltip");
			}
			TMXObject triggerArea = areas.getObjectByName(name + " Tooltip Trigger Area");
			ToolTip tooltip = new ToolTip(app, map, template, triggerArea, tooltipArea, iconTile);
			tooltips.add(tooltip);
		}
		tooltipTemplates.clear();

	}

	public void draw(PApplet app) {
		Boilerplate.drawOrthoStaticLayer(app, map, staticTiles);
		Boilerplate.drawObjectLayer(app, staticText);
		Boilerplate.drawObjectLayer(app, spawns);
		staticInfoPanel.draw(app);
		if (floatingInfoPanelIsVisible) {
			floatingInfoPanel.draw(app);
		}
		for (ToolTip tooltip : tooltips) {
			tooltip.draw();
		}
	}

	static class ToolTip {
		PApplet app;
		MapFile<PImage> parentMap;

		TileLayer<PImage> staticTiles;
		ObjectLayer<PImage> staticText;
		ObjectLayer<PImage> spawns;
		TMXObject triggerArea;
		int offsetX;
		int offsetY;

		ToolTip(PApplet app, MapFile<PImage> parentMap, LayerGroup<PImage> template, TMXObject triggerArea) {
			this(app, parentMap, template, triggerArea, null, null);
		}

		@SuppressWarnings("unchecked")
		ToolTip(PApplet app, MapFile<PImage> parentMap, LayerGroup<PImage> template, TMXObject triggerArea, TMXObject tooltipArea, Tile<PImage> resourceIcon) {
			this.app = app;
			this.triggerArea = triggerArea;
			this.parentMap = parentMap;
			staticTiles = template.getLayerByName("Static Tiles").asTiles();
			staticText = template.getLayerByName("Static Text").asObjects();
			offsetX = (int) template.offset.x;
			offsetY = (int) template.offset.y;
			if (resourceIcon != null) {
				spawns = (ObjectLayer<PImage>) template.getLayerByName("Spawns");
				ArrayList<TMXObject> icons = spawns.getObjectsByName("Resource Icon");
				for (TMXObject object : icons) {
					TileObject<PImage> castObject = object.asTile();
					castObject.tile = resourceIcon;
				}

				offsetX = (int) tooltipArea.position.x;
				offsetY = (int) tooltipArea.position.y;
			}
		}

		void draw() {
			if (app.mouseX >= triggerArea.position.x && app.mouseY >= triggerArea.position.y) {
				float maxX = triggerArea.position.x + triggerArea.size.x;
				float maxY = triggerArea.position.y + triggerArea.size.y;
				if (app.mouseX < maxX && app.mouseY < maxY) {
					// Draw the tooltip.
					Boilerplate.drawOrthoStaticLayer(app, parentMap, staticTiles, offsetX, offsetY);
					Boilerplate.drawObjectLayer(app, staticText, offsetX, offsetY);
					if (spawns != null) {
						Boilerplate.drawObjectLayer(app, spawns, offsetX, offsetY);
					}
				}
			}
		}
	}
}
