package net.github.douwevos.cnc.head.impl;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

import net.github.douwevos.cnc.head.CncConfiguration;
import net.github.douwevos.cnc.head.CncHead;
import net.github.douwevos.cnc.head.CncHeadSpeed;
import net.github.douwevos.cnc.head.CncLocation;
import net.github.douwevos.cnc.head.MicroLocation;

public class PiCncHead implements CncHead {

	private final CncConfiguration configuration;
	private final GpioPinDigitalOutput pinLRPull;
	private final GpioPinDigitalOutput pinLRDir;
	private final GpioPinDigitalOutput pinUDPull;
	private final GpioPinDigitalOutput pinUDDir;
	private final GpioPinDigitalOutput pinBFPull;
	private final GpioPinDigitalOutput pinBFDir;

	private final GpioPinDigitalOutput pinActive;
	
	private boolean lrDir;
	private boolean udDir;
	private boolean bfDir;
	
	private SleepPeriod lrSleepFactor = new SleepPeriod("35.000");
	private SleepPeriod udSleepFactor =  new SleepPeriod("350.000");;
	private SleepPeriod bfSleepFactor =  new SleepPeriod("65.000");;
	
	private CncLocation location;

	
	public PiCncHead(CncConfiguration configuration) {
		this.configuration = configuration;
		final GpioController gpio = GpioFactory.getInstance();

		// GPIO_00   "GPIO 17"
		// GPIO_01   "GPIO 18"
		// GPIO_02   "GPIO 22"
		// GPIO_03   "GPIO 27"
		
		GpioPinDigitalOutput pinA = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, "PullX", PinState.LOW);
		GpioPinDigitalOutput pinA1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "DirectionX", PinState.LOW);
		GpioPinDigitalOutput pinB = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, "PullZ", PinState.LOW);
		GpioPinDigitalOutput pinB1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, "DirectionZ", PinState.HIGH);
		GpioPinDigitalOutput pinC = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, "PullY", PinState.LOW);
		GpioPinDigitalOutput pinC1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05, "DirectionY", PinState.LOW);
		this.pinLRPull = pinA;
		this.pinLRDir = pinA1;
		this.pinBFPull = pinC;
		this.pinBFDir = pinC1;
		this.pinUDPull = pinB;
		this.pinUDDir = pinB1;
		lrDir = pinLRDir.isHigh();
		udDir = pinUDDir.isHigh();
		bfDir = pinBFDir.isHigh();
		location = new CncLocation(0, 0, 0);
		
		this.pinActive = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_06, "Active", PinState.HIGH);
		pinActive.setPullResistance(PinPullResistance.PULL_DOWN);

	}
	
	
	@Override
	public void setEnabled(boolean enabled) {
		pinActive.setState(enabled);
		
	}
	
	@Override
	public void stepTo(MicroLocation microLocation, CncHeadSpeed speed) {
		CncLocation next = configuration.toCncLocation(microLocation);
		long lx = location.x;
		long ly = location.y;
		long lz = location.z;
		
		if (lx==next.x && ly==next.y && lz==next.z) {
			return;
		}
		SleepPeriod minimalSleep = new SleepPeriod("10.000");
		
		if (location.x != next.x) {
			if (minimalSleep.isSmallerThen(lrSleepFactor)) {
				minimalSleep = lrSleepFactor;
			}
			boolean xDir;
			if (lx<next.x) {
				xDir = true;
				lx++;
			} else {
				xDir = false;
				lx--;
			}
			if (xDir != lrDir) {
				pinLRDir.setState(xDir);
				lrDir = xDir;
			} 
			
			pinLRPull.toggle();
		}

		if (location.y != next.y) {
			if (minimalSleep.isSmallerThen(bfSleepFactor)) {
				minimalSleep = bfSleepFactor;
			}
			boolean yDir;
			if (ly<next.y) {
				yDir = true;
				ly++;
			} else {
				yDir = false;
				ly--;
			}
			if (yDir != bfDir) {
				pinBFDir.setState(yDir);
				bfDir = yDir;
			} 
			
			pinBFPull.toggle();
		}

		if (location.z != next.z) {
			if (minimalSleep.isSmallerThen(udSleepFactor)) {
				minimalSleep = udSleepFactor;
			}
			boolean zDir;
			if (lz<next.z) {
				zDir = true;
				lz++;
			} else {
				zDir = false;
				lz--;
			}
			if (zDir != udDir) {
				pinUDDir.setState(zDir);
				udDir = zDir;
			} 
			
			pinUDPull.toggle();
		}

		configuration.sleep(speed, minimalSleep);
		
		if (lx != location.x) {
			pinLRPull.toggle();
		}
		if (ly != location.y) {
			pinBFPull.toggle();
		}
		if (lz != location.z) {
			pinUDPull.toggle();
		}

		configuration.sleep(speed, minimalSleep);
		
		location = new CncLocation(lx, ly, lz);
		
	}

	@Override
	public CncLocation getLocation() {
		return location;
	}
	
	@Override
	public CncLocation toCncLocation(MicroLocation microLocation) {
		return configuration.toCncLocation(microLocation);
	}

	
	@Override
	public MicroLocation toMicroLocation(CncLocation cncLocation) {
		return configuration.toMicroLocation(cncLocation);
	}
	
}
