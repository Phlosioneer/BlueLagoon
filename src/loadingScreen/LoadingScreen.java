package loadingScreen;

import main.Screen;
import processing.core.PApplet;

public class LoadingScreen extends Screen {

	int counter;

	public LoadingScreen() {
		counter = 0;
	}

	@Override
	public void draw(PApplet app) {
		app.background(0);
		app.fill(255);
		int rectBottom = app.height / 2 - 10;
		int x1 = (counter % 60) * (app.width / 60);
		int x2 = x1 - app.width;

		app.rect(x1, rectBottom, app.width / 2, 20);
		app.rect(x2, rectBottom, app.width / 2, 20);

		counter += 1;
	}
}
