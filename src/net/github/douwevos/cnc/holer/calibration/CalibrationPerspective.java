package net.github.douwevos.cnc.holer.calibration;

import net.github.douwevos.cnc.head.CncHeadService;
import net.github.douwevos.cnc.holer.CncPerspective;
import net.github.douwevos.cnc.holer.CncPerspectiveBoard;
import net.github.douwevos.cnc.ui.widget.CncUIButton;
import net.github.douwevos.cnc.ui.widget.CncUIButtons;
import net.github.douwevos.cnc.ui.widget.CncUIFrame;
import net.github.douwevos.cnc.ui.widget.CncUIPanel;

public class CalibrationPerspective implements CncPerspective {

	private CncPerspectiveBoard perspectiveBoard;
	CalibrationBoardPanel calibrationControlPanel;
	CncUIPanel propertyPanel = new CncUIPanel();

	CalibrationContext calibrationContext;
	CncUIButton buttonLock;
	CncUIButton buttonUnlock;
	
	
	public CalibrationPerspective(CncUIFrame frameMenu) {

		propertyPanel.add(frameMenu);

		calibrationControlPanel = new CalibrationBoardPanel();

		
		CncUIFrame actionsMenu = new CncUIFrame("Actions");
		CncUIButtons buttons = new CncUIButtons();
		
		buttonLock = new CncUIButton("Lock", () -> {
			calibrationContext.lock();
			buttonLock.needsRepaint();
		}) {
			@Override
			public boolean isEnabled() {
				return calibrationContext.getActionQueue()==null;
			}
		};
		buttons.addButton(buttonLock);

		buttonUnlock = new CncUIButton("unLock", () -> {
			calibrationContext.unlock();
			buttonLock.needsRepaint();
		}) {
			@Override
			public boolean isEnabled() {
				return calibrationContext.getActionQueue()!=null;
			}
		};
		buttons.addButton(buttonUnlock);

		actionsMenu.add(buttons);
		propertyPanel.add(actionsMenu);
	}
	

	@Override
	public void show(CncPerspectiveBoard perspectiveBoard) {
		perspectiveBoard.setPropertiesPanel(propertyPanel);
		perspectiveBoard.setContentPanel(calibrationControlPanel);
		CncHeadService cncHeadService = perspectiveBoard.getRuntimeContext().getHeadService();
		calibrationControlPanel.setCncHeadService(cncHeadService);
		CncHeadService headService = perspectiveBoard.getRuntimeContext().getHeadService();
		calibrationContext = new CalibrationContext(headService);
		calibrationControlPanel.setCalibrationContext(calibrationContext);
	}

	@Override
	public void hide() {
		calibrationContext.dispose();
		calibrationContext = null;
	}

}
