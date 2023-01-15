package net.github.douwevos.cnc.ui.plan;

import net.github.douwevos.cnc.model.EditableModel;
import net.github.douwevos.cnc.model.value.Model;
import net.github.douwevos.justflat.contour.ContourLayer;
import net.github.douwevos.justflat.types.values.Bounds2D;

public class PlanViewModel {
	
	Model snapshot;
	EditableModel editableModel;
	
	ContourLayer allContours;
	ContourLayer ghostLayer;
	
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

	public void setGhostLayer(ContourLayer ghostLayer) {
		this.ghostLayer = ghostLayer;
	}

	public void setPlanPath(ContourLayer allContours) {
		this.allContours = allContours;
	}
	
	public ContourLayer getAllContours() {
		return allContours;
	}

}
