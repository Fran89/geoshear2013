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
    
    private final static String SERIAL_TOKEN = "\\[[^\\]]+\\]"; // anything of the form "[foo]"
    private final static String KV_TOKEN = "=";

    private final static String KEY_PEBBLES = "pebbles";
    private final static String KEY_DEFORMATIONS = "deformations";
    private final static String KEY_BG_IMAGE = "bgimage";
    private final static String KEY_CENTER = "center";

    public double harmonicMean;
    public double vectorMean;
    
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

    public String serialize() {
        return this.serializeSpecificSet(0);
    }
    
    public String serializeCurrent() {
        return this.serializeSpecificSet(this.currentDeformationNumber-1);
    }
    
    public String serializeSpecificSet(int setIndex) {
        String s = "";
        s += "\n["+GSComplex.KEY_CENTER+"]\n";
        s += GSComplex.KEY_CENTER + "=" + this.center.serialize() +"\n";

        s += "\n["+GSComplex.KEY_BG_IMAGE+"]\n";
        if (! this.getBgImageFileName().isEmpty()) {
            s += GSComplex.KEY_BG_IMAGE+"="+this.getBgImageFileName()+"\n";
        }

        s += "\n["+GSComplex.KEY_PEBBLES+"]\n";
        s += GSComplex.KEY_PEBBLES + "=\n" + this.pebbleSets.get(setIndex).serialize() +"\n";
        
        s += "\n["+GSComplex.KEY_DEFORMATIONS+"]\n";
        if (setIndex == 0) {
            s += GSComplex.KEY_DEFORMATIONS + "=\n" + this.deformations.serialize() +"\n";
        }
        return s;
    }
    
    
    public String serializeToTabDelimited() {
        return this.serializeToTabDelimitedSpecificSet(0);
    }

    public String serializeCurrentToTabDelimited() {
        return this.serializeToTabDelimitedSpecificSet(this.currentDeformationNumber-1);
    }
    
    public String serializeToTabDelimitedSpecificSet(int setIndex) {
        String s = "";
        s += "\n["+GSComplex.KEY_CENTER+"]\n";
        s += GSComplex.KEY_CENTER+"\n";
        s += GSPoint.serializeHeadersToTabDelimited()+"\n";
        s += this.center.serializeToTabDelimited()+"\n";

        s += "\n["+GSComplex.KEY_BG_IMAGE+"]\n";
        s += GSComplex.KEY_BG_IMAGE+"\n";
        if (! this.getBgImageFileName().isEmpty()) {
            s += "File\n";
            s += this.getBgImageFileName()+"\n";
        }

        s += "\n["+GSComplex.KEY_PEBBLES+"]\n";
        s += GSComplex.KEY_PEBBLES+"\n";
        s += GSPebble.serializeHeadersToTabDelimited()+"\n";
        s += this.pebbleSets.get(setIndex).serializeToTabDelimited()+"\n";
        
        s += "\n["+GSComplex.KEY_DEFORMATIONS+"]\n";
        s += GSComplex.KEY_DEFORMATIONS+"\n";
        if (setIndex == 0) {
            s += Deformation.serializeHeadersToTabDelimited()+"\n";
            s += this.deformations.serializeToTabDelimited()+"\n";
        }
        return s;
    }
    
    public static GSComplex deserialize(String serializedGSComplex) {
        GSComplex gsc = new GSComplex();
               
        serializedGSComplex = serializedGSComplex.replaceAll("(?m)^\\s*#",""); // remove comments
        serializedGSComplex = serializedGSComplex.replaceAll("(?m)^\\s*?\n",""); // remove blank lines
        
        //System.err.println(serializedGSComplex);

        if (serializedGSComplex.indexOf("\t") > -1) {
            // HANDLE TABBED IMPORT
            String[] gscData = serializedGSComplex.split(GSComplex.SERIAL_TOKEN);
            for(int i=0; i < gscData.length; i++) {
                gscData[i] = gscData[i].trim();
                if (gscData[i].isEmpty()) {
                    continue;  // skip empty sections
                }
                String data = gscData[i];
                data = data.substring(data.indexOf('\n')+1); // strip the first line - the identifier for the section
                data = data.substring(data.indexOf('\n')+1); // strip the second line of the section, the headers for the columns
                if (gscData[i].startsWith(GSComplex.KEY_PEBBLES)) {
                    GSPebbleSet basePS = GSPebbleSet.deserialize(data);
                    basePS.ofComplex = gsc;
                    gsc.pebbleSets.clear();
                    gsc.pebbleSets.add(basePS);
                }
                if (gscData[i].startsWith(GSComplex.KEY_DEFORMATIONS)) {
                    GSDeformationSeries ds = GSDeformationSeries.deserialize(data);
                    gsc.deformations = ds;
                }
                if (gscData[i].startsWith(GSComplex.KEY_BG_IMAGE)) {
                    if (! data.isEmpty())
                    {
                        gsc.setBgImageFileName(data);
                        gsc.loadBgImage();
                    }
                }
                if (gscData[i].startsWith(GSComplex.KEY_CENTER)) {
                    gsc.setCenter(GSPoint.deserialize(data));
                }
            }
        } else {
            // HANDLE .GES
            String[] gscData = serializedGSComplex.split(GSComplex.SERIAL_TOKEN);
            for(int i=0; i < gscData.length; i++) {
                gscData[i] = gscData[i].trim(); // remove leading and trailing whitespace
                if (gscData[i].isEmpty()) {
                    continue;  // skip empty sections
                }
                
                if (gscData[i].startsWith(GSComplex.KEY_PEBBLES+"=")) {
                    GSPebbleSet basePS = GSPebbleSet.deserialize(gscData[i].substring(GSComplex.KEY_PEBBLES.length()+1));
                    basePS.ofComplex = gsc;
                    gsc.pebbleSets.clear();
                    gsc.pebbleSets.add(basePS);
                } else 
                if (gscData[i].startsWith(GSComplex.KEY_DEFORMATIONS+"=")) {
                    GSDeformationSeries ds = GSDeformationSeries.deserialize(gscData[i].substring(GSComplex.KEY_DEFORMATIONS.length()+1));
                    gsc.deformations = ds;
                } else
                if (gscData[i].startsWith(GSComplex.KEY_BG_IMAGE+"=")) {
                    String[] keyVal = gscData[i].split(GSComplex.KV_TOKEN);
                    keyVal[1] = keyVal[1].trim();
                    if (! keyVal[1].isEmpty())
                    {
                        gsc.setBgImageFileName(keyVal[1]);
                        gsc.loadBgImage();
                    }
                } else
                if (gscData[i].startsWith(GSComplex.KEY_CENTER+"=")) {
                    String[] keyVal = gscData[i].split(GSComplex.KV_TOKEN);
                    keyVal[1] = keyVal[1].trim();
                    if (! keyVal[1].isEmpty())
                    {
                        gsc.setCenter(GSPoint.deserialize(keyVal[1]));
                    }
                }
            }
        }

        gsc.rebuildPebbleSetsFromDeformationSeries();
        
        // advance deform nav to last deformation in the gsc
        for (int i=gsc.deformations.size(); i>0; i--) {
            gsc.nextDeformation();
        }
        return gsc;
    }
    
    /*------------------------------------------------------------------------*/

    /**
     * @return the currentDeformationIndex
     */
    public int getCurrentDeformationNumber() {
        return this.currentDeformationNumber;
    }
    
    public GSPebbleSet getCurrentlyDeformedPebbleSet() {
        return this.pebbleSets.get(this.currentDeformationNumber-1);
    }
    
    /**
     * remove the deformation associated with currentDeformationNumber (lowering the index of subsequent ones) and rebuild the pebblesets to match the modified deformation series
     * NOTE: the value of currentDeformationNumber is unchanged unless it would be out of bounds, in which case it is set to the end of the series
     */
    public void removeCurrentDeformation() {
        this.deformations.remove(this.currentDeformationNumber-2);
        this.rebuildPebbleSetsFromDeformationSeries();
        if (this.currentDeformationNumber > this.deformations.size()+1) {
            this.currentDeformationNumber = this.deformations.size()+1;
        }
        this.notifyWatchers();        
    }
    
    public void nextDeformation() {
        if (this.currentDeformationNumber <= this.deformations.size()) {
            this.currentDeformationNumber++;
        }
        this.notifyWatchers();
    }
    
    public void prevDeformation() {
        if (this.currentDeformationNumber > 1) {
            this.currentDeformationNumber--;
        }
        this.notifyWatchers();
    }
    
    public void lastDeformation() {
        this.currentDeformationNumber = this.deformations.size() + 1;
        this.notifyWatchers();
    }

    public Deformation getCompositeTransform() {
        return this.deformations.getCompositeTransform(this.currentDeformationNumber-1);
    }
    
    /*------------------------------------------------------------------------*/
    
    public void drawOnto(Graphics2D g2d, boolean isFilled, boolean showAxes, Deformation tenativeDeformation) {
        GSPebbleSet workingPebbleSet = this.pebbleSets.get(this.currentDeformationNumber-1);
        if (! tenativeDeformation.isIdentity()) {
            workingPebbleSet = this.pebbleSets.getLast().clone();
            workingPebbleSet.applyDeformation(tenativeDeformation);
        }
        
        boolean inEditMode = this.usedUI.getCurrentUIMode()==GSComplexUI.UI_MODE_EDIT_PEBBLES;
        for (int i=0; i<workingPebbleSet.size(); i++) {
            workingPebbleSet.get(i).drawOnto(g2d, isFilled, showAxes, inEditMode);
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
        w.reactTo(this, null);
    }

    @Override
    public void removeWatcher(Watcher w) {
        watchedBy.remove(w);
    }

    @Override
    public void removeAllWatchers() {
        ListIterator li = watchedBy.listIterator();
        while (li.hasNext())
        {
            Watcher wr = (Watcher)(li.next());
            wr.clearWatched(this);
        }
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
        this.notifyWatchers();
    }
    
    /**
     * @param x, y the coordinates to which the center should be set
     */
    public void setCenter(double x, double y) {
        this.resetPositionOfPebblesRelativeToNewOrigin(this.center.x, this.center.y, x, y);
        this.center.x = x;
        this.center.y = y;
        this.notifyWatchers();
    }
    
    private void resetPositionOfPebblesRelativeToNewOrigin(double initial_origin_x, double initial_origin_y, double new_origin_x, double new_origin_y) {
        double origin_shift_x = new_origin_x - initial_origin_x;
        double origin_shift_y = initial_origin_y - new_origin_y;
        for (int i_sets=0; i_sets<this.pebbleSets.size(); i_sets++) {
            GSPebbleSet pebbles = this.pebbleSets.get(i_sets);
            for (int i_pebbles=0; i_pebbles<pebbles.size(); i_pebbles++) {
                pebbles.get(i_pebbles).shiftPosition(origin_shift_x,origin_shift_y);
            }
        }
        this.notifyWatchers();
    }

    void applyDeformation(Deformation d) {
        this.deformations.add(d);
        GSPebbleSet newPebbleSet = this.pebbleSets.get(0).clone();
        ListIterator li = this.deformations.listIterator();
        while (li.hasNext()) {
            newPebbleSet.applyDeformation((Deformation)(li.next()));
        }
        this.pebbleSets.add(newPebbleSet);
        this.lastDeformation();
        this.notifyWatchers();
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
        this.notifyWatchers();
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
                img = tk.getImage (this.getBgImageFileName());
                m.addImage (img, 0);
                m.waitForAll ();
                this.bgImage = new BufferedImage(img.getWidth(null),img.getHeight(null),BufferedImage.TYPE_INT_RGB);
                this.bgImage.createGraphics ().drawImage (img,0,0,null);
            }
            catch (Exception e)
            {
                // ideally would have a more graceful failure here...
                Util.todo("pop up alert when BG image load fails");
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

    void deleteSelectedPebbles() {
        GSPebbleSet base = this.pebbleSets.get(0);
        for (int i=0; i<base.size(); i++) {
            if (base.get(i).isSelected()) {
                base.remove(i);
                i--;
            }
        }
        this.rebuildPebbleSetsFromDeformationSeries();
        this.notifyWatchers();
    }
    
    public void setMeans()
    {
        double sumRFinvert = 0;
        double sumSinPhi = 0;
        double sumCosPhi = 0;
        double cumuAngle = 0;

        GSPebbleSet currentPebbleSet = this.getCurrentlyDeformedPebbleSet().clone();
        currentPebbleSet.applyDeformation(this.usedUI.getTentativeDeformationCopy());
        
        ListIterator pli = currentPebbleSet.listIterator ();
        while (pli.hasNext ())
        {
            GSPebble peb = (GSPebble)(pli.next ());
            sumRFinvert += (1/peb.getRF());

            double pebbleTheta = peb.getThetaRad();

            sumSinPhi += Math.sin(pebbleTheta);
            sumCosPhi += Math.cos(pebbleTheta);
            cumuAngle += pebbleTheta;
        }

        this.harmonicMean = ((double)(currentPebbleSet.size())) / sumRFinvert;
        this.vectorMean = Math.atan(sumSinPhi/sumCosPhi); 
    }

    /**
     * testing for this class
     */
    public static void main(String[] args) {
        GSComplex gsc1 = new GSComplex();

        gsc1.pebbleSets.get(0).add(new GSPebble("p12",200,100,45,30,.5, Color.GREEN));
        gsc1.pebbleSets.get(0).add(new GSPebble("p13",100,200,60,40,-1, Color.BLUE));
        gsc1.pebbleSets.get(0).add(new GSPebble("p14",200,200,75,50,2, Color.MAGENTA));
        
        gsc1.deformations.add(new Deformation(1, .5, 0, 1));
        gsc1.deformations.add(new Deformation(1, 0, .75, 1));
        gsc1.deformations.add(new Deformation(0.5253, 0.8509, -0.8509, 0.5253));
        
//        System.out.println("deformations:\n"+gsc1.deformations.serialize());
        
        System.out.println("-------\ngsc1:\n"+gsc1.serialize());
        GSComplex gsc2 = GSComplex.deserialize(gsc1.serialize());
        System.out.println("-------\ngsc2:\n"+gsc2.serialize());
//        
//        System.out.println("(tab)ds1: "+ds1.serializeToTabDelimited());
//        ds2 = GSDeformationSeries.deserialize(ds1.serializeToTabDelimited());
//        System.out.println("(tab)ds2: "+ds2.serializeToTabDelimited());
    }

    /**
     * @return the harmonicMean
     */
    public double getHarmonicMean() {
        return harmonicMean;
    }

    /**
     * @param harmonicMean the harmonicMean to set
     */
    public void setHarmonicMean(double harmonicMean) {
        this.harmonicMean = harmonicMean;
    }

    /**
     * @return the vectorMean
     */
    public double getVectorMean() {
        return vectorMean;
    }

    /**
     * @param vectorMean the vectorMean to set
     */
    public void setVectorMean(double vectorMean) {
        this.vectorMean = vectorMean;
    }
}
