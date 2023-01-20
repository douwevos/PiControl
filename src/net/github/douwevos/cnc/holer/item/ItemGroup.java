package net.github.douwevos.cnc.holer.item;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.github.douwevos.cnc.holer.HolerModelRun.RunContext;
import net.github.douwevos.cnc.holer.ItemGrabInfo;
import net.github.douwevos.cnc.holer.HolerModel.ToolItemRun;
import net.github.douwevos.cnc.holer.design.SelectionModel;
import net.github.douwevos.cnc.holer.design.ViewCamera;
import net.github.douwevos.cnc.layer.Layer;
import net.github.douwevos.cnc.layer.LayerDescription;
import net.github.douwevos.justflat.values.Bounds2D;

public class ItemGroup implements Item {

	private List<Item> items;
	private final Bounds2D bounds;

	public ItemGroup(Item ... items) {
		this.items = new ArrayList<>(Arrays.asList(items));
		Bounds2D bounds = null;
		for(Item item : items) {
			Bounds2D itemBounds = item.bounds();
			if (itemBounds == null) {
				continue;
			}
			if (bounds == null) {
				bounds = itemBounds;
			} else {
				bounds = bounds.union(itemBounds);
			}
		}
		this.bounds = bounds;
	}

	@Override
	public void paint(Graphics2D gfx, ToolItemRun toolItemRun, ViewCamera designView, SelectionModel selectionModel) {
		for(Item item : items) {
			item.paint(gfx, toolItemRun, designView, selectionModel);
		}
	}

	@Override
	public void run(RunContext runContext) {
		for(Item item : items) {
			item.run(runContext);
		}
	}

	@Override
	public ItemGrabInfo createGrabInfo(int mouseX, int mouseY) {
		return null;
	}
	
	@Override
	public Bounds2D bounds() {
		return bounds;
	}
	
	
}
