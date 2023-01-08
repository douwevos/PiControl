package net.github.douwevos.cnc.holer;

import java.awt.Graphics2D;

import net.github.douwevos.cnc.type.Distance;

public class SourcePiece {

	private final Distance width;
	private final Distance height;
	private final Distance depth;
	
	public SourcePiece(Distance width, Distance height, Distance depth) {
		this.width = width;
		this.height = height;
		this.depth = depth;
	}

	public Distance getWidth() {
		return width;
	}
	
	public Distance getHeight() {
		return height;
	}
	
	public Distance getDepth() {
		return depth;
	}

	public void paint(Graphics2D gfx) {
		gfx.drawRect(0, 0, (int) width.asMicrometers(), (int) height.asMicrometers());
	}
	
}
