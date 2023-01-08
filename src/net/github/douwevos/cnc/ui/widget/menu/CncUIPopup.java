package net.github.douwevos.cnc.ui.widget.menu;

import java.awt.Graphics;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

public class CncUIPopup extends JDialog {

	
	public CncUIPopup(Window window) {
		super(window);
		setUndecorated(true);
		
		this.addWindowFocusListener(new WindowFocusListener() {

            public void windowGainedFocus(WindowEvent e) {
            }

            public void windowLostFocus(WindowEvent e) {
                Window oppositeWindow = e.getOppositeWindow();
				if (oppositeWindow==null || SwingUtilities.isDescendingFrom(oppositeWindow, CncUIPopup.this)) {
                    return;
                }
                CncUIPopup.this.setVisible(false);
            }

        });
	}
	
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.drawLine(0, 0, 200, 200);
	}
	
	
}
