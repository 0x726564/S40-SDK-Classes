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
   static final int SELECT_SOFTKEY_COMMAND = 9;
   static final int RIGHT_SOFTKEY_COMMAND = 10;
   static final int SEARCH = 11;
   static final int LAST_IN_OPTIONS_COMMAND = 12;
   static final int LEFT_SOFTKEY_COMMAND = 13;
   String label;
   String longLabel;
   String menuLabel;
   private int priority;
   private int commandType;
   private boolean isLabelEmpty;
   final boolean isInternalCommand;
   final boolean isRSKCommand;

   public Command(String var1, String var2, int var3, int var4) {
      this(var1, var3, var4);
      this.longLabel = var2;
      this.menuLabel = var2;
      if (this.menuLabel == null || Displayable.screenNormMainZone.width < Displayable.screenNormMainZone.getFont().stringWidth(this.menuLabel)) {
         this.menuLabel = var1;
      }

   }

   public Command(String var1, int var2, int var3) {
      this.longLabel = null;
      this.menuLabel = null;
      this.isLabelEmpty = false;
      if (var2 != 2 && var2 != 3 && var2 != 7 && var2 != 5 && var2 != 8 && var2 != 4 && var2 != 1 && var2 != 6) {
         throw new IllegalArgumentException();
      } else if (var1 == null) {
         throw new NullPointerException();
      } else {
         if (var1.trim().equals("")) {
            this.isLabelEmpty = true;
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
         this.commandType = var2;
         this.isInternalCommand = false;
         this.isRSKCommand = isRightSoftkeyType(var2);
         this.menuLabel = this.label;
      }
   }

   Command(int var1, int var2) {
      this.longLabel = null;
      this.menuLabel = null;
      this.isLabelEmpty = false;
      this.label = TextDatabase.getText(var2);
      this.isLabelEmpty = true;
      this.priority = 0;
      this.commandType = var1;
      this.isInternalCommand = true;
      this.isRSKCommand = isRightSoftkeyType(var1);
      this.menuLabel = this.label;
   }

   Command(int var1, String var2) {
      this.longLabel = null;
      this.menuLabel = null;
      this.isLabelEmpty = false;
      this.label = var2;
      this.isLabelEmpty = true;
      this.priority = 0;
      this.commandType = var1;
      this.isInternalCommand = true;
      this.isRSKCommand = isRightSoftkeyType(var1);
      this.menuLabel = this.label;
   }

   public int getCommandType() {
      return this.commandType;
   }

   public String getLabel() {
      return this.isLabelEmpty ? "" : this.label;
   }

   public int getPriority() {
      return this.priority;
   }

   public String getLongLabel() {
      return this.longLabel;
   }

   static boolean isRightSoftkeyType(int var0) {
      boolean var1 = false;
      if (var0 == 6 || var0 == 3 || var0 == 2 || var0 == 7 || var0 == 10) {
         var1 = true;
      }

      return var1;
   }
}
