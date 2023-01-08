package net.github.douwevos.cnc.run;

public enum Direction {
		RIGHT(1,0, 100),
		UP(0,1, 70),
		LEFT(-1,0, 50),
		DOWN(0,-1, 20),
//		RIGHT_DOWN(1,-1),
//		RIGHT_UP(1,1),
//		LEFT_DOWN(-1,-1),
//		LEFT_UP(-1,1)
		;
		
		public int deltaX;
		public int deltaY;
		public int preferFactor;
		
		Direction(int dx, int dy, int preferFactor) {
			deltaX = dx;
			deltaY = dy;
			this.preferFactor = preferFactor;
		}
	}