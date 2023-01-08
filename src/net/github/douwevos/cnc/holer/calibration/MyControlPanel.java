package net.github.douwevos.cnc.holer.calibration;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Objects;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import douwe.Point3D;
import net.github.douwevos.cnc.head.CncHeadService;

public class MyControlPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	private final CncHeadService cncHeadService;
	
	ValueController leftRightValueController;
	ValueController upDownValueController;
	ValueController backwardForwardValueController;
	
	Point3D requestedPoint = Point3D.of(0, 0, 0);
	
	public MyControlPanel(CncHeadService cncHeadService) {
		this.cncHeadService = cncHeadService;

		GridBagLayout gridBagLayout = new GridBagLayout();
		setLayout(gridBagLayout);
		
		
		SliderHandler sliderHandlerX = new SliderHandler() {
			@Override
			public long getPosition() {
				return requestedPoint.x;
			}
			
			@Override
			public void setPosition(long position) {
				Point3D newPoint = requestedPoint.withX(position);
				if (!Objects.equals(newPoint, requestedPoint)) {
					requestedPoint = newPoint;
//					cncControlContext.resetAndForceTo(newPoint);
				}
			}
		};
		leftRightValueController = new ValueController(sliderHandlerX, this, gridBagLayout, 0, -25000, 25000);

		
		SliderHandler sliderHandlerY = new SliderHandler() {
			@Override
			public long getPosition() {
				return requestedPoint.y;
			}
			
			@Override
			public void setPosition(long position) {
				Point3D newPoint = requestedPoint.withY(position);
				if (!Objects.equals(newPoint, requestedPoint)) {
					requestedPoint = newPoint;
//					cncControlContext.resetAndForceTo(newPoint);
				}
			}
		};
		backwardForwardValueController = new ValueController(sliderHandlerY, this, gridBagLayout, 1, -15000, 15000);
		
		SliderHandler sliderHandlerZ = new SliderHandler() {
			@Override
			public long getPosition() {
				return requestedPoint.z;
			}
			
			@Override
			public void setPosition(long position) {
				Point3D newPoint = requestedPoint.withZ(position);
				if (!Objects.equals(newPoint, requestedPoint)) {
					requestedPoint = newPoint;
//					cncControlContext.resetAndForceTo(newPoint);
				}
			}
		};
		upDownValueController = new ValueController(sliderHandlerZ, this, gridBagLayout, 2, -10000, 10000);
		
		
	}

	
	static class ValueController implements ChangeListener {
		protected static final int STEP = 10;
		SliderHandler sliderHandler;
		JSlider requestedLocation;
		JTextField fldValue;
		
		
		public ValueController(SliderHandler sliderHandler, Container container, GridBagLayout gridBagLayout, int row, int min, int max) {

			this.sliderHandler = sliderHandler;
			
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
			sliderHandler.setPosition(value);
			fldValue.setText(""+requestedLocation.getValue());
		}
		
	}

	interface SliderHandler {
		public long getPosition();
		public void setPosition(long position);
	}
	
}
