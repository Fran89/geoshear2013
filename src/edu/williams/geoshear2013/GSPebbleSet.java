/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.williams.geoshear2013;

import java.awt.Color;
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

    private final static String SERIAL_TOKEN = "\n";

    //private ArrayList boundSets;
    public GSPebbleSet() {
        super();
    }
    
    public GSPebbleSet(GSComplex gsc) {
        super();
        this.ofComplex = gsc;
    }

    public GSPebbleSet(int initialCapacity) {
        super(initialCapacity);
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
    
    public boolean equals(GSPebbleSet other) {
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
            s = s+((GSPebble)(li.next())).serialize()+GSPebbleSet.SERIAL_TOKEN;
        }
        return s;
    }
    
    public String serializeToTabDelimited() {
        String s = "";
        ListIterator li = this.listIterator();
        while (li.hasNext()) {
            s = s+((GSPebble)(li.next())).serializeToTabDelimited()+GSPebbleSet.SERIAL_TOKEN;
        }
        return s;
    }

    public static GSPebbleSet deserialize(String serializedPebbleSet) {
        GSPebbleSet newPS = new GSPebbleSet();
        
        String[] pebData = serializedPebbleSet.split(GSPebbleSet.SERIAL_TOKEN);
        
        for(int i=0;i<pebData.length;i++) {
            pebData[i] = pebData[i].trim();
            if (pebData[i].isEmpty()) { continue; }
            newPS.add(GSPebble.deserialize(pebData[i]));
        }
        
        return newPS;
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
    
    /*---------------------------------------------------------------------*/
    
    public void selectAll() {
        this.selectSelectStateForAll(true);
    }
    
    public void unselectAll() {
        this.selectSelectStateForAll(false);
    }

    public void selectSelectStateForAll(boolean state) {
        ListIterator li = this.listIterator();
        while (li.hasNext()) {
            GSPebble p = (GSPebble)(li.next());
            p.setSelected(state);
        }
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
            }
        }

        li = toRemove.listIterator();
        while (li.hasNext())
        {
            GSPebble p = (GSPebble)(li.next());
            this.remove(p);
        }
    }

    /**
     * change the color of all pebbles that are selected
     * NOTE: alias for colorSelectedPebbles
     */
    public void colorSelected(Color newColor) {
       this.colorSelectedPebbles(newColor);
    }
    
    /**
     * change the color of all pebbles that are selected
     */
    public void colorSelectedPebbles(Color c) {
        ListIterator li = this.listIterator();
        while (li.hasNext()) {
            GSPebble peb = (GSPebble)(li.next());
            if (peb.isSelected()) {
                peb.setColor(c);
            }
        }    
    }
    
    /**
     * apply the given deformation to each pebble in this set
     * @param deformation 
     */
    public void applyDeformation(Deformation deformation) {
        ListIterator li = this.listIterator();
        while (li.hasNext()) {
            ((GSPebble)(li.next())).deform(deformation);
        }
    }

    /**
     * gets a list of pebble ids for pebbles that are 'hit' (i.e. within the selection radius distance) of the given point
     * @param p a point in the GSC coordinate system
     * @return a List of strings that are the ids of all the pebbles whose centers are within the selection radius of the point
     */
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
    
    /**
     * sets the selected attribute on all relevant pebbles in this set
     * @param ids a List of strings which are ids of pebble objects
     * @param shiftIsDown the state of the shift key - this controls whether selection states are toggled or wiped
     */
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

    /**
     * @return a string that may be used as an id for a pebble to be added to this set - the id will be unique within this set
     */
    public String getNewId() {
        int basis = (this.size()+1);
        String newId = "pebble_"+Util.fillIntLeft(basis, 8);
        while (this.containsId(newId)) {
            basis++;
            newId = newId = "pebble_"+Util.fillIntLeft(basis, 8);
        }
        return newId;
    }
    /**
     * @param id the id to check
     * @return true if this sets contains a pebble with the given id, false otherwise
     */
    public boolean containsId(String id) {
        ListIterator li = this.listIterator();
        while (li.hasNext()) {
            GSPebble peb = (GSPebble)(li.next());
            if (peb.getId().equals(id)) { return true; }
        }
        return false;
    }

    public double getMaxRf() {
        double maxRf = 0;
        ListIterator li = this.listIterator();
        while (li.hasNext()) {
            GSPebble peb = (GSPebble)(li.next());
            if (peb.getRF() > maxRf) { maxRf = peb.getRF(); }
        }
        return maxRf;
    }
    
    /**
     * testing for this class
     */
    public static void main(String[] args) {
        GSPebbleSet ps1 = new GSPebbleSet();
        ps1.add(new GSPebble("p12",200,100,45,30,.5, Color.GREEN));
        ps1.add(new GSPebble("p13",100,200,60,40,-1, Color.BLUE));
        ps1.add(new GSPebble("p14",200,200,75,50,2, Color.MAGENTA));
        
        System.out.println("ps1: "+ps1.serialize());
        GSPebbleSet ps2 = GSPebbleSet.deserialize(ps1.serialize());
        System.out.println("ps2: "+ps2.serialize());
        System.out.println("ps1==ps2: "+ps1.equals(ps2));
        
        System.out.println("ps1: "+ps1.serializeToTabDelimited());
        ps2 = GSPebbleSet.deserialize(ps1.serializeToTabDelimited());
        System.out.println("ps2: "+ps2.serializeToTabDelimited());
    }
}
