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

    public static String SERIALIZE_TOKEN = ";";
    
    public static String SERIALIZE_LABEL_M00 = "m00";
    public static String SERIALIZE_LABEL_M01 = "m01";
    public static String SERIALIZE_LABEL_M10 = "m10";
    public static String SERIALIZE_LABEL_M11 = "m11";
    
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
    
    /*--------------------------------------------------------------------*/
    @Override
    public Deformation clone() {
        return new Deformation(this.m00,this.m01,this.m10,this.m11);
    }
    
    /*--------------------------------------------------------------------*/

    public String serialize() {
        return Deformation.SERIALIZE_LABEL_M00+"="+this.m00+Deformation.SERIALIZE_TOKEN+
                " "+Deformation.SERIALIZE_LABEL_M01+"="+this.m01+Deformation.SERIALIZE_TOKEN+
                " "+Deformation.SERIALIZE_LABEL_M10+"="+this.m10+Deformation.SERIALIZE_TOKEN+
                " "+Deformation.SERIALIZE_LABEL_M11+"="+this.m11;
    }
    
    static String serializeHeadersToTabDelimited() {
        return Deformation.SERIALIZE_LABEL_M00+"\t"+Deformation.SERIALIZE_LABEL_M01+"\t"+Deformation.SERIALIZE_LABEL_M10+"\t"+Deformation.SERIALIZE_LABEL_M11;
    }

    public String serializeToTabDelimited() {
        return this.m00+"\t"+
               this.m01+"\t"+
               this.m10+"\t"+
               this.m11;   
    }

    public static Deformation deserialize(String serializedDeformation) {
        double newM00 = 1;
        double newM01 = 0;
        double newM10 = 0;
        double newM11 = 1;

        if (serializedDeformation.indexOf("\t") > -1) {
            String[] deformationDataPieces = serializedDeformation.split("\t");
            newM00 = Double.parseDouble(deformationDataPieces[0]);
            newM01 = Double.parseDouble(deformationDataPieces[1]);
            newM10 = Double.parseDouble(deformationDataPieces[2]);
            newM11 = Double.parseDouble(deformationDataPieces[3]);
        } else {
            String deformationData = serializedDeformation.replaceAll("\\s+", "");
            String[] deformationDataPieces = deformationData.split(Deformation.SERIALIZE_TOKEN);
            for (int i=0; i<deformationDataPieces.length; i++) {
                String[] keyValue = deformationDataPieces[i].split("=");             
                if ("m00".equals(keyValue[0])) { newM00 = Double.parseDouble(keyValue[1]); }
                if ("m01".equals(keyValue[0])) { newM01 = Double.parseDouble(keyValue[1]); }
                if ("m10".equals(keyValue[0])) { newM10 = Double.parseDouble(keyValue[1]); }
                if ("m11".equals(keyValue[0])) { newM11 = Double.parseDouble(keyValue[1]); }
            }
        }
        
        return new Deformation(newM00,newM01,newM10,newM11);
    }
    /*--------------------------------------------------------------------*/

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
        Matrix2x2 scalingMatrix = new Matrix2x2(rf*Math.pow(1/rf,.5), 0, 0, Math.pow(1/rf,.5));
        Matrix2x2 rotationMatrix = new Matrix2x2(Math.cos(phiRad), -1 * Math.sin(phiRad), Math.sin(phiRad), Math.cos(phiRad));
        Matrix2x2 res = scalingMatrix.times(rotationMatrix);
        return new Deformation(res);
    }
    
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
        return Util.toDegrees(this.getRotAngleRad());
    }

    public double getRotAngleRad() {
        return Math.acos(this.m00) * ((this.m01 > 0) ? -1 : 1);
    }
    
    public void drawOnto(Graphics2D g2d) {
        this.drawOnto(g2d, Deformation.DEFORMATION_COLOR);
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
