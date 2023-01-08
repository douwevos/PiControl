package net.github.douwevos.cnc.layer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class LayerLineDouble {
	private static final StartStopDouble[] EMPTY_START_STOPS = new StartStopDouble[0];

	public StartStopDouble startStops[];

	public LayerLineDouble() {
		startStops = EMPTY_START_STOPS;
	}

	public boolean testDot(double x) {
		if (startStops.length == 0) {
			return false;
		}
		for(StartStopDouble startStop : startStops) {
			if (x>=startStop.start && x<=startStop.stop) {
				return true;
			}
			if (x<startStop.start) {
				break;
			}
		}
		return false;
	}

	
	public LayerLineDouble duplicate() {
		LayerLineDouble copy = new LayerLineDouble();
		copy.startStops = new StartStopDouble[startStops.length];
		System.arraycopy(startStops, 0, copy.startStops, 0, startStops.length);
//		copy.rawDots = rawDots == null ? null :  new ArrayList<>(rawDots);
		return copy;
	}
	
	public void exclude(LayerLineDouble layerLine) {
		for(StartStopDouble ss : layerLine.startStops) {
			cut(ss.start, ss.stop);
		}
	}
	
	public void edge(LayerLineDouble linePre, LayerLineDouble linePost) {
		
		List<StartStopDouble> out = new ArrayList<>();
		
		linePre = linePre.duplicate();
		linePost = linePost.duplicate();

		List<StartStopDouble> linePreEdge = new ArrayList<>();
		List<StartStopDouble> linePostEdge = new ArrayList<>();
		
		for(StartStopDouble ss : startStops) {
//			System.out.println("------------- "+ss);
			
			edgeStartStop(linePre, linePreEdge, ss);
			edgeStartStop(linePost, linePostEdge, ss);
//			System.out.println(""+linePreEdge);
			
			Iterator<StartStopDouble> preIterator = linePreEdge.iterator();
			Iterator<StartStopDouble> postIterator = linePostEdge.iterator();
			StartStopDouble pre = preIterator.hasNext() ? preIterator.next() : null;
			StartStopDouble post = postIterator.hasNext() ? postIterator.next() : null;

			
//			System.out.println("###########");

			int startInsertIndex = out.size();
			out.add(new StartStopDouble(ss.start, ss.start));
			
			while(pre!=null || post!=null) {
//				System.out.println("pre="+pre+" post="+post);
				if (pre!=null) {
					if (post!=null) {
						if (post.stop<pre.start) {
//							System.out.println("  adding post");
							out.add(post);
							post = postIterator.hasNext() ? postIterator.next() : null;
						} else if (pre.stop<post.stop) {
//							System.out.println("  adding pre");
							out.add(pre);
							pre = preIterator.hasNext() ? preIterator.next() : null;
						} else {
							if (pre.start<=post.start) {
								if (post.stop>pre.stop) {
									pre = pre.withStop(post.stop);
								}
								post = postIterator.hasNext() ? postIterator.next() : null;
//								System.out.println("  next post");
							} else {
								if (pre.stop>post.stop) {
									post = post.withStop(pre.stop);
								}
								pre = preIterator.hasNext() ? preIterator.next() : null;
//								System.out.println("  next pre");
							}
							
						}
					} else {
//						System.out.println("  adding pre");
						out.add(pre);
						pre = preIterator.hasNext() ? preIterator.next() : null;
					}
				} else if (post!=null) {
//					System.out.println("  adding post");
					out.add(post);
					post = postIterator.hasNext() ? postIterator.next() : null;
				}
			}

			StartStopDouble last = out.get(out.size()-1);
			if (last.stop!=ss.stop) {
				out.add(new StartStopDouble(ss.stop, ss.stop));
			}
			
			// Remove additional start if it was added double
			StartStopDouble postStart = out.get(startInsertIndex+1);
			if (postStart.start == ss.start) {
				out.remove(startInsertIndex);
			}
		}
		
		if (out.size() == 0) {
			startStops = EMPTY_START_STOPS;
			return;
		}
		
		startStops = new StartStopDouble[out.size()];
		out.toArray(startStops);
	}

	
	private void edgeStartStop(LayerLineDouble sourceLine, List<StartStopDouble> cropped, StartStopDouble ss) {
		cropped.clear();
		StartStopDouble crop = new StartStopDouble(ss);
//		System.out.println("to crop="+crop);
		for(StartStopDouble source : sourceLine.startStops) {
//			System.out.println("  source="+source+", crop="+crop);
			
			if (source.start>=crop.stop) {
				break;
			}
			if ((source.stop<=crop.start) || (source.start>=crop.stop)) {
				continue;
			}
			if (crop.start<source.start) {
				cropped.add(new StartStopDouble(crop.start, source.start-1));
				if (source.stop>=crop.stop) {
					crop = null;
					break;
				}
			}
			
			if (crop.start<source.stop) {
				if (source.stop>=crop.stop) {
					crop = null;
					break;
				}
				crop = crop.withStart(source.stop+1);
			}
		}
		if (crop!=null) {
			cropped.add(crop);
		}
//		System.out.println("cropped="+cropped);
	}

	


	public double cut(double left, double right) {
		long cutCount = 0;
		for(int i=0; i<startStops.length; i++) {
			StartStopDouble startStop = startStops[i];
			double tleft = startStop.start;
			double tright= startStop.stop;
			
			double hitLeft = tleft<left ? left : tleft;
			double hitRight = tright>right ? right : tright;
			double hitCount = 1+hitRight-hitLeft;
			if (hitCount>0) {
				cutCount += hitCount;
					
				// toggle:  ********
				// to-cut:     **
				if ((left>tleft) && right<tright) {
					startStops[i] = startStop.withStop(left-1);
					i++;
					StartStopDouble copy[] = new StartStopDouble[startStops.length+1];
					System.arraycopy(startStops, 0, copy, 0, i);
					System.arraycopy(startStops, i, copy, i+1, startStops.length-i);
					startStops = copy;
					startStops[i] = new StartStopDouble(right+1, tright);
					break;
				}

				// toggle:  ********
				// to-cut:        ****
				if ((left>tleft) && right>=tright) {
					startStops[i] = startStop.withStop(left-1);
					if (right==tright) {
						break;
					}
				}
					
				// toggle:    ********
				// to-cut:   ****
				if ((left<=tleft) && right<tright) {
					startStops[i] = startStop.withStart(right+1);
				}
					

				// toggle:    ********
				
				// to-cut:   ***********tLeftRight(int at, OnOffDot left, OnOffDot right) {
//				ensureCapacity(size+2);
//				
//				if (at<size) {
//					System.arraycopy(dots, at, dots, at+2, size-at);
//				}
//				dots[at] = left;
//				dots[at+1] = right;
//				size += 2;
//			}


				if ((left<=tleft) && right>=tright) {

					StartStopDouble copy[] = new StartStopDouble[startStops.length-1];
					System.arraycopy(startStops, 0, copy, 0, i);
					System.arraycopy(startStops, i+1, copy, i, startStops.length-1-i);
					i--;
					startStops = copy;
				}
				
			}
		}
		return cutCount;
	}

	
	public static void main(String[] args) {
		LayerLineDouble linePre = new LayerLineDouble();
		LayerLineDouble linePost = new LayerLineDouble();
		LayerLineDouble layerLine = new LayerLineDouble();
		layerLine.startStops = new StartStopDouble[] { new StartStopDouble(10,15), new StartStopDouble(19,35) };
//		layerLine.add(127, true);
//		layerLine.add(9, false);
//		layerLine.add(9, false);
//		layerLine.apply();
		
		layerLine.invert(Arrays.asList(new StartStopDouble(4,30)));
		dumpLine(layerLine.startStops, 50);

//		linePre.startStops = new StartStopDouble[] { new StartStopDouble(10,27), new StartStopDouble(35, 42) };
//		linePost.startStops = new StartStopDouble[] { new StartStopDouble(12,20), new StartStopDouble(25,25), new StartStopDouble(30, 37) };
//
//		dumpLine(linePre.startStops, 50);
//		dumpLine(layerLine.startStops, 50);
//		dumpLine(linePost.startStops, 50);
//
//		LayerLine edge1 = linePre.duplicate();
//		edge1.edge(new LayerLine(), layerLine);
//
//		LayerLine edge2 = layerLine.duplicate();
//		edge2.edge(linePre, linePost);
//
//		LayerLine edge3 = linePost.duplicate();
//		edge3.edge(layerLine, new LayerLine());
//		
//		System.out.println();
//		
//		dumpLine(edge1.startStops, 50);
//		dumpLine(edge2.startStops, 50);
//		dumpLine(edge3.startStops, 50);

//		System.out.println(layerLine);
	}

	public static void dumpLine(StartStopDouble[] startStops, int till) {
		StringBuilder buf = new StringBuilder();
		for(StartStopDouble ss : startStops) {
			while(buf.length()<ss.start) {
				buf.append("-");
			}

			while(buf.length()<=ss.stop) {
				buf.append("*");
			}
		}
		while(buf.length()<till) {
			buf.append("-");
		}
		System.out.println(buf);
	}


	public void merge(LayerLineDouble mergeLine) {
		List<StartStopDouble> shapeStartStop = Arrays.asList(mergeLine.startStops);
		if (shapeStartStop==null || shapeStartStop.isEmpty()) {
			return;
		}
		
		if (startStops.length==0) {
			startStops = new StartStopDouble[shapeStartStop.size()];
			startStops = shapeStartStop.toArray(startStops);
			return;
		}
		
		List<StartStopDouble> mergedList = new ArrayList<>();
		
		int sourceIndex = 0;
		StartStopDouble source = startStops[sourceIndex];
		
		for(StartStopDouble doInvert : shapeStartStop) {
			
			while(sourceIndex<startStops.length) {
				
				// sssss
				//        iiiii
				if (source.stop<doInvert.start) {
					mergedList.add(source);
					sourceIndex++;
					source = sourceIndex<startStops.length ? startStops[sourceIndex] : null;
					continue;
				}
				
				//              sssss
				//        iiiii
				if (doInvert.stop<source.start) {
					mergedList.add(doInvert);
					break;
				}

				//        sssss
				//        iiiii

				if (doInvert.start == source.start && doInvert.stop == source.stop) {
					mergedList.add(source);
					sourceIndex++;
					source = sourceIndex<startStops.length ? startStops[sourceIndex] : null;
					break;
				}

				if (doInvert.start<source.start) {
					
					if (doInvert.stop==source.stop) {
						mergedList.add(doInvert);
						//          sss
						//        iiiii         ii
						sourceIndex++;
						source = sourceIndex<startStops.length ? startStops[sourceIndex] : null;
						break;
					}

					if (source.stop>doInvert.stop) {
						source = new StartStopDouble(doInvert.start, source.stop);
						//           sssss     
						//        iiiii         
						break;
					}


					//        sssss
					//       iiiiiii
					
					
				} else {

					if (doInvert.start==source.start) {
						
						if (doInvert.stop>source.stop) {
							//        sss
							//        iiiii            ii
							doInvert = new StartStopDouble(source.start, doInvert.stop);
							sourceIndex++;
							source = sourceIndex<startStops.length ? startStops[sourceIndex] : null;
							continue;
						} else {
							//        sssssss
							//        iiiii               ss
							doInvert = new StartStopDouble(doInvert.start, source.stop);
							break;
						}		
					}


					if (source.stop==doInvert.stop) {
						mergedList.add(source);
						//      sssssss       ss  
						//        iiiii
						sourceIndex++;
						source = sourceIndex<startStops.length ? startStops[sourceIndex] : null;
						break;
					}

					if (source.stop>doInvert.stop) {
						//        sssss
						//         iii
						source = new StartStopDouble(doInvert.stop+1, source.stop);
						break;
					}

					//      sssss
					//        iiiii       ss   ii
					
					doInvert = new StartStopDouble(source.start, doInvert.stop);
					sourceIndex++;
					source = sourceIndex<startStops.length ? startStops[sourceIndex] : null;
					
				}
				
			}
			
		}
		
		while(source!=null) {
			mergedList.add(source);
			sourceIndex++;
			source = sourceIndex<startStops.length ? startStops[sourceIndex] : null;
		}
		
		startStops = new StartStopDouble[mergedList.size()];
		startStops = mergedList.toArray(startStops);
	}

	
	public void invert(List<StartStopDouble> shapeStartStop) {
		if (shapeStartStop==null || shapeStartStop.isEmpty()) {
			return;
		}
		
		if (startStops.length==0) {
			startStops = new StartStopDouble[shapeStartStop.size()];
			startStops = shapeStartStop.toArray(startStops);
			return;
		}
		
		List<StartStopDouble> invertedList = new ArrayList<>();
		
		int sourceIndex = 0;
		StartStopDouble source = startStops[sourceIndex];
		
		for(StartStopDouble doInvert : shapeStartStop) {
			
			while(source != null) {
				
				// sssss
				//        iiiii
				if (source.stop<doInvert.start) {
					invertedList.add(source);
					sourceIndex++;
					source = sourceIndex<startStops.length ? startStops[sourceIndex] : null;
					continue;
				}
				
				//              sssss
				//        iiiii
				if (doInvert.stop<source.start) {
					invertedList.add(doInvert);
					doInvert = null;
					break;
				}

				//        sssss
				//        iiiii

				if (doInvert.start == source.start && doInvert.stop == source.stop) {
					sourceIndex++;
					source = sourceIndex<startStops.length ? startStops[sourceIndex] : null;
					doInvert = null;
					break;
				}

				if (doInvert.start<source.start) {
					
					invertedList.add(new StartStopDouble(doInvert.start, source.start-1));
					if (doInvert.stop==source.stop) {
						//          sss
						//        iiiii         ii
						sourceIndex++;
						source = sourceIndex<startStops.length ? startStops[sourceIndex] : null;
						doInvert = null;
						break;
					}

					if (source.stop>doInvert.stop) {
						source = new StartStopDouble(doInvert.stop+1, source.stop);
						//           sssss     
						//        iiiii         
						doInvert = null;
						break;
					}


					//        sssss
					//       iiiiiii
					
					doInvert = new StartStopDouble(source.stop+1, doInvert.stop);
					sourceIndex++;
					source = sourceIndex<startStops.length ? startStops[sourceIndex] : null;
					
				} else {

					if (doInvert.start==source.start) {
						
						if (doInvert.stop>source.stop) {
							//        sss
							//        iiiii            ii
							doInvert = new StartStopDouble(source.stop+1, doInvert.stop);
							sourceIndex++;
							source = sourceIndex<startStops.length ? startStops[sourceIndex] : null;
							continue;
						} else {
							//        sssssss
							//        iiiii               ss
							doInvert = new StartStopDouble(doInvert.stop+1, source.stop);
							break;
						}
					}

					invertedList.add(new StartStopDouble(source.start, doInvert.start-1));

					if (source.stop==doInvert.stop) {
						//      sssssss       ss  
						//        iiiii
						sourceIndex++;
						source = sourceIndex<startStops.length ? startStops[sourceIndex] : null;
						doInvert = null;
						break;
					}

					if (source.stop>doInvert.stop) {
						//        sssss
						//         iii
						source = new StartStopDouble(doInvert.stop+1, source.stop);
						doInvert = null;
						break;
					}

					//      sssss
					//        iiiii       ss   ii
					
					doInvert = new StartStopDouble(source.stop+1, doInvert.stop);
					sourceIndex++;
					source = sourceIndex<startStops.length ? startStops[sourceIndex] : null;
				}
			}
			
			if (doInvert != null) {
				invertedList.add(doInvert);
			}
			
		}
		
		while(source!=null) {
			invertedList.add(source);
			sourceIndex++;
			source = sourceIndex<startStops.length ? startStops[sourceIndex] : null;
		}
		
		
		startStops = new StartStopDouble[invertedList.size()];
		startStops = invertedList.toArray(startStops);		
	}

	
	
	public void fillCollisionInfo(LayerCollisionInfo info, double left, double right) {
		double totalHit = 0;
		for(StartStopDouble startStop : startStops) {
			if (right<startStop.start) {
				break;
			}
			double tleft = startStop.start;
			double tright= startStop.stop;

			double hitLeft = tleft<left ? left : tleft;
			double hitRight = tright>right ? right : tright;
			double hitCount = 1+hitRight-hitLeft;
			if (hitCount>0) {
				info.hitCount += hitCount;
				totalHit += hitCount;
			}
		}
		info.misCount += (right+1-left)-totalHit;
	}

	public boolean doesBreach(double left, double right) {

		for(StartStopDouble startStop : startStops) {
			if (left>startStop.stop) {
				//       iiiiiii
				// ssss
				continue;
			}
			if (left<startStop.start) {
				//       iiiiiii
				//                  ssss

				// iiiiiii
				//      ssss

				// iiiiiii
				//   ssssssssssss

				// iiiiiii
				//   sss

				// iiiiiii
				//     sss
				return true;
			}
			
			if (right<=startStop.stop) {
				//   iiiiiii
				// ssssssssssss

				// iiiiiii
				// ssssssssssss

				//        iiiiiii
				//   ssssssssssss
				return false;
			}



			//   iiiiiii
			// ssss

			// iiiiiii
			// sss




			//          iiiiiii
			//   ssssssssssss

			left = startStop.stop+1;
		}
		return true;
	}

	

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		for(int idx=0; idx<startStops.length; idx++) {
			StartStopDouble startStop = startStops[idx];
			if (idx>0) {
				s.append(',');
			}
			s.append(startStop.start).append('-').append(startStop.stop);
			
		}
		return "Line["+s+"]";
	}

	public boolean isEmpty() {
		return startStops.length==0;
	}

	
}