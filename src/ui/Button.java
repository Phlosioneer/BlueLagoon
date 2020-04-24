package ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;


public class Button {
	private int x, y, w, h;
	private Collection<ButtonClickAction> clickActions;
	private Collection<ButtonHoverEnterAction> hoverEnterActions;
	private Collection<ButtonHoverExitAction> hoverExitActions;
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getW() {
		return w;
	}
	/**
	 * Sets the width of this button. Ignores sign.
	 * @param w
	 */
	public void setW(int w) {
		this.w = Math.abs(w);
	}

	public int getH() {
		return h;
	}
	/**
	 * Sets the height of this button. Ignores sign.
	 * @param h
	 */
	public void setH(int h) {
		this.h = Math.abs(h);
	}

	/**
	 * Creates a button with the specified position and size. Treats negative size values as positive.
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	public Button(int x, int y, int w, int h) {
		this.x = Math.min(x, x + w);
		this.y = Math.min(y, y + h);
		this.w = Math.abs(w);
		this.h = Math.abs(h);
		this.clickActions = new ArrayList<>();
		this.hoverEnterActions = new ArrayList<>();
		this.hoverExitActions = new ArrayList<>();
	}
	
	/**
	 * Runs all click handlers.
	 */
	public void click() {
		throw new RuntimeException("TODO: implement click handler");
	}
	
	/**
	 * Runs all hover enter handlers.
	 */
	public void hoverEnter() {
		throw new RuntimeException("TODO: implement hover enter handler");
	}
	
	/**
	 * Runs all hover exit handlers.
	 */
	public void hoverExit() {
		throw new RuntimeException("TODO: implement hover exit handler");
	}
	
	/**
	 * Registers a handler to be called whenever the mouse clicks this button.
	 * @param clickAction
	 */
	public void onClick(ButtonClickAction clickAction) {
		this.clickActions.add(clickAction);
	}
	
	/**
	 * Registers a handler to be called whenever the mouse hovers over this button.
	 * @param hoverAction
	 */
	public void onHoverEnter(ButtonHoverEnterAction hoverEnterAction) {
		this.hoverEnterActions.add(hoverEnterAction);
	}
	
	/**
	 * Registers a handler to be called whenever the mouse leaves the area of this button.
	 * @param hoverExitAction
	 */
	public void onHoverExit(ButtonHoverExitAction hoverExitAction) {
		this.hoverExitActions.add(hoverExitAction);
	}

	/*
	 * These interfaces are all the same but with different names. Right now they're identical but later we might want to provide
	 * different information in each one.
	 */
	public interface ButtonClickAction {
		void perform();
	}
	
	public interface ButtonHoverEnterAction {
		void perform();
	}
	
	public interface ButtonHoverExitAction {
		void perform();
	}
}
