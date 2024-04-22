package com.nokia.mid.impl.isa.mmedia.video;

import com.nokia.mid.impl.isa.mmedia.BasicPlayer;
import com.nokia.mid.impl.isa.mmedia.MediaPrefs;
import com.nokia.mid.impl.isa.mmedia.ParsedLocator;
import com.nokia.mid.impl.isa.mmedia.control.Switchable;
import com.nokia.mid.impl.isa.ui.InitJALM;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Item;
import javax.microedition.media.MediaException;
import javax.microedition.media.control.GUIControl;
import javax.microedition.media.control.VideoControl;

public class VideoCtrlImpl extends Switchable implements GUIControl, VideoControl {
   private int mode = -1;
   private Canvas eo;
   private MMItemAccessor ep;
   private int dx;
   private int dy;
   private int eq;
   private int er;
   private int dw = 128;
   private int es = 96;
   private int et = 128;
   private int eu = 96;
   private int ev;
   private int ew;
   private int ex = 128;
   private int ey = 96;
   private boolean visible;
   private boolean ez;
   private RecordCtrlImpl at;

   public VideoCtrlImpl(BasicPlayer var1, VideoOutImpl var2) {
      this.player = var1;
   }

   public int getDisplayHeight() {
      this.checkState();
      return this.eu;
   }

   public int getDisplayWidth() {
      this.checkState();
      return this.et;
   }

   public int getDisplayX() {
      return this.mode == 0 ? this.ep.getDisplayX() : this.eq;
   }

   public int getDisplayY() {
      return this.mode == 0 ? this.ep.getDisplayY() : this.er;
   }

   public byte[] getSnapshot(String var1) throws MediaException {
      this.checkState();
      MediaPrefs.checkPermission(2, 1);
      if (this.at == null) {
         throw new MediaException("Only capture://image locator supports getSnapshot()");
      } else {
         return this.D(var1);
      }
   }

   public int getSourceWidth() {
      this.h(false);
      return this.ex;
   }

   public int getSourceHeight() {
      this.h(false);
      return this.ey;
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

            this.ep = (MMItemAccessor)this.getItem();
            this.visible = true;
            break;
         case 1:
            if (!(var2 instanceof Canvas)) {
               throw new IllegalArgumentException("Container must be a Canvas");
            }

            this.eo = (Canvas)var2;
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

         return this.ep;
      }
   }

   public synchronized void setDisplayFullScreen(boolean var1) throws MediaException {
      this.checkState();
      if (this.ez != var1) {
         this.ez = var1;
         if (this.mode == 0) {
            if (var1) {
               this.a(this.eq, this.er, this.ep.getMaxWidth(), this.ep.getMaxHeight());
               return;
            }

            this.a(this.eq, this.er, this.dw, this.es);
            return;
         }

         if (this.mode == 1) {
            if (var1) {
               this.a(0, 0, this.eo.getWidth(), this.eo.getHeight());
               return;
            }

            this.a(this.dx, this.dy, this.dw, this.es);
         }
      }

   }

   public synchronized void setDisplayLocation(int var1, int var2) {
      this.checkState();
      if (this.mode == 1) {
         this.dx = var1;
         this.dy = var2;
         if (this.ez) {
            return;
         }

         if (var1 != this.eq || var2 != this.er) {
            this.eq = var1;
            this.er = var2;
            synchronized(this.player) {
               if (this.player.isActive()) {
                  this.J();
               }

               return;
            }
         }
      }

   }

   public synchronized void setDisplaySize(int var1, int var2) throws MediaException {
      this.checkState();
      if (var1 >= 1 && var2 >= 1) {
         this.dw = var1;
         this.es = var2;
         if (!this.ez) {
            this.a(this.eq, this.er, var1, var2);
         }
      } else {
         throw new IllegalArgumentException("Invalid size");
      }
   }

   private synchronized void a(int var1, int var2, int var3, int var4) throws MediaException {
      if (var1 != this.eq || var2 != this.er || var3 != this.et || var4 != this.eu) {
         if (var3 != this.et || var4 != this.eu) {
            this.player.serializeEvent(6, -1L);
         }

         this.eq = var1;
         this.er = var2;
         this.et = var3;
         this.eu = var4;
         this.K();
      }

   }

   public synchronized void setVisible(boolean var1) {
      this.checkState();
      if (this.visible != var1) {
         this.visible = var1;
         this.g(var1);
      }

   }

   public void setSnapshotSupport(RecordCtrlImpl var1) {
      this.at = var1;
   }

   public void activate() {
      if (this.mode == 0 || this.mode == 1) {
         this.h(true);
         this.K();
         if (this.mode == 0) {
            this.g(this.visible);
         }
      }

      if (this.at != null) {
         this.at.activate();
      }

   }

   public void deactivate() {
      if (this.mode == 0) {
         this.ep.showIcon(0, 2);
      } else if (this.mode == 1) {
         this.g(false);
      }

      if (this.at != null) {
         this.at.deactivate();
      }

   }

   private void checkState() {
      if (this.mode == -1) {
         throw new IllegalStateException("initDisplayMode not called yet");
      }
   }

   private Item getItem() {
      return InitJALM.s_getMIDletAccessor().getDisplayAccessor().createMMItem();
   }

   private void J() {
      int var1 = this.eq + (this.et - this.ev) / 2;
      int var2 = this.er + (this.eu - this.ew) / 2;
      int[] var3 = new int[]{this.eq, this.er, this.et, this.eu};
      nSetVideoDisplayParams(this.player.getPlayerId(), var1, var2, var3);
      this.g(this.visible);
   }

   private void g(boolean var1) {
      synchronized(this.player) {
         if (this.player.isActive()) {
            if (this.mode == 0) {
               if (var1) {
                  this.ep.showIcon(this.player.getPlayerId(), 0);
               } else {
                  this.ep.showIcon(this.player.getPlayerId(), 1);
               }
            } else if (this.mode == 1) {
               InitJALM.s_getMIDletAccessor().getDisplayAccessor().showCanvasVideo(this.eo, this.player.getPlayerId(), var1, this.eq, this.er, this.et, this.eu);
            }
         }

      }
   }

   private byte[] D(String var1) throws MediaException {
      int var2 = -1;
      int var3 = -1;
      int var4 = -1;
      String var5 = null;
      if (var1 != null && var1.length() > 0) {
         ParsedLocator var9;
         var2 = (var9 = new ParsedLocator("capture://video?" + var1)).getParamValueAsInt("width", 1, 32767, -1);
         var3 = var9.getParamValueAsInt("height", 1, 32767, -1);
         var4 = var9.getParamValueAsInt("quality", 1, 100, -1);
         var5 = var9.getParamValueAsString("encoding");
      }

      if ((var2 != -1 || var3 <= 0) && (var2 <= 0 || var3 != -1)) {
         if (var5 != null) {
            var1 = System.getProperty("video.snapshot.encodings") + " ";
            boolean var6 = false;
            boolean var7 = false;
            int var8 = 0;

            do {
               int var10;
               int var10000 = (var10 = var1.indexOf(61, var8)) > var8 ? var1.indexOf(32, var10) : -1;
               var8 = var10000;
               String var11;
               if (var10000 > 0 && ((var11 = var1.substring(var10 + 1, var8)).equalsIgnoreCase(var5) || var11.equalsIgnoreCase("image/" + var5))) {
                  var5 = var11;
                  var6 = true;
               }
            } while(var8 > 0 && !var6);

            if (!var6) {
               throw new MediaException("Invalid encoding: " + var5);
            }
         }

         return this.at.a(var2, var3, (byte)var4, var5);
      } else {
         throw new MediaException("Both parameters (width and height) required.");
      }
   }

   private void K() {
      int[] var1 = new int[2];
      nSetScaledSize(this.player.getPlayerId(), this.et, this.eu, var1);
      this.ev = var1[0];
      this.ew = var1[1];
      if (this.mode == 0) {
         this.ep.init(this.ev, this.ew);
         this.ep.setDisplaySize(this.et, this.eu);
      } else if (this.mode == 1) {
         synchronized(this.player) {
            if (this.player.isActive()) {
               this.J();
            }

         }
      }
   }

   private void h(boolean var1) {
      int[] var2 = new int[]{this.ex, this.ey};
      nGetVideoOriginalSize(this.player.getPlayerId(), var2);
      if (var2[0] != this.ex || var2[1] != this.ey) {
         this.ex = var2[0];
         this.ey = var2[1];
         if (var1) {
            this.player.serializeEvent(6, -1L);
         }
      }

   }

   private static native void nSetVideoDisplayParams(int var0, int var1, int var2, int[] var3);

   private static native void nSetScaledSize(int var0, int var1, int var2, int[] var3);

   private static native void nGetVideoOriginalSize(int var0, int[] var1);
}
