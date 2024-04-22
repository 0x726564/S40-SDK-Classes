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

   public CommandVector(int var1) {
      this.commands = new Command[var1];
      this.length = 0;
   }

   public boolean addItemCommand(Command var1) {
      if (var1 == null) {
         return false;
      } else {
         int var2 = var1.getPriority();
         int var3 = 0;

         for(Command var4 = null; var3 < this.length; ++var3) {
            var4 = this.commands[var3];
            if (var4 == null) {
               break;
            }

            if (var4 == var1) {
               return false;
            }

            if (var2 < var4.getPriority()) {
               break;
            }
         }

         this.insertCommand(var3, var1);
         return true;
      }
   }

   public boolean addCommand(Command var1) {
      if (var1 == null) {
         return false;
      } else {
         int var2 = listOrder[var1.getCommandType()];
         int var3 = var1.getPriority();
         int var4 = 0;
         boolean var5 = false;

         for(Command var6 = null; var4 < this.length; ++var4) {
            var6 = this.commands[var4];
            if (var6 == null) {
               break;
            }

            if (var6 == var1) {
               return false;
            }

            int var7 = listOrder[var6.getCommandType()];
            if (var2 < var7 || var2 == var7 && var3 < var6.getPriority()) {
               break;
            }
         }

         this.insertCommand(var4, var1);
         return true;
      }
   }

   public void addCommands(Command[] var1) {
      this.resize(this.length + var1.length);

      for(int var2 = 0; var2 < var1.length; ++var2) {
         this.addCommand(var1[var2]);
      }

   }

   public boolean removeCommand(Command var1) {
      if (var1 == null) {
         return false;
      } else {
         int var2 = this.indexOfCommand(var1);
         if (var2 != -1) {
            this.removeCommandAt(var2);
            return true;
         } else {
            return false;
         }
      }
   }

   public Command removeCommandAt(int var1) {
      if (var1 >= 0 && var1 < this.length) {
         Command var2 = this.commands[var1];
         --this.length;

         while(var1 < this.length) {
            this.commands[var1] = this.commands[var1 + 1];
            ++var1;
         }

         this.commands[this.length] = null;
         return var2;
      } else {
         return null;
      }
   }

   public Command getCommand(int var1) {
      return var1 >= 0 && var1 < this.length ? this.commands[var1] : null;
   }

   public void setCommand(int var1, Command var2) {
      if (var1 >= this.length) {
         this.resize(var1 + 1);
         this.length = var1 + 1;
      }

      this.commands[var1] = var2;
   }

   public void insertCommand(int var1, Command var2) {
      int var3 = this.length + 1;
      if (var1 > this.length) {
         var3 = var1 + 1;
         this.resize(var3);
      } else if (var1 < 0) {
         var1 = this.length;
      }

      Command[] var4 = this.commands;
      if (this.commands.length < var3) {
         this.commands = new Command[var4.length + 1];
         System.arraycopy(var4, 0, this.commands, 0, var1);
      }

      for(int var5 = this.length; var5 > var1; --var5) {
         this.commands[var5] = var4[var5 - 1];
      }

      this.length = var3;
      this.commands[var1] = var2;
   }

   public void promoteCommand(int var1) {
      if (var1 >= this.length) {
         throw new IndexOutOfBoundsException();
      } else {
         Command var2 = this.commands[var1];

         for(int var3 = var1; var3 > 0; --var3) {
            this.commands[var3] = this.commands[var3 - 1];
         }

         this.commands[0] = var2;
      }
   }

   public void promoteCommand(Command var1) {
      if (var1 == null) {
         throw new IllegalArgumentException();
      } else {
         int var2 = this.indexOfCommand(var1);
         if (var2 != -1) {
            this.promoteCommand(var2);
         }

      }
   }

   public void reconstruct(CommandVector var1, Command[] var2, CommandVector var3, Command var4, boolean var5) {
      if (!var5 && var2 != null && var2.length > 0) {
         this.commands = new Command[var2.length];
         System.arraycopy(var2, 0, this.commands, 0, var2.length);
         this.length = var2.length;
      } else {
         int var6 = 0;
         int var7 = var1.length + (var2 != null ? var2.length : 0) + (var3 != null ? var3.length : 0);
         int var8;
         if (var7 > this.commands.length) {
            this.commands = new Command[var7];
         } else if (var7 < this.commands.length) {
            for(var8 = var7; var8 < this.commands.length; ++var8) {
               this.commands[var8] = null;
            }
         }

         int var9;
         if (var2 != null) {
            var8 = var2.length;

            for(var9 = 0; var9 < var8; ++var9) {
               if (var2[var9].getCommandType() == 12) {
                  System.arraycopy(var2, var9, this.commands, var7 - (var8 - var9), var8 - var9);
                  var8 = var9;
                  break;
               }
            }

            System.arraycopy(var2, 0, this.commands, 0, var8);
            var6 = var8;
         }

         if (var4 != null) {
            this.commands[var6++] = var4;
         }

         if (var3 != null) {
            for(var9 = 0; var9 < var3.length; ++var9) {
               Command var10 = var3.commands[var9];
               if (var10 != var4) {
                  this.commands[var6++] = var10;
               }
            }
         }

         if (var1 != null) {
            System.arraycopy(var1.commands, 0, this.commands, var6, var1.length);
         }

         this.length = var7;
      }
   }

   public void reset() {
      for(int var1 = 0; var1 < this.length; ++var1) {
         this.commands[var1] = null;
      }

      this.length = 0;
   }

   public int length() {
      return this.length;
   }

   public boolean isNotEmpty() {
      return this.length > 0;
   }

   public int indexOfCommand(Command var1) {
      for(int var2 = 0; var2 < this.length; ++var2) {
         if (var1 == this.commands[var2]) {
            return var2;
         }
      }

      return -1;
   }

   public boolean containsCommand(Command var1) {
      return this.indexOfCommand(var1) != -1;
   }

   public boolean hasPositiveCommand() {
      int var1 = 0;

      for(Command var2 = null; var1 < this.length; ++var1) {
         var2 = this.commands[var1];
         if (var2 == null) {
            return false;
         }

         if (!var2.isRSKCommand) {
            return true;
         }
      }

      return false;
   }

   private void resize(int var1) {
      if (var1 > this.commands.length) {
         Command[] var2 = new Command[var1];
         System.arraycopy(this.commands, 0, var2, 0, this.length);
         this.commands = var2;
      }

   }
}
