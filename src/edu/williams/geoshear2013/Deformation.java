/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.williams.geoshear2013;

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
     * create a Deformation from the upper left square of the given affine transform
     */
    public Deformation(AffineTransform t) {
        this(t.getScaleX(),t.getShearY(),t.getShearX(),t.getScaleY());
    }

    /**
     * create a Deformation from the given 2x2 matrix
     */
    public Deformation(Matrix2x2 basis) {
        this(basis.m00,basis.m01,basis.m10,basis.m11);
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
    
    public Deformation times(Deformation d) {
        return new Deformation(super.times(d));
    }
    
    public static Deformation createFromAngle(double angleRad) {
        return new Deformation(Math.cos(angleRad), -1 * Math.sin(angleRad), Math.sin(angleRad), Math.cos(angleRad));
    }

    public static Deformation createFromRF(double rf) {
        return new Deformation(rf*Math.pow(1/rf,.5), 0, 0, Math.pow(1/rf,.5));
    }
 
    public static Deformation createFromRfPhi(double rf, double phiDeg) {
        System.err.println("potentially some problems in Deformation.createFromRfPhi");
        double phiRad = Util.toRadians(phiDeg);
//        System.err.println("Deformation createFromRfPhi: "+rf+","+phiDeg+"["+phiRad+"])");
        Matrix2x2 scalingMatrix = new Matrix2x2(rf*Math.pow(1/rf,.5), 0, 0, Math.pow(1/rf,.5));
//        System.err.println("  scaling: "+scalingMatrix.toString());
        Matrix2x2 rotationMatrix = new Matrix2x2(Math.cos(phiRad), -1 * Math.sin(phiRad), Math.sin(phiRad), Math.cos(phiRad));
//        System.err.println("  rotation: "+scalingMatrix.toString());
//        
        Matrix2x2 res = scalingMatrix.times(rotationMatrix);
//        System.err.println("  combined: "+scalingMatrix.toString());
        
//        res.m01 *= -1;
//        res.m10 *= -1;

        return new Deformation(res);
//        
////        Deformation dRF = Deformation.createFromRF(rf);
////        Deformation dPhi = Deformation.createFromAngle(phiRad);
////        dRF.timesInPlace(dPhi);


//        GSPebble s = new GSPebble(rf*Math.pow(1/rf,.5), Math.pow(1/rf,.5), phiRad);
        
//        GSPebble s = new GSPebble(1,1);
//        Deformation d = new Deformation();
//        while (s.getMajorRadius()/s.getMinorRadius() < rf) {
//            d.m10 += -.0001;
//            s = new GSPebble(1,1);
//            s.deform(d);
//        }
//        System.err.println("on initial find rf theta is: "+s.getTheta());
//        if (phiDeg < Util.toDegrees(s.getTheta())) {
//            while (phiDeg < Util.toDegrees(s.getTheta())) {
//                d.m01 += .0001;
//                s = new GSPebble(1,1);
//                while (s.getMajorRadius()/s.getMinorRadius() < rf) {
//                    d.m10 += -.0001;
//                    s = new GSPebble(1,1);
//                    s.deform(d);
//                }
//                s.deform(d);
//            }
//        }
//        System.err.println("tmp strain key data is:"+s.keyDataAsString());
//        return d;
//
//        return new Deformation(s.getMatrix());
//        return dRF;
    }
    
//    public AffineTransform asAffineTransform() {
//        return new AffineTransform(this.m00, this.m10, this.m01, this.m11, 0, 0);
//    }
    
    /**
     * @return true if ththis matrix represents a simple rotational transformation
     */
    public boolean isRotational() {
        return (! this.isIdentity()) && this.m00==this.m11 && this.m01==(-1*this.m10);
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
    
    public double getRotAngleDegr() {
//        return Util.toDegrees(Math.acos(this.m00) * ((this.m01 > 0) ? -1 : 1));
        return Util.toDegrees(this.getRotAngleRad());
    }

    public double getRotAngleRad() {
        return Math.acos(this.m00) * ((this.m01 > 0) ? -1 : 1);
    }
    
    public void drawOnto(Graphics2D g2d) {
        this.drawOnto(g2d, Deformation.DEFORMATION_COLOR);
//        if (this.isRotational()) {
//            g2d.setColor(Deformation.DEFORMATION_COLOR);
//
//            // NOTE: using draw instead of fill so that we don't hide potentially important info near the origin
//            g2d.drawArc(-1*Deformation.DISPLAY_RADIUS, -1*Deformation.DISPLAY_RADIUS,2*Deformation.DISPLAY_RADIUS, 2*Deformation.DISPLAY_RADIUS, 0, (int)this.getRotAngleDegr());
//            
//            g2d.drawLine(0,0, Deformation.DISPLAY_RADIUS, 0);
//        } else {
//            GSPebble strain = new GSPebble(Deformation.DISPLAY_RADIUS, Deformation.DISPLAY_RADIUS);
//            strain.setColor(Deformation.DEFORMATION_COLOR);
//            strain.deform(this);
//            System.err.println("** drawing strain: "+strain.keyDataAsString());
//            strain.drawOnto(g2d, false, true);
//        }
    }

    public void drawOnto(Graphics2D g2d, Color c) {
        if (this.isRotational()) {
            g2d.setColor(c);

            // NOTE: using draw instead of fill so that we don't hide potentially important info near the origin
            g2d.drawArc(-1*Deformation.DISPLAY_RADIUS, -1*Deformation.DISPLAY_RADIUS,2*Deformation.DISPLAY_RADIUS, 2*Deformation.DISPLAY_RADIUS, 0, (int)this.getRotAngleDegr());
            
            g2d.drawLine(0,0, Deformation.DISPLAY_RADIUS, 0);
        } else {
            GSPebble strain = new GSPebble(Deformation.DISPLAY_RADIUS, Deformation.DISPLAY_RADIUS);
            strain.setColor(c);
            strain.deform(this);
//            System.err.println("** drawing strain: "+strain.keyDataAsString());
            strain.drawOnto(g2d, false, true, false); // strain always show axis, not filled, not in edit mode
        }
    }

    public Deformation inverted() {
        if (this.isIdentity()) { return this.clone(); }
        if (this.isScaling()) { return new Deformation(1/this.m00, this.m01, this.m10, 1/this.m11); }
        if (this.isShearing()) { return new Deformation(this.m00, this.m01*-1, this.m10*-1, this.m11); }
        if (this.isRotational()) { return new Deformation(this.m00, this.m01*-1, this.m10*-1, this.m11); }
        throw new UnsupportedOperationException("Not supporting inversions of non-basic deformations yet.");
    }
    
}
