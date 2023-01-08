package douwe;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import douwe.design.DesignPanel;
import douwe.design.Model;

public class MyControlPanel extends JPanel {

	
	Executor executor = Executors.newFixedThreadPool(1); 
	
	ValueController leftRightValueController;
	ValueController upDownValueController;
	ValueController backwardForwardValueController;
	
	
	DesignPanel designPanel;
	
	public MyControlPanel() {

		GridBagLayout gridBagLayout = new GridBagLayout();
		setLayout(gridBagLayout);
		
		leftRightValueController = new ValueController(this, gridBagLayout, 0, -75000, 75000);
		upDownValueController = new ValueController(this, gridBagLayout, 1, -20000, 20000);
		backwardForwardValueController = new ValueController(this, gridBagLayout, 2, -75000, 75000);


		AbstractAction actionRun = new AbstractAction("run") {
			public void actionPerformed(ActionEvent e) {
				Program program = new Program(upDownValueController.sliderHandler, leftRightValueController.sliderHandler, backwardForwardValueController.sliderHandler);
				executor.execute(program);
			}
		};
		
		JButton butLaunchProgram = new JButton(actionRun);
		GridBagConstraints constraint2 = new GridBagConstraints(0, 3, 2, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0);
		add(butLaunchProgram, constraint2);
		
		Model model = new Model();
		
		designPanel = new DesignPanel(model);
		GridBagConstraints constraint = new GridBagConstraints(0, 4, 4, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0);
		add(designPanel, constraint);
		
	}


	public void setLeftRightSliderHandler(SliderHandler sliderHandler) {
		leftRightValueController.setSliderHandler(sliderHandler);
	}

	public void setUpDownSliderHandler(SliderHandler sliderHandler) {
		upDownValueController.setSliderHandler(sliderHandler);
	}

	public void setbackwardForwardSliderHandler(SliderHandler sliderHandler) {
		backwardForwardValueController.setSliderHandler(sliderHandler);
	}

	
	static class ValueController implements ChangeListener {
		protected static final int STEP = 10;
		SliderHandler sliderHandler;
		JSlider requestedLocation;
		JTextField fldValue;
		
		
		public ValueController(Container container, GridBagLayout gridBagLayout, int row, int min, int max) {

			AbstractAction actionMinus = new AbstractAction("-") {
				public void actionPerformed(ActionEvent e) {
					int value = requestedLocation.getValue();
					requestedLocation.setValue(value-STEP);
				}
			};
			
			JButton lrButMinus = new JButton(actionMinus);
			container.add(lrButMinus);
			GridBagConstraints constraint = new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0);
			gridBagLayout.addLayoutComponent(lrButMinus, constraint);

			
			requestedLocation = new JSlider(JSlider.HORIZONTAL, min, max, (max+min)/2 /*, 100000, 50000*/);
			
			requestedLocation.addChangeListener(this);
			
			//Turn on labels at major tick marks.
			requestedLocation.setMajorTickSpacing(10);
			requestedLocation.setMinorTickSpacing(1);
			requestedLocation.setPaintTicks(true);
			requestedLocation.setPaintLabels(false);
			
			container.add(requestedLocation);
			
			constraint = new GridBagConstraints(1, row, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0);
			gridBagLayout.addLayoutComponent(requestedLocation, constraint);

			
			AbstractAction actionAdd = new AbstractAction("+") {
				public void actionPerformed(ActionEvent e) {
					int value = requestedLocation.getValue();
					requestedLocation.setValue(value+STEP);
				}
			};
			
			JButton lrButAdd = new JButton(actionAdd);
			container.add(lrButAdd);
			constraint = new GridBagConstraints(2, row, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0);
			gridBagLayout.addLayoutComponent(lrButAdd, constraint);
			
			

			fldValue = new JTextField(8);
			container.add(fldValue);
			constraint = new GridBagConstraints(3, row, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0);
			gridBagLayout.addLayoutComponent(fldValue, constraint);
			
			stateChanged(null);
		}
		

		@Override
		public void stateChanged(ChangeEvent e) {
			int value = requestedLocation.getValue();
			if (sliderHandler != null) {
				sliderHandler.requestPosition(value);
			}
			fldValue.setText(""+requestedLocation.getValue());
		}
		
		public void setSliderHandler(SliderHandler sliderHandler) {
			this.sliderHandler = sliderHandler;
			sliderHandler.setPosition(requestedLocation.getValue());
		}
		
	}
	
	static class Program implements Runnable {


		Line line;
		
		private final SliderHandler shUpDown;
		private final SliderHandler shLeftRight;
		private final SliderHandler shBackForward;
		
		public Program(SliderHandler shUpDown, SliderHandler shLeftRight, SliderHandler shBackForward) {
			this.shUpDown = shUpDown;
			this.shLeftRight = shLeftRight;
			this.shBackForward = shBackForward;
			Point p0 = new Point(0,0,0);
			Point p1 = new Point(1500, 1500, 0);
			line = new Line(p0, p1);
		}
		
		@Override
		public void run() {
			
			Point current = new Point();
			
			current = runLine(current, line);
			
			System.out.println("program done");
		}

		private Point runLine(Point current, Line line) {
			Point a = line.a;
			Point b = line.b;
			
			long dx = b.x - a.x;
			long dy = b.y - a.y;
			long dz = b.z - a.z;
			
			int length = (int) Math.ceil(Math.sqrt(dx*dx + dy*dy + dz*dz));
			length = length * 2;
			
			double qx = ((double) dx/length);
			double qy = ((double) dy/length);
			double qz = ((double) dz/length);

			shUpDown.requestPosition((int) current.z);
			shLeftRight.requestPosition((int) current.x);
			shBackForward.requestPosition((int) current.y);

			
			for(int pos=0; pos<length; pos++) {
				long nx = a.x + (long) Math.round(qx*pos);
				long ny = a.y + (long) Math.round(qy*pos);
				long nz = a.z + (long) Math.round(qz*pos);
				Point newPoint = new Point(nx, ny, nz);
				if (newPoint.equals(current)) {
					continue;
				}
				shUpDown.waitForPosition((int) current.z);
				shLeftRight.waitForPosition((int) current.x);
				shBackForward.waitForPosition((int) current.y);
				
				shUpDown.requestPosition((int) nz);
				shLeftRight.requestPosition((int) nx);
				shBackForward.requestPosition((int) ny);


				
//				while(true) {
//					if ((shUpDown.getPosition() == nz)
//							&& (shLeftRight.getPosition() == nx)
//							&& (shBackForward.getPosition() == ny))
//							{
//						break;
//					}
//					try {
//						Thread.sleep(0, 150);
//					} catch (InterruptedException e) {
//					}
//				}
				
				current = newPoint;
			}
			
			return current;
		}
		
		
		
	}
	
	static class Line {
		Point a;
		Point b;
		
		public Line(Point a, Point b) {
			this.a = a;
			this.b = b;
		}
	}
	
	static class Point {
		public long x;
		public long y;
		public long z;
		
		public Point() {
		}

		public Point(long x, long y, long z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}
			if (obj instanceof Point) {
				Point that = (Point) obj;
				return this.x==that.x && this.y==that.y && this.z==that.z;
			}
			return false;
		}
	}

	public void rot() {
		designPanel.rot();
	}
	
}
