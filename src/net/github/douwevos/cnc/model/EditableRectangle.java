package net.github.douwevos.cnc.model;

import java.util.Objects;

import net.github.douwevos.cnc.model.value.Item;
import net.github.douwevos.cnc.model.value.PolyLine;
import net.github.douwevos.cnc.model.value.Rectangle;
import net.github.douwevos.justflat.values.Bounds2D;

public class EditableRectangle extends Editable {

	private Bounds2D bounds;
	private long depth;
	
	private Rectangle build;
	
	public EditableRectangle(Bounds2D bounds, long depth) {
		super(ItemType.RECTANGLE);
		this.bounds = bounds;
		this.depth = depth;
	}
	

	@Override
	protected Item snapshot() {
		if (build != null && build.getBounds()!=bounds && build.getDepth()!=depth) {
			build = null;
		}
		if (build == null) {
			build = new Rectangle(bounds, depth);
		}
		return build;
	}
	
	
	public Bounds2D getBounds() {
		return bounds;
	}
	
	public void setBounds(Bounds2D bounds) {
		if (Objects.equals(bounds, this.bounds)) {
			return;
		}
		this.bounds = bounds;
		dispatchModelChanged();
	}
	
	public long getDepth() {
		return depth;
	}
	
	@Override
	public Bounds2D calculateBounds() {
		return bounds;
	}
}
