package javax.microedition.lcdui;

class ThreeSoftkeyManager extends SoftkeyManager {
   final void a(Displayable var1, CommandVector var2, CommandVector var3) {
      if (var1 instanceof Alert) {
         Alert var8 = (Alert)var1;
         if (var2 != null && var2.length() == 1 && !var8.isModal()) {
            var2.reset();
         }
      }

      int var9 = -1;
      int var10 = -1;
      int var4 = -1;
      int var5 = var2.length();
      int var6 = 0;

      for(int var7 = 3; var6 < var5 && var7 > 0; ++var6) {
         if (var2.getCommand(var6).f) {
            if (var10 == -1) {
               var10 = var6;
               --var7;
            }
         } else if (var2.getCommand(var6).getCommandType() != 12) {
            if (var2.getCommand(var6).getCommandType() == 13) {
               if (var4 == -1) {
                  var4 = var6;
                  --var7;
               }
            } else if (var9 == -1) {
               var9 = var6;
               --var7;
            }
         }
      }

      Command var12 = var9 != -1 ? var2.getCommand(var9) : null;
      Command var13 = var10 != -1 ? var2.getCommand(var10) : null;
      Command var11 = var4 != -1 ? var2.getCommand(var4) : null;
      var3.setCommand(1, var12);
      var3.setCommand(2, var13);
      var2.h(var12);
      var2.h(var13);
      if (var11 != null) {
         var3.setCommand(0, var11);
         var2.h(var11);
      } else if ((var5 = var2.length()) == 0) {
         var3.setCommand(0, (Command)null);
      } else if (var5 == 1 && !var2.getCommand(0).f) {
         var3.setCommand(0, var2.D(0));
      } else {
         var3.setCommand(0, OptionsMenu.iZ);
      }
   }
}
