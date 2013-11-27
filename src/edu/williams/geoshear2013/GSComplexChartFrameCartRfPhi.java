/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.williams.geoshear2013;

import java.awt.Point;

/**
 *
 * @author cwarren
 */
public class GSComplexChartFrameCartRfPhi extends GSComplexChartFrame {

    public GSComplexChartFrameCartRfPhi(MainWindow launchedFromWindow) {
        super(launchedFromWindow);
        scaleTextItem.setText("7.0");
        double initChartWidth = java.awt.Toolkit.getDefaultToolkit ().getScreenSize().getWidth() - launchedFromWindow.getWidth();
        this.setSize((int)initChartWidth, launchedFromWindow.getHeight());
        Point startingLoc = launchedFromWindow.getLocation();
        startingLoc.translate(launchedFromWindow.getWidth(), 0);
        this.setLocation(startingLoc);
        this.setTitle("Chart of Rf by Phi, Cartesian Plot");
    }

    @Override
    public void initChart() {
        this.chart = new GSComplexChartCartesianRfPhi();
        this.add(chart);
    }

    @Override
    public void handleChartScaling()
    {
        GSComplexChartCartesianRfPhi ch = (GSComplexChartCartesianRfPhi)chart;
        if (chart.isUseLogScale())
        {
            ch.setMinValX(0.0);
            ch.setMaxValX(ch.getMaxValX() - 1);
            
        } else {
            ch.setMinValX(1.0);
            ch.setMaxValX(ch.getMaxValX() + 1);
        }
        ch.rescaleInfo();
    }

    @Override
    protected void formattedTextFieldMenuItemScaleFixedActionPerformed(java.awt.event.ActionEvent evt) {
        try
        {
            ((GSComplexChartCartesian)chart).setMaxValX(Double.parseDouble(this.scaleTextItem.getText()));
        } catch (NumberFormatException exc) {
            // nothin doin
        }
        super.formattedTextFieldMenuItemScaleFixedActionPerformed(evt);
    }

    double getScaleMax() {
        return ((GSComplexChartCartesianRfPhi)chart).getMaxValX();
    }
}
