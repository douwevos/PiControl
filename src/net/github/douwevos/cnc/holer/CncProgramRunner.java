//package net.github.douwevos.cnc.holer;
//
//import java.util.concurrent.CopyOnWriteArrayList;
//
//import net.github.douwevos.cnc.head.CncConfiguration;
//import net.github.douwevos.cnc.head.CncHeadService;
//
//public class CncProgramRunner implements Runnable {
//
//	private final CncConfiguration configuration;
//	private final CncHeadService cncHeadService;
//	private HolerModel holerModelRunning;
//	private volatile HolerModelRun holerModelRun;
//	private CopyOnWriteArrayList<Listener> listeners = new CopyOnWriteArrayList<>();
//	
//	public CncProgramRunner(CncConfiguration configuration, CncHeadService cncHeadService) {
//		this.configuration = configuration;
//		this.cncHeadService = cncHeadService;
//		Thread thread = new Thread(this);
//		thread.start();
//	}
//
//	public synchronized void start(HolerModel holerModel) {
//		if (holerModelRunning == null) {
//			holerModelRunning = holerModel;
//			notifyAll();
//		}
//	}
//
//	public void nextItem() {
//		HolerModelRun lcHolerModelRun = holerModelRun;
//		if (lcHolerModelRun != null) {
//			lcHolerModelRun.nextItem();
//		}
//	}
//
//	
//	
//	@Override
//	public void run() {
//		
//		while(true) {
//			HolerModel model = waitForHolerModel();
//			if (model==null) {
//				continue;
//			}
//			holerModelRun = new HolerModelRun(configuration, model, cncHeadService);
//			notifyNewModelRunning(holerModelRun);
//			holerModelRun.run();
//			finishedRun();
//			holerModelRun = null;
//			notifyNewModelRunning(null);			
//		}
//	}
//
//	private synchronized void finishedRun() {
//		holerModelRunning = null;
//	}
//
//	private synchronized HolerModel waitForHolerModel() {
//		try {
//			wait(2000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return holerModelRunning;
//	}
//
//	public void addListener(Listener listener) {
//		listener.onNewModelRun(holerModelRun);
//		listeners.add(listener);
//	}
//	
//
//	public void removeListener(Listener listener) {
//		listeners.remove(listener);
//	}
//	
//	private void notifyNewModelRunning(HolerModelRun run) {
//		for(Listener listener : listeners) {
//			listener.onNewModelRun(run);
//		}
//	}
//	
//
//	
//	public interface Listener {
//		public void onNewModelRun(HolerModelRun run);
//	}
//
//	
//}
