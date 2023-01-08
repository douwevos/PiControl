package net.github.douwevos.cnc.tool;

import net.github.douwevos.cnc.type.Distance;

public class FraseTool implements Tool {

	private final int diameter;
	
	public FraseTool(int diameter) {
		this.diameter = diameter;
	}

	public FraseTool(Distance diameter) {
		this.diameter = (int) diameter.asMicrometers();
	}

	@Override
	public int getDiameter() {
		return diameter;
	}
}
