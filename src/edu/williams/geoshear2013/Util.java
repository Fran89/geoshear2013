/**
 *
 * @author cwarren
 */
/*
 * Util.java
 *
 * Created on May 5, 2010, 2:55 PM
 *
GeoShear - A program to explore deformation of cross sections of stone

Copyright (C) September 2007, Chris Warren (Chris.Warren@williams.edu) and
Paul Karabinos (Paul.M.Karabinos@williams.edu)

This program is licensed under the terms of the Williams College Software
License as published by Williams College; either version 1 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE. See the Williams College Software License for more details.

You should have received a copy of the Williams College Software License along
with this program; if not, write to Williams College Office for Information
Technology, 22 Lab Campus Drive, Williamstown, MA 01267 USA
 *
 */

package edu.williams.geoshear2013;

import java.awt.Color;
import java.io.File;
import javax.swing.JTextField;

/**
 * This is a utility class to hold a number of commonly used functions
 * @author cwarren
 */
public class Util
{

    /**
     * max number of places to show after the decimal on displayed numbers
     */
    public static int DISPLAY_PRECISION = 3;

    /**
     * A number used in the truncation process
     */
    private static double TRUNCING_NUMBER = Math.pow (10.0,(double)Util.DISPLAY_PRECISION);

    /** Util is never instantiated */
    private Util () {}

    /**
     * Truncates or fills a text version of a decimal to a given number of places
     * e.g. truncTextDecimal("1.23456",2) -> "1.23". NOTE: this function may behave
     * strangely if passed a string that does not represent a number!
     * @param textDecimal a string that has the text version of a decimal number
     * @param places an integer indicating the desired number of places after the decimal place
     * @return a version of the string that has at most 'places' characters after the
     * decimal point
     */
    public static String truncTextDecimal(String textDecimal, int places)
    {
        //System.out.println("textDecimal: "+textDecimal);
        //System.out.println("places: "+places);
        if (places < 0) { places = 0; }
        String sign = "";
        if (textDecimal.startsWith("-")) {
            sign = "-";
            textDecimal = textDecimal.substring(1);
        }
        if (places > 0)
        {
            if (textDecimal.indexOf (".") > 0)
            {
                places = places + 1;
                int truncLength = textDecimal.length () - textDecimal.indexOf (".");
                if (places > truncLength)
                {
                    int padSize = places-truncLength;
                    for (int i=0; i<padSize; i++) {
                        textDecimal += "0";
                    }
                    return Util.signAsNeeded(textDecimal,sign);
                }
                if (truncLength > places)
                {
                    truncLength = places;
                }
                return Util.signAsNeeded(textDecimal.substring (0,textDecimal.indexOf (".")+truncLength),sign);
            } else { // starts with no decimal places, so pad with 0's
                textDecimal += ".";
                for (int i=0; i<places; i++) {
                    textDecimal += "0";
                }
            }
            return Util.signAsNeeded(textDecimal,sign);
        } else { // places == 0
            if (textDecimal.indexOf (".") > 0)
            {
                return Util.signAsNeeded(textDecimal.substring(0,textDecimal.indexOf (".")),sign); // chop decimal point and everything after
            } else {
                return Util.signAsNeeded(textDecimal,sign); // no decimal point so return as is
            }
        }
    }

    // add given sign to non-zero numbers
    public static String signAsNeeded(String numberAsString, String signSymbol) {
        if (signSymbol.equalsIgnoreCase("")) {
            return numberAsString;
        }
        if (Double.parseDouble(numberAsString) == 0) {
            return numberAsString;
        }
        return signSymbol+numberAsString;
    }
    
    /**
     * convert an integer to a string of a given length
     * NOTE: if the base is already longer than the target size then the base is returned unchanged - it is NOT truncated
     * @param base the integer to use as a basis
     * @param fullSize the length of the string to be returned
     * @return a string that is the integer padded on the left with 0's until it is fullSize characters long
     */
    public static String fillIntLeft(int base, int fullSize)
    {
        return Util.fillLeftWith(Integer.toString(base), fullSize, "0");
    }

    /**
     * convert a string to a new string of a given length, padded on the left with the given character to reach that length
     * NOTE: if the base is already longer than the target size then the base is returned unchanged - it is NOT truncated
     * NOTE: you may use a string of length > 1 as the fill, but the result may be > fullSize in length if you do so
     * @param base the string to be padded
     * @param fullSize the length of the results
     * @param fill the character/string to use to do the filling
     * @return a new string, which is the base padded on the left with the fill until it is at least fullSize long
     */
    public static String fillLeftWith(String base, int fullSize, String fill)
    {
        for (int i=base.length(); i<fullSize; i++)
        {
            base = fill+base;
        }
        return base;
    }

    /**
     * Truncates a given double to a fixed number of decimal places. The exact number
     * of places is conrolled by the DISPLAY_PRECISION value in the Pebble class.
     * @param num the number to be truncated
     * @return the original number truncated to Pebble.DISPLAY_PRECISION places
     * @see Pebble
     */
    public static double truncForDisplayNumber (double num)
    {
        // the .001 mess is to deal with stupid floating point math issues
        if (num > 0)
        {
            return ((double)((int)(num * Util.TRUNCING_NUMBER + .00000001)))/Util.TRUNCING_NUMBER;
        } else if (num < 0)
        {
            return ((double)((int)(num * Util.TRUNCING_NUMBER - .00000001)))/Util.TRUNCING_NUMBER;
        } else
        {
            return ((double)((int)(num * Util.TRUNCING_NUMBER + .00000001)))/Util.TRUNCING_NUMBER;
        }
    }

    /**
     * Truncates a given double to a fixed number of decimal places. The exact number
     * of places is conrolled by the DISPLAY_PRECISION value in the Pebble class.
     * @param num the number to be truncated
     * @return the original number truncated to Pebble.DISPLAY_PRECISION places
     * @see Pebble
     */
    public static double truncForDisplayNumber (double num, int precision)
    {
        double tn = Math.pow (10.0,(double)precision);
        // the .000...0001 mess is to deal with stupid floating point math issues
        if (num > 0)
        {
            return ((double)((int)(num * tn + .00000001)))/tn;
        } else if (num < 0)
        {
            return ((double)((int)(num * tn - .00000001)))/tn;
        } else
        {
            return ((double)((int)(num * tn + .00000001)))/tn;
        }
    }

    /**
     * Truncates a given double to a fixed number of decimal places. The exact number
     * of places is conrolled by the DISPLAY_PRECISION value in the Pebble class.
     * @param num the number to be truncated
     * @return the original number truncated to Pebble.DISPLAY_PRECISION places
     * @see Pebble
     */
    public static String truncForDisplay (String numAsString, int places)
    {
        return Util.truncTextDecimal (numAsString, places);
    }
    /**
     * Truncates a given double to a fixed number of decimal places. The exact number
     * of places is conrolled by the DISPLAY_PRECISION value in the Pebble class.
     * @param num the number to be truncated
     * @return the original number truncated to Pebble.DISPLAY_PRECISION places
     * @see Pebble
     */
    public static String truncForDisplay (String numAsString)
    {
        return Util.truncTextDecimal (numAsString, Util.DISPLAY_PRECISION);
    }

    /**
     * Truncates a given double to a fixed number of decimal places. The exact number
     * of places is conrolled by the DISPLAY_PRECISION value in the Pebble class.
     * @param num the number to be truncated
     * @return the original number truncated to Pebble.DISPLAY_PRECISION places
     * @see Pebble
     */
    public static String truncForDisplay (double numAsDouble)
    {
        if (numAsDouble > 0) {
            numAsDouble += .0001;
        } else if (numAsDouble < 0) {
            numAsDouble -= .0001;
        }
        return Util.truncTextDecimal (Double.toString(numAsDouble), Util.DISPLAY_PRECISION);
    }

    /**
     * Truncates a given double to a fixed number of decimal places. The exact number
     * of places is conrolled by the DISPLAY_PRECISION value in the Pebble class.
     * @param num the number to be truncated
     * @param places how many digits after the decimal to show
     * @return the original number truncated to Pebble.DISPLAY_PRECISION places
     * @see Pebble
     */
    public static String truncForDisplay (double numAsDouble, int places)
    {
        if (numAsDouble > 0) {
            numAsDouble += .00000001;
        } else if (numAsDouble < 0) {
            numAsDouble -= .00000001;
        }
        return Util.truncTextDecimal (Double.toString(numAsDouble), places);
    }
    
    /**
     * A function to return the last (rightmost) N characters of a string. E.g.
     * rightmost("abcde",3) -> "cde"
     * @param s The string
     * @param numChars the number of characters to return
     * @return the string that's the rightmost (last) numChars characters of the string s
     * E.g. rightmost("abcde",3) -> "cde"
     */
    public static String rightmost(String s, int numChars)
    {
        return s.substring (s.length ()-numChars,s.length ());
    }

    /**
     * get the extension of the given file - copied from java.sun.com example
     * @param f The File of which the extension is desired
     * @return the extension of the file, defined as the substring from the rightmost "." in
     * the file name ot the end of the file name.
     */
    public static String getExtension(File f)
    {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }

    /**
     * Checks whether the points specified are l.t.e. dist apart
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param dist
     * @return true if points are less than dist apart, false otherwise
     */
    public static boolean pointsWithin(int x1, int y1, int x2, int y2,int dist)
    {
        return (Math.pow(x2 - x1,2) + Math.pow(y2 - y1,2)) <= Math.pow(dist,2);
    }

    /**
     * returns the HTML-stype hex string for the given color. That is, #rrggbb.
     * 
     * @param c the color for which a hex string is provided
     * @return
     */
    public static String getColorHexString(Color c) {
        String r = Integer.toString(c.getRed(), 16);
        if (r.length() < 2) { r = "0"+r; }
        String g = Integer.toString(c.getGreen(), 16);
        if (g.length() < 2) { g = "0"+g; }
        String b = Integer.toString(c.getBlue(), 16);
        if (b.length() < 2) { b = "0"+b; }

        return "#"+r+g+b;
    }

    /**
     * 
     * @param s an HTML-style representation of a color in RGB space - e.g. #ff0000 is red
     * @return a Color that the string represents
     */
    public static Color getColorFromHexString(String s) {
        return Color.decode(s);
    }
    
    /**
     * Gets a lighter version of a color, but never lighter than 245,245,245
     *
     * @param initColor
     * @param factor a number between 0 and 1, the degree to which the color is lightened
     * @return
     */
    public static Color getLighterColor(Color initColor, double factor)
    {
        int max = 230;
        int iRed = initColor.getRed();
        int iGreen = initColor.getGreen();
        int iBlue = initColor.getBlue();

        iRed += (int)((255-iRed) * factor);
        iGreen += (int)((255-iGreen) * factor);
        iBlue += (int)((255-iBlue) * factor);

        return new Color(iRed < max ? iRed : max, iGreen < max ? iGreen : max,iBlue < max ? iBlue : max);
    }

    /**
     * Gets a lighter version of a color, but never lighter than 245,245,245
     *
     * @param initColor
     * @param factor a number between 0 and 1, the degree to which the color is lightened
     * @return
     */
    public static Color getLighterColorByStep(Color initColor, int step)
    {
        int max = 230;
        int iRed = initColor.getRed() + step;
        int iGreen = initColor.getGreen() + step;
        int iBlue = initColor.getBlue() + step;



        return new Color(iRed < max ? iRed : max, iGreen < max ? iGreen : max,iBlue < max ? iBlue : max);
    }

    /**
     * Gets the average value of the color - the avg of R, G, and B
     */
    public static double getAvgColorValue(Color c)
    {
        double red = c.getRed();
        double green = c.getGreen();
        double blue = c.getBlue();

        return (red+green+blue)/3.0;
    }

    /**
     * Given a color, returns either black or white, which ever contrasts best with that color for writing easily visible text on it
     * @param c
     * @return
     */
    public static Color getContrastingTextColor(Color c) {
        if (Util.getAvgColorValue(c) < 126.0) {
            return Color.WHITE;
        } else {
            return Color.BLACK;
        }
    }

    /**
     * converts degrees (int) to radians (double)
     * @param deg angle measurement in degrees (integer)
     * @return
     */
    public static double toRadians(double deg)
    {
        return (deg/180.0) * Math.PI;
    }

    /**
     * converts radians (double) to degrees (int)
     * @param rad angle measurement in radians (double)
     * @return
     */
    public static double toDegrees(double rad)
    {
        return (rad/Math.PI)*180.0;
    }

    /**
     * Makes sure the text in a text field is a string that can be read by ParseDouble and is truncated to the default number of decimal places
     * @param tf
     */
    public static void sanitizeForDoubleNumberFormat(javax.swing.JTextField tf) {
        Util.sanitizeForDoubleNumberFormat(tf, Util.DISPLAY_PRECISION);
    }
    /**
     * Makes sure the text in a text field is a string that can be read by ParseDouble and is truncated to the given number of decimal places
     * @param tf
     * @param precision the number of places after the decimal place
     */
    public static void sanitizeForDoubleNumberFormat(javax.swing.JTextField tf,  int precision) {
        int origCaretPosition = tf.getCaretPosition();
        String origText = tf.getText();
        String newText = Util.sanitizeForDoubleNumberFormat(origText,precision);
        tf.setText(newText);
        if (origCaretPosition > newText.length()) {
            tf.setCaretPosition(newText.length());
        } else
        if ((! newText.equalsIgnoreCase(origText)) && (origCaretPosition > 0)) {
            tf.setCaretPosition(origCaretPosition-1);
        } else {
            tf.setCaretPosition(origCaretPosition);
        }
    }
    
    /**
     * Makes sure the string can be read by ParseDouble (strips out offending chars
     * @param s
     * @param precision the number of places after the decimal place
     */
    public static String sanitizeForDoubleNumberFormat(String s,  int precision) {
        String t = s.replaceAll("[\\S\\s&&[^\\d\\.\\-]]", ""); // strip out all except numbers, ., and -
        if (t.indexOf(".") <0) { t += ".0"; } // tack a .0 on the end of bare integers
        boolean isNegative = t.startsWith("-");
        t = t.replaceAll("[\\S\\s&&[^\\d\\.]]", ""); // remove all dashes
        int decimalPointPos = t.indexOf(".");
        if (decimalPointPos > -1) {
            t = t.replaceAll("[\\S\\s&&[^\\d]]", ""); // remove all .'s
            t = t.substring(0, decimalPointPos)+"."+t.substring(decimalPointPos);
        }
        while (t.startsWith("0")) { // huh - tried to use t.replaceAll("/^0*/",""), but it completely failed, as did every other attempted replaceAll thing.. I suspect some pattern caching issue
            t = t.substring(1); // chop the front character, the leading 0
        }
        if (t.startsWith(".")) { t = "0" + t; } // add a leading 0 if it starts with a .
        t=((isNegative && (t.matches(".*[1-9].*")))?"-":"")+t;

        return Util.truncForDisplay(t, precision);
    }
  
    /**
     * 
     * @param origValue
     * @param evt
     * @param increment
     * @return
     */
    public static double adjustFieldValue(double origValue, double increment)
    {
        double newValue = origValue;
        newValue =  Util.truncForDisplayNumber (origValue + increment);
        return newValue;
    }
    
    /**
     * 
     * @param origValue
     * @param increment
     * @param precision
     * @return
     */
    public static String adjustedFieldValue(String origString, double increment, int precision)
    {
        String stringValue = sanitizeForDoubleNumberFormat(origString,precision);
        double origValue = Double.parseDouble(stringValue);
        double newValue = origValue + increment;
        return String.valueOf(Util.truncForDisplay(newValue,precision));
    }

    /**
     * 
     * @param origString
     * @param increment
     * @return 
     */
    public static String adjustedFieldValue(String origString, double increment)
    {
        return Util.adjustedFieldValue(origString, increment, Util.DISPLAY_PRECISION);
    }
    /**
     * 
     * @param tf
     * @param vc 
     */
    public static void fieldValueUp(JTextField tf, ValueConstrainer vc)
    {
        String stringValue = sanitizeForDoubleNumberFormat(tf.getText(), vc.getDisplayPrecision());
        double newValue = vc.up(Double.parseDouble(stringValue));
        tf.setText(Util.truncForDisplay(newValue, vc.getDisplayPrecision()));
    }
    /**
     * 
     * @param tf
     * @param vc 
     */
    public static void fieldValueDown(JTextField tf, ValueConstrainer vc)
    {
        String stringValue = sanitizeForDoubleNumberFormat(tf.getText(), vc.getDisplayPrecision());
        double newValue = vc.down(Double.parseDouble(stringValue));
        tf.setText(Util.truncForDisplay(newValue, vc.getDisplayPrecision()));
    }

    /**
     * dump a to-do messate to stdout, with a note of where the to do call is
     * @param s 
     */
    public static void todo(String s) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        String callerInfo = stackTraceElements[2].getFileName() + " " + stackTraceElements[2].getMethodName() + " - line "+ stackTraceElements[2].getLineNumber();
        System.out.println("TODO ("+callerInfo+"): "+s);
    }
    
    /**
     * dump a message to stdout
     * @param s 
     */
    public static void debugOut(String s) {
        System.out.println("DEBUG: "+s);
    }
}
