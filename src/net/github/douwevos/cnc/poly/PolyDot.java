package net.github.douwevos.cnc.poly;

public class PolyDot {

	public final long x;
	public final long y;
	public final boolean isCurve;
	
	public PolyDot(long x, long y, boolean isCurve) {
		this.x = x;
		this.y = y;
		this.isCurve = isCurve;
	}
	
	public PolyDot withLocation(long x, long y) {
		return new PolyDot(x, y, isCurve);
	}

}
