package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.ReinitialiseListener;
import com.nokia.mid.impl.isa.ui.TextDatabase;
import com.nokia.mid.impl.isa.ui.gdi.ColorCtrl;
import com.nokia.mid.impl.isa.ui.gdi.TextBreaker;
import com.nokia.mid.impl.isa.ui.gdi.TextLine;
import com.nokia.mid.impl.isa.ui.style.UIStyle;
import com.nokia.mid.impl.isa.ui.style.Zone;
import java.util.Vector;

public abstract class Item {
   public static final int LAYOUT_DEFAULT = 0;
   public static final int LAYOUT_LEFT = 1;
   public static final int LAYOUT_RIGHT = 2;
   public static final int LAYOUT_CENTER = 3;
   public static final int LAYOUT_TOP = 16;
   public static final int LAYOUT_BOTTOM = 32;
   public static final int LAYOUT_VCENTER = 48;
   public static final int LAYOUT_NEWLINE_BEFORE = 256;
   public static final int LAYOUT_NEWLINE_AFTER = 512;
   public static final int LAYOUT_SHRINK = 1024;
   public static final int LAYOUT_EXPAND = 2048;
   public static final int LAYOUT_VSHRINK = 4096;
   public static final int LAYOUT_VEXPAND = 8192;
   public static final int LAYOUT_2 = 16384;
   public static final int PLAIN = 0;
   public static final int HYPERLINK = 1;
   public static final int BUTTON = 2;
   static final int BUTTON_BORDER_WIDTH;
   static final int BUTTON_BORDER_HEIGHT;
   private static String EMPTY_STRING;
   static final int ak;
   private static int al;
   private static final char am;
   private static final char[] an;
   static final Zone ao;
   static String ap;
   static int aq;
   int[] ar;
   boolean hasFocus;
   boolean visible;
   boolean as;
   ItemCommandListener at;
   String label;
   Screen au;
   int av = 0;
   Command aw;
   int ax = -1;
   int ay = -1;
   int az = -1;
   int aA = -1;
   int rowHeight;
   int aB = -1;
   int aC;
   int aD;
   boolean aE = false;
   int aF;
   CommandVector aG = new CommandVector();
   private Font aH = Font.getDefaultFont();
   private Vector aI;
   Item.Label aJ;
   boolean aK;

   Item(String var1) {
      Item.Label var2 = !a(var1) ? new Item.Label(this, var1) : null;
      synchronized(Display.hG) {
         this.label = var1;
         this.aJ = var2;
      }
   }

   public void setLabel(String var1) {
      Item.Label var2 = !a(var1) ? new Item.Label(this, var1) : null;
      synchronized(Display.hG) {
         if (this.au != null && this.au instanceof Alert) {
            throw new IllegalStateException();
         }

         this.label = var1;
         this.aJ = var2;
      }

      this.invalidate();
   }

   public String getLabel() {
      return this.label;
   }

   public int getLayout() {
      return this.av;
   }

   public void setLayout(int var1) {
      synchronized(Display.hG) {
         if (this.au != null && this.au instanceof Alert) {
            throw new IllegalStateException();
         } else {
            this.setLayoutImpl(var1);
            this.invalidate();
         }
      }
   }

   public void addCommand(Command var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         boolean var2;
         synchronized(Display.hG) {
            if (this.au != null && this.au instanceof Alert) {
               throw new IllegalStateException();
            }

            var2 = this.b(var1) && this.A() && this.aG.length() == 1;
         }

         if (var2) {
            this.setPreferredSize(this.ax, this.ay);
         }

      }
   }

   public void removeCommand(Command var1) {
      if (var1 != null) {
         synchronized(Display.hG) {
            if (var1.equals(this.aw)) {
               this.aw = null;
            }

            this.d(var1);
         }
      }
   }

   public void setItemCommandListener(ItemCommandListener var1) {
      synchronized(Display.hG) {
         if (this.au != null && this.au instanceof Alert) {
            throw new IllegalStateException();
         } else {
            this.at = var1;
         }
      }
   }

   public int getPreferredWidth() {
      boolean var1 = false;
      boolean var2 = false;
      int var6;
      int var7;
      synchronized(Display.hG) {
         var6 = this.ax;
         var7 = this.ay;
      }

      return var6 != -1 ? var6 : this.h(var7);
   }

   public int getPreferredHeight() {
      boolean var1 = false;
      boolean var2 = false;
      boolean var3 = false;
      int var7;
      int var8;
      int var9;
      synchronized(Display.hG) {
         var7 = this.ax;
         var8 = this.ay;
         var9 = this.getLabelHeight(-1);
      }

      return var8 != -1 ? var8 : this.b(var7) + var9;
   }

   public void setPreferredSize(int var1, int var2) {
      if (var1 >= -1 && var2 >= -1) {
         int var3 = this.a();
         int var4 = this.b();
         synchronized(Display.hG) {
            if (this.au != null && this.au instanceof Alert) {
               throw new IllegalStateException();
            } else {
               this.ax = var1 != -1 && var1 < var3 ? var3 : var1;
               this.ax = this.ax > ak ? ak : this.ax;
               if (this.aJ != null && (this.ax < this.getLabelWidth() || this.getLabelWidth() != ak)) {
                  this.aJ.g(this.ax);
               }

               var1 = this.getLabelHeight(this.ax);
               this.ay = var2 != -1 && var2 < var4 + var1 ? var4 + var1 : var2;
               this.az = this.ay;
               this.invalidate();
            }
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public int getMinimumWidth() {
      return this.a();
   }

   public int getMinimumHeight() {
      int var1 = this.b();
      synchronized(Display.hG) {
         return this.ay != -1 ? this.ay : var1 + this.getLabelHeight(-1);
      }
   }

   public void setDefaultCommand(Command var1) {
      boolean var2;
      synchronized(Display.hG) {
         var2 = this.c(var1) && this.A() && this.aG.length() == 1;
      }

      if (var2) {
         this.setPreferredSize(this.ax, this.ay);
      }

   }

   public void notifyStateChanged() {
      synchronized(Display.hG) {
         if (this.au != null && this.au instanceof Form) {
            this.au.b(this);
         } else {
            throw new IllegalStateException();
         }
      }
   }

   boolean m() {
      return true;
   }

   boolean isFocusable() {
      return false;
   }

   int getMinimumScroll(int var1) {
      return Form.FORM_MAX_SCROLL;
   }

   final void a(Graphics var1, int var2, int var3, int var4, int var5, boolean var6) {
      if (this.aG != null && this.aG.length() > 0) {
         ColorCtrl var7;
         com.nokia.mid.impl.isa.ui.gdi.Graphics var10;
         int var8 = (var7 = (var10 = var1.getImpl()).getColorCtrl()).getFgColor();
         int var9 = var5 - this.aH.getHeight();
         if (var6) {
            UIStyle.getUIStyle().drawHighlightBar(var10, var2, var3, var4 + 1, var5, false);
            var7.setFgColor(UIStyle.COLOUR_HIGHLIGHT_TEXT);
         } else {
            var7.setFgColor(UIStyle.COLOUR_TEXT);
         }

         var10.drawTextInArea(var2, var3 + var9 / 2 + 1, var4, var5 - var9, this.aI, 2);
         var7.setFgColor(var8);
      }
   }

   final int a(int var1, Font var2) {
      int var3 = 0;
      this.aI = new Vector();
      this.aH = var2 == null ? Font.getDefaultFont() : var2;
      TextBreaker var4;
      (var4 = TextBreaker.getBreaker(this.aH.getImpl(), EMPTY_STRING, false)).setLeading(0);

      TextLine var5;
      while((var5 = var4.getTextLine(var1)) != null) {
         var3 += var5.getTextLineHeight();
         this.aI.addElement(var5);
      }

      var4.destroyBreaker();
      return var3 + 1;
   }

   final int a(Font var1) {
      this.aH = var1 == null ? Font.getDefaultFont() : var1;
      this.aC = this.aH.charsWidth(EMPTY_STRING.toCharArray(), 0, EMPTY_STRING.length());
      this.aC = this.aC > ak ? ak : this.aC;
      return this.aC;
   }

   int getLabelHeight(int var1) {
      return this.aJ == null ? 0 : this.aJ.getLabelHeight();
   }

   int getLabelWidth() {
      return this.aJ == null ? 0 : this.aJ.C;
   }

   abstract int a(int var1);

   abstract int b(int var1);

   abstract int a();

   abstract int b();

   final int h(int var1) {
      return this.aJ == null ? this.a(var1) : Math.max(this.a(var1), this.getLabelWidth());
   }

   int s() {
      return this.ar[2];
   }

   int t() {
      return this.ar[0];
   }

   final boolean u() {
      return (this.av & 1024) == 1024;
   }

   final boolean v() {
      return (this.av & 2048) == 2048;
   }

   final boolean w() {
      return (this.av & 4096) == 4096;
   }

   boolean d() {
      return (this.av & 512) == 512;
   }

   boolean e() {
      if (this.label != null && this.label.length() > 0 && this.label.charAt(0) == '\n') {
         return true;
      } else {
         return (this.av & 256) == 256;
      }
   }

   boolean c() {
      return false;
   }

   void a(Graphics var1, int var2, int var3, boolean var4) {
      var1 = var1;
      Item var6 = this;
      synchronized(Display.hG) {
         if (var6.aJ != null) {
            var6.aJ.a(var1);
         }

      }
   }

   void repaint() {
      if (this.ar != null) {
         this.repaint(0, 0, this.ar[2], this.ar[3]);
      }

   }

   void repaint(int var1, int var2, int var3, int var4) {
      if (this.au != null) {
         if (var1 < 0 || var1 <= this.ar[2]) {
            if (var2 < 0 || var2 <= this.ar[3]) {
               ((Form)this.au).ag();
            }
         }
      }
   }

   void g(int var1, int var2) {
      synchronized(Display.hG) {
         if (this.aJ != null) {
            this.aJ.g(var1);
         }

      }
   }

   void invalidate() {
      synchronized(Display.hG) {
         if (this.au != null) {
            this.au.invalidate();
         }

      }
   }

   boolean a(int var1, int var2, int var3, int[] var4) {
      this.hasFocus = true;
      return false;
   }

   void f() {
      this.hasFocus = false;
   }

   void c(int var1, int var2) {
      boolean var6 = false;
      synchronized(Display.hG) {
         if (var1 == -10 && this.aw != null && this.at != null) {
            var6 = true;
         }
      }

      if (var6) {
         synchronized(Display.hH) {
            this.at.commandAction(this.aw, this);
         }
      }
   }

   void h(int var1, int var2) {
   }

   void i(int var1, int var2) {
   }

   void o() {
      this.visible = true;
   }

   void x() {
      this.visible = false;
   }

   boolean b(Command var1) {
      boolean var2;
      if ((var2 = this.aG.f(var1)) && this.au != null && this.hasFocus && this.isFocusable()) {
         this.au.c(true);
      }

      return var2;
   }

   boolean c(Command var1) {
      boolean var2 = false;
      if (this.au != null && this.au instanceof Alert) {
         throw new IllegalStateException();
      } else if (this.aw == var1) {
         return false;
      } else {
         this.aw = var1;
         if (var1 != null) {
            var2 = this.b(var1);
         }

         if (!var2 && this.au != null) {
            this.au.c(true);
         }

         return var2;
      }
   }

   boolean d(Command var1) {
      boolean var2;
      if ((var2 = this.aG.h(var1)) && this.au != null && this.hasFocus && this.isFocusable()) {
         this.au.c(true);
      }

      return var2;
   }

   Command[] getExtraCommands() {
      return null;
   }

   boolean a(Command var1) {
      return false;
   }

   ItemCommandListener getItemCommandListener() {
      return this.at;
   }

   void setOwner(Screen var1) {
      synchronized(Display.hG) {
         if (this.au != null && var1 != null) {
            throw new IllegalStateException();
         } else {
            this.au = var1;
         }
      }
   }

   void setLayoutImpl(int var1) {
      if ((var1 & ~al) != 0) {
         throw new IllegalArgumentException();
      } else {
         this.av = var1;
         this.aF = var1 & 240;
      }
   }

   int y() {
      int var1 = this.av & 255;
      int var2 = UIStyle.isAlignedLeftToRight ? 1 : 2;
      switch(var1) {
      case 0:
         var1 = 16 | var2;
         break;
      case 1:
      case 2:
      case 3:
         var1 |= 16;
         break;
      case 16:
      case 32:
      case 48:
         var1 |= var2;
      }

      this.aF = var1 & 240;
      return var1;
   }

   boolean a(boolean var1, int var2, int var3) {
      this.aE = false;
      return this.aE;
   }

   static boolean a(String var0) {
      if (var0 == null) {
         return true;
      } else {
         for(int var1 = 0; var1 < var0.length(); ++var1) {
            if (var0.charAt(var1) != ' ') {
               return false;
            }
         }

         return true;
      }
   }

   boolean n() {
      return false;
   }

   boolean z() {
      return false;
   }

   static String b(String var0) {
      String var1 = null;
      if (var0 != null) {
         int var2 = (var1 = var0.trim()).length() - 1;
         if (var1.length() == 0) {
            return null;
         }

         int var3 = -1;
         boolean var4 = false;

         for(int var5 = 0; var5 < an.length; ++var5) {
            char var6 = an[var5];
            var3 = var1.lastIndexOf(var6);
            if (var2 == var3) {
               var3 = var0.lastIndexOf(var6);
               var4 = true;
               break;
            }

            var3 = -1;
         }

         if (-1 == var3) {
            var3 = var0.lastIndexOf(var1.charAt(var2)) + 1;
         }

         StringBuffer var7 = new StringBuffer(var0);
         if (var4) {
            var7.setCharAt(var3, am);
         } else {
            var7.insert(var3, am);
         }

         var1 = var7.toString();
      }

      return var1;
   }

   boolean A() {
      return false;
   }

   final boolean j(int var1, int var2) {
      if (this.ar == null) {
         return false;
      } else {
         boolean var3 = false;
         if (var1 <= this.ar[1] && var1 + var2 >= this.ar[1]) {
            var3 = true;
         } else if (var1 <= this.ar[1] + this.ar[3] && var1 + var2 >= this.ar[1] + this.ar[3]) {
            var3 = true;
         } else if (this.ar[3] > var2 && this.ar[1] < var1 && this.ar[1] + this.ar[3] > var1 + var2) {
            var3 = true;
         }

         return var3;
      }
   }

   static {
      BUTTON_BORDER_WIDTH = UIStyle.BUTTON_BORDER_WIDTH;
      BUTTON_BORDER_HEIGHT = UIStyle.BUTTON_BORDER_HEIGHT;
      EMPTY_STRING = TextDatabase.getText(33);
      ak = Displayable.eL.width;
      am = TextDatabase.getText(36).charAt(0);
      an = new char[]{':', '：'};
      ao = Displayable.eI.getZone(21);
      new Font(ao.getFont());
      ap = TextDatabase.getText(34);
      aq = Font.getFont(0, 1, 0).charsWidth(ap.toCharArray(), 0, ap.length());
      al = 32563;
      UIStyle.registerReinitialiseListener(new ReinitialiseListener() {
         public void reinitialiseForForeground() {
            Item.ap = TextDatabase.getText(34);
            Item.aq = Font.getFont(0, 1, 0).charsWidth(Item.ap.toCharArray(), 0, Item.ap.length());
         }
      });
   }

   class Label {
      private Vector B;
      int C;
      private int D;
      private String E;
      private final Item F;

      Label(Item var1, String var2) {
         this.F = var1;
         this.B = new Vector();
         var1.label = var2;
         this.setLabelString(var1.label);
      }

      void setLabelString(String var1) {
         int var2 = this.F.b();
         synchronized(Display.hG) {
            this.D = 0;
            this.C = 0;
            this.E = var1.charAt(0) == '\n' ? var1.substring(1) : var1;
            this.F.label = var1;
            int var5;
            if (this.F.ax == -1) {
               var5 = Item.ak;
            } else {
               if (this.F.ax < Item.aq) {
                  this.F.ax = Item.aq;
               }

               var5 = this.F.ax;
            }

            this.g(var5);
            var5 = this.D + var2;
            this.F.ay = this.F.az != -1 && this.F.az < var5 ? var5 : this.F.az;
         }
      }

      final void g(int var1) {
         String var2 = Item.b(this.E);
         this.B.removeAllElements();
         TextLine var3 = null;
         TextBreaker var4;
         (var4 = TextBreaker.getBreaker()).setFont(Displayable.eI.getZone(20).getFont());
         var4.setLeading(TextBreaker.DEFAULT_TEXT_LEADING, false);
         var4.setText(var2);
         var4.setTruncation(false);

         while((var3 = var4.getTextLine(var1)) != null && this.B.size() < 2) {
            this.B.addElement(var3);
            if (this.B.size() == 1) {
               var4.setTruncation(true);
            }
         }

         var4.destroyBreaker();
         Item.Label var5;
         (var5 = this).C = 0;
         var5.D = 0;
         if (var5.B != null && var5.B.size() != 0) {
            TextLine var6 = (TextLine)var5.B.firstElement();
            var5.C = var5.B.size() == 1 ? var6.getTextLineWidth() : var1;
            var1 = ((TextLine)var5.B.lastElement()).getTextLineHeight();
            var5.D = var1 * var5.B.size() + UIStyle.LABEL_PAD_AFTER;
         }
      }

      int getLabelHeight() {
         return this.F.aB > 0 ? this.D + UIStyle.LABEL_PAD_BEFORE : this.D;
      }

      final int a(Graphics var1) {
         if (Item.a(this.E)) {
            return -1;
         } else {
            int var2 = var1.getTranslateX();
            int var3 = var1.getTranslateY();
            com.nokia.mid.impl.isa.ui.gdi.Graphics var4;
            ColorCtrl var5;
            int var6 = (var5 = (var4 = var1.getImpl()).getColorCtrl()).getFgColor();
            var5.setFgColor(UIStyle.COLOUR_LABEL_TEXT);
            var4.drawTextInArea(UIStyle.isAlignedLeftToRight ? var2 : var2 + this.F.ar[2] - this.C, this.F.aB > 0 ? var3 + UIStyle.LABEL_PAD_BEFORE : var3, this.C, this.D, this.B, UIStyle.isAlignedLeftToRight ? 1 : 3);
            var1.translate(0, this.getLabelHeight());
            var5.setFgColor(var6);
            return 0;
         }
      }
   }
}
