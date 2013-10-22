/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geoshear2013;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

/**
 *
 * @author cwarren
 */
public class Deformation extends Matrix2x2 {

    public static Color DEFORMATION_COLOR = Color.RED;
    public static int DISPLAY_RADIUS = 100;    
    /**
     * create a 2x2 identity matrix
     */
    public Deformation() {
        this(1,0,0,1);
    }
        
    /**
     * create a 2x2 matrix from the upper left square of the given affine transform
     */
    public Deformation(AffineTransform t) {
        this(t.getScaleX(),t.getShearY(),t.getShearX(),t.getScaleY());
    }
    
    /**
     * create a new 2x2 matrix of the form 
     *   m00 m01
     *   m10 m11
     * 
     * @param m00
     * @param m01
     * @param m10
     * @param m11 
     */
    public Deformation(double m00, double m01, double m10, double m11) {
        this.m00 = m00;
        this.m01 = m01;
        this.m10 = m10;
        this.m11 = m11;
    }
    
    @Override
    public Deformation clone() {
        return new Deformation(this.m00,this.m01,this.m10,this.m11);
    }
    
    
    
    /**
     * @return true if ththis matrix represents a simple rotational transformation
     */
    public boolean isRotational() {
        return this.m00==this.m11 && this.m01==(-1*this.m10);
    }

    /**
     * @return true if this matrix represents a simple scaling transformation
     */
    public boolean isScaling() {
        return this.m01==0 && this.m10==0;
    }

    /**
     * @return true if this matrix represents a simple shearing transformation
     */
    public boolean isShearing() {
        return this.m00==1 && this.m11==1 && ( this.m01!=0 || this.m10!=0);
    }
    
    public void drawOnto(Graphics2D g2d) {
        if (this.isRotational()) {
            g2d.setColor(Deformation.DEFORMATION_COLOR);
            double rotDegr = (180/Math.PI) * Math.acos(this.m00) * ((this.m01 > 0) ? -1 : 1);
            // NOTE: using draw instead of fill so that we don't hide potentially important info near the origin
            g2d.drawArc(-1*Deformation.DISPLAY_RADIUS, -1*Deformation.DISPLAY_RADIUS,2*Deformation.DISPLAY_RADIUS, 2*Deformation.DISPLAY_RADIUS, 0, (int)rotDegr);
        } else {
            GSPebble strain = new GSPebble(Deformation.DISPLAY_RADIUS, Deformation.DISPLAY_RADIUS);
            strain.setColor(Deformation.DEFORMATION_COLOR);
            strain.deform(this);
            strain.drawOnto(g2d, false, true);
        }
    }
    
}
