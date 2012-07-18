package vgi;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;
import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTextField;

/*
 * edge_properties.java
 *
 * Created on 2012/1/6, 下午 03:59:32
 */
/**
 *
 * @author bl606
 */
public class EdgePropertiesPanel extends javax.swing.JPanel {

    /**
     * Creates new form edge_properties
     */
    public EdgePropertiesPanel(mxCell cell, Transition transition, DisplayUtil display,JgraphXInternalFrame jif) {
        
        initComponents();
        this.cell = cell;
        this.transition = transition;
        this.graph = display.getGraph();
        this.automata = display.getAutomata();
        
        
        showLabel();
        showGeoAndDrawingData();
        
        
        
        jInternalFrame=jif;
        
        
    }
    
    private void showLabel() {
        labelTextField.setText(cell.getValue().toString());
    }
    
    private void showGeoAndDrawingData(){
        
        mxCell source=(mxCell)cell.getSource();
        mxCell target=(mxCell)cell.getTarget();
        if(source==target || source==null || target==null){
            angleLabel.setVisible(true);
            angleTextField.setVisible(true);
            lengthLabel.setVisible(true);
            lengthTextField.setVisible(true);
        }else{
            angleLabel.setVisible(false);
            angleTextField.setVisible(false);
            lengthLabel.setVisible(false);
            lengthTextField.setVisible(false);
        }
        NumberFormat formatter=new DecimalFormat("#.##");
        if(source==null){
            mxPoint term=cell.getGeometry().getTerminalPoint(true);
            mxPoint center=new mxPoint(target.getGeometry().getCenterX(),target.getGeometry().getCenterY());
            
            //System.out.println(term.toString()+" "+center.toString());
            
            double deltax=term.getX()-center.getX();
            double deltay=term.getY()-center.getY();
            double length=Math.sqrt(deltax*deltax+deltay*deltay);
            lengthTextField.setText(formatter.format(length));
            
            double theta=Math.atan2(deltay,deltax);
            theta=Math.toDegrees(theta);
            angleTextField.setText(formatter.format(theta));
                
            
            
        }else if(target==null){
            mxPoint term=cell.getGeometry().getTerminalPoint(false);
            mxPoint center=new mxPoint(source.getGeometry().getCenterX(),source.getGeometry().getCenterY());
            
            //System.out.println(term.toString()+" "+center.toString());
             
            double deltax=term.getX()-center.getX();
            double deltay=term.getY()-center.getY();
            double length=Math.sqrt(deltax*deltax+deltay*deltay);
            lengthTextField.setText(formatter.format(length));
            
            double theta=Math.atan2(deltay,deltax);
            theta=Math.toDegrees(theta);
            angleTextField.setText(String.valueOf(theta));
        
        }else if(source==target){
            mxPoint controlpt=cell.getGeometry().getPoints().get(0);
            mxPoint center=new mxPoint(source.getGeometry().getCenterX(),source.getGeometry().getCenterY());
            
            double deltax=controlpt.getX()-center.getX();
            double deltay=controlpt.getY()-center.getY();
            double length=Math.sqrt(deltax*deltax+deltay*deltay);
            lengthTextField.setText(formatter.format(length));
            
            double theta=Math.atan2(deltay,deltax);
            theta=Math.toDegrees(theta);
            angleTextField.setText(formatter.format(theta));
            
          
        }
        
        Map<String,Object> styles=graph.getCellStyle(cell);
        String color=(String)styles.get("strokeColor");
        strokeColor=Color.decode(color);
        strokeColorButton.setBackground(strokeColor);
        
        strokeWidth=(String)styles.get("strokeWidth");
        if(strokeWidth!=null){
            float width=Float.parseFloat(strokeWidth);
            int ind=(int)width;
            strokeWidthBox.setSelectedIndex(ind-1);
        }
        String startArrow=(String)styles.get("startArrow");
        if(startArrow!=null) startStyleComboBox.setSelectedItem(startArrow);
        
        String endArrow=(String)styles.get("endArrow");
        if(endArrow!=null) endStyleComboBox.setSelectedItem(endArrow);
        
        String textShape=(String)styles.get("textShape");
        if(textShape.equals("curve")) curveLabelCheckBox.setSelected(true);
        
        
        
    }
    private void setStartEndArrow(JComboBox comboBox, Boolean startEnd) {
        String arrowDir = (startEnd) ? "startArrow" : "endArrow";
        String arrowType = ((String)comboBox.getSelectedItem()).toLowerCase();
        Object[] edge = {cell};
        
        graph.setCellStyles(arrowDir, arrowType, edge);
        if(jInternalFrame!=null) jInternalFrame.setModified(true);
    }
    
    public void setStrokeColor(mxGraph graph, Color color) {
        Object[] edge = {cell};
        graph.setCellStyles("strokeColor", mxUtils.hexString(color), edge);
        if(jInternalFrame!=null)jInternalFrame.setModified(true);
    }

    public void setStrokeWidth(mxGraph graph, float width) {
        Object[] edge = {cell};
        String wid = String.valueOf(width);
        graph.setCellStyles("strokeWidth", wid, edge);
        if(jInternalFrame!=null) jInternalFrame.setModified(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel3 = new javax.swing.JLabel();
        labelLabel = new javax.swing.JLabel();
        labelTextField = new javax.swing.JTextField();
        startStyleLabel = new javax.swing.JLabel();
        startStyleComboBox = new javax.swing.JComboBox();
        ednStyleLabel = new javax.swing.JLabel();
        endStyleComboBox = new javax.swing.JComboBox();
        strokeLabel = new javax.swing.JLabel();
        strokeColorButton = new javax.swing.JButton();
        strokeWidthBox = new javax.swing.JComboBox();
        angleLabel = new javax.swing.JLabel();
        angleTextField = new javax.swing.JTextField();
        lengthLabel = new javax.swing.JLabel();
        lengthTextField = new javax.swing.JTextField();
        curveLabelCheckBox = new javax.swing.JCheckBox();

        jLabel3.setText("jLabel3");

        setPreferredSize(new java.awt.Dimension(325, 362));
        setLayout(new java.awt.GridBagLayout());

        labelLabel.setText("Label :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(labelLabel, gridBagConstraints);

        labelTextField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelTextFieldMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        add(labelTextField, gridBagConstraints);

        startStyleLabel.setText("Start_Style :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(startStyleLabel, gridBagConstraints);

        startStyleComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "classic", "block", "open", "oval", "diamond" }));
        startStyleComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startStyleComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        add(startStyleComboBox, gridBagConstraints);

        ednStyleLabel.setText("End_Style :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(ednStyleLabel, gridBagConstraints);

        endStyleComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "classic", "block", "open", "oval", "diamond" }));
        endStyleComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                endStyleComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        add(endStyleComboBox, gridBagConstraints);

        strokeLabel.setText("Stroke :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(strokeLabel, gridBagConstraints);

        strokeColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                strokeColorButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(strokeColorButton, gridBagConstraints);

        strokeWidthBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }));
        strokeWidthBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                strokeWidthBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        add(strokeWidthBox, gridBagConstraints);

        angleLabel.setText("Angle :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(angleLabel, gridBagConstraints);

        angleTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                angleTextFieldKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(angleTextField, gridBagConstraints);

        lengthLabel.setText("Length :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(lengthLabel, gridBagConstraints);

        lengthTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                lengthTextFieldKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(lengthTextField, gridBagConstraints);

        curveLabelCheckBox.setText("curve label");
        curveLabelCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                curveLabelCheckBoxItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 8;
        add(curveLabelCheckBox, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void startStyleComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startStyleComboBoxActionPerformed
        setStartEndArrow((JComboBox)evt.getSource(), true);
    }//GEN-LAST:event_startStyleComboBoxActionPerformed

    private void endStyleComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_endStyleComboBoxActionPerformed
        setStartEndArrow((JComboBox)evt.getSource(), false);
    }//GEN-LAST:event_endStyleComboBoxActionPerformed

    private void labelTextFieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelTextFieldMouseClicked
        ExpressionEditor editor = new ExpressionEditor(
                new JFrame(), 
                true, 
                (WeightedRegularExpression) ((mxCell) cell).getValue());
        editor.setVisible(true);
        ((mxCell) cell).setValue(editor.getExpression());
    }//GEN-LAST:event_labelTextFieldMouseClicked

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

    private void angleTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_angleTextFieldKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            String str = ((JTextField)evt.getSource()).getText();
            if (str.compareTo("") == 0)
                str = null;
            setAngle(str);
        }
         graph.refresh();
        
    }//GEN-LAST:event_angleTextFieldKeyPressed

    private void lengthTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lengthTextFieldKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            String str = ((JTextField)evt.getSource()).getText();
            if (str.compareTo("") == 0)
                str = null;
            setLength(str);
        }
         graph.refresh();
    }//GEN-LAST:event_lengthTextFieldKeyPressed

    private void curveLabelCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_curveLabelCheckBoxItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED){
            
            setCurveLabel(true);
        }else if (evt.getStateChange() == ItemEvent.DESELECTED){
            setCurveLabel(false);
        }
    }//GEN-LAST:event_curveLabelCheckBoxItemStateChanged

    private void setAngle(String str){
        mxGeometry geo=cell.getGeometry();
        Double angle_=Double.valueOf(str);
        angle_=Math.toRadians(angle_);
        
        if(angle_!=null){
            mxCell source=(mxCell)cell.getSource();
            mxCell target=(mxCell)cell.getTarget();
            if(source==target){ // loop
                mxPoint ctrlpt=geo.getPoints().get(0);
                
                double deltax=ctrlpt.getX()-source.getGeometry().getCenterX();
                double deltay=ctrlpt.getY()-source.getGeometry().getCenterY();
                double length=Math.sqrt(deltax*deltax+deltay*deltay);
                
                mxPoint newterm=new mxPoint(source.getGeometry().getCenterX()+length*Math.cos(angle_),
                                            source.getGeometry().getCenterY()+length*Math.sin(angle_));
                ArrayList<mxPoint> list=new ArrayList<mxPoint>();
                list.add(newterm);
                geo.setPoints(list);
                cell.setGeometry(geo);
                
            }else if(source==null){ // initial
                mxPoint terminal=geo.getTerminalPoint(true);
                
                mxGeometry vertexgeo=target.getGeometry();
                mxPoint center=new mxPoint(vertexgeo.getCenterX(),vertexgeo.getCenterY());
                
                double deltax=terminal.getX()-center.getX();
                double deltay=terminal.getY()-center.getY();
                double length=Math.sqrt(deltax*deltax+deltay*deltay);
                
                
                mxPoint newterm=new mxPoint();
                newterm.setX(center.getX()+length*Math.cos(angle_));
                newterm.setY(center.getY()+length*Math.sin(angle_));
                geo.setTerminalPoint(newterm, true);
                
            }else if(target==null){ // final
                mxPoint terminal=geo.getTerminalPoint(false);
                
                mxGeometry vertexgeo=source.getGeometry();
                mxPoint center=new mxPoint(vertexgeo.getCenterX(),vertexgeo.getCenterY());
                
                double deltax=terminal.getX()-center.getX();
                double deltay=terminal.getY()-center.getY();
                double length=Math.sqrt(deltax*deltax+deltay*deltay);
                
                
                mxPoint newterm=new mxPoint();
                newterm.setX(center.getX()+length*Math.cos(angle_));
                newterm.setY(center.getY()+length*Math.sin(angle_));
                geo.setTerminalPoint(newterm, false);
               
                
            }
            
            cell.setGeometry(geo);
            jInternalFrame.setModified(true);
         }
    }
    private void setLength(String str){
        mxGeometry geo=cell.getGeometry();
        Double len=Double.valueOf(str);
        
        if(len!=null){
            mxCell source=(mxCell)cell.getSource();
            mxCell target=(mxCell)cell.getTarget();
            if(source==target){ // loop
                mxPoint ctrlpt=geo.getPoints().get(0);
                
                double deltax=ctrlpt.getX()-source.getGeometry().getCenterX();
                double deltay=ctrlpt.getY()-source.getGeometry().getCenterY();
                
                double theta=Math.atan2(deltay,deltax);
                
                mxPoint newterm=new mxPoint(source.getGeometry().getCenterX()+len*Math.cos(theta),
                                            source.getGeometry().getCenterY()+len*Math.sin(theta));
                ArrayList<mxPoint> list=new ArrayList<mxPoint>();
                list.add(newterm);
                geo.setPoints(list);
                cell.setGeometry(geo);
                
            }else if(source==null){ // initial
                mxPoint terminal=geo.getTerminalPoint(true);
                
                mxGeometry vertexgeo=target.getGeometry();
                mxPoint center=new mxPoint(vertexgeo.getCenterX(),vertexgeo.getCenterY());
                
                double deltax=terminal.getX()-center.getX();
                double deltay=terminal.getY()-center.getY();
                double theta=Math.atan2(deltay,deltax);
                
                mxPoint newterm=new mxPoint();
                newterm.setX(center.getX()+len*Math.cos(theta));
                newterm.setY(center.getY()+len*Math.sin(theta));
                geo.setTerminalPoint(newterm, true);
                
            }else if(target==null){ // final
                mxPoint terminal=geo.getTerminalPoint(false);
                
                mxGeometry vertexgeo=source.getGeometry();
                mxPoint center=new mxPoint(vertexgeo.getCenterX(),vertexgeo.getCenterY());
                
                double deltax=terminal.getX()-center.getX();
                double deltay=terminal.getY()-center.getY();
                double theta=Math.atan2(deltay,deltax);
                
                
                mxPoint newterm=new mxPoint();
                newterm.setX(center.getX()+len*Math.cos(theta));
                newterm.setY(center.getY()+len*Math.sin(theta));
                geo.setTerminalPoint(newterm, false);
               
                
            }
            
            cell.setGeometry(geo);
            jInternalFrame.setModified(true);
         }
        
        
        
    }
    private void setCurveLabel(boolean useCurveLabel){
        
        Object[] edge = {cell};
        if(useCurveLabel)
            graph.setCellStyles("textShape","curve");
        else
            graph.setCellStyles("textShape","default");
        
        if(jInternalFrame!=null) jInternalFrame.setModified(true);
        
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel angleLabel;
    private javax.swing.JTextField angleTextField;
    private javax.swing.JCheckBox curveLabelCheckBox;
    private javax.swing.JLabel ednStyleLabel;
    private javax.swing.JComboBox endStyleComboBox;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel labelLabel;
    private javax.swing.JTextField labelTextField;
    private javax.swing.JLabel lengthLabel;
    private javax.swing.JTextField lengthTextField;
    private javax.swing.JComboBox startStyleComboBox;
    private javax.swing.JLabel startStyleLabel;
    private javax.swing.JButton strokeColorButton;
    private javax.swing.JLabel strokeLabel;
    private javax.swing.JComboBox strokeWidthBox;
    // End of variables declaration//GEN-END:variables
    private mxCell cell;
    private mxGraph graph;
    private Automata automata;
    private Transition transition;
    private DisplayUtil display;
    private Color strokeColor = Color.white;
    private String strokeWidth = null;
    private double angle;
    JgraphXInternalFrame jInternalFrame;
    
}
