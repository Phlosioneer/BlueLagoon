

class Player {
  // Used by MainMap.
  Tile<PImage> pieceSprite;
  Tile<PImage> hutSprite;
  int remainingPieces;
  int remainingHuts;
  
  // Used by UiMap.
  Tile<PImage> pieceIcon;
  Tile<PImage> hutIcon;
  TileObject<PImage> colorIndicator;
  TextObject nameObject;
  TextObject scoreObject;
  
  TextObject rankNameObject;
  TileObject<PImage> rankColorIndicator;
  
  int gemCount;
  int woodCount;
  int breadCount;
  int rockCount;
  int goldCount;
  
  int islandsVisited;
  int islandsControlled;
  int islandsControlledScore;
  int longestChain;
  int previousRoundScore;
  
  String playerColor;
  int playerNumber;
  String playerName;
  
  Player(int playerNumber) {
    pieceIcon = null;
    hutIcon = null;
    remainingPieces = 0;
    remainingHuts = 0;
    
    gemCount = 0;
    woodCount = 0;
    breadCount = 0;
    rockCount = 0;
    goldCount = 0;
    
    playerColor = null;
    this.playerNumber = playerNumber;
    playerName = null;
  }
  
  int getRockScore() {
    return getResourceScore(rockCount);
  }
  
  int getWoodScore() {
    return getResourceScore(woodCount);
  }
  
  int getBreadScore() {
    return getResourceScore(breadCount);
  }
  
  int getGemScore() {
    return getResourceScore(gemCount);
  }
  
  int getResourceScore(int count) {
    if (count < 2) {
      return 0;
    } else if (count == 2) {
      return 5;
    } else if (count == 3) {
      return 10;
    } else {
      return 20;
    }
  }
  
  int getGoldScore() {
    return goldCount * 4;
  }
  
  boolean hasSetBonus() {
    return woodCount > 0 && rockCount > 0 && gemCount > 0 && breadCount > 0;
  }
  
  int getSetBonusScore() {
    if (hasSetBonus()) {
      return 10;
    } else {
      return 0;
    }
  }
  
  int getIslandsVisitedScore() {
    if (islandsVisited == 7) {
      return 10;
    } else if (islandsVisited == 8) {
      return 20;
    } else {
      return 0;
    }
  }
  
  int getLongestChainScore() {
    return longestChain * 5;
  }
  
  int getTotalScore() {
    return
        getRockScore()
      + getGemScore()
      + getWoodScore()
      + getBreadScore()
      + getGoldScore()
      + getSetBonusScore()
      + getIslandsVisitedScore()
      + islandsControlledScore
      + getLongestChainScore()
      + previousRoundScore;
  }
}
