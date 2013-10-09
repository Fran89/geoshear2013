/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geoshear2013;

import java.util.ArrayList;

/**
 *
 * @author cwarren
 */
public class GSEllipseSeries extends ArrayList {

    public GSEllipseSeries() {
        super();
       // this.boundSets = new ArrayList(4);
    }

    public GSEllipseSeries(int initialCapacity) {
        super(initialCapacity);
       // this.boundSets = new ArrayList(4);
    }

    /*---------------------------------------------------------------------*/
    
    @Override
    public GSEllipse get(int index) {
        return (GSEllipse)(super.get(index));
    }

    /**
     * Adds the given pebble to this set, making sure the pebble knows what set it's in
     * @param p
     * @return
     */
    public boolean add(GSEllipse e) {
        boolean res = super.add(e);
        return res;
    }    
}
