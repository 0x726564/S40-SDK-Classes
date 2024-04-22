package com.nokia.mid.impl.isa.ui;

import javax.microedition.midlet.MIDlet;

final class MIDletRTInfo {
   private final boolean isMIDletExplorer;
   private String mName;
   private String mIcon;
   private Class mCls;
   private MIDletState mInstance;

   static int s_skipSpaces(String var0, int var1, int var2) {
      while(var1 < var2 && var0.charAt(var1) == ' ') {
         ++var1;
      }

      return var1;
   }

   static int s_skipSpacesBack(String var0, int var1) {
      while(var1 >= 0 && var0.charAt(var1) == ' ') {
         --var1;
      }

      return var1;
   }

   static int s_skipToken(String var0, int var1, int var2) {
      while(var1 < var2 && var0.charAt(var1) != ',') {
         ++var1;
      }

      return var1;
   }

   MIDletRTInfo(String var1) throws ClassNotFoundException {
      if (var1 == null) {
         throw new NullPointerException("MIDlet property string null");
      } else {
         String[] var2 = new String[3];
         int var3 = 0;
         int var4 = var1.length();

         for(int var5 = 0; var5 < 3; ++var5) {
            int var6 = s_skipSpaces(var1, var3, var4);
            var3 = s_skipToken(var1, var6, var4);
            int var7 = s_skipSpacesBack(var1, var3 - 1) + 1;
            if (var7 - var6 > 0) {
               var2[var5] = var1.substring(var6, var7);
            }

            if (var3 >= var4) {
               break;
            }

            ++var3;
         }

         this.mName = var2[0];
         this.mIcon = var2[1];
         this.mCls = Class.forName(var2[2]);
         this.isMIDletExplorer = false;
      }
   }

   MIDletRTInfo(Class var1) {
      this.mName = "MIDletExplorer";
      this.mIcon = null;
      this.mCls = var1;
      this.isMIDletExplorer = true;
   }

   final String getName() {
      return this.mName;
   }

   final String getIconName() {
      return this.mIcon;
   }

   final Class getCls() {
      return this.mCls;
   }

   final boolean isMIDletExplorer() {
      return this.isMIDletExplorer;
   }

   final MIDlet constructMIDlet() throws IllegalAccessException, InstantiationException {
      if (this.mCls != null && this.mInstance == null) {
         return (MIDlet)this.mCls.newInstance();
      } else {
         throw new InstantiationException("Failed to construct instance of " + this.mCls);
      }
   }

   final void setInstance(MIDletState var1) {
      this.mInstance = var1;
   }

   final void removeInstance() {
      this.mInstance = null;
   }

   final MIDletState getInstance() {
      return this.mInstance;
   }
}
