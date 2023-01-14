package net.github.douwevos.cnc.ui.editor;

import java.awt.Image;
import java.awt.Toolkit;

import net.github.douwevos.cnc.head.CncHeadService;
import net.github.douwevos.cnc.holer.CncPerspective;
import net.github.douwevos.cnc.holer.CncPerspectiveBoard;
import net.github.douwevos.cnc.holer.CncRuntimeContext;
import net.github.douwevos.cnc.holer.HolerModel;
import net.github.douwevos.cnc.holer.HolerModelRun;
import net.github.douwevos.cnc.ui.editor.rectangle.PolyLineCreator;
import net.github.douwevos.cnc.ui.widget.CncUIButton;
import net.github.douwevos.cnc.ui.widget.CncUIButtons;
import net.github.douwevos.cnc.ui.widget.CncUIComponent;
import net.github.douwevos.cnc.ui.widget.CncUIFrame;
import net.github.douwevos.cnc.ui.widget.CncUIPanel;

public class EditorPerspective implements CncPerspective {

	private static final int BUTTON_HEIGHT = 23;

	private CncPerspectiveBoard perspectiveBoard;
	
	EditorView editorView = new EditorView();

	SelectionPropertiesList selectionPropertiesList;
	
	CncUIPanel menuPanel = new CncUIPanel();

//	CncUIPropertyGroup locationProperties;
	
	
	
	public EditorPerspective(CncUIFrame frameMenu) {
		
		menuPanel.add(frameMenu);
		
		CncUIFrame frameToolbar = createToolbarFrame();
		menuPanel.add(frameToolbar);
		
		SelectionModel selectionModel = editorView.getSelectionModel();
		selectionPropertiesList = new SelectionPropertiesList(menuPanel, selectionModel);
	}
	
	
	private CncUIFrame createToolbarFrame() {
		CncUIFrame result = new CncUIFrame("Toolbar");
		CncUIButtons buttons = new CncUIButtons();
		
		buttons.addButton(new CncUIButton("Add PolyLine", () -> {
			editorView.setCreator(new PolyLineCreator());
		}));

		
		result.add(buttons);
		
		return result;
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
		perspectiveBoard.setContentPanel(editorView);
//		perspectiveBoard.setPropertiesPanel(propertyPanel);
		this.perspectiveBoard = perspectiveBoard;
		selectionPropertiesList.setPerspectiveBoard(perspectiveBoard);
		
		CncRuntimeContext runtimeContext = perspectiveBoard.getRuntimeContext();
		HolerModel holerModel = runtimeContext.getHolerModel();
		
		editorView.setModel(runtimeContext.getEditableModel());
//		holerDesignView.setHolerModel(holerModel);
		
	}

	@Override
	public void hide() {
//		perspectiveBoard.setPropertiesPanel(null);
		perspectiveBoard.setContentPanel(null);
		selectionPropertiesList.setPerspectiveBoard(null);
//		holerDesignView.setHolerModel(null);
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
//		holerDesignView.startNewPolyLine();
	}


}
