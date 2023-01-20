package net.github.douwevos.cnc.ui.editor;

import javax.swing.JPopupMenu;

import net.github.douwevos.cnc.model.Editable;
import net.github.douwevos.cnc.model.EditableLayer;
import net.github.douwevos.cnc.ui.ModelGraphics;
import net.github.douwevos.cnc.ui.ModelMouseEvent;
import net.github.douwevos.justflat.values.Point2D;

public interface ItemController {

	void paint(ModelGraphics modelGraphics, EditableLayer layer, Editable item);
	void paintHighlighted(Editable item, ItemGrabInfo<?> grabInfo, ModelGraphics modelGraphics);
	void paintSelected(Editable item, ItemGrabInfo<?> selected, ModelGraphics modelGraphics);

	
	ItemGrabInfo<?> getGrabInfo(Editable item, ModelMouseEvent modelMouseEvent, double minSnapSize);
	void addPopupItems(Editable item, ItemGrabInfo<?> grabInfo, JPopupMenu popupMenu, Point2D modelPoint);
}
