package network.messages;

import network.Message;
import network.MessageParser;

public class UnrecognizedPlayerMessage extends Message {
	public String clientId;

	public UnrecognizedPlayerMessage() {}

	public UnrecognizedPlayerMessage(String clientId) {
		super("UnrecognizedPlayer");
		this.clientId = clientId;
	}

	public static void register() {
		MessageParser.registerMessage("UnrecognizedPlayer", UnrecognizedPlayerMessage.class);
	}
}
