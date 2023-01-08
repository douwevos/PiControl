package net.github.douwevos.cnc.head.impl;

import net.github.douwevos.cnc.head.CncConfiguration;
import net.github.douwevos.cnc.head.CncHead;
import net.github.douwevos.cnc.head.CncHeadSpeed;
import net.github.douwevos.cnc.head.CncLocation;
import net.github.douwevos.cnc.head.MicroLocation;

public class MockCncHead implements CncHead {

	private final CncConfiguration configuration;
	
	private boolean lrDir;
	private boolean udDir;
	private boolean bfDir;
	
	private CncLocation location;

	
	public MockCncHead(CncConfiguration configuration) {
		this.configuration = configuration;

		// GPIO_00   "GPIO 17"
		// GPIO_01   "GPIO 18"
		// GPIO_02   "GPIO 22"
		// GPIO_03   "GPIO 27"
		
		location = new CncLocation(0, 0, 0);
	}
	
	@Override
	public void stepTo(MicroLocation microLocation, CncHeadSpeed speed) {
		CncLocation next = configuration.toCncLocation(microLocation);
		long lx = location.x;
		long ly = location.y;
		long lz = location.z;
		
		if (lx==next.x && ly==next.y && lz==next.z) {
			return;
		}
		
		if (location.x != next.x) {
			boolean xDir;
			if (lx<next.x) {
				xDir = true;
				lx++;
			} else {
				xDir = false;
				lx--;
			}
			if (xDir != lrDir) {
				lrDir = xDir;
			} 
			microSleep();
		}

		if (location.y != next.y) {
			boolean yDir;
			if (ly<next.y) {
				yDir = true;
				ly++;
			} else {
				yDir = false;
				ly--;
			}
			if (yDir != bfDir) {
				bfDir = yDir;
			} 
			
			microSleep();
		}

		if (location.z != next.z) {
			boolean zDir;
			if (lz<next.z) {
				zDir = true;
				lz++;
			} else {
				zDir = false;
				lz--;
			}
			if (zDir != udDir) {
				udDir = zDir;
			} 
			
			microSleep();
		}

		SleepPeriod minimalSleep = new SleepPeriod("30.000");

		configuration.sleep(speed, minimalSleep);
		
		if (lx != location.x) {
			microSleep();
		}
		if (ly != location.y) {
			microSleep();
		}
		if (lz != location.z) {
			microSleep();
		}

		configuration.sleep(speed, minimalSleep);
		
		location = new CncLocation(lx, ly, lz);
		
	}

	private void microSleep() {
//		try {
//			Thread.sleep(0, 500);
//		} catch (InterruptedException e) {
//		}
	}

	@Override
	public CncLocation getLocation() {
		return location;
	}
	
	@Override
	public CncLocation toCncLocation(MicroLocation microLocation) {
		return configuration.toCncLocation(microLocation);
	}

	
	@Override
	public MicroLocation toMicroLocation(CncLocation cncLocation) {
		return configuration.toMicroLocation(cncLocation);
	}

	
	@Override
	public void setEnabled(boolean enabled) {
		
	}
}
