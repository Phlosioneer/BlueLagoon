package network.messages;

import network.Message;
import network.MessageParser;
import network.enums.Color;

public class HostConcedeMessage extends Message {
	public Color oldColor;
	public Color newColor;

	public HostConcedeMessage() {}

	public HostConcedeMessage(Color oldColor, Color newColor) {
		super("HostConcede");
		this.oldColor = oldColor;
		this.newColor = newColor;
	}

	public static void register() {
		MessageParser.registerMessage("HostConcede", HostConcedeMessage.class);
	}
}
