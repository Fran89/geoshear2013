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
public class GSDeformationSeries extends ArrayList {

    private final static String SERIAL_TOKEN = "\n";
    
    private Deformation compositeDeformation;
    
    public GSDeformationSeries() {
        super();
        this.compositeDeformation = new Deformation();
    }

    public GSDeformationSeries(int initialCapacity) {
        super(initialCapacity);
        this.compositeDeformation = new Deformation();
    }


    /*---------------------------------------------------------------------*/
    public boolean equals(GSDeformationSeries other) {
        if (this.size() != other.size()) { return false; }
        for (int i=0; i<this.size(); i++) {
            if (! this.get(i).equals(other.get(i))) { return false; }
        }
        return true;
    }
    
    /*---------------------------------------------------------------------*/
    
    public String serialize() {
        String s = "";
        ListIterator li = this.listIterator();
        while (li.hasNext()) {
            s = s+((Deformation)(li.next())).serialize()+GSDeformationSeries.SERIAL_TOKEN;
        }
        return s;
    }
    
    public String serializeToTabDelimited() {
        String s = "";
        ListIterator li = this.listIterator();
        while (li.hasNext()) {
            s = s+((Deformation)(li.next())).serializeToTabDelimited()+GSDeformationSeries.SERIAL_TOKEN;
        }
        return s;
    }

    public static GSDeformationSeries deserialize(String serializedDeformationSeries) {
        GSDeformationSeries newDS = new GSDeformationSeries();
        
        String[] defData = serializedDeformationSeries.split(GSDeformationSeries.SERIAL_TOKEN);
        
        for(int i=0;i<defData.length;i++) {
            defData[i] = defData[i].trim();
            if (defData[i].isEmpty()) { continue; }
            newDS.add(Deformation.deserialize(defData[i]));
        }
        
        return newDS;
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
            p.deform((Deformation)(li.next()));
        }
    }
    public void runAllDeformationsOn(GSPebble p, int nth) {
        ListIterator li = this.listIterator();
        int counter = 0;
        while (li.hasNext() && counter < nth) {
            p.deform((Deformation)(li.next()));
            counter++;
        }
    }
    
    public void runAllDeformationsOn(GSPebbleSet ps, int nth) {
        ListIterator li = this.listIterator();
        int counter = 0;
        while (li.hasNext() && counter < nth) {
            ps.applyDeformation((Deformation)(li.next()));
            counter++;
        }
    }
    
    /*---------------------------------------------------------------------*/
    
    @Override
    public Deformation get(int index) {
        if (index >= 0) {
            return (Deformation)(super.get(index));
        }
        return new Deformation();
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
    
    /**
     * testing for this class
     */
    public static void main(String[] args) {
        GSDeformationSeries ds1 = new GSDeformationSeries();
        ds1.add(new Deformation(1, .5, 0, 1));
        ds1.add(new Deformation(1, 0, .75, 1));
        ds1.add(new Deformation(0.5253, 0.8509, -0.8509, 0.5253));
        
        System.out.println("ds1: "+ds1.serialize());
        GSDeformationSeries ds2 = GSDeformationSeries.deserialize(ds1.serialize());
        System.out.println("ds2: "+ds2.serialize());
        System.out.println("ds1==ds2: "+ds1.equals(ds2));
        
        System.out.println("(tab)ds1: "+ds1.serializeToTabDelimited());
        ds2 = GSDeformationSeries.deserialize(ds1.serializeToTabDelimited());
        System.out.println("(tab)ds2: "+ds2.serializeToTabDelimited());
    }
}
