package com.nokia.mid.impl.isa.m2g;

import java.lang.ref.WeakReference;
import java.util.TimerTask;

class SVGCanvasTask extends TimerTask {
   private WeakReference weakSvgCanvas;

   public SVGCanvasTask(SvgCanvas newCanvas) {
      this.weakSvgCanvas = new WeakReference(newCanvas);
   }

   public void run() {
      SvgCanvas svgCanvas = (SvgCanvas)this.weakSvgCanvas.get();
      if (svgCanvas != null) {
         svgCanvas.increaseCurrentTime();
         svgCanvas.repaint();
      } else {
         this.cancel();
      }

   }
}
