package net.github.douwevos.cnc.ui;

import java.awt.Dimension;
import java.awt.event.MouseEvent;

import net.github.douwevos.cnc.ui.controller.MouseEventType;

public class ModelMouseEvent {

	public final MouseEvent event;
	public final MouseEventType type;
	public final Camera camera;
	public final double modelX;
	public final double modelY;
	
	public ModelMouseEvent(MouseEvent event, MouseEventType type, Camera camera, double modelX, double modelY) {
		this.event = event;
		this.type = type;
		this.camera = camera;
		this.modelX = modelX;
		this.modelY = modelY;
	}
	
	
	public static ModelMouseEvent create(MouseEvent awtEvent, MouseEventType type, Camera camera, Dimension viewDimension) {
		int mouseY = awtEvent.getY();
		int mouseX = awtEvent.getX();
		
		int bottom = viewDimension.height-1;
		int loc = bottom-mouseY;

		double cameraZoom = camera.getZoom();
		double ny = camera.getTranslateY() + loc * cameraZoom;
		double nx = camera.getTranslateX() + mouseX * cameraZoom;
		return new ModelMouseEvent(awtEvent, type, camera, nx, ny);
		
	}
}
