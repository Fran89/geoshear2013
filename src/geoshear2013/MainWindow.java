/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geoshear2013;

/**
 *
 * @author cwarren
 */
public class MainWindow extends javax.swing.JFrame {
//    private double cx;
//    private double cy;
    private GSComplexUI gscUI;

    /**
     * Creates new form MainWindow
     */
    public MainWindow() {
        initComponents();
        
        this.gscUI = new GSComplexUI(new GSComplex());
        
        this.gscUI.gsc.pebbles.add(new GSPebble(150,200,100,150,1));
        
        int w = this.displayPanel.getWidth();
        int h = this.displayPanel.getHeight();
        this.gscUI.setPreferredSize (new java.awt.Dimension (w,h));
        this.gscUI.setBounds(0, 0, w, h);
        this.gscUI.setDoubleBuffered (true);
        this.gscUI.setCenter(this.displayPanel.getWidth()/2, this.displayPanel.getHeight()/2);
        this.displayPanel.add(this.gscUI);

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

        displayPanel = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        displayPanel.setBackground(new java.awt.Color(255, 255, 255));
        displayPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        displayPanel.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                displayPanelMouseWheelMoved(evt);
            }
        });
        displayPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                displayPanelMousePressed(evt);
            }
        });
        displayPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                displayPanelComponentResized(evt);
            }
        });
        displayPanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                displayPanelMouseDragged(evt);
            }
        });

        javax.swing.GroupLayout displayPanelLayout = new javax.swing.GroupLayout(displayPanel);
        displayPanel.setLayout(displayPanelLayout);
        displayPanelLayout.setHorizontalGroup(
            displayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 699, Short.MAX_VALUE)
        );
        displayPanelLayout.setVerticalGroup(
            displayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jButton1.setText("set center");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton1MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(jButton1)
                .addGap(77, 77, 77)
                .addComponent(displayPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(displayPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(485, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(193, 193, 193))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseClicked
        this.gscUI.setCenter(this.displayPanel.getWidth()/2, this.displayPanel.getHeight()/2);
        this.gscUI.repaint();
    }//GEN-LAST:event_jButton1MouseClicked

    private void displayPanelComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_displayPanelComponentResized
        int w = this.displayPanel.getWidth();
        int h = this.displayPanel.getHeight();
        if (this.gscUI != null) {
            this.gscUI.setPreferredSize (new java.awt.Dimension (w,h));
            this.gscUI.setBounds(0, 0, w, h);
            this.gscUI.repaint();
        }
    }//GEN-LAST:event_displayPanelComponentResized

    private void displayPanelMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_displayPanelMouseDragged
        this.gscUI.handleMouseDrag(evt);
    }//GEN-LAST:event_displayPanelMouseDragged

    private void displayPanelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_displayPanelMousePressed
        this.gscUI.handleMousePressed(evt);
    }//GEN-LAST:event_displayPanelMousePressed

    private void displayPanelMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_displayPanelMouseWheelMoved
        this.gscUI.handleMouseWheelMoved(evt);
    }//GEN-LAST:event_displayPanelMouseWheelMoved

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
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainWindow().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel displayPanel;
    private javax.swing.JButton jButton1;
    // End of variables declaration//GEN-END:variables
}
