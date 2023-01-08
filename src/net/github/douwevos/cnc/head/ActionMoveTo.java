package net.github.douwevos.cnc.head;

import java.util.Objects;

public class ActionMoveTo implements CncAction {

	private final MicroLocation point;
	private final CncHeadSpeed speed;
	private LineScanner lineScanner;
	private volatile boolean hasFinished;
	
	public ActionMoveTo(MicroLocation point, CncHeadSpeed speed) {
		this.point = point;
		this.speed = speed;
	}
	
	@Override
	public void run(CncContext context) {
		CncHead head = context.getHead();
		if (lineScanner == null) {
			MicroLocation currentLocation = context.getHeadLocation();
			lineScanner = new LineScanner(currentLocation, point);
//			System.out.println("line from: "+currentLocation+" to:" +point);
		}
		
		MicroLocation next = lineScanner.next(head);
		if (next == null) {
			hasFinished = true;
		} 
		else {
			context.moveHeadLocation(next, speed);
		}
	}

	@Override
	public boolean hasFinished(CncContext context) {
		if (hasFinished) {
			return true;
		}
		MicroLocation currentLocation = context.getHeadLocation();
		return Objects.equals(currentLocation, point);
	}
	
	public void markFinished() {
		hasFinished = true;
	}

	

	static class LineScanner {
		private final MicroLocation from;
		private final MicroLocation to;
		
		private double qx;
		private double qy;
		private double qz;
		private int stepCount;
		private int stepOffset;
		private MicroLocation current;

		
		public LineScanner(MicroLocation from, MicroLocation to) {
			this.from = from;
			this.to = to;
			
			long dx = to.x - from.x;
			long dy = to.y - from.y;
			long dz = to.z - from.z;

			int length = (int) Math.ceil(Math.sqrt(dx*dx + dy*dy + dz*dz));
			stepCount = (length*6)/5;

			qx = ((double) dx/stepCount);
			qy = ((double) dy/stepCount);
			qz = ((double) dz/stepCount);

			stepOffset = -1;
		}
		
		public MicroLocation next(CncHead head) {
			if (stepOffset<0) {
				stepOffset = 0;
				current = from;
			} else if (stepOffset==stepCount) {
				return null;
			} else {
				CncLocation lastCnc = head.toCncLocation(current);
				MicroLocation last = current;
				current = null;
				while(stepOffset<stepCount) {
					stepOffset++;
					long nx = from.x + (long) Math.round(qx*stepOffset);
					long ny = from.y + (long) Math.round(qy*stepOffset);
					long nz = from.z + (long) Math.round(qz*stepOffset);
					MicroLocation next = last.with(nx, ny, nz);
					if (next!=last) {
						CncLocation cncLocation = head.toCncLocation(next);
						if (!cncLocation.equals(lastCnc)) {
							current = next;
							break;
						}
					}
				}
			}
			return current;
		}
		
	}

	
}
