/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view.demo;

import java.awt.geom.Point2D;
import org.jgraph.JGraph;
import view.jgraph.JGraphController;

/**
 *
 * @author Cody
 */
public class Demo {

    public Demo() {
        JGraph graph = new JGraph();
        JGraphController JGController = new JGraphController(graph,null);
        Point2D.Float point = new Point2D.Float(100, 200);
        JGController.insertState(0, "123", point);
        point = new Point2D.Float(100, 300);
        JGController.insertState(1, "123", point);
        point = new Point2D.Float(100, 400);
        JGController.insertState(2, "123", point);
        JGController.changeStateStyle(0, false, true);
        JGController.changeStateStyle(1, true, false);
        JGController.changeStateStyle(2, false, true);
        JGController.createTransition(0,1,0,"hello", null);
        JGController.createTransition(1,2,0,"hello", null);
        JGController.show();
    }

    public static void main(String[] args) {
        Demo instance = new Demo();
    }
}
