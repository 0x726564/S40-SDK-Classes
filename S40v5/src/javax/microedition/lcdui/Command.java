package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.TextDatabase;

public class Command {
   public static final int BACK = 2;
   public static final int CANCEL = 3;
   public static final int EXIT = 7;
   public static final int HELP = 5;
   public static final int ITEM = 8;
   public static final int OK = 4;
   public static final int SCREEN = 1;
   public static final int STOP = 6;
   String label;
   private String b;
   String c;
   private int priority;
   private int d;
   private boolean e;
   final boolean f;

   public Command(String var1, String var2, int var3, int var4) {
      this(var1, var3, var4);
      this.b = var2;
      this.c = var2;
      if (this.c == null || Displayable.eL.width < Displayable.eL.getFont().stringWidth(this.c)) {
         this.c = var1;
      }

   }

   public Command(String var1, int var2, int var3) {
      this.b = null;
      this.c = null;
      this.e = false;
      if (var2 != 2 && var2 != 3 && var2 != 7 && var2 != 5 && var2 != 8 && var2 != 4 && var2 != 1 && var2 != 6) {
         throw new IllegalArgumentException();
      } else if (var1 == null) {
         throw new NullPointerException();
      } else {
         if (var1.trim().equals("")) {
            this.e = true;
            switch(var2) {
            case 1:
               this.label = TextDatabase.getText(9);
               break;
            case 2:
               this.label = TextDatabase.getText(3);
               break;
            case 3:
               this.label = TextDatabase.getText(8);
               break;
            case 4:
               this.label = TextDatabase.getText(6);
               break;
            case 5:
               this.label = TextDatabase.getText(31);
               break;
            case 6:
               this.label = TextDatabase.getText(32);
               break;
            case 7:
               this.label = TextDatabase.getText(30);
               break;
            case 8:
               this.label = TextDatabase.getText(9);
            }
         } else {
            this.label = var1;
         }

         this.priority = var3;
         this.d = var2;
         this.f = c(var2);
         this.c = this.label;
      }
   }

   Command(int var1, int var2) {
      this.b = null;
      this.c = null;
      this.e = false;
      this.label = TextDatabase.getText(var2);
      this.e = true;
      this.priority = 0;
      this.d = var1;
      this.f = c(var1);
      this.c = this.label;
   }

   Command(int var1, String var2) {
      this.b = null;
      this.c = null;
      this.e = false;
      this.label = var2;
      this.e = true;
      this.priority = 0;
      this.d = var1;
      this.f = c(var1);
      this.c = this.label;
   }

   public int getCommandType() {
      return this.d;
   }

   public String getLabel() {
      return this.e ? "" : this.label;
   }

   public int getPriority() {
      return this.priority;
   }

   public String getLongLabel() {
      return this.b;
   }

   private static boolean c(int var0) {
      boolean var1 = false;
      if (var0 == 6 || var0 == 3 || var0 == 2 || var0 == 7 || var0 == 10) {
         var1 = true;
      }

      return var1;
   }
}
