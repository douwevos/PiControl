package net.github.douwevos.cnc.holer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JPanel;

import net.github.douwevos.cnc.ui.widget.CncSwingBridge;
import net.github.douwevos.cnc.ui.widget.CncUIPanel;
import net.github.douwevos.cnc.ui.widget.CncUIPropertyGroup;

public class CncMainPanel extends JPanel implements CncPerspectiveBoard {

	CncPerspective perspective;
	
	CncRuntimeContext runtimeContext;
	
	
	CncSwingBridge westPanel = new CncSwingBridge();
	
	JPanel contentPanel = new JPanel();
	
	CncUIPropertyGroup locationProperties;
	
	public CncMainPanel(CncRuntimeContext runtimeContext) {
		this.runtimeContext = runtimeContext;
				
		BorderLayout borderLayout = new BorderLayout();
		setLayout(borderLayout);
		
		
		westPanel.setPreferredSize(new Dimension(350, 350));
		westPanel.setSize(new Dimension(350, 350));
		
		add(westPanel, BorderLayout.WEST);
		
		
		contentPanel.setLayout(new BorderLayout());
		add(contentPanel, BorderLayout.CENTER);
	}

	
	public void selectPerspective(CncPerspective perspective) {
		if (this.perspective == perspective) {
			return;
		}
		if (this.perspective != null) {
			this.perspective.hide();
		}
		 
		this.perspective = perspective;
		if (perspective != null) {
			perspective.show(this);
		}
		invalidate();
		revalidate();
		repaint();
	}
	
	
	@Override
	public void setPropertiesPanel(CncUIPanel panel) {
		westPanel.set(panel);

		getRootPane().invalidate();
		repaintPropertiesPanel();
	}

	@Override
	public void setContentPanel(JPanel panel) {
		while(contentPanel.getComponentCount()>0) {
			Component component = contentPanel.getComponent(0);
			if (panel == component) {
				return;
			}
			contentPanel.remove(0);
		}
		if (panel != null) {
			contentPanel.add(panel, BorderLayout.CENTER);
		}
		getRootPane().invalidate();
		repaintPropertiesPanel();
	}
	
	@Override
	public void repaintPropertiesPanel() {
		westPanel.componentResized(null);
		westPanel.repaint();
		contentPanel.repaint();
	}

	@Override
	public CncRuntimeContext getRuntimeContext() {
		return runtimeContext;
	}
	
}
