package com.nokia.mid.impl.isa.ui.gdi;

public class TextLine {
   public static final int ALIGNMENT_UNDEFINED = 0;
   public static final int ALIGNMENT_LEFT = 1;
   public static final int ALIGNMENT_CENTER = 2;
   public static final int ALIGNMENT_RIGHT = 3;
   int _resultFlags;
   String _strText;
   int _textWidth;
   int _textHeight;
   int _textBaseline;
   int _textAbove;
   int _flowDirection;
   int _numberOfChars;
   int _offset;
   Font _font = null;
   private int _alignment = 0;

   TextLine(String var1, int[] var2, Font var3, int var4, int var5) {
      this._strText = var1;
      this._font = var3;
      this._textWidth = var2[0];
      this._textHeight = var2[1];
      this._textBaseline = var2[2];
      this._textAbove = var2[3];
      this._flowDirection = var2[4];
      this._resultFlags = var2[5];
      this._numberOfChars = var4;
      this._offset = var5;
   }

   public int getTextLineWidth() {
      return this._textWidth;
   }

   public int getTextLineHeight() {
      return this._textHeight;
   }

   public int getTextLineBase() {
      return this._textBaseline;
   }

   public int getTextLineLeading() {
      return this._textAbove;
   }

   public Font getTextLineFont() {
      return this._font;
   }

   public int getAlignment() {
      return this._alignment;
   }

   public boolean isTextFlowLTR() {
      return this._flowDirection <= 0;
   }

   public boolean isTruncated() {
      return this._offset + this._numberOfChars < this._strText.length();
   }

   public void setAlignment(int var1) {
      if (var1 != 0 && var1 != 1 && var1 != 2 && var1 != 3) {
         throw new IllegalArgumentException();
      } else {
         this._alignment = var1;
      }
   }

   public void setEllipsis(String var1) {
      if (var1 != null) {
         String var2 = this._strText.substring(this._offset, this._offset + this._numberOfChars);
         this._strText = var2.concat(var1);
         this._offset = 0;
         this._numberOfChars += var1.length();
      }
   }
}
