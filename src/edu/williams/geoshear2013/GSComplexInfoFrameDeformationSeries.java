/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.williams.geoshear2013;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;

/**
 *
 * @author cwarren
 */
public class GSComplexInfoFrameDeformationSeries extends javax.swing.JFrame {

    private GSComplex watchedComplex;
    private MainWindow launchedFromWindow;
    
    private int currentDeformationComponentIndex;
    
    /**
     * Creates new form GSComplexInfoFrameDeformationSeries
     */
    public GSComplexInfoFrameDeformationSeries(MainWindow launchedFrom) {
        this.launchedFromWindow = launchedFrom;
        initComponents();
        Point startingLoc = launchedFromWindow.getLocation();
        startingLoc.translate(launchedFromWindow.getWidth()/2, launchedFromWindow.getHeight()-10);
        this.setLocation(startingLoc);
        this.setTitle("Deformations");
        this.currentDeformationComponentIndex = 0;
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelLabelsContainer = new javax.swing.JPanel();
        jLabelDefSeriesIndiv = new javax.swing.JLabel();
        jLabelDefSeruesCumu = new javax.swing.JLabel();
        jScrollPaneDeformSeriesDisplay = new javax.swing.JScrollPane();
        jPanelDefSeriesMatrixDisplay = new javax.swing.JPanel();
        gSDeformationSeriesBasisDisplay1 = new edu.williams.geoshear2013.GSDeformationSeriesBasisDisplay();

        setMaximumSize(new java.awt.Dimension(2147483647, 260));
        setMinimumSize(new java.awt.Dimension(96, 260));
        setPreferredSize(new java.awt.Dimension(966, 260));
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        jPanelLabelsContainer.setMaximumSize(new java.awt.Dimension(73, 215));
        jPanelLabelsContainer.setMinimumSize(new java.awt.Dimension(73, 215));
        jPanelLabelsContainer.setPreferredSize(new java.awt.Dimension(73, 215));
        jPanelLabelsContainer.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabelDefSeriesIndiv.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelDefSeriesIndiv.setText("Steps");
        jLabelDefSeriesIndiv.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jPanelLabelsContainer.add(jLabelDefSeriesIndiv, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 30, 70, -1));

        jLabelDefSeruesCumu.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelDefSeruesCumu.setText("Cumulative");
        jPanelLabelsContainer.add(jLabelDefSeruesCumu, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 140, 70, -1));

        getContentPane().add(jPanelLabelsContainer);

        jScrollPaneDeformSeriesDisplay.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jScrollPaneDeformSeriesDisplay.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPaneDeformSeriesDisplay.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPaneDeformSeriesDisplay.setAlignmentX(0.0F);
        jScrollPaneDeformSeriesDisplay.setAlignmentY(0.0F);
        jScrollPaneDeformSeriesDisplay.setMaximumSize(new java.awt.Dimension(32767, 215));
        jScrollPaneDeformSeriesDisplay.setPreferredSize(new java.awt.Dimension(10000, 219));

        jPanelDefSeriesMatrixDisplay.setMaximumSize(new java.awt.Dimension(32767, 200));
        jPanelDefSeriesMatrixDisplay.setMinimumSize(new java.awt.Dimension(200, 200));
        jPanelDefSeriesMatrixDisplay.setName(""); // NOI18N
        jPanelDefSeriesMatrixDisplay.setPreferredSize(new java.awt.Dimension(200, 200));
        jPanelDefSeriesMatrixDisplay.setLayout(new javax.swing.BoxLayout(jPanelDefSeriesMatrixDisplay, javax.swing.BoxLayout.LINE_AXIS));
        jPanelDefSeriesMatrixDisplay.add(gSDeformationSeriesBasisDisplay1);

        jScrollPaneDeformSeriesDisplay.setViewportView(jPanelDefSeriesMatrixDisplay);

        getContentPane().add(jScrollPaneDeformSeriesDisplay);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    public void setFromDeformationSeries(GSDeformationSeries defs) {
        this.jPanelDefSeriesMatrixDisplay.removeAll();
        this.jPanelDefSeriesMatrixDisplay.add(this.gSDeformationSeriesBasisDisplay1, 0);
        for (int i=0; i<defs.size(); i++) {
            this.jPanelDefSeriesMatrixDisplay.add(new GSDeformationSeriesStepDisplay(defs.get(i),defs.getCompositeTransform(i+1),i+1)
                                                  ,0);
        }
        this.jPanelDefSeriesMatrixDisplay.setPreferredSize(null);
        this.validate();
        this.repaint();
    }
    
    
    public void markCurrentDeformation(int deformationNumber) {
        int numComps = this.jPanelDefSeriesMatrixDisplay.getComponentCount();
        for (int i=0; i<numComps; i++) {
            ((HighlightableComponent)(this.jPanelDefSeriesMatrixDisplay.getComponent(i))).unhighlight();
        }        
        this.currentDeformationComponentIndex = numComps - deformationNumber;
        ((HighlightableComponent)(this.jPanelDefSeriesMatrixDisplay.getComponent(this.currentDeformationComponentIndex))).highlight();
        this.jPanelDefSeriesMatrixDisplay.scrollRectToVisible(this.jPanelDefSeriesMatrixDisplay.getComponent(this.currentDeformationComponentIndex).getBounds());
    }
    
    
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
            java.util.logging.Logger.getLogger(GSComplexInfoFrameDeformationSeries.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GSComplexInfoFrameDeformationSeries.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GSComplexInfoFrameDeformationSeries.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GSComplexInfoFrameDeformationSeries.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GSComplexInfoFrameDeformationSeries(null).setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private edu.williams.geoshear2013.GSDeformationSeriesBasisDisplay gSDeformationSeriesBasisDisplay1;
    private javax.swing.JLabel jLabelDefSeriesIndiv;
    private javax.swing.JLabel jLabelDefSeruesCumu;
    private javax.swing.JPanel jPanelDefSeriesMatrixDisplay;
    private javax.swing.JPanel jPanelLabelsContainer;
    private javax.swing.JScrollPane jScrollPaneDeformSeriesDisplay;
    // End of variables declaration//GEN-END:variables

   
}