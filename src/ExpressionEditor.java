
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Highlighter;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ExpressionEditor.java
 *
 * Created on Feb 7, 2012, 2:28:06 PM
 */
/**
 *
 * @author Luser0
 */
public class ExpressionEditor extends javax.swing.JDialog {

	static final public Color HIGHLIGHT_COLOR = Color.CYAN;
	private WeightedRegularExpression expressionRoot;
	private WeightedRegularExpression selectedExpression;

	/** Creates new form ExpressionEditor */
	public ExpressionEditor(
			java.awt.Frame parent,
			boolean modal,
			AutomataInterface.Alphabet alphabet,
			AutomataInterface.Weight weight,
			AutomataInterface.WritingData writingData) {
		super(parent, modal);

		if ((alphabet == null)
				|| (weight == null)
				|| (writingData == null)) {
			throw new IllegalArgumentException("The expression argument is invalid.");
		}

		initComponents();
		WeightedRegularExpression expression = new WeightedRegularExpression.Zero();
		expression.setAlphabet(alphabet);
		expression.setWeight(weight);
		expression.setWritingData(writingData);
		this.expressionRoot = expression;
		this.expressionDisplay.setText(this.expressionRoot.toString());
		this.setSelectedExpression(this.expressionRoot.getFirstSelectedExpression());

		DefaultComboBoxModel model = new DefaultComboBoxModel(this.expressionRoot.getAlphabet().allSymbols.toArray());
//		model.insertElementAt(alphabet.identitySymbol, 0);
//		model.setSelectedItem(alphabet.identitySymbol);
		this.alphabetSymbolComboBox.setModel(model);

		if (this.expressionRoot.getWeight().semiring == TAFKitInterface.AutomataType.Semiring.B_BOOLEAN) {
			this.weightLabel.setVisible(false);
			this.weightTextField.setVisible(false);
			this.leftMultiplyButton.setVisible(false);
			this.rightMultiplyButton.setVisible(false);
		}
	}  // End public ExpressionEditor(...)

	public ExpressionEditor(
			java.awt.Frame parent,
			boolean modal,
			WeightedRegularExpression expression) {
		this(parent, modal, expression.getAlphabet(), expression.getWeight(), expression.getWritingData());
		this.expressionRoot = expression;
		this.expressionDisplay.setText(this.expressionRoot.toString());
		this.setSelectedExpression(this.expressionRoot.getFirstSelectedExpression());
	}

	public WeightedRegularExpression getExpression() {
		return this.expressionRoot;
	}

	private void setSelectedExpression(WeightedRegularExpression newExpression) {
		this.selectedExpression = newExpression;
		Highlighter highlighter = this.expressionDisplay.getHighlighter();
		DefaultHighlightPainter painter = new DefaultHighlightPainter(HIGHLIGHT_COLOR);
		WeightedRegularExpression.HighlightRange highlightRange = this.selectedExpression.getHighlightRange();
		Highlighter.Highlight existingHighlights[] = highlighter.getHighlights();

		try {
			if (existingHighlights.length < 1) {
				highlighter.addHighlight(highlightRange.start, highlightRange.end, painter);
			} else {
				highlighter.changeHighlight(existingHighlights[0], highlightRange.start, highlightRange.end);
			}
		} catch (BadLocationException ex) {
			Logger.getLogger(ExpressionEditor.class.getName()).log(Level.SEVERE, null, ex);
		}

		if ((WeightedRegularExpression.ExpressionWithOneChildExpression.class.isInstance(newExpression))
				|| (WeightedRegularExpression.ExpressionWithTwoChildrenExpressions.class.isInstance(newExpression.getParent()))) {
			this.deleteButton.setEnabled(true);
		} else {
			this.deleteButton.setEnabled(false);
		}
	}  // End public void setSelectedExpression(WeightedRegularExpression selectedExpression)

	private void insertNewExpression(WeightedRegularExpression expression) {
		WeightedRegularExpression parent = this.selectedExpression.getParent();

		if (parent == null) {

			expression.setAlphabet(this.expressionRoot.getAlphabet());
			expression.setWeight(this.expressionRoot.getWeight());
			expression.setWritingData(this.expressionRoot.getWritingData());
			this.expressionRoot = expression;

		} else {  // End if (parent == null)

			if (WeightedRegularExpression.ExpressionWithOneChildExpression.class.isInstance(parent)) {
				((WeightedRegularExpression.ExpressionWithOneChildExpression) parent).setExpression(expression);
			} else if (WeightedRegularExpression.ExpressionWithTwoChildrenExpressions.class.isInstance(parent)) {
				WeightedRegularExpression.ExpressionWithTwoChildrenExpressions parentWithTwoChildren = (WeightedRegularExpression.ExpressionWithTwoChildrenExpressions) parent;
				if (this.selectedExpression.equals(parentWithTwoChildren.getLeftExpression())) {
					parentWithTwoChildren.setLeftExpression(expression);
				} else if (this.selectedExpression.equals(parentWithTwoChildren.getRightExpression())) {
					parentWithTwoChildren.setRightExpression(expression);
				}
			}

		}  // End else part of if (parent == null)

		if (WeightedRegularExpression.ExpressionWithOneChildExpression.class.isInstance(expression)) {
			((WeightedRegularExpression.ExpressionWithOneChildExpression) expression).setExpression(this.selectedExpression);
		} else if (WeightedRegularExpression.ExpressionWithTwoChildrenExpressions.class.isInstance(expression)) {
			((WeightedRegularExpression.ExpressionWithTwoChildrenExpressions) expression).setLeftExpression(this.selectedExpression);
		}

		this.expressionDisplay.setText(this.expressionRoot.toString());

		if (WeightedRegularExpression.ExpressionWithTwoChildrenExpressions.class.isInstance(expression)) {
			this.setSelectedExpression(((WeightedRegularExpression.ExpressionWithTwoChildrenExpressions) expression).getRightExpression());
		} else {
			this.setSelectedExpression(expression);
		}
	}  // End public void insertNewExpression(WeightedRegularExpression expression)

	private Object parseWeightTextField() {

		switch (this.expressionRoot.getWeight().semiring) {
			case Z_INTEGER:
			case ZMIN_MIN_TROPICAL:
			case ZMAX_MAX_TROPICAL:
				try {
					Integer integerObj = Integer.valueOf(this.weightTextField.getText());
					this.weightTextField.setText(integerObj.toString());
					return integerObj;
				} catch (NumberFormatException numberFormatException) {
					JOptionPane.showMessageDialog(this, "Please enter an integer for weight.", null, JOptionPane.ERROR_MESSAGE);
					return null;
				}
			case Q_RATIONAL:
			case R_REAL:
				try {
					Double doubleObj = Double.valueOf(this.weightTextField.getText());
					NumberFormat numberFormat = NumberFormat.getInstance();
					numberFormat.setGroupingUsed(false);
					String string = numberFormat.format((Double) doubleObj);
					this.weightTextField.setText(string);
					return doubleObj;
				} catch (NumberFormatException numberFormatException) {
					JOptionPane.showMessageDialog(this, "Please enter a number for weight.", null, JOptionPane.ERROR_MESSAGE);
					return null;
				}
			case B_BOOLEAN:
			case F2_TWO_ELEMENT_FIELD:
				throw new IllegalArgumentException("The boolean semiring and the two-element field do not need explicitly written weights.");
			default:
				throw new IllegalArgumentException("Unrecognizable semiring set.");
		}  // End switch (this.expressionRoot.getWeight().semiring)

	}  // End private Object parseWeightTextField()

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        expressionDisplay = new javax.swing.JTextField();
        zeroButton = new javax.swing.JButton();
        oneButton = new javax.swing.JButton();
        alphabetSymbolLabel = new javax.swing.JLabel();
        alphabetSymbolComboBox = new javax.swing.JComboBox();
        weightLabel = new javax.swing.JLabel();
        weightTextField = new javax.swing.JTextField();
        sumButton = new javax.swing.JButton();
        productButton = new javax.swing.JButton();
        starButton = new javax.swing.JButton();
        leftMultiplyButton = new javax.swing.JButton();
        rightMultiplyButton = new javax.swing.JButton();
        leftArrowButton = new javax.swing.JButton();
        rightArrowButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Weighted Regular Expression Editor");

        expressionDisplay.setEditable(false);
        expressionDisplay.setFocusable(false);

        zeroButton.setText("Zero");
        zeroButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zeroButtonActionPerformed(evt);
            }
        });

        oneButton.setText("One");
        oneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                oneButtonActionPerformed(evt);
            }
        });

        alphabetSymbolLabel.setText("Alphabet Symbol:");

        alphabetSymbolComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                alphabetSymbolComboBoxActionPerformed(evt);
            }
        });

        weightLabel.setText("Weight:");

        weightTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                weightTextFieldActionPerformed(evt);
            }
        });

        sumButton.setText("Sum");
        sumButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sumButtonActionPerformed(evt);
            }
        });

        productButton.setText("Product");
        productButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                productButtonActionPerformed(evt);
            }
        });

        starButton.setText("Star");
        starButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                starButtonActionPerformed(evt);
            }
        });

        leftMultiplyButton.setText("Left Multiply");
        leftMultiplyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                leftMultiplyButtonActionPerformed(evt);
            }
        });

        rightMultiplyButton.setText("Right Multiply");
        rightMultiplyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rightMultiplyButtonActionPerformed(evt);
            }
        });

        leftArrowButton.setText("←");
        leftArrowButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                leftArrowButtonActionPerformed(evt);
            }
        });

        rightArrowButton.setText("→");
        rightArrowButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rightArrowButtonActionPerformed(evt);
            }
        });

        deleteButton.setText("Delete");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(expressionDisplay, javax.swing.GroupLayout.DEFAULT_SIZE, 543, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(zeroButton)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(oneButton))
                                    .addComponent(alphabetSymbolLabel))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(productButton)
                                    .addComponent(sumButton))))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(leftArrowButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rightArrowButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteButton)
                        .addContainerGap(392, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(weightTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
                                    .addGap(18, 18, 18))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(weightLabel)
                                    .addGap(98, 98, 98))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(alphabetSymbolComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(cancelButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(leftMultiplyButton)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addComponent(starButton)
                                    .addGap(201, 201, 201)))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(okButton)
                                .addComponent(rightMultiplyButton)))
                        .addGap(163, 163, 163))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(expressionDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(zeroButton)
                            .addComponent(oneButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(alphabetSymbolLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(alphabetSymbolComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(weightLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(weightTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(sumButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(productButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(starButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(leftMultiplyButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rightMultiplyButton)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(leftArrowButton)
                    .addComponent(rightArrowButton)
                    .addComponent(deleteButton))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	private void zeroButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zeroButtonActionPerformed
		this.insertNewExpression(new WeightedRegularExpression.Zero());
	}//GEN-LAST:event_zeroButtonActionPerformed

	private void oneButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_oneButtonActionPerformed
		this.insertNewExpression(new WeightedRegularExpression.One());
	}//GEN-LAST:event_oneButtonActionPerformed

	private void alphabetSymbolComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_alphabetSymbolComboBoxActionPerformed
		this.insertNewExpression(new WeightedRegularExpression.Atomic(this.alphabetSymbolComboBox.getSelectedItem()));
	}//GEN-LAST:event_alphabetSymbolComboBoxActionPerformed

	private void weightTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_weightTextFieldActionPerformed
		// TODO add your handling code here:
	}//GEN-LAST:event_weightTextFieldActionPerformed

	private void sumButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sumButtonActionPerformed
		this.insertNewExpression(new WeightedRegularExpression.Sum(null, new WeightedRegularExpression.Zero()));
	}//GEN-LAST:event_sumButtonActionPerformed

	private void productButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productButtonActionPerformed
		this.insertNewExpression(new WeightedRegularExpression.Product(null, new WeightedRegularExpression.One()));
	}//GEN-LAST:event_productButtonActionPerformed

	private void starButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_starButtonActionPerformed
		this.insertNewExpression(new WeightedRegularExpression.Star(null));
	}//GEN-LAST:event_starButtonActionPerformed

	private void leftMultiplyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_leftMultiplyButtonActionPerformed
		Object weight = this.parseWeightTextField();
		if (weight == null) {
			return;
		}
		this.insertNewExpression(new WeightedRegularExpression.LeftMultiply(weight, null));
	}//GEN-LAST:event_leftMultiplyButtonActionPerformed

	private void rightMultiplyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rightMultiplyButtonActionPerformed
		Object weight = this.parseWeightTextField();
		if (weight == null) {
			return;
		}
		this.insertNewExpression(new WeightedRegularExpression.RightMultiply(null, weight));
	}//GEN-LAST:event_rightMultiplyButtonActionPerformed

	private void leftArrowButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_leftArrowButtonActionPerformed
		WeightedRegularExpression expression = this.selectedExpression.getPreviousSelectedExpression();
		if (expression == null) {
			return;
		}
		this.setSelectedExpression(expression);
	}//GEN-LAST:event_leftArrowButtonActionPerformed

	private void rightArrowButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rightArrowButtonActionPerformed
		WeightedRegularExpression expression = this.selectedExpression.getNextSelectedExpression();
		if (expression == null) {
			return;
		}
		this.setSelectedExpression(expression);
	}//GEN-LAST:event_rightArrowButtonActionPerformed

	private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed

		WeightedRegularExpression parent = this.selectedExpression.getParent();
		WeightedRegularExpression expression = null;

		if (WeightedRegularExpression.ExpressionWithOneChildExpression.class.isInstance(this.selectedExpression)) {
			expression = ((WeightedRegularExpression.ExpressionWithOneChildExpression) this.selectedExpression).getExpression();

			if (parent == null) {
				expression.setParent(null);
				expression.setAlphabet(this.expressionRoot.getAlphabet());
				expression.setWeight(this.expressionRoot.getWeight());
				expression.setWritingData(this.expressionRoot.getWritingData());
				this.expressionRoot = expression;
			} else if (WeightedRegularExpression.ExpressionWithOneChildExpression.class.isInstance(parent)) {
				((WeightedRegularExpression.ExpressionWithOneChildExpression) parent).setExpression(expression);
			} else if (WeightedRegularExpression.ExpressionWithTwoChildrenExpressions.class.isInstance(parent)) {
				WeightedRegularExpression.ExpressionWithTwoChildrenExpressions parentWithTwoChildren = (WeightedRegularExpression.ExpressionWithTwoChildrenExpressions) parent;
				if (this.selectedExpression.equals(parentWithTwoChildren.getLeftExpression())) {
					parentWithTwoChildren.setLeftExpression(expression);
				} else if (this.selectedExpression.equals(parentWithTwoChildren.getRightExpression())) {
					parentWithTwoChildren.setRightExpression(expression);
				}
			}

			this.selectedExpression.setParent(null);
			((WeightedRegularExpression.ExpressionWithOneChildExpression) this.selectedExpression).setExpression(null);

		} else {  // End if (WeightedRegularExpression.ExpressionWithOneChildExpression.class.isInstance(this.selectedExpression))

			if (!(WeightedRegularExpression.ExpressionWithTwoChildrenExpressions.class.isInstance(parent))) {
				return;
			}

			WeightedRegularExpression.ExpressionWithTwoChildrenExpressions parentWithTwoChildren = (WeightedRegularExpression.ExpressionWithTwoChildrenExpressions) parent;

			if (this.selectedExpression.equals(parentWithTwoChildren.getLeftExpression())) {
				expression = parentWithTwoChildren.getRightExpression();
			} else if (this.selectedExpression.equals(parentWithTwoChildren.getRightExpression())) {
				expression = parentWithTwoChildren.getLeftExpression();
			}

			WeightedRegularExpression grandparent = parent.getParent();

			if (grandparent == null) {

				expression.setParent(null);
				expression.setAlphabet(this.expressionRoot.getAlphabet());
				expression.setWeight(this.expressionRoot.getWeight());
				expression.setWritingData(this.expressionRoot.getWritingData());
				this.expressionRoot = expression;

			} else if (WeightedRegularExpression.ExpressionWithOneChildExpression.class.isInstance(grandparent)) {

				((WeightedRegularExpression.ExpressionWithOneChildExpression) grandparent).setExpression(expression);

			} else if (WeightedRegularExpression.ExpressionWithTwoChildrenExpressions.class.isInstance(grandparent)) {

				parentWithTwoChildren = (WeightedRegularExpression.ExpressionWithTwoChildrenExpressions) grandparent;

				if (parent.equals(parentWithTwoChildren.getLeftExpression())) {
					parentWithTwoChildren.setLeftExpression(expression);
				} else if (parent.equals(parentWithTwoChildren.getRightExpression())) {
					parentWithTwoChildren.setRightExpression(expression);
				}
			}  // End if (WeightedRegularExpression.ExpressionWithTwoChildrenExpressions.class.isInstance(grandparent))

			parent.setParent(null);
			parentWithTwoChildren = (WeightedRegularExpression.ExpressionWithTwoChildrenExpressions) parent;
			parentWithTwoChildren.setLeftExpression(null);
			parentWithTwoChildren.setRightExpression(null);
		}  // End else part of if (WeightedRegularExpression.ExpressionWithOneChildExpression.class.isInstance(this.selectedExpression))

		this.expressionDisplay.setText(this.expressionRoot.toString());
		this.setSelectedExpression(expression);
	}//GEN-LAST:event_deleteButtonActionPerformed

	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
		this.expressionRoot = null;
		Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}//GEN-LAST:event_cancelButtonActionPerformed

	private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
		Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}//GEN-LAST:event_okButtonActionPerformed

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
			java.util.logging.Logger.getLogger(ExpressionEditor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(ExpressionEditor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(ExpressionEditor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(ExpressionEditor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
		//</editor-fold>

		/* Create and display the dialog */
		java.awt.EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				AutomataInterface.WritingData writingData = new AutomataInterface.WritingData();
				writingData.closePar = ')';
				writingData.openPar = '(';
				writingData.plusSym = '+';
				writingData.spacesSym = ' ';
				writingData.starSym = '*';
				writingData.timesSym = '.';
				writingData.weightClosing = '}';
				writingData.weightOpening = '{';
				writingData.zeroSym = '0';

				AutomataInterface.Weight weight = new AutomataInterface.Weight();
				weight.semiring = TAFKitInterface.AutomataType.Semiring.R_REAL;
				weight.identitySymbol = (int) 1;
				weight.zeroSymbol = (int) 0;

				AutomataInterface.Alphabet alphabet = new AutomataInterface.Alphabet();
				alphabet.dataType = TAFKitInterface.AutomataType.AlphabetDataType.CHAR;
				alphabet.allSymbols.add('a');
				alphabet.allSymbols.add('b');
				alphabet.identitySymbol = 'e';

				ExpressionEditor dialog = new ExpressionEditor(new javax.swing.JFrame(), true, alphabet, weight, writingData);
				dialog.addWindowListener(new java.awt.event.WindowAdapter() {

					@Override
					public void windowClosing(java.awt.event.WindowEvent e) {
						System.exit(0);
					}
				});
				dialog.setVisible(true);
			}
		});
	}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox alphabetSymbolComboBox;
    private javax.swing.JLabel alphabetSymbolLabel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JTextField expressionDisplay;
    private javax.swing.JButton leftArrowButton;
    private javax.swing.JButton leftMultiplyButton;
    private javax.swing.JButton okButton;
    private javax.swing.JButton oneButton;
    private javax.swing.JButton productButton;
    private javax.swing.JButton rightArrowButton;
    private javax.swing.JButton rightMultiplyButton;
    private javax.swing.JButton starButton;
    private javax.swing.JButton sumButton;
    private javax.swing.JLabel weightLabel;
    private javax.swing.JTextField weightTextField;
    private javax.swing.JButton zeroButton;
    // End of variables declaration//GEN-END:variables
}
