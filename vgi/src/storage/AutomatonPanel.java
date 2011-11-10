/*
 * AutomatonPan.java
 *
 * Created on Aug 20, 2009, 11:36:28 PM
 */
package storage;

import java.util.Enumeration;
import java.util.Vector;
import model.Automata2.FreeMonoid;
import model.Automata2.GenCompSort;
import model.Automata2.MonCompGen;
import model.Automata2.MonGen;
import model.Automata2.Monoid;
import model.Automata2.NumericalSemiring;
import model.Automata2.ProductMonoid;
import model.Automata2.Semiring;
import model.Automata2.SeriesSemiring;
import model.Automata2.ValueType;

/**
 * to show the properties of automata in a panel. please refer to the "fsmxml" for more info.
 * @author jllu
 */
public class AutomatonPanel extends javax.swing.JPanel {
    /**
     * the structure of properties of automata
     */
    protected ValueType valueType;
    /** Creates new form AutomatonPan */
    public AutomatonPanel() {
        initComponents();
    }
    /**
     * to show the properties of automata with the assigned index
     * @param index the index of the graph
     * @author jllu
     */
    public void ShowAutomaton(int index){
        try {
            this.valueType = Storage.getInstance().getController(index).getDataModel().getValueType();
            this.zeroLabel.setText(this.valueType.getZeroSymbol().toString());
            this.oneLabel.setText(this.valueType.getOneSymbol().toString());
            this.semiringTextArea.setText(this.GetSemiringData());
            this.monoidTextArea.setText(this.GetMonoidData(this.valueType.getMonoid()));
        } catch (Exception e) {
            return;
        }
    }
    /**
     * @return the info of semiring
     * @author jllu
     */
    protected String GetSemiringData() {
        Semiring semiring = this.valueType.getSemiring();
        String data ="";
        if(semiring != null) {
            if(semiring.getTypeE().toString().equals("NUMERICAL")) {
                NumericalSemiring numericalSemiring = (NumericalSemiring)semiring;
                this.semiringTypeLabel.setText(numericalSemiring.getTypeE().toString());
                data +="set = "+ numericalSemiring.getSetE().toString()+"\n";
                data +="operations = "+ numericalSemiring.getOperationE().toString()+"\n";
                
            }
            else if(semiring.getTypeE().toString().equals("SERIES")) {
                SeriesSemiring seriesSemiring = (SeriesSemiring)semiring;
                this.semiringTypeLabel.setText(seriesSemiring.getTypeE().toString());
                NumericalSemiring numericalSemiring = (NumericalSemiring)seriesSemiring.getSemiring();
                data +="semiring\n";
                data +="type = "+ numericalSemiring.getTypeE().toString()+"\n";
                data +="set = "+ numericalSemiring.getSetE().toString()+"\n";
                data +="operations = "+ numericalSemiring.getOperationE().toString()+"\n";
                data +="/semiring\n";
                data +="monoid\n";
                data += this.GetMonoidData(seriesSemiring.getMonoid());
                data +="/monoid\n";
            }
        }
        return data;
    }
    /**
     * @param monoid the monoid structure
     * @return the info. of monoid
     * @author jllu
     */
    protected String GetMonoidData(Monoid monoid) {
        String data ="";
        if(monoid != null) {
            if(monoid.getTypeE().toString().equals("UNIT")) {
                // not supported now
            }else if(monoid.getTypeE().toString().equals("FREE")) {
                FreeMonoid freeMonoid = (FreeMonoid)monoid;
                data += this.GetFreeMonoidData(freeMonoid);
            }else if(monoid.getTypeE().toString().equals("PRODUCT")) {
                ProductMonoid productMonoid = (ProductMonoid)monoid;
                data += this.GetProductMonoidData(productMonoid);
            }
        }
        return data;
    }
    /**
     * @param freeMonoid the freeMonoid structure
     * @return the info of free monoid
     * @author jllu
     */
    protected String GetFreeMonoidData(FreeMonoid freeMonoid) {
        String data = "type = free\n";
        if(freeMonoid.getGenKindE().toString().equals("SIMPLE")) {
            data += "genkind = simple\n";
            data += "genSort = "+freeMonoid.getGenSortE().toString()+"\n";
            data += this.GetMonGenData("SIMPLE",freeMonoid.getMonGenV());
        }else if(freeMonoid.getGenKindE().toString().equals("TUPLE")){
            data += "genkind = tuple\n";
            data += "genDim = "+ Integer.toString(freeMonoid.getGenDim())+"\n";
            data += "genSort\n";
            for (Enumeration e =  freeMonoid.getGenSort().getGenCompSortV().elements();e.hasMoreElements();) {
                GenCompSort genCompSort = (GenCompSort)e.nextElement();
                data += "    genComSort value = "+ genCompSort.getValueE().toString() +"\n";
            }
            data += "/genSort\n";
            data += this.GetMonGenData("TUPLE",freeMonoid.getMonGenV());
        }
        return data;
    }
    /**
    * @param productMonoid the productMonoid structure
    * @return the info of product monoid
    * @author jllu
    */
    protected String GetProductMonoidData(ProductMonoid productMonoid) {
        String data = "type = product\n";
        data += "prodDim = "+ Integer.toString(productMonoid.getProdDim())+"\n";
        for (Enumeration e = productMonoid.getFreeMonoidV().elements();e.hasMoreElements();) {
            FreeMonoid freeMonoid = (FreeMonoid)e.nextElement();
            data += "monoid\n";
            data += this.GetFreeMonoidData(freeMonoid);
            data += "/monoid\n";
        }
        return data;
    }
    /**
    * @param type the type of gensort
    * @param monGenList the list of mongen
    * @return the info of all mongens
    * @author jllu
    */
    protected String GetMonGenData(String type,Vector<MonGen> monGenList) {
        String data ="";
        if(type.equals("SIMPLE")) {
            for (Enumeration e =  monGenList.elements();e.hasMoreElements();) {
                MonGen monGen = (MonGen)e.nextElement();
                data += "monGen value = "+monGen.getValue() + "\n";
            }
            
        }else if(type.equals("TUPLE")){
            for (Enumeration e =  monGenList.elements();e.hasMoreElements();) {
                MonGen monGen = (MonGen)e.nextElement();
                data += "monGen\n";
                for (Enumeration it =  monGen.getMonCompGenV().elements();it.hasMoreElements();) {
                    MonCompGen monCompGen = (MonCompGen)it.nextElement();
                    data += "   monComGen value = "+monCompGen.getValue() + "\n";
                }
                data += "/monGen\n";
            }
        }
        return data;
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fsmxmlPan = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        xmlnsLabel = new javax.swing.JLabel();
        versionLabel = new javax.swing.JLabel();
        semiringPane = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        semiringTypeLabel = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        zeroLabel = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        oneLabel = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        semiringTextArea = new javax.swing.JTextArea();
        semiringPane1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        monoidTextArea = new javax.swing.JTextArea();

        fsmxmlPan.setBorder(javax.swing.BorderFactory.createTitledBorder("FSMXML"));

        jLabel2.setText("xmlns:");

        jLabel3.setText("Version:");

        xmlnsLabel.setText("http://vaucanson-project.org");

        versionLabel.setText("1.0");

        javax.swing.GroupLayout fsmxmlPanLayout = new javax.swing.GroupLayout(fsmxmlPan);
        fsmxmlPan.setLayout(fsmxmlPanLayout);
        fsmxmlPanLayout.setHorizontalGroup(
            fsmxmlPanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fsmxmlPanLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(fsmxmlPanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addGap(26, 26, 26)
                .addGroup(fsmxmlPanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(versionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(xmlnsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(64, Short.MAX_VALUE))
        );
        fsmxmlPanLayout.setVerticalGroup(
            fsmxmlPanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fsmxmlPanLayout.createSequentialGroup()
                .addGroup(fsmxmlPanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(xmlnsLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(fsmxmlPanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(versionLabel))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        semiringPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Semiring"));
        semiringPane.setPreferredSize(new java.awt.Dimension(228, 179));

        jLabel4.setText("type:");

        jLabel5.setText("zeroSymbol:");

        zeroLabel.setText(" ");
        zeroLabel.setFocusCycleRoot(true);

        jLabel6.setText("oneSymbol:");

        oneLabel.setText(" ");

        semiringTextArea.setColumns(20);
        semiringTextArea.setRows(5);
        jScrollPane3.setViewportView(semiringTextArea);

        javax.swing.GroupLayout semiringPaneLayout = new javax.swing.GroupLayout(semiringPane);
        semiringPane.setLayout(semiringPaneLayout);
        semiringPaneLayout.setHorizontalGroup(
            semiringPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(semiringPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(semiringPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(semiringPaneLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(36, 36, 36)
                        .addComponent(semiringTypeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, semiringPaneLayout.createSequentialGroup()
                        .addGroup(semiringPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(semiringPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(oneLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(zeroLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE))))
                .addContainerGap(140, Short.MAX_VALUE))
            .addGroup(semiringPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(semiringPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 299, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(32, Short.MAX_VALUE)))
        );
        semiringPaneLayout.setVerticalGroup(
            semiringPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(semiringPaneLayout.createSequentialGroup()
                .addGroup(semiringPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(semiringTypeLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(semiringPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(zeroLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(semiringPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(oneLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(semiringPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(semiringPaneLayout.createSequentialGroup()
                    .addGap(75, 75, 75)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        semiringPane1.setBorder(javax.swing.BorderFactory.createTitledBorder("Monoid"));
        semiringPane1.setPreferredSize(new java.awt.Dimension(228, 179));

        monoidTextArea.setColumns(20);
        monoidTextArea.setRows(5);
        jScrollPane2.setViewportView(monoidTextArea);

        javax.swing.GroupLayout semiringPane1Layout = new javax.swing.GroupLayout(semiringPane1);
        semiringPane1.setLayout(semiringPane1Layout);
        semiringPane1Layout.setHorizontalGroup(
            semiringPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(semiringPane1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );
        semiringPane1Layout.setVerticalGroup(
            semiringPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(semiringPane1Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(semiringPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 355, Short.MAX_VALUE)
                    .addComponent(semiringPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 355, Short.MAX_VALUE)
                    .addComponent(fsmxmlPan, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fsmxmlPan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(semiringPane, javax.swing.GroupLayout.PREFERRED_SIZE, 308, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(semiringPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel fsmxmlPan;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea monoidTextArea;
    private javax.swing.JLabel oneLabel;
    private javax.swing.JPanel semiringPane;
    private javax.swing.JPanel semiringPane1;
    private javax.swing.JTextArea semiringTextArea;
    private javax.swing.JLabel semiringTypeLabel;
    private javax.swing.JLabel versionLabel;
    private javax.swing.JLabel xmlnsLabel;
    private javax.swing.JLabel zeroLabel;
    // End of variables declaration//GEN-END:variables
}
