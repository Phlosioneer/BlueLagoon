package network.messages;

import network.Message;
import network.MessageParser;
import network.enums.Color;

public class JoinRequestMessage extends Message {
	public String clientId;
	public Color color;

	public JoinRequestMessage() {}

	public static void register() {
		MessageParser.registerMessage("JoinRequest", JoinRequestMessage.class);
	}
}
