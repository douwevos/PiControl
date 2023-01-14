package net.github.douwevos.cnc.ui.editor.rectangle;

import java.util.ArrayList;
import java.util.List;

import net.github.douwevos.cnc.model.Editable;
import net.github.douwevos.cnc.model.EditableLayer;
import net.github.douwevos.cnc.model.EditablePolyLine;
import net.github.douwevos.cnc.model.value.PolyLine;
import net.github.douwevos.cnc.poly.PolyDot;
import net.github.douwevos.cnc.poly.PolyForm;
import net.github.douwevos.cnc.ui.ModelGraphics;
import net.github.douwevos.cnc.ui.ModelMouseEvent;
import net.github.douwevos.justflat.types.values.Point2D;

public class PolyLineCreator implements EditableCreator {

	private EditablePolyLine polyLine;
	
	private ItemPolyLineController itemPolyLineController = new ItemPolyLineController();
	
	private Point2D modelMouseLocation;
	
	@Override
	public void moveLocationTo(Point2D modelMouseLocation) {
		this.modelMouseLocation = modelMouseLocation;
	}
	
	@Override
	public boolean clicked(ModelMouseEvent modelEvent) {
		Point2D modelPoint = modelEvent.createModelPoint();
		PolyDot polyDot = new PolyDot(modelPoint.x, modelPoint.y, false);
		if (polyLine == null) {
			List<PolyDot> dotList = new ArrayList<>();
			dotList.add(polyDot);
			PolyForm polyForm  = new PolyForm(dotList , true);
			polyLine = new EditablePolyLine(polyForm, 10);
		} else {
			PolyForm polyForm = polyLine.getPolyForm();
			polyForm = polyForm.add(polyDot);
			polyLine.setPolyForm(polyForm);
		}
		return true;
	}
	
	@Override
	public Editable createEditable() {
		return polyLine;
	}

	@Override
	public void paint(ModelGraphics modelGraphics, EditableLayer editableLayer) {
		if (polyLine!=null) {
			itemPolyLineController.paint(modelGraphics, editableLayer, polyLine, modelMouseLocation, false);
			if (modelMouseLocation != null) {
				modelGraphics.colorHighlight();
				PolyForm polyForm = polyLine.getPolyForm();
				PolyDot polyDot = polyForm.get(0);
				Point2D polyPoint = new Point2D(polyDot.x, polyDot.y);
				modelGraphics.drawLine(modelMouseLocation, polyPoint);
				int dotCount = polyForm.dotCount();
				if (dotCount>1) {
					polyDot = polyForm.get(dotCount-1);
					polyPoint = new Point2D(polyDot.x, polyDot.y);
					modelGraphics.drawLine(modelMouseLocation, polyPoint);
					
				}
			}
		}
		
	}
	
}
