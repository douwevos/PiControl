package net.github.douwevos.cnc.head;

import douwe.Point3D;

public class MicroLocation extends Point3D {

	public MicroLocation(long x, long y, long z) {
		super(x, y, z);
	}
	
	public MicroLocation with(long x, long y, long z) {
		if (this.x==x && this.y==y && this.z==z) {
			return this;
		}
		return new MicroLocation(x, y, z);
	}

	
	public MicroLocation withX(long x) {
		return new MicroLocation(x,y,z);
	}

	public MicroLocation withY(long y) {
		return new MicroLocation(x,y,z);
	}

	public MicroLocation withZ(long z) {
		return new MicroLocation(x,y,z);
	}
	

	public static MicroLocation of(Point3D location) {
		if (location==null || location instanceof MicroLocation) {
			return (MicroLocation) location;
		}
		return new MicroLocation(location.x, location.y, location.z);
	}

	public boolean inShortRange(MicroLocation location) {
		return ((location.x==x) || (location.x==x+1) || (location.x==x-1))
				&& ((location.y==y) || (location.y==y+1) || (location.y==y-1))
				&& ((location.z==z) || (location.z==z+1) || (location.z==z-1))
				;
	}

	public boolean isNeighbour(MicroLocation location) {
		return ((location.y==y) && ((location.x==x+1) || (location.x==x-1)))
				|| ((location.x==x) && ((location.y==y+1) || (location.y==y-1)))
//				&& ((location.z==z) || (location.z==z+1) || (location.z==z-1))
				;
	}

	
	public long distanceSq(MicroLocation location) {
		long deltaX = location.x - x;
		long deltaY = location.y - y;
		long deltaZ = location.z - z;
		return deltaX*deltaX + deltaY*deltaY + deltaZ*deltaZ;
	}


}
