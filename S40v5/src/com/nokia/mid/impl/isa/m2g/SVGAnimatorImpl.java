package com.nokia.mid.impl.isa.m2g;

import javax.microedition.m2g.SVGAnimator;
import javax.microedition.m2g.SVGEventListener;
import javax.microedition.m2g.SVGImage;

public class SVGAnimatorImpl extends SVGAnimator {
   private SvgCanvas mm;

   public static SVGAnimator createAnimatorImpl(SVGImage var0) {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         return new SVGAnimatorImpl(var0);
      }
   }

   public static SVGAnimator createAnimatorImpl(SVGImage var0, String var1) {
      if (var0 == null) {
         throw new NullPointerException();
      } else if (var1 != null && !var1.equals("javax.microedition.lcdui.Canvas")) {
         throw new IllegalArgumentException();
      } else {
         return createAnimatorImpl(var0);
      }
   }

   public void setSVGEventListener(SVGEventListener var1) {
      this.mm.setEventListener(var1);
   }

   public void setTimeIncrement(float var1) {
      if (var1 <= 0.0F) {
         throw new IllegalArgumentException();
      } else {
         this.mm.setTimeIncrement(var1);
      }
   }

   public float getTimeIncrement() {
      return this.mm.getTimeIncrement();
   }

   public void play() {
      if (this.mm.W()) {
         throw new IllegalStateException();
      } else {
         this.mm.play();
      }
   }

   public void pause() {
      if (!this.mm.W()) {
         throw new IllegalStateException();
      } else {
         this.mm.pause();
      }
   }

   public void stop() {
      if (this.mm.isStopped()) {
         throw new IllegalStateException();
      } else {
         this.mm.stop();
      }
   }

   public Object getTargetComponent() {
      return this.mm;
   }

   public void invokeAndWait(Runnable var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (this.mm.isStopped()) {
         throw new IllegalStateException();
      } else {
         var1.run();
      }
   }

   public void invokeLater(Runnable var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (this.mm.isStopped()) {
         throw new IllegalStateException();
      } else {
         (new Thread(var1)).start();
      }
   }

   private SVGAnimatorImpl(SVGImage var1) {
      this.mm = new SvgCanvas(false, var1);
   }
}
