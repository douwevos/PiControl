package net.github.douwevos.cnc.layer;

import java.util.List;

import net.github.douwevos.justflat.values.Bounds2D;


public interface Layer {

	long bottom();
	long top();
	
//	<T extends Layer> T duplicate();

	Bounds2D bounds();
	boolean testDot(long x, long y, boolean defaultValue);
	void invert(int lineIndex, List<StartStop> startStopList);
	boolean isEmpty();
	void merge(Layer layer);

	Object selectAt(double nx, double ny, double zoomFactor);
	boolean dragTo(Object selected, double nx, double ny);
	
}