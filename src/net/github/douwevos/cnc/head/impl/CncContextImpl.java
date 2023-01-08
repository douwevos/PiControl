package net.github.douwevos.cnc.head.impl;

import net.github.douwevos.cnc.head.CncAction;
import net.github.douwevos.cnc.head.CncActionQueue;
import net.github.douwevos.cnc.head.CncContext;
import net.github.douwevos.cnc.head.CncHead;
import net.github.douwevos.cnc.head.CncHeadSpeed;
import net.github.douwevos.cnc.head.MicroLocation;
import net.github.douwevos.cnc.holer.SourcePiece;

public class CncContextImpl implements CncContext {
	
	private final CncHead head;
	private SourcePiece sourcePiece;
	
	private CncActionQueueImpl activeQueue = new CncActionQueueImpl(this);
	
	private MicroLocation location = new MicroLocation(0, 0, 0);
	
	public CncContextImpl(CncHead head) {
		this.head = head;
	}

	public CncAction dequeueAction() {
		CncActionQueueImpl activeQueue;
		synchronized (this) {
			activeQueue = this.activeQueue;
		}
		CncAction result = null;
		if (activeQueue==null) {
			try {
				System.err.println("no more active queue");
				Thread.sleep(2000L);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			result = activeQueue.dequeueAction();
		}
		return result;
	}
	
	public synchronized CncActionQueue getActiveQueue() {
		return activeQueue;
	}
	
	@Override
	public synchronized void setActiveQueue(CncActionQueue queue) {
		activeQueue = (CncActionQueueImpl) queue;
	}
	
	@Override
	public MicroLocation getHeadLocation() {
		return location;
	}


	
	public void moveHeadLocation(MicroLocation next, CncHeadSpeed speed) {
		head.stepTo(next, speed);
		location = next;
	}
	
	@Override
	public CncHead getHead() {
		return head;
	}
	
	
	public SourcePiece getSourcePiece() {
		return sourcePiece;
	}
	
	public void setSourcePiece(SourcePiece sourcePiece) {
		this.sourcePiece = sourcePiece;
	}
	
	@Override
	public void setActive(boolean active) {
		head.setEnabled(active);
	}
	
}