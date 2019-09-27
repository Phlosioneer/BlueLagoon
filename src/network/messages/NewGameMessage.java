package network.messages;

import network.Message;
import network.MessageParser;

public class NewGameMessage extends Message {
	public String topic;
	public String name;

	public NewGameMessage() {}

	public NewGameMessage(String topic, String name) {
		super("NewGame");
		this.topic = topic;
		this.name = name;
	}

	public static void register() {
		MessageParser.registerMessage("NewGame", NewGameMessage.class);
	}
}
