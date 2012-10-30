/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vgi.geometrictools;

import java.awt.geom.Point2D;
import java.text.NumberFormat;

/**
 *
 * @author JLiu
 */
public class Vector2D {

	private double x;
	private double y;
	private Double length;
	private Double angle;

	public Vector2D(double x, double y) {
		this.x = x;
		this.y = y;
		this.length = null;
		this.angle = null;
	}

	public Vector2D(Point2D point2d) {
		this(point2d.getX(), point2d.getY());
	}

	public static Vector2D fromPolarCoordinates(double length, double angle) {
		if (length < 0) {
			throw new IllegalArgumentException("Input length is less than 0.");
		}
		return new Vector2D(length * Math.cos(angle), length * Math.sin(angle));
	}  // End public static Vector2D fromPolarCoordinates(double length, double angle)

	public double getX() {
		return this.x;
	}

	public void setX(double x) {
		if (x != this.x) {
			this.x = x;
			this.length = null;
			this.angle = null;
		}
	}  // End public void setX(double x)

	public double getY() {
		return this.y;
	}

	public void setY(double y) {
		if (y != this.y) {
			this.y = y;
			this.length = null;
			this.angle = null;
		}
	}  // End public void setY(double y)

	@Override
	public boolean equals(Object object) {
		if (object instanceof Vector2D) {
			Vector2D vector2d = (Vector2D) object;
			return ((this.x == vector2d.x) && (this.y == vector2d.y));
		}
		return super.equals(object);
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 83 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
		hash = 83 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
		return hash;
	}

	@Override
	public String toString() {
		NumberFormat numberFormat = NumberFormat.getInstance();
		numberFormat.setGroupingUsed(false);
		numberFormat.setMinimumFractionDigits(0);
		return "(" + numberFormat.format(this.x) + ", " + numberFormat.format(this.y) + ")";
	}

	public static Vector2D add(double x1, double y1, double x2, double y2) {
		return new Vector2D(x1 + x2, y1 + y2);
	}

	public Vector2D add(Vector2D vector2d) {
		return Vector2D.add(this.x, this.y, vector2d.x, vector2d.y);
	}

	public static double angle(double x, double y) {
		double tempAngle = Math.acos(x / Vector2D.length(x, y));
		if (y < 0) {
			return -tempAngle;
		}
		return tempAngle;
	}  // End public static double angle(double x, double y)

	public double angle() {
		if (this.angle != null) {
			return angle.doubleValue();
		}
		this.angle = Vector2D.angle(this.x, this.y);
		return this.angle.doubleValue();
	}  // End public double angle()

	public static double dotProduct(double x1, double y1, double x2, double y2) {
		return (x1 * x2 + y1 * y2);
	}

	public double dotProduct(Vector2D vector2d) {
		return Vector2D.dotProduct(this.x, this.y, vector2d.x, vector2d.y);
	}

	public static boolean isAntiParallel(double x1, double y1, double x2, double y2) {
		return (Vector2D.isParallelOrAntiParallel(x1, y1, x2, y2)
				&& (Vector2D.dotProduct(x1, y1, x2, y2) < 0));
	}

	public boolean isAntiParallel(Vector2D vector2d) {
		return Vector2D.isAntiParallel(this.x, this.y, vector2d.x, vector2d.y);
	}

	public static boolean isParallel(double x1, double y1, double x2, double y2) {
		return (Vector2D.isParallelOrAntiParallel(x1, y1, x2, y2)
				&& (Vector2D.dotProduct(x1, y1, x2, y2) > 0));
	}

	public boolean isParallel(Vector2D vector2d) {
		return Vector2D.isParallel(this.x, this.y, vector2d.x, vector2d.y);
	}

	public static boolean isParallelOrAntiParallel(double x1, double y1, double x2, double y2) {
		double lengthSquared1 = Vector2D.lengthSquared(x1, y1);
		double lengthSquared2 = Vector2D.lengthSquared(x2, y2);
		double dotProduct = Vector2D.dotProduct(x1, y1, x2, y2);
		return (lengthSquared1 * lengthSquared2 == dotProduct * dotProduct);
	}  // End public static boolean isParallel(double x1, double y1, double x2, double y2)

	public boolean isParallelOrAntiParallel(Vector2D vector2d) {
		return Vector2D.isParallelOrAntiParallel(this.x, this.y, vector2d.x, vector2d.y);
	}

	public static boolean isPerpendicular(double x1, double y1, double x2, double y2) {
		Vector2D vector2d = Vector2D.rotate90DegreesPositively(x2, y2);
		return Vector2D.isParallelOrAntiParallel(x1, y1, vector2d.x, vector2d.y);
	}

	public boolean isPerpendicular(Vector2D vector2d) {
		return Vector2D.isPerpendicular(this.x, this.y, vector2d.x, vector2d.y);
	}

	public static double length(double x, double y) {
		return Math.sqrt(Vector2D.lengthSquared(x, y));
	}

	public double length() {
		if (this.length != null) {
			return this.length.doubleValue();
		}
		this.length = Vector2D.length(this.x, this.y);
		return this.length.doubleValue();
	}  // End public double length()

	public static double lengthSquared(double x, double y) {
		return (x * x + y * y);
	}

	public double lengthSquared() {
		return Vector2D.lengthSquared(this.x, this.y);
	}

	public static Vector2D reverse(double x, double y) {
		return new Vector2D(-x, -y);
	}

	public Vector2D reverse() {
		return Vector2D.reverse(this.x, this.y);
	}

	public static Vector2D rotate(double x, double y, double angle) {
		return new Vector2D(x * Math.cos(angle) - y * Math.sin(angle), x * Math.sin(angle) + y * Math.cos(angle));
	}

	public Vector2D rotate(double angle) {
		return Vector2D.rotate(this.x, this.y, angle);
	}

	public static Vector2D rotate90DegreesPositively(double x, double y) {
		return new Vector2D(-y, x);
	}

	public Vector2D rotate90DegreesPositively() {
		return Vector2D.rotate90DegreesPositively(this.x, this.y);
	}

	public static Vector2D rotate90DegreesNegatively(double x, double y) {
		return new Vector2D(y, -x);
	}

	public Vector2D rotate90DegreesNegatively() {
		return Vector2D.rotate90DegreesNegatively(this.x, this.y);
	}

	public static Vector2D scalarProduct(double x, double y, double scalar) {
		return new Vector2D(scalar * x, scalar * y);
	}

	public Vector2D scalarProduct(double scalar) {
		return Vector2D.scalarProduct(this.x, this.y, scalar);
	}

	public static Vector2D subtract(double x1, double y1, double x2, double y2) {
		return new Vector2D(x1 - x2, y1 - y2);
	}

	public Vector2D subtract(Vector2D vector2d) {
		return Vector2D.subtract(this.x, this.y, vector2d.x, vector2d.y);
	}

	public static Vector2D unitVecotr(double x, double y) {
		double length = Vector2D.length(x, y);
		return new Vector2D(x / length, y / length);
	}

	public Vector2D unitVector() {
		return Vector2D.unitVecotr(this.x, this.y);
	}

	public static void main(String args[]) {

		Vector2D vector = new Vector2D(3, 4);
		Vector2D vector2 = new Vector2D(12, 5);
		NumberFormat numberFormat = NumberFormat.getInstance();
		numberFormat.setGroupingUsed(false);
		numberFormat.setMinimumFractionDigits(0);

		System.out.println(vector + " add " + vector2 + " = "
				+ vector.add(vector2));
		System.out.println(vector + "'s angle = " + numberFormat.format(
				vector.angle()));
		System.out.println(vector2 + "'s angle = " + numberFormat.format(
				vector2.angle()));
		System.out.println(vector + " dot product " + vector2 + " = " + numberFormat.format(
				vector.dotProduct(vector2)));
		System.out.println("Vector with length of sqrt(2) and angle of PI * 3 / 4) = "
				+ Vector2D.fromPolarCoordinates(Math.sqrt(2), Math.PI * 3 / 4));
		System.out.println(vector + " is anti parallel to " + vector2 + " = "
				+ vector.isAntiParallel(vector2));
		System.out.println(vector + " is anti parallel to " + vector.scalarProduct(-2) + " = "
				+ vector.isAntiParallel(vector.scalarProduct(-2)));
		System.out.println(vector + " is parallel to " + vector2 + " = "
				+ vector.isParallel(vector2));
		System.out.println(vector + " is parallel to " + vector.scalarProduct(3) + " = "
				+ vector.isParallel(vector.scalarProduct(3)));
		System.out.println(vector + " is perpendicular to " + vector2 + " = "
				+ vector.isPerpendicular(vector2));
		System.out.println(vector + " is perpendicular to " + vector.rotate90DegreesPositively() + " = "
				+ vector.isPerpendicular(vector.rotate90DegreesPositively()));
		System.out.println(vector + "'s length = " + numberFormat.format(
				vector.length()));
		System.out.println(vector2 + "'s length = " + numberFormat.format(
				vector2.length()));
		System.out.println(vector + "'s length squared = " + numberFormat.format(
				vector.lengthSquared()));
		System.out.println(vector2 + "'s length squared = " + numberFormat.format(
				vector2.lengthSquared()));
		System.out.println(vector + "'s reverse = "
				+ vector.reverse());
		System.out.println(vector2 + "'s reverse = "
				+ vector2.reverse());
		System.out.println("Rotate " + vector + " by 30 degrees = "
				+ vector.rotate(Math.PI / 6) + " whose angle = " + numberFormat.format(
				vector.rotate(Math.PI / 6).angle()));
		System.out.println("Rotate " + vector2 + " by 30 degrees = "
				+ vector2.rotate(Math.PI / 6) + " whose angle = " + numberFormat.format(
				vector2.rotate(Math.PI / 6).angle()));
		System.out.println("Rotate " + vector + " 90 degrees positively = "
				+ vector.rotate90DegreesPositively());
		System.out.println("Rotate " + vector2 + " 90 degrees positively = "
				+ vector2.rotate90DegreesPositively());
		System.out.println("Rotate " + vector + " 90 degrees negatively = "
				+ vector.rotate90DegreesNegatively());
		System.out.println("Rotate " + vector2 + " 90 degrees negatively = "
				+ vector2.rotate90DegreesNegatively());
		double scalar = -2.5;
		System.out.println(vector + " scalar pruduct " + numberFormat.format(scalar) + " = "
				+ vector.scalarProduct(scalar));
		System.out.println(vector2 + " scalar pruduct " + numberFormat.format(scalar) + " = "
				+ vector2.scalarProduct(scalar));
		System.out.println(vector + " subtract " + vector2 + " = "
				+ vector.subtract(vector2));
		System.out.println(vector + "'s unit vector = "
				+ vector.unitVector());
		System.out.println(vector2 + "'s unit vector = "
				+ vector2.unitVector());
	}  // End public static void main(String args[])
}  // End public class Vector2D
