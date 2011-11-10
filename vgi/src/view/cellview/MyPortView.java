/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package view.cellview;

import org.jgraph.graph.EdgeView;
import org.jgraph.graph.PortView;

/**
 *
 * @author cody_liu
 */
public class MyPortView extends PortView {

    public MyPortView(Object cell) {
        super(cell);
    }
   
    @Override
    protected boolean shouldInvokePortMagic(EdgeView arg0) {
        return false;
//        return super.shouldInvokePortMagic(arg0);
    }
    
}
