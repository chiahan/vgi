package vgi;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.util.mxSwingConstants;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;
import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.*;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * state_properties.java
 *
 * Created on 2012/1/6, 下午 03:53:38
 */
/**
 *
 * @author bl606
 */
public class StatePropertiesPanel extends javax.swing.JPanel {

    /** Creates new form state_properties */
    public StatePropertiesPanel(mxCell cell, State state, DisplayUtil display,JgraphXInternalFrame jif) {
        initComponents();
        
        this.cell = cell;
        this.state = state;
        this.graph = display.getGraph();
        this.automata = display.getAutomata();
        this.display = display;
        
        
        style = cell.getStyle();

		this.initialEdge = (mxCell) this.display.getInitialEdge(cell);
		this.finalEdge = (mxCell) this.display.getFinalEdge(cell);

		if ((this.automata.getWeight().semiring == TAFKitInterface.AutomataType.Semiring.B_BOOLEAN)
				|| (this.automata.getWeight().semiring == TAFKitInterface.AutomataType.Semiring.F2_TWO_ELEMENT_FIELD)) {
			this.initialWeightTextField.setVisible(false);
			this.finalWeightTextField.setVisible(false);
		}

        showName();
        showTransition();
        showInitialWeight();
        showFinalWeight();
        showGeometricDataAndDrawingData();
        

        
        
        jInternalFrame=jif;
        
    }
    
    
    private void showName() {
        this.nameTextField.setText(cell.getValue().toString());
    }
    
    private void showTransition() {
        // from automata
        ArrayList<Transition> transitions = (ArrayList<Transition>) state.getTransitions();
        int size = transitions.size();
        for (int i=0; i<size; i++) {
            transitionComboBox.addItem(transitions.get(i));
        }
    }

    private void showInitialWeight() {
        if (state.getInitialWeight() != null) {
            initialCheckBox.setSelected(true);
            initialWeightTextField.setText(state.getInitialWeight().toString());
        }
    }
    
    private void showFinalWeight() {
        if (state.getFinalWeight() != null) {
            finalCheckBox.setSelected(true);
            finalWeightTextField.setText(state.getFinalWeight().toString());
        }
    }
    private void showGeometricDataAndDrawingData(){
        Map<String, Object> styles = graph.getCellStyle(cell);
        String color = (String) styles.get("strokeColor");
        strokeColor = Color.decode(color);
        strokeColorButton.setBackground(strokeColor);

        fillColor = Color.decode((String) styles.get("fillColor"));
        colorButton.setBackground(fillColor);

        strokeWidth = (String) styles.get("strokeWidth");
        //System.out.println(strokeWidth);
        if (strokeWidth != null) {
            float width = Float.parseFloat(strokeWidth);
            int ind = (int) width;
            strokeWidthBox.setSelectedIndex(ind - 1);
        }
        
        
        shape=(String)styles.get("shape");
        styleComboBox.setSelectedItem(shape.toUpperCase());
        
        mxGeometry geo=cell.getGeometry();
        width=geo.getWidth();
        height=geo.getHeight();
        widthTextField.setText(String.valueOf(width));
        heightTextField.setText(String.valueOf(height));
        
        posX=geo.getCenterX();
        posY=geo.getCenterY();
        posXTextField.setText(String.valueOf(posX));
        posYTextField.setText(String.valueOf(posY));
        
    }
    
    public InitialFinalWeight getDefaultWeightValue() {
		InitialFinalWeight initialFinalWeight = new InitialFinalWeight();
		switch (automata.getWeight().semiring) {
			case Z_INTEGER:
			case ZMIN_MIN_TROPICAL:
			case ZMAX_MAX_TROPICAL:
				initialFinalWeight.setValue(new Integer(1));
				break;
			case Q_RATIONAL:
			case R_REAL:
				initialFinalWeight.setValue(new Double(1));
				break;
			case B_BOOLEAN:
			case F2_TWO_ELEMENT_FIELD:
				initialFinalWeight.setValue(true);
				break;
			default:
				return null;
		}  // End switch (automata.getWeight().semiring)
		return initialFinalWeight;
	}  // End public InitialFinalWeight getDefaultWeightValue()

    private void setFinalState(boolean isSet) {
        if (isSet) {
            if (this.finalEdge == null) {
                InitialFinalWeight initialFinalWeight = getDefaultWeightValue();
                state.setFinalWeight(initialFinalWeight);
                setInitialFinal(false, initialFinalWeight);
                this.finalEdge = (mxCell) this.display.getFinalEdge(this.cell);
            }
        }else {
            Object[] edges = graph.getEdges(cell);

            for (int i=0; i<edges.length; i++) {
                if (((mxCell)edges[i]).getTarget() == null) {
                    Object[] cell = {edges[i]};
                    graph.removeCells(cell);
                    break;
                }
            }

            this.finalEdge = null;
            finalWeightTextField.setText("");
            state.setFinalWeight(null);
        }
    }
    
    private void setInitialState(boolean isSet) {
        if (isSet) {
            Object edge = display.getInitialEdge(cell);
            if (this.initialEdge == null) {
                InitialFinalWeight initialFinalWeight = getDefaultWeightValue();
                state.setInitialWeight(initialFinalWeight);
                setInitialFinal(true, initialFinalWeight);
                this.initialEdge = (mxCell) this.display.getInitialEdge(this.cell);
            }
        }else {
            Object[] edges = graph.getEdges(cell);

            for (int i=0; i<edges.length; i++) {
                if (((mxCell)edges[i]).getSource() == null) {
                    Object[] cell = {edges[i]};
                    graph.removeCells(cell);
                    break;
                }
            }

            this.initialEdge = null;
            initialWeightTextField.setText("");
            state.setInitialWeight(null);
        }
    }
    
    private void setInitialFinal(boolean isInitial, Object expression) {
        if (isInitial) {
            initialWeightTextField.setText(expression.toString());
            // JgraphXInternalFram . setupInitialFinal(Object parent, Object weight, Object vertex, boolean vertexIsSource)
        } else {
            finalWeightTextField.setText(expression.toString());
            // JgraphXInternalFram . setupInitialFinal(Object parent, Object weight, Object vertex, boolean vertexIsSource)
        }
        display.showInitialFinal(graph.getDefaultParent(), expression, cell, !isInitial);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        nameLabel = new javax.swing.JLabel();
        transitionLabel = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        initialWeightTextField = new javax.swing.JTextField();
        finalWeightTextField = new javax.swing.JTextField();
        styleComboBox = new javax.swing.JComboBox();
        transitionComboBox = new javax.swing.JComboBox();
        initialCheckBox = new javax.swing.JCheckBox();
        finalCheckBox = new javax.swing.JCheckBox();
        styleLabel1 = new javax.swing.JLabel();
        colorLabel = new javax.swing.JLabel();
        colorButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        strokeWidthBox = new javax.swing.JComboBox();
        strokeColorButton = new javax.swing.JButton();
        transformPanel = new javax.swing.JPanel();
        xLabel = new javax.swing.JLabel();
        posXTextField = new javax.swing.JTextField();
        wLabel = new javax.swing.JLabel();
        widthTextField = new javax.swing.JTextField();
        yLabel = new javax.swing.JLabel();
        posYTextField = new javax.swing.JTextField();
        hLabel = new javax.swing.JLabel();
        heightTextField = new javax.swing.JTextField();

        setLayout(new java.awt.GridBagLayout());

        nameLabel.setText("Name :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(nameLabel, gridBagConstraints);

        transitionLabel.setText("Transition :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(transitionLabel, gridBagConstraints);

        nameTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                nameTextFieldKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        add(nameTextField, gridBagConstraints);

        initialWeightTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                initialWeightTextFieldActionPerformed(evt);
            }
        });
        initialWeightTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                initialWeightTextFieldKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        add(initialWeightTextField, gridBagConstraints);

        finalWeightTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                finalWeightTextFieldActionPerformed(evt);
            }
        });
        finalWeightTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                finalWeightTextFieldKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        add(finalWeightTextField, gridBagConstraints);

        styleComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ELLIPSE", "RECTANGLE", "RHOMBUS", "HEXAGON" }));
        styleComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                styleComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        add(styleComboBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.1;
        add(transitionComboBox, gridBagConstraints);

        initialCheckBox.setText("Initial State");
        initialCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                initialCheckBoxItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(initialCheckBox, gridBagConstraints);

        finalCheckBox.setText("Final State");
        finalCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                finalCheckBoxItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(finalCheckBox, gridBagConstraints);

        styleLabel1.setText("Style :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(styleLabel1, gridBagConstraints);

        colorLabel.setText("FillColor :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(colorLabel, gridBagConstraints);

        colorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(colorButton, gridBagConstraints);

        jLabel1.setText("Stroke:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(jLabel1, gridBagConstraints);

        strokeWidthBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }));
        strokeWidthBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                strokeWidthBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        add(strokeWidthBox, gridBagConstraints);

        strokeColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                strokeColorButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(strokeColorButton, gridBagConstraints);
        strokeColorButton.getAccessibleContext().setAccessibleName("strokeColorButton");

        transformPanel.setLayout(new java.awt.GridLayout(2, 2));

        xLabel.setText("x :");
        transformPanel.add(xLabel);

        posXTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                posXTextFieldKeyPressed(evt);
            }
        });
        transformPanel.add(posXTextField);

        wLabel.setText("w :");
        transformPanel.add(wLabel);

        widthTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                widthTextFieldActionPerformed(evt);
            }
        });
        widthTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                widthTextFieldKeyPressed(evt);
            }
        });
        transformPanel.add(widthTextField);

        yLabel.setText("y :");
        transformPanel.add(yLabel);

        posYTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                posYTextFieldKeyPressed(evt);
            }
        });
        transformPanel.add(posYTextField);

        hLabel.setText("h :");
        transformPanel.add(hLabel);

        heightTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                heightTextFieldActionPerformed(evt);
            }
        });
        heightTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                heightTextFieldKeyPressed(evt);
            }
        });
        transformPanel.add(heightTextField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(transformPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void nameTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nameTextFieldKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            String name = ((JTextField)evt.getSource()).getText();
            if (name.compareTo("") == 0)
                name = null;
            cell.setValue(name);
            state.setName(name);
        }
        graph.refresh();
    }//GEN-LAST:event_nameTextFieldKeyPressed

	private void initialWeightTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_initialWeightTextFieldActionPerformed
		if (this.initialEdge == null) {
			return;
		}
		InitialFinalWeight initialFinalWeight = new InitialFinalWeight();
		switch (automata.getWeight().semiring) {
			case Z_INTEGER:
			case ZMIN_MIN_TROPICAL:
			case ZMAX_MAX_TROPICAL:
				initialFinalWeight.setValue(Integer.valueOf(this.initialWeightTextField.getText()));
				break;
			case Q_RATIONAL:
			case R_REAL:
				initialFinalWeight.setValue(Double.valueOf(this.initialWeightTextField.getText()));
				break;
			case B_BOOLEAN:
			case F2_TWO_ELEMENT_FIELD:
				initialFinalWeight.setValue(true);
				break;
			default:
				return;
		}  // End switch (automata.getWeight().semiring)
		this.initialEdge.setValue(initialFinalWeight);
		this.state.setInitialWeight(initialFinalWeight);
                
               
	}//GEN-LAST:event_initialWeightTextFieldActionPerformed

	private void finalWeightTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_finalWeightTextFieldActionPerformed
		if (this.finalEdge == null) {
			return;
		}
		InitialFinalWeight initialFinalWeight = new InitialFinalWeight();
		switch (automata.getWeight().semiring) {
			case Z_INTEGER:
			case ZMIN_MIN_TROPICAL:
			case ZMAX_MAX_TROPICAL:
				initialFinalWeight.setValue(Integer.valueOf(this.finalWeightTextField.getText()));
				break;
			case Q_RATIONAL:
			case R_REAL:
				initialFinalWeight.setValue(Double.valueOf(this.finalWeightTextField.getText()));
				break;
			case B_BOOLEAN:
			case F2_TWO_ELEMENT_FIELD:
				initialFinalWeight.setValue(new Boolean(true));
				break;
			default:
				return;
		}  // End switch (automata.getWeight().semiring)
		this.finalEdge.setValue(initialFinalWeight);
		this.state.setFinalWeight(initialFinalWeight);
                
               
	}//GEN-LAST:event_finalWeightTextFieldActionPerformed

	private void initialWeightTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_initialWeightTextFieldKeyPressed
		if ((evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE)
				&& (this.initialEdge != null)){
			this.initialWeightTextField.setText(this.initialEdge.getValue().toString());
		}
                
                jInternalFrame.setModified(true);
	}//GEN-LAST:event_initialWeightTextFieldKeyPressed

	private void finalWeightTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_finalWeightTextFieldKeyPressed
		if ((evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE)
				&& (this.finalEdge != null)){
			this.finalWeightTextField.setText(this.finalEdge.getValue().toString());
		}
                
                
	}//GEN-LAST:event_finalWeightTextFieldKeyPressed

    private void styleComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_styleComboBoxActionPerformed
        JComboBox cb = (JComboBox)evt.getSource();
        //Style = "shape=" + ((String)cb.getSelectedItem()).toLowerCase();
 //       Object[] objects = {cell};
//        System.out.print(style);
       // graph.setCellStyles("shape", ((String) cb.getSelectedItem()).toLowerCase(), objects);
        //jInternalFrame.setModified(true);
        setShape(((String) cb.getSelectedItem()).toLowerCase());
    }//GEN-LAST:event_styleComboBoxActionPerformed

    private void initialCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_initialCheckBoxItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            setInitialState(true);
        } else if (evt.getStateChange() == ItemEvent.DESELECTED) {
            setInitialState(false);
        }
        //jInternalFrame.setModified(true);
    }//GEN-LAST:event_initialCheckBoxItemStateChanged

    private void finalCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_finalCheckBoxItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            setFinalState(true);
            ((JCheckBox)evt.getSource()).setSelected(true);
        } else if (evt.getStateChange() == ItemEvent.DESELECTED) {
            setFinalState(false);
            ((JCheckBox)evt.getSource()).setSelected(false);
        }
        //jInternalFrame.setModified(true);
    }//GEN-LAST:event_finalCheckBoxItemStateChanged

    private void colorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorButtonActionPerformed
        
        fillColor=JColorChooser.showDialog( this,
                     "Fill color", fillColor );
        setFillColor(graph,fillColor);
        colorButton.setBackground(fillColor);
        
        
    }//GEN-LAST:event_colorButtonActionPerformed

    private void strokeColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_strokeColorButtonActionPerformed
        strokeColor=JColorChooser.showDialog( this,
                     "Fill color", strokeColor );
        setStrokeColor(graph,strokeColor);
        strokeColorButton.setBackground(strokeColor);
        
    }//GEN-LAST:event_strokeColorButtonActionPerformed

    private void strokeWidthBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_strokeWidthBoxActionPerformed
        JComboBox cb = (JComboBox)evt.getSource();
        String width=(String)cb.getSelectedItem();
        float wid=Float.parseFloat(width);
        setStrokeWidth(graph,wid);
        
    }//GEN-LAST:event_strokeWidthBoxActionPerformed

    private void widthTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_widthTextFieldActionPerformed
       
    }//GEN-LAST:event_widthTextFieldActionPerformed

    private void heightTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_heightTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_heightTextFieldActionPerformed

    private void widthTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_widthTextFieldKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            String str = ((JTextField)evt.getSource()).getText();
            if (str.compareTo("") == 0)
                str = null;
            setWidth(str);
        }
        graph.refresh();
    }//GEN-LAST:event_widthTextFieldKeyPressed

    private void heightTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_heightTextFieldKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            String str = ((JTextField)evt.getSource()).getText();
            if (str.compareTo("") == 0)
                str = null;
            setHeight(str);
        }
        graph.refresh();
    }//GEN-LAST:event_heightTextFieldKeyPressed

    private void posXTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_posXTextFieldKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            String str = ((JTextField)evt.getSource()).getText();
            if (str.compareTo("") == 0)
                str = null;
            setPosX(str);
        }
        graph.refresh();
    }//GEN-LAST:event_posXTextFieldKeyPressed

    private void posYTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_posYTextFieldKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            String str = ((JTextField)evt.getSource()).getText();
            if (str.compareTo("") == 0)
                str = null;
            setPosY(str);
        }
        graph.refresh();
    }//GEN-LAST:event_posYTextFieldKeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton colorButton;
    private javax.swing.JLabel colorLabel;
    private javax.swing.JCheckBox finalCheckBox;
    private javax.swing.JTextField finalWeightTextField;
    private javax.swing.JLabel hLabel;
    private javax.swing.JTextField heightTextField;
    private javax.swing.JCheckBox initialCheckBox;
    private javax.swing.JTextField initialWeightTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JTextField posXTextField;
    private javax.swing.JTextField posYTextField;
    private javax.swing.JButton strokeColorButton;
    private javax.swing.JComboBox strokeWidthBox;
    private javax.swing.JComboBox styleComboBox;
    private javax.swing.JLabel styleLabel1;
    private javax.swing.JPanel transformPanel;
    private javax.swing.JComboBox transitionComboBox;
    private javax.swing.JLabel transitionLabel;
    private javax.swing.JLabel wLabel;
    private javax.swing.JTextField widthTextField;
    private javax.swing.JLabel xLabel;
    private javax.swing.JLabel yLabel;
    // End of variables declaration//GEN-END:variables

    private String style;
    private mxCell cell;
	protected mxCell initialEdge;
	protected mxCell finalEdge;
    private mxGraph graph;
    private State state;
    private Automata automata;
    DisplayUtil display;
    private Color fillColor = Color.white;
    private Color strokeColor = Color.white;
    private String strokeWidth = null;
    private String shape=null;
    private double width,height;
    private double posX,posY;
    
    JgraphXInternalFrame jInternalFrame;
    
    
    public void setFillColor(mxGraph graph,Color color){
        Object[] objects = new Object[1];
        objects[0]=cell;
        if(color!=null){
            graph.setCellStyles("fillColor", mxUtils.hexString(color),objects);
            jInternalFrame.setModified(true);
        }
        
    }
    public void setStrokeColor(mxGraph graph,Color color){
        Object[] objects = new Object[1];
        objects[0]=cell;
        if(color!=null){
            graph.setCellStyles("strokeColor", mxUtils.hexString(color),objects);
            jInternalFrame.setModified(true);
        }
    }
    public void setStrokeWidth(mxGraph graph,float width){
        Object[] objects = new Object[1];
        objects[0]=cell;
        String wid=String.valueOf(width);
        graph.setCellStyles("strokeWidth",wid,objects);
        if(jInternalFrame!=null) jInternalFrame.setModified(true);
    }
    
    //fill cell with gradient color
    //fromColor-->toColor
    //direction: 1-north 2-south 3-east 4-west
    public void setGradientColor(mxGraph graph,Color fromColor, Color toColor,int direction)
    {
        Object[] objects = new Object[1];
        objects[0]=cell;
        graph.setCellStyles("fillColor", mxUtils.hexString(fromColor),objects);
        graph.setCellStyles("gradientColor", mxUtils.hexString(toColor),objects);
        switch(direction){
            case 1:
                graph.setCellStyles("gradientDirection",mxConstants.DIRECTION_NORTH,objects);
                break;
            case 2:
                graph.setCellStyles("gradientDirection",mxConstants.DIRECTION_SOUTH,objects);
                break;
            case 3:
                graph.setCellStyles("gradientDirection",mxConstants.DIRECTION_EAST,objects);
                break;
            case 4:
                graph.setCellStyles("gradientDirection",mxConstants.DIRECTION_WEST,objects);
                break;
                
        }
        
    }
    
    public void setShadow(mxGraph graph,boolean hasShadow)
    {
        Object[] objects = new Object[1];
        objects[0]=cell;
        if(hasShadow)
            graph.setCellStyles("shadow", "true",objects);
        else
            graph.setCellStyles("shadow", "false",objects);
    }
    
    //Global
    public void setShadowColor(Color color)
    {
        mxSwingConstants.SHADOW_COLOR = color;
    }
    public void setShadowOffset(int offsetx,int offsety)
    {
      
        mxConstants.SHADOW_OFFSETX=offsetx;
        mxConstants.SHADOW_OFFSETY=offsety;
        
    }
    public void setShape(String string){
        graph.setCellStyles("shape",string);
        graph.setCellStyles("perimeter",string+"Perimeter");
        
        if(jInternalFrame!=null) jInternalFrame.setModified(true);
    }
    public void setWidth(String w){
        mxGeometry geo=cell.getGeometry();
        Double w_=Double.valueOf(w);
        if(w_!=null){
            geo.setWidth(w_);
            cell.setGeometry(geo);
            jInternalFrame.setModified(true);
            width=w_;
        }
    }
    public void setHeight(String h){
        mxGeometry geo=cell.getGeometry();
        Double h_=Double.valueOf(h);
        if(h_!=null){
            geo.setHeight(h_);
            cell.setGeometry(geo);
            jInternalFrame.setModified(true);
            height=h_;
        }
    }
    public void setPosX(String x){
        mxGeometry geo=cell.getGeometry();
        Double x_=Double.valueOf(x);
        if(x_!=null){
            geo.setX(x_-width/2);
            cell.setGeometry(geo);
            jInternalFrame.setModified(true);
            posX=x_;
        }
    }
    
    public void setPosY(String y){
        mxGeometry geo=cell.getGeometry();
        Double y_=Double.valueOf(y);
        if(y_!=null){
            geo.setY(y_-height/2);
            cell.setGeometry(geo);
            jInternalFrame.setModified(true);
            posY=y_;
        }
    }
    
}
