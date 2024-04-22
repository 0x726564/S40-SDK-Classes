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

   public void init(int width, int height) {
      synchronized(Display.LCDUILock) {
         this.videoW = width;
         this.videoH = height;
         this.displayW = width;
         this.displayH = height;
         this.invalidate();
      }
   }

   public void setDisplaySize(int width, int height) {
      synchronized(Display.LCDUILock) {
         if (this.displayW != width || this.displayH != height) {
            this.displayW = width;
            this.displayH = height;
            this.invalidate();
         }

      }
   }

   public void showIcon(int playerId, int icon) {
      synchronized(Display.LCDUILock) {
         this.iconToShow = icon == 1 ? PIXMAP_PLACE_HOLDER : (icon == 2 ? PIXMAP_BROKEN_VIDEO : null);
         this.playerId = playerId;
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

   int callPreferredWidth(int h) {
      int pW = this.displayW + 2 * BORDER_W;
      return pW > MAX_WIDTH ? MAX_WIDTH : (pW < MIN_WIDTH ? MIN_WIDTH : pW);
   }

   int callMinimumHeight() {
      return MIN_HEIGHT;
   }

   int callPreferredHeight(int w) {
      int pH = this.displayH + 2 * BORDER_H;
      return pH < MIN_HEIGHT ? MIN_HEIGHT : pH;
   }

   void callPaint(Graphics g, int width, int height, boolean isFocused) {
      super.callPaint(g, width, height, isFocused);
      synchronized(Display.LCDUILock) {
         height -= super.getLabelHeight(-1);
         int tX;
         if (isFocused) {
            Displayable.uistyle.drawBorder(g.getImpl(), g.getTranslateX(), g.getTranslateY(), width, height, UIStyle.BORDER_IMAGE_HIGHLIGHT, isFocused);
         } else {
            tX = g.getColor();
            g.setColor(UIStyle.COLOUR_BLACK);
            g.drawRect(0, 0, width - 1, height - 1);
            g.setColor(tX);
         }

         if (this.iconToShow != null) {
            tX = g.getTranslateX() + BORDER_W + (width - MIN_WIDTH) / 2;
            int tY = g.getTranslateY() + BORDER_H + (height - MIN_HEIGHT) / 2;
            g.getImpl().drawPixmap(this.iconToShow, (short)tX, (short)tY);
         } else {
            this.calculatePosAndClip(g, width, height);
         }

      }
   }

   private void calculatePosAndClip(Graphics g, int width, int height) {
      int visibleW = this.videoW < this.displayW ? this.videoW : this.displayW;
      int visibleH = this.videoH < this.displayH ? this.videoH : this.displayH;
      boolean visibleWFitsItem = visibleW <= width - 2 * BORDER_W;
      boolean visibleHFitsItem = visibleH <= height - 2 * BORDER_H;
      int horizLayout = visibleWFitsItem ? 3 : this.getLayout() & 3;
      if (horizLayout != 3 && horizLayout != 2 && horizLayout != 1) {
         horizLayout = UIStyle.isAlignedLeftToRight ? 1 : 2;
      }

      switch(horizLayout) {
      case 1:
         this.videoX = BORDER_W;
         break;
      case 2:
         this.videoX = width - visibleW - BORDER_W;
         break;
      case 3:
         this.videoX = (width - visibleW) / 2;
      }

      int vertLayout = visibleHFitsItem ? 48 : this.getLayout() & 48;
      switch(vertLayout) {
      case 16:
      default:
         this.videoY = BORDER_H;
         break;
      case 32:
         this.videoY = height - visibleH - BORDER_H;
         break;
      case 48:
         this.videoY = (height - visibleH) / 2;
      }

      int visibleClipX = visibleWFitsItem ? this.videoX : BORDER_W;
      int visibleClipY = visibleHFitsItem ? this.videoY : BORDER_H;
      int visibleClipW = visibleWFitsItem ? visibleW : width - 2 * BORDER_W;
      int visibleClipH = visibleHFitsItem ? visibleH : height - 2 * BORDER_H;
      int[] visClip = new int[]{g.getTranslateX() + visibleClipX, g.getTranslateY() + visibleClipY, visibleClipW, visibleClipH};
      int[] screenClip = new int[]{g.getTranslateX() + g.getClipX(), g.getTranslateY() + g.getClipY(), g.getClipWidth(), g.getClipHeight()};
      int vidOriginX = visibleWFitsItem ? (width - this.videoW) / 2 : this.videoX;
      int vidOriginY = visibleHFitsItem ? (height - this.videoH) / 2 : this.videoY;
      nNotifyPainted(this.playerId, vidOriginX + g.getTranslateX(), vidOriginY + g.getTranslateY(), visClip, screenClip);
   }

   private static native void nNotifyPainted(int var0, int var1, int var2, int[] var3, int[] var4);

   static {
      BORDER_W = UIStyle.HYPERLINK_CUSTOMITEM_BORDER_WIDTH;
      BORDER_H = UIStyle.HYPERLINK_CUSTOMITEM_BORDER_HEIGHT;
      PIXMAP_PLACE_HOLDER = Pixmap.createPixmap(18);
      PIXMAP_BROKEN_VIDEO = Pixmap.createPixmap(19);
      MAX_WIDTH = UIStyle.getUIStyle().getZone(4).width;
      MAX_HEIGHT = UIStyle.getUIStyle().getZone(4).height;
      MIN_WIDTH = PIXMAP_PLACE_HOLDER.getWidth() + 2 * BORDER_W < Item.MIN_LABEL_WIDTH ? Item.MIN_LABEL_WIDTH : PIXMAP_PLACE_HOLDER.getWidth() + 2 * BORDER_W;
      MIN_HEIGHT = PIXMAP_PLACE_HOLDER.getHeight() + 2 * BORDER_H;
   }
}
