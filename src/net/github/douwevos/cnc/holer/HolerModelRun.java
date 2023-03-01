//package net.github.douwevos.cnc.holer;
//
//import java.util.List;
//import java.util.concurrent.atomic.AtomicBoolean;
//
//import net.github.douwevos.cnc.head.CncActionQueue;
//import net.github.douwevos.cnc.head.CncConfiguration;
//import net.github.douwevos.cnc.head.CncContext;
//import net.github.douwevos.cnc.head.CncHeadService;
//import net.github.douwevos.cnc.head.CncHeadSpeed;
//import net.github.douwevos.cnc.head.MicroLocation;
//import net.github.douwevos.cnc.holer.HolerModel.ToolItemRun;
//import net.github.douwevos.cnc.holer.item.Item;
//import net.github.douwevos.cnc.layer.Layer;
//import net.github.douwevos.cnc.layer.LayerDescription;
//import net.github.douwevos.cnc.layer.StartStopLayer;
//import net.github.douwevos.cnc.run.Chain;
//import net.github.douwevos.cnc.run.LayeredCncContext;
//import net.github.douwevos.cnc.tool.Tool;
//import net.github.douwevos.cnc.type.Distance;
//import net.github.douwevos.justflat.shape.PolygonLayer;
//
//public class HolerModelRun implements Runnable {
//
//	private final CncConfiguration configuration;
//	private final HolerModel holerModel;
//	
//	private final CncHeadService cncHeadService;
//	
//	private CncActionQueue actionQueue;
//	
//	private volatile RunContext runContext;
//	
//	public HolerModelRun(CncConfiguration configuration, HolerModel holerModel, CncHeadService cncHeadService) {
//		this.configuration = configuration;
//		this.holerModel = holerModel;
//		this.cncHeadService = cncHeadService;
//	}
//	
//	@Override
//	public void run() {
//		Distance height = holerModel.getHeight();
//		
//		
//		CncContext context = cncHeadService.getContext();
//		actionQueue = context.getActiveQueue().branch(false);
//		context.setSourcePiece(holerModel.getSourcePiece());
//		
//		runContext = new RunContext(configuration, actionQueue, holerModel);
//		
////		CompileContext compileContext = new CompileContext();
////		RunBlock runBlock = new RunBlock(0, 0, 0, holerModel.getWidth().asMicrometers(), holerModel.getHeight().asMicrometers(), holerModel.getDeptth().asMicrometers());
//
//		List<ToolItemRun> itemRuns = holerModel.getToolItemRuns();
//		for(ToolItemRun run : itemRuns) {
//			runToolItemRun(runContext, run);
//		}
//		
//		actionQueue.returnFromBranch(true);
//		context.setSourcePiece(null);
//	}
//
//	private void runToolItemRun(RunContext runContext, ToolItemRun itemRun) {
//		runContext.setSelectedTool(itemRun.getTool());
//		
//		long heightMicros = holerModel.getHeight().asMicrometers();
//		long widthMicros = holerModel.getWidth().asMicrometers();
//		
//		Layer modelLayer = new StartStopLayer(widthMicros, (int) heightMicros);
//		
//		int scalar = 2;
//		LayerDescription layerDescription = new LayerDescription(1, (int) Math.ceil(widthMicros/scalar), (int) Math.ceil(heightMicros/scalar));
//		
//		for(Item item : itemRun) {
//			Layer layer = item.produceLayer(layerDescription);
//			if (layer != null) {
//				modelLayer.merge(layer);
//			} else {
//				item.run(runContext);
//			}
//		}
//		runContext.setFloating(true);
//		
//		
//		if (!modelLayer.isEmpty()) {
//			LayeredCncContext layeredCncContext = new LayeredCncContext(itemRun.getTool(), 0, null);
//			List<Chain> chains = layeredCncContext.calculateModelAndCnc(runContext, System.nanoTime());
//			
//			for(Chain chain : chains) {
//				doCncChain(runContext, chain);
//			}
//		}
//		
//	}
//
//	private void doCncChain(RunContext runContext, Chain chain) {
//		runContext.setFloating(true);
//		for(MicroLocation location : chain.locations) {
//			runContext.lineTo(location, CncHeadSpeed.NORMAL);
//			runContext.setFloating(false);
//		}
//		runContext.setFloating(true);
//	}
//
//	public static class Layers {
//		
//		public long depth;
//		public PolygonLayer shapeLayer;
//		public PolygonLayer fillLayer;
//	}
//	
//	
//	public static class RunContext {
//	
//		private long floatingHeight = -Distance.ofMillMeters(6).asMicrometers();
//
//		private final CncConfiguration configuration;
//		private final CncActionQueue actionQueue;
//		private final HolerModel holerModel;
//		
//		private Tool tool;
//		boolean isFloatingAbove;
//		private MicroLocation last = new MicroLocation(0,0,0);
//		private AtomicBoolean nextItem = new AtomicBoolean(false);
//
//		public RunContext(CncConfiguration configuration, CncActionQueue actionQueue, HolerModel holerModel) {
//			this.configuration = configuration;
//			this.actionQueue = actionQueue;
//			this.holerModel = holerModel;
//		}
//
//		public void setSelectedTool(Tool tool) {
//			this.tool = tool;
//		}
//		
//		public Tool getSelectedTool() {
//			return tool;
//		}
//		
//		public CncConfiguration getConfiguration() {
//			return configuration;
//		}
//
//		public void setFloating(boolean isFloatingAbove) {
//			if (this.isFloatingAbove == isFloatingAbove) {
//				return;
//			}
//			this.isFloatingAbove = isFloatingAbove;
//			
//			if (isFloatingAbove) {
//				actionQueue.lineTo(withFloat(last), CncHeadSpeed.FAST);
//			} 
//			else {
//				actionQueue.lineTo(last.withZ(0), CncHeadSpeed.SLOW);
//				actionQueue.lineTo(last, CncHeadSpeed.VERY_SLOW);
//			}
//			
//		}
//		
//		public MicroLocation withFloat(MicroLocation p) {
//			if (isFloatingAbove) {
//				return p.withZ(floatingHeight);
//			}
//			return p;
//		}
//
//		public void lineTo(MicroLocation location, CncHeadSpeed speed) {
//			actionQueue.lineTo(withFloat(location), speed);
//			last = location;
//		}
//
//		public long dropSpeed(Tool tool) {
//			return configuration.getMaxDrop();
//		}
//
//		public void nextItem() {
//			nextItem.set(true);
//		}
//		
//		public boolean shouldMoveToNextItem() {
//			return nextItem.getAndSet(false);
//		}
//
////		public long dropSpeed(Tool tool) {
//////			private static final long DROP_SPEED = 70*4; // item-circle
//////			return 175; // ALU
//////			return 250; // wood
//////			return 2500; // soft_wood
////			return 1000; // siebdruck-platte
////		}
//		
//	}
//	
//	
//	public CncActionQueue getActionQueue() {
//		return actionQueue;
//	}
//
//	public void nextItem() {
//		RunContext lcRunContext = runContext;
//		if (lcRunContext != null) {
//			lcRunContext.nextItem();
//		}
//	}
//}
