package net.github.douwevos.cnc.ui.editor;

import java.awt.Image;
import java.awt.Toolkit;

import net.github.douwevos.cnc.holer.CncPerspective;
import net.github.douwevos.cnc.holer.CncPerspectiveBoard;
import net.github.douwevos.cnc.holer.CncRuntimeContext;
import net.github.douwevos.cnc.ui.editor.rectangle.PolyLineCreator;
import net.github.douwevos.cnc.ui.widget.CncUIButton;
import net.github.douwevos.cnc.ui.widget.CncUIButtons;
import net.github.douwevos.cnc.ui.widget.CncUIFrame;
import net.github.douwevos.cnc.ui.widget.CncUIPanel;

public class EditorPerspective implements CncPerspective {

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




	
	@Override
	public void show(CncPerspectiveBoard perspectiveBoard) {
		perspectiveBoard.setContentPanel(editorView);
		this.perspectiveBoard = perspectiveBoard;
		selectionPropertiesList.setPerspectiveBoard(perspectiveBoard);
		
		CncRuntimeContext runtimeContext = perspectiveBoard.getRuntimeContext();
		
		editorView.setModel(runtimeContext.getEditableModel());
		
	}

	@Override
	public void hide() {
		perspectiveBoard.setContentPanel(null);
		selectionPropertiesList.setPerspectiveBoard(null);
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
