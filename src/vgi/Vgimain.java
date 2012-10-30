/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vgi;

import vgi.display.VGI;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import vgi.automata.Automata;
import vgi.fsmxml.FsmXml;
import vgi.fsmxml.FsmXmlInterface;

/**
 *
 * @author reng
 */
public class Vgimain {
    public static void main(final String args[]) {
		/*
		 * Set the Nimbus look and feel
		 */
		//<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
		 * If Nimbus (introduced in Java SE 6) is not available, stay with the
		 * default look and feel. For details see
		 * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
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

		/*
		 * Create and display the form
		 */
               // FsmXml fsmXml = new FsmXml();
               // fsmXml.read(System.in);
                //final String[] filename=args;
		java.awt.EventQueue.invokeLater(new Runnable() {

			public void run() {
                                VGI vgi=new VGI();
                                boolean standardInputError=false;
                                String errorString=null;
                                    
                                if(args.length>0){
                                    String filename=args[0];
                                    if(filename!=null){
                                        if(filename.charAt(0)=='-'){
                                            FsmXml fsmXml=new FsmXml();
                                            List<Automata> automataList = null;
           
                                            try{
                                                        automataList=fsmXml.read(System.in);
                                                        //System.out.println(System.in.toString());
                                                    
                                            }catch(FsmXmlInterface.FsmXmlException ex){
                                                Logger.getLogger(VGI.class.getName()).log(Level.SEVERE, null, ex);
                                                standardInputError=true;
                                                errorString=ex.getMessage();
                                            }
                                            
                                            if ((automataList != null) && (automataList.size() > 0)) {
       
                                                Automata automata = automataList.get(0);
//                                                vgi.pmAutomataType = new TAFKitInterface.AutomataType(automata);
//                                                vgi.updateAlgorithmMenuItems();
                                                vgi.createInternalFrame(automata, "");
                                            }
                                            
                                        }else{
                                            vgi.openFile(filename);
                                        }
                                    }
                                }
				vgi.setVisible(true);
                                if(standardInputError) vgi.showErrorDialog(errorString);
                                
			}
		});
	}
}
