/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.williams.geoshear2013;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

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
    protected void paintMeans(Graphics2D g2d) {
        if (this.isShowMeans() && this.watchedComplex.pebbleSets.get(0).size() > 0)
        {
//             System.err.println("-----------------");
             g2d.setColor(Color.YELLOW);
             g2d.setStroke(STROKE_HEAVY_DOTTED);
             this.watchedComplex.setMeans();
                     
             // log scale stuff handled in paintValueRing
             double paintRad = this.watchedComplex.getHarmonicMean();
//             double paintVec = 2.0 * this.watchedComplex.getVectorMean() - Math.PI/2;
             double paintVec = 2.0 * this.watchedComplex.getVectorMean();
             
//             System.err.println("paintRad: "+paintRad);
//             System.err.println("paintVec: "+paintVec);
             
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
//                double eftAngle = Double.parseDouble(this.infoAngleVal)/2.0;
//                if (eftAngle < 0) { eftAngle = 180 + eftAngle; }
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
//        return "Rf: "+Util.truncForDisplay(p.getRF())+"  2*phi: "+Util.truncForDisplay(360.0 - (this.constrainDegrees(Util.toDegrees(p.getThetaRad()))*2.0));
        return "Rf: "+Util.truncForDisplay(p.getRF())+"  2*phi: "+Util.truncForDisplay((this.constrainDegrees(Util.toDegrees(p.getThetaRad()))*2.0),1);
    }

}
