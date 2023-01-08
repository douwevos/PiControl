package net.github.douwevos.cnc.holer.design;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import net.github.douwevos.cnc.holer.HolerModel;
import net.github.douwevos.cnc.holer.design.ViewCamera.CameraListener;
import net.github.douwevos.cnc.holer.item.NewPolyLineController;
import net.github.douwevos.cnc.tool.Tool;
import net.github.douwevos.cnc.type.Distance;
import net.github.douwevos.cnc.ui.controller.KeyEventType;
import net.github.douwevos.cnc.ui.controller.MouseEventType;
import net.github.douwevos.cnc.ui.controller.UiController;
import net.github.douwevos.justflat.types.values.Bounds2D;

public class DesignView extends JPanel implements MouseListener, MouseMotionListener, KeyListener, ComponentListener, MouseWheelListener, CameraListener {

	private final SelectionModel selectionModel = new SelectionModel();
	
	private final ViewCamera viewCamera = new ViewCamera();
	private final SelectionController selectionController;
	
	private final List<Color> toolColorList = new ArrayList<>();

	private HolerModel holerModel2;

	UiController controller;
	
	
	public DesignView() {
		selectionController = new SelectionController(selectionModel, viewCamera);
		toolColorList.add(Color.yellow);
		toolColorList.add(Color.magenta);
		toolColorList.add(Color.cyan);
		toolColorList.add(Color.green.brighter());
		toolColorList.add(Color.blue.brighter());
		toolColorList.add(Color.orange);
		controller = selectionController;
		setFocusable(true);
		viewCamera.addListener(this);
	}

	public void startNewPolyLine() {
		if (holerModel2 == null) {
			return;
		}
		controller = new NewPolyLineController(holerModel2, selectionModel, viewCamera, this::setSelectionController);
	}
	
	public void setSelectionController() {
		controller = selectionController;
	}

	
	public void setHolerModel(HolerModel holerModel) {
		this.holerModel2 = holerModel;
		selectionController.setHolerModel(holerModel);
		updateViewCamera();
	}
	
	@Override
	public void addNotify() {
		super.addNotify();
		addMouseMotionListener(this);
		addMouseListener(this);
		addMouseWheelListener(this);
		
		addKeyListener(this);
		addComponentListener(this);
		updateViewCamera();
		
	}
	
	@Override
	public void update(Graphics g) {
		super.update(g);
		int width = getWidth();
		Shape clip = g.getClip();
		System.out.println("Update:"+System.currentTimeMillis()+", width="+width+", x="+getX()+", clip="+clip);
	}
	
	@Override
	public void paint(Graphics g) {
		Graphics2D gfx = (Graphics2D) g;
//		Graphics2D gfx = (Graphics2D) g.create();
		
		
		int width = getWidth();
		int height = getHeight();
		gfx.clearRect(0, 0, width, height);

		if (holerModel2 == null) {
			return;
		}
		
		Shape clip = g.getClip();
		
		
		g.setColor(Color.black);
		g.fillRect(0, 0, width, height);


		Distance modelWidth = holerModel2.getWidth();
		Distance modelHeight = holerModel2.getHeight();
		
		long asMicrometers = modelWidth.asMicrometers();
		double zoomW = (double) (width-20)/asMicrometers;
		double zoomH = (double) (height-20)/asMicrometers;
		double zoom = zoomH<zoomW ? zoomH : zoomW;
//		viewCamera.setScale(zoom);
//		viewCamera.setViewHeight(height-1);
		
//		updateViewCamera();
		
		AffineTransform transform2 = gfx.getTransform();
		
		AffineTransform transform = viewCamera.createTransform();
//		AffineTransform translateInstance = AffineTransform.getTranslateInstance(getX(), getY());
//		translateInstance.concatenate(transform);
//		gfx.setTransform(translateInstance);
		
		AffineTransform affineTransform = new AffineTransform(transform2);
		affineTransform.concatenate(transform);
		
		gfx.setTransform(affineTransform);

//		System.out.println("Paint:"+System.currentTimeMillis()+", width="+width+", x="+getX()+", clip="+clip+", t="+transform+", transform2="+transform2);

		
		
		holerModel2.paint(gfx, viewCamera, toolColorList, selectionModel);
//		gfx.dispose();
//		gfx.setTransform(transform2);
	}
	
	private void updateViewCamera() {
		
		CameraLockType lockType = viewCamera.getLockType();
		if (lockType == CameraLockType.FREE) {
			return;
		}
		int width = getWidth();
		int height = getHeight();
		if (holerModel2 ==null) {
			return;
		}
		long pieceWidth = holerModel2.getWidth().asMicrometers();
		long pieceHeight = holerModel2.getHeight().asMicrometers();
		
		Bounds2D bounds = holerModel2.bounds();
		
		long modelWidth = bounds.right-bounds.left;
		long modelHeight = bounds.top-bounds.bottom;

		
		
//		long maxWidth = modelWidth>pieceWidth ? modelWidth : pieceWidth;
//		long maxHeight = modelHeight>pieceHeight ? modelHeight : pieceHeight;

		long maxWidth = pieceWidth;
		long maxHeight = pieceHeight;

		double zoomW = (double) (width-20)/maxWidth;
		double zoomH = (double) (height-20)/maxHeight;
		double zoom = zoomH<zoomW ? zoomH : zoomW;
		
		long left = bounds.left<0 ? bounds.left : 0;
		long bottom = bounds.bottom<0 ? bounds.bottom : 0;
		
//		viewCamera.setTranslate(-left*zoom, -bottom*zoom);
//		viewCamera.setScale(zoom);
//		viewCamera.setViewHeight(height-1);
		viewCamera.setValues(-left*zoom, -bottom*zoom, zoom, height-1);
		
		
//
//		Distance modelWidth = holerModel2.getWidth();
//		Distance modelHeight = holerModel2.getHeight();
//		
//		long asMicrometers = modelWidth.asMicrometers();
//		double zoomW = (double) (width-20)/asMicrometers;
//		double zoomH = (double) (height-20)/asMicrometers;
//		double zoom = zoomH<zoomW ? zoomH : zoomW;
//		viewCamera.setScale(zoom);
//		viewCamera.setViewHeight(height-1);
		
//		System.out.println("zoom="+zoom+", left="+left);
		
	}
	
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int mouseX = e.getX();
		int viewHeight = e.getComponent().getHeight();
		int mouseY = viewHeight-e.getY();
		
		int wheelRotation = e.getWheelRotation();
		
		if (wheelRotation<0) {
			viewCamera.zoomIn(mouseX, mouseY);
		} else if (wheelRotation>0) {
			viewCamera.zoomOut(mouseX, mouseY);
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		controller.onMouseEvent(e, MouseEventType.DRAGGED);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		controller.onMouseEvent(e, MouseEventType.MOVED);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		grabFocus();
		controller.onMouseEvent(e, MouseEventType.CLICKED);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		controller.onMouseEvent(e, MouseEventType.PRESSED);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		controller.onMouseEvent(e, MouseEventType.RELEASED);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		controller.onMouseEvent(e, MouseEventType.ENTERED);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		controller.onMouseEvent(e, MouseEventType.EXITED);
	}
	
	
	@Override
	public void keyPressed(KeyEvent e) {
		controller.onKeyEvent(e, KeyEventType.PRESSED);
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		controller.onKeyEvent(e, KeyEventType.RELEASED);
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		controller.onKeyEvent(e, KeyEventType.TYPED);
	}
	
	public SelectionModel getSelectionModel() {
		return selectionModel;
	}

	@Override
	public void componentResized(ComponentEvent e) {
		updateViewCamera();		
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		updateViewCamera();		
	}

	@Override
	public void componentShown(ComponentEvent e) {
		updateViewCamera();		
	}

	@Override
	public void componentHidden(ComponentEvent e) {
	}


	@Override
	public void onCameraChanged() {
		updateViewCamera();		
		repaint();
	}
	
}
