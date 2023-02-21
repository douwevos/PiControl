package net.github.douwevos.cnc.ui.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.github.douwevos.cnc.model.EditableModel;
import net.github.douwevos.cnc.model.value.Model;
import net.github.douwevos.cnc.ui.Camera;
import net.github.douwevos.justflat.values.Point2D;

public class SelectionModel {

	
	public ItemGrabInfo<?> highlight;
	
	private List<ItemGrabInfo<?>> selected = new ArrayList<ItemGrabInfo<?>>();
	
	private List<Listener> listeners = new ArrayList<>();
	
	
	private Point2D modelPointStart;
	
	public ItemGrabInfo<?> getHighlight() {
		return highlight;
	}
	
	public void setHighlight(ItemGrabInfo<?> highlight) {
		if (Objects.equals(this.highlight, highlight)) {
			return;
		}
		this.highlight = highlight;
		notifyListeners();
	}
	
	public boolean addSelection(ItemGrabInfo<?> item, boolean append) {
		boolean changed = false;
		if (!append && !selected.isEmpty()) {
			selected.clear();
			changed = true;
		}
		if (item!=null) {
			if (!selected.contains(item)) {
				selected.add(item);
				changed = true;
			}
		}
		
		if (changed) {
			notifyListeners();
		}
		return changed;
	}

	public boolean hasSelected(ItemGrabInfo<?> grabInfoP) {
		return selected.contains(grabInfoP);
	}
	
	public Iterable<ItemGrabInfo<?>> selections() {
		return selected;
	}
	
	public void startDrag(long mouseX, long mouseY) {
		for(ItemGrabInfo<?> info : selected) {
			info.startDrag(mouseX, mouseY);
		}
	}

	public void doDrag(Camera camera, long mouseX, long mouseY) {
		ArrayList<ItemGrabInfo<?>> selected = new ArrayList<>(this.selected);
		for(ItemGrabInfo<?> info : selected) {
			info.doDrag(camera, mouseX, mouseY);
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
		for(ItemGrabInfo<?> info : selected) {
			info.delete();
		}
		selected.clear();
		notifyListeners();
	}

	public boolean isEmpty() {
		return selected.isEmpty();
	}

	public void clear() {
		selected.clear();
		notifyListeners();
	}

	public void validate(EditableModel model) {
		boolean didChange = false;
		for(int idx=selected.size()-1; idx>=0; idx--) {
			ItemGrabInfo<?> info = selected.get(idx);
			if (!info.isValid() || !model.containsItem(info.item)) {
				selected.remove(idx);
				didChange = true;
			}
		}
		
		if (highlight!=null) {
			if (!highlight.isValid() || !model.containsItem(highlight.item)) {
				highlight = null;
				didChange = true;
			}
			
		}
		if (didChange) {
			notifyListeners();
		}
	}

	public void startSelectArea(Point2D modelPointStart) {
		this.modelPointStart = modelPointStart;
	}
	
}
