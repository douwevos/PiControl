package net.github.douwevos.cnc.holer.item;

import java.awt.Graphics2D;

import douwe.Point3D;
import net.github.douwevos.cnc.head.CncHeadSpeed;
import net.github.douwevos.cnc.head.MicroLocation;
import net.github.douwevos.cnc.holer.HolerModel.ToolItemRun;
import net.github.douwevos.cnc.holer.HolerModelRun.RunContext;
import net.github.douwevos.cnc.holer.ItemGrabInfo;
import net.github.douwevos.cnc.holer.design.SelectionModel;
import net.github.douwevos.cnc.holer.design.ViewCamera;
import net.github.douwevos.cnc.layer.Layer;
import net.github.douwevos.cnc.layer.LayerDescription;
import net.github.douwevos.cnc.layer.StartStopLayer;
import net.github.douwevos.cnc.tool.Tool;
import net.github.douwevos.cnc.type.Distance;
import net.github.douwevos.justflat.types.values.Bounds2D;

public class ItemBoreHole implements Item {
	
	public final Point3D location;
	public final int depth;
	private final Bounds2D bounds;

	public ItemBoreHole(Point3D location, int depth) {
		this.location = location;
		this.depth = depth;
		this.bounds = createBounds();
	}

	public ItemBoreHole(Point3D location, Distance depth) {
		this.location = location;
		this.depth = (int) depth.asMicrometers();
		this.bounds = createBounds();
	}

	
	private Bounds2D createBounds() {
		return new Bounds2D(location.x, location.y, location.x, location.y);
	}

	@Override
	public void paint(Graphics2D gfx, ToolItemRun toolItemRun, ViewCamera designView, SelectionModel selectionModel) {
		long x = location.x;
		long y = location.y;
		Tool tool = toolItemRun.getTool();
		int diameter = tool.getDiameter();
		int radius = diameter/2;
		gfx.fillArc((int) x - radius, (int) y-radius, diameter, diameter, 0, 360);
	}
	
	@Override
	public void run(RunContext runContext) {
		runContext.setFloating(true);
		runContext.lineTo(MicroLocation.of(location), CncHeadSpeed.NORMAL);
		runContext.setFloating(false);
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
