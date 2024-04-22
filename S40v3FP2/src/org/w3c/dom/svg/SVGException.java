package org.w3c.dom.svg;

public class SVGException extends RuntimeException {
   public short code;
   public static final short SVG_INVALID_VALUE_ERR = 1;
   public static final short SVG_MATRIX_NOT_INVERTABLE = 2;

   public SVGException(short var1, String var2) {
      super(var2);
      this.code = var1;
   }
}
