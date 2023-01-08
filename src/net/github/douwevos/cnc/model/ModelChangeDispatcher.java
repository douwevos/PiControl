package net.github.douwevos.cnc.model;

import java.util.ArrayList;
import java.util.List;

import net.github.douwevos.cnc.model.EditableModel.Listener;

public class ModelChangeDispatcher {

	private List<Listener> listeners = new ArrayList<>();
	
	public void notifyChanged() {
		for(Listener listener : listeners) {
			listener.onModelChanged();
		}
	}

	public void addListener(Listener listener) {
		this.listeners.add(listener);
	}
	
	public void removeListener(Listener listener) {
		this.listeners.remove(listener);
	}

}
