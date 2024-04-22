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

   TextLine(String text, int[] dimensions, Font f, int numberOfChars, int offset) {
      this._strText = text;
      this._font = f;
      this._textWidth = dimensions[0];
      this._textHeight = dimensions[1];
      this._textBaseline = dimensions[2];
      this._textAbove = dimensions[3];
      this._flowDirection = dimensions[4];
      this._resultFlags = dimensions[5];
      this._numberOfChars = numberOfChars;
      this._offset = offset;
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

   public void setAlignment(int alignment) {
      if (alignment != 0 && alignment != 1 && alignment != 2 && alignment != 3) {
         throw new IllegalArgumentException();
      } else {
         this._alignment = alignment;
      }
   }

   public void setEllipsis(String strEllipsis) {
      if (strEllipsis != null) {
         String actualString = this._strText.substring(this._offset, this._offset + this._numberOfChars);
         int skipFromIx = actualString.indexOf(10);
         int lastLFIx = actualString.indexOf(13);
         if (skipFromIx == -1 || lastLFIx < skipFromIx && lastLFIx > -1) {
            skipFromIx = lastLFIx;
         }

         if (skipFromIx > -1) {
            actualString = actualString.substring(0, skipFromIx);
         }

         this._strText = actualString.concat(strEllipsis);
         this._offset = 0;
         this._numberOfChars = this._strText.length();
      }
   }
}
