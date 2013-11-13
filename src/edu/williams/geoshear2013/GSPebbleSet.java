/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.williams.geoshear2013;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.List;

/**
 *
 * @author cwarren
 */
public class GSPebbleSet extends ArrayList {
    protected double harmonicMean = 0;
    protected double vectorMean = 0;

    public GSComplex ofComplex;

    private final static String SERIAL_TOKEN = "\n%";

    //private ArrayList boundSets;

    public GSPebbleSet(GSComplex gsc) {
        super();
       // this.boundSets = new ArrayList(4);
        this.ofComplex = gsc;
    }

    public GSPebbleSet(int initialCapacity) {
        super(initialCapacity);
       // this.boundSets = new ArrayList(4);
    }

    /*---------------------------------------------------------------------*/
    @Override
    public GSPebbleSet clone() {
        GSPebbleSet theClone = new GSPebbleSet(this.ofComplex);
        
        ListIterator li = this.listIterator();
        while (li.hasNext()) {
            theClone.add( ((GSPebble)(li.next())).clone() );
        }
        theClone.harmonicMean = this.harmonicMean;
        theClone.vectorMean = this.vectorMean;
        
        return theClone;
    }
    
    /*---------------------------------------------------------------------*/
    
    @Override
    public GSPebble get(int index) {
        return (GSPebble)(super.get(index));
    }

    /**
     * Adds the given pebble to this set, making sure the pebble knows what set it's in
     * @param p
     * @return
     */
    public boolean add(GSPebble p) {
        boolean res = super.add(p);
        if (res) {p.ofSet = this;}
        return res;
    }
    
    /**
     * remove from this all pebbles that are selected
     */
    public void deleteSelected() {
        ListIterator li = this.listIterator();
        ArrayList toRemove = new ArrayList();

        // need to do this in two stages to avoid a ConcurrentModification exception (i.e. modifing the list over which we are iterating, a big no-no)
        while (li.hasNext()) {
            GSPebble p = (GSPebble)(li.next());
            if (p.isSelected()) {
                toRemove.add(p);
                //this.remove(p);
            }
        }

        li = toRemove.listIterator();
        while (li.hasNext())
        {
            GSPebble p = (GSPebble)(li.next());
            this.remove(p);
        }
    }

    public void colorSelected(Color newColor) {
        ListIterator li = this.listIterator();
        while (li.hasNext()) {
            GSPebble p = ((GSPebble)(li.next()));
            if (p.isSelected()) { p.setColor(newColor); }
        }
    }

    public void applyDeformation(Deformation deformation) {
        ListIterator li = this.listIterator();
        while (li.hasNext()) {
            ((GSPebble)(li.next())).deform(deformation);
        }
    }

    public List getIdsOfPebblesHitByPoint(Point2D p) {
        List res = new ArrayList();
        ListIterator li = this.listIterator();
        while (li.hasNext()) {
            GSPebble peb = (GSPebble)(li.next());
            if (GSPebble.SELECTION_RADIUS >= peb.getCenterAsPoint2D().distance(p)) {
                res.add(peb.getId());
            }
        }
        
        return res;
    }
    
    public void selectPebblesByIds(List ids, boolean shiftIsDown) {
        ListIterator li = this.listIterator();
        while (li.hasNext()) {
            GSPebble peb = (GSPebble)(li.next());
            if (ids.contains(peb.getId())) {
                if (shiftIsDown) {
                    peb.setSelected(! peb.isSelected());
                } else {
                    peb.setSelected(true);
                }
            } else {
                if (! shiftIsDown) {
                    peb.setSelected(false);
                }
            }
        }
    }
}
