/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.williams.geoshear2013;

/**
 * holds a set of (double) boundary values to be used to constrain another (double) value
 * NOTE: this is a somewhat fragile class - e.g. it assumes that upper bound > lower bound and will break in exciting ways if that is not the case
 * 
 * @author cwarren
 */
public class ValueConstrainer {
   private double lowerBound;
   private double lowerBoundOverflow;
   private double upperBound;
   private double upperBoundOverflow;
   private double increment;
   private int displayPrecision;
   private double defaultVal;   

   private int constraintType;
   private double span;

   public static int CONSTRAINT_STOP = 1;
   public static int CONSTRAINT_WRAP = 2;

    public ValueConstrainer(double lowerBound, double lowerBoundOverflow, double upperBound, double upperBoundOverflow, double increment, int displayPrecision, double defaultVal) {
        this(lowerBound, lowerBoundOverflow, upperBound, upperBoundOverflow, increment, displayPrecision, defaultVal, ValueConstrainer.CONSTRAINT_STOP);
    }

    public ValueConstrainer(double lowerBound, double lowerBoundOverflow, double upperBound, double upperBoundOverflow, double increment, int displayPrecision, double defaultVal, int constraintType) {
        this.lowerBound = lowerBound;
        this.lowerBoundOverflow = lowerBoundOverflow;
        this.upperBound = upperBound;
        this.upperBoundOverflow = upperBoundOverflow;
        this.increment = increment;
        this.displayPrecision = displayPrecision;
        this.defaultVal = defaultVal;
        this.constraintType = constraintType;
        if (this.constraintType != ValueConstrainer.CONSTRAINT_WRAP) {
            this.constraintType = ValueConstrainer.CONSTRAINT_STOP;
        }
        this.span = this.upperBound-this.lowerBound;
    }
   
   public double up(double base) {
       return this.constrain(base + this.increment);
   }
   public double down(double base) {
       return this.constrain(base - this.increment);
   }
    public double constrain(double base) {
        if (this.constraintType == ValueConstrainer.CONSTRAINT_STOP) {
            if (base < this.lowerBound) {
                return this.lowerBoundOverflow;
            }
            if (base > this.upperBound) {
                return this.upperBoundOverflow;
            }
            return base;
        } else {
            if (base < this.lowerBound) {
                double overflow = this.lowerBound - base;
                while (overflow >this.span) {
                    overflow -= this.span;
                }
                return this.upperBound-overflow;
            }
            if (base > this.upperBound) {
                double overflow = base - this.lowerBound;
                while (overflow >this.span) {
                    overflow -= this.span;
                }
                return this.lowerBound+overflow;
            }
            return base;
        }
    }
   public boolean isOutOfRange(double d) {
       return (d < this.lowerBound) || (d > this.upperBound);
   }

    /**
     * @return the constraintType
     */
    public int getConstraintType() {
        return constraintType;
    }

    /**
     * @param constraintType the constraintType to set
     */
    public void setConstraintType(int constraintType) {
        this.constraintType = constraintType;
        if (this.constraintType != ValueConstrainer.CONSTRAINT_WRAP) {
            this.constraintType = ValueConstrainer.CONSTRAINT_STOP;
        }
    }

    /**
     * @return the lowerBound
     */
    public double getLowerBound() {
        return lowerBound;
    }

    /**
     * @param lowerBound the lowerBound to set
     */
    public void setLowerBound(double lowerBound) {
        this.lowerBound = lowerBound;
        this.span = this.upperBound-this.lowerBound;
    }

    /**
     * @return the lowerBoundOverflow
     */
    public double getLowerBoundOverflow() {
        return lowerBoundOverflow;
    }

    /**
     * @param lowerBoundOverflow the lowerBoundOverflow to set
     */
    public void setLowerBoundOverflow(double lowerBoundOverflow) {
        this.lowerBoundOverflow = lowerBoundOverflow;
    }

    /**
     * @return the upperBound
     */
    public double getUpperBound() {
        return upperBound;
    }

    /**
     * @param upperBound the upperBound to set
     */
    public void setUpperBound(double upperBound) {
        this.upperBound = upperBound;
        this.span = this.upperBound-this.lowerBound;
    }

    /**
     * @return the upperBoundOverflow
     */
    public double getUpperBoundOverflow() {
        return upperBoundOverflow;
    }

    /**
     * @param upperBoundOverflow the upperBoundOverflow to set
     */
    public void setUpperBoundOverflow(double upperBoundOverflow) {
        this.upperBoundOverflow = upperBoundOverflow;
    }

    /**
     * @return the increment
     */
    public double getIncrement() {
        return increment;
    }

    /**
     * @param increment the increment to set
     */
    public void setIncrement(double increment) {
        this.increment = increment;
    }

    /**
     * @return the displayPrecision
     */
    public int getDisplayPrecision() {
        return displayPrecision;
    }

    /**
     * @param displayPrecision the displayPrecision to set
     */
    public void setDisplayPrecision(int displayPrecision) {
        this.displayPrecision = displayPrecision;
    }

    /**
     * @return the defaultVal
     */
    public double getDefaultVal() {
        return defaultVal;
    }

    /**
     * @param defaultVal the defaultVal to set
     */
    public void setDefaultVal(double defaultVal) {
        this.defaultVal = defaultVal;
    }
}
