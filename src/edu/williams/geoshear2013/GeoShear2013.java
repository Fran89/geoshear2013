/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.williams.geoshear2013;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author cwarren
 */
public class GeoShear2013 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try
        {
            UIManager.setLookAndFeel (UIManager.getCrossPlatformLookAndFeelClassName ());
        } catch (UnsupportedLookAndFeelException e)
        {
        } catch (ClassNotFoundException e)
        {
        } catch (InstantiationException e)
        {
        } catch (IllegalAccessException e)
        {
        }
        
        MainWindow app = new MainWindow();
        app.setVisible(true); 
    }
}
