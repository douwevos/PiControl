package douwe;

import java.awt.Container;

import javax.swing.JFrame;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class Main {

	public static void main(String[] args) throws InterruptedException {

		JFrame frame = new JFrame();
		frame.setBounds(0, 0, 2000, 900);
		Container contentPane = frame.getContentPane();
		MyControlPanel panel = new MyControlPanel();
		contentPane.add(panel);
		frame.setVisible(true);

		// create gpio controller
		final GpioController gpio = GpioFactory.getInstance();


		// GPIO_00   "GPIO 17"
		// GPIO_01   "GPIO 18"
		// GPIO_02   "GPIO 22"
		// GPIO_03   "GPIO 27"
		
		GpioPinDigitalOutput pinA = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, "Pull1", PinState.LOW);
		GpioPinDigitalOutput pinA1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "Direction1", PinState.LOW);
		GpioPinDigitalOutput pinB = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, "MyInput", PinState.LOW);
		GpioPinDigitalOutput pinB1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, "Direction2", PinState.LOW);
		GpioPinDigitalOutput pinC = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, "PullY", PinState.LOW);
		GpioPinDigitalOutput pinC1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05, "DirectionY", PinState.LOW);


//		
		SliderHandler sliderHandler = new SliderHandler(pinA, pinA1, false);
		SliderHandler sliderHandler2 = new SliderHandler(pinB, pinB1, false);
		SliderHandler sliderHandler3 = new SliderHandler(pinC, pinC1, false);
		
		panel.setLeftRightSliderHandler(sliderHandler);
		panel.setUpDownSliderHandler(sliderHandler2);
		panel.setbackwardForwardSliderHandler(sliderHandler3);
//		
		
		for(int idx=0; idx<10000000; idx++) {
			Thread.sleep(2000L);
//			panel.rot();
			
		}
		
		

		// stop all GPIO activity/threads by shutting down the GPIO controller
		// (this method will forcefully shutdown all GPIO monitoring threads and
		// scheduled tasks)
//		gpio.shutdown();
	}


	
	public static void s(int s) {
		try {
			Thread.sleep(s);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
