package net.github.douwevos.cnc.ui.plan;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.SwingUtilities;

import net.github.douwevos.cnc.model.value.Item;
import net.github.douwevos.cnc.model.value.Layer;
import net.github.douwevos.cnc.model.value.Model;
import net.github.douwevos.cnc.ui.ModelGraphics;
import net.github.douwevos.cnc.ui.ModelViewer;
import net.github.douwevos.justflat.shape.PolygonLayer;
import net.github.douwevos.justflat.shape.PolygonLayerNonSimpleToSimpleSplitter;
import net.github.douwevos.justflat.shape.PolygonLayerResolutionReducer;
import net.github.douwevos.justflat.shape.scaler.PolygonLayerScaler;
import net.github.douwevos.justflat.values.Bounds2D;
import net.github.douwevos.justflat.values.Line2D;
import net.github.douwevos.justflat.values.Point2D;
import net.github.douwevos.justflat.values.shape.Polygon;

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
		PolygonLayer ghostLayer = planViewModel.ghostLayer;
		if (ghostLayer != null) {
			for(Polygon contour : ghostLayer) {
				drawContour(modelGraphics, contour, true);
			}
		}
		PolygonLayer allContours = planViewModel.allContours;
		if (allContours != null) {
			for(Polygon contour : allContours) {
				drawContour(modelGraphics, contour, false);
			}
		}
		
	}
	
	
	private void drawContour(ModelGraphics modelGraphics, Polygon contour, boolean ghost) {
		if (ghost) {
			modelGraphics.faintDefault();
		} else { 
			modelGraphics.colorDefault();
		}
		List<Line2D> lines = contour.createLines(true);
		Set<Point2D> points = new HashSet<>();
		for(Line2D line : lines) {
			modelGraphics.drawLine(line.pointA(), line.pointB());
			points.add(line.pointA());
			points.add(line.pointB());
		}

		int r = (int) Math.ceil(5 * camera.getZoom());
		if (!ghost) {
			modelGraphics.colorDot();
		}
		for(Point2D p : points) {
			modelGraphics.drawCircle(p, r, true);
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
	
				PolygonLayer contourLayer = new PolygonLayer();
				
				Model model = planViewModel2.getSnapshot();
				Layer layer = model.layerAt(0);
				for(Item item : layer) {
					item.writeToContourLayer(contourLayer, 0);
				}
				
				PolygonLayer allContours =  new PolygonLayer();
				
//				for(Contour contour : contourLayer.contours) {
//					allContours.add(contour);
//				}
				
				
				long toolDiameter = 150;
				
				int discSize = 800;
				int discSizeSq = discSize*discSize;


				PolygonLayerResolutionReducer resolutionReducer = new PolygonLayerResolutionReducer();
				PolygonLayer reducedResolution = resolutionReducer.reduceResolution(contourLayer, discSizeSq, 1);

				PolygonLayerNonSimpleToSimpleSplitter splitter = new PolygonLayerNonSimpleToSimpleSplitter();
				PolygonLayer cutted = splitter.createSimplePolygonLayer(reducedResolution);

				
				
//				for(Contour contour : cutted.contours) {
//					allContours.add(contour);
//				}

				
				for(int idx=1; idx<100; idx++) {
					PolygonLayer duplicate = cutted.duplicate();
					PolygonLayerScaler contourLayerScaler = new PolygonLayerScaler();
					PolygonLayer scaled = contourLayerScaler.scale(duplicate, idx*toolDiameter, false);
					if (scaled.isEmpty()) {
						break;
					}
					System.err.println("scaled.count="+scaled.count());
					for(Polygon contour : scaled) {
						allContours.add(contour);
					}
				}
				System.err.println("allCo="+allContours);
				
				planViewModel.setPlanPath(allContours);
				planViewModel.setGhostLayer(cutted);
				SwingUtilities.invokeLater(() -> {
					
					repaint();
					System.err.println("repaint:"+cutted.count());
					
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
		System.err.println("##"+planViewModel);
		modelImageDirty = true;
		repaint();
	}

}
