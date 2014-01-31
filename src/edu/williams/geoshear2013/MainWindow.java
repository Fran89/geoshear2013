package edu.williams.geoshear2013;

import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
/*
 * TODO:
 *  - (3) OPTIONAL implement change tracks in cartesian chart
 *  - (.5) OPTIONAL implement change track in polar chart
 *  - (2) OPTIONAL in main window gscUI, implement pebble dragging when in edit mode (control down)
 *  - (1) OPTIONAL in main window gscUI, implement pebble rotation when in edit mode (alt down)
 *  - (.5) OPTIONAL in main window gscUI, add a confirm dialog for pebble deletion when in edit mode
 * 
 */

/**
 * GeoShear is a structural geology tool to explore and analyze shear, compression, and rotation deformations of sets of ellipses, which represent outlines of cross-sections of pebbles in conglomerate rocks.
 * 
 * @author cwarren
 */
public class MainWindow extends javax.swing.JFrame {
    private GSComplexUI gscUI;
    private HelpWindow helpWindow;
    private AboutWindow aboutWindow;
    private AutoColorOptionsDialog autoColorOptions;

    private HashMap displayNumberConstraints;
    
    public static String LABEL_DEFORM_APPLY = "Apply";
    public static String LABEL_DEFORM_REMOVE = "Remove";

    private boolean cachedStrainNavPrevEnableState = false;
    private boolean cachedStrainNavNextEnableState = false;

//    private final JFileChooser fileChooser = new JFileChooser ();
    private final FileDialog fileDialog;
    private final FileFilterImage filterImage = new FileFilterImage();
    private final FileFilterTab filterTab = new FileFilterTab();
    private final FileFilterGeoShear filterGeoShear = new FileFilterGeoShear();
    public static int FILE_IO_TYPE_GES = 1;
    public static int FILE_IO_TYPE_TAB = 2;
    public static int FILE_IO_TYPE_IMG = 3;
    
    private GSComplexChartFrameCartRfPhi chartCartRfPhi;
    private GSComplexChartFramePolarRfPhi chartPolarRfPhi;
    private GSComplexInfoFrameDeformationSeries windowDeformationsSeries;
    
    private ImageIcon linkedIcon = new javax.swing.ImageIcon(getClass().getResource("/edu/williams/geoshear2013/img/linked.gif"));
    private String linkedToolTip = "x and y compression are linked to preserve area";
    private ImageIcon unlinkedIcon = new javax.swing.ImageIcon(getClass().getResource("/edu/williams/geoshear2013/img/unlinked.gif"));
    private String unlinkedToolTip = "x and y compression are independent";
    
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

        this.fileDialog = new FileDialog(this);

        //        this.jButtonLinkCompressionDeform.setVisible(false);

        
        this.displayNumberConstraints = new HashMap();

        this.displayNumberConstraints.put(this.jTextFieldShearX, new ValueConstrainer(-10, -10, 10, 10, .01, 3, 0));
        this.displayNumberConstraints.put(this.jTextFieldShearY, new ValueConstrainer(-10, -10, 10, 10, .01, 3, 0));
        this.displayNumberConstraints.put(this.jTextFieldCompressX, new ValueConstrainer(.1, .1, 10, 10, .01, 3, 1));
        this.displayNumberConstraints.put(this.jTextFieldCompressY, new ValueConstrainer(.1, .1, 10, 10, .01, 3, 1));
        this.displayNumberConstraints.put(this.jTextFieldRotDeg, new ValueConstrainer(-180, 180, 180, -180, 1, 2, 0, ValueConstrainer.CONSTRAINT_WRAP));
        this.displayNumberConstraints.put(this.jTextFieldRotRad, new ValueConstrainer(-1*Math.PI, Math.PI, Math.PI, -1*Math.PI, .01, 3, 0, ValueConstrainer.CONSTRAINT_WRAP));

        this.displayNumberConstraints.put(this.jTextFieldRFPhiCurrentRF, new ValueConstrainer(1, 1, 1000, 1000, .01, 3, 1));
        this.displayNumberConstraints.put(this.jTextFieldRFPhiCurrentPhi, new ValueConstrainer(-90, 90, 90, -90, 1, 2, 0, ValueConstrainer.CONSTRAINT_WRAP));
        
        this.displayNumberConstraints.put(this.jTextFieldStrainM00, new ValueConstrainer(-10, -10, 10, 10, .01, 3, 1));
        this.displayNumberConstraints.put(this.jTextFieldStrainM01, new ValueConstrainer(-10, -10, 10, 10, .01, 3, 0));
        this.displayNumberConstraints.put(this.jTextFieldStrainM10, new ValueConstrainer(-10, -10, 10, 10, .01, 3, 0));
        this.displayNumberConstraints.put(this.jTextFieldStrainM11, new ValueConstrainer(-10, -10, 10, 10, .01, 3, 1));

        this.displayNumberConstraints.put(this.jTextFieldNavStrainM00, new ValueConstrainer(-10, -10, 10, 10, .01, 3, 1));
        this.displayNumberConstraints.put(this.jTextFieldNavStrainM01, new ValueConstrainer(-10, -10, 10, 10, .01, 3, 0));
        this.displayNumberConstraints.put(this.jTextFieldNavStrainM10, new ValueConstrainer(-10, -10, 10, 10, .01, 3, 0));
        this.displayNumberConstraints.put(this.jTextFieldNavStrainM11, new ValueConstrainer(-10, -10, 10, 10, .01, 3, 1));

        this.displayNumberConstraints.put(this.jTextFieldStrainCumuRF, new ValueConstrainer(1, 1, 1000, 1000, .01, 3, 1));
        this.displayNumberConstraints.put(this.jTextFieldStrainCumuPhi, new ValueConstrainer(-90, 90, 90, -90, 1, 2, 0, ValueConstrainer.CONSTRAINT_WRAP));
        this.displayNumberConstraints.put(this.jTextFieldStrainCumuTentRF, new ValueConstrainer(1, 1, 1000, 1000, .01, 3, 1));
        this.displayNumberConstraints.put(this.jTextFieldStrainCumuTentPhi, new ValueConstrainer(-90, 90, 90, -90, 1, 2, 0, ValueConstrainer.CONSTRAINT_WRAP));

        Iterator keyIter = this.displayNumberConstraints.keySet().iterator();
        while(keyIter.hasNext()) {
            JTextField tf = (JTextField) keyIter.next();
            tf.setText(Util.truncTextDecimal(Double.toString(((ValueConstrainer)this.displayNumberConstraints.get(tf)).getDefaultVal()), ((ValueConstrainer)this.displayNumberConstraints.get(tf)).getDisplayPrecision()));
        }
                
        this.helpWindow = new HelpWindow();
        this.helpWindow.setLocationByPlatform(true);
        this.helpWindow.setSize(this.jPanelContainerDisplay.getWidth() + 700,this.jPanelContainerDisplay.getHeight() + 50);
        
        this.aboutWindow = new AboutWindow();
        this.aboutWindow.setLocationByPlatform(true);
        
        this.autoColorOptions = new AutoColorOptionsDialog(this);
        this.autoColorOptions.setLocationByPlatform(true);
        
        this.gscUI = new GSComplexUI(new GSComplex(),this);
        this.initializeGscUI();
        this.jPanelContainerDisplay.add(this.gscUI);

        this.updateDeformNavControlsStates();
        this.updateStateOfCurrentDeformControls();
        this.updateStrainMatricesVisibilities();
        
        jButtonLinkCompressionDeform.setToolTipText(this.linkedToolTip);
        jButtonLinkCompressionDeform.setIcon(linkedIcon);

        this.chartCartRfPhi = new GSComplexChartFrameCartRfPhi(this);
        this.gscUI.gsc.addWatcher(this.chartCartRfPhi);
        this.gscUI.addWatcher(this.chartCartRfPhi);
        
        this.chartPolarRfPhi = new GSComplexChartFramePolarRfPhi(this);
        this.gscUI.gsc.addWatcher(this.chartPolarRfPhi);
        this.gscUI.addWatcher(this.chartPolarRfPhi);
        while (this.chartPolarRfPhi.chart.getWidth() < 1) {
            // there were timing problems where occasionally the program would crash on start up because of some internal timing issue where the width would nto be set before the code needed it to be non-zero
        }
        this.chartPolarRfPhi.setChartScaleTypeToLog();
        
        this.windowDeformationsSeries = new GSComplexInfoFrameDeformationSeries(this);
        this.windowDeformationsSeries.markCurrentDeformation(this.gscUI.gsc.getCurrentDeformationNumber());
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

        jLabel14 = new javax.swing.JLabel();
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
        jLabelSimpleShearActionKey = new javax.swing.JLabel();
        jPanelDeformCompressControls = new javax.swing.JPanel();
        jLabelCompressControl = new javax.swing.JLabel();
        jTextFieldCompressX = new javax.swing.JTextField();
        jLabelCompressX = new javax.swing.JLabel();
        jLabelCompressrY = new javax.swing.JLabel();
        jTextFieldCompressY = new javax.swing.JTextField();
        jButtonLinkCompressionDeform = new javax.swing.JButton();
        jLabelPureShearActionKey = new javax.swing.JLabel();
        jPanelDeformRotateControls = new javax.swing.JPanel();
        jLabelShearControl1 = new javax.swing.JLabel();
        jLabelShearY1 = new javax.swing.JLabel();
        jLabelShearX1 = new javax.swing.JLabel();
        jTextFieldRotDeg = new javax.swing.JTextField();
        jTextFieldRotRad = new javax.swing.JTextField();
        jLabelRotateActionKey = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jPanelDeformNavControls = new javax.swing.JPanel();
        jButtonDeformApplyRemove = new javax.swing.JButton();
        jButtonDeformReset = new javax.swing.JButton();
        jPanelDeformMatrixLeft = new javax.swing.JPanel();
        jTextFieldStrainM00 = new javax.swing.JTextField();
        jTextFieldStrainM10 = new javax.swing.JTextField();
        jTextFieldRFPhiCurrentRF = new javax.swing.JTextField();
        jLabelRf = new javax.swing.JLabel();
        jLabelCurStrainLeftBracket = new javax.swing.JLabel();
        jTextFieldStrainCumuRF = new javax.swing.JTextField();
        jLabelCumuRF = new javax.swing.JLabel();
        jLabelStrainNavPosition = new javax.swing.JLabel();
        jLabelCumuStrainM00 = new javax.swing.JLabel();
        jLabelCumuStrainM01 = new javax.swing.JLabel();
        jLabelCumuStrainM10 = new javax.swing.JLabel();
        jLabelCumuStrainM11 = new javax.swing.JLabel();
        jLabelCumuStrainRightBracket = new javax.swing.JLabel();
        jLabelCumuStrainLeftBracket = new javax.swing.JLabel();
        jTextFieldStrainCumuPhi = new javax.swing.JTextField();
        jLabelCumuPhi = new javax.swing.JLabel();
        jButtonStrainNavPrevious = new javax.swing.JButton();
        jLabelNavStrainLeftBracket = new javax.swing.JLabel();
        jTextFieldNavStrainM00 = new javax.swing.JTextField();
        jTextFieldNavStrainM10 = new javax.swing.JTextField();
        jPanelMatrixBgMostRecent = new javax.swing.JPanel();
        jPanelMatrixBgCumu = new javax.swing.JPanel();
        jPanelDeformMatrixRight = new javax.swing.JPanel();
        jButtonStrainNavNext = new javax.swing.JButton();
        jTextFieldStrainM01 = new javax.swing.JTextField();
        jTextFieldStrainM11 = new javax.swing.JTextField();
        jTextFieldRFPhiCurrentPhi = new javax.swing.JTextField();
        jLabelPhi = new javax.swing.JLabel();
        jLabelCurStrainRightBracket = new javax.swing.JLabel();
        jLabelStrainNavCount = new javax.swing.JLabel();
        jLabelCumuTentStrainLeftBracket = new javax.swing.JLabel();
        jLabelCumuTentStrainM00 = new javax.swing.JLabel();
        jLabelCumuTentStrainM10 = new javax.swing.JLabel();
        jLabelCumuTentStrainM11 = new javax.swing.JLabel();
        jLabelCumuTentStrainM01 = new javax.swing.JLabel();
        jLabelCumuTentStrainRightBracket = new javax.swing.JLabel();
        jLabelCumuTentRF = new javax.swing.JLabel();
        jTextFieldStrainCumuTentRF = new javax.swing.JTextField();
        jTextFieldStrainCumuTentPhi = new javax.swing.JTextField();
        jLabelCumuTentPhi = new javax.swing.JLabel();
        jTextFieldNavStrainM01 = new javax.swing.JTextField();
        jLabelNavStrainRightBracket = new javax.swing.JLabel();
        jTextFieldNavStrainM11 = new javax.swing.JTextField();
        jPanelMatrixBgCumuTent = new javax.swing.JPanel();
        jPanelEditPebbleControls = new javax.swing.JPanel();
        jToggleButtonEditPebbles = new javax.swing.JToggleButton();
        jButtonPebbleColorSet = new javax.swing.JButton();
        jButtonPebbleColorApply = new javax.swing.JButton();
        jButtonBackgroundImage = new javax.swing.JButton();
        jButtonAutoColorOnPhi = new javax.swing.JButton();
        jButtonAutoColorOnRf = new javax.swing.JButton();
        jPanelSnapshotControls = new javax.swing.JPanel();
        jButtonSnapshotter = new javax.swing.JButton();
        MainWindowMenuBar = new javax.swing.JMenuBar();
        GeoshearMenu = new javax.swing.JMenu();
        HelpMenuItem = new javax.swing.JMenuItem();
        AboutMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        ExitMenuItem = new javax.swing.JMenuItem();
        FileMenu = new javax.swing.JMenu();
        jMenuItemSave = new javax.swing.JMenuItem();
        jMenuItemLoad = new javax.swing.JMenuItem();
        jMenuItemSaveCurrentDeformed = new javax.swing.JMenuItem();
        DisplayMenu = new javax.swing.JMenu();
        jCheckBoxMenuItemShowPebbleAxes = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItemFillPebbles = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItemShowBackgroundAxis = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItemShowBackgroundImage = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItemShowStrainEllipses = new javax.swing.JCheckBoxMenuItem();
        ChartsMenu = new javax.swing.JMenu();
        jMenuItemChartRfPhiCart = new javax.swing.JMenuItem();
        jMenuItemChartRf2PhiPolar = new javax.swing.JMenuItem();
        jMenuItemChartDeformationSeries = new javax.swing.JMenuItem();

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel14.setText("[");

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
        jLabelZoom.setFocusable(false);
        jPanelZoomControl.add(jLabelZoom);

        jSliderZoom.setMajorTickSpacing(50);
        jSliderZoom.setMinorTickSpacing(5);
        jSliderZoom.setPaintTicks(true);
        jSliderZoom.setToolTipText("left to zoom out, right to zoom in");
        jSliderZoom.setNextFocusableComponent(jButtonUnzoom);
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
        jButtonUnzoom.setToolTipText("reset zoom level to 1x");
        jButtonUnzoom.setMargin(new java.awt.Insets(2, 8, 2, 8));
        jButtonUnzoom.setNextFocusableComponent(jButtonCenter);
        jButtonUnzoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUnzoomActionPerformed(evt);
            }
        });
        jPanelResetButtons.add(jButtonUnzoom);

        jButtonCenter.setText("Center in view");
        jButtonCenter.setToolTipText("remove all view panning/shifting");
        jButtonCenter.setMargin(new java.awt.Insets(2, 8, 2, 8));
        jButtonCenter.setNextFocusableComponent(jTextFieldShearX);
        jButtonCenter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCenterActionPerformed(evt);
            }
        });
        jPanelResetButtons.add(jButtonCenter);

        jPanelDisplayControls.add(jPanelResetButtons);

        jPanelContainerControls.add(jPanelDisplayControls);

        jPanelDeformControls.setAutoscrolls(true);
        jPanelDeformControls.setMaximumSize(new java.awt.Dimension(5000, 5000));
        jPanelDeformControls.setMinimumSize(new java.awt.Dimension(1, 1));
        jPanelDeformControls.setOpaque(false);
        jPanelDeformControls.setPreferredSize(new java.awt.Dimension(220, 375));
        jPanelDeformControls.setRequestFocusEnabled(false);
        jPanelDeformControls.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanelDeformShearControls.setMaximumSize(new java.awt.Dimension(100, 75));
        jPanelDeformShearControls.setMinimumSize(new java.awt.Dimension(100, 75));
        jPanelDeformShearControls.setPreferredSize(new java.awt.Dimension(100, 75));
        jPanelDeformShearControls.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabelShearControl.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelShearControl.setText("simple shear");
        jLabelShearControl.setToolTipText("hold SHIFT to do this using the mouse");
        jLabelShearControl.setFocusable(false);
        jLabelShearControl.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jPanelDeformShearControls.add(jLabelShearControl, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 1, 80, -1));

        jTextFieldShearX.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldShearX.setText("0.000");
        jTextFieldShearX.setToolTipText("set the x shear value in the deformation matrix");
        jTextFieldShearX.setBorder(null);
        jTextFieldShearX.setMaximumSize(new java.awt.Dimension(42, 14));
        jTextFieldShearX.setMinimumSize(new java.awt.Dimension(42, 14));
        jTextFieldShearX.setNextFocusableComponent(jTextFieldShearY);
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
        jPanelDeformShearControls.add(jTextFieldShearX, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, -1, -1));

        jLabelShearX.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelShearX.setText("X");
        jLabelShearX.setFocusable(false);
        jPanelDeformShearControls.add(jLabelShearX, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 14, -1));

        jLabelShearY.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelShearY.setText("Y");
        jLabelShearY.setFocusable(false);
        jPanelDeformShearControls.add(jLabelShearY, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 14, -1));

        jTextFieldShearY.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldShearY.setText("0.000");
        jTextFieldShearY.setToolTipText("set the y shear value in the deformation matrix");
        jTextFieldShearY.setBorder(null);
        jTextFieldShearY.setMaximumSize(new java.awt.Dimension(42, 14));
        jTextFieldShearY.setMinimumSize(new java.awt.Dimension(42, 14));
        jTextFieldShearY.setNextFocusableComponent(jTextFieldCompressX);
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
        jPanelDeformShearControls.add(jTextFieldShearY, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, -1, -1));

        jLabelSimpleShearActionKey.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelSimpleShearActionKey.setText("(SHIFT)");
        jPanelDeformShearControls.add(jLabelSimpleShearActionKey, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 80, -1));

        jPanelDeformControls.add(jPanelDeformShearControls, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 1, 110, 79));

        jPanelDeformCompressControls.setMaximumSize(new java.awt.Dimension(100, 75));
        jPanelDeformCompressControls.setMinimumSize(new java.awt.Dimension(100, 75));
        jPanelDeformCompressControls.setPreferredSize(new java.awt.Dimension(100, 75));
        jPanelDeformCompressControls.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabelCompressControl.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelCompressControl.setText("pure shear");
        jLabelCompressControl.setToolTipText("hold CONTROL to do this using the mouse");
        jLabelCompressControl.setFocusable(false);
        jLabelCompressControl.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jPanelDeformCompressControls.add(jLabelCompressControl, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 1, 66, -1));

        jTextFieldCompressX.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldCompressX.setText("1");
        jTextFieldCompressX.setToolTipText("set the x and y compression values in the deformation matrix");
        jTextFieldCompressX.setBorder(null);
        jTextFieldCompressX.setMaximumSize(new java.awt.Dimension(42, 14));
        jTextFieldCompressX.setMinimumSize(new java.awt.Dimension(42, 14));
        jTextFieldCompressX.setNextFocusableComponent(jTextFieldCompressY);
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
        jPanelDeformCompressControls.add(jTextFieldCompressX, new org.netbeans.lib.awtextra.AbsoluteConstraints(24, 40, -1, -1));

        jLabelCompressX.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelCompressX.setText("X");
        jLabelCompressX.setFocusable(false);
        jPanelDeformCompressControls.add(jLabelCompressX, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 40, 14, -1));

        jLabelCompressrY.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelCompressrY.setText("Y");
        jLabelCompressrY.setFocusable(false);
        jPanelDeformCompressControls.add(jLabelCompressrY, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 60, 14, -1));

        jTextFieldCompressY.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldCompressY.setText("1");
        jTextFieldCompressY.setToolTipText("set the x and y compression values in the deformation matrix");
        jTextFieldCompressY.setBorder(null);
        jTextFieldCompressY.setMaximumSize(new java.awt.Dimension(42, 14));
        jTextFieldCompressY.setMinimumSize(new java.awt.Dimension(42, 14));
        jTextFieldCompressY.setNextFocusableComponent(jButtonLinkCompressionDeform);
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
        jPanelDeformCompressControls.add(jTextFieldCompressY, new org.netbeans.lib.awtextra.AbsoluteConstraints(24, 60, -1, -1));

        jButtonLinkCompressionDeform.setIcon(new javax.swing.ImageIcon(getClass().getResource("/edu/williams/geoshear2013/img/linked.gif"))); // NOI18N
        jButtonLinkCompressionDeform.setToolTipText("x and y compression are linked to preserve area");
        jButtonLinkCompressionDeform.setNextFocusableComponent(jTextFieldRotDeg);
        jButtonLinkCompressionDeform.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLinkCompressionDeformActionPerformed(evt);
            }
        });
        jPanelDeformCompressControls.add(jButtonLinkCompressionDeform, new org.netbeans.lib.awtextra.AbsoluteConstraints(72, 26, 23, -1));

        jLabelPureShearActionKey.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelPureShearActionKey.setText("(CTRL)");
        jPanelDeformCompressControls.add(jLabelPureShearActionKey, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 60, -1));

        jPanelDeformControls.add(jPanelDeformCompressControls, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 1, 110, 79));

        jPanelDeformRotateControls.setMaximumSize(new java.awt.Dimension(100, 75));
        jPanelDeformRotateControls.setMinimumSize(new java.awt.Dimension(100, 75));
        jPanelDeformRotateControls.setPreferredSize(new java.awt.Dimension(100, 75));
        jPanelDeformRotateControls.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabelShearControl1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelShearControl1.setText("rotation");
        jLabelShearControl1.setToolTipText("hold ALT to do this using the mouse");
        jLabelShearControl1.setFocusable(false);
        jLabelShearControl1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jPanelDeformRotateControls.add(jLabelShearControl1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 1, 68, -1));

        jLabelShearY1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelShearY1.setText("rad.");
        jLabelShearY1.setFocusable(false);
        jPanelDeformRotateControls.add(jLabelShearY1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 60, 22, -1));

        jLabelShearX1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelShearX1.setText("deg.");
        jLabelShearX1.setFocusable(false);
        jPanelDeformRotateControls.add(jLabelShearX1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 40, -1, -1));

        jTextFieldRotDeg.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldRotDeg.setText("0.0");
        jTextFieldRotDeg.setToolTipText("set the rotation angle in degrees");
        jTextFieldRotDeg.setBorder(null);
        jTextFieldRotDeg.setMaximumSize(new java.awt.Dimension(42, 14));
        jTextFieldRotDeg.setMinimumSize(new java.awt.Dimension(42, 14));
        jTextFieldRotDeg.setNextFocusableComponent(jTextFieldRotRad);
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
        jPanelDeformRotateControls.add(jTextFieldRotDeg, new org.netbeans.lib.awtextra.AbsoluteConstraints(27, 40, -1, -1));

        jTextFieldRotRad.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldRotRad.setText("0.000");
        jTextFieldRotRad.setToolTipText("set the rotation angle in radians");
        jTextFieldRotRad.setBorder(null);
        jTextFieldRotRad.setMaximumSize(new java.awt.Dimension(42, 14));
        jTextFieldRotRad.setMinimumSize(new java.awt.Dimension(42, 14));
        jTextFieldRotRad.setNextFocusableComponent(jButtonDeformApplyRemove);
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
        jPanelDeformRotateControls.add(jTextFieldRotRad, new org.netbeans.lib.awtextra.AbsoluteConstraints(27, 60, -1, -1));

        jLabelRotateActionKey.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelRotateActionKey.setText("(ALT/OPT)");
        jPanelDeformRotateControls.add(jLabelRotateActionKey, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 70, -1));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/edu/williams/geoshear2013/img/linked.gif"))); // NOI18N
        jLabel1.setToolTipText("values for degrees and radians are always linked");
        jLabel1.setEnabled(false);
        jPanelDeformRotateControls.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(71, 37, 20, 40));

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
        jButtonDeformApplyRemove.setNextFocusableComponent(jButtonDeformReset);
        jButtonDeformApplyRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeformApplyRemoveActionPerformed(evt);
            }
        });
        jPanelDeformNavControls.add(jButtonDeformApplyRemove, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 80, 40));

        jButtonDeformReset.setText("Reset");
        jButtonDeformReset.setToolTipText("remove/cancel the tentative deformation");
        jButtonDeformReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeformResetActionPerformed(evt);
            }
        });
        jPanelDeformNavControls.add(jButtonDeformReset, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 80, -1));

        jPanelDeformControls.add(jPanelDeformNavControls, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 80, 110, 79));

        jPanelDeformMatrixLeft.setToolTipText("");
        jPanelDeformMatrixLeft.setPreferredSize(new java.awt.Dimension(93, 75));
        jPanelDeformMatrixLeft.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTextFieldStrainM00.setEditable(false);
        jTextFieldStrainM00.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldStrainM00.setText("1.000");
        jTextFieldStrainM00.setToolTipText("matrix for the current or most recent deformation");
        jTextFieldStrainM00.setBorder(null);
        jTextFieldStrainM00.setFocusable(false);
        jTextFieldStrainM00.setForeground(GSComplexUI.INFO_COLOR_TENT);
        jPanelDeformMatrixLeft.add(jTextFieldStrainM00, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 40, 60, -1));

        jTextFieldStrainM10.setEditable(false);
        jTextFieldStrainM10.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldStrainM10.setText("0.000");
        jTextFieldStrainM10.setToolTipText("matrix for the current or most recent deformation");
        jTextFieldStrainM10.setBorder(null);
        jTextFieldStrainM10.setFocusable(false);
        jTextFieldStrainM10.setForeground(GSComplexUI.INFO_COLOR_TENT);
        jPanelDeformMatrixLeft.add(jTextFieldStrainM10, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 70, 60, -1));

        jTextFieldRFPhiCurrentRF.setEditable(false);
        jTextFieldRFPhiCurrentRF.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jTextFieldRFPhiCurrentRF.setText("1.000");
        jTextFieldRFPhiCurrentRF.setToolTipText("the RF value of the ellipse for the current deformation");
        jTextFieldRFPhiCurrentRF.setBorder(javax.swing.BorderFactory.createLineBorder(GSComplexUI.INFO_COLOR_TENT));
        jTextFieldRFPhiCurrentRF.setFocusable(false);
        jTextFieldRFPhiCurrentRF.setForeground(GSComplexUI.INFO_COLOR_TENT);
        jPanelDeformMatrixLeft.add(jTextFieldRFPhiCurrentRF, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 6, 60, 20));

        jLabelRf.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelRf.setText("RF");
        jLabelRf.setFocusable(false);
        jLabelRf.setForeground(GSComplexUI.INFO_COLOR_TENT);
        jPanelDeformMatrixLeft.add(jLabelRf, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 6, 18, 20));

        jLabelCurStrainLeftBracket.setFont(new java.awt.Font("Tahoma", 0, 72)); // NOI18N
        jLabelCurStrainLeftBracket.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelCurStrainLeftBracket.setText("[");
        jLabelCurStrainLeftBracket.setToolTipText("matrix for the current or most recent deformation");
        jLabelCurStrainLeftBracket.setFocusable(false);
        jLabelCurStrainLeftBracket.setForeground(GSComplexUI.INFO_COLOR_TENT);
        jPanelDeformMatrixLeft.add(jLabelCurStrainLeftBracket, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 30, 90));

        jTextFieldStrainCumuRF.setEditable(false);
        jTextFieldStrainCumuRF.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jTextFieldStrainCumuRF.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jTextFieldStrainCumuRF.setText("1.000");
        jTextFieldStrainCumuRF.setToolTipText("the RF value of the ellipse for the cumulative deformation");
        jTextFieldStrainCumuRF.setBorder(javax.swing.BorderFactory.createLineBorder(GSComplexUI.INFO_COLOR_CUMU));
        jTextFieldStrainCumuRF.setFocusable(false);
        jTextFieldStrainCumuRF.setForeground(GSComplexUI.INFO_COLOR_CUMU);
        jPanelDeformMatrixLeft.add(jTextFieldStrainCumuRF, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 150, 50, 20));

        jLabelCumuRF.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabelCumuRF.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelCumuRF.setText("RF");
        jLabelCumuRF.setFocusable(false);
        jLabelCumuRF.setForeground(GSComplexUI.INFO_COLOR_CUMU);
        jPanelDeformMatrixLeft.add(jLabelCumuRF, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, 18, 20));

        jLabelStrainNavPosition.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelStrainNavPosition.setText("0 /");
        jLabelStrainNavPosition.setToolTipText("number of the current deformation");
        jLabelStrainNavPosition.setFocusable(false);
        jPanelDeformMatrixLeft.add(jLabelStrainNavPosition, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 200, 30, 20));

        jLabelCumuStrainM00.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabelCumuStrainM00.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelCumuStrainM00.setText("1");
        jLabelCumuStrainM00.setForeground(GSComplexUI.INFO_COLOR_CUMU);
        jLabelCumuStrainM00.setToolTipText("matrix for the cumulative deformation");
        jLabelCumuStrainM00.setFocusable(false);
        jLabelCumuStrainM00.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanelDeformMatrixLeft.add(jLabelCumuStrainM00, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 110, 33, -1));

        jLabelCumuStrainM01.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabelCumuStrainM01.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelCumuStrainM01.setText("0");
        jLabelCumuStrainM01.setForeground(GSComplexUI.INFO_COLOR_CUMU);
        jLabelCumuStrainM01.setToolTipText("matrix for the cumulative deformation");
        jLabelCumuStrainM01.setFocusable(false);
        jLabelCumuStrainM01.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanelDeformMatrixLeft.add(jLabelCumuStrainM01, new org.netbeans.lib.awtextra.AbsoluteConstraints(57, 110, 33, -1));

        jLabelCumuStrainM10.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabelCumuStrainM10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelCumuStrainM10.setText("0");
        jLabelCumuStrainM10.setForeground(GSComplexUI.INFO_COLOR_CUMU);
        jLabelCumuStrainM10.setToolTipText("matrix for the cumulative deformation");
        jLabelCumuStrainM10.setFocusable(false);
        jLabelCumuStrainM10.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanelDeformMatrixLeft.add(jLabelCumuStrainM10, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, 33, -1));

        jLabelCumuStrainM11.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabelCumuStrainM11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelCumuStrainM11.setText("1");
        jLabelCumuStrainM11.setForeground(GSComplexUI.INFO_COLOR_CUMU);
        jLabelCumuStrainM11.setToolTipText("matrix for the cumulative deformation");
        jLabelCumuStrainM11.setFocusable(false);
        jLabelCumuStrainM11.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanelDeformMatrixLeft.add(jLabelCumuStrainM11, new org.netbeans.lib.awtextra.AbsoluteConstraints(57, 130, 33, -1));

        jLabelCumuStrainRightBracket.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabelCumuStrainRightBracket.setText("]");
        jLabelCumuStrainRightBracket.setToolTipText("matrix for the cumulative deformation");
        jLabelCumuStrainRightBracket.setFocusable(false);
        jLabelCumuStrainRightBracket.setForeground(GSComplexUI.INFO_COLOR_CUMU);
        jPanelDeformMatrixLeft.add(jLabelCumuStrainRightBracket, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 98, 14, 50));

        jLabelCumuStrainLeftBracket.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabelCumuStrainLeftBracket.setText("[");
        jLabelCumuStrainLeftBracket.setToolTipText("matrix for the cumulative deformation");
        jLabelCumuStrainLeftBracket.setFocusable(false);
        jLabelCumuStrainLeftBracket.setForeground(GSComplexUI.INFO_COLOR_CUMU);
        jPanelDeformMatrixLeft.add(jLabelCumuStrainLeftBracket, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 98, 14, 50));

        jTextFieldStrainCumuPhi.setEditable(false);
        jTextFieldStrainCumuPhi.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jTextFieldStrainCumuPhi.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jTextFieldStrainCumuPhi.setText("0.000");
        jTextFieldStrainCumuPhi.setToolTipText("the phi value of the ellipse for the cumulative deformation");
        jTextFieldStrainCumuPhi.setBorder(javax.swing.BorderFactory.createLineBorder(GSComplexUI.INFO_COLOR_CUMU));
        jTextFieldStrainCumuPhi.setFocusable(false);
        jTextFieldStrainCumuPhi.setForeground(GSComplexUI.INFO_COLOR_CUMU);
        jPanelDeformMatrixLeft.add(jTextFieldStrainCumuPhi, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 170, 50, 20));

        jLabelCumuPhi.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabelCumuPhi.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelCumuPhi.setText("phi");
        jLabelCumuPhi.setFocusable(false);
        jLabelCumuPhi.setForeground(GSComplexUI.INFO_COLOR_CUMU);
        jPanelDeformMatrixLeft.add(jLabelCumuPhi, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, 20, 20));

        jButtonStrainNavPrevious.setIcon(new javax.swing.ImageIcon(getClass().getResource("/edu/williams/geoshear2013/img/arrow_left_light.gif"))); // NOI18N
        jButtonStrainNavPrevious.setToolTipText("go to the previous deformation in the series");
        jButtonStrainNavPrevious.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStrainNavPreviousActionPerformed(evt);
            }
        });
        jPanelDeformMatrixLeft.add(jButtonStrainNavPrevious, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 200, 60, -1));

        jLabelNavStrainLeftBracket.setFont(new java.awt.Font("Tahoma", 0, 72)); // NOI18N
        jLabelNavStrainLeftBracket.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelNavStrainLeftBracket.setText("[");
        jLabelNavStrainLeftBracket.setForeground(GSComplexUI.INFO_COLOR_NAV_DEF);
        jPanelDeformMatrixLeft.add(jLabelNavStrainLeftBracket, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 30, 90));

        jTextFieldNavStrainM00.setEditable(false);
        jTextFieldNavStrainM00.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldNavStrainM00.setText("1.000");
        jTextFieldNavStrainM00.setBorder(null);
        jTextFieldNavStrainM00.setForeground(GSComplexUI.INFO_COLOR_NAV_DEF);
        jPanelDeformMatrixLeft.add(jTextFieldNavStrainM00, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 40, 60, -1));

        jTextFieldNavStrainM10.setEditable(false);
        jTextFieldNavStrainM10.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldNavStrainM10.setText("0.000");
        jTextFieldNavStrainM10.setBorder(null);
        jTextFieldNavStrainM10.setForeground(GSComplexUI.INFO_COLOR_NAV_DEF);
        jPanelDeformMatrixLeft.add(jTextFieldNavStrainM10, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 70, 60, -1));

        jPanelMatrixBgMostRecent.setToolTipText("matrix for the current or most recent deformation");

        javax.swing.GroupLayout jPanelMatrixBgMostRecentLayout = new javax.swing.GroupLayout(jPanelMatrixBgMostRecent);
        jPanelMatrixBgMostRecent.setLayout(jPanelMatrixBgMostRecentLayout);
        jPanelMatrixBgMostRecentLayout.setHorizontalGroup(
            jPanelMatrixBgMostRecentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 190, Short.MAX_VALUE)
        );
        jPanelMatrixBgMostRecentLayout.setVerticalGroup(
            jPanelMatrixBgMostRecentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 70, Short.MAX_VALUE)
        );

        jPanelDeformMatrixLeft.add(jPanelMatrixBgMostRecent, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 190, 70));

        jPanelMatrixBgCumu.setToolTipText("matrix for the cumulative deformation");

        javax.swing.GroupLayout jPanelMatrixBgCumuLayout = new javax.swing.GroupLayout(jPanelMatrixBgCumu);
        jPanelMatrixBgCumu.setLayout(jPanelMatrixBgCumuLayout);
        jPanelMatrixBgCumuLayout.setHorizontalGroup(
            jPanelMatrixBgCumuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 110, Short.MAX_VALUE)
        );
        jPanelMatrixBgCumuLayout.setVerticalGroup(
            jPanelMatrixBgCumuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 50, Short.MAX_VALUE)
        );

        jPanelDeformMatrixLeft.add(jPanelMatrixBgCumu, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 100, 110, 50));

        jPanelDeformControls.add(jPanelDeformMatrixLeft, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 160, 110, 230));

        jPanelDeformMatrixRight.setPreferredSize(new java.awt.Dimension(93, 75));
        jPanelDeformMatrixRight.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButtonStrainNavNext.setIcon(new javax.swing.ImageIcon(getClass().getResource("/edu/williams/geoshear2013/img/arrow_right_light.gif"))); // NOI18N
        jButtonStrainNavNext.setToolTipText("go to the next deformation in the series");
        jButtonStrainNavNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStrainNavNextActionPerformed(evt);
            }
        });
        jPanelDeformMatrixRight.add(jButtonStrainNavNext, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 200, 60, -1));

        jTextFieldStrainM01.setEditable(false);
        jTextFieldStrainM01.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldStrainM01.setText("0.000");
        jTextFieldStrainM01.setToolTipText("matrix for the current or most recent deformation");
        jTextFieldStrainM01.setBorder(null);
        jTextFieldStrainM01.setFocusable(false);
        jTextFieldStrainM01.setForeground(GSComplexUI.INFO_COLOR_TENT);
        jPanelDeformMatrixRight.add(jTextFieldStrainM01, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, 60, -1));

        jTextFieldStrainM11.setEditable(false);
        jTextFieldStrainM11.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldStrainM11.setText("1.000");
        jTextFieldStrainM11.setToolTipText("matrix for the current or most recent deformation");
        jTextFieldStrainM11.setBorder(null);
        jTextFieldStrainM11.setFocusable(false);
        jTextFieldStrainM11.setForeground(GSComplexUI.INFO_COLOR_TENT);
        jPanelDeformMatrixRight.add(jTextFieldStrainM11, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 60, -1));

        jTextFieldRFPhiCurrentPhi.setEditable(false);
        jTextFieldRFPhiCurrentPhi.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jTextFieldRFPhiCurrentPhi.setText("0.000");
        jTextFieldRFPhiCurrentPhi.setToolTipText("the phi value of the ellipse for the current deformation");
        jTextFieldRFPhiCurrentPhi.setBorder(javax.swing.BorderFactory.createLineBorder(GSComplexUI.INFO_COLOR_TENT));
        jTextFieldRFPhiCurrentPhi.setFocusable(false);
        jTextFieldRFPhiCurrentPhi.setForeground(GSComplexUI.INFO_COLOR_TENT);
        jPanelDeformMatrixRight.add(jTextFieldRFPhiCurrentPhi, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 6, 60, 20));

        jLabelPhi.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelPhi.setText("phi");
        jLabelPhi.setFocusable(false);
        jLabelPhi.setForeground(GSComplexUI.INFO_COLOR_TENT);
        jPanelDeformMatrixRight.add(jLabelPhi, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 6, 20, 20));

        jLabelCurStrainRightBracket.setFont(new java.awt.Font("Tahoma", 0, 72)); // NOI18N
        jLabelCurStrainRightBracket.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelCurStrainRightBracket.setText("]");
        jLabelCurStrainRightBracket.setToolTipText("matrix for the current or most recent deformation");
        jLabelCurStrainRightBracket.setFocusable(false);
        jLabelCurStrainRightBracket.setForeground(GSComplexUI.INFO_COLOR_TENT);
        jPanelDeformMatrixRight.add(jLabelCurStrainRightBracket, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, 30, 90));

        jLabelStrainNavCount.setText("0");
        jLabelStrainNavCount.setToolTipText("number of deformations in the series");
        jLabelStrainNavCount.setFocusable(false);
        jPanelDeformMatrixRight.add(jLabelStrainNavCount, new org.netbeans.lib.awtextra.AbsoluteConstraints(4, 200, 26, 20));

        jLabelCumuTentStrainLeftBracket.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabelCumuTentStrainLeftBracket.setText("[");
        jLabelCumuTentStrainLeftBracket.setToolTipText("matrix for the next cumulative deformation");
        jLabelCumuTentStrainLeftBracket.setFocusable(false);
        jLabelCumuTentStrainLeftBracket.setForeground(GSComplexUI.INFO_COLOR_CUMUTENT);
        jPanelDeformMatrixRight.add(jLabelCumuTentStrainLeftBracket, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 98, 14, 50));

        jLabelCumuTentStrainM00.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabelCumuTentStrainM00.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelCumuTentStrainM00.setText("1");
        jLabelCumuTentStrainM00.setForeground(GSComplexUI.INFO_COLOR_CUMUTENT);
        jLabelCumuTentStrainM00.setToolTipText("matrix for the next cumulative deformation");
        jLabelCumuTentStrainM00.setFocusable(false);
        jLabelCumuTentStrainM00.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanelDeformMatrixRight.add(jLabelCumuTentStrainM00, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 110, 33, -1));

        jLabelCumuTentStrainM10.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabelCumuTentStrainM10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelCumuTentStrainM10.setText("0");
        jLabelCumuTentStrainM10.setForeground(GSComplexUI.INFO_COLOR_CUMUTENT);
        jLabelCumuTentStrainM10.setToolTipText("matrix for the next cumulative deformation");
        jLabelCumuTentStrainM10.setFocusable(false);
        jLabelCumuTentStrainM10.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanelDeformMatrixRight.add(jLabelCumuTentStrainM10, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 130, 33, -1));

        jLabelCumuTentStrainM11.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabelCumuTentStrainM11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelCumuTentStrainM11.setText("1");
        jLabelCumuTentStrainM11.setForeground(GSComplexUI.INFO_COLOR_CUMUTENT);
        jLabelCumuTentStrainM11.setToolTipText("matrix for the next cumulative deformation");
        jLabelCumuTentStrainM11.setFocusable(false);
        jLabelCumuTentStrainM11.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanelDeformMatrixRight.add(jLabelCumuTentStrainM11, new org.netbeans.lib.awtextra.AbsoluteConstraints(53, 130, 33, -1));

        jLabelCumuTentStrainM01.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabelCumuTentStrainM01.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelCumuTentStrainM01.setText("0");
        jLabelCumuTentStrainM01.setForeground(GSComplexUI.INFO_COLOR_CUMUTENT);
        jLabelCumuTentStrainM01.setToolTipText("matrix for the next cumulative deformation");
        jLabelCumuTentStrainM01.setFocusable(false);
        jLabelCumuTentStrainM01.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanelDeformMatrixRight.add(jLabelCumuTentStrainM01, new org.netbeans.lib.awtextra.AbsoluteConstraints(53, 110, 33, -1));

        jLabelCumuTentStrainRightBracket.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabelCumuTentStrainRightBracket.setText("]");
        jLabelCumuTentStrainRightBracket.setToolTipText("matrix for the next cumulative deformation");
        jLabelCumuTentStrainRightBracket.setFocusable(false);
        jLabelCumuTentStrainRightBracket.setForeground(GSComplexUI.INFO_COLOR_CUMUTENT);
        jPanelDeformMatrixRight.add(jLabelCumuTentStrainRightBracket, new org.netbeans.lib.awtextra.AbsoluteConstraints(86, 98, 14, 50));

        jLabelCumuTentRF.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabelCumuTentRF.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelCumuTentRF.setText("RF");
        jLabelCumuTentRF.setFocusable(false);
        jLabelCumuTentRF.setForeground(GSComplexUI.INFO_COLOR_CUMUTENT);
        jPanelDeformMatrixRight.add(jLabelCumuTentRF, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, 18, 20));

        jTextFieldStrainCumuTentRF.setEditable(false);
        jTextFieldStrainCumuTentRF.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jTextFieldStrainCumuTentRF.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jTextFieldStrainCumuTentRF.setText("1.000");
        jTextFieldStrainCumuTentRF.setToolTipText("the RF value of the ellipse for the next cumulative deformation");
        jTextFieldStrainCumuTentRF.setBorder(javax.swing.BorderFactory.createLineBorder(GSComplexUI.INFO_COLOR_CUMUTENT));
        jTextFieldStrainCumuTentRF.setFocusable(false);
        jTextFieldStrainCumuTentRF.setForeground(GSComplexUI.INFO_COLOR_CUMUTENT);
        jPanelDeformMatrixRight.add(jTextFieldStrainCumuTentRF, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 150, 50, 20));

        jTextFieldStrainCumuTentPhi.setEditable(false);
        jTextFieldStrainCumuTentPhi.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jTextFieldStrainCumuTentPhi.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jTextFieldStrainCumuTentPhi.setText("0.000");
        jTextFieldStrainCumuTentPhi.setToolTipText("the phi value of the ellipse for the next cumulative deformation");
        jTextFieldStrainCumuTentPhi.setBorder(javax.swing.BorderFactory.createLineBorder(GSComplexUI.INFO_COLOR_CUMUTENT));
        jTextFieldStrainCumuTentPhi.setFocusable(false);
        jTextFieldStrainCumuTentPhi.setForeground(GSComplexUI.INFO_COLOR_CUMUTENT);
        jPanelDeformMatrixRight.add(jTextFieldStrainCumuTentPhi, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 170, 50, 20));

        jLabelCumuTentPhi.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabelCumuTentPhi.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelCumuTentPhi.setText("phi");
        jLabelCumuTentPhi.setFocusable(false);
        jLabelCumuTentPhi.setForeground(GSComplexUI.INFO_COLOR_CUMUTENT);
        jPanelDeformMatrixRight.add(jLabelCumuTentPhi, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, 20, 20));

        jTextFieldNavStrainM01.setEditable(false);
        jTextFieldNavStrainM01.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldNavStrainM01.setText("0.000");
        jTextFieldNavStrainM01.setBorder(null);
        jTextFieldNavStrainM01.setForeground(GSComplexUI.INFO_COLOR_NAV_DEF);
        jPanelDeformMatrixRight.add(jTextFieldNavStrainM01, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, 60, -1));

        jLabelNavStrainRightBracket.setFont(new java.awt.Font("Tahoma", 0, 72)); // NOI18N
        jLabelNavStrainRightBracket.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelNavStrainRightBracket.setText("]");
        jLabelNavStrainRightBracket.setForeground(GSComplexUI.INFO_COLOR_NAV_DEF);
        jPanelDeformMatrixRight.add(jLabelNavStrainRightBracket, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, 30, 90));

        jTextFieldNavStrainM11.setEditable(false);
        jTextFieldNavStrainM11.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldNavStrainM11.setText("1.000");
        jTextFieldNavStrainM11.setBorder(null);
        jTextFieldNavStrainM11.setForeground(GSComplexUI.INFO_COLOR_NAV_DEF);
        jPanelDeformMatrixRight.add(jTextFieldNavStrainM11, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 60, -1));

        jPanelMatrixBgCumuTent.setToolTipText("matrix for the next cumulative deformation");

        javax.swing.GroupLayout jPanelMatrixBgCumuTentLayout = new javax.swing.GroupLayout(jPanelMatrixBgCumuTent);
        jPanelMatrixBgCumuTent.setLayout(jPanelMatrixBgCumuTentLayout);
        jPanelMatrixBgCumuTentLayout.setHorizontalGroup(
            jPanelMatrixBgCumuTentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanelMatrixBgCumuTentLayout.setVerticalGroup(
            jPanelMatrixBgCumuTentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 50, Short.MAX_VALUE)
        );

        jPanelDeformMatrixRight.add(jPanelMatrixBgCumuTent, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 100, -1, 50));

        jPanelDeformControls.add(jPanelDeformMatrixRight, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 160, 110, 230));

        jPanelContainerControls.add(jPanelDeformControls);

        jPanelEditPebbleControls.setAlignmentX(0.0F);
        jPanelEditPebbleControls.setAlignmentY(0.0F);
        jPanelEditPebbleControls.setMaximumSize(new java.awt.Dimension(220, 100));
        jPanelEditPebbleControls.setMinimumSize(new java.awt.Dimension(220, 100));
        jPanelEditPebbleControls.setPreferredSize(new java.awt.Dimension(220, 100));
        jPanelEditPebbleControls.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jToggleButtonEditPebbles.setText("Edit Mode");
        jToggleButtonEditPebbles.setToolTipText("toggle edit mode on and off");
        jToggleButtonEditPebbles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonEditPebblesActionPerformed(evt);
            }
        });
        jPanelEditPebbleControls.add(jToggleButtonEditPebbles, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 190, -1));

        jButtonPebbleColorSet.setBackground(new java.awt.Color(0, 0, 255));
        jButtonPebbleColorSet.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jButtonPebbleColorSet.setForeground(new java.awt.Color(255, 255, 255));
        jButtonPebbleColorSet.setText("Pick Color");
        jButtonPebbleColorSet.setToolTipText("choose a color for new pebbles and/or to be applied to existing pebbles");
        jButtonPebbleColorSet.setEnabled(false);
        jButtonPebbleColorSet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPebbleColorSetActionPerformed(evt);
            }
        });
        jPanelEditPebbleControls.add(jButtonPebbleColorSet, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 27, 87, -1));

        jButtonPebbleColorApply.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jButtonPebbleColorApply.setText("Apply");
        jButtonPebbleColorApply.setToolTipText("change the color of selected pebbles to the chosen color");
        jButtonPebbleColorApply.setEnabled(false);
        jButtonPebbleColorApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPebbleColorApplyActionPerformed(evt);
            }
        });
        jPanelEditPebbleControls.add(jButtonPebbleColorApply, new org.netbeans.lib.awtextra.AbsoluteConstraints(115, 27, 85, -1));

        jButtonBackgroundImage.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jButtonBackgroundImage.setText("Background Image");
        jButtonBackgroundImage.setToolTipText("load a backgorund image");
        jButtonBackgroundImage.setEnabled(false);
        jButtonBackgroundImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBackgroundImageActionPerformed(evt);
            }
        });
        jPanelEditPebbleControls.add(jButtonBackgroundImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 52, 190, -1));

        jButtonAutoColorOnPhi.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jButtonAutoColorOnPhi.setText("Color by phi");
        jButtonAutoColorOnPhi.setToolTipText("automatically color pebbles based on their phi values");
        jButtonAutoColorOnPhi.setEnabled(false);
        jButtonAutoColorOnPhi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAutoColorOnPhiActionPerformed(evt);
            }
        });
        jPanelEditPebbleControls.add(jButtonAutoColorOnPhi, new org.netbeans.lib.awtextra.AbsoluteConstraints(107, 78, 93, 19));

        jButtonAutoColorOnRf.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jButtonAutoColorOnRf.setText("Color By RF");
        jButtonAutoColorOnRf.setToolTipText("automatically color pebbles based on their RF values");
        jButtonAutoColorOnRf.setEnabled(false);
        jButtonAutoColorOnRf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAutoColorOnRfActionPerformed(evt);
            }
        });
        jPanelEditPebbleControls.add(jButtonAutoColorOnRf, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 78, 93, 19));

        jPanelContainerControls.add(jPanelEditPebbleControls);

        jPanelSnapshotControls.setAlignmentX(0.0F);
        jPanelSnapshotControls.setAlignmentY(0.0F);
        jPanelSnapshotControls.setMaximumSize(new java.awt.Dimension(220, 100));
        jPanelSnapshotControls.setMinimumSize(new java.awt.Dimension(220, 100));
        jPanelSnapshotControls.setPreferredSize(new java.awt.Dimension(220, 100));

        jButtonSnapshotter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/edu/williams/geoshear2013/img/icon_camera_large_40x30.gif"))); // NOI18N
        jButtonSnapshotter.setText("Take Snapshot");
        jButtonSnapshotter.setToolTipText("save a copy of the pebble UI panel and a .png, .jpg, or .bmp image");
        jButtonSnapshotter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSnapshotterActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelSnapshotControlsLayout = new javax.swing.GroupLayout(jPanelSnapshotControls);
        jPanelSnapshotControls.setLayout(jPanelSnapshotControlsLayout);
        jPanelSnapshotControlsLayout.setHorizontalGroup(
            jPanelSnapshotControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 220, Short.MAX_VALUE)
            .addGroup(jPanelSnapshotControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelSnapshotControlsLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jButtonSnapshotter, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanelSnapshotControlsLayout.setVerticalGroup(
            jPanelSnapshotControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
            .addGroup(jPanelSnapshotControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelSnapshotControlsLayout.createSequentialGroup()
                    .addContainerGap(30, Short.MAX_VALUE)
                    .addComponent(jButtonSnapshotter, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(31, Short.MAX_VALUE)))
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
        FileMenu.setToolTipText("Save and load");

        jMenuItemSave.setText("Save");
        jMenuItemSave.setToolTipText("Save everything to a .tab file");
        jMenuItemSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSaveActionPerformed(evt);
            }
        });
        FileMenu.add(jMenuItemSave);

        jMenuItemLoad.setText("Load");
        jMenuItemLoad.setToolTipText("Load new pebbles and deformations from a .tab file");
        jMenuItemLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemLoadActionPerformed(evt);
            }
        });
        FileMenu.add(jMenuItemLoad);

        jMenuItemSaveCurrentDeformed.setText("Save current deformed");
        jMenuItemSaveCurrentDeformed.setToolTipText("Save the currently deformed view as a new basis (with no deformations)");
        jMenuItemSaveCurrentDeformed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSaveCurrentDeformedActionPerformed(evt);
            }
        });
        FileMenu.add(jMenuItemSaveCurrentDeformed);

        MainWindowMenuBar.add(FileMenu);

        DisplayMenu.setText("Display");
        DisplayMenu.setToolTipText("Options for the main display area");

        jCheckBoxMenuItemShowPebbleAxes.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jCheckBoxMenuItemShowPebbleAxes.setSelected(true);
        jCheckBoxMenuItemShowPebbleAxes.setText("Show pebble axes");
        jCheckBoxMenuItemShowPebbleAxes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItemShowPebbleAxesActionPerformed(evt);
            }
        });
        DisplayMenu.add(jCheckBoxMenuItemShowPebbleAxes);

        jCheckBoxMenuItemFillPebbles.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jCheckBoxMenuItemFillPebbles.setText("Fill pebbles");
        jCheckBoxMenuItemFillPebbles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItemFillPebblesActionPerformed(evt);
            }
        });
        DisplayMenu.add(jCheckBoxMenuItemFillPebbles);

        jCheckBoxMenuItemShowBackgroundAxis.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jCheckBoxMenuItemShowBackgroundAxis.setSelected(true);
        jCheckBoxMenuItemShowBackgroundAxis.setText("Show background axis");
        jCheckBoxMenuItemShowBackgroundAxis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItemShowBackgroundAxisActionPerformed(evt);
            }
        });
        DisplayMenu.add(jCheckBoxMenuItemShowBackgroundAxis);

        jCheckBoxMenuItemShowBackgroundImage.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jCheckBoxMenuItemShowBackgroundImage.setSelected(true);
        jCheckBoxMenuItemShowBackgroundImage.setText("Show background image");
        jCheckBoxMenuItemShowBackgroundImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItemShowBackgroundImageActionPerformed(evt);
            }
        });
        DisplayMenu.add(jCheckBoxMenuItemShowBackgroundImage);

        jCheckBoxMenuItemShowStrainEllipses.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jCheckBoxMenuItemShowStrainEllipses.setSelected(true);
        jCheckBoxMenuItemShowStrainEllipses.setText("Show strain ellipses");
        jCheckBoxMenuItemShowStrainEllipses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItemShowStrainEllipsesActionPerformed(evt);
            }
        });
        DisplayMenu.add(jCheckBoxMenuItemShowStrainEllipses);

        MainWindowMenuBar.add(DisplayMenu);

        ChartsMenu.setText("Windows");
        ChartsMenu.setToolTipText("Open new windows to display charts");

        jMenuItemChartRfPhiCart.setText("RF-Phi cartesian chart");
        jMenuItemChartRfPhiCart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemChartRfPhiCartActionPerformed(evt);
            }
        });
        ChartsMenu.add(jMenuItemChartRfPhiCart);

        jMenuItemChartRf2PhiPolar.setText("RF-2*Phi polar chart");
        jMenuItemChartRf2PhiPolar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemChartRf2PhiPolarActionPerformed(evt);
            }
        });
        ChartsMenu.add(jMenuItemChartRf2PhiPolar);

        jMenuItemChartDeformationSeries.setText("Deformations series");
        jMenuItemChartDeformationSeries.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemChartDeformationSeriesActionPerformed(evt);
            }
        });
        ChartsMenu.add(jMenuItemChartDeformationSeries);

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
            .addComponent(jPanelContainerDisplay, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanelContainerControls, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
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
        this.jPanelContainerDisplay.requestFocus();
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
        this.handleDeformationApplyRemove();
    }//GEN-LAST:event_jButtonDeformApplyRemoveActionPerformed

    public void handleDeformationApplyRemove() {
        if (this.jButtonDeformApplyRemove.getText().equals(MainWindow.LABEL_DEFORM_APPLY)) {
            this.gscUI.handleApplyTentativeTransform();
            this.handleDeformationReset();
        }
        else if (this.jButtonDeformApplyRemove.getText().equals(MainWindow.LABEL_DEFORM_REMOVE)) {
            this.gscUI.gsc.removeCurrentDeformation();
            this.gscUI.resetDeformations();
        }
        this.updateNavPositionInfo();
        this.setValuesForCumuStrain();
        this.setValuesForCumuRfPhi();
        this.setValuesForCumuTentStrain();
        this.setValuesForCumuTentRfPhi();
        this.updateDeformNavControlsStates();
        this.updateStrainMatricesVisibilities();
        this.updateNavStrainInfo();
        this.updateStateOfCurrentDeformControls();
        this.windowDeformationsSeries.setFromDeformationSeries(this.gscUI.gsc.deformations);
        this.windowDeformationsSeries.markCurrentDeformation(this.gscUI.gsc.getCurrentDeformationNumber());
        this.gscUI.repaint();
    }
    
    public void updateDeformNavControlsStates() {
        this.jButtonStrainNavPrevious.setEnabled(this.gscUI.gsc.getCurrentDeformationNumber()-1 > 0);
        this.jButtonStrainNavNext.setEnabled(this.gscUI.gsc.getCurrentDeformationNumber()-1 < this.gscUI.gsc.deformations.size());
    }
    
    /**
     * set all the relevant deformation control and strain control values to reflect the given deformation
     * @param d 
     */
    public void updateDeformAndStrainControlsFromDeformation(Deformation d) {
        if (d.isIdentity()) {
            this.setValuesForCumuTentStrain();
            this.handleDeformationReset();
            this.updateStateOfCurrentDeformControls();
            this.jLabelStrainNavPosition.setText((this.gscUI.gsc.getCurrentDeformationNumber()-1)+" /");
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
                this.setValueForControl(this.jTextFieldCompressY,d.m11);
            }
        } else
        if (d.isRotational()) {
            this.setValueForDeformControlExclusively(this.jTextFieldRotDeg,d.getRotAngleDegr());
            this.setValueForControl(this.jTextFieldRotRad,d.getRotAngleRad());
        }
        this.updateStateOfCurrentDeformControls();
        if (! d.isIdentity()) {
            this.jLabelStrainNavPosition.setText("* /");
        }
        this.setValuesForCumuTentStrain();
        this.updateOtherControlsFromDeformControls();

        this.updateStrainControlsFromDeformation(d);
        this.setValuesForCumuRfPhi();
        this.setValuesForCumuTentRfPhi();
        this.updateStrainMatricesVisibilities();
    }
    
    public void updateStrainControlsFromDeformation(Matrix2x2 d) {
        this.setValueForControl(this.jTextFieldStrainM00, d.m00);
        this.setValueForControl(this.jTextFieldStrainM01, d.m01*-1);
        this.setValueForControl(this.jTextFieldStrainM10, d.m10*-1);
        this.setValueForControl(this.jTextFieldStrainM11, d.m11);
    }

    public void updateNavStrainInfo() {
        if (this.gscUI.gsc.getCurrentDeformationNumber() >= 2) {
            Deformation d = this.gscUI.gsc.deformations.get(this.gscUI.gsc.getCurrentDeformationNumber()-2);
            this.setValueForControl(this.jTextFieldNavStrainM00, d.m00);
            this.setValueForControl(this.jTextFieldNavStrainM01, d.m01*-1);
            this.setValueForControl(this.jTextFieldNavStrainM10, d.m10*-1);
            this.setValueForControl(this.jTextFieldNavStrainM11, d.m11);
        }
    }    
    
    public void updateOtherControlsFromDeformControls() {
        if (controlFieldHasDefaultValue(this.jTextFieldRotDeg)) {
            this.setValueForControl(this.jTextFieldStrainM00, Double.parseDouble(this.jTextFieldCompressX.getText()));
            this.setValueForControl(this.jTextFieldStrainM01, Double.parseDouble(this.jTextFieldShearY.getText()));
            this.setValueForControl(this.jTextFieldStrainM10, Double.parseDouble(this.jTextFieldShearX.getText()));
            this.setValueForControl(this.jTextFieldStrainM11, Double.parseDouble(this.jTextFieldCompressY.getText()));
            
            Deformation d = new Deformation(Double.parseDouble(this.jTextFieldCompressX.getText()),
                                            Double.parseDouble(this.jTextFieldShearY.getText()),
                                            Double.parseDouble(this.jTextFieldShearX.getText()),
                                            Double.parseDouble(this.jTextFieldCompressY.getText()));
            GSPebble s = new GSPebble(10,10);
            s.deform(d);
            this.setValueForControl(this.jTextFieldRFPhiCurrentRF, s.getMajorRadius()/s.getMinorRadius());
            this.setValueForControl(this.jTextFieldRFPhiCurrentPhi, Util.toDegrees(s.getThetaRad()*-1));
        } else {
            this.updateStrainControlsFromDeformation(Deformation.createFromAngle(Double.parseDouble(this.jTextFieldRotRad.getText())));
            this.setValueForControl(this.jTextFieldRFPhiCurrentRF, 1);
            this.setValueForControl(this.jTextFieldRFPhiCurrentPhi, Double.parseDouble(this.jTextFieldRotDeg.getText()));
        }
    }
    
    /**
     * sets the visibility of the strain info sets (matrix and rf-phi) for tent, cumu, and cumu-tent depending on which are applicable
     */
    public void updateStrainMatricesVisibilities() {
        this.setVisibilityOnCurDeformStrainInfo(! this.gscUI.tentativeDeformation.isIdentity());
        this.setVisibilityOnCumuStrainInfo(! this.gscUI.cumuDeformation.isIdentity());
        this.setVisibilityOnCumuTentDeformStrainInfo(! this.gscUI.cumuDeformation.isIdentity() && ! this.gscUI.tentativeDeformation.isIdentity());
        this.setVisibilityOnNavDeformStrainInfo(this.gscUI.tentativeDeformation.isIdentity() && this.gscUI.gsc.getCurrentDeformationNumber() > 1);
    }

    
    public void updateOtherControlsFromRFPhiControls() {
        Util.todo("this is unsupported due to ambiguities in decomposing RF-phi data into deformation data");
    }
    
    public void updateOtherControlsFromStrainControls() {
        Util.todo("this is unsupported due to ambiguities in decomposing strain data into deformation data");
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
    private void setValueForControl(javax.swing.JTextField controlField, double val) {
        if (controlField != null) {
            ValueConstrainer vc = (ValueConstrainer) this.displayNumberConstraints.get(controlField);
            val = vc.constrain(val);
            controlField.setText(Util.truncForDisplay(val, vc.getDisplayPrecision()));
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
        this.clearOutControlFieldsOtherThan(controlField);
    }
        
    /**
     * resets all the deform control fields to the default that are set in the value contraints for those fields.
     * NOTE: safely handles a null (i.e. reset every deform control field)
     * @param stableControlField 
     */
    private void clearOutControlFieldsOtherThan(javax.swing.JTextField stableControlField) {
        Iterator keyIter = this.displayNumberConstraints.keySet().iterator();
        while(keyIter.hasNext()) {
            JTextField tf = (JTextField) keyIter.next();
            if (! tf.equals(stableControlField)) {
                if ((this.jTextFieldCompressX.equals(stableControlField) || this.jTextFieldCompressY.equals(stableControlField)) && 
                    (! this.gscUI.compressionXandYareLinked) && 
                    (this.jTextFieldCompressX.equals(tf) || this.jTextFieldCompressY.equals(tf))) {
                    continue;
                }
                tf.setText(Util.truncTextDecimal(Double.toString(((ValueConstrainer)this.displayNumberConstraints.get(tf)).getDefaultVal()), ((ValueConstrainer)this.displayNumberConstraints.get(tf)).getDisplayPrecision()));
            }
        }
        if (stableControlField != null) {
            if (this.isControlDeform(stableControlField)) {
                this.updateOtherControlsFromDeformControls();
            } else if (this.isControlStrainMatrix(stableControlField)) {
                this.updateOtherControlsFromStrainControls();
            } else if (this.isControlRfPhi(stableControlField)) {
                this.updateOtherControlsFromRFPhiControls();
            }
        }
    }
    
    private void jTextFieldShearXKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldShearXKeyPressed
        this.alterValueByKeyPressInControl(this.jTextFieldShearX, evt.getKeyCode());
    }//GEN-LAST:event_jTextFieldShearXKeyPressed

    private void jTextFieldShearXKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldShearXKeyReleased
        this.processKeyReleaseOnControl(this.jTextFieldShearX,evt);
    }//GEN-LAST:event_jTextFieldShearXKeyReleased

    private void jTextFieldShearYKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldShearYKeyPressed
        this.alterValueByKeyPressInControl(this.jTextFieldShearY, evt.getKeyCode());
    }//GEN-LAST:event_jTextFieldShearYKeyPressed

    private void jTextFieldShearYKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldShearYKeyReleased
        this.processKeyReleaseOnControl(this.jTextFieldShearY,evt);
    }//GEN-LAST:event_jTextFieldShearYKeyReleased

    private void jTextFieldCompressXKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldCompressXKeyPressed
        this.alterValueByKeyPressInControl(this.jTextFieldCompressX, evt.getKeyCode());
    }//GEN-LAST:event_jTextFieldCompressXKeyPressed

    private void jTextFieldCompressXKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldCompressXKeyReleased
        this.processKeyReleaseOnControl(this.jTextFieldCompressX,evt);
    }//GEN-LAST:event_jTextFieldCompressXKeyReleased

    private void jTextFieldCompressYKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldCompressYKeyPressed
        this.alterValueByKeyPressInControl(this.jTextFieldCompressY, evt.getKeyCode());
    }//GEN-LAST:event_jTextFieldCompressYKeyPressed

    private void jTextFieldCompressYKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldCompressYKeyReleased
        this.processKeyReleaseOnControl(this.jTextFieldCompressY,evt);
    }//GEN-LAST:event_jTextFieldCompressYKeyReleased

    private void jTextFieldRotDegKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldRotDegKeyPressed
        this.alterValueByKeyPressInControl(this.jTextFieldRotDeg, evt.getKeyCode());
    }//GEN-LAST:event_jTextFieldRotDegKeyPressed

    private void jTextFieldRotDegKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldRotDegKeyReleased
        this.processKeyReleaseOnControl(this.jTextFieldRotDeg,evt);
    }//GEN-LAST:event_jTextFieldRotDegKeyReleased

    private void jTextFieldRotRadKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldRotRadKeyPressed
        this.alterValueByKeyPressInControl(this.jTextFieldRotRad, evt.getKeyCode());
    }//GEN-LAST:event_jTextFieldRotRadKeyPressed

    private void jTextFieldRotRadKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldRotRadKeyReleased
        this.processKeyReleaseOnControl(this.jTextFieldRotRad,evt);
    }//GEN-LAST:event_jTextFieldRotRadKeyReleased

    private void jButtonDeformResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeformResetActionPerformed
        this.handleDeformReset();
    }//GEN-LAST:event_jButtonDeformResetActionPerformed

    public void handleDeformReset() {
        this.gscUI.tentativeDeformationClear();
        this.handleDeformationReset();
        this.gscUI.repaint();
    }
    
    private void jTextFieldShearXFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldShearXFocusLost
        this.processLostFocusOnControl(this.jTextFieldShearX);
    }//GEN-LAST:event_jTextFieldShearXFocusLost

    private void jTextFieldShearYFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldShearYFocusLost
        this.processLostFocusOnControl(this.jTextFieldShearY);
    }//GEN-LAST:event_jTextFieldShearYFocusLost

    private void jTextFieldCompressXFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldCompressXFocusLost
        this.processLostFocusOnControl(this.jTextFieldCompressX);
    }//GEN-LAST:event_jTextFieldCompressXFocusLost

    private void jTextFieldCompressYFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldCompressYFocusLost
        this.processLostFocusOnControl(this.jTextFieldCompressY);
    }//GEN-LAST:event_jTextFieldCompressYFocusLost

    private void jTextFieldRotDegFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldRotDegFocusLost
        this.processLostFocusOnControl(this.jTextFieldRotDeg);
    }//GEN-LAST:event_jTextFieldRotDegFocusLost

    private void jTextFieldRotRadFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldRotRadFocusLost
        this.processLostFocusOnControl(this.jTextFieldRotRad);
    }//GEN-LAST:event_jTextFieldRotRadFocusLost

    private void jButtonStrainNavNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStrainNavNextActionPerformed
        this.gscUI.gsc.nextDeformation();
        this.handleStrainNavPostAction();
    }//GEN-LAST:event_jButtonStrainNavNextActionPerformed

    private void jButtonStrainNavPreviousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStrainNavPreviousActionPerformed
        this.gscUI.gsc.prevDeformation();
        this.handleStrainNavPostAction();
    }//GEN-LAST:event_jButtonStrainNavPreviousActionPerformed

    private void jCheckBoxMenuItemFillPebblesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemFillPebblesActionPerformed
        this.gscUI.setFlagDisplayPebbleFill(this.jCheckBoxMenuItemFillPebbles.isSelected());
        this.repaint();
    }//GEN-LAST:event_jCheckBoxMenuItemFillPebblesActionPerformed

    private void jCheckBoxMenuItemShowPebbleAxesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemShowPebbleAxesActionPerformed
        this.gscUI.setFlagDisplayPebbleAxes(this.jCheckBoxMenuItemShowPebbleAxes.isSelected());
        this.repaint();
    }//GEN-LAST:event_jCheckBoxMenuItemShowPebbleAxesActionPerformed

    private void jCheckBoxMenuItemShowBackgroundAxisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemShowBackgroundAxisActionPerformed
        this.gscUI.setFlagDisplayBackgroundAxis(this.jCheckBoxMenuItemShowBackgroundAxis.isSelected());
        this.repaint();
    }//GEN-LAST:event_jCheckBoxMenuItemShowBackgroundAxisActionPerformed

    private void jCheckBoxMenuItemShowBackgroundImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemShowBackgroundImageActionPerformed
        this.gscUI.setFlagDisplayBackgroundImage(this.jCheckBoxMenuItemShowBackgroundImage.isSelected());
        this.repaint();
    }//GEN-LAST:event_jCheckBoxMenuItemShowBackgroundImageActionPerformed

    private void jCheckBoxMenuItemShowStrainEllipsesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemShowStrainEllipsesActionPerformed
        this.gscUI.setFlagDisplayStrainEllipses(this.jCheckBoxMenuItemShowStrainEllipses.isSelected());
        this.repaint();
    }//GEN-LAST:event_jCheckBoxMenuItemShowStrainEllipsesActionPerformed

    private void jToggleButtonEditPebblesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonEditPebblesActionPerformed
        this.setEnableOnDeformControls(! this.jToggleButtonEditPebbles.isSelected());
        this.setEnableOnRfPhiControls(! this.jToggleButtonEditPebbles.isSelected());
        this.setEnableOnStrainMatrixControls(! this.jToggleButtonEditPebbles.isSelected());
        this.jButtonDeformApplyRemove.setEnabled((! this.jToggleButtonEditPebbles.isSelected()) && (this.gscUI.gsc.deformations.size() > 0) && (this.gscUI.gsc.getCurrentDeformationNumber() > 1));
        
        if (this.jButtonDeformReset.isEnabled()) {
            this.jButtonDeformResetActionPerformed(null); // editing always removes the current deformation - identical to hitting the reset button
        }
   
        if (this.jToggleButtonEditPebbles.isSelected()) {
            this.cachedStrainNavPrevEnableState = this.jButtonStrainNavPrevious.isEnabled();
            this.cachedStrainNavNextEnableState = this.jButtonStrainNavNext.isEnabled();
            this.jButtonStrainNavPrevious.setEnabled(false);
            this.jButtonStrainNavNext.setEnabled(false);
        } else {
            this.jButtonStrainNavPrevious.setEnabled(this.cachedStrainNavPrevEnableState);
            this.jButtonStrainNavNext.setEnabled(this.cachedStrainNavNextEnableState);
        }
        
        this.gscUI.toggleEditUIMode(this.jToggleButtonEditPebbles.isSelected());
        this.setEnableOnPebbleEditingControls(this.jToggleButtonEditPebbles.isSelected());
        this.jPanelContainerDisplay.requestFocus();
        this.repaint();
    }//GEN-LAST:event_jToggleButtonEditPebblesActionPerformed

    private void jButtonPebbleColorSetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPebbleColorSetActionPerformed
        Color newColor = JColorChooser.showDialog (this,"Pebble Color Chooser",this.gscUI.getColorOfNewPebbles());
        if (newColor != null)
        {
            this.gscUI.setColorOfNewPebbles(newColor);
            this.jButtonPebbleColorSet.setBackground (newColor);
            this.jButtonPebbleColorSet.setForeground(Util.getContrastingTextColor(newColor));
        }
        this.repaint();
    }//GEN-LAST:event_jButtonPebbleColorSetActionPerformed

    private void jButtonBackgroundImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBackgroundImageActionPerformed
        this.gscUI.handleChooseBackgroundImage(evt);
        this.jCheckBoxMenuItemShowBackgroundImage.setSelected(this.gscUI.isFlagDisplayBackgroundImage());
        this.repaint ();
    }//GEN-LAST:event_jButtonBackgroundImageActionPerformed

    private void jButtonPebbleColorApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPebbleColorApplyActionPerformed
        this.gscUI.handleApplyColor(this.jButtonPebbleColorSet.getBackground());
        this.repaint();
    }//GEN-LAST:event_jButtonPebbleColorApplyActionPerformed

    private void jMenuItemSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveActionPerformed
//        File saveFile = chooseFileForIO(MainWindow.FILE_IO_TYPE_GES,"Save");
//        if (saveFile == null) { return; }
//        this.handleDataToFile(saveFile,this.gscUI.gsc.serialize());
//        this.repaint ();
        this.handleExportAsTabbedActionPerformed(evt);
    }//GEN-LAST:event_jMenuItemSaveActionPerformed

    private File chooseFileForIO(int ioType,String buttonText) {
        FileFilter theFilter = this.filterGeoShear;
        String filterText = "";
        String defaultExtension = "";
        if (ioType == MainWindow.FILE_IO_TYPE_GES) {
            System.out.println("file type .ges is deprecated");
            theFilter = this.filterGeoShear;
            filterText = "*.ges";
            defaultExtension = "ges";
        } 
        else if (ioType == MainWindow.FILE_IO_TYPE_TAB) {
            theFilter = this.filterTab;
            filterText = "*.tab";
            defaultExtension = "tab";
        }
        else if (ioType == MainWindow.FILE_IO_TYPE_IMG) {
            theFilter = this.filterImage;
            filterText = "*.jpg;*.jpeg;*.png;*.bmp";
            defaultExtension = "png";
        }
        
        if (buttonText.equals("Save") || buttonText.equals("Export")) {
            fileDialog.setMode(FileDialog.SAVE);
        } else {
            fileDialog.setMode(FileDialog.LOAD);
        }
        fileDialog.setFile(filterText);
        fileDialog.setVisible(true);
        File[] saveFiles = fileDialog.getFiles();
        if (saveFiles.length > 0) {

            File saveFile = saveFiles[0]; //fileChooser.getSelectedFile ();
            if (! saveFile.getName().matches(".*\\.\\w+$")) {
                saveFile = new File(saveFile.getPath() + "."+defaultExtension);
            }
            if (theFilter.accept (saveFile))
            {
                return saveFile;
            }
            else
            {
                JOptionPane.showMessageDialog (this,"Unsupported format, only "+theFilter.getDescription ()+".");
            }
        }
        return null;
    }
    
    private void handleDataToFile(File f, String data) { 
        try
        {
            FileWriter fstream = new FileWriter(f.getCanonicalPath());
            BufferedWriter fout = new BufferedWriter(fstream);
            fout.write(data);
            fout.close();
            JOptionPane.showMessageDialog (this,"Saved to "+f.getCanonicalPath ());
        }
        catch (IOException exc)
        {
            JOptionPane.showMessageDialog (this,"Problem saving - aborted:\n"+exc.toString());
            exc.printStackTrace ();
        }
    }
    
    private void jMenuItemLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemLoadActionPerformed
//        File dataFile = chooseFileForIO(MainWindow.FILE_IO_TYPE_GES,"Open");
//        if (dataFile == null) { return; }
//        this.handleLoadFromFile(dataFile);
        this.handleImportFromTabbedActionPerformed(evt);
    }
    
    private void handleLoadFromFile(File dataFile) {
        try
        {
            BufferedReader fin = new BufferedReader(new FileReader(dataFile.getCanonicalPath()));
            String string_in = "";
            String cumu_string_in = "";
            while ((string_in = fin.readLine()) != null)
            {
                cumu_string_in += string_in + "\n";
            }
            fin.close();

            // actually make the new gsc the live one.
            // this is done by throwing away the entire existing gscUI and builing a new one
            
            this.jPanelContainerDisplay.remove(this.gscUI);
            this.gscUI = new GSComplexUI(GSComplex.deserialize(cumu_string_in),this);
            this.gscUI.gsc.setCenter(this.jPanelContainerDisplay.getHeight()/2, this.jPanelContainerDisplay.getWidth()/2);
            this.initializeGscUI();
            this.jPanelContainerDisplay.add(this.gscUI);                    

            // then un-link the old charts / windows
            GSComplexChartFrameCartRfPhi priorChartCart = this.chartCartRfPhi;
            GSComplexChartFramePolarRfPhi priorChartPolar = this.chartPolarRfPhi;
            GSComplexInfoFrameDeformationSeries priorWinDefSeries = this.windowDeformationsSeries;
                    
            boolean initialCartChartVis = this.chartCartRfPhi.isVisible();
            boolean initialPolarChartVis = this.chartPolarRfPhi.isVisible();
            boolean initialWinDefSeriesVis = this.windowDeformationsSeries.isVisible();
            this.chartCartRfPhi.setVisible(false);
            this.chartPolarRfPhi.setVisible(false);
            this.windowDeformationsSeries.setVisible(false);
            this.gscUI.gsc.removeAllWatchers();
            this.gscUI.removeAllWatchers();
            
            // now add new charts
            this.chartCartRfPhi = new GSComplexChartFrameCartRfPhi(this);
            this.chartPolarRfPhi = new GSComplexChartFramePolarRfPhi(this);
            this.windowDeformationsSeries = new GSComplexInfoFrameDeformationSeries(this);
            this.windowDeformationsSeries.setFromDeformationSeries(this.gscUI.gsc.deformations);
            this.windowDeformationsSeries.markCurrentDeformation(this.gscUI.gsc.getCurrentDeformationNumber());
            this.gscUI.gsc.addWatcher(this.chartCartRfPhi);
            this.gscUI.addWatcher(this.chartCartRfPhi);
            this.gscUI.gsc.addWatcher(this.chartPolarRfPhi);
            this.gscUI.addWatcher(this.chartPolarRfPhi);
        
            // and do the approp notifications to ensure painting is correct
            this.gscUI.notifyWatchers();
            this.gscUI.gsc.notifyWatchers();
            
            // set all the chart attributes
            this.chartCartRfPhi.setBounds(priorChartCart.getBounds());
            this.chartPolarRfPhi.setBounds(priorChartPolar.getBounds());
                    
            // set visibility as appropriate
            this.chartCartRfPhi.setVisible(initialCartChartVis);
            this.chartPolarRfPhi.setVisible(initialPolarChartVis);
            this.windowDeformationsSeries.setVisible(initialWinDefSeriesVis);
            this.chartCartRfPhi.repaint();
            this.chartPolarRfPhi.repaint();
            this.windowDeformationsSeries.repaint();
        
            // now update the main window from the new gscUI state
            this.updateDeformNavControlsStates();
            this.updateStateOfCurrentDeformControls();
            this.updateStrainMatricesVisibilities();
            this.handleStrainNavPostAction();

            // now update the gscUI state from the main window
            // set all the various display flags
            this.gscUI.setFlagDisplayPebbleFill(this.jCheckBoxMenuItemFillPebbles.isSelected());
            this.gscUI.setFlagDisplayPebbleAxes(this.jCheckBoxMenuItemShowPebbleAxes.isSelected());
            this.gscUI.setFlagDisplayBackgroundAxis(this.jCheckBoxMenuItemShowBackgroundAxis.isSelected());
            this.gscUI.setFlagDisplayBackgroundImage(this.jCheckBoxMenuItemShowBackgroundImage.isSelected());
            this.gscUI.setFlagDisplayStrainEllipses(this.jCheckBoxMenuItemShowStrainEllipses.isSelected());

            // reset the display transforms and other such things to their bases
            this.jButtonUnzoomActionPerformed(null);
            this.jButtonCenterActionPerformed(null);

            // drop out of edit mode if we're in it
            if (this.jToggleButtonEditPebbles.isSelected()) {
                this.jToggleButtonEditPebbles.setSelected(false);
                this.jToggleButtonEditPebblesActionPerformed(null);
            }
        }
        catch (IOException exc)
        {
            JOptionPane.showMessageDialog (this,"Problem loading - aborted:\n"+exc.toString());
            exc.printStackTrace ();
        }

        this.chartCartRfPhi.validate();
        this.chartPolarRfPhi.validate();
        while (this.chartPolarRfPhi.chart.getWidth() < 1) {
            // there were timing problems where occasionally the program would crash on start up because of some internal timing issue where the width would nto be set before the code needed it to be non-zero
        }
        this.chartPolarRfPhi.setChartScaleTypeToLog();

        this.repaint ();
    }//GEN-LAST:event_jMenuItemLoadActionPerformed

    private void jMenuItemSaveCurrentDeformedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveCurrentDeformedActionPerformed
//        File saveFile = chooseFileForIO(MainWindow.FILE_IO_TYPE_GES,"Save");
//        if (saveFile == null) { return; }
//        this.handleDataToFile(saveFile,this.gscUI.gsc.serializeCurrent());
//        this.repaint ();
        this.handleExportCurrentDeformedActionPerformed(evt);
    }//GEN-LAST:event_jMenuItemSaveCurrentDeformedActionPerformed

    private void jButtonSnapshotterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSnapshotterActionPerformed
        try
        {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Robot robot = new Robot();
            BufferedImage image = robot.createScreenCapture (new Rectangle(this.jPanelContainerDisplay.getLocationOnScreen (), this.jPanelContainerDisplay.getSize ()));

            File saveFile = this.chooseFileForIO(MainWindow.FILE_IO_TYPE_IMG, "Save");
            if (saveFile == null) { return; }
            ImageIO.write (image,Util.getExtension (saveFile),saveFile);
            JOptionPane.showMessageDialog (this,"Saved to "+saveFile.getCanonicalPath ());
        }
        catch (Exception exc)
        {
            JOptionPane.showMessageDialog (this,"Error taking snapshot: "+exc.getMessage());
        }
    }//GEN-LAST:event_jButtonSnapshotterActionPerformed

    private void jMenuItemChartRfPhiCartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemChartRfPhiCartActionPerformed
        this.chartCartRfPhi.setVisible(true);
    }//GEN-LAST:event_jMenuItemChartRfPhiCartActionPerformed

    private void jMenuItemChartRf2PhiPolarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemChartRf2PhiPolarActionPerformed
        this.chartPolarRfPhi.setVisible(true);
    }//GEN-LAST:event_jMenuItemChartRf2PhiPolarActionPerformed

    private void jMenuItemChartDeformationSeriesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemChartDeformationSeriesActionPerformed
        this.windowDeformationsSeries.setVisible(true);
    }//GEN-LAST:event_jMenuItemChartDeformationSeriesActionPerformed

    private void jButtonAutoColorOnRfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAutoColorOnRfActionPerformed
        this.autoColorOptions.setDoAction(false);
        this.autoColorOptions.setAutoColorFor(AutoColorOptionsDialog.COLOR_ON_RF);
        this.autoColorOptions.setVisible(true);
    }//GEN-LAST:event_jButtonAutoColorOnRfActionPerformed

    public void handleDoAutoColorOnRF() {
        if (! this.autoColorOptions.isDoAction()) { return; }
        GSPebbleSet canonicalPebbles = this.gscUI.gsc.pebbleSets.get(0);
        GSPebbleSet setToUseAsBasis = this.gscUI.gsc.pebbleSets.get(0);
        if (this.autoColorOptions.isUseCurrentDeformation()) {
            setToUseAsBasis = this.gscUI.gsc.getCurrentlyDeformedPebbleSet();
        }
        int numGroups = this.autoColorOptions.getNumberOfColorGroups();
        
        if (this.autoColorOptions.isRangeFull()) {
            double min = 1.0;
            double max = this.chartCartRfPhi.getScaleMax();
            double rangeStep = (max-min)/numGroups;
            for (int i=0; i<setToUseAsBasis.size(); i++) {
                int pebbleGroupNumber = (int) ((setToUseAsBasis.get(i).getRF() - min)/rangeStep)+1;
                if (pebbleGroupNumber > numGroups) { pebbleGroupNumber = numGroups; }
                if (pebbleGroupNumber < 1) { pebbleGroupNumber = 1; }
                canonicalPebbles.get(i).setColor(this.autoColorOptions.getColorForGroupNumber(pebbleGroupNumber));
            }
        } else if (this.autoColorOptions.isRangeDynamic()) {
            double min = setToUseAsBasis.getMinRf();
            double max = setToUseAsBasis.getMaxRf();
            double rangeStep = (max-min)/numGroups;
            for (int i=0; i<setToUseAsBasis.size(); i++) {
                int pebbleGroupNumber = (int) ((setToUseAsBasis.get(i).getRF() - min)/rangeStep)+1;
                if (pebbleGroupNumber > numGroups) { pebbleGroupNumber = numGroups; }
                if (pebbleGroupNumber < 1) { pebbleGroupNumber = 1; }
                canonicalPebbles.get(i).setColor(this.autoColorOptions.getColorForGroupNumber(pebbleGroupNumber));
            }
        } else if (this.autoColorOptions.isRangeCount()) {
            int pebblesPerGroup = (int) (setToUseAsBasis.size()/numGroups);
            int pebblesInCurrentGroup = 0;
            int pebbleGroupNumber = 1;

            // sort the pebbles, then color them by (roughly) evenly sized group in their sorted order
            
            GSPebbleSet usingClone = setToUseAsBasis.clone();
            Collections.sort(usingClone,
                             new Comparator<GSPebble>() {
                                public int compare(GSPebble p1, GSPebble p2) {
                                   if (p1.getRF() > p2.getRF()) { return 1; }
                                   if (p1.getRF() < p2.getRF()) { return -1; }
                                   return 0;
                                }
                             }
                            );
            
            for (int i=0; i<usingClone.size(); i++) {
                canonicalPebbles.getPebbleById(usingClone.get(i).getId()).setColor(this.autoColorOptions.getColorForGroupNumber(pebbleGroupNumber));
                pebblesInCurrentGroup++;
                if (pebblesInCurrentGroup >= pebblesPerGroup) {
                    pebbleGroupNumber++;
                    pebblesInCurrentGroup = 0;
                }
            }
        }
        this.gscUI.gsc.rebuildPebbleSetsFromDeformationSeries();
        this.repaint();
    }
    
    private void jButtonAutoColorOnPhiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAutoColorOnPhiActionPerformed
        this.autoColorOptions.setDoAction(false);
        this.autoColorOptions.setAutoColorFor(AutoColorOptionsDialog.COLOR_ON_PHI);
        this.autoColorOptions.setVisible(true);
    }//GEN-LAST:event_jButtonAutoColorOnPhiActionPerformed

    private void jButtonLinkCompressionDeformActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLinkCompressionDeformActionPerformed
        this.gscUI.compressionXandYareLinked = ! this.gscUI.compressionXandYareLinked;
        if (this.gscUI.compressionXandYareLinked) {
            jButtonLinkCompressionDeform.setIcon(linkedIcon);
            jButtonLinkCompressionDeform.setToolTipText(this.linkedToolTip);
        } else {
            jButtonLinkCompressionDeform.setIcon(unlinkedIcon);
            jButtonLinkCompressionDeform.setToolTipText(this.unlinkedToolTip);
        }
    }//GEN-LAST:event_jButtonLinkCompressionDeformActionPerformed

    private void handleExportCurrentDeformedActionPerformed(java.awt.event.ActionEvent evt) {                                                               
        File saveFile = chooseFileForIO(MainWindow.FILE_IO_TYPE_TAB,"Export");
        if (saveFile == null) { return; }
        this.handleDataToFile(saveFile,this.gscUI.gsc.serializeCurrentToTabDelimited());
        this.repaint ();
    }                                                              

    private void handleImportFromTabbedActionPerformed(java.awt.event.ActionEvent evt) {                                                          
        File dataFile = chooseFileForIO(MainWindow.FILE_IO_TYPE_TAB,"Import");
        if (dataFile == null) { return; }
        this.handleLoadFromFile(dataFile);
    }                                                         

    private void handleExportAsTabbedActionPerformed(java.awt.event.ActionEvent evt) {                                                        
        File saveFile = chooseFileForIO(MainWindow.FILE_IO_TYPE_TAB,"Export");
        if (saveFile == null) { return; }
        this.handleDataToFile(saveFile,this.gscUI.gsc.serializeToTabDelimited());
        this.repaint ();
    }   
    
    public void handleDoAutoColorOnPhi() {
        if (! this.autoColorOptions.isDoAction()) { return; }
        GSPebbleSet canonicalPebbles = this.gscUI.gsc.pebbleSets.get(0);
        GSPebbleSet setToUseAsBasis = this.gscUI.gsc.pebbleSets.get(0);
        if (this.autoColorOptions.isUseCurrentDeformation()) {
            setToUseAsBasis = this.gscUI.gsc.getCurrentlyDeformedPebbleSet();
        }
        int numGroups = this.autoColorOptions.getNumberOfColorGroups();
        
        if (this.autoColorOptions.isRangeFull()) {
            double rangeStep = (180)/numGroups;
            for (int i=0; i<setToUseAsBasis.size(); i++) {
                int pebbleGroupNumber = (int) ((Util.toDegrees(setToUseAsBasis.get(i).getThetaRad()) + 90)/rangeStep)+1;
                if (pebbleGroupNumber > numGroups) { pebbleGroupNumber = numGroups; }
                if (pebbleGroupNumber < 1) { pebbleGroupNumber = 1; }
                canonicalPebbles.get(i).setColor(this.autoColorOptions.getColorForGroupNumber(pebbleGroupNumber));
            }
        }
        else if (this.autoColorOptions.isRangeDynamic()) {
            double min = setToUseAsBasis.getMinPhi();
            double max = setToUseAsBasis.getMaxPhi();
            double rangeStep = (max-min)/numGroups;
            for (int i=0; i<setToUseAsBasis.size(); i++) {
                double relevantTheta = setToUseAsBasis.get(i).getThetaRad();
                if (relevantTheta > Math.PI/2) {relevantTheta = relevantTheta - Math.PI;}
                if (relevantTheta < -1* Math.PI/2) {relevantTheta = relevantTheta + Math.PI;}
                int pebbleGroupNumber = (int) ((relevantTheta - min)/rangeStep)+1;
                if (pebbleGroupNumber > numGroups) { pebbleGroupNumber = numGroups; }
                if (pebbleGroupNumber < 1) { pebbleGroupNumber = 1; }
                canonicalPebbles.get(i).setColor(this.autoColorOptions.getColorForGroupNumber(pebbleGroupNumber));
            }
        }
        else if (this.autoColorOptions.isRangeCount()) {
            int pebblesPerGroup = (int) (setToUseAsBasis.size()/numGroups);
            int pebblesInCurrentGroup = 0;
            int pebbleGroupNumber = 1;

            // sort the pebbles, then color them by (roughly) evenly sized group in their sorted order
            
            GSPebbleSet usingClone = setToUseAsBasis.clone();
            Collections.sort(usingClone,
                             new Comparator<GSPebble>() {
                                public int compare(GSPebble p1, GSPebble p2) {
                                   if (p1.getThetaRad() > p2.getThetaRad()) { return 1; }
                                   if (p1.getThetaRad() < p2.getThetaRad()) { return -1; }
                                   return 0;
                                }
                             }
                            );
            
            for (int i=0; i<usingClone.size(); i++) {
                canonicalPebbles.getPebbleById(usingClone.get(i).getId()).setColor(this.autoColorOptions.getColorForGroupNumber(pebbleGroupNumber));
                pebblesInCurrentGroup++;
                if (pebblesInCurrentGroup >= pebblesPerGroup) {
                    pebbleGroupNumber++;
                    pebblesInCurrentGroup = 0;
                }
            }
        }
        this.gscUI.gsc.rebuildPebbleSetsFromDeformationSeries();
        this.repaint();
    }
    
    private void handleStrainNavPostAction() {
        this.gscUI.tentativeDeformationClear();
        this.handleDeformationReset();
        this.updateDeformNavControlsStates();
        this.setValuesForCumuRfPhi();
        this.setValuesForCumuTentRfPhi();
        this.setValuesForCumuStrain();
        this.updateNavPositionInfo();
        this.updateStateOfCurrentDeformControls();
        this.updateNavStrainInfo();
        this.updateStrainMatricesVisibilities();
        this.windowDeformationsSeries.markCurrentDeformation(this.gscUI.gsc.getCurrentDeformationNumber());
        this.repaint();
    }

    public void updateNavPositionInfo() {
        this.jLabelStrainNavPosition.setText((this.gscUI.gsc.getCurrentDeformationNumber()-1)+" /");
        this.jLabelStrainNavCount.setText(Integer.toString(this.gscUI.gsc.deformations.size()));
    }
    
    private void processKeyReleaseOnControl(javax.swing.JTextField controlField, java.awt.event.KeyEvent evt) {
        if (! this.keyCodeIgnoredOnRelease(evt.getKeyCode())) {
            if (evt.isShiftDown()) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER && ! this.gscUI.isTentativeDeformationCleared()) {
                    this.handleDeformationApplyRemove();
                } else 
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE  && ! this.gscUI.isTentativeDeformationCleared()) {
                    this.handleDeformReset();
                }
            } else
            if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                this.handleControlActivation(controlField);
            } else
            if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE  && ! this.gscUI.isTentativeDeformationCleared()) {
                this.handleDeformReset();
            }
        }
    }
    
    private void processLostFocusOnControl(javax.swing.JTextField controlField) {        
        ValueConstrainer vc = (ValueConstrainer) this.displayNumberConstraints.get(controlField);
        Util.sanitizeForDoubleNumberFormat(controlField, vc.getDisplayPrecision());
        if (vc.getDefaultVal() != Double.parseDouble(controlField.getText())) {
            this.handleControlActivation(controlField);
        }
    }
        
    private void handleControlActivation(javax.swing.JTextField controlField) {
        ValueConstrainer vc = (ValueConstrainer) this.displayNumberConstraints.get(controlField);
        Util.sanitizeForDoubleNumberFormat(controlField, vc.getDisplayPrecision());
        double d = Double.parseDouble(controlField.getText());
        if (vc.isOutOfRange(d)) {
            controlField.setText(Util.truncForDisplay(vc.constrain(d), vc.getDisplayPrecision()));
        }
        this.handleControlChange(controlField);
        this.updateStrainMatricesVisibilities();
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
    
    private void alterValueByKeyPressInControl(javax.swing.JTextField controlField,int keyCode) {
        String initialFieldText = controlField.getText();
        ValueConstrainer vc = (ValueConstrainer) this.displayNumberConstraints.get(controlField);
        if (keyCode == java.awt.event.KeyEvent.VK_UP) {
            Util.fieldValueUp(controlField, vc);
            this.handleControlChange(controlField);
        } else if (keyCode == java.awt.event.KeyEvent.VK_DOWN) {
            Util.fieldValueDown(controlField, vc);
            this.handleControlChange(controlField);
        }
    }
    
    private void handleDeformationReset() {
        this.clearOutControlFieldsOtherThan(null);
        this.enableDeformControls();
        this.enableStrainMatrixControls();
        this.enableRfPhiControls();
        this.updateStateOfCurrentDeformControls();
        this.jLabelStrainNavPosition.setText((this.gscUI.gsc.getCurrentDeformationNumber()-1) + " /");
        this.setValuesForCumuStrain();
        this.setValuesForCumuRfPhi();
        this.setValuesForCumuTentStrain();
        this.setValuesForCumuTentRfPhi();
        this.updateStrainMatricesVisibilities();
}
    
    private void handleControlChange(javax.swing.JTextField controlField) {
        if (this.isControlDeform(controlField)) {
            this.clearOutControlFieldsOtherThan(controlField);
        }
        this.setOtherFieldsLinkedToThisField(controlField);
        this.updateGSCUITentativeDeformBasedOn(controlField);
        if (this.gscUI.isTentativeDeformationCleared()) {
            this.updateStateOfCurrentDeformControls();
            this.jLabelStrainNavPosition.setText((this.gscUI.gsc.getCurrentDeformationNumber()-1)+" /");
            this.enableDeformControls();
            this.enableStrainMatrixControls();
            this.enableRfPhiControls();
        } else {
            this.updateStateOfCurrentDeformControls();
            this.jLabelStrainNavPosition.setText("* /");
            if (this.isControlDeform(controlField)) {
                this.disableStrainMatrixControls();
                this.disableRfPhiControls();
            } else if (this.isControlStrainMatrix(controlField)) {
                this.disableDeformControls();
                this.disableRfPhiControls();
            } else if (this.isControlRfPhi(controlField)) {
                this.disableStrainMatrixControls();
                this.disableDeformControls();
            }
        }
        this.setValuesForCumuTentStrain();
        this.updateStrainMatricesVisibilities();
    }
    
    private void updateStateOfCurrentDeformControls() {
        if (this.gscUI.isTentativeDeformationCleared()) {
            this.jButtonDeformReset.setEnabled(false);
            if (this.gscUI.gsc.getCurrentDeformationNumber()-1 == this.gscUI.gsc.deformations.size()) {
                this.setEnableOnDeformControls(true);
                this.gscUI.setModeDeforms();
            } else {
                this.setEnableOnDeformControls(false);
                this.gscUI.setModeStrainNav();
            }
            this.jButtonDeformApplyRemove.setText(MainWindow.LABEL_DEFORM_REMOVE);
            this.jButtonDeformApplyRemove.setEnabled((this.gscUI.gsc.deformations.size() > 0) && (this.gscUI.gsc.getCurrentDeformationNumber() > 1));
            this.jButtonDeformApplyRemove.setToolTipText("delete the current deformation from the series");
        } else {
            this.jButtonDeformApplyRemove.setEnabled(true);
            this.jButtonDeformApplyRemove.setText(MainWindow.LABEL_DEFORM_APPLY);
            this.jButtonDeformApplyRemove.setToolTipText("add the current, tentative deformation to the series");
            this.jButtonDeformReset.setEnabled(true);
        }
//        if (this.windowDeformationsSeries != null) {
//            this.windowDeformationsSeries.markCurrentDeformation(this.gscUI.gsc.getCurrentDeformationNumber());
//        }
    }
    
    private void setValuesForCumuStrain() {
        Deformation d = this.gscUI.gsc.getCompositeTransform();
        this.jLabelCumuStrainM00.setText(Util.truncForDisplay(d.m00));
        this.jLabelCumuStrainM10.setText(Util.truncForDisplay(d.m10*-1));
        this.jLabelCumuStrainM01.setText(Util.truncForDisplay(d.m01*-1));
        this.jLabelCumuStrainM11.setText(Util.truncForDisplay(d.m11));
    }
    private void setValuesForCumuRfPhi() {
        GSPebble s = new GSPebble(10,10);
        s.deform(this.gscUI.gsc.getCompositeTransform());
        this.setValueForControl(this.jTextFieldStrainCumuRF, s.getMajorRadius()/s.getMinorRadius());
        this.setValueForControl(this.jTextFieldStrainCumuPhi, Util.toDegrees(s.getThetaRad()));        
    }
    private void setValuesForCumuTentStrain() {
        if (this.gscUI.isTentativeDeformationCleared()) {
            this.jLabelCumuTentStrainM00.setText(this.jLabelCumuStrainM00.getText());
            this.jLabelCumuTentStrainM10.setText(this.jLabelCumuStrainM10.getText());
            this.jLabelCumuTentStrainM01.setText(this.jLabelCumuStrainM01.getText());
            this.jLabelCumuTentStrainM11.setText(this.jLabelCumuStrainM11.getText());
        } else {
            Deformation dT = this.gscUI.getTentativeDeformationCopy();
            Matrix2x2 d =  dT.times(this.gscUI.gsc.getCompositeTransform());
            this.jLabelCumuTentStrainM00.setText(Util.truncForDisplay(d.m00));
            this.jLabelCumuTentStrainM10.setText(Util.truncForDisplay(d.m10*-1));
            this.jLabelCumuTentStrainM01.setText(Util.truncForDisplay(d.m01*-1));
            this.jLabelCumuTentStrainM11.setText(Util.truncForDisplay(d.m11));
        }
        this.setValuesForCumuTentRfPhi();
    }
    private void setValuesForCumuTentRfPhi() {
        if (this.gscUI.isTentativeDeformationCleared()) {
            this.jTextFieldStrainCumuTentRF.setText(this.jTextFieldStrainCumuRF.getText());
            this.jTextFieldStrainCumuTentPhi.setText(this.jTextFieldStrainCumuPhi.getText());
        } else {
            Deformation ct = this.gscUI.cumuDeformation.clone();
            ct.timesInPlace(this.gscUI.tentativeDeformation);
            GSPebble strain = new GSPebble(Deformation.DISPLAY_RADIUS, Deformation.DISPLAY_RADIUS);
            strain.deform(ct);
            this.setValueForControl(this.jTextFieldStrainCumuTentRF, strain.getRF());
            this.setValueForControl(this.jTextFieldStrainCumuTentPhi, Util.toDegrees(strain.thetaRad));        
        }
    }
    
    private void setOtherFieldsLinkedToThisField(javax.swing.JTextField controlField) {
        if (this.jTextFieldShearX.equals(controlField)) {
            this.updateOtherControlsFromDeformControls();
        } else
        if (this.jTextFieldShearY.equals(controlField)) {
            this.updateOtherControlsFromDeformControls();
        } else
        if (this.jTextFieldCompressX.equals(controlField)) {
            if (this.gscUI.compressionXandYareLinked) {
                this.setValueForControl(this.jTextFieldCompressY, 1/Double.parseDouble(this.jTextFieldCompressX.getText()));
            }
            this.updateOtherControlsFromDeformControls();
        } else
        if (this.jTextFieldCompressY.equals(controlField)) {
            if (this.gscUI.compressionXandYareLinked) {
                this.setValueForControl(this.jTextFieldCompressX, 1/Double.parseDouble(this.jTextFieldCompressY.getText()));
            }
            this.updateOtherControlsFromDeformControls();
        } else
        if (this.jTextFieldRotDeg.equals(controlField)) {
            this.setValueForControl(this.jTextFieldRotRad, Util.toRadians(Double.parseDouble(this.jTextFieldRotDeg.getText())));
            this.updateOtherControlsFromDeformControls();
        } else
        if (this.jTextFieldRotRad.equals(controlField)) {
            this.setValueForControl(this.jTextFieldRotDeg, Util.toDegrees(Double.parseDouble(this.jTextFieldRotRad.getText())));
            this.updateOtherControlsFromDeformControls();
        } else
        if (this.jTextFieldStrainM00.equals(controlField) ||
            this.jTextFieldStrainM01.equals(controlField) ||
            this.jTextFieldStrainM10.equals(controlField) ||
            this.jTextFieldStrainM11.equals(controlField))
        {
            this.updateOtherControlsFromStrainControls();
        } else
        if (this.jTextFieldRFPhiCurrentPhi.equals(controlField) ||
            this.jTextFieldRFPhiCurrentRF.equals(controlField))
        {
            this.updateOtherControlsFromRFPhiControls();
        }
    }
    
    private void updateGSCUITentativeDeformBasedOn(javax.swing.JTextField controlField) {
        if ((this.jTextFieldShearX.equals(controlField)) || (this.jTextFieldShearY.equals(controlField))) {
            this.gscUI.tentativeDeformationSetToShear(Double.parseDouble(this.jTextFieldShearX.getText()), 
                                                      Double.parseDouble(this.jTextFieldShearY.getText()),
                                                      this.jTextFieldShearX.equals(controlField));
        } else
        if ((this.jTextFieldCompressX.equals(controlField)) || (this.jTextFieldCompressY.equals(controlField))) {
                this.gscUI.tentativeDeformationSetToCompression(Double.parseDouble(this.jTextFieldCompressX.getText()), 
                                                                Double.parseDouble(this.jTextFieldCompressY.getText()),
                                                                this.jTextFieldCompressX.equals(controlField));
        } else
        if (this.jTextFieldRotDeg.equals(controlField)) {
            this.gscUI.tentativeDeformationSetToRotate(Util.toRadians(Double.parseDouble(this.jTextFieldRotDeg.getText())));
        } else
        if (this.jTextFieldRotRad.equals(controlField)) {
            this.gscUI.tentativeDeformationSetToRotate(Double.parseDouble(this.jTextFieldRotRad.getText()));
        } else
        if (this.jTextFieldRFPhiCurrentRF.equals(controlField) || this.jTextFieldRFPhiCurrentPhi.equals(controlField)) {
            this.gscUI.tentativeDeformationSetFromRfPhi(Double.parseDouble(this.jTextFieldRFPhiCurrentRF.getText()),
                                                        Double.parseDouble(this.jTextFieldRFPhiCurrentPhi.getText()));
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
    private boolean isControlDeform(javax.swing.JTextField controlField) {
        return
            this.jTextFieldShearX.equals(controlField) ||
            this.jTextFieldShearY.equals(controlField) ||
            this.jTextFieldCompressX.equals(controlField) ||
            this.jTextFieldCompressY.equals(controlField) ||
            this.jTextFieldRotDeg.equals(controlField) ||
            this.jTextFieldRotRad.equals(controlField);
    }
    
    
    // NOTE: as of 2013/11/01 strain matrix controls are not active (until various pplication of deformation issues can be resolved)    
    private void disableStrainMatrixControls() {
//        this.setEnableOnStrainMatrixControls(false);
    }
    private void enableStrainMatrixControls() {
//        this.setEnableOnStrainMatrixControls(true);
    }
    
    private void setEnableOnStrainMatrixControls(boolean state) {
        this.jTextFieldStrainM00.setEnabled(state);
        this.jTextFieldStrainM01.setEnabled(state);
        this.jTextFieldStrainM10.setEnabled(state);
        this.jTextFieldStrainM11.setEnabled(state);
    }
    private boolean isControlStrainMatrix(javax.swing.JTextField controlField) {
        return
            this.jTextFieldStrainM00.equals(controlField) ||
            this.jTextFieldStrainM01.equals(controlField) ||
            this.jTextFieldStrainM10.equals(controlField) ||
            this.jTextFieldStrainM11.equals(controlField);
    }

    // NOTE: as of 2013/11/01 rf-phi controls are not active (until various pplication of deformation issues can be resolved)    
    private void disableRfPhiControls() {
//        this.setEnableOnRfPhiControls(false);
    }
    private void enableRfPhiControls() {
//        this.setEnableOnRfPhiControls(true);
    }
    private void setEnableOnRfPhiControls(boolean state) {
        this.jTextFieldRFPhiCurrentRF.setEnabled(state);
        this.jTextFieldRFPhiCurrentPhi.setEnabled(state);
    }
    private boolean isControlRfPhi(javax.swing.JTextField controlField) {
        return
            this.jTextFieldRFPhiCurrentRF.equals(controlField) ||
            this.jTextFieldRFPhiCurrentPhi.equals(controlField);
    }    

    private void setVisibilityOnCurDeformStrainInfo(boolean state) {
        this.jTextFieldStrainM00.setVisible(state);
        this.jTextFieldStrainM01.setVisible(state);
        this.jTextFieldStrainM10.setVisible(state);
        this.jTextFieldStrainM11.setVisible(state);
        this.jTextFieldRFPhiCurrentRF.setVisible(state);
        this.jTextFieldRFPhiCurrentPhi.setVisible(state);
        this.jLabelCurStrainLeftBracket.setVisible(state);
        this.jLabelCurStrainRightBracket.setVisible(state);
        this.jLabelRf.setVisible(state);
        this.jLabelPhi.setVisible(state);
    }
    private void setVisibilityOnNavDeformStrainInfo(boolean state) {
        this.jTextFieldNavStrainM00.setVisible(state);
        this.jTextFieldNavStrainM01.setVisible(state);
        this.jTextFieldNavStrainM10.setVisible(state);
        this.jTextFieldNavStrainM11.setVisible(state);
        this.jLabelNavStrainLeftBracket.setVisible(state);
        this.jLabelNavStrainRightBracket.setVisible(state);
    }
    private void setVisibilityOnCumuTentDeformStrainInfo(boolean state) {
        this.jLabelCumuTentStrainM00 .setVisible(state);
        this.jLabelCumuTentStrainM01.setVisible(state);
        this.jLabelCumuTentStrainM10.setVisible(state);
        this.jLabelCumuTentStrainM11.setVisible(state);
        this.jLabelCumuTentStrainLeftBracket.setVisible(state);
        this.jLabelCumuTentStrainRightBracket.setVisible(state);
        this.jTextFieldStrainCumuTentRF.setVisible(state);
        this.jTextFieldStrainCumuTentPhi.setVisible(state);
        this.jLabelCumuTentRF.setVisible(state);
        this.jLabelCumuTentPhi.setVisible(state);
    }
    private void setVisibilityOnCumuStrainInfo(boolean state) {
        this.jLabelCumuStrainM00 .setVisible(state);
        this.jLabelCumuStrainM01.setVisible(state);
        this.jLabelCumuStrainM10.setVisible(state);
        this.jLabelCumuStrainM11.setVisible(state);
        this.jLabelCumuStrainLeftBracket.setVisible(state);
        this.jLabelCumuStrainRightBracket.setVisible(state);
        this.jTextFieldStrainCumuRF.setVisible(state);
        this.jTextFieldStrainCumuPhi.setVisible(state);
        this.jLabelCumuRF.setVisible(state);
        this.jLabelCumuPhi.setVisible(state);
    }
    
    private void disablePebbleEditingControls() {
        this.setEnableOnPebbleEditingControls(false);
    }
    private void enablePebbleEditingControls() {
        this.setEnableOnPebbleEditingControls(true);
    }
    private void setEnableOnPebbleEditingControls(boolean state) {
        this.jButtonPebbleColorSet.setEnabled(state);
        this.jButtonPebbleColorApply.setEnabled(state);
        this.jButtonBackgroundImage.setEnabled(state);
        this.jButtonAutoColorOnRf.setEnabled(state);
        this.jButtonAutoColorOnPhi.setEnabled(state);
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
    private javax.swing.JButton jButtonAutoColorOnPhi;
    private javax.swing.JButton jButtonAutoColorOnRf;
    private javax.swing.JButton jButtonBackgroundImage;
    private javax.swing.JButton jButtonCenter;
    private javax.swing.JButton jButtonDeformApplyRemove;
    private javax.swing.JButton jButtonDeformReset;
    private javax.swing.JButton jButtonLinkCompressionDeform;
    private javax.swing.JButton jButtonPebbleColorApply;
    private javax.swing.JButton jButtonPebbleColorSet;
    private javax.swing.JButton jButtonSnapshotter;
    private javax.swing.JButton jButtonStrainNavNext;
    private javax.swing.JButton jButtonStrainNavPrevious;
    private javax.swing.JButton jButtonUnzoom;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItemFillPebbles;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItemShowBackgroundAxis;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItemShowBackgroundImage;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItemShowPebbleAxes;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItemShowStrainEllipses;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabelCompressControl;
    private javax.swing.JLabel jLabelCompressX;
    private javax.swing.JLabel jLabelCompressrY;
    private javax.swing.JLabel jLabelCumuPhi;
    private javax.swing.JLabel jLabelCumuRF;
    private javax.swing.JLabel jLabelCumuStrainLeftBracket;
    private javax.swing.JLabel jLabelCumuStrainM00;
    private javax.swing.JLabel jLabelCumuStrainM01;
    private javax.swing.JLabel jLabelCumuStrainM10;
    private javax.swing.JLabel jLabelCumuStrainM11;
    private javax.swing.JLabel jLabelCumuStrainRightBracket;
    private javax.swing.JLabel jLabelCumuTentPhi;
    private javax.swing.JLabel jLabelCumuTentRF;
    private javax.swing.JLabel jLabelCumuTentStrainLeftBracket;
    private javax.swing.JLabel jLabelCumuTentStrainM00;
    private javax.swing.JLabel jLabelCumuTentStrainM01;
    private javax.swing.JLabel jLabelCumuTentStrainM10;
    private javax.swing.JLabel jLabelCumuTentStrainM11;
    private javax.swing.JLabel jLabelCumuTentStrainRightBracket;
    private javax.swing.JLabel jLabelCurStrainLeftBracket;
    private javax.swing.JLabel jLabelCurStrainRightBracket;
    private javax.swing.JLabel jLabelNavStrainLeftBracket;
    private javax.swing.JLabel jLabelNavStrainRightBracket;
    private javax.swing.JLabel jLabelPhi;
    private javax.swing.JLabel jLabelPureShearActionKey;
    private javax.swing.JLabel jLabelRf;
    private javax.swing.JLabel jLabelRotateActionKey;
    private javax.swing.JLabel jLabelShearControl;
    private javax.swing.JLabel jLabelShearControl1;
    private javax.swing.JLabel jLabelShearX;
    private javax.swing.JLabel jLabelShearX1;
    private javax.swing.JLabel jLabelShearY;
    private javax.swing.JLabel jLabelShearY1;
    private javax.swing.JLabel jLabelSimpleShearActionKey;
    private javax.swing.JLabel jLabelStrainNavCount;
    private javax.swing.JLabel jLabelStrainNavPosition;
    private javax.swing.JLabel jLabelZoom;
    private javax.swing.JMenuItem jMenuItemChartDeformationSeries;
    private javax.swing.JMenuItem jMenuItemChartRf2PhiPolar;
    private javax.swing.JMenuItem jMenuItemChartRfPhiCart;
    private javax.swing.JMenuItem jMenuItemLoad;
    private javax.swing.JMenuItem jMenuItemSave;
    private javax.swing.JMenuItem jMenuItemSaveCurrentDeformed;
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
    private javax.swing.JPanel jPanelMatrixBgCumu;
    private javax.swing.JPanel jPanelMatrixBgCumuTent;
    private javax.swing.JPanel jPanelMatrixBgMostRecent;
    private javax.swing.JPanel jPanelResetButtons;
    private javax.swing.JPanel jPanelSnapshotControls;
    private javax.swing.JPanel jPanelZoomControl;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JSlider jSliderZoom;
    private javax.swing.JTextField jTextFieldCompressX;
    private javax.swing.JTextField jTextFieldCompressY;
    private javax.swing.JTextField jTextFieldNavStrainM00;
    private javax.swing.JTextField jTextFieldNavStrainM01;
    private javax.swing.JTextField jTextFieldNavStrainM10;
    private javax.swing.JTextField jTextFieldNavStrainM11;
    private javax.swing.JTextField jTextFieldRFPhiCurrentPhi;
    private javax.swing.JTextField jTextFieldRFPhiCurrentRF;
    private javax.swing.JTextField jTextFieldRotDeg;
    private javax.swing.JTextField jTextFieldRotRad;
    private javax.swing.JTextField jTextFieldShearX;
    private javax.swing.JTextField jTextFieldShearY;
    private javax.swing.JTextField jTextFieldStrainCumuPhi;
    private javax.swing.JTextField jTextFieldStrainCumuRF;
    private javax.swing.JTextField jTextFieldStrainCumuTentPhi;
    private javax.swing.JTextField jTextFieldStrainCumuTentRF;
    private javax.swing.JTextField jTextFieldStrainM00;
    private javax.swing.JTextField jTextFieldStrainM01;
    private javax.swing.JTextField jTextFieldStrainM10;
    private javax.swing.JTextField jTextFieldStrainM11;
    private javax.swing.JToggleButton jToggleButtonEditPebbles;
    // End of variables declaration//GEN-END:variables

    private void initializeGscUI() {
        int w = this.jPanelContainerDisplay.getWidth();
        int h = this.jPanelContainerDisplay.getHeight();
        this.gscUI.setPreferredSize (new java.awt.Dimension (w,h));
        this.gscUI.setBounds(0, 0, w, h);
        this.gscUI.setDoubleBuffered (true);
        this.gscUI.setCenter(this.jPanelContainerDisplay.getWidth()/2, this.jPanelContainerDisplay.getHeight()/2);
    }
}
