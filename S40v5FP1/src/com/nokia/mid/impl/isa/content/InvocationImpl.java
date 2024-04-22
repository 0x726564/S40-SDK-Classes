package com.nokia.mid.impl.isa.content;

import com.nokia.mid.impl.isa.source_handling.JavaConsumerSource;
import com.nokia.mid.impl.isa.source_handling.JavaProducerSource;
import com.nokia.mid.impl.isa.util.UrlParser;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.content.ContentHandlerException;
import javax.microedition.content.Invocation;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

public class InvocationImpl {
   public static final int INVALID_ID = -1;
   private int uniqueID;
   private String type;
   private String action;
   private String[] args;
   private byte[] data;
   private String ID;
   private String invokingAppName;
   private String invokingAuthority;
   private String invokingID;
   private InvocationImpl previous;
   private boolean responseRequired;
   private boolean toNative;
   private int status;
   private String url;
   private String username;
   private char[] password;
   private String invokingAppClassName;
   private String serverClassName;
   private int invokingAppMIDletID;
   private int serverMIDletID;
   private long cmSourceId;
   private HttpConnection cachedHttpConnection;
   private boolean returnPrevious;
   private Invocation parent;
   private byte[] sourceRawData;

   public InvocationImpl(Invocation inv) {
      this(inv, (String)null, (String)null, (String)null, true, (String)null);
   }

   public InvocationImpl(Invocation inv, String url) {
      this(inv, url, (String)null, (String)null, true, (String)null);
   }

   public InvocationImpl(Invocation inv, String url, String type) {
      this(inv, url, type, (String)null, true, (String)null);
   }

   public InvocationImpl(Invocation inv, String url, String type, String ID) {
      this(inv, url, type, ID, true, (String)null);
   }

   public InvocationImpl(Invocation inv, String url, String type, String ID, boolean responseRequired, String action) {
      this.toNative = false;
      this.returnPrevious = true;
      this.parent = inv;
      this.url = url;
      this.type = type;
      this.ID = ID;
      this.responseRequired = responseRequired;
      this.action = action;
      this.status = 1;
      this.uniqueID = -1;
   }

   public String findType() throws IOException, ContentHandlerException, SecurityException {
      if (this.type != null) {
         return this.type;
      } else if (this.url == null) {
         throw new ContentHandlerException("URL is null", 2);
      } else {
         String[] uriComps = UrlParser.getUriComponents(this.url);
         if (!this.isHttpUrl() && !this.url.startsWith("file://")) {
            this.checkScheme(uriComps[0]);
            Connector.open(this.url, 1);
            throw new IllegalArgumentException("Only http(s):// and file:// URLs are supported");
         } else {
            if (this.isHttpUrl()) {
               HttpConnection httpConnection;
               if ((httpConnection = this.getCachedHttpConnection()) == null) {
                  httpConnection = (HttpConnection)Connector.open(this.url, 1);
               }

               this.type = httpConnection.getType();

               try {
                  if (this.getCachedHttpConnection() == null) {
                     httpConnection.close();
                  }
               } catch (IOException var5) {
               }

               if (this.type != null) {
                  return this.type;
               }
            }

            String filePath = uriComps[2];
            int index = filePath.lastIndexOf(46);
            if (index != -1) {
               String extension = filePath.substring(index);
               this.type = nativeFindType(extension);
               if (this.type != null) {
                  return this.type;
               }
            }

            throw new ContentHandlerException("Cannot find out content type", 2);
         }
      }
   }

   private void checkScheme(String scheme) {
      if (scheme != null && scheme.length() != 0) {
         if (Character.isDigit(scheme.charAt(0))) {
            throw new IllegalArgumentException("Scheme must start with an alpha character");
         }
      } else {
         throw new IllegalArgumentException("URI shoulc contain a scheme");
      }
   }

   public String getAction() {
      return this.action;
   }

   public String[] getArgs() {
      return this.args == null ? new String[0] : this.args;
   }

   public byte[] getData() {
      return this.data == null ? new byte[0] : this.data;
   }

   public String getID() {
      return this.ID;
   }

   public String getInvokingAppName() {
      return this.status != 2 && this.status != 4 ? null : this.invokingAppName;
   }

   public String getInvokingAuthority() {
      return this.status != 2 && this.status != 4 ? null : this.invokingAuthority;
   }

   public String getInvokingID() {
      return this.status != 2 && this.status != 4 ? null : this.invokingID;
   }

   public InvocationImpl getPrevious() {
      return !this.returnPrevious ? null : this.previous;
   }

   public boolean getResponseRequired() {
      return this.responseRequired;
   }

   public int getStatus() {
      return this.status;
   }

   public String getType() {
      return this.type;
   }

   public String getURL() {
      return this.url;
   }

   public Connection open(boolean timeouts) throws IOException, SecurityException {
      if (this.url == null) {
         throw new NullPointerException();
      } else {
         String[] uriComps = UrlParser.getUriComponents(this.url);
         this.checkScheme(uriComps[0]);
         Connection con = null;
         IOException oopsOpen = null;

         try {
            con = Connector.open(this.url, 1, timeouts);
         } catch (IOException var9) {
            oopsOpen = var9;
         }

         if (this.cmSourceId == 0L && this.sourceRawData == null) {
            if (oopsOpen != null) {
               throw oopsOpen;
            } else {
               return con;
            }
         } else {
            if (con != null) {
               con.close();
            }

            if (this.sourceRawData == null) {
               ByteArrayOutputStream baos = new ByteArrayOutputStream(102400);
               JavaConsumerSource jcs = new JavaConsumerSource(this.cmSourceId, baos, 1024);

               try {
                  jcs.consumeData();
               } catch (Exception var8) {
                  throw new IOException(var8.getMessage());
               }

               jcs = null;
               this.cmSourceId = 0L;
               this.sourceRawData = baos.toByteArray();
               baos = null;
            }

            return new GenericContentImpl(this.sourceRawData, (String)null, this.getType());
         }
      }
   }

   public void setAction(String action) {
      this.action = action;
   }

   public void setArgs(String[] args) {
      this.args = args;
   }

   public void setCredentials(String username, char[] password) {
      this.username = username;
      this.password = password;
   }

   public void setData(byte[] data) {
      this.data = data;
   }

   public void setID(String sID) {
      this.ID = sID;
   }

   public void setResponseRequired(boolean responseRequired) {
      if (this.getStatus() != 1) {
         throw new IllegalStateException();
      } else {
         this.responseRequired = responseRequired;
      }
   }

   public void setType(String type) {
      this.type = type;
   }

   public void setURL(String url) {
      this.url = url;
   }

   public Invocation getParent() {
      return this.parent;
   }

   void copyFrom(InvocationImpl srcInvImpl) throws IOException, SecurityException {
      this.uniqueID = srcInvImpl.uniqueID;
      srcInvImpl.uniqueID = -1;
      this.action = srcInvImpl.action;
      this.ID = srcInvImpl.ID;
      this.invokingAppClassName = srcInvImpl.invokingAppClassName;
      this.invokingAppMIDletID = srcInvImpl.invokingAppMIDletID;
      this.invokingAppName = srcInvImpl.invokingAppName;
      this.invokingAuthority = srcInvImpl.invokingAuthority;
      this.invokingID = srcInvImpl.invokingID;
      this.status = srcInvImpl.status;
      this.previous = srcInvImpl.previous;
      this.responseRequired = srcInvImpl.responseRequired;
      this.toNative = srcInvImpl.toNative;
      this.returnPrevious = srcInvImpl.returnPrevious;
      this.serverClassName = srcInvImpl.serverClassName;
      this.serverMIDletID = srcInvImpl.serverMIDletID;
      this.type = srcInvImpl.type;
      this.url = srcInvImpl.url;
      this.username = srcInvImpl.username;
      if (srcInvImpl.cmSourceId != 0L && srcInvImpl.sourceRawData == null) {
         srcInvImpl.open(true);
      }

      this.sourceRawData = srcInvImpl.sourceRawData;
      if (srcInvImpl.args != null) {
         this.args = new String[srcInvImpl.args.length];
         System.arraycopy(srcInvImpl.args, 0, this.args, 0, this.args.length);
      }

      if (srcInvImpl.data != null) {
         this.data = new byte[srcInvImpl.data.length];
         System.arraycopy(srcInvImpl.data, 0, this.data, 0, this.data.length);
      }

      if (srcInvImpl.password != null) {
         this.password = new char[srcInvImpl.password.length];
         System.arraycopy(srcInvImpl.password, 0, this.password, 0, this.password.length);
      }

   }

   void setInvokingApplicationClassName(String className) {
      this.invokingAppClassName = className;
   }

   void setAppName(String appName) {
      this.invokingAppName = appName;
   }

   void setInvokingAuthority(String invokingAuthority) {
      this.invokingAuthority = invokingAuthority;
   }

   void setServerDetails(String className, int midletID) {
      this.serverClassName = className;
      this.serverMIDletID = midletID;
   }

   void restoreFromNative(int invocationId) {
      this.nativeRestoreSnapshot(invocationId);
   }

   boolean storeToNative() {
      if (this.uniqueID == -1) {
         this.uniqueID = nativeCreateUniqueID();
      }

      if (this.cmSourceId == 0L) {
         this.cmSourceId = this.createCMSourceFromRawData();
      }

      return this.nativeCreateSnapshot(this.previous == null ? -1 : this.previous.uniqueID);
   }

   void setReturnPrevious(boolean returnPrevious) {
      this.returnPrevious = returnPrevious;
   }

   void setStatus(int status) {
      this.status = status;
   }

   void setPrevious(InvocationImpl previous) {
      this.previous = previous;
   }

   int getUniqueID() {
      return this.uniqueID;
   }

   void setInvokingID(String invokingID) {
      this.invokingID = invokingID;
   }

   void setToNative(boolean nativeCH) {
      this.toNative = nativeCH;
   }

   boolean isHttpUrl() {
      return this.url != null && (this.url.startsWith("http://") || this.url.startsWith("https://"));
   }

   void checkHttpCacheMode() throws IOException {
      if (this.isHttpUrl() && this.getType() == null) {
         this.cachedHttpConnection = (HttpConnection)Connector.open(this.getURL(), 1);
      }

   }

   void shutdownHttpCacheMode() {
      if (this.cachedHttpConnection != null) {
         try {
            this.cachedHttpConnection.close();
         } catch (IOException var2) {
         }

         this.cachedHttpConnection = null;
      }
   }

   void cacheInvocationContents() throws IOException {
      if (this.cmSourceId == 0L && this.sourceRawData == null) {
         HttpConnection hc = this.getCachedHttpConnection();
         if (hc == null && this.isHttpUrl()) {
            hc = (HttpConnection)Connector.open(this.getURL(), 1);
            this.cachedHttpConnection = hc;
         }

         InputStream in = hc.openInputStream();
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         byte[] buffer = new byte[10240];
         boolean var5 = false;

         int size;
         while((size = in.read(buffer)) > 0) {
            baos.write(buffer, 0, size);
         }

         this.setSourceRawData(baos.toByteArray());
      }
   }

   private HttpConnection getCachedHttpConnection() {
      return this.cachedHttpConnection;
   }

   private long createCMSourceFromRawData() {
      byte[] cmSrcId = null;
      if (this.sourceRawData != null) {
         JavaProducerSource jps = new JavaProducerSource();
         jps.useActiveSource = false;
         jps.setData(this.sourceRawData);

         try {
            byte[] cmSrcId = jps.generateSourceId();
            return Util.convertSourceID(cmSrcId);
         } catch (Exception var4) {
            throw new RuntimeException(var4.getMessage());
         }
      } else {
         return 0L;
      }
   }

   private void setSourceRawData(byte[] sourceRawData) {
      this.sourceRawData = sourceRawData;
   }

   private native boolean nativeCreateSnapshot(int var1);

   private native void nativeRestoreSnapshot(int var1);

   private static native int nativeCreateUniqueID();

   private static native String nativeFindType(String var0);
}
