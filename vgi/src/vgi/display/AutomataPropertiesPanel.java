package vgi.display;

import vgi.automata.Transition;
import vgi.automata.State;
import vgi.automata.Automata;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.util.Hashtable;
import java.util.Iterator;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import vgi.automata.*;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Automata_properties.java
 *
 * Created on 2012/1/6, 下午 04:01:27
 */
/**
 *
 * @author bl606
 */
public class AutomataPropertiesPanel extends javax.swing.JPanel {

    /** Creates new form Automata_properties */
   public AutomataPropertiesPanel(DisplayUtil display,JgraphXInternalFrame jif) {
        initComponents();
        this.display = display;
        this.automata = display.getAutomata();
        this.graph = display.getGraph();
        
        if (automata != null) {
            showTransitions();
            showStates();
            showFinals();
            showInitials();
            showDrawingData();
        }
        
        jInternalFrame=jif;
        
    }
    
    private void showStates() {
        stateTextField.setText(
                Integer.toString(automata.getAllStates().size()));

        Iterator<State> iterator = this.automata.getAllStates().iterator();
        while (iterator.hasNext()) {
            State state = iterator.next();
            stateComboBox.addItem((Object)state);
        }
    }
    
    private void showTransitions() {
        transitionTextField.setText(
                Integer.toString(automata.getAllTransitions().size()));
        
        Iterator<Transition> iterator = automata.getAllTransitions().iterator();
        while(iterator.hasNext()) {
            Transition transition = iterator.next();
            transitionComboBox.addItem(transition);
        }
    }
    
    private void showDrawingData(){
        
        
        StateDrawingData dd=automata.getAutomataStateDrawingData();
        
        strokeColor = Color.decode(dd.getStrokeColor());
        strokeColorButton.setBackground(strokeColor);

        fillColor = Color.decode(dd.getFillColor());
        colorButton.setBackground(fillColor);

        strokeWidth = dd.getStrokeWidth();
        strokeWidthBox.setSelectedIndex((int)strokeWidth-1);
        
        
        
        
        StateGeometricData gd=automata.getAutomataStateGeometricData();
        stateShape=gd.getShape();
        this.stateShapeComboBox.setSelectedItem(stateShape.toUpperCase());
        //stateSize=gd.getSize();
        this.stateWidthTextField.setText(String.valueOf(gd.getWidth()));
        this.stateHeightTextField.setText(String.valueOf(gd.getHeight()));
        
        
        TransitionDrawingData tdd=automata.getAutomataTransitionDrawingData();
        transitionStrokeColor=Color.decode(tdd.getStrokeColor());
        this.transitionStrokeColorButton.setBackground(transitionStrokeColor);
        
        transitionStrokeWidth=tdd.getStrokeWidth();
        this.transitionStokeWidthComboBox.setSelectedIndex((int)transitionStrokeWidth-1);
        
        transitionStartArrow=tdd.getStartArrow();
        this.startArrowComboBox.setSelectedItem(transitionStartArrow);
        transitionEndArrow=tdd.getEndArrow();
        this.endArrowComboBox.setSelectedItem(transitionEndArrow);
        
    }
    private void showInitials() {
        this.initialTextField.setText(
                Integer.toString(automata.getInitialStates().size()));
        
        Iterator<State> iterator = this.automata.getInitialStates().iterator();
        while (iterator.hasNext()) {
            State state = iterator.next();
            initialComboBox.addItem((Object)state);
        }
    }
    
    private void showFinals() {
        this.finalTextField.setText(
                Integer.toString(automata.getFinalStates().size()));
        
        Iterator<State> iterator = this.automata.getFinalStates().iterator();
        while (iterator.hasNext()) {
            State state = iterator.next();
            finalComboBox.addItem((Object)state);
        }
    }
    private void setSelectionCell(Object[] cells, Object object, Boolean state) {
        for (int i=0; i<cells.length; i++) {
            
            mxCell source=(mxCell)((mxCell)cells[i]).getSource();
            mxCell target=(mxCell)((mxCell)cells[i]).getTarget();
            if(!state && (source==null || target==null)) continue;
            
            String cellString = (state) ?
                                ((mxCell)cells[i]).getValue().toString():
                                ((mxCell)cells[i]).getValue().toString() + " : '" + 
                                    ((mxCell)cells[i]).getSource().getValue() + "' to '" + 
                                    ((mxCell)cells[i]).getTarget().getValue() +"'";
            String itemString = (state) ? 
                                ((State)object).getName() :
                                ((Transition)object).toString();
            if (cellString.compareTo(itemString) == 0) {
                graph.setSelectionCell(cells[i]);
                selectedCell = (mxCell) cells[i];
                break;
            }
        }
    }
    
    private DisplayUtil display;
    private Automata automata;
    private mxGraph graph;
    private Object selectedItem;
    private mxCell selectedCell;
    private Color fillColor = Color.white;
    private Color strokeColor = Color.white;
    private double strokeWidth;
    //private Point2D stateSize;
    private double stateWidth;
    private double stateHeight;
    private String stateShape;
    private double transitionStrokeWidth;
    private Color transitionStrokeColor;
    private String transitionStartArrow;
    private String transitionEndArrow;
    
    
    JgraphXInternalFrame jInternalFrame;
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        stateLabel = new javax.swing.JLabel();
        stateTextField = new javax.swing.JTextField();
        transitionLabel = new javax.swing.JLabel();
        transitionTextField = new javax.swing.JTextField();
        initialLabel = new javax.swing.JLabel();
        initialTextField = new javax.swing.JTextField();
        finalLabel = new javax.swing.JLabel();
        finalTextField = new javax.swing.JTextField();
        stateComboBox = new javax.swing.JComboBox();
        transitionComboBox = new javax.swing.JComboBox();
        initialComboBox = new javax.swing.JComboBox();
        finalComboBox = new javax.swing.JComboBox();
        selectButton = new javax.swing.JButton();
        stateStylePanel = new javax.swing.JPanel();
        colorButton = new javax.swing.JButton();
        colorLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        strokeColorButton = new javax.swing.JButton();
        strokeWidthBox = new javax.swing.JComboBox();
        stateShapeComboBox = new javax.swing.JComboBox();
        stateShapeLabel = new javax.swing.JLabel();
        stateSizePanel = new javax.swing.JPanel();
        stateWidthLabel = new javax.swing.JLabel();
        stateWidthTextField = new javax.swing.JTextField();
        stateHeightLabel = new javax.swing.JLabel();
        stateHeightTextField = new javax.swing.JTextField();
        transitionStylePanel = new javax.swing.JPanel();
        stateStrokeColorLabel1 = new javax.swing.JLabel();
        transitionStokeWidthComboBox = new javax.swing.JComboBox();
        transitionStrokeColorButton = new javax.swing.JButton();
        startArrowLabel = new javax.swing.JLabel();
        startArrowComboBox = new javax.swing.JComboBox();
        endArrowComboBox = new javax.swing.JComboBox();
        endArrowLabel = new javax.swing.JLabel();
        projectionButton = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(300, 300));
        java.awt.GridBagLayout layout = new java.awt.GridBagLayout();
        layout.columnWidths = new int[] {80, 100, 120};
        setLayout(layout);

        stateLabel.setText("States :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(stateLabel, gridBagConstraints);

        stateTextField.setColumns(5);
        stateTextField.setEditable(false);
        stateTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        stateTextField.setText("0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(stateTextField, gridBagConstraints);

        transitionLabel.setText("Transitions :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(transitionLabel, gridBagConstraints);

        transitionTextField.setColumns(5);
        transitionTextField.setEditable(false);
        transitionTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        transitionTextField.setText("0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(transitionTextField, gridBagConstraints);

        initialLabel.setText("Initial states :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(initialLabel, gridBagConstraints);

        initialTextField.setColumns(5);
        initialTextField.setEditable(false);
        initialTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        initialTextField.setText("0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(initialTextField, gridBagConstraints);

        finalLabel.setText("Final states :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(finalLabel, gridBagConstraints);

        finalTextField.setColumns(5);
        finalTextField.setEditable(false);
        finalTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        finalTextField.setText("0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(finalTextField, gridBagConstraints);

        stateComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stateComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.1;
        add(stateComboBox, gridBagConstraints);

        transitionComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transitionComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.1;
        add(transitionComboBox, gridBagConstraints);

        initialComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                initialComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.1;
        add(initialComboBox, gridBagConstraints);

        finalComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                finalComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.1;
        add(finalComboBox, gridBagConstraints);

        selectButton.setText("Select");
        selectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(selectButton, gridBagConstraints);

        stateStylePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "State Style", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        java.awt.GridBagLayout jPanel1Layout = new java.awt.GridBagLayout();
        jPanel1Layout.columnWidths = new int[] {90, 90, 90};
        jPanel1Layout.rowHeights = new int[] {25, 25, 25, 25};
        stateStylePanel.setLayout(jPanel1Layout);

        colorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        stateStylePanel.add(colorButton, gridBagConstraints);

        colorLabel.setText("FillColor :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        stateStylePanel.add(colorLabel, gridBagConstraints);

        jLabel1.setText("Stroke:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        stateStylePanel.add(jLabel1, gridBagConstraints);

        strokeColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                strokeColorButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        stateStylePanel.add(strokeColorButton, gridBagConstraints);

        strokeWidthBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }));
        strokeWidthBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                strokeWidthBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        stateStylePanel.add(strokeWidthBox, gridBagConstraints);

        stateShapeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ELLIPSE", "RECTANGLE", "RHOMBUS", "HEXAGON" }));
        stateShapeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stateShapeComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        stateStylePanel.add(stateShapeComboBox, gridBagConstraints);

        stateShapeLabel.setText("Shape:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        stateStylePanel.add(stateShapeLabel, gridBagConstraints);

        java.awt.GridBagLayout stateSizePanelLayout = new java.awt.GridBagLayout();
        stateSizePanelLayout.rowHeights = new int[] {25};
        stateSizePanelLayout.columnWeights = new double[] {0.25, 0.25, 0.25, 0.25};
        stateSizePanel.setLayout(stateSizePanelLayout);

        stateWidthLabel.setText("w:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        stateSizePanel.add(stateWidthLabel, gridBagConstraints);

        stateWidthTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                stateWidthTextFieldKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        stateSizePanel.add(stateWidthTextField, gridBagConstraints);

        stateHeightLabel.setText("h:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        stateSizePanel.add(stateHeightLabel, gridBagConstraints);

        stateHeightTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                stateHeightTextFieldKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        stateSizePanel.add(stateHeightTextField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        stateStylePanel.add(stateSizePanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(stateStylePanel, gridBagConstraints);

        transitionStylePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Transition Style", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        transitionStylePanel.setMinimumSize(new java.awt.Dimension(350, 120));
        transitionStylePanel.setPreferredSize(new java.awt.Dimension(350, 120));
        java.awt.GridBagLayout transitionStylePanelLayout = new java.awt.GridBagLayout();
        transitionStylePanelLayout.columnWidths = new int[] {90, 90, 90};
        transitionStylePanelLayout.rowHeights = new int[] {25, 25, 25};
        transitionStylePanel.setLayout(transitionStylePanelLayout);

        stateStrokeColorLabel1.setText("Stroke:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        transitionStylePanel.add(stateStrokeColorLabel1, gridBagConstraints);

        transitionStokeWidthComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }));
        transitionStokeWidthComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transitionStokeWidthComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        transitionStylePanel.add(transitionStokeWidthComboBox, gridBagConstraints);

        transitionStrokeColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transitionStrokeColorButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        transitionStylePanel.add(transitionStrokeColorButton, gridBagConstraints);

        startArrowLabel.setText("Start Arrow");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        transitionStylePanel.add(startArrowLabel, gridBagConstraints);

        startArrowComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "classic", "block", "open", "oval", "diamond", "none" }));
        startArrowComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startArrowComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        transitionStylePanel.add(startArrowComboBox, gridBagConstraints);

        endArrowComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "classic", "block", "open", "oval", "diamond", "none" }));
        endArrowComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                endArrowComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        transitionStylePanel.add(endArrowComboBox, gridBagConstraints);

        endArrowLabel.setText("End Arrow");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        transitionStylePanel.add(endArrowLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(transitionStylePanel, gridBagConstraints);

        projectionButton.setText("Projection");
        projectionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projectionButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(projectionButton, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void stateComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stateComboBoxActionPerformed
        selectedItem = ((JComboBox)evt.getSource()).getSelectedItem();
        Object[] cells = graph.getChildCells(graph.getDefaultParent(), true, false);
        setSelectionCell(cells, (State)selectedItem, true);        
    }//GEN-LAST:event_stateComboBoxActionPerformed

    private void transitionComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transitionComboBoxActionPerformed
        selectedItem = ((JComboBox)evt.getSource()).getSelectedItem();
        Object[] cells = graph.getChildCells(graph.getDefaultParent(), false, true);
        setSelectionCell(cells, (Transition)selectedItem, false);
    }//GEN-LAST:event_transitionComboBoxActionPerformed

    private void initialComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_initialComboBoxActionPerformed
        selectedItem = ((JComboBox)evt.getSource()).getSelectedItem();
        Object[] cells = graph.getChildCells(graph.getDefaultParent(), true, false);
        setSelectionCell(cells, (State)selectedItem, true);        
    }//GEN-LAST:event_initialComboBoxActionPerformed

    private void finalComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_finalComboBoxActionPerformed
        selectedItem = ((JComboBox)evt.getSource()).getSelectedItem();
        Object[] cells = graph.getChildCells(graph.getDefaultParent(), true, false);
        setSelectionCell(cells, (State)selectedItem, true);        
    }//GEN-LAST:event_finalComboBoxActionPerformed

    private void selectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectButtonActionPerformed
        JSplitPane parent = (JSplitPane) this.getParent();
        if (selectedCell.isVertex()) {
            parent.setTopComponent(
                    new StatePropertiesPanel(selectedCell, 
                                             display.cellToState(selectedCell), 
                                             display,jInternalFrame));
        } else if (selectedCell.isEdge()) {
            parent.setTopComponent(
                    new EdgePropertiesPanel(selectedCell, 
                                       display.cellToTransition(selectedCell),
                                       display,jInternalFrame));
        }
    }//GEN-LAST:event_selectButtonActionPerformed

    private void colorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorButtonActionPerformed

        fillColor = JColorChooser.showDialog(this,
                "Fill color", fillColor);
        if (jInternalFrame != null) {
            setFillColor(fillColor);
        }
        colorButton.setBackground(fillColor);


    }//GEN-LAST:event_colorButtonActionPerformed
    private void setFillColor(Color fc){
        
       //StateDrawingData dd=jInternalFrame.automata.getStateDrawingData();
       StateDrawingData dd=new StateDrawingData();
       if(fc!=null) dd.setFillColor(mxUtils.hexString(fc));
        
        jInternalFrame.automata.setAutomataStateDrawingData(dd);
        jInternalFrame.updateAllGDData();
        graph.refresh();
    }
    private void strokeColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_strokeColorButtonActionPerformed
        strokeColor = JColorChooser.showDialog(this,
                "Fill color", strokeColor);
        if (jInternalFrame != null) {
            setStrokeColor(strokeColor);
        }
        strokeColorButton.setBackground(strokeColor);

    }//GEN-LAST:event_strokeColorButtonActionPerformed

    private void setStrokeColor(Color sc){
        
      // StateDrawingData dd=jInternalFrame.automata.getStateDrawingData();
       StateDrawingData dd=new StateDrawingData();
       dd.setStrokeColor(mxUtils.hexString(sc));
        
        jInternalFrame.automata.setAutomataStateDrawingData(dd);
        jInternalFrame.updateAllGDData();
        graph.refresh();
    }
    private void strokeWidthBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_strokeWidthBoxActionPerformed
        JComboBox cb = (JComboBox) evt.getSource();
        String width = (String) cb.getSelectedItem();
        float wid = Float.parseFloat(width);
        if (jInternalFrame != null) {
            setStrokeWidth(wid);
        }

    }//GEN-LAST:event_strokeWidthBoxActionPerformed
    private void setStrokeWidth(float w){
        
       //StateDrawingData dd=jInternalFrame.automata.getStateDrawingData();
       StateDrawingData dd=new StateDrawingData();
       dd.setStrokeWidth(w);
       automata.setAutomataStateDrawingData(dd);
       jInternalFrame.updateAllGDData();
       graph.refresh();
    }
    private void stateShapeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stateShapeComboBoxActionPerformed
        JComboBox cb = (JComboBox) evt.getSource();
        String shape = (String) cb.getSelectedItem();
        if (jInternalFrame != null) {
            setStateShape(shape);
        }
    }//GEN-LAST:event_stateShapeComboBoxActionPerformed
    private void setStateShape(String shape){
        StateGeometricData gd=new StateGeometricData();
        gd.setShape(shape);
        automata.setAutomataStateGeometircData(gd);
        jInternalFrame.updateAllGDData();
       graph.refresh();
    }
    private void stateWidthTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_stateWidthTextFieldKeyPressed
         if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            String str = ((JTextField)evt.getSource()).getText();
            if (str.compareTo("") == 0)
                str = null;
            if(jInternalFrame!=null) setStateWidth(str);
        }
    }//GEN-LAST:event_stateWidthTextFieldKeyPressed
    private void setStateWidth(String str){
        Double w_=Double.valueOf(str);
        if(w_!=null){
            StateGeometricData gd=new StateGeometricData();
            gd.setWidth(w_);
            automata.setAutomataStateGeometircData(gd);
            jInternalFrame.updateAllGDData();
            graph.refresh();
        }
    }
    private void stateHeightTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_stateHeightTextFieldKeyPressed
         if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            String str = ((JTextField)evt.getSource()).getText();
            if (str.compareTo("") == 0)
                str = null;
            if(jInternalFrame!=null) setStateHeight(str);
        }
    }//GEN-LAST:event_stateHeightTextFieldKeyPressed
    private void setStateHeight(String str){
        Double w_=Double.valueOf(str);
        if(w_!=null){
            StateGeometricData gd=new StateGeometricData();
            gd.setHeight(w_);
            automata.setAutomataStateGeometircData(gd);
            jInternalFrame.updateAllGDData();
            graph.refresh();
        }
    }
    private void transitionStrokeColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transitionStrokeColorButtonActionPerformed
        transitionStrokeColor = JColorChooser.showDialog(this,"Stroke color", transitionStrokeColor);
        if (jInternalFrame != null) {
            setTransitionStrokeColor(strokeColor);
        }
        transitionStrokeColorButton.setBackground(strokeColor);
    }//GEN-LAST:event_transitionStrokeColorButtonActionPerformed
    private void setTransitionStrokeColor(Color color){
        TransitionDrawingData dd=new TransitionDrawingData();
        dd.setStrokeColor(mxUtils.hexString(color));
        
        jInternalFrame.automata.setAutomataTransitionDrawingData(dd);
        jInternalFrame.updateAllGDData();
        graph.refresh();
    }
    private void transitionStokeWidthComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transitionStokeWidthComboBoxActionPerformed
        JComboBox cb = (JComboBox) evt.getSource();
        String width = (String) cb.getSelectedItem();
        float wid = Float.parseFloat(width);
        if (jInternalFrame != null) {
            setTransitionStrokeWidth(wid);
        }

    }                                              
    private void setTransitionStrokeWidth(float w){
        
       TransitionDrawingData dd=new TransitionDrawingData();
       dd.setStrokeWidth(w);
       automata.setAutomataTransitionDrawingData(dd);
       jInternalFrame.updateAllGDData();
       graph.refresh();
    
    }//GEN-LAST:event_transitionStokeWidthComboBoxActionPerformed

    private void startArrowComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startArrowComboBoxActionPerformed
       JComboBox cb = (JComboBox) evt.getSource();
       transitionStartArrow = (String) cb.getSelectedItem();
        if (jInternalFrame != null) {
            setStateShape(transitionStartArrow);
        }
    }                                                  
    private void setStartArrow(String startarrow){
        TransitionDrawingData gd=new TransitionDrawingData();
        gd.setStartArrow(startarrow);
        automata.setAutomataTransitionDrawingData(gd);
        jInternalFrame.updateAllGDData();
        graph.refresh();  
    }//GEN-LAST:event_startArrowComboBoxActionPerformed

    private void endArrowComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_endArrowComboBoxActionPerformed
         JComboBox cb = (JComboBox) evt.getSource();
       transitionEndArrow = (String) cb.getSelectedItem();
        if (jInternalFrame != null) {
            setStateShape(transitionEndArrow);
        }
    }                                                  
    private void setEndArrow(String arrow){
        TransitionDrawingData gd=new TransitionDrawingData();
        gd.setEndArrow(arrow);
        automata.setAutomataTransitionDrawingData(gd);
        jInternalFrame.updateAllGDData();
        graph.refresh();  
    }//GEN-LAST:event_endArrowComboBoxActionPerformed

    private void projectionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_projectionButtonActionPerformed
        ProjectionDialog projectionDialog=new ProjectionDialog(automata);
        projectionDialog.setVisible(true);
        
    }//GEN-LAST:event_projectionButtonActionPerformed
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton colorButton;
    private javax.swing.JLabel colorLabel;
    private javax.swing.JComboBox endArrowComboBox;
    private javax.swing.JLabel endArrowLabel;
    private javax.swing.JComboBox finalComboBox;
    private javax.swing.JLabel finalLabel;
    private javax.swing.JTextField finalTextField;
    private javax.swing.JComboBox initialComboBox;
    private javax.swing.JLabel initialLabel;
    private javax.swing.JTextField initialTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton projectionButton;
    private javax.swing.JButton selectButton;
    private javax.swing.JComboBox startArrowComboBox;
    private javax.swing.JLabel startArrowLabel;
    private javax.swing.JComboBox stateComboBox;
    private javax.swing.JLabel stateHeightLabel;
    private javax.swing.JTextField stateHeightTextField;
    private javax.swing.JLabel stateLabel;
    private javax.swing.JComboBox stateShapeComboBox;
    private javax.swing.JLabel stateShapeLabel;
    private javax.swing.JPanel stateSizePanel;
    private javax.swing.JLabel stateStrokeColorLabel1;
    private javax.swing.JPanel stateStylePanel;
    private javax.swing.JTextField stateTextField;
    private javax.swing.JLabel stateWidthLabel;
    private javax.swing.JTextField stateWidthTextField;
    private javax.swing.JButton strokeColorButton;
    private javax.swing.JComboBox strokeWidthBox;
    private javax.swing.JComboBox transitionComboBox;
    private javax.swing.JLabel transitionLabel;
    private javax.swing.JComboBox transitionStokeWidthComboBox;
    private javax.swing.JButton transitionStrokeColorButton;
    private javax.swing.JPanel transitionStylePanel;
    private javax.swing.JTextField transitionTextField;
    // End of variables declaration//GEN-END:variables
}
