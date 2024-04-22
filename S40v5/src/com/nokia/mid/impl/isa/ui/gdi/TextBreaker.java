package com.nokia.mid.impl.isa.ui.gdi;

import com.nokia.mid.impl.isa.ui.ReinitialiseListener;
import com.nokia.mid.impl.isa.ui.TextDatabase;
import com.nokia.mid.impl.isa.ui.style.UIStyle;
import com.nokia.mid.impl.isa.ui.style.Zone;
import java.util.Enumeration;
import java.util.Vector;

public class TextBreaker {
   public static final int TEXTLINE_MAX_WIDTH = 32767;
   public static final int OFFSET_NO_BREAK_DONE = 0;
   public static int NBR_OF_AREAS_AS_NEEDED;
   public static final int DEFAULT_TEXT_LEADING;
   public static final int TEXTLINE_HEIGHT_IGNORE = -1;
   static Font cg;
   private static Vector ch;
   private int ci = 0;
   private int cj = 0;
   private Font ck;
   private boolean cl;
   private boolean cm;
   private int cn;
   private boolean co;
   private String cp;
   private String cq;
   private int[] cr;

   public static synchronized TextBreaker getBreaker() {
      return getBreaker((Font)null, (String)null, true);
   }

   public static synchronized TextBreaker getBreaker(Font var0, String var1, boolean var2) {
      TextBreaker var3 = null;
      int var4 = 0;

      boolean var5;
      for(var5 = false; var4 < ch.size() && !var5; ++var4) {
         if ((var3 = (TextBreaker)ch.elementAt(var4)) != null && !var3.cm) {
            var5 = true;
            var3.cm = true;
            if (var2) {
               var3.cl = false;
               var3.cn = DEFAULT_TEXT_LEADING;
            }
         }
      }

      if (!var5) {
         var3 = new TextBreaker();
         ch.addElement(var3);
      }

      if (null == var0) {
         var3.ck = cg;
      } else {
         var3.ck = var0;
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
               (var13 = getBreaker(var3, (String)null, false)).cn = var5;
               var13.cl = var7;
               var13.setText(var4);
            }

            int var25 = var6 ? var3.getDefaultCharHeight() + var5 : var3.getDefaultCharHeight();
            var5 += var3.getDefaultCharHeight();
            int var26 = var25 + var5;
            var5 <<= 1;
            var7 = false;
            boolean var14 = false;
            int var15 = 0;
            Vector var16 = var8;
            TextLine var17 = null;
            int var18 = 0;
            boolean var19 = false;
            boolean var20 = false;
            boolean var21 = false;
            int var22 = 0;
            int var23 = 0;

            while(!var7 && (var2 == NBR_OF_AREAS_AS_NEEDED || var2 > var15)) {
               var14 = false;
               int var28 = var0;
               int var27 = var18;
               var18 = 0;

               while(!var14 && !var7) {
                  if (0 == var16.size()) {
                     var13.co = true;
                     if (var18 + var26 > var1) {
                        var14 = true;
                     }
                  } else if (var18 + var5 > var1) {
                     var14 = true;
                  }

                  if (var14 && var2 != 0 && var2 <= var15 + 1) {
                     var13.setTruncation(var9);
                  }

                  var17 = var13.getTextLine(var28);
                  int var29;
                  if (var10 && var14) {
                     var29 = var3.stringWidth(TextDatabase.getText(34));
                     if (var17.lC + var17.lB < var17.lv.length()) {
                        if (var28 - var17.getTextLineWidth() >= var29) {
                           var17.setEllipsis(TextDatabase.getText(34));
                        } else {
                           var13.rebreakTextFromOffset(var23, true);
                           (var17 = var13.getTextLine(var28 - var29)).setEllipsis(TextDatabase.getText(34));
                        }
                     }
                  }

                  if (null == var17) {
                     var11 = true;
                     var7 = true;
                  } else {
                     var29 = var17.getTextLineHeight();
                     if (var18 + var29 <= var1) {
                        var16.addElement(var17);
                        var18 += var29;
                        var22 = var23;
                        if ((var23 = var13.getCurrentOffset()) >= var13.cq.length()) {
                           if (var17.getTextLineWidth() <= var0) {
                              var11 = true;
                           }

                           var7 = true;
                        }
                     } else {
                        var14 = true;
                        if ((var2 == 0 || var2 > var15 + 1) && 0 != var16.size()) {
                           var13.rebreakTextFromOffset(var23, true);
                        } else {
                           if (0 == var16.size()) {
                              if (0 == var8.size()) {
                                 var7 = true;
                              } else {
                                 var17 = (TextLine)(var16 = (Vector)var8.elementAt(var8.size() - 1)).elementAt(var16.size() - 1);
                                 var16.removeElementAt(var16.size() - 1);
                                 var18 = var27 - var17.getTextLineHeight();
                              }
                           } else {
                              var17 = (TextLine)var16.elementAt(var16.size() - 1);
                              var16.removeElementAt(var16.size() - 1);
                              var18 -= var17.getTextLineHeight();
                           }

                           if (!var7) {
                              var13.rebreakTextFromOffset(var22, 0 == var16.size());
                              var17 = var13.getTextLine(32767);
                              if (null != var17 && var18 + var17.getTextLineHeight() < var1) {
                                 var16.addElement(var17);
                                 var18 += var29;
                              }

                              var7 = true;
                           }
                        }
                     }
                  }
               }

               ++var15;
               if (!var7 && (var2 == NBR_OF_AREAS_AS_NEEDED || var2 < var15)) {
                  if (var15 == 1) {
                     var16 = new Vector(var8.size());
                     Enumeration var30 = var8.elements();

                     while(var30.hasMoreElements()) {
                        var16.addElement(var30.nextElement());
                     }

                     var8.removeAllElements();
                  }

                  var8.addElement(var16);
                  var16 = new Vector(var1 / var25, 1);
               } else {
                  if (var15 != 1) {
                     var16.trimToSize();
                     var8.addElement(var16);
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
            (var7 = getBreaker(var2, (String)null, false)).cn = var4;
            var7.cl = var5;
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
      Object var6 = null;
      int var7 = var1 ? var0.width - var0.getMarginLeft() - var0.getMarginRight() : 32767;
      int var8 = var2 ? var0.height - var0.getMarginBottom() - var0.getMarginTop() : -1;
      return breakOneLineTextInArea(var7, var8, var0.getFont(), var3, var4, var5, false);
   }

   TextBreaker() {
      this.ck = cg;
      this.cl = false;
      this.cm = true;
      this.cn = DEFAULT_TEXT_LEADING;
      this.co = true;
      this.cr = new int[6];
   }

   public synchronized TextLine getTextLine(int var1) {
      return this.getTextLine(var1, false);
   }

   public synchronized TextLine getTextLine(int var1, boolean var2) {
      TextLine var3 = null;
      if (this.ci < this.cp.length()) {
         if ((var1 = this.getLine(var1, this.cr, this.ci, var2)) != 0) {
            var3 = new TextLine(this.cp, this.cr, this.ck, var1, this.ci);
            if (this.co) {
               this.co = false;
            }
         }

         this.ci += var1;
      }

      return var3;
   }

   public synchronized void setText(String var1) {
      if (var1 != null) {
         this.cp = var1.replace('\u0000', '\ue000');
         this.cq = this.cp;
         this.co = true;
         this.ci = 0;
         this.cj = 0;
      }

   }

   public void rebreakTextFromOffset(int var1, boolean var2) {
      if (var1 >= 0 && var1 < this.cq.length()) {
         int var3 = 0 == var1 ? 0 : var1;
         if (0 == var3) {
            this.cp = this.cq;
         } else {
            this.cp = this.cq.substring(var3);
         }

         this.co = var2;
         this.ci = 0;
         if (0 == var1) {
            this.cj = 0;
            return;
         }

         this.cj = var1;
      }

   }

   public int getCurrentOffset() {
      int var1 = this.ci;
      if (0 != this.cj) {
         if (0 == this.ci) {
            var1 = this.cj;
         } else {
            var1 = this.cj + this.ci;
         }
      }

      return var1;
   }

   public void setFont(Font var1) {
      if (var1 != null) {
         this.ck = var1;
      }

   }

   public void enableWordWrapping(boolean var1) {
      this.cl = var1;
   }

   public boolean isWordWrappingAllowed() {
      return this.cl;
   }

   public int getLeading() {
      return this.cn;
   }

   public void setLeading(int var1) {
      this.cn = var1;
   }

   public void setLeading(int var1, boolean var2) {
      this.cn = var1;
   }

   public void setLeadingOnFirstLine(boolean var1) {
   }

   public void setFirstLine(boolean var1) {
      this.co = var1;
   }

   public boolean isFirstLine() {
      return this.co;
   }

   private native int getLine(int var1, int[] var2, int var3, boolean var4);

   public void destroyBreaker() {
      synchronized(this.getClass()) {
         this.cm = false;
      }
   }

   public void setTruncation(boolean var1) {
   }

   static {
      UIStyle.registerReinitialiseListener(new ReinitialiseListener() {
         public void reinitialiseForForeground() {
            TextBreaker.cg = UIStyle.getUIStyle().getDefaultFont();
         }
      });
      NBR_OF_AREAS_AS_NEEDED = -1;
      DEFAULT_TEXT_LEADING = UIStyle.DEFAULT_TEXT_LEADING;
      cg = UIStyle.getUIStyle().getDefaultFont();
      ch = new Vector(2);
   }
}
