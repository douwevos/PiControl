package net.github.douwevos.cnc.holer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import net.github.douwevos.cnc.head.CncActionQueue;
import net.github.douwevos.cnc.head.CncConfiguration;
import net.github.douwevos.cnc.head.CncContext;
import net.github.douwevos.cnc.head.CncHeadService;
import net.github.douwevos.cnc.head.CncHeadSpeed;
import net.github.douwevos.cnc.head.MicroLocation;
import net.github.douwevos.cnc.model.value.Item;
import net.github.douwevos.cnc.model.value.Layer;
import net.github.douwevos.cnc.model.value.Model;
import net.github.douwevos.cnc.plan.CncPlan;
import net.github.douwevos.cnc.plan.CncPlanFactory;
import net.github.douwevos.cnc.plan.CncPlanItem;
import net.github.douwevos.cnc.tool.Tool;
import net.github.douwevos.cnc.type.Distance;
import net.github.douwevos.cnc.type.DistanceUnit;
import net.github.douwevos.justflat.shape.PolygonLayer;
import net.github.douwevos.justflat.shape.PolygonLayerNonSimpleToSimpleSplitter;
import net.github.douwevos.justflat.shape.PolygonLayerResolutionReducer;
import net.github.douwevos.justflat.shape.scaler.PolygonLayerScaler;
import net.github.douwevos.justflat.values.Bounds2D;
import net.github.douwevos.justflat.values.Point2D;
import net.github.douwevos.justflat.values.shape.Polygon;

public class ModelRun {

	private final CncConfiguration configuration;
	private final CncHeadService cncHeadService;
	private final Model model;

	private CncActionQueue actionQueue;
	private RunContext runContext;

	
	public ModelRun(CncConfiguration configuration, CncHeadService cncHeadService, Model model) {
		this.configuration = configuration;
		this.cncHeadService = cncHeadService;
		this.model = model;
	}

	public void run() {
		SourcePiece sourcePiece = hackExtractSourcePiece(model);	// TODO source-piece should be configuravble
		
		CncContext context = cncHeadService.getContext();
		actionQueue = context.getActiveQueue().branch(false);
		context.setSourcePiece(sourcePiece);

		runContext = new RunContext(configuration, actionQueue, model);

		model.streamLayers().forEach(l -> runLayer(context, l));
		
		
		actionQueue.returnFromBranch(true);
		context.setSourcePiece(null);
	}

	
	
	private void runLayer(CncContext context, Layer layer) {
		CncPlanFactory planFactory = new CncPlanFactory();
		
		long maxDrop = configuration.getMaxDrop();
		long toolDiameter = 150;
		
		
		Long nextDepth = planFactory.nextLayerDepth(layer, 0, maxDrop/2);
		while(nextDepth != null) {
			List<CncPlan> planList = planFactory.producePlanList(layer, toolDiameter, nextDepth);
			for(CncPlan plan : planList) {
				runContext.setFloating(true);
				plan.streamItems().forEach(pi -> pi.runCnc(runContext));
			}
			
			long min = nextDepth+1;
			long max = min + maxDrop-1;
			
			nextDepth = planFactory.nextLayerDepth(layer, min, max);
		}
		
		
//
//		
//		
//		
//		
//		
//		int discSize = 800;
//		int discSizeSq = discSize*discSize;
//
//		PolygonLayerResolutionReducer resolutionReducer = new PolygonLayerResolutionReducer();
//		PolygonLayer reducedResolution = resolutionReducer.reduceResolution(contourLayer, discSizeSq, 1);
//
//		PolygonLayerNonSimpleToSimpleSplitter splitter = new PolygonLayerNonSimpleToSimpleSplitter();
//		PolygonLayer cutted = splitter.createSimplePolygonLayer(reducedResolution);
//
//		List<PolygonLayer> subLayers = new ArrayList<>();
//		
//		for(int idx=1; idx<100; idx++) {
//			PolygonLayer duplicate = cutted.duplicate();
//			PolygonLayerScaler contourLayerScaler = new PolygonLayerScaler();
//			PolygonLayer scaled = contourLayerScaler.scale(duplicate, idx*toolDiameter, false);
//			if (scaled.isEmpty()) {
//				break;
//			}
//			subLayers.add(scaled);
//		}
//
//		for(int idx=subLayers.size()-1; idx>=0; idx--) {
//			PolygonLayer subLayer = subLayers.get(idx);
//			cncSingleSubLayer(subLayer, depth);
//		}
		
		
	}

	private void cncSingleSubLayer(PolygonLayer subLayer, long depth) {
		subLayer.streamPolygons().forEach(p -> cncSinglePolygon(p, depth));
		
	}

	private void cncSinglePolygon(Polygon polygon, long depth) {
		runContext.setFloating(true);
		Point2D dotAt = polygon.dotAt(0);
		long mx = Distance.ofMillMeters(dotAt.x).asMicrometers()/1000;
		MicroLocation startMicroLocation = toMicroLocation(dotAt, depth);
		runContext.lineTo(startMicroLocation, CncHeadSpeed.FAST);
		runContext.setFloating(false);
		
		polygon.streamDots().forEach(dot -> {
			MicroLocation dotMicroLocation = toMicroLocation(dot, depth);
			runContext.lineTo(dotMicroLocation, CncHeadSpeed.NORMAL);
			System.out.println("moving to:"+dotMicroLocation);
		});
		
		runContext.setFloating(true);
	}
	
	private MicroLocation toMicroLocation(Point2D dot, long depth) {
		long mx = Distance.ofMillMeters(dot.x).asMicrometers()/1000;
		long my = Distance.ofMillMeters(dot.y).asMicrometers()/1000;
		return new MicroLocation(mx, my, depth);
	}

	private SourcePiece hackExtractSourcePiece(Model model) {
		return model.streamLayers().map(this::hackExtractSourcePiece).reduce(this::reduceSourcePiece).orElse(null);
	}


	private SourcePiece hackExtractSourcePiece(Layer layer) {
		return layer.streamItems().map(this::hackExtractSourcePiece).reduce(this::reduceSourcePiece).orElse(null);
	}
	
	private SourcePiece reduceSourcePiece(SourcePiece sourcepiece1, SourcePiece sourcepiece2) {
		if (sourcepiece1==null) {
			return sourcepiece2;
		}
		if (sourcepiece2==null) {
			return sourcepiece1;
		}
		Distance depth = sourcepiece1.getDepth().asMicrometers()>sourcepiece2.getDepth().asMicrometers() ? sourcepiece1.getDepth() : sourcepiece2.getDepth();
		Distance width = sourcepiece1.getWidth().asMicrometers()>sourcepiece2.getWidth().asMicrometers() ? sourcepiece1.getWidth() : sourcepiece2.getWidth();
		Distance height = sourcepiece1.getHeight().asMicrometers()>sourcepiece2.getHeight().asMicrometers() ? sourcepiece1.getHeight() : sourcepiece2.getHeight();
		return new SourcePiece(width, height, depth);
	}

	private SourcePiece hackExtractSourcePiece(Item item) {
		Bounds2D bounds = item.calculateBounds();
		long maxDepth = item.getMaxDepth();
		Distance distanceDepth = new Distance(maxDepth, DistanceUnit.MICROMETER);
		Distance distanceWidth = new Distance(bounds.right, DistanceUnit.MICROMETER);
		Distance distanceHeight = new Distance(bounds.top, DistanceUnit.MICROMETER);
		return new SourcePiece(distanceWidth, distanceHeight, distanceDepth);
	}



	public static class RunContext {
		
		private long floatingHeight = -Distance.ofMillMeters(6).asMicrometers();

		private final CncConfiguration configuration;
		private final CncActionQueue actionQueue;
		private final Model model;
		
		private Tool tool;
		boolean isFloatingAbove;
		private MicroLocation last = new MicroLocation(0,0,0);
		private AtomicBoolean nextItem = new AtomicBoolean(false);

		public RunContext(CncConfiguration configuration, CncActionQueue actionQueue, Model model) {
			this.configuration = configuration;
			this.actionQueue = actionQueue;
			this.model = model;
		}

		public void setSelectedTool(Tool tool) {
			this.tool = tool;
		}
		
		public Tool getSelectedTool() {
			return tool;
		}
		
		public CncConfiguration getConfiguration() {
			return configuration;
		}

		public void setFloating(boolean isFloatingAbove) {
			if (this.isFloatingAbove == isFloatingAbove) {
				return;
			}
			this.isFloatingAbove = isFloatingAbove;
			
			if (isFloatingAbove) {
				actionQueue.lineTo(withFloat(last), CncHeadSpeed.FAST);
			} 
			else {
				actionQueue.lineTo(last.withZ(0), CncHeadSpeed.SLOW);
				actionQueue.lineTo(last, CncHeadSpeed.VERY_SLOW);
			}
			
		}
		
		public MicroLocation withFloat(MicroLocation p) {
			if (isFloatingAbove) {
				return p.withZ(floatingHeight);
			}
			return p;
		}

		public void lineTo(MicroLocation location, CncHeadSpeed speed) {
			actionQueue.lineTo(withFloat(location), speed);
			last = location;
		}

		public long dropSpeed(Tool tool) {
			return configuration.getMaxDrop();
		}

		public void nextItem() {
			nextItem.set(true);
		}
		
		public boolean shouldMoveToNextItem() {
			return nextItem.getAndSet(false);
		}

//		public long dropSpeed(Tool tool) {
////			private static final long DROP_SPEED = 70*4; // item-circle
////			return 175; // ALU
////			return 250; // wood
////			return 2500; // soft_wood
//			return 1000; // siebdruck-platte
//		}
		
	}





	
}
