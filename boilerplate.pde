


MapFile<PImage> openMap(String filename) {
  ProcessingFileOpener fileOpener = new ProcessingFileOpener();
  ProcessingImageHandler imageHandler = new ProcessingImageHandler(this);
  return new MapFile<PImage>(filename, fileOpener, imageHandler);
}

class ProcessingFileOpener implements FileLocatorDelegate {
  ProcessingFileOpener() {
  }
  
  InputStream openFile(String filename) {
    return createInput(filename);    
  }
}

class ProcessingImageHandler implements ImageDelegate<PImage> {
  PApplet app;
  
  ProcessingImageHandler(PApplet app) {
    this.app = app;
  }
  
  PImage loadImage(String filename, TMXColor transparentColor) {
    PImage ret = app.loadImage(filename);
    ret.loadPixels();
    
    if (transparentColor != null) {
      color convertedColor = tmxColorConvert(transparentColor);
      for (int i = 0; i < ret.pixels.length; i++) {
        if (ret.pixels[i] == convertedColor) {
          ret.pixels[i] = color(0, 0);
        }
      }
    }
    return ret;
  }
  
  PImage sliceImage(PImage tilesetImage, Rect bounds) {
    PImage ret = createImage(bounds.width, bounds.height, ARGB);
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

void drawObjectLayer(ObjectLayer<PImage> layer) {
  drawObjectLayer(layer, 0, 0);
}

void drawObjectLayer(ObjectLayer<PImage> layer, int offsetX, int offsetY) {
  for (TMXObject object : layer.objects) {
    if (!object.isVisible) {
      continue;
    }
    
    if (object instanceof TileObject) {
      TileObject<PImage> castedObject = (TileObject<PImage>)object;
      PImage tile = castedObject.tile.image;
      image(tile, castedObject.position.x + offsetX, castedObject.position.y - tile.height + offsetY);
    } else if (object instanceof TextObject) {
      drawText((TextObject)object, offsetX, offsetY);
    }
  }
}

void drawOrthoStaticLayer(MapFile<PImage> map, TileLayer<PImage> layer) {
  drawOrthoStaticLayer(map, layer, 0, 0);
}

void drawOrthoStaticLayer(MapFile<PImage> map, TileLayer<PImage> layer, int offsetX, int offsetY) {
  tint(255, layer.opacity * 255);
  for (int x = 0; x < layer.width; x++) {
    for (int y = 0; y < layer.height; y++) {
      Tile<PImage> tile = layer.tiles[x][y];
      if (tile == null) {
        continue;
      }
      int pixelX = offsetX + x * map.tileWidth + (int)layer.offset.x;
      int pixelY = offsetY + y * map.tileHeight + (int)layer.offset.y;
      image(tile.image, pixelX, pixelY);
    }
  }
  noTint();
}

void drawHexStaticLayer(MapFile<PImage> map, TileLayer<PImage> layer) {
  drawHexStaticLayer(map, layer, 0, 0);
}

// Assumes staggerIdex = odd, and staggerAxis = Y
void drawHexStaticLayer(MapFile<PImage> map, TileLayer<PImage> layer, int offsetX, int offsetY) {
  int hexSideLength = (int)map.getHexSideLength();
  tint(255, layer.opacity * 255);
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
      int rowPairs = (int) floor((float)y / 2.0);
      int pixelY = offsetY + combinedHeight * rowPairs;
      if (y % 2 == 1) {
        pixelY += hexSideLength + (map.tileHeight - hexSideLength) / 2;
      }
      image(tile.image, pixelX, pixelY);
    }
  }
  noTint();
}

color tmxColorConvert(TMXColor otherColor) {
  return color(otherColor.red, otherColor.green, otherColor.blue, otherColor.alpha);
}
