package main;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import gameScreen.GameScreen;
import loadingScreen.LoadingScreen;
import processing.core.PApplet;
import startScreen.StartScreen;
import ui.NineSlice;

public class BlueLagoon extends PApplet {
	public static void main(String[] args) {
		PApplet.main("main.BlueLagoon");
	}

	private LoadingScreen loadingScreen;
	private CachedFuture<StartScreen> startScreen;
	private CachedFuture<GameScreen> gameScreen;
	private boolean gameStarted;

	private NineSlice nSlice;
	
	@Override
	public void settings() {
		size(1250, 800);
	}

	@Override
	public void setup() {
		nSlice = new NineSlice(this,this.loadImage("jankyslice.png"),32,32,32,32);
		
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
		this.background(255);
		if(nSlice != null) {
			
			nSlice.drawAround(this.g, 50, 50, this.mouseX - 50, this.mouseY - 50);
		}
		
		
	}
	
	public void draw2() {
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
