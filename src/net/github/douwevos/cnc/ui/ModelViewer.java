package net.github.douwevos.cnc.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import net.github.douwevos.cnc.ui.Camera.CameraListener;
import net.github.douwevos.cnc.ui.controller.MouseEventType;
import net.github.douwevos.justflat.values.Bounds2D;


public abstract class ModelViewer extends JPanel implements MouseListener, MouseMotionListener, KeyListener, ComponentListener, MouseWheelListener, CameraListener {

	protected Camera camera = new Camera();

	protected int dragX;
	protected int dragY;
	protected double dragTX;
	protected double dragTY;

	
	protected volatile boolean modelImageDirty;
	protected BufferedImage modelImage;

	
	public ModelViewer() {
		setFocusable(true);
		camera.addListener(this);
	}

	protected Dimension getViewDimension() {
		return new Dimension(getWidth(), getHeight());
	}
	
	public abstract Bounds2D getModelBounds();


	@Override
	public void addNotify() {
		super.addNotify();
		addMouseMotionListener(this);
		addMouseListener(this);
		addMouseWheelListener(this);
		
		addKeyListener(this);
		addComponentListener(this);
		updateViewCamera();
		
		if (isVisible()) {
			componentShown(null);
		}
	}

	
	protected Image updateModelImage() {
		int width = getWidth();
		int height = getHeight();
		if (modelImage != null) {
//			if (modelImage.validate(getGraphicsConfiguration())==VolatileImage.IMAGE_INCOMPATIBLE) {
//				modelImage = null;
//			} else 
			if (modelImage.getWidth()!=width || modelImage.getHeight()!=height) {
				modelImage = null;
			}
		}
		
		if (modelImage == null ) {
//			modelImage = createVolatileImage(width, height);
			modelImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			modelImageDirty = true;
		}
		
		if (modelImageDirty) {

			Graphics2D graphics2d = modelImage.createGraphics();
			graphics2d.setColor(Color.black);
			graphics2d.fillRect(0, 0, width, height);
			ModelGraphics modelGraphics = new ModelGraphics(graphics2d, camera);
			drawModelImage(modelGraphics);
			graphics2d.dispose();
			
			modelImageDirty = false;
		}
		
		return modelImage;
	}

	
	protected abstract void drawModelImage(ModelGraphics modelGraphics);

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		ModelMouseEvent modelEvent = ModelMouseEvent.create(e, MouseEventType.WHEEL, camera, getViewDimension());
		onModelMouseEvent(modelEvent);
	}
	


	protected boolean onModelMouseEvent(ModelMouseEvent modelEvent) {
		if (modelEvent.type == MouseEventType.WHEEL) {
			MouseEvent event = modelEvent.event;
			int mouseX = event.getX();
			int viewHeight = event.getComponent().getHeight();
			int mouseY = viewHeight-event.getY();
			
			int wheelRotation = ((MouseWheelEvent) event).getWheelRotation();
			
			if (wheelRotation<0) {
				camera.zoomIn(mouseX, mouseY);
				return true;
			} else if (wheelRotation>0) {
				camera.zoomOut(mouseX, mouseY);
				return true;
			}
		} else if (modelEvent.type == MouseEventType.PRESSED) {
			MouseEvent event = modelEvent.event;
			dragX = event.getX();
			dragY = event.getY();
			dragTX = camera.getTranslateX();
			dragTY = camera.getTranslateY();
		} else if (modelEvent.type == MouseEventType.DRAGGED) {
			MouseEvent event = modelEvent.event;
			int mouseX = event.getX();
			int mouseY = event.getY();
			double cameraZoom = camera.getZoom();
			double nx = dragTX + (dragX - mouseX)*cameraZoom;
			double ny = dragTY + (mouseY - dragY)*cameraZoom;

			camera.setTranslate(nx,ny);
			
		}
		return false;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		ModelMouseEvent modelEvent = ModelMouseEvent.create(e, MouseEventType.DRAGGED, camera, getViewDimension());
		onModelMouseEvent(modelEvent);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		ModelMouseEvent modelEvent = ModelMouseEvent.create(e, MouseEventType.MOVED, camera, getViewDimension());
		onModelMouseEvent(modelEvent);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		grabFocus();
		ModelMouseEvent modelEvent = ModelMouseEvent.create(e, MouseEventType.CLICKED, camera, getViewDimension());
		onModelMouseEvent(modelEvent);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		ModelMouseEvent modelEvent = ModelMouseEvent.create(e, MouseEventType.PRESSED, camera, getViewDimension());
		onModelMouseEvent(modelEvent);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		ModelMouseEvent modelEvent = ModelMouseEvent.create(e, MouseEventType.RELEASED, camera, getViewDimension());
		onModelMouseEvent(modelEvent);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
//		controller.onMouseEvent(e, MouseEventType.ENTERED);
	}

	@Override
	public void mouseExited(MouseEvent e) {
//		controller.onMouseEvent(e, MouseEventType.EXITED);
	}
	
	
	@Override
	public void keyPressed(KeyEvent e) {
//		controller.onKeyEvent(e, KeyEventType.PRESSED);
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
//		controller.onKeyEvent(e, KeyEventType.RELEASED);
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
//		controller.onKeyEvent(e, KeyEventType.TYPED);
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

	protected void updateViewCamera() {
		camera.refit(getModelBounds(), getViewDimension());
		modelImageDirty = true;
	}
		
}
