/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.williams.geoshear2013;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author cwarren
 */
public class GSPebble extends GSEllipse {
    
    /**
     * A constant indicating how close to the center of a pebble the user must click
     * to select that pebble. Also used to display the 'handle' at the center of a
     * pebble.
     */
    public static int SELECTION_RADIUS = 12;

    /**
     * The Color used to highlight a selected pebble when it's displayed
     */
    public static Color SELECTION_COLOR = Color.LIGHT_GRAY;
    /**
     * The Color used to highlight the point representing a selected pebble in a plot
     */
    public static Color SELECTION_PLOT_COLOR = Color.DARK_GRAY;

    /**
     * minimum long axis when adding a new pebble
     */
    public static int MIN_LONG_AXIS = 20;

    private final static String SERIAL_TOKEN = ";";

    /*------------------------------------------------------------------------*/
    
    public GSPebbleSet ofSet=null; // the set that contains this pebble    
    public boolean selected=false; // whether or not this pebble is selected
    
    public Color color=Color.BLACK;
    public Color colorContrast=Color.LIGHT_GRAY;

    /*------------------------------------------------------------------------*/
    public GSPebble() {
        super(0,0,1,1,0);
    }
    
    public GSPebble(double major, double minor) {
        super(major, minor);
    }

    public GSPebble(double major, double minor, double theta) {
        super(major, minor, theta);
    }

    public GSPebble(double x, double y, double major, double minor, double theta) {
        super(x, y, major, minor, theta);
    }
    
    public GSPebble(double x, double y, double major, double minor, double theta, Color color) {
        super(x, y, major, minor, theta);
        this.setColor(color);
    }
    
    /*------------------------------------------------------------------------*/
    @Override
    public GSPebble clone() {
        GSPebble theClone = new GSPebble(this.x, this.y, this.majorRadius, this.minorRadius, this.theta);
        theClone.setColor(this.color);
        return theClone;
    }
    
    /*------------------------------------------------------------------------*/

    public void drawOnto(Graphics2D g2d, boolean isFilled, boolean showAxes) {
        g2d.setColor(this.color);
        Color axisColor = this.color;

        if (isFilled) {
            g2d.fill(this.shape);
            if (showAxes) {
                g2d.setColor(this.colorContrast);
                this.drawAxes(g2d);
            }
        } else {
            g2d.draw(this.shape);
            if (showAxes) {
                this.drawAxes(g2d);
            }
        }
    }
    
    private void drawAxes(Graphics2D g2d) {
        // long axis
        g2d.drawLine((int)(this.x - Math.cos(this.theta)*this.majorRadius), -1 * (int)(this.y - Math.sin(this.theta)*this.majorRadius),
                     (int)(this.x + Math.cos(this.theta)*this.majorRadius), -1 * (int)(this.y + Math.sin(this.theta)*this.majorRadius));

        // short axis
        g2d.drawLine((int)(this.x - Math.cos(this.theta+Math.PI/2)*this.minorRadius), -1 * (int)(this.y - Math.sin(this.theta+Math.PI/2)*this.minorRadius),
                     (int)(this.x + Math.cos(this.theta+Math.PI/2)*this.minorRadius), -1 * (int)(this.y + Math.sin(this.theta+Math.PI/2)*this.minorRadius));
    }

    /*------------------------------------------------------------------------*/

    public void setColor(Color c) {
        this.color = c;
        this.determineColorContrast();
    }
    
    private void determineColorContrast() {
        this.colorContrast = Color.LIGHT_GRAY;
        if (((this.color.getRed() + this.color.getGreen() + this.color.getBlue()) / 3) > 128) {
            this.colorContrast = Color.DARK_GRAY;
        }
    }
}
