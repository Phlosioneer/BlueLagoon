

class PlayerInfoPanel {
  int playerNumber;
  
  MapFile<PImage> parentMap;
  
  TileLayer<PImage> staticTiles;
  TileLayer<PImage> resourceTiles;
  ObjectLayer<PImage> staticText;
  ObjectLayer<PImage> spawns;
  
  ArrayList<TMXObject> previousRoundStaticText;
  ArrayList<CountAndScore> scores;
  
  TextObject remainingPieces;
  TextObject remainingHuts;
  TextObject totalPoints;
  TextObject previousRoundPoints;
  
  TileObject<PImage> playerPieceIcon;
  TileObject<PImage> playerHutIcon;
  
  int offsetX;
  int offsetY;
  
  PlayerInfoPanel(MapFile<PImage> parentMap, int playerNumber, LayerGroup<PImage> template) {
    this.playerNumber = playerNumber;
    this.parentMap = parentMap;
    
    offsetX = (int)template.offset.x;
    offsetY = (int)template.offset.y;
    
    staticTiles = (TileLayer<PImage>) template.getLayerByName("Static Tiles");
    resourceTiles = (TileLayer<PImage>) template.getLayerByName("Resource Tiles");
    staticText = (ObjectLayer<PImage>) template.getLayerByName("Static Text");
    spawns = (ObjectLayer<PImage>) template.getLayerByName("Spawns");
    
    previousRoundStaticText = spawns.getObjectsByName("Previous Round Static Text");
    for (TMXObject object : previousRoundStaticText) {
      object.isVisible = false;
    }
    
    remainingPieces = (TextObject) spawns.getObjectByName("Tokens Left");
    remainingHuts = (TextObject) spawns.getObjectByName("Huts Left");
    totalPoints = (TextObject) spawns.getObjectByName("Player Total Score");
    previousRoundPoints = (TextObject) spawns.getObjectByName("Previous Round Score");
    playerPieceIcon = (TileObject<PImage>) spawns.getObjectByName("Player Token");
    playerHutIcon = (TileObject<PImage>) spawns.getObjectByName("Player Hut");
    
    scores = new ArrayList<CountAndScore>();
    scores.add(makeScoreObj("Rock", new ScoreUpdater() {
      void update(CountAndScore obj, Player player) {
        obj.count.text = "" + player.rockCount;
        obj.score.text = "" + player.getRockScore();
      }
    }));
    scores.add(makeScoreObj("Wood", new ScoreUpdater() {
      void update(CountAndScore obj, Player player) {
        obj.count.text = "" + player.woodCount;
        obj.score.text = "" + player.getWoodScore();
      }
    }));
    scores.add(makeScoreObj("Bread", new ScoreUpdater() {
      void update(CountAndScore obj, Player player) {
        obj.count.text = "" + player.breadCount;
        obj.score.text = "" + player.getBreadScore();
      }
    }));
    scores.add(makeScoreObj("Gem", new ScoreUpdater() {
      void update(CountAndScore obj, Player player) {
        obj.count.text = "" + player.gemCount;
        obj.score.text = "" + player.getGemScore();
      }
    }));
    scores.add(makeScoreObj("Gold", new ScoreUpdater() {
      void update(CountAndScore obj, Player player) {
        obj.count.text = "" + player.goldCount;
        obj.score.text = "" + player.getGoldScore();
      }
    }));
    scores.add(makeScoreObj("Islands Visited", new ScoreUpdater() {
      void update(CountAndScore obj, Player player) {
        obj.count.text = "" + player.islandsVisited;
        obj.score.text = "" + player.getIslandsVisitedScore();
      }
    }));
    scores.add(makeScoreObj("Islands Controlled", new ScoreUpdater() {
      void update(CountAndScore obj, Player player) {
        obj.count.text = "" + player.islandsControlled;
        obj.score.text = "" + player.islandsControlledScore;
      }
    }));
    scores.add(makeScoreObj("Longest Chain", new ScoreUpdater() {
      void update(CountAndScore obj, Player player) {
        obj.count.text = "" + player.longestChain;
        obj.score.text = "" + player.getLongestChainScore();
      }
    }));
    
    TextObject setBonusStatus = (TextObject) spawns.getObjectByName("Set Bonus Status");
    TextObject setBonusScore = (TextObject) spawns.getObjectByName("Set Bonus Score");
    scores.add(new CountAndScore(this, setBonusStatus, setBonusScore, new ScoreUpdater() {
      void update(CountAndScore obj, Player player) {
        if (player.hasSetBonus()) {
          obj.count.text = "Yes";
        } else {
          obj.count.text = "No";
        }
        obj.score.text = "" + player.getSetBonusScore();
      }
    }));
  }
  
  CountAndScore makeScoreObj(String base, ScoreUpdater updater) {
    TextObject count = (TextObject) spawns.getObjectByName(base + " Count");
    TextObject score = (TextObject) spawns.getObjectByName(base + " Score");
    return new CountAndScore(this, count, score, updater);
  }
  
  void draw() {
    for (CountAndScore score : scores) {
      score.update();
    }
    Player player = players[playerNumber];
    totalPoints.text = "" + player.getTotalScore();
    if (player.previousRoundScore != 0) {
      previousRoundPoints.text = "" + player.previousRoundScore;
      previousRoundPoints.isVisible = true;
    } else {
      previousRoundPoints.isVisible = false;
    }
    remainingPieces.text = "" + player.remainingPieces;
    remainingHuts.text = "" + player.remainingHuts;
    if (player.pieceIcon != null) {
      playerPieceIcon.tile = player.pieceIcon;
    }
    if (player.hutIcon != null) {
      playerHutIcon.tile = player.hutIcon;
    }
    drawOrthoStaticLayer(parentMap, staticTiles, offsetX, offsetY);
    drawOrthoStaticLayer(parentMap, resourceTiles, offsetX, offsetY);
    drawObjectLayer(staticText, offsetX, offsetY);
    drawObjectLayer(spawns, offsetX, offsetY);
  }
}

class CountAndScore {
  TextObject count;
  TextObject score;
  PlayerInfoPanel parent;
  ScoreUpdater updater;
  
  CountAndScore(PlayerInfoPanel parent, TextObject count, TextObject score, ScoreUpdater updater) {
    this.count = count;
    this.score = score;
    this.parent = parent;
    this.updater = updater;
  }
  
  void update() {
    Player player = players[parent.playerNumber];
    updater.update(this, player);
  }
}

interface ScoreUpdater {
  void update(CountAndScore obj, Player player);
}