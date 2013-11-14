/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.williams.geoshear2013;

import java.awt.Color;
import java.util.List;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.ListIterator;

/**
 *
 * @author cwarren
 */
public class GSPebbleSetSeries extends ArrayList {
    
    public GSPebbleSetSeries() {
        super();
    }

    public GSPebbleSetSeries(int initialCapacity) {
        super(initialCapacity);
    }


    /*---------------------------------------------------------------------*/
    public GSPebbleSet getLast() {
        return (GSPebbleSet)(super.get(this.size()-1));
    }

    public void selectPebblesByUndeformedPoint(Point2D p, boolean shiftIsDown) {
        GSPebbleSet baseSet = this.get(0);
        List hitPebbleIds = baseSet.getIdsOfPebblesHitByPoint(p);
//        System.out.println("hit pebble ids length: "+hitPebbleIds.toArray().length);
        this.selectPebblesByIds(hitPebbleIds, shiftIsDown);
    }
    
    public void selectPebblesByIds(List ids, boolean shiftIsDown) {
        ListIterator li = this.listIterator();
        while (li.hasNext()) {
            GSPebbleSet pebSet = (GSPebbleSet)(li.next());
            pebSet.selectPebblesByIds(ids, shiftIsDown);
        }
    }
    
     public void colorSelectedPebbles(Color c) {
        ListIterator li = this.listIterator();
        while (li.hasNext()) {
            GSPebbleSet pebSet = (GSPebbleSet)(li.next());
            pebSet.colorSelectedPebbles(c);
        }    
    }
    
    /*---------------------------------------------------------------------*/

    @Override
    public GSPebbleSet get(int index) {
        return (GSPebbleSet)(super.get(index));
    }

    /**
     * Adds the given pebble to this set, making sure the pebble knows what set it's in
     * @param p
     * @return
     */
    public boolean add(GSPebbleSet ps) {
        boolean res = super.add(ps);
        return res;
    }
    
    public void truncateFrom(int i) {
        this.removeRange(i, this.size());
    }
}
