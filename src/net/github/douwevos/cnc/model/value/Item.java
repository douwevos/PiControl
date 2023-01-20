package net.github.douwevos.cnc.model.value;

import net.github.douwevos.justflat.shape.PolygonLayer;

public interface Item {

	void writeToContourLayer(PolygonLayer polygonLayer, long atDepth);

}
