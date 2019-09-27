package network.messages;

import network.Message;
import network.MessageParser;
import network.enums.Color;

public class KickedGamePauseMessage extends Message {

	public Color color;

	public KickedGamePauseMessage() {}

	public KickedGamePauseMessage(Color color) {
		super("KickedGamePause");
		this.color = color;
	}

	public static void register() {
		MessageParser.registerMessage("KickedGamePause", KickedGamePauseMessage.class);
	}
}
