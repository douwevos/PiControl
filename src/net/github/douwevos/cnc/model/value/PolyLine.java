package net.github.douwevos.cnc.model.value;

import net.github.douwevos.cnc.poly.PolyForm;

public class PolyLine implements Item {

	private final PolyForm polyForm;
	private final long depth;
	
	public PolyLine(PolyForm polyForm, long depth) {
		this.polyForm = polyForm;
		this.depth = depth;
	}
	
	public PolyForm getPolyForm() {
		return polyForm;
	}
	
	public long getDepth() {
		return depth;
	}

}
