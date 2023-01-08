package net.github.douwevos.cnc.run;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import net.github.douwevos.cnc.head.CncHeadSpeed;
import net.github.douwevos.cnc.head.MicroLocation;
import net.github.douwevos.cnc.holer.HolerModelRun.RunContext;
import net.github.douwevos.cnc.layer.Layer;
import net.github.douwevos.cnc.layer.LayerCollisionInfo;
import net.github.douwevos.cnc.layer.LayerLine;
import net.github.douwevos.cnc.layer.StartStop;
import net.github.douwevos.cnc.layer.StartStopLayer;
import net.github.douwevos.cnc.tool.Tool;
import net.github.douwevos.justflat.types.values.Bounds2D;

public class LayeredCncContext {
	private static final boolean ONLY_TEST = true;
	public final long z;
	public final CircleCoords innerCircleCoords;
	public final CircleCoords circleCoords;
	public final CircleCoords outsideCircleCoords;
	public final List<CircleCoords> outside;
	public List<MicroLocation> floatingBalls;
	public List<Chain> chains;
	public StartStopLayer realModelLayer;
	public StartStopLayer modelReachableLayer;
	public StartStopLayer modelReachableLayer2;
	public Layer modelReachableLayer3;
	public StartStopLayer cutLayer;
	public StartStopLayer passedLayer;
	public StartStopLayer adaptiveLayer;
	public MicroLocation lastLocation;
	
	private TimedLogger timedLogger = new TimedLogger();
	
	Notifier notifier; 
	
	public LayeredCncContext(Tool tool, long z, Notifier notifier) {
		this.z = z;
		this.notifier = notifier;
		Tool selectedTool = tool;
		innerCircleCoords = new CircleCoords((selectedTool.getDiameter()*2)/3);
		circleCoords = new CircleCoords(selectedTool.getDiameter());
		outsideCircleCoords = new CircleCoords((selectedTool.getDiameter()*4)/3);
		int toolDiameter = selectedTool.getDiameter();
		int k = toolDiameter;
		outside = new ArrayList<>();
		for(int idx=0; idx<20; idx++) {
			k += toolDiameter/4;
			outside.add(new CircleCoords(k));
		}
	}

	@FunctionalInterface
	public interface Notifier {
		void notified();
	}

	public List<Chain> calculateModelAndCnc(RunContext runContext, long startNs) {
		long timeANs = System.nanoTime();
		System.out.println("produced layer: "+asTimeText(timeANs-startNs));

		timedLogger.log(true, "produced layer");
		
		timedLogger.log(false, "duplicating modelLayer to create modelReachableLayer");
		doNotify();
		modelReachableLayer = realModelLayer.duplicate();
		modelReachableLayer2 = realModelLayer.duplicate();
		modelReachableLayer3 = realModelLayer.duplicate();

		

		
		timedLogger.log(true, "Creating modelReachableLayer");
		createModelReachableLayer();
		
		
//		circelize(modelReachableLayer3, modelReachableLayer2);
		
		long timeBNs = System.nanoTime();
		timedLogger.log(true, "Created modelReachableLayer: "+asTimeText(timeBNs - timeANs));
		doNotify();

//		modelReachableLayer3 = modelReachableLayer2.duplicate();
//		modelReachableLayer3.exclude(modelReachableLayer);
		doNotify();
		
		
		timedLogger.log(true, "duplicating cutLayer");
		cutLayer = realModelLayer.duplicate();
		
		
		StartStopLayer unreachableLayer = realModelLayer.duplicate();
		
		
		timedLogger.log(true, "calculating reachable");
		doNotify();
//		cutReachable(layeredCncContext, unreachableLayer);
		newCutReachable(this, unreachableLayer);

		long timeCNs = System.nanoTime();
		cutLayer.exclude(unreachableLayer);
		timedLogger.log(true, "Calculated reachable: "+asTimeText(timeCNs - timeBNs));
		doNotify();
		
		produceOuterChains();
		doNotify();
		
		
		
		
		Bounds2D modelBounds = realModelLayer.bounds();

		timedLogger.log(true, "creating chains");


		List<MicroLocation> oldBalls = new ArrayList<>();
		for(long scanX=modelBounds.left; scanX<=modelBounds.right; scanX++) {
			oldBalls.add(new MicroLocation(scanX, modelBounds.bottom, z));
		}

		
		createLayers(this, modelBounds, oldBalls);

		
		
		timedLogger.log(true, "Connect all chains");
		chains = connectAllChains();
		
		
		long endNs = System.nanoTime();
		long diffNs = endNs-startNs;

		timedLogger.log(true, "Total time: "+asTimeText(diffNs));

		
		timedLogger.log(true, "Run actual cnc");

		for(Chain chain : chains) {
			doCncChain(runContext, chain);
		}

		return chains;
	}

	private void doNotify() {
		if (notifier!=null) {
			notifier.notified();
		}
	}


	private void doCncChain(RunContext runContext, Chain chain) {
		runContext.setFloating(true);
		for(MicroLocation location : chain.locations) {
			runContext.lineTo(location, CncHeadSpeed.NORMAL);
			runContext.setFloating(false);
		}
		runContext.setFloating(true);
	}

	
	

	public void produceOuterChains() {
		StartStopLayer scanLayer = modelReachableLayer;
		LayerLine emptyLine = new LayerLine();
		
		CircleCoords circleCoords = outsideCircleCoords;
		
		LocationList outerLocations[] = new LocationList[scanLayer.layerLine.length];
		LayerLine linePre = emptyLine;
		LayerLine linePost = emptyLine;
		LayerLine lineMain = emptyLine;
		for(int lineIdx=-1; lineIdx<scanLayer.layerLine.length; lineIdx++) {
			if (lineIdx+1<scanLayer.layerLine.length) {
				linePost = scanLayer.layerLine[lineIdx+1];
			} else {
				linePost = emptyLine;
			}
			if (lineMain != emptyLine) {
				LocationList locationList = new LocationList();
				outerLocations[lineIdx] = locationList;
				LayerLine duplicate = lineMain.duplicate();
				duplicate.edge(linePre, linePost);
				for(StartStop ss : duplicate.startStops) {
					cutLayer.cutAt(circleCoords, ss.start, ss.stop, lineIdx+scanLayer.bottom);
					long y = lineIdx + scanLayer.bottom;
					for(long x=ss.start; x<=ss.stop; x++) {
						locationList.add(new MicroLocation(x, y, z));
					}
				}
			}
			linePre = lineMain;
			lineMain = linePost;
		}
		List<Chain> outerChains = new ArrayList<>();

		for(int lineIdx=0; lineIdx<outerLocations.length; lineIdx++) {
			LocationList locationList = outerLocations[lineIdx];
			while(locationList!=null && !locationList.isEmpty()) {
				MicroLocation start = locationList.removeLast();
				Chain chain = produceChain(outerLocations, lineIdx, start);
				outerChains.add(chain);
			}
		}
		
		chains = outerChains;
		
	}

	

	private Chain produceChain(LocationList outerLocations[], int lineIdx, MicroLocation start) {
		Chain chain = new Chain(start);
		
		boolean isSecondRound = false;
		MicroLocation current = start;
		while(true) {
			MicroLocation close = null;
			int closeLineIdx = lineIdx;
			
			boolean didAppend = false;
			for(int subLineIdx=lineIdx-1; subLineIdx<=lineIdx+1; subLineIdx++) {
				if (subLineIdx<0 || subLineIdx>=outerLocations.length) {
					continue;
				}
				LocationList checkList = outerLocations[subLineIdx];
				if (checkList != null) {
					MicroLocation scan = checkList.scanCloseToX(current.x);
					if (scan != null) {
						if (current.isNeighbour(scan)) {
							current = scan;
							lineIdx = subLineIdx;
							chain.add(current);
							checkList.remove(current);
							didAppend = true;
							close = null;
							break;
						}
						close = scan;
						closeLineIdx = subLineIdx;
					}
				}
			}
			

			if (close != null) {
				lineIdx = closeLineIdx;
				LocationList checkList = outerLocations[lineIdx];
				checkList.remove(close);
				current = close;
				chain.add(current);
				didAppend = true;
				
			}
			
			if (!didAppend) {
				if (chain.getLastLocation().inShortRange(chain.getFirstLocation())) {
					chain.setIsCircular(true);
					break;
				}
				if (isSecondRound) {
					break;
				}
				Collections.reverse(chain.locations);
				isSecondRound = true;
				current = start;
			}
		}
		
		
		return chain;
	}

	
	public void createModelReachableLayer() {
		LayerLine emptyLine = new LayerLine();
		
		LayerLine linePre = emptyLine;
		LayerLine linePost = emptyLine;
		LayerLine lineMain = emptyLine;
		LayerLine edgeLineA = emptyLine;
		int r = circleCoords.diameter/2;
		int fullHitCount = 0;
		int misCount = 0;
		for(int lineIdx=-1; lineIdx<realModelLayer.layerLine.length; lineIdx++) {
			if (lineIdx+1<realModelLayer.layerLine.length) {
				linePost = realModelLayer.layerLine[lineIdx+1];
			} else {
				linePost = emptyLine;
			}
			if (lineMain != emptyLine) {
				LayerLine edgeLineB = lineMain.duplicate();
				edgeLineB.edge(linePre, linePost);
//				modelReachableLayer3.layerLine[lineIdx] = edgeLineB;
//				
//				for(StartStop ss : edgeLineB.startStops) {
////					modelReachableLayer.cutAt(circleCoords, ss.start, ss.stop, lineIdx+realModelLayer.bottom);
//					long left = ss.start-r;
//					long right= ss.stop+r;
//					modelReachableLayer2.layerLine[lineIdx].cut(left, right);
//					
//					
////					modelReachableLayer3.layerLine[lineIdx].cut(left, right);
////
////					left = ss.start-1;
////					right = ss.stop+1;
////
////					for(int px=1; px<r; px++) {
////						int down = lineIdx-px;
////						if (down>=0) {
////							modelReachableLayer3.layerLine[down].cut(left, right);
////						}
////						
////						int up= lineIdx+px;
////						if (up<modelReachableLayer3.layerLine.length) {
////							modelReachableLayer3.layerLine[up].cut(left, right);
////						}
////					}
//
//				}
//				boolean fullHit = true;
//				
//				LayerLine doubleEdge = edgeLineB.duplicate();
//				doubleEdge.exclude(edgeLineA);
//
//				if (doubleEdge.isEmpty()) {
//				} else {
//					fullHit = false;
//					for(StartStop ss : doubleEdge.startStops) {
//						modelReachableLayer2.cutAt(circleCoords, ss.start, ss.stop, lineIdx+realModelLayer.bottom());
//					}
//				}
//
//				doubleEdge = edgeLineA.duplicate();
//				doubleEdge.exclude(edgeLineB);
//				if (doubleEdge.isEmpty()) {
//				} else {
//					fullHit = false;
//					for(StartStop ss : doubleEdge.startStops) {
//						modelReachableLayer2.cutAt(circleCoords, ss.start, ss.stop, lineIdx+realModelLayer.bottom());
//					}
//				}
//				
//				if (fullHit) {
//					fullHitCount++;
//				} else {
//					misCount++;
//				}

				
				edgeLineA = edgeLineB;
			}
			linePre = lineMain;
			lineMain = linePost;
		}
		System.out.println("Full hit = "+fullHitCount+", misCount="+misCount);
	}
	     
	
	public List<Chain> connectAllChains() {
		List<Chain> result = new ArrayList<>();
		List<Chain> queue = new ArrayList<>(chains);
		while(!queue.isEmpty()) {
			Chain chain = queue.remove(queue.size()-1);
			while(true) {
				Chain next = tryConnectChain(queue, chain);
				if (next == null) {
					break;
				}
				chain = next;
			}
			result.add(chain);
		}
		
		return result;
	}

	private Chain tryConnectChain(List<Chain> queue, Chain chain) {
		MicroLocation lastLocation = chain.getLastLocation();

		List<ChainAndDistance> chainAndDistanceList = new ArrayList<>();
		
		for(Chain item : queue) {
			MicroLocation itemLocation = item.getFirstLocation();
			long distanceSq = itemLocation.distanceSq(lastLocation);
			chainAndDistanceList.add(new ChainAndDistance(item, true, distanceSq));

			itemLocation = item.getLastLocation();
			distanceSq = itemLocation.distanceSq(lastLocation);
			chainAndDistanceList.add(new ChainAndDistance(item, false, distanceSq));
		}
		
		chainAndDistanceList.sort((a,b) -> Long.compare(a.distSq, b.distSq));
		
		while(!chainAndDistanceList.isEmpty()) {
			ChainAndDistance chainAndDistance = chainAndDistanceList.remove(0);
			MicroLocation location = chainAndDistance.getTail();
			Chain linkChain = produceValidChain(lastLocation, location);
			if (linkChain == null) {
				continue;
			}
			queue.remove(chainAndDistance.chain);
			chain.concat(linkChain);
			chainAndDistance.stream().forEach(chain::add);
			return chain;
		}
		
		return null;
	}
	
	
	private Chain produceValidChain(MicroLocation from, MicroLocation to) {
		Chain chain = new Chain(from);
		
		long dxab = to.x - from.x;
		long dyab = to.y - from.y;
		dxab = (dxab*7)/6;
		dyab = (dyab*7)/6;
		
		long maxDistSq = dxab*dxab + dyab*dyab;
		
		Set<MicroLocation> beenThere = new HashSet<>();
		beenThere.add(from);
		
		MicroLocation current = from;
		while(true) {
			MicroLocation bestLocation = null;
			Direction bestDirection = null;
			long bestDistSq = 0;
			
			for(Direction direction : Direction.values()) {
				long scanX = current.x + direction.deltaX;
				long scanY = current.y + direction.deltaY;
				MicroLocation scanLocation = new MicroLocation(scanX, scanY, from.z);
				if (beenThere.contains(scanLocation)) {
					continue;
				}
				
				long scanDistSq = to.distanceSq(scanLocation);
				
				if (bestDirection==null || scanDistSq<bestDistSq) {
					if (modelReachableLayer.testDot(scanX, scanY, false)) {
						bestDistSq = scanDistSq;
						bestDirection = direction;
						bestLocation = scanLocation;
					}
				}
			}
			
			if (bestDirection==null || bestDistSq>maxDistSq) {
				return null;
			}

			current = bestLocation;
			beenThere.add(current);
			if (current.equals(to)) {
				break;
			}
			chain.add(current);
			int dotCount = beenThere.size();
			if (dotCount*dotCount>maxDistSq) {
				return null;
			}
			
		}
		chain.add(to);
		return chain;
	}

	
	public void newCutReachable(LayeredCncContext layeredCncContext, StartStopLayer noneReachableLayer) {
//		RunContext runContext = layeredCncContext.runContext;
		CircleCoords circleCoords = layeredCncContext.circleCoords;
//		Bounds2D modelBounds = cutLayer.bounds();
//		Tool tool = runContext.getSelectedTool();
//		int grid = tool.getDiameter()/4;
//
//		System.out.println("calc reacahble");
//		for(long scanY=modelBounds.bottom; scanY<=modelBounds.top; scanY+=grid) {
//			System.out.println("fast grid : scanY = "+scanY);
//			for(long scanX=modelBounds.left; scanX<=modelBounds.right; scanX+=grid) {
//				LayerCollisionInfo info = layeredCncContext.realModelLayer.calculateAt(circleCoords, scanX, scanY);
//				if (info.misCount<=0) {
////				if (layeredCncContext.modelReachableLayer.testDot(scanX, scanY, false)) {
//					cutLayer.cutAt(circleCoords, scanX, scanY);
//				}
//			}
//		}
//		
		StartStopLayer reachableLayer = layeredCncContext.modelReachableLayer;
		
		long scanY = reachableLayer.bottom();
		for(int lineIdx=0; lineIdx<reachableLayer.layerLine.length; lineIdx++) {
			LayerLine reachableLine = reachableLayer.layerLine[lineIdx];
			for(StartStop startStop : reachableLine.startStops) {
				noneReachableLayer.cutAt(circleCoords, startStop.start, startStop.stop, scanY);
			}
			scanY++;
		}
	}	
	
	public void createLayers(LayeredCncContext layeredCncContext, Bounds2D modelBounds, List<MicroLocation> oldBalls) {
		for(int r=0; r<120; r++) {
			
			long timeRStartNs = System.nanoTime();

			
			List<MicroLocation> floatingBalls = new ArrayList<>();
			int index = 0;
			for(long scanX=modelBounds.left; scanX<=modelBounds.right; scanX++) {
				MicroLocation oldBall = oldBalls.get(index++);
				MicroLocation flt = null;
				if (oldBall!=null) {
					flt = floatUp(layeredCncContext, modelBounds, oldBall);
				}
				floatingBalls.add(flt);
			}
			oldBalls.clear();
			oldBalls.addAll(floatingBalls);
			long timeFloatBallsNs = System.nanoTime();
			System.out.println("floating-balls: round "+r+": "+asTimeText(timeFloatBallsNs-timeRStartNs));

			
			
			layeredCncContext.adaptiveLayer = layeredCncContext.cutLayer.duplicate();
			layeredCncContext.floatingBalls = floatingBalls;
			floatingBalls = repositionFloatingBalls(layeredCncContext, floatingBalls);
			if (floatingBalls.isEmpty()) {
				break;
			}
			long timeReposFloatBallsNs = System.nanoTime();
			System.out.println("repositioned floating-balls: round "+r+": "+asTimeText(timeReposFloatBallsNs-timeFloatBallsNs));

			
			List<Chain> chains = createChains(layeredCncContext, floatingBalls);
			long timeCreateChainsNs = System.nanoTime();
			System.out.println("created-chains: round "+r+": "+asTimeText(timeCreateChainsNs - timeReposFloatBallsNs));
			
			
//			List<Chain> longerChains = chains.stream().filter(s -> s.locations.size()>1).collect(Collectors.toList());
			List<Chain> longerChains = chains;
		
			
	
			longerChains.stream().forEach(s -> connectChainDots(layeredCncContext, s));
		
			if (layeredCncContext.chains == null) {
				layeredCncContext.chains = longerChains;
			} else {
				ArrayList<Chain> newChains = new ArrayList<>(layeredCncContext.chains);
				newChains.addAll(longerChains);
				layeredCncContext.chains = newChains;
			}

			long timeConnectedChainDotsNs = System.nanoTime();
			System.out.println("connect-chain-dots: round "+r+": "+asTimeText(timeConnectedChainDotsNs- timeCreateChainsNs));
			
			
			MicroLocation last = null;
			for(Chain chain : longerChains) {
				for(MicroLocation ml : chain.locations) {
					if (Objects.equals(ml, last)) {
						continue;
					}
					layeredCncContext.cutLayer.cutAt(layeredCncContext.circleCoords, ml.x, ml.y);
					last = ml;
				}
			}

			long timeChainCutNs = System.nanoTime();
			System.out.println("cut-chain-dots: round "+r+": "+asTimeText(timeChainCutNs - timeConnectedChainDotsNs));

			
			long timeREndNs = System.nanoTime();

			System.out.println("Chain round "+r+": "+asTimeText(timeREndNs - timeRStartNs));

		}
	}


	String asTimeText(long time) {
		return time+"ns(" + ((500_000l+time)/1_000_000L) + "ms)";
	}

	
	private MicroLocation floatUp(LayeredCncContext layeredCncContext, Bounds2D modelBounds, MicroLocation oldBall) {
		long scanX = oldBall.x;
		for(long scanY=oldBall.y; scanY<=modelBounds.top; scanY++) {
			if (layeredCncContext.cutLayer.testDot(scanX, scanY, false)) {
				return new MicroLocation(scanX, scanY, layeredCncContext.z);
			}
		}
		return null;
	}

	
	private List<MicroLocation> repositionFloatingBalls(LayeredCncContext layeredCncContext, List<MicroLocation> floatingBalls) {
		StartStopLayer adaptiveLayer = layeredCncContext.adaptiveLayer;
		CircleCoords circleCoords = layeredCncContext.circleCoords;
		List<MicroLocation> result = new ArrayList<>(floatingBalls.size());
		int c = 0;
		for(MicroLocation location : floatingBalls) {
			c++;
//			if ((c%100)==0) {
//				System.out.println("c="+c+" of "+floatingBalls.size());
//			}
			if (location == null || !adaptiveLayer.testDot(location.x, location.y, false)) {
				continue;
			}
			MicroLocation betterLocation = repositionFloatingBall(layeredCncContext, location);
			if (betterLocation != null) {
				result.add(betterLocation);
				adaptiveLayer.cutAt(circleCoords, betterLocation.x, betterLocation.y);
			}
		}
		return result;
	}

	private MicroLocation repositionFloatingBall(LayeredCncContext layeredCncContext, MicroLocation location) {
		CircleCoords circleCoords = layeredCncContext.circleCoords;
		
		int diameter = layeredCncContext.circleCoords.diameter;
		
		MicroLocation improved = scanModelFloater(layeredCncContext, location);
		if (improved == null) {
			return null;
		}

		/* ensure covered by model */
		
		if (!layeredCncContext.modelReachableLayer.testDot(location.x, location.y, false)) {
				
			LayerCollisionInfo current = layeredCncContext.realModelLayer.calculateAt(circleCoords, location.x, location.y);
			if (current.misCount>0) {
				Set<MicroLocation> beenThere = new HashSet<>();
				beenThere.add(improved);
				while(true) {
					Direction bestDirection = null;
					LayerCollisionInfo bestInfo = current;
					for(Direction direction : Direction.values()) {
						long testY = improved.y+direction.deltaY;
						long testX = improved.x+direction.deltaX;
						int deltaX = (int) (testX - location.x);
						int deltaY = (int) (testY - location.y);

						if (!layeredCncContext.circleCoords.contains(deltaX, deltaY)) {
							continue;
						}

						MicroLocation testLocation = new MicroLocation(improved.x+direction.deltaX, improved.y+direction.deltaY, improved.z);
						if (!beenThere.add(testLocation)) {
							continue;
						}

						
						LayerCollisionInfo test = layeredCncContext.realModelLayer.calculateAt(circleCoords, testX, testY);
						if (test.misCount<bestInfo.misCount || (test.misCount==bestInfo.misCount && direction==Direction.UP)) {
							bestDirection = direction;
							bestInfo = test;
						}
					}
					if (bestDirection == null) {
						return null;
					}
	
					improved = new MicroLocation(improved.x+bestDirection.deltaX, improved.y+bestDirection.deltaY, improved.z);
					current = bestInfo;
					
					if (bestInfo.misCount == 0) {
						break;
					}
				}
			}
		}
		
		
		/* float-up for best cut value */

		LayerCollisionInfo cutInfo = layeredCncContext.cutLayer.calculateAt(circleCoords, improved.x, improved.y);
		
		int step = 32;
		while(step*2>diameter) {
			step = step/2;
		}
		
		while(true) {
			Direction direction = Direction.UP;
			long testY = improved.y+direction.deltaY*step;
			long testX = improved.x+direction.deltaX*step;
			int deltaX = (int) (testX - location.x);
			int deltaY = (int) (testY - location.y);
			if (!layeredCncContext.circleCoords.contains(deltaX, deltaY)) {
				if (step>1) {
					step = step/2;
					continue;
				}
				break;
			}
			if (!layeredCncContext.modelReachableLayer.testDot(deltaX, deltaY, false)) {
				if (step>1) {
					step = step/2;
					continue;
				}
				break;
			}
			
			
			LayerCollisionInfo test = layeredCncContext.cutLayer.calculateAt(circleCoords, testX, testY);
			if (test.hitCount<cutInfo.hitCount) {
				if (step>1) {
					step = step/2;
					continue;
				}
				break;
			}
			
			improved = new MicroLocation(improved.x+direction.deltaX, improved.y+direction.deltaY, improved.z);
			cutInfo = test;
		}
		
		return improved;
	}

	private MicroLocation scanModelFloater(LayeredCncContext layeredCncContext, MicroLocation location) {
		CircleCoords circleCoords = layeredCncContext.circleCoords;
		int cuts = 40;
		double scraps = 180D/(Math.PI*cuts);
		
		for(int idx=0; idx<cuts; idx++) {
			long dx = (long) Math.ceil(Math.sin(idx*scraps) * circleCoords.radius);
			long dy = (long) Math.ceil(Math.cos(idx*scraps) * circleCoords.radius);
			if (!layeredCncContext.realModelLayer.doesBreach(circleCoords, location.x+dx, location.y+dy)) {
//			if (layeredCncContext.realModelLayer.testDot(location.x+dx, location.y+dy, false)) {
//				LayerCollisionInfo current = layeredCncContext.modelLayer.calculateAt(circleCoords, location.x+dx, location.y+dy);
//				if (current.misCount == 0) {
					return new MicroLocation(location.x+dx, location.y+dy,location.z);
//				}
			}
		}
		
		return null;
	}

	private List<Chain> createChains(LayeredCncContext layeredCncContext, List<MicroLocation> floatingBalls) {
		int diameter = layeredCncContext.circleCoords.diameter;
		long diameterSq = diameter*diameter;

		List<Chain> result = new ArrayList<>();
		Chain chain = null;
		for(MicroLocation location : floatingBalls) {
			if (chain==null) {
				chain = new Chain(location);
				result.add(chain);
				continue;
			}
			
			MicroLocation last = chain.getLastLocation();
			long distSq = last.distanceSq(location);
			if (distSq>=diameterSq) {
				chain = new Chain(location);
				result.add(chain);
			} else {
				chain.add(location);
			}
		}
		return result;
	}

	private void connectChainDots(LayeredCncContext layeredCncContext, Chain chain) {
		List<MicroLocation> result = new ArrayList<>();
		
		MicroLocation from = null;
		
		for(MicroLocation to : chain.locations) {
			if (from!=null) {
				if (!from.inShortRange(to)) {
					List<Direction> dirs;
					if (from.y<to.y) {
						dirs = Arrays.asList(Direction.UP, Direction.RIGHT);
					} else {
						dirs = Arrays.asList(Direction.DOWN, Direction.RIGHT);
					}
					while(true) {
						MicroLocation next = null;
						for(Direction dir : dirs) {
							long scanX = from.x + dir.deltaX;
							long scanY = from.y + dir.deltaY;
							if (from.y==to.y && dir.deltaY!=0) {
								continue;
							}
							if (scanX>to.x) {
								break;
//								scanX = from.x - dir.deltaX;
							}
							if (!layeredCncContext.modelReachableLayer.testDot(scanX, scanY, false)) {
								next = new MicroLocation(scanX, scanY, from.z);
								break;
							}
						}
						if (next == null || Objects.equals(from, to)) {
							break;
						}
						result.add(next);
						from = next;
					}
				}
			}
			result.add(to);
			from = to;
		}
		chain.locations.clear();
		chain.locations.addAll(result);
	}

	static class TimedLogger {
		
		long startNs = System.nanoTime();
		long stampNs = startNs;
		
		public void log(boolean stamp, String txt) {
			long nowNs = System.nanoTime();
			StringBuilder buf = new StringBuilder();
			String passed = "                            " + asTimeText(nowNs-startNs);
			buf.append(passed.substring(passed.length()-27));
			buf.append("  ");

			String stamped = "                           " + asTimeText(nowNs-stampNs);
			buf.append(stamped.substring(stamped.length()-27));
			buf.append("  ");
			buf.append(txt);
			System.out.println(buf.toString());
			if (stamp) {
				stampNs = nowNs;
			}

		}
		
		String asTimeText(long time) {
			int millis = (int) ((time/1_000_000L)); 
			int micros = (int) ((time/1000L) % 1_000L);
			String t = "000"+micros;
			t = t.substring(t.length()-3);
			return time+"ns(" + millis+"." + t + "ms)";
		}

		
	}

}