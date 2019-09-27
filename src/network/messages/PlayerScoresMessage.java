package network.messages;

import network.Message;
import network.MessageParser;
import network.enums.Color;

public class PlayerScoresMessage extends Message {
	public ScoreEntry[] scores;

	public PlayerScoresMessage() {}

	public PlayerScoresMessage(int playerCount) {
		super("PlayerScores");
		scores = new ScoreEntry[playerCount];
	}

	public static void register() {
		MessageParser.registerMessage("PlayerScores", PlayerScoresMessage.class);
	}

	public static class ScoreEntry {
		public Color color;
		public int score;

		public ScoreEntry() {}

		public ScoreEntry(Color color, int score) {
			this.color = color;
			this.score = score;
		}
	}
}
