package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.DateInputField;
import com.nokia.mid.impl.isa.ui.DeviceInfo;
import com.nokia.mid.impl.isa.ui.TextDatabase;
import com.nokia.mid.impl.isa.ui.gdi.TextBreaker;
import com.nokia.mid.impl.isa.ui.gdi.TextLine;
import com.nokia.mid.impl.isa.ui.style.UIStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;

public class DateField extends Item {
   public static final int DATE = 1;
   public static final int TIME = 2;
   public static final int DATE_TIME = 3;
   private static boolean g = DeviceInfo.is24HoursClock();
   Vector i;
   int j;
   Calendar calendar;
   boolean k;
   boolean l;
   TextLine m;
   TextLine n;
   int o;
   boolean p;
   private boolean q;
   private static boolean r = UIStyle.isInline();
   private DateEditor s;

   public DateField(String var1, int var2) {
      this(var1, var2, (TimeZone)null);
   }

   public DateField(String var1, int var2, TimeZone var3) {
      super(var1);
      this.i = new Vector(6);
      this.j = 1;
      this.calendar = null;
      this.k = false;
      this.l = false;
      this.m = null;
      this.n = null;
      this.o = -1;
      this.q = g;
      this.s = null;
      synchronized(Display.hG) {
         if (var3 == null) {
            var3 = TimeZone.getDefault();
         }

         this.q = g;
         this.calendar = Calendar.getInstance(var3);
         var3 = null;

         try {
            Class var6;
            if (r) {
               var6 = Class.forName("javax.microedition.lcdui.DateFieldInlineEditor");
            } else {
               var6 = Class.forName("javax.microedition.lcdui.DateFieldScreenEditor");
            }

            this.s = (DateEditor)var6.newInstance();
            this.s.a(this);
         } catch (Exception var4) {
         }

         this.setInputModeImpl(var2);
      }
   }

   public Date getDate() {
      synchronized(Display.hG) {
         return this.isInitialized() ? this.calendar.getTime() : null;
      }
   }

   public void setDate(Date var1) {
      synchronized(Display.hG) {
         this.setDateImpl(var1);
         this.h();
         this.invalidate();
      }
   }

   public int getInputMode() {
      return this.j;
   }

   public void setInputMode(int var1) {
      synchronized(Display.hG) {
         if (var1 != this.j) {
            this.setInputModeImpl(var1);
         }

      }
   }

   void setOwner(Screen var1) {
      super.setOwner(var1);
      synchronized(Display.hG) {
         if (this.au != null) {
            this.s.setTitle(this.au.getTitle());
         }

      }
   }

   final boolean d() {
      if (super.d()) {
         return true;
      } else {
         return (this.av & 16384) != 16384;
      }
   }

   final boolean e() {
      if (super.e()) {
         return true;
      } else {
         return (this.av & 16384) != 16384;
      }
   }

   void setInputModeImpl(int var1) {
      boolean var2 = true;
      switch(var1) {
      case 1:
         this.i.removeAllElements();
         this.i();
         this.l = false;
         if (this.k) {
            var2 = false;
            this.s.a(1, true);
         }
         break;
      case 2:
         this.i.removeAllElements();
         this.j();
         this.k = false;
         this.calendar.set(5, 1);
         this.calendar.set(2, 0);
         this.calendar.set(1, 1970);
         if (this.l) {
            var2 = false;
            this.s.a(2, true);
         }
         break;
      case 3:
         this.i.removeAllElements();
         this.i();
         this.j();
         byte var3 = 0;
         if (this.k && this.l) {
            var3 = 3;
         } else if (this.k) {
            var3 = 1;
         } else if (this.l) {
            var3 = 2;
         }

         if (var3 != 0) {
            var2 = false;
            this.s.a(var3, true);
         }
         break;
      default:
         throw new IllegalArgumentException();
      }

      if (!this.l) {
         this.calendar.set(11, 24);
         this.calendar.set(12, 0);
         this.calendar.set(13, 0);
         this.calendar.set(14, 0);
      }

      this.j = var1;
      if (var2) {
         this.setDateImpl((Date)null);
      } else {
         this.setDateImpl(this.calendar.getTime());
      }

      if (this.p) {
         this.o = 0;
      }

      this.invalidate();
      this.h();
      if (this.au != null) {
         this.au.ag();
      }

   }

   boolean get24HourClockFlag() {
      return this.q;
   }

   void set24HourClockFlag(boolean var1) {
      this.q = var1;
      this.h();
      if (this.au != null) {
         this.au.ag();
      }

   }

   int getHighlight() {
      return this.o;
   }

   final boolean isFocusable() {
      return true;
   }

   int getHeight() {
      synchronized(Display.hG) {
         return this.s != null ? this.s.getHeight() : 0;
      }
   }

   final int a(int var1) {
      synchronized(Display.hG) {
         return this.s.T();
      }
   }

   final int b(int var1) {
      return this.getHeight();
   }

   final int a() {
      return this.a(-1);
   }

   final int b() {
      return this.getHeight();
   }

   final void a(Graphics var1, int var2, int var3, boolean var4) {
      super.a(var1, var2, var3, var4);
      synchronized(Display.hG) {
         this.s.setDisplay(this.au.eV);
         this.s.a(var1, var4);
      }
   }

   final boolean a(int var1, int var2, int var3, int[] var4) {
      super.a(var1, var2, var3, var4);
      synchronized(Display.hG) {
         return this.s.a(var1, var4);
      }
   }

   final void f() {
      super.f();
      synchronized(Display.hG) {
         ((DateInputField)this.i.elementAt(this.o)).setFocus(false);
         this.p = false;
      }
   }

   final void c(int var1, int var2) {
      boolean var3 = false;
      synchronized(Display.hG) {
         var3 = this.s.o(var1);
      }

      if (!var3) {
         super.c(var1, var2);
      }

   }

   Command[] getExtraCommands() {
      synchronized(Display.hG) {
         return this.s.getExtraCommands();
      }
   }

   final boolean a(Command var1) {
      synchronized(Display.hG) {
         return this.s.a(var1);
      }
   }

   final void g() {
      if (this.au != null) {
         if (this.isInitialized()) {
            this.au.b(this);
         }

      }
   }

   private void h() {
      Screen var1 = this.au;
      int var2 = this.calendar.get(5);
      int var3 = this.calendar.get(2) + 1;
      int var4 = this.calendar.get(1);
      int var5 = this.calendar.get(12);
      int var7 = 0;
      int var6;
      if (this.q) {
         var6 = this.calendar.get(11);
      } else {
         var6 = (var6 = this.calendar.get(10)) == 0 ? 12 : var6;
         var7 = this.calendar.get(9);
      }

      DateInputField var8 = null;

      for(int var9 = 0; var9 < this.i.size(); ++var9) {
         switch((var8 = (DateInputField)this.i.elementAt(var9)).getSubType()) {
         case 1:
            var8.setValue(var5);
            break;
         case 2:
            var8.setValue(var6);
            break;
         case 3:
            var8.setValue(var2);
            break;
         case 4:
            var8.setValue(var3);
            break;
         case 5:
            var8.setValue(var4);
            break;
         case 6:
            var8.setValue(var7);
         }
      }

      DateField var10 = this;
      StringBuffer var11 = new StringBuffer(0);
      StringBuffer var12 = new StringBuffer(0);
      boolean var13 = false;
      boolean var14 = false;
      if (!r) {
         if ((this.j == 1 || this.j == 3) && !this.k) {
            var11.append(TextDatabase.getText(28));
            var13 = true;
         }

         if ((this.j == 2 || this.j == 3) && !this.l) {
            var12.append(TextDatabase.getText(27));
            var14 = true;
         }
      }

      for(var6 = 0; var6 < var10.i.size(); ++var6) {
         DateInputField var15;
         switch((var15 = (DateInputField)var10.i.elementAt(var6)).getSubType()) {
         case 1:
         case 2:
         case 6:
            if (!var14) {
               var12.append(var15.getDigits());
            }
            break;
         case 3:
         case 4:
         case 5:
            if (!var13) {
               var11.append(var15.getDigits());
            }
         }
      }

      if (var11.length() > 0) {
         var10.m = TextBreaker.breakOneLineTextInZone(var10.s.getZone(1), true, true, var11.toString(), 0, false);
      } else if (!r) {
         var11.append(TextDatabase.getText(28));
      }

      if (var12.length() > 0) {
         var10.n = TextBreaker.breakOneLineTextInZone(var10.s.getZone(2), true, true, var12.toString(), 0, false);
      }

      if (var10.j == 1) {
         var10.n = null;
      } else if (var10.j == 2) {
         var10.m = null;
      }

      if (var1 != null) {
         var1.ag();
      }

   }

   private void setDateImpl(Date var1) {
      if (var1 == null) {
         this.s.a(this.j, false);
      } else {
         switch(this.j) {
         case 1:
            this.calendar.setTime(var1);
            this.calendar.set(11, 0);
            this.calendar.set(12, 0);
            this.s.a(1, true);
            break;
         case 2:
            int var2 = this.calendar.getTimeZone().getRawOffset();
            long var3;
            if ((var3 = var1.getTime() + (long)var2) < 86400000L && var3 >= 0L) {
               this.calendar.setTime(var1);
               this.s.a(2, true);
            } else {
               this.s.a(2, false);
            }
            break;
         case 3:
            this.calendar.setTime(var1);
            this.s.a(3, true);
         }

         this.calendar.set(13, 0);
         this.calendar.set(14, 0);
      }
   }

   private boolean isInitialized() {
      return this.j == 3 && this.k && this.l || this.j == 1 && this.k || this.j == 2 && this.l;
   }

   private void i() {
      String var1 = DeviceInfo.getDateFormatString();
      DateInputField var2 = null;
      byte var3 = 0;
      int var4 = 0;

      for(int var5 = 0; var5 < var1.length(); ++var5) {
         switch(var1.charAt(var5)) {
         case 'D':
         case 'd':
            if (var3 != 3) {
               (var2 = new DateInputField(3, 1, 31, false)).initialise(!r);
               var3 = 3;
               this.i.addElement(var2);
               ++var4;
            }
            break;
         case 'M':
         case 'm':
            if (var3 != 4) {
               (var2 = new DateInputField(4, 1, 12, false)).initialise(!r);
               var3 = 4;
               this.i.addElement(var2);
               ++var4;
            }
            break;
         case 'Y':
         case 'y':
            if (var3 != 5) {
               (var2 = new DateInputField(5, 1, 9999, false)).initialise(!r);
               var3 = 5;
               this.i.addElement(var2);
               ++var4;
            }
            break;
         default:
            if (var4 != 0) {
               var2.setSeparator(var1.charAt(var5));
            }
         }
      }

   }

   private void j() {
      String var1 = TextDatabase.getText(16);
      DateInputField var2 = null;
      byte var3 = 23;
      byte var4 = 0;
      int var5 = 0;
      if (!this.q) {
         var3 = 12;
         var4 = 1;
      }

      byte var6 = 0;

      for(int var7 = 0; var7 < var1.length(); ++var7) {
         switch(var1.charAt(var7)) {
         case 'H':
         case 'h':
            if (var6 != 2) {
               (var2 = new DateInputField(2, var4, var3, false)).initialise(!r);
               var6 = 2;
               this.i.addElement(var2);
               ++var5;
            }
            break;
         case 'M':
         case 'm':
            if (var6 != 1) {
               (var2 = new DateInputField(1, 0, 59, false)).initialise(!r);
               var6 = 1;
               this.i.addElement(var2);
               ++var5;
            }
            break;
         default:
            if (var5 != 0) {
               var2.setSeparator(var1.charAt(var7));
               var2.initialise(!r);
            }
         }
      }

      if (!this.q) {
         (var2 = new DateInputField(6, 0, 1, false)).initialise(!r);
         this.i.addElement(var2);
      }

   }
}
