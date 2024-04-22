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
   private String m_jadEncoding = "UTF-8";
   private String m_signerOrg;
   int[] m_conflictingMIDlets = null;
   private Hashtable m_properties;
   private boolean m_changed = false;
   int m_errorCode = 0;

   public JADFile(String uri) throws NullPointerException, IllegalArgumentException {
      if (uri == null) {
         throw new NullPointerException();
      } else {
         File jadFile = File.getFile(uri);
         if (!jadFile.exists()) {
            throw new IllegalArgumentException("Specified file does not exist");
         } else {
            this.m_mode = 2;
            this.m_uri = uri;
            this.initErrorCode();
         }
      }
   }

   public JADFile(byte[] jadContent, String uri) throws NullPointerException, IndexOutOfBoundsException, IllegalArgumentException {
      if (jadContent.length == 0) {
         throw new IllegalArgumentException("jadContent length is zero");
      } else {
         this.m_mode = 1;
         this.m_jadContent = jadContent;
         this.m_uri = uri;
         this.initErrorCode();
      }
   }

   public JADFile(byte[] jadContent, String uri, String encoding) throws NullPointerException, IndexOutOfBoundsException, IllegalArgumentException {
      if (jadContent.length == 0) {
         throw new IllegalArgumentException("jadContent length is zero");
      } else {
         this.m_mode = 1;
         this.m_jadContent = jadContent;
         this.m_uri = uri;
         this.initErrorCode();
         if (encoding != null && !encoding.equals("")) {
            this.m_jadEncoding = encoding;
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
            FileInputStream in = null;

            try {
               File jadFile = File.getFile(this.m_uri);
               int jadSize = (int)jadFile.getSize(false);
               this.m_jadContent = new byte[jadSize];
               in = new FileInputStream(this.m_uri);
               in.read(this.m_jadContent, 0, jadSize);
               in.close();
            } catch (Exception var10) {
               try {
                  if (in != null) {
                     in.close();
                  }
               } catch (Exception var9) {
               }

               return false;
            }
         }

         try {
            this.m_properties = new Hashtable();
            String jad = new String(this.m_jadContent, this.m_jadEncoding);
            String key = null;
            String value = null;
            int state = 1;
            int prev = 0;
            int jadLength = jad.length();

            for(int curr = 0; curr < jadLength; ++curr) {
               char ch = jad.charAt(curr);
               if (ch != '\r' && ch != '\n' && ch != 26 && curr != jadLength - 1) {
                  if (ch == ':' && state == 1) {
                     key = jad.substring(prev, curr).trim();
                     if (key.length() == 0 || key.indexOf(32) != -1 || key.indexOf(9) != -1) {
                        return false;
                     }

                     state = 2;
                     prev = curr + 1;
                  }
               } else {
                  if (state == 2) {
                     if (curr != jadLength - 1) {
                        value = jad.substring(prev, curr).trim();
                     } else {
                        value = jad.substring(prev, jadLength).trim();
                     }

                     this.m_properties.put(key, value);
                  }

                  prev = curr + 1;
                  state = 1;
                  key = null;
                  value = null;
               }
            }

            return true;
         } catch (UnsupportedEncodingException var11) {
            return false;
         }
      }
   }

   public boolean validate() {
      String tempFilePath = null;
      if (this.m_mode == 1) {
         String midletFolderName = this.getMIDletFolderName();
         if (midletFolderName == null) {
            this.setErrorCode(-200);
            return false;
         }

         tempFilePath = FileSystem.getFileSystem().getSystemFilePath(7);
         tempFilePath = tempFilePath + "\\" + midletFolderName + ".jad";
         if (!this.saveTo(tempFilePath)) {
            this.setErrorCode(-121);
            return false;
         }
      } else {
         tempFilePath = this.m_uri;
      }

      this.setErrorCode(Installer.getInstaller().jadValidate(tempFilePath, this));
      if (this.m_mode == 1) {
         FileSystem.getFileSystem().delete(tempFilePath, false);
      }

      return this.m_errorCode > 0;
   }

   void initErrorCode() {
      this.m_errorCode = 0;
      this.m_status = 1;
   }

   void setErrorCode(int code) {
      this.m_errorCode = code;
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

   public String getProperty(String key) {
      return !this.parse() ? null : (String)this.m_properties.get(key.trim());
   }

   public Hashtable getAllProperties() {
      return !this.parse() ? null : this.m_properties;
   }

   public boolean setProperty(String key, String value) {
      if (key != null && value != null) {
         if (!key.startsWith("MIDlet-") && !key.startsWith("MicroEdition-")) {
            if (this.parse()) {
               this.m_properties.put(key, value);
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
         byte[] jadContent = this.formatProperties().getBytes();
         FileOutputStream out = null;

         try {
            out = new FileOutputStream(this.m_uri, false, true);
            out.truncate(0);
            out.write(jadContent, 0, jadContent.length);
            out.close();
         } catch (IOException var6) {
            try {
               if (out != null) {
                  out.close();
               }
            } catch (IOException var5) {
            }

            return false;
         }
      }

      return true;
   }

   public int getMIDletType() {
      String hash = (String)this.m_properties.get("MIDlet-Jar-RSA-SHA1");
      return hash != null && !hash.equals("") ? 2 : 1;
   }

   public int getStatus() {
      return this.m_status;
   }

   public MIDletSuite[] getConflictingMIDlets() {
      if (this.m_conflictingMIDlets == null) {
         return null;
      } else {
         MIDletSuite[] suites = new MIDletSuite[this.m_conflictingMIDlets.length];

         for(int i = 0; i < this.m_conflictingMIDlets.length; ++i) {
            suites[i] = MIDletSuite.getMIDletSuite(this.m_conflictingMIDlets[i]);
         }

         return suites;
      }
   }

   public String getSignerOrg() {
      return this.m_signerOrg;
   }

   void setSignerOrg(String signer) {
      this.m_signerOrg = signer;
   }

   public String getAbsoluteUrl(String inputUrl) {
      try {
         if (inputUrl == null) {
            return null;
         } else if (!inputUrl.startsWith("http") && !inputUrl.startsWith("https") && !inputUrl.startsWith("//wwww") && !inputUrl.startsWith("www")) {
            String jadUrl = this.getProperty("Nokia-Update");
            if (jadUrl == null) {
               return null;
            } else {
               int jadFileIndex = jadUrl.lastIndexOf(47);
               if (jadFileIndex == -1) {
                  return null;
               } else {
                  int numParentDirs = 0;
                  int pos = true;

                  int startIndex;
                  int pos;
                  for(startIndex = 0; (pos = inputUrl.indexOf("..", startIndex)) != -1; startIndex = pos + 1) {
                     ++numParentDirs;
                  }

                  int temp = numParentDirs;
                  startIndex = 0;

                  for(pos = false; temp > 0; --temp) {
                     pos = inputUrl.indexOf("..", startIndex);
                     if (pos == -1) {
                        break;
                     }

                     startIndex = pos + "..".length() + 1;
                  }

                  String tempInputUrl = inputUrl.substring(startIndex);
                  if (tempInputUrl.startsWith("/")) {
                     tempInputUrl = tempInputUrl.substring(1);
                  }

                  startIndex = jadFileIndex - 1;
                  temp = numParentDirs;

                  for(pos = false; temp > 0; --temp) {
                     pos = jadUrl.lastIndexOf(47, startIndex);
                     if (pos == -1) {
                        break;
                     }

                     startIndex = pos - 1;
                  }

                  String tempJadUrl = jadUrl.substring(0, startIndex + 2);
                  return tempJadUrl + tempInputUrl;
               }
            }
         } else {
            return inputUrl;
         }
      } catch (Exception var10) {
         return null;
      }
   }

   public String getAbsoluteJARUrl() {
      return this.getAbsoluteUrl(this.getProperty("MIDlet-Jar-URL"));
   }

   boolean saveTo(String path) {
      byte[] jadContent = this.m_jadContent;
      if (this.m_mode == 2) {
         return false;
      } else {
         if (this.m_changed) {
            jadContent = this.formatProperties().getBytes();
         }

         FileOutputStream out = null;

         try {
            out = new FileOutputStream(path, true, true);
            out.write(jadContent, 0, jadContent.length);
            out.close();
            return true;
         } catch (IOException var7) {
            try {
               if (out != null) {
                  out.close();
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
         StringBuffer buffer = new StringBuffer();
         Enumeration keys = this.m_properties.keys();

         while(keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            String value = (String)this.m_properties.get(key);
            buffer.append(key + ": " + value + "\n");
         }

         return buffer.toString();
      }
   }

   String getMIDletFolderName() {
      String retVal = null;
      String vendor = this.getProperty("MIDlet-Vendor");
      String suiteName = this.getProperty("MIDlet-Name");
      String validName = null;
      if (vendor != null && suiteName != null) {
         if (vendor.length() != 0 && suiteName.length() != 0) {
            if (vendor.length() < 60) {
               retVal = vendor;
            } else {
               retVal = vendor.substring(0, 60);
            }

            if (suiteName.length() < 60) {
               retVal = retVal + suiteName;
            } else {
               retVal = retVal + suiteName.substring(0, 60);
            }

            validName = FileSystem.makeAValidFileName(retVal);
            return validName;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }
}
