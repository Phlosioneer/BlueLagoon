package ui;

import java.util.HashMap;
import java.util.Map;

import processing.event.MouseEvent;

public class ButtonManager {

	private Map<String, Button> buttons;
	public ButtonManager() {
		this.buttons = new HashMap<>();
	}
	
	void addButton(Button b, String name) {
		this.buttons.put(name, b);
	}
	
	void handleMouseEvent(MouseEvent e) {
		throw new RuntimeException("TODO: implement event handler for button manager");
		//here we check if the event is relevant to us, and if so, applies it to the relevant button we hold.
	}
}
