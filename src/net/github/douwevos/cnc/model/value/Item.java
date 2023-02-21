package net.github.douwevos.cnc.model.value;

import net.github.douwevos.justflat.shape.PolygonLayer;
import net.github.douwevos.justflat.values.Bounds2D;

public interface Item {

	void writeToContourLayer(PolygonLayer polygonLayer, long atDepth);

	Bounds2D calculateBounds();
	
	long getMaxDepth();

}
