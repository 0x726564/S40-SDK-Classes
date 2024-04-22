package com.nokia.mid.impl.isa.m2g;

import java.util.Timer;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.m2g.SVGEventListener;
import javax.microedition.m2g.SVGImage;
import javax.microedition.m2g.ScalableGraphics;

class SvgCanvas extends GameCanvas {
   private ScalableGraphics sg = ScalableGraphics.createInstance();
   private SVGImage svgImage;
   private float deltaTime;
   private Timer timer;
   private SVGCanvasTask task;
   private SVGEventListener eventListener;
   private int state;
   private static final int STATE_STOPPED = 1;
   private static final int STATE_PLAYING = 2;
   private static final int STATE_PAUSED = 3;

   public SvgCanvas(boolean var1, SVGImage var2) {
      super(var1);
      this.sg.setRenderingQuality(2);
      this.svgImage = var2;
      this.deltaTime = 0.1F;
      this.state = 1;
   }

   public void paint(Graphics var1) {
      var1.setColor(255, 255, 255);
      var1.fillRect(0, 0, this.getWidth(), this.getHeight());
      this.sg.bindTarget(var1);
      this.sg.setTransparency(1.0F);
      this.sg.render(0, 0, this.svgImage);
      this.sg.releaseTarget();
   }

   public void play() {
      if (this.state == 1) {
         this.timer = new Timer();
         this.task = new SVGCanvasTask(this);
         this.timer.scheduleAtFixedRate(this.task, 100L, (long)((int)(this.deltaTime * 1000.0F)));
      }

      this.state = 2;
   }

   public void pause() {
      this.state = 3;
   }

   public void stop() {
      if (this.state != 1) {
         this.timer.cancel();
      }

      this.state = 1;
   }

   public boolean isPlaying() {
      return this.state == 2;
   }

   public boolean isPaused() {
      return this.state == 3;
   }

   public boolean isStopped() {
      return this.state == 1;
   }

   public void setTimeIncrement(float var1) {
      this.deltaTime = var1;
      if (this.state != 1) {
         this.timer.cancel();
         this.timer = new Timer();
         this.task = new SVGCanvasTask(this);
         this.timer.scheduleAtFixedRate(this.task, 100L, (long)((int)(this.deltaTime * 1000.0F)));
      }

   }

   public float getTimeIncrement() {
      return this.deltaTime;
   }

   public void increaseCurrentTime() {
      if (this.state == 2) {
         this.svgImage.incrementTime(this.deltaTime);
      }

   }

   public void setEventListener(SVGEventListener var1) {
      this.eventListener = var1;
   }

   public SVGEventListener getEventListener() {
      return this.eventListener;
   }

   protected void keyPressed(int var1) {
      if (this.eventListener != null) {
         this.eventListener.keyPressed(var1);
      }

   }

   protected void keyReleased(int var1) {
      if (this.eventListener != null) {
         this.eventListener.keyReleased(var1);
      }

   }

   protected void pointerPressed(int var1, int var2) {
      if (this.eventListener != null) {
         this.eventListener.pointerPressed(var1, var2);
      }

   }

   protected void pointerReleased(int var1, int var2) {
      if (this.eventListener != null) {
         this.eventListener.pointerReleased(var1, var2);
      }

   }

   protected void showNotify() {
      if (this.eventListener != null) {
         this.eventListener.showNotify();
      }

   }

   protected void hideNotify() {
      if (this.eventListener != null) {
         this.eventListener.hideNotify();
      }

   }

   protected void sizeChanged(int var1, int var2) {
      if (this.eventListener != null) {
         this.eventListener.sizeChanged(var1, var2);
      }

   }
}
