/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.williams.geoshear2013;

import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.JTextField;
/**
 *
 * @author cwarren
 */
public class MainWindow extends javax.swing.JFrame {
//    private double cx;
//    private double cy;
    private GSComplexUI gscUI;
    private HelpWindow helpWindow;
    private AboutWindow aboutWindow;

    private HashMap displayNumberConstraints;
    
    
    /**
     * Creates new form MainWindow
     * 
     * The window has two main areas: a GSComplexUI (geoshear complex user
     * interface) in the big pane on the right and various controls in a column
     * on the left.
     *
     * The GSCUI handles all the mouse interface (and the key-based
     * modifiers: shift for shear, control for compress, and alt for around
     * (rotation)) - the relevant events are passed along to the GSCUI object to
     * be dealt with.
     *
     * The controls on the left are handled in this class/object have several
     * sub-sections. The first set are the deform controls, which let the user
     * set the shear, compression, or rotation directly, and also to 'lock in'
     * the current tentative deformation. The next section is the strain
     * controls. This has both a matrix that shows the current strain as well as
     * nav elements to step through all the deformations that have been applied.
     * Below those are the editing controls, which let a user modify the set of
     * pebbles / ellipses. Lastly, at the bottom is a snap-shot tool that does a
     * simple export of the current view of the gscUI.
     * 
     */
    public MainWindow() {
        initComponents();
        
        this.displayNumberConstraints = new HashMap();

        this.displayNumberConstraints.put(this.jTextFieldShearX, new ValueConstrainer(-10, -10, 10, 10, .01, 3, 0));
        this.displayNumberConstraints.put(this.jTextFieldShearY, new ValueConstrainer(-10, -10, 10, 10, .01, 3, 0));
        this.displayNumberConstraints.put(this.jTextFieldCompressX, new ValueConstrainer(.1, .1, 10, 10, .01, 3, 1));
        this.displayNumberConstraints.put(this.jTextFieldCompressY, new ValueConstrainer(.1, .1, 10, 10, .01, 3, 1));
        this.displayNumberConstraints.put(this.jTextFieldRotDeg, new ValueConstrainer(-180, 180, 180, -180, 1, 2, 0, ValueConstrainer.CONSTRAINT_WRAP));
        this.displayNumberConstraints.put(this.jTextFieldRotRad, new ValueConstrainer(-1*Math.PI, Math.PI, Math.PI, -1*Math.PI, .01, 3, 0, ValueConstrainer.CONSTRAINT_WRAP));

        this.displayNumberConstraints.put(this.jTextFieldRFPhiCurrentRF, new ValueConstrainer(-10000, -10000, 10000, 10000, .01, 3, 1));
        this.displayNumberConstraints.put(this.jTextFieldRFPhiCurrentPhi, new ValueConstrainer(-1*Math.PI, Math.PI, Math.PI, -1*Math.PI, .01, 3, 0, ValueConstrainer.CONSTRAINT_WRAP));
        
        this.displayNumberConstraints.put(this.jTextFieldStrainM00, new ValueConstrainer(-10, -10, 10, 10, .01, 3, 1));
        this.displayNumberConstraints.put(this.jTextFieldStrainM01, new ValueConstrainer(-10, -10, 10, 10, .01, 3, 0));
        this.displayNumberConstraints.put(this.jTextFieldStrainM10, new ValueConstrainer(-10, -10, 10, 10, .01, 3, 0));
        this.displayNumberConstraints.put(this.jTextFieldStrainM11, new ValueConstrainer(-10, -10, 10, 10, .01, 3, 1));

        Iterator keyIter = this.displayNumberConstraints.keySet().iterator();
        while(keyIter.hasNext()) {
            JTextField tf = (JTextField) keyIter.next();
            tf.setText(Util.truncTextDecimal(Double.toString(((ValueConstrainer)this.displayNumberConstraints.get(tf)).getDefaultVal()), ((ValueConstrainer)this.displayNumberConstraints.get(tf)).getDisplayPrecision()));
        }
                
        this.helpWindow = new HelpWindow();
        this.helpWindow.setLocationByPlatform(true);
        this.helpWindow.setSize(this.jPanelContainerDisplay.getWidth() + 50,this.jPanelContainerDisplay.getHeight() + 50);
        
        this.aboutWindow = new AboutWindow();
        this.aboutWindow.setLocationByPlatform(true);
        
        this.gscUI = new GSComplexUI(new GSComplex(),this);

        int w = this.jPanelContainerDisplay.getWidth();
        int h = this.jPanelContainerDisplay.getHeight();
        this.gscUI.setPreferredSize (new java.awt.Dimension (w,h));
        this.gscUI.setBounds(0, 0, w, h);
        this.gscUI.setDoubleBuffered (true);
        this.gscUI.setCenter(this.jPanelContainerDisplay.getWidth()/2, this.jPanelContainerDisplay.getHeight()/2);
        this.jPanelContainerDisplay.add(this.gscUI);

        // dev data
//        this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble(-150,-200,150,100,Math.PI/2 + Math.PI / 5));
//        this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble(-150,200,100,100,Math.PI/2 + Math.PI / 5));
        
//        this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble(150,200,150,100,0));
//        this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble(-150,200,150,100,.5));
//        this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble(-150,-200,150,100,1));
//        this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble(150,-200,150,100,1.5));
       
        this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble(100,100,30,20,0, Color.CYAN));
        this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble(200,100,45,30,.5, Color.GREEN));
        this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble(100,200,60,40,-1, Color.BLUE));
        this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble(200,200,75,50,2, Color.MAGENTA));

        this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble(-100,100,30,20,0, Color.CYAN));
        this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble(-200,100,45,30,.5, Color.GREEN));
        this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble(-100,200,60,40,-1, Color.BLUE));
        this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble(-200,200,75,50,2, Color.MAGENTA));
        
        this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble(100,-100,30,20,0, Color.CYAN));
        this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble(200,-100,45,30,.5, Color.GREEN));
        this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble(100,-200,60,40,-1, Color.BLUE));
        this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble(200,-200,75,50,2, Color.MAGENTA));

        this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble(-100,-100,30,20,0, Color.CYAN));
        this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble(-200,-100,45,30,.5, Color.GREEN));
        this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble(-100,-200,60,40,-1, Color.BLUE));
        this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble(-200,-200,75,50,2, Color.MAGENTA));
        
//        this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble(0,0,150,150,0));

//        this.gscUI.setCenter(this.displayPanel.getWidth()/2, this.displayPanel.getHeight()/2);
//        this.gscUI.repaint();
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

        jPanelContainerDisplay = new javax.swing.JPanel();
        jPanelContainerControls = new javax.swing.JPanel();
        jPanelDisplayControls = new javax.swing.JPanel();
        jPanelZoomControl = new javax.swing.JPanel();
        jLabelZoom = new javax.swing.JLabel();
        jSliderZoom = new javax.swing.JSlider();
        jPanelResetButtons = new javax.swing.JPanel();
        jButtonUnzoom = new javax.swing.JButton();
        jButtonCenter = new javax.swing.JButton();
        jPanelDeformControls = new javax.swing.JPanel();
        jPanelDeformShearControls = new javax.swing.JPanel();
        jLabelShearControl = new javax.swing.JLabel();
        jTextFieldShearX = new javax.swing.JTextField();
        jLabelShearX = new javax.swing.JLabel();
        jLabelShearY = new javax.swing.JLabel();
        jTextFieldShearY = new javax.swing.JTextField();
        jPanelDeformCompressControls = new javax.swing.JPanel();
        jLabelCompressControl = new javax.swing.JLabel();
        jTextFieldCompressX = new javax.swing.JTextField();
        jLabelCompressX = new javax.swing.JLabel();
        jLabelCompressrY = new javax.swing.JLabel();
        jTextFieldCompressY = new javax.swing.JTextField();
        jButtonLinkCompressionDeform = new javax.swing.JButton();
        jPanelDeformRotateControls = new javax.swing.JPanel();
        jLabelShearControl1 = new javax.swing.JLabel();
        jLabelShearY1 = new javax.swing.JLabel();
        jLabelShearX1 = new javax.swing.JLabel();
        jTextFieldRotDeg = new javax.swing.JTextField();
        jTextFieldRotRad = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jPanelDeformNavControls = new javax.swing.JPanel();
        jButtonDeformApplyRemove = new javax.swing.JButton();
        jButtonDeformReset = new javax.swing.JButton();
        jPanelDeformMatrixLeft = new javax.swing.JPanel();
        jButtonStrainNavPrevious = new javax.swing.JButton();
        jTextFieldStrainM00 = new javax.swing.JTextField();
        jTextFieldStrainM10 = new javax.swing.JTextField();
        jTextFieldRFPhiCurrentRF = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldStrainCumuRF = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabelStrainNavPosition = new javax.swing.JLabel();
        jPanelDeformMatrixRight = new javax.swing.JPanel();
        jButtonStrainNavNext = new javax.swing.JButton();
        jTextFieldStrainM01 = new javax.swing.JTextField();
        jTextFieldStrainM11 = new javax.swing.JTextField();
        jTextFieldRFPhiCurrentPhi = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jTextFieldStrainCumuPhi = new javax.swing.JTextField();
        jLabelStrainNavCount = new javax.swing.JLabel();
        jPanelStrainControls = new javax.swing.JPanel();
        jPanelEditPebbleControls = new javax.swing.JPanel();
        jPanelSnapshotControls = new javax.swing.JPanel();
        MainWindowMenuBar = new javax.swing.JMenuBar();
        GeoshearMenu = new javax.swing.JMenu();
        HelpMenuItem = new javax.swing.JMenuItem();
        AboutMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        ExitMenuItem = new javax.swing.JMenuItem();
        FileMenu = new javax.swing.JMenu();
        DisplayMenu = new javax.swing.JMenu();
        ChartsMenu = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanelContainerDisplay.setBackground(new java.awt.Color(255, 255, 255));
        jPanelContainerDisplay.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanelContainerDisplay.setMinimumSize(new java.awt.Dimension(20, 20));
        jPanelContainerDisplay.setPreferredSize(new java.awt.Dimension(700, 700));
        jPanelContainerDisplay.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                jPanelContainerDisplayMouseWheelMoved(evt);
            }
        });
        jPanelContainerDisplay.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanelContainerDisplayMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jPanelContainerDisplayMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jPanelContainerDisplayMouseReleased(evt);
            }
        });
        jPanelContainerDisplay.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jPanelContainerDisplayComponentResized(evt);
            }
        });
        jPanelContainerDisplay.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jPanelContainerDisplayMouseDragged(evt);
            }
        });
        jPanelContainerDisplay.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jPanelContainerDisplayKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jPanelContainerDisplayKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanelContainerDisplayLayout = new javax.swing.GroupLayout(jPanelContainerDisplay);
        jPanelContainerDisplay.setLayout(jPanelContainerDisplayLayout);
        jPanelContainerDisplayLayout.setHorizontalGroup(
            jPanelContainerDisplayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 698, Short.MAX_VALUE)
        );
        jPanelContainerDisplayLayout.setVerticalGroup(
            jPanelContainerDisplayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanelContainerControls.setMaximumSize(new java.awt.Dimension(220, 131123));
        jPanelContainerControls.setMinimumSize(new java.awt.Dimension(220, 700));
        jPanelContainerControls.setPreferredSize(new java.awt.Dimension(220, 700));
        jPanelContainerControls.setLayout(new javax.swing.BoxLayout(jPanelContainerControls, javax.swing.BoxLayout.Y_AXIS));

        jPanelDisplayControls.setAlignmentX(0.0F);
        jPanelDisplayControls.setAlignmentY(0.0F);
        jPanelDisplayControls.setMaximumSize(new java.awt.Dimension(32787, 70));
        jPanelDisplayControls.setMinimumSize(new java.awt.Dimension(219, 70));
        jPanelDisplayControls.setPreferredSize(new java.awt.Dimension(219, 70));
        jPanelDisplayControls.setLayout(new javax.swing.BoxLayout(jPanelDisplayControls, javax.swing.BoxLayout.Y_AXIS));

        jPanelZoomControl.setLayout(new javax.swing.BoxLayout(jPanelZoomControl, javax.swing.BoxLayout.X_AXIS));

        jLabelZoom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/edu/williams/geoshear2013/img/magnifier.gif"))); // NOI18N
        jPanelZoomControl.add(jLabelZoom);

        jSliderZoom.setMajorTickSpacing(50);
        jSliderZoom.setMinorTickSpacing(5);
        jSliderZoom.setPaintTicks(true);
        jSliderZoom.setToolTipText("left to zoom out, right to zoom in");
        jSliderZoom.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderZoomStateChanged(evt);
            }
        });
        jPanelZoomControl.add(jSliderZoom);
        jSliderZoom.getAccessibleContext().setAccessibleName("zoom control");

        jPanelDisplayControls.add(jPanelZoomControl);

        jPanelResetButtons.setLayout(new javax.swing.BoxLayout(jPanelResetButtons, javax.swing.BoxLayout.X_AXIS));

        jButtonUnzoom.setText("Unzoom");
        jButtonUnzoom.setMargin(new java.awt.Insets(2, 8, 2, 8));
        jButtonUnzoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUnzoomActionPerformed(evt);
            }
        });
        jPanelResetButtons.add(jButtonUnzoom);

        jButtonCenter.setText("Center in view");
        jButtonCenter.setMargin(new java.awt.Insets(2, 8, 2, 8));
        jButtonCenter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCenterActionPerformed(evt);
            }
        });
        jPanelResetButtons.add(jButtonCenter);

        jPanelDisplayControls.add(jPanelResetButtons);

        jPanelContainerControls.add(jPanelDisplayControls);

        jPanelDeformControls.setMaximumSize(new java.awt.Dimension(5000, 5000));
        jPanelDeformControls.setMinimumSize(new java.awt.Dimension(1, 1));
        jPanelDeformControls.setPreferredSize(new java.awt.Dimension(220, 395));
        jPanelDeformControls.setRequestFocusEnabled(false);
        jPanelDeformControls.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanelDeformShearControls.setMaximumSize(new java.awt.Dimension(100, 75));
        jPanelDeformShearControls.setMinimumSize(new java.awt.Dimension(100, 75));
        jPanelDeformShearControls.setPreferredSize(new java.awt.Dimension(100, 75));
        jPanelDeformShearControls.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabelShearControl.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelShearControl.setText("shear");
        jLabelShearControl.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jPanelDeformShearControls.add(jLabelShearControl, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 64, -1));

        jTextFieldShearX.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldShearX.setText("0.000");
        jTextFieldShearX.setBorder(null);
        jTextFieldShearX.setMaximumSize(new java.awt.Dimension(42, 14));
        jTextFieldShearX.setMinimumSize(new java.awt.Dimension(42, 14));
        jTextFieldShearX.setPreferredSize(new java.awt.Dimension(42, 14));
        jTextFieldShearX.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldShearXFocusLost(evt);
            }
        });
        jTextFieldShearX.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldShearXKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldShearXKeyReleased(evt);
            }
        });
        jPanelDeformShearControls.add(jTextFieldShearX, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 30, -1, -1));

        jLabelShearX.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelShearX.setText("X");
        jPanelDeformShearControls.add(jLabelShearX, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 14, -1));

        jLabelShearY.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelShearY.setText("Y");
        jPanelDeformShearControls.add(jLabelShearY, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 14, -1));

        jTextFieldShearY.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldShearY.setText("0.000");
        jTextFieldShearY.setBorder(null);
        jTextFieldShearY.setMaximumSize(new java.awt.Dimension(42, 14));
        jTextFieldShearY.setMinimumSize(new java.awt.Dimension(42, 14));
        jTextFieldShearY.setPreferredSize(new java.awt.Dimension(42, 14));
        jTextFieldShearY.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldShearYFocusLost(evt);
            }
        });
        jTextFieldShearY.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldShearYKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldShearYKeyReleased(evt);
            }
        });
        jPanelDeformShearControls.add(jTextFieldShearY, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 50, -1, -1));

        jPanelDeformControls.add(jPanelDeformShearControls, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 1, 110, 79));

        jPanelDeformCompressControls.setMaximumSize(new java.awt.Dimension(100, 75));
        jPanelDeformCompressControls.setMinimumSize(new java.awt.Dimension(100, 75));
        jPanelDeformCompressControls.setPreferredSize(new java.awt.Dimension(100, 75));
        jPanelDeformCompressControls.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabelCompressControl.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelCompressControl.setText("compress");
        jLabelCompressControl.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jPanelDeformCompressControls.add(jLabelCompressControl, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 11, 66, -1));

        jTextFieldCompressX.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldCompressX.setText("1");
        jTextFieldCompressX.setBorder(null);
        jTextFieldCompressX.setMaximumSize(new java.awt.Dimension(42, 14));
        jTextFieldCompressX.setMinimumSize(new java.awt.Dimension(42, 14));
        jTextFieldCompressX.setPreferredSize(new java.awt.Dimension(42, 14));
        jTextFieldCompressX.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldCompressXFocusLost(evt);
            }
        });
        jTextFieldCompressX.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldCompressXKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldCompressXKeyReleased(evt);
            }
        });
        jPanelDeformCompressControls.add(jTextFieldCompressX, new org.netbeans.lib.awtextra.AbsoluteConstraints(24, 30, -1, -1));

        jLabelCompressX.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelCompressX.setText("X");
        jPanelDeformCompressControls.add(jLabelCompressX, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 30, 14, -1));

        jLabelCompressrY.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelCompressrY.setText("Y");
        jPanelDeformCompressControls.add(jLabelCompressrY, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 50, 14, -1));

        jTextFieldCompressY.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldCompressY.setText("1");
        jTextFieldCompressY.setBorder(null);
        jTextFieldCompressY.setMaximumSize(new java.awt.Dimension(42, 14));
        jTextFieldCompressY.setMinimumSize(new java.awt.Dimension(42, 14));
        jTextFieldCompressY.setPreferredSize(new java.awt.Dimension(42, 14));
        jTextFieldCompressY.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldCompressYFocusLost(evt);
            }
        });
        jTextFieldCompressY.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldCompressYKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldCompressYKeyReleased(evt);
            }
        });
        jPanelDeformCompressControls.add(jTextFieldCompressY, new org.netbeans.lib.awtextra.AbsoluteConstraints(24, 50, -1, -1));

        jButtonLinkCompressionDeform.setIcon(new javax.swing.ImageIcon(getClass().getResource("/edu/williams/geoshear2013/img/linked.gif"))); // NOI18N
        jPanelDeformCompressControls.add(jButtonLinkCompressionDeform, new org.netbeans.lib.awtextra.AbsoluteConstraints(72, 22, 23, -1));

        jPanelDeformControls.add(jPanelDeformCompressControls, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 1, 110, 79));

        jPanelDeformRotateControls.setMaximumSize(new java.awt.Dimension(100, 75));
        jPanelDeformRotateControls.setMinimumSize(new java.awt.Dimension(100, 75));
        jPanelDeformRotateControls.setPreferredSize(new java.awt.Dimension(100, 75));
        jPanelDeformRotateControls.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabelShearControl1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelShearControl1.setText("rotation");
        jLabelShearControl1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jPanelDeformRotateControls.add(jLabelShearControl1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 11, 68, -1));

        jLabelShearY1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelShearY1.setText("rad.");
        jPanelDeformRotateControls.add(jLabelShearY1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 51, 22, -1));

        jLabelShearX1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelShearX1.setText("deg.");
        jPanelDeformRotateControls.add(jLabelShearX1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 31, -1, -1));

        jTextFieldRotDeg.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldRotDeg.setText("0.0");
        jTextFieldRotDeg.setBorder(null);
        jTextFieldRotDeg.setMaximumSize(new java.awt.Dimension(42, 14));
        jTextFieldRotDeg.setMinimumSize(new java.awt.Dimension(42, 14));
        jTextFieldRotDeg.setPreferredSize(new java.awt.Dimension(42, 14));
        jTextFieldRotDeg.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldRotDegFocusLost(evt);
            }
        });
        jTextFieldRotDeg.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldRotDegKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldRotDegKeyReleased(evt);
            }
        });
        jPanelDeformRotateControls.add(jTextFieldRotDeg, new org.netbeans.lib.awtextra.AbsoluteConstraints(27, 31, -1, -1));

        jTextFieldRotRad.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldRotRad.setText("0.000");
        jTextFieldRotRad.setBorder(null);
        jTextFieldRotRad.setMaximumSize(new java.awt.Dimension(42, 14));
        jTextFieldRotRad.setMinimumSize(new java.awt.Dimension(42, 14));
        jTextFieldRotRad.setPreferredSize(new java.awt.Dimension(42, 14));
        jTextFieldRotRad.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldRotRadFocusLost(evt);
            }
        });
        jTextFieldRotRad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldRotRadKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldRotRadKeyReleased(evt);
            }
        });
        jPanelDeformRotateControls.add(jTextFieldRotRad, new org.netbeans.lib.awtextra.AbsoluteConstraints(27, 51, -1, -1));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/edu/williams/geoshear2013/img/linked.gif"))); // NOI18N
        jLabel1.setEnabled(false);
        jPanelDeformRotateControls.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 20, 20, 55));

        jPanelDeformControls.add(jPanelDeformRotateControls, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 80, 110, 79));

        jPanelDeformNavControls.setMaximumSize(new java.awt.Dimension(100, 75));
        jPanelDeformNavControls.setMinimumSize(new java.awt.Dimension(100, 75));
        jPanelDeformNavControls.setPreferredSize(new java.awt.Dimension(100, 75));
        jPanelDeformNavControls.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButtonDeformApplyRemove.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jButtonDeformApplyRemove.setText("Remove");
        jButtonDeformApplyRemove.setToolTipText("delete the current deformation from the series");
        jButtonDeformApplyRemove.setAlignmentX(0.5F);
        jButtonDeformApplyRemove.setEnabled(false);
        jButtonDeformApplyRemove.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jButtonDeformApplyRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeformApplyRemoveActionPerformed(evt);
            }
        });
        jPanelDeformNavControls.add(jButtonDeformApplyRemove, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 80, 40));

        jButtonDeformReset.setText("Reset");
        jButtonDeformReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeformResetActionPerformed(evt);
            }
        });
        jPanelDeformNavControls.add(jButtonDeformReset, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 80, -1));

        jPanelDeformControls.add(jPanelDeformNavControls, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 80, 110, 79));

        jPanelDeformMatrixLeft.setPreferredSize(new java.awt.Dimension(93, 75));
        jPanelDeformMatrixLeft.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButtonStrainNavPrevious.setIcon(new javax.swing.ImageIcon(getClass().getResource("/edu/williams/geoshear2013/img/arrow_left_light.gif"))); // NOI18N
        jPanelDeformMatrixLeft.add(jButtonStrainNavPrevious, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, 60, -1));

        jTextFieldStrainM00.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jTextFieldStrainM00.setText("1.000");
        jPanelDeformMatrixLeft.add(jTextFieldStrainM00, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 40, 60, -1));

        jTextFieldStrainM10.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jTextFieldStrainM10.setText("0.000");
        jPanelDeformMatrixLeft.add(jTextFieldStrainM10, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 70, 60, -1));

        jTextFieldRFPhiCurrentRF.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jTextFieldRFPhiCurrentRF.setText("1.000");
        jTextFieldRFPhiCurrentRF.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanelDeformMatrixLeft.add(jTextFieldRFPhiCurrentRF, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 6, 60, 20));

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel2.setText("RF");
        jPanelDeformMatrixLeft.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 6, 18, 20));

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 72)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel3.setText("[");
        jPanelDeformMatrixLeft.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 30, 90));

        jTextFieldStrainCumuRF.setEditable(false);
        jTextFieldStrainCumuRF.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jTextFieldStrainCumuRF.setText("1.000");
        jTextFieldStrainCumuRF.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanelDeformMatrixLeft.add(jTextFieldStrainCumuRF, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 180, 60, 20));

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel6.setText("RF");
        jPanelDeformMatrixLeft.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 180, 18, 20));

        jLabelStrainNavPosition.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelStrainNavPosition.setText("0 /");
        jPanelDeformMatrixLeft.add(jLabelStrainNavPosition, new org.netbeans.lib.awtextra.AbsoluteConstraints(76, 150, 30, 20));

        jPanelDeformControls.add(jPanelDeformMatrixLeft, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 160, 110, 210));

        jPanelDeformMatrixRight.setPreferredSize(new java.awt.Dimension(93, 75));
        jPanelDeformMatrixRight.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButtonStrainNavNext.setIcon(new javax.swing.ImageIcon(getClass().getResource("/edu/williams/geoshear2013/img/arrow_right_light.gif"))); // NOI18N
        jPanelDeformMatrixRight.add(jButtonStrainNavNext, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 150, 60, -1));

        jTextFieldStrainM01.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jTextFieldStrainM01.setText("0.000");
        jPanelDeformMatrixRight.add(jTextFieldStrainM01, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, 60, -1));

        jTextFieldStrainM11.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jTextFieldStrainM11.setText("1.000");
        jPanelDeformMatrixRight.add(jTextFieldStrainM11, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 60, -1));

        jTextFieldRFPhiCurrentPhi.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jTextFieldRFPhiCurrentPhi.setText("0.000");
        jTextFieldRFPhiCurrentPhi.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanelDeformMatrixRight.add(jTextFieldRFPhiCurrentPhi, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 6, 60, 20));

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("phi");
        jPanelDeformMatrixRight.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 6, 20, 20));

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 72)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel5.setText("]");
        jPanelDeformMatrixRight.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, 30, 90));

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("phi");
        jPanelDeformMatrixRight.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 180, 20, 20));

        jTextFieldStrainCumuPhi.setEditable(false);
        jTextFieldStrainCumuPhi.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jTextFieldStrainCumuPhi.setText("0.000");
        jTextFieldStrainCumuPhi.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanelDeformMatrixRight.add(jTextFieldStrainCumuPhi, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 180, 60, 20));

        jLabelStrainNavCount.setText("0");
        jPanelDeformMatrixRight.add(jLabelStrainNavCount, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 150, 30, 20));

        jPanelDeformControls.add(jPanelDeformMatrixRight, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 160, 110, 210));

        jPanelContainerControls.add(jPanelDeformControls);

        jPanelStrainControls.setAlignmentX(0.0F);
        jPanelStrainControls.setAlignmentY(0.0F);
        jPanelStrainControls.setMaximumSize(new java.awt.Dimension(220, 100));
        jPanelStrainControls.setMinimumSize(new java.awt.Dimension(220, 100));
        jPanelStrainControls.setPreferredSize(new java.awt.Dimension(220, 100));

        javax.swing.GroupLayout jPanelStrainControlsLayout = new javax.swing.GroupLayout(jPanelStrainControls);
        jPanelStrainControls.setLayout(jPanelStrainControlsLayout);
        jPanelStrainControlsLayout.setHorizontalGroup(
            jPanelStrainControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 220, Short.MAX_VALUE)
        );
        jPanelStrainControlsLayout.setVerticalGroup(
            jPanelStrainControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jPanelContainerControls.add(jPanelStrainControls);

        jPanelEditPebbleControls.setAlignmentX(0.0F);
        jPanelEditPebbleControls.setAlignmentY(0.0F);
        jPanelEditPebbleControls.setMaximumSize(new java.awt.Dimension(220, 100));
        jPanelEditPebbleControls.setMinimumSize(new java.awt.Dimension(220, 100));
        jPanelEditPebbleControls.setPreferredSize(new java.awt.Dimension(220, 100));

        javax.swing.GroupLayout jPanelEditPebbleControlsLayout = new javax.swing.GroupLayout(jPanelEditPebbleControls);
        jPanelEditPebbleControls.setLayout(jPanelEditPebbleControlsLayout);
        jPanelEditPebbleControlsLayout.setHorizontalGroup(
            jPanelEditPebbleControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 220, Short.MAX_VALUE)
        );
        jPanelEditPebbleControlsLayout.setVerticalGroup(
            jPanelEditPebbleControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jPanelContainerControls.add(jPanelEditPebbleControls);

        jPanelSnapshotControls.setAlignmentX(0.0F);
        jPanelSnapshotControls.setAlignmentY(0.0F);
        jPanelSnapshotControls.setMaximumSize(new java.awt.Dimension(220, 100));
        jPanelSnapshotControls.setMinimumSize(new java.awt.Dimension(220, 100));
        jPanelSnapshotControls.setPreferredSize(new java.awt.Dimension(220, 100));

        javax.swing.GroupLayout jPanelSnapshotControlsLayout = new javax.swing.GroupLayout(jPanelSnapshotControls);
        jPanelSnapshotControls.setLayout(jPanelSnapshotControlsLayout);
        jPanelSnapshotControlsLayout.setHorizontalGroup(
            jPanelSnapshotControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 220, Short.MAX_VALUE)
        );
        jPanelSnapshotControlsLayout.setVerticalGroup(
            jPanelSnapshotControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jPanelContainerControls.add(jPanelSnapshotControls);

        GeoshearMenu.setText("GeoShear");
        GeoshearMenu.setToolTipText("Control the application");

        HelpMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        HelpMenuItem.setText("Help");
        HelpMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                HelpMenuItemActionPerformed(evt);
            }
        });
        GeoshearMenu.add(HelpMenuItem);

        AboutMenuItem.setText("About");
        AboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AboutMenuItemActionPerformed(evt);
            }
        });
        GeoshearMenu.add(AboutMenuItem);
        GeoshearMenu.add(jSeparator1);

        ExitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        ExitMenuItem.setText("Quit");
        ExitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExitMenuItemActionPerformed(evt);
            }
        });
        GeoshearMenu.add(ExitMenuItem);

        MainWindowMenuBar.add(GeoshearMenu);

        FileMenu.setText("File");
        FileMenu.setToolTipText("Save, Open, Export");
        MainWindowMenuBar.add(FileMenu);

        DisplayMenu.setText("Display");
        DisplayMenu.setToolTipText("Options for the main display area");
        MainWindowMenuBar.add(DisplayMenu);

        ChartsMenu.setText("Charts");
        ChartsMenu.setToolTipText("Open new windows to display charts");
        MainWindowMenuBar.add(ChartsMenu);

        setJMenuBar(MainWindowMenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelContainerControls, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelContainerDisplay, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelContainerControls, javax.swing.GroupLayout.DEFAULT_SIZE, 746, Short.MAX_VALUE)
            .addComponent(jPanelContainerDisplay, javax.swing.GroupLayout.DEFAULT_SIZE, 746, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jPanelContainerDisplayComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jPanelContainerDisplayComponentResized
        int w = this.jPanelContainerDisplay.getWidth();
        int h = this.jPanelContainerDisplay.getHeight();
        if (this.gscUI != null) {
            this.gscUI.setPreferredSize (new java.awt.Dimension (w,h));
            this.gscUI.setBounds(0, 0, w, h);
            this.gscUI.repaint();
        }
    }//GEN-LAST:event_jPanelContainerDisplayComponentResized

    private void jPanelContainerDisplayMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanelContainerDisplayMouseDragged
        this.gscUI.handleMouseDrag(evt);
    }//GEN-LAST:event_jPanelContainerDisplayMouseDragged

    private void jPanelContainerDisplayMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanelContainerDisplayMousePressed
        this.gscUI.handleMousePressed(evt);
    }//GEN-LAST:event_jPanelContainerDisplayMousePressed

    private void jPanelContainerDisplayMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_jPanelContainerDisplayMouseWheelMoved
        this.gscUI.handleMouseWheelMoved(evt);
    }//GEN-LAST:event_jPanelContainerDisplayMouseWheelMoved

    private void jPanelContainerDisplayMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanelContainerDisplayMouseReleased
        this.gscUI.handleMouseReleased(evt);
    }//GEN-LAST:event_jPanelContainerDisplayMouseReleased

    private void jPanelContainerDisplayKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPanelContainerDisplayKeyPressed
        this.gscUI.handleKeyPressed(evt);
    }//GEN-LAST:event_jPanelContainerDisplayKeyPressed

    private void jPanelContainerDisplayKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPanelContainerDisplayKeyReleased
        this.gscUI.handleKeyReleased(evt);
    }//GEN-LAST:event_jPanelContainerDisplayKeyReleased

    private void AboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AboutMenuItemActionPerformed
        this.aboutWindow.setVisible(true);
    }//GEN-LAST:event_AboutMenuItemActionPerformed

    private void ExitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExitMenuItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event_ExitMenuItemActionPerformed

    private void HelpMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_HelpMenuItemActionPerformed
        this.helpWindow.setVisible(true);
    }//GEN-LAST:event_HelpMenuItemActionPerformed

    private void jSliderZoomStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSliderZoomStateChanged
        double amt = this.jSliderZoom.getModel().getValue();
        double scaling = 1.0;
        double outIncr = (1-GSComplexUI.ZOOM_MIN) / 50;
        double inIncr = (GSComplexUI.ZOOM_MAX - 1) / 50;
        
        if (amt < 50)
        {
            this.gscUI.setDisplayZoom(GSComplexUI.ZOOM_MIN + outIncr*amt, true, this.gscUI.gsc.getCenter().asPoint2D());
        }
        else if (amt > 50)
        {
            this.gscUI.setDisplayZoom(1 + inIncr*(amt-50), true, this.gscUI.gsc.getCenter().asPoint2D());
        } else {
            this.gscUI.setDisplayZoom(1, true, this.gscUI.gsc.getCenter().asPoint2D());
        }
        
        this.repaint();
    }//GEN-LAST:event_jSliderZoomStateChanged

    public void updateZoomSlider(double zoomValue) {
        double outIncr = (1-GSComplexUI.ZOOM_MIN) / 50;
        double inIncr = (GSComplexUI.ZOOM_MAX - 1) / 50;
        int sliderVal = 50;
        if (zoomValue < 1) {
            zoomValue -= GSComplexUI.ZOOM_MIN;
            sliderVal = (int) (zoomValue/outIncr);
        } else if (zoomValue > 1) {
            zoomValue -= 1;
            sliderVal = (int) (zoomValue/inIncr)+50;
        }
        this.jSliderZoom.getModel().setValue(sliderVal);
        this.repaint ();
    }
    
    private void jButtonUnzoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUnzoomActionPerformed
        this.gscUI.setDisplayZoom(1, true, this.gscUI.gsc.getCenter().asPoint2D());
        this.jSliderZoom.getModel().setValue(50);
        this.repaint ();
    }//GEN-LAST:event_jButtonUnzoomActionPerformed

    private void jButtonCenterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCenterActionPerformed
        this.gscUI.centerDisplay();
    }//GEN-LAST:event_jButtonCenterActionPerformed

    private void jPanelContainerDisplayMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanelContainerDisplayMouseClicked
        this.gscUI.handleMouseClicked(evt);
    }//GEN-LAST:event_jPanelContainerDisplayMouseClicked

    private void jButtonDeformApplyRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeformApplyRemoveActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonDeformApplyRemoveActionPerformed

    /**
     * set all the relevant deformation control and strain control values to reflect the given deformation
     * @param d 
     */
    public void updateDeformAndStrainControlsFromDeformation(Deformation d) {
        if (d.isIdentity()) {
            this.handleDeformationReset();
        } else
        if (d.isShearing()) {
            if (d.m10 != 0 ) { 
                this.setValueForDeformControlExclusively(this.jTextFieldShearX,d.m10*-1);
            } else {
                this.setValueForDeformControlExclusively(this.jTextFieldShearY,d.m01*-1);
            }            
        } else
        if (d.isScaling()) {
            if ((d.m00 != 1) || (d.m11 != 1)) { 
                this.setValueForDeformControlExclusively(this.jTextFieldCompressX,d.m00);
                this.setValueForDeformOrStrainControl(this.jTextFieldCompressY,d.m11);
            }
        } else
        if (d.isRotational()) {
            this.setValueForDeformControlExclusively(this.jTextFieldRotDeg,d.getRotAngleDegr());
            this.setValueForDeformOrStrainControl(this.jTextFieldRotRad,d.getRotAngleRad());
        }
        this.updateOtherControlsFromDeformControls();

        this.updateStrainControlsFromDeformation(d);
    }
    
    public void updateStrainControlsFromDeformation(Matrix2x2 d) {
        this.setValueForDeformOrStrainControl(this.jTextFieldStrainM00, d.m00);
        this.setValueForDeformOrStrainControl(this.jTextFieldStrainM01, d.m01*-1);
        this.setValueForDeformOrStrainControl(this.jTextFieldStrainM10, d.m10*-1);
        this.setValueForDeformOrStrainControl(this.jTextFieldStrainM11, d.m11);
    }

    
    public void updateOtherControlsFromDeformControls() {
        if (controlFieldHasDefaultValue(this.jTextFieldRotDeg)) {
            this.setValueForDeformOrStrainControl(this.jTextFieldStrainM00, Double.parseDouble(this.jTextFieldCompressX.getText()));
            this.setValueForDeformOrStrainControl(this.jTextFieldStrainM01, Double.parseDouble(this.jTextFieldShearY.getText()));
            this.setValueForDeformOrStrainControl(this.jTextFieldStrainM10, Double.parseDouble(this.jTextFieldShearX.getText()));
            this.setValueForDeformOrStrainControl(this.jTextFieldStrainM11, Double.parseDouble(this.jTextFieldCompressY.getText()));
            
            Deformation d = new Deformation(Double.parseDouble(this.jTextFieldCompressX.getText()),
                                            Double.parseDouble(this.jTextFieldShearY.getText()),
                                            Double.parseDouble(this.jTextFieldShearX.getText()),
                                            Double.parseDouble(this.jTextFieldCompressY.getText()));
            GSPebble s = new GSPebble(10,10);
            s.deform(d);
            this.setValueForDeformOrStrainControl(this.jTextFieldRFPhiCurrentRF, s.getMajorRadius()/s.getMinorRadius());
            this.setValueForDeformOrStrainControl(this.jTextFieldRFPhiCurrentPhi, Util.toDegrees(s.getTheta()*-1));
        } else {
            this.updateStrainControlsFromDeformation(Deformation.createFromAngle(Double.parseDouble(this.jTextFieldRotRad.getText())));
            this.setValueForDeformOrStrainControl(this.jTextFieldRFPhiCurrentRF, 1);
            this.setValueForDeformOrStrainControl(this.jTextFieldRFPhiCurrentPhi, Double.parseDouble(this.jTextFieldRotDeg.getText()));
        }
    }
        
    public void updateStrainControlsFromRFPhiControls() {
        Deformation dRF = Deformation.createFromRF(Double.parseDouble(this.jTextFieldRFPhiCurrentRF.getText()));
        Deformation dPhi = Deformation.createFromAngle(Double.parseDouble(this.jTextFieldRFPhiCurrentPhi.getText()));
        GSPebble s = new GSPebble(10,10);
        s.deform(dRF);
        s.deform(dPhi);
        this.updateStrainControlsFromDeformation(s.getMatrix());
    }
        
    private boolean controlFieldHasDefaultValue(javax.swing.JTextField controlField) {
        String curVal = controlField.getText();
        double defaultVal = ((ValueConstrainer) this.displayNumberConstraints.get(controlField)).getDefaultVal();
        return defaultVal == Double.parseDouble(curVal);
    }
    
    /**
     * set the given field to the given value
     * NOTE: safely handles null fields (sets no field / does nothing)
     * @param controlField
     * @param val 
     */
    private void setValueForDeformOrStrainControl(javax.swing.JTextField controlField, double val) {
        if (controlField != null) {
            controlField.setText(Util.truncForDisplay(val, ((ValueConstrainer) this.displayNumberConstraints.get(controlField)).getDisplayPrecision()));
        }
    }

    /**
     * set the given field to the given value, and resets all others
     * NOTE: safely handles null fields (sets no field and reset all)
     * @param controlField
     * @param val 
     */
    private void setValueForDeformControlExclusively(javax.swing.JTextField controlField, double val) {
        if (controlField != null) {
            controlField.setText(Util.truncForDisplay(val, ((ValueConstrainer) this.displayNumberConstraints.get(controlField)).getDisplayPrecision()));
        }
        this.clearOutDeformAndStrainControlFieldsOtherThan(controlField);
    }
        
    /**
     * resets all the deform control fields to the default that are set in the value contraints for those fields.
     * NOTE: safely handles a null (i.e. reset every deform control field)
     * @param stableControlField 
     */
    private void clearOutDeformAndStrainControlFieldsOtherThan(javax.swing.JTextField stableControlField) {
        Iterator keyIter = this.displayNumberConstraints.keySet().iterator();
        while(keyIter.hasNext()) {
            JTextField tf = (JTextField) keyIter.next();
            if (! tf.equals(stableControlField)) {
                tf.setText(Util.truncTextDecimal(Double.toString(((ValueConstrainer)this.displayNumberConstraints.get(tf)).getDefaultVal()), ((ValueConstrainer)this.displayNumberConstraints.get(tf)).getDisplayPrecision()));
            }
        }
        this.updateOtherControlsFromDeformControls();
    }
    
    private void jTextFieldShearXKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldShearXKeyPressed
        this.alterDeformValueByKeyPressInField(this.jTextFieldShearX, evt.getKeyCode());
    }//GEN-LAST:event_jTextFieldShearXKeyPressed

    private void jTextFieldShearXKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldShearXKeyReleased
        this.processKeyReleaseOnDeformValueField(this.jTextFieldShearX,evt);
    }//GEN-LAST:event_jTextFieldShearXKeyReleased

    private void jTextFieldShearYKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldShearYKeyPressed
        this.alterDeformValueByKeyPressInField(this.jTextFieldShearY, evt.getKeyCode());
    }//GEN-LAST:event_jTextFieldShearYKeyPressed

    private void jTextFieldShearYKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldShearYKeyReleased
        this.processKeyReleaseOnDeformValueField(this.jTextFieldShearY,evt);
    }//GEN-LAST:event_jTextFieldShearYKeyReleased

    private void jTextFieldCompressXKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldCompressXKeyPressed
        this.alterDeformValueByKeyPressInField(this.jTextFieldCompressX, evt.getKeyCode());
    }//GEN-LAST:event_jTextFieldCompressXKeyPressed

    private void jTextFieldCompressXKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldCompressXKeyReleased
        this.processKeyReleaseOnDeformValueField(this.jTextFieldCompressX,evt);
    }//GEN-LAST:event_jTextFieldCompressXKeyReleased

    private void jTextFieldCompressYKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldCompressYKeyPressed
        this.alterDeformValueByKeyPressInField(this.jTextFieldCompressY, evt.getKeyCode());
    }//GEN-LAST:event_jTextFieldCompressYKeyPressed

    private void jTextFieldCompressYKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldCompressYKeyReleased
        this.processKeyReleaseOnDeformValueField(this.jTextFieldCompressY,evt);
    }//GEN-LAST:event_jTextFieldCompressYKeyReleased

    private void jTextFieldRotDegKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldRotDegKeyPressed
        this.alterDeformValueByKeyPressInField(this.jTextFieldRotDeg, evt.getKeyCode());
    }//GEN-LAST:event_jTextFieldRotDegKeyPressed

    private void jTextFieldRotDegKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldRotDegKeyReleased
        this.processKeyReleaseOnDeformValueField(this.jTextFieldRotDeg,evt);
    }//GEN-LAST:event_jTextFieldRotDegKeyReleased

    private void jTextFieldRotRadKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldRotRadKeyPressed
        this.alterDeformValueByKeyPressInField(this.jTextFieldRotRad, evt.getKeyCode());
    }//GEN-LAST:event_jTextFieldRotRadKeyPressed

    private void jTextFieldRotRadKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldRotRadKeyReleased
        this.processKeyReleaseOnDeformValueField(this.jTextFieldRotRad,evt);
    }//GEN-LAST:event_jTextFieldRotRadKeyReleased

    private void jButtonDeformResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeformResetActionPerformed
        this.handleDeformationReset();
        this.gscUI.tentativeDeformationClear();
        this.gscUI.repaint();
    }//GEN-LAST:event_jButtonDeformResetActionPerformed

    private void jTextFieldShearXFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldShearXFocusLost
        this.processLostFocusOnDeformValueField(this.jTextFieldShearX);
    }//GEN-LAST:event_jTextFieldShearXFocusLost

    private void jTextFieldShearYFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldShearYFocusLost
        this.processLostFocusOnDeformValueField(this.jTextFieldShearY);
    }//GEN-LAST:event_jTextFieldShearYFocusLost

    private void jTextFieldCompressXFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldCompressXFocusLost
        this.processLostFocusOnDeformValueField(this.jTextFieldCompressX);
    }//GEN-LAST:event_jTextFieldCompressXFocusLost

    private void jTextFieldCompressYFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldCompressYFocusLost
        this.processLostFocusOnDeformValueField(this.jTextFieldCompressY);
    }//GEN-LAST:event_jTextFieldCompressYFocusLost

    private void jTextFieldRotDegFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldRotDegFocusLost
        this.processLostFocusOnDeformValueField(this.jTextFieldRotDeg);
    }//GEN-LAST:event_jTextFieldRotDegFocusLost

    private void jTextFieldRotRadFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldRotRadFocusLost
        this.processLostFocusOnDeformValueField(this.jTextFieldRotRad);
    }//GEN-LAST:event_jTextFieldRotRadFocusLost
    
    // TODO: consider how to generalize this support strain matrix controls (L1)
    private void processKeyReleaseOnDeformValueField(javax.swing.JTextField controlField, java.awt.event.KeyEvent evt) {
        if (! this.keyCodeIgnoredOnRelease(evt.getKeyCode())) {
            if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                this.handleDeformControlActivation(controlField);
            }
        }
    }
    
    // TODO: consider how to generalize this support strain matrix controls (L1)
    private void processLostFocusOnDeformValueField(javax.swing.JTextField controlField) {        
        ValueConstrainer vc = (ValueConstrainer) this.displayNumberConstraints.get(controlField);
        if (vc.getDefaultVal() != Double.parseDouble(controlField.getText())) {
            this.handleDeformControlActivation(controlField);
        }
    }
        
    // TODO: consider how to generalize this support strain matrix controls (L1)
    private void handleDeformControlActivation(javax.swing.JTextField controlField) {
        ValueConstrainer vc = (ValueConstrainer) this.displayNumberConstraints.get(controlField);
        Util.sanitizeForDoubleNumberFormat(controlField, vc.getDisplayPrecision());
        double d = Double.parseDouble(controlField.getText());
        if (vc.isOutOfRange(d)) {
            controlField.setText(Util.truncForDisplay(vc.constrain(d), vc.getDisplayPrecision()));
        }
        this.handleDeformControlChange(controlField);
    }
    
    private boolean keyCodeIgnoredOnRelease(int kc) {
        return 
            (kc == java.awt.event.KeyEvent.VK_UP) ||
            (kc == java.awt.event.KeyEvent.VK_DOWN) ||
            (kc == java.awt.event.KeyEvent.VK_LEFT) ||
            (kc == java.awt.event.KeyEvent.VK_RIGHT) ||
            (kc == java.awt.event.KeyEvent.VK_SHIFT) ||
            (kc == java.awt.event.KeyEvent.VK_ALT) ||
            (kc == java.awt.event.KeyEvent.VK_CONTROL) ||
            (kc == java.awt.event.KeyEvent.VK_HOME) ||
            (kc == java.awt.event.KeyEvent.VK_END);
    }
    
    // TODO: consider how to generalize this support strain matrix controls (L1)
    private void alterDeformValueByKeyPressInField(javax.swing.JTextField theField,int keyCode) {
//        System.out.println("alterDefVal using key code: "+keyCode);
        String initialFieldText = theField.getText();
//        System.out.println("initial text: "+initialFieldText);
        ValueConstrainer vc = (ValueConstrainer) this.displayNumberConstraints.get(theField);
        if (keyCode == java.awt.event.KeyEvent.VK_UP) {
//            System.out.println("up key");
            Util.fieldValueUp(theField, vc);
            this.handleDeformControlChange(theField);
        } else if (keyCode == java.awt.event.KeyEvent.VK_DOWN) {
//            System.out.println("down key");
            Util.fieldValueDown(theField, vc);
            this.handleDeformControlChange(theField);
        }
    }
    
    private void handleDeformationReset() {
        this.clearOutDeformAndStrainControlFieldsOtherThan(null);
        this.enableDeformControls();
        this.enableStrainMatrixControls();
        this.enableRfPhiControls();
    }
    
    // TODO: genearl to handle strain field (L2)
    private void handleDeformControlChange(javax.swing.JTextField theField) {
        this.clearOutDeformAndStrainControlFieldsOtherThan(theField);
        this.setDeformAndStrainFieldsLinkedToThisField(theField);
        this.updateGSCUITentativeDeformBasedOn(theField);
        if (this.gscUI.isTentativeDeformationCleared()) {
            this.enableDeformControls();
            this.enableStrainMatrixControls();
            this.enableRfPhiControls();
        } else {
            this.disableStrainMatrixControls();
            this.disableRfPhiControls();
        }
    }
    
    // TODO: genearl to handle strain field (L3)
    private void setDeformAndStrainFieldsLinkedToThisField(javax.swing.JTextField theField) {
        if (this.jTextFieldShearX.equals(theField)) {
            this.updateOtherControlsFromDeformControls();
        } else
        if (this.jTextFieldShearY.equals(theField)) {
            this.updateOtherControlsFromDeformControls();
        } else
        if (this.jTextFieldCompressX.equals(theField)) {
            this.setValueForDeformOrStrainControl(this.jTextFieldCompressY, 1/Double.parseDouble(this.jTextFieldCompressX.getText()));
            this.updateOtherControlsFromDeformControls();
        } else
        if (this.jTextFieldCompressY.equals(theField)) {
            this.setValueForDeformOrStrainControl(this.jTextFieldCompressX, 1/Double.parseDouble(this.jTextFieldCompressY.getText()));
            this.updateOtherControlsFromDeformControls();
        } else
        if (this.jTextFieldRotDeg.equals(theField)) {
            this.setValueForDeformOrStrainControl(this.jTextFieldRotRad, Util.toRadians(Double.parseDouble(this.jTextFieldRotDeg.getText())));
            this.updateOtherControlsFromDeformControls();
        } else
        if (this.jTextFieldRotRad.equals(theField)) {
            this.setValueForDeformOrStrainControl(this.jTextFieldRotDeg, Util.toDegrees(Double.parseDouble(this.jTextFieldRotRad.getText())));
            this.updateOtherControlsFromDeformControls();
        }
    }
    
    private void updateGSCUITentativeDeformBasedOn(javax.swing.JTextField theField) {
        if ((this.jTextFieldShearX.equals(theField)) || (theField.equals(this.jTextFieldShearY))) {
            this.gscUI.tentativeDeformationSetToShear(Double.parseDouble(this.jTextFieldShearX.getText()), 
                                                      Double.parseDouble(this.jTextFieldShearY.getText()),
                                                      theField.equals(this.jTextFieldShearX));
        } else
        if ((this.jTextFieldCompressX.equals(theField)) || (theField.equals(this.jTextFieldCompressY))) {
            this.gscUI.tentativeDeformationSetToCompression(Double.parseDouble(this.jTextFieldCompressX.getText()), 
                                                            Double.parseDouble(this.jTextFieldCompressY.getText()),
                                                            theField.equals(this.jTextFieldCompressX));
        } else
        if (this.jTextFieldRotDeg.equals(theField)) {
            this.gscUI.tentativeDeformationSetToRotate(Util.toRadians(Double.parseDouble(this.jTextFieldRotDeg.getText())));
        } else
        if (this.jTextFieldRotRad.equals(theField)) {
            this.gscUI.tentativeDeformationSetToRotate(Double.parseDouble(this.jTextFieldRotRad.getText()));
        }
        this.gscUI.repaint();
    }

    
    private void disableDeformControls() {
        this.setEnableOnDeformControls(false);
    }
    private void enableDeformControls() {
        this.setEnableOnDeformControls(true);
    }
    private void setEnableOnDeformControls(boolean state) {
        this.jTextFieldShearX.setEnabled(state);
        this.jTextFieldShearY.setEnabled(state);
        this.jTextFieldCompressX.setEnabled(state);
        this.jTextFieldCompressY.setEnabled(state);
        this.jTextFieldRotDeg.setEnabled(state);
        this.jTextFieldRotRad.setEnabled(state);
    }
    
        
    private void disableStrainMatrixControls() {
        this.setEnableOnStrainMatrixControls(false);
    }
    private void enableStrainMatrixControls() {
        this.setEnableOnStrainMatrixControls(true);
    }
    private void setEnableOnStrainMatrixControls(boolean state) {
        this.jTextFieldStrainM00.setEnabled(state);
        this.jTextFieldStrainM01.setEnabled(state);
        this.jTextFieldStrainM10.setEnabled(state);
        this.jTextFieldStrainM11.setEnabled(state);
    }

    private void disableRfPhiControls() {
        this.setEnableOnRfPhiControls(false);
    }
    private void enableRfPhiControls() {
        this.setEnableOnRfPhiControls(true);
    }
    private void setEnableOnRfPhiControls(boolean state) {
        this.jTextFieldRFPhiCurrentRF.setEnabled(state);
        this.jTextFieldRFPhiCurrentPhi.setEnabled(state);
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
            @Override
            public void run() {
                new MainWindow().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem AboutMenuItem;
    private javax.swing.JMenu ChartsMenu;
    private javax.swing.JMenu DisplayMenu;
    private javax.swing.JMenuItem ExitMenuItem;
    private javax.swing.JMenu FileMenu;
    private javax.swing.JMenu GeoshearMenu;
    private javax.swing.JMenuItem HelpMenuItem;
    private javax.swing.JMenuBar MainWindowMenuBar;
    private javax.swing.JButton jButtonCenter;
    private javax.swing.JButton jButtonDeformApplyRemove;
    private javax.swing.JButton jButtonDeformReset;
    private javax.swing.JButton jButtonLinkCompressionDeform;
    private javax.swing.JButton jButtonStrainNavNext;
    private javax.swing.JButton jButtonStrainNavPrevious;
    private javax.swing.JButton jButtonUnzoom;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabelCompressControl;
    private javax.swing.JLabel jLabelCompressX;
    private javax.swing.JLabel jLabelCompressrY;
    private javax.swing.JLabel jLabelShearControl;
    private javax.swing.JLabel jLabelShearControl1;
    private javax.swing.JLabel jLabelShearX;
    private javax.swing.JLabel jLabelShearX1;
    private javax.swing.JLabel jLabelShearY;
    private javax.swing.JLabel jLabelShearY1;
    private javax.swing.JLabel jLabelStrainNavCount;
    private javax.swing.JLabel jLabelStrainNavPosition;
    private javax.swing.JLabel jLabelZoom;
    private javax.swing.JPanel jPanelContainerControls;
    private javax.swing.JPanel jPanelContainerDisplay;
    private javax.swing.JPanel jPanelDeformCompressControls;
    private javax.swing.JPanel jPanelDeformControls;
    private javax.swing.JPanel jPanelDeformMatrixLeft;
    private javax.swing.JPanel jPanelDeformMatrixRight;
    private javax.swing.JPanel jPanelDeformNavControls;
    private javax.swing.JPanel jPanelDeformRotateControls;
    private javax.swing.JPanel jPanelDeformShearControls;
    private javax.swing.JPanel jPanelDisplayControls;
    private javax.swing.JPanel jPanelEditPebbleControls;
    private javax.swing.JPanel jPanelResetButtons;
    private javax.swing.JPanel jPanelSnapshotControls;
    private javax.swing.JPanel jPanelStrainControls;
    private javax.swing.JPanel jPanelZoomControl;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JSlider jSliderZoom;
    private javax.swing.JTextField jTextFieldCompressX;
    private javax.swing.JTextField jTextFieldCompressY;
    private javax.swing.JTextField jTextFieldRFPhiCurrentPhi;
    private javax.swing.JTextField jTextFieldRFPhiCurrentRF;
    private javax.swing.JTextField jTextFieldRotDeg;
    private javax.swing.JTextField jTextFieldRotRad;
    private javax.swing.JTextField jTextFieldShearX;
    private javax.swing.JTextField jTextFieldShearY;
    private javax.swing.JTextField jTextFieldStrainCumuPhi;
    private javax.swing.JTextField jTextFieldStrainCumuRF;
    private javax.swing.JTextField jTextFieldStrainM00;
    private javax.swing.JTextField jTextFieldStrainM01;
    private javax.swing.JTextField jTextFieldStrainM10;
    private javax.swing.JTextField jTextFieldStrainM11;
    // End of variables declaration//GEN-END:variables
}
