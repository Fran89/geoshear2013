/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.williams.geoshear2013;

import java.awt.geom.Point2D;

/**
 *
 * @author cwarren
 */
public class GSPoint {
    public double x;
    public double y;

    public GSPoint() {
        this(0,0);
    }
    
    public GSPoint(double initx,double inity) {
        this.x = initx;
        this.y = inity;
    }

    public String serialize() {
        return this.x+","+this.y;
    }
    public static String serializeHeadersToTabDelimited() {
        return "X\tY";
    }
    public String serializeToTabDelimited() {
        return this.x+"\t"+this.y;
    }

    public static GSPoint deserialize(String serializedPoint) {
        serializedPoint = serializedPoint.trim();
        String[] xy = serializedPoint.split(",");
        xy[0] = xy[0].trim();
        xy[1] = xy[1].trim();
        try {
            return new GSPoint(Double.parseDouble(xy[0]), Double.parseDouble(xy[1]));
        } catch(Exception exc) {
            return new GSPoint();
        }
    }
    
    void set(double sx, double sy) {
        this.x = sx;
        this.y = sy;
    }
    
    public Point2D asPoint2D() {
        return new Point2D.Double(this.x, this.y);
    }
}
