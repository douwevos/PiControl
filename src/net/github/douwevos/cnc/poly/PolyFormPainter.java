package net.github.douwevos.cnc.poly;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.List;

import net.github.douwevos.cnc.layer.Layer;
import net.github.douwevos.cnc.layer.OnOffArea;
import net.github.douwevos.cnc.layer.StartStop;
import net.github.douwevos.justflat.types.values.Point2D;

public class PolyFormPainter {

	private final PolyForm polyForm;
	
	public PolyFormPainter(PolyForm polyForm) {
		this.polyForm = polyForm;
	}

	
	public void draw(Graphics2D gfx, int x, int y) {
		Rectangle clipBounds = gfx.getClipBounds();

		GfxPolyOutput output = new GfxPolyOutput(gfx);
		polyForm.produce(output, 1d, x, y);

		
//		OnOffArea onOffArea = new OnOffArea(0, clipBounds.height);
//		Layer layer = new Layer(0, clipBounds.height);
//		LayerPolyOutput output = new LayerPolyOutput(layer, onOffArea);
//		polyForm.produce(output, 1d, x, y);
//		
//		
//		for(int idx=0; idx<layer.lineCount(); idx++) {
//			LayerLine layerLine = layer.layerLine[idx];
//			if (layerLine==null) {
//				continue;
//			}
//			for(StartStop ss : layerLine.startStops) {
//				gfx.drawLine((int) ss.start, idx, (int) ss.stop, idx);
//			}
//		}
		
	}
	
	private static class LayerPolyOutput implements PolyForm.PolyOutput {

		private final Layer layer;
		private final OnOffArea onOffArea;
		
		public LayerPolyOutput(Layer layer, OnOffArea onOffArea) {
			this.layer = layer;
			this.onOffArea = onOffArea;
//			this.gfx = onOffArea;
		}
		

		@Override
		public void line(int idx, Point2D pointA, Point2D pointB) {
//			int xa = (int) pointA.x;
//			int ya = (int) pointA.y;
//			int xb = (int) pointB.x;
//			int yb = (int) pointB.y;
//			gfx.drawLine(xa, ya, xb, yb);
			onOffArea.line(pointA, pointB);
		}

		@Override
		public void contourBegin() {
		}

		@Override
		public void contourEnd() {
			int lineCount = (int) (layer.top()-layer.bottom());
			for(int lineIndex=0; lineIndex<lineCount; lineIndex++) {
				List<StartStop> startStopList = onOffArea.lineToStartStopList(lineIndex);
				if (startStopList != null) {
					layer.invert(lineIndex, startStopList);
				}
			}
			onOffArea.reset();
		}
		
	}


	private static class GfxPolyOutput implements PolyForm.PolyOutput {

		private final Graphics2D gfx;
		
		public GfxPolyOutput(Graphics2D gfx) {
			this.gfx = gfx;
		}
		

		@Override
		public void line(int dotIdx, Point2D pointA, Point2D pointB) {
			int xa = (int) pointA.x;
			int ya = (int) pointA.y;
			int xb = (int) pointB.x;
			int yb = (int) pointB.y;
			gfx.drawLine(xa, ya, xb, yb);
		}

		@Override
		public void contourBegin() {
		}

		@Override
		public void contourEnd() {
		}
		
	}

	
}
