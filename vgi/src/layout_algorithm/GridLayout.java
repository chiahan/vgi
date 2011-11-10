/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package layout_algorithm;

import java.awt.geom.Point2D;
import java.util.Vector;
import model.DataModelInterface;

/**
 * change graph layout to a rectangular grid fashion
 * @author polar
 */
public class GridLayout {

    /** contains tools required for graph manipulation */
	private DataModelInterface dmi;

    /** list of graph node ids */
	Vector<Integer> stateIDs;
    
    /**
     * constructor
     * @param dmi contains all necessary tools for graph manipulation
     * @author polar
     */
	public GridLayout(DataModelInterface dmi) {
		this.dmi = dmi;
		stateIDs = dmi.getAllStates();
	}

    /**
     * executes graph algorithm to place nodes in a rectangular grid layout fashion
     * @author polar
     */
	public void run() {
		double numRow = Math.ceil(0.7 * Math.sqrt(dmi.getNumberOfStates()));
		int row = (int)numRow;
		run(row);
	}

    /**
     * executes graph algorithm to place nodes in a rectangular grid layout fashion with
     * @param numRow specifies the number of rows for grid layout
     * @author polar
     */
	public void run(int numRow) {
		stateIDs = dmi.getAllStates();
		int numStates = stateIDs.size();
		float margin = 50;
		float step = 100;
		int r = 0;
		int c = 0;
		//double numCol = Math.ceil(numStates/numRow);

		for (int i = 0; i<numStates; i++) {
			if (r >= numRow) {
				r = 0;
				c++;
			}
			double x = c * step + margin;
			double y = r * step + margin;
			dmi.setStatePosition(stateIDs.get(i),
					  new Point2D.Float( (float)x, (float)y));
			r++;
		}
	}
}
