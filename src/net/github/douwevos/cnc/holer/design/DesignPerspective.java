package net.github.douwevos.cnc.holer.design;

import java.awt.Image;
import java.awt.Toolkit;

import net.github.douwevos.cnc.head.CncHeadService;
import net.github.douwevos.cnc.holer.CncPerspective;
import net.github.douwevos.cnc.holer.CncPerspectiveBoard;
import net.github.douwevos.cnc.holer.CncRuntimeContext;
import net.github.douwevos.cnc.holer.HolerModel;
import net.github.douwevos.cnc.holer.HolerModelRun;
import net.github.douwevos.cnc.holer.ItemGrabInfo;
import net.github.douwevos.cnc.holer.feature.CurvableFeature;
import net.github.douwevos.cnc.holer.feature.LocationFeature;
import net.github.douwevos.cnc.ui.widget.CncUIButton;
import net.github.douwevos.cnc.ui.widget.CncUIFrame;
import net.github.douwevos.cnc.ui.widget.CncUIPanel;
import net.github.douwevos.cnc.ui.widget.CncUIProperty;
import net.github.douwevos.cnc.ui.widget.CncUIPropertyGroup;

public class DesignPerspective implements CncPerspective {

	private static final int BUTTON_HEIGHT = 23;

	private CncPerspectiveBoard perspectiveBoard;
	
	DesignView holerDesignView = new DesignView();

	
	CncUIPanel propertyPanel = new CncUIPanel();

	CncUIPropertyGroup locationProperties;
	
	
	public DesignPerspective(CncUIFrame frameMenu2) {
//		CncUIFrame frameMenu = new CncUIFrame("menu");
		propertyPanel.add(frameMenu2);		

//		CncUIButtons buttons = new CncUIButtons();
//		
//		buttons.addButton(createRunButton());
//		buttons.addButton(createSelectButton());
//		
//		buttons.addButton(new CncUIButton("testje", () -> {
//			CalibrationPerspective calibrationPerspective = new CalibrationPerspective();
//			perspectiveBoard.selectPerspective(calibrationPerspective);
//		}));
//
//		buttons.addButton(new CncUIButton("Run", () -> {
//			RunPerspective calibrationPerspective = new RunPerspective();
//			perspectiveBoard.selectPerspective(calibrationPerspective);
//		}));

		
//		propertyPanel.add(buttons);

		CncUIFrame frameSelection = new CncUIFrame("selection");
		propertyPanel.add(frameSelection);

		locationProperties = new CncUIPropertyGroup();
		
		CncUIProperty propX = new CncUIProperty("X:", "", (nv) -> {
			try {
				long newX = Long.parseLong(nv);
				SelectionModel selectionModel = holerDesignView.getSelectionModel();
				for(ItemGrabInfo grabInfo : selectionModel.selections()) {
					if (grabInfo instanceof LocationFeature) {
						LocationFeature lf = (LocationFeature) grabInfo;
						lf.setX(newX);
					}
				}
				holerDesignView.getParent().repaint();
				return true;
			} catch(NumberFormatException e) {
				return false;
			}
		});
		propertyPanel.add(propX);
		locationProperties.addProperty(propX);

		CncUIProperty propY = new CncUIProperty("Y:", "", (nv) -> {
			try {
				long newY = Long.parseLong(nv);
				SelectionModel selectionModel = holerDesignView.getSelectionModel();
				for(ItemGrabInfo grabInfo : selectionModel.selections()) {
					if (grabInfo instanceof LocationFeature) {
						LocationFeature lf = (LocationFeature) grabInfo;
						lf.setY(newY);
					}
				}
				holerDesignView.getParent().repaint();
				return true;
			} catch(NumberFormatException e) {
				return false;
			}
		});
		propertyPanel.add(propY);
		locationProperties.addProperty(propY);


		CncUIProperty propCurved = new CncUIProperty("curved:", "", (nv) -> {
			try {
				boolean newCurved = Boolean.parseBoolean(nv);
				SelectionModel selectionModel = holerDesignView.getSelectionModel();
				for(ItemGrabInfo grabInfo : selectionModel.selections()) {
					if (grabInfo instanceof CurvableFeature) {
						CurvableFeature lf = (CurvableFeature) grabInfo;
						lf.setCurved(newCurved);
					}
				}
				holerDesignView.getParent().repaint();
				return true;
			} catch(NumberFormatException e) {
				return false;
			}
		});
		propertyPanel.add(propCurved);
		locationProperties.addProperty(propCurved);

		

		SelectionModel selectionModel = holerDesignView.getSelectionModel();
		selectionModel.addListener(() -> {
			for(ItemGrabInfo grabInfo : selectionModel.selections()) {
				if (grabInfo instanceof LocationFeature) {
					LocationFeature lf = (LocationFeature) grabInfo;
					propX.setValue(""+lf.getX());
					propY.setValue(""+lf.getY());
				}
				if (grabInfo instanceof CurvableFeature) {
					CurvableFeature lf = (CurvableFeature) grabInfo;
					propCurved.setValue(""+lf.isCurved());
				}
			}
			perspectiveBoard.repaintPropertiesPanel();
			
		});
		
	}
	
	
	private CncUIButton createRunButton() {
		Runnable actionRun = () -> {
			if (perspectiveBoard == null) {
				return;
			}
			CncRuntimeContext runtimeContext = perspectiveBoard.getRuntimeContext();
			HolerModel holerModel = runtimeContext.getHolerModel();
			CncHeadService cncHeadService = runtimeContext.getHeadService();
			HolerModelRun modelRun = new HolerModelRun(runtimeContext.getConfiguration(), holerModel, cncHeadService);
			modelRun.run();
			
		};
		return new CncUIButton(loadImage("./src/run.png"), actionRun, BUTTON_HEIGHT);
	}

	private CncUIButton createSelectButton() {
		Runnable actionRun = () -> {
//			setSelectMode();
			startPolyLine();
		};
		return new CncUIButton(loadImage("./src/select.png"), actionRun, BUTTON_HEIGHT);
	}

	
	@Override
	public void show(CncPerspectiveBoard perspectiveBoard) {
		perspectiveBoard.setContentPanel(holerDesignView);
		perspectiveBoard.setPropertiesPanel(propertyPanel);
		this.perspectiveBoard = perspectiveBoard;
		
		CncRuntimeContext runtimeContext = perspectiveBoard.getRuntimeContext();
		HolerModel holerModel = runtimeContext.getHolerModel();
		holerDesignView.setHolerModel(holerModel);
		
	}

	@Override
	public void hide() {
		perspectiveBoard.setPropertiesPanel(null);
		perspectiveBoard.setContentPanel(null);
		holerDesignView.setHolerModel(null);
	}

	public Image loadImage(String name) {
		try {
			Image image = Toolkit.getDefaultToolkit().createImage(name);
//			image.getWidth(this);
			return image;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	
	private void startPolyLine() {
		holerDesignView.startNewPolyLine();
	}


}
