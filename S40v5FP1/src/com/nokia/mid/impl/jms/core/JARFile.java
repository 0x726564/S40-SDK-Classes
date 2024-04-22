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

   public JARFile(byte[] jarContent, String uri) throws NullPointerException {
      if (jarContent.length == 0) {
         throw new IllegalArgumentException("jarContent length is zero");
      } else {
         this.m_mode = 1;
         this.m_uri = uri;
         this.m_jarContent = jarContent;
         this.m_mimeType = "application/java-archive";
      }
   }

   public JARFile(InputStream input, String uri) throws NullPointerException {
      if (input == null) {
         throw new NullPointerException();
      } else {
         this.m_mode = 2;
         this.m_uri = uri;
         this.m_inputStream = input;
         this.m_mimeType = "application/java-archive";
      }
   }

   public JARFile(String uri) throws NullPointerException, IllegalArgumentException {
      File jarFile = File.getFile(uri);
      if (!jarFile.exists()) {
         throw new IllegalArgumentException("Specified file does not exist");
      } else {
         this.m_mode = 3;
         this.m_uri = uri;
         this.m_mimeType = "application/java-archive";
      }
   }

   public String getURI() {
      return this.m_uri;
   }

   public boolean setMimeType(String type) {
      if (!type.equals("application/java-archive") && !type.equals("application/vnd.oma.drm.message") && !type.equals("application/vnd.oma.drm.content")) {
         return false;
      } else {
         this.m_mimeType = type;
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
         String tempFolder = null;
         String jarPath = this.m_uri;
         if (this.m_mode == 1) {
            String javaTempRoot = FileSystem.getFileSystem().getSystemFilePath(7);
            tempFolder = javaTempRoot + "\\" + "MIDLET" + System.currentTimeMillis();
            if (!FileSystem.getFileSystem().mkdir(tempFolder)) {
               return null;
            }

            String tempJarFilePath = tempFolder + "\\" + "JAR" + System.currentTimeMillis() + ".jar";
            if (!this.saveTo(tempJarFilePath)) {
               FileSystem.getFileSystem().delete(tempFolder, false);
               return null;
            }

            jarPath = tempJarFilePath;
         }

         byte[] jadBuffer = FileSystem.getFileSystem().getFileContentFromJar(jarPath, "meta-inf/MANIFEST.MF");
         if (jadBuffer == null) {
            return null;
         } else {
            StringBuffer buffer = new StringBuffer();
            JADFile temp = new JADFile(jadBuffer, (String)null);
            Enumeration keys = temp.getAllProperties().keys();

            while(keys.hasMoreElements()) {
               String key = (String)keys.nextElement();
               String value = temp.getProperty(key);
               buffer.append(key + " : " + value + "\n");
            }

            long midletJarSize = 0L;

            try {
               midletJarSize = File.getFile(jarPath).getSize();
            } catch (IOException var11) {
            }

            String midletJarUrl = this.m_uri;
            if (midletJarUrl == null) {
               midletJarUrl = "Dummy.jar";
            }

            buffer.append("MIDlet-Jar-Size:" + midletJarSize + "\n");
            buffer.append("MIDlet-Jar-URL:" + midletJarUrl + "\n");
            JADFile jad = new JADFile(buffer.toString().getBytes(), (String)null);
            if (this.m_mode == 1) {
               FileSystem.getFileSystem().delete(tempFolder, false);
            }

            return jad;
         }
      }
   }

   boolean saveTo(String path) {
      if (this.m_mode == 3) {
         return false;
      } else {
         FileOutputStream out = null;

         try {
            out = new FileOutputStream(path, true, true);
            if (this.m_mode == 1) {
               out.write(this.m_jarContent, 0, this.m_jarContent.length);
            } else {
               int bufferLen = 5120;
               byte[] buffer = new byte[bufferLen];

               int bytesRead;
               while((bytesRead = this.m_inputStream.read(buffer, 0, bufferLen)) > 0) {
                  out.write(buffer, 0, bytesRead);
               }

               this.m_inputStream.close();
               this.m_inputStream = null;
            }

            out.close();
            return true;
         } catch (IOException var7) {
            try {
               if (out != null) {
                  out.close();
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
