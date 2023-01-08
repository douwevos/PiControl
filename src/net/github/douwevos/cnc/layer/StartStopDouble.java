package net.github.douwevos.cnc.layer;

public class StartStopDouble {

	public final double start;
	public final double stop;

	public StartStopDouble(StartStopDouble other) {
		this.start = other.start;
		this.stop = other.stop;
	}
	
	public StartStopDouble(double start, double stop) {
		if (start<stop) {
			this.start = start;
			this.stop = stop;
		} else {
			this.start = stop;
			this.stop = start;
		}
	}

	public StartStopDouble withStart(double start) {
		if (this.start == start) {
			return this;
		}
		return new StartStopDouble(start, stop);
	}

	public StartStopDouble withStop(double stop) {
		if (this.stop == stop) {
			return this;
		}
		return new StartStopDouble(start, stop);
	}
	

	
	@Override
	public String toString() {
		return "SS[" + start + " - " + stop + "]";
	}

}
