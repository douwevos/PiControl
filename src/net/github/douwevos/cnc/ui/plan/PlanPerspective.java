package net.github.douwevos.cnc.ui.plan;

import net.github.douwevos.cnc.holer.CncPerspective;
import net.github.douwevos.cnc.holer.CncPerspectiveBoard;
import net.github.douwevos.cnc.holer.CncRuntimeContext;
import net.github.douwevos.cnc.ui.widget.CncUIFrame;
import net.github.douwevos.cnc.ui.widget.CncUIPanel;

public class PlanPerspective implements CncPerspective {

	CncPerspectiveBoard perspectiveBoard;
	CncUIPanel menuPanel = new CncUIPanel();

	PlanView planView = new PlanView();
	
	public PlanPerspective(CncUIFrame frameMenu) {
		menuPanel.add(frameMenu);
	}


	@Override
	public void show(CncPerspectiveBoard perspectiveBoard) {
		this.perspectiveBoard = perspectiveBoard;
		
		CncRuntimeContext runtimeContext = perspectiveBoard.getRuntimeContext();
		PlanViewModel planViewModel = new PlanViewModel(runtimeContext.getEditableModel()); 
		planView.setPlanViewModel(planViewModel);

		perspectiveBoard.setContentPanel(planView);
		perspectiveBoard.setPropertiesPanel(menuPanel);
}

	@Override
	public void hide() {
		perspectiveBoard.setContentPanel(null);
		perspectiveBoard.setPropertiesPanel(null);
	}

}
