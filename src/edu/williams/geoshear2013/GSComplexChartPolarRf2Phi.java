/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.williams.geoshear2013;

import static edu.williams.geoshear2013.GSComplexChart.STROKE_LIGHT;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;

/**
 *
 * @author cwarren
 */
public class GSComplexChartPolarRf2Phi extends GSComplexChartPolar {

    public GSComplexChartPolarRf2Phi() {
        super();
        this.numMajorContoursRadius = 3;
        this.numMinorContoursRadius = 1;
        this.numMajorContoursAngle = 3;
        this.numMinorContoursAngle = 2;
        this.setMarkSize(8);
        this.setMarkShape(GSComplexChart.MARK_CIRCLE);
    }

    @Override
    protected void paintChartTitle(Graphics2D g2d) {
        if (this.isUseLogScale()) {
            this.setTitle(" ln(Rf) vs. 2*phi ");
        } else {
            this.setTitle(" Rf vs. 2*phi ");
        }
        super.paintChartTitle(g2d);
    }

    @Override
    protected void paintAxisLabels(Graphics2D g2d) {
        g2d.setFont(this.getPlotAxisLabelFont());
        FontMetrics metrics = g2d.getFontMetrics(this.getPlotLabelFont());
        int fontHeightSpacing = metrics.getHeight() + 4;

        int cx = (int)(this.frameCenter.x);
        int cy = (int)(this.frameCenter.y);
        int labelY = ringsB + this.textAllowance + this.generalInset;
        
        String label = "Rf";
        if (this.isUseLogScale()) {
            label = "ln(Rf)";
        }
        g2d.setColor(Color.BLACK);
        
        g2d.drawString(label, 
                       cx - this.textAllowance/2 - (int)(g2d.getFontMetrics().getStringBounds(label, g2d).getWidth()),
                       labelY+fontHeightSpacing+(this.isUseLogScale() ? fontHeightSpacing/2 : 0));
        
//        g2d.drawString(label, this.generalInset*2, this.frameHeight+fontHeightSpacing+fontHeightSpacing/2);
//        label = "phi";
//        this.drawTurnedString(g2d, label, this.getPlotLabelFont(),
//                              this.generalInset + fontHeightSpacing,
//                              this.frameHeight - (int)(g2d.getFontMetrics().getStringBounds(label, g2d).getWidth()), TEXT_TURNER);
        
        label = " 2*phi ";
        int labelWidth = (int)(g2d.getFontMetrics().getStringBounds(label, g2d).getWidth());
        Point2D.Double innerPoint = new Point2D.Double((this.frameRadius - labelWidth*2) * Math.cos(Math.PI*-.75),
                                                      (this.frameRadius - labelWidth*2) * Math.sin(Math.PI*-.75));
        Point2D.Double outerPoint = new Point2D.Double((this.frameRadius + labelWidth*2) * Math.cos(Math.PI*-.75),
                                                      (this.frameRadius + labelWidth*2) * Math.sin(Math.PI*-.75));
                
        int exEdge = cx + (int)(innerPoint.x);
        int eyEdge = cy - (int)(innerPoint.y);

        int exEnd = cx + (int)(outerPoint.x);
        int eyEnd = cy - (int)(outerPoint.y);

        this.drawTurnedString(g2d, label, this.getPlotAxisLabelFont(), 
//                              (int)(edgePoint.x), 
//                              (int)(edgePoint.y), 
                              exEnd, 
                              eyEnd, 
                              new AffineTransformOp (AffineTransform.getRotateInstance (Math.PI*-.25),AffineTransformOp.TYPE_NEAREST_NEIGHBOR));
//        
//        this.drawTurnedString(g2d, label, this.getPlotLabelFont(),
//                              cx+(int)(edgePoint.x), cy-(int)(edgePoint.y), 
//                              new AffineTransformOp (AffineTransform.getRotateInstance (Math.PI*.25),AffineTransformOp.TYPE_NEAREST_NEIGHBOR));


        g2d.setStroke(STROKE_LIGHT);
//        g2d.setColor(Color.GRAY);
        g2d.drawLine(exEdge, eyEdge, exEnd, eyEnd);
        
    }
   
    @Override
    protected void paintMeans(Graphics2D g2d) {
        if (this.isShowMeans() && this.watchedComplex.pebbleSets.get(0).size() > 0)
        {
             g2d.setColor(Color.YELLOW);
             g2d.setStroke(STROKE_HEAVY_DOTTED);
             this.watchedComplex.setMeans();
                     
             // log scale stuff handled in paintValueRing
             double paintRad = this.watchedComplex.getHarmonicMean();
             double paintVec = 2.0 * this.watchedComplex.getVectorMean();
             
             this.paintValueRing(g2d, paintRad, this.frameLeft,this.frameTop);
             this.paintRay(g2d, paintVec,this.frameLeft,this.frameTop);
        }
    }

    @Override
    protected void paintInfo(Graphics2D g2d)
    {
        if (this.showClickInfo)
        {
            this.rescaleInfo();
            if (! this.infoString.equals(""))
            {
                String radDisp = this.infoRadiusVal;
                if (this.isUseLogScale())
                {
                    radDisp = Util.truncForDisplay(Math.pow(Math.E,Double.parseDouble(radDisp)),2); // -1 is for 0-base of log chart as opposed to 1 base of linear chart
                }
                double eftAngle = Double.parseDouble(this.infoAngleVal)/-2.0;
                if (eftAngle < -90) {eftAngle+=180;}
                this.infoString = "Rf: "+radDisp+"  2*phi: "+Util.truncForDisplay(eftAngle*2,0);
                g2d.setColor(Color.BLACK);
                g2d.setStroke(STROKE_MEDIUM);
                g2d.drawOval(this.infoX-2, this.infoY-2,5,5);
            }
            super.paintInfo(g2d);
        }
    }

    @Override
    protected Point2D.Double getPebbleBasePaintPoint(GSPebble p)
    {        
        return this.getPaintPoint(new Point2D.Double(p.getRF(),-2.0*this.constrainDegrees(Util.toDegrees(p.getThetaRad()))));
    }

    private double constrainDegrees(double initialDegrees) {
        while (initialDegrees > 90) {
            initialDegrees -= 180;
        }
        while (initialDegrees < -90) {
            initialDegrees += 180;
        }
        return initialDegrees;
    }
    
    @Override
    protected String getPebbleInfoString(GSPebble p) {
        return "Rf: "+Util.truncForDisplay(p.getRF())+"  2*phi: "+Util.truncForDisplay((this.constrainDegrees(Util.toDegrees(p.getThetaRad()))*2.0),1);
    }

}
