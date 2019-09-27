package network.messages;

import network.Message;
import network.MessageParser;
import network.enums.Color;
import network.enums.GamePiece;

public class PlayPieceMessage extends Message {
	public Color color;
	public GamePiece piece;
	public int x;
	public int y;

	public PlayPieceMessage() {}

	public PlayPieceMessage(Color color, GamePiece piece, int x, int y) {
		super("PlayPiece");
		this.color = color;
		this.piece = piece;
		this.x = x;
		this.y = y;
	}

	public static void register() {
		MessageParser.registerMessage("PlayPiece", PlayPieceMessage.class);
	}
}
