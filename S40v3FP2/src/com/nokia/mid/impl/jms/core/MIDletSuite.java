package com.nokia.mid.impl.jms.core;

import com.nokia.mid.impl.isa.ui.DisplayAccess;
import com.nokia.mid.impl.isa.ui.InitJALM;
import com.nokia.mid.impl.isa.ui.MIDletAccess;
import com.nokia.mid.impl.isa.ui.gdi.Pixmap;
import com.nokia.mid.impl.jms.file.File;
import com.nokia.mid.impl.jms.file.FileSystem;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.microedition.lcdui.Image;

public class MIDletSuite extends File {
   public static final short MIDLET_SUITE_STATUS_UNKNOWN = 0;
   public static final short MIDLET_SUITE_STATUS_CREATED = 1;
   public static final short MIDLET_SUITE_STATUS_PENDING_INSTALL = 2;
   public static final short MIDLET_SUITE_STATUS_INSTALLED = 3;
   public static final short MIDLET_SUITE_STATUS_PENDING_DELETE = 4;
   public static final short MIDLET_SUITE_STATUS_PENDING_UPDATE = 5;
   public static final short MIDLET_SUITE_STATUS_INVALID = 6;
   public static final short MIDLET_SUITE_STATUS_BROKEN = 8;
   public static final int STAGE_INSTALL = 1;
   public static final int STAGE_VALIDATE = 2;
   public static final int STAGE_INSTALL_VALIDATE = 3;
   public static final int MIDLET_TYPE_UNKNOWN = 0;
   public static final int MIDLET_TYPE_UNTRUSTED = 1;
   public static final int MIDLET_TYPE_TRUSTED = 2;
   public static final int SUCCESS_OK = 1;
   public static final int SUCCESS_PARTIAL_PERMISSIONS_GRANTED = 2;
   public static final int ERROR_901_INTERNAL_MEMORY_FULL = -100;
   public static final int ERROR_901_INSUFFICIENT_MEMORY = -101;
   public static final int ERROR_901_MIDLET_TOO_LARGE = -102;
   public static final int ERROR_902_USER_CANCELLED = -120;
   public static final int ERROR_902_INTERNAL_ERROR = -121;
   /** @deprecated */
   public static final int ERROR_902_ILLEGAL_ACCESS = -122;
   public static final int ERROR_902_INSTALLER_BUSY = -123;
   public static final int ERROR_903_LOSS_OF_SERVICE = -140;
   public static final int ERROR_904_JAR_SIZE_MISMATCH = -160;
   public static final int ERROR_905_ATTRIBUTE_MISMATCH = -180;
   public static final int ERROR_906_INVALID_JAD = -200;
   public static final int ERROR_906_UPDATE_SECURITY_ERR = -201;
   public static final int ERROR_907_INVALID_JAR = -220;
   public static final int ERROR_907_DRM_PARSING_ERR = -221;
   public static final int ERROR_908_INVALID_CONFIG_OR_PROFILE = -240;
   public static final int ERROR_909_AUTHENT_FAIL = -260;
   public static final int ERROR_909_INVALID_CERTIFICATE_INFO = -261;
   public static final int ERROR_909_NO_VALID_ROOT_CERTIFICATE = -262;
   public static final int ERROR_909_NO_VALID_DOMAIN = -263;
   public static final int ERROR_909_OPERATOR_ROOT_NO_LONGER_AVAILABLE = -265;
   public static final int ERROR_909_ROOT_NO_LONGER_AVAILABLE = -266;
   public static final int ERROR_909_ROOT_DISABLED = -267;
   public static final int ERROR_909_OTHER_ERROR = -268;
   public static final int ERROR_909_CERTS_NOT_VALID_YET = -269;
   public static final int ERROR_909_CERTS_EXPIRED = -270;
   public static final int ERROR_909_CORRUPT_SIGNATURE = -271;
   public static final int ERROR_910_AUTHORIZE_FAIL = -280;
   public static final int ERROR_910_PERMISSIONS_CHECK_FAILED = -281;
   public static final int ERROR_911_PUSH_REG_FAIL = -300;
   public static final int ERROR_911_PUSH_REG_PORT_CONFLICT = -301;
   /** @deprecated */
   public static final int ERROR_912_DELETE_NOTIF = -320;
   private int m_midletId;
   private int m_midletStatus;
   private int m_errorCode;
   private String m_jadFilename;
   private JADFile m_jad;
   private static String[] tmpRmsFileNames;
   private int m_midletType = 0;
   private String m_midletSuiteIconName = null;
   String m_midletSuiteName = null;

   public static MIDletSuite getMIDletSuite(int var0) {
      MIDletSuite var1 = null;
      String var2 = MIDletRegistry.getMIDletRegistry().getMIDletLocation(var0);
      if (var2 == null) {
         return null;
      } else {
         int var3 = var2.length();
         String var4 = var2.substring(0, var3 - 4) + ".jad";
         var1 = new MIDletSuite(var0, var2, var4);
         return var1;
      }
   }

   static MIDletSuite getMIDletSuite(String var0, String var1) {
      if (var0 != null && var1 != null) {
         try {
            return new MIDletSuite(-1, var0, var1);
         } catch (IllegalArgumentException var3) {
            return null;
         }
      } else {
         return null;
      }
   }

   private MIDletSuite(int var1, String var2, String var3) throws IllegalArgumentException {
      super(var2);
      this.setup(var2, var3);
      if (var1 <= 0) {
         this.m_midletId = MIDletRegistry.getMIDletRegistry().findMIDletSuiteByLocation(var2);
      } else {
         this.m_midletId = var1;
      }

   }

   private void setup(String var1, String var2) throws IllegalArgumentException {
      this.m_jadFilename = var2;
      if (super.exists() && (this.m_jad = this.createJAD(var2)) != null) {
         this.setMIDletStatus(0);
      } else {
         this.setMIDletStatus(6);
      }

      this.m_errorCode = 1;
   }

   private JADFile createJAD(String var1) {
      boolean var2 = false;
      String var3 = this.getPath();
      JADFile var4 = null;
      File var5 = File.getFile(var1);

      try {
         if (var5.exists()) {
            var4 = new JADFile(var1);
         } else if ((var4 = (new JARFile(var3)).createJAD()) != null) {
            FileSystem.getFileSystem().createFile(var1, new byte[0], 0, 0);
            var4.saveTo(var1);
         }
      } catch (IOException var7) {
      }

      if (var4 != null) {
         this.cacheProperties(var4.getAllProperties());
      }

      return var4;
   }

   public int install(int var1) {
      this.getMIDletStatus();
      switch(var1) {
      case 1:
         this.basicInstall();
         break;
      case 2:
         this.basicValidate();
         break;
      case 3:
         this.basicValidate();
         this.basicInstall();
         break;
      default:
         throw new IllegalArgumentException("Unknown install stage.");
      }

      return this.getMIDletStatus();
   }

   private boolean basicInstall() {
      try {
         this.m_midletId = Installer.getInstaller().basicInstall(this);
         return true;
      } catch (InstallerException var2) {
         this.m_midletId = -1;
         this.m_errorCode = var2.getErrorCode();
         return false;
      }
   }

   private boolean basicValidate() {
      try {
         this.m_midletId = Installer.getInstaller().basicValidate(this);
         return true;
      } catch (InstallerException var2) {
         this.m_midletId = -1;
         this.m_errorCode = var2.getErrorCode();
         return false;
      }
   }

   public int getMIDletId() {
      return this.m_midletId;
   }

   void setMIDletStatus(int var1) {
      this.m_midletStatus = var1;
   }

   public int getMIDletStatus() {
      this.m_midletStatus = MIDletRegistry.getMIDletRegistry().getMIDletStatus(this.m_midletId);
      return this.m_midletStatus;
   }

   public int getErrorCode() {
      return this.m_errorCode;
   }

   /** @deprecated */
   public int getDetailedErrorCode() {
      return this.m_errorCode;
   }

   void setErrorCode(int var1) {
      this.m_errorCode = var1;
   }

   public byte[] getIcon() {
      String var1 = null;
      if (this.m_midletSuiteIconName != null) {
         var1 = this.m_midletSuiteIconName;
      } else {
         var1 = this.getJADProperty("MIDlet-Icon");
         if (var1 == null || var1.length() == 0) {
            return null;
         }

         var1 = var1.trim();
         if (var1.charAt(0) == '/') {
            var1 = var1.substring(1);
         }
      }

      return FileSystem.getFileSystem().getFileContentFromJar(this.getPath(), var1);
   }

   public byte[] getIcon(int var1) {
      String var2 = this.getJADProperty("MIDlet-" + var1);
      if (var2 == null) {
         return null;
      } else {
         int var3;
         if ((var3 = var2.indexOf(44)) == -1) {
            return null;
         } else {
            int var4;
            if ((var4 = var2.indexOf(44, var3 + 1)) == -1) {
               return null;
            } else {
               String var5 = var2.substring(var3 + 1, var4).trim();
               if (var5.length() == 0) {
                  return null;
               } else {
                  if (var5.charAt(0) == '/') {
                     var5 = var5.substring(1);
                  }

                  return FileSystem.getFileSystem().getFileContentFromJar(this.getPath(), var5);
               }
            }
         }
      }
   }

   public Image getDefaultIcon() {
      MIDletAccess var1 = InitJALM.s_getMIDletAccessor();
      DisplayAccess var2 = var1.getDisplayAccessor();
      return var2.createImage(Pixmap.createPixmap(0));
   }

   public long getSpaceUsed() {
      long var1 = 0L;
      String[] var3 = this.getFilesOwnedByMIDlet();
      if (var3 != null) {
         File var4 = null;

         for(int var5 = 0; var5 < var3.length; ++var5) {
            if (var3[var5] != null) {
               var4 = File.getFile(var3[var5]);
               if (var4.exists()) {
                  try {
                     var1 += var4.getSize();
                  } catch (IOException var7) {
                  }
               }
            }
         }
      }

      return var1;
   }

   public String[] getMIDletNames() {
      Hashtable var1;
      if ((var1 = this.getAllJADProperties()) == null) {
         return null;
      } else {
         Enumeration var2 = var1.keys();
         Hashtable var3 = new Hashtable();

         while(var2.hasMoreElements()) {
            String var4 = (String)var2.nextElement();
            if (var4.startsWith("MIDlet-")) {
               int var5 = -1;
               String var6 = var4.substring("MIDlet-".length()).trim();

               try {
                  var5 = Integer.parseInt(var6);
               } catch (NumberFormatException var9) {
               }

               if (var5 > 0) {
                  String var7 = (String)var1.get(var4);
                  int var8 = var7.indexOf(44);
                  if (var8 == -1) {
                     return null;
                  }

                  var7 = var7.substring(0, var8);
                  var3.put(var6, var7);
               }
            }
         }

         int var10 = var3.size();
         String[] var11 = new String[var10];

         for(int var12 = 0; var12 < var10; ++var12) {
            var11[var12] = (String)var3.get((new Integer(var12 + 1)).toString());
         }

         return var11;
      }
   }

   public int getMIDletType() {
      if (this.m_midletType != 0) {
         return this.m_midletType;
      } else {
         Hashtable var1;
         if ((var1 = this.getAllJADProperties()) == null) {
            return 0;
         } else {
            String var2 = (String)var1.get("MIDlet-Jar-RSA-SHA1");
            return var2 != null && !var2.equals("") ? 2 : 1;
         }
      }
   }

   public byte[] getMIDletAttribute(int var1) throws IOException {
      return MIDletRegistry.getMIDletRegistry().getMIDletAttribute(this.m_midletId, var1);
   }

   public String getJADProperty(String var1) {
      if (var1 == null) {
         return null;
      } else if (var1.equals("MIDlet-Name") && this.m_midletSuiteName != null) {
         return this.m_midletSuiteName;
      } else {
         Hashtable var2;
         return (var2 = this.getAllJADProperties()) == null ? null : (String)var2.get(var1);
      }
   }

   public boolean delete() {
      return this.delete(true);
   }

   public boolean delete(boolean var1) {
      boolean var2 = false;
      boolean var3 = true;
      String[] var4 = null;
      if (this.m_midletId > 0) {
         var4 = this.getFilesOwnedByMIDlet();
         var2 = Installer.getInstaller().uninstall(this.m_midletId);
      } else {
         var4 = new String[]{this.getJARFilePath(), this.getJADFilePath()};
         var2 = true;
      }

      if (var2 && var4 != null) {
         File var5 = null;

         for(int var6 = 0; var6 < var4.length; ++var6) {
            if (var4[var6] != null) {
               var5 = File.getFile(var4[var6]);
               if (var5.exists() && !var5.delete(false)) {
                  var3 = false;
               }
            }
         }
      }

      return var2 && var3;
   }

   public boolean rename(String var1) {
      return false;
   }

   public boolean moveTo(File var1) {
      boolean var2 = true;

      try {
         if (!var1.isDirectory()) {
            throw new IllegalArgumentException("destination is not a directory.");
         }
      } catch (IOException var7) {
         var7.printStackTrace();
         throw new IllegalArgumentException("Unable to access the destination.");
      }

      String[] var3 = this.getFilesOwnedByMIDlet();
      int var4 = TransactionManager.startTransaction();

      for(int var5 = 0; var5 < var3.length; ++var5) {
         File var6 = File.getFile(var3[var5]);
         if (!var6.moveTo(var1)) {
            var2 = false;
         }
      }

      TransactionManager.endTransaction(var4);
      return var2;
   }

   public String[] getFilesOwnedByMIDlet() {
      return this.getFilesOwnedByMIDlet0(this.getJARFilePath());
   }

   public boolean copyTo(File var1) {
      return false;
   }

   public String getJADFilePath() {
      return File.getFile(this.m_jadFilename).getPath();
   }

   /** @deprecated */
   public String getJARFilePath() {
      return this.getPath();
   }

   public String getAbsoluteJARUrl() {
      try {
         JADFile var1 = new JADFile(this.getJADFilePath());
         return var1.getAbsoluteJARUrl();
      } catch (Exception var2) {
         return null;
      }
   }

   private Hashtable getAllJADProperties() {
      try {
         JADFile var1 = new JADFile(this.m_jadFilename);
         Hashtable var2 = var1.getAllProperties();
         byte[] var3 = FileSystem.getFileSystem().getFileContentFromJar(this.getPath(), "meta-inf/MANIFEST.MF");
         if (var3 != null) {
            new StringBuffer();
            JADFile var5 = new JADFile(var3, (String)null);
            Enumeration var6 = var5.getAllProperties().keys();
            if (var2 == null) {
               var2 = new Hashtable();
            }

            while(var6.hasMoreElements()) {
               String var7 = (String)var6.nextElement();
               String var8 = var5.getProperty(var7);
               if (!var2.containsKey(var7)) {
                  var2.put(var7, var8);
               }
            }
         }

         return var2;
      } catch (Exception var9) {
         return null;
      }
   }

   private void cacheProperties(Hashtable var1) {
      this.m_midletSuiteName = (String)var1.get("MIDlet-Name");
      this.m_midletSuiteIconName = (String)var1.get("MIDlet-Icon");
      if (this.m_midletSuiteIconName == null || this.m_midletSuiteIconName.length() == 0) {
         this.m_midletSuiteIconName = (String)var1.get("MIDlet-1");
         if (this.m_midletSuiteIconName != null && this.m_midletSuiteIconName.length() != 0) {
            int var2;
            int var3;
            if ((var2 = this.m_midletSuiteIconName.indexOf(44)) != -1 && (var3 = this.m_midletSuiteIconName.indexOf(44, var2 + 1)) != -1) {
               this.m_midletSuiteIconName = this.m_midletSuiteIconName.substring(var2 + 1, var3).trim();
            }

            if (this.m_midletSuiteIconName != null && this.m_midletSuiteIconName.length() == 0) {
               this.m_midletSuiteIconName = null;
            }
         } else {
            this.m_midletSuiteIconName = null;
         }
      }

      if (this.m_midletSuiteIconName != null) {
         this.m_midletSuiteIconName = this.m_midletSuiteIconName.trim();
         if (this.m_midletSuiteIconName.charAt(0) == '/') {
            this.m_midletSuiteIconName = this.m_midletSuiteIconName.substring(1);
         }
      }

      String var4 = (String)var1.get("MIDlet-Jar-RSA-SHA1");
      if (var4 != null && !var4.equals("")) {
         this.m_midletType = 2;
      } else {
         this.m_midletType = 1;
      }

   }

   private native String[] getFilesOwnedByMIDlet0(String var1);
}
