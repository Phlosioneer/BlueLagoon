package network.messages;

import network.Message;
import network.MessageParser;
import network.enums.Color;

public class KickedComputerStandinMessage extends Message {

	public Color color;
	public String newName;

	public KickedComputerStandinMessage() {}

	public KickedComputerStandinMessage(Color color, String newName) {
		super("KickedComputerStandin");
		this.color = color;
		this.newName = newName;
	}

	public static void register() {
		MessageParser.registerMessage("KickedComputerStandin", KickedComputerStandinMessage.class);
	}
}
