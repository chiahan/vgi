/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package storage;

import controller.Controller;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import model.Automata2;
import model.Automata2.NumericalSemiring;
import org.jgraph.JGraph;
/**
* the structure of tab panel for graph
* @author Junli Lu
*/
public class TabbedPaneForGraph extends JTabbedPane {
        /**
        * to initialize the structure of tab panel for graph
        * @author Junli Lu
        */
        TabbedPaneForGraph(){
            this.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                     if(Storage.getInstance().getTabbedForProperty().getComponentCount()!=0)
                         Storage.getInstance().getAutomatonPanel().ShowAutomaton( Storage.getInstance().getSelectedGraphIndex());
                }
            });
            this.addFocusListener(new FocusListener() {
                public void focusGained(FocusEvent e) {
                     if(Storage.getInstance().getTabbedForProperty().getComponentCount()!=0)
                         Storage.getInstance().getAutomatonPanel().ShowAutomaton( Storage.getInstance().getSelectedGraphIndex());
                }
                public void focusLost(FocusEvent e) {
                }
            });
            final JPopupMenu menu = new JPopupMenu();
            JMenuItem item = new JMenuItem("Save graph");
            item.addActionListener(new java.awt.event.ActionListener() {
                 public void actionPerformed(java.awt.event.ActionEvent evt) {
                    Storage.getInstance().fileSave(Storage.getInstance().getSelectedGraphIndex());
                 }
            });
            menu.add(item);
            item = new JMenuItem("Close graph");
            item.addActionListener(new java.awt.event.ActionListener() {
                 public void actionPerformed(java.awt.event.ActionEvent evt) {
                    Storage.getInstance().removeFile(Storage.getInstance().getSelectedGraphIndex());
                 }
            });
            menu.add(item);
            item = new JMenuItem("Show on new window");
            item.addActionListener(new java.awt.event.ActionListener() {
                 public void actionPerformed(java.awt.event.ActionEvent evt) {
                         JGraph graph = new JGraph();
                         graph.setModel( Storage.getInstance().getController().getGraph().getModel());
                         graph.setGraphLayoutCache( Storage.getInstance().getController().getGraph().getGraphLayoutCache());
                         Dimension dimension = Storage.getInstance().getController().getGraph().getSize();
                         JFrame frame = new JFrame();
                         frame.setSize((int)dimension.getWidth(),(int)dimension.getHeight()/2);
                         frame.setLocationRelativeTo( Storage.getInstance().getFrame());
                         frame.setLocation( Storage.getInstance().getFrame().getSize().width/2, 100);
                         frame.add(new JScrollPane(graph));
                         TabComponent tabComponent = (TabComponent)Storage.getInstance().getTabbedForGraph().getTabComponentAt(Storage.getInstance().getSelectedGraphIndex());
                         String fileName = tabComponent.getFilename();
                         frame.setTitle("copy of "+fileName);
                         frame.setVisible(true);
                 }
            });
            menu.add(item);
            this.addMouseListener(new  MouseAdapter  (){
                public void mouseClicked(MouseEvent e)  {
                      //Debug.echo("mouseClicked: "+ e.getX()+e.getY());
                      if(e.getButton() == 3){
                            menu.show(e.getComponent(),e.getX(), e.getY());
                      }
                }
             });
        }

		// BEGIN
		// Automatically update Algorithms menu items depending on current automata type.
		@Override
		public void setSelectedIndex(int index) {
			super.setSelectedIndex(index);

			Storage storage = Storage.getInstance();
			Controller controller = storage.getController();
			if (controller == null) {
				return;
			}

			Automata2.ValueType valueType = controller.getDataModel().getValueType();
			if (valueType.getSemiringType() != Automata2.ValueType.SemiringTypeE.NUMERICAL) {
				return;
			}
			NumericalSemiring numSemiring = (NumericalSemiring) valueType.getSemiring();

			switch (valueType.getMonoidType()) {
				case FREE:
					if (numSemiring.getSetE() == NumericalSemiring.SetE.B) {
						storage.getMenubar().UpdateAlgorithmsMenuItems(0);
					} else if (numSemiring.getSetE() == NumericalSemiring.SetE.Z) {
						storage.getMenubar().UpdateAlgorithmsMenuItems(1);
					}
					break;
				case PRODUCT:
					if (numSemiring.getSetE() == NumericalSemiring.SetE.B) {
						storage.getMenubar().UpdateAlgorithmsMenuItems(2);
					} else if (numSemiring.getSetE() == NumericalSemiring.SetE.Z) {
						storage.getMenubar().UpdateAlgorithmsMenuItems(3);
					}
					break;
				case UNIT:
				default:
					break;
			}  // End switch (valueType.getMonoidType())
		}  // End public void setSelectedIndex(int index)
		// Automatically update Algorithms menu items depending on current automata type.
		// END
}
