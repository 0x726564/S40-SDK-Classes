package com.nokia.mid.impl.jms.core;

import com.nokia.mid.impl.jms.file.File;
import com.nokia.mid.impl.jms.file.FileSystem;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

public class Installer {
   private static Object jp = new Object();
   private static Installer jq;
   private int jr = 0;
   private int[] js = null;

   protected Installer() {
   }

   public static Installer getInstaller() {
      if (jq == null) {
         jq = new Installer();
      }

      return jq;
   }

   public MIDletSuite install(String var1, JADFile var2, JARFile var3, boolean var4) throws InstallerException, IllegalArgumentException {
      if (!File.getFile(var1).exists()) {
         throw new IllegalArgumentException("The destination folder does not exist");
      } else if (var2 != null && var3 != null) {
         if (var3.getMimeType().equals("application/vnd.oma.drm.content")) {
            throw new InstallerException(-220);
         } else {
            String var5 = FileSystem.getFileSystem().getSystemFilePath(7);
            String var6 = var5 + "\\" + "MIDLET" + System.currentTimeMillis();
            this.js = null;
            int var15;
            synchronized(jp) {
               int var8 = TransactionManager.startTransaction();

               try {
                  if (!FileSystem.getFileSystem().mkdir(var6)) {
                     throw new InstallerException(-121);
                  }

                  if (var2.getURI() != null && !var2.setProperty("Nokia-Update", var2.getURI())) {
                     throw new InstallerException(-200);
                  }

                  String var9 = var6 + "\\" + var2.getProperty("MIDlet-Name") + ".jar";
                  if (!var3.O(var9)) {
                     throw new InstallerException(-200);
                  }

                  String var10 = var6 + "\\" + var2.getProperty("MIDlet-Name") + ".jad";
                  if (!var2.O(var10)) {
                     throw new InstallerException(-121);
                  }

                  this.jr = 0;
                  if ((var15 = this.install0(var1, var10, var9, var3.getMimeType(), var4)) <= 0) {
                     throw new InstallerException(0);
                  }
               } finally {
                  FileSystem.getFileSystem().delete(var6, false);
                  TransactionManager.endTransaction(var8);
               }
            }

            MIDletSuite var7;
            if ((var7 = MIDletSuite.getMIDletSuite(var15)) == null) {
               throw new InstallerException(0);
            } else {
               var7.setErrorCode(0);
               return var7;
            }
         }
      } else {
         throw new IllegalArgumentException("arguments cannot be null");
      }
   }

   public boolean uninstall(int var1) {
      synchronized(jp) {
         return this.uninstall0(var1);
      }
   }

   /** @deprecated */
   public MIDletSuite validate(String var1, String var2) {
      MIDletSuite var3 = null;
      boolean var4 = false;
      int var7;
      synchronized(jp) {
         this.jr = 0;
         var7 = this.validate0(var1, var2);
      }

      if (var7 > 0) {
         var3 = MIDletSuite.getMIDletSuite(var7);
      }

      var3.setErrorCode(0);
      return var3;
   }

   /** @deprecated */
   public MIDletSuite validate(MIDletSuite var1) {
      return this.validate(var1.getJADFilePath(), var1.getJARFilePath());
   }

   final int a(MIDletSuite var1) throws InstallerException {
      synchronized(jp) {
         this.jr = 0;
         return this.basicvalidate0(var1.getMIDletId(), var1.getJADFilePath(), var1.getJARFilePath());
      }
   }

   final int b(MIDletSuite var1) throws InstallerException {
      synchronized(jp) {
         this.jr = 0;
         return this.basicinstall0((String)null, var1.getJADFilePath(), var1.getJARFilePath(), "application/java-archive", true, var1.getMIDletId());
      }
   }

   final int a(String var1, JADFile var2) {
      synchronized(jp) {
         this.jr = 0;
         int var5;
         if ((var5 = this.jadValidate0(var1)) > 0) {
            var2.setSignerOrg((String)null);
            return var5;
         } else {
            var2.js = null;
            this.js = null;
            return var5;
         }
      }
   }

   public boolean deleteContent(String var1) {
      File var2;
      if (!(var2 = File.getFile(var1)).exists()) {
         throw new IllegalArgumentException();
      } else {
         return var2.delete();
      }
   }

   public boolean saveContent(InputStream var1, String var2, boolean var3) {
      boolean var4 = false;
      OutputStream var5 = null;
      FileConnection var6 = null;
      if (var1 != null && var2 != null) {
         File var27;
         if ((var27 = File.getFile(var2)).exists()) {
            this.deleteContent(var2);
         }

         String var24 = "file:///" + var2.replace('\\', '/');
         if (var3) {
            var24 = var24 + "?drm=enc";
         }

         boolean var15 = false;

         label181: {
            try {
               var15 = true;
               byte[] var26 = new byte[256];
               (var6 = (FileConnection)Connector.open(var24, 2)).create();
               var5 = var6.openOutputStream();

               for(int var25 = var1.read(var26); var25 != -1; var25 = var1.read(var26)) {
                  var5.write(var26, 0, var25);
               }

               var27 = true;
               var15 = false;
               break label181;
            } catch (IOException var22) {
               var22.printStackTrace();
               var15 = false;
            } finally {
               if (var15) {
                  if (var5 != null) {
                     try {
                        var5.close();
                     } catch (IOException var17) {
                        var17.printStackTrace();
                     }
                  }

                  if (var6 != null) {
                     try {
                        var6.close();
                     } catch (IOException var16) {
                        var16.printStackTrace();
                     }
                  }

               }
            }

            if (var5 != null) {
               try {
                  var5.close();
               } catch (IOException var19) {
                  var19.printStackTrace();
                  var27 = false;
               }
            }

            if (var6 != null) {
               try {
                  var6.close();
               } catch (IOException var18) {
                  var18.printStackTrace();
                  var27 = false;
               }

               return (boolean)var27;
            }

            return (boolean)var27;
         }

         if (var5 != null) {
            try {
               var5.close();
            } catch (IOException var21) {
               var21.printStackTrace();
               var27 = false;
            }
         }

         if (var6 != null) {
            try {
               var6.close();
            } catch (IOException var20) {
               var20.printStackTrace();
               var27 = false;
            }
         }

         return (boolean)var27;
      } else {
         throw new IllegalArgumentException();
      }
   }

   private native int jadValidate0(String var1);

   private native int basicinstall0(String var1, String var2, String var3, String var4, boolean var5, int var6);

   private native int basicvalidate0(int var1, String var2, String var3);

   private native int install0(String var1, String var2, String var3, String var4, boolean var5);

   private native int validate0(String var1, String var2);

   private native boolean uninstall0(int var1);
}
