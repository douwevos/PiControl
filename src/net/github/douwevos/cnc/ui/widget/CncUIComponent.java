package net.github.douwevos.cnc.ui.widget;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import net.github.douwevos.cnc.ui.controller.KeyEventType;
import net.github.douwevos.cnc.ui.controller.MouseEventType;

public abstract class CncUIComponent implements WithBoundaries {

	protected boolean visible = true;
	protected CncUIContext context;
	protected Boundaries boundaries = new Boundaries(0, 0, 0, 0);
	
	protected abstract void paint(Graphics2D gfx);
	
	protected void setContext(CncUIContext context) {
		this.context = context;
	}
	
	public int getPreferedHeight() {
		return 32;
	}
	
	public boolean setVisible(boolean visible) {
		if (this.visible == visible) {
			return false;
		}
		this.visible = visible;
		if (context!=null) {
			context.repaint();
		}
		return true;
	}
	
	@Override
	public Boundaries getBoundaries() {
		return boundaries;
	}
	
	
	@Override
	public Boundaries updateBoundaries(Boundaries boundaries) {
		this.boundaries = boundaries;
		return boundaries;
	}


	protected abstract void onMouseEvent(MouseEvent e, MouseEventType type, int mx, int my);

	
	protected abstract void onKeyEvent(KeyEvent e, KeyEventType keyEventType);

	
	public void needsRepaint() {
		if (context!=null) {
			context.repaint();
		}
	}
}
