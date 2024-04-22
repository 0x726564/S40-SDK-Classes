package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.mmedia.video.MMItemAccessor;
import com.nokia.mid.impl.isa.ui.gdi.Pixmap;
import com.nokia.mid.impl.isa.ui.style.UIStyle;

class MMItemImpl extends Item implements MMItemAccessor {
   private static final int LAYOUT_HMASK = 3;
   private static final int LAYOUT_VMASK = 48;
   private static final int BORDER_W;
   private static final int BORDER_H;
   private static final Pixmap PIXMAP_PLACE_HOLDER;
   private static final Pixmap PIXMAP_BROKEN_VIDEO;
   private static final int MAX_WIDTH;
   private static final int MAX_HEIGHT;
   private static final int MIN_WIDTH;
   private static final int MIN_HEIGHT;
   private int videoX;
   private int videoY;
   private int videoW;
   private int videoH;
   private int displayW;
   private int displayH;
   private Pixmap iconToShow;
   private int playerId;

   MMItemImpl() {
      super((String)null);
      this.videoW = MIN_WIDTH;
      this.videoH = MIN_HEIGHT;
      this.displayW = MIN_WIDTH;
      this.displayH = MIN_HEIGHT;
      this.iconToShow = PIXMAP_PLACE_HOLDER;
   }

   public void init(int var1, int var2) {
      synchronized(Display.LCDUILock) {
         this.videoW = var1;
         this.videoH = var2;
         this.displayW = var1;
         this.displayH = var2;
         this.invalidate();
      }
   }

   public void setDisplaySize(int var1, int var2) {
      synchronized(Display.LCDUILock) {
         if (this.displayW != var1 || this.displayH != var2) {
            this.displayW = var1;
            this.displayH = var2;
            this.invalidate();
         }

      }
   }

   public void showIcon(int var1, int var2) {
      synchronized(Display.LCDUILock) {
         this.iconToShow = var2 == 1 ? PIXMAP_PLACE_HOLDER : (var2 == 2 ? PIXMAP_BROKEN_VIDEO : null);
         this.playerId = var1;
      }

      this.repaint();
   }

   public int getDisplayX() {
      return this.videoX;
   }

   public int getDisplayY() {
      return this.videoY;
   }

   public int getMaxWidth() {
      return MAX_WIDTH - 2 * BORDER_W;
   }

   public int getMaxHeight() {
      return MAX_HEIGHT - 2 * BORDER_H;
   }

   boolean isFocusable() {
      synchronized(Display.LCDUILock) {
         return this.itemCommands.length() >= 1;
      }
   }

   int callMinimumWidth() {
      return MIN_WIDTH;
   }

   int callPreferredWidth(int var1) {
      int var2 = this.displayW + 2 * BORDER_W;
      return var2 > MAX_WIDTH ? MAX_WIDTH : (var2 < MIN_WIDTH ? MIN_WIDTH : var2);
   }

   int callMinimumHeight() {
      return MIN_HEIGHT;
   }

   int callPreferredHeight(int var1) {
      int var2 = this.displayH + 2 * BORDER_H;
      return var2 < MIN_HEIGHT ? MIN_HEIGHT : var2;
   }

   void callPaint(Graphics var1, int var2, int var3, boolean var4) {
      super.callPaint(var1, var2, var3, var4);
      synchronized(Display.LCDUILock) {
         var3 -= super.getLabelHeight(-1);
         int var6;
         if (var4) {
            Displayable.uistyle.drawBorder(var1.getImpl(), var1.getTranslateX(), var1.getTranslateY(), var2, var3, UIStyle.BORDER_IMAGE_HIGHLIGHT, var4);
         } else {
            var6 = var1.getColor();
            var1.setColor(UIStyle.COLOUR_BLACK);
            var1.drawRect(0, 0, var2 - 1, var3 - 1);
            var1.setColor(var6);
         }

         if (this.iconToShow != null) {
            var6 = var1.getTranslateX() + BORDER_W + (var2 - MIN_WIDTH) / 2;
            int var7 = var1.getTranslateY() + BORDER_H + (var3 - MIN_HEIGHT) / 2;
            var1.getImpl().drawPixmap(this.iconToShow, (short)var6, (short)var7);
         } else {
            this.calculatePosAndClip(var1, var2, var3);
         }

      }
   }

   private void calculatePosAndClip(Graphics var1, int var2, int var3) {
      int var4 = this.videoW < this.displayW ? this.videoW : this.displayW;
      int var5 = this.videoH < this.displayH ? this.videoH : this.displayH;
      boolean var6 = var4 <= var2 - 2 * BORDER_W;
      boolean var7 = var5 <= var3 - 2 * BORDER_H;
      int var8 = var6 ? 3 : this.getLayout() & 3;
      if (var8 != 3 && var8 != 2 && var8 != 1) {
         var8 = UIStyle.isAlignedLeftToRight ? 1 : 2;
      }

      switch(var8) {
      case 1:
         this.videoX = BORDER_W;
         break;
      case 2:
         this.videoX = var2 - var4 - BORDER_W;
         break;
      case 3:
         this.videoX = (var2 - var4) / 2;
      }

      int var9 = var7 ? 48 : this.getLayout() & 48;
      switch(var9) {
      case 16:
      default:
         this.videoY = BORDER_H;
         break;
      case 32:
         this.videoY = var3 - var5 - BORDER_H;
         break;
      case 48:
         this.videoY = (var3 - var5) / 2;
      }

      int var10 = var6 ? this.videoX : BORDER_W;
      int var11 = var7 ? this.videoY : BORDER_H;
      int var12 = var6 ? var4 : var2 - 2 * BORDER_W;
      int var13 = var7 ? var5 : var3 - 2 * BORDER_H;
      int[] var14 = new int[]{var1.getTranslateX() + var10, var1.getTranslateY() + var11, var12, var13};
      int[] var15 = new int[]{var1.getTranslateX() + var1.getClipX(), var1.getTranslateY() + var1.getClipY(), var1.getClipWidth(), var1.getClipHeight()};
      int var16 = var6 ? (var2 - this.videoW) / 2 : this.videoX;
      int var17 = var7 ? (var3 - this.videoH) / 2 : this.videoY;
      nNotifyPainted(this.playerId, var16 + var1.getTranslateX(), var17 + var1.getTranslateY(), var14, var15);
   }

   private static native void nNotifyPainted(int var0, int var1, int var2, int[] var3, int[] var4);

   static {
      BORDER_W = UIStyle.HYPERLINK_CUSTOMITEM_BORDER_WIDTH;
      BORDER_H = UIStyle.HYPERLINK_CUSTOMITEM_BORDER_HEIGHT;
      PIXMAP_PLACE_HOLDER = Pixmap.createPixmap(17);
      PIXMAP_BROKEN_VIDEO = Pixmap.createPixmap(18);
      MAX_WIDTH = UIStyle.getUIStyle().getZone(4).width;
      MAX_HEIGHT = UIStyle.getUIStyle().getZone(4).height;
      MIN_WIDTH = PIXMAP_PLACE_HOLDER.getWidth() + 2 * BORDER_W < Item.MIN_LABEL_WIDTH ? Item.MIN_LABEL_WIDTH : PIXMAP_PLACE_HOLDER.getWidth() + 2 * BORDER_W;
      MIN_HEIGHT = PIXMAP_PLACE_HOLDER.getHeight() + 2 * BORDER_H;
   }
}
