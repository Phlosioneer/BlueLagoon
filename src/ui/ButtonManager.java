package ui;

import java.util.HashMap;
import java.util.Map;

import processing.event.MouseEvent;

public class ButtonManager {

	private int prevMouseX, prevMouseY;
	
	private Map<String, Button> buttons;
	public ButtonManager() {
		this.buttons = new HashMap<>();
		this.prevMouseX = 0;
		this.prevMouseY = 0;
	}
	
	void addButton(Button b, String name) {
		this.buttons.put(name, b);
	}
	
	//Not entirely sure what MouseEvent.getX() and getY() stand for in a MOVE event. For now, I assume it means
	// "Where the mouse ended up after the move"
	void handleMouseEvent(MouseEvent e) {
		// Unhover all buttons that _were_ previously being pointed at, but are currently not.
		// If for whatever reason a button misses an unhover event, hovering the button again and taking the mouse away 
		// will trigger one again, "unsticking" any highlight the button might have.
		if (e.getAction() == MouseEvent.MOVE) {
			for (Button b : this.buttons.values()) {
				if (!b.containsPoint(e.getX(), e.getY()) 
						&& b.containsPoint(this.prevMouseX, this.prevMouseY)) {
					b.hoverExit();
				}
			}			
		}
		
		this.prevMouseX = e.getX();
		this.prevMouseY = e.getY();
		
		// Find the first button that contains the mouse and forward the appropriate event to it.
		// Do not forward the same event to more than one button.
		// Only one button should be clickable if more than one overlap.
		this.buttons
			.values()
			.stream()
			.filter((Button b) -> b.containsPoint(e.getX(), e.getY()))
			.findFirst() //We don't want to run handlers on every button that matches this position if they overlap.
			.ifPresent((Button b) -> {
				switch (e.getAction()) {
				case MouseEvent.CLICK: {
					b.click();
				} break;
				case MouseEvent.MOVE: {
					b.hoverEnter();
				} break;
				default:
					break;
				}
			});
	}
}
