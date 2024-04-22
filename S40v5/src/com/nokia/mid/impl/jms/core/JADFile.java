package com.nokia.mid.impl.jms.core;

import com.nokia.mid.impl.jms.file.File;
import com.nokia.mid.impl.jms.file.FileOutputStream;
import com.nokia.mid.impl.jms.file.FileSystem;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

public class JADFile {
   public static final byte JAD_STATE_INITIALIZED = 1;
   public static final byte JAD_STATE_VALID = 2;
   public static final byte JAD_STATE_INVALID = 3;
   public static final String ATTRIB_MIDP_VERSION = "MicroEdition-Profile";
   public static final String ATTRIB_CLDC_VERSION = "MicroEdition-Configuration";
   public static final String ATTRIB_MIDLET_SUITE_NAME = "MIDlet-Name";
   public static final String ATTRIB_MIDLET_VENDOR = "MIDlet-Vendor";
   public static final String ATTRIB_MIDLET_ICON = "MIDlet-Icon";
   public static final String ATTRIB_MIDLET_VERSION = "MIDlet-Version";
   public static final String ATTRIB_MIDLET_DESCRIPTION = "MIDlet-Description";
   public static final String ATTRIB_MIDLET_INFO_URL = "MIDlet-Info-URL";
   public static final String ATTRIB_MIDLET_JAR_URL = "MIDlet-Jar-URL";
   public static final String ATTRIB_MIDLET_JAR_SIZE = "MIDlet-Jar-Size";
   public static final String ATTRIB_MIDLET_DATA_SIZE = "MIDlet-Data-Size";
   public static final String ATTRIB_MIDLET_INSTALL_NOTIFY = "MIDlet-Install-Notify";
   public static final String ATTRIB_MIDLET_DELETE_NOTIFY = "MIDlet-Delete-Notify";
   public static final String ATTRIB_MIDLET_DELETE_CONFIRM = "MIDlet-Delete-Confirm";
   public static final String ATTRIB_MIDLET_JAR_HASH = "MIDlet-Jar-RSA-SHA1";
   public static final String ATTRIB_MIDLET_PREFIX = "MIDlet-";
   public static final String ATTRIB_MICROEDITION_PREFIX = "MicroEdition-";
   public static final String ATTRIB_NOKIA_JAD_URL = "Nokia-Update";
   private byte lL = 1;
   private byte jY = 1;
   private String m_uri = null;
   private byte[] lM = null;
   private String lN = null;
   private String lO;
   int[] js = null;
   private Hashtable lP;
   private boolean lQ = false;
   int dO = 0;

   public JADFile(String var1) throws NullPointerException, IllegalArgumentException {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (!File.getFile(var1).exists()) {
         throw new IllegalArgumentException("Specified file does not exist");
      } else {
         this.jY = 2;
         this.m_uri = var1;
         this.ae();
         this.lN = System.getProperty("microedition.encoding");
      }
   }

   public JADFile(byte[] var1, String var2) throws NullPointerException, IndexOutOfBoundsException, IllegalArgumentException {
      if (var1.length == 0) {
         throw new IllegalArgumentException("jadContent length is zero");
      } else {
         this.jY = 1;
         this.lM = var1;
         this.m_uri = var2;
         this.ae();
         this.lN = System.getProperty("microedition.encoding");
      }
   }

   public JADFile(byte[] var1, String var2, String var3) throws NullPointerException, IndexOutOfBoundsException, IllegalArgumentException {
      if (var1.length == 0) {
         throw new IllegalArgumentException("jadContent length is zero");
      } else {
         this.jY = 1;
         this.lM = var1;
         this.m_uri = var2;
         this.ae();
         this.lN = var3;
         if (var3 == null || var3.equals("")) {
            this.lN = System.getProperty("microedition.encoding");
         }

      }
   }

   public String getURI() {
      return this.m_uri;
   }

   private boolean ad() {
      // $FF: Couldn't be decompiled
   }

   public boolean validate() {
      String var1 = null;
      if (this.jY == 1) {
         String var2;
         if ((var2 = this.getMIDletFolderName()) == null) {
            this.setErrorCode(-200);
            return false;
         }

         var1 = FileSystem.getFileSystem().getSystemFilePath(7);
         var1 = var1 + "\\" + var2 + ".jad";
         if (!this.O(var1)) {
            this.setErrorCode(-121);
            return false;
         }
      } else {
         var1 = this.m_uri;
      }

      this.setErrorCode(Installer.getInstaller().a(var1, this));
      if (this.jY == 1) {
         FileSystem.getFileSystem().delete(var1, false);
      }

      return this.dO > 0;
   }

   private void ae() {
      this.dO = 0;
      this.lL = 1;
   }

   void setErrorCode(int var1) {
      this.dO = var1;
      if (this.dO > 0) {
         this.lL = 2;
      } else {
         this.lL = 3;
      }
   }

   public int getErrorCode() {
      return this.dO;
   }

   /** @deprecated */
   public int getDetailedErrorCode() {
      return this.dO;
   }

   public String getProperty(String var1) {
      return !this.ad() ? null : (String)this.lP.get(var1.trim());
   }

   public Hashtable getAllProperties() {
      return !this.ad() ? null : this.lP;
   }

   public boolean setProperty(String var1, String var2) {
      if (var1 != null && var2 != null) {
         if (!var1.startsWith("MIDlet-") && !var1.startsWith("MicroEdition-")) {
            if (this.ad()) {
               this.lP.put(var1, var2);
               this.lQ = true;
               return true;
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean commitChanges() {
      if (this.jY == 2 && this.lQ) {
         byte[] var1 = this.af().getBytes();
         FileOutputStream var2 = null;

         try {
            (var2 = new FileOutputStream(this.m_uri, false, true)).truncate(0);
            var2.write(var1, 0, var1.length);
            var2.close();
         } catch (IOException var4) {
            try {
               if (var2 != null) {
                  var2.close();
               }
            } catch (IOException var3) {
            }

            return false;
         }
      }

      return true;
   }

   public int getMIDletType() {
      String var1;
      return (var1 = (String)this.lP.get("MIDlet-Jar-RSA-SHA1")) != null && !var1.equals("") ? 2 : 1;
   }

   public int getStatus() {
      return this.lL;
   }

   public MIDletSuite[] getConflictingMIDlets() {
      if (this.js == null) {
         return null;
      } else {
         MIDletSuite[] var1 = new MIDletSuite[this.js.length];

         for(int var2 = 0; var2 < this.js.length; ++var2) {
            var1[var2] = MIDletSuite.getMIDletSuite(this.js[var2]);
         }

         return var1;
      }
   }

   public String getSignerOrg() {
      return this.lO;
   }

   void setSignerOrg(String var1) {
      this.lO = var1;
   }

   public String getAbsoluteUrl(String var1) {
      if (var1 == null) {
         return null;
      } else {
         try {
            if (!var1.startsWith("http") && !var1.startsWith("https") && !var1.startsWith("//wwww") && !var1.startsWith("www")) {
               String var8;
               if ((var8 = this.getProperty("Nokia-Update")) == null) {
                  return null;
               } else {
                  int var2;
                  if ((var2 = var8.lastIndexOf(47)) == -1) {
                     return null;
                  } else {
                     int var3 = 0;
                     boolean var4 = false;

                     int var5;
                     int var9;
                     for(var5 = 0; (var9 = var1.indexOf("..", var5)) != -1; var5 = var9 + 1) {
                        ++var3;
                     }

                     int var6 = var3;

                     for(var5 = 0; var6 > 0 && (var9 = var1.indexOf("..", var5)) != -1; --var6) {
                        var5 = var9 + "..".length() + 1;
                     }

                     if ((var1 = var1.substring(var5)).startsWith("/")) {
                        var1 = var1.substring(1);
                     }

                     var5 = var2 - 1;

                     for(var6 = var3; var6 > 0 && (var9 = var8.lastIndexOf(47, var5)) != -1; --var6) {
                        var5 = var9 - 1;
                     }

                     var8 = var8.substring(0, var5 + 2);
                     return var8 + var1;
                  }
               }
            } else {
               return var1;
            }
         } catch (Exception var7) {
            return null;
         }
      }
   }

   public String getAbsoluteJARUrl() {
      return this.getAbsoluteUrl(this.getProperty("MIDlet-Jar-URL"));
   }

   final boolean O(String var1) {
      byte[] var2 = this.lM;
      if (this.jY == 2) {
         return false;
      } else {
         if (this.lQ) {
            var2 = this.af().getBytes();
         }

         FileOutputStream var5 = null;

         try {
            (var5 = new FileOutputStream(var1, true, true)).write(var2, 0, var2.length);
            var5.close();
            return true;
         } catch (IOException var4) {
            try {
               if (var5 != null) {
                  var5.close();
               }
            } catch (IOException var3) {
            }

            return false;
         }
      }
   }

   private String af() {
      if (!this.ad()) {
         return null;
      } else {
         StringBuffer var1 = new StringBuffer();
         Enumeration var2 = this.lP.keys();

         while(var2.hasMoreElements()) {
            String var3 = (String)var2.nextElement();
            String var4 = (String)this.lP.get(var3);
            var1.append(var3 + ": " + var4 + "\n");
         }

         return var1.toString();
      }
   }

   String getMIDletFolderName() {
      String var1 = null;
      String var2 = this.getProperty("MIDlet-Vendor");
      String var3 = this.getProperty("MIDlet-Name");
      var1 = null;
      if (var2 != null && var3 != null) {
         if (var2.length() != 0 && var3.length() != 0) {
            if (var2.length() < 60) {
               var1 = var2;
            } else {
               var1 = var2.substring(0, 60);
            }

            if (var3.length() < 60) {
               var1 = var1 + var3;
            } else {
               var1 = var1 + var3.substring(0, 60);
            }

            return FileSystem.makeAValidFileName(var1);
         } else {
            return null;
         }
      } else {
         return null;
      }
   }
}
