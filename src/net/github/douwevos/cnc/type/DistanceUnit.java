package net.github.douwevos.cnc.type;

public enum DistanceUnit {

	METER(1_000_000L, "m"),
	CM(10_000L, "cm"),
	MILLIMETER(1000L, "mm"),
	MICROMETER(1l, "µm");
	
	long unitInMicrometer;
	String unitName;
	
	DistanceUnit(long unitInMicrometer, String unitName) {
		this.unitInMicrometer = unitInMicrometer;
		this.unitName = unitName;
	}
	
	public long toMicroMeter(long amount) {
		return amount * unitInMicrometer;
	}
	
	public long toMilliMeter(long amount) {
		return (amount * unitInMicrometer + 499L) / 1000L;
	}
	
	public String unitName() {
		return unitName;
	}
	
	
}
