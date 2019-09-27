package network.messages;

import network.Message;
import network.MessageParser;

public class GameStartedMessage extends Message {
	public String topic;

	public GameStartedMessage() {}

	public GameStartedMessage(String topic) {
		super("GameStarted");
		this.topic = topic;
	}

	public static void register() {
		MessageParser.registerMessage("GameStarted", GameStartedMessage.class);
	}
}
