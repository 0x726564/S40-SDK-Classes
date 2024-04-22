package com.nokia.mid.impl.isa.amms.audio;

import javax.microedition.amms.control.audio3d.OrientationControl;

public class OrientationControlSpectatorImpl implements OrientationControl {
   private int[] fY = new int[]{0, 0, 0};
   private int[] fZ = new int[]{0, 1000, 0};
   private int[] ga = new int[]{0, 0, -1000};

   public OrientationControlSpectatorImpl() {
      synchronized(SpectatorImpl.specLock) {
         nActivate();
      }
   }

   public synchronized int[] getOrientationVectors() {
      int[] var1 = new int[6];
      System.arraycopy(this.ga, 0, var1, 0, 3);
      System.arraycopy(this.fZ, 0, var1, 3, 3);
      return var1;
   }

   public synchronized void setOrientation(int[] var1, int[] var2) throws IllegalArgumentException {
      if (var1 != null && var2 != null) {
         if (var1.length == 3 && var2.length == 3) {
            if (!a(var1) && !a(var2)) {
               double[] var3 = new double[3];
               double[] var4;
               (var4 = new double[3])[0] = (double)var1[0];
               var4[1] = (double)var1[1];
               var4[2] = (double)var1[2];
               var3[0] = (double)var2[0];
               var3[1] = (double)var2[1];
               var3[2] = (double)var2[2];
               b(var4, var4);
               b(var3, var3);
               double[] var6 = a(var3, var4);
               double[] var7 = a(var4, var6);
               if (a(var6) < 0.001D) {
                  throw new IllegalArgumentException("Parallel vectors.");
               } else {
                  b(var7, var7);
                  this.ga[0] = (int)(var4[0] * 1000.0D);
                  this.ga[1] = (int)(var4[1] * 1000.0D);
                  this.ga[2] = (int)(var4[2] * 1000.0D);
                  this.fZ[0] = (int)(var7[0] * 1000.0D);
                  this.fZ[1] = (int)(var7[1] * 1000.0D);
                  this.fZ[2] = (int)(var7[2] * 1000.0D);
                  synchronized(SpectatorImpl.specLock) {
                     nConvertVectorsToSpherical(this.ga, this.fZ, this.fY);
                     nApplyChanges(this.fY[0], this.fY[1], this.fY[2]);
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

   public synchronized void setOrientation(int var1, int var2, int var3) {
      this.fY[0] = var1;
      this.fY[1] = var2;
      this.fY[2] = var3;
      synchronized(SpectatorImpl.specLock) {
         nConvertSphericalToVectors(this.ga, this.fZ, this.fY);
         nApplyChanges(this.fY[0], this.fY[1], this.fY[2]);
      }
   }

   private static double[] a(double[] var0, double[] var1) {
      double[] var2;
      (var2 = new double[3])[0] = var1[1] * var0[2] - var1[2] * var0[1];
      var2[1] = var1[2] * var0[0] - var0[2] * var1[0];
      var2[2] = var1[0] * var0[1] - var1[1] * var0[0];
      return var2;
   }

   private static boolean a(int[] var0) {
      return var0[0] == 0 && var0[1] == 0 && var0[2] == 0;
   }

   private static double a(double[] var0) {
      return Math.sqrt(var0[0] * var0[0] + var0[1] * var0[1] + var0[2] * var0[2]);
   }

   private static void b(double[] var0, double[] var1) {
      double var2 = a(var0);
      var1[0] = var0[0] / var2;
      var1[1] = var0[1] / var2;
      var1[2] = var0[2] / var2;
   }

   private static native void nActivate();

   private static native void nApplyChanges(int var0, int var1, int var2);

   private static native void nConvertVectorsToSpherical(int[] var0, int[] var1, int[] var2);

   private static native void nConvertSphericalToVectors(int[] var0, int[] var1, int[] var2);
}
