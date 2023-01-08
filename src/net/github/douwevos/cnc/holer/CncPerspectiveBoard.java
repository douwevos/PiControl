package net.github.douwevos.cnc.holer;

import javax.swing.JPanel;

import net.github.douwevos.cnc.ui.widget.CncUIPanel;

public interface CncPerspectiveBoard {

	
	CncRuntimeContext getRuntimeContext();
	
	void setPropertiesPanel(CncUIPanel panel);

	void setContentPanel(JPanel panel);

	
	public void selectPerspective(CncPerspective perspective);

	void repaintPropertiesPanel();
}
