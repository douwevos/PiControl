package net.github.douwevos.cnc.holer.item;

import java.awt.Color;
import java.awt.Graphics2D;

import net.github.douwevos.cnc.head.CncHeadSpeed;
import net.github.douwevos.cnc.head.MicroLocation;
import net.github.douwevos.cnc.holer.HolerModel.ToolItemRun;
import net.github.douwevos.cnc.holer.HolerModelRun.RunContext;
import net.github.douwevos.cnc.holer.ItemGrabInfo;
import net.github.douwevos.cnc.holer.design.SelectionModel;
import net.github.douwevos.cnc.holer.design.ViewCamera;
import net.github.douwevos.cnc.tool.Tool;
import net.github.douwevos.justflat.values.Bounds2D;

public class ItemRectangle implements Item {

	private static final long PUSH_UP = 2000;
	public final long startDepth;
	public final long depth;
	public final FillMode fill;
	private final Bounds2D bounds;
	
	
	public ItemRectangle(Bounds2D bounds, long startDepth, long depth, FillMode fill) {
		this.bounds = bounds;
		this.depth = depth;
		this.startDepth = startDepth;
		this.fill = fill;
	}

	@Override
	public void paint(Graphics2D gfx, ToolItemRun toolItemRun, ViewCamera designView, SelectionModel selectionModel) {
		Tool tool = toolItemRun.getTool();
		
		Color color = gfx.getColor();
		gfx.setColor(color.darker());
		
		int diameter = tool.getDiameter();
		int radius = diameter/2;
		
		int x = (int) bounds.left;
		int y = (int) bounds.bottom;
		int width = (int) (bounds.right-bounds.left);
		int height = (int) (bounds.top-bounds.bottom);
//		if (fill==FillMode.NONE) {
			gfx.drawRect(x-radius, y-radius, width+diameter, height+diameter);
			gfx.setColor(color);
			gfx.drawRect(x, y, width, height);
//		} else {
//			gfx.fillRect(x-radius, y-radius, width+diameter, height+diameter);
//			gfx.setColor(color);
//			gfx.fillRect(x, y, width, height);
//		}
	}
	
	
	@Override
	public void run(RunContext runContext) {
		Tool tool = runContext.getSelectedTool();
		runContext.setFloating(true);
		Context context = new Context();
		context.firstRound = true;
		boolean keepGoing = true;
		long dropSpeed = runContext.dropSpeed(tool);
		long nextDropSpeed = dropSpeed;
		for(long z=startDepth + dropSpeed; keepGoing;) {
			if (z>=depth) {
				z = depth;
				keepGoing = false;
			}
			context.z = z;
			switch(fill) {
				case NONE : {
					outerLine(runContext, context);
				} break;
//				case LEFT_RIGHT : {
//					innerFill(runContext, context);
//					runContext.setFloating(true);
//					outerLine(runContext, context);
//					context.moveForward = !context.moveForward; 
//				} break;
				case SPIRAL : {
					spiralFill(runContext, context);
				} break;
			}
			
			z+=nextDropSpeed;
			nextDropSpeed = dropSpeed;
			context.firstRound = false;
		}
		runContext.setFloating(true);

//		context.moveHeadDown();
	}

	private void outerLine(RunContext runContext, Context context) {
//		int circumference = (int) (diameter*Math.PI);
//		
//		MicroLocation last = null;
//		
//		long useZ = context.z;
//		for(int idx=0; idx<=circumference; idx++) {
//			long ry = Math.round(Math.cos(2d * Math.PI*idx/circumference) * diameter / 2d);
//			long rx = Math.round(Math.sin(2d * Math.PI*idx/circumference) * diameter / 2d);
//			
//			MicroLocation to = new MicroLocation(location.x + rx, location.y + (context.moveForward ? -ry : ry), context.z);
//			if (Objects.equals(last, to)) {
//				continue;
//			}
//			if (idx==0) {
//				runContext.lineTo(to.withZ(useZ - PUSH_UP), CncHeadSpeed.FAST);
//			}
//			runContext.lineTo(to, CncHeadSpeed.SLOW);
//			runContext.setFloating(false);
//			last = to;
//		}
//		if (last!=null) {
//			runContext.lineTo(last.withZ(useZ - PUSH_UP), CncHeadSpeed.FAST);
//		}
	}

	
	
	static class Context {
		boolean moveRight;
		boolean moveForward;
		long z;
		boolean firstRound;
	}
	
	
	private void spiralFill(RunContext runContext, Context context) {
		Tool tool = runContext.getSelectedTool();
		int toolDiameter = tool.getDiameter();
		long useZ = context.z;
		
		
		long width = bounds.right - bounds.left;
		long height = bounds.top - bounds.bottom;
		
		
		
		runContext.lineTo(new MicroLocation(bounds.left+width/2, bounds.bottom+height/2, useZ-PUSH_UP), CncHeadSpeed.FAST);
		runContext.setFloating(false);
		
		long maxDistance = width<height ? width : height;
		
		
		int stepCount = (int) ((maxDistance+toolDiameter-1) / toolDiameter);
		
		for(int step = 0; step<=stepCount; step++) {
			long dy = (step*height)/stepCount;
			long dx = (step*width)/stepCount;
		
			long left = bounds.left + (width-dx)/2;
			long right = bounds.left + (width+dx)/2;

			long bottom = bounds.bottom + (height-dy)/2;
			long top = bounds.bottom + (height+dy)/2;

			runContext.lineTo(new MicroLocation(left, bottom, useZ), CncHeadSpeed.NORMAL);
			runContext.lineTo(new MicroLocation(right, bottom, useZ), CncHeadSpeed.NORMAL);
			runContext.lineTo(new MicroLocation(right, top, useZ), CncHeadSpeed.NORMAL);
			runContext.lineTo(new MicroLocation(left, top, useZ), CncHeadSpeed.NORMAL);
			runContext.lineTo(new MicroLocation(left, bottom, useZ), CncHeadSpeed.NORMAL);
		}

		runContext.lineTo(new MicroLocation(bounds.left+width/2, bounds.bottom+height/2, useZ-PUSH_UP), CncHeadSpeed.FAST);

	}
	
	
	
	@Override
	public ItemGrabInfo createGrabInfo(int mouseX, int mouseY) {
		return null;
	}
	
	@Override
	public Bounds2D bounds() {
		return bounds;
	}

	
	public enum FillMode {
		NONE,
		LEFT_RIGHT,
		SPIRAL
	}
	
}
