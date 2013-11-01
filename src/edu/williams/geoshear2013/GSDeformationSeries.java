/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.williams.geoshear2013;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.ListIterator;

/**
 *
 * @author cwarren
 */
public class GSDeformationSeries extends ArrayList {

    private Deformation compositeDeformation;
    
    public GSDeformationSeries() {
        super();
       // this.boundSets = new ArrayList(4);
        this.compositeDeformation = new Deformation();
    }

    public GSDeformationSeries(int initialCapacity) {
        super(initialCapacity);
       // this.boundSets = new ArrayList(4);
        this.compositeDeformation = new Deformation();
    }


    /*---------------------------------------------------------------------*/
    
    /**
     * 
     * @return an ellipse that is the composite of all ellipses in the series as if each were a strain applied to the next (sans translations)
     */
    public Deformation getCompositeTransform() {
        return this.compositeDeformation;
    }
    
    private void rebuildCompositeTransform() {
        this.compositeDeformation = new Deformation();
        ListIterator li = this.listIterator();
        while (li.hasNext()) {
//            this.compositeDeformation.timesInPlace(((Deformation)(li.next())));
            this.compositeDeformation = new Deformation((((Deformation)(li.next()))).times(this.compositeDeformation));
        }
    }
    /*---------------------------------------------------------------------*/
    
    @Override
    public Deformation get(int index) {
        return (Deformation)(super.get(index));
    }

    /**
     * Adds the given pebble to this set, making sure the pebble knows what set it's in
     * @param p
     * @return
     */
    public boolean add(Deformation d) {
        boolean res = super.add(d);
        if (res) {
           this.rebuildCompositeTransform();
        }
        return res;
    }    
}
