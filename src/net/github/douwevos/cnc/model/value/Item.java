package net.github.douwevos.cnc.model.value;

import net.github.douwevos.justflat.contour.ContourLayer;

public interface Item {

	void writeToContourLayer(ContourLayer contourLayer, long atDepth);

}
