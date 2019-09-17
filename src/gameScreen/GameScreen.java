package gameScreen;

import main.Screen;
import processing.core.PApplet;

public class GameScreen extends Screen {

	public MainMap board;
	public UiMap ui;
	public Player[] players;

	public GameScreen(PApplet app) {
		players = new Player[4];
		for (int i = 0; i < 4; i++) {
			players[i] = new Player(i);
		}
		board = new MainMap(this, app, "data/main_map.tmx");
		ui = new UiMap(this, app, "data/ui.tmx");
	}

	@Override
	public void draw(PApplet app) {
		app.background(0);
		board.draw(app);
		ui.draw(app);
	}
}
