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
   public static final int ERROR_938_CONTENT_HANDLER_CONFLICT = -340;
   public static final int ERROR_939_CONTENT_HANDLER_FAIL = -360;
   private int m_midletId;
   private int m_midletStatus;
   private int m_errorCode;
   private String m_jadFilename;
   private JADFile m_jad;
   private static String[] tmpRmsFileNames;
   private int m_midletType = 0;
   private String m_midletSuiteIconName = null;
   String m_midletSuiteName = null;

   public static MIDletSuite getMIDletSuite(int midletId) {
      MIDletSuite suite = null;
      String location = MIDletRegistry.getMIDletRegistry().getMIDletLocation(midletId);
      if (location == null) {
         return null;
      } else {
         int length = location.length();
         String midletSuiteJadLocation = location.substring(0, length - 4) + ".jad";
         suite = new MIDletSuite(midletId, location, midletSuiteJadLocation);
         return suite;
      }
   }

   static MIDletSuite getMIDletSuite(String jar, String jad) {
      if (jar != null && jad != null) {
         try {
            return new MIDletSuite(-1, jar, jad);
         } catch (IllegalArgumentException var3) {
            return null;
         }
      } else {
         return null;
      }
   }

   private MIDletSuite(int midletId, String jarFile, String jadFile) throws IllegalArgumentException {
      super(jarFile);
      this.setup(jarFile, jadFile);
      if (midletId <= 0) {
         this.m_midletId = MIDletRegistry.getMIDletRegistry().findMIDletSuiteByLocation(jarFile);
      } else {
         this.m_midletId = midletId;
      }

   }

   private void setup(String jarFile, String jadFile) throws IllegalArgumentException {
      this.m_jadFilename = jadFile;
      if (super.exists() && (this.m_jad = this.createJAD(jadFile)) != null) {
         this.setMIDletStatus(0);
      } else {
         this.setMIDletStatus(6);
      }

      this.m_errorCode = 1;
   }

   private JADFile createJAD(String jadFilePath) {
      String jarFilePath = this.getPath();
      JADFile jad = null;
      File jadTest = File.getFile(jadFilePath);

      try {
         if (jadTest.exists()) {
            jad = new JADFile(jadFilePath);
         } else if ((jad = (new JARFile(jarFilePath)).createJAD()) != null) {
            FileSystem.getFileSystem().createFile(jadFilePath, new byte[0], 0, 0);
            jad.saveTo(jadFilePath);
         }
      } catch (IOException var6) {
      }

      if (jad != null) {
         this.cacheProperties(jad.getAllProperties());
      }

      return jad;
   }

   public int install(int stage) {
      this.getMIDletStatus();
      switch(stage) {
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

   void setMIDletStatus(int midletStatus) {
      this.m_midletStatus = midletStatus;
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

   void setErrorCode(int error) {
      this.m_errorCode = error;
   }

   public byte[] getIcon() {
      String iconFileName = null;
      if (this.m_midletSuiteIconName != null) {
         iconFileName = this.m_midletSuiteIconName;
      } else {
         iconFileName = this.getJADProperty("MIDlet-Icon");
         if (iconFileName == null || iconFileName.length() == 0) {
            return null;
         }

         iconFileName = iconFileName.trim();
         if (iconFileName.charAt(0) == '/') {
            iconFileName = iconFileName.substring(1);
         }
      }

      return FileSystem.getFileSystem().getFileContentFromJar(this.getPath(), iconFileName);
   }

   public byte[] getIcon(int midletNum) {
      String temp = this.getJADProperty("MIDlet-" + midletNum);
      if (temp == null) {
         return null;
      } else {
         int startPos;
         if ((startPos = temp.indexOf(44)) == -1) {
            return null;
         } else {
            int endPos;
            if ((endPos = temp.indexOf(44, startPos + 1)) == -1) {
               return null;
            } else {
               String iconFileName = temp.substring(startPos + 1, endPos).trim();
               if (iconFileName.length() == 0) {
                  return null;
               } else {
                  if (iconFileName.charAt(0) == '/') {
                     iconFileName = iconFileName.substring(1);
                  }

                  return FileSystem.getFileSystem().getFileContentFromJar(this.getPath(), iconFileName);
               }
            }
         }
      }
   }

   public Image getDefaultIcon() {
      MIDletAccess ma = InitJALM.s_getMIDletAccessor();
      DisplayAccess da = ma.getDisplayAccessor();
      return da.createImage(Pixmap.createPixmap(0));
   }

   public long getSpaceUsed() {
      long size = 0L;
      String[] files = this.getFilesOwnedByMIDlet();
      if (files != null) {
         File tmp = null;

         for(int i = 0; i < files.length; ++i) {
            if (files[i] != null) {
               tmp = File.getFile(files[i]);
               if (tmp.exists()) {
                  try {
                     size += tmp.getSize();
                  } catch (IOException var7) {
                  }
               }
            }
         }
      }

      return size;
   }

   public String[] getMIDletNames() {
      Hashtable properties;
      if ((properties = this.getAllJADProperties()) == null) {
         return null;
      } else {
         Enumeration keys = properties.keys();
         Hashtable midlets = new Hashtable();

         while(keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            if (key.startsWith("MIDlet-")) {
               int midletNum = -1;
               String tempKey = key.substring("MIDlet-".length()).trim();

               try {
                  midletNum = Integer.parseInt(tempKey);
               } catch (NumberFormatException var9) {
               }

               if (midletNum > 0) {
                  String value = (String)properties.get(key);
                  int pos = value.indexOf(44);
                  if (pos == -1) {
                     return null;
                  }

                  value = value.substring(0, pos);
                  midlets.put(tempKey, value);
               }
            }
         }

         int size = midlets.size();
         String[] result = new String[size];

         for(int i = 0; i < size; ++i) {
            result[i] = (String)midlets.get((new Integer(i + 1)).toString());
         }

         return result;
      }
   }

   public int getMIDletType() {
      if (this.m_midletType != 0) {
         return this.m_midletType;
      } else {
         Hashtable properties;
         if ((properties = this.getAllJADProperties()) == null) {
            return 0;
         } else {
            String hash = (String)properties.get("MIDlet-Jar-RSA-SHA1");
            return hash != null && !hash.equals("") ? 2 : 1;
         }
      }
   }

   public byte[] getMIDletAttribute(int key) throws IOException {
      return MIDletRegistry.getMIDletRegistry().getMIDletAttribute(this.m_midletId, key);
   }

   public String getJADProperty(String key) {
      if (key == null) {
         return null;
      } else if (key.equals("MIDlet-Name") && this.m_midletSuiteName != null) {
         return this.m_midletSuiteName;
      } else {
         Hashtable properties;
         return (properties = this.getAllJADProperties()) == null ? null : (String)properties.get(key);
      }
   }

   public boolean delete() {
      return this.delete(true);
   }

   public boolean delete(boolean failIfNotEmpty) {
      boolean success = false;
      boolean filesDeleted = true;
      String[] files = null;
      if (this.m_midletId > 0) {
         files = this.getFilesOwnedByMIDlet();
         success = Installer.getInstaller().uninstall(this.m_midletId);
      } else {
         files = new String[]{this.getJARFilePath(), this.getJADFilePath()};
         success = true;
      }

      if (success && files != null) {
         File tmp = null;

         for(int i = 0; i < files.length; ++i) {
            if (files[i] != null) {
               tmp = File.getFile(files[i]);
               if (tmp.exists() && !tmp.delete(false)) {
                  filesDeleted = false;
               }
            }
         }
      }

      return success && filesDeleted;
   }

   public boolean rename(String newName) {
      return false;
   }

   public boolean moveTo(File destination) {
      boolean retVal = true;

      try {
         if (!destination.isDirectory()) {
            throw new IllegalArgumentException("destination is not a directory.");
         }
      } catch (IOException var7) {
         var7.printStackTrace();
         throw new IllegalArgumentException("Unable to access the destination.");
      }

      String[] files = this.getFilesOwnedByMIDlet();
      int transId = TransactionManager.startTransaction();

      for(int i = 0; i < files.length; ++i) {
         File tmpFile = File.getFile(files[i]);
         if (!tmpFile.moveTo(destination)) {
            retVal = false;
         }
      }

      TransactionManager.endTransaction(transId);
      return retVal;
   }

   public String[] getFilesOwnedByMIDlet() {
      return this.getFilesOwnedByMIDlet0(this.getJARFilePath());
   }

   public boolean copyTo(File destination) {
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
         JADFile jad = new JADFile(this.getJADFilePath());
         return jad.getAbsoluteJARUrl();
      } catch (Exception var2) {
         return null;
      }
   }

   private Hashtable getAllJADProperties() {
      try {
         JADFile jad = new JADFile(this.m_jadFilename);
         Hashtable mergedHash = jad.getAllProperties();
         byte[] jadBuffer = FileSystem.getFileSystem().getFileContentFromJar(this.getPath(), "meta-inf/MANIFEST.MF");
         if (jadBuffer != null) {
            JADFile temp = new JADFile(jadBuffer, (String)null);
            Enumeration keys = temp.getAllProperties().keys();
            if (mergedHash == null) {
               mergedHash = new Hashtable();
            }

            while(keys.hasMoreElements()) {
               String key = (String)keys.nextElement();
               String value = temp.getProperty(key);
               if (!mergedHash.containsKey(key)) {
                  mergedHash.put(key, value);
               }
            }
         }

         return mergedHash;
      } catch (Exception var8) {
         return null;
      }
   }

   private void cacheProperties(Hashtable properties) {
      this.m_midletSuiteName = (String)properties.get("MIDlet-Name");
      this.m_midletSuiteIconName = (String)properties.get("MIDlet-Icon");
      if (this.m_midletSuiteIconName == null || this.m_midletSuiteIconName.length() == 0) {
         this.m_midletSuiteIconName = (String)properties.get("MIDlet-1");
         if (this.m_midletSuiteIconName != null && this.m_midletSuiteIconName.length() != 0) {
            int startPos;
            int endPos;
            if ((startPos = this.m_midletSuiteIconName.indexOf(44)) != -1 && (endPos = this.m_midletSuiteIconName.indexOf(44, startPos + 1)) != -1) {
               this.m_midletSuiteIconName = this.m_midletSuiteIconName.substring(startPos + 1, endPos).trim();
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

      String hash = (String)properties.get("MIDlet-Jar-RSA-SHA1");
      if (hash != null && !hash.equals("")) {
         this.m_midletType = 2;
      } else {
         this.m_midletType = 1;
      }

   }

   private native String[] getFilesOwnedByMIDlet0(String var1);
}
