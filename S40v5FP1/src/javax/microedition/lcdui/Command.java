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

   public Command(String shortLabel, String longLabel, int commandType, int priority) {
      this(shortLabel, commandType, priority);
      this.longLabel = longLabel;
      this.menuLabel = longLabel;
      if (this.menuLabel == null || Displayable.screenNormMainZone.width < Displayable.screenNormMainZone.getFont().stringWidth(this.menuLabel)) {
         this.menuLabel = shortLabel;
      }

   }

   public Command(String label, int commandType, int priority) {
      this.longLabel = null;
      this.menuLabel = null;
      this.isLabelEmpty = false;
      if (commandType != 2 && commandType != 3 && commandType != 7 && commandType != 5 && commandType != 8 && commandType != 4 && commandType != 1 && commandType != 6) {
         throw new IllegalArgumentException();
      } else if (label == null) {
         throw new NullPointerException();
      } else {
         if (label.trim().equals("")) {
            this.isLabelEmpty = true;
            switch(commandType) {
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
            this.label = label;
         }

         this.priority = priority;
         this.commandType = commandType;
         this.isInternalCommand = false;
         this.isRSKCommand = isRightSoftkeyType(commandType);
         this.menuLabel = this.label;
      }
   }

   Command(int commandType, int textId) {
      this.longLabel = null;
      this.menuLabel = null;
      this.isLabelEmpty = false;
      this.label = TextDatabase.getText(textId);
      this.isLabelEmpty = true;
      this.priority = 0;
      this.commandType = commandType;
      this.isInternalCommand = true;
      this.isRSKCommand = isRightSoftkeyType(commandType);
      this.menuLabel = this.label;
   }

   Command(int commandType, String label) {
      this.longLabel = null;
      this.menuLabel = null;
      this.isLabelEmpty = false;
      this.label = label;
      this.isLabelEmpty = true;
      this.priority = 0;
      this.commandType = commandType;
      this.isInternalCommand = true;
      this.isRSKCommand = isRightSoftkeyType(commandType);
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

   static boolean isRightSoftkeyType(int commandType) {
      boolean isRSK = false;
      if (commandType == 6 || commandType == 3 || commandType == 2 || commandType == 7 || commandType == 10) {
         isRSK = true;
      }

      return isRSK;
   }
}
