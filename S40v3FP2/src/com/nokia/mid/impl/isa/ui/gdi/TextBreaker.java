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

   public static synchronized TextBreaker getBreaker(Font var0, String var1, boolean var2) {
      TextBreaker var3 = null;
      int var4 = 0;

      boolean var5;
      for(var5 = false; var4 < _breakerInstances.size() && !var5; ++var4) {
         var3 = (TextBreaker)_breakerInstances.elementAt(var4);
         if (var3 != null && !var3._breaker_in_use) {
            var5 = true;
            var3._breaker_in_use = true;
            if (var2) {
               var3._word_wrapping_allowed = false;
               var3._leading = DEFAULT_TEXT_LEADING;
               var3._leading_on_first_line = true;
            }
         }
      }

      if (!var5) {
         var3 = new TextBreaker();
         _breakerInstances.addElement(var3);
      }

      if (null == var0) {
         var3._font = _defaultFont;
      } else {
         var3._font = var0;
      }

      if (null != var1) {
         var3.setText(var1);
      }

      return var3;
   }

   public static boolean breakTextInArea(int var0, int var1, int var2, Font var3, String var4, Vector var5) {
      return breakTextInArea(var0, var1, var2, var3, var4, DEFAULT_TEXT_LEADING, true, false, var5, true);
   }

   public static boolean breakTextInArea(int var0, int var1, int var2, Font var3, String var4, int var5, boolean var6, boolean var7, Vector var8, boolean var9) {
      return breakTextInArea(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, false);
   }

   public static boolean breakTextInArea(int var0, int var1, int var2, Font var3, String var4, int var5, boolean var6, boolean var7, Vector var8, boolean var9, boolean var10) {
      boolean var11 = false;
      synchronized(var8) {
         if (var8.size() > 0) {
            var8.removeAllElements();
         }

         if (null != var4 && !var4.equals("")) {
            TextBreaker var13 = null;
            if (DEFAULT_TEXT_LEADING == var5 && var6 && !var7) {
               var13 = getBreaker(var3, var4, true);
            } else {
               var13 = getBreaker(var3, (String)null, false);
               var13._leading = var5;
               var13._leading_on_first_line = var6;
               var13._word_wrapping_allowed = var7;
               var13.setText(var4);
            }

            int var14 = var6 ? var3.getDefaultCharHeight() + var5 : var3.getDefaultCharHeight();
            int var15 = var3.getDefaultCharHeight() + var5;
            int var16 = var14 + var15;
            int var17 = 2 * var15;
            boolean var18 = false;
            boolean var19 = false;
            int var20 = 0;
            Vector var21 = var8;
            TextLine var22 = null;
            int var23 = 0;
            boolean var24 = false;
            boolean var25 = false;
            boolean var26 = false;
            int var27 = 0;
            int var28 = 0;

            while(!var18 && (var2 == NBR_OF_AREAS_AS_NEEDED || var2 > var20)) {
               var19 = false;
               int var33 = var0;
               int var32 = var23;
               var23 = 0;

               while(!var19 && !var18) {
                  if (0 == var21.size()) {
                     var13._is_first_line = true;
                     if (var23 + var16 > var1) {
                        var19 = true;
                     }
                  } else if (var23 + var17 > var1) {
                     var19 = true;
                  }

                  if (var19 && var2 != 0 && var2 <= var20 + 1) {
                     var13.setTruncation(var9);
                  }

                  var22 = var13.getTextLine(var33);
                  if (var10 && var19) {
                     int var29 = var3.stringWidth(TextDatabase.getText(34));
                     if (var22._offset + var22._numberOfChars < var22._strText.length()) {
                        if (var33 - var22.getTextLineWidth() >= var29) {
                           var22.setEllipsis(TextDatabase.getText(34));
                        } else {
                           var13.rebreakTextFromOffset(var28, true);
                           var22 = var13.getTextLine(var33 - var29);
                           var22.setEllipsis(TextDatabase.getText(34));
                        }
                     }
                  }

                  if (null == var22) {
                     var11 = true;
                     var18 = true;
                  } else {
                     int var34 = var22.getTextLineHeight();
                     if (var23 + var34 <= var1) {
                        var21.addElement(var22);
                        var23 += var34;
                        var27 = var28;
                        var28 = var13.getCurrentOffset();
                        if (var28 >= var13.original_strText.length()) {
                           if (var22.getTextLineWidth() <= var0) {
                              var11 = true;
                           }

                           var18 = true;
                        }
                     } else {
                        var19 = true;
                        if ((var2 == 0 || var2 > var20 + 1) && 0 != var21.size()) {
                           var13.rebreakTextFromOffset(var28, true);
                        } else {
                           if (0 == var21.size()) {
                              if (0 == var8.size()) {
                                 var18 = true;
                              } else {
                                 var21 = (Vector)var8.elementAt(var8.size() - 1);
                                 var22 = (TextLine)var21.elementAt(var21.size() - 1);
                                 var21.removeElementAt(var21.size() - 1);
                                 var23 = var32 - var22.getTextLineHeight();
                              }
                           } else {
                              var22 = (TextLine)var21.elementAt(var21.size() - 1);
                              var21.removeElementAt(var21.size() - 1);
                              var23 -= var22.getTextLineHeight();
                           }

                           if (!var18) {
                              var13.rebreakTextFromOffset(var27, 0 == var21.size());
                              var22 = var13.getTextLine(32767);
                              if (null != var22 && var23 + var22.getTextLineHeight() < var1) {
                                 var21.addElement(var22);
                                 var23 += var34;
                              }

                              var18 = true;
                           }
                        }
                     }
                  }
               }

               ++var20;
               if (!var18 && (var2 == NBR_OF_AREAS_AS_NEEDED || var2 < var20)) {
                  if (var20 == 1) {
                     var21 = new Vector(var8.size());
                     Enumeration var35 = var8.elements();

                     while(var35.hasMoreElements()) {
                        var21.addElement(var35.nextElement());
                     }

                     var8.removeAllElements();
                  }

                  var8.addElement(var21);
                  var21 = new Vector(var1 / var14, 1);
               } else {
                  if (var20 != 1) {
                     var21.trimToSize();
                     var8.addElement(var21);
                  }

                  var8.trimToSize();
               }
            }

            var13.destroyBreaker();
            return var11;
         } else {
            return true;
         }
      }
   }

   public static boolean breakTextInZone(Zone var0, int var1, String var2, Vector var3) {
      return breakTextInArea(var0.width, var0.height, var1, var0.getFont(), var2, DEFAULT_TEXT_LEADING, true, false, var3, true);
   }

   public static boolean breakTextInZone(Zone var0, int var1, String var2, int var3, boolean var4, boolean var5, Vector var6, boolean var7, boolean var8) {
      return breakTextInArea(var0.width, var0.height, var1, var0.getFont(), var2, var3, var4, var5, var6, var7, var8);
   }

   public static boolean breakTextInZone(Zone var0, int var1, String var2, int var3, boolean var4, boolean var5, Vector var6, boolean var7) {
      return breakTextInArea(var0.width, var0.height, var1, var0.getFont(), var2, var3, var4, var5, var6, var7);
   }

   public static TextLine breakOneLineTextInArea(int var0, int var1, Font var2, String var3, boolean var4) {
      return breakOneLineTextInArea(var0, var1, var2, var3, DEFAULT_TEXT_LEADING, false, var4);
   }

   public static TextLine breakOneLineTextInArea(int var0, int var1, Font var2, String var3, int var4, boolean var5, boolean var6) {
      if (null != var3 && !var3.equals("")) {
         TextBreaker var7 = null;
         if (DEFAULT_TEXT_LEADING == var4 && !var5) {
            var7 = getBreaker(var2, var3, true);
         } else {
            var7 = getBreaker(var2, (String)null, false);
            var7._leading = var4;
            if (0 != var4) {
               var7._leading_on_first_line = true;
            }

            var7._word_wrapping_allowed = var5;
            var7.setText(var3);
         }

         var7.setTruncation(var6);
         TextLine var8 = var7.getTextLine(var0);
         var7.destroyBreaker();
         if (-1 != var1 && null != var8 && var8.getTextLineHeight() > var1) {
            var8 = null;
         }

         return var8;
      } else {
         return null;
      }
   }

   public static TextLine breakOneLineTextInZone(Zone var0, String var1) {
      return breakOneLineTextInArea(32767, var0.height, var0.getFont(), var1, DEFAULT_TEXT_LEADING, false, false);
   }

   public static TextLine breakOneLineTextInZone(Zone var0, boolean var1, boolean var2, String var3, int var4, boolean var5) {
      TextLine var6 = null;
      int var7 = var1 ? var0.width - var0.getMarginLeft() - var0.getMarginRight() : 32767;
      int var8 = var2 ? var0.height - var0.getMarginBottom() - var0.getMarginTop() : -1;
      var6 = breakOneLineTextInArea(var7, var8, var0.getFont(), var3, var4, var5, false);
      return var6;
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

   public synchronized TextLine getTextLine(int var1) {
      return this.getTextLine(var1, false);
   }

   public synchronized TextLine getTextLine(int var1, boolean var2) {
      TextLine var3 = null;
      if (this._current_breaker_offset < this.strText.length()) {
         int var4 = this.getLine(var1, this.textParams, this._current_breaker_offset, var2);
         if (var4 != 0) {
            var3 = new TextLine(this.strText, this.textParams, this._font, var4, this._current_breaker_offset);
            if (this._is_first_line) {
               this._is_first_line = false;
            }
         }

         this._current_breaker_offset += var4;
      }

      return var3;
   }

   public synchronized void setText(String var1) {
      if (var1 != null) {
         this.strText = var1.replace('\u0000', '\ue000');
         this.setTextInternal();
      }

   }

   private void setTextInternal() {
      this.original_strText = this.strText;
      this._is_first_line = true;
      this._current_breaker_offset = 0;
      this.current_start_offset = 0;
   }

   public void rebreakTextFromOffset(int var1, boolean var2) {
      if (var1 >= 0 && var1 < this.original_strText.length()) {
         int var3 = 0 == var1 ? 0 : var1;
         if (0 == var3) {
            this.strText = this.original_strText;
         } else {
            this.strText = this.original_strText.substring(var3);
         }

         this._is_first_line = var2;
         this._current_breaker_offset = 0;
         if (0 == var1) {
            this.current_start_offset = 0;
         } else {
            this.current_start_offset = var1;
         }
      }

   }

   public int getCurrentOffset() {
      int var1 = this._current_breaker_offset;
      if (0 != this.current_start_offset) {
         if (0 == this._current_breaker_offset) {
            var1 = this.current_start_offset;
         } else {
            var1 = this.current_start_offset + this._current_breaker_offset;
         }
      }

      return var1;
   }

   public void setFont(Font var1) {
      if (var1 != null) {
         this._font = var1;
      }

   }

   public void enableWordWrapping(boolean var1) {
      this._word_wrapping_allowed = var1;
   }

   public boolean isWordWrappingAllowed() {
      return this._word_wrapping_allowed;
   }

   public int getLeading() {
      return this._leading;
   }

   public void setLeading(int var1) {
      this._leading = var1;
   }

   public void setLeading(int var1, boolean var2) {
      this._leading = var1;
      this._leading_on_first_line = var2;
   }

   public void setLeadingOnFirstLine(boolean var1) {
      this._leading_on_first_line = var1;
   }

   public void setFirstLine(boolean var1) {
      this._is_first_line = var1;
   }

   public boolean isFirstLine() {
      return this._is_first_line;
   }

   private native int getLine(int var1, int[] var2, int var3, boolean var4);

   public void destroyBreaker() {
      Class var1 = this.getClass();
      synchronized(var1) {
         this._breaker_in_use = false;
         this.applyTruncate = false;
      }
   }

   public void setTruncation(boolean var1) {
      this.applyTruncate = var1;
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
