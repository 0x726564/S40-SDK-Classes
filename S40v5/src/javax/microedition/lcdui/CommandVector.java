package javax.microedition.lcdui;

final class CommandVector {
   private static int[] gx = new int[]{0, 8, 10, 6, 5, 9, 4, 12, 7, 2, 1, 11, 13, 3};
   private Command[] gy = null;
   private int length = 0;

   public CommandVector() {
      this.gy = new Command[0];
      this.length = 0;
   }

   public CommandVector(int var1) {
      this.gy = new Command[3];
      this.length = 0;
   }

   public final boolean f(Command var1) {
      if (var1 == null) {
         return false;
      } else {
         int var2 = var1.getPriority();
         int var3 = 0;

         for(Command var4 = null; var3 < this.length && (var4 = this.gy[var3]) != null; ++var3) {
            if (var4 == var1) {
               return false;
            }

            if (var2 < var4.getPriority()) {
               break;
            }
         }

         this.a(var3, var1);
         return true;
      }
   }

   public final boolean g(Command var1) {
      if (var1 == null) {
         return false;
      } else {
         int var2 = gx[var1.getCommandType()];
         int var3 = var1.getPriority();
         int var4 = 0;
         boolean var5 = false;

         for(Command var6 = null; var4 < this.length && (var6 = this.gy[var4]) != null; ++var4) {
            if (var6 == var1) {
               return false;
            }

            int var7 = gx[var6.getCommandType()];
            if (var2 < var7 || var2 == var7 && var3 < var6.getPriority()) {
               break;
            }
         }

         this.a(var4, var1);
         return true;
      }
   }

   public final boolean h(Command var1) {
      if (var1 == null) {
         return false;
      } else {
         int var2;
         if ((var2 = this.i(var1)) != -1) {
            this.D(var2);
            return true;
         } else {
            return false;
         }
      }
   }

   public final Command D(int var1) {
      if (var1 >= 0 && var1 < this.length) {
         Command var2 = this.gy[var1];
         --this.length;

         while(var1 < this.length) {
            this.gy[var1] = this.gy[var1 + 1];
            ++var1;
         }

         this.gy[this.length] = null;
         return var2;
      } else {
         return null;
      }
   }

   public final Command getCommand(int var1) {
      return var1 >= 0 && var1 < this.length ? this.gy[var1] : null;
   }

   public final void setCommand(int var1, Command var2) {
      if (var1 >= this.length) {
         this.resize(var1 + 1);
         this.length = var1 + 1;
      }

      this.gy[var1] = var2;
   }

   private void a(int var1, Command var2) {
      int var3 = this.length + 1;
      if (var1 > this.length) {
         var3 = var1 + 1;
         this.resize(var3);
      } else if (var1 < 0) {
         var1 = this.length;
      }

      Command[] var4 = this.gy;
      if (this.gy.length < var3) {
         this.gy = new Command[var4.length + 1];
         System.arraycopy(var4, 0, this.gy, 0, var1);
      }

      for(int var5 = this.length; var5 > var1; --var5) {
         this.gy[var5] = var4[var5 - 1];
      }

      this.length = var3;
      this.gy[var1] = var2;
   }

   public final void a(CommandVector var1, Command[] var2, CommandVector var3, Command var4, boolean var5) {
      if (!var5 && var2 != null && var2.length > 0) {
         this.gy = new Command[var2.length];
         System.arraycopy(var2, 0, this.gy, 0, var2.length);
         this.length = var2.length;
      } else {
         int var9 = 0;
         int var6;
         int var7;
         if ((var6 = var1.length + (var2 != null ? var2.length : 0) + (var3 != null ? var3.length : 0)) > this.gy.length) {
            this.gy = new Command[var6];
         } else if (var6 < this.gy.length) {
            for(var7 = var6; var7 < this.gy.length; ++var7) {
               this.gy[var7] = null;
            }
         }

         int var8;
         if (var2 != null) {
            var7 = var2.length;

            for(var8 = 0; var8 < var7; ++var8) {
               if (var2[var8].getCommandType() == 12) {
                  System.arraycopy(var2, var8, this.gy, var6 - (var7 - var8), var7 - var8);
                  var7 = var8;
                  break;
               }
            }

            System.arraycopy(var2, 0, this.gy, 0, var7);
            var9 = var7;
         }

         if (var4 != null) {
            this.gy[var9++] = var4;
         }

         if (var3 != null) {
            for(var8 = 0; var8 < var3.length; ++var8) {
               Command var10;
               if ((var10 = var3.gy[var8]) != var4) {
                  this.gy[var9++] = var10;
               }
            }
         }

         if (var1 != null) {
            System.arraycopy(var1.gy, 0, this.gy, var9, var1.length);
         }

         this.length = var6;
      }
   }

   public final void reset() {
      for(int var1 = 0; var1 < this.length; ++var1) {
         this.gy[var1] = null;
      }

      this.length = 0;
   }

   public final int length() {
      return this.length;
   }

   public final boolean ar() {
      return this.length > 0;
   }

   public final int i(Command var1) {
      for(int var2 = 0; var2 < this.length; ++var2) {
         if (var1 == this.gy[var2]) {
            return var2;
         }
      }

      return -1;
   }

   public final boolean j(Command var1) {
      return this.i(var1) != -1;
   }

   public final boolean as() {
      int var1 = 0;

      for(Command var2 = null; var1 < this.length; ++var1) {
         if ((var2 = this.gy[var1]) == null) {
            return false;
         }

         if (!var2.f) {
            return true;
         }
      }

      return false;
   }

   private void resize(int var1) {
      if (var1 > this.gy.length) {
         Command[] var2 = new Command[var1];
         System.arraycopy(this.gy, 0, var2, 0, this.length);
         this.gy = var2;
      }

   }
}
