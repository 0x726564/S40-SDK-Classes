package com.nokia.mid.impl.isa.ui.gdi;

import com.nokia.mid.impl.isa.ui.ReinitialiseListener;
import com.nokia.mid.impl.isa.ui.TextDatabase;
import com.nokia.mid.impl.isa.ui.style.UIStyle;
import com.nokia.mid.impl.isa.ui.style.Zone;
import java.util.Enumeration;
import java.util.Vector;

public class TextBreaker {
   public static final int TEXTLINE_MAX_WIDTH = 32767;
   static final int TEXTLINE_MAX_IMPL_WIDTH = 127;
   public static final int OFFSET_NO_BREAK_DONE = 0;
   public static int NBR_OF_AREAS_AS_NEEDED;
   public static final int DEFAULT_TEXT_LEADING;
   public static final int TEXTLINE_HEIGHT_IGNORE = -1;
   static Font _defaultFont;
   private static Vector _breakerInstances;
   private int _nativeBufferRef = 0;
   private int _nativeBreakerRef = 0;
   private int _temp_line = 0;
   private int _current_breaker_offset = 0;
   private int current_start_offset = 0;
   private Font _font;
   private boolean _word_wrapping_allowed;
   private boolean _breaker_in_use;
   private boolean _isR2Left;
   private int _leading;
   private boolean _leading_on_first_line;
   private boolean _is_first_line;
   private String strText;
   private String original_strText;
   private int[] textParams;
   private boolean applyTruncate;
   static boolean textFlow;

   public static synchronized TextBreaker getBreaker() {
      return getBreaker((Font)null, (String)null, true);
   }

   public static synchronized TextBreaker getBreaker(Font font, String text, boolean reset) {
      TextBreaker breaker = null;
      int cnt = 0;

      boolean found;
      for(found = false; cnt < _breakerInstances.size() && !found; ++cnt) {
         breaker = (TextBreaker)_breakerInstances.elementAt(cnt);
         if (breaker != null && !breaker._breaker_in_use) {
            found = true;
            breaker._breaker_in_use = true;
            if (reset) {
               breaker._word_wrapping_allowed = false;
               breaker._leading = DEFAULT_TEXT_LEADING;
               breaker._leading_on_first_line = true;
            }
         }
      }

      if (!found) {
         breaker = new TextBreaker();
         _breakerInstances.addElement(breaker);
      }

      if (null == font) {
         breaker._font = _defaultFont;
      } else {
         breaker._font = font;
      }

      if (null != text) {
         breaker.setText(text);
      }

      return breaker;
   }

   public static boolean breakTextInArea(int width, int height, int maxNbrOfAreas, Font font, String text, Vector textLines) {
      return breakTextInArea(width, height, maxNbrOfAreas, font, text, DEFAULT_TEXT_LEADING, true, false, textLines, true);
   }

   public static boolean breakTextInArea(int width, int height, int maxNbrOfAreas, Font font, String text, int leading, boolean leadingOnFirstLine, boolean enableWordWrapping, Vector textLines, boolean truncate) {
      return breakTextInArea(width, height, maxNbrOfAreas, font, text, leading, leadingOnFirstLine, enableWordWrapping, textLines, truncate, false);
   }

   public static boolean breakTextInArea(int width, int height, int maxNbrOfAreas, Font font, String text, int leading, boolean leadingOnFirstLine, boolean enableWordWrapping, Vector textLines, boolean truncate, boolean useEllipsisAtMultiplePages) {
      boolean textFits = false;
      synchronized(textLines) {
         if (textLines.size() > 0) {
            textLines.removeAllElements();
         }

         if (null != text && !text.equals("")) {
            TextBreaker breaker = null;
            if (DEFAULT_TEXT_LEADING == leading && leadingOnFirstLine && !enableWordWrapping) {
               breaker = getBreaker(font, text, true);
            } else {
               breaker = getBreaker(font, (String)null, false);
               breaker._leading = leading;
               breaker._leading_on_first_line = leadingOnFirstLine;
               breaker._word_wrapping_allowed = enableWordWrapping;
               breaker.setText(text);
            }

            int firstLineExpectedHeight = leadingOnFirstLine ? font.getDefaultCharHeight() + leading : font.getDefaultCharHeight();
            int lineExpectedHeight = font.getDefaultCharHeight() + leading;
            int firstTwoLineExpectedHeight = firstLineExpectedHeight + lineExpectedHeight;
            int twoLineExpectedHeight = 2 * lineExpectedHeight;
            boolean textBreakComplete = false;
            boolean doingLastAreaLine = false;
            int nbrOfAreasDone = 0;
            Vector breakVec = textLines;
            TextLine tLine = null;
            int offsetY = 0;
            int previousAreaOffsetY = false;
            int lineBreakingWidth = false;
            int lineHeight = false;
            int previousLineStartBreakingOffset = 0;
            int breakingOffset = 0;

            while(!textBreakComplete && (maxNbrOfAreas == NBR_OF_AREAS_AS_NEEDED || maxNbrOfAreas > nbrOfAreasDone)) {
               doingLastAreaLine = false;
               int lineBreakingWidth = width;
               int previousAreaOffsetY = offsetY;
               offsetY = 0;

               while(!doingLastAreaLine && !textBreakComplete) {
                  if (0 == breakVec.size()) {
                     breaker._is_first_line = true;
                     if (offsetY + firstTwoLineExpectedHeight > height) {
                        doingLastAreaLine = true;
                     }
                  } else if (offsetY + twoLineExpectedHeight > height) {
                     doingLastAreaLine = true;
                  }

                  if (doingLastAreaLine && maxNbrOfAreas != 0 && maxNbrOfAreas <= nbrOfAreasDone + 1) {
                     breaker.setTruncation(truncate);
                  }

                  tLine = breaker.getTextLine(lineBreakingWidth);
                  if (useEllipsisAtMultiplePages && doingLastAreaLine) {
                     int elipsesWidth = font.stringWidth(TextDatabase.getText(34));
                     if (tLine._offset + tLine._numberOfChars < tLine._strText.length()) {
                        if (lineBreakingWidth - tLine.getTextLineWidth() >= elipsesWidth) {
                           tLine.setEllipsis(TextDatabase.getText(34));
                        } else {
                           breaker.rebreakTextFromOffset(breakingOffset, true);
                           tLine = breaker.getTextLine(lineBreakingWidth - elipsesWidth);
                           tLine.setEllipsis(TextDatabase.getText(34));
                        }
                     }
                  }

                  if (null == tLine) {
                     textFits = true;
                     textBreakComplete = true;
                  } else {
                     int lineHeight = tLine.getTextLineHeight();
                     if (offsetY + lineHeight <= height) {
                        breakVec.addElement(tLine);
                        offsetY += lineHeight;
                        previousLineStartBreakingOffset = breakingOffset;
                        breakingOffset = breaker.getCurrentOffset();
                        if (breakingOffset >= breaker.original_strText.length()) {
                           if (tLine.getTextLineWidth() <= width) {
                              textFits = true;
                           }

                           textBreakComplete = true;
                        }
                     } else {
                        doingLastAreaLine = true;
                        if ((maxNbrOfAreas == 0 || maxNbrOfAreas > nbrOfAreasDone + 1) && 0 != breakVec.size()) {
                           breaker.rebreakTextFromOffset(breakingOffset, true);
                        } else {
                           if (0 == breakVec.size()) {
                              if (0 == textLines.size()) {
                                 textBreakComplete = true;
                              } else {
                                 breakVec = (Vector)textLines.elementAt(textLines.size() - 1);
                                 tLine = (TextLine)breakVec.elementAt(breakVec.size() - 1);
                                 breakVec.removeElementAt(breakVec.size() - 1);
                                 offsetY = previousAreaOffsetY - tLine.getTextLineHeight();
                              }
                           } else {
                              tLine = (TextLine)breakVec.elementAt(breakVec.size() - 1);
                              breakVec.removeElementAt(breakVec.size() - 1);
                              offsetY -= tLine.getTextLineHeight();
                           }

                           if (!textBreakComplete) {
                              breaker.rebreakTextFromOffset(previousLineStartBreakingOffset, 0 == breakVec.size());
                              tLine = breaker.getTextLine(32767);
                              if (null != tLine && offsetY + tLine.getTextLineHeight() < height) {
                                 breakVec.addElement(tLine);
                                 offsetY += lineHeight;
                              }

                              textBreakComplete = true;
                           }
                        }
                     }
                  }
               }

               ++nbrOfAreasDone;
               if (!textBreakComplete && (maxNbrOfAreas == NBR_OF_AREAS_AS_NEEDED || maxNbrOfAreas < nbrOfAreasDone)) {
                  if (nbrOfAreasDone == 1) {
                     breakVec = new Vector(textLines.size());
                     Enumeration tle = textLines.elements();

                     while(tle.hasMoreElements()) {
                        breakVec.addElement(tle.nextElement());
                     }

                     textLines.removeAllElements();
                  }

                  textLines.addElement(breakVec);
                  breakVec = new Vector(height / firstLineExpectedHeight, 1);
               } else {
                  if (nbrOfAreasDone != 1) {
                     breakVec.trimToSize();
                     textLines.addElement(breakVec);
                  }

                  textLines.trimToSize();
               }
            }

            breaker.destroyBreaker();
            return textFits;
         } else {
            return true;
         }
      }
   }

   public static boolean breakTextInZone(Zone textZone, int maxNbrOfZones, String text, Vector textLines) {
      return breakTextInArea(textZone.width, textZone.height, maxNbrOfZones, textZone.getFont(), text, DEFAULT_TEXT_LEADING, true, false, textLines, true);
   }

   public static boolean breakTextInZone(Zone textZone, int maxNbrOfZones, String text, int leading, boolean leadingOnFirstLine, boolean enableWordWrapping, Vector textLines, boolean truncate, boolean useEllipsisAtMultiplePages) {
      return breakTextInArea(textZone.width, textZone.height, maxNbrOfZones, textZone.getFont(), text, leading, leadingOnFirstLine, enableWordWrapping, textLines, truncate, useEllipsisAtMultiplePages);
   }

   public static boolean breakTextInZone(Zone textZone, int maxNbrOfZones, String text, int leading, boolean leadingOnFirstLine, boolean enableWordWrapping, Vector textLines, boolean truncate) {
      return breakTextInArea(textZone.width, textZone.height, maxNbrOfZones, textZone.getFont(), text, leading, leadingOnFirstLine, enableWordWrapping, textLines, truncate);
   }

   public static TextLine breakOneLineTextInArea(int width, int height, Font font, String text, int leading, boolean enableWordWrapping, boolean truncate, boolean useNativeDigits) {
      if (null != text && !text.equals("")) {
         TextBreaker breaker = null;
         if (DEFAULT_TEXT_LEADING == leading && !enableWordWrapping) {
            breaker = getBreaker(font, text, true);
         } else {
            breaker = getBreaker(font, (String)null, false);
            breaker._leading = leading;
            if (0 != leading) {
               breaker._leading_on_first_line = true;
            }

            breaker._word_wrapping_allowed = enableWordWrapping;
            breaker.setText(text);
         }

         breaker.setTruncation(truncate);
         TextLine tLine = useNativeDigits ? breaker.getTextLine(width, false, true) : breaker.getTextLine(width);
         breaker.destroyBreaker();
         if (-1 != height && null != tLine && tLine.getTextLineHeight() > height) {
            tLine = null;
         }

         return tLine;
      } else {
         return null;
      }
   }

   public static TextLine breakOneLineTextInZone(Zone textZone, String text) {
      return breakOneLineTextInArea(32767, textZone.height, textZone.getFont(), text, DEFAULT_TEXT_LEADING, false, false, true);
   }

   public static TextLine breakOneLineTextInZone(Zone textZone, boolean useWidth, boolean useHeight, String text, int leading, boolean enableWordWrapping) {
      TextLine tLine = null;
      int w = useWidth ? textZone.width - textZone.getMarginLeft() - textZone.getMarginRight() : 32767;
      int h = useHeight ? textZone.height - textZone.getMarginBottom() - textZone.getMarginTop() : -1;
      tLine = breakOneLineTextInArea(w, h, textZone.getFont(), text, leading, enableWordWrapping, false, true);
      return tLine;
   }

   TextBreaker() {
      this._font = _defaultFont;
      this._word_wrapping_allowed = false;
      this._breaker_in_use = true;
      this._isR2Left = false;
      this._leading = DEFAULT_TEXT_LEADING;
      this._leading_on_first_line = true;
      this._is_first_line = true;
      this.textParams = new int[6];
      this.applyTruncate = false;
   }

   public synchronized TextLine getTextLine(int availableWidth) {
      return this.getTextLine(availableWidth, false, false);
   }

   public synchronized TextLine getTextLine(int availableWidth, boolean bForceWordWrap, boolean useNativeDigits) {
      TextLine tLine = null;
      if (this._current_breaker_offset < this.strText.length()) {
         int numberOfChars = this.getLine(availableWidth, this.textParams, this._current_breaker_offset, bForceWordWrap, useNativeDigits);
         if (numberOfChars != 0) {
            tLine = new TextLine(this.strText, this.textParams, this._font, numberOfChars, this._current_breaker_offset);
            if (this._is_first_line) {
               this._is_first_line = false;
            }
         }

         this._current_breaker_offset += numberOfChars;
      }

      return tLine;
   }

   public synchronized void setText(String text) {
      if (text != null) {
         this.strText = text;
         if (text != "" && this.getNativeUCS2StringLength(text) != text.length()) {
            StringBuffer sourceBuf = new StringBuffer(text);
            StringBuffer result = new StringBuffer();

            for(int i = 0; i < sourceBuf.length(); ++i) {
               char currentChar = sourceBuf.charAt(i);
               if (currentChar != 0) {
                  result.append(currentChar);
               }
            }

            if (result.length() != 0) {
               this.strText = result.toString();
            } else {
               this.strText = "";
            }
         }

         this.setTextInternal();
      }

   }

   private void setTextInternal() {
      this.original_strText = this.strText;
      this._is_first_line = true;
      this._current_breaker_offset = 0;
      this.current_start_offset = 0;
   }

   public void rebreakTextFromOffset(int offset, boolean isFirstLine) {
      if (offset >= 0 && offset < this.original_strText.length()) {
         int realOffset = 0 == offset ? 0 : offset;
         if (0 == realOffset) {
            this.strText = this.original_strText;
         } else {
            this.strText = this.original_strText.substring(realOffset);
         }

         this._is_first_line = isFirstLine;
         this._current_breaker_offset = 0;
         if (0 == offset) {
            this.current_start_offset = 0;
         } else {
            this.current_start_offset = offset;
         }
      }

   }

   public int getCurrentOffset() {
      int current_offset = this._current_breaker_offset;
      if (0 != this.current_start_offset) {
         if (0 == this._current_breaker_offset) {
            current_offset = this.current_start_offset;
         } else {
            current_offset = this.current_start_offset + this._current_breaker_offset;
         }
      }

      return current_offset;
   }

   public void setFont(Font font) {
      if (font != null) {
         this._font = font;
      }

   }

   public void enableWordWrapping(boolean allowWordWrapping) {
      this._word_wrapping_allowed = allowWordWrapping;
   }

   public boolean isWordWrappingAllowed() {
      return this._word_wrapping_allowed;
   }

   public int getLeading() {
      return this._leading;
   }

   public void setLeading(int leading) {
      this._leading = leading;
   }

   public void setLeading(int leading, boolean leadingOnFirstLine) {
      this._leading = leading;
      this._leading_on_first_line = leadingOnFirstLine;
   }

   public void setLeadingOnFirstLine(boolean leadingOnFirstLine) {
      this._leading_on_first_line = leadingOnFirstLine;
   }

   public void setFirstLine(boolean isFirstLine) {
      this._is_first_line = isFirstLine;
   }

   public boolean isFirstLine() {
      return this._is_first_line;
   }

   private native int getLine(int var1, int[] var2, int var3, boolean var4, boolean var5);

   private native int getNativeUCS2StringLength(String var1);

   public void destroyBreaker() {
      Class clazz = this.getClass();
      synchronized(clazz) {
         this._breaker_in_use = false;
         this.applyTruncate = false;
      }
   }

   public void setTruncation(boolean truncate) {
      this.applyTruncate = truncate;
   }

   static {
      UIStyle.registerReinitialiseListener(new ReinitialiseListener() {
         public void reinitialiseForForeground() {
            TextBreaker.textFlow = UIStyle.isAlignedLeftToRight;
            TextBreaker._defaultFont = UIStyle.getUIStyle().getDefaultFont();
         }
      });
      NBR_OF_AREAS_AS_NEEDED = -1;
      DEFAULT_TEXT_LEADING = UIStyle.DEFAULT_TEXT_LEADING;
      _defaultFont = UIStyle.getUIStyle().getDefaultFont();
      _breakerInstances = new Vector(2);
      textFlow = UIStyle.isAlignedLeftToRight;
   }
}
