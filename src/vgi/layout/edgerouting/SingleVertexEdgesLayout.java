/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vgi.layout.edgerouting;

import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;
import java.util.*;
import vgi.geometrictools.Vector2D;

/**
 *
 * @author JLiu
 */
public class SingleVertexEdgesLayout extends mxGraphLayout {

	public SingleVertexEdgesLayout(mxGraph graph) {
		super(graph);
	}

	@Override
	public void execute(Object parent) {

		if (this.graph == null) {
			throw new IllegalStateException("The required protected variable 'graph' of this class is null.");
		}
		if (parent == null) {
			parent = this.graph.getDefaultParent();
		}

		Object objects[] = this.graph.getChildVertices(parent);
		int verticesCount = objects.length;

		for (int index = 0; index < verticesCount; index++) {

			if (!(objects[index] instanceof mxICell)) {
				throw new IllegalStateException("A vertex is not of the type mxICell.");
			}
			mxICell vertex = (mxICell) objects[index];
			SingleVertexEdgesLayout.run(vertex);

		}  // End for (int index = 0; index < verticesCount; index++)

		this.graph.refresh();
	}  // End public void execute(Object parent)

	public static void run(mxICell vertex) {

		if ((vertex == null) || (!(vertex.isVertex()))) {
			throw new IllegalArgumentException("Input vertex is null or not a vertex.");
		}
		mxGeometry vertexGeometry = vertex.getGeometry();
		if (vertexGeometry == null) {
			throw new IllegalArgumentException("The 'vertex' variable has null geometry.");
		}

		List<Vector2D> fixedVectors = new LinkedList<Vector2D>();
		List<mxGeometry> movableGeometries = new LinkedList<mxGeometry>();

		int edgeCount = vertex.getEdgeCount();
		for (int index = 0; index < edgeCount; index++) {

			mxICell edge = vertex.getEdgeAt(index);
			if ((edge == null) || (!(edge.isEdge()))) {
				throw new IllegalStateException("The 'edge' variable is null or not an edge.");
			}
			mxGeometry geometry = edge.getGeometry();
			if (geometry == null) {
				throw new IllegalStateException("The 'edge' variable has null geometry.");
			}
			List<mxPoint> points = geometry.getPoints();
			mxICell source = edge.getTerminal(true);
			mxICell target = edge.getTerminal(false);
			boolean isEdgeMovable = false;
			mxPoint point = null;
			if (source == null) {
				if (target == null) {
					throw new IllegalStateException("The 'edge' variable has null source and target vertices.");
				}
				isEdgeMovable = true;
			} else if (target == null) {
				isEdgeMovable = true;
			} else if (source == target) {
				isEdgeMovable = true;
			} else if (source == vertex) {
				if ((points == null) || (points.isEmpty())) {
					mxGeometry targetGeometry = target.getGeometry();
					if (targetGeometry == null) {
						throw new IllegalStateException("The 'target' variable has null geometry.");
					}
					point = new mxPoint(targetGeometry.getCenterX(), targetGeometry.getCenterY());
				} else {
					point = points.get(0);
				}
			} else if (target == vertex) {
				if ((points == null) || (points.isEmpty())) {
					mxGeometry sourceGeometry = source.getGeometry();
					if (sourceGeometry == null) {
						throw new IllegalStateException("The 'source' variable has null geometry.");
					}
					point = new mxPoint(sourceGeometry.getCenterX(), sourceGeometry.getCenterY());
				} else {
					point = points.get(points.size() - 1);
				}
			} else {
				throw new IllegalStateException("This edge is not connected to the vertex in question.");
			}

			if (isEdgeMovable) {
				movableGeometries.add(geometry);
			} else if (point == null) {
				throw new IllegalStateException("The current edge is fixed but a point on the edge is not found.");
			} else {
				fixedVectors.add(Vector2D.subtract(point.getX(), point.getY(),
						vertexGeometry.getCenterX(), vertexGeometry.getCenterY()));
			}

		}  // End for (int index = 0; index < edgeCount; index++)

		if (fixedVectors.size() == 2) {
			if (Vector2DComparator.compareAngles(fixedVectors.get(0), fixedVectors.get(1)) > 0) {
				Vector2D vector2d = fixedVectors.remove(0);
				fixedVectors.add(vector2d);
			}
		} else if (fixedVectors.size() > 2) {
			Collections.sort(fixedVectors, new Vector2DComparator());
		}

		List<AngularRange> angularRanges = new LinkedList<AngularRange>();

		if (fixedVectors.isEmpty()) {
			AngularRange angularRange = new AngularRange();
			angularRange.startVector2d = Vector2D.rotate(1, 0, -2 * Math.PI / edgeCount);
			angularRange.angularSpan = 2 * Math.PI;
			angularRanges.add(angularRange);
		}  // End if (fixedVectors.isEmpty())

		for (int index = 0; index < fixedVectors.size(); index++) {

			AngularRange angularRange = new AngularRange();
			angularRange.startVector2d = fixedVectors.get(index);
			double currentAngle = angularRange.startVector2d.angle();
			if (currentAngle < 0) {
				currentAngle = currentAngle + 2 * Math.PI;
			}
			int nextIndex = (index + 1 >= fixedVectors.size()) ? 0 : (index + 1);
			double nextAngle = fixedVectors.get(nextIndex).angle();
			if (nextAngle < 0) {
				nextAngle = nextAngle + 2 * Math.PI;
			}
			if (nextIndex == 0) {
				nextAngle = nextAngle + 2 * Math.PI;
			}
			angularRange.angularSpan = nextAngle - currentAngle;
			angularRanges.add(angularRange);

		}  // End for (int index = 0; index < fixedVectors.size(); index++)

		if (angularRanges.size() == 2) {
			if (angularRanges.get(0).compareTo(angularRanges.get(1)) > 0) {
				AngularRange angularRange = angularRanges.remove(0);
				angularRanges.add(angularRange);
			}
		} else if (angularRanges.size() > 2) {
			Collections.sort(angularRanges);
		}

		double idealAngle = 2 * Math.PI / edgeCount;
		Iterator<AngularRange> iterateAngularRange = angularRanges.iterator();
		while (iterateAngularRange.hasNext()) {

			AngularRange angularRange = iterateAngularRange.next();

			int count = 1;
			for (; count <= movableGeometries.size(); count++) {
				if (angularRange.angularSpan / count < idealAngle) {
					break;
				}
			}  // End for (; count <= movableGeometries.size(); count++)

			if (count > movableGeometries.size()) {
				count = movableGeometries.size();
			}

			for (int index = 0; index < count; index++) {

				Vector2D direction = angularRange.startVector2d.rotate(angularRange.angularSpan * (index + 1) / (count + 1)).unitVector();
				mxGeometry geometry = movableGeometries.remove(0);
				mxPoint sourcePoint = geometry.getSourcePoint();
				mxPoint targetPoint = geometry.getTargetPoint();
				Vector2D position = new Vector2D(vertexGeometry.getCenterX(), vertexGeometry.getCenterY());
				double distance;

				if (sourcePoint == null) {

					if (targetPoint == null) {

						List<mxPoint> points = geometry.getPoints();
						if (points == null) {
							points = new LinkedList<mxPoint>();
						}
						if (points.isEmpty()) {
							distance = vertexGeometry.getWidth();
						} else {
							mxPoint point = points.get(0);
							distance = Vector2D.subtract(point.getX(), point.getY(),
									position.getX(), position.getY()).length();
							points.clear();
						}
						position = position.add(direction.scalarProduct(distance));
						points.add(new mxPoint(position.getX(), position.getY()));
						geometry.setPoints(points);
						points = null;  // points = new LinkedList<mxPoint>();

					} else {  // End if (targetPoint == null)

						distance = Vector2D.subtract(
								targetPoint.getX(), targetPoint.getY(),
								position.getX(), position.getY()).length();
						position = position.add(direction.scalarProduct(distance));
						targetPoint.setX(position.getX());
						targetPoint.setY(position.getY());
						geometry.setTargetPoint(targetPoint);

					}  // End else part of if (targetPoint == null)

				} else if (targetPoint == null) {

					distance = Vector2D.subtract(
							sourcePoint.getX(), sourcePoint.getY(),
							position.getX(), position.getY()).length();
					position = position.add(direction.scalarProduct(distance));
					sourcePoint.setX(position.getX());
					sourcePoint.setY(position.getY());
					geometry.setSourcePoint(sourcePoint);

				} else {
					throw new IllegalStateException("The 'edge' variable is not a movalbe edge.");
				}

			}  // End for (int index = 0; index < count; index++)

			if (movableGeometries.size() <= 0) {
				break;
			}

		}  // End while (iterateAngularRange.hasNext())

	}  // End public void static run(mxICell vertex)

	protected static class Vector2DComparator implements Comparator<Vector2D> {

		public static int compareAngles(Vector2D vector1, Vector2D vector2) {
			double angle1 = vector1.angle();
			if (angle1 < 0) {
				angle1 = 2 * Math.PI + angle1;
			}
			double angle2 = vector2.angle();
			if (angle2 < 0) {
				angle2 = 2 * Math.PI + angle2;
			}
			double difference = angle1 - angle2;
			if (difference < 0) {
				return -1;
			} else if (difference == 0) {
				return 0;
			} else {
				return 1;
			}
		}  // End public static int compareAngles(Vector2D vector1, Vector2D vector2)

		@Override
		public int compare(Vector2D vector1, Vector2D vector2) {
			return Vector2DComparator.compareAngles(vector1, vector2);
		}  // End public int compare(Vector2D vector1, Vector2D vector2)
	}  // End protected static class Vector2DComparator implements Comparator<Vector2D>

	protected static class AngularRange implements Comparable<AngularRange> {

		public Vector2D startVector2d;
		public double angularSpan;

		@Override
		public int compareTo(AngularRange anotherAngularRange) {
			double difference = this.angularSpan - anotherAngularRange.angularSpan;
			if (difference > 0) {
				return -1;
			} else if (difference == 0) {
				return 0;
			} else {
				return 1;
			}
		}  // End public int compareTo(AngularRange anotherAngularRange)
	}  // End protected static class AngularRange
}  // End public class SingleVertexEdgesLayout extends mxGraphLayout
