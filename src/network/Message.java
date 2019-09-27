package network;

import com.google.gson.Gson;

abstract public class Message {

	public String type;

	public Message() {}

	public Message(String type) {
		this.type = type;
	}

	public String toJson() {
		return new Gson().toJson(this);
	}
}
