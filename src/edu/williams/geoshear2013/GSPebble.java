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

    private final static String SERIAL_TOKEN = ";";

    public static int CENTER_MARK_RADIUS = 6;
    public static Color CENTER_MARK_COLOR = Color.DARK_GRAY;
    
    /*------------------------------------------------------------------------*/
    
    public GSPebbleSet ofSet=null; // the set that contains this pebble    
    private boolean selected=false; // whether or not this pebble is selected
    private String id="p";
    
    public Color color=Color.BLACK;
    public Color colorContrast=Color.LIGHT_GRAY;

    /*------------------------------------------------------------------------*/
    public GSPebble() {
        super(0,0,1,1,0);
    }
    
    public GSPebble(double major, double minor) {
        super(major, minor);
    }

    public GSPebble(double major, double minor, double thetaRad) {
        super(major, minor, thetaRad);
    }

    public GSPebble(double x, double y, double major, double minor, double thetaRad) {
        super(x, y, major, minor, thetaRad);
    }
    
    public GSPebble(double x, double y, double major, double minor, double thetaRad, Color color) {
        super(x, y, major, minor, thetaRad);
        this.setColor(color);
    }
    
    public GSPebble(String id, double x, double y, double major, double minor, double thetaRad, Color color) {
        super(x, y, major, minor, thetaRad);
        this.setId(id);
        this.setColor(color);
    }
    
    /*------------------------------------------------------------------------*/
    @Override
    public GSPebble clone() {
        GSPebble theClone = new GSPebble(this.x, this.y, this.majorRadius, this.minorRadius, this.thetaRad);
        theClone.setColor(this.color);
        theClone.setId(this.id);
        theClone.setSelected(this.selected);
        return theClone;
    }
    
    /*------------------------------------------------------------------------*/

    public void drawOnto(Graphics2D g2d, boolean isFilled, boolean showAxes, boolean inEditMode) {
//        System.err.println("---------");
        
        if (isFilled) {
            g2d.setColor(this.color);
            g2d.fill(this.shape);
            if (this.isSelected()) {
                g2d.setColor(GSPebble.SELECTION_COLOR);
                g2d.fillOval((int) this.x-GSPebble.SELECTION_RADIUS/2, -1*((int) this.y+GSPebble.SELECTION_RADIUS/2), GSPebble.SELECTION_RADIUS, GSPebble.SELECTION_RADIUS);
            }
            if (showAxes) {
                g2d.setColor(this.colorContrast);
                this.drawAxes(g2d);
            }
        } else {
            if (this.isSelected()) {
                g2d.setColor(GSPebble.SELECTION_COLOR);
                g2d.fillOval((int) this.x-GSPebble.SELECTION_RADIUS/2, -1*((int) this.y+GSPebble.SELECTION_RADIUS/2), GSPebble.SELECTION_RADIUS, GSPebble.SELECTION_RADIUS);
            }
            g2d.setColor(this.color);
            g2d.draw(this.shape);
            if (showAxes) {
                this.drawAxes(g2d);
            }
        }

        if (inEditMode) {
//            System.err.println("center point: "+this.x+","+this.y);
            g2d.setColor(GSPebble.CENTER_MARK_COLOR);
            g2d.fillOval((int) this.x-GSPebble.CENTER_MARK_RADIUS/2, -1*((int) this.y+GSPebble.CENTER_MARK_RADIUS/2), GSPebble.CENTER_MARK_RADIUS, GSPebble.CENTER_MARK_RADIUS);
//            g2d.drawString(""+this.x+","+this.y, (int)this.x, -1*(int)this.y);
        }

    }
    
    private void drawAxes(Graphics2D g2d) {
//        System.err.println("center of axes: "+this.x+","+this.y);
//        System.err.println("long A: "+(this.x - Math.cos(this.thetaRad)*this.majorRadius)+","+(-1 * (int)(this.y - Math.sin(this.thetaRad)*this.majorRadius)));
        
//        // short axis
//        g2d.drawLine((int)(this.x - Math.sin(this.thetaRad)*this.minorRadius), (int)(this.y - Math.cos(this.thetaRad)*this.minorRadius),
//                     (int)(this.x + Math.sin(this.thetaRad)*this.minorRadius), (int)(this.y + Math.cos(this.thetaRad)*this.minorRadius));
//
//        // long axis
//        g2d.drawLine((int)(this.x - Math.sin(this.thetaRad+Math.PI/2)*this.majorRadius), (int)(this.y - Math.cos(this.thetaRad+Math.PI/2)*this.majorRadius),
//                     (int)(this.x + Math.sin(this.thetaRad+Math.PI/2)*this.majorRadius), (int)(this.y + Math.cos(this.thetaRad+Math.PI/2)*this.majorRadius));

        // long axis
        g2d.drawLine((int)(this.x - Math.cos(this.thetaRad)*this.majorRadius), -1 * (int)(this.y - Math.sin(this.thetaRad)*this.majorRadius),
                     (int)(this.x + Math.cos(this.thetaRad)*this.majorRadius), -1 * (int)(this.y + Math.sin(this.thetaRad)*this.majorRadius));

        // short axis
        g2d.drawLine((int)(this.x - Math.cos(this.thetaRad+Math.PI/2)*this.minorRadius), -1 * (int)(this.y - Math.sin(this.thetaRad+Math.PI/2)*this.minorRadius),
                     (int)(this.x + Math.cos(this.thetaRad+Math.PI/2)*this.minorRadius), -1 * (int)(this.y + Math.sin(this.thetaRad+Math.PI/2)*this.minorRadius));
        


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

    /**
     * @return the selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * @param selected the selected to set
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }
}
