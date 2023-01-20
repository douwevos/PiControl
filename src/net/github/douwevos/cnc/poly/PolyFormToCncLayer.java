package net.github.douwevos.cnc.poly;

import java.util.List;

import net.github.douwevos.cnc.layer.Layer;
import net.github.douwevos.cnc.layer.OnOffArea;
import net.github.douwevos.cnc.layer.StartStop;
import net.github.douwevos.justflat.values.Point2D;

public class PolyFormToCncLayer {

	private final PolyForm polyForm;
	private final double scalar;
	
	public PolyFormToCncLayer(PolyForm polyForm, double scalar) {
		this.polyForm = polyForm;
		this.scalar = scalar;
	}

	public void produceLayer(Layer layer, long x, long y) {
		OnOffArea onOffArea = new OnOffArea(layer.bottom(), (int) (layer.top()-layer.bottom()));
		
		PolyFormOutput glyphOutput = new PolyFormOutput(layer, onOffArea);
		polyForm.produce(glyphOutput, scalar, x, y);
	}
	
	
	private static class PolyFormOutput implements PolyForm.PolyOutput {

		private final Layer layer;
		private final OnOffArea onOffArea;
		
		public PolyFormOutput(Layer layer, OnOffArea onOffArea) {
			this.layer = layer;
			this.onOffArea = onOffArea;
		}

		@Override
		public void line(int dotIndex, Point2D pointA, Point2D pointB) {
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
	
}
