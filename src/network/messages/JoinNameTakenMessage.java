package network.messages;

import network.Message;
import network.MessageParser;

public class JoinNameTakenMessage extends Message {

	public String clientId;

	public JoinNameTakenMessage() {}

	public JoinNameTakenMessage(String clientId) {
		super("JoinNameTaken");
		this.clientId = clientId;
	}

	public static void register() {
		MessageParser.registerMessage("JoinNameTaken", JoinNameTakenMessage.class);
	}
}
