/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.williams.geoshear2013;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ListIterator;

/**
 *
 * @author cwarren
 */
public abstract class GSComplexChart extends javax.swing.JPanel implements Watcher
{

    public static int MARK_RECT = 1;
    public static int MARK_CIRCLE = 2;
    public static int MARK_RING = 3;
    public static int MARK_BOX = 4;

    protected final static AffineTransformOp TEXT_TURNER = new AffineTransformOp (AffineTransform.getRotateInstance (Math.PI/2.0),AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
    protected final static BasicStroke STROKE_LIGHT = new BasicStroke(1.0f);
    protected final static BasicStroke STROKE_MEDIUM = new BasicStroke(2.0f);
    protected final static BasicStroke STROKE_HEAVY = new BasicStroke(3.0f);
    protected final static BasicStroke STROKE_LIGHT_DOTTED = new BasicStroke(1.0f,                    // Width
                                                   BasicStroke.CAP_SQUARE,    // End cap
                                                   BasicStroke.JOIN_MITER,    // Join style
                                                   10.0f,                     // Miter limit
                                                   new float[] {3.0f,3.0f}, // Dash pattern
                                                   0.0f);                     // Dash phase
    protected final static BasicStroke STROKE_MEDIUM_DOTTED = new BasicStroke(2.0f,                    // Width
                                                   BasicStroke.CAP_SQUARE,    // End cap
                                                   BasicStroke.JOIN_MITER,    // Join style
                                                   10.0f,                     // Miter limit
                                                   new float[] {5.0f,5.0f}, // Dash pattern
                                                   0.0f);                     // Dash phase
    protected final static BasicStroke STROKE_HEAVY_DOTTED = new BasicStroke(3.0f,                    // Width
                                                   BasicStroke.CAP_SQUARE,    // End cap
                                                   BasicStroke.JOIN_MITER,    // Join style
                                                   10.0f,                     // Miter limit
                                                   new float[] {8.0f,8.0f}, // Dash pattern
                                                   0.0f);                     // Dash phase

    protected BufferedImage chartFrame;
    protected String title = "";
    protected Font plotFont = Font.decode ("Arial-PLAIN-9");
    protected Font plotTitleFont = Font.decode ("Arial-BOLD-11");

    protected boolean showTrace = false;
    protected boolean showReferenceDataSparse = true;
    public boolean showReferenceDataDense = false;
    protected boolean showMeans = true;
    protected boolean showContoursMajor = true; // i.e. major and minor tick lines
    protected boolean showContoursMinor = false; // i.e. major and minor tick lines
    protected boolean showInfoForSelected = true;
    protected boolean showClickInfo = false;
    protected boolean useLogScale = false;
    protected boolean useAdaptiveScale = false;

    protected int frameLeft;
    protected int frameTop;
    protected int frameWidth;
    protected int frameHeight;

    protected int generalInset = 4;
    protected int textAllowance = 16;

    
    protected int markSize = 4;
    protected boolean markFill = false;
    protected int markShape = GSComplexChart.MARK_RECT;

    protected Point chartOrigin;

    protected GSPebbleSet referencePebblesSparse;
    public GSPebbleSet referencePebblesDense;

    protected String infoString = "";
    protected int infoX=0;
    protected int infoY=0;

    protected GSComplex watchedComplex;
    protected GSComplexUI watchedComplexUI;
    protected GSDeformationSeries watchedDeforms;

    protected static int INFO_PAINT_OFFSET = 4;

    //--------------------------------------------------------------------------
    public GSComplexChart() {
        this.setBackground(Color.WHITE);
        this.setForeground(Color.BLACK);

        this.referencePebblesSparse = new GSPebbleSet();
        int refPebCounter = 0;
        referencePebblesDense = new GSPebbleSet();
       /* Pebble np = new Pebble("rp"+refPebCounter,
                                                     0.0,
                                                     0.0,
                                                     2.0,
                                                     1.0,
                                                     45.0,
                                                     Color.RED);
        this.referencePebblesSparse.add(np);
         *
         */
        for (int phiVal = -90; phiVal < 90; phiVal += 15)
        {
            for (double rfVal = 1.5; rfVal < 30; rfVal += .5)
            {
                refPebCounter++;
                // String id, int x, int y, int major, int minor, int thetaDeg, Color color
                this.referencePebblesSparse.add(new GSPebble("rp"+refPebCounter, // id
                                                     0, // x
                                                     0, // y
                                                     rfVal, // major
                                                     1, // minor
                                                     Util.toRadians(phiVal), // phi
                                                     Color.DARK_GRAY));
            }
        }
        for (int phiVal = -90; phiVal < 90; phiVal += 5)
        {
            for (double rfVal = 1.2; rfVal < 30; rfVal += .25)
            {
                refPebCounter++;
                // String id, int x, int y, int major, int minor, int thetaDeg, Color color
                this.referencePebblesDense.add(new GSPebble("rp"+refPebCounter, // id
                                                     0, // x
                                                     0, // y
                                                     rfVal, // major
                                                     1, // minor
                                                     Util.toRadians(phiVal), // phi
                                                     Color.DARK_GRAY));
            }
        }
        
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                GSComplexChartMouseClicked(evt);
            }
        });
    }

    //--------------------------------------------------------------------------

    @Override
    public void paint(Graphics g)
    {
        super.paint(g);
        Graphics2D g2d = (Graphics2D)g;

        this.determineChartFrame();
        //System.out.println("left: "+this.frameLeft+"    top: "+this.frameTop);
        g2d.drawImage(this.chartFrame, null, this.frameLeft, this.frameTop);

        this.paintMeans(g2d);
        this.paintReferencePebbles(g2d);
        this.paintPebbles(g2d);
        this.paintInfo(g2d);
    }

    public abstract void determineChartFrame();

    /**
     * Display the harmonic and vector means on the given Graphics2D object
     * @param g2d
     */
    protected abstract void paintMeans(Graphics2D g2d);

    /**
     * Display the pebble key values (RF and phi) on the given Graphics2D object
     * @param g2d
     */
    protected abstract void paintPebbles(Graphics2D g2d);

    protected void paintPebbleSet(Graphics2D g2d, GSPebbleSet ps, int size, int markShapeId)
    {
        ListIterator pli = ps.listIterator();
        while (pli.hasNext())
        {
            GSPebble p = (GSPebble)(pli.next());
            this.paintPebble(g2d, p, size,markShapeId);
        }
    }
    protected void paintPebbleSet(Graphics2D g2d, GSPebbleSet ps)
    {
        this.paintPebbleSet(g2d, ps, this.markSize, this.markShape);
    }

    // huh- tried to make this abstract, but the run time system was complaining
    // that it wasn't implemented in a grandchild class that implemented it.
    // Making it a straight over-ride worked fine.
    protected String getPebbleInfoString(GSPebble p) { return p.toString(); }
    
    protected void paintPebble(Graphics2D g2d, GSPebble p, int markSize, int markShapeId)
    {

        Point2D.Double pp = this.getPebbleBasePaintPoint(p);
        double pebx = pp.x  - (markSize/2);
        double peby = pp.y  - (markSize/2);

        if (this.isShowTrace())
        {
            Util.todo("implement show trace for pebbles");
//            int pIndex = this.watchedComplex.getPebbles().indexOf(p);
//            //System.out.println("peb index is "+pIndex);
//            if (pIndex >= 0)
//            {
//                Pebble ip = this.watchedComplex.getBasePebbles().get(pIndex).clone();
//                g2d.setColor(p.getColor().brighter().brighter());
//                g2d.setStroke(STROKE_MEDIUM_DOTTED);
//                Point2D.Double pdpPrior = this.getPebbleBasePaintPoint(ip);
//                ListIterator dli = this.watchedComplex.getDeformations().listIterator();
//                int dIndex = 0;
//                while (dli.hasNext() && (dIndex <= this.watchedComplex.getDeformations().getCurIndex()))
//                {
//                    Deformation d = (Deformation)(dli.next());
//                    ip.deform(d);
//                    Point2D.Double pdp = this.getPebbleBasePaintPoint(ip);
//                    g2d.drawLine((int)(pdp.x), (int)(pdp.y), (int)(pdpPrior.x), (int)(pdpPrior.y));
//                    pdpPrior = (Point2D.Double)(pdp.clone());
//                    dIndex++;
//                }
//                if (pdpPrior != null) {
//                    g2d.drawLine((int)(pp.x), (int)(pp.y), (int)(pdpPrior.x), (int)(pdpPrior.y));
//                }
//            }
        }


        if (p.isSelected())
        {
            g2d.setColor(Color.LIGHT_GRAY);
            if ((markShapeId == GSComplexChart.MARK_CIRCLE) || (markShapeId == GSComplexChart.MARK_RING)) {
                g2d.fillOval((int)pebx-markSize/2, (int)peby-markSize/2, markSize*2, markSize*2);
            } else if ((markShapeId == GSComplexChart.MARK_RECT) || (markShapeId == GSComplexChart.MARK_BOX)) {
                g2d.fillRect((int)pebx-markSize/2, (int)peby-markSize/2, markSize*2, markSize*2);
            }
            if (this.isShowInfoForSelected())
            {
                // for some unknown reason the call to paintString makes nothing appear, while re-creating the code here works just fine
                // hack-y, but it works
                String pInfo = getPebbleInfoString(p); //"Rf: "+Util.truncForDisplay(p.getRf())+"  2*phi: "+Util.truncForDisplay(p.getThetaDeg()*2.0);
                //paintString(g2d,pInfo,(int)pebx,(int)peby);
                //g2d.setColor(Color.BLACK);
                g2d.setColor(p.getColor().darker().darker());
                int paintX = (int)pebx + markSize;
                int infoSize = (int)(g2d.getFontMetrics().getStringBounds(pInfo, g2d).getWidth() + 1);
                if (paintX + infoSize > this.getWidth()) { paintX = paintX - infoSize - 3; }
                int paintY = (int)peby - 2;
                infoSize = (int)(g2d.getFontMetrics().getStringBounds(pInfo, g2d).getHeight() + 1);
                if (paintY < infoSize) {paintY += infoSize + 4; }
                //g2d.drawString(pInfo,paintX,(int)peby);
                g2d.drawString(pInfo,paintX,paintY);
            }
        }

        g2d.setColor(p.getColor());
        if (markShapeId == GSComplexChart.MARK_CIRCLE) {
            g2d.fillOval((int)pebx, (int)peby, markSize, markSize);
        } else if (markShapeId == GSComplexChart.MARK_RING) {
            g2d.drawOval((int)pebx, (int)peby, markSize, markSize);
        } else if (markShapeId == GSComplexChart.MARK_RECT) {
            g2d.fillRect((int)pebx, (int)peby, markSize, markSize);
        } else if (markShapeId == GSComplexChart.MARK_BOX) {
            g2d.drawRect((int)pebx, (int)peby, markSize, markSize);
        }
    }


    protected abstract Point2D.Double getPebbleBasePaintPoint(GSPebble p);

    /**
     * Display the reference pebble key values (RF and phi) on the given Graphics2D object
     * @param g2d
     */
    protected void paintReferencePebbles(Graphics2D g2d) {
        //throw new UnsupportedOperationException("Not supported yet.");
        if (this.isShowReferenceDataSparse() || this.isShowReferenceDataDense())
        {
            GSPebbleSet rp = null;
            if (this.isShowReferenceDataDense()) {
                rp = this.referencePebblesDense.clone();
            } else {
                rp = this.referencePebblesSparse.clone();
            }
            this.watchedComplex.deformations.runAllDeformationsOn(rp, this.watchedComplex.getCurrentDeformationNumber()-1);
//            rp.deform(((GSComplex)(this.watchedComplex)).getDeformations().getCurCumuDeformation());
//            rp.deform(((GSComplexUI)(this.watchedComplexUI)).getCurrentDeformation());
            rp.applyDeformation(this.watchedComplex.getUsedUI().getTentativeDeformationCopy());
            g2d.setStroke(STROKE_MEDIUM);
            this.paintPebbleSet(g2d, rp,4,GSComplexChart.MARK_CIRCLE);
        }
    }
    
    /**
     * Display the contours (major and minor tick lines) on the given Graphics2D object
     * @param g2d
     */
    protected abstract void paintContours(Graphics2D g2d);

    /**
     * Converts a set of values to a set of x,y coordindates used for marking
     * that point on a g2d
     *
     * @param valueP the values of the point to paint
     * @return the x and y g2d coordinates corresponding to those values
     */
    protected abstract Point2D.Double getPaintPoint(Point2D.Double valueP);

    /**
     * reset the chart info data based on the given screen coordinates
     * @param x
     * @param y
     */
    protected abstract void handleInfoFor(int screen_x, int screen_y);

    protected void GSComplexChartMouseClicked(java.awt.event.MouseEvent evt) {
        //this.showClickInfo = true;
        this.checkShowClick(evt);
        //System.out.println("mouse click event is "+evt.toString());
        this.handleInfoFor(evt.getX(), evt.getY());
        this.repaint();
    }

    // turn on or off the click-inspection info
    protected void checkShowClick(java.awt.event.MouseEvent evt) {
        this.showClickInfo = ! evt.isShiftDown();
    }

    protected void rescaleInfo()
    {
        this.handleInfoFor(this.infoX,this.infoY+INFO_PAINT_OFFSET);
    }
    protected void paintInfo(Graphics2D g2d)
    {
        this.paintString(g2d, this.infoString, this.infoX, this.infoY);
    }

    protected void paintString(Graphics2D g2d, String s, int x, int y)
    {
        if (! s.equals(""))
        {
            g2d.setColor(Color.BLACK);
            int paintX = x;
            int infoSize = (int)(g2d.getFontMetrics().getStringBounds(s, g2d).getWidth());
            if (paintX + infoSize + 1 > this.getWidth()) {paintX -= infoSize + 3; }
            infoSize = (int)(g2d.getFontMetrics().getStringBounds(s, g2d).getHeight());
            int paintY = y-2;
            if (paintY < infoSize) {paintY += infoSize + INFO_PAINT_OFFSET; }
            g2d.drawString(this.infoString, paintX, paintY);
        }
    }

    //--------------------------------------------------------------------------
    // useful functions
    public void drawTurnedString(Graphics2D g2d, String s, int x, int y, AffineTransformOp op)
    {
        Rectangle2D bounds = g2d.getFontMetrics ().getStringBounds (s,(Graphics)g2d);
        BufferedImage textImage = new BufferedImage ((int)bounds.getWidth (),(int)bounds.getHeight ()+3,BufferedImage.TYPE_INT_RGB);

        Graphics2D textG2D = textImage.createGraphics ();
        textG2D.setColor (Color.WHITE);
        textG2D.fillRect (0,0,(int)bounds.getWidth (),(int)bounds.getHeight ()+3);
        textG2D.setColor (g2d.getColor());
        //textG2D.setFont (this.getPlotFont ());
        textG2D.drawString (s,0,(int)bounds.getHeight ());

        g2d.drawImage (textImage,op,x,y);
        //g2d.drawImage (textImage,op,10,100);
        //g2d.drawString("called drawTurnedString(g2d,"+",'"+s+"',"+x+","+y+","+op.toString()+")", 100, 100);
    }

    // adds appropriate text if we're using a log scale, otherwise leaves it alone
    protected String logifyContourLabel(String label)
    {
        if (this.isUseLogScale())
        {
            return "e^"+label;
        }
        return label;
    }

    //--------------------------------------------------------------------------
    // standard getters and setters

    public BufferedImage getChartFrame() {
        return chartFrame;
    }

    public void setChartFrame(BufferedImage chartFrame) {
        this.chartFrame = chartFrame;
    }

    public Point getChartOrigin() {
        return chartOrigin;
    }

    public void setChartOrigin(Point chartOrigin) {
        this.chartOrigin = chartOrigin;
    }

    public int getGeneralInset() {
        return generalInset;
    }

    public void setGeneralInset(int generalInset) {
        this.generalInset = generalInset;
    }

    public boolean isMarkFill() {
        return markFill;
    }

    public void setMarkFill(boolean markFill) {
        this.markFill = markFill;
    }

    public int getMarkShape() {
        return markShape;
    }

    public void setMarkShape(int markShape) {
        this.markShape = markShape;
    }

    public int getMarkSize() {
        return markSize;
    }

    public void setMarkSize(int markSize) {
        this.markSize = markSize;
    }

    public Font getPlotFont() {
        return plotFont;
    }

    public void setPlotFont(Font plotFont) {
        this.plotFont = plotFont;
    }

    public Font getPlotTitleFont() {
        return plotTitleFont;
    }

    public void setPlotTitleFont(Font plotTitleFont) {
        this.plotTitleFont = plotTitleFont;
    }

    public boolean isShowContoursMajor() {
        return showContoursMajor;
    }

    public void setShowContoursMajor(boolean showContoursMajor) {
        this.showContoursMajor = showContoursMajor;
    }

    public boolean isShowContoursMinor() {
        return showContoursMinor;
    }

    public void setShowContoursMinor(boolean showContoursMinor) {
        this.showContoursMinor = showContoursMinor;
    }

    public boolean isShowMeans() {
        return showMeans;
    }

    public void setShowMeans(boolean showMeans) {
        this.showMeans = showMeans;
    }

    public boolean isShowReferenceDataSparse() {
        return showReferenceDataSparse;
    }

    public void setShowReferenceDataSparse(boolean showReferenceData) {
        this.showReferenceDataSparse = showReferenceData;
    }


    /**
     * @return the showReferenceDataDense
     */
    public boolean isShowReferenceDataDense() {
        return showReferenceDataDense;
    }

    /**
     * @param showReferenceDataDense the showReferenceDataDense to set
     */
    public void setShowReferenceDataDense(boolean showReferenceDataDense) {
        this.showReferenceDataDense = showReferenceDataDense;
    }
    
    public boolean isShowTrace() {
        return showTrace;
    }

    public void setShowTrace(boolean showTrace) {
        this.showTrace = showTrace;
    }

    public int getTextAllowance() {
        return textAllowance;
    }

    public void setTextAllowance(int textAllowance) {
        this.textAllowance = textAllowance;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInfoString() {
        return infoString;
    }

    public void setInfoString(String infoString) {
        this.infoString = infoString;
    }

    public int getInfoX() {
        return infoX;
    }

    public void setInfoX(int infoX) {
        this.infoX = infoX;
    }

    public int getInfoY() {
        return infoY;
    }

    public void setInfoY(int infoY) {
        this.infoY = infoY;
    }

    public boolean isShowInfoForSelected() {
        return showInfoForSelected;
    }

    public void setShowInfoForSelected(boolean showInfoForSelected) {
        this.showInfoForSelected = showInfoForSelected;
    }

    public boolean isUseLogScale() {
        return useLogScale;
    }

    public void setUseLogScale(boolean useLogScale) {
        this.useLogScale = useLogScale;
    }

    public boolean isUseAdaptiveScale() {
        return useAdaptiveScale;
    }

    public void setUseAdaptiveScale(boolean useAdaptiveScale) {
        this.useAdaptiveScale = useAdaptiveScale;
    }

    //--------------------------------------------------------------------------
    // implement Watcher stuff
    public void reactTo(Watchable w, Object arg) {
        if (w.getClass() == GSComplex.class)
        {
            if (this.watchedComplex == null)
            {
                this.watchedComplex = (GSComplex)w;
            }
        } else if (w.getClass().toString() == GSComplexUI.class.toString())
        {
            if (this.watchedComplexUI == null)
            {
                this.watchedComplexUI = (GSComplexUI)w;
            }
        } else if (w.getClass().toString() == GSDeformationSeries.class.toString())
        {
            if (this.watchedDeforms == null)
            {
                this.watchedDeforms = (GSDeformationSeries)w;
            }
        }
        this.repaint();
    }

    public void setWatched(Watchable w)
    {
        if (w.getClass() == GSComplex.class)
        {
            this.watchedComplex = (GSComplex)w;
        } else if (w.getClass().toString() == GSComplexUI.class.toString())
        {
            this.watchedComplexUI = (GSComplexUI)w;
        } else if (w.getClass().toString() == GSDeformationSeries.class.toString())
        {
            this.watchedDeforms = (GSDeformationSeries)w;
        }
        this.reactTo(w, null);
    }
    public Watchable getWatched()
    {
        return this.watchedComplex;
    }
    public void clearWatched()
    {
        this.watchedDeforms = null;
        this.watchedComplexUI = null;
        this.watchedComplex = null;
    }
    // NOTE: in this implemetation there is no partial watcher clearing - clearing any clears all
    public void clearWatched(Watchable w)
    {
        this.watchedDeforms = null;
        this.watchedComplexUI = null;
        this.watchedComplex = null;
    }
}
