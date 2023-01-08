package net.github.douwevos.cnc.model.value;

import net.github.douwevos.justflat.types.values.Bounds2D;

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
	
	
}
