package network.messages;

import network.Message;
import network.MessageParser;
import network.enums.Color;

public class CurrentTurnMessage extends Message {
	public Color color;

	public CurrentTurnMessage() {}

	public CurrentTurnMessage(Color color) {
		super("CurrentTurn");
		this.color = color;
	}

	public static void register() {
		MessageParser.registerMessage("CurrentTurn", CurrentTurnMessage.class);
	}
}
