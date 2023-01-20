package net.github.douwevos.cnc.model;

import net.github.douwevos.cnc.model.value.Item;
import net.github.douwevos.justflat.values.Bounds2D;

public abstract class Editable {
	
	protected ModelChangeDispatcher modelChangeDispatcher;
	public final ItemType itemType;
	
	protected Editable(ItemType itemType) {
		this.itemType = itemType;
	}

	protected abstract Item snapshot();
	
	public abstract Bounds2D calculateBounds();

	protected void attach(ModelChangeDispatcher modelChangeDispatcher) {
		this.modelChangeDispatcher = modelChangeDispatcher;
	}
	
	protected void dispatchModelChanged() {
		if (modelChangeDispatcher!=null) {
			modelChangeDispatcher.notifyChanged();
		}
	}

}
