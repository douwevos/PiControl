package net.github.douwevos.cnc.ui.widget;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;

import javax.swing.JPanel;

public class CncUIContext {

	private final Font titleFont = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
	private final Font basicFont = new Font(Font.SANS_SERIF, Font.PLAIN, 17);

	private final JPanel panel;
	
	private CncUIComponent focus;
	
	public CncUIContext(JPanel panel) {
		this.panel = panel;
	}
	
	
	public Graphics2D getGraphics() {
		return (Graphics2D) panel.getGraphics();
	}
	
	public Font getTitleFont() {
		return titleFont;
	}
	
	public Font getBasicFont() {
		return basicFont;
	}
	
	public void setFocus(CncUIComponent component) {
		this.focus = component;
	}
	
	public CncUIComponent getFocus() {
		return focus;
	}


	public void repaint() {
		panel.repaint();
	}

	
	ImageObserver getImageObserver() {
		return panel;
	}
	
}
