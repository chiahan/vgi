
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * VGI.java
 *
 * Created on Nov 30, 2011, 1:08:01 PM
 */
/**
 *
 * @author JLiu
 */
public class VGI extends javax.swing.JFrame {

    private VGI pmVGI;
    private TAFKit pmTAFKit;
    private TAFKitInterface.AutomataType pmAutomataType;

    private class AlgorithmMenuItemActionListener implements java.awt.event.ActionListener {

        public TAFKitInterface.VcsnAlgorithm vcsnAlgorithm;

        public AlgorithmMenuItemActionListener(TAFKitInterface.VcsnAlgorithm vcsnAlgorithm) {
            if (vcsnAlgorithm == null) {
                throw new IllegalArgumentException("Input argument vcsnAlgorithm cannot be null!");
            }
            this.vcsnAlgorithm = vcsnAlgorithm;
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {

            ArrayList<Object> inputs = new ArrayList<Object>();

            for (int index = 0; index < this.vcsnAlgorithm.inputsInfo.size(); index++) {
                TAFKitInterface.VcsnAlgorithm.IoInfo inputInfo = this.vcsnAlgorithm.inputsInfo.get(index);
                switch (inputInfo.type) {

                    case AUTOMATON:
                        JFileChooser fileChooser = new JFileChooser();
                        fileChooser.setDialogTitle("Please choose a FSM XML file");
                        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                        fileChooser.setMultiSelectionEnabled(false);
                        fileChooser.setFileFilter(new FileFilter() {

                            @Override
                            public boolean accept(File file) {
                                return ((file.getName().toLowerCase().endsWith(".xml"))
                                        || (file.isDirectory()));
                            }

                            @Override
                            public String getDescription() {
                                return "FSM XML file";
                            }
                        });
                        int returnValue = fileChooser.showOpenDialog(pmVGI);
                        if (returnValue != JFileChooser.APPROVE_OPTION) {
                            return;
                        }
                        inputs.add(fileChooser.getSelectedFile());
                        break;

                    case BOOLEAN:
                        throw new IllegalArgumentException("The " + (index + 1) + "th argument is boolean, but no argument should be boolean.");

                    case INTEGER:
                        String string = JOptionPane.showInputDialog("Please input an integer.");
                        if ((string == null) || (string.isEmpty())) {
                            return;
                        }
                        inputs.add(Integer.decode(string));
                        break;

                    case REGULAR_EXPRESSION:
                        string = JOptionPane.showInputDialog("Please input a regular expression.");
                        if ((string == null) || (string.isEmpty())) {
                            return;
                        }
                        inputs.add(string);
                        break;

                    case TEXT:
                        throw new IllegalArgumentException("The " + (index + 1) + "th argument is text, but no argument should be text.");

                    case WEIGHT:
                        string = JOptionPane.showInputDialog("Please input a weight value.");
                        if ((string == null) || (string.isEmpty())) {
                            return;
                        }
                        inputs.add(string);
                        break;

                    case WORD:
                        string = JOptionPane.showInputDialog("Please input a word.");
                        if ((string == null) || (string.isEmpty())) {
                            return;
                        }
                        inputs.add(string);
                        break;

                    default:
                        break;

                }  // End switch (inputInfo.type)
            }  // End for (int index = 0; index < this.vcsnAlgorithm.inputsInfo.size(); index++)

            List<Object> outputs;
            try {
                outputs = pmTAFKit.runVcsnAlgorithm(pmAutomataType, this.vcsnAlgorithm, inputs);
            } catch (Exception exception) {
                exception.printStackTrace();
                return;
            }

            for (int index = 0; index < this.vcsnAlgorithm.outputsInfo.size(); index++) {

                TAFKitInterface.VcsnAlgorithm.IoInfo outputInfo = this.vcsnAlgorithm.outputsInfo.get(index);
                Object object = outputs.get(index);

                switch (outputInfo.type) {

                    case AUTOMATON:
                        // TODO:  Convert stream to a FSM XML file and open it in VGI.
                        JOptionPane.showMessageDialog(
                                pmVGI,
                                "Output of \"" + this.vcsnAlgorithm.name + "\" is an automaton, but we cannot display it yet.",
                                null,
                                JOptionPane.INFORMATION_MESSAGE);
                        break;

                    case BOOLEAN:
                        if (object instanceof Boolean) {
                            Boolean bool = (Boolean) object;
                            JOptionPane.showMessageDialog(
                                    pmVGI,
                                    "Output of \"" + this.vcsnAlgorithm.name + "\" is:\n" + bool,
                                    null,
                                    JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            throw new IllegalArgumentException("The " + (index + 1) + "th output should be an integer, but it is not!");
                        }
                        break;

                    case INTEGER:
                        if (object instanceof Integer) {
                            Integer integer = (Integer) object;
                            JOptionPane.showMessageDialog(
                                    pmVGI,
                                    "Output of \"" + this.vcsnAlgorithm.name + "\" is:\n" + integer,
                                    null,
                                    JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            throw new IllegalArgumentException("The " + (index + 1) + "th output should be an integer, but it is not!");
                        }
                        break;

                    case REGULAR_EXPRESSION:
                        if (object instanceof String) {
                            String string = (String) object;
                            JOptionPane.showMessageDialog(
                                    pmVGI,
                                    "Output of \"" + this.vcsnAlgorithm.name + "\" is:\n" + string,
                                    null,
                                    JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            throw new IllegalArgumentException("The " + (index + 1) + "th output should be a regular expression, but it is not!");
                        }
                        break;

                    case TEXT:
                        if (object instanceof String) {
                            String string = (String) object;
                            JOptionPane.showMessageDialog(
                                    pmVGI,
                                    "Output of \"" + this.vcsnAlgorithm.name + "\" is:\n" + string,
                                    null,
                                    JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            throw new IllegalArgumentException("The " + (index + 1) + "th output should be text, but it is not!");
                        }
                        break;

                    case WEIGHT:
                        throw new UnsupportedOperationException("The " + (index + 1) + "th output should be a weight, but VGI does not support weight outputs yet!");

                    case WORD:
                        if (object instanceof String) {
                            String string = (String) object;
                            JOptionPane.showMessageDialog(
                                    pmVGI,
                                    "Output of \"" + this.vcsnAlgorithm.name + "\" is:\n" + string,
                                    null,
                                    JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            throw new IllegalArgumentException("The " + (index + 1) + "th output should be a word, but it is not!");
                        }
                        break;

                    default:
                        throw new IllegalArgumentException("The " + (index + 1) + "th output does not have a recognizable type.");

                }  // End switch (outputInfo.type)

            }  // End for (int index = 0; index < this.vcsnAlgorithm.outputsInfo.size(); index++)

        }  // End public void actionPerformed(ActionEvent actionEvent)
    }  // End private class AlgorithmMenuItemActionListener implements java.awt.event.ActionListener

    /** Creates new form VGI */
    public VGI() {
        pmVGI = this;
        pmTAFKit = null;
        pmAutomataType = new TAFKitInterface.AutomataType(
				TAFKitInterface.AutomataType.Semiring.B_BOOLEAN,
				TAFKitInterface.AutomataType.AlphabetDataType.CHAR,
				null,
				false);
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        toolBar = new javax.swing.JToolBar();
        addStateButton = new javax.swing.JButton();
        addTransitionButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        toolBarSeparator1 = new javax.swing.JToolBar.Separator();
        fitWindowButton = new javax.swing.JButton();
        zoomInButton = new javax.swing.JButton();
        zoomOutButton = new javax.swing.JButton();
        actualSizeButton = new javax.swing.JButton();
        toolBarSeparator2 = new javax.swing.JToolBar.Separator();
        setAsInitialStateButton = new javax.swing.JButton();
        setAsFinalStateButton = new javax.swing.JButton();
        mainPanel = new javax.swing.JPanel();
        statusLabel = new javax.swing.JLabel();
        mainSplitPane = new javax.swing.JSplitPane();
        drawPanel = new javax.swing.JPanel();
        infoPanel = new javax.swing.JPanel();
        tableScrollPane = new javax.swing.JScrollPane();
        infoTable1 = new javax.swing.JTable();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        newMenuItem = new javax.swing.JMenuItem();
        openMenuItem = new javax.swing.JMenuItem();
        closeMenuItem = new javax.swing.JMenuItem();
        saveMenuItem = new javax.swing.JMenuItem();
        saveAsMenuItem = new javax.swing.JMenuItem();
        fileMenuSeparator1 = new javax.swing.JPopupMenu.Separator();
        exitMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        deleteMenuItem = new javax.swing.JMenuItem();
        editMenuSeparator1 = new javax.swing.JPopupMenu.Separator();
        setAsInitialStateMenuItem = new javax.swing.JMenuItem();
        setAsFinalStateMenuItem = new javax.swing.JMenuItem();
        viewMenu = new javax.swing.JMenu();
        fitWindowMenuItem = new javax.swing.JMenuItem();
        zoomInMenuItem = new javax.swing.JMenuItem();
        zoomOutMenuItem = new javax.swing.JMenuItem();
        actualSizeMenuItem = new javax.swing.JMenuItem();
        viewMenuSeparator1 = new javax.swing.JPopupMenu.Separator();
        displayAutomataPropertiesCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        displayStatusBarCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        algorithmsMenu = new javax.swing.JMenu();
        setTAFKitPathMenuItem = new javax.swing.JMenuItem();
        currentSettingMenuItem = new javax.swing.JMenuItem();
        algorithmsMenuSeparator1 = new javax.swing.JPopupMenu.Separator();
        helpMenu = new javax.swing.JMenu();
        rationalExpressionSymbolsMenuItem = new javax.swing.JMenuItem();
        aboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("VGI");

        toolBar.setFloatable(false);
        toolBar.setRollover(true);

        addStateButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/yellow/24/add.png"))); // NOI18N
        addStateButton.setFocusable(false);
        addStateButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addStateButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(addStateButton);

        addTransitionButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/yellow/24/arrow_right.png"))); // NOI18N
        addTransitionButton.setFocusable(false);
        addTransitionButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addTransitionButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(addTransitionButton);

        deleteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/yellow/24/close.png"))); // NOI18N
        deleteButton.setFocusable(false);
        deleteButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        deleteButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(deleteButton);
        toolBar.add(toolBarSeparator1);

        fitWindowButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/yellow/24/zoom.png"))); // NOI18N
        fitWindowButton.setFocusable(false);
        fitWindowButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        fitWindowButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(fitWindowButton);

        zoomInButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/yellow/24/zoomin.png"))); // NOI18N
        zoomInButton.setFocusable(false);
        zoomInButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        zoomInButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(zoomInButton);

        zoomOutButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/yellow/24/zoomout.png"))); // NOI18N
        zoomOutButton.setFocusable(false);
        zoomOutButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        zoomOutButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(zoomOutButton);

        actualSizeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/yellow/24/refresh.png"))); // NOI18N
        actualSizeButton.setFocusable(false);
        actualSizeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        actualSizeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(actualSizeButton);
        toolBar.add(toolBarSeparator2);

        setAsInitialStateButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/yellow/24/object_07.png"))); // NOI18N
        setAsInitialStateButton.setFocusable(false);
        setAsInitialStateButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        setAsInitialStateButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(setAsInitialStateButton);

        setAsFinalStateButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/yellow/24/object_06.png"))); // NOI18N
        setAsFinalStateButton.setFocusable(false);
        setAsFinalStateButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        setAsFinalStateButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(setAsFinalStateButton);

        getContentPane().add(toolBar, java.awt.BorderLayout.NORTH);

        mainPanel.setLayout(new java.awt.BorderLayout());

        statusLabel.setText("status:");
        mainPanel.add(statusLabel, java.awt.BorderLayout.SOUTH);

        mainSplitPane.setDividerSize(20);

        drawPanel.setPreferredSize(new java.awt.Dimension(800, 600));

        org.jdesktop.layout.GroupLayout drawPanelLayout = new org.jdesktop.layout.GroupLayout(drawPanel);
        drawPanel.setLayout(drawPanelLayout);
        drawPanelLayout.setHorizontalGroup(
            drawPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 899, Short.MAX_VALUE)
        );
        drawPanelLayout.setVerticalGroup(
            drawPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 776, Short.MAX_VALUE)
        );

        mainSplitPane.setLeftComponent(drawPanel);

        infoTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Type", "Value"
            }
        ));
        infoTable1.setEnabled(false);
        tableScrollPane.setViewportView(infoTable1);

        org.jdesktop.layout.GroupLayout infoPanelLayout = new org.jdesktop.layout.GroupLayout(infoPanel);
        infoPanel.setLayout(infoPanelLayout);
        infoPanelLayout.setHorizontalGroup(
            infoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 510, Short.MAX_VALUE)
            .add(infoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(infoPanelLayout.createSequentialGroup()
                    .add(12, 12, 12)
                    .add(tableScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 375, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(123, Short.MAX_VALUE)))
        );
        infoPanelLayout.setVerticalGroup(
            infoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 776, Short.MAX_VALUE)
            .add(infoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(infoPanelLayout.createSequentialGroup()
                    .add(0, 0, 0)
                    .add(tableScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 267, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(509, Short.MAX_VALUE)))
        );

        mainSplitPane.setRightComponent(infoPanel);

        mainPanel.add(mainSplitPane, java.awt.BorderLayout.CENTER);

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        fileMenu.setMnemonic('F');
        fileMenu.setText("File");

        newMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        newMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/yellow/18/Forward.png"))); // NOI18N
        newMenuItem.setText("New");
        newMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(newMenuItem);

        openMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        openMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/yellow/18/open.png"))); // NOI18N
        openMenuItem.setText("Open...");
        fileMenu.add(openMenuItem);

        closeMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_MASK));
        closeMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/yellow/18/stop.png"))); // NOI18N
        closeMenuItem.setText("Close");
        fileMenu.add(closeMenuItem);

        saveMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        saveMenuItem.setText("Save");
        fileMenu.add(saveMenuItem);

        saveAsMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        saveAsMenuItem.setText("Save As...");
        fileMenu.add(saveAsMenuItem);
        fileMenu.add(fileMenuSeparator1);

        exitMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/yellow/18/door.png"))); // NOI18N
        exitMenuItem.setText("Exit");
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        editMenu.setMnemonic('E');
        editMenu.setText("Edit");

        deleteMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        deleteMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/yellow/18/close.png"))); // NOI18N
        deleteMenuItem.setText("Delete");
        editMenu.add(deleteMenuItem);
        editMenu.add(editMenuSeparator1);

        setAsInitialStateMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/yellow/18/object_07.png"))); // NOI18N
        setAsInitialStateMenuItem.setText("Set As Initial State");
        editMenu.add(setAsInitialStateMenuItem);

        setAsFinalStateMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/yellow/18/object_06.png"))); // NOI18N
        setAsFinalStateMenuItem.setText("Set As Final State");
        editMenu.add(setAsFinalStateMenuItem);

        menuBar.add(editMenu);

        viewMenu.setMnemonic('V');
        viewMenu.setText("View");

        fitWindowMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/yellow/18/zoom.png"))); // NOI18N
        fitWindowMenuItem.setText("Fit Window");
        viewMenu.add(fitWindowMenuItem);

        zoomInMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/yellow/18/zoomin.png"))); // NOI18N
        zoomInMenuItem.setText("Zoom In");
        viewMenu.add(zoomInMenuItem);

        zoomOutMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/yellow/18/zoomout.png"))); // NOI18N
        zoomOutMenuItem.setText("Zoom Out");
        viewMenu.add(zoomOutMenuItem);

        actualSizeMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/yellow/18/refresh.png"))); // NOI18N
        actualSizeMenuItem.setText("Actual Size");
        viewMenu.add(actualSizeMenuItem);
        viewMenu.add(viewMenuSeparator1);

        displayAutomataPropertiesCheckBoxMenuItem.setSelected(true);
        displayAutomataPropertiesCheckBoxMenuItem.setText("Display Automata Properties");
        viewMenu.add(displayAutomataPropertiesCheckBoxMenuItem);

        displayStatusBarCheckBoxMenuItem.setSelected(true);
        displayStatusBarCheckBoxMenuItem.setText("Display Status Bar");
        viewMenu.add(displayStatusBarCheckBoxMenuItem);

        menuBar.add(viewMenu);

        algorithmsMenu.setMnemonic('A');
        algorithmsMenu.setText("Algorithms");

        setTAFKitPathMenuItem.setText("Set TAF-Kit Path...");
        setTAFKitPathMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setTAFKitPathMenuItemActionPerformed(evt);
            }
        });
        algorithmsMenu.add(setTAFKitPathMenuItem);

        currentSettingMenuItem.setText("Current setting:  N/A");
        currentSettingMenuItem.setEnabled(false);
        algorithmsMenu.add(currentSettingMenuItem);
        algorithmsMenu.add(algorithmsMenuSeparator1);

        menuBar.add(algorithmsMenu);

        helpMenu.setMnemonic('H');
        helpMenu.setText("Help");

        rationalExpressionSymbolsMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/yellow/18/question.png"))); // NOI18N
        rationalExpressionSymbolsMenuItem.setText("Rational Expression Symbols");
        helpMenu.add(rationalExpressionSymbolsMenuItem);

        aboutMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons/yellow/18/about.png"))); // NOI18N
        aboutMenuItem.setText("About");
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void setTAFKitPathMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setTAFKitPathMenuItemActionPerformed

        JFileChooser tafKitPathChooser = new JFileChooser();
        if (pmTAFKit != null) {
            tafKitPathChooser.setCurrentDirectory(pmTAFKit.getTafKitPath());
        }
        tafKitPathChooser.setDialogTitle(this.setTAFKitPathMenuItem.getText());
        tafKitPathChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        tafKitPathChooser.setMultiSelectionEnabled(false);

        while (true) {

            int returnValue = tafKitPathChooser.showOpenDialog(this);
            if (returnValue != JFileChooser.APPROVE_OPTION) {
                break;
            }

            try {
                pmTAFKit = new TAFKit(tafKitPathChooser.getSelectedFile());
                break;
            } catch (IllegalArgumentException illegalArgumentException) {
                returnValue = JOptionPane.showConfirmDialog(
                        this,
                        illegalArgumentException.getMessage() + "\nDo you want to try setting TAF-Kit path again?",
                        "Invalid TAF-Kit path",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (returnValue != JOptionPane.YES_OPTION) {
                    break;
                }
            }

        }  // End while (true)

        if (pmTAFKit != null) {
            this.currentSettingMenuItem.setText("Current setting:  " + pmTAFKit.getTafKitPath());
            this.updateAlgorithmMenuItems();
        }  // End if (pmTAFKit != null)

    }//GEN-LAST:event_setTAFKitPathMenuItemActionPerformed

    private void newMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newMenuItemActionPerformed
        // TODO add your handling code here:
        CreateAutomataDialog createDialog = new CreateAutomataDialog(this, true);
        createDialog.setVisible(true);
        Monoid monoid = createDialog.getMonoid();
        Semiring semiring = createDialog.getSemiring();
        Automata automata = new Automata();
        System.out.println("Create automata from 'NewMenuItem'");

        /* Create draw panel for new automata */
        mxGraph graph = new mxGraph();
        mxGraphComponent graphComponent = new mxGraphComponent(graph);
//        this.drawPanel = new DrawPanel(graphComponent);
        this.drawPanel = new DrawJgraphXPanel(graphComponent);
        this.mainSplitPane.setLeftComponent(this.drawPanel);
        this.validate();
        System.out.println("adding DrawPanel is done! ");
    }//GEN-LAST:event_newMenuItemActionPerformed

    private void updateAlgorithmMenuItems() {

        if (pmAutomataType == null) {
            return;
        }

        List<TAFKitInterface.VcsnAlgorithm> vcsnAlgorithms = null;

        try {
            vcsnAlgorithms = pmTAFKit.listVcsnAlgorithms(pmAutomataType);
        } catch (FileNotFoundException fileNotFoundException) {
            if (this.algorithmsMenu.getMenuComponentCount() > 3) {
                this.algorithmsMenu.removeAll();
                this.algorithmsMenu.add(this.setTAFKitPathMenuItem);
                this.algorithmsMenu.add(this.currentSettingMenuItem);
                this.algorithmsMenu.add(this.algorithmsMenuSeparator1);
            }
            JMenuItem menuItem = new JMenuItem();
            menuItem.setText("TAF-Kit currently does not support this type of automata, \"" + pmAutomataType.toExecutableFileName() + "\".");
            menuItem.setEnabled(false);
            this.algorithmsMenu.add(menuItem);
            return;
        } catch (TAFKitInterface.TAFKitException tafKitException) {
            JOptionPane.showMessageDialog(
                    this,
                    tafKitException.getMessage(),
                    null,
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (this.algorithmsMenu.getMenuComponentCount() > 3) {
            this.algorithmsMenu.removeAll();
            this.algorithmsMenu.add(this.setTAFKitPathMenuItem);
            this.algorithmsMenu.add(this.currentSettingMenuItem);
            this.algorithmsMenu.add(this.algorithmsMenuSeparator1);
        }

        JMenu submenu = null;

        for (int index = 0; index < vcsnAlgorithms.size(); index++) {
            TAFKitInterface.VcsnAlgorithm vcsnAlgorithm = vcsnAlgorithms.get(index);
            JMenuItem menuItem;
            if (vcsnAlgorithm.name.equalsIgnoreCase("category")) {
                submenu = new JMenu(vcsnAlgorithm.description);
                this.algorithmsMenu.add(submenu);
            } else {
                menuItem = new JMenuItem(vcsnAlgorithm.name);
                AlgorithmMenuItemActionListener algorithmMenuItemActionListener = new AlgorithmMenuItemActionListener(vcsnAlgorithm);
                menuItem.addActionListener(algorithmMenuItemActionListener);
                submenu.add(menuItem);
            }
        }  // End for (int index = 0; index < vcsnAlgorithms.size(); index ++)
    }  // End private void updateAlgorithmMenuItems()

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VGI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VGI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VGI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VGI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new VGI().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JButton actualSizeButton;
    private javax.swing.JMenuItem actualSizeMenuItem;
    private javax.swing.JButton addStateButton;
    private javax.swing.JButton addTransitionButton;
    private javax.swing.JMenu algorithmsMenu;
    private javax.swing.JPopupMenu.Separator algorithmsMenuSeparator1;
    private javax.swing.JMenuItem closeMenuItem;
    private javax.swing.JMenuItem currentSettingMenuItem;
    private javax.swing.JButton deleteButton;
    private javax.swing.JMenuItem deleteMenuItem;
    private javax.swing.JCheckBoxMenuItem displayAutomataPropertiesCheckBoxMenuItem;
    private javax.swing.JCheckBoxMenuItem displayStatusBarCheckBoxMenuItem;
    private javax.swing.JPanel drawPanel;
    private javax.swing.JMenu editMenu;
    private javax.swing.JPopupMenu.Separator editMenuSeparator1;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JPopupMenu.Separator fileMenuSeparator1;
    private javax.swing.JButton fitWindowButton;
    private javax.swing.JMenuItem fitWindowMenuItem;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JPanel infoPanel;
    private javax.swing.JTable infoTable1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JSplitPane mainSplitPane;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem newMenuItem;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JMenuItem rationalExpressionSymbolsMenuItem;
    private javax.swing.JMenuItem saveAsMenuItem;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JButton setAsFinalStateButton;
    private javax.swing.JMenuItem setAsFinalStateMenuItem;
    private javax.swing.JButton setAsInitialStateButton;
    private javax.swing.JMenuItem setAsInitialStateMenuItem;
    private javax.swing.JMenuItem setTAFKitPathMenuItem;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JScrollPane tableScrollPane;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JToolBar.Separator toolBarSeparator1;
    private javax.swing.JToolBar.Separator toolBarSeparator2;
    private javax.swing.JMenu viewMenu;
    private javax.swing.JPopupMenu.Separator viewMenuSeparator1;
    private javax.swing.JButton zoomInButton;
    private javax.swing.JMenuItem zoomInMenuItem;
    private javax.swing.JButton zoomOutButton;
    private javax.swing.JMenuItem zoomOutMenuItem;
    // End of variables declaration//GEN-END:variables
}
