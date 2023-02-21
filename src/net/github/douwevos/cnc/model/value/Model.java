package net.github.douwevos.cnc.model.value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class Model {

	private final List<Layer> layers;
	
	public Model(List<Layer> layers) {
		this.layers = new ArrayList<>();
		if (layers != null) {
			this.layers.addAll(layers);
		}
	}

	public Model setLayers(List<Layer> newLayers) {
		if (layers.size() == newLayers.size()) {
			boolean contentSame = true;
			for(int idx=0; idx<layers.size(); idx++) {
				if (layers.get(idx)!=newLayers.get(idx)) {
					contentSame = false;
					break;
				}
			}
			if (contentSame) {
				return this;
			}
		}
		return new Model(newLayers);
	}

	public Layer layerAt(int index) {
		return layers.get(index);
	}
	
	public int layerCount() {
		return layers.size();
	}
	
	public Stream<Layer> streamLayers() {
		return layers.stream();
	}

}
