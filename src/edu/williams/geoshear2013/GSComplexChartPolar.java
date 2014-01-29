/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.williams.geoshear2013;

import static edu.williams.geoshear2013.GSComplexChart.STROKE_LIGHT;
import static edu.williams.geoshear2013.GSComplexChart.TEXT_TURNER;
import java.awt.Color;
import java.awt.FontMetrics;
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
    protected double maxValRadius = 3;
    protected double minValAngle = -180;
    protected double maxValAngle = 180;

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
//        if ((this.frameWidth == 0) || (this.frameHeight == 0)) {
//            return;
//        }
        //System.err.println("determining chart frame of "+this);

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

        g2d.setFont(this.getPlotLabelFont());
        FontMetrics metrics = g2d.getFontMetrics(this.getPlotLabelFont());
        int fontHeightSpacing = metrics.getHeight() + 4;
        
        // radius min
        String label = this.logifyContourLabel(Util.truncForDisplay(this.minValRadius, 2));
//        g2d.drawString(label, cx-this.generalInset, labelY+this.textAllowance);
        g2d.drawString(label, cx-this.generalInset, labelY+fontHeightSpacing);
        
        if (this.isUseLogScale()) {
            label = Util.truncForDisplay(Math.exp(this.minValRadius),3);
            g2d.drawString(label,
                    cx-this.generalInset,
                    labelY+fontHeightSpacing+fontHeightSpacing);
            
        }
        
        // radius max
        label = this.logifyContourLabel(Util.truncForDisplay(this.getMaxValRadius(), 2));
//        g2d.drawString(label, ringsR - (int)(g2d.getFontMetrics().getStringBounds(label, g2d).getWidth())+this.generalInset, labelY+this.textAllowance);
        g2d.drawString(label, ringsR - (int)(g2d.getFontMetrics().getStringBounds(label, g2d).getWidth())+this.generalInset, labelY+fontHeightSpacing);

        if (this.isUseLogScale()) {
            label = "~"+Util.truncForDisplay(Math.exp(this.getMaxValRadius()),3);
            g2d.drawString(label,
                    ringsR - (int)(g2d.getFontMetrics().getStringBounds(label, g2d).getWidth())+this.generalInset,
                    labelY+fontHeightSpacing+fontHeightSpacing);            
        }
        
        // angle min/max
        label = "0";
//        fontHeightSpacing
                
//        this.drawTurnedString(g2d, label, this.getPlotLabelFont(), ringsR+this.textAllowance+this.generalInset, cy - (int)((g2d.getFontMetrics().getStringBounds(label, g2d).getWidth())/2.0), TEXT_TURNER);
        this.drawTurnedString(g2d, label, this.getPlotLabelFont(), ringsR+fontHeightSpacing+this.generalInset, cy - (int)((g2d.getFontMetrics().getStringBounds(label, g2d).getWidth())/2.0), TEXT_TURNER);

        g2d.setStroke(STROKE_HEAVY);
        g2d.fillOval(cx-3, cy-3, 6, 6);
        g2d.drawOval(ringsL, ringsT, ringsDiam, ringsDiam);
        g2d.drawLine(cx, cy, ringsR, cy);

        g2d.setStroke(STROKE_HEAVY_DOTTED);
        g2d.drawLine(cx, labelY, cx, cy);
        g2d.drawLine(ringsR, labelY, ringsR, cy);        
    }

    @Override
    protected void paintChartTitle(Graphics2D g2d) {
        String titleText = this.getTitle();
        
        g2d.setFont(this.getPlotTitleFont());
        FontMetrics metrics = g2d.getFontMetrics(this.getPlotLabelFont());
        int fontHeightSpacing = (int)(metrics.getHeight() * 1.8);
        
        int titleWidth = (int)(g2d.getFontMetrics().getStringBounds(titleText, g2d).getWidth());
        int titleX = this.generalInset + this.generalInset;
        int titleY = fontHeightSpacing + this.generalInset;

        // draw a cleared box in which the title will be shown
        g2d.setStroke(STROKE_LIGHT);
        g2d.setBackground(Color.WHITE);
        g2d.setColor(Color.WHITE);                
        g2d.fillRect(titleX-this.generalInset, this.generalInset, titleWidth+this.generalInset+this.generalInset, fontHeightSpacing+this.generalInset+this.generalInset);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(titleX-this.generalInset, this.generalInset, titleWidth+this.generalInset+this.generalInset, fontHeightSpacing+this.generalInset+this.generalInset);
        
        g2d.drawString(titleText, titleX, titleY);
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

        g2d.setFont(this.getPlotLabelFont());
        FontMetrics metrics = g2d.getFontMetrics(this.getPlotLabelFont());
        int fontHeightSpacing = metrics.getHeight() + 4;
                
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
//                g2d.drawString(contourLabel, (int)(this.frameCenter.x + contourPosRadius - labelOffset), labelY + this.textAllowance);
                g2d.drawString(contourLabel, (int)(this.frameCenter.x + contourPosRadius - labelOffset), labelY + fontHeightSpacing);
                
                if (this.isUseLogScale()) {
                    contourLabel = "~"+Util.truncForDisplay(Math.exp(contourValRadius),3);
                    g2d.drawString(contourLabel,
                            (int)(this.frameCenter.x + contourPosRadius - labelOffset),
                            labelY+fontHeightSpacing+fontHeightSpacing);            
                }
                
            }
        }

        int totContoursAngle = (this.numMajorContoursAngle+1)*(this.numMinorContoursAngle+1);
        double contourStepAngleVal = (this.maxValAngle - this.minValAngle) / totContoursAngle;

        int labelX; // labelY already defined above, though NOTE: use changes!

        int cx = (int)(this.frameCenter.x);
        int cy = (int)(this.frameCenter.y);

        double contourValAngle = this.minValAngle;
        showContour = true;
        for (int i=1; i<totContoursAngle; i++) {
            showContour = this.isShowContoursMinor();
            contourValAngle += contourStepAngleVal;
            
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
                double effectiveContourValAngle = contourValAngle;
                if (contourValAngle == 0) { effectiveContourValAngle = 180; }
                double contourThetaRad = Math.toRadians(effectiveContourValAngle);

                this.paintRay(g2d, contourThetaRad,0,0);

                if (contourValAngle == 0) {
                    contourLabel = "-180/180";
                } else {
                    contourLabel = Util.truncForDisplay(effectiveContourValAngle,0);
                }
                double labelAngle = contourThetaRad;
                double labelRadius = this.frameRadius;

                if (labelAngle > 0) {
                    labelAngle = labelAngle*-1 + Math.PI/2;
//                    labelRadius += this.textAllowance + this.generalInset;
                    labelRadius += fontHeightSpacing + this.generalInset;
                } else {
                    labelAngle = labelAngle*-1 - Math.PI/2;
                    labelRadius += this.generalInset;
                }
                if (contourValAngle == -90) // additional label offset on the bottom to avoid collision with other drawing of the frame
                {
                    contourLabel = " "+contourLabel;
                }
                Point2D.Double textPoint = new Point2D.Double(labelRadius * Math.cos(contourThetaRad),
                                                              labelRadius * Math.sin(contourThetaRad));
                this.drawTurnedString(g2d, contourLabel, this.getPlotLabelFont(), cx+(int)(textPoint.x), cy-(int)(textPoint.y), 
                                      new AffineTransformOp (AffineTransform.getRotateInstance (labelAngle),AffineTransformOp.TYPE_NEAREST_NEIGHBOR));
            }
        }
    }

    @Override
    /* NOTE: valueP.y is in DEGREES!
     * takes a value pair (x = radius, y = angle in degrees), returns a screen coordinate for that value point
     */
    protected Double getPaintPoint(Double valueP) {

        double angle = Math.toRadians(valueP.y*-1);
        
        double valRadius = valueP.x;
        if (this.isUseLogScale())
        {
            valRadius = Math.log(valRadius);
        }

        double radiusCoeff = (valRadius-this.minValRadius)/(this.getMaxValRadius()-this.minValRadius);

        double xOffset = (radiusCoeff*this.frameRadius*Math.cos(angle));
        double yOffset = (radiusCoeff*this.frameRadius*Math.sin(angle));

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
        double mx = screen_x - this.frameLeft;
        double my = screen_y - this.frameTop;

        double clickRadius = this.frameCenter.distance(mx, my);

        double radiusCoeff = Math.min(clickRadius/(double)(this.frameRadius),1);
        double radiusVal =  (radiusCoeff * (this.getMaxValRadius()-this.minValRadius)) + this.minValRadius;
        if ((mx < this.frameCenter.x) && (my < this.frameCenter.y))
        {
            radiusVal *= .996; // adjustment for screen wackiness re: distance calculation
        }
        double angleVal = Math.toDegrees(Math.atan2(this.frameCenter.x - mx, this.frameCenter.y - my)) + 90;

        this.infoRadiusVal = Util.truncForDisplay(radiusVal);
        this.infoAngleVal  = Util.truncForDisplay(angleVal);

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
//            GSPebbleSet curPebs = this.watchedComplex.getCurrentlyDeformedPebbleSet().clone();
//            curPebs.applyDeformation(this.watchedComplex.getUsedUI().getTentativeDeformationCopy());
//            double rfMax = curPebs.getMaxRf();
//            if (this.isUseLogScale())
//            {
//                rfMax = Math.log(rfMax);
//            }
//            double maxStep = this.numMajorContoursRadius + 1;
//            double maxRad = 0;
//            while (maxRad < rfMax) {maxRad += maxStep; }
//            return maxRad + this.getMinValRadius();
//            
            GSPebbleSet curPebs = this.watchedComplex.getCurrentlyDeformedPebbleSet().clone();
            curPebs.applyDeformation(this.watchedComplex.getUsedUI().getTentativeDeformationCopy());
            // NOTE: the 1.1 factor is to keep the pebbles a bit further from the right edge of the chart
            double rfMax = curPebs.getMaxRf() * 1.1; // !! NOTE: ideally generalize this away from using getMaxRf - pass in the potential max instead?
            if (this.isUseLogScale())
            {
                double maxRad = 0;
                double eftMaxRad = Math.exp(maxRad);
                while (eftMaxRad < rfMax) {
                    maxRad++;
                    eftMaxRad = Math.exp(maxRad);
                }
                return maxRad;
            } else {
                double maxStep = this.numMajorContoursRadius + 1;
                double maxRad = 0;
                while (maxRad < (rfMax - this.getMinValRadius())) {maxRad += maxStep; }
                return maxRad + this.getMinValRadius();
            }
            
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
