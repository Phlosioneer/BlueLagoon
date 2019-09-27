package main;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import gameScreen.GameScreen;
import loadingScreen.LoadingScreen;
import processing.core.PApplet;
import startScreen.StartScreen;

public class BlueLagoon extends PApplet {
	public static void main(String[] args) {
		PApplet.main("main.BlueLagoon");
	}

	private LoadingScreen loadingScreen;
	private CachedFuture<StartScreen> startScreen;
	private CachedFuture<GameScreen> gameScreen;
	private boolean gameStarted;

	@Override
	public void settings() {
		size(1250, 800);
	}

	@Override
	public void setup() {
		loadingScreen = new LoadingScreen();
		// We can only load one map at a time, because the image slicing code is single-threaded.
		ExecutorService loader = Executors.newSingleThreadExecutor();
		startScreen = new CachedFuture<>(loader.submit(()->new StartScreen(this)));
		gameScreen = new CachedFuture<>(loader.submit(()->new GameScreen(this)));

		// TODO: Disabled start screen.
		// gameStarted = false;
		gameStarted = true;
	}

	@Override
	public void draw() {
		if (!gameStarted && startScreen.isDone()) {
			startScreen.get().draw(this);
		} else if (gameStarted && gameScreen.isDone()) {
			gameScreen.get().draw(this);
		} else {
			loadingScreen.draw(this);
		}
	}

	void drawLoadingScreen() {

	}
}
