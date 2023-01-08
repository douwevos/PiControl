package net.github.douwevos.cnc.run;

import java.util.ArrayList;
import java.util.List;

import net.github.douwevos.cnc.head.MicroLocation;

public class LocationList {
	List<MicroLocation> locations = new ArrayList<>();

	public void add(MicroLocation location) {
		locations.add(location);
	}

	public boolean isEmpty() {
		return locations.isEmpty();
	}

	public MicroLocation removeLast() {
		return locations.isEmpty() ? null : locations.remove(locations.size() - 1);
	}

	public MicroLocation getLast() {
		return locations.isEmpty() ? null : locations.get(locations.size() - 1);
	}

	public MicroLocation scanCloseToX(long x) {
		MicroLocation result = null;
		long nextX = x + 1;
		long prevX = x - 1;
		for (MicroLocation location : locations) {
			long locX = location.x;
			if (locX == x) {
				result = location;
				break;
			}
//				if (locX>nextX) {
//					break;
//				}
			if ((locX == nextX) || locX == prevX) {
				result = location;
			}
		}
		return result;
	}

	public void remove(MicroLocation location) {
		locations.remove(location);
	}
}