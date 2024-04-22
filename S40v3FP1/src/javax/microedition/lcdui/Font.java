package javax.microedition.lcdui;

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
   private static final Font DEFAULT_FONT = new Font(0, 0, 0);
   private com.nokia.mid.impl.isa.ui.gdi.Font impl;

   Font(int var1, int var2, int var3) {
      if (var3 != 8 && var3 != 0 && var3 != 16) {
         throw new IllegalArgumentException();
      } else if ((var2 & -8) != 0) {
         throw new IllegalArgumentException();
      } else if (var1 != 0 && var1 != 32 && var1 != 64) {
         throw new IllegalArgumentException();
      } else {
         this.impl = new com.nokia.mid.impl.isa.ui.gdi.Font(var3, var2, true);
      }
   }

   public static Font getDefaultFont() {
      return DEFAULT_FONT;
   }

   public static Font getFont(int var0, int var1, int var2) {
      return new Font(var0, var1, var2);
   }

   public static Font getFont(int var0) {
      switch(var0) {
      case 0:
      case 1:
         Font var1 = DEFAULT_FONT;
         return var1;
      default:
         throw new IllegalArgumentException();
      }
   }

   public int getStyle() {
      return this.impl.getMIDPStyle();
   }

   public int getSize() {
      return this.impl.getMIDPSize();
   }

   public int getFace() {
      return 0;
   }

   public boolean isPlain() {
      return (this.getStyle() & 7) == 0;
   }

   public boolean isBold() {
      return (this.getStyle() & 1) != 0;
   }

   public boolean isItalic() {
      return (this.getStyle() & 2) != 0;
   }

   public boolean isUnderlined() {
      return (this.getStyle() & 4) != 0;
   }

   public int getHeight() {
      return this.impl.getAbove() + this.impl.getDefaultCharHeight() + this.impl.getBelow();
   }

   public int getBaselinePosition() {
      return this.impl.getBaselinePositionImpl();
   }

   public int charWidth(char var1) {
      int var2 = this.impl.getCharASpace(var1) + this.impl.getCharWidth(var1);
      int var3 = this.impl.getCharCSpace(var1);
      if (var3 > 0) {
         var2 += var3;
      }

      return var2;
   }

   public int charsWidth(char[] var1, int var2, int var3) {
      try {
         String var4 = (new String(var1)).substring(var2, var2 + var3);
         return this.impl.stringWidth(this.impl.getStringWithCompatibleFont(var4));
      } catch (StringIndexOutOfBoundsException var5) {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   public int stringWidth(String var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return this.impl.stringWidth(this.impl.getStringWithCompatibleFont(var1));
      }
   }

   public int substringWidth(String var1, int var2, int var3) {
      return this.impl.stringWidth(this.impl.getStringWithCompatibleFont(var1.substring(var2, var2 + var3)));
   }

   Font(com.nokia.mid.impl.isa.ui.gdi.Font var1) {
      this.impl = var1;
   }

   com.nokia.mid.impl.isa.ui.gdi.Font getImpl() {
      return this.impl;
   }
}
