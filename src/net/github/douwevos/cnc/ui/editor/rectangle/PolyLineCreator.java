package net.github.douwevos.cnc.ui.editor.rectangle;

import java.util.ArrayList;
import java.util.List;

import net.github.douwevos.cnc.model.EditableLayer;
import net.github.douwevos.cnc.model.EditablePolyLine;
import net.github.douwevos.cnc.model.value.PolyLine;
import net.github.douwevos.cnc.poly.PolyDot;
import net.github.douwevos.cnc.poly.PolyForm;
import net.github.douwevos.cnc.ui.ModelGraphics;
import net.github.douwevos.cnc.ui.ModelMouseEvent;
import net.github.douwevos.justflat.types.values.Point2D;

public class PolyLineCreator implements EditableCreator {

	EditablePolyLine polyLine;
	
	ItemPolyLineController itemPolyLineController = new ItemPolyLineController();
	
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
	public void paint(ModelGraphics modelGraphics, EditableLayer editableLayer) {
		if (polyLine!=null) {
			itemPolyLineController.paint(modelGraphics, editableLayer, polyLine);
		}
	}
	
}
