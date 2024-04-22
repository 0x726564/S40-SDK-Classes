package com.nokia.mid.impl.jms.core;

import com.nokia.mid.impl.jms.file.File;
import com.nokia.mid.impl.jms.file.FileInputStream;
import com.nokia.mid.impl.jms.file.FileOutputStream;
import com.nokia.mid.impl.jms.file.FileSystem;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
   private static final byte JADFILE_MODE_MEMORY = 1;
   private static final byte JADFILE_MODE_FILESYSTEM = 2;
   private static final byte PARSE_STATE_LOOK_FOR_KEY = 1;
   private static final byte PARSE_STATE_LOOK_FOR_VALUE = 2;
   private static final byte PARSE_STATE_LOOK_FOR_END_OF_LINE = 3;
   private static final byte CHAR_HORIZ_TAB = 9;
   private static final byte CHAR_LINE_FEED = 10;
   private static final byte CHAR_CARRIAGE_RETURN = 13;
   private static final byte CHAR_END_OF_FILE = 26;
   private static final byte CHAR_SPACE = 32;
   private static final byte CHAR_DELIMITER = 58;
   private byte m_status = 1;
   private byte m_mode = 1;
   private String m_uri = null;
   private byte[] m_jadContent = null;
   private String m_jadEncoding = null;
   private String m_signerOrg;
   int[] m_conflictingMIDlets = null;
   private Hashtable m_properties;
   private boolean m_changed = false;
   int m_errorCode = 0;

   public JADFile(String var1) throws NullPointerException, IllegalArgumentException {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         File var2 = File.getFile(var1);
         if (!var2.exists()) {
            throw new IllegalArgumentException("Specified file does not exist");
         } else {
            this.m_mode = 2;
            this.m_uri = var1;
            this.initErrorCode();
            this.m_jadEncoding = System.getProperty("microedition.encoding");
         }
      }
   }

   public JADFile(byte[] var1, String var2) throws NullPointerException, IndexOutOfBoundsException, IllegalArgumentException {
      if (var1.length == 0) {
         throw new IllegalArgumentException("jadContent length is zero");
      } else {
         this.m_mode = 1;
         this.m_jadContent = var1;
         this.m_uri = var2;
         this.initErrorCode();
         this.m_jadEncoding = System.getProperty("microedition.encoding");
      }
   }

   public JADFile(byte[] var1, String var2, String var3) throws NullPointerException, IndexOutOfBoundsException, IllegalArgumentException {
      if (var1.length == 0) {
         throw new IllegalArgumentException("jadContent length is zero");
      } else {
         this.m_mode = 1;
         this.m_jadContent = var1;
         this.m_uri = var2;
         this.initErrorCode();
         this.m_jadEncoding = var3;
         if (var3 == null || var3.equals("")) {
            this.m_jadEncoding = System.getProperty("microedition.encoding");
         }

      }
   }

   public String getURI() {
      return this.m_uri;
   }

   private boolean parse() {
      if (this.m_properties != null) {
         return true;
      } else {
         if (this.m_jadContent == null) {
            FileInputStream var1 = null;

            try {
               File var2 = File.getFile(this.m_uri);
               int var3 = (int)var2.getSize(false);
               this.m_jadContent = new byte[var3];
               var1 = new FileInputStream(this.m_uri);
               var1.read(this.m_jadContent, 0, var3);
               var1.close();
            } catch (Exception var10) {
               try {
                  if (var1 != null) {
                     var1.close();
                  }
               } catch (Exception var9) {
               }

               return false;
            }
         }

         try {
            this.m_properties = new Hashtable();
            String var12 = new String(this.m_jadContent, this.m_jadEncoding);
            String var13 = null;
            String var14 = null;
            byte var4 = 1;
            int var5 = 0;
            int var6 = var12.length();

            for(int var7 = 0; var7 < var6; ++var7) {
               char var8 = var12.charAt(var7);
               if (var8 != '\r' && var8 != '\n' && var8 != 26 && var7 != var6 - 1) {
                  if (var8 == ':' && var4 == 1) {
                     var13 = var12.substring(var5, var7).trim();
                     if (var13.length() == 0 || var13.indexOf(32) != -1 || var13.indexOf(9) != -1) {
                        return false;
                     }

                     var4 = 2;
                     var5 = var7 + 1;
                  }
               } else {
                  if (var4 == 2) {
                     if (var7 != var6 - 1) {
                        var14 = var12.substring(var5, var7).trim();
                     } else {
                        var14 = var12.substring(var5, var6).trim();
                     }

                     this.m_properties.put(var13, var14);
                  }

                  var5 = var7 + 1;
                  var4 = 1;
                  var13 = null;
                  var14 = null;
               }
            }

            return true;
         } catch (UnsupportedEncodingException var11) {
            return false;
         }
      }
   }

   public boolean validate() {
      boolean var1 = true;
      String var2 = null;
      if (this.m_mode == 1) {
         String var3 = this.getMIDletFolderName();
         if (var3 == null) {
            this.setErrorCode(-200);
            return false;
         }

         var2 = FileSystem.getFileSystem().getSystemFilePath(7);
         var2 = var2 + "\\" + var3 + ".jad";
         if (!this.saveTo(var2)) {
            this.setErrorCode(-121);
            return false;
         }
      } else {
         var2 = this.m_uri;
      }

      this.setErrorCode(Installer.getInstaller().jadValidate(var2, this));
      if (this.m_mode == 1) {
         FileSystem.getFileSystem().delete(var2, false);
      }

      return this.m_errorCode > 0;
   }

   void initErrorCode() {
      this.m_errorCode = 0;
      this.m_status = 1;
   }

   void setErrorCode(int var1) {
      this.m_errorCode = var1;
      if (this.m_errorCode > 0) {
         this.m_status = 2;
      } else {
         this.m_status = 3;
      }

   }

   public int getErrorCode() {
      return this.m_errorCode;
   }

   /** @deprecated */
   public int getDetailedErrorCode() {
      return this.m_errorCode;
   }

   public String getProperty(String var1) {
      return !this.parse() ? null : (String)this.m_properties.get(var1.trim());
   }

   public Hashtable getAllProperties() {
      return !this.parse() ? null : this.m_properties;
   }

   public boolean setProperty(String var1, String var2) {
      if (var1 != null && var2 != null) {
         if (!var1.startsWith("MIDlet-") && !var1.startsWith("MicroEdition-")) {
            if (this.parse()) {
               this.m_properties.put(var1, var2);
               this.m_changed = true;
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
      if (this.m_mode == 2 && this.m_changed) {
         byte[] var1 = this.formatProperties().getBytes();
         FileOutputStream var2 = null;

         try {
            var2 = new FileOutputStream(this.m_uri, false, true);
            var2.truncate(0);
            var2.write(var1, 0, var1.length);
            var2.close();
         } catch (IOException var6) {
            try {
               if (var2 != null) {
                  var2.close();
               }
            } catch (IOException var5) {
            }

            return false;
         }
      }

      return true;
   }

   public int getMIDletType() {
      String var1 = (String)this.m_properties.get("MIDlet-Jar-RSA-SHA1");
      return var1 != null && !var1.equals("") ? 2 : 1;
   }

   public int getStatus() {
      return this.m_status;
   }

   public MIDletSuite[] getConflictingMIDlets() {
      if (this.m_conflictingMIDlets == null) {
         return null;
      } else {
         MIDletSuite[] var1 = new MIDletSuite[this.m_conflictingMIDlets.length];

         for(int var2 = 0; var2 < this.m_conflictingMIDlets.length; ++var2) {
            var1[var2] = MIDletSuite.getMIDletSuite(this.m_conflictingMIDlets[var2]);
         }

         return var1;
      }
   }

   public String getSignerOrg() {
      return this.m_signerOrg;
   }

   void setSignerOrg(String var1) {
      this.m_signerOrg = var1;
   }

   public String getAbsoluteUrl(String var1) {
      try {
         if (var1 == null) {
            return null;
         } else if (!var1.startsWith("http") && !var1.startsWith("https") && !var1.startsWith("//wwww") && !var1.startsWith("www")) {
            String var2 = this.getProperty("Nokia-Update");
            if (var2 == null) {
               return null;
            } else {
               int var3 = var2.lastIndexOf(47);
               if (var3 == -1) {
                  return null;
               } else {
                  int var4 = 0;
                  boolean var5 = true;

                  int var6;
                  int var11;
                  for(var6 = 0; (var11 = var1.indexOf("..", var6)) != -1; var6 = var11 + 1) {
                     ++var4;
                  }

                  int var7 = var4;
                  var6 = 0;

                  for(var5 = false; var7 > 0; --var7) {
                     var11 = var1.indexOf("..", var6);
                     if (var11 == -1) {
                        break;
                     }

                     var6 = var11 + "..".length() + 1;
                  }

                  String var8 = var1.substring(var6);
                  if (var8.startsWith("/")) {
                     var8 = var8.substring(1);
                  }

                  var6 = var3 - 1;
                  var7 = var4;

                  for(var5 = false; var7 > 0; --var7) {
                     var11 = var2.lastIndexOf(47, var6);
                     if (var11 == -1) {
                        break;
                     }

                     var6 = var11 - 1;
                  }

                  String var9 = var2.substring(0, var6 + 2);
                  return var9 + var8;
               }
            }
         } else {
            return var1;
         }
      } catch (Exception var10) {
         return null;
      }
   }

   public String getAbsoluteJARUrl() {
      return this.getAbsoluteUrl(this.getProperty("MIDlet-Jar-URL"));
   }

   boolean saveTo(String var1) {
      byte[] var2 = this.m_jadContent;
      if (this.m_mode == 2) {
         return false;
      } else {
         if (this.m_changed) {
            var2 = this.formatProperties().getBytes();
         }

         FileOutputStream var3 = null;

         try {
            var3 = new FileOutputStream(var1, true, true);
            var3.write(var2, 0, var2.length);
            var3.close();
            return true;
         } catch (IOException var7) {
            try {
               if (var3 != null) {
                  var3.close();
               }
            } catch (IOException var6) {
            }

            return false;
         }
      }
   }

   private String formatProperties() {
      if (!this.parse()) {
         return null;
      } else {
         StringBuffer var1 = new StringBuffer();
         Enumeration var2 = this.m_properties.keys();

         while(var2.hasMoreElements()) {
            String var3 = (String)var2.nextElement();
            String var4 = (String)this.m_properties.get(var3);
            var1.append(var3 + ": " + var4 + "\n");
         }

         return var1.toString();
      }
   }

   String getMIDletFolderName() {
      String var1 = null;
      String var2 = this.getProperty("MIDlet-Vendor");
      String var3 = this.getProperty("MIDlet-Name");
      String var4 = null;
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

            var4 = FileSystem.makeAValidFileName(var1);
            return var4;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }
}
