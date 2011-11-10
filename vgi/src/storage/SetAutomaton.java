/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package storage;

import event.UIEvent;
import event.UserEventConstants;
import java.util.Vector;
import model.Automata2.FreeMonoid;
import model.Automata2.FreeMonoid.GenDescriptE;
import model.Automata2.FreeMonoid.GenSortE;
import model.Automata2.GenCompSort;
import model.Automata2.GenSort;
import model.Automata2.MonCompGen;
import model.Automata2.MonGen;
import model.Automata2.Monoid;
import model.Automata2.NumericalSemiring;
import model.Automata2.ProductMonoid;
import model.Automata2.Semiring;
import model.Automata2.SeriesSemiring;
import model.Automata2.ValueType;
/**
* include all dialogs and procedures to set the automata
* @author Junli Lu
*/
public class SetAutomaton {
    /** the setting dialog for semiring */
    protected SemiringMainDialog semiringMainDialog;
    /** the setting dialog for numerical semiring                     */
    protected SemiringNumericalDialog semiringNumericalDialog;
    /** the setting dialog for monoid */
    protected MonoidMainDialog monoidMainDialog;
    /**the setting dialog for gen sort */
    protected MonoidGenSortDialog monoidGenSortDialog;
    /** the setting dialog for mon gen */
    protected MonoidMonGenDialog monoidMonGenDialog;
    /** indicate to set series semiring or not */
    protected boolean isSeriesSemiring;
    /** indicate to set product monoid or not */
    protected boolean isSetProductMonoid;
    /** the structure of properties of automata */
    protected ValueType editValueType;
    /** a space including all the items of UI */
    protected Storage storage = Storage.getInstance();
    /** the main frame */
    java.awt.Frame parent = storage.getFrame();
    /** create all instances of dialogs */
    SetAutomaton() {
        //MonoidMainDialog monoidMainDialog = new MonoidMainDialog();
        this.semiringMainDialog = new SemiringMainDialog(storage.getFrame(),false);
        this.semiringNumericalDialog = new SemiringNumericalDialog(storage.getFrame(),false);
        this.monoidMainDialog = new MonoidMainDialog(storage.getFrame(),false);
        this.monoidGenSortDialog = new MonoidGenSortDialog(storage.getFrame(),false);
        this.monoidMonGenDialog = new MonoidMonGenDialog(storage.getFrame(),false);
        this.editValueType = new ValueType();
    }

    /** start procedure */
    public void Start() {
        this.semiringMainDialog.Initialize();
    }
    /**
    * save monoid
    * @param monoid, the monoid structure
    * @author Junli Lu
    */
    protected void SaveMonoind(Monoid monoid) {
        if(this.isSeriesSemiring){
            this.isSeriesSemiring = false;
            SeriesSemiring seriesSemiring = (SeriesSemiring)this.editValueType.getSemiring();
            seriesSemiring.setMonoid((monoid));
            monoidMainDialog.Initialize();
            monoidMainDialog.setTitle("ValueType::Monoid");
        }else{
            if(isSetProductMonoid){
                this.editValueType.setProductMonoid((ProductMonoid)monoid);
            }else{
                this.editValueType.setFreeMonoid((FreeMonoid)monoid);
            }
            SaveValueType();
        } 
    }
    /**
    * save the value type
    * @author Junli Lu
    */
    protected void SaveValueType() {
        this.storage.controllerCreate(null);
        this.storage.getController().getDataModel().setValueType(this.editValueType);
        UIEvent uievent = new UIEvent(UserEventConstants.VALUETYPE_HAVE_BEEN_SET);
        this.storage.eventHappen(uievent);
    }

    /**
     * the dialog to set semiring
     */
    public class SemiringMainDialog extends javax.swing.JDialog {
        /**
         * initialize componenets
         * @param parent the main frame
         * @param modal the status of the dialog
         */
        public SemiringMainDialog(java.awt.Frame parent, boolean modal) {
            super(parent, modal);
            initComponents();    
        }
        /**
         * initializaiton
         */
        public void Initialize(){
            this.setLocationRelativeTo(parent);
            this.setTitle("ValueType::Semiring");
            this.setVisible(true);
            isSeriesSemiring = false;
        }
        /**
         * actions when ok is pressed
         * @param evt key events
         */
        private void okBtnActionPerformed(java.awt.event.ActionEvent evt) {
            if(this.zeroSymbolText.getText() != null)
                editValueType.setZeroSymbol(this.zeroSymbolText.getText());
            if(this.identitySymbolText.getText() != null)
                editValueType.setOneSymbol(this.identitySymbolText.getText());
            if(this.typeCombBox.getSelectedItem().toString().equals("series")){
                isSeriesSemiring = true;
                editValueType.setSeriesSemiring(new SeriesSemiring(null,null));
                semiringNumericalDialog.Initialize(editValueType.getSemiring());
                semiringNumericalDialog.setTitle("SeriesSemiring::Semiring");
            }else{
                editValueType.setNumericalSemiring(new NumericalSemiring(null,null));
                semiringNumericalDialog.Initialize(editValueType.getSemiring());
            }
            this.setVisible(false);
           
        }
        /**
         * actions when cancel is pressed
         * @param evt key events
         */
        private void cancelBtnActionPerformed(java.awt.event.ActionEvent evt) {
            this.setVisible(false);
        }
        /**
         * actions when identitySymbol text is typed
         * @param evt key events
         */
        private void identitySymbolTextKeyTyped(java.awt.event.KeyEvent evt) {
            this.identitySymbolText.setText("");
        }
        /**
         * actions when identitySymbol text is released
         * @param evt key events
         */
        private void identitySymbolTextKeyReleased(java.awt.event.KeyEvent evt) {
            if(this.identitySymbolText.getText().length() == 1) {
                char c = this.identitySymbolText.getText().charAt(0);
                if(!((c <= 57 && c >= 48) || (c <= 122 && c >= 97))) {
                    identitySymbolText.setText("");
                }
            }
        }
        /**
         * actions when zeroSymbol text is typed
         * @param evt key events
         */
        private void zeroSymbolTextKeyTyped(java.awt.event.KeyEvent evt) {
            this.zeroSymbolText.setText("");
        }
        /**
         * actions when zeroSymbol text is released
         * @param evt key events
         */
        private void zeroSymbolTextKeyReleased(java.awt.event.KeyEvent evt) {
            if(this.zeroSymbolText.getText().length() == 1) {
                char c = this.zeroSymbolText.getText().charAt(0);
                if(!((c <= 57 && c >= 48) || (c <= 122 && c >= 97))) {
                    zeroSymbolText.setText("");
                }
            }
        }

        @SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated Code">
        private void initComponents() {

            jPanel1 = new javax.swing.JPanel();
            jPanel2 = new javax.swing.JPanel();
            jLabel1 = new javax.swing.JLabel();
            typeCombBox = new javax.swing.JComboBox();
            identitySymbolText = new javax.swing.JTextField();
            jLabel4 = new javax.swing.JLabel();
            jLabel5 = new javax.swing.JLabel();
            zeroSymbolText = new javax.swing.JTextField();
            okBtn = new javax.swing.JButton();
            cancelBtn = new javax.swing.JButton();

            setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

            jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Semiring"));

            jLabel1.setText("type");

            typeCombBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "numerical", "series" }));

            zeroSymbolText.setText("0");
            zeroSymbolText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                zeroSymbolTextKeyTyped(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                zeroSymbolTextKeyReleased(evt);
            }});
            identitySymbolText.setText("1");
            identitySymbolText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                identitySymbolTextKeyTyped(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                identitySymbolTextKeyReleased(evt);
            }});


            jLabel4.setText("zeroSymbol");

            jLabel5.setText("identitySymbol");

            zeroSymbolText.setText("0");

            javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
            jPanel2.setLayout(jPanel2Layout);
            jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(typeCombBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(18, 18, 18)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(identitySymbolText, javax.swing.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE)
                                .addComponent(zeroSymbolText, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE))))
                    .addContainerGap())
            );
            jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(typeCombBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(zeroSymbolText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(identitySymbolText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );

            okBtn.setText("OK");
            okBtn.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    okBtnActionPerformed(evt);
                }
            });

            cancelBtn.setText("cancel");
            cancelBtn.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    cancelBtnActionPerformed(evt);
                }
            });

            javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
            jPanel1.setLayout(jPanel1Layout);
            jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(okBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(113, 113, 113)
                            .addComponent(cancelBtn))
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(okBtn)
                        .addComponent(cancelBtn))
                    .addContainerGap())
            );

            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
            getContentPane().setLayout(layout);
            layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            );
            layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );

            pack();
        }// </editor-fold>

        private javax.swing.JButton cancelBtn;
        private javax.swing.JTextField identitySymbolText;
        private javax.swing.JLabel jLabel1;
        private javax.swing.JLabel jLabel4;
        private javax.swing.JLabel jLabel5;
        private javax.swing.JPanel jPanel1;
        private javax.swing.JPanel jPanel2;
        private javax.swing.JButton okBtn;
        private javax.swing.JComboBox typeCombBox;
        private javax.swing.JTextField zeroSymbolText;

    }
    /**
     * the dialog to set numerical semiring
     */
    public class SemiringNumericalDialog extends javax.swing.JDialog {
        /** the semiring */
        protected Semiring semiring;
        /**
         * initialize componenets
         * @param parent the main frame
         * @param modal the status of the dialog
         */
        public SemiringNumericalDialog(java.awt.Frame parent, boolean modal) {
            super(parent, modal);
            initComponents();
        }
        /**
         * initializaiton
         * @param semiring the structure of semiring
         */
        public void Initialize(Semiring semiring) {
            this.setLocationRelativeTo(parent);
            this.setTitle("ValueType::Semiring");
            this.setVisible(true);
            this.semiring = semiring;
        }
        /**
        * save the semiring
        * @author Junli Lu
        */
        protected void SaveSemiring() {
            NumericalSemiring.SetE setE = null;
            if(this.setComboBox.getSelectedItem().toString().equals("B")){
                setE = NumericalSemiring.SetE.B;
            }else if(this.setComboBox.getSelectedItem().toString().equals("N")){
                setE = NumericalSemiring.SetE.N;
            }else if(this.setComboBox.getSelectedItem().toString().equals("Z")){
                setE = NumericalSemiring.SetE.Z;
            }else if(this.setComboBox.getSelectedItem().toString().equals("Q")){
                setE = NumericalSemiring.SetE.Q;
            }else if(this.setComboBox.getSelectedItem().toString().equals("R")){
                setE = NumericalSemiring.SetE.R;
            }else if(this.setComboBox.getSelectedItem().toString().equals("C")){
                setE = NumericalSemiring.SetE.C;
            }
            NumericalSemiring.OperationE opE = null;
            if(this.opComboBox.getSelectedItem().toString().equals("classical")) {
                opE = NumericalSemiring.OperationE.CLASSICAL;
            }
            else if(this.opComboBox.getSelectedItem().toString().equals("MinPlus")) {
                opE = NumericalSemiring.OperationE.MINPLUS;
            }
            else if(this.opComboBox.getSelectedItem().toString().equals("MaxPlus")) {
                opE = NumericalSemiring.OperationE.MAXPLUS;
            }
            NumericalSemiring numericalSemiring = new NumericalSemiring(setE, opE);
            if(this.semiring.getTypeE().toString().equals("SERIES")){
                SeriesSemiring seriesSemiring = (SeriesSemiring)this.semiring;
                seriesSemiring.setSemiring(numericalSemiring);
                editValueType.setSeriesSemiring(seriesSemiring);
                monoidMainDialog.Initialize();
                monoidMainDialog.setTitle("SeriesSemiring::Monoid");
            }else{
                editValueType.setNumericalSemiring(numericalSemiring);
                monoidMainDialog.Initialize();
                monoidMainDialog.setTitle("ValueType::Monoid");
            }
        }
        /**
         * actions when ok is pressed
         * @param evt key events
         */
        private void okBtnActionPerformed(java.awt.event.ActionEvent evt) {
            this.SaveSemiring();
            this.setVisible(false);
        }

        @SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated Code">
        private void initComponents() {

            jPanel1 = new javax.swing.JPanel();
            jPanel2 = new javax.swing.JPanel();
            jLabel1 = new javax.swing.JLabel();
            jLabel6 = new javax.swing.JLabel();
            setComboBox = new javax.swing.JComboBox();
            jLabel2 = new javax.swing.JLabel();
            jLabel3 = new javax.swing.JLabel();
            opComboBox = new javax.swing.JComboBox();
            okBtn = new javax.swing.JButton();
            resetBtn = new javax.swing.JButton();

            setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

            jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Semiring"));

            jLabel1.setText("type");

            jLabel6.setText("numerical");

            setComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "B", "Z", "R" }));

            jLabel2.setText("set");

            jLabel3.setText("opertion");

            opComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "classical", "MinPlus", "MaxPlus" }));

            javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
            jPanel2.setLayout(jPanel2Layout);
            jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(opComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(18, 18, 18)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(setComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addContainerGap())
            );
            jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(setComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(opComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );

            okBtn.setText("OK");
            okBtn.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    okBtnActionPerformed(evt);
                }
            });

            resetBtn.setText("reset");
            resetBtn.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    resetBtnActionPerformed(evt);
                }
            });

            javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
            jPanel1.setLayout(jPanel1Layout);
            jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(okBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(resetBtn))
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(resetBtn)
                        .addComponent(okBtn))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );

            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
            getContentPane().setLayout(layout);
            layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            );
            layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            );

            pack();
        }// </editor-fold>
        /**
         * actions when reset is pressed
         * @param evt key events
         */
        private void resetBtnActionPerformed(java.awt.event.ActionEvent evt) {
            this.setVisible(false);
            semiringMainDialog.Initialize();
        }
        
        // Variables declaration - do not modify
        private javax.swing.JLabel jLabel1;
        private javax.swing.JLabel jLabel2;
        private javax.swing.JLabel jLabel3;
        private javax.swing.JLabel jLabel6;
        private javax.swing.JPanel jPanel1;
        private javax.swing.JPanel jPanel2;
        private javax.swing.JButton okBtn;
        private javax.swing.JComboBox opComboBox;
        private javax.swing.JButton resetBtn;
        private javax.swing.JComboBox setComboBox;
    }
    /**
     * the dialog to set monoid
     */
    public class MonoidMainDialog extends javax.swing.JDialog {
        /** the dimension of product monoid */
        protected int prodDimTimes;
        /** a variable to count the number of monoids*/
        protected int countProdDimTimes;
        /** the list of free monoids*/
        protected Vector<FreeMonoid> freemonoidList;

        /**
         * initialize componenets
         * @param parent the main frame
         * @param modal the status of the dialog
         */
        public MonoidMainDialog(java.awt.Frame parent, boolean modal) {
            super(parent, modal);
            initComponents();
            OpenPanRule();
        }
        /**
         * initialization
         */
        public void Initialize(){
            this.setLocationRelativeTo(parent);            
            this.freemonoidList = null;
            isSetProductMonoid = false;
            this.setVisible(true);
            OpenPanRule();
        }
        /**
        * set the free monoid with simple
        * @param monGenList the list of mongen
        * @author Junli Lu
        */
        protected void SetFreeWithSimple(Vector<MonGen> monGenList)
        {
            GenSortE genSortE = null;
            if(this.genSortComboBox.getSelectedItem().toString().equals("letters")){
                genSortE = GenSortE.LETTERS;
            }else if(this.genSortComboBox.getSelectedItem().toString().equals("integer")){
                genSortE = GenSortE.INTEGER;
            }
            FreeMonoid freemonoid = new FreeMonoid(GenDescriptE.ENUM,genSortE,monGenList);
            this.SetMonoid(freemonoid);
        }
        /**
        * set the free monoid with tuple
        * @param monGenList the list of mongen
        * @param genSort the sort of mongen
        * @author Junli Lu
        */
        protected void SetFreeWithTuple(Vector<MonGen> monGenList,GenSort genSort){
            FreeMonoid freemonoid = 
                    new FreeMonoid(GenDescriptE.ENUM,genSort.getGenCompSortV().size(),genSort,monGenList);
            this.SetMonoid(freemonoid);
        }
       /**
        * set the monoid
        * @param freemonoid the structure of freemonoid
        * @author Junli Lu
        */
        protected void SetMonoid(FreeMonoid freemonoid){
            if(!isSetProductMonoid){
                SaveMonoind(freemonoid);
            }else{
                if(this.countProdDimTimes == 0){
                    this.freemonoidList = new Vector<FreeMonoid>();
                }
                this.freemonoidList.add(freemonoid);
                this.countProdDimTimes++;
                if(this.countProdDimTimes < this.prodDimTimes){
                    this.setTitle("Monoid "+ Integer.toString(this.countProdDimTimes));
                    this.setVisible(true);

                }else{
                    ProductMonoid productMonoid = new ProductMonoid(this.prodDimTimes,this.freemonoidList);
                    SaveMonoind(productMonoid);
                }
            }
        }
        /**
        * the rule to open items
        * @author Junli Lu
        */
        protected void OpenPanRule(){
            CloseAllPan();
            if(this.typeComboBox.getSelectedItem().toString().equals("free")){
                this.genKindPan.setVisible(true);                  
                if(this.genKindComboBox.getSelectedItem().toString().equals("simple")){
                    this.genSortPan.setVisible(true);
                }else if(this.genKindComboBox.getSelectedItem().toString().equals("tuple")){
                    this.genDimPan.setVisible(true);
                }
            }else if(this.typeComboBox.getSelectedItem().toString().equals("product")){
                this.prodDimPan.setVisible(true);
            }
        }
        /**
        * close all items
        * @author Junli Lu
        */
        protected void CloseAllPan(){
            this.genSortPan.setVisible(false);
            this.genDimPan.setVisible(false);
            this.genKindPan.setVisible(false);
            this.prodDimPan.setVisible(false);
        }
        /**
         * actions when ok is pressed
         * @param evt key events
         */
        private void okBtnActionPerformed(java.awt.event.ActionEvent evt) {
            this.setVisible(false);
            if(this.typeComboBox.getSelectedItem().toString().equals("free")){
                if(this.genKindComboBox.getSelectedItem().toString().equals("simple")){
                    monoidMonGenDialog.Initialize(1,this.genSortComboBox.getSelectedItem().toString(),null);
                }
                else if(this.genKindComboBox.getSelectedItem().toString().equals("tuple")){
                    monoidGenSortDialog.Initialize(Integer.parseInt(this.genDimText.getText().toString()));
                }
            }
            else if(this.typeComboBox.getSelectedItem().toString().equals("product")){                
                this.prodDimTimes = Integer.parseInt(this.prodDimText.getText().toString());
                this.countProdDimTimes = 0;
                this.typeComboBox.setSelectedIndex(0);
                this.prodDimPan.setVisible(false);

                this.Initialize();
                this.setTitle("Monoid 0");
                isSetProductMonoid = true;
            }
        }
        /**
         * actions when back button is pressed
         * @param evt key events
         */
        private void backBtnActionPerformed(java.awt.event.ActionEvent evt) {
            this.setTitle("");
            this.setVisible(false);
            semiringMainDialog.Initialize();
        }
        /**
         * actions when genKind combox is changed
         * @param evt key events
         */
        private void genKindComboBoxActionPerformed(java.awt.event.ActionEvent evt) {
            OpenPanRule();
        }
       /**
         * actions when type combox is changed
         * @param evt key events
         */
        private void typeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {
            OpenPanRule();
        }

        @SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        typeComboBox = new javax.swing.JComboBox();
        okBtn = new javax.swing.JButton();
        genSortPan = new javax.swing.JPanel();
        g1 = new javax.swing.JLabel();
        genSortComboBox = new javax.swing.JComboBox();
        prodDimPan = new javax.swing.JPanel();
        g2 = new javax.swing.JLabel();
        prodDimText = new javax.swing.JLabel();
        genKindPan = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        genKindComboBox = new javax.swing.JComboBox();
        backBtn = new javax.swing.JButton();
        genDimPan = new javax.swing.JPanel();
        g = new javax.swing.JLabel();
        genDimText = new javax.swing.JLabel();

        genDimText.setText("2");
        prodDimText.setText("2");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Monoid"));

        jLabel2.setText("type");

        typeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "free", "product" }));
        typeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                typeComboBoxActionPerformed(evt);
            }
        });

        okBtn.setText("ok");
        okBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okBtnActionPerformed(evt);
            }
        });

        g1.setText("genSort");

        genSortComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "letters", "integer" }));

        javax.swing.GroupLayout genSortPanLayout = new javax.swing.GroupLayout(genSortPan);
        genSortPan.setLayout(genSortPanLayout);
        genSortPanLayout.setHorizontalGroup(
            genSortPanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(genSortPanLayout.createSequentialGroup()
                .addComponent(g1)
                .addGap(18, 18, 18)
                .addComponent(genSortComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        genSortPanLayout.setVerticalGroup(
            genSortPanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, genSortPanLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(genSortPanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(g1)
                    .addComponent(genSortComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(146, 146, 146))
        );

        g2.setText("prodDim");

        javax.swing.GroupLayout prodDimPanLayout = new javax.swing.GroupLayout(prodDimPan);
        prodDimPan.setLayout(prodDimPanLayout);
        prodDimPanLayout.setHorizontalGroup(
            prodDimPanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, prodDimPanLayout.createSequentialGroup()
                .addComponent(g2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(prodDimText, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
        );
        prodDimPanLayout.setVerticalGroup(
            prodDimPanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, prodDimPanLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(prodDimPanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(g2)
                    .addComponent(prodDimText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jLabel3.setText("genKind");

        genKindComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "simple", "tuple" }));
        genKindComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genKindComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout genKindPanLayout = new javax.swing.GroupLayout(genKindPan);
        genKindPan.setLayout(genKindPanLayout);
        genKindPanLayout.setHorizontalGroup(
            genKindPanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(genKindPanLayout.createSequentialGroup()
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(genKindComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(23, Short.MAX_VALUE))
        );
        genKindPanLayout.setVerticalGroup(
            genKindPanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(genKindPanLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(genKindPanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(genKindComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        backBtn.setText("back");
        backBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backBtnActionPerformed(evt);
            }
        });

        g.setText("genDim");

        javax.swing.GroupLayout genDimPanLayout = new javax.swing.GroupLayout(genDimPan);
        genDimPan.setLayout(genDimPanLayout);
        genDimPanLayout.setHorizontalGroup(
            genDimPanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(genDimPanLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(g)
                .addGap(18, 18, 18)
                .addComponent(genDimText, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        genDimPanLayout.setVerticalGroup(
            genDimPanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(genDimPanLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(genDimPanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(genDimText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(g))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(genSortPan, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(38, 38, 38)
                        .addComponent(typeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(genKindPan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(prodDimPan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(genDimPan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(okBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(backBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(23, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(typeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(genKindPan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(prodDimPan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(genSortPan, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(genDimPan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(backBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 317, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>
        // Variables declaration - do not modify
        private javax.swing.JLabel g;
        private javax.swing.JLabel g1;
        private javax.swing.JLabel g2;
        private javax.swing.JPanel genDimPan;
        private javax.swing.JLabel genDimText;
        private javax.swing.JComboBox genKindComboBox;
        private javax.swing.JPanel genKindPan;
        private javax.swing.JComboBox genSortComboBox;
        private javax.swing.JPanel genSortPan;
        private javax.swing.JLabel jLabel2;
        private javax.swing.JLabel jLabel3;
        private javax.swing.JPanel jPanel2;
        private javax.swing.JButton okBtn;
        private javax.swing.JPanel prodDimPan;
        private javax.swing.JLabel prodDimText;
        private javax.swing.JComboBox typeComboBox;
        private javax.swing.JButton backBtn;
        // End of variables declaration
    }
    /**
     * set the gen sort dialog
     */
    public class MonoidGenSortDialog extends javax.swing.JDialog {
        /** the dimension fo gensort */
        protected int genDimTimes;
        /** a variable to count the number of gensort*/
        protected int countGenDimTimes;
        /** a list of gencompsort */
        public Vector<GenCompSort> genSortList;

        /**
         * initialize components
         * @param parent the main frame
         * @param modal the status of the dialog
         */
        public MonoidGenSortDialog(java.awt.Frame parent, boolean modal) {
            super(parent, modal);
            initComponents();
            
        }
        /**
         * initialization
         * @param genDimTimes the dimension fo gensort
         */
        public void Initialize(int genDimTimes){
            this.setLocationRelativeTo(parent);
            this.genDimTimes = genDimTimes;
            this.genSortList = new Vector<GenCompSort>();
            this.countGenDimTimes = 0;
            this.setVisible(true);
            this.setTitle("GenComSort 0");
        }
        /**
         * actions when ok button is pressed
         * @param evt key events
         */
        private void okBtnActionPerformed(java.awt.event.ActionEvent evt) {         
            GenCompSort.ValueE value = null;
            if(this.genComSortComboBox.getSelectedItem().toString().equals("letters")){
                value = GenCompSort.ValueE.LETTERS;
            }else if(this.genComSortComboBox.getSelectedItem().toString().equals("integer")){
                value = GenCompSort.ValueE.INTEGER;
            }
            GenCompSort genCompSort = new GenCompSort(value);
            this.genSortList.add(genCompSort);
            this.countGenDimTimes++;
            this.setTitle("GenComSort "+this.genSortList.size());

            if(this.countGenDimTimes == this.genDimTimes){
                this.setVisible(false);
                monoidMonGenDialog.Initialize(this.genDimTimes,null,this.genSortList);
            }
        }
        /**
         * actions when back button is pressed
         * @param evt key events
         */
        private void backBtnActionPerformed(java.awt.event.ActionEvent evt) {
            this.setVisible(false);
            monoidMainDialog.Initialize();
        }
        @SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated Code">
        private void initComponents() {

            jPanel1 = new javax.swing.JPanel();
            jLabel3 = new javax.swing.JLabel();
            genComSortComboBox = new javax.swing.JComboBox();
            okBtn = new javax.swing.JButton();
            backBtn = new javax.swing.JButton();

            setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

            jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("genSort"));

            jLabel3.setText("genCompSort");

            genComSortComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "letters", "integer" }));

            javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
            jPanel1.setLayout(jPanel1Layout);
            jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel3)
                    .addGap(18, 18, 18)
                    .addComponent(genComSortComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(genComSortComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );

            okBtn.setText("ok");
            okBtn.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    okBtnActionPerformed(evt);
                }
            });

            backBtn.setText("back");
            backBtn.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    backBtnActionPerformed(evt);
                }
            });

            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
            getContentPane().setLayout(layout);
            layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(okBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 80, Short.MAX_VALUE)
                            .addComponent(backBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap())
            );
            layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(okBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(backBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap())
            );

            pack();
        }// </editor-fold>
        // Variables declaration - do not modify
        private javax.swing.JComboBox genComSortComboBox;
        private javax.swing.JLabel jLabel3;
        private javax.swing.JPanel jPanel1;
        private javax.swing.JButton okBtn;
        private javax.swing.JButton backBtn;
        // End of variables declaration
    }
    /**
     * the dialog to set mongen
     */
    public class MonoidMonGenDialog extends javax.swing.JDialog {
        /** the demsion of gensort */
        protected int genDimTimes;
        /** a variable to count the number of gensort */
        protected int countGenDimTimes;
        /** gensort string */
        protected String genSortText;
        /** the panel in use */
        protected javax.swing.JPanel usingPanel;
        /** a list of gencompsort */
        protected Vector<GenCompSort> genSortList;
        /** a list of mongen */
        protected Vector<MonGen> monGenList;
        /** a list of moncompgen */
        protected Vector<MonCompGen> monCompGenVList;

        /**
         * initialize components
         * @param parent the main frame
         * @param modal the status of the dialog
         */
        public MonoidMonGenDialog(java.awt.Frame parent, boolean modal) {
            super(parent, modal);
            initComponents();
        }
        /**
         * initialization
         * @param genDimTimes the demsion of gensort
         * @param genSortText gensort string
         * @param genSortList the list of gencompsort
         */
        public void Initialize(int genDimTimes,String genSortText,Vector<GenCompSort> genSortList){
            this.setLocationRelativeTo(parent);
            this.genDimTimes = genDimTimes;
            this.genSortList = genSortList;
            this.genSortText = genSortText;
            this.monGenList = new Vector<MonGen>();
            this.countGenDimTimes = 0;
            this.setVisible(true);
            if(this.genDimTimes == 1){
                this.usingPanel = this.monGenPan;
                this.monComGenPan.setVisible(false);                
            }else{
                this.usingPanel = this.monComGenPan;
                this.monGenPan.setVisible(false);
            }
            this.SetView();
            this.usingPanel.setVisible(true);
            this.okBtn.setEnabled(true);
            this.finishBtn.setEnabled(false);
            this.newMonGenBtn.setEnabled(false);
        }
        /**
        * set the mongen
        * @author Junli Lu
        */
        protected void SetMonGen(){
            MonGen monGen = new MonGen(this.monGenText.getText());
            this.monGenList.add(monGen);
        }
        /**
        * set moncomgen
        * @author Junli Lu
        */
        protected void SetMonComGen(){
            if(this.countGenDimTimes == 0){
                this.monCompGenVList = new Vector<MonCompGen>();
            }
            MonCompGen monCompGen = new MonCompGen(this.monComGenText.getText());
            this.monCompGenVList.add(monCompGen);
            this.countGenDimTimes++;
        }
        /**
         * actions when ok button is pressed
         * @param evt key events
         */
        private void okBtnActionPerformed(java.awt.event.ActionEvent evt) {
            if(this.genDimTimes == 1){
                SetMonGen();
                this.usingPanel.setVisible(false);
                this.okBtn.setEnabled(false);
                this.newMonGenBtn.setEnabled(true);
                this.finishBtn.setEnabled(true);
            }else{
                SetMonComGen();    
                if(this.countGenDimTimes == this.genDimTimes){
                    MonGen monGen = new MonGen(this.monCompGenVList);
                    this.monGenList.add(monGen);
                    this.countGenDimTimes = 0;
                    this.usingPanel.setVisible(false);
                    this.okBtn.setEnabled(false);
                    this.newMonGenBtn.setEnabled(true);
                    this.finishBtn.setEnabled(true);
                }
            }
            this.SetView();
        }
        /**
         * set the view of the dialog
         */
        private void SetView(){        
            this.setTitle("monGen "+this.monGenList.size());
            if(this.genDimTimes == 1){
                this.monGenHintLabel.setText("input "+this.genSortText);
                if(this.genSortText.equals("letters"))
                    this.monGenText.setText("a");
                else if(this.genSortText.equals("integer"))
                    this.monGenText.setText("1");
            }else {
                this.monComGenLabel.setText( "monComGen " + Integer.toString(this.countGenDimTimes));
                this.monComGenHintLabel.setText("input "+this.genSortList.get(this.countGenDimTimes).getValueE().toString());
                if(this.genSortList.get(this.countGenDimTimes).getValueE().toString().equals("LETTERS"))
                    this.monComGenText.setText("a");
                else if(this.genSortList.get(this.countGenDimTimes).getValueE().toString().equals("INTEGER"))
                    this.monComGenText.setText("1");
            }
        }
        /**
         * actions when newMonGen button is pressed
         * @param evt key events
         */
        private void newMonGenBtnActionPerformed(java.awt.event.ActionEvent evt) {
             this.usingPanel.setVisible(true);
             this.okBtn.setEnabled(true);
             this.newMonGenBtn.setEnabled(false);
        }
        /**
         * actions when finish button is pressed
         * @param evt key events
         */
        private void finishBtnActionPerformed(java.awt.event.ActionEvent evt) {
            this.setVisible(false);
            if(this.genDimTimes == 1){
                 monoidMainDialog.SetFreeWithSimple(this.monGenList);
            }else{
                 monoidMainDialog.SetFreeWithTuple(this.monGenList, new GenSort(this.genSortList));
            }
        }
        /**
         * actions when back button is pressed
         * @param evt key events
         */
        private void backBtnActionPerformed(java.awt.event.ActionEvent evt) {
             this.setVisible(false);
             monoidMainDialog.Initialize();    
        }
        /**
         * actions when mongen text is typed
         * @param evt key events
         */
        private void monGenTextKeyTyped(java.awt.event.KeyEvent evt) {
            this.monGenText.setText("");
        }
        /**
         * actions when mongen text is released
         * @param evt key events
         */
        private void monGenTextKeyReleased(java.awt.event.KeyEvent evt) {
            if(this.monGenText.getText().length() == 1) {
                char c = this.monGenText.getText().charAt(0);
                if(this.genSortText.equals("letters")) {
                    if(!((c <= 57 && c >= 48) || (c <= 122 && c >= 97))) {
                        monGenText.setText("");
                    }
                }else if(this.genSortText.equals("integer")) {
                    if(!(c <= 57 && c >= 48)) {
                        monGenText.setText("");
                    }
                }
            }
        }
        /**
         * actions when monComGen text is typed
         * @param evt key events
         */
        private void monComGenTextKeyTyped(java.awt.event.KeyEvent evt) {
            this.monComGenText.setText("");
        }
        /**
         * actions when monComGen text is released
         * @param evt key events
         */
        private void monComGenTextKeyReleased(java.awt.event.KeyEvent evt) {
            if(monComGenText.getText().length() == 1) {
                char c = monComGenText.getText().charAt(0);
                if(this.genSortList.get(this.countGenDimTimes).getValueE().toString().equals("LETTERS")) {
                    if(!((c <= 57 && c >= 48) || (c <= 122 && c >= 97))) {
                        this.monComGenText.setText("");
                    }
                }else if(this.genSortList.get(this.countGenDimTimes).getValueE().toString().equals("INTEGER")) {
                    if(!(c <= 57 && c >= 48)) {
                        this.monComGenText.setText("");
                    }
                }
            }
        }
        @SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated Code">
        private void initComponents() {

        okBtn = new javax.swing.JButton();
        newMonGenBtn = new javax.swing.JButton();
        monGenPan = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        monGenText = new javax.swing.JTextField();
        monGenHintLabel = new javax.swing.JLabel();
        backBtn = new javax.swing.JButton();
        monComGenPan = new javax.swing.JPanel();
        monComGenLabel = new javax.swing.JLabel();
        monComGenText = new javax.swing.JTextField();
        monComGenHintLabel = new javax.swing.JLabel();
        finishBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        okBtn.setText("ok");
        okBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okBtnActionPerformed(evt);
            }
        });

        newMonGenBtn.setText("new monGen");
        newMonGenBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newMonGenBtnActionPerformed(evt);
            }
        });

        monGenPan.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        jLabel5.setText("monGen");

        monGenHintLabel.setText("jLabel1");

        javax.swing.GroupLayout monGenPanLayout = new javax.swing.GroupLayout(monGenPan);
        monGenPan.setLayout(monGenPanLayout);
        monGenPanLayout.setHorizontalGroup(
            monGenPanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(monGenPanLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addGap(26, 26, 26)
                .addComponent(monGenText, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(monGenHintLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
                .addContainerGap())
        );
        monGenPanLayout.setVerticalGroup(
            monGenPanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(monGenPanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel5)
                .addComponent(monGenText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(monGenHintLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        backBtn.setText("back");
        backBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backBtnActionPerformed(evt);
            }
        });

        monComGenPan.setBorder(javax.swing.BorderFactory.createTitledBorder("monGen"));

        monComGenLabel.setText("monComGen");

        monComGenHintLabel.setText("jLabel1");

        javax.swing.GroupLayout monComGenPanLayout = new javax.swing.GroupLayout(monComGenPan);
        monComGenPan.setLayout(monComGenPanLayout);
        monComGenPanLayout.setHorizontalGroup(
            monComGenPanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(monComGenPanLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(monComGenLabel)
                .addGap(18, 18, 18)
                .addComponent(monComGenText, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(monComGenHintLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
                .addContainerGap())
        );
        monComGenPanLayout.setVerticalGroup(
            monComGenPanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(monComGenPanLayout.createSequentialGroup()
                .addGroup(monComGenPanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(monComGenLabel)
                    .addComponent(monComGenText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(monComGenHintLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        finishBtn.setText("finish setting");
        finishBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                finishBtnActionPerformed(evt);
            }
        });

        monGenText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                monGenTextKeyTyped(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                monGenTextKeyReleased(evt);
            }
        });

        monComGenText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                monComGenTextKeyTyped(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                monComGenTextKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(monGenPan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(okBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(newMonGenBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(finishBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(backBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(monComGenPan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(monGenPan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(monComGenPan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okBtn)
                    .addComponent(newMonGenBtn)
                    .addComponent(backBtn)
                    .addComponent(finishBtn))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>
        // Variables declaration - do not modify
        private javax.swing.JButton backBtn;
        private javax.swing.JButton finishBtn;
        private javax.swing.JLabel jLabel5;
        private javax.swing.JLabel monComGenHintLabel;
        private javax.swing.JLabel monComGenLabel;
        private javax.swing.JPanel monComGenPan;
        private javax.swing.JTextField monComGenText;
        private javax.swing.JLabel monGenHintLabel;
        private javax.swing.JPanel monGenPan;
        private javax.swing.JTextField monGenText;
        private javax.swing.JButton newMonGenBtn;
        private javax.swing.JButton okBtn;
    }
}
