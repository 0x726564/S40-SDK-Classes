package com.nokia.mid.impl.isa.m2g;

import java.lang.ref.WeakReference;
import java.util.TimerTask;

class SVGCanvasTask extends TimerTask {
   private WeakReference weakSvgCanvas;

   public SVGCanvasTask(SvgCanvas var1) {
      this.weakSvgCanvas = new WeakReference(var1);
   }

   public void run() {
      SvgCanvas var1 = (SvgCanvas)this.weakSvgCanvas.get();
      if (var1 != null) {
         var1.increaseCurrentTime();
         var1.repaint();
      } else {
         this.cancel();
      }

   }
}
