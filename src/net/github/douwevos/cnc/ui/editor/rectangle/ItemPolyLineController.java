package net.github.douwevos.cnc.ui.editor.rectangle;

import java.awt.Color;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.util.Objects;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPopupMenu;

import net.github.douwevos.cnc.holer.feature.LocationFeature;
import net.github.douwevos.cnc.model.Editable;
import net.github.douwevos.cnc.model.EditableLayer;
import net.github.douwevos.cnc.model.EditablePolyLine;
import net.github.douwevos.cnc.poly.PolyDot;
import net.github.douwevos.cnc.poly.PolyForm;
import net.github.douwevos.cnc.poly.PolyForm.PolyOutput;
import net.github.douwevos.cnc.ui.Camera;
import net.github.douwevos.cnc.ui.ModelGraphics;
import net.github.douwevos.cnc.ui.ModelMouseEvent;
import net.github.douwevos.cnc.ui.editor.ItemController;
import net.github.douwevos.cnc.ui.editor.ItemGrabInfo;
import net.github.douwevos.justflat.values.FracPoint2D;
import net.github.douwevos.justflat.values.Line2D;
import net.github.douwevos.justflat.values.Point2D;

public class ItemPolyLineController implements ItemController {

	@Override
	public void paint(ModelGraphics modelGraphics, EditableLayer layer, Editable item) {
		paint(modelGraphics, layer, item, null, false);
	}

	
	public void paint(ModelGraphics modelGraphics, EditableLayer layer, Editable item, Point2D editPoint, boolean editIsCurve) {
		EditablePolyLine itemPolyline = (EditablePolyLine) item;
		PolyForm polyForm = itemPolyline.getPolyForm();
		GfxPolyOutput gfxPolyOutput = new GfxPolyOutput(modelGraphics, -1);
		if (editPoint!=null) {
			modelGraphics.colorSelection();
			polyForm = polyForm.add(new PolyDot(editPoint.x, editPoint.y, editIsCurve));
			polyForm.produce(gfxPolyOutput, 1d, 0, 0);
			return;
		}
		modelGraphics.colorDefault();
		polyForm.produce(gfxPolyOutput, 1d, 0, 0);
		
		modelGraphics.colorDot();
		for(PolyDot polyDot : polyForm) {
			modelGraphics.drawDot(new Point2D(polyDot.x, polyDot.y));
		}
		
	}

	@Override
	public void paintHighlighted(Editable item, ItemGrabInfo<?> grabInfo, ModelGraphics modelGraphics) {
		if (grabInfo instanceof ItemPolylineDotGrabInfo) {
			int dotIdx = ((ItemPolylineDotGrabInfo) grabInfo).dotIdx;
			PolyDot polyDot = ((EditablePolyLine) item).getPolyForm().get(dotIdx);
			Point2D point = new Point2D(polyDot.x, polyDot.y);
			modelGraphics.drawCircle(point, modelGraphics.getCamera().toModelSize(5), false);
		} else if (grabInfo instanceof ItemPolylineGrabInfo) {
			ItemPolylineGrabInfo polylineGrabInfo = (ItemPolylineGrabInfo) grabInfo;
			PolyForm polyForm = polylineGrabInfo.getItem().getPolyForm();
			GfxPolyOutput gfxPolyOutput = new GfxPolyOutput(modelGraphics, polylineGrabInfo.dotIndex);
			polyForm.produce(gfxPolyOutput, 1d, 0, 0);
		}
	}

	@Override
	public void paintSelected(Editable item, ItemGrabInfo<?> grabInfo, ModelGraphics modelGraphics) {
		if (grabInfo instanceof ItemPolylineDotGrabInfo) {
			int dotIdx = ((ItemPolylineDotGrabInfo) grabInfo).dotIdx;
			PolyDot polyDot = ((EditablePolyLine) item).getPolyForm().get(dotIdx);
			Point2D point = new Point2D(polyDot.x, polyDot.y);
			modelGraphics.drawCircle(point, modelGraphics.getCamera().toModelSize(5), true);
		} else if (grabInfo instanceof ItemPolylineGrabInfo) {
			ItemPolylineGrabInfo polylineGrabInfo = (ItemPolylineGrabInfo) grabInfo;
			PolyForm polyForm = polylineGrabInfo.getItem().getPolyForm();
			GfxPolyOutput gfxPolyOutput = new GfxPolyOutput(modelGraphics, polylineGrabInfo.dotIndex);
			polyForm.produce(gfxPolyOutput, 1d, 0, 0);
		}
	}

	@Override
	public void addPopupItems(Editable item, ItemGrabInfo<?> grabInfo, JPopupMenu popupMenu, Point2D modelPoint) {
		if (grabInfo instanceof ItemPolylineGrabInfo) {
			ItemPolylineGrabInfo dotGrabInfo = (ItemPolylineGrabInfo) grabInfo;
			Action actDummy = new AbstractAction("Add point") {
				public void actionPerformed(ActionEvent e) {
					int dotIdx = dotGrabInfo.dotIndex;
					EditablePolyLine editablePolyLine = dotGrabInfo.getItem();
					PolyForm polyForm = editablePolyLine.getPolyForm();
					PolyForm newPolyForm = polyForm.add(new PolyDot(modelPoint.x, modelPoint.y, false), dotIdx);
					editablePolyLine.setPolyForm(newPolyForm);
				}
			};
			popupMenu.add(actDummy);
		} else if (grabInfo instanceof ItemPolylineDotGrabInfo) {
			ItemPolylineDotGrabInfo dotGrabInfo = (ItemPolylineDotGrabInfo) grabInfo;
			Action actDummy = new AbstractAction("Delete point") {
				public void actionPerformed(ActionEvent e) {
					int dotIdx = dotGrabInfo.dotIdx;
					EditablePolyLine editablePolyLine = dotGrabInfo.getItem();
					PolyForm polyForm = editablePolyLine.getPolyForm();
					PolyForm newPolyForm = polyForm.remove(dotIdx);
					editablePolyLine.setPolyForm(newPolyForm);
				}
			};
			popupMenu.add(actDummy);
		}
		
	}
	
	@Override
	public ItemGrabInfo<?> getGrabInfo(Editable item, ModelMouseEvent modelMouseEvent, double minSnapSize) {
		
		double modelMouseX = modelMouseEvent.modelX;
		double modelMouseY = modelMouseEvent.modelY;

		double bestDistSq = 0;
		ItemGrabInfo<?> bestGrabInfo = null;
		
		EditablePolyLine itemPolyline = (EditablePolyLine) item;
		PolyForm polyForm = itemPolyline.getPolyForm();
		for(int dotIdx=0; dotIdx<polyForm.dotCount(); dotIdx++) {
			PolyDot polyDot = polyForm.get(dotIdx);
			double dx = polyDot.x-modelMouseX;
			double dy = polyDot.y-modelMouseY;
			double distSq = dx*dx + dy*dy;
			if (distSq>minSnapSize) {
				continue;
			}
			if (bestGrabInfo == null || distSq<bestDistSq) {
				bestGrabInfo = new ItemPolylineDotGrabInfo(itemPolyline, dotIdx, Math.round(distSq));
				bestDistSq = distSq;
			}
		}
		if (bestGrabInfo != null) {
			return bestGrabInfo;
		}
		
		
		PolyFromDistanceOuput polyFromDistanceOuput = new PolyFromDistanceOuput(new FracPoint2D(modelMouseX, modelMouseY).toNonFractional());
		polyForm.produce(polyFromDistanceOuput, 1d, 0, 0);
		
		double bestDistanceSq = polyFromDistanceOuput.getBestDistanceSq();
		if (bestDistanceSq<minSnapSize) {		
			return new ItemPolylineGrabInfo(itemPolyline, Math.round(bestDistanceSq), polyFromDistanceOuput.dotIndex);
		}
		return null;
	}
	
	public static class PolyFromDistanceOuput implements PolyOutput {

		Point2D point;
		double bestDistanceSq = Double.MAX_VALUE;
		int dotIndex;
		
		public PolyFromDistanceOuput(Point2D point) {
			this.point = point;
		}
		
		public double getBestDistanceSq() {
			return bestDistanceSq;
		}
		
		@Override
		public void contourBegin() {
			
		}

		@Override
		public void line(int dotIdxA, Point2D pointA, Point2D pointB) {
			if (pointA.equals(pointB)) {
				return;
			}
			Line2D line2d = new Line2D(pointA, pointB);
			double distanceSq = 2d+line2d.pointDistanceSq(point);
			if (distanceSq<bestDistanceSq) {
				bestDistanceSq = distanceSq;
				dotIndex = dotIdxA;
			}
		}

		@Override
		public void contourEnd() {
			
		}
		
	}

	public static class ItemPolylineDotGrabInfo extends ItemGrabInfo<EditablePolyLine> implements LocationFeature {

		public final int dotIdx;
		public PolyDot dot;
		
		long dragStartX;
		long dragStartY;
		PolyDot dragStartDot;

		
		public ItemPolylineDotGrabInfo(EditablePolyLine item, int dotIdx, long distanceSq) {
			super(item, distanceSq);
			this.dotIdx = dotIdx;
			dot = getDot();
		}

		@Override
		public void startDrag(long mouseX, long mouseY) {
			dragStartX = mouseX;
			dragStartY = mouseY;
			dragStartDot = getDot();
		}

		@Override
		public void doDrag(Camera camera, long mouseX, long mouseY) {
			PolyForm polyForm = item.getPolyForm();
			long deltaX = mouseX - dragStartX;
			long deltaY = mouseY - dragStartY;
			long modelDeltaX = camera.toModelSize(deltaX);
			long modelDeltaY = camera.toModelSize(deltaY);
			long newX = dragStartDot.x + modelDeltaX;
			long newY = dragStartDot.y - modelDeltaY;
			PolyDot polyDot = polyForm.get(dotIdx);
			PolyDot newPolyDot = new PolyDot(newX, newY, polyDot.isCurve);
			PolyForm newPolyForm = polyForm.set(dotIdx, newPolyDot);
			dot = newPolyDot;
			item.setPolyForm(newPolyForm);
		}
		

		
		public PolyDot getDot() {
			return item.getPolyForm().get(dotIdx);
		}

		@Override
		public void delete() {
			
		}

		@Override
		public long getX() {
			return item.getPolyForm().get(dotIdx).x;
		}


		@Override
		public long getY() {
			return item.getPolyForm().get(dotIdx).y;
		}


		@Override
		public void setX(long newX) {
			PolyForm polyForm = item.getPolyForm();
			PolyDot polyDot = polyForm.get(dotIdx);
			PolyDot polyDotNew = polyDot.withLocation(newX, polyDot.y);
			PolyForm polyFormNew = polyForm.set(dotIdx, polyDotNew);
			dot = polyDotNew;
			item.setPolyForm(polyFormNew);
		}


		@Override
		public void setY(long newY) {
			PolyForm polyForm = item.getPolyForm();
			PolyDot polyDot = polyForm.get(dotIdx);
			PolyDot polyDotNew = polyDot.withLocation(polyDot.x, newY);
			PolyForm polyFormNew = polyForm.set(dotIdx, polyDotNew);
			dot = polyDotNew;
			item.setPolyForm(polyFormNew);
		}
		
		@Override
		public boolean isSame(ItemGrabInfo<?> other) {
			if (other instanceof ItemPolylineDotGrabInfo) {
				ItemPolylineDotGrabInfo that = (ItemPolylineDotGrabInfo) other;
				return that.item == item && dotIdx == that.dotIdx;
			}
			return false;
		}
		
		@Override
		protected boolean isValid() {
			PolyForm polyForm = item.getPolyForm();
			if (dotIdx<0 || dotIdx>=polyForm.dotCount()) {
				return false;
			}
			return Objects.equals(polyForm.get(dotIdx), dot);
		}
		
	}
	
	public static class ItemPolylineGrabInfo extends ItemGrabInfo<EditablePolyLine> {

		public final int dotIndex;
		
		public ItemPolylineGrabInfo(EditablePolyLine item, long distanceSq, int dotIndex) {
			super(item, distanceSq);
			this.dotIndex = dotIndex;
		}

		@Override
		public void startDrag(long mouseX, long mouseY) {
			
		}

		@Override
		public void doDrag(Camera camera, long mouseX, long mouseY) {
			
		}

		@Override
		public void delete() {
			
		}

		@Override
		public boolean isSame(ItemGrabInfo<?> other) {
			if (other instanceof ItemPolylineGrabInfo) {
				ItemPolylineGrabInfo that = (ItemPolylineGrabInfo) other;
				return that.item == item && that.dotIndex == dotIndex;
			}
			return false;
		}
		
		@Override
		protected boolean isValid() {
			PolyForm polyForm = item.getPolyForm();
			if (dotIndex<0 || dotIndex>=polyForm.dotCount()) {
				return false;
			}
			return true;
		}
		
	}

	

	private static class GfxPolyOutput implements PolyForm.FracPolyOutput {

		private int dotIndex;
		private final ModelGraphics modelGraphics;
		
		public GfxPolyOutput(ModelGraphics modelGraphics, int dotIndex) {
			this.dotIndex = dotIndex;
			this.modelGraphics = modelGraphics;
		}

		@Override
		public void line(int dotIdxA, FracPoint2D pointA, FracPoint2D pointB) {
			if (dotIdxA==dotIndex || dotIndex<0) {
				modelGraphics.drawLine(pointA, pointB);
			}
		}

		@Override
		public void contourBegin() {
		}

		@Override
		public void contourEnd() {
		}
		
	}	
	
}
