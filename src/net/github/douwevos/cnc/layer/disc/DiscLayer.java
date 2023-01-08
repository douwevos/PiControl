package net.github.douwevos.cnc.layer.disc;

import java.util.Iterator;
import java.util.List;

import net.github.douwevos.cnc.layer.Layer;
import net.github.douwevos.cnc.layer.StartStop;
import net.github.douwevos.justflat.contour.Contour;
import net.github.douwevos.justflat.contour.ContourLayer;
import net.github.douwevos.justflat.types.values.Line2D;
import net.github.douwevos.justflat.types.values.Point2D;

public class DiscLayer extends ContourLayer implements Layer {

	private final int discSize;
	
	public DiscLayer(long width, long height, int discSize) {
		super(width, height);
		this.discSize = discSize;
		
		int k = 1;
		while(k<=discSize) {
			k = k<<1;
		}
		
		int guess = k * 16;
	
	}
	
	@Override
	public long bottom() {
		return 0;
	}

	@Override
	public long top() {
		return getHeight();
	}
	
	public long getWidth() {
		return getWidth();
	}
	
	
	public int getDiscSize() {
		return discSize;
	}
	

//	
//	@Override
//	public Layer duplicate() {
//		DiscLayer result = new DiscLayer(getWidth(), getHeight(), discSize);
//		for(Contour contour : contours) {
//			result.contours.add(contour.duplicate());
//		}
//		return result;
//	}

//	@Override
//	public Bounds2D bounds() {
//		Bounds2D b = null;
//		for(Contour contour : contours) {
//			Bounds2D bounds = contour.getBounds();
//			if (b==null) {
//				b = bounds;
//			} else {
//				b = b.union(bounds);
//			}
//			
//		}
//		return b != null ? b : new Bounds2D(0, 0, width, height);
//	}

	@Override
	public boolean testDot(long x, long y, boolean defaultValue) {
		return false;
	}

	@Override
	public void invert(int lineIndex, List<StartStop> startStopList) {
	}

	@Override
	public boolean isEmpty() {
		return contours.isEmpty();
	}

	@Override
	public void merge(Layer layer) {
		
	}
	
	
//	public List<StartStop> scanlineHorizontal(long y) {
//		OnOffLine onOffLine = new OnOffLine();
//		LayerLine ll = new LayerLine();
//		for(Contour contour : contours) {
//			onOffLine.reset();
//			contour.scanlineHorizontal(y, onOffLine);
//			List<StartStop> apply = onOffLine.apply();
//			ll.invert(apply);
//		}
//		return Arrays.asList(ll.startStops);
//		
////		return onOffLine.apply();
//	}
//
//	public List<StartStop> scanlineVertical(long x) {
//		OnOffLine onOffLine = new OnOffLine();
//		LayerLine ll = new LayerLine();
//		for(Contour contour : contours) {
//			onOffLine.reset();
//			contour.scanlineVertical(x, onOffLine);
//			List<StartStop> apply = onOffLine.apply();
//			ll.invert(apply);
//		}
//		return Arrays.asList(ll.startStops);
//		
////		return onOffLine.apply();
//	}

//	
//	public List<StartStopDouble> scanLineDouble(long y) {
//		OnOffLineDouble onOffLine = new OnOffLineDouble();
//		LayerLineDouble ll = new LayerLineDouble();
//		for(Contour contour : contours) {
//			onOffLine.reset();
//			contour.scanLineDouble(y, onOffLine);
//			List<StartStopDouble> apply = onOffLine.apply();
//			ll.invert(apply);
//		}
//		return Arrays.asList(ll.startStops);
//		
////		return onOffLine.apply();
//	}

	
	

//	private void scanLine(long y, OnOffLine onOffLine, Contour contour) {
//		Point2D pa = contour.getLast();
//		for(Point2D pb : contour) {
//			if ((pa.y<y &&  pb.y<y) ||
//					(pa.y>y &&  pb.y>y)) {
//				pa = pb;
//				continue;
//			}
//			if (pa.y==pb.y) {
//				pa = pb;
//				continue;
//			}
//
//			long deltaYBA = pb.y - pa.y;
//			boolean up = true;
//			if (deltaYBA<0) {
//				up = false;
//			}
//
//			long deltaXBA = pb.x - pa.x;
//			long deltaYYA = y - pa.y;
//			long nx = pa.x + (deltaYBA/2 +  deltaXBA*deltaYYA)/deltaYBA;
//			onOffLine.add(nx, up);
//
//			pa = pb;
//		}
//	}

	@Override
	public Iterator<Contour> iterator() {
		return contours.iterator();
	}

	public void add(Contour contour) {
		contours.add(contour);
	}
	
	public int count() {
		return contours.size();
	}
	
	public Contour getAt(int index) {
		return contours.get(index);
	}
//	
//	@Override
//	public Object selectAt(double nx, double ny, double zoomFactor) {
//		Point2D bestDot = null;
//		Contour bestContour = null;
//		int bestDotIndex = -1;
//		double bestSqDist = 0;
//		
//		for(Contour contour : contours) {
//			int dotIndex=0;
//			for(Point2D dot : contour) {
//				double sx = dot.x-nx;
//				double sy = dot.y-ny;
//				double d = sx*sx + sy*sy;
//				if (bestDot==null || d<bestSqDist) {
//					bestSqDist = d;
//					bestDot = dot;
//					bestDotIndex = dotIndex;
//					bestContour = contour;
//				}
//				dotIndex++;
//			}
//		}
////		System.out.println("nx="+nx+", ny="+ny+", bestDot="+bestDot+", zoomFactor="+zoomFactor+" bestSqDist="+bestSqDist);
//		
//		if (bestDot!=null && bestSqDist<(400d*zoomFactor*zoomFactor)) {
//			return new Selection(bestContour, bestDotIndex, nx, ny);
//		}
//
//		bestDot = null;
//		bestContour = null;
//		bestDotIndex = -1;
//		bestSqDist = 0;
//		
//		for(Contour contour : contours) {
//			int dotIndex=0;
//			Point2D lastDot = contour.getLast();
//			for(Point2D dot : contour) {
//				double ptSegDistSq = Line2D.ptSegDistSq(dot.x, dot.y, lastDot.x, lastDot.y, nx,ny);
//				if (bestDot==null || ptSegDistSq<bestSqDist) {
//					bestSqDist = ptSegDistSq;
//					bestDot = dot;
//					bestDotIndex = dotIndex-1;
//					bestContour = contour;
//				}
//				dotIndex++;
//				lastDot = dot;
//			}
//		}
//
//		if (bestDot!=null && bestSqDist<(400d*zoomFactor*zoomFactor)) {
//			LineSelection lineSelection = new LineSelection(bestContour, bestDotIndex);
//			return enrichIntersectionPoint(lineSelection, contours, nx, ny, zoomFactor);
//		}
//		
//		return null;
//	}
//	
	
//	private Object enrichIntersectionPoint(LineSelection lineSelection, List<Contour> contours, double nx, double ny,
//			double zoomFactor) {
//		Line lineMain = lineSelection.getLine();
//		
//		IntersectionInfo info = new IntersectionInfo();
//		Point2D bestIntersectionPoint = null;
//		double bestDistSq = 0d;
//		for(Contour contour : contours) {
//			for(Line line : contour.createLines(false)) {
//				if (line.equals(lineMain)) {
//					continue;
//				}
//				Point2D intersectionPoint = line.intersectionPoint(lineMain, info);
//				if (intersectionPoint != null && info.ua>=0d && info.ua<=1d) {
//					intersectionPoint = lineMain.intersectionPoint(line, info);
//					if (intersectionPoint != null && info.ua>=0d && info.ua<=1d) {
//						double dx = intersectionPoint.x - nx;
//						double dy = intersectionPoint.y - ny;
//						double sqDist = dx*dx + dy*dy;
//						if (bestIntersectionPoint==null || sqDist<bestDistSq) {
//							bestIntersectionPoint = intersectionPoint;
//							bestDistSq = sqDist;
//						}
//					}
//				}
//			}
//		}
//		
//		if (bestIntersectionPoint!=null) {
//			return new LineSelection(lineSelection.contour, lineSelection.dotIndex, bestIntersectionPoint);
//		}
//		
//		return lineSelection;
//	}

	@Override
	public boolean dragTo(Object selected, double nx, double ny) {
		if (selected instanceof Selection) {
			return dragSelection((Selection) selected, nx, ny);
		}
		return false;
	}
	
	
	
	private boolean dragSelection(Selection selected, double nx, double ny) {
		if (selected.dotIndex<0) {
			return false;
		}
		Point2D point2d = selected.contour.dotAt(selected.dotIndex);
		if (selected.startDot==null) {
			selected.startDot = point2d;
		} else {
			double dx = selected.grabX-selected.startDot.x;
			double dy = selected.grabY-selected.startDot.y;
			
			long newX = Math.round(nx-dx);
			long newY = Math.round(ny-dy);
			Point2D moved = Point2D.of(newX, newY);
			selected.contour.setDotAt(selected.dotIndex, moved);
			
		}
		return true;
	}



	public static class Selection {
		public final Contour contour;
		public final int dotIndex;
		public final double grabX;
		public final double grabY;
		public Point2D startDot;
		
		public Selection(Contour contour, int dotIndex, double grabX, double grabY) {
			this.contour = contour;
			this.dotIndex = dotIndex;
			this.grabX = grabX;
			this.grabY = grabY;
		}

		public Point2D getDot() {
			return dotIndex<0 ? null : contour.dotAt(dotIndex);
		}
	}

	public static class LineSelection {
		public final Contour contour;
		public final int dotIndex;
		public final Line2D line;
		public final Point2D intersectionPoint;
		
		public LineSelection(Contour contour, int dotIndex) {
			this(contour, dotIndex, null);
		}

		public LineSelection(Contour contour, int dotIndex, Point2D intersectionPoint) {
			this.contour = contour;
			this.dotIndex = dotIndex;
			Point2D pa = contour.dotAt(dotIndex);
			Point2D pb = contour.dotAt(dotIndex+1);
			line = new Line2D(pa, pb);
			this.intersectionPoint = intersectionPoint;
		}

		public Line2D getLine() {
			return line;
		}
		
		public Point2D getDot() {
			return dotIndex<0 ? null : contour.dotAt(dotIndex);
		}
		
		public Point2D getIntersectionPoint() {
			return intersectionPoint;
		}
	}

	public void moveDot(Point2D from, Point2D to) {
		
		for(int idx=0; idx<contours.size(); idx++) {
			Contour contour = contours.get(idx);
			Contour replacement = contour.moveDot(from, to);
			if (replacement != null) {
				contours.set(idx, replacement);
				return;
			}
		}
	}

}
