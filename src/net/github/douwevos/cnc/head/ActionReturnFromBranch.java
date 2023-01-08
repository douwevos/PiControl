package net.github.douwevos.cnc.head;

import java.util.Objects;

import net.github.douwevos.cnc.type.Distance;

public class ActionReturnFromBranch implements CncAction {

	private long floatingHeight = -Distance.ofMillMeters(3).asMicrometers();

	private boolean hasFinished;
	
	@Override
	public void run(CncContext context) {
		CncActionQueue activeQueue = context.getActiveQueue();
		CncActionQueue parent = activeQueue.getParent();
		MicroLocation locationAtActivation = activeQueue.getLocationAtActivation();
		MicroLocation headLocation = context.getHeadLocation();
		if (locationAtActivation!=null && !Objects.equals(locationAtActivation, headLocation)) {
			parent.enqueueAction(new ActionMoveTo(locationAtActivation, CncHeadSpeed.SLOW), true);
//			context.getHeadLocation().
			MicroLocation hovering = locationAtActivation.withZ(floatingHeight);
			parent.enqueueAction(new ActionMoveTo(hovering, CncHeadSpeed.FAST), true);
		}
		
		context.setActiveQueue(parent);
		hasFinished = true;
		synchronized (this) {
			this.notifyAll();
		}
	}

	@Override
	public boolean hasFinished(CncContext context) {
		return hasFinished;
	}
	
	public synchronized void waitForFinished() {
		while(!hasFinished) {
			try {
				wait(3000);
			} catch (InterruptedException e) {
			}
		}
	}

}
