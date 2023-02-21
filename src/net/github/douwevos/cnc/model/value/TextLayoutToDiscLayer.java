package net.github.douwevos.cnc.model.value;

import net.github.douwevos.justflat.shape.PolygonLayer;
import net.github.douwevos.justflat.ttf.TextLayout;
import net.github.douwevos.justflat.ttf.TextLayoutGlyph;
import net.github.douwevos.justflat.values.Point2D;
import net.github.douwevos.justflat.values.shape.Polygon;
import net.github.douwevos.justflat.values.shape.Polygon.PolygonBuilder;

public class TextLayoutToDiscLayer {
	
	public void produceLayer(PolygonLayer layer, TextLayout textLayout, long textSize, Point2D location) {
		double scalar = (double) textSize/textLayout.getMaxHeight();
		LayerGlyphOutput glyphOutput = new LayerGlyphOutput(layer);
		long x = location.x;
		long y = location.y;
		for(TextLayoutGlyph glyph : textLayout) {
			glyphOutput.startGlyph();
			glyph.produce(glyphOutput, scalar, x, y);
		}
	}

	
	private static class LayerGlyphOutput implements TextLayoutGlyph.GlyphOutput {

		private final PolygonLayer layer;
		private PolygonBuilder polygonBuilder;
		boolean first = true;
		
		public LayerGlyphOutput(PolygonLayer layer) {
			this.layer = layer;
		}

		public void startGlyph() {
			first = true;
		}

		@Override
		public void line(Point2D pointA, Point2D pointB) {
			if (polygonBuilder.isEmpty()) {
				polygonBuilder.add(pointA);
			}
			polygonBuilder.add(pointB);
		}

		@Override
		public void contourBegin() {
			polygonBuilder = new Polygon().builder();
		}

		@Override
		public void contourEnd() {
			if (!first) {
				polygonBuilder.reverse();
			}
			polygonBuilder.closed(true);
			layer.add(polygonBuilder.build());
			
			first = false;
		}
		
	}	
	

	
	
}
