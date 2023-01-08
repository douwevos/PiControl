package net.github.douwevos.cnc.ttf;

import java.util.List;

import net.github.douwevos.cnc.layer.Layer;
import net.github.douwevos.cnc.layer.OnOffArea;
import net.github.douwevos.cnc.layer.StartStop;
import net.github.douwevos.justflat.types.values.Point2D;

public class TextLayoutToCncLayer {

	private final TextLayout textLayout;
	private final double scalar;
	
	public TextLayoutToCncLayer(TextLayout textLayout, long size) {
		this.textLayout = textLayout;
		this.scalar = (double) size/textLayout.getMaxHeight();
	}

	public void produceLayer(Layer layer, long x, long y) {
		OnOffArea onOffArea = new OnOffArea(layer.bottom(), (int) (layer.top()-layer.bottom()));
		
		LayerGlyphOutput glyphOutput = new LayerGlyphOutput(layer, onOffArea);
		
		for(TextLayoutGlyph glyph : textLayout) {
			glyph.produce(glyphOutput, scalar, x, y);
		}
	}
	
	
	private static class LayerGlyphOutput implements TextLayoutGlyph.GlyphOutput {

		private final Layer layer;
		private final OnOffArea onOffArea;
		
		public LayerGlyphOutput(Layer layer, OnOffArea onOffArea) {
			this.layer = layer;
			this.onOffArea = onOffArea;
		}

		@Override
		public void line(Point2D pointA, Point2D pointB) {
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
