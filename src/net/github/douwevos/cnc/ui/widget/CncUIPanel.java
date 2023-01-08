package net.github.douwevos.cnc.ui.widget;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import net.github.douwevos.cnc.ui.controller.KeyEventType;
import net.github.douwevos.cnc.ui.controller.MouseEventType;

public class CncUIPanel extends CncUIComponent {

	List<CncUIComponent> components = new ArrayList<>();
	
	public CncUIPanel() {
	}

	
	@Override
	protected void paint(Graphics2D gfx) {
		gfx.setColor(Color.darkGray);
		
		gfx.fillRect(0, 0, boundaries.width, boundaries.height);
		for(CncUIComponent component : components) {
			if (!component.visible) {
				continue;
			}
			Graphics2D childGfx = (Graphics2D) gfx.create();
			Boundaries boundaries = component.getBoundaries();
			childGfx.translate(boundaries.x, boundaries.y);
			component.paint(childGfx);
			childGfx.dispose();
		}
	}
	
	public void add(CncUIComponent component) {
		if (components.contains(component)) {
			return;
		}
		components.add(component);
		component.setContext(context);
	}

	public void remove(CncUIComponent component) {
		if (!components.contains(component)) {
			return;
		}
		components.remove(component);
		component.setContext(null);
	}

	@Override
	public Boundaries updateBoundaries(Boundaries boundaries) {
		int width = boundaries.width;
		int y = 0;
		for(CncUIComponent c : components) {
			if (!c.visible) {
				continue;
			}
			int currentHeight = c.getBoundaries().height;
			Boundaries b = new Boundaries(0, y, width, currentHeight);
			Boundaries updatedBounds = c.updateBoundaries(b);
			y += updatedBounds.height;
		}
		this.boundaries = boundaries.withHeight(y);
		return boundaries;
	}
	
	@Override
	protected void onKeyEvent(KeyEvent e, KeyEventType keyEventType) {
		for(CncUIComponent component : components) {
			if (!component.visible) {
				continue;
			}
			component.onKeyEvent(e, keyEventType);
		}
	}
	
	
	@Override
	protected void onMouseEvent(MouseEvent e, MouseEventType type, int mx, int my) {
		boolean alwaysFire = type==MouseEventType.EXITED || type==MouseEventType.ENTERED || type==MouseEventType.MOVED || type==MouseEventType.DRAGGED;
		for(CncUIComponent component : components) {
			if (!component.visible) {
				continue;
			}
			Boundaries boundaries = component.getBoundaries();
			if (alwaysFire || boundaries.contains(mx, my)) {
				int cmx = mx - boundaries.x;
				int cmy = my - boundaries.y;
				component.onMouseEvent(e, type, cmx, cmy);
			}
		}
	}

	@Override
	protected void setContext(CncUIContext context) {
		super.setContext(context);
		for(CncUIComponent component : components) {
			component.setContext(context);
		}
	}
	
}
