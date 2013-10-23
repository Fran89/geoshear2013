/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.williams.geoshear2013;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

/**
 *
 * @author cwarren
 */
class GSComplexUI extends JPanel {
//    private GSPoint center;

//    public GSPebble p;
    
    public GSComplex gsc;

    private Deformation tenativeDeformation;
    
    private AffineTransform displayTransform; // the pan and zoom controlled by the user
    
    private int currentUIMode;
    
    private static int UI_MODE_DEFORMS = 1;
    private static int UI_MODE_EDIT_PEBBLES = 2;
    
    private static double zoomPerScrollFactor = .025;

    private static BasicStroke INFO_STROKE = new BasicStroke(3,BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0,new float[] { 1, 5 }, 0);
    
    private double lastMouseDownX;
    private double lastMouseDownY;
    private Point2D lastMouseDownPoint;
    private Point2D lastMouseDownPointInGSCSystem;
    private double lastMouseDownAngleInGSCSystem;

    private double lastMouseDragX;
    private double lastMouseDragY;
    private Point2D lastMouseDragPoint;
    private Point2D lastMouseDragPointInGSCSystem;
    private double lastMouseDragAngleInGSCSystem;
        
    private boolean shiftIsDown = false;
    private boolean ctrlIsDown = false;
    private boolean altIsDown = false;
    
    /*------------------------------------------------------------------------*/

    public GSComplexUI(GSComplex gsc) {
        this.gsc = gsc;//.setCenter = new GSPoint(0,0);
        this.displayTransform = new AffineTransform();
        this.tenativeDeformation = new Deformation();
        this.currentUIMode = GSComplexUI.UI_MODE_DEFORMS;
    }

    public void handleKeyPressed(java.awt.event.KeyEvent evt) {
        this.altIsDown = evt.isAltDown();
        this.ctrlIsDown = evt.isControlDown();
        this.shiftIsDown = evt.isShiftDown();
    }
    public void handleKeyReleased(java.awt.event.KeyEvent evt) {
        this.altIsDown = evt.isAltDown();
        this.ctrlIsDown = evt.isControlDown();
        this.shiftIsDown = evt.isShiftDown();
    }

    public Point2D inGSCSystem(Point2D panelClickP) {
        Point2D pointOfGSCOrigin = this.gsc.getCenter().asPoint2D();
        this.displayTransform.transform(pointOfGSCOrigin, pointOfGSCOrigin);
//        System.out.println("inGSCSystem: pointOfGSCOrigin point is : "+pointOfGSCOrigin.toString());
            
        return new Point2D.Double((panelClickP.getX()-pointOfGSCOrigin.getX())/this.displayTransform.getScaleX(),
                                  (pointOfGSCOrigin.getY()-panelClickP.getY())/this.displayTransform.getScaleX());
    }
    
    public void handleMousePressed(java.awt.event.MouseEvent evt) {
//        System.out.println("click point is : "+evt.getPoint().toString());
        this.lastMouseDownX = evt.getPoint().x;
        this.lastMouseDownY = evt.getPoint().y;
        this.lastMouseDownPoint = (Point2D) evt.getPoint().clone();
        this.lastMouseDownPointInGSCSystem = this.inGSCSystem(evt.getPoint());
        this.lastMouseDownAngleInGSCSystem = Math.atan(this.lastMouseDownPointInGSCSystem.getY()/this.lastMouseDownPointInGSCSystem.getX());
        if (this.lastMouseDownPointInGSCSystem.getX() < 0) {
            if (this.lastMouseDownPointInGSCSystem.getY() > 0) {
                this.lastMouseDownAngleInGSCSystem += Math.PI;
            } else {
                this.lastMouseDownAngleInGSCSystem -= Math.PI;
            }
        }

        this.lastMouseDragX = evt.getPoint().x;
        this.lastMouseDragY = evt.getPoint().y;
        this.lastMouseDragPoint = (Point2D) evt.getPoint().clone();
        this.lastMouseDragAngleInGSCSystem = this.lastMouseDownAngleInGSCSystem;
        this.lastMouseDragPointInGSCSystem = (Point2D) this.lastMouseDownPointInGSCSystem.clone();
    
        this.altIsDown = evt.isAltDown();
        this.ctrlIsDown = evt.isControlDown();
        this.shiftIsDown = evt.isShiftDown();

        if (this.altIsDown || this.ctrlIsDown || this.shiftIsDown) {
            this.tenativeDeformation = new Deformation();
        }
    }  

    public void handleMouseReleased(java.awt.event.MouseEvent evt) {
        //this.tenativeDeformation = new Matrix2x2();
        this.altIsDown = evt.isAltDown();
        this.ctrlIsDown = evt.isControlDown();
        this.shiftIsDown = evt.isShiftDown();
        this.repaint();
    } 
    
    public void handleMouseDrag(java.awt.event.MouseEvent evt) {
        Point2D evtPointInGSCSystem = this.inGSCSystem(evt.getPoint()); 
        double deltaX = evt.getX() - this.lastMouseDownX;
        double deltaY = this.lastMouseDownY - evt.getY();
        if (this.currentUIMode == GSComplexUI.UI_MODE_DEFORMS) {
            if (evt.isAltDown()) {
                double angleDiff = this.lastMouseDragAngleInGSCSystem - this.lastMouseDownAngleInGSCSystem;    
                this.tenativeDeformation = new Deformation(Math.cos(angleDiff), -1 * Math.sin(angleDiff), Math.sin(angleDiff), Math.cos(angleDiff));
            }  else if (evt.isControlDown()) {
                double xCompress = evtPointInGSCSystem.getX()/this.lastMouseDownPointInGSCSystem.getX();
                double yCompress = evtPointInGSCSystem.getY()/this.lastMouseDownPointInGSCSystem.getY();
                if (xCompress < .01) { xCompress = .01; }
                if (yCompress < .01) { yCompress = .01; }
                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    this.tenativeDeformation = new Deformation(xCompress, 0, 0, 1/xCompress);
                } else {
                    this.tenativeDeformation = new Deformation(1/yCompress, 0, 0, yCompress);
                }
            } else if (evt.isShiftDown()) {
                double xShear = deltaX/this.lastMouseDragPointInGSCSystem.getY();
                double yShear = deltaY/this.lastMouseDragPointInGSCSystem.getX();
                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    this.tenativeDeformation = new Deformation(1, 0, xShear*-1, 1);
                } else {
                    this.tenativeDeformation = new Deformation(1, yShear*-1, 0, 1);
                }
            } else {
                this.displayTransform.translate((evt.getPoint().x - this.lastMouseDragX) * 1/this.displayTransform.getScaleX(),
                                                (evt.getPoint().y - this.lastMouseDragY) * 1/this.displayTransform.getScaleX());
            }
            this.repaint();
        }

        this.lastMouseDragX = evt.getPoint().x;
        this.lastMouseDragY = evt.getPoint().y;
        this.lastMouseDragPoint = (Point2D) evt.getPoint().clone();
        this.lastMouseDragPointInGSCSystem = (Point2D) evtPointInGSCSystem.clone();
        this.lastMouseDragAngleInGSCSystem = Math.atan(evtPointInGSCSystem.getY()/evtPointInGSCSystem.getX());
        if (evtPointInGSCSystem.getX() < 0) {
            if (evtPointInGSCSystem.getY() > 0) {
                this.lastMouseDragAngleInGSCSystem += Math.PI;
            } else {
                this.lastMouseDragAngleInGSCSystem -= Math.PI;
            }
        }
    }
    
    public void handleMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
//        System.err.println("mouse wheel move in gscomplex ui");
//        System.err.println("evt is: "+evt.toString());
        
        double zoom_factor = 1- ((evt.getPreciseWheelRotation() * evt.getScrollAmount()) * GSComplexUI.zoomPerScrollFactor);
        
//        AffineTransform zoomTransform = new AffineTransform(zoom_factor, 0, 0, zoom_factor, this.gsc.getCenter().x, this.gsc.getCenter().y);

        this.displayTransform.scale(zoom_factor, zoom_factor);
        
        // this re-centers the display around the point at which the zoom occurs
        // TODO: this doesn't work quite right, but it's close enough for now...
        this.displayTransform.translate(-1 * (evt.getPoint().x - evt.getPoint().x / zoom_factor), 
                                        -1 * (evt.getPoint().y - evt.getPoint().y / zoom_factor));

        this.repaint();
    }
            
    /*------------------------------------------------------------------------*/

    public void setCenter(double x, double y) {
        this.gsc.setCenter(x,y);
    }
    
    public void setPan(double x, double y) {
        // REFACTOR? a way to just update the translate vals of the display transform
        double[] matrixVals = new double[6];
        this.displayTransform.getMatrix(matrixVals);
        matrixVals[4] = x;
        matrixVals[5] = y;
        this.displayTransform = new AffineTransform(matrixVals);
    }
    public void setZoom(double scale) {
        // REFACTOR? a way to just update the translate vals of the display transform
        double[] matrixVals = new double[6];
        this.displayTransform.getMatrix(matrixVals);
        matrixVals[0] = scale;
        matrixVals[3] = scale;
        this.displayTransform = new AffineTransform(matrixVals);
    }
    public void resetDisplayTransform() {
        this.displayTransform = new AffineTransform();
    }
    
    @Override
    public void paintComponent (Graphics g)
    {
        super.paintComponent (g);
        Graphics2D g2d = (Graphics2D) g;
        
        g2d.setColor (Color.WHITE);
        g2d.fillRect (0,0, this.getWidth (), this.getHeight ());

        
        g2d.transform(this.displayTransform);
 
        g2d.setColor (Color.BLACK);
        g2d.drawLine(0,(int)this.gsc.getCenter().y,this.getWidth(),(int)this.gsc.getCenter().y); // horizontal axis
        g2d.drawLine((int)this.gsc.getCenter().x,0,(int)this.gsc.getCenter().x,this.getHeight()); // vertical axis
        // TODO: figure out how to get 1px wide axes (e.g. apply translation and scaling transforms separately, manually calc the additional scaling offset needed for the axes?
        // TODO: figure out how to draw the axes to the edge of the widow regarless of other factors (quick and dirty would be to set limits to extreme values - e.g. +/- 32000
                
        g2d.translate(this.gsc.getCenter().x, this.gsc.getCenter().y);
        
        this.gsc.drawOnto(g2d, false, true, this.tenativeDeformation);
        
        // This section draws hints/signifiers that show the drag action origin and current state re: the deformation
        if (this.currentUIMode == GSComplexUI.UI_MODE_DEFORMS && ! this.tenativeDeformation.isIdentity()) {
            g2d.setStroke(GSComplexUI.INFO_STROKE);
            this.tenativeDeformation.drawOnto(g2d);
        
            g2d.translate(this.gsc.getCenter().x*-1, this.gsc.getCenter().y*-1);
            g2d.setColor (Color.BLUE);
            if (this.altIsDown) {
                double rad = this.lastMouseDownPointInGSCSystem.distance(0,0);
                double angleDiff = this.lastMouseDragAngleInGSCSystem - this.lastMouseDownAngleInGSCSystem;
                double angleDiffDeg = (angleDiff)*180/Math.PI;
                if (angleDiffDeg > 180) {
                    angleDiffDeg -= 360;
                } else if (angleDiffDeg < -180) {
                    angleDiffDeg += 360;
                }
                g2d.drawArc((int)(this.gsc.getCenter().x - rad), (int)(this.gsc.getCenter().y - rad), (int) (2*rad), (int) (2*rad), 
                            (int) (this.lastMouseDownAngleInGSCSystem*180/Math.PI), (int)angleDiffDeg);
            }  else if (this.ctrlIsDown || this.shiftIsDown) {
//                System.err.println("TODO: paint UI_MODE_DEFORMS with CTRL down (compress)");
                Point2D invertedLastDown = (Point2D) this.lastMouseDownPoint.clone();
                Point2D invertedLastDrag = (Point2D) this.lastMouseDragPoint.clone();
                try {
                    this.displayTransform.inverseTransform(invertedLastDown, invertedLastDown);
                    this.displayTransform.inverseTransform(invertedLastDrag, invertedLastDrag);
                } catch (NoninvertibleTransformException noninvertibleTransformException) {
                }
                if (Math.abs(this.lastMouseDragX-this.lastMouseDownX) >= Math.abs(this.lastMouseDragY-this.lastMouseDownY)) {
                    g2d.drawLine((int) invertedLastDown.getX(), (int) invertedLastDown.getY(), (int) invertedLastDrag.getX(), (int) invertedLastDown.getY());
                } else {
                    g2d.drawLine((int) invertedLastDown.getX(), (int) invertedLastDown.getY(), (int) invertedLastDown.getX(), (int) invertedLastDrag.getY());
                }
            }

        }
    }
    
    
    
}