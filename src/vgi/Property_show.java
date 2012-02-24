package vgi;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

    
import com.mxgraph.model.mxCell;

import com.mxgraph.view.mxGraph;
/**
 *
 * @author bl606
 */
public class Property_show {
    
    public javax.swing.JPanel show(mxGraph graph ,mxCell cell){
    
    if(cell.isVertex()){
        state_properties state = new state_properties(graph, cell);
        return state;
    }    
    else if(cell.isEdge()){
        
        edge_properties edge = new edge_properties(graph,cell);
        return edge;
    }
    else if( (!(cell.isEdge())&&(!(cell.isVertex())) )){
        
        Automata_properties auto = new Automata_properties();
        return auto;
    }
    else 
        return null;
    
    
    }
    
    
    
    /*
    public static void main(String[] args) {
         
         
    JFrame f = new JFrame("Text Form Example");
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    state_properties p = new state_properties();
    f.getContentPane().add(p,BorderLayout.SOUTH);
    f.pack();
    f.setVisible(true);
    }
     * 
     */
    
}
