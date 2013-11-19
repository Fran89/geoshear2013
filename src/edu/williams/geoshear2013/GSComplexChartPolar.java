/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.williams.geoshear2013;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

/**
 *
 * @author cwarren
 */
public abstract class GSComplexChartPolar extends GSComplexChart {

    protected double minValRadius = 1;
    private double maxValRadius = 5;
    protected double minValAngle = 0;
    protected double maxValAngle = 360;

    protected double majorCountourStepRadius;
    protected double minorCountourStepRadius;
    protected double majorCountourStepAngle;
    protected double minorCountourStepAngle;

    protected int numMajorContoursRadius = 3;
    protected int numMinorContoursRadius = 2; // per major tick
    protected int numMajorContoursAngle = 6;
    protected int numMinorContoursAngle = 3; // per major tick

    protected String infoRadiusVal="";
    protected String infoAngleVal="";

    protected Point2D.Double frameCenter;
    protected int frameRadius;

    protected int ringsL;
    protected int ringsR;
    protected int ringsT;
    protected int ringsB;
    protected int ringsDiam;

    @Override
    protected void GSComplexChartMouseClicked(java.awt.event.MouseEvent evt) {
        this.checkShowClick(evt);
        this.handleInfoFor(evt.getX(), evt.getY());
        this.repaint();
    }

    @Override
    public void determineChartFrame() {
        //this.setBackground(Color.CYAN);
        this.frameWidth = Math.min(this.getWidth(), this.getHeight());//-this.textAllowance-this.generalInset;
        this.frameHeight = frameWidth;
        this.frameCenter = new Point2D.Double(this.frameWidth/2.0, this.frameHeight/2.0 - (this.textAllowance) + this.generalInset );
        this.frameRadius = (this.frameWidth/2) - (int)(1.5*(this.textAllowance + this.generalInset))  - 1;

        this.chartFrame = new BufferedImage(this.frameWidth,this.frameHeight,BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = (Graphics2D)(this.chartFrame.getGraphics());
        g2d.setBackground(Color.WHITE);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, this.frameWidth, this.frameHeight);

        this.frameLeft = (this.getWidth()-this.frameWidth) / 2;
        this.frameTop = (this.getHeight()-this.frameHeight) / 2;

        int cx = (int)(this.frameCenter.x);
        int cy = (int)(this.frameCenter.y);
        ringsDiam = 2*this.frameRadius;
        ringsL = cx - this.frameRadius;
        ringsR = cx + this.frameRadius;
        ringsT = cy - this.frameRadius;
        ringsB = cy + this.frameRadius;

        int labelY = ringsB + this.textAllowance + this.generalInset;

        this.paintContours(g2d);

        g2d.setColor(Color.BLACK);

        // radius min
        String label = this.logifyContourLabel(Util.truncForDisplay(this.minValRadius, 2));
        g2d.drawString(label, cx-this.generalInset, labelY+this.textAllowance);

        // radius max
        label = this.logifyContourLabel(Util.truncForDisplay(this.getMaxValRadius(), 2));
        g2d.drawString(label, ringsR - (int)(g2d.getFontMetrics().getStringBounds(label, g2d).getWidth())+this.generalInset, labelY+this.textAllowance);

        // angle min/max
        label = Util.truncForDisplay(this.minValAngle, 0) + " / " + Util.truncForDisplay(this.maxValAngle, 0);
        //g2d.drawString(label, ringsR+this.generalInset, cy);
        this.drawTurnedString(g2d, label, ringsR+this.textAllowance+this.generalInset, cy - (int)((g2d.getFontMetrics().getStringBounds(label, g2d).getWidth())/2.0), TEXT_TURNER);

        g2d.setStroke(STROKE_HEAVY);
        g2d.fillOval(cx-3, cy-3, 6, 6);
        g2d.drawOval(ringsL, ringsT, ringsDiam, ringsDiam);
        g2d.drawLine(cx, cy, ringsR, cy);

        g2d.setStroke(STROKE_HEAVY_DOTTED);
        g2d.drawLine(cx, labelY, cx, cy);
        g2d.drawLine(ringsR, labelY, ringsR, cy);        
    }

    @Override
    protected void paintPebbles(Graphics2D g2d) {
        g2d.setStroke(STROKE_MEDIUM);
        GSPebbleSet curPebs = this.watchedComplex.getCurrentlyDeformedPebbleSet().clone();
        curPebs.applyDeformation(this.watchedComplex.getUsedUI().getTentativeDeformationCopy());
        this.paintPebbleSet(g2d,curPebs);
    }

    // these utility function assume the color and stroke are already all set
    protected void paintValueRing(Graphics2D g2d, double val, int xoffset, int yoffset)
    {
        if (this.isUseLogScale())
        {
            val = Math.log(val);
        }
        double radiusCoeff = (val-this.minValRadius)/(this.getMaxValRadius()-this.minValRadius);
        // implement log scale stuff
        paintScreenRing(g2d,radiusCoeff*(double)(this.frameRadius), xoffset, yoffset);
    }
    protected void paintScreenRing(Graphics2D g2d, double radius, int xoffset, int yoffset)
    {
        g2d.drawOval((int)(this.frameCenter.x + xoffset - radius),
                     (int)(this.frameCenter.y + yoffset - radius),
                     (int)(2.0 * radius),
                     (int)(2.0 * radius));
    }
    protected void paintRay(Graphics2D g2d, double angleRadians, int xoffset, int yoffset)
    {
        //double contourThetaRad = Math.toRadians(angleDegrees);
        Point2D.Double edgePoint = new Point2D.Double(this.frameRadius * Math.cos(angleRadians),
                                                      this.frameRadius * Math.sin(angleRadians));
        int cx = (int)(this.frameCenter.x+xoffset);
        int cy = (int)(this.frameCenter.y+yoffset);

        int ex = cx + (int)(edgePoint.x);
        int ey = cy - (int)(edgePoint.y);
        g2d.drawLine(cx, cy, ex, ey);
    }

    @Override
    protected void paintContours(Graphics2D g2d) {
        if (! (this.isShowContoursMajor() || this.isShowContoursMinor()))
        {
            return;
        }

        int totContoursRadius = (this.numMajorContoursRadius+1)*(this.numMinorContoursRadius+1);
        double contourStepRadiusVal = (this.getMaxValRadius() - this.minValRadius) / totContoursRadius;
        double contourStepRadiusPos = this.frameRadius / totContoursRadius;

        int labelY = ringsB + this.textAllowance + this.generalInset;

        String contourLabel;
        double contourValRadius = this.minValRadius;
        double contourPosRadius = 0;
        boolean showContour = true;
        for (int i=1; i<totContoursRadius; i++)
        {
            showContour = this.isShowContoursMinor();

            contourValRadius += contourStepRadiusVal;
            contourPosRadius += contourStepRadiusPos;

            g2d.setColor(Util.getLighterColorByStep(Color.LIGHT_GRAY,20));
            g2d.setStroke(STROKE_LIGHT);
            if (i % (this.numMinorContoursRadius+1) == 0)
            {
                showContour = this.isShowContoursMajor();
                g2d.setColor(Color.GRAY);
                g2d.setStroke(STROKE_MEDIUM);
                contourPosRadius += (double)(this.frameRadius)/205.0; // deals with some exact placement issues - probably arising from an inopportune use of an int cast somewhere else in my code, or perhaps something else
            }
            if (showContour)
            {
                paintScreenRing(g2d,contourPosRadius,0,0);

                g2d.setStroke(STROKE_LIGHT_DOTTED);
                if (i % (this.numMinorContoursRadius+1) == 0)
                {
                    g2d.setStroke(STROKE_MEDIUM_DOTTED);
                }
                g2d.drawLine((int)(this.frameCenter.x + contourPosRadius), labelY, (int)(this.frameCenter.x + contourPosRadius), (int)(this.frameCenter.y));

                contourLabel = this.logifyContourLabel(Util.truncForDisplay(contourValRadius,2));
                double labelOffset = g2d.getFontMetrics().getStringBounds(contourLabel, g2d).getWidth() / 2;
                g2d.drawString(contourLabel, (int)(this.frameCenter.x + contourPosRadius - labelOffset), labelY + this.textAllowance);
            }
        }

        int totContoursAngle = (this.numMajorContoursAngle+1)*(this.numMinorContoursAngle+1);
        double contourStepAngleVal = (this.maxValAngle - this.minValAngle) / totContoursAngle;

        int labelX; // labelY already defined above, though NOTE: use changes!

        int cx = (int)(this.frameCenter.x);
        int cy = (int)(this.frameCenter.y);

        double contourValAngle = this.minValAngle;
        showContour = true;
        for (int i=1; i<totContoursAngle; i++)
        {
            showContour = this.isShowContoursMinor();

            contourValAngle += contourStepAngleVal;

            //System.out.println("contourValAngle = "+contourValAngle);

            g2d.setColor(Util.getLighterColorByStep(Color.LIGHT_GRAY,20));
            g2d.setStroke(STROKE_LIGHT);
            if (i % (this.numMinorContoursAngle+1) == 0)
            {
                showContour = this.isShowContoursMajor();
                g2d.setColor(Color.GRAY);
                g2d.setStroke(STROKE_MEDIUM);
            }
            if (showContour)
            {

                double contourThetaRad = Math.toRadians(contourValAngle);
                paintRay(g2d, contourThetaRad,0,0);

                contourLabel = Util.truncForDisplay(contourValAngle,0);
                double labelAngle = (-1.0 * contourThetaRad) - (Math.PI/2.0);
                double labelRadius = this.frameRadius;
                if (contourThetaRad < Math.PI)
                {
                    labelAngle += Math.PI;
                    labelRadius += this.textAllowance + this.generalInset;
                }
                if (contourValAngle == 270)
                {
                    contourLabel = " "+contourLabel;
                }
                Point2D.Double textPoint = new Point2D.Double(labelRadius * Math.cos(contourThetaRad),
                                                              labelRadius * Math.sin(contourThetaRad));
                this.drawTurnedString(g2d, contourLabel, cx+(int)(textPoint.x), cy-(int)(textPoint.y), 
                                      new AffineTransformOp (AffineTransform.getRotateInstance (labelAngle),AffineTransformOp.TYPE_NEAREST_NEIGHBOR));
            }
        }
    }

    @Override
    /* NOTE: valueP.y is in DEGREES!
     * takes a value pair (x = radius, y = angle in degrees), returns a screen coordinate for that value point
     */
    protected Double getPaintPoint(Double valueP) {

        double angle = Math.toRadians(valueP.y);

        double valRadius = valueP.x;
        if (this.isUseLogScale())
        {
            valRadius = Math.log(valRadius);
        }

        double radiusCoeff = (valRadius-this.minValRadius)/(this.getMaxValRadius()-this.minValRadius);


        /*
         System.out.println("  valueP="+valueP);
        System.out.println("  radiusCoeff="+radiusCoeff);
        System.out.println("  angle="+angle);
         *
         */

        double xOffset = (radiusCoeff*this.frameRadius*Math.cos(angle));
        double yOffset = (radiusCoeff*this.frameRadius*Math.sin(angle));
        //System.out.println("  xOffset="+xOffset);
        //System.out.println("  yOffset="+yOffset);

        return new Point2D.Double(this.frameCenter.x + xOffset + this.frameLeft,
                                  this.frameCenter.y - yOffset + this.frameTop);
    }

    @Override
    protected void rescaleInfo()
    {
        this.determineChartFrame();
        this.handleInfoFor(this.infoX,this.infoY);
    }

    // debugging function
    private void screenIt(Graphics2D g2d, Color c, String s, double x, double y) {
        screenIt(g2d, c, s, (int)x, (int)y);
    }

    // debugging function
    private void screenIt(Graphics2D g2d, Color c, String s, int x, int y)
    {
        g2d.setColor(c);
        g2d.drawString(s, x, y);
    }

    @Override
    protected void handleInfoFor(int screen_x, int screen_y) {

//        Graphics2D g2d = this.chartFrame.createGraphics();
        //Graphics2D g2d = (Graphics2D)(this.getGraphics());
        //screenIt(g2d,Color.GREEN,"screen: "+screen_x+","+screen_y,screen_x,screen_y);

        double mx = screen_x - this.frameLeft;
        double my = screen_y - this.frameTop;

        //screenIt(g2d,Color.BLUE,"m: "+mx+","+my,mx,my);

        double clickRadius = this.frameCenter.distance(mx, my);

        //System.out.println(" > clickRadius="+clickRadius);
        //System.out.println(" > this.frameRadius="+this.frameRadius);
        double radiusCoeff = Math.min(clickRadius/(double)(this.frameRadius),1);
        double radiusVal =  (radiusCoeff * (this.getMaxValRadius()-this.minValRadius)) + this.minValRadius;
        if ((mx < this.frameCenter.x) && (my < this.frameCenter.y))
        {
            radiusVal *= .996; // adjustment for screen wackiness re: distance calculation
        }
        double angleVal = Math.toDegrees(Math.atan2(this.frameCenter.x - mx, this.frameCenter.y - my)) + 90;
        
        /*
        System.out.println(" > this.frameCenter="+this.frameCenter);
        System.out.println(" > screen_x="+screen_x);
        System.out.println(" > screen_y="+screen_y);
        System.out.println(" > mx="+mx);
        System.out.println(" > my="+my);
        System.out.println(" > radiusCoeff="+radiusCoeff);
        System.out.println(" > radiusVal="+radiusVal);
        System.out.println(" > angleVal="+angleVal);
        

         *
         */

        this.infoRadiusVal = Util.truncForDisplay(radiusVal);
        this.infoAngleVal  = Util.truncForDisplay(angleVal);


        //System.out.println("center: "+this.frameCenter.toString());
        this.infoString = this.infoRadiusVal+", "+this.infoAngleVal;

        // this ensures the info isn't outside the chart frame
        
        Point2D.Double locPt =
                getPaintPoint(
                    new Point2D.Double(this.isUseLogScale()?Math.pow(Math.E, radiusVal):radiusVal,
                                       angleVal)
                              );
        this.infoX = (int)(locPt.x);
        this.infoY = (int)(locPt.y);
    }

    public double getMaxValAngle() {
        return maxValAngle;
    }

    public void setMaxValAngle(double maxValAngle) {
        this.maxValAngle = maxValAngle;
    }

    public double getMaxValRadius() {

        if (this.isUseAdaptiveScale())
        {
            GSPebbleSet curPebs = this.watchedComplex.getCurrentlyDeformedPebbleSet().clone();
            curPebs.applyDeformation(this.watchedComplex.getUsedUI().getTentativeDeformationCopy());
            double rfMax = curPebs.getMaxRf();
            if (this.isUseLogScale())
            {
                rfMax = Math.log(rfMax);
            }
            double maxStep = this.numMajorContoursRadius + 1;
            double maxRad = 0;
            while (maxRad < rfMax) {maxRad += maxStep; }
            return maxRad + this.getMinValRadius();
        } else {
            return maxValRadius;
        }
    }

    public void setMaxValRadius(double maxValRadius) {
        this.maxValRadius = maxValRadius;
    }

    public double getMinValAngle() {
        return minValAngle;
    }

    public void setMinValAngle(double minValAngle) {
        this.minValAngle = minValAngle;
    }

    public double getMinValRadius() {
        return minValRadius;
    }

    public void setMinValRadius(double minValRadius) {
        this.minValRadius = minValRadius;
    }
}
