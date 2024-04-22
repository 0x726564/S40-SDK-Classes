package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.gdi.AnimationListener;
import com.nokia.mid.impl.isa.ui.gdi.Pixmap;
import com.nokia.mid.impl.isa.ui.gdi.TextBreaker;
import com.nokia.mid.impl.isa.ui.style.UIStyle;

public class ImageItem extends Item {
   public static final int LAYOUT_DEFAULT = 0;
   public static final int LAYOUT_LEFT = 1;
   public static final int LAYOUT_RIGHT = 2;
   public static final int LAYOUT_CENTER = 3;
   public static final int LAYOUT_NEWLINE_BEFORE = 256;
   public static final int LAYOUT_NEWLINE_AFTER = 512;
   static final int HYPERLINK_BORDER_WIDTH;
   static final int HYPERLINK_BORDER_HEIGHT;
   private Image img;
   private Image mutImg;
   private String altText;
   private int appearanceMode;
   private int originalAppearanceMode;
   int borderWidth;
   int borderHeight;
   int borderType;

   public ImageItem(String var1, Image var2, int var3, String var4) {
      this(var1, var2, var3, var4, 0);
   }

   public ImageItem(String var1, Image var2, int var3, String var4, int var5) {
      super(var1);
      this.borderWidth = 0;
      this.borderHeight = 0;
      this.borderType = -1;
      synchronized(Display.LCDUILock) {
         this.borderWidth = 0;
         this.borderHeight = 0;
         this.setImageImpl(var2);
         this.setLayoutImpl(var3);
         this.altText = var4;
         switch(var5) {
         case 0:
         case 1:
         case 2:
            this.originalAppearanceMode = var5;
            this.appearanceMode = 0;
            return;
         default:
            throw new IllegalArgumentException();
         }
      }
   }

   public Image getImage() {
      return this.mutImg;
   }

   public void setImage(Image var1) {
      synchronized(Display.LCDUILock) {
         this.setImageImpl(var1);
         this.invalidate();
      }
   }

   public String getAltText() {
      return this.altText;
   }

   public void setAltText(String var1) {
      this.altText = var1;
   }

   public int getLayout() {
      return super.getLayout();
   }

   public void setLayout(int var1) {
      super.setLayout(var1);
   }

   public int getAppearanceMode() {
      return this.appearanceMode;
   }

   int callMinimumWidth() {
      return this.callPreferredWidth(-1);
   }

   int callPreferredWidth(int var1) {
      int var2;
      if (this.img != null) {
         var2 = this.img.getWidth() + 2 * this.borderWidth;
      } else if (this.isFocusable()) {
         var2 = this.getEmptyStringWidth((Font)null);
         if (this.appearanceMode == 2) {
            var2 += 2 * this.borderWidth;
         }
      } else {
         var2 = this.getLabelWidth();
      }

      if (var2 > DEFAULT_WIDTH) {
         var2 = DEFAULT_WIDTH;
      } else if (this.itemLabel != null && var2 < Item.MIN_LABEL_WIDTH) {
         var2 = Item.MIN_LABEL_WIDTH;
      }

      return var2;
   }

   int callMinimumHeight() {
      return this.callPreferredHeight(-1);
   }

   int callPreferredHeight(int var1) {
      if (this.img != null) {
         return this.img.getHeight() + 2 * this.borderHeight;
      } else if (!this.isFocusable()) {
         return 0;
      } else {
         Font var2 = Font.getDefaultFont();
         if (this.appearanceMode == 1) {
            var2 = Font.getFont(var2.getFace(), var2.getStyle() | 4, var2.getSize());
            return TextBreaker.DEFAULT_TEXT_LEADING + this.getEmptyStringHeight(var1, var2);
         } else {
            return 2 * this.borderHeight + this.getEmptyStringHeight(var1, var2);
         }
      }
   }

   void callPaint(Graphics var1, int var2, int var3, boolean var4) {
      super.callPaint(var1, var2, var3, var4);
      var3 -= this.getLabelHeight(var2);
      if (this.img == null) {
         this.paintEmptyItem(var1, var2, var3, var4);
      } else {
         com.nokia.mid.impl.isa.ui.gdi.Graphics var5 = var1.getImpl();
         Pixmap var6 = this.img.getPixmap();
         boolean var7 = false;
         byte var8 = 0;
         int var9 = this.getLayout() & 3;
         int var10 = var1.getTranslateX();
         int var11 = var1.getTranslateY();
         int var12 = this.img.getWidth();
         int var13 = var12 >= this.bounds[2] - 2 * this.borderWidth ? this.bounds[2] - 2 * this.borderWidth : var12;
         boolean var14 = false;
         int var15;
         int var16;
         int var20;
         int var21;
         switch(var9) {
         case 1:
            var20 = 0;
            var21 = 0;
            break;
         case 2:
            var20 = var2 - 2 * this.borderWidth - var13;
            var21 = var2 - 2 * this.borderWidth - var12;
            break;
         case 3:
            var15 = var13 / 2;
            var16 = var12 / 2;
            var20 = var2 / 2 - var15 - this.borderWidth;
            var21 = this.bounds[2] / 2 - var16 - this.borderWidth;
            break;
         default:
            if (this.owner.alignedLeftToRight) {
               var20 = 0;
               var21 = 0;
            } else {
               var20 = var2 - var13 - 2 * this.borderWidth;
               var21 = var2 - var12 - 2 * this.borderWidth;
            }
         }

         if (this.appearanceMode != 0) {
            Displayable.uistyle.drawBorder(var5, var10 + var20, var11 + var8, var13 + 2 * this.borderWidth, this.img.getHeight() + 2 * this.borderHeight, this.borderType, var4);
         }

         if (var12 <= var2 - 2 * this.borderWidth) {
            var5.drawPixmap(var6, (short)(var10 + var20 + this.borderWidth), (short)(var11 + var8 + this.borderHeight));
         } else {
            var15 = var1.getClipX();
            var16 = var1.getClipY();
            int var17 = var1.getClipWidth();
            int var18 = var1.getClipHeight();
            int var19 = var11 + var8 + this.borderHeight > this.owner.viewport[1] ? var11 + var8 + this.borderHeight : this.owner.viewport[1];
            var5.setClip(var10 + var20 + this.borderWidth, var19, (short)var13, this.img.getHeight());
            var5.drawPixmap(var6, var10 + var21 + this.borderWidth, var11 + var8 + this.borderHeight);
            var5.setClip(var15, var16, var17, var18);
         }

      }
   }

   void callShowNotify() {
      super.callShowNotify();
      synchronized(Display.LCDUILock) {
         this.startAnimation();
      }
   }

   void callHideNotify() {
      super.callHideNotify();
      synchronized(Display.LCDUILock) {
         this.stopAnimation();
      }
   }

   boolean addCommandImpl(Command var1) {
      boolean var2 = super.addCommandImpl(var1);
      if (var2 && this.itemCommands.length() >= 1 && this.appearanceMode == 0) {
         this.appearanceMode = this.originalAppearanceMode == 2 ? 2 : 1;
         if (this.appearanceMode == 2) {
            this.borderWidth = Item.BUTTON_BORDER_WIDTH;
            this.borderHeight = Item.BUTTON_BORDER_HEIGHT;
            this.borderType = UIStyle.BORDER_TYPE_8;
         } else {
            this.borderWidth = HYPERLINK_BORDER_WIDTH;
            this.borderHeight = HYPERLINK_BORDER_HEIGHT;
            this.borderType = UIStyle.BORDER_TYPE_5;
         }

         this.invalidate();
      }

      return var2;
   }

   boolean removeCommandImpl(Command var1) {
      boolean var2 = super.removeCommandImpl(var1);
      if (var2 && this.itemCommands.length() < 1 && this.appearanceMode != 0) {
         this.originalAppearanceMode = this.appearanceMode;
         this.appearanceMode = 0;
         this.borderWidth = 0;
         this.borderHeight = 0;
         this.borderType = -1;
         this.invalidate();
      }

      return var2;
   }

   boolean shouldSkipTraverse() {
      return (this.label == null || this.label.length() == 0) && this.img == null && !this.isFocusable();
   }

   boolean isFocusable() {
      return this.itemCommands.length() >= 1;
   }

   private void setImageImpl(Image var1) {
      this.mutImg = var1;
      this.img = var1 != null && var1.isMutable() ? Image.createImage(var1) : var1;
   }

   void paintEmptyItem(Graphics var1, int var2, int var3, boolean var4) {
      if (this.isFocusable()) {
         int var5 = var1.getTranslateX();
         int var6 = var1.getTranslateY();
         com.nokia.mid.impl.isa.ui.gdi.Graphics var7 = var1.getImpl();
         if (this.appearanceMode == 2) {
            Displayable.uistyle.drawBorder(var7, var5, var6, var2, var3, UIStyle.BORDER_TYPE_8, var4);
            var6 += Item.BUTTON_BORDER_HEIGHT;
            var5 += Item.BUTTON_BORDER_WIDTH;
            var2 -= 2 * Item.BUTTON_BORDER_WIDTH;
            var3 -= 2 * Item.BUTTON_BORDER_HEIGHT;
         } else {
            var7.getColorCtrl().setFgColor(UIStyle.COLOUR_HIGHLIGHT);
         }

         this.paintEmptyString(var1, var5, var6, var2, var3, var4);
      }

   }

   boolean canItemAppearanceChange() {
      return true;
   }

   void stopAnimation() {
      if (this.img != null) {
         Pixmap var1 = this.img.getPixmap();
         if (var1.isAnimatedPixmap()) {
            var1.stopAnimationTimer();
            var1.setAnimationListener((AnimationListener)null);
         }
      }

   }

   void startAnimation() {
      if (this.img != null) {
         Pixmap var1 = this.img.getPixmap();
         if (var1.isAnimatedPixmap()) {
            var1.resetAnimation();
            var1.setAnimationListener(new ImageItem.ImageAnimationListener());
            var1.startAnimationTimer();
         }
      }

   }

   static {
      HYPERLINK_BORDER_WIDTH = UIStyle.HYPERLINK_CUSTOMITEM_BORDER_WIDTH;
      HYPERLINK_BORDER_HEIGHT = UIStyle.HYPERLINK_CUSTOMITEM_BORDER_HEIGHT;
   }

   private class ImageAnimationListener implements AnimationListener {
      private ImageAnimationListener() {
      }

      public void frameAdvanced(Pixmap var1) {
         synchronized(Display.LCDUILock) {
            if (ImageItem.this.img != null && var1 == ImageItem.this.img.getPixmap() && ImageItem.this.owner != null) {
               ImageItem.this.repaint();
            }

         }
      }

      // $FF: synthetic method
      ImageAnimationListener(Object var2) {
         this();
      }
   }
}
