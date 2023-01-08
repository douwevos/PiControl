package net.github.douwevos.cnc.head.impl;

public class SleepPeriod {

	private final long millis;
	private final int nanos;
	
	public SleepPeriod(String txt) {
		String replace = txt.replace(".", "");
		if (replace.length()>6) {
			millis = Long.parseLong(replace.substring(0, replace.length()-6));
			nanos = Integer.parseInt(replace.substring(replace.length()-6));
		} 
		else {
			millis = 0;
			nanos = Integer.parseInt(replace);
		}
	}

	
	
	public boolean isSmallerThen(SleepPeriod other) {
		return millis<other.millis ||  (millis==other.millis && nanos<other.nanos);
	}

	public void doSleep() {
		if (millis<=0) {
			long endNs = System.nanoTime()+nanos;
			while(System.nanoTime()<endNs) {}
			return;
		}
		try {
			Thread.sleep(millis, nanos);
		} catch (InterruptedException e) {
		}
	}
	
}
