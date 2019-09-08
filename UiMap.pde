
class UiMap {
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
  
  UiMap(String filePath) {
    map = openMap(filePath);
    
    staticTiles = (TileLayer<PImage>) map.getLayerByName("Static Tiles");
    staticText = (ObjectLayer<PImage>) map.getLayerByName("Static Text");
    spawns = (ObjectLayer<PImage>) map.getLayerByName("Spawns");
    ObjectLayer<PImage> areas = (ObjectLayer<PImage>) map.getLayerByName("Areas");
    LayerGroup<PImage> infoPanelGroup = (LayerGroup<PImage>) map.getLayerByName("Player Info Panel");
    
    staticInfoPanel = new PlayerInfoPanel(map, 1, infoPanelGroup);
    
    connectionStatusMessage = (TextObject) spawns.getObjectByName("Connection Status");
    hostColorIndicator = (TileObject<PImage>) spawns.getObjectByName("Host Color");
    turnCountMessage = (TextObject) spawns.getObjectByName("Turn Count");
    
    // Get all the rank and player objects.
    rankNames = new ArrayList<TextObject>(4);
    rankColorIndicators = new ArrayList<TileObject<PImage>>(4);
    for (int i = 1; i <= 4; i++) {
      rankNames.add((TextObject) spawns.getObjectByName("Rank " + i + " Name"));
      rankColorIndicators.add((TileObject<PImage>) spawns.getObjectByName("Rank " + i + " Color"));
      players[i - 1].nameObject = (TextObject) spawns.getObjectByName("Player " + i + " Name");
      players[i - 1].colorIndicator = (TileObject<PImage>) spawns.getObjectByName("Player " + i + " Color");
    }
    
    // Disable all the indicator tiles. They only exist for metadata.
    for (TMXObject obj : spawns.getObjectsByType("Indicator Tile")) {
      obj.isVisible = false;
    }
    
    TMXObject boardArea = areas.getObjectByName("Board Area");
    board.offsetX += boardArea.position.x;
    board.offsetY += boardArea.position.y;
    
    // Create tooltips.
    tooltips = new ArrayList<ToolTip>();
    LayerGroup<PImage> resourceTooltipTemplate = (LayerGroup<PImage>) map.getLayerByName("Resource Tooltip");
    ArrayList<LayerGroup<PImage>> tooltipTemplates = new ArrayList<LayerGroup<PImage>>(4);
    tooltipTemplates.add(resourceTooltipTemplate);
    try {
      tooltipTemplates.add((LayerGroup<PImage>) resourceTooltipTemplate.clone());
      tooltipTemplates.add((LayerGroup<PImage>) resourceTooltipTemplate.clone());
      tooltipTemplates.add((LayerGroup<PImage>) resourceTooltipTemplate.clone());
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
    String[] tooltipNames = new String[] {"Wood", "Rock", "Gems", "Bread", "Gold", "Set Bonus", "Islands Visited", "Islands Controlled", "Longest Chain"};
    for (int i = 0; i < tooltipNames.length; i++) {
      String name = tooltipNames[i];
      LayerGroup<PImage> template = null;
      if (i < 4) {
        template = tooltipTemplates.get(i);
      } else {
        template = (LayerGroup<PImage>) map.getLayerByName(name + " Tooltip");
      }
      TMXObject triggerArea = areas.getObjectByName(name + " Tooltip Trigger Area");
      TMXObject tooltipArea = areas.getObjectByName(name + " Tooltip Area");
      TileObject<PImage> icon = (TileObject<PImage>) spawns.getObjectByName(name);
      Tile<PImage> iconTile = null;
      if (icon != null) {
        iconTile = icon.tile;
      }
      ToolTip tooltip = new ToolTip(map, template, triggerArea, tooltipArea, iconTile);
      tooltips.add(tooltip);
    }
    tooltipTemplates.clear();
    
  }
  
  void draw() {
    drawOrthoStaticLayer(map, staticTiles);
    drawObjectLayer(staticText);
    drawObjectLayer(spawns);
    staticInfoPanel.draw();
    if (floatingInfoPanelIsVisible) {
      floatingInfoPanel.draw();
    }
    for (ToolTip tooltip : tooltips) {
      tooltip.draw();
    }
  }
}

class ToolTip {
  MapFile<PImage> parentMap;
  
  TileLayer<PImage> staticTiles;
  ObjectLayer<PImage> staticText;
  ObjectLayer<PImage> spawns;
  TMXObject triggerArea;
  int offsetX;
  int offsetY;
  
  ToolTip(MapFile<PImage> parentMap, LayerGroup<PImage> template, TMXObject triggerArea) {
    this(parentMap, template, triggerArea, null, null);
  }
  
  ToolTip(MapFile<PImage> parentMap, LayerGroup<PImage> template, TMXObject triggerArea, TMXObject tooltipArea, Tile<PImage> resourceIcon) {
    this.triggerArea = triggerArea;
    this.parentMap = parentMap;
    staticTiles = (TileLayer<PImage>) template.getLayerByName("Static Tiles");
    staticText = (ObjectLayer<PImage>) template.getLayerByName("Static Text");
    offsetX = (int)template.offset.x;
    offsetY = (int)template.offset.y;
    if (resourceIcon != null) {
      spawns = (ObjectLayer<PImage>) template.getLayerByName("Spawns");
      ArrayList<TMXObject> icons = spawns.getObjectsByName("Resource Icon");
      for (TMXObject object : icons) {
        TileObject<PImage> castObject = (TileObject<PImage>) object;
        castObject.tile = resourceIcon;
      }
      
      offsetX = (int)tooltipArea.position.x;
      offsetY = (int)tooltipArea.position.y;
    }
  }
  
  void draw() {
    if (mouseX >= triggerArea.position.x && mouseY >= triggerArea.position.y) {
      float maxX = triggerArea.position.x + triggerArea.size.x;
      float maxY = triggerArea.position.y + triggerArea.size.y;
      if (mouseX < maxX && mouseY < maxY) {
        // Draw the tooltip.
        drawOrthoStaticLayer(parentMap, staticTiles, offsetX, offsetY);
        drawObjectLayer(staticText, offsetX, offsetY);
        if (spawns != null) {
          drawObjectLayer(spawns, offsetX, offsetY);
        }
      }
    }
  }
}