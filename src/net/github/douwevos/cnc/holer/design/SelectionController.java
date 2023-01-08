package net.github.douwevos.cnc.holer.design;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import net.github.douwevos.cnc.holer.HolerModel;
import net.github.douwevos.cnc.holer.ItemGrabInfo;
import net.github.douwevos.cnc.ui.controller.KeyEventType;
import net.github.douwevos.cnc.ui.controller.MouseEventType;
import net.github.douwevos.cnc.ui.controller.UiController;

public class SelectionController implements UiController {

	private HolerModel holerModel2;
	private final SelectionModel selectionModel;
	private final ViewCamera viewCamera;
	
	int dragX;
	int dragY;
	double dragTX;
	double dragTY;
	
	public SelectionController(SelectionModel selectionModel, ViewCamera viewCamera) {
		this.selectionModel = selectionModel;
		this.viewCamera = viewCamera;
	}
	
	
	public void setHolerModel(HolerModel holerModel) {
		this.holerModel2 = holerModel;
	}
	
	@Override
	public void onMouseEvent(MouseEvent event, MouseEventType eventType) {
		if (holerModel2 == null) {
			return;
		}
		switch(eventType) {
			case DRAGGED : handleDrag(event); break;
			case MOVED : handleMove(event); break;
			case CLICKED : handleClick(event); break;
			case PRESSED : handlePress(event); break;
		}
	}

	private void callRepaint(Component component) {
		component.getParent().repaint();
	}
	
	private void handleDrag(MouseEvent event) {
		if (selectionModel.isEmpty()) {
			int mouseX = event.getX();
			int mouseY = event.getY();
			double cameraZoom = viewCamera.getScale();
			double nx = dragTX - (dragX - mouseX);
			double ny = dragTY - (mouseY - dragY);
			viewCamera.setTranslate(nx,ny);
			return;
		}
		Point2D.Double s = getMousePoint(event);
		long mouseX = (long) s.getX();
		long mouseY = (long) s.getY();
		selectionModel.doDrag(mouseX, mouseY);
		callRepaint(event.getComponent());
	}
	
	private void handleMove(MouseEvent event) {
		Point2D.Double s = getMousePoint(event);
		ItemGrabInfo grabInfo = holerModel2.findNearestGrabInfo((int) (s.getX()), (int) (s.getY()));
		selectionModel.setHighlight(grabInfo);
		callRepaint(event.getComponent());
	}
	
	public void handleClick(MouseEvent event) {
		Point2D.Double s = getMousePoint(event);
		if (event.getButton() == 1) {
			if (event.getClickCount()==2) {
				viewCamera.setLockType(CameraLockType.FIT_MODEL);
			} else {
				ItemGrabInfo grabInfo = holerModel2.findNearestGrabInfo((int) (s.getX()), (int) (s.getY()));
				selectionModel.addSelection(grabInfo, false);
				callRepaint(event.getComponent());
			}
//		} else if (e.getButton() == 3) {
//			Container topLevelAncestor = getTopLevelAncestor();
//			CncUIPopup popup = new CncUIPopup((Window) topLevelAncestor);
//			int mouseX = e.getXOnScreen();
//			int mouseY = e.getYOnScreen();
//			popup.setLocation(mouseX, mouseY);
//			popup.setSize(300, 300);
//			popup.setVisible(true);
		}
	}
	
	public void handlePress(MouseEvent e) {
		if (e.getButton() == 1) {
			Point2D.Double s = getMousePoint(e);
			long mouseX = (long) s.getX();
			long mouseY = (long) s.getY();
			ItemGrabInfo grabInfo = holerModel2.findNearestGrabInfo((int) (s.getX()), (int) (s.getY()));
			selectionModel.addSelection(grabInfo, false);
			selectionModel.startDrag(mouseX, mouseY);

			dragX = e.getX();
			dragY = e.getY();
			dragTX = viewCamera.getTranslateX();
			dragTY = viewCamera.getTranslateY();
		}
	}

	
	private Point2D.Double getMousePoint(MouseEvent e) {
		int mouseX = e.getX();
		int mouseY = e.getY();
		AffineTransform transform = viewCamera.createReverseTransform();
		Point2D.Double d = new Point2D.Double(mouseX, mouseY);
		Point2D.Double s = new Point2D.Double();
		transform.transform(d, s);
		return s;
	}

	
	@Override
	public void onKeyEvent(KeyEvent event, KeyEventType eventType) {
		switch (eventType) {
			case PRESSED: handleKeyPress(event); break;

		default:
			break;
		}
	}


	private void handleKeyPress(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.VK_DELETE) {
			selectionModel.deleteSelected();
		}
	}
	
}
