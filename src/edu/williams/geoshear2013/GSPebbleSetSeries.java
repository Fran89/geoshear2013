/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.williams.geoshear2013;

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
}
