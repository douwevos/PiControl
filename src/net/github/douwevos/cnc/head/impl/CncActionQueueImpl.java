package net.github.douwevos.cnc.head.impl;

import java.util.ArrayList;
import java.util.List;

import net.github.douwevos.cnc.head.ActionBranch;
import net.github.douwevos.cnc.head.ActionMoveTo;
import net.github.douwevos.cnc.head.ActionReturnFromBranch;
import net.github.douwevos.cnc.head.CncAction;
import net.github.douwevos.cnc.head.CncActionQueue;
import net.github.douwevos.cnc.head.CncHeadSpeed;
import net.github.douwevos.cnc.head.MicroLocation;

public class CncActionQueueImpl implements CncActionQueue {
	
	private final CncContextImpl context;
	private final CncActionQueueImpl parent;
	
	private final List<CncAction> queue = new ArrayList<>();
	
	private MicroLocation locationAtActivation;
	
	
	public CncActionQueueImpl(CncContextImpl context) {
		this.context = context;
		this.parent = null;
	}

	private CncActionQueueImpl(CncContextImpl context, CncActionQueueImpl parent) {
		this.context = context;
		this.parent = parent;
	}

	@Override
	public void activate() {
		if (parent != null) {
			locationAtActivation = context.getHeadLocation();
		}
		context.setActiveQueue(this);
	}
	
	@Override
	public MicroLocation getLocationAtActivation() {
		return locationAtActivation;
	}
	
	public synchronized CncAction dequeueAction() {
		while(true) {
			if (queue.isEmpty()) {
				try {
					this.wait(100);
				} catch (InterruptedException e) {
				}
				return null;
			} else {
				CncAction action = queue.get(0);
				if (action.hasFinished(context)) {
					queue.remove(0);
					notifyAll();
				} else {
					return action;
				}
			}
		}
	}
	
	@Override
	public CncActionQueue getParent() {
		return parent;
	}
	
	@Override
	public void lineTo(MicroLocation location, CncHeadSpeed speed) {
		ActionMoveTo moveTo = new ActionMoveTo(location, speed);
		enqueueAction(moveTo, false);
	}

	@Override
	public ActionMoveTo resetTo(MicroLocation location, CncHeadSpeed speed) {
		ActionMoveTo moveTo = new ActionMoveTo(location, speed);
		synchronized (this) {
			queue.clear();
			queue.add(moveTo);
			notifyAll();
		}
		return moveTo;
	}

	@Override
	public CncActionQueue branch(boolean atFront) {
		CncActionQueueImpl branchedQueue = new CncActionQueueImpl(context, this);
		ActionBranch action = new ActionBranch(branchedQueue);
		enqueueAction(action, atFront);
		return branchedQueue;
	}
	
	
	@Override
	public ActionReturnFromBranch returnFromBranch(boolean waitFor) {
		ActionReturnFromBranch action = new ActionReturnFromBranch();
		enqueueAction(action, false);
		if (waitFor) {
			action.waitForFinished();
		}
		return action;
	}

	
	@Override
	public synchronized void enqueueAction(CncAction action, boolean atFront) {
		if (atFront) {
			queue.add(0, action);
		} 
		else {
			if(queue.size()>1500) {
				try {
					wait(50);
				} catch (InterruptedException e) {
				}
			}
			queue.add(action);
			
		}
		notifyAll();
	}
	
	
	
}