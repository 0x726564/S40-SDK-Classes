package com.nokia.mid.impl.isa.m2g;

import javax.microedition.m2g.SVGAnimator;
import javax.microedition.m2g.SVGEventListener;
import javax.microedition.m2g.SVGImage;

public class SVGAnimatorImpl extends SVGAnimator {
   private SvgCanvas svgCanvas;

   public static SVGAnimator createAnimatorImpl(SVGImage svgImage) {
      if (svgImage == null) {
         throw new NullPointerException();
      } else {
         return new SVGAnimatorImpl(svgImage);
      }
   }

   public static SVGAnimator createAnimatorImpl(SVGImage svgImage, String componentBaseClass) {
      if (svgImage == null) {
         throw new NullPointerException();
      } else if (componentBaseClass != null && !componentBaseClass.equals("javax.microedition.lcdui.Canvas")) {
         throw new IllegalArgumentException();
      } else {
         return createAnimatorImpl(svgImage);
      }
   }

   public void setSVGEventListener(SVGEventListener svgEventListener) {
      this.svgCanvas.setEventListener(svgEventListener);
   }

   public void setTimeIncrement(float timeIncrement) {
      if (timeIncrement <= 0.0F) {
         throw new IllegalArgumentException();
      } else {
         this.svgCanvas.setTimeIncrement(timeIncrement);
      }
   }

   public float getTimeIncrement() {
      return this.svgCanvas.getTimeIncrement();
   }

   public void play() {
      if (this.svgCanvas.isPlaying()) {
         throw new IllegalStateException();
      } else {
         this.svgCanvas.play();
      }
   }

   public void pause() {
      if (!this.svgCanvas.isPlaying()) {
         throw new IllegalStateException();
      } else {
         this.svgCanvas.pause();
      }
   }

   public void stop() {
      if (this.svgCanvas.isStopped()) {
         throw new IllegalStateException();
      } else {
         this.svgCanvas.stop();
      }
   }

   public Object getTargetComponent() {
      return this.svgCanvas;
   }

   public void invokeAndWait(Runnable runnable) {
      if (runnable == null) {
         throw new NullPointerException();
      } else if (this.svgCanvas.isStopped()) {
         throw new IllegalStateException();
      } else {
         runnable.run();
      }
   }

   public void invokeLater(Runnable runnable) {
      if (runnable == null) {
         throw new NullPointerException();
      } else if (this.svgCanvas.isStopped()) {
         throw new IllegalStateException();
      } else {
         Thread t = new Thread(runnable);
         t.start();
      }
   }

   private SVGAnimatorImpl(SVGImage newImage) {
      this.svgCanvas = new SvgCanvas(false, newImage);
   }
}
