package net.github.douwevos.cnc.holer.calibration;

import net.github.douwevos.cnc.head.CncActionQueue;
import net.github.douwevos.cnc.head.CncHeadService;

public class CalibrationContext {

	private final CncHeadService cncHeadService;
	
	private CncActionQueue lockQueue;
	
	public CalibrationContext(CncHeadService cncHeadService) {
		this.cncHeadService = cncHeadService;
	}

	public void dispose() {
		unlock();
	}

	
	public void lock() {
		if (lockQueue != null) {
			return;
		}
		CncActionQueue activeQueue = cncHeadService.getContext().getActiveQueue();
		lockQueue = activeQueue.branch(true);
	}
	
	public void unlock() {
		if (lockQueue!=null) {
			lockQueue.returnFromBranch(false);
			lockQueue = null;
		}
	}

	public CncActionQueue getActionQueue() {
		return lockQueue;
	}


}
