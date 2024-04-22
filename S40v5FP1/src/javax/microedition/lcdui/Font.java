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
   private static final Font DEFAULT_FONT = new Font(UIStyle.getUIStyle().getDefaultFont());
   private com.nokia.mid.impl.isa.ui.gdi.Font impl;

   Font(int face, int style, int size) {
      if (size != 8 && size != 0 && size != 16) {
         throw new IllegalArgumentException();
      } else if ((style & -8) != 0) {
         throw new IllegalArgumentException();
      } else if (face != 0 && face != 32 && face != 64) {
         throw new IllegalArgumentException();
      } else {
         this.impl = new com.nokia.mid.impl.isa.ui.gdi.Font(size, style, true);
      }
   }

   public static Font getDefaultFont() {
      return DEFAULT_FONT;
   }

   public static Font getFont(int face, int style, int size) {
      return new Font(face, style, size);
   }

   public static Font getFont(int fontSpecifier) {
      switch(fontSpecifier) {
      case 0:
      case 1:
         Font font = DEFAULT_FONT;
         return font;
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

   public int charWidth(char ch) {
      int width = this.impl.getCharASpace(ch) + this.impl.getCharWidth(ch);
      int cSpace = this.impl.getCharCSpace(ch);
      if (cSpace > 0) {
         width += cSpace;
      }

      return width;
   }

   public int charsWidth(char[] ch, int offset, int len) {
      try {
         String _str = (new String(ch)).substring(offset, offset + len);
         return this.impl.stringWidth(this.impl.getStringWithCompatibleFont(_str));
      } catch (StringIndexOutOfBoundsException var5) {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   public int stringWidth(String str) {
      if (str == null) {
         throw new NullPointerException();
      } else {
         return this.impl.stringWidth(this.impl.getStringWithCompatibleFont(str));
      }
   }

   public int substringWidth(String str, int offset, int len) {
      return this.impl.stringWidth(this.impl.getStringWithCompatibleFont(str.substring(offset, offset + len)));
   }

   Font(com.nokia.mid.impl.isa.ui.gdi.Font impl) {
      this.impl = impl;
   }

   com.nokia.mid.impl.isa.ui.gdi.Font getImpl() {
      return this.impl;
   }
}
