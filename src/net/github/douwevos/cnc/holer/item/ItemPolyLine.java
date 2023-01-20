package net.github.douwevos.cnc.holer.item;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import net.github.douwevos.cnc.head.CncHeadSpeed;
import net.github.douwevos.cnc.head.MicroLocation;
import net.github.douwevos.cnc.holer.HolerModel.ToolItemRun;
import net.github.douwevos.cnc.holer.HolerModelRun.RunContext;
import net.github.douwevos.cnc.holer.ItemGrabInfo;
import net.github.douwevos.cnc.holer.design.SelectionModel;
import net.github.douwevos.cnc.holer.design.ViewCamera;
import net.github.douwevos.cnc.holer.feature.CurvableFeature;
import net.github.douwevos.cnc.holer.feature.LocationFeature;
import net.github.douwevos.cnc.layer.Layer;
import net.github.douwevos.cnc.layer.LayerDescription;
import net.github.douwevos.cnc.poly.PolyDot;
import net.github.douwevos.cnc.poly.PolyForm;
import net.github.douwevos.cnc.poly.PolyForm.PolyOutput;
import net.github.douwevos.cnc.poly.PolyFormPainter;
import net.github.douwevos.cnc.tool.Tool;
import net.github.douwevos.cnc.type.Distance;
import net.github.douwevos.justflat.values.Bounds2D;
import net.github.douwevos.justflat.values.Point2D;

public class ItemPolyLine implements Item {

	private PolyForm polyForm;
	private final long startDepth;
	private final long depth;
	
	public ItemPolyLine(PolyForm polyForm, long depth) {
		this.polyForm = polyForm;
		this.depth = depth;
		this.startDepth = 0;
	}

	public ItemPolyLine(PolyForm polyForm, long startDepth, long depth) {
		this.polyForm = polyForm;
		this.depth = depth;
		this.startDepth = startDepth;
	}

	public ItemPolyLine(PolyForm polyForm, Distance depth) {
		this.polyForm = polyForm;
		this.depth = depth.asMicrometers();
		this.startDepth = 0;
	}
	
	
	public void moveLastDot(long x, long y) {
		int pidx = polyForm.dotCount()-1;
		PolyDot polyDot = polyForm.get(pidx);
		polyDot = polyDot.withLocation(x, y);
		polyForm = polyForm.set(pidx, polyDot);
	}
	
	public void removeLastDot() {
		polyForm = polyForm.remove(polyForm.dotCount());
	}

	public int dotCount() {
		return polyForm.dotCount();
	}

	public void addDot(PolyDot dot) {
		polyForm = polyForm.add(dot);
	}

	@Override
	public void paint(Graphics2D gfx, ToolItemRun toolItemRun, ViewCamera designView, SelectionModel selectionModel) {
		if (polyForm.isEmpty()) {
			return;
		}

		Tool tool = toolItemRun.getTool();
		Color color = gfx.getColor();
		gfx.setColor(color.darker().darker());

		PolyFormPainter polyFormPainter = new PolyFormPainter(polyForm);

		Stroke oldStroke = gfx.getStroke();
//		gfx.setStroke(new BasicStroke(tool.getDiameter(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
//		gfx.setColor(color.darker());
		
//		polyFormPainter.draw(gfx, 0, 0);

		gfx.setColor(color);
		gfx.setStroke(oldStroke);

		
		polyFormPainter.draw(gfx, 0, 0);

		
		ItemGrabInfo highlight = selectionModel.getHighlight();
		gfx.setColor(Color.red);
		drawGrabInfo(gfx, designView, highlight, 7);
		
		gfx.setColor(Color.CYAN);
		for(ItemGrabInfo grabInfo : selectionModel.selections()) {
			drawGrabInfo(gfx, designView, grabInfo, 6);
		}
		
	}

	private void drawGrabInfo(Graphics2D gfx, ViewCamera designView, ItemGrabInfo grabInfo, int thickness) {
		if (grabInfo instanceof PointGrabInfo) {
			PointGrabInfo pgi = (PointGrabInfo) grabInfo;
			if (pgi.item == this) {
				int l = (int) (thickness/designView.getScale());
				PolyDot p = pgi.dot;
				gfx.fillArc((int) p.x-l/2, (int) p.y-l/2, l, l, 0, 360);
			}
		} else if (grabInfo instanceof LineGrabInfo) {
			LineGrabInfo pgi = (LineGrabInfo) grabInfo;
			if (pgi.item == this) {
				int l = (int) (thickness/designView.getScale());
				PolyDot p1 = polyForm.get(pgi.pointIndex1);
				PolyDot p2 = polyForm.get(pgi.pointIndex2);
				Stroke oldStroke = gfx.getStroke();
				gfx.setStroke(new BasicStroke(l, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				gfx.drawLine((int) p1.x, (int) p1.y, (int) p2.x, (int) p2.y);
				gfx.setStroke(oldStroke);
			}
		}
	}
//

	@Override
	public void run(RunContext runContext) {
		Tool tool = runContext.getSelectedTool();
		AtomicBoolean done = new AtomicBoolean(false);
		boolean keepGoing = true;
		runContext.setFloating(true);
		int dropCount = 0;
		long dropSpeed = runContext.dropSpeed(tool);
		MicroLocation lastLocation = null;
		for(long z=startDepth + dropSpeed/4; !done.get() && keepGoing; ) {
			keepGoing = false;
			long useZ = z;
			if (useZ>=depth) {
				useZ = depth;
			} else {
				keepGoing = true;
			}
			
			
			CncPolyProduce output = new CncPolyProduce(runContext, done, lastLocation, useZ);
			
			polyForm.produce(output, 1d, 0, 0);
			
			lastLocation = output.getLastLocation();

			
//			CncHeadSpeed startFast = CncHeadSpeed.FAST;
//			for(PolyDot point : polyForm) {
//				runContext.lineTo(new MicroLocation(point.x, point.y, useZ), startFast);
//				runContext.setFloating(false);
//				startFast = CncHeadSpeed.SLOW;
//			}
//			runContext.setFloating(true);
			
			dropCount++;

			if (dropCount<2) {
				z+=dropSpeed/2;
			} else if (dropCount<3) {
				z+=dropSpeed*3/4;
			} else {
				z+=dropSpeed;
			}
		}
		runContext.setFloating(true);
	}
	
	private static class CncPolyProduce implements PolyOutput {

		private final RunContext runContext;
		private final long z; 
		private final AtomicBoolean done; 
		private MicroLocation lastLocation;
		
		
		public CncPolyProduce(RunContext runContext, AtomicBoolean done, MicroLocation lastLocation, long z) {
			this.runContext = runContext;
			this.lastLocation = null;
			this.done = done;
			this.z = z;
		}
		
		@Override
		public void contourBegin() {
		}

		
		@Override
		public void line(int dotIndexA, Point2D pointA, Point2D pointB) {
			if (done.get()) {
				return;
			}
			done.set(runContext.shouldMoveToNextItem());
			MicroLocation microLocationA = asMicroLocation(pointA, z);
			MicroLocation microLocationB = asMicroLocation(pointB, z);
			if (!Objects.equals(microLocationA, lastLocation)) {
				if (lastLocation != null && lastLocation.withZ(z).equals(microLocationA)) {
					runContext.lineTo(microLocationA, CncHeadSpeed.SLOW);
				} else {
					runContext.setFloating(true);
					runContext.lineTo(microLocationA, CncHeadSpeed.FAST);
				}
			}
			runContext.setFloating(false);
			
			runContext.lineTo(microLocationB, CncHeadSpeed.SLOW);
			lastLocation = microLocationB;
		}

		@Override
		public void contourEnd() {
			
		}
		
		private MicroLocation asMicroLocation(Point2D point, long z) {
			return new MicroLocation(point.x, point.y, z);
		}
		
		public MicroLocation getLastLocation() {
			return lastLocation;
		}
		
	}
	
	
//	private void cnc(CncControlContext controlContext, List<PolyDot> dotList, long z) {
//
//		int dotCount = dotList.size();
//		PolyDot dot = dotList.get(0);
//		PolyDot lastDot = dot; 
//		float x0 = (float) lastDot.x;
//		float y0 = (float) -lastDot.y;
//		Point p0 = Point.of((long) x0, (long) y0, z);
//		controlContext.setFloating(true);
//		controlContext.lineTo(p0, true);
//		
//		for(int dotIdx=0; dotIdx<=dotCount; dotIdx++) {
//			dot = dotList.get(dotIdx % dotCount);
//			PolyDot nextDot = dotList.get((dotIdx+1)%dotCount);
//			
//			float x1 = dot.x;
//			float y1 = -dot.y;
////				System.out.println(""+(dotIdx % dotCount)+" ## x0="+x0+", y0="+y0+", x1="+x1+", y1="+y1);
//			if (dot.isCurve) {
//				
//				float x2 = nextDot.x;
//				float y2 = nextDot.y;
//				
//				if (nextDot.isCurve) {
//					x2 = (x1+x2)/2f;
//					y2 = (y1+y2)/2f;
//				}
//				float xa = (x0+2f*x1)/3f;
//				float ya = (y0+2f*y1)/3f;
//
//				float xb = (2f*x1+x2)/3f;
//				float yb = (2f*y1+y2)/3f;
//
//				controlContext.setFloating(false);
//				curveTo(controlContext, x0, y0
//						, xa, ya, xb, yb
//						, x2, y2, z);
//				x0 = x2;
//				y0 = y2;
//			} else {
//				if (nextDot.isCurve) {
//					x1 = (x1+x0)/2f;
//					y1 = (y1+y0)/2f;
//				}
//				Point p1 = Point.of(Math.round(x1), Math.round(y1), z);
//				controlContext.setFloating(false);
//				controlContext.lineTo(p1, false);
//				x0 = x1;
//				y0 = y1;
//			}
//		}
//	}
//
//	private void curveTo(CncControlContext controlContext, double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3, long z) {
//		double xp[] = new double[] {x0,x1,x2,x3};
//		double yp[] = new double[] {y0,y1,y2,y3};
//		List<XY> bezierCurve = bezierCurve(xp, yp);
//		
//		for(XY xy : bezierCurve) {
//			Point pxy = Point.of(Math.round(xy.x), Math.round(xy.y), z);
//			controlContext.lineTo(pxy, false);
//		}
//	}

	

	@Override
	public ItemGrabInfo createGrabInfo(int mouseX, int mouseY) {
		ItemGrabInfo itemGrabInfo = null;
		for(int pidx=0; pidx<polyForm.dotCount(); pidx++) {
			PolyDot p = polyForm.get(pidx);
			long dx = mouseX - (int) p.x;
			long dy = mouseY - (int) p.y;
			long distSq = dx*dx + dy*dy;
			if ((itemGrabInfo == null) || (distSq<itemGrabInfo.getSquareDistance())) {
				itemGrabInfo = new PointGrabInfo(this, p, distSq);
			}
		}
		

		for(int pidx=0; pidx<polyForm.dotCount(); pidx++) {
			PolyDot p0 = polyForm.get(pidx);
			int index2 = (pidx+1) % polyForm.dotCount();
			PolyDot p1 = polyForm.get(index2);
			
			long distSq = pDistance(mouseX, mouseY, p0.x, p0.y, p1.x, p1.y);
			if ((itemGrabInfo == null) || (distSq<itemGrabInfo.getSquareDistance())) {
				itemGrabInfo = new LineGrabInfo(this, pidx, index2, distSq);
			}
		}
		
		return itemGrabInfo;
	}
	
	
	public long pDistance(long x, long y, long x1, long y1, long x2, long y2) {
		long dx1 = x - x1;
		long dy1 = y - y1;
		long dx2 = x2 - x1;
		long dy2 = y2 - y1;

		long dot = dx1 * dx2 + dy1 * dy2;
		long lengthSq = dx2 * dx2 + dy2 * dy2;
		double param = -1;
		if (lengthSq != 0) {
			param = (double) dot / lengthSq;
		}


		if (param < 0 || param > 1) {
			return Long.MAX_VALUE;
		}
	    long projX = x1 + Math.round(param*dx2);
	    long projY = y1 + Math.round(param*dy2);

	    long dx = x - projX;
	    long dy = y - projY;
	    return dx*dx + dy*dy;
	}

	static class PointGrabInfo extends ItemGrabInfo implements LocationFeature, CurvableFeature {

		private final Item item;
		private PolyDot dot;
		
		public PointGrabInfo(Item item, PolyDot dot, long distSq) {
			super(distSq);
			this.item = item;
			this.dot = dot;
		}

		private PolyDot getDot() {
			return dot;
		}
		
		@Override
		public void startDrag(long  mouseX, long mouseY) {
			PolyDot point = getDot();
			this.grabDeltaX = point.x-mouseX;
			this.grabDeltaY = point.y-mouseY;
		}
		
		@Override
		public void doDrag(long mouseX, long mouseY) {
			ItemPolyLine ipl = (ItemPolyLine) item;
			int pointIndex = ipl.polyForm.indexOf(dot);
			dot = new PolyDot(grabDeltaX+mouseX, grabDeltaY+mouseY, dot.isCurve);
			ipl.polyForm.set(pointIndex, dot);
		}
		
		
		@Override
		public void delete() {
			ItemPolyLine ipl = (ItemPolyLine) item;
			int index = ipl.polyForm.indexOf(dot);
			ipl.polyForm.remove(index);
		}
		
		@Override
		public long getX() {
			PolyDot dot = getDot();
			return dot.x;
		}
		
		
		@Override
		public long getY() {
			PolyDot dot = getDot();
			return dot.y;
		}

//		@Override
//		public void setLocation(long x, long y) {
//			ItemPolyLine ipl = (ItemPolyLine) item;
//			int pointIndex = ipl.polyForm.indexOf(dot);
//			dot = new PolyDot(x, y, dot.isCurve);
//			ipl.polyForm.set(pointIndex, dot);
//			
//		}

		@Override
		public void setX(long x) {
			ItemPolyLine ipl = (ItemPolyLine) item;
			int pointIndex = ipl.polyForm.indexOf(dot);
			dot = new PolyDot(x, dot.y, dot.isCurve);
			ipl.polyForm.set(pointIndex, dot);
		}

		@Override
		public void setY(long y) {
			ItemPolyLine ipl = (ItemPolyLine) item;
			int pointIndex = ipl.polyForm.indexOf(dot);
			dot = new PolyDot(dot.x, y, dot.isCurve);
			ipl.polyForm.set(pointIndex, dot);
		}
		
		
		@Override
		public boolean isCurved() {
			return dot.isCurve;
		}
		
		@Override
		public void setCurved(boolean curved) {
			ItemPolyLine ipl = (ItemPolyLine) item;
			int pointIndex = ipl.polyForm.indexOf(dot);
			dot = new PolyDot(dot.x, dot.y, curved);
			ipl.polyForm.set(pointIndex, dot);
		}
	}
	
	
	
	static class LineGrabInfo extends ItemGrabInfo {

		private final Item item;
		private final int pointIndex1;
		private final int pointIndex2;
		protected long grabDeltaX2;
		protected long grabDeltaY2;

		public LineGrabInfo(Item item, int pidx1, int pidx2, long distSq) {
			super(distSq);
			this.item = item;
			this.pointIndex1 = pidx1;
			this.pointIndex2 = pidx2;
		}

		private PolyDot getDot(int pidx) {
			ItemPolyLine ipl = (ItemPolyLine) item;
			return ipl.polyForm.get(pidx);
		}
		
		@Override
		public void startDrag(long  mouseX, long mouseY) {
			PolyDot point1 = getDot(pointIndex1);
			this.grabDeltaX = point1.x-mouseX;
			this.grabDeltaY = point1.y-mouseY;
			PolyDot point2 = getDot(pointIndex2);
			this.grabDeltaX2 = point2.x-mouseX;
			this.grabDeltaY2 = point2.y-mouseY;
		}
		
		@Override
		public void doDrag(long mouseX, long mouseY) {
			ItemPolyLine ipl = (ItemPolyLine) item;

			PolyDot point1src = getDot(pointIndex1);
			PolyDot point1new = new PolyDot(grabDeltaX+mouseX, grabDeltaY+mouseY, point1src.isCurve);
			ipl.polyForm.set(pointIndex1, point1new);
			PolyDot point2src = getDot(pointIndex2);
			PolyDot point2new = new PolyDot(grabDeltaX2+mouseX, grabDeltaY2+mouseY, point2src.isCurve);
			ipl.polyForm.set(pointIndex2, point2new);
		}
		
		@Override
		public void delete() {
			
		}
	}
	
	
	@Override
	public Bounds2D bounds() {
		return polyForm.calculateBounds();
	}

	
}
