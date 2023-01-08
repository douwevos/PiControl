package douwe;

import com.pi4j.io.gpio.GpioPinDigitalOutput;

public class SliderHandler implements Runnable {

	private static final long PERIOD_NS = 155_000L;
	private volatile int requestedPos;
	private volatile int currentPos;
	private final GpioPinDigitalOutput pull;
	private final GpioPinDigitalOutput dir;
	private final boolean isBadMotor;
	
	public SliderHandler(GpioPinDigitalOutput pull, GpioPinDigitalOutput dir, boolean isBadMotor) {
		this.pull = pull;
		this.dir = dir;
		this.isBadMotor = isBadMotor;
		Thread thread = new Thread(this);
		thread.setPriority(Thread.MAX_PRIORITY);
		thread.start();
	}
	
	@Override
	public void run() {
		boolean cdir = dir.isHigh();
			while(true) {
				sleepNano(isBadMotor ? PERIOD_NS*15 : PERIOD_NS);
//				Thread.sleep(0, 1400);
				
				int rpos = requestedPos;
				if (rpos == currentPos) {
					continue;
				}
		
				boolean ndir = currentPos<rpos;
				if (ndir != cdir) {
					dir.setState(ndir);
					cdir = ndir;
				}
				
				pull.toggle();
				if (cdir) {
					currentPos++;
				} else {
					currentPos--;
				}
				synchronized (this) {
					this.notifyAll();
				}
				sleepNano(isBadMotor ? PERIOD_NS*15 : PERIOD_NS);

				pull.toggle();

//				c++;
//				if (c>5) {
//					c = 0;
//					System.out.println("requested=" + rpos + ", currentPos="+currentPos+", dir="+cdir);
//				}
				
			}
	}
	
	
	private void sleepNano(long waitNs) {
		long nanoTime = System.nanoTime()+waitNs;
		while(nanoTime>System.nanoTime()) {
		}
	}

	public void requestPosition(int value) {
		this.requestedPos = value;
	}

	public void setPosition(int value) {
		this.requestedPos = value;
		currentPos = value;
	}

	public synchronized void waitForPosition(int val) {
		while(val!=currentPos) {
			try {
				this.wait(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public int getPosition() {
		return currentPos;
	}

}
