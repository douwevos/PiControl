package net.github.douwevos.cnc.plan;

import net.github.douwevos.cnc.holer.ModelRun.RunContext;

public abstract class CncPlanItem {

	public abstract void runCnc(RunContext runContext);

}
