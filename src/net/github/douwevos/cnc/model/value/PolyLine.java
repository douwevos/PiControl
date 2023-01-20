package net.github.douwevos.cnc.model.value;

import net.github.douwevos.cnc.poly.PolyForm;
import net.github.douwevos.cnc.poly.PolyForm.FracPolyOutput;
import net.github.douwevos.justflat.shape.PolygonLayer;
import net.github.douwevos.justflat.values.FracPoint2D;
import net.github.douwevos.justflat.values.Point2D;
import net.github.douwevos.justflat.values.shape.Polygon;

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
	public void writeToContourLayer(PolygonLayer polygonLayer, long atDepth) {
		if (atDepth > depth) {
			return;
		}

		PolyFormToContourOutput fracPolyOutput = new PolyFormToContourOutput(); 
		polyForm.produce(fracPolyOutput, 1d, 0, 0);
		Polygon polygon = fracPolyOutput.polygon;
		polygon.setClosed(true);
		polygonLayer.add(polygon);
	}
	
	
	
	private static class PolyFormToContourOutput implements FracPolyOutput {

		public Polygon polygon = new Polygon();
		
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
			if (polygon.isEmpty() || !polygon.getLast().equals(nfa)) {
				polygon.add(nfa);
			}
			if (polygon.isEmpty() || !polygon.getLast().equals(nfb)) {
				polygon.add(nfb);
			}
		}
	}

}
