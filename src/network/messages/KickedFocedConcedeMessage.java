package network.messages;

import network.Message;
import network.MessageParser;
import network.enums.Color;

public class KickedFocedConcedeMessage extends Message {
	public Color color;

	public KickedFocedConcedeMessage() {}

	public KickedFocedConcedeMessage(Color color) {
		super("KickedForcedConcede");
		this.color = color;
	}

	public static void register() {
		MessageParser.registerMessage("KickedForcedConcede", KickedFocedConcedeMessage.class);
	}
}
