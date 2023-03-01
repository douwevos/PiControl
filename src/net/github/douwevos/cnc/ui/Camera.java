package net.github.douwevos.cnc.ui;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.github.douwevos.justflat.values.Bounds2D;
import net.github.douwevos.justflat.values.FracPoint2D;
import net.github.douwevos.justflat.values.Point2D;

public class Camera {
	
	private CameraLockType cameraLockType;
	private CameraConfig cameraConfig = new CameraConfig(0,0, 1d);
	private int viewHeight;
	private List<CameraListener> listeners = new ArrayList<>();
	

	public Camera() {
		cameraLockType = CameraLockType.FIT_MODEL;
	}
	
	public void setViewHeight(int viewHeight) {
		if (this.viewHeight == viewHeight) {
			return;
		}
		this.viewHeight = viewHeight;
		notifyChanged();
	}
	
	public void refit(Bounds2D modelBounds, Dimension viewDimension) {
		boolean doNotify = false;
		if (viewDimension.height != viewHeight) {
			viewHeight = viewDimension.height;
			doNotify = true;
		}
		 
		CameraConfig newCameraConfig = cameraConfig;
		CameraLockType lockType = getLockType();
		switch(lockType) {
			case FIT_LAYER : 
			case FIT_MODEL :
				if (modelBounds == null) {
					newCameraConfig = new CameraConfig(0, 0, 1d);
				} else {
					int viewWidth = viewDimension.width;
					int INSETS = 10;
					int mViewWidth = viewWidth - INSETS*2;
					int mViewHeight = viewHeight - INSETS*2;
					int modelHeight = (int) (1l + modelBounds.top - modelBounds.bottom);
					int modelWidth = (int) (1l + modelBounds.right-modelBounds.left);
					
					double zoomY = (double) modelHeight/mViewHeight;
					double zoomX = (double) modelWidth/mViewWidth;
					double zoom = zoomY;
					double tx, ty;
			
					if (zoomX>zoomY) {
						zoom = zoomX;
						tx = -INSETS*zoom + modelBounds.left;
						ty = -INSETS*zoom + modelBounds.bottom + (modelHeight-viewHeight*zoom)/2;
					} else {
						zoom = zoomY;
						ty = -INSETS*zoom + modelBounds.bottom;
						tx = -INSETS*zoom + modelBounds.left + (modelWidth-viewWidth*zoom)/2;
					}
					newCameraConfig = new CameraConfig(tx, ty, zoom);
				}
			case FREE :
			default : 
				break;
		}
		if (!Objects.equals(newCameraConfig, cameraConfig)) {
			cameraConfig = newCameraConfig;
			doNotify = true;
		}
		
		if (doNotify) {
			notifyChanged();
		}
	}

	
	
	public int getViewHeight() {
		return viewHeight;
	}
	
	public double getZoom() {
		return cameraConfig.zoom;
	}
	
	public double getTranslateX() {
		return cameraConfig.translateX;
	}
	
	public double getTranslateY() {
		return cameraConfig.translateY;
	}
	
	public CameraLockType getLockType() {
		return cameraLockType;
	}
	
	public void setLockType(CameraLockType cameraLockType) {
		this.cameraLockType = cameraLockType;
		notifyChanged();
	}
	
	public void zoomIn(int x, int y) {
		zoomWith(x, y, cameraConfig.zoom*0.9d);
	}

	public void zoomOut(int x, int y) {
		zoomWith(x, y, cameraConfig.zoom*1.1d);
	}

	public void zoomWith(int x, int y, double newZoom) {
		if (newZoom == cameraConfig.zoom) {
			return;
		}
		cameraLockType = CameraLockType.FREE;

		double nx = cameraConfig.translateX + x*cameraConfig.zoom;
		double ny = cameraConfig.translateY + y*cameraConfig.zoom;

		nx = nx - x*newZoom;
		ny = ny - y*newZoom;
		double translateX = nx;
		double translateY = ny;
		
		cameraConfig = new CameraConfig(translateX, translateY, newZoom);
		
		notifyChanged();
	}


	public void setValues(double zoom, double x, double y) {
		if (cameraConfig.zoom == zoom && cameraConfig.translateX==x && cameraConfig.translateY==y) {
			return;
		}
		cameraConfig = new CameraConfig(x, y, zoom);
		notifyChanged();
	}

	public void setTranslate(double nx, double ny) {
		if (cameraConfig.translateX == nx && cameraConfig.translateY == ny) {
			return;
		}
		cameraLockType = CameraLockType.FREE;
		cameraConfig = new CameraConfig(nx, ny, cameraConfig.zoom);
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

	public long toViewSize(long modelSize) {
		CameraConfig cc = cameraConfig;
		return Math.round(modelSize/cc.zoom);
	}

	public long toModelSize(long viewSize) {
		CameraConfig cc = cameraConfig;
		return Math.round(viewSize*cc.zoom);
	}

	public double toModelSize(double viewSize) {
		CameraConfig cc = cameraConfig;
		return viewSize*cc.zoom;
	}

	
	public Point2D toViewCoords(Point2D p) {
		return toViewCoords(p.x, p.y);
	}

	public FracPoint2D toViewCoords(FracPoint2D p) {
		return toViewCoords(p.x, p.y);
	}

	public Point2D toViewCoords(long x, long y) {
		CameraConfig cc = cameraConfig;
		long ixb = Math.round((-cc.translateX + x)/cc.zoom);
		long iyb = viewHeight + Math.round((cc.translateY - y)/cc.zoom);
		return Point2D.of(ixb, iyb);
	}

	public FracPoint2D toViewCoords(double x, double y) {
		CameraConfig cc = cameraConfig;
		double ixb = (-cc.translateX + x)/cc.zoom;
		double iyb = viewHeight + (cc.translateY - y)/cc.zoom;
		return FracPoint2D.of(ixb, iyb);
	}
	
	public static class CameraConfig {
		public final double translateX;
		public final double translateY;
		public final double zoom;
		
		public CameraConfig(double translateX, double translateY, double zoom) {
			this.translateX = translateX;
			this.translateY = translateY;
			this.zoom = zoom;
		}

		@Override
		public String toString() {
			return "CameraConfig [translateX=" + translateX + ", translateY=" + translateY + ", zoom=" + zoom + "]";
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}
			if (obj instanceof CameraConfig) {
				CameraConfig that = (CameraConfig) obj;
				return that.translateX == translateX && that.translateY==translateY && that.zoom==zoom;
			}
			return false;
		}
		
	}

	@Override
	public String toString() {
		return "Camera [cameraLockType=" + cameraLockType + ", cameraConfig=" + cameraConfig + ", viewHeight="
				+ viewHeight + "]";
	}
	
}