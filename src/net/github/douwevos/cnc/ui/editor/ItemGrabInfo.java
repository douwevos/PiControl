package net.github.douwevos.cnc.ui.editor;

import net.github.douwevos.cnc.model.Editable;
import net.github.douwevos.cnc.ui.Camera;

public abstract class ItemGrabInfo<T extends Editable> {

	protected T item;
	protected long grabDeltaX;
	protected long grabDeltaY;
	protected long squareDistance;
	
	public ItemGrabInfo(T item, long distSq) {
		this.item = item;
		this.squareDistance = distSq;
	}
	
	public T getItem() {
		return item;
	}
	
	public long getSquareDistance() {
		return squareDistance;
	}

	public long getGrabDeltaX() {
		return grabDeltaX;
	}
	
	public long getGrabDeltaY() {
		return grabDeltaY;
	}

	public abstract void startDrag(long mouseX, long mouseY);

	public abstract void doDrag(Camera camera, long mouseX, long mouseY);
	
	public abstract void delete();
	
	public abstract boolean isSame(ItemGrabInfo<?> other);
	
	@Override
	public boolean equals(Object obj) {
		return isSame((ItemGrabInfo<?>) obj);
	}

	protected abstract boolean isValid();
}
