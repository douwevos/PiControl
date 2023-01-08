package net.github.douwevos.cnc.holer.item;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import net.github.douwevos.cnc.holer.HolerModel;
import net.github.douwevos.cnc.holer.design.SelectionModel;
import net.github.douwevos.cnc.holer.design.ViewCamera;
import net.github.douwevos.cnc.poly.PolyDot;
import net.github.douwevos.cnc.poly.PolyForm;
import net.github.douwevos.cnc.type.Distance;
import net.github.douwevos.cnc.ui.controller.KeyEventType;
import net.github.douwevos.cnc.ui.controller.MouseEventType;
import net.github.douwevos.cnc.ui.controller.UiController;

public class NewPolyLineController implements UiController {

	private final HolerModel holerModel;
	private final SelectionModel selectionModel;
	private final ViewCamera designView;
	private final Runnable callbackToFinish;
	
	private ItemPolyLine polyLine;

	
	public NewPolyLineController(HolerModel holerModel, SelectionModel selectionModel, ViewCamera designView, Runnable callbackToFinish) {
		this.holerModel = holerModel;
		this.selectionModel = selectionModel;
		this.designView = designView;
		this.callbackToFinish = callbackToFinish;
	}
	
	
	@Override
	public void onMouseEvent(MouseEvent event, MouseEventType eventType) {
		switch(eventType) {
//			case DRAGGED : handleDrag(event); break;
			case MOVED : handleMove(event); break;
			case CLICKED : handleClick(event); break;
//			case PRESSED : handlePress(event); break;
		}
	}
	
	
	@Override
	public void onKeyEvent(KeyEvent event, KeyEventType eventType) {
		switch(eventType) {
		case PRESSED : handleKeyPressed(event); break;
		}
	}

	private void handleKeyPressed(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
			polyLine.removeLastDot();
			if (polyLine.dotCount()<=1) {
				holerModel.remove(polyLine);
			}
			callRepaint(event.getComponent());
			callbackToFinish.run();
		}
	}


	private void callRepaint(Component component) {
		component.getParent().repaint();
	}


	private void handleClick(MouseEvent event) {
		Point2D.Double s = getMousePoint(event);
		PolyDot p = new PolyDot(Math.round(s.x), Math.round(s.y), false);
		if (polyLine==null) {
			List<PolyDot> points = new ArrayList<>();
			points.add(p);
			points.add(p);
			PolyForm polyForm = new PolyForm(points, false);
			polyLine = new ItemPolyLine(polyForm, Distance.ofMillMeters(15));
			holerModel.add(polyLine);
		} else {
			polyLine.addDot(p);
		}
	}


	private void handleMove(MouseEvent event) {
		if (polyLine != null) {
			Point2D.Double s = getMousePoint(event);
			callRepaint(event.getComponent());
			polyLine.moveLastDot(Math.round(s.x), Math.round(s.y));
		}
	}



	private Point2D.Double getMousePoint(MouseEvent e) {
		int mouseX = e.getX();
		int mouseY = e.getY();
		AffineTransform transform = designView.createReverseTransform();
		Point2D.Double d = new Point2D.Double(mouseX, mouseY);
		Point2D.Double s = new Point2D.Double();
		transform.transform(d, s);
		return s;
	}

}
