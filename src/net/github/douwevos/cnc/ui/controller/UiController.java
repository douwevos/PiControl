package net.github.douwevos.cnc.ui.controller;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public interface UiController {

	public void onMouseEvent(MouseEvent event, MouseEventType eventType);
	
	public void onKeyEvent(KeyEvent event, KeyEventType eventType);
	
}
