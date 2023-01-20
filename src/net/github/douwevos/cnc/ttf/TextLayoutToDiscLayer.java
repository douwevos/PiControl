package net.github.douwevos.cnc.ttf;

import net.github.douwevos.cnc.layer.disc.DiscLayer;
import net.github.douwevos.justflat.values.Point2D;
import net.github.douwevos.justflat.values.shape.Polygon;

public class TextLayoutToDiscLayer {

	private final TextLayout textLayout;
	private final double scalar;
	
	public TextLayoutToDiscLayer(TextLayout textLayout, long size) {
		this.textLayout = textLayout;
		this.scalar = (double) size/textLayout.getMaxHeight();
	}
	
	
	public void produceLayer(DiscLayer layer, long x, long y) {
		
		LayerGlyphOutput glyphOutput = new LayerGlyphOutput(layer);
		
		for(TextLayoutGlyph glyph : textLayout) {
			glyphOutput.startGlyph();
			
			glyph.produce(glyphOutput, scalar, x, y);
		}

	}

	
	private static class LayerGlyphOutput implements TextLayoutGlyph.GlyphOutput {

		private final DiscLayer layer;
		Polygon contour;
		boolean first = true;
		
		public LayerGlyphOutput(DiscLayer layer) {
			this.layer = layer;
		}

		public void startGlyph() {
			first = true;
		}

		@Override
		public void line(Point2D pointA, Point2D pointB) {
			if (contour.isEmpty()) {
				contour.add(pointA);
			}
			contour.add(pointB);
		}

		@Override
		public void contourBegin() {
			contour = new Polygon();
			layer.add(contour);
		}

		@Override
		public void contourEnd() {
			if (!first) {
				contour.reverse();
			}
			
			first = false;
		}
		
	}	
	

	
	
}
