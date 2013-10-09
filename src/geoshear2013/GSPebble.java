/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geoshear2013;

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
    
    /*------------------------------------------------------------------------*/

    public void drawOnto(Graphics2D g2d, boolean isFilled, boolean showAxes) {
        g2d.setColor(this.color);
        Color axisColor = this.color;

        if (isFilled) {
            g2d.fill(this.shape);
        } else {
            g2d.draw(this.shape);
        }
    }
    
    /*------------------------------------------------------------------------*/

    public void setColor(Color c) {
        this.color = c;
        this.setColorContrast();
    }
    
    private void setColorContrast() {
        this.colorContrast = Color.LIGHT_GRAY;
        if (((this.color.getRed() + this.color.getGreen() + this.color.getBlue()) / 3) > 128) {
            this.colorContrast = Color.DARK_GRAY;
        }
    }
}
