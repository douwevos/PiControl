package douwe.design;

import douwe.Point3D;

public class TransformMatrix3D {

	private final double m[][];
	
	private TransformMatrix3D(double m[][]) {
		this.m = m;
	}
	
	public static TransformMatrix3D identity() {
		double s[][] = new double[4][4];
		s[0][0] = 1d;
		s[1][1] = 1d;
		s[2][2] = 1d;
		s[3][3] = 1d;
		return new TransformMatrix3D(s);
	}
	
	private double[][] copy(double in[][]) {
		double[][] result = new double[4][4];
		for(int idx=0; idx<4; idx++) {
			result[idx][0] = in[idx][0];
			result[idx][1] = in[idx][1];
			result[idx][2] = in[idx][2];
			result[idx][3] = in[idx][3];
		}
		return result;
	}

	private double[][] multiply(double multiply[][], double with[][]) {
		double[][] result = new double[4][4];
		for(int x=0; x<4; x++) {
			for(int y=0; y<4; y++) {
				double m=0;
				for(int p=0; p<4; p++) {
					m = m + multiply[p][y]*with[x][p]; 
				}
				result[x][y] = m;
			}
		}
		return result;
	}

	
	public TransformMatrix3D offset(double xOffset, double yOffset, double zOffset) {
		double s[][] = copy(m);
		s[0][3] += xOffset;
		s[1][3] += yOffset;
		s[2][3] += zOffset;
		return new TransformMatrix3D(s);
	}

	public TransformMatrix3D rotate(double xAngle, double yAngle, double zAngle) {
		
		// rotate xAngle
		double sinX = Math.sin(xAngle);
		double cosX = Math.cos(xAngle);

		
		double[][] rotM = new double[4][4];
		rotM[0][0]=1;
		rotM[1][1]=cosX;
		rotM[2][1]=-sinX;
		rotM[1][2]=sinX;
		rotM[2][2]=cosX;
		
		double rotated[][] = multiply(m, rotM);

		rotM[0][0]=0;
		rotM[1][1]=0;
		rotM[2][1]=0;
		rotM[1][2]=0;
		rotM[2][2]=0;


		// rotate yAngle
		
		double sinY = Math.sin(yAngle);
		double cosY = Math.cos(yAngle);

		rotM[0][0]=cosY;
		rotM[2][0]=sinY;
		rotM[1][1]=1;
		rotM[0][2]=-sinY;
		rotM[2][2]=cosY;
		
		rotated = multiply(rotated, rotM);

		rotM[0][0]=0;
		rotM[2][0]=0;
		rotM[1][1]=0;
		rotM[0][2]=0;
		rotM[2][2]=0;


		// rotate zAngle
		
		double sinZ = Math.sin(zAngle);
		double cosZ = Math.cos(zAngle);
		
		rotM[0][0] = cosZ;
		rotM[1][0] = -sinZ;
		rotM[0][1] = sinZ;
		rotM[1][1] = cosZ;
		rotM[2][2] = 1;
		
		rotated = multiply(rotated,rotM);
		return new TransformMatrix3D(rotated);
	}
	
	

	public TransformMatrix3D rotateReverse(double xAngle, double yAngle, double zAngle) {
		double[][] rotM = new double[4][4];
		
		// rotate zAngle
		
		double sinZ = Math.sin(-zAngle);
		double cosZ = Math.cos(-zAngle);
		
		rotM[0][0] = cosZ;
		rotM[1][0] = -sinZ;
		rotM[0][1] = sinZ;
		rotM[1][1] = cosZ;
		rotM[2][2] = 1;

		double rotated[][] = multiply(m, rotM);

		rotM[0][0] = 0;
		rotM[1][0] = 0;
		rotM[0][1] = 0;
		rotM[1][1] = 0;
		rotM[2][2] = 0;


		// rotate yAngle
		
		double sinY = Math.sin(-yAngle);
		double cosY = Math.cos(-yAngle);

		rotM[0][0]=cosY;
		rotM[2][0]=sinY;
		rotM[1][1]=1;
		rotM[0][2]=-sinY;
		rotM[2][2]=cosY;
		
		rotated = multiply(rotated, rotM);

		rotM[0][0]=0;
		rotM[2][0]=0;
		rotM[1][1]=0;
		rotM[0][2]=0;
		rotM[2][2]=0;

		
		// rotate xAngle
		double sinX = Math.sin(-xAngle);
		double cosX = Math.cos(-xAngle);

		
		rotM[0][0]=1;
		rotM[1][1]=cosX;
		rotM[2][1]=-sinX;
		rotM[1][2]=sinX;
		rotM[2][2]=cosX;
		
		rotated = multiply(rotated, rotM);
		
		return new TransformMatrix3D(rotated);
	}

	
	public TransformMatrix3D projection() {
		double angleOfView = 90; 
		double near = 10.1d; 
		double far = 60; 

//		double angleOfView = 90; 
//		double near = 0.9d; 
//		double far = 1; 
		
		
		double[][] projectionMatrix= new double[4][4];

//		double scale = 1d / Math.atan(angleOfView * Math.PI / 360d); 
//		projectionMatrix[0][0] = scale;  
//		projectionMatrix[1][1] = scale;  
//		projectionMatrix[2][2] = -far / (far - near);  
//		projectionMatrix[3][2] = -far * near / (far - near);  
//		projectionMatrix[2][3] = -1; // set w = -z 
//		projectionMatrix[3][3] = 0;

//		projectionMatrix[0][0] = scale;  
//		projectionMatrix[1][1] = scale;  
//		projectionMatrix[2][2] = -far / (far - near);  
//		projectionMatrix[2][3] = -far * near / (far - near);  
//		projectionMatrix[3][2] = -1; // set w = -z 
//		projectionMatrix[3][3] = 0;

		double scale = 1.1d / Math.atan(angleOfView * Math.PI / 180d); 
		projectionMatrix[0][0] = scale;  
		projectionMatrix[1][1] = scale;  
//		projectionMatrix[2][2] = 1;  
		projectionMatrix[2][1] = (far+near) / (far- near  );  
		projectionMatrix[3][1] = 2 * far * near / (far - near );  
		projectionMatrix[2][2] = 5;  
		projectionMatrix[3][2] = 1;  
		projectionMatrix[2][3] = -1; // set w = -z 
		projectionMatrix[3][3] = 0;

		
		double[][] result = multiply(m, projectionMatrix);
		return new TransformMatrix3D(result);
	}

	
	public Point3D apply(Point3D in) {
		double inX = in.x;
		double inY = in.y;
		double inZ = in.z;
		
		double outX = (m[0][0] * inX) + (m[0][1] * inY)  + (m[0][2] * inZ)  + (m[0][3]); 
		double outY = (m[1][0] * inX) + (m[1][1] * inY)  + (m[1][2] * inZ)  + (m[1][3]); 
		double outZ = (m[2][0] * inX) + (m[2][1] * inY)  + (m[2][2] * inZ)  + (m[2][3]); 
		
		return Point3D.of(Math.round(outX), Math.round(outY), Math.round(outZ));
	}
	
	
}
