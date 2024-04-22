package com.nokia.mid.impl.jms.core;

import com.nokia.mid.impl.jms.file.File;
import com.nokia.mid.impl.jms.file.FileOutputStream;
import com.nokia.mid.impl.jms.file.FileSystem;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

public class JARFile {
   public static final String MIME_TYPE_JAR = "application/java-archive";
   public static final String MIME_TYPE_DRM_MESSAGE = "application/vnd.oma.drm.message";
   public static final String MIME_TYPE_DRM_CONTENT = "application/vnd.oma.drm.content";
   private static final byte JARFILE_MODE_MEMORY = 1;
   private static final byte JARFILE_MODE_INPUTSTREAM = 2;
   private static final byte JARFILE_MODE_FILESYSTEM = 3;
   private byte m_mode;
   private String m_uri = null;
   private byte[] m_jarContent = null;
   private String m_mimeType;
   private InputStream m_inputStream;

   public JARFile(byte[] var1, String var2) throws NullPointerException {
      if (var1.length == 0) {
         throw new IllegalArgumentException("jarContent length is zero");
      } else {
         this.m_mode = 1;
         this.m_uri = var2;
         this.m_jarContent = var1;
         this.m_mimeType = "application/java-archive";
      }
   }

   public JARFile(InputStream var1, String var2) throws NullPointerException {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.m_mode = 2;
         this.m_uri = var2;
         this.m_inputStream = var1;
         this.m_mimeType = "application/java-archive";
      }
   }

   public JARFile(String var1) throws NullPointerException, IllegalArgumentException {
      File var2 = File.getFile(var1);
      if (!var2.exists()) {
         throw new IllegalArgumentException("Specified file does not exist");
      } else {
         this.m_mode = 3;
         this.m_uri = var1;
         this.m_mimeType = "application/java-archive";
      }
   }

   public String getURI() {
      return this.m_uri;
   }

   public boolean setMimeType(String var1) {
      if (!var1.equals("application/java-archive") && !var1.equals("application/vnd.oma.drm.message") && !var1.equals("application/vnd.oma.drm.content")) {
         return false;
      } else {
         this.m_mimeType = var1;
         return true;
      }
   }

   public String getMimeType() {
      return this.m_mimeType;
   }

   public JADFile createJAD() {
      if (this.m_mode == 2) {
         return null;
      } else {
         String var1 = null;
         String var2 = this.m_uri;
         if (this.m_mode == 1) {
            String var3 = FileSystem.getFileSystem().getSystemFilePath(7);
            var1 = var3 + "\\" + "MIDLET" + System.currentTimeMillis();
            if (!FileSystem.getFileSystem().mkdir(var1)) {
               return null;
            }

            String var4 = var1 + "\\" + "JAR" + System.currentTimeMillis() + ".jar";
            if (!this.saveTo(var4)) {
               FileSystem.getFileSystem().delete(var1, false);
               return null;
            }

            var2 = var4;
         }

         byte[] var12 = FileSystem.getFileSystem().getFileContentFromJar(var2, "meta-inf/MANIFEST.MF");
         if (var12 == null) {
            return null;
         } else {
            StringBuffer var13 = new StringBuffer();
            JADFile var5 = new JADFile(var12, (String)null);
            Enumeration var6 = var5.getAllProperties().keys();

            while(var6.hasMoreElements()) {
               String var7 = (String)var6.nextElement();
               String var8 = var5.getProperty(var7);
               var13.append(var7 + " : " + var8 + "\n");
            }

            long var14 = 0L;

            try {
               var14 = File.getFile(var2).getSize();
            } catch (IOException var11) {
            }

            String var9 = this.m_uri;
            if (var9 == null) {
               var9 = "Dummy.jar";
            }

            var13.append("MIDlet-Jar-Size:" + var14 + "\n");
            var13.append("MIDlet-Jar-URL:" + var9 + "\n");
            JADFile var10 = new JADFile(var13.toString().getBytes(), (String)null);
            if (this.m_mode == 1) {
               FileSystem.getFileSystem().delete(var1, false);
            }

            return var10;
         }
      }
   }

   boolean saveTo(String var1) {
      if (this.m_mode == 3) {
         return false;
      } else {
         FileOutputStream var2 = null;

         try {
            var2 = new FileOutputStream(var1, true, true);
            if (this.m_mode == 1) {
               var2.write(this.m_jarContent, 0, this.m_jarContent.length);
            } else {
               short var3 = 5120;
               byte[] var4 = new byte[var3];

               int var5;
               while((var5 = this.m_inputStream.read(var4, 0, var3)) > 0) {
                  var2.write(var4, 0, var5);
               }

               this.m_inputStream.close();
               this.m_inputStream = null;
            }

            var2.close();
            return true;
         } catch (IOException var7) {
            try {
               if (var2 != null) {
                  var2.close();
               }

               if (this.m_inputStream != null) {
                  this.m_inputStream.close();
               }
            } catch (IOException var6) {
            }

            return false;
         }
      }
   }
}
