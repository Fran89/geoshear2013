/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.williams.geoshear2013;

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

    private int currentDeformationNumber = 1;

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

        /**
     * @return the currentDeformationIndex
     */
    public int getCurrentDeformationNumber() {
        return currentDeformationNumber;
    }
    
    public void nextDeformation() {
        if (this.currentDeformationNumber <= this.deformations.size()) {
            this.currentDeformationNumber++;
        }
        System.out.println("TO BE FINISHED/IMPLEMENTED: gsc.nextDeformation");
    }
    
    public void prevDeformation() {
        if (this.currentDeformationNumber > 1) {
            this.currentDeformationNumber--;
        }
        System.out.println("TO BE FINISHED/IMPLEMENTED: gsc.prevDeformation");
    }
    
    public void lastDeformation() {
        this.currentDeformationNumber = this.deformations.size() + 1;
        System.out.println("TO BE FINISHED/IMPLEMENTED: gsc.lastDeformation");
    }

    public Deformation getCompositeTransform() {
        return this.deformations.getCompositeTransform(this.currentDeformationNumber-1);
    }
    
    /*------------------------------------------------------------------------*/
    
    public void drawOnto(Graphics2D g2d, boolean isFilled, boolean showAxes, Deformation tenativeDeformation) {
//        GSPebbleSet workingPebbleSet = this.pebbleSets.getLast();
//        if (this.currentDeformationNumber < this.deformations.size()) {
//            workingPebbleSet = this.pebbleSets.get(this.currentDeformationNumber-1);
//        } 
        GSPebbleSet workingPebbleSet = this.pebbleSets.get(this.currentDeformationNumber-1);
        if (! tenativeDeformation.isIdentity()) {
            workingPebbleSet = this.pebbleSets.getLast().clone();
            workingPebbleSet.applyDeformation(tenativeDeformation);
        }
        
        for (int i=0; i<workingPebbleSet.size(); i++) {
            workingPebbleSet.get(i).drawOnto(g2d, isFilled, showAxes);
            //tenativelyDeformedPebbles.get(i).errDump();
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

    void applyDeformation(Deformation d) {
        this.deformations.add(d);
        GSPebbleSet newPebbleSet = this.pebbleSets.get(0).clone();
        ListIterator li = this.deformations.listIterator();
        while (li.hasNext()) {
//            this.compositeDeformation.timesInPlace(((Deformation)(li.next())));
            newPebbleSet.applyDeformation((Deformation)(li.next()));
        }
        this.pebbleSets.add(newPebbleSet);
        this.lastDeformation();
    }
}
