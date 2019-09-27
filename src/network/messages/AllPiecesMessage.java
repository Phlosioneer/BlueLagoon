package network.messages;

import network.Message;
import network.MessageParser;
import network.enums.Color;
import network.enums.GamePiece;

public class AllPiecesMessage extends Message {
	public PieceEntry[] pieces;

	public AllPiecesMessage() {}

	public AllPiecesMessage(int playerCount) {
		super("AllPieces");
		pieces = new PieceEntry[playerCount];
	}

	public static void register() {
		MessageParser.registerMessage("AllPieces", AllPiecesMessage.class);
	}

	public static class PieceEntry {
		public GamePiece name;
		public int x;
		public int y;
		public Color color;

		public PieceEntry() {}

		public PieceEntry(GamePiece name, int x, int y) {
			this.name = name;
			this.x = x;
			this.y = y;
		}

		public PieceEntry(GamePiece name, int x, int y, Color color) {
			this.name = name;
			this.x = x;
			this.y = y;
			this.color = color;
		}
	}
}
