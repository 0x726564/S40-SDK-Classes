package javax.microedition.lcdui;

class ThreeSoftkeyManager extends SoftkeyManager {
   void selectSoftkeys(Displayable forDisplayable, CommandVector options, CommandVector softkeys, boolean optionMenuShowing) {
      if (forDisplayable instanceof Alert) {
         Alert al = (Alert)forDisplayable;
         if (options != null && options.length() == 1 && !al.isModal()) {
            options.reset();
         }
      }

      int middle_index = -1;
      int right_index = -1;
      int left_index = -1;
      int opt_len = options.length();
      int i = 0;

      for(int not_found = 3; i < opt_len && not_found > 0; ++i) {
         if (options.getCommand(i).isRSKCommand) {
            if (right_index == -1) {
               right_index = i;
               --not_found;
            }
         } else if (options.getCommand(i).getCommandType() != 12) {
            if (options.getCommand(i).getCommandType() == 13) {
               if (left_index == -1) {
                  left_index = i;
                  --not_found;
               }
            } else if (middle_index == -1) {
               middle_index = i;
               --not_found;
            }
         }
      }

      Command m_cmd = middle_index != -1 ? options.getCommand(middle_index) : null;
      Command r_cmd = right_index != -1 ? options.getCommand(right_index) : null;
      Command l_cmd = left_index != -1 ? options.getCommand(left_index) : null;
      softkeys.setCommand(1, m_cmd);
      softkeys.setCommand(2, r_cmd);
      options.removeCommand(m_cmd);
      options.removeCommand(r_cmd);
      if (l_cmd != null) {
         softkeys.setCommand(0, l_cmd);
         options.removeCommand(l_cmd);
      } else {
         opt_len = options.length();
         if (opt_len == 0) {
            softkeys.setCommand(0, (Command)null);
         } else if (opt_len == 1 && !options.getCommand(0).isRSKCommand && !optionMenuShowing) {
            softkeys.setCommand(0, options.removeCommandAt(0));
         } else {
            softkeys.setCommand(0, OptionsMenu.optionsCommand);
         }

      }
   }
}
