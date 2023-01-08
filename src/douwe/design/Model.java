package douwe.design;

import static douwe.Point3D.of;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import douwe.Point3D;

public class Model {

	private List<Point3D> pointList = new ArrayList<Point3D>();

	private List<Vertex> vertexList = new ArrayList<>();


	
	public Model() {
		pointList.add(of(-250,-250,100));
		pointList.add(of(250,-250,100));
		pointList.add(of(250,250,100));
		pointList.add(of(-250,250,100));

		pointList.add(of(-250,-250,400));
		pointList.add(of(250,-250,400));
		pointList.add(of(250,250,400));
		pointList.add(of(-250,250,400));

		vertexList.add(new Vertex(0, 1));
		vertexList.add(new Vertex(1, 2));
		vertexList.add(new Vertex(2, 3));
		vertexList.add(new Vertex(3, 0));

		vertexList.add(new Vertex(4, 5));
		vertexList.add(new Vertex(5, 6));
		vertexList.add(new Vertex(6, 7));
		vertexList.add(new Vertex(7, 4));

		vertexList.add(new Vertex(0, 4));
		vertexList.add(new Vertex(1, 5));
		vertexList.add(new Vertex(2, 6));
		vertexList.add(new Vertex(3, 7));
	}

	
	public void setPoint(int index, Point3D point) {
		pointList.set(index, point);
		
	}

	public Stream<Point3D> streamPoints() {
		return pointList.stream();
	}
	
	public List<Vertex> getVertexList() {
		return vertexList;
	}
	
	public static class Vertex {
		public final int from;
		public final int to;
		
		public Vertex(int from, int to) {
			this.from = from;
			this.to = to;
		}
	}



}
