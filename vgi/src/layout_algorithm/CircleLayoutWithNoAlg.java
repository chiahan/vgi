/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package layout_algorithm;

import java.awt.geom.Point2D;
import java.util.Vector;
import model.DataModelInterface;

/**
 * run circle layout without algorithm
 * @author Junli Lu
 */
public class CircleLayoutWithNoAlg {
        /**
         * data model interface
         */
	private DataModelInterface dmi;
        /**
         * the list of id of states
         */
	Vector<Integer> statesId;
        /**
         * initialization
         * @param dmi data model interface
         * @author Junli Lu
         */
	public CircleLayoutWithNoAlg(DataModelInterface dmi) {
		this.dmi = dmi;
		statesId = dmi.getAllStates();
	}
        /**
         * run the circular layout at random
         * @author Junli Lu
         */
	public void run() {
		double radius = Math.max(10 * statesId.size(), 100);
		run(radius);
	}
        /**
         * run the circular layout with a assigned radius at random
         * @param  radius the radius of the layout circle
         * @author Junli Lu
         */
	public void run(double radius) {
		int numStates = statesId.size();
		double step = Math.PI * 2 / numStates;
		double X = radius + 50;
		double Y = radius + 50;

		for (int i = 0; i<numStates; i++) {
			double angle = step * i;
			double x = X + radius * Math.sin(angle);
			double y = Y - radius * Math.cos(angle);

			dmi.setStatePosition(statesId.get(i),
					  new Point2D.Float( (float)x, (float)y));
		}
	}
}
