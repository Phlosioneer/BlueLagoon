package network.messages;

import network.Message;
import network.MessageParser;

public class ListGamesMessage extends Message {

	public ListGamesMessage() {
		super("ListGames");
	}

	public static void register() {
		MessageParser.registerMessage("ListGames", ListGamesMessage.class);
	}
}
