package net.github.douwevos.cnc.ui.plan;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.util.List;

import javax.swing.SwingUtilities;

import net.github.douwevos.cnc.model.value.Item;
import net.github.douwevos.cnc.model.value.Layer;
import net.github.douwevos.cnc.model.value.Model;
import net.github.douwevos.cnc.ui.ModelGraphics;
import net.github.douwevos.cnc.ui.ModelViewer;
import net.github.douwevos.justflat.contour.Contour;
import net.github.douwevos.justflat.contour.ContourLayer;
import net.github.douwevos.justflat.contour.ContourLayerOverlapCutter;
import net.github.douwevos.justflat.contour.ContourLayerResolutionReducer;
import net.github.douwevos.justflat.contour.scaler.ContourLayerScaler;
import net.github.douwevos.justflat.types.values.Bounds2D;
import net.github.douwevos.justflat.types.values.Line2D;

public class PlanView extends ModelViewer implements Runnable {


	private volatile boolean keepRunning = false;
	PlanViewModel planViewModel;
	
	public PlanView() {
	}
	
	
	@Override
	public void paint(Graphics g) {
		if (planViewModel == null) {
			return;
		}
		
		Graphics2D gfx = (Graphics2D) g;
		
		Image modelImage = updateModelImage();

		gfx.drawImage(modelImage, 0, 0, null);
		
	}


	@Override
	protected void drawModelImage(ModelGraphics modelGraphics) {
		modelGraphics.colorDefault();
		ContourLayer allContours = planViewModel.allContours;
		if (allContours == null) {
			return;
		}
		for(Contour contour : allContours) {
			drawContour(modelGraphics, contour);
		}
		
	}
	
	
	private void drawContour(ModelGraphics modelGraphics, Contour contour) {
		List<Line2D> lines = contour.createLines(true);
		for(Line2D line : lines) {
			modelGraphics.drawLine(line.pointA(), line.pointB());
		}
	}


	@Override
	public void componentShown(ComponentEvent e) {
		super.componentShown(e);
		System.err.println("showing");
		keepRunning = true;
		new Thread(this).start();
	}
	
	@Override
	public void componentHidden(ComponentEvent e) {
		super.componentHidden(e);
		synchronized (this) {
			keepRunning = false;
			this.notifyAll();
		}
	}
	
	
	public void setPlanViewModel(PlanViewModel planViewModel) {
		this.planViewModel = planViewModel;
	}
	
	
	@Override
	public Bounds2D getModelBounds() {
		return planViewModel==null ? null : planViewModel.bounds();
	}

	
	
	@Override
	public void run() {
		while(keepRunning) {
			
			PlanViewModel planViewModel2 = planViewModel;
			
			if (planViewModel2!=null && planViewModel2.getAllContours() == null) {
	
				ContourLayer contourLayer = new ContourLayer(10, 10);
				
				Model model = planViewModel2.getSnapshot();
				Layer layer = model.layerAt(0);
				for(Item item : layer) {
					item.writeToContourLayer(contourLayer, 0);
				}
				
				ContourLayer allContours =  new ContourLayer(10, 10);
				
				for(Contour contour : contourLayer.contours) {
					allContours.add(contour);
				}
				
				
				long toolDiameter = 150;
				
				
				ContourLayerResolutionReducer resolutionReducer = new ContourLayerResolutionReducer();
				ContourLayer reducedResolution = resolutionReducer.reduceResolution(contourLayer, toolDiameter/2d, 1);
				
				ContourLayerOverlapCutter cutter = new ContourLayerOverlapCutter();
				ContourLayer cutted = cutter.scale(reducedResolution, true);
				
				
				for(int idx=1; idx<100; idx++) {
					ContourLayerScaler contourLayerScaler = new ContourLayerScaler();
					ContourLayer scaled = contourLayerScaler.scale(cutted, idx*toolDiameter, false);
					if (scaled.isEmpty()) {
						break;
					}
					for(Contour contour : scaled.contours) {
						allContours.add(contour);
					}
				}
				System.err.println("allCo="+allContours);
				
				planViewModel.setPlanPath(allContours);
				SwingUtilities.invokeLater(() -> {
					
					repaint();
					System.err.println("repaint");
					
				});
				repaintModel();
			}
			
			synchronized (this) {
				try {
					this.wait(200);
				} catch (InterruptedException e) {
				}
			}
		}
	}
	
	private void repaintModel() {
		modelImageDirty = true;
		repaint();
	}

}
