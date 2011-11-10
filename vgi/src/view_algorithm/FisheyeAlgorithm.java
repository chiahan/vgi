/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package view_algorithm;

import debug.Debug;
import java.awt.geom.Point2D;
import java.util.HashMap;
import model.DataModelInterface;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;

/**
 *
 * @author wkren
 */
public class FisheyeAlgorithm {
	private DataModelInterface model;
	//private static int distortionFactor = 5;
	private static int distortionFactor = 3;

    private static int vertexSizeScaleFactor = 1;
	private static HashMap stateMap = new HashMap();
	static int height, width;

	public static Point2D fisheyePoint(Point2D focus, Point2D state) {
		double focusX = focus.getX();
		double focusY = focus.getY();
		double stateX = state.getX();
		double stateY = state.getY();

		double DmaxX, DmaxY, DnormX, DnormY;
		if (focusX < stateX) {
			DmaxX = width - focusX;
			DnormX = stateX - focusX;
		}
		else {
			DmaxX = focusX;
			DnormX = focusX - stateX;
		}

		if (focusY < stateY) {
			DmaxY = height - focusY;
			DnormY = stateY -focusY;
		}
		else {
			DmaxY = focusY;
			DnormY = focusY - stateY;
		}

		double gX = (distortionFactor + 1) * DnormX /
					(distortionFactor * DnormX + DmaxX);
		double gY = (distortionFactor + 1) * DnormY /
					(distortionFactor * DnormY + DmaxY);

		double newX, newY;
		if (focusX < stateX)
			newX = focusX + gX * DmaxX;
		else
			newX = focusX - gX * DmaxX;

		if (focusY < stateY)
			newY = focusY + gY * DmaxY;
		else
			newY = focusY - gY * DmaxY;

//		Debug.echo("focus = (" + focusX + ", " + focusY + ")");
//		Debug.echo("state = (" + stateX + ", " + stateY + ")");
//		Debug.echo("new X = (" + newX + ", " + newY +")");
		return new Point2D.Double(newX, newY);
	}

	public static Point2D fisheyePoint2(Point2D focus, Point2D state) {
		double focusX = focus.getX();
		double focusY = focus.getY();
		double stateX = state.getX();
		double stateY = state.getY();
//		Debug.echo("focus x = " + focusX);
//		Debug.echo("focus y = " + focusY);
//		Debug.echo("state x = " + stateX);
//		Debug.echo("state y = " + stateY);

		double DmaxX = getDmaxX(focusX, stateX);
		double DmaxY = getDmaxY(focusY, stateY);
//		Debug.echo("Dmax X = " + DmaxX);
//		Debug.echo("Dmax Y = " + DmaxY);

		double DnormX = getDnorm(focusX, stateX);
		double DnormY = getDnorm(focusY, stateY);
//		Debug.echo("Dnorm X = " + DnormX);
//		Debug.echo("Dnorm Y = " + DnormY);

		double gX = g(DnormX / DmaxX);
		double gY = g(DnormY / DmaxY);
//		Debug.echo("g X = " + gX);
//		Debug.echo("g Y = " + gY);

		double newX = gX * DmaxX + focusX;
		double newY = gY * DmaxY + focusY;
//		Debug.echo("new X = " + newX);
//		Debug.echo("new Y = " + newY);

		Debug.echo("fisheye: calculate new point");
		return new Point2D.Double(newX, newY);
	}

	public void setDistortionFactor(int value) {
		distortionFactor = value;
	}

	public static void setHeight(int height) {
		FisheyeAlgorithm.height = height;
	}

	public static void setWidth(int width) {
		FisheyeAlgorithm.width = width;
	}

	public static double getSgeom(Point2D focus, Point2D point) {
		double x = Math.abs(point.getX() - focus.getX());
		double y = Math.abs(point.getY() - focus.getY());
		double result = Math.min(x, y) * Math.pow(0.75 * 1, 0.75);
		return result;
	}

	public static void setStateMap(HashMap stateMap) {
		for (int i=0; stateMap.containsKey(i); i++) {
			DefaultGraphCell state = (DefaultGraphCell) stateMap.get(i);
			double x = GraphConstants.getBounds((state).getAttributes()).getX();
			double y = GraphConstants.getBounds((state).getAttributes()).getY();
			FisheyeAlgorithm.stateMap.put(i, new Point2D.Double(x, y));
//			Debug.echo("origin x = " + x);
//			Debug.echo("origin y = " + y);
		}
	}

	public static HashMap getSateMap() {
		return stateMap;
	}

	public static int getVertexSizeScaleFactor() {
		return vertexSizeScaleFactor;
	}

	private static double g(double x) {
		return ((distortionFactor + 1) * x) / (distortionFactor * x + 1);
	}

	private static double getDmaxX(double focusX, double stateX) {
		if (focusX < stateX) {
			return width - focusX;
		}
		else {
			return -focusX;
		}
	}

	private static double getDmaxY(double focusY, double stateY) {
		if (focusY < stateY) {
			return height - focusY;
		}
		else {
			return -focusY;
		}
	}

	private static double getDnorm(double focus, double state) {
			return state - focus;
	}
}
