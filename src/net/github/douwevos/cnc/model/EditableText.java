package net.github.douwevos.cnc.model;


import net.github.douwevos.cnc.model.value.Item;
import net.github.douwevos.cnc.model.value.Text;
import net.github.douwevos.justflat.ttf.TextLayout;
import net.github.douwevos.justflat.values.Bounds2D;
import net.github.douwevos.justflat.values.Point2D;

public class EditableText extends Editable {

	private TextLayout textLayout;
	private Point2D location;
	private long textSize;
	private long depth;
	
	public EditableText(TextLayout textLayout, Point2D location, long textSize, long depth) {
		super(ItemType.TEXT);
		this.textLayout = textLayout;
		this.location = location;
		this.textSize = textSize;
		this.depth = depth;
	}

	@Override
	protected Item snapshot() {
		return new Text(textLayout, location, textSize, depth);
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

	
	public TextLayout getTextLayout() {
		return textLayout;
	}
	
	public long getTextSize() {
		return textSize;
	}
	
	public Point2D getLocation() {
		return location;
	}
}
