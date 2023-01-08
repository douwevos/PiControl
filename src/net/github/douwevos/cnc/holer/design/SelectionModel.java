package net.github.douwevos.cnc.holer.design;

import java.util.ArrayList;
import java.util.List;

import net.github.douwevos.cnc.holer.ItemGrabInfo;

public class SelectionModel {

	
	public ItemGrabInfo highlight;
	
	private List<ItemGrabInfo> selected = new ArrayList<ItemGrabInfo>();
	
	private List<Listener> listeners = new ArrayList<>();
	
	
	public ItemGrabInfo getHighlight() {
		return highlight;
	}
	
	public void setHighlight(ItemGrabInfo highlight) {
		this.highlight = highlight;
	}
	
	public void addSelection(ItemGrabInfo item, boolean append) {
		if (!append) {
			selected.clear();
		}
		if (item!=null) {
			selected.add(item);
		}
		
		notifyListeners();
	}

	
	public Iterable<ItemGrabInfo> selections() {
		return selected;
	}
	
	public void startDrag(long mouseX, long mouseY) {
		for(ItemGrabInfo info : selected) {
			info.startDrag(mouseX, mouseY);
		}
	}

	public void doDrag(long mouseX, long mouseY) {
		for(ItemGrabInfo info : selected) {
			info.doDrag(mouseX, mouseY);
		}
		notifyListeners();
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	private void notifyListeners() {
		for(Listener listener : listeners) {
			listener.selectionChanged();
		}
	}

	
	public interface Listener {
		public void selectionChanged();
	}


	public void deleteSelected() {
		for(ItemGrabInfo info : selected) {
			info.delete();
		}
		selected.clear();
	}

	public boolean isEmpty() {
		return selected.isEmpty();
	}

	
}
