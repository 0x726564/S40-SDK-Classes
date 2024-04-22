package javax.microedition.lcdui;

class ThreeSoftkeyManager extends SoftkeyManager {
   void selectSoftkeys(Displayable var1, CommandVector var2, CommandVector var3) {
      if (var1 instanceof TextBox) {
         this.selectSoftkeysForTextbox(var2);
      } else {
         if (var1 instanceof Alert) {
            Alert var4 = (Alert)var1;
            if (var2 != null && var2.length() == 1 && !var4.isModal()) {
               var2.reset();
            }
         }

         int var11 = -1;
         int var5 = -1;
         int var6 = -1;
         int var7 = var2.length();
         int var8 = 0;

         for(int var9 = 3; var8 < var7 && var9 > 0; ++var8) {
            if (var2.getCommand(var8).isRSKCommand) {
               if (var5 == -1) {
                  var5 = var8;
                  --var9;
               }
            } else if (var2.getCommand(var8).getCommandType() != 12) {
               if (var2.getCommand(var8).getCommandType() == 13) {
                  if (var6 == -1) {
                     var6 = var8;
                     --var9;
                  }
               } else if (var11 == -1) {
                  var11 = var8;
                  --var9;
               }
            }
         }

         Command var12 = var11 != -1 ? var2.getCommand(var11) : null;
         Command var13 = var5 != -1 ? var2.getCommand(var5) : null;
         Command var10 = var6 != -1 ? var2.getCommand(var6) : null;
         var3.setCommand(1, var12);
         var3.setCommand(2, var13);
         var2.removeCommand(var12);
         var2.removeCommand(var13);
         if (var10 != null) {
            var3.setCommand(0, var10);
            var2.removeCommand(var10);
         } else {
            var7 = var2.length();
            if (var7 == 0) {
               var3.setCommand(0, (Command)null);
            } else {
               var3.setCommand(0, OptionsMenu.optionsCommand);
            }

         }
      }
   }

   void selectSoftkeysForTextbox(CommandVector var1) {
      int var2 = 0;

      for(int var3 = var1.length(); var2 < var3; ++var2) {
         if (!var1.getCommand(var2).isRSKCommand) {
            var1.promoteCommand(var2);
            break;
         }
      }

   }
}
