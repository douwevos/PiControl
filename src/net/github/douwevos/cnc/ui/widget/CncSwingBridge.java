package net.github.douwevos.cnc.ui.widget;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

import net.github.douwevos.cnc.ui.controller.KeyEventType;
import net.github.douwevos.cnc.ui.controller.MouseEventType;

public class CncSwingBridge extends JPanel implements ComponentListener, MouseListener, MouseMotionListener, KeyListener {

	private final CncUIContext context = new CncUIContext(this);
	
	private boolean isAdded;

	CncUIComponent component;
	
	public CncSwingBridge() {
		setFocusable(true);
	}
	
	@Override
	public void paint(Graphics g) {
		Graphics2D gfx = (Graphics2D) g;
		gfx.setColor(Color.darkGray);
		gfx.fillRect(0, 0, getWidth(), getHeight());
		if (component != null) {
			Graphics2D childGfx = (Graphics2D) gfx.create();
			Boundaries boundaries = component.getBoundaries();
			childGfx.translate(boundaries.x, boundaries.y);
			component.paint(childGfx);
			childGfx.dispose();
		}
	}
	
	public void set(CncUIComponent newComponent) {
		if (this.component == newComponent) {
			return;
		}
		if (component != null) {
			component.setContext(null);
		}
		component = newComponent;
		if (component!=null) {
			component.setContext(context);
		}
		updateBoundaries();
		context.repaint();
	}
	
	@Override
	public void addNotify() {
		super.addNotify();
		isAdded = true;
		addComponentListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
		updateBoundaries();
	}
	
	@Override
	public void removeNotify() {
		super.removeNotify();
		isAdded = false;
	}

	private void updateBoundaries() {
		if (!isAdded) {
			return;
		}
		int width = getWidth();
		if (component != null) {
			int currentHeight = component.getBoundaries().height;
			Boundaries b = new Boundaries(0, 0, width, currentHeight);
			Boundaries updatedBounds = component.updateBoundaries(b);
		}
	}

	@Override
	public void componentResized(ComponentEvent e) {
		updateBoundaries();
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		
	}

	@Override
	public void componentShown(ComponentEvent e) {
		
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		handleMouseEvent(e, MouseEventType.CLICKED);
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		grabFocus();
		handleMouseEvent(e, MouseEventType.PRESSED);
	}


	@Override
	public void mouseReleased(MouseEvent e) {
		handleMouseEvent(e, MouseEventType.RELEASED);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		handleMouseEvent(e, MouseEventType.ENTERED);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		handleMouseEvent(e, MouseEventType.EXITED);
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		handleMouseEvent(e, MouseEventType.MOVED);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		handleMouseEvent(e, MouseEventType.DRAGGED);
	}

	private void handleMouseEvent(MouseEvent e, MouseEventType type) {
		int x = e.getX();
		int y = e.getY();
		boolean alwaysFire = type==MouseEventType.EXITED || type==MouseEventType.ENTERED || type==MouseEventType.MOVED || type==MouseEventType.DRAGGED;

		if (component != null) {
			Boundaries boundaries = component.getBoundaries();
			if (alwaysFire || boundaries.contains(x, y)) {
				int mx = x - boundaries.x;
				int my = y - boundaries.y;
				component.onMouseEvent(e, type, mx, my);
			}
		}
	}

	
	@Override
	public void keyPressed(KeyEvent e) {
		handleKeyEvent(e, KeyEventType.PRESSED);
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		handleKeyEvent(e, KeyEventType.RELEASED);
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		handleKeyEvent(e, KeyEventType.TYPED);
	}

	private void handleKeyEvent(KeyEvent event, KeyEventType type) {
		CncUIComponent focus = context.getFocus();
		if (focus != null) {
			focus.onKeyEvent(event, type);
		}
	}


}
