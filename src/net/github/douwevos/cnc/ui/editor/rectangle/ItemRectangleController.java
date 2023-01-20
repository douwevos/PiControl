package net.github.douwevos.cnc.ui.editor.rectangle;

import java.awt.Color;
import java.util.Objects;
import java.util.stream.Stream;

import javax.swing.JPopupMenu;

import net.github.douwevos.cnc.holer.feature.LocationFeature;
import net.github.douwevos.cnc.model.Editable;
import net.github.douwevos.cnc.model.EditableLayer;
import net.github.douwevos.cnc.model.EditableRectangle;
import net.github.douwevos.cnc.ui.Camera;
import net.github.douwevos.cnc.ui.ModelGraphics;
import net.github.douwevos.cnc.ui.ModelMouseEvent;
import net.github.douwevos.cnc.ui.editor.ItemController;
import net.github.douwevos.cnc.ui.editor.ItemGrabInfo;
import net.github.douwevos.justflat.values.Bounds2D;
import net.github.douwevos.justflat.values.Line2D;
import net.github.douwevos.justflat.values.Point2D;

public class ItemRectangleController implements ItemController {

	
	@Override
	public void paint(ModelGraphics modelGraphics, EditableLayer layer, Editable item) {
		Bounds2D bounds = item.calculateBounds();
		modelGraphics.setColor(Color.WHITE);
		modelGraphics.drawRectangle(bounds);
	}
	
	@Override
	public void paintHighlighted(Editable item, ItemGrabInfo<?> grabInfo, ModelGraphics modelGraphics) {
		if (grabInfo instanceof ItemRectangleGrabInfo) {
			ItemRectangleGrabInfo rectGrabInfo = (ItemRectangleGrabInfo) grabInfo;
			Point2D point = rectGrabInfo.getPoint();
			modelGraphics.drawCircle(point, modelGraphics.getCamera().toModelSize(5), false);
		} else if (grabInfo instanceof ItemSideRectangleGrabInfo) {
			ItemSideRectangleGrabInfo rectGrabInfo = (ItemSideRectangleGrabInfo) grabInfo;
			Line2D grabLine = rectGrabInfo.getLine();
			modelGraphics.drawLine(grabLine.pointA(), grabLine.pointB());
		}
	}

	@Override
	public void paintSelected(Editable item, ItemGrabInfo<?> grabInfo, ModelGraphics modelGraphics) {
		if (grabInfo instanceof ItemRectangleGrabInfo) {
			ItemRectangleGrabInfo rectGrabInfo = (ItemRectangleGrabInfo) grabInfo;
			Point2D point = rectGrabInfo.getPoint();
			modelGraphics.drawCircle(point, modelGraphics.getCamera().toModelSize(5), true);
		} else if (grabInfo instanceof ItemSideRectangleGrabInfo) {
			ItemSideRectangleGrabInfo rectGrabInfo = (ItemSideRectangleGrabInfo) grabInfo;
			Line2D grabLine = rectGrabInfo.getLine();
			modelGraphics.drawLine(grabLine.pointA(), grabLine.pointB());
		}
	}
	
	@Override
	public void addPopupItems(Editable item, ItemGrabInfo<?> grabInfo, JPopupMenu popupMenu, Point2D modelPoint) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ItemGrabInfo<?> getGrabInfo(Editable item, ModelMouseEvent modelMouseEvent, double minSnapSize) {
		Bounds2D bounds = item.calculateBounds();
		
		long ldx = (long) (bounds.left-modelMouseEvent.modelX);
		long rdx = (long) (bounds.right -modelMouseEvent.modelX);
		long tdy = (long) (bounds.top -modelMouseEvent.modelY);
		long bdy = (long) (bounds.bottom -modelMouseEvent.modelY);
		
		EditableRectangle itemRect = (EditableRectangle) item;
		ItemRectangleGrabInfo grabInfo = Stream.of(
				new ItemRectangleGrabInfo(itemRect, false, false, bdy*bdy + rdx*rdx),
				new ItemRectangleGrabInfo(itemRect, false, true , bdy*bdy + ldx*ldx),
				new ItemRectangleGrabInfo(itemRect, true , false, tdy*tdy + rdx*rdx),
				new ItemRectangleGrabInfo(itemRect, true , true , tdy*tdy + ldx*ldx)
				)
		.filter(s -> s.getSquareDistance()<minSnapSize)
		.min((a,b) -> Long.compare(a.getSquareDistance(), b.getSquareDistance()))
		.orElse(null);
		if (grabInfo != null) {
			return grabInfo;
		}

		Point2D pa = new Point2D(bounds.left, bounds.top);
		Point2D pb = new Point2D(bounds.right, bounds.top);
		Point2D pc = new Point2D(bounds.right, bounds.bottom);
		Point2D pd = new Point2D(bounds.left, bounds.bottom);
		Line2D top = new Line2D(pa, pb);
		Line2D right = new Line2D(pb, pc);
		Line2D bottom = new Line2D(pc, pd);
		Line2D left = new Line2D(pd, pa);
		
		
		Point2D mp = new Point2D(Math.round(modelMouseEvent.modelX), Math.round(modelMouseEvent.modelY));

		ItemSideRectangleGrabInfo secGrabInfo = Stream.of(
				new ItemSideRectangleGrabInfo(itemRect, Math.round(top.pointDistanceSq(mp)), SquareSide.TOP),
				new ItemSideRectangleGrabInfo(itemRect, Math.round(right.pointDistanceSq(mp)), SquareSide.RIGHT),
				new ItemSideRectangleGrabInfo(itemRect, Math.round(bottom.pointDistanceSq(mp)), SquareSide.BOTTOM),
				new ItemSideRectangleGrabInfo(itemRect, Math.round(left.pointDistanceSq(mp)), SquareSide.LEFT)
				)
			.filter(s -> s.getSquareDistance()<minSnapSize)
			.min((a,b) -> Long.compare(a.getSquareDistance(), b.getSquareDistance()))
			.orElse(null);
		return secGrabInfo;
	
	}

	enum SquareSide {
		TOP,
		RIGHT,
		BOTTOM,
		LEFT
	}
	
	public static class ItemSideRectangleGrabInfo extends ItemGrabInfo<EditableRectangle> {

		private final SquareSide squareSide;
		private Line2D line;
		private Bounds2D boundsForLine;

		long dragStartX;
		long dragStartY;
		Line2D dragStartLine;

		public ItemSideRectangleGrabInfo(EditableRectangle item, long distSq, SquareSide squareSide) {
			super(item, distSq);
			this.squareSide = squareSide;
		}

		public Line2D getLine() {
			Bounds2D bounds = item.getBounds();
			if (line == null || !Objects.equals(bounds, boundsForLine)) {
				boundsForLine = bounds;
				switch(squareSide) {
					case BOTTOM : 
						line = new Line2D(new Point2D(bounds.left, bounds.bottom), new Point2D(bounds.right, bounds.bottom));
						break;
					case TOP : 
						line = new Line2D(new Point2D(bounds.left, bounds.top), new Point2D(bounds.right, bounds.top));
						break;
					case LEFT :
						line = new Line2D(new Point2D(bounds.left, bounds.top), new Point2D(bounds.left, bounds.bottom));
						break;
					case RIGHT :
						line = new Line2D(new Point2D(bounds.right, bounds.top), new Point2D(bounds.right, bounds.bottom));
						break;
				}
			}
			return line;
		}

		@Override
		public void startDrag(long mouseX, long mouseY) {
			dragStartX = mouseX;
			dragStartY = mouseY;
			dragStartLine = getLine();
			
		}

		@Override
		public void doDrag(Camera camera, long mouseX, long mouseY) {
			line = null;

			Bounds2D bounds = item.getBounds();
			long deltaX = mouseX - dragStartX;
			long deltaY = mouseY - dragStartY;
			long modelDeltaX = camera.toModelSize(deltaX);
			long modelDeltaY = camera.toModelSize(deltaY);
			switch(squareSide) {
				case BOTTOM :
					bounds = new Bounds2D(bounds.left, dragStartLine.pointA().getY()-modelDeltaY, bounds.right, bounds.top);
					break;
				case TOP :
					bounds = new Bounds2D(bounds.left, bounds.bottom, bounds.right, dragStartLine.pointA().getY()-modelDeltaY);
					break;
				case LEFT :
					bounds = new Bounds2D(dragStartLine.pointA().getX()+modelDeltaX, bounds.bottom, bounds.right, bounds.top);
					break;
				case RIGHT :
					bounds = new Bounds2D(bounds.left, bounds.bottom, dragStartLine.pointA().getX()+modelDeltaX, bounds.top);
					break;
			}
			item.setBounds(bounds);
		}

		@Override
		public void delete() {
			
		}

		@Override
		public boolean isSame(ItemGrabInfo<?> other) {
			if (other == this) {
				return true;
			}
			if (other instanceof ItemSideRectangleGrabInfo) {
				ItemSideRectangleGrabInfo that = (ItemSideRectangleGrabInfo) other;
				return that.item == item && that.squareSide == squareSide;
			}
			return false;
		}
		
		@Override
		protected boolean isValid() {
			return true;
		}
		
	}

	
	public static class ItemRectangleGrabInfo extends ItemGrabInfo<EditableRectangle> implements LocationFeature {
		
		private final boolean top;
		private final boolean left;
		
		long dragStartX;
		long dragStartY;
		Point2D dragStartPoint;
		
		public ItemRectangleGrabInfo(EditableRectangle item, boolean top, boolean left, long distanceSq) {
			super(item, distanceSq);
			this.top = top;
			this.left = left;
		}

		@Override
		public void startDrag(long mouseX, long mouseY) {
			dragStartX = mouseX;
			dragStartY = mouseY;
			dragStartPoint = getPoint();
		}

		@Override
		public void doDrag(Camera camera, long mouseX, long mouseY) {
			Bounds2D bounds = item.getBounds();
			long deltaX = mouseX - dragStartX;
			long deltaY = mouseY - dragStartY;
			long modelDeltaX = camera.toModelSize(deltaX);
			long modelDeltaY = camera.toModelSize(deltaY);
			long newX = dragStartPoint.x + modelDeltaX;
			if (left) {
				bounds = new Bounds2D(newX, bounds.bottom, bounds.right, bounds.top);
			} else {
				bounds = new Bounds2D(bounds.left, bounds.bottom, newX, bounds.top);
			}
			
			long newY = dragStartPoint.y - modelDeltaY;
			if (top) {
				bounds = new Bounds2D(bounds.left, bounds.bottom, bounds.right, newY);
			} else {
				bounds = new Bounds2D(bounds.left, newY, bounds.right, bounds.top);
			}
			item.setBounds(bounds);
		}
		

		public Point2D getPoint() {
			Bounds2D bounds = item.getBounds();
			return Point2D.of(left ? bounds.left : bounds.right, top ? bounds.top : bounds.bottom);
		}

		@Override
		public void delete() {
			
		}

//
//		@Override
//		public void setLocation(long newX, long newY) {
//			Bounds2D bounds = item.getBounds();
//			if (left) {
//				bounds = new Bounds2D(newX, bounds.bottom, bounds.right, bounds.top);
//			} else {
//				bounds = new Bounds2D(bounds.left, bounds.bottom, newX, bounds.top);
//			}
//			
//			if (top) {
//				bounds = new Bounds2D(bounds.left, bounds.bottom, bounds.right, newY);
//			} else {
//				bounds = new Bounds2D(bounds.left, newY, bounds.right, bounds.top);
//			}
//			item.setBounds(bounds);
//		}


		@Override
		public long getX() {
			Bounds2D bounds = item.getBounds();
			return left ? bounds.left : bounds.right;
		}


		@Override
		public long getY() {
			Bounds2D bounds = item.getBounds();
			return top ? bounds.top : bounds.bottom;
		}


		@Override
		public void setX(long newX) {
			Bounds2D bounds = item.getBounds();
			if (left) {
				bounds = new Bounds2D(newX, bounds.bottom, bounds.right, bounds.top);
			} else {
				bounds = new Bounds2D(bounds.left, bounds.bottom, newX, bounds.top);
			}
			item.setBounds(bounds);
		}


		@Override
		public void setY(long newY) {
			Bounds2D bounds = item.getBounds();
			if (top) {
				bounds = new Bounds2D(bounds.left, bounds.bottom, bounds.right, newY);
			} else {
				bounds = new Bounds2D(bounds.left, newY, bounds.right, bounds.top);
			}
			item.setBounds(bounds);
		}

		@Override
		public boolean isSame(ItemGrabInfo<?> other) {
			if (other instanceof ItemRectangleGrabInfo) {
				ItemRectangleGrabInfo that = (ItemRectangleGrabInfo) other;
				return that.item == item && that.top==top && that.left==left;
			}
			return false;
		}

		@Override
		protected boolean isValid() {
			return true;
		}
	}
	
}
