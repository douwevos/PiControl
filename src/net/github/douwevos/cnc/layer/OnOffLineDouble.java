package net.github.douwevos.cnc.layer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OnOffLineDouble {

	List<OnOffDoubleDot> rawDots;

	public OnOffLineDouble duplicate() {
		OnOffLineDouble copy = new OnOffLineDouble();
		copy.rawDots = rawDots == null ? null : new ArrayList<>(rawDots);
		return copy;
	}

	public void reset() {
		if (rawDots != null) {
			rawDots.clear();
		}
	}

	public List<StartStopDouble> apply() {
		if (rawDots == null || rawDots.isEmpty()) {
			return null;
		}
		List<StartStopDouble> shapeStartStop = new ArrayList<>();

		OnOffDoubleDot last = rawDots.get(0);
		OnOffDoubleDot start = last;
		OnOffDoubleDot stop = null;
		for (int in = 1; in < rawDots.size(); in++) {
			OnOffDoubleDot onOffDot = rawDots.get(in);

			if (onOffDot.up == last.up) {
				if (stop != null) {
					stop = onOffDot;
				}
			} else {
				if (stop == null) {
					stop = onOffDot;
				} else {
					shapeStartStop.add(new StartStopDouble(start.x, stop.x));
					stop = null;
					start = onOffDot;
				}
			}
			last = onOffDot;
		}
		if (stop != null) {
			shapeStartStop.add(new StartStopDouble(start.x, stop.x));
		}

		double offsets[] = new double[shapeStartStop.size() * 2];
		int ou = 0;
		for (StartStopDouble ss : shapeStartStop) {
			offsets[ou++] = ss.start;
			offsets[ou++] = ss.stop;
		}
		shapeStartStop.clear();
		Arrays.sort(offsets);
		for (ou = 0; ou < offsets.length; ou += 2) {
			shapeStartStop.add(new StartStopDouble(offsets[ou], offsets[ou + 1]));
		}

//			shapeStartStop.sort((a,b) -> {
//				return a.start<b.start ? -1 : 1;
//			});

//			System.out.println("rawDots="+rawDots);
//			System.out.println("shapeStartStop="+shapeStartStop);

		return shapeStartStop;
	}

	public void add(double pos, boolean up) {
		if (rawDots == null) {
			rawDots = new ArrayList<>();
		}
		rawDots.add(new OnOffDoubleDot(pos, up));
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		for (OnOffDoubleDot dot : rawDots) {
			if (s.length() > 0) {
				s.append(',');
			}
			s.append(dot.x).append(dot.up ? 'u' : 'd');

		}
		return "Line[" + s + "]";
	}

	static class OnOffDoubleDot {
		public final double x;
		public final boolean up;
		public OnOffDoubleDot(double x, boolean up) {
			this.x = x;
			this.up = up;
			
		}
		public OnOffDoubleDot withX(double newX) {
			if (x==newX) {
				return this;
			}
			return new OnOffDoubleDot(newX, up);
		}
		@Override
		public String toString() {
			return "OnOffDot[x=" + x + (up ? 'u' : 'd') + "]";
		}
	}

	
}