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

   public ImageItem(String label, Image img, int layout, String altText) {
      this(label, img, layout, altText, 0);
   }

   public ImageItem(String label, Image image, int layout, String altText, int appearanceMode) {
      super(label);
      this.borderWidth = 0;
      this.borderHeight = 0;
      this.borderType = -1;
      synchronized(Display.LCDUILock) {
         this.borderWidth = 0;
         this.borderHeight = 0;
         this.setImageImpl(image);
         this.setLayoutImpl(layout);
         this.altText = altText;
         switch(appearanceMode) {
         case 0:
         case 1:
         case 2:
            this.originalAppearanceMode = appearanceMode;
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

   public void setImage(Image img) {
      synchronized(Display.LCDUILock) {
         this.setImageImpl(img);
         this.invalidate();
      }
   }

   public String getAltText() {
      return this.altText;
   }

   public void setAltText(String text) {
      this.altText = text;
   }

   public int getLayout() {
      return super.getLayout();
   }

   public void setLayout(int layout) {
      super.setLayout(layout);
   }

   public int getAppearanceMode() {
      return this.originalAppearanceMode;
   }

   int callMinimumWidth() {
      return this.callPreferredWidth(-1);
   }

   int callPreferredWidth(int h) {
      int prefW;
      if (this.img != null) {
         prefW = this.img.getWidth() + 2 * this.borderWidth;
      } else if (this.isFocusable()) {
         prefW = this.getEmptyStringWidth((Font)null);
         if (this.appearanceMode == 2) {
            prefW += 2 * this.borderWidth;
         }
      } else {
         prefW = this.getLabelWidth();
      }

      if (prefW > DEFAULT_WIDTH) {
         prefW = DEFAULT_WIDTH;
      } else if (this.itemLabel != null && prefW < Item.MIN_LABEL_WIDTH) {
         prefW = Item.MIN_LABEL_WIDTH;
      }

      return prefW;
   }

   int callMinimumHeight() {
      return this.callPreferredHeight(-1);
   }

   int callPreferredHeight(int w) {
      if (this.img != null) {
         return this.img.getHeight() + 2 * this.borderHeight;
      } else if (!this.isFocusable()) {
         return 0;
      } else {
         Font font = Font.getDefaultFont();
         if (this.appearanceMode == 1) {
            font = Font.getFont(font.getFace(), font.getStyle() | 4, font.getSize());
            return TextBreaker.DEFAULT_TEXT_LEADING + this.getEmptyStringHeight(w, font);
         } else {
            return 2 * this.borderHeight + this.getEmptyStringHeight(w, font);
         }
      }
   }

   void callPaint(Graphics g, int width, int height, boolean isFocused) {
      super.callPaint(g, width, height, isFocused);
      height -= this.getLabelHeight(width);
      if (this.img == null) {
         this.paintEmptyItem(g, width, height, isFocused);
      } else {
         com.nokia.mid.impl.isa.ui.gdi.Graphics ng = g.getImpl();
         Pixmap pixmap = this.img.getPixmap();
         int x = false;
         int y = 0;
         int l = this.getLayout() & 3;
         int transX = g.getTranslateX();
         int transY = g.getTranslateY();
         int realImageWidth = this.img.getWidth();
         int imageWidth = realImageWidth >= this.bounds[2] - 2 * this.borderWidth ? this.bounds[2] - 2 * this.borderWidth : realImageWidth;
         int xT = false;
         int clx;
         int cly;
         int x;
         int xT;
         switch(l) {
         case 1:
            x = 0;
            xT = 0;
            break;
         case 2:
            x = width - 2 * this.borderWidth - imageWidth;
            xT = width - 2 * this.borderWidth - realImageWidth;
            break;
         case 3:
            clx = imageWidth / 2;
            cly = realImageWidth / 2;
            x = width / 2 - clx - this.borderWidth;
            xT = this.bounds[2] / 2 - cly - this.borderWidth;
            break;
         default:
            if (UIStyle.isAlignedLeftToRight) {
               x = 0;
               xT = 0;
            } else {
               x = width - imageWidth - 2 * this.borderWidth;
               xT = width - realImageWidth - 2 * this.borderWidth;
            }
         }

         if (this.appearanceMode != 0) {
            Displayable.uistyle.drawBorder(ng, transX + x, transY + y, imageWidth + 2 * this.borderWidth, this.img.getHeight() + 2 * this.borderHeight, this.borderType, isFocused);
         }

         if (realImageWidth <= width - 2 * this.borderWidth) {
            ng.drawPixmap(pixmap, (short)(transX + x + this.borderWidth), (short)(transY + y + this.borderHeight));
         } else {
            clx = g.getClipX();
            cly = g.getClipY();
            int clw = g.getClipWidth();
            int clh = g.getClipHeight();
            int clipY = transY + y + this.borderHeight > this.owner.viewport[1] ? transY + y + this.borderHeight : this.owner.viewport[1];
            ng.setClip(transX + x + this.borderWidth, clipY, (short)imageWidth, this.img.getHeight());
            ng.drawPixmap(pixmap, transX + xT + this.borderWidth, transY + y + this.borderHeight);
            ng.setClip(clx, cly, clw, clh);
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

   boolean addCommandImpl(Command cmd) {
      boolean wasAdded = super.addCommandImpl(cmd);
      if (wasAdded && this.itemCommands.length() >= 1 && this.appearanceMode == 0) {
         this.appearanceMode = this.originalAppearanceMode == 2 ? 2 : 1;
         if (this.appearanceMode == 2) {
            this.borderWidth = Item.BUTTON_BORDER_WIDTH;
            this.borderHeight = Item.BUTTON_BORDER_HEIGHT;
            this.borderType = UIStyle.BORDER_BUTTON;
         } else {
            this.borderWidth = HYPERLINK_BORDER_WIDTH;
            this.borderHeight = HYPERLINK_BORDER_HEIGHT;
            this.borderType = UIStyle.BORDER_IMAGE_HIGHLIGHT;
         }

         this.invalidate();
      }

      return wasAdded;
   }

   boolean removeCommandImpl(Command cmd) {
      boolean wasRemoved = super.removeCommandImpl(cmd);
      if (wasRemoved && this.itemCommands.length() < 1 && this.appearanceMode != 0) {
         this.originalAppearanceMode = this.appearanceMode;
         this.appearanceMode = 0;
         this.borderWidth = 0;
         this.borderHeight = 0;
         this.borderType = -1;
         this.invalidate();
      }

      return wasRemoved;
   }

   boolean shouldSkipTraverse() {
      return (this.label == null || this.label.length() == 0) && this.img == null && !this.isFocusable();
   }

   boolean isFocusable() {
      return this.itemCommands.length() >= 1;
   }

   private void setImageImpl(Image img) {
      this.mutImg = img;
      this.img = img != null && img.isMutable() ? Image.createImage(img) : img;
   }

   void paintEmptyItem(Graphics g, int width, int height, boolean isFocused) {
      if (this.isFocusable()) {
         int tX = g.getTranslateX();
         int tY = g.getTranslateY();
         com.nokia.mid.impl.isa.ui.gdi.Graphics ng = g.getImpl();
         if (this.appearanceMode == 2) {
            Displayable.uistyle.drawBorder(ng, tX, tY, width, height, UIStyle.BORDER_BUTTON, isFocused);
            tY += Item.BUTTON_BORDER_HEIGHT;
            tX += Item.BUTTON_BORDER_WIDTH;
            width -= 2 * Item.BUTTON_BORDER_WIDTH;
            height -= 2 * Item.BUTTON_BORDER_HEIGHT;
         } else {
            ng.getColorCtrl().setFgColor(UIStyle.COLOUR_HIGHLIGHT);
         }

         this.paintEmptyString(g, tX, tY, width, height, isFocused);
      }

   }

   boolean canItemAppearanceChange() {
      return true;
   }

   void stopAnimation() {
      if (this.img != null) {
         Pixmap p = this.img.getPixmap();
         if (p.isAnimatedPixmap()) {
            p.stopAnimationTimer();
            p.setAnimationListener((AnimationListener)null);
         }
      }

   }

   void startAnimation() {
      if (this.img != null) {
         Pixmap p = this.img.getPixmap();
         if (p.isAnimatedPixmap()) {
            p.resetAnimation();
            p.setAnimationListener(new ImageItem.ImageAnimationListener());
            p.startAnimationTimer();
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

      public void frameAdvanced(Pixmap p) {
         synchronized(Display.LCDUILock) {
            if (ImageItem.this.img != null && p == ImageItem.this.img.getPixmap() && ImageItem.this.owner != null) {
               ImageItem.this.repaint();
            }

         }
      }

      // $FF: synthetic method
      ImageAnimationListener(Object x1) {
         this();
      }
   }
}
