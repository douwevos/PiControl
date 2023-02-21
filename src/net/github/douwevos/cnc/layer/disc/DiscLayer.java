package net.github.douwevos.cnc.layer.disc;


import net.github.douwevos.justflat.shape.PolygonLayer;
import net.github.douwevos.justflat.types.Layer;
import net.github.douwevos.justflat.values.Line2D;
import net.github.douwevos.justflat.values.Point2D;
import net.github.douwevos.justflat.values.shape.Polygon;

public class DiscLayer extends PolygonLayer implements Layer {

	private final long width;
	private final long height;
	private final int discSize;
	
	public DiscLayer(long width, long height, int discSize) {
		super();
		this.width = width;
		this.height = height;
		this.discSize = discSize;
	
	}
	
	
	public long getWidth() {
		return width;
	}
	
	
	public int getDiscSize() {
		return discSize;
	}
	




	@Override
	public boolean isEmpty() {
		return isEmpty();
	}

	

	
	public Polygon getAt(int index) {
		return at(index);
	}

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
//			selected.contour.setDotAt(selected.dotIndex, moved);
			
		}
		return true;
	}



	public static class Selection {
		public final Polygon contour;
		public final int dotIndex;
		public final double grabX;
		public final double grabY;
		public Point2D startDot;
		
		public Selection(Polygon contour, int dotIndex, double grabX, double grabY) {
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
		public final Polygon contour;
		public final int dotIndex;
		public final Line2D line;
		public final Point2D intersectionPoint;
		
		public LineSelection(Polygon contour, int dotIndex) {
			this(contour, dotIndex, null);
		}

		public LineSelection(Polygon contour, int dotIndex, Point2D intersectionPoint) {
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

}
