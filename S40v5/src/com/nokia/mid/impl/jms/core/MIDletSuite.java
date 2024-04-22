package com.nokia.mid.impl.jms.core;

import com.nokia.mid.impl.isa.ui.InitJALM;
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
   private int eA;
   private int eB;
   private int dO;
   private String eC;
   private int eD = 0;
   private String eE = null;
   private String eF = null;

   public static MIDletSuite getMIDletSuite(int var0) {
      String var1 = null;
      if ((var1 = MIDletRegistry.getMIDletRegistry().getMIDletLocation(var0)) == null) {
         return null;
      } else {
         int var2 = var1.length();
         String var3 = var1.substring(0, var2 - 4) + ".jad";
         return new MIDletSuite(var0, var1, var3);
      }
   }

   static MIDletSuite d(String var0, String var1) {
      if (var0 != null && var1 != null) {
         try {
            return new MIDletSuite(-1, var0, var1);
         } catch (IllegalArgumentException var2) {
            return null;
         }
      } else {
         return null;
      }
   }

   private MIDletSuite(int var1, String var2, String var3) throws IllegalArgumentException {
      super(var2);
      this.eC = var3;
      if (super.exists() && this.E(var3) != null) {
         this.setMIDletStatus(0);
      } else {
         this.setMIDletStatus(6);
      }

      this.dO = 1;
      if (var1 <= 0) {
         this.eA = MIDletRegistry.getMIDletRegistry().findMIDletSuiteByLocation(var2);
      } else {
         this.eA = var1;
      }
   }

   private void setup$16da05f7(String var1) throws IllegalArgumentException {
      this.eC = var1;
      if (super.exists() && this.E(var1) != null) {
         this.setMIDletStatus(0);
      } else {
         this.setMIDletStatus(6);
      }

      this.dO = 1;
   }

   private JADFile E(String var1) {
      String var2 = this.getPath();
      JADFile var3 = null;
      File var4 = File.getFile(var1);

      try {
         if (var4.exists()) {
            var3 = new JADFile(var1);
         } else if ((var3 = (new JARFile(var2)).createJAD()) != null) {
            FileSystem.getFileSystem().createFile(var1, new byte[0], 0, 0);
            var3.O(var1);
         }
      } catch (IOException var5) {
      }

      if (var3 != null) {
         Hashtable var6 = var3.getAllProperties();
         this.eF = (String)var6.get("MIDlet-Name");
         this.eE = (String)var6.get("MIDlet-Icon");
         if (this.eE == null || this.eE.length() == 0) {
            this.eE = (String)var6.get("MIDlet-1");
            if (this.eE != null && this.eE.length() != 0) {
               int var7;
               int var8;
               if ((var7 = this.eE.indexOf(44)) != -1 && (var8 = this.eE.indexOf(44, var7 + 1)) != -1) {
                  this.eE = this.eE.substring(var7 + 1, var8).trim();
               }

               if (this.eE != null && this.eE.length() == 0) {
                  this.eE = null;
               }
            } else {
               this.eE = null;
            }
         }

         if (this.eE != null) {
            this.eE = this.eE.trim();
            if (this.eE.charAt(0) == '/') {
               this.eE = this.eE.substring(1);
            }
         }

         if ((var2 = (String)var6.get("MIDlet-Jar-RSA-SHA1")) != null && !var2.equals("")) {
            this.eD = 2;
         } else {
            this.eD = 1;
         }
      }

      return var3;
   }

   public int install(int var1) {
      this.getMIDletStatus();
      switch(var1) {
      case 1:
         this.L();
         break;
      case 2:
         this.M();
         break;
      case 3:
         this.M();
         this.L();
         break;
      default:
         throw new IllegalArgumentException("Unknown install stage.");
      }

      return this.getMIDletStatus();
   }

   private boolean L() {
      try {
         this.eA = Installer.getInstaller().b(this);
         return true;
      } catch (InstallerException var2) {
         this.eA = -1;
         this.dO = var2.getErrorCode();
         return false;
      }
   }

   private boolean M() {
      try {
         this.eA = Installer.getInstaller().a(this);
         return true;
      } catch (InstallerException var2) {
         this.eA = -1;
         this.dO = var2.getErrorCode();
         return false;
      }
   }

   public int getMIDletId() {
      return this.eA;
   }

   void setMIDletStatus(int var1) {
      this.eB = var1;
   }

   public int getMIDletStatus() {
      this.eB = MIDletRegistry.getMIDletRegistry().getMIDletStatus(this.eA);
      return this.eB;
   }

   public int getErrorCode() {
      return this.dO;
   }

   /** @deprecated */
   public int getDetailedErrorCode() {
      return this.dO;
   }

   void setErrorCode(int var1) {
      this.dO = var1;
   }

   public byte[] getIcon() {
      String var1 = null;
      if (this.eE != null) {
         var1 = this.eE;
      } else {
         if ((var1 = this.getJADProperty("MIDlet-Icon")) == null || var1.length() == 0) {
            return null;
         }

         if ((var1 = var1.trim()).charAt(0) == '/') {
            var1 = var1.substring(1);
         }
      }

      return FileSystem.getFileSystem().getFileContentFromJar(this.getPath(), var1);
   }

   public byte[] getIcon(int var1) {
      String var4;
      if ((var4 = this.getJADProperty("MIDlet-" + var1)) == null) {
         return null;
      } else {
         int var2;
         if ((var2 = var4.indexOf(44)) == -1) {
            return null;
         } else {
            int var3;
            if ((var3 = var4.indexOf(44, var2 + 1)) == -1) {
               return null;
            } else if ((var4 = var4.substring(var2 + 1, var3).trim()).length() == 0) {
               return null;
            } else {
               if (var4.charAt(0) == '/') {
                  var4 = var4.substring(1);
               }

               return FileSystem.getFileSystem().getFileContentFromJar(this.getPath(), var4);
            }
         }
      }
   }

   public Image getDefaultIcon() {
      return InitJALM.s_getMIDletAccessor().getDisplayAccessor().createImage(Pixmap.createPixmap(0));
   }

   public long getSpaceUsed() {
      long var1 = 0L;
      String[] var6;
      if ((var6 = this.getFilesOwnedByMIDlet()) != null) {
         File var3 = null;

         for(int var4 = 0; var4 < var6.length; ++var4) {
            if (var6[var4] != null && (var3 = File.getFile(var6[var4])).exists()) {
               try {
                  var1 += var3.getSize();
               } catch (IOException var5) {
               }
            }
         }
      }

      return var1;
   }

   public String[] getMIDletNames() {
      Hashtable var7;
      if ((var7 = this.getAllJADProperties()) == null) {
         return null;
      } else {
         Enumeration var1 = var7.keys();
         Hashtable var2 = new Hashtable();

         while(var1.hasMoreElements()) {
            String var3;
            if ((var3 = (String)var1.nextElement()).startsWith("MIDlet-")) {
               int var4 = -1;
               String var5 = var3.substring("MIDlet-".length()).trim();

               try {
                  var4 = Integer.parseInt(var5);
               } catch (NumberFormatException var6) {
               }

               if (var4 > 0) {
                  if ((var4 = (var3 = (String)var7.get(var3)).indexOf(44)) == -1) {
                     return null;
                  }

                  var3 = var3.substring(0, var4);
                  var2.put(var5, var3);
               }
            }
         }

         int var8;
         String[] var9 = new String[var8 = var2.size()];

         for(int var10 = 0; var10 < var8; ++var10) {
            var9[var10] = (String)var2.get((new Integer(var10 + 1)).toString());
         }

         return var9;
      }
   }

   public int getMIDletType() {
      if (this.eD != 0) {
         return this.eD;
      } else {
         Hashtable var1;
         if ((var1 = this.getAllJADProperties()) == null) {
            return 0;
         } else {
            String var2;
            return (var2 = (String)var1.get("MIDlet-Jar-RSA-SHA1")) != null && !var2.equals("") ? 2 : 1;
         }
      }
   }

   public byte[] getMIDletAttribute(int var1) throws IOException {
      return MIDletRegistry.getMIDletRegistry().getMIDletAttribute(this.eA, var1);
   }

   public String getJADProperty(String var1) {
      if (var1 == null) {
         return null;
      } else if (var1.equals("MIDlet-Name") && this.eF != null) {
         return this.eF;
      } else {
         Hashtable var2;
         return (var2 = this.getAllJADProperties()) == null ? null : (String)var2.get(var1);
      }
   }

   public boolean delete() {
      return this.delete(true);
   }

   public boolean delete(boolean var1) {
      var1 = false;
      boolean var2 = true;
      String[] var3 = null;
      if (this.eA > 0) {
         var3 = this.getFilesOwnedByMIDlet();
         var1 = Installer.getInstaller().uninstall(this.eA);
      } else {
         (var3 = new String[2])[0] = this.getJARFilePath();
         var3[1] = this.getJADFilePath();
         var1 = true;
      }

      if (var1 && var3 != null) {
         File var5 = null;

         for(int var4 = 0; var4 < var3.length; ++var4) {
            if (var3[var4] != null && (var5 = File.getFile(var3[var4])).exists() && !var5.delete(false)) {
               var2 = false;
            }
         }
      }

      return var1 && var2;
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
      } catch (IOException var6) {
         var6.printStackTrace();
         throw new IllegalArgumentException("Unable to access the destination.");
      }

      String[] var3 = this.getFilesOwnedByMIDlet();
      int var7 = TransactionManager.startTransaction();

      for(int var4 = 0; var4 < var3.length; ++var4) {
         if (!File.getFile(var3[var4]).moveTo(var1)) {
            var2 = false;
         }
      }

      TransactionManager.endTransaction(var7);
      return var2;
   }

   public String[] getFilesOwnedByMIDlet() {
      return this.getFilesOwnedByMIDlet0(this.getJARFilePath());
   }

   public boolean copyTo(File var1) {
      return false;
   }

   public String getJADFilePath() {
      return File.getFile(this.eC).getPath();
   }

   /** @deprecated */
   public String getJARFilePath() {
      return this.getPath();
   }

   public String getAbsoluteJARUrl() {
      try {
         return (new JADFile(this.getJADFilePath())).getAbsoluteJARUrl();
      } catch (Exception var1) {
         return null;
      }
   }

   private Hashtable getAllJADProperties() {
      try {
         Hashtable var1 = (new JADFile(this.eC)).getAllProperties();
         byte[] var6;
         if ((var6 = FileSystem.getFileSystem().getFileContentFromJar(this.getPath(), "meta-inf/MANIFEST.MF")) != null) {
            JADFile var7;
            Enumeration var2 = (var7 = new JADFile(var6, (String)null)).getAllProperties().keys();
            if (var1 == null) {
               var1 = new Hashtable();
            }

            while(var2.hasMoreElements()) {
               String var3 = (String)var2.nextElement();
               String var4 = var7.getProperty(var3);
               if (!var1.containsKey(var3)) {
                  var1.put(var3, var4);
               }
            }
         }

         return var1;
      } catch (Exception var5) {
         return null;
      }
   }

   private native String[] getFilesOwnedByMIDlet0(String var1);
}
