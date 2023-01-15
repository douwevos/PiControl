package net.github.douwevos.cnc.ui;

import java.awt.Color;
import java.awt.Graphics2D;

import net.github.douwevos.justflat.types.values.Bounds2D;
import net.github.douwevos.justflat.types.values.FracPoint2D;
import net.github.douwevos.justflat.types.values.Point2D;

public class ModelGraphics {

	private Color COLOR_DEFAULT = new Color(0xFF, 0xFF, 0xFF);
	private Color COLOR_DOT = new Color(0xAA, 0xAA, 0xAA);
	private Color COLOR_SELECTION = new Color(0, 0xFF, 0);
	private Color COLOR_HIGHLIGHT = new Color(0xFF, 0xFF, 0);
	private Color COLOR_FAINT = new Color(0x44, 0x44, 0x44);
	
	Graphics2D gfx;
	Camera camera;
	
	public ModelGraphics(Graphics2D gfx, Camera camera) {
		this.gfx = gfx;
		this.camera = camera;
	}

	public Camera getCamera() {
		return camera;
	}
	
	public void setColor(Color color) {
		gfx.setColor(color);
	}

	public Color colorDefault() {
		gfx.setColor(COLOR_DEFAULT);
		return COLOR_DEFAULT;
	}

	public Color colorDot() {
		gfx.setColor(COLOR_DOT);
		return COLOR_DOT;
	}
	
	public Color colorSelection() {
		gfx.setColor(COLOR_SELECTION);
		return COLOR_SELECTION;
	}

	public Color colorHighlight() {
		gfx.setColor(COLOR_HIGHLIGHT);
		return COLOR_HIGHLIGHT;
	}
	
	public Color faintDefault() {
		gfx.setColor(COLOR_FAINT);
		return COLOR_FAINT;
	}


	public void drawRectangle(Bounds2D rectangle) {
		Point2D vcLeftBottom = camera.toViewCoords(rectangle.left, rectangle.bottom);
		Point2D vcRightTop = camera.toViewCoords(rectangle.right, rectangle.top);
		
		int x0 = (int) vcLeftBottom.x;
		int y0 = (int) vcRightTop.y;
		int x1 = (int) (vcRightTop.x - vcLeftBottom.x);
		int y1 = (int) (vcLeftBottom.y - vcRightTop.y);

		gfx.drawRect(x0, y0, x1, y1);
	}

	public void drawDot(Point2D location) {
		drawCircle(location, Math.round(3*camera.getZoom()), true);
	}

	public void drawCircle(Point2D center, long radius, boolean filled) {
		Point2D vcCenter = camera.toViewCoords(center);
		int viewRadius = (int) camera.toViewSize(radius);
		int xc = (int) vcCenter.x;
		int yc = (int) vcCenter.y;
		int d = viewRadius*2;
		if (filled) {
			gfx.fillArc(xc-viewRadius, yc-viewRadius, d, d, 0,360);
		} else {
			gfx.drawArc(xc-viewRadius, yc-viewRadius, d, d, 0,360);
		}
	}

	public void drawLine(Point2D pointA, Point2D pointB) {
		Point2D vcA = camera.toViewCoords(pointA);
		Point2D vcB = camera.toViewCoords(pointB);
		int x0 = (int) vcA.x;
		int y0 = (int) vcA.y;
		int x1 = (int) vcB.x;
		int y1 = (int) vcB.y;
		gfx.drawLine(x0, y0, x1, y1);
	}

	public void drawLine(FracPoint2D pointA, FracPoint2D pointB) {
		Point2D vcA = camera.toViewCoords(pointA).toNonFractional();
		Point2D vcB = camera.toViewCoords(pointB).toNonFractional();
		int x0 = (int) vcA.x;
		int y0 = (int) vcA.y;
		int x1 = (int) vcB.x;
		int y1 = (int) vcB.y;
		gfx.drawLine(x0, y0, x1, y1);
	}


}
