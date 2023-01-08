package net.github.douwevos.cnc.ui.editor.rectangle;

import net.github.douwevos.cnc.model.EditableLayer;
import net.github.douwevos.cnc.ui.ModelGraphics;
import net.github.douwevos.cnc.ui.ModelMouseEvent;

public interface EditableCreator {

	boolean clicked(ModelMouseEvent modelEvent);

	void paint(ModelGraphics modelGraphics, EditableLayer editableLayer);

}
