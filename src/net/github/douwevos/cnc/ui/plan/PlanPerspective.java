package net.github.douwevos.cnc.ui.plan;

import net.github.douwevos.cnc.holer.CncPerspective;
import net.github.douwevos.cnc.holer.CncPerspectiveBoard;
import net.github.douwevos.cnc.holer.CncProgramRunner;
import net.github.douwevos.cnc.holer.CncRuntimeContext;
import net.github.douwevos.cnc.holer.NewCncProgramRunner;
import net.github.douwevos.cnc.model.EditableModel;
import net.github.douwevos.cnc.model.value.Model;
import net.github.douwevos.cnc.ui.editor.rectangle.PolyLineCreator;
import net.github.douwevos.cnc.ui.widget.CncUIButton;
import net.github.douwevos.cnc.ui.widget.CncUIButtons;
import net.github.douwevos.cnc.ui.widget.CncUIFrame;
import net.github.douwevos.cnc.ui.widget.CncUIPanel;

public class PlanPerspective implements CncPerspective {

	CncPerspectiveBoard perspectiveBoard;
	CncUIPanel menuPanel = new CncUIPanel();

	PlanView planView = new PlanView();
	
	public PlanPerspective(CncUIFrame frameMenu) {
		menuPanel.add(frameMenu);

		CncUIFrame frameToolbar = createToolbarFrame();
		menuPanel.add(frameToolbar);

	}

	private CncUIFrame createToolbarFrame() {
		CncUIFrame result = new CncUIFrame("Toolbar");
		CncUIButtons buttons = new CncUIButtons();
		
		buttons.addButton(new CncUIButton("Run", () -> {
			if (perspectiveBoard == null) {
				return;
			}
			CncRuntimeContext runtimeContext = perspectiveBoard.getRuntimeContext();
			EditableModel editableModel = runtimeContext.getEditableModel();
			Model model = editableModel.snapshot();
			
			NewCncProgramRunner cncProgramRunner = runtimeContext.getNewCncProgramRunner();
			cncProgramRunner.start(model);
		}));

		
		result.add(buttons);
		
		return result;
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
