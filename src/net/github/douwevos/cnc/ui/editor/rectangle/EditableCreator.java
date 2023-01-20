package net.github.douwevos.cnc.ui.editor.rectangle;

import net.github.douwevos.cnc.model.Editable;
import net.github.douwevos.cnc.model.EditableLayer;
import net.github.douwevos.cnc.ui.ModelGraphics;
import net.github.douwevos.cnc.ui.ModelMouseEvent;
import net.github.douwevos.justflat.values.Point2D;

public interface EditableCreator {

	void moveLocationTo(Point2D modelMouseLocation);
	boolean clicked(ModelMouseEvent modelEvent);
	Editable createEditable();

	void paint(ModelGraphics modelGraphics, EditableLayer editableLayer);

}
