package net.github.douwevos.cnc.model.value;

import net.github.douwevos.cnc.poly.PolyDot;
import net.github.douwevos.cnc.poly.PolyForm;
import net.github.douwevos.cnc.poly.PolyForm.FracPolyOutput;
import net.github.douwevos.justflat.contour.Contour;
import net.github.douwevos.justflat.contour.ContourLayer;
import net.github.douwevos.justflat.types.values.FracPoint2D;
import net.github.douwevos.justflat.types.values.Point2D;

public class PolyLine implements Item {

	private final PolyForm polyForm;
	private final long depth;
	
	public PolyLine(PolyForm polyForm, long depth) {
		this.polyForm = polyForm;
		this.depth = depth;
	}
	
	public PolyForm getPolyForm() {
		return polyForm;
	}
	
	public long getDepth() {
		return depth;
	}
	
	
	@Override
	public void writeToContourLayer(ContourLayer contourLayer, long atDepth) {
		if (atDepth > depth) {
			return;
		}

		PolyFormToContourOutput fracPolyOutput = new PolyFormToContourOutput(); 
		polyForm.produce(fracPolyOutput, 1d, 0, 0);
		Contour contour = fracPolyOutput.contour;
		contour.setClosed(true);
		contourLayer.add(contour);
	}
	
	
	
	private static class PolyFormToContourOutput implements FracPolyOutput {

		public Contour contour = new Contour();
		
		@Override
		public void contourBegin() {
			
		}
		
		@Override
		public void contourEnd() {
			
		}
		
		
		@Override
		public void line(int dotIndexA, FracPoint2D pointA, FracPoint2D pointB) {
			Point2D nfa = pointA.toNonFractional();
			Point2D nfb = pointB.toNonFractional();
			if (contour.isEmpty() || !contour.getLast().equals(nfa)) {
				contour.add(nfa);
			}
			if (contour.isEmpty() || !contour.getLast().equals(nfb)) {
				contour.add(nfb);
			}
		}
	}

}
