package net.github.douwevos.cnc.holer;

import net.github.douwevos.cnc.head.CncConfiguration;
import net.github.douwevos.cnc.head.CncHeadService;
import net.github.douwevos.cnc.model.value.Model;

public class NewCncProgramRunner implements Runnable {

	private final CncConfiguration configuration;
	private final CncHeadService cncHeadService;
	private Model modelRunning;
	private volatile ModelRun modelRun;

	
	public NewCncProgramRunner(CncConfiguration configuration, CncHeadService cncHeadService) {
		this.configuration = configuration;
		this.cncHeadService = cncHeadService;
		Thread thread = new Thread(this);
		thread.start();
	}

	public synchronized void start(Model model) {
		if (modelRunning == null) {
			modelRunning = model;
			notifyAll();
		}
	}

	
	@Override
	public void run() {
		while(true) {
			Model model = waitForModel();
			if (model==null) {
				continue;
			}
			modelRun = new ModelRun(configuration, cncHeadService, model);
//			notifyNewModelRunning(holerModelRun);
			modelRun.run();
//			finishedRun();
			modelRun = null;
//			notifyNewModelRunning(null);			
		}
	}

	private synchronized Model waitForModel() {
		try {
			wait(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return modelRunning;
	}

}
