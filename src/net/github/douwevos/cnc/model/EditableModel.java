package net.github.douwevos.cnc.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.github.douwevos.cnc.model.value.Layer;
import net.github.douwevos.cnc.model.value.Model;
import net.github.douwevos.justflat.types.values.Bounds2D;

public class EditableModel implements Iterable<EditableLayer> {

	private final ModelChangeDispatcher modelChangeDispatcher = new ModelChangeDispatcher();
	
	private List<EditableLayer> layers = new ArrayList<>();
	
	private Model build = null;
	
	public void addLayer(EditableLayer layer) {
		layer.attach(modelChangeDispatcher);
		layers.add(layer);
		modelChangeDispatcher.notifyChanged();
	}
	
	public Bounds2D calculateBounds() {
		if (layers.isEmpty()) {
			return null;
		}
		Bounds2D result = null;
		for(EditableLayer layer : layers) {
			Bounds2D layerBounds = layer.calculateBounds();
			if (layerBounds == null) {
				continue;
			}
			if (result == null) {
				result = layerBounds;
			} else {
				result = result.union(layerBounds);
			}
		}
		return result;
	}
	
	@Override
	public Iterator<EditableLayer> iterator() {
		return layers.iterator();
	}
	
	
	public EditableLayer layerAt(int index) {
		return layers.get(index);
	}
	
	public void addListener(Listener listener) {
		modelChangeDispatcher.addListener(listener);
	}
	
	public void removeListener(Listener listener) {
		modelChangeDispatcher.removeListener(listener);
	}
	
	public Model snapshot() {
		List<Layer> layers = new ArrayList<>(this.layers.size());
		for(EditableLayer editabelLayer : this.layers) {
			Layer layer = editabelLayer.snapshot();
			layers.add(layer);
		}
		build = build==null ? new Model(layers) : build.setLayers(layers);
		return build;
	}
	
	public interface Listener {
		public void onModelChanged();
	}

	public boolean containsItem(Editable item) {
		return layers.stream().anyMatch(l -> l.containsItem(item));
	}

}
