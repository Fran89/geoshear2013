/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.williams.geoshear2013;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.ListIterator;
import javax.swing.JPanel;

/**
 *
 * @author cwarren
 */
public class GSComplex implements Watchable {
    public GSPebbleSetSeries pebbleSets;
    public GSDeformationSeries deformations;

    private int currentDeformationNumber = 1;

    /**
     * the center of this complex relative to 0,0 as the upper left
     */
    private GSPoint center;

    private BufferedImage bgImage;
    private String bgImageFileName;
    
    private GSComplexUI usedUI;
    
    /*------------------------------------------------------------------------*/
    public GSComplex() {
        this.pebbleSets = new GSPebbleSetSeries();
        this.pebbleSets.add(new GSPebbleSet(this));
        this.deformations = new GSDeformationSeries();
        this.center = new GSPoint(0,0);
        this.bgImage = null;
        this.bgImageFileName = "";
    }
    
    /*------------------------------------------------------------------------*/

        /**
     * @return the currentDeformationIndex
     */
    public int getCurrentDeformationNumber() {
        return this.currentDeformationNumber;
    }
    
    /**
     * remove the deformation associated with currentDeformationNumber (lowering the index of subsequent ones) and rebuild the pebblesets to match the modified deformation series
     * NOTE: the value of currentDeformationNumber is unchanged unless it would be out of bounds, in which case it is set to the end of the series
     */
    public void removeCurrentDeformation() {
//        Util.todo("implement removeCurrentDeformation");
//        System.err.println("   pre-remove currentDeformationNumber: "+this.currentDeformationNumber);
//        System.err.println("   pre-remove deformation series size: "+this.deformations.size());
//        System.err.println("   pre-remove pebble set series size: "+this.pebbleSets.size());
        this.deformations.remove(this.currentDeformationNumber-2);
//        Util.todo("handle pebble set rebuilding");
//        this.pebbleSets.truncateFrom(currentDeformationNumber-1);
        this.rebuildPebbleSetsFromDeformationSeries();
        if (this.currentDeformationNumber > this.deformations.size()+1) {
            this.currentDeformationNumber = this.deformations.size()+1;
        }
//        System.err.println("   post-remove currentDeformationNumber: "+this.currentDeformationNumber);
//        System.err.println("   post-remove deformation series size: "+this.deformations.size());
//        System.err.println("   post-remove pebble set series size: "+this.pebbleSets.size());
    }
    
    public void nextDeformation() {
        if (this.currentDeformationNumber <= this.deformations.size()) {
            this.currentDeformationNumber++;
        }
//        System.out.println("TO BE FINISHED/IMPLEMENTED: gsc.nextDeformation");
    }
    
    public void prevDeformation() {
        if (this.currentDeformationNumber > 1) {
            this.currentDeformationNumber--;
        }
//        System.out.println("TO BE FINISHED/IMPLEMENTED: gsc.prevDeformation");
    }
    
    public void lastDeformation() {
        this.currentDeformationNumber = this.deformations.size() + 1;
//        System.out.println("TO BE FINISHED/IMPLEMENTED: gsc.lastDeformation");
    }

    public Deformation getCompositeTransform() {
        return this.deformations.getCompositeTransform(this.currentDeformationNumber-1);
    }
    
    /*------------------------------------------------------------------------*/
    
    public void drawOnto(Graphics2D g2d, boolean isFilled, boolean showAxes, Deformation tenativeDeformation) {
//        GSPebbleSet workingPebbleSet = this.pebbleSets.getLast();
//        if (this.currentDeformationNumber < this.deformations.size()) {
//            workingPebbleSet = this.pebbleSets.get(this.currentDeformationNumber-1);
//        }       
        GSPebbleSet workingPebbleSet = this.pebbleSets.get(this.currentDeformationNumber-1);
        if (! tenativeDeformation.isIdentity()) {
            workingPebbleSet = this.pebbleSets.getLast().clone();
            workingPebbleSet.applyDeformation(tenativeDeformation);
        }
        
        boolean inEditMode = this.usedUI.getCurrentUIMode()==GSComplexUI.UI_MODE_EDIT_PEBBLES;
        for (int i=0; i<workingPebbleSet.size(); i++) {
            workingPebbleSet.get(i).drawOnto(g2d, isFilled, showAxes, inEditMode);
            //tenativelyDeformedPebbles.get(i).errDump();
        }
    }
    
    /*------------------------------------------------------------------------*/

    /*
     * implementing the Watchable interface
     */
    private ArrayList watchedBy = new ArrayList();

    @Override
    public void addWatcher(Watcher w) {
        watchedBy.add(w);
        //w.setWatched(this);
        w.reactTo(this, null);
    }

    @Override
    public void removeWatcher(Watcher w) {
        watchedBy.remove(w);
    }

    @Override
    public void removeAllWatchers() {
        watchedBy.clear();
    }

    @Override
    public void notifyWatchers() {
        this.notifyWatchers(null);
    }

    @Override
    public void notifyWatchers(Object arg) {
        ListIterator li = watchedBy.listIterator();
        while (li.hasNext())
        {
            Watcher wr = (Watcher)(li.next());
            wr.reactTo(this,arg);
        }
    }

    /*------------------------------------------------------------------------*/

    /**
     * @return the center
     */
    public GSPoint getCenter() {
        return this.center;
    }

    /**
     * @param center the center to set
     */
    public void setCenter(GSPoint center) {
        this.resetPositionOfPebblesRelativeToNewOrigin(this.center.x, this.center.y, center.x, center.y);
        this.center.x = center.x;
        this.center.y = center.y;
    }
    
    /**
     * @param x, y the coordinates to which the center should be set
     */
    public void setCenter(double x, double y) {
        this.resetPositionOfPebblesRelativeToNewOrigin(this.center.x, this.center.y, x, y);
        this.center.x = x;
        this.center.y = y;
    }
    
    private void resetPositionOfPebblesRelativeToNewOrigin(double initial_origin_x, double initial_origin_y, double new_origin_x, double new_origin_y) {
//        System.err.println("orig origin: "+initial_origin_x+","+initial_origin_y);
//        System.err.println("new origin: "+new_origin_x+","+new_origin_y);
        double origin_shift_x = new_origin_x - initial_origin_x;
        double origin_shift_y = initial_origin_y - new_origin_y;
//        System.err.println("re-centering shift: "+origin_shift_x+","+origin_shift_y);
        for (int i_sets=0; i_sets<this.pebbleSets.size(); i_sets++) {
            GSPebbleSet pebbles = this.pebbleSets.get(i_sets);
            for (int i_pebbles=0; i_pebbles<pebbles.size(); i_pebbles++) {
                pebbles.get(i_pebbles).shiftPosition(origin_shift_x,origin_shift_y);
            }
        }
    }

    void applyDeformation(Deformation d) {
        this.deformations.add(d);
        GSPebbleSet newPebbleSet = this.pebbleSets.get(0).clone();
        ListIterator li = this.deformations.listIterator();
        while (li.hasNext()) {
//            this.compositeDeformation.timesInPlace(((Deformation)(li.next())));
            newPebbleSet.applyDeformation((Deformation)(li.next()));
        }
        this.pebbleSets.add(newPebbleSet);
        this.lastDeformation();
    }
    
    /**
     * re-create the series of pebble sets based on the current contents of the deformation series
     */
    public void rebuildPebbleSetsFromDeformationSeries() {
        GSPebbleSet base = this.pebbleSets.get(0);
        this.pebbleSets.clear();
        this.pebbleSets.add(base);
        for (int deformationMaxIndex=0; deformationMaxIndex<this.deformations.size(); deformationMaxIndex++) {
            GSPebbleSet newPebbleSet = this.pebbleSets.get(0).clone();
            for (int deformationCurIndex=0; deformationCurIndex<=deformationMaxIndex; deformationCurIndex++) {
                newPebbleSet.applyDeformation(this.deformations.get(deformationCurIndex));
            }
            this.pebbleSets.add(newPebbleSet);
        }
    }

    public String getBgImageFileName() {
        return bgImageFileName;
    }

    public void setBgImageFileName(String bgImageFileName) {
        this.bgImageFileName = bgImageFileName;
        this.loadBgImage();
    }

    @SuppressWarnings("static-access")
    public void loadBgImage ()
    {
        if ((this.getBgImageFileName () != null) && (this.getBgImageFileName ().length () > 0))
        {
            Toolkit tk = Toolkit.getDefaultToolkit ();
            Image img = null;
            try
            {
                MediaTracker m = new MediaTracker (new JPanel ());
                //System.out.println("bg file name is "+this.getBgImageFileName()); // debugging
                img = tk.getImage (this.getBgImageFileName());
                m.addImage (img, 0);
                m.waitForAll ();
                this.bgImage = new BufferedImage(img.getWidth(null),img.getHeight(null),BufferedImage.TYPE_INT_RGB);
                this.bgImage.createGraphics ().drawImage (img,0,0,null);
            }
            catch (Exception e)
            {
                // ideally would have a more graceful failure here...
                // TODO - pop up alert when BG image load fails
                e.printStackTrace ();
            }
        }
    }

    public BufferedImage getBgImage() {
        return bgImage;
    }    

    /**
     * @return the usedUI
     */
    public GSComplexUI getUsedUI() {
        return usedUI;
    }

    /**
     * @param usedUI the usedUI to set
     */
    public void setUsedUI(GSComplexUI usedUI) {
        this.usedUI = usedUI;
    }
}
