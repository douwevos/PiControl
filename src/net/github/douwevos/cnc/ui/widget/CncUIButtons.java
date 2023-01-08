package net.github.douwevos.cnc.ui.widget;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import net.github.douwevos.cnc.ui.controller.KeyEventType;
import net.github.douwevos.cnc.ui.controller.MouseEventType;

public class CncUIButtons extends CncUIComponent {

	List<CncUIButton> buttons = new ArrayList<>();
	
	
	@Override
	protected void paint(Graphics2D gfx) {
		Boundaries boundaries = getBoundaries();
		if (boundaries == null) {
			return;
		}
		Graphics2D child = (Graphics2D) gfx.create();
//		child.translate(boundaries.x, boundaries.y);
//		child.setColor(Color.red);
		child.fillRect(0, 0, boundaries.width, boundaries.height);
		for(CncUIButton button : buttons) {
			Boundaries childBoundaries = button.getBoundaries();
			child.translate(childBoundaries.x, childBoundaries.y);
			button.paint(child);
			child.translate(-childBoundaries.x, -childBoundaries.y);
		}
		child.dispose();
	}
	
	
	public void addButton(CncUIButton button) {
		buttons.add(button);
		button.setContext(context);
	}
	
	@Override
	protected void setContext(CncUIContext context) {
		super.setContext(context);
		for(CncUIComponent child : buttons) {
			child.setContext(context);
		}
	}
	
	
	@Override
	public Boundaries updateBoundaries(Boundaries boundaries) {
		int x = 0;
		int y = 0;
		int nextY = 0;
		int lineHeight = 0;
		for(CncUIButton b : buttons) {
			Boundaries buttonBaseBounds = b.getBoundaries();
			Boundaries childBoundaries = new Boundaries(x, y, buttonBaseBounds.width, buttonBaseBounds.height);
			if (childBoundaries.x+childBoundaries.width > boundaries.width) {
				if (childBoundaries.x!=0) {
					y += lineHeight;
					x = 0;
					childBoundaries = new Boundaries(x, y, buttonBaseBounds.width, buttonBaseBounds.height);
				}
			}
			
			Boundaries updatedChildBounds = b.updateBoundaries(childBoundaries);
			x += updatedChildBounds.width;
			if (nextY<y+updatedChildBounds.height) {
				nextY = y+updatedChildBounds.height;
			}
		}
		
		this.boundaries = boundaries.withHeight(nextY);
		return this.boundaries;
	}
	
	@Override
	protected void onMouseEvent(MouseEvent e, MouseEventType type, int mx, int my) {
		boolean alwaysFire = type==MouseEventType.EXITED || type==MouseEventType.ENTERED || type==MouseEventType.MOVED || type==MouseEventType.DRAGGED;
		for(CncUIButton b : buttons) {
			Boundaries childBoundaries = b.getBoundaries();
			if (alwaysFire || childBoundaries.contains(mx, my)) {
				int px = mx - childBoundaries.x;
				int py = my - childBoundaries.y;
				b.onMouseEvent(e, type, px, py);
			}
		}
	}
	
	@Override
	protected void onKeyEvent(KeyEvent e, KeyEventType keyEventType) {
		
	}
	
}
