package com.nokia.mid.impl.isa.amms.audio;

import javax.microedition.amms.control.audio3d.OrientationControl;

public class OrientationControlSpectatorImpl implements OrientationControl {
   private int[] angles = new int[]{0, 0, 0};
   private int[] uV = new int[]{0, 1000, 0};
   private int[] fV = new int[]{0, 0, -1000};

   public OrientationControlSpectatorImpl() {
      synchronized(SpectatorImpl.specLock) {
         nActivate();
      }
   }

   public synchronized int[] getOrientationVectors() {
      int[] res = new int[6];
      System.arraycopy(this.fV, 0, res, 0, 3);
      System.arraycopy(this.uV, 0, res, 3, 3);
      return res;
   }

   public synchronized void setOrientation(int[] frontVector, int[] aboveVector) throws IllegalArgumentException {
      if (frontVector != null && aboveVector != null) {
         if (frontVector.length == 3 && aboveVector.length == 3) {
            if (!this.isZeroVector(frontVector) && !this.isZeroVector(aboveVector)) {
               double[] aVt = new double[3];
               double[] fVt = new double[]{(double)frontVector[0], (double)frontVector[1], (double)frontVector[2]};
               aVt[0] = (double)aboveVector[0];
               aVt[1] = (double)aboveVector[1];
               aVt[2] = (double)aboveVector[2];
               normalize(fVt, fVt);
               normalize(aVt, aVt);
               double[] rVt = this.crossProduct(aVt, fVt);
               double[] uVt = this.crossProduct(fVt, rVt);
               if (this.isZeroVector(rVt)) {
                  throw new IllegalArgumentException("Parallel vectors.");
               } else {
                  normalize(uVt, uVt);
                  this.fV[0] = (int)(fVt[0] * 1000.0D);
                  this.fV[1] = (int)(fVt[1] * 1000.0D);
                  this.fV[2] = (int)(fVt[2] * 1000.0D);
                  this.uV[0] = (int)(uVt[0] * 1000.0D);
                  this.uV[1] = (int)(uVt[1] * 1000.0D);
                  this.uV[2] = (int)(uVt[2] * 1000.0D);
                  synchronized(SpectatorImpl.specLock) {
                     nConvertVectorsToSpherical(this.fV, this.uV, this.angles);
                     nApplyChanges(this.angles[0], this.angles[1], this.angles[2]);
                  }
               }
            } else {
               throw new IllegalArgumentException("Vectors cannot be zero.");
            }
         } else {
            throw new IllegalArgumentException("Vector length must be 3!");
         }
      } else {
         throw new IllegalArgumentException("Null vector(s).");
      }
   }

   public synchronized void setOrientation(int heading, int pitch, int roll) {
      this.angles[0] = heading;
      this.angles[1] = pitch;
      this.angles[2] = roll;
      synchronized(SpectatorImpl.specLock) {
         nConvertSphericalToVectors(this.fV, this.uV, this.angles);
         nApplyChanges(this.angles[0], this.angles[1], this.angles[2]);
      }
   }

   private double[] crossProduct(double[] v2, double[] v1) {
      double[] res = new double[]{v1[1] * v2[2] - v1[2] * v2[1], v1[2] * v2[0] - v2[2] * v1[0], v1[0] * v2[1] - v1[1] * v2[0]};
      return res;
   }

   private boolean isZeroVector(int[] v) {
      return v[0] == 0 && v[1] == 0 && v[2] == 0;
   }

   private boolean isZeroVector(double[] v) {
      return length(v) < 0.001D;
   }

   private static double length(double[] v) {
      return Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
   }

   private static void normalize(double[] v, double[] res) {
      double l = length(v);
      res[0] = v[0] / l;
      res[1] = v[1] / l;
      res[2] = v[2] / l;
   }

   private static native void nActivate();

   private static native void nApplyChanges(int var0, int var1, int var2);

   private static native void nConvertVectorsToSpherical(int[] var0, int[] var1, int[] var2);

   private static native void nConvertSphericalToVectors(int[] var0, int[] var1, int[] var2);
}
