/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geoshear2013;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.ListIterator;

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
            if (p.selected) {
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
            if (p.selected) { p.setColor(newColor); }
        }
    }

    public void applyDeformation(AffineTransform deformation) {
        ListIterator li = this.listIterator();
        while (li.hasNext()) {
            ((GSPebble)(li.next())).deform(deformation);
        }
    }
}
