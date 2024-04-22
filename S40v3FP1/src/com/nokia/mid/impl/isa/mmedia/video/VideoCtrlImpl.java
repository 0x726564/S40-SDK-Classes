package com.nokia.mid.impl.isa.mmedia.video;

import com.nokia.mid.impl.isa.mmedia.BasicPlayer;
import com.nokia.mid.impl.isa.mmedia.ParsedLocator;
import com.nokia.mid.impl.isa.mmedia.control.Switchable;
import com.nokia.mid.impl.isa.ui.DisplayAccess;
import com.nokia.mid.impl.isa.ui.InitJALM;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Item;
import javax.microedition.media.MediaException;
import javax.microedition.media.control.GUIControl;
import javax.microedition.media.control.VideoControl;

public class VideoCtrlImpl extends Switchable implements GUIControl, VideoControl {
   private static final String LOCATOR_WIDTH = "width";
   private static final String LOCATOR_HEIGHT = "height";
   private static final String LOCATOR_QUALITY = "quality";
   private static final String LOCATOR_ENCODING = "encoding";
   private static final int MAX_UNSIGNED_INT = 32767;
   private static final byte PHOTO_MAX_QUALITY = 100;
   private static final byte PHOTO_MIN_QUALITY = 1;
   private int mode = -1;
   private Canvas canvas;
   private MMItemAccessor mmItem;
   private int dx;
   private int dy;
   private int dw = 128;
   private int dh = 96;
   private int actualDw = 128;
   private int actualDh = 96;
   private int scaledW;
   private int scaledH;
   private int srcW = 128;
   private int srcH = 96;
   private boolean visible;
   private VideoOutImpl nVidOut;
   private boolean fullScreen;
   private RecordCtrlImpl recordCtrl;
   private static final int DEF_W = 128;
   private static final int DEF_H = 96;

   public VideoCtrlImpl(BasicPlayer var1, VideoOutImpl var2) {
      this.player = var1;
      this.nVidOut = var2;
   }

   public int getDisplayHeight() {
      this.checkState();
      return this.actualDh;
   }

   public int getDisplayWidth() {
      this.checkState();
      return this.actualDw;
   }

   public int getDisplayX() {
      return this.mode == 0 ? this.mmItem.getDisplayX() : this.dx;
   }

   public int getDisplayY() {
      return this.mode == 0 ? this.mmItem.getDisplayY() : this.dy;
   }

   public byte[] getSnapshot(String var1) throws MediaException {
      this.checkState();
      RecordCtrlImpl.checkSnapshotSecurity();
      if (this.recordCtrl == null) {
         throw new MediaException("Only capture://image locator supports getSnapshot()");
      } else {
         return this.parseParamsAndGetSnapshot(var1);
      }
   }

   public int getSourceWidth() {
      return this.srcW;
   }

   public int getSourceHeight() {
      return this.srcH;
   }

   public synchronized Object initDisplayMode(int var1, Object var2) {
      if (this.mode != -1) {
         throw new IllegalStateException("Mode already set");
      } else {
         switch(var1) {
         case 0:
            if (var2 != null && (!(var2 instanceof String) || !var2.equals("javax.microedition.lcdui.Item"))) {
               throw new IllegalArgumentException("Container invalid");
            }

            this.mmItem = (MMItemAccessor)this.getItem();
            this.visible = true;
            break;
         case 1:
            if (!(var2 instanceof Canvas)) {
               throw new IllegalArgumentException("Container must be a Canvas");
            }

            this.canvas = (Canvas)var2;
            break;
         default:
            throw new IllegalArgumentException("Unsupported mode");
         }

         this.mode = var1;
         synchronized(this.player) {
            if (this.player.isActive()) {
               this.activate();
            }
         }

         return this.mmItem;
      }
   }

   public synchronized void setDisplayFullScreen(boolean var1) throws MediaException {
      this.checkState();
      if (this.fullScreen != var1) {
         this.fullScreen = var1;
         this.setDisplaySize(this.dw, this.dh);
      }

   }

   public synchronized void setDisplayLocation(int var1, int var2) {
      this.checkState();
      if (this.mode == 1 && (var1 != this.dx || var2 != this.dy)) {
         this.dx = var1;
         this.dy = var2;
         synchronized(this.player) {
            if (this.player.isActive()) {
               this.updateVideoDisplayParams();
            }
         }
      }

   }

   public synchronized void setDisplaySize(int var1, int var2) throws MediaException {
      this.checkState();
      if (var1 >= 1 && var2 >= 1) {
         this.dw = var1;
         this.dh = var2;
         int var3;
         int var4;
         if (this.mode == 0) {
            var3 = this.fullScreen ? this.mmItem.getMaxWidth() : var1;
            var4 = this.fullScreen ? this.mmItem.getMaxHeight() : var2;
         } else {
            var3 = this.fullScreen ? this.canvas.getWidth() : var1;
            var4 = this.fullScreen ? this.canvas.getHeight() : var2;
         }

         if (this.actualDw != var3 || this.actualDh != var4) {
            this.actualDw = var3;
            this.actualDh = var4;
            this.attemptResize();
            this.player.serializeEvent(6, -1L);
         }

      } else {
         throw new IllegalArgumentException("Invalid size");
      }
   }

   public synchronized void setVisible(boolean var1) {
      this.checkState();
      if (this.visible != var1) {
         this.visible = var1;
         this.reportVideoState(var1);
      }

   }

   public void setSnapshotSupport(RecordCtrlImpl var1) {
      this.recordCtrl = var1;
   }

   public void activate() {
      if (this.mode == 0) {
         this.getSourceDimensions();
         this.attemptResize();
         this.reportVideoState(this.visible);
      } else if (this.mode == 1) {
         this.getSourceDimensions();
         this.attemptResize();
      }

      if (this.recordCtrl != null) {
         this.recordCtrl.activate();
      }

   }

   public void deactivate() {
      if (this.mode == 0) {
         this.mmItem.showIcon(0, 2);
      } else if (this.mode == 1) {
         this.reportVideoState(false);
      }

      if (this.recordCtrl != null) {
         this.recordCtrl.deactivate();
      }

   }

   private void checkState() {
      if (this.mode == -1) {
         throw new IllegalStateException("initDisplayMode not called yet");
      }
   }

   private Item getItem() {
      DisplayAccess var1 = InitJALM.s_getMIDletAccessor().getDisplayAccessor();
      return var1.createMMItem();
   }

   private void updateVideoDisplayParams() {
      int var1 = this.dx + (this.actualDw - this.scaledW) / 2;
      int var2 = this.dy + (this.actualDh - this.scaledH) / 2;
      int[] var3 = new int[]{this.dx, this.dy, this.actualDw, this.actualDh};
      nSetVideoDisplayParams(this.player.getPlayerId(), var1, var2, var3);
      this.reportVideoState(this.visible);
   }

   private void reportVideoState(boolean var1) {
      synchronized(this.player) {
         if (this.player.isActive()) {
            if (this.mode == 0) {
               if (var1) {
                  this.mmItem.showIcon(this.player.getPlayerId(), 0);
               } else {
                  this.mmItem.showIcon(this.player.getPlayerId(), 1);
               }
            } else if (this.mode == 1) {
               DisplayAccess var3 = InitJALM.s_getMIDletAccessor().getDisplayAccessor();
               var3.showCanvasVideo(this.canvas, this.player.getPlayerId(), var1, this.dx, this.dy, this.actualDw, this.actualDh);
            }
         }

      }
   }

   private byte[] parseParamsAndGetSnapshot(String var1) throws MediaException {
      int var2 = -1;
      int var3 = -1;
      int var4 = -1;
      String var5 = null;
      if (var1 != null && var1.length() > 0) {
         ParsedLocator var6 = new ParsedLocator("capture://video?" + var1);
         var2 = var6.getParamValueAsInt("width", 1, 32767, -1);
         var3 = var6.getParamValueAsInt("height", 1, 32767, -1);
         var4 = var6.getParamValueAsInt("quality", 1, 100, -1);
         var5 = var6.getParamValueAsString("encoding");
      }

      if (var2 == -1 && var3 > 0 || var2 > 0 && var3 == -1) {
         throw new MediaException("Both parameters (width and height) required.");
      } else {
         if (var5 != null) {
            String var7 = System.getProperty("video.snapshot.encodings");
            var7 = var7.substring(var7.lastIndexOf(61) + 1);
            if (!var5.equals(var7)) {
               throw new MediaException("Invalid encoding: " + var5);
            }
         }

         return this.recordCtrl.getSnapshot(var2, var3, (byte)var4);
      }
   }

   private void attemptResize() {
      int[] var1 = new int[2];
      nSetScaledSize(this.player.getPlayerId(), this.actualDw, this.actualDh, var1);
      this.scaledW = var1[0];
      this.scaledH = var1[1];
      if (this.mode == 0) {
         this.mmItem.init(this.scaledW, this.scaledH);
         this.mmItem.setDisplaySize(this.actualDw, this.actualDh);
      } else if (this.mode == 1) {
         synchronized(this.player) {
            if (this.player.isActive()) {
               this.updateVideoDisplayParams();
            }
         }
      }

   }

   private void getSourceDimensions() {
      int[] var1 = new int[]{this.srcW, this.srcH};
      nGetVideoOriginalSize(this.player.getPlayerId(), var1);
      if (var1[0] != this.srcW || var1[1] != this.srcH) {
         this.srcW = var1[0];
         this.srcH = var1[1];
         this.player.serializeEvent(6, -1L);
      }

   }

   private static native void nSetVideoDisplayParams(int var0, int var1, int var2, int[] var3);

   private static native void nSetScaledSize(int var0, int var1, int var2, int[] var3);

   private static native void nGetVideoOriginalSize(int var0, int[] var1);
}
