package net.github.douwevos.cnc.ui.widget;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.util.ArrayList;
import java.util.List;

import net.github.douwevos.cnc.ui.controller.KeyEventType;
import net.github.douwevos.cnc.ui.controller.MouseEventType;

public class CncUIFrame extends CncUIComponent {

	private final String title;
	private List<CncUIComponent> children = new ArrayList<>();

	
	public CncUIFrame(String title) {
		this.title = title;
	}
	
	public void add(CncUIComponent component) {
		children.add(component);
		component.setContext(context);
	}

	@Override
	protected void setContext(CncUIContext context) {
		super.setContext(context);
		for(CncUIComponent child : children) {
			child.setContext(context);;
		}
	}
	
	
	@Override
	protected void paint(Graphics2D gfx) {
		gfx.setColor(Color.lightGray);
		FontMetrics fontMetrics = gfx.getFontMetrics();
		LineMetrics lineMetrics = fontMetrics.getLineMetrics(title, gfx);
		gfx.fillRect(0, 0, boundaries.width, (int) Math.ceil(lineMetrics.getHeight()));
		gfx.setColor(Color.DARK_GRAY);
		Font titleFont = context.getTitleFont();
		gfx.setFont(titleFont);
		int maxAscent = fontMetrics.getMaxAscent();
		gfx.drawString(title, 0, maxAscent);
		
		for(CncUIComponent component : children) {
			if (!component.visible) {
				continue;
			}
			Boundaries childBounds = component.getBoundaries();
			Graphics2D child = (Graphics2D) gfx.create();
			child.translate(0, childBounds.y);
			component.paint(child);
			child.dispose();
		}
	}

	@Override
	public Boundaries updateBoundaries(Boundaries boundaries) {
		Graphics2D graphics = context.getGraphics();
		FontRenderContext fontRenderContext = graphics.getFontRenderContext();
		Font titleFont = context.getTitleFont();
		LineMetrics lineMetrics = titleFont.getLineMetrics(title, fontRenderContext);
		int width = boundaries.width;
		int childY = 0;
		childY += (int) Math.ceil(lineMetrics.getHeight());
		for(CncUIComponent c : children) {
			if (!c.visible) {
				continue;
			}
			Boundaries b = new Boundaries(0, childY, width, c.getPreferedHeight());
			Boundaries childBoundaries = c.updateBoundaries(b);
			childY += childBoundaries.height;
		}
		
		this.boundaries = boundaries.withHeight(childY);
		return this.boundaries;
	}
	
	
	@Override
	protected void onMouseEvent(MouseEvent e, MouseEventType type, int mx, int my) {
		boolean alwaysFire = type==MouseEventType.EXITED || type==MouseEventType.ENTERED || type==MouseEventType.MOVED || type==MouseEventType.DRAGGED;
		for(CncUIComponent component : children) {
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
	protected void onKeyEvent(KeyEvent e, KeyEventType keyEventType) {
		for(CncUIComponent component : children) {
			if (!component.visible) {
				continue;
			}
			component.onKeyEvent(e, keyEventType);
		}
	}

}
