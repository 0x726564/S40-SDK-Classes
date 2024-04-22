package javax.microedition.lcdui;

final class CommandVector {
   static final int DEFAULT_VECTOR_SIZE = 0;
   static final int REALLOCATE_GROWTH_SIZE = 1;
   static final int[] listOrder = new int[]{0, 8, 10, 6, 5, 9, 4, 12, 7, 2, 1, 11, 13, 3};
   private Command[] commands = null;
   private int length = 0;

   public CommandVector() {
      this.commands = new Command[0];
      this.length = 0;
   }

   public CommandVector(int size) {
      this.commands = new Command[size];
      this.length = 0;
   }

   public boolean addItemCommand(Command newCommand) {
      if (newCommand == null) {
         return false;
      } else {
         int priority = newCommand.getPriority();
         int index = 0;

         for(Command cmd = null; index < this.length; ++index) {
            cmd = this.commands[index];
            if (cmd == null) {
               break;
            }

            if (cmd == newCommand) {
               return false;
            }

            if (priority < cmd.getPriority()) {
               break;
            }
         }

         this.insertCommand(index, newCommand);
         return true;
      }
   }

   public boolean addCommand(Command newCommand) {
      if (newCommand == null) {
         return false;
      } else {
         int order = listOrder[newCommand.getCommandType()];
         int priority = newCommand.getPriority();
         int index = 0;
         int cmd_order = false;

         for(Command cmd = null; index < this.length; ++index) {
            cmd = this.commands[index];
            if (cmd == null) {
               break;
            }

            if (cmd == newCommand) {
               return false;
            }

            int cmd_order = listOrder[cmd.getCommandType()];
            if (order < cmd_order || order == cmd_order && priority < cmd.getPriority()) {
               break;
            }
         }

         this.insertCommand(index, newCommand);
         return true;
      }
   }

   public void addCommands(Command[] newCommands) {
      this.resize(this.length + newCommands.length);

      for(int i = 0; i < newCommands.length; ++i) {
         this.addCommand(newCommands[i]);
      }

   }

   public boolean removeCommand(Command oldCommand) {
      if (oldCommand == null) {
         return false;
      } else {
         int index = this.indexOfCommand(oldCommand);
         if (index != -1) {
            this.removeCommandAt(index);
            return true;
         } else {
            return false;
         }
      }
   }

   public Command removeCommandAt(int index) {
      if (index >= 0 && index < this.length) {
         Command returnCommand = this.commands[index];
         --this.length;

         while(index < this.length) {
            this.commands[index] = this.commands[index + 1];
            ++index;
         }

         this.commands[this.length] = null;
         return returnCommand;
      } else {
         return null;
      }
   }

   public Command getCommand(int index) {
      return index >= 0 && index < this.length ? this.commands[index] : null;
   }

   public void setCommand(int atIndex, Command value) {
      if (atIndex >= this.length) {
         this.resize(atIndex + 1);
         this.length = atIndex + 1;
      }

      this.commands[atIndex] = value;
   }

   public void insertCommand(int atIndex, Command value) {
      int new_length = this.length + 1;
      if (atIndex > this.length) {
         new_length = atIndex + 1;
         this.resize(new_length);
      } else if (atIndex < 0) {
         atIndex = this.length;
      }

      Command[] src = this.commands;
      if (this.commands.length < new_length) {
         this.commands = new Command[src.length + 1];
         System.arraycopy(src, 0, this.commands, 0, atIndex);
      }

      for(int i = this.length; i > atIndex; --i) {
         this.commands[i] = src[i - 1];
      }

      this.length = new_length;
      this.commands[atIndex] = value;
   }

   public void promoteCommand(int index) {
      if (index >= this.length) {
         throw new IndexOutOfBoundsException();
      } else {
         Command temp = this.commands[index];

         for(int i = index; i > 0; --i) {
            this.commands[i] = this.commands[i - 1];
         }

         this.commands[0] = temp;
      }
   }

   public void promoteCommand(Command command) {
      if (command == null) {
         throw new IllegalArgumentException();
      } else {
         int index = this.indexOfCommand(command);
         if (index != -1) {
            this.promoteCommand(index);
         }

      }
   }

   public void reconstruct(CommandVector original, Command[] extras, CommandVector itemCommands, Command defaultCommand, boolean isMidletCommandsSupported) {
      if (!isMidletCommandsSupported && extras != null && extras.length > 0) {
         this.commands = new Command[extras.length];
         System.arraycopy(extras, 0, this.commands, 0, extras.length);
         this.length = extras.length;
      } else {
         int arrayIndex = 0;
         int new_len = original.length + (extras != null ? extras.length : 0) + (itemCommands != null ? itemCommands.length : 0);
         int len;
         if (new_len > this.commands.length) {
            this.commands = new Command[new_len];
         } else if (new_len < this.commands.length) {
            for(len = new_len; len < this.commands.length; ++len) {
               this.commands[len] = null;
            }
         }

         int i;
         if (extras != null) {
            len = extras.length;

            for(i = 0; i < len; ++i) {
               if (extras[i].getCommandType() == 12) {
                  System.arraycopy(extras, i, this.commands, new_len - (len - i), len - i);
                  len = i;
                  break;
               }
            }

            System.arraycopy(extras, 0, this.commands, 0, len);
            arrayIndex = len;
         }

         if (defaultCommand != null) {
            this.commands[arrayIndex++] = defaultCommand;
         }

         if (itemCommands != null) {
            for(i = 0; i < itemCommands.length; ++i) {
               Command c = itemCommands.commands[i];
               if (c != defaultCommand) {
                  this.commands[arrayIndex++] = c;
               }
            }
         }

         if (original != null) {
            System.arraycopy(original.commands, 0, this.commands, arrayIndex, original.length);
         }

         this.length = new_len;
      }
   }

   public void reset() {
      for(int i = 0; i < this.length; ++i) {
         this.commands[i] = null;
      }

      this.length = 0;
   }

   public int length() {
      return this.length;
   }

   public boolean isNotEmpty() {
      return this.length > 0;
   }

   public int indexOfCommand(Command command) {
      for(int i = 0; i < this.length; ++i) {
         if (command == this.commands[i]) {
            return i;
         }
      }

      return -1;
   }

   public boolean containsCommand(Command cmd) {
      return this.indexOfCommand(cmd) != -1;
   }

   public boolean hasPositiveCommand() {
      int index = 0;

      for(Command cmd = null; index < this.length; ++index) {
         cmd = this.commands[index];
         if (cmd == null) {
            return false;
         }

         if (!cmd.isRSKCommand) {
            return true;
         }
      }

      return false;
   }

   public Command[] getAsArray() {
      Command[] cmds = new Command[this.length];
      System.arraycopy(this.commands, 0, cmds, 0, this.length);
      return cmds;
   }

   private void resize(int newSize) {
      if (newSize > this.commands.length) {
         Command[] nca = new Command[newSize];
         System.arraycopy(this.commands, 0, nca, 0, this.length);
         this.commands = nca;
      }

   }
}
