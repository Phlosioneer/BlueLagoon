package main;

import processing.core.PApplet;

public abstract class Screen {

	public abstract void draw(PApplet app);

	public void mousePressed(PApplet app) {};

	public void keyPressed(PApplet app) {};
}
