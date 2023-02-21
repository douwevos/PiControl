package net.github.douwevos.cnc.ui.widget;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import net.github.douwevos.cnc.ui.controller.KeyEventType;
import net.github.douwevos.cnc.ui.controller.MouseEventType;

public class CncUIButton extends CncUIComponent {

	private final Integer preferredHeight;
	private final Runnable runnable;
	private final String text;
	private Boundaries boundaries;
	private Image icon;
	private Image cache;
	private boolean activated;
	private boolean enabled = true;
	private boolean highlighted;
	
	public CncUIButton(Image icon, Runnable runnable, Integer preferredHeight) {
		this.text = null;
		this.icon = icon;
		this.runnable = runnable;
		this.preferredHeight = preferredHeight;
		boundaries = new Boundaries(0, 0, 1, 1);
	}

	public CncUIButton(String text, Runnable runnable) {
		this.text = text;
		this.icon = null;
		this.runnable = runnable;
		this.preferredHeight = null;
		boundaries = new Boundaries(0, 0, 1, 1);
	}

	public void setActivated(boolean activated) {
		if (this.activated == activated) {
			return;
		}
		this.activated = activated;
		needsRepaint();
	}
	
	
	public void setEnabled(boolean enabled) {
		if (this.enabled == enabled) {
			return;
		}
		this.enabled = enabled;
		needsRepaint();
	}
	
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void paint(Graphics2D gfx) {
		if (boundaries==null || boundaries.width<=0 || boundaries.height<=0) {
			return;
		}
		if (activated && isEnabled()) {
			gfx.setColor(Color.darkGray);
		} else if (highlighted && isEnabled()) {
			gfx.setColor(Color.cyan.darker());
		} else {
			gfx.setColor(Color.black);
		}
		gfx.fillRect(0, 0, boundaries.width, boundaries.height);
		if (icon != null) {
			paintIcon(gfx);
		}
		if (text != null) {
			paintText(gfx);
		}
	}

	
	private void paintText(Graphics2D gfx) {
		gfx.setFont(this.context.getBasicFont());
		FontMetrics fontMetrics = gfx.getFontMetrics();
		int maxAscent = fontMetrics.getMaxAscent();
		gfx.setColor(isEnabled() ? Color.white : Color.gray);
		gfx.drawString(text, 2, 2+maxAscent);
		gfx.drawRect(0, 0, boundaries.width-1, boundaries.height-1);
		
	}

	private void paintIcon(Graphics2D gfx) {
		if ((cache==null || cache.getWidth(null)!=boundaries.width || cache.getHeight(null)!=boundaries.height)) {
			BufferedImage bufferedImage = new BufferedImage(boundaries.width, boundaries.height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D gfxIm = bufferedImage.createGraphics();
			int iconWidth = icon.getWidth(null);
			if (iconWidth == -1) {
				return;
			}
			int iconHeight = icon.getHeight(null);
			gfxIm.drawImage(icon, 0, 0, boundaries.width, boundaries.height,0,0,iconWidth, iconHeight, null);
			gfxIm.dispose();
			cache = bufferedImage;
		}
		gfx.drawImage(cache, 0, 0, null);
	}

	@Override
	public Boundaries getBoundaries() {
		return boundaries;
	}
	
	@Override
	public Boundaries updateBoundaries(Boundaries boundaries) {
		if (icon !=null) {
			int width = icon.getWidth(null);
			int height = icon.getHeight(null);
			if (preferredHeight!=null) {
				if (width<=0 || height<=0 ) {
					width = preferredHeight;
					height = preferredHeight;
				} else {
					width = (width*preferredHeight)/height;
					height = preferredHeight;
				}
			}
			this.boundaries = boundaries.withSize(width, height);
		}
		if (text != null) {
			Graphics2D graphics = context.getGraphics();
			Font titleFont = context.getBasicFont();
			graphics.setFont(titleFont);
			Rectangle2D stringBounds = graphics.getFontMetrics().getStringBounds(text, graphics);
			int width = (int) Math.ceil(stringBounds.getWidth());
			int height = (int) Math.ceil(stringBounds.getHeight());
			this.boundaries = boundaries.withSize(width+4, height+5);
		}
		return this.boundaries;
		
	}
	
	
	@Override
	protected void onMouseEvent(MouseEvent e, MouseEventType type, int mx, int my) {
		switch(type) {
			case CLICKED : runnable.run(); break;
			case MOVED : {
				boolean newHighlighted = (mx>0 && my>0 && mx<boundaries.width && my<boundaries.height);
				if (newHighlighted != highlighted) {
					highlighted = newHighlighted;
					needsRepaint();
				}
				break;
			}
			case ENTERED :  
			case EXITED : highlighted = false; needsRepaint(); break;
			default : break;
		}
	}
	
	
	@Override
	protected void onKeyEvent(KeyEvent e, KeyEventType keyEventType) {
	}
	
	
	@Override
	protected void setContext(CncUIContext context) {
		super.setContext(context);
		if (context!=null) {
			if (icon!=null) {
				icon.getWidth(context.getImageObserver());
			}
			
		}
	}
}
