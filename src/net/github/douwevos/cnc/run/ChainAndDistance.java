package net.github.douwevos.cnc.run;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Stream;

import net.github.douwevos.cnc.head.MicroLocation;

public class ChainAndDistance {
	public final Chain chain;
	public final boolean forward;
	public final long distSq;
	public ChainAndDistance(Chain chain, boolean forward, long distSq) {
		this.chain = chain;
		this.forward = forward;
		this.distSq = distSq;
	}
	public Stream<MicroLocation> stream() {
		if (!forward) {
			ArrayList<MicroLocation> arrayList = new ArrayList<>(chain.locations);
			Collections.reverse(arrayList);
			return arrayList.stream();
		}
		return chain.locations.stream();
	}
	public MicroLocation getTail() {
		return forward ? chain.getFirstLocation() : chain.getLastLocation();
	}
}