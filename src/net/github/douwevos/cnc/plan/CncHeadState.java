package net.github.douwevos.cnc.plan;

import net.github.douwevos.cnc.holer.ModelRun.RunContext;

public class CncHeadState extends CncPlanItem {

	public final boolean headUp;
	
	public CncHeadState(boolean headUp) {
		this.headUp = headUp;
	}
	
	
	@Override
	public void runCnc(RunContext runContext) {
		runContext.setFloating(headUp);
	}
}
