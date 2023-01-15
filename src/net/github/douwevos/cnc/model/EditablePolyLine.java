package net.github.douwevos.cnc.model;

import java.util.Objects;

import net.github.douwevos.cnc.model.value.Item;
import net.github.douwevos.cnc.model.value.PolyLine;
import net.github.douwevos.cnc.poly.PolyForm;
import net.github.douwevos.justflat.types.values.Bounds2D;

public class EditablePolyLine extends Editable {

	private PolyForm polyForm;
	private long depth;
	
	private PolyLine build;
	
	public EditablePolyLine(PolyForm polyForm, long depth) {
		super(ItemType.POLYLINE);
		this.polyForm = polyForm;
		this.depth = depth;
	}

	@Override
	protected Item snapshot() {
		if (build != null && (build.getPolyForm()!=polyForm || build.getDepth()!=depth)) {
			build = null;
		}
		if (build == null) {
			build = new PolyLine(polyForm, depth);
		}
		return build;
	}
	
	public PolyForm getPolyForm() {
		return polyForm;
	}
	
	public void setPolyForm(PolyForm polyForm) {
		if (Objects.equals(this.polyForm, polyForm)) {
			return;
		}
		this.polyForm = polyForm;
		dispatchModelChanged();
		
	}
	
	public long getDepth() {
		return depth;
	}
	
	@Override
	public Bounds2D calculateBounds() {
		return polyForm.calculateBounds();
	}

}
