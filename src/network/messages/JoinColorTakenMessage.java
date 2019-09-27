package network.messages;

import network.Message;
import network.MessageParser;

public class JoinColorTakenMessage extends Message {
	public String clientId;

	public JoinColorTakenMessage() {}

	public JoinColorTakenMessage(String clientId) {
		super("JoinColorTaken");
		this.clientId = clientId;
	}

	public static void register() {
		MessageParser.registerMessage("JoinColorTaken", JoinColorTakenMessage.class);
	}
}
