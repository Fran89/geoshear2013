/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geoshear2013;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import javax.swing.JPanel;

/**
 *
 * @author cwarren
 */
class GSComplexUI extends JPanel {
//    private GSPoint center;

//    public GSPebble p;
    
    public GSComplex gsc;

    private GSEllipse tenativeDeformation;
    
    private AffineTransform displayTransform; // the pan and zoom controlled by the user
    
    private int currentUIMode;
    
    private static int UI_MODE_DEFORMS = 1;
    private static int UI_MODE_EDIT_PEBBLES = 2;
    
    private static double zoomPerScrollFactor = .025;

    private double lastMouseDownX;
    private double lastMouseDownY;

    private double lastMouseDragX;
    private double lastMouseDragY;
    /*------------------------------------------------------------------------*/

    public GSComplexUI(GSComplex gsc) {
        this.gsc = gsc;//.setCenter = new GSPoint(0,0);
        this.displayTransform = new AffineTransform();
        this.tenativeDeformation = new GSEllipse();
        this.currentUIMode = GSComplexUI.UI_MODE_DEFORMS;
    }

//    public void handleKeyDown() {
//        
//    }
//    public void handleKeyUp() {
//        
//    }

    public void handleMousePressed(java.awt.event.MouseEvent evt) {
        this.lastMouseDownX = evt.getPoint().x;
        this.lastMouseDownY = evt.getPoint().y;

        this.lastMouseDragX = evt.getPoint().x;
        this.lastMouseDragY = evt.getPoint().y;
    }  
    
    public void handleMouseDrag(java.awt.event.MouseEvent evt) {
//        System.err.println("mouse drag in gscomplex ui");
//        System.err.println("evt is: "+evt.toString());
        if (this.currentUIMode == GSComplexUI.UI_MODE_DEFORMS) {
            if (evt.isAltDown()) {
                System.err.println("to be implemented: UI_MODE_DEFORMS mouse drag with ALT down");
            }  else if (evt.isControlDown()) {
                System.err.println("to be implemented: UI_MODE_DEFORMS mouse drag with CTRL down");
            } else if (evt.isShiftDown()) {
                System.err.println("to be implemented: UI_MODE_DEFORMS mouse drag with SHIFT down");
            } else {
                this.displayTransform.translate((evt.getPoint().x - this.lastMouseDragX) * 1/this.displayTransform.getScaleX(),
                                                (evt.getPoint().y - this.lastMouseDragY) * 1/this.displayTransform.getScaleX());
            }
            this.repaint();
        }
        
        this.lastMouseDragX = evt.getPoint().x;
        this.lastMouseDragY = evt.getPoint().y;
    }
    
    public void handleMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
        System.err.println("mouse wheel move in gscomplex ui");
        System.err.println("evt is: "+evt.toString());
        
        double zoom_factor = 1- ((evt.getPreciseWheelRotation() * evt.getScrollAmount()) * GSComplexUI.zoomPerScrollFactor);
        
//        AffineTransform zoomTransform = new AffineTransform(zoom_factor, 0, 0, zoom_factor, this.gsc.getCenter().x, this.gsc.getCenter().y);

        this.displayTransform.scale(zoom_factor, zoom_factor);
        
        // this re-centers the display around the point at which the zoom occurs
        // TODO: this doesn't work quite right, but it's close enough for now...
        this.displayTransform.translate(-1 * (evt.getPoint().x - evt.getPoint().x / zoom_factor), 
                                        -1 * (evt.getPoint().y - evt.getPoint().y / zoom_factor));

        this.repaint();    }
            
    /*------------------------------------------------------------------------*/

    public void setCenter(double x, double y) {
        this.gsc.setCenter(x,y);
    }
    
    public void setPan(double x, double y) {
        // REFACTOR? any way to just update the translate vals of the display transform
        double[] matrixVals = new double[6];
        this.displayTransform.getMatrix(matrixVals);
        matrixVals[4] = x;
        matrixVals[5] = y;
        this.displayTransform = new AffineTransform(matrixVals);
    }
    public void setZoom(double scale) {
        // REFACTOR? any way to just update the translate vals of the display transform
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
//        g2d.translate(this.displayTransform.getTranslateX(),this.displayTransform.getTranslateY());

        // TODO: figure out how to get 1px wide axes (e.g. apply translation and scaling transforms separately, manually calc the additional scaling offset needed for the axes?
        // TODO: figure out how to draw the axes to the edge of the widow regarless of other factors (quick and dirty would be to set limits to extreme values - e.g. +/- 32000
  
        g2d.setColor (Color.BLACK);
        g2d.drawLine(0,(int)this.gsc.getCenter().y,this.getWidth(),(int)this.gsc.getCenter().y); // horizontal axis
        g2d.drawLine((int)this.gsc.getCenter().x,0,(int)this.gsc.getCenter().x,this.getHeight()); // vertical axis
                
//        g2d.scale(this.displayTransform.getScaleX(),this.displayTransform.getScaleX()); // NOTE: for display the scaling is the same in both dimensions

        g2d.translate(this.gsc.getCenter().x, this.gsc.getCenter().y);

        this.gsc.drawOnto(g2d, true, true);
    }
    
    
    
}
