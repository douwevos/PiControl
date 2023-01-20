package net.github.douwevos.cnc.model.value;

import net.github.douwevos.justflat.values.Bounds2D;
import net.github.douwevos.justflat.values.Point2D;
import net.github.douwevos.justflat.values.shape.Polygon;
import net.github.douwevos.justflat.shape.PolygonLayer;

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
	public void writeToContourLayer(PolygonLayer polygonLayer, long atDepth) {
		if (atDepth > depth) {
			return;
		}
		
		long x0 = bounds.left;
		long y0 = bounds.bottom;
		long x1 = bounds.right;
		long y1 = bounds.top;
		Polygon polygon = new Polygon();
		polygon.add(Point2D.of(x0, y0));
		polygon.add(Point2D.of(x0, y1));
		polygon.add(Point2D.of(x1, y1));
		polygon.add(Point2D.of(x1, y0));
		polygon.setClosed(true);
		polygonLayer.add(polygon);
	}
	
}
