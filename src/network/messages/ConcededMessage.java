package network.messages;

import network.Message;
import network.MessageParser;
import network.enums.Color;

public class ConcededMessage extends Message {

	public Color color;

	public ConcededMessage() {}

	public ConcededMessage(Color color) {
		super("Conceded");
		this.color = color;
	}

	public static void register() {
		MessageParser.registerMessage("Conceded", ConcededMessage.class);
	}
}
