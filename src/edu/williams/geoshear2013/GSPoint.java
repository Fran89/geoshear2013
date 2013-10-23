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

    void set(double sx, double sy) {
        this.x = sx;
        this.y = sy;
    }
    
    public Point2D asPoint2D() {
        return new Point2D.Double(this.x, this.y);
    }
}
