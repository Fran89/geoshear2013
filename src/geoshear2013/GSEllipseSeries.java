/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geoshear2013;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.ListIterator;

/**
 *
 * @author cwarren
 */
public class GSEllipseSeries extends ArrayList {

    private Matrix2x2 compositeTransform;
    
    public GSEllipseSeries() {
        super();
       // this.boundSets = new ArrayList(4);
        this.compositeTransform = new Matrix2x2();
    }

    public GSEllipseSeries(int initialCapacity) {
        super(initialCapacity);
       // this.boundSets = new ArrayList(4);
        this.compositeTransform = new Matrix2x2();
    }


    /*---------------------------------------------------------------------*/
    
    /**
     * 
     * @return an ellipse that is the composite of all ellipses in the series as if each were a strain applied to the next (sans translations)
     */
    public Matrix2x2 getCompositeTransform() {
        return this.compositeTransform;
    }
    
    private void rebuildCompositeTransform() {
        this.compositeTransform = new Matrix2x2();
        ListIterator li = this.listIterator();
        while (li.hasNext()) {
            this.compositeTransform.timesInPlace(((GSPebble)(li.next())).getMatrix());
        }
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
        if (res) {
           this.rebuildCompositeTransform();
        }
        return res;
    }    
}
