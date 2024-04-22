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
   private int actualDx;
   private int actualDy;
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

   public VideoCtrlImpl(BasicPlayer player, VideoOutImpl nVidOut) {
      this.player = player;
      this.nVidOut = nVidOut;
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
      return this.mode == 0 ? this.mmItem.getDisplayX() : this.actualDx;
   }

   public int getDisplayY() {
      return this.mode == 0 ? this.mmItem.getDisplayY() : this.actualDy;
   }

   public byte[] getSnapshot(String imageType) throws MediaException {
      this.checkState();
      RecordCtrlImpl.checkSnapshotSecurity();
      if (this.recordCtrl == null) {
         throw new MediaException("Only capture://image locator supports getSnapshot()");
      } else {
         return this.parseParamsAndGetSnapshot(imageType);
      }
   }

   public int getSourceWidth() {
      this.getSourceDimensions(false);
      return this.srcW;
   }

   public int getSourceHeight() {
      this.getSourceDimensions(false);
      return this.srcH;
   }

   public synchronized Object initDisplayMode(int mode, Object container) {
      if (this.mode != -1) {
         throw new IllegalStateException("Mode already set");
      } else {
         switch(mode) {
         case 0:
            if (container != null && (!(container instanceof String) || !container.equals("javax.microedition.lcdui.Item"))) {
               throw new IllegalArgumentException("Container invalid");
            }

            this.mmItem = (MMItemAccessor)this.getItem();
            this.visible = true;
            break;
         case 1:
            if (!(container instanceof Canvas)) {
               throw new IllegalArgumentException("Container must be a Canvas");
            }

            this.canvas = (Canvas)container;
            break;
         default:
            throw new IllegalArgumentException("Unsupported mode");
         }

         this.mode = mode;
         synchronized(this.player) {
            if (this.player.isActive()) {
               this.activate();
            }
         }

         return this.mmItem;
      }
   }

   public synchronized void setDisplayFullScreen(boolean fullScreen) throws MediaException {
      this.checkState();
      if (this.fullScreen != fullScreen) {
         this.fullScreen = fullScreen;
         if (this.mode == 0) {
            if (fullScreen) {
               this.setDisplay(this.actualDx, this.actualDy, this.mmItem.getMaxWidth(), this.mmItem.getMaxHeight());
            } else {
               this.setDisplay(this.actualDx, this.actualDy, this.dw, this.dh);
            }
         } else if (this.mode == 1) {
            if (fullScreen) {
               this.setDisplay(0, 0, this.canvas.getWidth(), this.canvas.getHeight());
            } else {
               this.setDisplay(this.dx, this.dy, this.dw, this.dh);
            }
         }
      }

   }

   public synchronized void setDisplayLocation(int x, int y) {
      this.checkState();
      if (this.mode == 1) {
         this.dx = x;
         this.dy = y;
         if (this.fullScreen) {
            return;
         }

         if (x != this.actualDx || y != this.actualDy) {
            this.actualDx = x;
            this.actualDy = y;
            synchronized(this.player) {
               if (this.player.isActive()) {
                  this.updateVideoDisplayParams();
               }
            }
         }
      }

   }

   public synchronized void setDisplaySize(int width, int height) throws MediaException {
      this.checkState();
      if (width >= 1 && height >= 1) {
         this.dw = width;
         this.dh = height;
         if (!this.fullScreen) {
            this.setDisplay(this.actualDx, this.actualDy, width, height);
         }
      } else {
         throw new IllegalArgumentException("Invalid size");
      }
   }

   private synchronized void setDisplay(int x, int y, int width, int height) throws MediaException {
      if (x != this.actualDx || y != this.actualDy || width != this.actualDw || height != this.actualDh) {
         if (width != this.actualDw || height != this.actualDh) {
            this.player.serializeEvent(6, -1L);
         }

         this.actualDx = x;
         this.actualDy = y;
         this.actualDw = width;
         this.actualDh = height;
         this.attemptResize();
      }

   }

   public synchronized void setVisible(boolean visible) {
      this.checkState();
      if (this.visible != visible) {
         this.visible = visible;
         this.reportVideoState(visible);
      }

   }

   public void setSnapshotSupport(RecordCtrlImpl recordCtrl) {
      this.recordCtrl = recordCtrl;
   }

   public void activate() {
      if (this.mode == 0 || this.mode == 1) {
         this.getSourceDimensions(true);
         this.attemptResize();
         if (this.mode == 0) {
            this.reportVideoState(this.visible);
         }
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
      DisplayAccess da = InitJALM.s_getMIDletAccessor().getDisplayAccessor();
      return da.createMMItem();
   }

   private void updateVideoDisplayParams() {
      int vidOriginX = this.actualDx + (this.actualDw - this.scaledW) / 2;
      int vidOriginY = this.actualDy + (this.actualDh - this.scaledH) / 2;
      int[] visClip = new int[]{this.actualDx, this.actualDy, this.actualDw, this.actualDh};
      nSetVideoDisplayParams(this.player.getPlayerId(), vidOriginX, vidOriginY, visClip);
      this.reportVideoState(this.visible);
   }

   private void reportVideoState(boolean isVisible) {
      synchronized(this.player) {
         if (this.player.isActive()) {
            if (this.mode == 0) {
               if (isVisible) {
                  this.mmItem.showIcon(this.player.getPlayerId(), 0);
               } else {
                  this.mmItem.showIcon(this.player.getPlayerId(), 1);
               }
            } else if (this.mode == 1) {
               DisplayAccess da = InitJALM.s_getMIDletAccessor().getDisplayAccessor();
               da.showCanvasVideo(this.canvas, this.player.getPlayerId(), isVisible, this.actualDx, this.actualDy, this.actualDw, this.actualDh);
            }
         }

      }
   }

   private byte[] parseParamsAndGetSnapshot(String imageType) throws MediaException {
      int width = -1;
      int height = -1;
      int quality = -1;
      String encoding = null;
      if (imageType != null && imageType.length() > 0) {
         ParsedLocator loc = new ParsedLocator("capture://video?" + imageType);
         width = loc.getParamValueAsInt("width", 1, 32767, -1);
         height = loc.getParamValueAsInt("height", 1, 32767, -1);
         quality = loc.getParamValueAsInt("quality", 1, 100, -1);
         encoding = loc.getParamValueAsString("encoding");
      }

      if ((width != -1 || height <= 0) && (width <= 0 || height != -1)) {
         if (encoding != null) {
            String sEnc = System.getProperty("video.snapshot.encodings") + " ";
            boolean isValidEncoding = false;
            int iniIdx = false;
            int endIdx = 0;

            do {
               int iniIdx = sEnc.indexOf(61, endIdx);
               endIdx = iniIdx > endIdx ? sEnc.indexOf(32, iniIdx) : -1;
               if (endIdx > 0) {
                  String enc = sEnc.substring(iniIdx + 1, endIdx);
                  if (enc.equalsIgnoreCase(encoding) || enc.equalsIgnoreCase("image/" + encoding)) {
                     encoding = enc;
                     isValidEncoding = true;
                  }
               }
            } while(endIdx > 0 && !isValidEncoding);

            if (!isValidEncoding) {
               throw new MediaException("Invalid encoding: " + encoding);
            }
         }

         return this.recordCtrl.getSnapshot(width, height, (byte)quality, encoding);
      } else {
         throw new MediaException("Both parameters (width and height) required.");
      }
   }

   private void attemptResize() {
      int[] actualSize = new int[2];
      nSetScaledSize(this.player.getPlayerId(), this.actualDw, this.actualDh, actualSize);
      this.scaledW = actualSize[0];
      this.scaledH = actualSize[1];
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

   private void getSourceDimensions(boolean sendEvent) {
      int[] origSize = new int[]{this.srcW, this.srcH};
      nGetVideoOriginalSize(this.player.getPlayerId(), origSize);
      if (origSize[0] != this.srcW || origSize[1] != this.srcH) {
         this.srcW = origSize[0];
         this.srcH = origSize[1];
         if (sendEvent) {
            this.player.serializeEvent(6, -1L);
         }
      }

   }

   private static native void nSetVideoDisplayParams(int var0, int var1, int var2, int[] var3);

   private static native void nSetScaledSize(int var0, int var1, int var2, int[] var3);

   private static native void nGetVideoOriginalSize(int var0, int[] var1);
}
