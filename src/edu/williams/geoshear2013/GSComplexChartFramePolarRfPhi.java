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
public class GSComplexChartFramePolarRfPhi extends GSComplexChartFrame {

    public GSComplexChartFramePolarRfPhi(MainWindow launchedFrom) {
        super(launchedFrom);
        scaleTextItem.setText("5.0");
        double initChartWidth = java.awt.Toolkit.getDefaultToolkit ().getScreenSize().getWidth() - launchedFrom.getWidth();
        this.setSize((int)initChartWidth, launchedFrom.getHeight());
        Point startingLoc = launchedFrom.getLocation();
        startingLoc.translate(launchedFrom.getWidth(), 0);
        this.setLocation(startingLoc);
        this.setTitle("Chart of Rf by 2*Phi, Polar Plot");
    }

    @Override
    public void initChart() {
        this.chart = new GSComplexChartPolarRf2Phi();
        this.add(chart);
    }

    @Override
    public void handleChartScaling()
    {
        GSComplexChartPolarRf2Phi ch = (GSComplexChartPolarRf2Phi)chart;
        if (chart.isUseLogScale())
        {
            ch.setMinValRadius(0.0);
            ch.setMaxValRadius(ch.getMaxValRadius() - 1);

        } else {
            ch.setMinValRadius(1.0);
            ch.setMaxValRadius(ch.getMaxValRadius() + 1);
        }
        ch.rescaleInfo();
    }

    @Override
    protected void formattedTextFieldMenuItemScaleFixedActionPerformed(java.awt.event.ActionEvent evt) {
        //jRadioButtonMenuItemScaleFixed.setSelected(true);
        //jRadioButtonMenuItemScaleFixedActionPerformed(evt);
        try
        {
            ((GSComplexChartPolarRf2Phi)chart).setMaxValRadius(Double.parseDouble(this.scaleTextItem.getText()));
        } catch (NumberFormatException exc) {
            // nothin doin
        }
        super.formattedTextFieldMenuItemScaleFixedActionPerformed(evt);
    }
}
