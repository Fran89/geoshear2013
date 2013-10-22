/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geoshear2013;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.ListIterator;

/**
 *
 * @author cwarren
 */
public class GSComplex implements Watchable {
    public GSPebbleSetSeries pebbleSets;
    public GSDeformationSeries deformations;
    
    /**
     * the center of this complex relative to 0,0 as the upper left
     */
    private GSPoint center;

    /*------------------------------------------------------------------------*/
    public GSComplex() {
        this.pebbleSets = new GSPebbleSetSeries();
        this.pebbleSets.add(new GSPebbleSet(this));
        this.deformations = new GSDeformationSeries();
        this.center = new GSPoint(0,0);
    }
    
    /*------------------------------------------------------------------------*/

    public void drawOnto(Graphics2D g2d, boolean isFilled, boolean showAxes, Deformation tenativeDeformation) {
        if (tenativeDeformation.isIdentity()) {
            for (int i=0; i<this.pebbleSets.getLast().size(); i++) {
                this.pebbleSets.getLast().get(i).drawOnto(g2d, isFilled, showAxes);
            }
        } else {
            GSPebbleSet tenativelyDeformedPebbles = this.pebbleSets.getLast().clone();
            tenativelyDeformedPebbles.applyDeformation(tenativeDeformation);
            for (int i=0; i<tenativelyDeformedPebbles.size(); i++) {
                tenativelyDeformedPebbles.get(i).drawOnto(g2d, isFilled, showAxes);
                //tenativelyDeformedPebbles.get(i).errDump();
            }
            
//            GSPebble strain = new GSPebble(100, 100);
//            strain.setColor(Color.red);
//            if (tenativeDeformation.isRotational()) {
//                g2d.setColor(strain.color);
//                double rotDegr = (180/Math.PI) * Math.acos(tenativeDeformation.m00) * ((tenativeDeformation.m01 > 0) ? -1 : 1);
////                System.err.println("strain rot: "+rotDegr);
//                g2d.fillArc(-100, -100, 200, 200, 0, (int)rotDegr);
//            } else {
//                strain.deform(tenativeDeformation);
//                strain.drawOnto(g2d, false, true);
//            }
        }
    }
    
    /*------------------------------------------------------------------------*/

    /*
     * implementing the Watchable interface
     */
    private ArrayList watchedBy = new ArrayList();

    @Override
    public void addWatcher(Watcher w) {
        watchedBy.add(w);
        //w.setWatched(this);
        w.reactTo(this, null);
    }

    @Override
    public void removeWatcher(Watcher w) {
        watchedBy.remove(w);
    }

    @Override
    public void removeAllWatchers() {
        watchedBy.clear();
    }

    @Override
    public void notifyWatchers() {
        this.notifyWatchers(null);
    }

    @Override
    public void notifyWatchers(Object arg) {
        ListIterator li = watchedBy.listIterator();
        while (li.hasNext())
        {
            Watcher wr = (Watcher)(li.next());
            wr.reactTo(this,arg);
        }
    }

    /*------------------------------------------------------------------------*/

    /**
     * @return the center
     */
    public GSPoint getCenter() {
        return this.center;
    }

    /**
     * @param center the center to set
     */
    public void setCenter(GSPoint center) {
        this.resetPositionOfPebblesRelativeToNewOrigin(this.center.x, this.center.y, center.x, center.y);
        this.center.x = center.x;
        this.center.y = center.y;
    }
    
    /**
     * @param x, y the coordinates to which the center should be set
     */
    public void setCenter(double x, double y) {
        this.resetPositionOfPebblesRelativeToNewOrigin(this.center.x, this.center.y, x, y);
        this.center.x = x;
        this.center.y = y;
    }
    
    private void resetPositionOfPebblesRelativeToNewOrigin(double initial_origin_x, double initial_origin_y, double new_origin_x, double new_origin_y) {
//        System.err.println("orig origin: "+initial_origin_x+","+initial_origin_y);
//        System.err.println("new origin: "+new_origin_x+","+new_origin_y);
        double origin_shift_x = new_origin_x - initial_origin_x;
        double origin_shift_y = initial_origin_y - new_origin_y;
//        System.err.println("re-centering shift: "+origin_shift_x+","+origin_shift_y);
        for (int i_sets=0; i_sets<this.pebbleSets.size(); i_sets++) {
            GSPebbleSet pebbles = this.pebbleSets.get(i_sets);
            for (int i_pebbles=0; i_pebbles<pebbles.size(); i_pebbles++) {
                pebbles.get(i_pebbles).shiftPosition(origin_shift_x,origin_shift_y);
            }
        }
    }
}
