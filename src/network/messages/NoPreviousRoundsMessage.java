package network.messages;

import network.Message;
import network.MessageParser;

public class NoPreviousRoundsMessage extends Message {

	public NoPreviousRoundsMessage() {
		super("NoPreviousRounds");
	}

	public static void register() {
		MessageParser.registerMessage("NoPreviousRounds", NoPreviousRoundsMessage.class);
	}
}
