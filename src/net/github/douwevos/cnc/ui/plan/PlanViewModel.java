package net.github.douwevos.cnc.ui.plan;

import net.github.douwevos.cnc.model.EditableModel;
import net.github.douwevos.cnc.model.value.Model;
import net.github.douwevos.justflat.shape.PolygonLayer;
import net.github.douwevos.justflat.values.Bounds2D;

public class PlanViewModel {
	
	Model snapshot;
	EditableModel editableModel;
	
	PolygonLayer allContours;
	PolygonLayer ghostLayer;
	
	public PlanViewModel(EditableModel editableModel) {
		this.editableModel = editableModel;
		snapshot = editableModel.snapshot();
	}
	

	public Bounds2D bounds() {
		return editableModel.calculateBounds();
	}
	
	public Model getSnapshot() {
		return snapshot;
	}

	public void setGhostLayer(PolygonLayer ghostLayer) {
		this.ghostLayer = ghostLayer;
	}

	public void setPlanPath(PolygonLayer allContours) {
		this.allContours = allContours;
	}
	
	public PolygonLayer getAllContours() {
		return allContours;
	}

}
