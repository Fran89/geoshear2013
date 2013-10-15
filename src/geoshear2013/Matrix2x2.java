/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geoshear2013;

import java.awt.geom.AffineTransform;

/**
 * implementation of 2x2 matrices, supporting a number of operations on them
 * 
 * @author cwarren
 */
public class Matrix2x2 {

    
    public double m00;
    public double m01;
    public double m10;
    public double m11;

    /**
     * create a 2x2 identity matrix
     */
    public Matrix2x2() {
        this(1,0,0,1);
    }
        
    /**
     * create a 2x2 identity matrix
     */
    public Matrix2x2(AffineTransform t) {
        this(t.getScaleX(),t.getShearY(),t.getShearX(),t.getScaleY());
    }
    
    /**
     * create a new 2x2 matrix of the form 
     *   m00 m01
     *   m10 m11
     * 
     * @param m00
     * @param m01
     * @param m10
     * @param m11 
     */
    public Matrix2x2(double m00, double m01, double m10, double m11) {
        this.m00 = m00;
        this.m01 = m01;
        this.m10 = m10;
        this.m11 = m11;
    }
    
    public Matrix2x2 clone() {
        return new Matrix2x2(this.m00,this.m01,this.m10,this.m11);
    }
    
    public String toString() {
        return "["+this.m00+" , "+this.m01+" ; "+this.m10+" , "+this.m11+"]";
    }
    
    public boolean equals(Matrix2x2 other) {
        return this.m00==other.m00 && this.m01==other.m01 && this.m10==other.m10 && this.m11==other.m11;
    }
    
    /**
     * flip the matrix around its diagonal
     */
    public void transpose() {
        double tmp = this.m01;
        this.m01 = this.m10;
        this.m10 = tmp;
    }

    /**
     * get a copy of flip the matrix around its diagonal
     */
    public Matrix2x2 transposed() {
        return new Matrix2x2(this.m00, this.m10, this.m01, this.m11);
    }

    /**
     * 
     * @return the determinate of this matrix: m00*m11 - m01*m10
     */
    public double determinate() {
        return this.m00*this.m11 - this.m01*this.m10;
    }
    /**
     * @param other
     * @return a new matrix that is a product of this and the other one
     */
    public Matrix2x2 times(Matrix2x2 other) {
        return new Matrix2x2(
                this.m00*other.m00+this.m01*other.m10,
                this.m00*other.m01+this.m01*other.m11,
                this.m10*other.m00+this.m11*other.m10,
                this.m10*other.m01+this.m11*other.m11
                );
    }
    
    /**
     * performs a single value decomposition on this matrix, generating three
     * matrices u, sig, and v such that this = u * sig * v'
     * NOTE: this algorithm is from http://www.ualberta.ca/~mlipsett/ENGM541/Readings/svd_ellis.pdf
     * 
     * @return three matrices u, sig, and v' such that this = u * sig * v'
     */
    public Matrix2x2[] svd() {
        Matrix2x2[] u_sig_v = new Matrix2x2[3];

/* from http://www.ualberta.ca/~mlipsett/ENGM541/Readings/svd_ellis.pdf
 * 
% [U,SIG,V] = svd2x2(A) finds the SVD of 2x2 matrix A
% where U and V are orthogonal, SIG is diagonal,
% and A=U*SIG*V’

* % Find U such that U*A*A’*U’=diag
Su = A*A’;
phi = 0.5*atan2(Su(1,2)+Su(2,1), Su(1,1)-Su(2,2));
Cphi = cos(phi);
Sphi = sin(phi);
U = [Cphi - Sphi ; Sphi Cphi];
* 
% Find W such that W’*A’*A*W=diag
Sw = A’*A;
theta = 0.5*atan2(Sw(1,2)+Sw(2,1), Sw(1,1)-Sw(2,2));
Ctheta = cos(theta);
Stheta = sin(theta);
W = [Ctheta, -Stheta ; Stheta, Ctheta];
* 
% Find the singular values from U
SUsum = Su(1,1)+Su(2,2);
SUdif = sqrt((Su(1,1)-Su(2,2))ˆ2 + 4*Su(1,2)*Su(2,1));
svals = [sqrt((SUsum+SUdif)/2) sqrt((SUsum-SUdif)/2)];
SIG = diag(svals);
* 
% Find the correction matrix for the right side
S = U’*A*W;
C = diag([sign(S(1,1)) sign(S(2,2))]);
V = W*C;
*/                
        Matrix2x2 Su = this.times(this.transposed());
        double phi = .5 * Math.atan2(Su.m01+Su.m10, Su.m00-Su.m11);
        double Cphi = Math.cos(phi);
        double Sphi = Math.sin(phi);
        u_sig_v[0] = new Matrix2x2(Cphi,-Sphi, Sphi, Cphi);
        
        Matrix2x2 Sw = this.transposed().times(this);
        double theta = .5 * Math.atan2(Sw.m01+Sw.m10, Sw.m00-Sw.m11);
        double Ctheta = Math.cos(theta);
        double Stheta = Math.sin(theta);
        Matrix2x2 W = new Matrix2x2(Ctheta,-Stheta, Stheta, Ctheta);
        
        double SUsum = Su.m00+Su.m11;
        double SUdiff = Math.pow( Math.pow(Su.m00-Su.m11,2) + 4*Su.m01*Su.m10,.5);
        u_sig_v[1] = new Matrix2x2(Math.pow((SUsum+SUdiff)/2,.5), 0, 0, Math.pow((SUsum-SUdiff)/2,.5));
        
        Matrix2x2 S = u_sig_v[0].transposed().times(this).times(W);
        Matrix2x2 C = new Matrix2x2(((S.m00 < 0) ? -1 : 1),0,0,((S.m11 < 0) ? -1 : 1));
        Matrix2x2 V = W.times(C);
        
        u_sig_v[2] = V.transposed();
        
        return u_sig_v;
    }

    public static Matrix2x2[] svdOf(AffineTransform t) {
        Matrix2x2 base = new Matrix2x2(t);
        return base.svd();
    }
    
    /**
     * testing for this class
     */
    public static void main(String[] args) {
        System.out.println("Starting tests...");

        Matrix2x2 A = new Matrix2x2();
        if (A.m00 != 1 || A.m01 != 0 || A.m10 != 0 || A.m11 != 1) {
            System.out.println("fail creation of identity matrix");
        }

        if (! A.toString().equals("[1.0 , 0.0 ; 0.0 , 1.0]")) {
            System.out.println("fail toString: "+A.toString());
        }
        
        Matrix2x2 B = new Matrix2x2(1,1,2,2);
        if (B.m00 != 1 || B.m01 != 1 || B.m10 != 2 || B.m11 != 2) {
            System.out.println("fail creation of valued matrix");
        }
        if ((B.equals(A)) || (! B.equals(B))) {
            System.out.println("fail equality check");
        }
        
        Matrix2x2 C = B.clone();
        if ((! C.equals(B))) {
            System.out.println("fail clone");
        }

        C.transpose();
        if (C.m00 != 1 || C.m01 != 2 || C.m10 != 1 || C.m11 != 2) {
            System.out.println("fail transpose matrix");
        }

        Matrix2x2 D = B.transposed();
        if ((! C.equals(D))) {
            System.out.println("fail new transposed");
        }

        Matrix2x2 E = new Matrix2x2(1,2,3,4);

        Matrix2x2 F = B.times(E);
        if (F.m00 != 4 || F.m01 != 6 || F.m10 != 8 || F.m11 != 12) {
            System.out.println("fail multiply");
        }
        
        if (F.determinate() != 0) {
            System.out.println("fail determinate");
        }
        
        Matrix2x2[] svd = F.svd();
        Matrix2x2 svdcheck = svd[0].times(svd[1]).times(svd[2]);

        if ((Math.abs(svdcheck.m00-F.m00) > .0000001)
                || (Math.abs(svdcheck.m01-F.m01) > .0000001)
                || (Math.abs(svdcheck.m10-F.m10) > .0000001)
                || (Math.abs(svdcheck.m11-F.m11) > .0000001))
        {
            System.out.println("fail svd");
            System.out.println("F: "+F.toString());

            System.out.println("U: "+svd[0].toString());
            System.out.println("S: "+svd[1].toString());
            System.out.println("V': "+svd[2].toString());

            System.out.println("USV': "+svdcheck.toString());
        }
    }
}
