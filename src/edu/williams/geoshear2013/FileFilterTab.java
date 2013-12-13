/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.williams.geoshear2013;

import java.io.File;
import javax.swing.filechooser.FileFilter;


/**
 *
 * @author cwarren
 */
public class FileFilterTab extends FileFilter {

    /**
     * the string extension used for tab delimited files
     */
    public static String FORMAT_TAB_EXTENSION = "tab";
    /**
     * the string extension used for text files
     */
//    public static String FORMAT_TXT_EXTENSION = "txt";

    /**
     * Checks to see whether the give File object passes the filter. It does if the
     * File is a directory or has a name that ends with an allowed extension.
     * @param f the file to check
     * @return true of the File is a directory or has a name that ends in one of the allowed
     * extensions, and false otherwise.
     */
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = Util.getExtension (f);
        if (extension != null) {
            return this.isExtensionOK (extension);
        }

        return false;
    }

    /**
     * Returns a string that describes what this filter does
     * @return a string that describes what this filter does
     */
    public String getDescription() {
        return "Tab delimited files (tab)";
    }

    /**
     * Checks to see if the string passed in is one of the allowed extensions. The
     * check is case insensitive.
     * @param ext The string to check
     * @return true if the string is one of the allowed extensions, and false otherwise
     */
    public boolean isExtensionOK(String ext)
    {
        ext = ext.toLowerCase ();
        if (ext.equals(FileFilterTab.FORMAT_TAB_EXTENSION))
        {
            return true;
        }
        return false;
    }

}