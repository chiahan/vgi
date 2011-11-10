/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package layout_algorithm;

import java.awt.geom.Point2D;
import java.util.Vector;
import model.DataModelInterface;

/**
 * relocate nodes to the center of the canvas drawing area
 * does not change relative positioning between nodes
 * @author polar
 */
public class CenterGraph {

    /** contains tools required for graph manipulation */
	private DataModelInterface dmi;

    /** list of graph node ids */
	Vector<Integer> stateIDs;

    /**
     * constructor
     * @param dmi contains all necessary tools for graph manipulation
     * @author polar
     */
	public CenterGraph(DataModelInterface dmi) {
		this.dmi = dmi;
	}

    /**
     * executes graph algorithm to relocate nodes to the center of the canvas area
     * @author polar
     */
	public void run() {
		System.out.println("CenterGraph started");

		stateIDs = dmi.getAllStates();

		Point2D.Float pos;
		double minX = 9999;
		double minY = 9999;
		double maxX = -9999;
		double maxY = -9999;
		double margin = 50;

		if (stateIDs.size() != 0) {
			pos = dmi.getStatePosition(stateIDs.get(0));
			minX = pos.getX();
			minY = pos.getY();
			maxX = pos.getX();
			maxY = pos.getY();
		}

		//** find minX and minY
		for (int i = 0; i < stateIDs.size(); i++) {
			pos = dmi.getStatePosition(stateIDs.get(i));
			if (pos.getX() < minX)
				minX = pos.getX();
			if (pos.getY() < minY)
				minY = pos.getY();
		}
		//DEBUG
		//System.out.println("minX: " + minX);
		//System.out.println("minY: " + minY);

		if (minX != margin) {
			double diff = minX - margin;
			for (int i = 0; i < stateIDs.size(); i++) {
				pos = dmi.getStatePosition(stateIDs.get(i));
				double newX = pos.getX() - diff;
				dmi.setStatePosition(stateIDs.get(i),
						  new Point2D.Float((float)newX, (float)pos.getY()));
			}
		}

		if (minY != margin) {
			double diff = minY - margin;
			for (int i = 0; i < stateIDs.size(); i++) {
				pos = dmi.getStatePosition(stateIDs.get(i));
				double newY = pos.getY() - diff;
				dmi.setStatePosition(stateIDs.get(i),
						  new Point2D.Float((float)pos.getX(), (float)newY));
			}
		}
		System.out.println("CenterGraph finished");
	} // end run()
}
