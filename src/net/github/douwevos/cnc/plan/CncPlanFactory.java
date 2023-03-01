package net.github.douwevos.cnc.plan;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.github.douwevos.cnc.head.MicroLocation;
import net.github.douwevos.cnc.model.value.Item;
import net.github.douwevos.cnc.model.value.Layer;
import net.github.douwevos.cnc.model.value.Model;
import net.github.douwevos.cnc.type.Distance;
import net.github.douwevos.justflat.shape.PolygonLayer;
import net.github.douwevos.justflat.shape.PolygonLayerNonSimpleToSimpleSplitter;
import net.github.douwevos.justflat.shape.PolygonLayerResolutionReducer;
import net.github.douwevos.justflat.shape.scaler.PolygonLayerScaler;
import net.github.douwevos.justflat.values.Point2D;
import net.github.douwevos.justflat.values.shape.Polygon;
import net.github.douwevos.justflat.values.shape.PolygonLinesWrapper;

public class CncPlanFactory {

	public Long nextLayerDepth(Layer layer, long minDepth, long maxDepth) {
		NextLayerInfo nextLayerInfo = new NextLayerInfo();
		layer.streamItems().forEach(item -> {
			long maxItemDepth = item.getMaxDepth();
			if (maxItemDepth<minDepth) {
				return;
			}
			if (maxItemDepth>maxDepth) {
				maxItemDepth = maxDepth;
			}
			if (!nextLayerInfo.depthSet || nextLayerInfo.depth<maxItemDepth) {
				nextLayerInfo.depthSet = true;
				nextLayerInfo.depth = maxItemDepth;
			}
		});
		
		return nextLayerInfo.depthSet ? nextLayerInfo.depth : null;
	}
	
	private static class NextLayerInfo {
		boolean depthSet;
		long depth;
	}
	
	
	public List<CncPlan> producePlanList(Model model, int layerIndex, long toolDiameter, long depth) {
		Layer layer = model.layerAt(layerIndex);
		return producePlanList(layer, toolDiameter, depth);
	}

	
	public List<CncPlan> producePlanList(Layer layer, long toolDiameter, long depth) {
		PolygonLayer contourLayer = new PolygonLayer();
		for(Item item : layer) {
			item.writeToContourLayer(contourLayer, 0);
		}
		
		long discSize = toolDiameter;
		long discSizeSq = discSize*discSize;


		PolygonLayerResolutionReducer resolutionReducer = new PolygonLayerResolutionReducer();
		PolygonLayer reducedResolution = resolutionReducer.reduceResolution(contourLayer, discSizeSq, 1);

		PolygonLayerNonSimpleToSimpleSplitter splitter = new PolygonLayerNonSimpleToSimpleSplitter();
		PolygonLayer cutted = splitter.createSimplePolygonLayer(reducedResolution);

		
		List<PolygonLayer> subLayers =  new ArrayList<PolygonLayer>();

		for(int idx=1; idx<100; idx += 2) {
			PolygonLayer duplicate = cutted.duplicate();
			PolygonLayerScaler contourLayerScaler = new PolygonLayerScaler();
			PolygonLayer scaled = contourLayerScaler.scale(duplicate, (idx*toolDiameter)/2, false);
			if (scaled.isEmpty()) {
				break;
			}
			System.err.println("scaled.count="+scaled.count());
			subLayers.add(scaled);
		}
		System.err.println("subLayers="+subLayers);
		
		
		List<CncPlan> planList = subLayersToSingleCncPolygon(subLayers, toolDiameter, depth);
		
		
		List<CncPlan> planListFinalCut = finalCut(contourLayer, toolDiameter, depth);
		planList.addAll(planListFinalCut);
		
		return planList;
//		planViewModel.setPlanList(planList);
//		
////		planViewModel.setPlanPath(allContours);
//		
//		planViewModel.setGhostLayer(contourLayer);		
	}


	private List<CncPlan> finalCut(PolygonLayer cutted, long toolDiameter, long depth) {

		List<CncPlan> finalPlanList = new ArrayList<>();
		
		for(Polygon polygon : cutted) {
		
			List<Point2D> allDots = polygon.streamDots().collect(Collectors.toList());
			Point2D startDot = allDots.get(0);
			allDots.add(startDot);
			
			MicroLocation startMicroLocation = toMicroLocation(startDot, depth);
			CncPlan newPlan = new CncPlan();
			newPlan.add(new CncHeadState(true));
			newPlan.add(new CncLineTo(startMicroLocation));
			newPlan.add(new CncHeadState(false));
			
			allDots.stream()
				.map(dot -> toMicroLocation(dot, depth))
				.forEach(ml -> newPlan.add(new CncLineTo(ml)));
			finalPlanList.add(newPlan);
		}

		return finalPlanList;
	}


	private List<CncPlan> subLayersToSingleCncPolygon(List<PolygonLayer> subLayers, long toolDiameter, long depth) {
		List<List<PolygonNode>> polygonNodesLayers = new ArrayList<>();
		if (subLayers.isEmpty()) {
			return new ArrayList<>();
		}
		
		List<PolygonNode> polygonNodes = null;
		for(int idx=0; idx<subLayers.size(); idx++) {
			PolygonLayer polygonLayer = subLayers.get(idx);
			List<PolygonNode> parentPolygonNodes = polygonNodes;
			polygonNodes = polygonLayer.streamPolygons()
				.map(p -> producePolygonNode(p, parentPolygonNodes))
				.collect(Collectors.toList());
			polygonNodesLayers.add(polygonNodes);
		}
		
		
		for(int idx=polygonNodesLayers.size()-1; idx>0; idx--) {
			polygonNodes = polygonNodesLayers.get(idx);
			for(PolygonNode node : polygonNodes) {
				node.produceUpwardPlan(toolDiameter, depth);
			}
		}
		List<PolygonNode> topPolygonLayer = polygonNodesLayers.get(0);
		return topPolygonLayer.stream().filter(p -> p.plan != null).map(p -> p.plan).collect(Collectors.toList());
	}

	private PolygonNode producePolygonNode(Polygon polygon, List<PolygonNode> parentPolygonNodes) {
		if (parentPolygonNodes == null) {
			return new PolygonNode(null, polygon);
		}
		
		for(PolygonNode parentNode : parentPolygonNodes) {
			if (parentNode.containsPolygon(polygon)) {
				return new PolygonNode(parentNode, polygon);
			}
		}

//		log.error("should not hapen");
		return new PolygonNode(null, polygon);
	}

	static class PolygonNode {
		
		private final PolygonNode parent;
		private final List<PolygonNode> children = new ArrayList<>();
		private final Polygon polygon;
		private final PolygonLinesWrapper polygonLinesWrapper;
		private CncPlan plan;
		private Point2D planStart;
		private Point2D planEnd;
		
		public PolygonNode(PolygonNode parent, Polygon polygon) {
			this.parent = parent;
			this.polygon = polygon;
			polygonLinesWrapper = new PolygonLinesWrapper(polygon);
			if (parent != null) {
				parent.children.add(this);
			}
		}
		
		public boolean containsPolygon(Polygon polygon) {
			return polygon.streamDots().map(p -> p.toFractional()).allMatch(fp -> polygonLinesWrapper.contains(fp));
		}


		
		public void produceUpwardPlan(long toolDiameter, long depth) {
			if (!children.isEmpty()) {
				for(PolygonNode child : children) {
					if (child.plan==null) {
						return;
					}
				}
				
				connectPlans(toolDiameter, depth);
				if (parent != null) {
					parent.produceUpwardPlan(toolDiameter, depth);
				}
				return;
			}

			
			List<Point2D> allDots = polygon.streamDots().collect(Collectors.toList());
			allDots.add(allDots.get(0));
			
			CncPlan newPlan = new CncPlan();
			allDots.stream()
				.map(dot -> toMicroLocation(dot, depth))
				.forEach(ml -> newPlan.add(new CncLineTo(ml)));
			plan = newPlan;
			planStart = allDots.get(0);
			planEnd = allDots.get(allDots.size()-1);
			
			if (parent != null) {
				parent.produceUpwardPlan(toolDiameter, depth);
			}

		}
		
		private void connectPlans(long toolDiameter, long depth) {
			CncPlan newPlan = new CncPlan();
			Point2D lastLocation = null; 
			for(int idx=0; idx<children.size(); idx++) {
				PolygonNode child = children.get(idx);
//				if (idx!=0) {
					newPlan.add(new CncHeadState(true));
					MicroLocation firstLocation = child.plan.getFirstLocation();
					newPlan.add(new CncLineTo(firstLocation));
					newPlan.add(new CncHeadState(false));
//				} else {
//					planStart = child.planStart;
//				}
				if (idx==0) {
					planStart = child.planStart;
				}
				
				child.plan.exportItemsTo(newPlan);
				lastLocation = child.planEnd;
			}
			
			
			int pointIndex = polygon.findClosestPointIndex(lastLocation.toFractional());
			Point2D nextLocation = polygon.dotAt(pointIndex);
			long squaredDistance = nextLocation.squaredDistance(lastLocation);
			
			long maxdist = (toolDiameter*toolDiameter*9l)/2l;
			if (squaredDistance>maxdist) {
				newPlan.add(new CncHeadState(true));
				MicroLocation nLocation = toMicroLocation(nextLocation, depth);
				newPlan.add(new CncLineTo(nLocation));
				newPlan.add(new CncHeadState(false));
				
			}
			
			List<Point2D> allDots = polygon.streamDots().collect(Collectors.toList());
			Stream<Point2D> streamStart = allDots.subList(pointIndex, allDots.size()).stream();
			Stream<Point2D> streamEnd = allDots.subList(0, pointIndex+1).stream();
			Stream.concat(streamStart, streamEnd)
				.map(dot -> toMicroLocation(dot, depth))
				.forEach(ml -> newPlan.add(new CncLineTo(ml)));
			
			plan = newPlan;
			planEnd = pointIndex>0 ? allDots.get(pointIndex) : allDots.get(0);

		}

		private MicroLocation toMicroLocation(Point2D dot, long depth) {
			long mx = Distance.ofMillMeters(dot.x).asMicrometers()/1000;
			long my = Distance.ofMillMeters(dot.y).asMicrometers()/1000;
			return new MicroLocation(mx, my, depth);
		}
	}

	private MicroLocation toMicroLocation(Point2D dot, long depth) {
		long mx = Distance.ofMillMeters(dot.x).asMicrometers()/1000;
		long my = Distance.ofMillMeters(dot.y).asMicrometers()/1000;
		return new MicroLocation(mx, my, depth);
	}


}
