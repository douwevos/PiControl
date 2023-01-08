package net.github.douwevos.cnc.holer.item;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import douwe.Point3D;
import net.github.douwevos.cnc.head.CncHeadSpeed;
import net.github.douwevos.cnc.head.MicroLocation;
import net.github.douwevos.cnc.holer.ItemGrabInfo;
import net.github.douwevos.cnc.holer.HolerModel.ToolItemRun;
import net.github.douwevos.cnc.holer.HolerModelRun.RunContext;
import net.github.douwevos.cnc.holer.design.ViewCamera;
import net.github.douwevos.cnc.holer.design.SelectionModel;
import net.github.douwevos.cnc.tool.Tool;
import net.github.douwevos.cnc.type.Distance;
import net.github.douwevos.justflat.types.values.Bounds2D;

public class ItemCircle implements Item {

	private static final long PUSH_UP = 100;
	public final Point3D location;
	public final long startDepth;
	public final int diameter;
	public final FillMode fill;
	private final Bounds2D bounds;
	
	
	public ItemCircle(Point3D location, int diameter, FillMode fill) {
		this.location = location;
		this.startDepth = 0;
		this.diameter = diameter;
		this.fill = fill;
		this.bounds = createBounds();
	}

	public ItemCircle(Point3D location, long startDepth, int diameter, FillMode fill) {
		this.location = location;
		this.startDepth = startDepth;
		this.diameter = diameter;
		this.fill = fill;
		this.bounds = createBounds();
	}

	public ItemCircle(Point3D location, Distance diameter, FillMode fill) {
		this.location = location;
		this.startDepth = 0;
		this.diameter = (int) diameter.asMicrometers();
		this.fill = fill;
		this.bounds = createBounds();
	}

	public ItemCircle(Point3D location, long startDepth, Distance diameter, FillMode fill) {
		this.location = location;
		this.startDepth = startDepth;
		this.diameter = (int) diameter.asMicrometers();
		this.fill = fill;
		this.bounds = createBounds();
	}

	
	private Bounds2D createBounds() {
		long r = diameter/2;
		return new Bounds2D(location.x-r, location.y-r, location.x+r, location.y+r);
	}

	
	@Override
	public void paint(Graphics2D gfx, ToolItemRun toolItemRun, ViewCamera designView, SelectionModel selectionModel) {
		Tool tool = toolItemRun.getTool();
		long x = location.x;
		long y = location.y;
		int radius = diameter/2;
		
		Color color = gfx.getColor();
		gfx.setColor(color.darker());
		int realDiameter = diameter + tool.getDiameter();
		int realRadius = (realDiameter)/2;
		
		if (fill==FillMode.NONE) {
			gfx.drawArc((int) x - realRadius, (int) y-realRadius, realDiameter, realDiameter, 0, 360);
			gfx.setColor(color);
			gfx.drawArc((int) x - radius, (int) y-radius, diameter, diameter, 0, 360);
		} else {
		
			gfx.fillArc((int) x - realRadius, (int) y-realRadius, realDiameter, realDiameter, 0, 360);
			gfx.setColor(color);
			gfx.fillArc((int) x - radius, (int) y-radius, diameter, diameter, 0, 360);
		}
	}
	
	
	@Override
	public void run(RunContext runContext) {
		Tool tool = runContext.getSelectedTool();
		runContext.setFloating(true);
		Context context = new Context();
		context.firstRound = true;
		boolean keepGoing = true;
		long dropSpeed = runContext.dropSpeed(tool);
		System.out.println("dropSpeed="+dropSpeed);
		long nextDropSpeed = dropSpeed/2;
		for(long z=startDepth + dropSpeed/4; keepGoing && !context.done;) {
//		for(long z=startDepth + dropSpeed; keepGoing;) {
			if (z>=location.z) {
				z = location.z;
				
				keepGoing = false;
			}
			context.z = z;
			switch(fill) {
				case NONE : {
					outerLine(runContext, context);
				} break;
				case LEFT_RIGHT : {
					innerFill(runContext, context);
					runContext.setFloating(true);
					outerLine(runContext, context);
					context.moveForward = !context.moveForward; 
				} break;
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
		int circumference = (int) (diameter*Math.PI);
		
		MicroLocation last = null;
		
		long useZ = context.z;
		for(int idx=0; !context.done && idx<=circumference; idx++) {
			long ry = Math.round(Math.cos(2d * Math.PI*idx/circumference) * diameter / 2d);
			long rx = Math.round(Math.sin(2d * Math.PI*idx/circumference) * diameter / 2d);
			
			MicroLocation to = new MicroLocation(location.x + rx, location.y + (context.moveForward ? -ry : ry), context.z);
			if (Objects.equals(last, to)) {
				continue;
			}
			if (idx==0) {
				runContext.lineTo(to.withZ(useZ - PUSH_UP), CncHeadSpeed.FAST);
			}
			runContext.lineTo(to, CncHeadSpeed.SLOW);
			runContext.setFloating(false);
			last = to;
			context.done |= runContext.shouldMoveToNextItem();
		}
		if (last!=null) {
			runContext.lineTo(last.withZ(useZ - PUSH_UP), CncHeadSpeed.FAST);
		}
	}

	private void innerFill(RunContext runContext, Context context) {
		Tool tool = runContext.getSelectedTool();
		int rTool = (2*tool.getDiameter()+2)/3;
		int rInner = (diameter-tool.getDiameter()/2+1)/2;
		
		List<Point3D> revs = new ArrayList<>();
		
		boolean keepGoing = true;
		for(int ry=rInner; keepGoing; ry -=rTool) {
			if (ry<=0) {
				keepGoing = false;
				ry = 0;
			}
			
//			x2 = r2-y2
			int rx2 = rInner*rInner - ry*ry;
			int rx = (int) Math.round(Math.sqrt(rx2));
			revs.add(new Point3D(rx,ry,0));
		}
		for(int i=revs.size()-2; i>=0; i--) {
			Point3D c = revs.get(i);
			revs.add(new Point3D(c.x, -c.y, 0));
		}
		

		if (!context.moveForward) {
			Collections.reverse(revs);
		}

		long useZ = context.z;
		boolean isFirst = true;
		MicroLocation last = null;
		for(int rIdx=0; rIdx<revs.size() && !context.done; rIdx++) {
			Point3D rev = revs.get(rIdx);
			MicroLocation left = new MicroLocation(location.x-rev.x, location.y+rev.y, useZ);
			MicroLocation right = new MicroLocation(location.x+rev.x, location.y+rev.y, useZ);

			MicroLocation from;
			MicroLocation to;

			
			if (context.moveRight) {
				from = left;
				to = right;
				context.moveRight = false;
			} else {
				from = right;
				to = left;
				context.moveRight = true;
			}
			if (isFirst) {
				runContext.lineTo(from.withZ((int) (useZ - PUSH_UP)), CncHeadSpeed.FAST);
				isFirst = false;
			}
			runContext.lineTo(from, CncHeadSpeed.NORMAL);
			runContext.setFloating(false);
			runContext.lineTo(to, CncHeadSpeed.NORMAL);
			last = to;
			context.done |= runContext.shouldMoveToNextItem();
		}
		
		if (last != null) {
			runContext.lineTo(last.withZ((int) (useZ - PUSH_UP)), CncHeadSpeed.FAST);
		}
	}
	
	
	static class Context {
		boolean moveRight;
		boolean moveForward;
		long z;
		boolean firstRound;
		boolean done;
	}
	
	
	private void spiralFill(RunContext runContext, Context context) {
		Tool tool = runContext.getSelectedTool();
		int circumference = (int) (diameter*Math.PI);
		long useZ = context.z;
		
		
		MicroLocation last = new MicroLocation(location.x, location.y, useZ);
		runContext.lineTo(last.withZ(useZ - PUSH_UP), context.firstRound ? CncHeadSpeed.FAST : CncHeadSpeed.SLOW);
		runContext.setFloating(false);
		runContext.lineTo(last, CncHeadSpeed.SLOW);
		
		
		int toolDiameter = tool.getDiameter();
		
		double stepR = (double) toolDiameter / circumference;
		stepR = stepR * 5d/9d;
		
		double currentR = 0;
		int step = 0;
		
		int endCircle = circumference+1;
		
		while(!context.done && endCircle>0) {

			step++;
			
			currentR += stepR; 

			
			int actualUsedDiameter = (int) Math.round(currentR);
			if (actualUsedDiameter>diameter) {
				actualUsedDiameter = diameter;
				endCircle--;
			}
			
			long ry = Math.round(Math.cos(2d * Math.PI*step/circumference) * actualUsedDiameter / 2d);
			long rx = Math.round(Math.sin(2d * Math.PI*step/circumference) * actualUsedDiameter / 2d);

			
			MicroLocation to = new MicroLocation(location.x + rx, location.y + ry, useZ);
			if (Objects.equals(last, to)) {
				continue;
			}
			
			runContext.lineTo(to, CncHeadSpeed.NORMAL);

			last = to;
			context.done |= runContext.shouldMoveToNextItem();

		}
		
		runContext.lineTo(last.withZ(useZ - PUSH_UP), CncHeadSpeed.FAST);
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
