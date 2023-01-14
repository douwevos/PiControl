package net.github.douwevos.cnc.model.value;

import net.github.douwevos.justflat.contour.Contour;
import net.github.douwevos.justflat.contour.ContourLayer;
import net.github.douwevos.justflat.types.values.Bounds2D;
import net.github.douwevos.justflat.types.values.Point2D;

public class Rectangle implements Item {
	
	private final Bounds2D bounds;
	private final long depth;
	
	public Rectangle(Bounds2D bounds, long depth) {
		this.bounds = bounds;
		this.depth = depth;
	}
	
	
	public Bounds2D getBounds() {
		return bounds;
	}
	
	public long getDepth() {
		return depth;
	}
	
	
	@Override
	public void writeToContourLayer(ContourLayer contourLayer, long atDepth) {
		if (atDepth > depth) {
			return;
		}
		
		long x0 = bounds.left;
		long y0 = bounds.bottom;
		long x1 = bounds.right;
		long y1 = bounds.top;
		Contour contour = new Contour();
		contour.add(Point2D.of(x0, y0));
		contour.add(Point2D.of(x0, y1));
		contour.add(Point2D.of(x1, y1));
		contour.add(Point2D.of(x1, y0));
		contour.setClosed(true);
		contourLayer.add(contour);
	}
	
}
