package net.github.douwevos.cnc.head.impl;

import net.github.douwevos.cnc.head.CncConfiguration;
import net.github.douwevos.cnc.head.CncHeadSpeed;
import net.github.douwevos.cnc.head.CncLocation;
import net.github.douwevos.cnc.head.MicroLocation;

public class CncConfigurationImpl implements CncConfiguration {

	private final double lrDotsPerMicro = 100d/317;
	private final double udDotsPerMicro = 10d/50;
	private final double bfDotsPerMicro = 100d/317;
	

	MaterialSpeed materialSpeed = MaterialSpeed.SOFT_WOOD2;
	
	
	
	
	
	
	@Override
	public void sleep(CncHeadSpeed speed, SleepPeriod minimalSleep) {
		switch(speed) {
			case FAST : sleep(minimalSleep, materialSpeed.fast); break;
			case NORMAL : sleep(minimalSleep, materialSpeed.normal); break;
			case SLOW : sleep(minimalSleep, materialSpeed.slow); break;
			case VERY_SLOW : sleep(minimalSleep, materialSpeed.verySlow); break;
		}
	}
	
	long lastMs=-1;
	int lastNs=-1;
	

	private void sleep(SleepPeriod minimalSleep, SleepPeriod sleepPeriod) {
		SleepPeriod use = sleepPeriod;
		if (sleepPeriod.isSmallerThen(minimalSleep)) {
			use = minimalSleep;
		}
		use.doSleep();
	}

	@Override
	public CncLocation toCncLocation(MicroLocation location) {
		if (location == null) {
			return null;
		}
		
		long cncX = Math.round(location.x * lrDotsPerMicro);
		long cncY = Math.round(location.y * bfDotsPerMicro);
		long cncZ = Math.round(location.z * udDotsPerMicro);
		return new CncLocation(cncX, cncY, cncZ);
	}

	@Override
	public MicroLocation toMicroLocation(CncLocation location) {
		if (location == null) {
			return null;
		}
		
		long cncX = Math.round(location.x / lrDotsPerMicro);
		long cncY = Math.round(location.y / bfDotsPerMicro);
		long cncZ = Math.round(location.z / udDotsPerMicro);
		return new MicroLocation(cncX, cncY, cncZ);
	}
	
	
	@Override
	public long getMaxDrop() {
////		return 175; // ALU
////		return 250; // wood
////		return 2500; // soft_wood
//		return 900; // siebdruck-platte
		return materialSpeed.maxDropSpeed;
	}

	
	enum MaterialSpeed {
		FAST_MOCK("5000", "5000", "5000", "5000", 3000),
		IRON("450.000", "4.250.000", "4.250.000", "15.000.000", 75),
		ALU("350.000", "3.250.000", "3.250.000", "15.000.000", 175),
		SOFT_WOOD("85.000", "750.000", "1.250.000", "4.000.000", 1500),
		SOFT_WOOD2("85.000", "900.000", "1.450.000", "4.000.000", 1200),
		HARD_WOOD("85.000", "1.400.000", "1.850.000", "2.500.000", 900),

		VERY_SLOW("135.000", "1.750.000", "2.050.000", "3.000.000", 100),

		FAST_WOOD("55.000", "650.000", "0.900.000", "2.000.000", 10),

		JUST_FAST("85.000", "120.000", "120.000", "4.000.000", 200),

		;
		
		SleepPeriod fast;
		SleepPeriod normal;
		SleepPeriod slow;
		SleepPeriod verySlow;
		long maxDropSpeed;

		MaterialSpeed(String fast, String normal, String slow, String verySlow, long maxDropSpeed) {
			this.fast = new SleepPeriod(fast);
			this.normal = new SleepPeriod(normal);
			this.slow = new SleepPeriod(slow);
			this.verySlow = new SleepPeriod(verySlow);
			this.maxDropSpeed = maxDropSpeed;
		}
		
	}


	
}
