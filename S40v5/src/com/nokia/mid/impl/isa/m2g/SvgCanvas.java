package com.nokia.mid.impl.isa.m2g;

import java.util.Timer;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.m2g.SVGEventListener;
import javax.microedition.m2g.SVGImage;
import javax.microedition.m2g.ScalableGraphics;

class SvgCanvas extends GameCanvas {
   private ScalableGraphics iC = ScalableGraphics.createInstance();
   private SVGImage iD;
   private float iE;
   private Timer timer;
   private SVGCanvasTask iF;
   private SVGEventListener iG;
   private int state;

   public SvgCanvas(boolean var1, SVGImage var2) {
      super(false);
      this.iC.setRenderingQuality(2);
      this.iD = var2;
      this.iE = 0.1F;
      this.state = 1;
   }

   public void paint(Graphics var1) {
      var1.setColor(255, 255, 255);
      var1.fillRect(0, 0, this.getWidth(), this.getHeight());
      this.iC.bindTarget(var1);
      this.iC.setTransparency(1.0F);
      this.iC.render(0, 0, this.iD);
      this.iC.releaseTarget();
   }

   public final void play() {
      if (this.state == 1) {
         this.timer = new Timer();
         this.iF = new SVGCanvasTask(this);
         this.timer.scheduleAtFixedRate(this.iF, 100L, (long)((int)(this.iE * 1000.0F)));
      }

      this.state = 2;
   }

   public final void pause() {
      this.state = 3;
   }

   public final void stop() {
      if (this.state != 1) {
         this.timer.cancel();
      }

      this.state = 1;
   }

   public final boolean W() {
      return this.state == 2;
   }

   public final boolean isStopped() {
      return this.state == 1;
   }

   public void setTimeIncrement(float var1) {
      this.iE = var1;
      if (this.state != 1) {
         this.timer.cancel();
         this.timer = new Timer();
         this.iF = new SVGCanvasTask(this);
         this.timer.scheduleAtFixedRate(this.iF, 100L, (long)((int)(this.iE * 1000.0F)));
      }

   }

   public float getTimeIncrement() {
      return this.iE;
   }

   public final void X() {
      if (this.state == 2) {
         this.iD.incrementTime(this.iE);
      }

   }

   public void setEventListener(SVGEventListener var1) {
      this.iG = var1;
   }

   public SVGEventListener getEventListener() {
      return this.iG;
   }

   protected void keyPressed(int var1) {
      if (this.iG != null) {
         this.iG.keyPressed(var1);
      }

   }

   protected void keyReleased(int var1) {
      if (this.iG != null) {
         this.iG.keyReleased(var1);
      }

   }

   protected void pointerPressed(int var1, int var2) {
      if (this.iG != null) {
         this.iG.pointerPressed(var1, var2);
      }

   }

   protected void pointerReleased(int var1, int var2) {
      if (this.iG != null) {
         this.iG.pointerReleased(var1, var2);
      }

   }

   protected void showNotify() {
      if (this.iG != null) {
         this.iG.showNotify();
      }

   }

   protected void hideNotify() {
      if (this.iG != null) {
         this.iG.hideNotify();
      }

   }

   protected void sizeChanged(int var1, int var2) {
      if (this.iG != null) {
         this.iG.sizeChanged(var1, var2);
      }

   }
}
