package net.github.douwevos.cnc.model.value;

import net.github.douwevos.justflat.shape.PolygonLayer;
import net.github.douwevos.justflat.ttf.TextLayout;
import net.github.douwevos.justflat.values.Bounds2D;
import net.github.douwevos.justflat.values.Point2D;

public class Text implements Item {

	private final TextLayout textLayout;
	private final Point2D location;
	private final long textSize;
	private final long depth;
	
	public Text(TextLayout textLayout, Point2D location, long textSize, long depth) {
		this.textLayout = textLayout;
		this.location = location;
		this.textSize = textSize;
		this.depth = depth;
	}


	@Override
	public Bounds2D calculateBounds() {
		Bounds2D layoutBounds = textLayout.calculateBounds();
		int maxHeight = textLayout.getMaxHeight();
		
		long x = location.x;
		long y = location.y;
		double scalar = (double) textSize/maxHeight;
		Bounds2D scaled = layoutBounds.scale(scalar);
		return new Bounds2D(x + scaled.left, y + scaled.bottom, x + scaled.right, y + scaled.top);
	}

	@Override
	public long getMaxDepth() {
		return depth;
	}

	@Override
	public void writeToContourLayer(PolygonLayer polygonLayer, long atDepth) {
//		if (atDepth>depth) {
//			return;
//		}
		TextLayoutToDiscLayer textLayoutToLayer = new TextLayoutToDiscLayer();
		textLayoutToLayer.produceLayer(polygonLayer, textLayout, textSize, location);
	}

	
}
