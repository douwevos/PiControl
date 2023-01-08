package net.github.douwevos.cnc.run;

import douwe.Point3D;

public interface RunInfo {

	boolean contains(Point3D p);

	boolean xInRange(long x);
	boolean yInRange(long y);
	boolean zInRange(long z);

	boolean setCut(Point3D location, boolean value);

	
//	boolean shouldCut(Point p);
//
//	boolean isCut(Point p);

}
