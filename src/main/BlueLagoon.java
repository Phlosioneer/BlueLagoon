package main;

import java.util.concurrent.atomic.AtomicBoolean;
import processing.core.PApplet;

public class BlueLagoon extends PApplet {

	public BlueLagoon() {
		// throw new UnsupportedOperationException("Constructor for Main not yet implemented.");
	}

	public static void main(String[] args) {
		PApplet.main("main.BlueLagoon");
	}

	// Developed as part of the Game-per-Week initiative.

	MainMap board;
	UiMap ui;
	Player[] players;

	// State machine:
	// - Start: Everything is false / null.
	// - Thread loads board and ui.
	// - When thread is done, it sets the atomic to true.
	//
	// - Every draw iteration, if loadingDone is true, continue with main draw loop.
	// - If it is false, check atomicLoadingDone. If it is true, set loadingDone to
	// true, continue with main draw loop.
	// - Otherwise, display loading screen.
	AtomicBoolean atomicLoadingDone;
	RuntimeException mapLoadError;
	boolean loadingDone;

	int loadingScreenCounter;

	@Override
	public void settings() {
		size(1250, 800);
	}

	@Override
	public void setup() {
		// Setup the map loader.
		atomicLoadingDone = new AtomicBoolean(false);
		mapLoadError = null;
		loadingDone = false;
		players = new Player[4];
		for (int i = 0; i < 4; i++) {
			players[i] = new Player(i);
		}

		BlueLagoon self = this;
		Thread loader = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					board = new MainMap(self, "data/main_map.tmx");
					ui = new UiMap(self, "data/ui.tmx");
				} catch (RuntimeException e) {
					mapLoadError = e;
				} finally {
					atomicLoadingDone.set(true);
				}
			}
		});
		loader.start();

		loadingScreenCounter = 0;
	}

	@Override
	public void draw() {
		// Check if we're loaded.
		if (!loadingDone) {
			if (atomicLoadingDone.get()) {
				if (mapLoadError != null) {
					mapLoadError.printStackTrace();
					throw mapLoadError;
				}
				loadingDone = true;
			} else {
				drawLoadingScreen();
				return;
			}
		}

		background(0);
		board.draw();
		ui.draw();
	}

	void drawLoadingScreen() {
		background(0);
		fill(255);
		int rectBottom = height / 2 - 10;
		int x1 = (loadingScreenCounter % 60) * (width / 60);
		int x2 = x1 - width;

		rect(x1, rectBottom, width / 2, 20);
		rect(x2, rectBottom, width / 2, 20);

		loadingScreenCounter += 1;
	}
}
