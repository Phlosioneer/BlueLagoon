package network.messages;

import network.Message;
import network.MessageParser;
import network.enums.Color;

public class PlayersMessage extends Message {

	public String hostColor;
	public PlayerEntry[] players;

	public PlayersMessage() {}

	public PlayersMessage(String hostColor, int playerCount) {
		super("Players");
		this.hostColor = hostColor;
		this.players = new PlayerEntry[playerCount];
	}

	public static void register() {
		MessageParser.registerMessage("Players", PlayersMessage.class);
	}

	public static class PlayerEntry {
		public String name;
		public Color color;
		public String clientId;

		public PlayerEntry(String name, Color color, String clientId) {
			this.name = name;
			this.color = color;
			this.clientId = clientId;
		}
	}
}
