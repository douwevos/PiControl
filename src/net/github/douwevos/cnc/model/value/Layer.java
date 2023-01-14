package net.github.douwevos.cnc.model.value;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Layer implements Iterable<Item> {

	private final List<Item> items;
	
	public Layer(List<Item> items) {
		this.items = new ArrayList<>();
		if (items!=null) {
			this.items.addAll(items);
		}
	}

	public Layer setItems(List<Item> newItems) {
		if (newItems.size() == items.size()) {
			boolean sameContent = true;
			for(int idx=0; idx<items.size(); idx++) {
				if (items.get(idx)!=newItems.get(idx)) {
					sameContent = false;
					break;
				}
			}
			if (sameContent) {
				return this;
			}
		}
		return new Layer(newItems);
	}

	
	@Override
	public Iterator<Item> iterator() {
		return items.iterator();
	}
}
