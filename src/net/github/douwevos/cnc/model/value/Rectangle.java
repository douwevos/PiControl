package net.github.douwevos.cnc.model.value;

import net.github.douwevos.justflat.values.Bounds2D;
import net.github.douwevos.justflat.values.Point2D;
import net.github.douwevos.justflat.values.shape.Polygon;
import net.github.douwevos.justflat.values.shape.Polygon.PolygonBuilder;
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
	
	@Override
	public Bounds2D calculateBounds() {
		return bounds;
	}
	
	@Override
	public long getMaxDepth() {
		return depth;
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
		PolygonBuilder polygon = new Polygon().builder();
		polygon.add(Point2D.of(x0, y0));
		polygon.add(Point2D.of(x0, y1));
		polygon.add(Point2D.of(x1, y1));
		polygon.add(Point2D.of(x1, y0));
		polygon.closed(true);
		polygonLayer.add(polygon.build());
	}
	
}
