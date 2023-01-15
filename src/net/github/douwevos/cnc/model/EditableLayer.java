package net.github.douwevos.cnc.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.github.douwevos.cnc.model.value.Item;
import net.github.douwevos.cnc.model.value.Layer;
import net.github.douwevos.justflat.types.values.Bounds2D;

public class EditableLayer implements Iterable<Editable> {

	private ModelChangeDispatcher modelChangeDispatcher;
	
	public List<Editable> items = new ArrayList<>();
	
	private Layer build;

	public Layer snapshot() {
		List<Item> items = new ArrayList<>(this.items.size());
		for(Editable editable : this.items) {
			Item item = editable.snapshot();
			items.add(item);
		}
		build = build == null ? new Layer(items) : build.setItems(items);
		return build;
	}

	
	public void addItem(Editable item) {
		item.attach(modelChangeDispatcher);
		items.add(item);
		dispatchModelChanged();
	}
	
	public Bounds2D calculateBounds() {
		if (items.isEmpty()) {
			return null;
		}
		Bounds2D result = null;
		for(Editable item : items) {
			Bounds2D itemBounds = item.calculateBounds();
			if (itemBounds == null) {
				continue;
			}
			if (result == null) {
				result = itemBounds;
			} else {
				result = result.union(itemBounds);
			}
		}
		return result;
	}
	
	@Override
	public Iterator<Editable> iterator() {
		return items.iterator();
	}

	public void attach(ModelChangeDispatcher modelChangeDispatcher) {
		if (this.modelChangeDispatcher == modelChangeDispatcher) {
			return;
		}
		this.modelChangeDispatcher = modelChangeDispatcher;
		for(Editable item : items) {
			item.attach(modelChangeDispatcher);
		}
	}
	
	private void dispatchModelChanged() {
		if (modelChangeDispatcher!=null) {
			modelChangeDispatcher.notifyChanged();
		}
	}

	public boolean containsItem(Editable item) {
		return items.stream().anyMatch(p -> item.equals(p));
	}


}
