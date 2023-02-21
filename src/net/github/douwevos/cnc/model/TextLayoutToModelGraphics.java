package net.github.douwevos.cnc.model;

import net.github.douwevos.cnc.ui.ModelGraphics;
import net.github.douwevos.justflat.ttf.TextLayout;
import net.github.douwevos.justflat.ttf.TextLayoutGlyph;
import net.github.douwevos.justflat.values.Point2D;


public class TextLayoutToModelGraphics {

	
	public void draw(ModelGraphics modelGraphics, TextLayout textLayout, long textSize, Point2D location) {
		double scalar = (double) textSize/textLayout.getMaxHeight();
		GfxGlyphOutput glyphOutput = new GfxGlyphOutput(modelGraphics, scalar, textLayout.getMaxHeight());
		long x = location.x;
		long y = location.y;
		for(TextLayoutGlyph glyph : textLayout) {
			glyph.produce(glyphOutput, scalar, x, y);
		}
	}
	
	private static class GfxGlyphOutput implements TextLayoutGlyph.GlyphOutput {

		private final ModelGraphics modelGraphics;
		private final double scalar;
		private final int maxHeight;
		
		public GfxGlyphOutput(ModelGraphics modelGraphics, double scalar, int maxHeight) {
			this.modelGraphics = modelGraphics;
			this.scalar = scalar;
			this.maxHeight = (int) (maxHeight*scalar);
		}

		@Override
		public void line(Point2D pointA, Point2D pointB) {
			modelGraphics.drawLine(pointA, pointB);
		}

		@Override
		public void contourBegin() {
		}

		@Override
		public void contourEnd() {
		}
		
	}

}
