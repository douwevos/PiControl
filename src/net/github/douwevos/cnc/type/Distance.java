package net.github.douwevos.cnc.type;

public class Distance {

	private final long amount;
	private final DistanceUnit distanceUnit;
	
	public Distance(long amount, DistanceUnit distanceUnit) {
		this.amount = amount;
		this.distanceUnit = distanceUnit;
	}
	
	public String describe() {
		return amount + distanceUnit.unitName();
	}
	
	public long asMicrometers() {
		return distanceUnit.toMicroMeter(amount);
	}
	
	public static Distance ofMillMeters(long amount) {
		return new Distance(amount, DistanceUnit.MILLIMETER);
	}

	public static Distance ofMicroMeters(long amount) {
		return new Distance(amount, DistanceUnit.MICROMETER);
	}
}
