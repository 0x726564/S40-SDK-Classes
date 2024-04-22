package com.nokia.mid.impl.jms.core;

import com.nokia.mid.impl.jms.file.File;
import com.nokia.mid.impl.jms.file.FileSystem;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class Explorer {
   public static final byte SEARCH_ALL_MIDLETS = 1;
   public static final byte SEARCH_INSTALLED_MIDLETS = 2;
   public static final byte SEARCH_BROKEN_MIDLETS = 3;
   public static final byte SEARCH_INVALID_MIDLETS = 4;
   private static Explorer dT;

   protected Explorer() {
   }

   public static Explorer getExplorer() {
      if (dT == null) {
         dT = new Explorer();
      }

      return dT;
   }

   public File[] listContents(String var1) throws IllegalArgumentException {
      return this.listContents(var1, 1);
   }

   public File[] listContents(String var1, int var2) throws IllegalArgumentException {
      File[] var3 = null;
      File var13;
      if (!(var13 = File.getFile(var1)).exists()) {
         throw new IllegalArgumentException("The specified file does not exist");
      } else if (var2 != 1 && var2 != 2 && var2 != 3 && var2 != 4) {
         throw new IllegalArgumentException("Unknown MIDlet Status " + var2);
      } else {
         var3 = null;

         try {
            if ((var3 = var13.listContents(false, "*")) == null) {
               return null;
            }

            Hashtable var14 = new Hashtable(var3.length);
            Vector var4 = new Vector(var3.length);

            MIDletSuite var17;
            for(int var7 = 0; var7 < var3.length; ++var7) {
               File var5 = null;
               var5 = null;
               if (var3[var7] != null) {
                  if (var3[var7].isDirectory()) {
                     var14.put(var3[var7].getName(), var3[var7]);
                  } else {
                     short var15 = var3[var7].getFileType(true);
                     String var6 = C(var3[var7].getName());
                     if (!var14.containsKey(var6)) {
                        String var8;
                        String var9;
                        switch(var15) {
                        case 1:
                           var5 = var3[var7];
                           var3[var7] = null;
                           if ((var8 = this.a(var3, var5)) == null) {
                              var9 = var5.getPath();
                              var8 = var9.substring(0, var9.length() - 4) + ".jar";
                           }

                           var17 = MIDletSuite.d(var8, var5.getPath());
                           var14.put(var6, var17);
                           break;
                        case 2:
                           var5 = var3[var7];
                           var3[var7] = null;
                           if ((var8 = this.a(var3, var5)) == null) {
                              var9 = var5.getPath();
                              var8 = var9.substring(0, var9.length() - 4) + ".jad";
                           }

                           var14.put(var6, MIDletSuite.d(var5.getPath(), var8));
                           break;
                        case 3:
                           var4.addElement(var3[var7]);
                           var3[var7] = null;
                        }
                     }
                  }
               }
            }

            Enumeration var18 = var14.keys();

            while(var18.hasMoreElements()) {
               String var16 = (String)var18.nextElement();

               for(int var19 = var4.size() - 1; var19 >= 0; --var19) {
                  File var11;
                  if ((var11 = (File)var4.elementAt(var19)).getName().startsWith(var16)) {
                     var4.removeElement(var11);
                  }
               }
            }

            if ((var3 = new File[var14.size() + var4.size()]).length > 0) {
               int var20 = 0;

               Enumeration var12;
               for(var12 = var14.elements(); var12.hasMoreElements(); ++var20) {
                  if ((var13 = (File)var12.nextElement()) instanceof MIDletSuite) {
                     var17 = (MIDletSuite)var13;
                     if (var2 != 1 && (var2 != 3 || var17.getMIDletStatus() != 8) && (var2 != 2 || var17.getMIDletStatus() != 3) && (var2 != 4 || var17.getMIDletStatus() != 6)) {
                        continue;
                     }
                  }

                  var3[var20] = var13;
               }

               for(var12 = var4.elements(); var12.hasMoreElements(); ++var20) {
                  var3[var20] = (File)var12.nextElement();
               }
            }
         } catch (IOException var10) {
            var3 = null;
         }

         return var3;
      }
   }

   /** @deprecated */
   public boolean renameFolder(String var1, String var2) throws NullPointerException, IOException, IllegalArgumentException {
      File var4 = File.getFile(var1);
      if (var2 == null) {
         throw new NullPointerException();
      } else if (var4.exists() && var4.isDirectory()) {
         if (var4.listContents(false, "*") == null) {
            return var4.rename(var2);
         } else {
            String var5 = var4.getParent() + "\\" + var2;
            return FileSystem.getFileSystem().rename(var1, var5) == 0;
         }
      } else {
         throw new IllegalArgumentException("The specified folder does not exist");
      }
   }

   private String a(File[] var1, File var2) {
      for(int var7 = 0; var7 < var1.length; ++var7) {
         if (var1[var7] != null) {
            String var3;
            String var4 = (var3 = var1[var7].getPath()).substring(var3.length() - 4);
            String var5;
            String var6 = (var5 = var2.getPath()).substring(var5.length() - 4);
            if (C(var3).equals(C(var5)) && !var4.equals(var6)) {
               var3 = var1[var7].getPath();
               var1[var7] = null;
               return var3;
            }
         }
      }

      return null;
   }

   private static String C(String var0) {
      int var1;
      return (var1 = var0.lastIndexOf(46)) > 0 ? var0.substring(0, var1) : var0;
   }
}
