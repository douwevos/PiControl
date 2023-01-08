package douwe.design;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

import douwe.Point3D;

public class DesignPanel extends JPanel implements MouseListener, MouseMotionListener {

	private final Model model;
	
	private volatile double xangle;
	private volatile double zangle;
	private ModelView modelView;
	
	private GrabPoint grabPoint;
	private Camera grabCamera;
	
	private boolean doRotateView;
	
	public DesignPanel(Model model) {
		this.model = model;
		modelView = new ModelView(model, new Camera(0.8d, 0.2d, 0.3d));
	}
	
	@Override
	public void addNotify() {
		super.addNotify();
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	
	@Override
	public void paint(Graphics g) {
		
		Graphics2D gfx = (Graphics2D) g;
		
		int width = getWidth();
		int height = getHeight();
		
		g.setColor(Color.black);
		g.fillRect(0, 0, width, height);
		
		gfx.translate(width/2, height/2);
		modelView.rebuild();
		modelView.paint(gfx);
	}


	public void rot() {
		Camera camera = modelView.getCamera();
		Camera cameraMoved = camera.xRotate(0.003d).zRotate(0.01d);
		modelView.setCamera(cameraMoved);
		xangle = xangle+0.003d;
		zangle = zangle+0.01d;
		invalidate();
		repaint();
	}


	@Override
	public void mouseClicked(MouseEvent e) {
	}


	@Override
	public void mousePressed(MouseEvent e) {
		int mouseX = e.getX() - getWidth()/2;
		int mouseY = e.getY() - getHeight()/2;
		
		
		if (e.getButton() == 2) {
			doRotateView = true;
			grabPoint = new GrabPoint(0, null, mouseX, mouseY);
			grabCamera = modelView.getCamera();
			return;
		}
		
		doRotateView = false;
		Integer snapPointIndex = modelView.findSnapPointIndex(mouseX, mouseY);
		if (snapPointIndex!=null && snapPointIndex>=0) {
			Point3D point = modelView.getViewPoint(snapPointIndex);
			Point3D perspectivePoint = modelView.toPerspectiveView(point);
			grabPoint = new GrabPoint(snapPointIndex, perspectivePoint, mouseX, mouseY);
		}
	}


	@Override
	public void mouseReleased(MouseEvent e) {
		grabPoint = null;
	}


	@Override
	public void mouseEntered(MouseEvent e) {
	}


	@Override
	public void mouseExited(MouseEvent e) {
	}


	@Override
	public void mouseDragged(MouseEvent e) {
		if (grabPoint != null) {
			int mouseX = e.getX() - getWidth()/2;
			int mouseY = e.getY() - getHeight()/2;
			
			if (doRotateView) {
				
				long deltaX = grabPoint.grabMouseX-mouseX;
				long deltaY = grabPoint.grabMouseY-mouseY;

				Camera xRotate = grabCamera.xRotate((double) Math.PI*deltaX/getWidth());
				xRotate = xRotate.yRotate((double) Math.PI*deltaY/getHeight());
				modelView.setCamera(xRotate);
				invalidate();
				repaint();
				
				return;
			}
			
			long x = grabPoint.grabPerspectivePoint.x-grabPoint.grabMouseX+mouseX; 
			long y = grabPoint.grabPerspectivePoint.y-grabPoint.grabMouseY+mouseY; 
			Point3D newPerspectivePoint = new Point3D(x,y, grabPoint.grabPerspectivePoint.z);
			Point3D s = modelView.perspectivePointToModel(newPerspectivePoint);
			model.setPoint(grabPoint.pointIndex, s);
			invalidate();
			repaint();
		}
	}


	@Override
	public void mouseMoved(MouseEvent e) {
		int mouseX = e.getX() - getWidth()/2;
		int mouseY = e.getY() - getHeight()/2;
		
		Integer snapPointIndex = modelView.findSnapPointIndex(mouseX, mouseY);
		if (modelView.selectSnapPointIndex(snapPointIndex)) {
			invalidate();
			repaint();
		}
	}
	
	
	static class GrabPoint {
		final int pointIndex;
		final Point3D grabPerspectivePoint;
		final int grabMouseX;
		final int grabMouseY;
		
		public GrabPoint(int pointIndex, Point3D grabPerspectivePoint, int grabMouseX, int grabMouseY) {
			this.pointIndex = pointIndex;
			this.grabPerspectivePoint = grabPerspectivePoint;
			this.grabMouseX = grabMouseX;
			this.grabMouseY = grabMouseY;
		}
	}

	
}
