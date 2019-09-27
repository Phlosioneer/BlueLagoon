package network.messages;

import network.Message;
import network.MessageParser;

public class LastWillMessage extends Message {
	public String clientId;

	public LastWillMessage() {}

	public LastWillMessage(String clientId) {
		super("LastWill");
		this.clientId = clientId;
	}

	public static void register() {
		MessageParser.registerMessage("LastWill", LastWillMessage.class);
	}
}
