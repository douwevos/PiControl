package net.github.douwevos.cnc.run;

import java.util.ArrayList;
import java.util.List;

import net.github.douwevos.cnc.head.MicroLocation;

public class Chain {
	public final List<MicroLocation> locations = new ArrayList<>();

	private boolean circular;
	
	public Chain(MicroLocation location) {
		locations.add(location);
	}

	public void concat(Chain second) {
		locations.addAll(second.locations);
		
	}

	public MicroLocation getFirstLocation() {
		return locations.get(0);
	}

	public MicroLocation getLastLocation() {
		return locations.get(locations.size()-1);
	}

	public void add(MicroLocation location) {
		locations.add(location);
	}
	
	public void setIsCircular(boolean circular) {
		this.circular = circular;
	}
	
	public boolean isCircular() {
		return circular;
	}
}