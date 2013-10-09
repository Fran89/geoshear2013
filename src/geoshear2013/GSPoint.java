/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geoshear2013;

/**
 *
 * @author cwarren
 */
class GSPoint {
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
}
