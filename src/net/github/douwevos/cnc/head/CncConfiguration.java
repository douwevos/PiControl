package net.github.douwevos.cnc.head;

import net.github.douwevos.cnc.head.impl.SleepPeriod;

public interface CncConfiguration {

	void sleep(CncHeadSpeed speed, SleepPeriod minimalSleep);
	
	CncLocation toCncLocation(MicroLocation location);
	MicroLocation toMicroLocation(CncLocation location);
	
	long getMaxDrop();
}
