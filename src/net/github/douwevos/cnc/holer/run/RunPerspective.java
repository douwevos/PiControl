package net.github.douwevos.cnc.holer.run;

import net.github.douwevos.cnc.holer.CncPerspective;
import net.github.douwevos.cnc.holer.CncPerspectiveBoard;
import net.github.douwevos.cnc.holer.CncRuntimeContext;
import net.github.douwevos.cnc.ui.widget.CncUIButton;
import net.github.douwevos.cnc.ui.widget.CncUIButtons;
import net.github.douwevos.cnc.ui.widget.CncUIFrame;
import net.github.douwevos.cnc.ui.widget.CncUIPanel;

public class RunPerspective implements CncPerspective/*, Listener */{

	private CncPerspectiveBoard perspectiveBoard;

	CncUIPanel propertyPanel = new CncUIPanel();
	
	CncUIButton buttonRun;
	CncUIButton buttonTogglePause;
	CncUIButton buttonNext;
	
//	HolerModelRun currentRun;

	
	public RunPerspective(CncUIFrame frameMenu) {

		propertyPanel.add(frameMenu);
		
		CncUIFrame actionsMenu = new CncUIFrame("Actions");


		
		CncUIButtons buttons = new CncUIButtons();
		buttonRun = createRunButton();
		buttons.addButton(buttonRun);
		
		buttonTogglePause = createPauseButton();
		buttons.addButton(buttonTogglePause);

		buttonNext = createNextItemButton();
		buttons.addButton(buttonNext);

		
		actionsMenu.add(buttons);
		
		propertyPanel.add(actionsMenu);

	}

	@Override
	public void hide() {
		perspectiveBoard.setPropertiesPanel(null);
//		perspectiveBoard.getRuntimeContext().getCncProgramRunner().removeListener(this);
	}

	@Override
	public void show(CncPerspectiveBoard perspectiveBoard) {
		perspectiveBoard.setPropertiesPanel(propertyPanel);
		this.perspectiveBoard = perspectiveBoard;
//		perspectiveBoard.getRuntimeContext().getCncProgramRunner().addListener(this);
	}

	
	private CncUIButton createRunButton() {
		Runnable actionRun = () -> {
			if (perspectiveBoard == null) {
				return;
			}
			CncRuntimeContext runtimeContext = perspectiveBoard.getRuntimeContext();
//			CncProgramRunner cncProgramRunner = runtimeContext.getCncProgramRunner();
			
			
//			HolerModel holerModel = runtimeContext.getHolerModel();
//			cncProgramRunner.start(holerModel);
			
//			
			
		};
		return new CncUIButton("Start", actionRun);
	}


	private CncUIButton createNextItemButton() {
		Runnable actionRun = () -> {
			if (perspectiveBoard == null) {
				return;
			}
			CncRuntimeContext runtimeContext = perspectiveBoard.getRuntimeContext();
//			CncProgramRunner cncProgramRunner = runtimeContext.getCncProgramRunner();
//			
//			cncProgramRunner.nextItem();
			
			
		};
		return new CncUIButton("Next", actionRun);
	}
	
	private CncUIButton createPauseButton() {
		Runnable actionTogglePause = () -> {
			if (perspectiveBoard == null) {
				return;
			}
//			HolerModelRun run = currentRun;
//			if (run == null) {
//				return;
//			}
//			
//			CncRuntimeContext runtimeContext = perspectiveBoard.getRuntimeContext();
//			CncHeadService headService = runtimeContext.getHeadService();
//			CncActionQueue activeQueue = headService.getContext().getActiveQueue();
//			
//			CncActionQueue runActionQueue = run.getActionQueue();
//			
//			if (activeQueue==runActionQueue) {
//				activeQueue.branch(true);
//			} else {
//				activeQueue.returnFromBranch(false);
//			}
			
			
		};
		return new CncUIButton("Toggle Pause", actionTogglePause);
	}

	
//	@Override
//	public void onNewModelRun(HolerModelRun run) {
//		buttonRun.setEnabled(run==null);
//		buttonTogglePause.setEnabled(run!=null);
//		currentRun = run;
//	}
	
}
