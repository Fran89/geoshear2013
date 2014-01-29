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
public class GSComplexChartCartesianRfPhi extends GSComplexChartCartesian {

    public GSComplexChartCartesianRfPhi() {
        super();
        this.minValX = 1.0;
        this.maxValX = 4.0;
        this.minValY = -90;
        this.maxValY = 90;
        this.numMajorContoursX = 5;
        this.numMajorContoursY = 3;
        this.numMinorContoursX = 4;
        this.numMinorContoursY = 2;
        this.setMarkSize(8);
        this.setMarkShape(GSComplexChart.MARK_CIRCLE);
    }

   
    @Override
    protected void paintChartTitle(Graphics2D g2d) {
        if (this.isUseLogScale()) {
            this.setTitle(" ln(Rf) vs. phi ");
        } else {
            this.setTitle(" Rf vs. phi ");
        }
        super.paintChartTitle(g2d);
    }
            
    @Override
    protected void paintMeans(Graphics2D g2d) {
        if (this.isShowMeans() && this.watchedComplex.pebbleSets.get(0).size() > 0)
        {
            this.watchedComplex.setMeans();
             Point2D.Double mp = this.getPaintPoint(
                     new Point2D.Double(this.watchedComplex.getHarmonicMean(),
                                        Util.toDegrees(this.watchedComplex.getVectorMean())));
             g2d.setColor(Color.yellow);
             g2d.setStroke(STROKE_HEAVY_DOTTED);
             g2d.drawLine((int)(mp.x), (int)(this.frameTop+this.generalInset), (int)(mp.x), (int)(this.frameTop + this.frameHeight));
             g2d.drawLine((int)(this.frameLeft + this.generalInset + this.textAllowance+1), (int)(mp.y), (int)(this.frameLeft+this.frameWidth),(int)(mp.y));
        }
    }

    @Override
    protected void paintInfo(Graphics2D g2d)
    {
        if (this.showClickInfo)
        {
            this.rescaleInfo();
            if (! this.infoXVal.equals(""))
            {
                String xDisp = this.infoXVal;
                if (this.isUseLogScale())
                {
                    xDisp = Util.truncForDisplay(Math.pow(Math.E,Double.parseDouble(xDisp)),2); // -1 is for 0-base of log chart as opposed to 1 base of linear chart
                }
                this.infoString = "Rf: "+xDisp+"  phi: "+Util.truncForDisplay(this.infoYVal,0);
                g2d.setColor(Color.BLACK);
                g2d.setStroke(STROKE_MEDIUM);
                g2d.drawOval(this.infoX-4, this.infoY,5,5);
            }
            super.paintInfo(g2d);
        }
    }

    protected Point2D.Double getPebbleBasePaintPoint(GSPebble p)
    {
        double deg = constrainDegrees(Util.toDegrees(p.getThetaRad()));
        return this.getPaintPoint(new Point2D.Double(p.getRF(), deg));
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
        double deg = constrainDegrees(Util.toDegrees(p.getThetaRad()));
        return "Rf: "+Util.truncForDisplay(p.getRF())+"  phi: "+Util.truncForDisplay(deg,1);
    }


}
