package network.messages;

import network.Message;
import network.MessageParser;
import network.enums.Color;

public class KickedGameResumeMessage extends Message {

	public Color color;
	public String name;

	public KickedGameResumeMessage() {}

	public KickedGameResumeMessage(Color color, String name) {
		super("KickedGameResume");
		this.color = color;
		this.name = name;
	}

	public static void register() {
		MessageParser.registerMessage("KickedGameResume", KickedGameResumeMessage.class);
	}
}
