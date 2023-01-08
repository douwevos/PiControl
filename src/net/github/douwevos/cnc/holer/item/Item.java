package net.github.douwevos.cnc.holer.item;

import java.awt.Graphics2D;

import net.github.douwevos.cnc.holer.HolerModel.ToolItemRun;
import net.github.douwevos.cnc.holer.HolerModelRun.Layers;
import net.github.douwevos.cnc.holer.HolerModelRun.RunContext;
import net.github.douwevos.cnc.holer.ItemGrabInfo;
import net.github.douwevos.cnc.holer.design.SelectionModel;
import net.github.douwevos.cnc.holer.design.ViewCamera;
import net.github.douwevos.cnc.layer.Layer;
import net.github.douwevos.cnc.layer.LayerDescription;
import net.github.douwevos.justflat.types.values.Bounds2D;

public interface Item {

	void paint(Graphics2D gfx, ToolItemRun toolItemRun, ViewCamera designView, SelectionModel selectionModel);

	void run(RunContext runContext);

	ItemGrabInfo createGrabInfo(int mouseX, int mouseY);
	
	default Layer produceLayer(LayerDescription description) {
		return null;
	}
	
	default void write(Layers layers) {
	}
	

	Bounds2D bounds();
	
	
}
