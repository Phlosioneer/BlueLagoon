package network.messages;

import network.Message;
import network.MessageParser;
import network.enums.Color;

public class TurnOrderMessage extends Message {
	public Color[] order;

	public TurnOrderMessage() {}

	public TurnOrderMessage(int playerCount) {
		super("TurnOrder");
		order = new Color[playerCount];
	}

	public static void register() {
		MessageParser.registerMessage("TurnOrder", TurnOrderMessage.class);
	}
}
