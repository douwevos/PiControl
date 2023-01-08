package net.github.douwevos.cnc.poly;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.github.douwevos.justflat.types.values.Bounds2D;
import net.github.douwevos.justflat.types.values.FracPoint2D;
import net.github.douwevos.justflat.types.values.Point2D;

public class PolyForm implements Iterable<PolyDot> {

	private final List<PolyDot> dotList;
	private final boolean closed;

	private PolyForm(List<PolyDot> dotList, boolean closed, boolean local) {
		this.dotList = dotList;
		this.closed = closed;
	}

	
	public PolyForm(List<PolyDot> dotList, boolean closed) {
		this.dotList = new ArrayList<>(dotList);
		this.closed = closed;
	}
	
	public boolean isClosed() {
		return closed;
	}

	public int dotCount() {
		return dotList.size();
	}

	public boolean isEmpty() {
		return dotList.isEmpty();
	}

	
	public PolyDot get(int index) {
		return dotList.get(index);
	}
	
	public Bounds2D calculateBounds() {
		Bounds2D result = null;
		if (!dotList.isEmpty()) {
			PolyDot dot = dotList.get(0);
			result = new Bounds2D(dot.x, dot.y, dot.x,dot.y);
			for(PolyDot pdot : dotList) {
				result = result.extend(pdot.x, pdot.y);
			}
		}
		return result;
	}

	public PolyForm set(int pidx, PolyDot dot) {
		ArrayList<PolyDot> newDotList = new ArrayList<>(dotList);
		newDotList.set(pidx, dot);
		return new PolyForm(newDotList, closed, closed);
	}


	public PolyForm remove(int index) {
		ArrayList<PolyDot> newDotList = new ArrayList<>(dotList);
		newDotList.remove(index);
		return new PolyForm(newDotList, closed, closed);
	}


	public PolyForm add(PolyDot dot) {
		ArrayList<PolyDot> newDotList = new ArrayList<>(dotList);
		newDotList.add(dot);
		return new PolyForm(newDotList, closed, closed);
	}

	public PolyForm add(PolyDot dot, int index) {
		ArrayList<PolyDot> newDotList = new ArrayList<>(dotList);
		newDotList.add(index, dot);
		return new PolyForm(newDotList, closed, closed);
	}

	@Override
	public Iterator<PolyDot> iterator() {
		return dotList.iterator();
	}


	public int indexOf(PolyDot dot) {
		return dotList.indexOf(dot);
	}
	
	
	public void produce(PolyOutput output, double scalar, long xpos, long ypos) {

		int dotCount = dotCount();
		PolyDot dot = get(0);
		PolyDot lastDot = dot; 
		double x0 = lastDot.x;
		double y0 = lastDot.y;

		int end = dotCount;
		if (!isClosed()) {
			end--;
		}
 		
		output.contourBegin();
		Point2D pointA = Point2D.of(xpos + (long) Math.round(x0*scalar), ypos + (long) Math.round(y0*scalar));

		for(int dotIdx=0; dotIdx<=end; dotIdx++) {
			dot = get(dotIdx % dotCount);
			PolyDot nextDot = get((dotIdx+1)%dotCount);
			
			double x1 = dot.x;
			double y1 = dot.y;
//			System.out.println(""+(dotIdx % dotCount)+" ## x0="+x0+", y0="+y0+", x1="+x1+", y1="+y1);
			if (dot.isCurve) {

				
				double x2 = (double) nextDot.x;
				double y2 = (double) nextDot.y;
				
				if (nextDot.isCurve) {
					x2 = (x1+x2)/2d;
					y2 = (y1+y2)/2d;
				}
				double xa = (x0+2d*x1)/3d;
				double ya = (y0+2d*y1)/3d;

				double xb = (2d*x1+x2)/3d;
				double yb = (2d*y1+y2)/3d;

				pointA = curveTo(output, dotIdx, pointA
						, xpos + x0*scalar, ypos + y0*scalar
						, xpos + xa*scalar, ypos + ya*scalar
						, xpos + xb*scalar, ypos + yb*scalar
						, xpos + x2*scalar, ypos + y2*scalar);


				x0 = x2;
				y0 = y2;
			} else {
//				if (nextDot.isCurve) {
//					x1 = (x1+x0)/2d;
//					y1 = (y1+y0)/2d;
//				}
				Point2D pointB = Point2D.of(xpos + (int) Math.round(x1*scalar), ypos + (int) Math.round(y1*scalar));
				output.line(dotIdx, pointA, pointB);
				x0 = x1;
				y0 = y1;
				pointA = pointB;
			}
		}		

		output.contourEnd();
	}


	private Point2D curveTo(PolyOutput output, int dotIdxA, Point2D pointA, double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3) {

		double xp[] = new double[] {x0,x1,x2,x3};
		double yp[] = new double[] {y0,y1,y2,y3};
		
		List<XY> bezierCurve = bezierCurve(xp, yp);
		
		for(XY xy : bezierCurve) {
			Point2D pointB = Point2D.of(Math.round(xy.x), Math.round(xy.y));
			output.line(dotIdxA, pointA, pointB);
			pointA = pointB;
		}
		return pointA;
	}
	


	public void produce(FracPolyOutput output, double scalar, long xpos, long ypos) {

		int dotCount = dotCount();
		PolyDot dot = get(0);
		PolyDot lastDot = dot; 
		double x0 = lastDot.x;
		double y0 = lastDot.y;

		int end = dotCount;
		if (!isClosed()) {
			end--;
		}
 		
		output.contourBegin();
		FracPoint2D pointA = FracPoint2D.of(xpos + x0*scalar, ypos + y0*scalar);

		for(int dotIdx=0; dotIdx<=end; dotIdx++) {
			dot = get(dotIdx % dotCount);
			PolyDot nextDot = get((dotIdx+1)%dotCount);
			
			double x1 = dot.x;
			double y1 = dot.y;
//			System.out.println(""+(dotIdx % dotCount)+" ## x0="+x0+", y0="+y0+", x1="+x1+", y1="+y1);
			if (dot.isCurve) {

				
				double x2 = (double) nextDot.x;
				double y2 = (double) nextDot.y;
				
				if (nextDot.isCurve) {
					x2 = (x1+x2)/2d;
					y2 = (y1+y2)/2d;
				}
				double xa = (x0+2d*x1)/3d;
				double ya = (y0+2d*y1)/3d;

				double xb = (2d*x1+x2)/3d;
				double yb = (2d*y1+y2)/3d;

				pointA = curveTo(output, dotIdx, pointA
						, xpos + x0*scalar, ypos + y0*scalar
						, xpos + xa*scalar, ypos + ya*scalar
						, xpos + xb*scalar, ypos + yb*scalar
						, xpos + x2*scalar, ypos + y2*scalar);


				x0 = x2;
				y0 = y2;
			} else {
//				if (nextDot.isCurve) {
//					x1 = (x1+x0)/2d;
//					y1 = (y1+y0)/2d;
//				}
				FracPoint2D pointB = FracPoint2D.of(xpos + x1*scalar, ypos + y1*scalar);
				output.line(dotIdx, pointA, pointB);
				x0 = x1;
				y0 = y1;
				pointA = pointB;
			}
		}		

		output.contourEnd();
	}


	private FracPoint2D curveTo(FracPolyOutput output, int dotIdxA, FracPoint2D pointA, double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3) {

		double xp[] = new double[] { x0,x1,x2,x3 };
		double yp[] = new double[] { y0,y1,y2,y3 };
		
		List<XY> bezierCurve = bezierCurve(xp, yp);
		
		for(XY xy : bezierCurve) {
			FracPoint2D pointB = FracPoint2D.of(xy.x, xy.y);
			output.line(dotIdxA, pointA, pointB);
			pointA = pointB;
		}
		return pointA;
	}
	


	List<XY> bezierCurve(double x[] , double y[]) {
	    double xu = 0.0 , yu = 0.0 , u = 0.0 ;
	    List<XY> result = new ArrayList<>();
	    for(u = 0.0 ; u <= 1.00001 ; u += 0.0001) {
	    	double v = 1.0 - u;
	    	double powMU2 = v*v;
	        double powMU3 = powMU2*v;
			double powU2 = u*u;
			double powU3 = powU2*u;
			xu = powMU3*x[0]+3*u*powMU2*x[1]+3*powU2*(v)*x[2] + powU3*x[3];
	        yu = powMU3*y[0]+3*u*powMU2*y[1]+3*powU2*(v)*y[2] + powU3*y[3];
	        result.add(new XY(xu, yu));
	    }
	    return result;
	}
	
	

	static class XY {
		public final double x;
		public final double y;
		
		public XY(double x, double y) {
			this.x = x;
			this.y = y;
		}
	}

	
	
	public interface PolyOutput {
		void contourBegin();
		void line(int dotIndexA, Point2D pointA, Point2D pointB);
		void contourEnd();
	}

	public interface FracPolyOutput {
		void contourBegin();
		void line(int dotIndexA, FracPoint2D pointA, FracPoint2D pointB);
		void contourEnd();
	}

	
}
