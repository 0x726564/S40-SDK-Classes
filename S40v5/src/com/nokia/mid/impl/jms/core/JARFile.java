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
   private byte jY;
   private String m_uri = null;
   private byte[] jZ = null;
   private String jR;
   private InputStream ka;

   public JARFile(byte[] var1, String var2) throws NullPointerException {
      if (var1.length == 0) {
         throw new IllegalArgumentException("jarContent length is zero");
      } else {
         this.jY = 1;
         this.m_uri = var2;
         this.jZ = var1;
         this.jR = "application/java-archive";
      }
   }

   public JARFile(InputStream var1, String var2) throws NullPointerException {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.jY = 2;
         this.m_uri = var2;
         this.ka = var1;
         this.jR = "application/java-archive";
      }
   }

   public JARFile(String var1) throws NullPointerException, IllegalArgumentException {
      if (!File.getFile(var1).exists()) {
         throw new IllegalArgumentException("Specified file does not exist");
      } else {
         this.jY = 3;
         this.m_uri = var1;
         this.jR = "application/java-archive";
      }
   }

   public String getURI() {
      return this.m_uri;
   }

   public boolean setMimeType(String var1) {
      if (!var1.equals("application/java-archive") && !var1.equals("application/vnd.oma.drm.message") && !var1.equals("application/vnd.oma.drm.content")) {
         return false;
      } else {
         this.jR = var1;
         return true;
      }
   }

   public String getMimeType() {
      return this.jR;
   }

   public JADFile createJAD() {
      if (this.jY == 2) {
         return null;
      } else {
         String var1 = null;
         String var2 = this.m_uri;
         if (this.jY == 1) {
            String var3 = FileSystem.getFileSystem().getSystemFilePath(7);
            var1 = var3 + "\\" + "MIDLET" + System.currentTimeMillis();
            if (!FileSystem.getFileSystem().mkdir(var1)) {
               return null;
            }

            String var4 = var1 + "\\" + "JAR" + System.currentTimeMillis() + ".jar";
            if (!this.O(var4)) {
               FileSystem.getFileSystem().delete(var1, false);
               return null;
            }

            var2 = var4;
         }

         byte[] var10;
         if ((var10 = FileSystem.getFileSystem().getFileContentFromJar(var2, "meta-inf/MANIFEST.MF")) == null) {
            return null;
         } else {
            StringBuffer var13 = new StringBuffer();
            JADFile var11;
            Enumeration var5 = (var11 = new JADFile(var10, (String)null)).getAllProperties().keys();

            while(var5.hasMoreElements()) {
               String var7 = (String)var5.nextElement();
               String var8 = var11.getProperty(var7);
               var13.append(var7 + " : " + var8 + "\n");
            }

            long var14 = 0L;

            try {
               var14 = File.getFile(var2).getSize();
            } catch (IOException var9) {
            }

            if ((var2 = this.m_uri) == null) {
               var2 = "Dummy.jar";
            }

            var13.append("MIDlet-Jar-Size:" + var14 + "\n");
            var13.append("MIDlet-Jar-URL:" + var2 + "\n");
            JADFile var12 = new JADFile(var13.toString().getBytes(), (String)null);
            if (this.jY == 1) {
               FileSystem.getFileSystem().delete(var1, false);
            }

            return var12;
         }
      }
   }

   final boolean O(String var1) {
      if (this.jY == 3) {
         return false;
      } else {
         FileOutputStream var2 = null;

         try {
            var2 = new FileOutputStream(var1, true, true);
            if (this.jY == 1) {
               var2.write(this.jZ, 0, this.jZ.length);
            } else {
               boolean var6 = false;
               byte[] var7 = new byte[5120];

               int var3;
               while((var3 = this.ka.read(var7, 0, 5120)) > 0) {
                  var2.write(var7, 0, var3);
               }

               this.ka.close();
               this.ka = null;
            }

            var2.close();
            return true;
         } catch (IOException var5) {
            try {
               if (var2 != null) {
                  var2.close();
               }

               if (this.ka != null) {
                  this.ka.close();
               }
            } catch (IOException var4) {
            }

            return false;
         }
      }
   }
}
