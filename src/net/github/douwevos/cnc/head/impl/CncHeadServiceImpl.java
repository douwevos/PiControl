package net.github.douwevos.cnc.head.impl;

import net.github.douwevos.cnc.head.CncAction;
import net.github.douwevos.cnc.head.CncContext;
import net.github.douwevos.cnc.head.CncHead;
import net.github.douwevos.cnc.head.CncHeadService;

public class CncHeadServiceImpl implements Runnable, CncHeadService {

	private CncContextImpl context;
	
	
	public CncHeadServiceImpl(CncHead head) {
		context = new CncContextImpl(head);
		Thread thread = new Thread(this);
		thread.setPriority(Thread.MAX_PRIORITY);
		thread.start();
	}
	
	
	@Override
	public void run() {
		while(true) {
			CncAction cncAction = context.dequeueAction();
			
			if (cncAction != null) {
				context.setActive(true);
				cncAction.run(context);
			} else {
				context.setActive(false);
			}
		}
	}

	@Override
	public CncContext getContext() {
		return context;
	}
	
}
