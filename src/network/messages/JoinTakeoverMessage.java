package network.messages;

import network.Message;
import network.MessageParser;

public class JoinTakeoverMessage extends Message {

	public String clientId;

	public JoinTakeoverMessage() {}

	public JoinTakeoverMessage(String clientId) {
		super("JoinTakeover");
		this.clientId = clientId;
	}

	public static void register() {
		MessageParser.registerMessage("JoinTakeover", JoinTakeoverMessage.class);
	}
}
