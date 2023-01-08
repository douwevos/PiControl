package net.github.douwevos.cnc.ui.widget;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.Objects;

import net.github.douwevos.cnc.ui.controller.KeyEventType;
import net.github.douwevos.cnc.ui.controller.MouseEventType;
import net.github.douwevos.cnc.ui.widget.CncUIPropertyGroup.PropertyObserver;

public class CncUIProperty extends CncUIComponent {

	public final String key;
	public String value;
	public String valueInput;
	private Integer useKeyWidth;
	private PropertyObserver observer;
	
	private int cursor;
	
	public CncUIProperty(String key, String value, PropertyObserver observer) {
		this.key = key;
		this.value = value;
		this.observer = observer;
	}

	protected CncUIProperty(String key, String value) {
		this.key = key;
		this.value = value;
	}

	
	public void setObserver(PropertyObserver observer) {
		this.observer = observer;
	}
	
	public void setValue(String value) {
		if (Objects.equals(value, this.value)) {
			return;
		}
		this.value = value;
		if (value!=null && cursor>value.length()) {
			cursor = value.length();
		}
		needsRepaint();
	}
	
	public String getValue() {
		return value;
	}

	
	public int getOwnKeyWidth() {
		if (context == null) {
			return 0;
		}
		Font basicFont = context.getBasicFont();
		Graphics2D graphics = context.getGraphics();
		FontRenderContext renderContext = graphics.getFontRenderContext();
		Rectangle2D stringBounds = basicFont.getStringBounds(key, renderContext);
		return (int) Math.ceil(stringBounds.getWidth());
	}
	
	
	public void useKeyWidth(int keyWidth) {
		useKeyWidth = keyWidth;
	}

	
	@Override
	protected void paint(Graphics2D gfx) {
		gfx.setColor(Color.lightGray);
		gfx.setFont(context.getBasicFont());
		FontMetrics fontMetrics = gfx.getFontMetrics();
		int maxAscent = fontMetrics.getMaxAscent();
		gfx.drawString(key, 0, maxAscent);

		gfx.setColor(Color.WHITE);

		int valX;
		if (useKeyWidth == null) {
			Rectangle2D stringBounds = fontMetrics.getStringBounds(key, gfx);
			valX = (int) Math.ceil(stringBounds.getWidth());
		} else {
			valX = useKeyWidth;
		}
		
		
		if (context.getFocus() == this) {
			gfx.drawString(valueInput, valX, maxAscent);
			String substring = valueInput.substring(0, cursor);
			Rectangle2D stringBounds = fontMetrics.getStringBounds(substring, gfx);
			int height = getBoundaries().height;
			int xCursor = valX+(int) stringBounds.getWidth();
			gfx.drawLine(xCursor, 0, xCursor, height);
		} else {
			gfx.drawString(value, valX, maxAscent);
		}
	
	}

	@Override
	protected void onMouseEvent(MouseEvent e, MouseEventType type, int mx, int my) {
		switch(type) {
			case PRESSED : {
				context.setFocus(this);
				context.repaint();
				valueInput = value;
			} break;
			default:
				break;
		}
	}

	@Override
	protected void onKeyEvent(KeyEvent e, KeyEventType keyEventType) {
		if (keyEventType==KeyEventType.TYPED) {
			char keyChar = e.getKeyChar();
			if (Character.isLetterOrDigit(keyChar)) {
				valueInput = valueInput.substring(0, cursor) + keyChar + valueInput.substring(cursor);
				cursor++;
				context.repaint();
			}
		} else if (keyEventType==KeyEventType.PRESSED) {
			switch(e.getKeyCode()) {
				case KeyEvent.VK_DELETE : {
					if (valueInput.length()>cursor) {
						valueInput = valueInput.substring(0, cursor) + valueInput.substring(cursor+1);
						context.repaint();
					} 
				} break;
				case KeyEvent.VK_BACK_SPACE : {
					if (cursor>0) {
						cursor--;
						valueInput = valueInput.substring(0, cursor) + valueInput.substring(cursor+1);
						context.repaint();
					} 
				} break;
				case KeyEvent.VK_HOME : {
					if (cursor!=0) {
						cursor=0;
						context.repaint();
					}
				} break;
				case KeyEvent.VK_END : {
					if (cursor!=valueInput.length()) {
						cursor=valueInput.length();
						context.repaint();
					}
				} break;
				case KeyEvent.VK_RIGHT : {
					if (cursor<valueInput.length()) {
						cursor++;
						context.repaint();
					}
				} break;
				case KeyEvent.VK_LEFT : {
					if (cursor>0) {
						cursor--;
						context.repaint();
					}
				} break;
				case KeyEvent.VK_ESCAPE : {
					context.setFocus(null);
					context.repaint();
				} break;
				case KeyEvent.VK_ENTER : {
					if (observer.valueChanged(valueInput)) {
						context.setFocus(null);
						value = valueInput;
						context.repaint();
					}
				}
			}
		}
	}
	
	
	@Override
	public Boundaries updateBoundaries(Boundaries boundaries) {
		Font basicFont = context.getBasicFont();
		this.boundaries = boundaries.withHeight(basicFont.getSize());
		return this.boundaries;
	}


	
	
}
