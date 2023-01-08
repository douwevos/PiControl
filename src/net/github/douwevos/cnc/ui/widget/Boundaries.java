package net.github.douwevos.cnc.ui.widget;

public class Boundaries {

	public final int x;
	public final int y;
	public final int width;
	public final int height;
	
	public Boundaries(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public Boundaries withHeight(int height) {
		return new Boundaries(x, y, width, height);
	}

	public Boundaries withSize(int width, int height) {
		return new Boundaries(x, y, width, height);
	}

	@Override
	public String toString() {
		return "Boundaries[x=" + x + ", y=" + y + ", width=" + width + ", height=" + height + "]";
	}

	public boolean contains(int x, int y) {
		return x>=this.x && y>=this.y && x<this.x+width && y<this.y+height;
	}
}
