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
   private static int dX;
   private static int dY;
   private Image dZ;
   private Image ea;
   private String eb;
   private int ec;
   private int ed;
   private int ee;
   private int ef;
   private int eg;

   public ImageItem(String var1, Image var2, int var3, String var4) {
      this(var1, var2, var3, var4, 0);
   }

   public ImageItem(String var1, Image var2, int var3, String var4, int var5) {
      super(var1);
      this.ee = 0;
      this.ef = 0;
      this.eg = -1;
      synchronized(Display.hG) {
         this.ee = 0;
         this.ef = 0;
         this.setImageImpl(var2);
         this.setLayoutImpl(var3);
         this.eb = var4;
         switch(var5) {
         case 0:
         case 1:
         case 2:
            this.ed = var5;
            this.ec = 0;
            return;
         default:
            throw new IllegalArgumentException();
         }
      }
   }

   public Image getImage() {
      return this.ea;
   }

   public void setImage(Image var1) {
      synchronized(Display.hG) {
         this.setImageImpl(var1);
         this.invalidate();
      }
   }

   public String getAltText() {
      return this.eb;
   }

   public void setAltText(String var1) {
      this.eb = var1;
   }

   public int getLayout() {
      return super.getLayout();
   }

   public void setLayout(int var1) {
      super.setLayout(var1);
   }

   public int getAppearanceMode() {
      return this.ec;
   }

   final int a() {
      return this.a(-1);
   }

   final int a(int var1) {
      if (this.dZ != null) {
         var1 = this.dZ.getWidth() + 2 * this.ee;
      } else if (this.isFocusable()) {
         var1 = this.a((Font)null);
         if (this.ec == 2) {
            var1 += 2 * this.ee;
         }
      } else {
         var1 = this.getLabelWidth();
      }

      if (var1 > ak) {
         var1 = ak;
      } else if (this.aJ != null && var1 < Item.aq) {
         var1 = Item.aq;
      }

      return var1;
   }

   final int b() {
      return this.b(-1);
   }

   final int b(int var1) {
      if (this.dZ != null) {
         return this.dZ.getHeight() + 2 * this.ef;
      } else if (!this.isFocusable()) {
         return 0;
      } else {
         Font var2 = Font.getDefaultFont();
         if (this.ec == 1) {
            var2 = Font.getFont(var2.getFace(), var2.getStyle() | 4, var2.getSize());
            return TextBreaker.DEFAULT_TEXT_LEADING + this.a(var1, var2);
         } else {
            return 2 * this.ef + this.a(var1, var2);
         }
      }
   }

   final void a(Graphics var1, int var2, int var3, boolean var4) {
      super.a(var1, var2, var3, var4);
      var3 -= this.getLabelHeight(var2);
      int var7;
      int var9;
      int var10;
      int var17;
      if (this.dZ == null) {
         var7 = var3;
         var17 = var2;
         if (this.isFocusable()) {
            var9 = var1.getTranslateX();
            var10 = var1.getTranslateY();
            com.nokia.mid.impl.isa.ui.gdi.Graphics var19 = var1.getImpl();
            if (this.ec == 2) {
               Displayable.eI.drawBorder(var19, var9, var10, var2, var3, UIStyle.BORDER_BUTTON, var4);
               var10 += Item.BUTTON_BORDER_HEIGHT;
               var9 += Item.BUTTON_BORDER_WIDTH;
               var17 = var2 - 2 * Item.BUTTON_BORDER_WIDTH;
               var7 = var3 - 2 * Item.BUTTON_BORDER_HEIGHT;
            } else {
               var19.getColorCtrl().setFgColor(UIStyle.COLOUR_HIGHLIGHT);
            }

            this.a(var1, var9, var10, var17, var7, var4);
         }

      } else {
         com.nokia.mid.impl.isa.ui.gdi.Graphics var15 = var1.getImpl();
         Pixmap var5 = this.dZ.getPixmap();
         boolean var6 = false;
         var17 = this.getLayout() & 3;
         var7 = var1.getTranslateX();
         int var8 = var1.getTranslateY();
         var10 = (var9 = this.dZ.getWidth()) >= this.ar[2] - 2 * this.ee ? this.ar[2] - 2 * this.ee : var9;
         boolean var11 = false;
         int var12;
         int var13;
         int var18;
         switch(var17) {
         case 1:
            var17 = 0;
            var18 = 0;
            break;
         case 2:
            var17 = var2 - 2 * this.ee - var10;
            var18 = var2 - 2 * this.ee - var9;
            break;
         case 3:
            var12 = var10 / 2;
            var13 = var9 / 2;
            var17 = var2 / 2 - var12 - this.ee;
            var18 = this.ar[2] / 2 - var13 - this.ee;
            break;
         default:
            if (UIStyle.isAlignedLeftToRight) {
               var17 = 0;
               var18 = 0;
            } else {
               var17 = var2 - var10 - 2 * this.ee;
               var18 = var2 - var9 - 2 * this.ee;
            }
         }

         if (this.ec != 0) {
            Displayable.eI.drawBorder(var15, var7 + var17, var8, var10 + 2 * this.ee, this.dZ.getHeight() + 2 * this.ef, this.eg, var4);
         }

         if (var9 <= var2 - 2 * this.ee) {
            var15.drawPixmap(var5, (short)(var7 + var17 + this.ee), (short)(var8 + this.ef));
         } else {
            var12 = var1.getClipX();
            var13 = var1.getClipY();
            var2 = var1.getClipWidth();
            int var14 = var1.getClipHeight();
            int var16 = var8 + this.ef > this.au.eR[1] ? var8 + this.ef : this.au.eR[1];
            var15.setClip(var7 + var17 + this.ee, var16, (short)var10, this.dZ.getHeight());
            var15.drawPixmap(var5, var7 + var18 + this.ee, var8 + this.ef);
            var15.setClip(var12, var13, var2, var14);
         }
      }
   }

   final void o() {
      super.o();
      synchronized(Display.hG) {
         this.ac();
      }
   }

   final void x() {
      super.x();
      synchronized(Display.hG) {
         this.ab();
      }
   }

   final boolean b(Command var1) {
      boolean var2;
      if ((var2 = super.b(var1)) && this.aG.length() >= 1 && this.ec == 0) {
         this.ec = this.ed == 2 ? 2 : 1;
         if (this.ec == 2) {
            this.ee = Item.BUTTON_BORDER_WIDTH;
            this.ef = Item.BUTTON_BORDER_HEIGHT;
            this.eg = UIStyle.BORDER_BUTTON;
         } else {
            this.ee = dX;
            this.ef = dY;
            this.eg = UIStyle.BORDER_IMAGE_HIGHLIGHT;
         }

         this.invalidate();
      }

      return var2;
   }

   final boolean d(Command var1) {
      boolean var2;
      if ((var2 = super.d(var1)) && this.aG.length() < 1 && this.ec != 0) {
         this.ed = this.ec;
         this.ec = 0;
         this.ee = 0;
         this.ef = 0;
         this.eg = -1;
         this.invalidate();
      }

      return var2;
   }

   final boolean c() {
      return (this.label == null || this.label.length() == 0) && this.dZ == null && !this.isFocusable();
   }

   final boolean isFocusable() {
      return this.aG.length() >= 1;
   }

   private void setImageImpl(Image var1) {
      this.ea = var1;
      this.dZ = var1 != null && var1.isMutable() ? Image.createImage(var1) : var1;
   }

   final boolean A() {
      return true;
   }

   final void ab() {
      Pixmap var1;
      if (this.dZ != null && (var1 = this.dZ.getPixmap()).isAnimatedPixmap()) {
         var1.stopAnimationTimer();
         var1.setAnimationListener((AnimationListener)null);
      }

   }

   final void ac() {
      Pixmap var1;
      if (this.dZ != null && (var1 = this.dZ.getPixmap()).isAnimatedPixmap()) {
         var1.resetAnimation();
         var1.setAnimationListener(new ImageItem.ImageAnimationListener(this));
         var1.startAnimationTimer();
      }

   }

   static Image a(ImageItem var0) {
      return var0.dZ;
   }

   static {
      dX = UIStyle.HYPERLINK_CUSTOMITEM_BORDER_WIDTH;
      dY = UIStyle.HYPERLINK_CUSTOMITEM_BORDER_HEIGHT;
   }

   private class ImageAnimationListener implements AnimationListener {
      private final ImageItem dk;

      public void frameAdvanced(Pixmap var1) {
         synchronized(Display.hG) {
            if (ImageItem.a(this.dk) != null && var1 == ImageItem.a(this.dk).getPixmap() && this.dk.au != null) {
               this.dk.repaint();
            }

         }
      }

      ImageAnimationListener(ImageItem var1, Object var2) {
         this.dk = var1;
      }
   }
}
