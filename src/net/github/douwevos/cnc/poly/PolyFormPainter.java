//package net.github.douwevos.cnc.poly;
//
//import java.awt.Graphics2D;
//
//import net.github.douwevos.justflat.values.Point2D;
//
//public class PolyFormPainter {
//
//	private final PolyForm polyForm;
//	
//	public PolyFormPainter(PolyForm polyForm) {
//		this.polyForm = polyForm;
//	}
//
//	
//	public void draw(Graphics2D gfx, int x, int y) {
//		GfxPolyOutput output = new GfxPolyOutput(gfx);
//		polyForm.produce(output, 1d, x, y);
//	}
//
//	private static class GfxPolyOutput implements PolyForm.PolyOutput {
//
//		private final Graphics2D gfx;
//		
//		public GfxPolyOutput(Graphics2D gfx) {
//			this.gfx = gfx;
//		}
//		
//
//		@Override
//		public void line(int dotIdx, Point2D pointA, Point2D pointB) {
//			int xa = (int) pointA.x;
//			int ya = (int) pointA.y;
//			int xb = (int) pointB.x;
//			int yb = (int) pointB.y;
//			gfx.drawLine(xa, ya, xb, yb);
//		}
//
//		@Override
//		public void contourBegin() {
//		}
//
//		@Override
//		public void contourEnd() {
//		}
//		
//	}
//
//	
//}
