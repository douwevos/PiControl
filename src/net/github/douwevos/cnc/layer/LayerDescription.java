package net.github.douwevos.cnc.layer;

public class LayerDescription {

	private final int scalar;
	private final int width;
	private final int height;
	
	public LayerDescription(int scalar, int width, int height) {
		this.scalar = scalar;
		this.width = width;
		this.height = height;
	}
	
	
	public StartStopLayer createLayer() {
		return new StartStopLayer(0, height);
	}
	
}
