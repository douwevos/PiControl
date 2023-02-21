package net.github.douwevos.cnc.plan;

import net.github.douwevos.cnc.head.CncHeadSpeed;
import net.github.douwevos.cnc.head.MicroLocation;
import net.github.douwevos.cnc.holer.ModelRun.RunContext;

public class CncLineTo extends CncPlanItem {

	private final MicroLocation location;
	
	public CncLineTo(MicroLocation location) {
		this.location = location;
	}
	
	public MicroLocation getLocation() {
		return location;
	}
	
	@Override
	public void runCnc(RunContext runContext) {
		runContext.lineTo(location, CncHeadSpeed.NORMAL);
	}
}
