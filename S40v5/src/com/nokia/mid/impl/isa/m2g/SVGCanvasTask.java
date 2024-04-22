package com.nokia.mid.impl.isa.m2g;

import java.lang.ref.WeakReference;
import java.util.TimerTask;

class SVGCanvasTask extends TimerTask {
   private WeakReference Z;

   public SVGCanvasTask(SvgCanvas var1) {
      this.Z = new WeakReference(var1);
   }

   public void run() {
      SvgCanvas var1;
      if ((var1 = (SvgCanvas)this.Z.get()) != null) {
         var1.X();
         var1.repaint();
      } else {
         this.cancel();
      }
   }
}
