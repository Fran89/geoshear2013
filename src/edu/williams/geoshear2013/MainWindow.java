package edu.williams.geoshear2013;

import java.awt.Color;
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
import java.util.HashMap;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
/*
 * TODO:
 *  - (2) OPTIONAL implement change tracks in cartesian chart
 *  - (.5) OPTIONAL implement change track in polar chart
 *  - (1) OPTIONAL in main window gscUI, implement pebble dragging when in edit mode (control down)
 *  - (.5) OPTIONAL in main window gscUI, implement pebble rotation when in edit mode (alt down)
 *  - (1) OPTIONAL implement deformation history/series chart
 *  - (1) final code clean up
 *    + find and resolve remaining to-do items
 *    + find and remove dev/debug output / comments
 *    + find and remove large commented out sections (wipe in-code versioning)
 * 
 *  - (1) write basic help text/file
 *    + note places that need additional work
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

    private HashMap displayNumberConstraints;
    
    public static String LABEL_DEFORM_APPLY = "Apply";
    public static String LABEL_DEFORM_REMOVE = "Remove";

    private boolean cachedStrainNavPrevEnableState = false;
    private boolean cachedStrainNavNextEnableState = false;

    private final JFileChooser fileChooser = new JFileChooser ();
    private final FileFilterImage filterImage = new FileFilterImage();
    private final FileFilterTab filterTab = new FileFilterTab();
    private final FileFilterGeoShear filterGeoShear = new FileFilterGeoShear();
    public static int FILE_IO_TYPE_GES = 1;
    public static int FILE_IO_TYPE_TAB = 2;
    public static int FILE_IO_TYPE_IMG = 3;
    
    private GSComplexChartFrameCartRfPhi chartCartRfPhi;
    private GSComplexChartFramePolarRfPhi chartPolarRfPhi;
    
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
        
//        Util.todo("fix charts not linked/active after data load");
        
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
        this.helpWindow.setSize(this.jPanelContainerDisplay.getWidth() + 50,this.jPanelContainerDisplay.getHeight() + 50);
        
        this.aboutWindow = new AboutWindow();
        this.aboutWindow.setLocationByPlatform(true);
        
        this.gscUI = new GSComplexUI(new GSComplex(),this);
        this.initializeGscUI();
        this.jPanelContainerDisplay.add(this.gscUI);

        // DEV DATA
//        GSPebble testPeb = new GSPebble("p11",100,100,30,20,0, Color.CYAN);
//        testPeb.setSelected(true);
        
//        System.err.println("test peb : "+testPeb.serialize());
//        GSPebble tp2 = GSPebble.deserialize(testPeb.serialize());         
//        System.err.println("test peb2: "+tp2.serialize());
//        System.err.println("testPeb==tp2: "+testPeb.equals(tp2));
//        
//        
//        System.err.println("(tab) test peb : "+testPeb.serializeToTabDelimited());
//        tp2 = GSPebble.deserialize(testPeb.serializeToTabDelimited()); 
//        System.err.println("(tab) test peb2: "+tp2.serializeToTabDelimited());
//
//        this.gscUI.gsc.pebbleSets.getLast().add(testPeb);
//        this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p12",200,100,45,30,.5, Color.GREEN));
//        this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p13",100,200,60,40,-1, Color.BLUE));
//        this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p14",200,200,75,50,2, Color.MAGENTA));

//        this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p21",-100,100,30,20,0, Color.CYAN));
//        this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p22",-200,100,45,30,.5, Color.GREEN));
//        this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p23",-100,200,60,40,-1, Color.BLUE));
//        this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p24",-200,200,75,50,2, Color.MAGENTA));
        
//        this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p31",100,-100,30,20,0, Color.CYAN));
//        this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p32",200,-100,45,30,.5, Color.GREEN));
//        this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p33",100,-200,60,40,-1, Color.BLUE));
//        this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p34",200,-200,75,50,2, Color.MAGENTA));

//        this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p41",-100,-100,30,20,0, Color.CYAN));
//        this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p42",-200,-100,45,30,.5, Color.GREEN));
//        this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p43",-100,-200,60,40,-1, Color.BLUE));
//        this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p44",-200,-200,75,50,2, Color.MAGENTA));

/*        
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p001",-300,300,25,7.5,0, new Color(185,138,116)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p002",-250,300,25,7.5,0.0371786112850863, new Color(142,243,164)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p003",-200,300,25,7.5,0.0743572225701726, new Color(223,38,106)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p004",-150,300,25,7.5,0.111535833855259, new Color(36,82,240)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p005",-100,300,25,7.5,0.148714445140345, new Color(104,74,243)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p006",-50,300,25,7.5,0.185893056425432, new Color(184,114,242)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p007",0,300,25,7.5,0.223071667710518, new Color(10,162,38)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p008",50,300,25,7.5,0.260250278995604, new Color(161,183,1)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p009",100,300,25,7.5,0.29742889028069, new Color(194,249,81)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p010",150,300,25,7.5,0.334607501565777, new Color(223,76,180)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p011",200,300,25,7.5,0.371786112850863, new Color(135,111,95)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p012",250,300,25,7.5,0.408964724135949, new Color(143,153,125)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p013",300,300,25,7.5,0.446143335421036, new Color(50,110,35)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p014",-300,250,25,10,0.483321946706122, new Color(73,170,219)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p015",-250,250,25,10,0.520500557991208, new Color(110,59,231)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p016",-200,250,25,10,0.557679169276295, new Color(214,100,153)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p017",-150,250,25,10,0.594857780561381, new Color(232,119,38)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p018",-100,250,25,10,0.632036391846467, new Color(39,160,90)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p019",-50,250,25,10,0.669215003131553, new Color(67,190,46)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p020",0,250,25,10,0.70639361441664, new Color(210,48,233)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p021",50,250,25,10,0.743572225701726, new Color(213,118,196)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p022",100,250,25,10,0.780750836986812, new Color(43,126,217)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p023",150,250,25,10,0.817929448271898, new Color(163,95,175)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p024",200,250,25,10,0.855108059556985, new Color(67,213,25)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p025",250,250,25,10,0.892286670842071, new Color(73,96,86)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p026",300,250,25,10,0.929465282127157, new Color(67,30,192)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p027",-300,200,25,12.5,0.966643893412243, new Color(44,160,83)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p028",-250,200,25,12.5,1.00382250469733, new Color(215,185,159)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p029",-200,200,25,12.5,1.04100111598242, new Color(124,184,227)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p030",-150,200,25,12.5,1.0781797272675, new Color(248,182,60)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p031",-100,200,25,12.5,1.11535833855259, new Color(11,118,111)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p032",-50,200,25,12.5,1.15253694983767, new Color(98,170,70)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p033",0,200,25,12.5,1.18971556112276, new Color(39,151,54)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p034",50,200,25,12.5,1.22689417240785, new Color(226,36,184)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p035",100,200,25,12.5,1.26407278369293, new Color(30,137,147)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p036",150,200,25,12.5,1.30125139497802, new Color(143,205,166)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p037",200,200,25,12.5,1.33843000626311, new Color(11,20,87)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p038",250,200,25,12.5,1.37560861754819, new Color(70,253,100)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p039",300,200,25,12.5,1.41278722883328, new Color(21,13,19)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p040",-300,150,25,15,1.44996584011836, new Color(250,153,175)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p041",-250,150,25,15,1.48714445140345, new Color(194,87,7)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p042",-200,150,25,15,1.52432306268854, new Color(210,64,165)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p043",-150,150,25,15,1.56150167397362, new Color(153,34,86)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p044",-100,150,25,15,1.59868028525871, new Color(249,119,177)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p045",-50,150,25,15,1.6358588965438, new Color(120,51,175)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p046",0,150,25,15,1.67303750782888, new Color(31,139,244)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p047",50,150,25,15,1.71021611911397, new Color(115,76,82)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p048",100,150,25,15,1.74739473039905, new Color(109,171,145)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p049",150,150,25,15,1.78457334168414, new Color(231,83,157)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p050",200,150,25,15,1.82175195296923, new Color(171,163,195)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p051",250,150,25,15,1.85893056425431, new Color(152,64,137)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p052",300,150,25,15,1.8961091755394, new Color(68,243,172)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p053",-300,100,25,17.5,1.93328778682449, new Color(252,252,13)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p054",-250,100,25,17.5,1.97046639810957, new Color(192,46,196)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p055",-200,100,25,17.5,2.00764500939466, new Color(195,29,249)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p056",-150,100,25,17.5,2.04482362067975, new Color(72,32,173)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p057",-100,100,25,17.5,2.08200223196483, new Color(26,165,83)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p058",-50,100,25,17.5,2.11918084324992, new Color(242,29,105)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p059",0,100,25,17.5,2.156359454535, new Color(254,198,199)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p060",50,100,25,17.5,2.19353806582009, new Color(227,195,234)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p061",100,100,25,17.5,2.23071667710518, new Color(67,34,125)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p062",150,100,25,17.5,2.26789528839026, new Color(132,132,61)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p063",200,100,25,17.5,2.30507389967535, new Color(30,208,66)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p064",250,100,25,17.5,2.34225251096044, new Color(6,28,115)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p065",300,100,25,17.5,2.37943112224552, new Color(214,12,23)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p066",-300,50,25,20,2.41660973353061, new Color(36,56,227)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p067",-250,50,25,20,2.4537883448157, new Color(56,148,179)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p068",-200,50,25,20,2.49096695610078, new Color(201,88,20)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p069",-150,50,25,20,2.52814556738587, new Color(191,125,8)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p070",-100,50,25,20,2.56532417867096, new Color(106,78,56)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p071",-50,50,25,20,2.60250278995604, new Color(209,202,181)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p072",0,50,25,20,2.63968140124113, new Color(113,132,197)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p073",50,50,25,20,2.67686001252622, new Color(152,164,177)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p074",100,50,25,20,2.7140386238113, new Color(172,218,142)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p075",150,50,25,20,2.75121723509639, new Color(205,117,49)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p076",200,50,25,20,2.78839584638148, new Color(199,237,140)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p077",250,50,25,20,2.82557445766656, new Color(194,40,70)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p078",300,50,25,20,2.86275306895165, new Color(91,91,123)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p079",-300,0,25,22.5,2.89993168023673, new Color(95,55,189)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p080",-250,0,25,22.5,2.93711029152182, new Color(17,14,142)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p081",-200,0,25,22.5,2.97428890280691, new Color(167,115,61)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p082",-150,0,25,22.5,3.01146751409199, new Color(197,221,26)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p083",-100,0,25,22.5,3.04864612537708, new Color(120,199,167)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p084",-50,0,25,22.5,3.08582473666217, new Color(82,85,101)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p085",0,0,25,22.5,3.12300334794725, new Color(227,123,171)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p086",50,0,25,22.5,3.16018195923234, new Color(90,5,154)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p087",100,0,25,22.5,3.19736057051743, new Color(175,133,77)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p088",150,0,25,22.5,3.23453918180251, new Color(146,75,181)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p089",200,0,25,22.5,3.2717177930876, new Color(150,109,210)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p090",250,0,25,22.5,3.30889640437269, new Color(190,197,251)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p091",300,0,25,22.5,3.34607501565777, new Color(23,67,194)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p092",-300,-50,25,25,3.38325362694286, new Color(111,28,14)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p093",-250,-50,25,25,3.42043223822795, new Color(15,6,180)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p094",-200,-50,25,25,3.45761084951303, new Color(228,136,249)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p095",-150,-50,25,25,3.49478946079812, new Color(92,83,124)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p096",-100,-50,25,25,3.5319680720832, new Color(210,130,176)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p097",-50,-50,25,25,3.56914668336829, new Color(237,191,48)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p098",0,-50,25,25,3.60632529465338, new Color(197,105,19)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p099",50,-50,25,25,3.64350390593846, new Color(246,44,180)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p100",100,-50,25,25,3.68068251722355, new Color(28,28,223)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p101",150,-50,25,25,3.71786112850864, new Color(89,37,142)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p102",200,-50,25,25,3.75503973979372, new Color(87,239,248)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p103",250,-50,25,25,3.79221835107881, new Color(48,19,155)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p104",300,-50,25,25,3.8293969623639, new Color(215,211,239)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p105",-300,-100,25,11.5,3.86657557364898, new Color(208,96,108)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p106",-250,-100,25,11.5,3.90375418493407, new Color(183,221,225)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p107",-200,-100,25,11.5,3.94093279621916, new Color(116,204,222)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p108",-150,-100,25,11.5,3.97811140750424, new Color(204,65,114)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p109",-100,-100,25,11.5,4.01529001878933, new Color(54,69,77)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p110",-50,-100,25,11.5,4.05246863007442, new Color(210,169,7)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p111",0,-100,25,11.5,4.0896472413595, new Color(91,192,139)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p112",50,-100,25,11.5,4.12682585264459, new Color(38,145,122)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p113",100,-100,25,11.5,4.16400446392967, new Color(140,42,135)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p114",150,-100,25,11.5,4.20118307521476, new Color(102,48,106)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p115",200,-100,25,11.5,4.23836168649985, new Color(137,219,77)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p116",250,-100,25,11.5,4.27554029778493, new Color(240,216,94)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p117",300,-100,25,11.5,4.31271890907002, new Color(120,184,89)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p118",-300,-150,25,14,4.34989752035511, new Color(79,166,124)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p119",-250,-150,25,14,4.38707613164019, new Color(66,165,180)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p120",-200,-150,25,14,4.42425474292528, new Color(88,15,30)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p121",-150,-150,25,14,4.46143335421037, new Color(140,72,148)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p122",-100,-150,25,14,4.49861196549545, new Color(86,91,24)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p123",-50,-150,25,14,4.53579057678054, new Color(225,250,62)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p124",0,-150,25,14,4.57296918806563, new Color(6,10,140)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p125",50,-150,25,14,4.61014779935071, new Color(175,252,4)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p126",100,-150,25,14,4.6473264106358, new Color(107,234,75)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p127",150,-150,25,14,4.68450502192089, new Color(79,217,193)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p128",200,-150,25,14,4.72168363320597, new Color(8,104,28)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p129",250,-150,25,14,4.75886224449106, new Color(237,26,141)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p130",300,-150,25,14,4.79604085577614, new Color(47,118,95)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p131",-300,-200,25,16.5,4.83321946706123, new Color(30,221,5)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p132",-250,-200,25,16.5,4.87039807834632, new Color(200,24,93)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p133",-200,-200,25,16.5,4.9075766896314, new Color(211,226,164)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p134",-150,-200,25,16.5,4.94475530091649, new Color(217,179,93)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p135",-100,-200,25,16.5,4.98193391220158, new Color(171,253,2)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p136",-50,-200,25,16.5,5.01911252348666, new Color(157,185,64)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p137",0,-200,25,16.5,5.05629113477175, new Color(222,85,187)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p138",50,-200,25,16.5,5.09346974605684, new Color(31,90,171)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p139",100,-200,25,16.5,5.13064835734192, new Color(107,71,31)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p140",150,-200,25,16.5,5.16782696862701, new Color(60,42,52)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p141",200,-200,25,16.5,5.2050055799121, new Color(70,21,154)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p142",250,-200,25,16.5,5.24218419119718, new Color(143,195,202)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p143",300,-200,25,16.5,5.27936280248227, new Color(80,213,91)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p144",-300,-250,25,19,5.31654141376736, new Color(100,212,22)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p145",-250,-250,25,19,5.35372002505244, new Color(129,51,233)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p146",-200,-250,25,19,5.39089863633753, new Color(33,251,16)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p147",-150,-250,25,19,5.42807724762261, new Color(74,114,92)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p148",-100,-250,25,19,5.4652558589077, new Color(201,49,139)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p149",-50,-250,25,19,5.50243447019279, new Color(49,94,170)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p150",0,-250,25,19,5.53961308147787, new Color(245,96,120)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p151",50,-250,25,19,5.57679169276296, new Color(128,139,144)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p152",100,-250,25,19,5.61397030404805, new Color(245,19,215)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p153",150,-250,25,19,5.65114891533313, new Color(126,185,236)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p154",200,-250,25,19,5.68832752661822, new Color(30,127,18)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p155",250,-250,25,19,5.72550613790331, new Color(245,130,5)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p156",300,-250,25,19,5.76268474918839, new Color(227,81,156)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p157",-300,-300,25,21.5,5.79986336047348, new Color(100,93,87)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p158",-250,-300,25,21.5,5.83704197175857, new Color(157,240,222)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p159",-200,-300,25,21.5,5.87422058304365, new Color(117,68,166)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p160",-150,-300,25,21.5,5.91139919432874, new Color(105,236,81)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p161",-100,-300,25,21.5,5.94857780561383, new Color(31,137,70)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p162",-50,-300,25,21.5,5.98575641689891, new Color(33,246,98)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p163",0,-300,25,21.5,6.022935028184, new Color(51,3,147)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p164",50,-300,25,21.5,6.06011363946909, new Color(69,213,240)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p165",100,-300,25,21.5,6.09729225075417, new Color(208,194,117)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p166",150,-300,25,21.5,6.13447086203926, new Color(243,195,69)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p167",200,-300,25,21.5,6.17164947332434, new Color(85,31,202)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p168",250,-300,25,21.5,6.20882808460943, new Color(170,78,99)));
this.gscUI.gsc.pebbleSets.getLast().add(new GSPebble("p169",300,-300,25,21.5,6.24600669589452, new Color(125,114,51)));
*/

        this.updateDeformNavControlsStates();
        this.updateStateOfCurrentDeformControls();
        this.updateStrainMatricesVisibilities();
        
        this.chartCartRfPhi = new GSComplexChartFrameCartRfPhi(this);
        this.gscUI.gsc.addWatcher(this.chartCartRfPhi);
        this.gscUI.addWatcher(this.chartCartRfPhi);
        
        this.chartPolarRfPhi = new GSComplexChartFramePolarRfPhi(this);
        this.gscUI.gsc.addWatcher(this.chartPolarRfPhi);
        this.gscUI.addWatcher(this.chartPolarRfPhi);
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
        jPanelEditPebbleControls = new javax.swing.JPanel();
        jToggleButtonEditPebbles = new javax.swing.JToggleButton();
        jButtonPebbleColorSet = new javax.swing.JButton();
        jButtonPebbleColorApply = new javax.swing.JButton();
        jButtonBackgroundImage = new javax.swing.JButton();
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
        jMenuItemExportAsTabbed = new javax.swing.JMenuItem();
        jMenuItemSaveCurrentDeformed = new javax.swing.JMenuItem();
        jMenuItemExportCurrentDeformed = new javax.swing.JMenuItem();
        DisplayMenu = new javax.swing.JMenu();
        jCheckBoxMenuItemShowPebbleAxes = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItemFillPebbles = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItemShowBackgroundAxis = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItemShowBackgroundImage = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItemShowStrainEllipses = new javax.swing.JCheckBoxMenuItem();
        ChartsMenu = new javax.swing.JMenu();
        jMenuItemChartRfPhiCart = new javax.swing.JMenuItem();
        jMenuItemChartRf2PhiPolar = new javax.swing.JMenuItem();

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

        jTextFieldStrainM00.setEditable(false);
        jTextFieldStrainM00.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldStrainM00.setText("1.000");
        jTextFieldStrainM00.setBorder(null);
        jTextFieldStrainM00.setForeground(GSComplexUI.INFO_COLOR_TENT);
        jPanelDeformMatrixLeft.add(jTextFieldStrainM00, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 40, 60, -1));

        jTextFieldStrainM10.setEditable(false);
        jTextFieldStrainM10.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldStrainM10.setText("0.000");
        jTextFieldStrainM10.setBorder(null);
        jTextFieldStrainM10.setForeground(GSComplexUI.INFO_COLOR_TENT);
        jPanelDeformMatrixLeft.add(jTextFieldStrainM10, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 70, 60, -1));

        jTextFieldRFPhiCurrentRF.setEditable(false);
        jTextFieldRFPhiCurrentRF.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jTextFieldRFPhiCurrentRF.setText("1.000");
        jTextFieldRFPhiCurrentRF.setBorder(javax.swing.BorderFactory.createLineBorder(GSComplexUI.INFO_COLOR_TENT));
        jTextFieldRFPhiCurrentRF.setForeground(GSComplexUI.INFO_COLOR_TENT);
        jPanelDeformMatrixLeft.add(jTextFieldRFPhiCurrentRF, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 6, 60, 20));

        jLabelRf.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelRf.setText("RF");
        jLabelRf.setForeground(GSComplexUI.INFO_COLOR_TENT);
        jPanelDeformMatrixLeft.add(jLabelRf, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 6, 18, 20));

        jLabelCurStrainLeftBracket.setFont(new java.awt.Font("Tahoma", 0, 72)); // NOI18N
        jLabelCurStrainLeftBracket.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelCurStrainLeftBracket.setText("[");
        jLabelCurStrainLeftBracket.setForeground(GSComplexUI.INFO_COLOR_TENT);
        jPanelDeformMatrixLeft.add(jLabelCurStrainLeftBracket, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 30, 90));

        jTextFieldStrainCumuRF.setEditable(false);
        jTextFieldStrainCumuRF.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jTextFieldStrainCumuRF.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jTextFieldStrainCumuRF.setText("1.000");
        jTextFieldStrainCumuRF.setBorder(javax.swing.BorderFactory.createLineBorder(GSComplexUI.INFO_COLOR_CUMU));
        jTextFieldStrainCumuRF.setForeground(GSComplexUI.INFO_COLOR_CUMU);
        jPanelDeformMatrixLeft.add(jTextFieldStrainCumuRF, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 150, 50, 20));

        jLabelCumuRF.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabelCumuRF.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelCumuRF.setText("RF");
        jLabelCumuRF.setForeground(GSComplexUI.INFO_COLOR_CUMU);
        jPanelDeformMatrixLeft.add(jLabelCumuRF, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, 18, 20));

        jLabelStrainNavPosition.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelStrainNavPosition.setText("0 /");
        jPanelDeformMatrixLeft.add(jLabelStrainNavPosition, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 200, 30, 20));

        jLabelCumuStrainM00.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabelCumuStrainM00.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelCumuStrainM00.setText("1");
        jLabelCumuStrainM00.setForeground(GSComplexUI.INFO_COLOR_CUMU);
        jLabelCumuStrainM00.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanelDeformMatrixLeft.add(jLabelCumuStrainM00, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 110, 33, -1));

        jLabelCumuStrainM01.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabelCumuStrainM01.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelCumuStrainM01.setText("0");
        jLabelCumuStrainM01.setForeground(GSComplexUI.INFO_COLOR_CUMU);
        jLabelCumuStrainM01.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanelDeformMatrixLeft.add(jLabelCumuStrainM01, new org.netbeans.lib.awtextra.AbsoluteConstraints(57, 110, 33, -1));

        jLabelCumuStrainM10.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabelCumuStrainM10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelCumuStrainM10.setText("0");
        jLabelCumuStrainM10.setForeground(GSComplexUI.INFO_COLOR_CUMU);
        jLabelCumuStrainM10.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanelDeformMatrixLeft.add(jLabelCumuStrainM10, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, 33, -1));

        jLabelCumuStrainM11.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabelCumuStrainM11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelCumuStrainM11.setText("1");
        jLabelCumuStrainM11.setForeground(GSComplexUI.INFO_COLOR_CUMU);
        jLabelCumuStrainM11.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanelDeformMatrixLeft.add(jLabelCumuStrainM11, new org.netbeans.lib.awtextra.AbsoluteConstraints(57, 130, 33, -1));

        jLabelCumuStrainRightBracket.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabelCumuStrainRightBracket.setText("]");
        jLabelCumuStrainRightBracket.setForeground(GSComplexUI.INFO_COLOR_CUMU);
        jPanelDeformMatrixLeft.add(jLabelCumuStrainRightBracket, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 98, 14, 50));

        jLabelCumuStrainLeftBracket.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabelCumuStrainLeftBracket.setText("[");
        jLabelCumuStrainLeftBracket.setForeground(GSComplexUI.INFO_COLOR_CUMU);
        jPanelDeformMatrixLeft.add(jLabelCumuStrainLeftBracket, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 98, 14, 50));

        jTextFieldStrainCumuPhi.setEditable(false);
        jTextFieldStrainCumuPhi.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jTextFieldStrainCumuPhi.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jTextFieldStrainCumuPhi.setText("0.000");
        jTextFieldStrainCumuPhi.setBorder(javax.swing.BorderFactory.createLineBorder(GSComplexUI.INFO_COLOR_CUMU));
        jTextFieldStrainCumuPhi.setForeground(GSComplexUI.INFO_COLOR_CUMU);
        jPanelDeformMatrixLeft.add(jTextFieldStrainCumuPhi, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 170, 50, 20));

        jLabelCumuPhi.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabelCumuPhi.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelCumuPhi.setText("phi");
        jLabelCumuPhi.setForeground(GSComplexUI.INFO_COLOR_CUMU);
        jPanelDeformMatrixLeft.add(jLabelCumuPhi, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, 20, 20));

        jButtonStrainNavPrevious.setIcon(new javax.swing.ImageIcon(getClass().getResource("/edu/williams/geoshear2013/img/arrow_left_light.gif"))); // NOI18N
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

        jPanelDeformControls.add(jPanelDeformMatrixLeft, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 160, 110, 230));

        jPanelDeformMatrixRight.setPreferredSize(new java.awt.Dimension(93, 75));
        jPanelDeformMatrixRight.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButtonStrainNavNext.setIcon(new javax.swing.ImageIcon(getClass().getResource("/edu/williams/geoshear2013/img/arrow_right_light.gif"))); // NOI18N
        jButtonStrainNavNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStrainNavNextActionPerformed(evt);
            }
        });
        jPanelDeformMatrixRight.add(jButtonStrainNavNext, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 200, 60, -1));

        jTextFieldStrainM01.setEditable(false);
        jTextFieldStrainM01.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldStrainM01.setText("0.000");
        jTextFieldStrainM01.setBorder(null);
        jTextFieldStrainM01.setForeground(GSComplexUI.INFO_COLOR_TENT);
        jPanelDeformMatrixRight.add(jTextFieldStrainM01, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, 60, -1));

        jTextFieldStrainM11.setEditable(false);
        jTextFieldStrainM11.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldStrainM11.setText("1.000");
        jTextFieldStrainM11.setBorder(null);
        jTextFieldStrainM11.setForeground(GSComplexUI.INFO_COLOR_TENT);
        jPanelDeformMatrixRight.add(jTextFieldStrainM11, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 60, -1));

        jTextFieldRFPhiCurrentPhi.setEditable(false);
        jTextFieldRFPhiCurrentPhi.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jTextFieldRFPhiCurrentPhi.setText("0.000");
        jTextFieldRFPhiCurrentPhi.setBorder(javax.swing.BorderFactory.createLineBorder(GSComplexUI.INFO_COLOR_TENT));
        jTextFieldRFPhiCurrentPhi.setForeground(GSComplexUI.INFO_COLOR_TENT);
        jPanelDeformMatrixRight.add(jTextFieldRFPhiCurrentPhi, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 6, 60, 20));

        jLabelPhi.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelPhi.setText("phi");
        jLabelPhi.setForeground(GSComplexUI.INFO_COLOR_TENT);
        jPanelDeformMatrixRight.add(jLabelPhi, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 6, 20, 20));

        jLabelCurStrainRightBracket.setFont(new java.awt.Font("Tahoma", 0, 72)); // NOI18N
        jLabelCurStrainRightBracket.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelCurStrainRightBracket.setText("]");
        jLabelCurStrainRightBracket.setForeground(GSComplexUI.INFO_COLOR_TENT);
        jPanelDeformMatrixRight.add(jLabelCurStrainRightBracket, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, 30, 90));

        jLabelStrainNavCount.setText("0");
        jPanelDeformMatrixRight.add(jLabelStrainNavCount, new org.netbeans.lib.awtextra.AbsoluteConstraints(4, 200, 26, 20));

        jLabelCumuTentStrainLeftBracket.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabelCumuTentStrainLeftBracket.setText("[");
        jLabelCumuTentStrainLeftBracket.setForeground(GSComplexUI.INFO_COLOR_CUMUTENT);
        jPanelDeformMatrixRight.add(jLabelCumuTentStrainLeftBracket, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 98, 14, 50));

        jLabelCumuTentStrainM00.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabelCumuTentStrainM00.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelCumuTentStrainM00.setText("1");
        jLabelCumuTentStrainM00.setForeground(GSComplexUI.INFO_COLOR_CUMUTENT);
        jLabelCumuTentStrainM00.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanelDeformMatrixRight.add(jLabelCumuTentStrainM00, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 110, 33, -1));

        jLabelCumuTentStrainM10.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabelCumuTentStrainM10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelCumuTentStrainM10.setText("0");
        jLabelCumuTentStrainM10.setForeground(GSComplexUI.INFO_COLOR_CUMUTENT);
        jLabelCumuTentStrainM10.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanelDeformMatrixRight.add(jLabelCumuTentStrainM10, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 130, 33, -1));

        jLabelCumuTentStrainM11.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabelCumuTentStrainM11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelCumuTentStrainM11.setText("1");
        jLabelCumuTentStrainM11.setForeground(GSComplexUI.INFO_COLOR_CUMUTENT);
        jLabelCumuTentStrainM11.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanelDeformMatrixRight.add(jLabelCumuTentStrainM11, new org.netbeans.lib.awtextra.AbsoluteConstraints(53, 130, 33, -1));

        jLabelCumuTentStrainM01.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabelCumuTentStrainM01.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelCumuTentStrainM01.setText("0");
        jLabelCumuTentStrainM01.setForeground(GSComplexUI.INFO_COLOR_CUMUTENT);
        jLabelCumuTentStrainM01.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanelDeformMatrixRight.add(jLabelCumuTentStrainM01, new org.netbeans.lib.awtextra.AbsoluteConstraints(53, 110, 33, -1));

        jLabelCumuTentStrainRightBracket.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabelCumuTentStrainRightBracket.setText("]");
        jLabelCumuTentStrainRightBracket.setForeground(GSComplexUI.INFO_COLOR_CUMUTENT);
        jPanelDeformMatrixRight.add(jLabelCumuTentStrainRightBracket, new org.netbeans.lib.awtextra.AbsoluteConstraints(86, 98, 14, 50));

        jLabelCumuTentRF.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabelCumuTentRF.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelCumuTentRF.setText("RF");
        jLabelCumuTentRF.setForeground(GSComplexUI.INFO_COLOR_CUMUTENT);
        jPanelDeformMatrixRight.add(jLabelCumuTentRF, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, 18, 20));

        jTextFieldStrainCumuTentRF.setEditable(false);
        jTextFieldStrainCumuTentRF.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jTextFieldStrainCumuTentRF.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jTextFieldStrainCumuTentRF.setText("1.000");
        jTextFieldStrainCumuTentRF.setBorder(javax.swing.BorderFactory.createLineBorder(GSComplexUI.INFO_COLOR_CUMUTENT));
        jTextFieldStrainCumuTentRF.setForeground(GSComplexUI.INFO_COLOR_CUMUTENT);
        jPanelDeformMatrixRight.add(jTextFieldStrainCumuTentRF, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 150, 50, 20));

        jTextFieldStrainCumuTentPhi.setEditable(false);
        jTextFieldStrainCumuTentPhi.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jTextFieldStrainCumuTentPhi.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jTextFieldStrainCumuTentPhi.setText("0.000");
        jTextFieldStrainCumuTentPhi.setBorder(javax.swing.BorderFactory.createLineBorder(GSComplexUI.INFO_COLOR_CUMUTENT));
        jTextFieldStrainCumuTentPhi.setForeground(GSComplexUI.INFO_COLOR_CUMUTENT);
        jPanelDeformMatrixRight.add(jTextFieldStrainCumuTentPhi, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 170, 50, 20));

        jLabelCumuTentPhi.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabelCumuTentPhi.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelCumuTentPhi.setText("phi");
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

        jPanelDeformControls.add(jPanelDeformMatrixRight, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 160, 110, 230));

        jPanelContainerControls.add(jPanelDeformControls);

        jPanelEditPebbleControls.setAlignmentX(0.0F);
        jPanelEditPebbleControls.setAlignmentY(0.0F);
        jPanelEditPebbleControls.setMaximumSize(new java.awt.Dimension(220, 100));
        jPanelEditPebbleControls.setMinimumSize(new java.awt.Dimension(220, 100));
        jPanelEditPebbleControls.setPreferredSize(new java.awt.Dimension(220, 100));

        jToggleButtonEditPebbles.setText("Edit");
        jToggleButtonEditPebbles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonEditPebblesActionPerformed(evt);
            }
        });

        jButtonPebbleColorSet.setBackground(new java.awt.Color(0, 0, 255));
        jButtonPebbleColorSet.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jButtonPebbleColorSet.setForeground(new java.awt.Color(255, 255, 255));
        jButtonPebbleColorSet.setText("Pick Color");
        jButtonPebbleColorSet.setEnabled(false);
        jButtonPebbleColorSet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPebbleColorSetActionPerformed(evt);
            }
        });

        jButtonPebbleColorApply.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jButtonPebbleColorApply.setText("Apply");
        jButtonPebbleColorApply.setEnabled(false);
        jButtonPebbleColorApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPebbleColorApplyActionPerformed(evt);
            }
        });

        jButtonBackgroundImage.setText("Background Image");
        jButtonBackgroundImage.setEnabled(false);
        jButtonBackgroundImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBackgroundImageActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelEditPebbleControlsLayout = new javax.swing.GroupLayout(jPanelEditPebbleControls);
        jPanelEditPebbleControls.setLayout(jPanelEditPebbleControlsLayout);
        jPanelEditPebbleControlsLayout.setHorizontalGroup(
            jPanelEditPebbleControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelEditPebbleControlsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelEditPebbleControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToggleButtonEditPebbles, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanelEditPebbleControlsLayout.createSequentialGroup()
                        .addComponent(jButtonPebbleColorSet, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonPebbleColorApply, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jButtonBackgroundImage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanelEditPebbleControlsLayout.setVerticalGroup(
            jPanelEditPebbleControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelEditPebbleControlsLayout.createSequentialGroup()
                .addComponent(jToggleButtonEditPebbles)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelEditPebbleControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonPebbleColorSet)
                    .addComponent(jButtonPebbleColorApply))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonBackgroundImage)
                .addGap(0, 16, Short.MAX_VALUE))
        );

        jPanelContainerControls.add(jPanelEditPebbleControls);

        jPanelSnapshotControls.setAlignmentX(0.0F);
        jPanelSnapshotControls.setAlignmentY(0.0F);
        jPanelSnapshotControls.setMaximumSize(new java.awt.Dimension(220, 100));
        jPanelSnapshotControls.setMinimumSize(new java.awt.Dimension(220, 100));
        jPanelSnapshotControls.setPreferredSize(new java.awt.Dimension(220, 100));

        jButtonSnapshotter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/edu/williams/geoshear2013/img/icon_camera_large_40x30.gif"))); // NOI18N
        jButtonSnapshotter.setText("Take Snapshot");
        jButtonSnapshotter.setToolTipText("Take a snapshot");
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
        FileMenu.setToolTipText("Save, Open, Export");

        jMenuItemSave.setText("Save");
        jMenuItemSave.setToolTipText("Save everything to a .ges file");
        jMenuItemSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSaveActionPerformed(evt);
            }
        });
        FileMenu.add(jMenuItemSave);

        jMenuItemLoad.setText("Load");
        jMenuItemLoad.setToolTipText("Load new pebbles and deformations from a .ges file");
        jMenuItemLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemLoadActionPerformed(evt);
            }
        });
        FileMenu.add(jMenuItemLoad);

        jMenuItemExportAsTabbed.setText("Export to .tab");
        jMenuItemExportAsTabbed.setToolTipText("Save everything in a tab-delimited format");
        jMenuItemExportAsTabbed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExportAsTabbedActionPerformed(evt);
            }
        });
        FileMenu.add(jMenuItemExportAsTabbed);

        jMenuItemSaveCurrentDeformed.setText("Save current deformed");
        jMenuItemSaveCurrentDeformed.setToolTipText("Save the currently deformed view as a new basis (with no deformations)");
        jMenuItemSaveCurrentDeformed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSaveCurrentDeformedActionPerformed(evt);
            }
        });
        FileMenu.add(jMenuItemSaveCurrentDeformed);

        jMenuItemExportCurrentDeformed.setText("Export current deformed to .tab");
        jMenuItemExportCurrentDeformed.setToolTipText("Export the currently deformed view in a tab-delimted format (with no deformations)");
        jMenuItemExportCurrentDeformed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExportCurrentDeformedActionPerformed(evt);
            }
        });
        FileMenu.add(jMenuItemExportCurrentDeformed);

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

        ChartsMenu.setText("Charts");
        ChartsMenu.setToolTipText("Open new windows to display charts");

        jMenuItemChartRfPhiCart.setText("RF-Phi cartesian");
        jMenuItemChartRfPhiCart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemChartRfPhiCartActionPerformed(evt);
            }
        });
        ChartsMenu.add(jMenuItemChartRfPhiCart);

        jMenuItemChartRf2PhiPolar.setText("RF-2*Phi polar");
        jMenuItemChartRf2PhiPolar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemChartRf2PhiPolarActionPerformed(evt);
            }
        });
        ChartsMenu.add(jMenuItemChartRf2PhiPolar);

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
        File saveFile = chooseFileForIO(MainWindow.FILE_IO_TYPE_GES,"Save");
        if (saveFile == null) { return; }
        this.handleDataToFile(saveFile,this.gscUI.gsc.serialize());
        this.repaint ();
    }//GEN-LAST:event_jMenuItemSaveActionPerformed

    private File chooseFileForIO(int ioType,String buttonText) {
        FileFilter theFilter = this.filterGeoShear;
        if (ioType == MainWindow.FILE_IO_TYPE_GES) {
            theFilter = this.filterGeoShear;
        } 
        else if (ioType == MainWindow.FILE_IO_TYPE_TAB) {
            theFilter = this.filterTab;
        }
        else if (ioType == MainWindow.FILE_IO_TYPE_IMG) {
            theFilter = this.filterImage;
        }
        this.fileChooser.setFileFilter (theFilter);
        int returnVal = fileChooser.showDialog(this, buttonText);
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            File saveFile = fileChooser.getSelectedFile ();
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
        File dataFile = chooseFileForIO(MainWindow.FILE_IO_TYPE_GES,"Open");
        if (dataFile == null) { return; }
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
            this.initializeGscUI();
            this.jPanelContainerDisplay.add(this.gscUI);                    

            // then un-link the old charts
            GSComplexChartFrameCartRfPhi priorChartCart = this.chartCartRfPhi;
            GSComplexChartFramePolarRfPhi priorChartPolar = this.chartPolarRfPhi;
            boolean initialCartChartVis = this.chartCartRfPhi.isVisible();
            boolean initialPolarChartVis = this.chartPolarRfPhi.isVisible();
            this.chartCartRfPhi.setVisible(false);
            this.chartPolarRfPhi.setVisible(false);
            this.gscUI.gsc.removeAllWatchers();
            this.gscUI.removeAllWatchers();
            
            // now add new charts
            this.chartCartRfPhi = new GSComplexChartFrameCartRfPhi(this);
            this.chartPolarRfPhi = new GSComplexChartFramePolarRfPhi(this);
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
            this.chartCartRfPhi.repaint();
            this.chartPolarRfPhi.repaint();
        
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
        this.repaint ();
    }//GEN-LAST:event_jMenuItemLoadActionPerformed

    private void jMenuItemExportAsTabbedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExportAsTabbedActionPerformed
        File saveFile = chooseFileForIO(MainWindow.FILE_IO_TYPE_TAB,"Export");
        if (saveFile == null) { return; }
        this.handleDataToFile(saveFile,this.gscUI.gsc.serializeToTabDelimited());
        this.repaint ();        
    }//GEN-LAST:event_jMenuItemExportAsTabbedActionPerformed

    private void jMenuItemSaveCurrentDeformedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveCurrentDeformedActionPerformed
        File saveFile = chooseFileForIO(MainWindow.FILE_IO_TYPE_GES,"Save");
        if (saveFile == null) { return; }
        this.handleDataToFile(saveFile,this.gscUI.gsc.serializeCurrent());
        this.repaint ();
    }//GEN-LAST:event_jMenuItemSaveCurrentDeformedActionPerformed

    private void jMenuItemExportCurrentDeformedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExportCurrentDeformedActionPerformed
        File saveFile = chooseFileForIO(MainWindow.FILE_IO_TYPE_TAB,"Export");
        if (saveFile == null) { return; }
        this.handleDataToFile(saveFile,this.gscUI.gsc.serializeCurrentToTabDelimited());
        this.repaint ();
    }//GEN-LAST:event_jMenuItemExportCurrentDeformedActionPerformed

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
        } else {
            this.jButtonDeformApplyRemove.setEnabled(true);
            this.jButtonDeformApplyRemove.setText(MainWindow.LABEL_DEFORM_APPLY);
            this.jButtonDeformReset.setEnabled(true);
        }
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
            this.setValueForControl(this.jTextFieldCompressY, 1/Double.parseDouble(this.jTextFieldCompressX.getText()));
            this.updateOtherControlsFromDeformControls();
        } else
        if (this.jTextFieldCompressY.equals(controlField)) {
            this.setValueForControl(this.jTextFieldCompressX, 1/Double.parseDouble(this.jTextFieldCompressY.getText()));
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
    private javax.swing.JLabel jLabelRf;
    private javax.swing.JLabel jLabelShearControl;
    private javax.swing.JLabel jLabelShearControl1;
    private javax.swing.JLabel jLabelShearX;
    private javax.swing.JLabel jLabelShearX1;
    private javax.swing.JLabel jLabelShearY;
    private javax.swing.JLabel jLabelShearY1;
    private javax.swing.JLabel jLabelStrainNavCount;
    private javax.swing.JLabel jLabelStrainNavPosition;
    private javax.swing.JLabel jLabelZoom;
    private javax.swing.JMenuItem jMenuItemChartRf2PhiPolar;
    private javax.swing.JMenuItem jMenuItemChartRfPhiCart;
    private javax.swing.JMenuItem jMenuItemExportAsTabbed;
    private javax.swing.JMenuItem jMenuItemExportCurrentDeformed;
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
