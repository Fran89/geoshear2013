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
     * @return a deformation that is the composite of all deformations in the series as if each were a deformation applied to the next (sans translations)
     */
    public Deformation getCompositeTransform() {
        return this.compositeDeformation;
    }
    
    /**
     * @param nth the number of the series to compose up to (1-based index - i.e. uses the first n items, not up to item index n). NOTE: if counter <= 0 returns the identity deformation, and if nth > this.size returns a composite of everything in this series
     * @return a deformation that is the composite of all deformations in the series up to the nth one, as if each were a deformation applied to the next (sans translations)
     */
    public Deformation getCompositeTransform(int nth) {
        Deformation composite = new Deformation();
        int counter = 0;
        ListIterator li = this.listIterator();
        while (li.hasNext() && counter < nth) {
            composite.timesInPlace(((Deformation)(li.next())));
            counter++;
        }
        return composite;
    }
    
    private void rebuildCompositeTransform() {
        this.compositeDeformation = new Deformation();
        ListIterator li = this.listIterator();
        while (li.hasNext()) {
            this.compositeDeformation.timesInPlace(((Deformation)(li.next())));
//            this.compositeDeformation = new Deformation((((Deformation)(li.next()))).times(this.compositeDeformation));
        }
    }
    
    public void runAllDeformationsOn(GSPebble p) {
        ListIterator li = this.listIterator();
        while (li.hasNext()) {
//            this.compositeDeformation.timesInPlace(((Deformation)(li.next())));
            p.deform((Deformation)(li.next()));
        }
    }
    public void runAllDeformationsOn(GSPebble p, int nth) {
        ListIterator li = this.listIterator();
        int counter = 0;
        while (li.hasNext() && counter < nth) {
//            this.compositeDeformation.timesInPlace(((Deformation)(li.next())));
            p.deform((Deformation)(li.next()));
            counter++;
        }
    }
    /*---------------------------------------------------------------------*/
    
    @Override
    public Deformation get(int index) {
        return (Deformation)(super.get(index));
    }

    /**
     * Adds the given Deformation to this set, rebuilding the composite
     * @param d
     * @return
     */
    public boolean add(Deformation d) {
        boolean res = super.add(d);
        if (res) {
           this.rebuildCompositeTransform();
        }
        return res;
    }
    
    /**
     * removes the deformation at the given index and rebuilds the composite
     * @param i
     * @return
     */
    @Override
    public Deformation remove(int i) {
        Deformation res = (Deformation) super.remove(i);
        this.rebuildCompositeTransform();
        return res;
    }
}
