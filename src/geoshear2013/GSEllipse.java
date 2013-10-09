/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geoshear2013;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

/**
 *
 * @author cwarren
 */
public class GSEllipse {
    protected double x,y;
    protected double majorRadius,minorRadius;
    protected double theta;
    
    protected AffineTransform matrix; // the affine tranform matrix that converts the unit circle into this ellipse;
    
    public Shape shape;
    
    /*------------------------------------------------------------------------*/
    public GSEllipse() { // unit circle is the default
        this(0, 0, 1, 1, 0);
    }
    public GSEllipse(double majorRadius, double minorRadius) {
        this(0, 0, majorRadius, minorRadius, 0);
    }
    public GSEllipse(double majorRadius, double minorRadius, double theta) {
        this(0, 0, majorRadius, minorRadius, theta);
    }
    public GSEllipse(double x, double y, double majorRadius, double minorRadius, double theta) {
        this.x = x;
        this.y = y;
        this.majorRadius = majorRadius;
        this.minorRadius = minorRadius;
        this.theta = theta;
        
        this.setMatrixFromKeyData();
    }
    
    /*------------------------------------------------------------------------*/

    public GSEllipse getDeformedGSEllipse(AffineTransform deformation) {
        
        AffineTransform base = (AffineTransform)(this.getMatrix().clone());
        
        base.concatenate(deformation);
        
        System.err.println("TODO: implement SVD of an affine transform");
        
        double newMajorRadius = 0;
        double newMinorRadius = 0;
        double newTheta = 0;
        
        GSEllipse deformedEllipse = new GSEllipse(base.getTranslateX(), base.getTranslateY(), newMajorRadius, newMinorRadius, newTheta);

        return deformedEllipse;
    }
    
    /*------------------------------------------------------------------------*/

    /**
     * set the matrix that defines this ellipse from its center (x and y), axes (majorRadius and minorRadius) and rotation (theta)
     */
    public final void setMatrixFromKeyData() {        
        this.matrix = new AffineTransform();
//        this.matrix = T*R*S
        this.matrix.concatenate(AffineTransform.getTranslateInstance(this.x, this.y*-1));
        this.matrix.concatenate(AffineTransform.getRotateInstance(this.theta*-1));
        this.matrix.concatenate(AffineTransform.getScaleInstance(this.majorRadius, this.minorRadius));

        //System.err.println(this.matrix);
        
        this.setShape();
    }
    
    public void setKeyDataFromMatrix() {
        System.err.println("TODO: implement setKeyDataFromMatrix in GSEllipse");
    }

    /**
     * set the shape object for this ellipse from its internal data
     */
    public void setShape() {
        this.shape = matrix.createTransformedShape(new Ellipse2D.Double(-1,-1,2,2)); // transform the unit circle
    }
    
    /**
     * set the center (x and y) of this ellipse relative to a new origin that's given in the coordinate of the original
     */
    public void resetPositionRelativeToNewOrigin(double x, double y) {
        this.x = this.x - x;
        this.y = this.y - y;
        this.setMatrixFromKeyData();
    }
    
    public void applyEllipseAsTransform(GSEllipse e) {
        System.err.println("TODO: implement setKeyDataFromMatrix in GSEllipse");
    }
            
    /*------------------------------------------------------------------------*/

    /**
     * @return the x
     */
    public double getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(double x) {
        this.x = x;
        this.setMatrixFromKeyData();
    }

    /**
     * @return the y
     */
    public double getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(double y) {
        this.y = y;
        this.setMatrixFromKeyData();
    }

    /**
     * @return the majorRadius
     */
    public double getMajorRadius() {
        return majorRadius;
    }

    /**
     * @param majorRadius the majorRadius to set
     */
    public void setMajorRadius(double majorRadius) {
        this.majorRadius = majorRadius;
        this.setMatrixFromKeyData();
    }

    /**
     * @return the minorRadius
     */
    public double getMinorRadius() {
        return minorRadius;
    }

    /**
     * @param minorRadius the minorRadius to set
     */
    public void setMinorRadius(double minorRadius) {
        this.minorRadius = minorRadius;
        this.setMatrixFromKeyData();
    }

    /**
     * NOTE: theta is internally inverted to implement counter-clockwise rotation
     * @return the theta
     */
    public double getTheta() {
        return theta;
    }

    /**
     * NOTE: theta is internally inverted to implement counter-clockwise rotation
     * @param theta the theta to set
     */
    public void setTheta(double theta) {
        this.theta = theta;
        this.setMatrixFromKeyData();
    }
    
    /**
     * @return the affine transform that converts the unit circle to this ellipse
     */
    public AffineTransform getMatrix() {
        return this.matrix;
    }

}
