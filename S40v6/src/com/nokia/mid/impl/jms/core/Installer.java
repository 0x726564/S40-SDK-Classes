package com.nokia.mid.impl.jms.core;

import com.nokia.mid.impl.jms.file.File;
import com.nokia.mid.impl.jms.file.FileSystem;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

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

   public MIDletSuite install(String destination, JADFile jad, JARFile jar, boolean keepRMS) throws InstallerException, IllegalArgumentException {
      File fileJadDestination = null;
      String strMidletSuiteName = "";
      String jadDestination = "";
      int oldJarAttributes = 0;
      int newJarAttributes = false;
      int oldJadAttributes = false;
      int newJadAttributes = false;
      File fileDestination = File.getFile(destination);
      if (!fileDestination.exists()) {
         throw new IllegalArgumentException("The destination folder does not exist");
      } else if (jad != null && jar != null) {
         if (jar.getMimeType().equals("application/vnd.oma.drm.content")) {
            throw new InstallerException(-220);
         } else {
            String javaTempRoot = FileSystem.getFileSystem().getSystemFilePath(7);
            String tempFolder = javaTempRoot + "\\" + "MIDLET" + System.currentTimeMillis();
            this.m_conflictingMIDlets = null;
            int midletId;
            synchronized(m_syncLock) {
               int transId = TransactionManager.startTransaction();

               try {
                  if (!FileSystem.getFileSystem().mkdir(tempFolder)) {
                     throw new InstallerException(-121);
                  }

                  if (jad.getURI() != null && !jad.setProperty("Nokia-Update", jad.getURI())) {
                     throw new InstallerException(-200);
                  }

                  strMidletSuiteName = jad.getProperty("MIDlet-Name");

                  try {
                     if (!fileDestination.isDirectory()) {
                        strMidletSuiteName = fileDestination.getName();
                        strMidletSuiteName = strMidletSuiteName.substring(0, strMidletSuiteName.lastIndexOf(46));
                     }
                  } catch (IOException var30) {
                  }

                  String jarFilePath = tempFolder + "\\" + strMidletSuiteName + ".jar";
                  if (!jar.saveTo(jarFilePath)) {
                     throw new InstallerException(-200);
                  }

                  String jadFilePath = tempFolder + "\\" + strMidletSuiteName + ".jad";
                  if (!jad.saveTo(jadFilePath)) {
                     throw new InstallerException(-121);
                  }

                  try {
                     if (!fileDestination.isDirectory()) {
                        oldJarAttributes = fileDestination.getAttributes();
                        int newJarAttributes = oldJarAttributes & -6;
                        fileDestination.setAttributes(newJarAttributes);
                        jadDestination = destination.substring(0, destination.indexOf(".jar")) + ".jad";
                        fileJadDestination = File.getFile(jadDestination);
                        int oldJadAttributes = fileJadDestination.getAttributes();
                        int newJadAttributes = oldJadAttributes & -6;
                        fileJadDestination.setAttributes(newJadAttributes);
                     }
                  } catch (IOException var29) {
                  }

                  this.m_currentErrorCode = 0;
                  midletId = this.install0(destination, jadFilePath, jarFilePath, jar.getMimeType(), keepRMS);

                  try {
                     fileDestination = File.getFile(destination);
                     if (!fileDestination.isDirectory()) {
                        fileJadDestination = File.getFile(jadDestination);
                        fileDestination.setAttributes(oldJarAttributes);
                        fileJadDestination.setAttributes(oldJarAttributes);
                     }
                  } catch (IOException var28) {
                  }

                  if (midletId <= 0 || this.m_currentErrorCode < 0) {
                     if (this.m_currentErrorCode == -301) {
                        jad.m_errorCode = this.m_currentErrorCode;
                        jad.m_conflictingMIDlets = this.m_conflictingMIDlets;
                        this.m_conflictingMIDlets = null;
                     }

                     throw new InstallerException(this.m_currentErrorCode);
                  }
               } finally {
                  FileSystem.getFileSystem().delete(tempFolder, false);
                  TransactionManager.endTransaction(transId);
               }
            }

            MIDletSuite suite = MIDletSuite.getMIDletSuite(midletId);
            if (suite == null) {
               throw new InstallerException(this.m_currentErrorCode);
            } else {
               suite.setErrorCode(this.m_currentErrorCode);
               if (this.m_currentErrorCode > 0) {
                  suite.setMIDletStatus(3);
               }

               return suite;
            }
         }
      } else {
         throw new IllegalArgumentException("arguments cannot be null");
      }
   }

   public boolean uninstall(int midletId) {
      synchronized(m_syncLock) {
         return this.uninstall0(midletId);
      }
   }

   /** @deprecated */
   public MIDletSuite validate(String jadFilePath, String jarFilePath) {
      MIDletSuite midlet = null;
      int midletId = true;
      int midletId;
      synchronized(m_syncLock) {
         this.m_currentErrorCode = 0;
         midletId = this.validate0(jadFilePath, jarFilePath);
      }

      if (midletId > 0) {
         midlet = MIDletSuite.getMIDletSuite(midletId);
      }

      midlet.setErrorCode(this.m_currentErrorCode);
      return midlet;
   }

   /** @deprecated */
   public MIDletSuite validate(MIDletSuite suite) {
      return this.validate(suite.getJADFilePath(), suite.getJARFilePath());
   }

   int basicValidate(MIDletSuite suite) throws InstallerException {
      synchronized(m_syncLock) {
         this.m_currentErrorCode = 0;
         int i = this.basicvalidate0(suite.getMIDletId(), suite.getJADFilePath(), suite.getJARFilePath());
         if (this.m_currentErrorCode < 0) {
            throw new InstallerException(this.m_currentErrorCode);
         } else {
            return i;
         }
      }
   }

   int basicInstall(MIDletSuite suite) throws InstallerException {
      synchronized(m_syncLock) {
         this.m_currentErrorCode = 0;
         int i = this.basicinstall0((String)null, suite.getJADFilePath(), suite.getJARFilePath(), "application/java-archive", true, suite.getMIDletId());
         if (this.m_currentErrorCode < 0) {
            throw new InstallerException(this.m_currentErrorCode);
         } else {
            return i;
         }
      }
   }

   int jadValidate(String jadFilePath, JADFile jad) {
      synchronized(m_syncLock) {
         this.m_currentErrorCode = 0;
         int retValue = this.jadValidate0(jadFilePath);
         if (retValue > 0) {
            jad.setSignerOrg(this.m_signerOrg);
            return retValue;
         } else {
            jad.m_conflictingMIDlets = this.m_conflictingMIDlets;
            this.m_conflictingMIDlets = null;
            return retValue;
         }
      }
   }

   public boolean deleteContent(String filePath) {
      File file = File.getFile(filePath);
      if (!file.exists()) {
         throw new IllegalArgumentException();
      } else {
         return file.delete();
      }
   }

   public boolean saveContent(InputStream content, String filePath, boolean isDRM) {
      boolean success = false;
      OutputStream fileOutStream = null;
      FileConnection fileConnection = null;
      if (content != null && filePath != null) {
         File f = File.getFile(filePath);
         if (f.exists()) {
            this.deleteContent(filePath);
         }

         String fileUrl = "file:///" + filePath.replace('\\', '/');
         if (isDRM) {
            fileUrl = fileUrl + "?drm=enc";
         }

         try {
            byte[] buffer = new byte[256];
            fileConnection = (FileConnection)Connector.open(fileUrl, 2);
            fileConnection.create();
            fileOutStream = fileConnection.openOutputStream();

            for(int bytesRead = content.read(buffer); bytesRead != -1; bytesRead = content.read(buffer)) {
               fileOutStream.write(buffer, 0, bytesRead);
            }

            success = true;
         } catch (IOException var24) {
            var24.printStackTrace();
         } finally {
            if (fileOutStream != null) {
               try {
                  fileOutStream.close();
               } catch (IOException var23) {
                  var23.printStackTrace();
                  success = false;
               }
            }

            if (fileConnection != null) {
               try {
                  fileConnection.close();
               } catch (IOException var22) {
                  var22.printStackTrace();
                  success = false;
               }
            }

         }

         return success;
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
