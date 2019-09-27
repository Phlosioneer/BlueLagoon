package network.messages;

import network.Message;
import network.MessageParser;

public class JoinSuccessMessage extends Message {
	public String clientId;

	public JoinSuccessMessage() {}

	public JoinSuccessMessage(String clientId) {
		super("JoinSuccess");
		this.clientId = clientId;
	}

	public static void register() {
		MessageParser.registerMessage("JoinSuccess", JoinSuccessMessage.class);
	}
}
