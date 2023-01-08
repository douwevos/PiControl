package douwe.design;

public class Camera {

	final double xAngle;
	final double yAngle;
	final double zAngle;
	
	public Camera(double xAngle, double yAngle, double zAngle) {
		this.xAngle = xAngle;
		this.yAngle = yAngle;
		this.zAngle = zAngle;
	}

	
	public Camera xRotate(double addX) {
		return new Camera(xAngle + addX, yAngle, zAngle);
	}

	public Camera yRotate(double addY) {
		return new Camera(xAngle, yAngle + addY, zAngle);
	}

	public Camera zRotate(double addZ) {
		return new Camera(xAngle, yAngle, zAngle + addZ);
	}
	
	public TransformMatrix3D createMatrix() {
		TransformMatrix3D identity = TransformMatrix3D.identity();
		TransformMatrix3D rotate = identity.rotate(xAngle, yAngle, zAngle);
		return rotate;
	}

	public TransformMatrix3D createReverseMatrix() {
		TransformMatrix3D identity = TransformMatrix3D.identity();
		TransformMatrix3D rotate = identity.rotateReverse(xAngle, yAngle, zAngle);
		return rotate;
	}


}
