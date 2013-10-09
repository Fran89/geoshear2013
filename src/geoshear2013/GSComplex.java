/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geoshear2013;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.ListIterator;

/**
 *
 * @author cwarren
 */
public class GSComplex implements Watchable {
    public GSPebbleSet pebbles;
    public GSEllipseSeries deformations;
    
    /**
     * the center of this complex relative to 0,0 as the upper left
     */
    private GSPoint center;

    /*------------------------------------------------------------------------*/
    public GSComplex() {
        this.pebbles = new GSPebbleSet(this);
        this.deformations = new GSEllipseSeries();
        this.center = new GSPoint(0,0);
    }
    
    /*------------------------------------------------------------------------*/

    public void drawOnto(Graphics2D g2d, boolean isFilled, boolean showAxes) {
       for (int i=0; i<this.pebbles.size(); i++) {
           this.pebbles.get(i).drawOnto(g2d, isFilled, showAxes);
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
        this.center = center;
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
       for (int i=0; i<this.pebbles.size(); i++) {
           this.pebbles.get(i).resetPositionRelativeToNewOrigin(new_origin_x-initial_origin_x,new_origin_y-initial_origin_y);
       }
    }
}
