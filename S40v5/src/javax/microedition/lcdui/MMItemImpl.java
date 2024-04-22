package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.mmedia.video.MMItemAccessor;
import com.nokia.mid.impl.isa.ui.gdi.Pixmap;
import com.nokia.mid.impl.isa.ui.style.UIStyle;

class MMItemImpl extends Item implements MMItemAccessor {
   private static final int cy;
   private static final int cz;
   private static final Pixmap cA;
   private static final Pixmap cB;
   private static final int cC;
   private static final int cD;
   private static final int cE;
   private static final int cF;
   private int cG;
   private int cH;
   private int cI;
   private int cJ;
   private int cK;
   private int cL;
   private Pixmap cM;
   private int playerId;

   MMItemImpl() {
      super((String)null);
      this.cI = cE;
      this.cJ = cF;
      this.cK = cE;
      this.cL = cF;
      this.cM = cA;
   }

   public void init(int var1, int var2) {
      synchronized(Display.hG) {
         this.cI = var1;
         this.cJ = var2;
         this.cK = var1;
         this.cL = var2;
         this.invalidate();
      }
   }

   public void setDisplaySize(int var1, int var2) {
      synchronized(Display.hG) {
         if (this.cK != var1 || this.cL != var2) {
            this.cK = var1;
            this.cL = var2;
            this.invalidate();
         }

      }
   }

   public void showIcon(int var1, int var2) {
      synchronized(Display.hG) {
         this.cM = var2 == 1 ? cA : (var2 == 2 ? cB : null);
         this.playerId = var1;
      }

      this.repaint();
   }

   public int getDisplayX() {
      return this.cG;
   }

   public int getDisplayY() {
      return this.cH;
   }

   public int getMaxWidth() {
      return cC - 2 * cy;
   }

   public int getMaxHeight() {
      return cD - 2 * cz;
   }

   final boolean isFocusable() {
      synchronized(Display.hG) {
         return this.aG.length() >= 1;
      }
   }

   final int a() {
      return cE;
   }

   final int a(int var1) {
      int var2;
      if ((var2 = this.cK + 2 * cy) > cC) {
         return cC;
      } else {
         return var2 < cE ? cE : var2;
      }
   }

   final int b() {
      return cF;
   }

   final int b(int var1) {
      int var2;
      return (var2 = this.cL + 2 * cz) < cF ? cF : var2;
   }

   final void a(Graphics var1, int var2, int var3, boolean var4) {
      super.a(var1, var2, var3, var4);
      synchronized(Display.hG) {
         var3 -= super.getLabelHeight(-1);
         int var13;
         if (var4) {
            Displayable.eI.drawBorder(var1.getImpl(), var1.getTranslateX(), var1.getTranslateY(), var2, var3, UIStyle.BORDER_IMAGE_HIGHLIGHT, var4);
         } else {
            var13 = var1.getColor();
            var1.setColor(UIStyle.COLOUR_BLACK);
            var1.drawRect(0, 0, var2 - 1, var3 - 1);
            var1.setColor(var13);
         }

         int var6;
         if (this.cM != null) {
            var13 = var1.getTranslateX() + cy + (var2 - cE) / 2;
            var6 = var1.getTranslateY() + cz + (var3 - cF) / 2;
            var1.getImpl().drawPixmap(this.cM, (short)var13, (short)var6);
         } else {
            MMItemImpl var12;
            var13 = (var12 = this).cI < var12.cK ? var12.cI : var12.cK;
            var6 = var12.cJ < var12.cL ? var12.cJ : var12.cL;
            boolean var7 = var13 <= var2 - 2 * cy;
            boolean var8 = var6 <= var3 - 2 * cz;
            int var10000 = var7 ? 3 : var12.getLayout() & 3;
            int var9 = var10000;
            if (var10000 != 3 && var9 != 2 && var9 != 1) {
               var9 = UIStyle.isAlignedLeftToRight ? 1 : 2;
            }

            switch(var9) {
            case 1:
               var12.cG = cy;
               break;
            case 2:
               var12.cG = var2 - var13 - cy;
               break;
            case 3:
               var12.cG = (var2 - var13) / 2;
            }

            switch(var8 ? 48 : var12.getLayout() & 48) {
            case 16:
            default:
               var12.cH = cz;
               break;
            case 32:
               var12.cH = var3 - var6 - cz;
               break;
            case 48:
               var12.cH = (var3 - var6) / 2;
            }

            var9 = var7 ? var12.cG : cy;
            int var10 = var8 ? var12.cH : cz;
            var13 = var7 ? var13 : var2 - 2 * cy;
            var6 = var8 ? var6 : var3 - 2 * cz;
            int[] var14 = new int[]{var1.getTranslateX() + var9, var1.getTranslateY() + var10, var13, var6};
            int[] var15 = new int[]{var1.getTranslateX() + var1.getClipX(), var1.getTranslateY() + var1.getClipY(), var1.getClipWidth(), var1.getClipHeight()};
            var2 = var7 ? (var2 - var12.cI) / 2 : var12.cG;
            var3 = var8 ? (var3 - var12.cJ) / 2 : var12.cH;
            nNotifyPainted(var12.playerId, var2 + var1.getTranslateX(), var3 + var1.getTranslateY(), var14, var15);
         }

      }
   }

   private static native void nNotifyPainted(int var0, int var1, int var2, int[] var3, int[] var4);

   static {
      cy = UIStyle.HYPERLINK_CUSTOMITEM_BORDER_WIDTH;
      cz = UIStyle.HYPERLINK_CUSTOMITEM_BORDER_HEIGHT;
      cA = Pixmap.createPixmap(18);
      cB = Pixmap.createPixmap(19);
      cC = UIStyle.getUIStyle().getZone(4).width;
      cD = UIStyle.getUIStyle().getZone(4).height;
      cE = cA.getWidth() + 2 * cy < Item.aq ? Item.aq : cA.getWidth() + 2 * cy;
      cF = cA.getHeight() + 2 * cz;
   }
}
