package net.github.douwevos.cnc.ui.plan;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.SwingUtilities;

import net.github.douwevos.cnc.head.MicroLocation;
import net.github.douwevos.cnc.model.value.Item;
import net.github.douwevos.cnc.model.value.Layer;
import net.github.douwevos.cnc.model.value.Model;
import net.github.douwevos.cnc.plan.CncHeadState;
import net.github.douwevos.cnc.plan.CncLineTo;
import net.github.douwevos.cnc.plan.CncPlan;
import net.github.douwevos.cnc.plan.CncPlanFactory;
import net.github.douwevos.cnc.plan.CncPlanItem;
import net.github.douwevos.cnc.ui.ModelGraphics;
import net.github.douwevos.cnc.ui.ModelViewer;
import net.github.douwevos.justflat.shape.PolygonLayer;
import net.github.douwevos.justflat.values.Bounds2D;
import net.github.douwevos.justflat.values.FracLine2D;
import net.github.douwevos.justflat.values.FracPoint2D;
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
//		PolygonLayer allContours = planViewModel.allContours;
//		if (allContours != null) {
//			for(Polygon contour : allContours) {
//				drawContour(modelGraphics, contour, false);
//			}
//		}
		List<CncPlan> planList = planViewModel.getPlanList();
		if (planList != null) {
			for(CncPlan cncPlan : planList) {
				drawCncPlan(modelGraphics, cncPlan);
			}
		}
		
	}
	
	
	private void drawCncPlan(ModelGraphics modelGraphics, CncPlan cncPlan) {
		if (cncPlan == null) {
			return;
		}
		modelGraphics.colorDefault();
		Point2D lastLocation = null;
		for(CncPlanItem item : cncPlan) {
			if (item instanceof CncLineTo) {
				MicroLocation microLocation = ((CncLineTo) item).getLocation();
				Point2D location = toPoint(microLocation);
				if (lastLocation!=null) {
					modelGraphics.drawLine(lastLocation, location);
				}
				lastLocation = location;
			} else if (item instanceof CncHeadState) {
				if (((CncHeadState) item).headUp) {
					modelGraphics.colorSelection();
				} else {
					modelGraphics.colorDefault();
				}
			}
		}
	}
	
	public Point2D toPoint(MicroLocation location) {
		long x = location.x;
		long y = location.y;
		return new Point2D(x, y);
	}


	private void drawContour(ModelGraphics modelGraphics, Polygon contour, boolean ghost) {
		if (ghost) {
			modelGraphics.colorHighlight();
		} else { 
			modelGraphics.colorDefault();
		}
		List<FracLine2D> lines = contour.createLines(true);
		Set<FracPoint2D> points = new HashSet<>();
		for(FracLine2D line : lines) {
			modelGraphics.drawLine(line.pointA(), line.pointB());
			points.add(line.pointA());
			points.add(line.pointB());
		}

		double zoom = camera.getZoom();
		if (zoom<4) {
			int r = (int) Math.ceil(5 * camera.getZoom());
			if (!ghost) {
				modelGraphics.colorDot();
			}
			for(FracPoint2D p : points) {
				modelGraphics.drawCircle(p, r, true);
			}
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
			
			if (planViewModel2!=null && planViewModel2.getPlanList() == null) {
	
				PolygonLayer contourLayer = new PolygonLayer();
				Model model = planViewModel2.getSnapshot();
				Layer layer = model.layerAt(0);
				for(Item item : layer) {
					item.writeToContourLayer(contourLayer, 0);
				}
				planViewModel.setGhostLayer(contourLayer);
				
				
				long toolDiameter = 150;
				long depth = 0;
				
				CncPlanFactory cncPlanFactory = new CncPlanFactory();
				List<CncPlan> producedPlanList = cncPlanFactory.producePlanList(model, 0, toolDiameter, depth);
				planViewModel.setPlanList(producedPlanList);
				
				
				SwingUtilities.invokeLater(() -> {
					repaint();
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
