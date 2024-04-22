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
   private static Explorer m_explorer;

   protected Explorer() {
   }

   public static Explorer getExplorer() {
      if (m_explorer == null) {
         m_explorer = new Explorer();
      }

      return m_explorer;
   }

   public File[] listContents(String var1) throws IllegalArgumentException {
      return this.listContents(var1, 1);
   }

   public File[] listContents(String var1, int var2) throws IllegalArgumentException {
      File[] var3 = null;
      File var4 = File.getFile(var1);
      if (!var4.exists()) {
         throw new IllegalArgumentException("The specified file does not exist");
      } else if (var2 != 1 && var2 != 2 && var2 != 3 && var2 != 4) {
         throw new IllegalArgumentException("Unknown MIDlet Status " + var2);
      } else {
         File[] var5 = null;

         try {
            var5 = var4.listContents(false, "*");
            if (var5 == null) {
               return null;
            }

            Hashtable var6 = new Hashtable(var5.length);
            Vector var7 = new Vector(var5.length);
            boolean var8 = false;

            for(int var16 = 0; var16 < var5.length; ++var16) {
               Object var11 = null;
               File var9 = null;
               File var10 = null;
               if (var5[var16] != null) {
                  if (var5[var16].isDirectory()) {
                     var6.put(var5[var16].getName(), var5[var16]);
                  } else {
                     short var12 = var5[var16].getFileType(true);
                     String var13 = this.getFileNameNoExt(var5[var16].getName());
                     String var14 = var5[var16].getParent();
                     (new StringBuffer()).append(var14).append("\\").append(var13).toString();
                     if (!var6.containsKey(var13)) {
                        String var17;
                        String var18;
                        switch(var12) {
                        case 1:
                           var9 = var5[var16];
                           var5[var16] = null;
                           var17 = this.findMatch(var5, var9);
                           if (var17 == null) {
                              var18 = var9.getPath();
                              var17 = var18.substring(0, var18.length() - 4) + ".jar";
                           }

                           MIDletSuite var24 = MIDletSuite.getMIDletSuite(var17, var9.getPath());
                           var6.put(var13, var24);
                           break;
                        case 2:
                           var10 = var5[var16];
                           var5[var16] = null;
                           var17 = this.findMatch(var5, var10);
                           if (var17 == null) {
                              var18 = var10.getPath();
                              var17 = var18.substring(0, var18.length() - 4) + ".jad";
                           }

                           var6.put(var13, MIDletSuite.getMIDletSuite(var10.getPath(), var17));
                           break;
                        case 3:
                           var7.addElement(var5[var16]);
                           var5[var16] = null;
                        }
                     }
                  }
               }
            }

            Enumeration var25 = var6.keys();

            while(var25.hasMoreElements()) {
               String var23 = (String)var25.nextElement();

               for(int var19 = var7.size() - 1; var19 >= 0; --var19) {
                  File var20 = (File)var7.elementAt(var19);
                  if (var20.getName().startsWith(var23)) {
                     var7.removeElement(var20);
                  }
               }
            }

            var3 = new File[var6.size() + var7.size()];
            if (var3.length > 0) {
               int var26 = 0;

               Enumeration var28;
               for(var28 = var6.elements(); var28.hasMoreElements(); ++var26) {
                  File var21 = (File)var28.nextElement();
                  if (var21 instanceof MIDletSuite) {
                     MIDletSuite var27 = (MIDletSuite)var21;
                     if (var2 == 1) {
                        var3[var26] = var21;
                     } else if (var2 == 3 && var27.getMIDletStatus() == 8) {
                        var3[var26] = var21;
                     } else if (var2 == 2 && var27.getMIDletStatus() == 3) {
                        var3[var26] = var21;
                     } else if (var2 == 4 && var27.getMIDletStatus() == 6) {
                        var3[var26] = var21;
                     }
                  } else {
                     var3[var26] = var21;
                  }
               }

               for(var28 = var7.elements(); var28.hasMoreElements(); ++var26) {
                  var3[var26] = (File)var28.nextElement();
               }
            }
         } catch (IOException var22) {
            var3 = null;
         }

         return var3;
      }
   }

   /** @deprecated */
   public boolean renameFolder(String var1, String var2) throws NullPointerException, IOException, IllegalArgumentException {
      File var3 = File.getFile(var1);
      if (var2 == null) {
         throw new NullPointerException();
      } else if (var3.exists() && var3.isDirectory()) {
         File[] var4 = var3.listContents(false, "*");
         if (var4 == null) {
            return var3.rename(var2);
         } else {
            String var5 = var3.getParent() + "\\" + var2;
            int var6 = FileSystem.getFileSystem().rename(var1, var5);
            return var6 == 0;
         }
      } else {
         throw new IllegalArgumentException("The specified folder does not exist");
      }
   }

   private String findMatch(File[] var1, File var2) {
      for(int var3 = 0; var3 < var1.length; ++var3) {
         if (var1[var3] != null) {
            String var4 = var1[var3].getPath();
            String var5 = var4.substring(var4.length() - 4);
            String var6 = var2.getPath();
            String var7 = var6.substring(var6.length() - 4);
            if (this.getFileNameNoExt(var4).equals(this.getFileNameNoExt(var6)) && !var5.equals(var7)) {
               String var8 = var1[var3].getPath();
               var1[var3] = null;
               return var8;
            }
         }
      }

      return null;
   }

   private String getFileNameNoExt(String var1) {
      int var2 = var1.lastIndexOf(46);
      return var2 > 0 ? var1.substring(0, var2) : var1;
   }
}
