package net.github.douwevos.cnc.holer;

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
import net.github.douwevos.cnc.tool.Tool;
import net.github.douwevos.cnc.type.Distance;
import net.github.douwevos.cnc.type.DistanceUnit;
import net.github.douwevos.justflat.logging.Log;
import net.github.douwevos.justflat.values.Bounds2D;

public class ModelRun {

	private Log log = Log.instance(true);
	
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
		long toolDiameter = 1900;
		
		
		Long nextDepth = planFactory.nextLayerDepth(layer, 0, maxDrop/2);
		while(nextDepth != null) {
			List<CncPlan> planList = planFactory.producePlanList(layer, toolDiameter, nextDepth);
			for(CncPlan plan : planList) {
				if (plan==null) {
					log.error("Error: plan is null");
					continue;
				}
				if (runContext == null) {
					log.error("Error: runContext is null");
				}
				runContext.setFloating(true);
				plan.streamItems().forEach(pi -> {
					if (pi==null) {
						log.error("Error: pi is null");
					}else {
						pi.runCnc(runContext);
					}
				});
			}
			
			long min = nextDepth+1;
			long max = min + maxDrop-1;
			
			nextDepth = planFactory.nextLayerDepth(layer, min, max);
		}
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
			actionQueue.lineTo(withFloat(location), isFloatingAbove ? CncHeadSpeed.FAST : speed);
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
