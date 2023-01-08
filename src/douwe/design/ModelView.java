package douwe.design;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import douwe.Point3D;
import douwe.design.Model.Vertex;

public class ModelView {

	private Model model;
	private Camera camera;
	
	private List<Point3D> viewPoints;
	private Integer snapPointIndex;
	
	public ModelView(Model model, Camera camera) {
		this.model = model;
		this.camera = camera;
	}
	
	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	
	public void rebuild() {

		TransformMatrix3D cameraMatrix = camera.createMatrix();
		

//		TransformMatrix3D offset = rotate.offset(width/2, height/2, 0);
//		TransformMatrix3D projected = rotate.projection();

		viewPoints = model.streamPoints()
			.map(p -> { return cameraMatrix.apply(p); })
			.collect(Collectors.toList());
		
		
		
		
//		model.streamPoints().map(p -> {
//			return transform.apply(p);
//		}).collect(Collectors.toList());
//
		
	}

	public void paint(Graphics2D gfx) {
		gfx.setColor(Color.YELLOW);
		List<Vertex> vertexList = model.getVertexList();
		double mul = 1500d; 
		double depth = 1500;

		for(Vertex vertex : vertexList) {
			Point3D a = viewPoints.get(vertex.from);
			Point3D b = viewPoints.get(vertex.to);
			
			int x1 = (int) ((a.x * mul) / (a.z+depth));
			int y1 = (int) ((a.y * mul) / (a.z+depth));
			int x2 = (int) ((b.x * mul) / (b.z+depth));
			int y2 = (int) ((b.y * mul) / (b.z+depth));

//			int x1 = (int) a.x;
//			int y1 = (int) a.y;
//			int x2 = (int) b.x;
//			int y2 = (int) b.y;
			gfx.drawLine(x1, y1, x2,y2);
		}
		
		if (snapPointIndex != null) {
			Point3D point = viewPoints.get(snapPointIndex);
			int x1 = (int) ((point.x * mul) / (point.z+depth));
			int y1 = (int) ((point.y * mul) / (point.z+depth));
			gfx.setColor(new Color(255,0,255,192));
			gfx.fillArc(x1-4, y1-4, 9, 9, 0, 360);
		}
		
	}

	public Integer findSnapPointIndex(int mouseX, int mouseY) {
		double mul = 1500d; 
		double depth = 1500;
		Point3D nearest = null;
		long bestDestSq = 100000;
		for(Point3D p : viewPoints) {
			int px = (int) ((p.x * mul) / (p.z+depth));
			int py = (int) ((p.y * mul) / (p.z+depth));

			long dx = px-mouseX;
			long dy = py-mouseY;
			long distSq = dx*dx + dy*dy;
			if (nearest==null || distSq<bestDestSq) {
				nearest = p;
				bestDestSq = distSq;
			}
		}
		
		if (bestDestSq>30*30) {
			return null;
		}
		
		int indexOf = viewPoints.indexOf(nearest);
		return indexOf<0 ? null : indexOf;
	}

	public boolean selectSnapPointIndex(Integer snapPointIndex) {
		boolean result = Objects.equals(snapPointIndex, this.snapPointIndex);
		this.snapPointIndex = snapPointIndex;
		return result;
	}

	public Point3D getViewPoint(int index) {
		return viewPoints.get(index);
	}
	
	
	public Point3D toPerspectiveView(Point3D in) {
		if (in==null) {
			return null;
		}
		double mul = 1500d; 
		double depth = 1500;
		int newX = (int) Math.round((in.x * mul) / (in.z+depth));
		int newY = (int) Math.round((in.y * mul) / (in.z+depth));
		return Point3D.of(newX, newY, in.z);
	}

	public Point3D toPlanarView(Point3D in) {
		if (in==null) {
			return null;
		}
		double mul = 1500d; 
		double depth = 1500;

		long resX = Math.round((in.x * (in.z+depth))/mul); 
		long resY = Math.round((in.y * (in.z+depth))/mul); 
		
		return Point3D.of(resX, resY, in.z);
	}

	
	public Point3D perspectivePointToModel(Point3D perspectivePoint) {
		if (perspectivePoint == null) {
			return null;
		}
		
		Point3D planarPoint = toPlanarView(perspectivePoint);
		
		TransformMatrix3D reverseMatrix = camera.createReverseMatrix();
		return reverseMatrix.apply(planarPoint);
	}

	
}
