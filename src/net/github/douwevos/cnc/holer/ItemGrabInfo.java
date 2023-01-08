package net.github.douwevos.cnc.holer;

public abstract class ItemGrabInfo {

	protected long grabDeltaX;
	protected long grabDeltaY;
	protected long squareDistance;
	
	public ItemGrabInfo(long distSq) {
		this.squareDistance = distSq;
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

	public abstract void doDrag(long mouseX, long mouseY);
	
	public abstract void delete();
}
