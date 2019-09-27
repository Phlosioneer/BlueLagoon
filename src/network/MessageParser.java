package network;

import java.util.HashMap;
import com.google.gson.Gson;

public class MessageParser {

	private static HashMap<String, Class<? extends Message>> registeredMessages = null;

	private MessageParser() {}

	public static <T extends Message> void registerMessage(String messageType, Class<T> messageClass) {
		if (registeredMessages == null) {
			registeredMessages = new HashMap<>();
		}

		registeredMessages.put(messageType, messageClass);
	}

	public static Message parseMessage(String input) {
		if (registeredMessages == null) {
			throw new RuntimeException("No registered parsers.");
		}

		var parserInstance = new Gson();
		PartialMessage messageType = parserInstance.fromJson(input, PartialMessage.class);
		var messageParser = registeredMessages.get(messageType.type);
		if (messageParser == null) {
			throw new RuntimeException("No registered parser for message type: '" + messageType + "'");
		}

		return parserInstance.fromJson(input, messageParser);
	}

	private static class PartialMessage extends Message {
		@SuppressWarnings("unused")
		public PartialMessage() {}
	}

}
