package com.nokia.mid.impl.jms.core;

import com.nokia.mid.impl.jms.file.File;
import com.nokia.mid.impl.jms.file.FileOutputStream;
import com.nokia.mid.impl.jms.file.FileSystem;
import java.io.IOException;
import java.io.InputStream;

public class Installer {
   private static Object m_syncLock = new Object();
   private static Installer m_installer;
   private int m_currentErrorCode = 0;
   private int[] m_conflictingMIDlets = null;
   private String m_signerOrg;

   protected Installer() {
   }

   public static Installer getInstaller() {
      if (m_installer == null) {
         m_installer = new Installer();
      }

      return m_installer;
   }

   public MIDletSuite install(String var1, JADFile var2, JARFile var3, boolean var4) throws InstallerException, IllegalArgumentException {
      if (!File.getFile(var1).exists()) {
         throw new IllegalArgumentException("The destination folder does not exist");
      } else if (var2 != null && var3 != null) {
         if (var3.getMimeType().equals("application/vnd.oma.drm.content")) {
            throw new InstallerException(-220);
         } else {
            String var6 = FileSystem.getFileSystem().getSystemFilePath(7);
            String var7 = var6 + "\\" + "MIDLET" + System.currentTimeMillis();
            this.m_conflictingMIDlets = null;
            int var5;
            synchronized(m_syncLock) {
               int var9 = TransactionManager.startTransaction();

               try {
                  if (!FileSystem.getFileSystem().mkdir(var7)) {
                     throw new InstallerException(-121);
                  }

                  if (var2.getURI() != null && !var2.setProperty("Nokia-Update", var2.getURI())) {
                     throw new InstallerException(-200);
                  }

                  String var10 = var7 + "\\" + var2.getProperty("MIDlet-Name") + ".jar";
                  if (!var3.saveTo(var10)) {
                     throw new InstallerException(-200);
                  }

                  String var11 = var7 + "\\" + var2.getProperty("MIDlet-Name") + ".jad";
                  if (!var2.saveTo(var11)) {
                     throw new InstallerException(-121);
                  }

                  this.m_currentErrorCode = 0;
                  var5 = this.install0(var1, var11, var10, var3.getMimeType(), var4);
                  if (var5 <= 0 || this.m_currentErrorCode < 0) {
                     if (this.m_currentErrorCode == -301) {
                        var2.m_errorCode = this.m_currentErrorCode;
                        var2.m_conflictingMIDlets = this.m_conflictingMIDlets;
                        this.m_conflictingMIDlets = null;
                     }

                     throw new InstallerException(this.m_currentErrorCode);
                  }
               } finally {
                  FileSystem.getFileSystem().delete(var7, false);
                  TransactionManager.endTransaction(var9);
               }
            }

            MIDletSuite var8 = MIDletSuite.getMIDletSuite(var5);
            if (var8 == null) {
               throw new InstallerException(this.m_currentErrorCode);
            } else {
               var8.setErrorCode(this.m_currentErrorCode);
               if (this.m_currentErrorCode > 0) {
                  var8.setMIDletStatus(3);
               }

               return var8;
            }
         }
      } else {
         throw new IllegalArgumentException("arguments cannot be null");
      }
   }

   public boolean uninstall(int var1) {
      synchronized(m_syncLock) {
         return this.uninstall0(var1);
      }
   }

   /** @deprecated */
   public MIDletSuite validate(String var1, String var2) {
      MIDletSuite var3 = null;
      boolean var4 = true;
      int var8;
      synchronized(m_syncLock) {
         this.m_currentErrorCode = 0;
         var8 = this.validate0(var1, var2);
      }

      if (var8 > 0) {
         var3 = MIDletSuite.getMIDletSuite(var8);
      }

      var3.setErrorCode(this.m_currentErrorCode);
      return var3;
   }

   /** @deprecated */
   public MIDletSuite validate(MIDletSuite var1) {
      return this.validate(var1.getJADFilePath(), var1.getJARFilePath());
   }

   int basicValidate(MIDletSuite var1) throws InstallerException {
      synchronized(m_syncLock) {
         this.m_currentErrorCode = 0;
         int var3 = this.basicvalidate0(var1.getMIDletId(), var1.getJADFilePath(), var1.getJARFilePath());
         if (this.m_currentErrorCode < 0) {
            throw new InstallerException(this.m_currentErrorCode);
         } else {
            return var3;
         }
      }
   }

   int basicInstall(MIDletSuite var1) throws InstallerException {
      synchronized(m_syncLock) {
         this.m_currentErrorCode = 0;
         int var3 = this.basicinstall0((String)null, var1.getJADFilePath(), var1.getJARFilePath(), "application/java-archive", true, var1.getMIDletId());
         if (this.m_currentErrorCode < 0) {
            throw new InstallerException(this.m_currentErrorCode);
         } else {
            return var3;
         }
      }
   }

   int jadValidate(String var1, JADFile var2) {
      synchronized(m_syncLock) {
         this.m_currentErrorCode = 0;
         int var4 = this.jadValidate0(var1);
         if (var4 > 0) {
            var2.setSignerOrg(this.m_signerOrg);
            return var4;
         } else {
            var2.m_conflictingMIDlets = this.m_conflictingMIDlets;
            this.m_conflictingMIDlets = null;
            return var4;
         }
      }
   }

   public boolean saveContent(InputStream var1, String var2, boolean var3) {
      boolean var4 = false;
      if (var1 != null && var2 != null) {
         FileOutputStream var5 = null;

         try {
            var5 = new FileOutputStream(var2, true, false);
            byte[] var6 = new byte[256];

            for(int var7 = var1.read(var6); var7 >= 0; var7 = var1.read(var6)) {
               var5.write(var6, 0, var7);
            }

            var4 = true;
         } catch (IOException var20) {
            var20.printStackTrace();
         } finally {
            if (var5 != null) {
               try {
                  var5.close();
               } catch (IOException var18) {
                  var18.printStackTrace();
               }
            }

         }

         if (var4) {
            synchronized(m_syncLock) {
               if (var3) {
                  this.m_currentErrorCode = 0;
                  this.saveDRMContent0(var2);
                  return this.m_currentErrorCode >= 0;
               } else {
                  return true;
               }
            }
         } else {
            return false;
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   private native boolean saveDRMContent0(String var1);

   private native int jadValidate0(String var1);

   private native int basicinstall0(String var1, String var2, String var3, String var4, boolean var5, int var6);

   private native int basicvalidate0(int var1, String var2, String var3);

   private native int install0(String var1, String var2, String var3, String var4, boolean var5);

   private native int validate0(String var1, String var2);

   private native boolean uninstall0(int var1);
}
