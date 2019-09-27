package startScreen;

import core.MapFile;
import core.TileLayer;
import main.Boilerplate;
import main.Screen;
import processing.core.PApplet;
import processing.core.PImage;

public class StartScreen extends Screen {

	MapFile<PImage> map;

	HostTab hostTab;
	JoinTab joinTab;
	ServerTab serverTab;

	TileLayer<PImage> windowTiles;

	public StartScreen(PApplet app) {
		map = Boilerplate.openMap(app, "new_game_screen.tmx");
		windowTiles = map.root.getLayerByName("Window Tiles").asTiles();

		hostTab = new HostTab(map.root.getLayerByName("Host Tab").asGroup());
		joinTab = new JoinTab(map.root.getLayerByName("Join Tab").asGroup());
		serverTab = new ServerTab(map.root.getLayerByName("Server Tab").asGroup());
	}

	@Override
	public void draw(PApplet app) {

	}
}
