package net.github.douwevos.cnc.ui;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

public class ViewCamera {

	private CameraLockType cameraLockType;

	public double translateX;
	public double translateY;
	
	private int viewHeight;
	
	private double zoom = 1d;

	private List<CameraListener> listeners = new ArrayList<>();

	public ViewCamera() {
		cameraLockType = CameraLockType.FIT_MODEL;
	}

	
	public void setLockType(CameraLockType fitModel) {
		if (cameraLockType == fitModel) {
			return;
		}
		cameraLockType = fitModel;
		notifyChanged();
	}
	
	public CameraLockType getLockType() {
		return cameraLockType;
	}

	public AffineTransform createTransform() {
		AffineTransform translateInstance = AffineTransform.getTranslateInstance(translateX, -translateY);
		translateInstance.translate(0, viewHeight);
		translateInstance.scale(zoom, -zoom);
		return translateInstance;
	}
	
	public AffineTransform createReverseTransform() {
		AffineTransform translateInstance = AffineTransform.getScaleInstance(1d/zoom, -1d/zoom);
		translateInstance.translate(0, -viewHeight);
		translateInstance.translate(-translateX,  translateY);
		return translateInstance;
	}

	public double getTranslateX() {
		return translateX;
	}
	
	public double getTranslateY() {
		return translateY;
	}
	
	public void setScale(double zoom2) {
		zoom = zoom2;
		notifyChanged();
	}
	
	public double getScale() {
		return zoom;
	}

	public void setViewHeight(int height) {
		if (viewHeight == height) {
			return;
		}
		this.viewHeight = height;
		notifyChanged();
	}

	public void setTranslate(double left, double bottom) {
		translateX = left;
		translateY = bottom;
		notifyChanged();
	}
	
	

	public void zoomIn(int x, int y) {
		zoomWith(x, y, zoom*1.1d);
	}

	public void zoomOut(int x, int y) {
		zoomWith(x, y, zoom*0.9d);
	}
	
	

	public void zoomWith(int x, int y, double newZoom) {
		if (newZoom == zoom) {
			return;
		}
		cameraLockType = CameraLockType.FREE;

		double nx = translateX + x*zoom;
		double ny = translateY + y*zoom;

		zoom = newZoom;
		nx = nx - x*zoom;
		ny = ny - y*zoom;
		translateX = nx;
		translateY = ny;
		notifyChanged();
	}




	public void addListener(CameraListener cameraListener) {
		if (!listeners.contains(cameraListener)) {
			listeners.add(cameraListener);
		}
	}
	
	public void removeListener(CameraListener cameraListener) {
		listeners.remove(cameraListener);
	}

	private void notifyChanged() {
		for(CameraListener listener : listeners) {
			listener.onCameraChanged();
		}
	}
	
	public interface CameraListener {
		void onCameraChanged();
	}

	public void setValues(double translateX, double translateY, double zoom, int viewHeight) {
		if ((translateX == this.translateX) && (translateY==this.translateY)
				&& zoom == this.zoom && this.viewHeight == viewHeight) {
			return;
		}
		this.translateX = translateX;
		this.translateY = translateY;
		this.zoom = zoom;
		this.viewHeight = viewHeight;
		notifyChanged();
	}


}
