package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.style.UIStyle;

public final class Font {
   public static final int STYLE_PLAIN = 0;
   public static final int STYLE_BOLD = 1;
   public static final int STYLE_ITALIC = 2;
   public static final int STYLE_UNDERLINED = 4;
   public static final int SIZE_SMALL = 8;
   public static final int SIZE_MEDIUM = 0;
   public static final int SIZE_LARGE = 16;
   public static final int FACE_SYSTEM = 0;
   public static final int FACE_MONOSPACE = 32;
   public static final int FACE_PROPORTIONAL = 64;
   public static final int FONT_STATIC_TEXT = 0;
   public static final int FONT_INPUT_TEXT = 1;
   private static final Font iN = new Font(UIStyle.getUIStyle().getDefaultFont());
   private com.nokia.mid.impl.isa.ui.gdi.Font iO;

   private Font(int var1, int var2, int var3) {
      if (var3 != 8 && var3 != 0 && var3 != 16) {
         throw new IllegalArgumentException();
      } else if ((var2 & -8) != 0) {
         throw new IllegalArgumentException();
      } else if (var1 != 0 && var1 != 32 && var1 != 64) {
         throw new IllegalArgumentException();
      } else {
         this.iO = new com.nokia.mid.impl.isa.ui.gdi.Font(var3, var2, true);
      }
   }

   public static Font getDefaultFont() {
      return iN;
   }

   public static Font getFont(int var0, int var1, int var2) {
      return new Font(var0, var1, var2);
   }

   public static Font getFont(int var0) {
      switch(var0) {
      case 0:
      case 1:
         Font var1 = iN;
         return var1;
      default:
         throw new IllegalArgumentException();
      }
   }

   public final int getStyle() {
      return this.iO.getMIDPStyle();
   }

   public final int getSize() {
      return this.iO.getMIDPSize();
   }

   public final int getFace() {
      return 0;
   }

   public final boolean isPlain() {
      return (this.getStyle() & 7) == 0;
   }

   public final boolean isBold() {
      return (this.getStyle() & 1) != 0;
   }

   public final boolean isItalic() {
      return (this.getStyle() & 2) != 0;
   }

   public final boolean isUnderlined() {
      return (this.getStyle() & 4) != 0;
   }

   public final int getHeight() {
      return this.iO.getAbove() + this.iO.getDefaultCharHeight() + this.iO.getBelow();
   }

   public final int getBaselinePosition() {
      return this.iO.getBaselinePositionImpl();
   }

   public final int charWidth(char var1) {
      int var2 = this.iO.getCharASpace(var1) + this.iO.getCharWidth(var1);
      int var3;
      if ((var3 = this.iO.getCharCSpace(var1)) > 0) {
         var2 += var3;
      }

      return var2;
   }

   public final int charsWidth(char[] var1, int var2, int var3) {
      try {
         String var5 = (new String(var1)).substring(var2, var2 + var3);
         return this.iO.stringWidth(this.iO.getStringWithCompatibleFont(var5));
      } catch (StringIndexOutOfBoundsException var4) {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   public final int stringWidth(String var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return this.iO.stringWidth(this.iO.getStringWithCompatibleFont(var1));
      }
   }

   public final int substringWidth(String var1, int var2, int var3) {
      return this.iO.stringWidth(this.iO.getStringWithCompatibleFont(var1.substring(var2, var2 + var3)));
   }

   Font(com.nokia.mid.impl.isa.ui.gdi.Font var1) {
      this.iO = var1;
   }

   final com.nokia.mid.impl.isa.ui.gdi.Font getImpl() {
      return this.iO;
   }
}
