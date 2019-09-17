package gameScreen;

import java.util.ArrayList;
import core.LayerGroup;
import core.MapFile;
import core.ObjectLayer;
import core.TMXObject;
import core.TextObject;
import core.TileLayer;
import core.TileObject;
import main.Boilerplate;
import processing.core.PApplet;
import processing.core.PImage;

public class PlayerInfoPanel {
	GameScreen parentScreen;

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

	PlayerInfoPanel(GameScreen parentScreen, MapFile<PImage> parentMap, int playerNumber, LayerGroup<PImage> template) {
		this.playerNumber = playerNumber;
		this.parentMap = parentMap;
		this.parentScreen = parentScreen;

		offsetX = (int) template.offset.x;
		offsetY = (int) template.offset.y;

		staticTiles = template.getLayerByName("Static Tiles").asTiles();
		resourceTiles = template.getLayerByName("Resource Tiles").asTiles();
		staticText = template.getLayerByName("Static Text").asObjects();
		spawns = template.getLayerByName("Spawns").asObjects();

		previousRoundStaticText = spawns.getObjectsByName("Previous Round Static Text");
		for (TMXObject object : previousRoundStaticText) {
			object.isVisible = false;
		}

		remainingPieces = spawns.getObjectByName("Tokens Left").asText();
		remainingHuts = spawns.getObjectByName("Huts Left").asText();
		totalPoints = spawns.getObjectByName("Player Total Score").asText();
		previousRoundPoints = spawns.getObjectByName("Previous Round Score").asText();
		playerPieceIcon = spawns.getObjectByName("Player Token").asTile();
		playerHutIcon = spawns.getObjectByName("Player Hut").asTile();

		scores = new ArrayList<CountAndScore>();
		scores.add(makeScoreObj("Rock", (obj, player)-> {
			obj.count.text = "" + player.rockCount;
			obj.score.text = "" + player.getRockScore();
		}));
		scores.add(makeScoreObj("Wood", (obj, player)-> {
			obj.count.text = "" + player.woodCount;
			obj.score.text = "" + player.getWoodScore();
		}));
		scores.add(makeScoreObj("Bread", (obj, player)-> {
			obj.count.text = "" + player.breadCount;
			obj.score.text = "" + player.getBreadScore();
		}));
		scores.add(makeScoreObj("Gem", (obj, player)-> {
			obj.count.text = "" + player.gemCount;
			obj.score.text = "" + player.getGemScore();
		}));
		scores.add(makeScoreObj("Gold", (obj, player)-> {
			obj.count.text = "" + player.goldCount;
			obj.score.text = "" + player.getGoldScore();
		}));
		scores.add(makeScoreObj("Islands Visited", (obj, player)-> {
			obj.count.text = "" + player.islandsVisited;
			obj.score.text = "" + player.getIslandsVisitedScore();
		}));
		scores.add(makeScoreObj("Islands Controlled", (obj, player)-> {
			obj.count.text = "" + player.islandsControlled;
			obj.score.text = "" + player.islandsControlledScore;
		}));
		scores.add(makeScoreObj("Longest Chain", (obj, player)-> {
			obj.count.text = "" + player.longestChain;
			obj.score.text = "" + player.getLongestChainScore();
		}));

		TextObject setBonusStatus = spawns.getObjectByName("Set Bonus Status").asText();
		TextObject setBonusScore = spawns.getObjectByName("Set Bonus Score").asText();
		scores.add(new CountAndScore(parentScreen, this, setBonusStatus, setBonusScore, (obj, player)-> {
			if (player.hasSetBonus()) {
				obj.count.text = "Yes";
			} else {
				obj.count.text = "No";
			}
			obj.score.text = "" + player.getSetBonusScore();
		}));
	}

	private CountAndScore makeScoreObj(String base, ScoreUpdater updater) {
		TextObject count = spawns.getObjectByName(base + " Count").asText();
		TextObject score = spawns.getObjectByName(base + " Score").asText();
		return new CountAndScore(parentScreen, this, count, score, updater);
	}

	public void draw(PApplet app) {
		for (CountAndScore score : scores) {
			score.update();
		}
		Player player = parentScreen.players[playerNumber];
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
		Boilerplate.drawOrthoStaticLayer(app, parentMap, staticTiles, offsetX, offsetY);
		Boilerplate.drawOrthoStaticLayer(app, parentMap, resourceTiles, offsetX, offsetY);
		Boilerplate.drawObjectLayer(app, staticText, offsetX, offsetY);
		Boilerplate.drawObjectLayer(app, spawns, offsetX, offsetY);
	}

	private static class CountAndScore {
		GameScreen parentScreen;
		TextObject count;
		TextObject score;
		PlayerInfoPanel parent;
		ScoreUpdater updater;

		public CountAndScore(GameScreen parentScreen, PlayerInfoPanel parent, TextObject count, TextObject score, ScoreUpdater updater) {
			this.parentScreen = parentScreen;
			this.count = count;
			this.score = score;
			this.parent = parent;
			this.updater = updater;
		}

		public void update() {
			Player player = parentScreen.players[parent.playerNumber];
			updater.update(this, player);
		}
	}

	private static interface ScoreUpdater {
		void update(CountAndScore obj, Player player);
	}
}
