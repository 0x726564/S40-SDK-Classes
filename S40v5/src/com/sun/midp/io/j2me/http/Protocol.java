package com.sun.midp.io.j2me.http;

import com.nokia.mid.impl.isa.ui.MIDletAccess;
import com.nokia.mid.impl.isa.util.SharedObjects;
import com.sun.midp.io.ConnectionBaseAdapter;
import com.sun.midp.io.DateParser;
import com.sun.midp.io.HttpUrl;
import com.sun.midp.io.InternalConnector;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.StreamConnection;
import javax.microedition.midlet.MIDlet;

public class Protocol extends ConnectionBaseAdapter implements HttpConnection {
   protected static final int PORT_NBR_NOT_SET = -1;
   protected int index;
   protected String url;
   protected String protocolType = "http";
   private String host;
   private String file;
   private String ref;
   private String query;
   protected int port = -1;
   private int responseCode = -1;
   private String aK;
   private Hashtable aL = new Hashtable();
   private Hashtable aM = new Hashtable();
   private String[] aN;
   private String[] aO;
   private String method = "GET";
   private int mode;
   private boolean aP = false;
   private boolean aQ = true;
   private boolean k = true;
   private boolean out_closed = true;
   private boolean connected = false;
   private boolean aR = false;
   private Protocol.PrivateInputStream aS;
   private Protocol.PrivateOutputStream aT;
   private DataInputStream aU;
   private StreamConnection aV;
   private DataOutputStream aW;
   private DataInputStream aX;
   private static Timer aY;
   private static MIDletAccess aZ;
   private static int ba = 0;

   public Protocol() {
      this.aP = false;
      this.protocol = "javax.microedition.io.Connector.http";
   }

   public int readBytes(byte[] var1, int var2, int var3) throws IOException {
      throw new RuntimeException("Function not supported");
   }

   public int writeBytes(byte[] var1, int var2, int var3) throws IOException {
      throw new RuntimeException("Function not supported");
   }

   public void disconnect() throws IOException {
      throw new RuntimeException("Function not supported");
   }

   public void connect(String var1, int var2, boolean var3) throws IOException {
      this.url = var1;
      this.mode = var2;
      if ((new HttpUrl(this.protocolType, var1)).host == null) {
         throw new IllegalArgumentException("missing host in URL");
      } else {
         this.parseURL();
         this.aQ = false;
      }
   }

   public void close() throws IOException {
      if (!this.aQ) {
         this.aQ = true;
         this.aR = true;
         if ((this.k || this.aS == null) && (this.out_closed || this.aT == null)) {
            this.closeConnection();
         }

      }
   }

   public InputStream openInputStream() throws IOException {
      if (this.aQ) {
         throw new IOException("connection is closed");
      } else if (this.aS != null) {
         throw new IOException("already open");
      } else if (this.mode != 1 && this.mode != 3) {
         throw new IOException("write-only connection");
      } else {
         this.connect();
         this.aS = new Protocol.PrivateInputStream(this);
         this.k = false;
         ++this.iStreams;
         return this.aS;
      }
   }

   public DataInputStream openDataInputStream() throws IOException {
      if (this.aU != null) {
         throw new IOException("already open");
      } else {
         if (this.aS == null) {
            this.openInputStream();
         }

         this.aU = new DataInputStream(this.aS);
         return this.aU;
      }
   }

   public Object prepareCMSourceId() throws IOException {
      if (this.aQ) {
         throw new IOException("connection is closed");
      } else if (this.aS != null) {
         throw new IOException("already open");
      } else if (this.mode != 1 && this.mode != 3) {
         throw new IOException("write-only connection");
      } else if (System.getProperty("ENABLE_HTTP_WIRE") != null) {
         throw new IOException("streaming over the bridge isn't supported");
      } else {
         synchronized(SharedObjects.getLock("com.sun.midp.io.j2me.http.Protocol.connectLock")) {
            this.c(true);
         }

         return this.aV;
      }
   }

   public OutputStream openOutputStream() throws IOException {
      return this.openDataOutputStream();
   }

   public DataOutputStream openDataOutputStream() throws IOException {
      if (this.mode != 2 && this.mode != 3) {
         throw new IOException("read-only connection");
      } else if (this.aQ) {
         throw new IOException("connection is closed");
      } else if (this.aT != null) {
         throw new IOException("already open");
      } else {
         this.aT = new Protocol.PrivateOutputStream(this);
         this.out_closed = false;
         ++this.oStreams;
         return new DataOutputStream(this.aT);
      }
   }

   public String getURL() {
      return this.protocolType + ":" + this.url;
   }

   public String getProtocol() {
      return this.protocolType;
   }

   public String getHost() {
      return this.host.length() == 0 ? null : this.host;
   }

   public String getFile() {
      return this.file.length() == 0 ? null : this.file;
   }

   public String getRef() {
      return this.ref.length() == 0 ? null : this.ref;
   }

   public String getQuery() {
      return this.query.length() == 0 ? null : this.query;
   }

   public int getPort() {
      return this.port == -1 ? 80 : this.port;
   }

   public String getRequestMethod() {
      return this.method;
   }

   public void setRequestMethod(String var1) throws IOException {
      if (this.aQ) {
         throw new IOException("connection is closed");
      } else if (this.connected) {
         throw new IOException("connection already open");
      } else if (!var1.equals("HEAD") && !var1.equals("GET") && !var1.equals("POST")) {
         throw new IOException("unsupported method: " + var1);
      } else if (this.out_closed) {
         this.method = new String(var1);
      }
   }

   public String getRequestProperty(String var1) {
      return (String)this.aL.get(var1.toLowerCase());
   }

   public void setRequestProperty(String var1, String var2) throws IOException {
      if (this.out_closed && this.k && this.aQ) {
         throw new IOException("connection is closed");
      } else if (this.connected) {
         throw new IOException("connection already open");
      } else if (this.out_closed) {
         if (var1.length() <= 39 && var2.length() <= 30000) {
            char[] var3 = new char[]{'\r', '\n', '\u0000'};

            for(int var4 = 0; var4 < var3.length; ++var4) {
               if (var1.indexOf(var3[var4]) != -1 || var2.indexOf(var3[var4]) != -1) {
                  throw new IllegalArgumentException("HTTP Property contains an unacceptable character");
               }
            }

            if (var1.indexOf(58) != -1) {
               throw new IllegalArgumentException("HTTP Property contains an unacceptable character");
            } else {
               this.b(var1, var2);
            }
         } else {
            throw new IllegalArgumentException("HTTP Property too large");
         }
      }
   }

   private void b(String var1, String var2) throws IOException {
      if (this.connected) {
         throw new IOException("connection already open");
      } else {
         this.aL.put(var1.toLowerCase(), var2);
      }
   }

   public int getResponseCode() throws IOException {
      if (this.aQ) {
         throw new IOException("connection is closed");
      } else {
         this.connect();
         return this.responseCode;
      }
   }

   public String getResponseMessage() throws IOException {
      if (this.aQ) {
         throw new IOException("connection is closed");
      } else {
         this.connect();
         return this.aK;
      }
   }

   public long getLength() {
      if (this.aQ) {
         return -1L;
      } else {
         try {
            return (long)this.getHeaderFieldInt("content-length", -1);
         } catch (IOException var1) {
            return -1L;
         }
      }
   }

   public String getType() {
      if (this.aQ) {
         return null;
      } else {
         try {
            return this.getHeaderField("content-type");
         } catch (IOException var1) {
            return null;
         }
      }
   }

   public String getEncoding() {
      if (this.aQ) {
         return null;
      } else {
         try {
            return this.getHeaderField("content-encoding");
         } catch (IOException var1) {
            return null;
         }
      }
   }

   public long getExpiration() throws IOException {
      if (this.aQ) {
         throw new IOException("connection is closed");
      } else {
         return this.getHeaderFieldDate("expires", 0L);
      }
   }

   public long getDate() throws IOException {
      if (this.aQ) {
         throw new IOException("connection is closed");
      } else {
         return this.getHeaderFieldDate("date", 0L);
      }
   }

   public long getLastModified() throws IOException {
      if (this.aQ) {
         throw new IOException("connection is closed");
      } else {
         return this.getHeaderFieldDate("last-modified", 0L);
      }
   }

   public String getHeaderField(String var1) throws IOException {
      if (this.aQ) {
         throw new IOException("connection is closed");
      } else {
         this.connect();
         return var1 != null ? (String)this.aM.get(var1.toLowerCase()) : null;
      }
   }

   public String getHeaderField(int var1) throws IOException {
      if (this.aQ) {
         throw new IOException("connection is closed");
      } else {
         this.connect();
         if (this.aO == null) {
            Protocol var2 = this;
            int var3 = 0;
            this.aO = new String[this.aM.size()];

            for(Enumeration var4 = this.aM.keys(); var4.hasMoreElements(); var2.aO[var3++] = (String)var2.aM.get(var4.nextElement())) {
            }
         }

         return var1 < this.aO.length && var1 >= 0 ? this.aO[var1] : null;
      }
   }

   public String getHeaderFieldKey(int var1) throws IOException {
      if (this.aQ) {
         throw new IOException("connection is closed");
      } else {
         this.connect();
         if (this.aN == null) {
            Protocol var2 = this;
            int var3 = 0;
            this.aN = new String[this.aM.size()];

            for(Enumeration var4 = this.aM.keys(); var4.hasMoreElements(); var2.aN[var3++] = (String)var4.nextElement()) {
            }
         }

         return var1 < this.aN.length && var1 >= 0 ? this.aN[var1] : null;
      }
   }

   public int getHeaderFieldInt(String var1, int var2) throws IOException {
      String var4 = this.getHeaderField(var1);

      try {
         return Integer.parseInt(var4);
      } catch (Throwable var3) {
         return var2;
      }
   }

   public long getHeaderFieldDate(String var1, long var2) throws IOException {
      String var5 = this.getHeaderField(var1);

      try {
         return DateParser.parse(var5);
      } catch (Throwable var4) {
         return var2;
      }
   }

   protected void connect() throws IOException {
      if (this.aR && this.out_closed) {
         throw new IOException("connection is not open");
      } else if (!this.connected) {
         if (System.getProperty("ENABLE_HTTP_WIRE") != null) {
            throw new IOException("TCPIP Bridge not supported");
         } else {
            synchronized(SharedObjects.getLock("com.sun.midp.io.j2me.http.Protocol.connectLock")) {
               this.c(false);
            }
         }
      }
   }

   private void c(boolean var1) throws IOException {
      if (this.aT != null && this.aT.size() > 0 && !this.method.equals("POST")) {
         throw new IOException("GET and HEAD requests can't include an entity body");
      } else {
         this.aV = (StreamConnection)InternalConnector.openInternal("wap://" + this.protocolType + "://" + this.host + (this.port == -1 ? "" : ":" + this.port) + (this.getFile() == null ? "/" : this.getFile()) + (this.getRef() == null ? "" : "#" + this.getRef()) + (this.getQuery() == null ? "" : "?" + this.getQuery()), 3, false);
         this.aW = this.aV.openDataOutputStream();
         if (this.getRequestProperty("Content-Length") == null) {
            this.b("Content-Length", "" + (this.aT == null ? 0 : this.aT.size()));
         }

         if (this.getRequestProperty("Accept") == null) {
            this.b("Accept", "*/*");
         }

         this.setRequestMethod0(this.method);
         Enumeration var2 = this.aL.keys();
         String var3;
         if (this.method.equals("POST")) {
            if ((var3 = this.getRequestProperty("content-type")) != null) {
               this.setRequestProperty0("Content-Type", var3);
            } else {
               this.setRequestProperty0("Content-Type", "text/plain");
            }
         }

         var3 = (String)this.aL.get("user-agent");
         if (this.checkIfUntrusted()) {
            if (var3 == null) {
               this.aL.put("user-agent", " UNTRUSTED/1.0");
            } else {
               this.aL.put("user-agent", var3 + " UNTRUSTED/1.0");
            }
         }

         while(var2.hasMoreElements()) {
            if (!(var3 = (String)var2.nextElement()).equals("content-type")) {
               this.setRequestProperty0(var3, (String)this.aL.get(var3));
            }
         }

         if (this.aT != null) {
            this.aW.write(this.aT.toByteArray());
         }

         this.aW.flush();
         this.connected = true;
         p();
         ++ba;
         if (var1) {
            ((com.nokia.mid.impl.isa.io.protocol.internal.wap.Protocol)this.aV).prepareCMSourceId();
         } else {
            this.aX = this.aV.openDataInputStream();
         }

         Protocol var7 = this;

         for(this.aK = null; var7.aK == null; var7.aK = var7.getResponseMessage0()) {
         }

         var7.responseCode = var7.getResponseCode0();
         var7 = this;

         for(String var6 = this.getFirstHeaderEntry0(); var6 != null && !var6.equals(""); var6 = var7.getNextHeaderEntry0()) {
            int var4;
            if ((var4 = var6.indexOf(58)) < 0) {
               throw new IOException("malformed header field");
            }

            String var5;
            if ((var5 = var6.substring(0, var4)).length() == 0) {
               throw new IOException("malformed header field");
            }

            if (var6.length() <= var4 + 2) {
               var3 = "";
            } else {
               var3 = var6.substring(var4 + 2).trim();
            }

            var7.aM.put(var5.toLowerCase(), var3);
         }

      }
   }

   private static String a(InputStream var0) {
      StringBuffer var2 = new StringBuffer();

      while(true) {
         int var1;
         try {
            if ((var1 = var0.read()) < 0) {
               return null;
            }
         } catch (IOException var3) {
            return null;
         }

         if (var1 != 13) {
            if (var1 == 10) {
               return var2.toString();
            }

            var2.append((char)var1);
         }
      }
   }

   protected void closeConnection() throws IOException {
      if (this.aW != null) {
         this.aW.close();
         this.aW = null;
      }

      if (this.aX != null) {
         this.aX.close();
         this.aX = null;
      }

      if (this.aV != null) {
         this.aV.close();
         this.aV = null;
      }

      this.responseCode = -1;
      this.aK = null;
      this.connected = false;
      if (--ba == 0) {
         aY = new Timer();
         aZ.registerTimer((MIDlet)null, aY);
         Protocol.TimerClient var1 = new Protocol.TimerClient(this);
         aY.schedule(var1, 10000L);
      }

   }

   protected String parseProtocol() throws IOException {
      int var1;
      if ((var1 = this.url.indexOf(58)) <= 0) {
         throw new IOException("malformed URL");
      } else {
         String var2;
         if (!(var2 = this.url.substring(0, var1)).equals("http")) {
            throw new IOException("protocol must be 'http'");
         } else {
            this.index = var1 + 1;
            return var2;
         }
      }
   }

   private int o() throws IOException, IllegalArgumentException {
      boolean var1 = false;
      String var2;
      if (!(var2 = this.url.substring(this.index)).startsWith(":")) {
         return -1;
      } else {
         var2 = var2.substring(1);
         ++this.index;
         int var3;
         if ((var3 = var2.indexOf(47)) < 0) {
            var3 = var2.length();
         }

         int var5;
         try {
            if ((var5 = Integer.parseInt(var2.substring(0, var3))) <= 0) {
               throw new NumberFormatException();
            }
         } catch (NumberFormatException var4) {
            throw new IllegalArgumentException("invalid port");
         }

         this.index += var3;
         return var5;
      }
   }

   protected synchronized void parseURL() throws IOException, IllegalArgumentException {
      this.index = 0;
      String var2;
      if ((var2 = this.url.substring(this.index)).startsWith("//")) {
         var2 = var2.substring(2);
         this.index += 2;
      }

      int var4 = var2.indexOf("[");
      int var5 = var2.indexOf("]");
      int var3;
      if (var4 != -1 && var5 != -1 && var4 < var5) {
         var3 = var2.indexOf(":", var5);
      } else {
         var3 = var2.indexOf(58);
      }

      if (var3 < 0) {
         var3 = var2.indexOf(47);
      }

      if (var3 < 0) {
         var3 = var2.length();
      }

      var2 = var2.substring(0, var3);
      this.index += var3;
      this.host = var2;
      this.port = this.o();
      var2 = "";
      String var10001;
      String var6;
      if ((var6 = this.url.substring(this.index)).length() == 0) {
         var10001 = var2;
      } else {
         if (!var6.startsWith("/")) {
            throw new IllegalArgumentException("invalid path");
         }

         var4 = var6.indexOf(35);
         var5 = var6.indexOf(63);
         if (var4 < 0 && var5 < 0) {
            var4 = var6.length();
         } else if (var4 < 0 || var5 > 0 && var5 < var4) {
            var4 = var5;
         }

         var2 = var6.substring(0, var4);
         this.index += var4;
         var10001 = var2;
      }

      this.file = var10001;
      if ((var2 = this.url.substring(this.index)).length() != 0 && var2.startsWith("?")) {
         var6 = var2.substring(1);
         if ((var4 = var2.indexOf(35)) > 0) {
            var6 = var2.substring(1, var4);
            this.index += var4;
         }

         var10001 = var6;
      } else {
         var10001 = "";
      }

      this.query = var10001;
      if ((var2 = this.url.substring(this.index)).length() != 0 && var2.charAt(0) != '?') {
         if (!var2.startsWith("#")) {
            throw new IllegalArgumentException("invalid ref");
         }

         if ((var3 = var2.indexOf(63)) < 0) {
            var3 = var2.length();
         }

         this.index += var3;
         var10001 = var2.substring(1, var3);
      } else {
         var10001 = "";
      }

      this.ref = var10001;
   }

   public static void s_setTimerDatabase(MIDletAccess var0) {
      if (aZ == null) {
         aZ = var0;
      } else {
         throw new SecurityException();
      }
   }

   private static void p() {
      if (aY != null) {
         aY.cancel();
         aZ.deregisterTimer((MIDlet)null, aY);
         aY = null;
      }

   }

   private native void setRequestMethod0(String var1) throws IOException;

   private native void setRequestProperty0(String var1, String var2) throws IOException;

   private native String getFirstHeaderEntry0() throws IOException;

   private native String getNextHeaderEntry0() throws IOException;

   private native int getResponseCode0() throws IOException;

   private native String getResponseMessage0() throws IOException;

   private native void loaderstop0();

   private native boolean checkIfUntrusted();

   private native int getBadCertificateID0();

   static Hashtable a(Protocol var0) {
      return var0.aM;
   }

   static boolean b(Protocol var0) {
      return var0.k;
   }

   static boolean c(Protocol var0) {
      return var0.connected;
   }

   static DataInputStream d(Protocol var0) {
      return var0.aX;
   }

   static String a(Protocol var0, InputStream var1) {
      return a(var1);
   }

   static boolean a(Protocol var0, boolean var1) {
      return var0.k = true;
   }

   static int e(Protocol var0) {
      return var0.iStreams--;
   }

   static boolean f(Protocol var0) {
      return var0.aQ;
   }

   static boolean g(Protocol var0) {
      return var0.out_closed;
   }

   static Protocol.PrivateOutputStream h(Protocol var0) {
      return var0.aT;
   }

   static boolean b(Protocol var0, boolean var1) {
      return var0.out_closed = true;
   }

   static int i(Protocol var0) {
      return var0.oStreams--;
   }

   static Protocol.PrivateInputStream j(Protocol var0) {
      return var0.aS;
   }

   static void k(Protocol var0) {
      p();
   }

   static void l(Protocol var0) {
      var0.loaderstop0();
   }

   private class TimerClient extends TimerTask {
      private final Protocol aJ;

      public final void run() {
         if ("pd".equals(System.getProperty("com.nokia.network.access"))) {
            Protocol.k(this.aJ);
         } else {
            Protocol.l(this.aJ);
            Protocol.k(this.aJ);
         }
      }

      TimerClient(Protocol var1, Object var2) {
         this.aJ = var1;
      }
   }

   class PrivateOutputStream extends OutputStream {
      private ByteArrayOutputStream ed;
      private final Protocol aJ;

      public PrivateOutputStream(Protocol var1) {
         this.aJ = var1;
         this.ed = new ByteArrayOutputStream();
      }

      private void checkConnection() throws IOException {
         if (Protocol.g(this.aJ)) {
            throw new IOException("connection is not open");
         }
      }

      public void write(int var1) throws IOException {
         this.checkConnection();
         this.ed.write(var1);
      }

      public void flush() throws IOException {
         this.checkConnection();
         if (this.ed.size() > 0) {
            this.aJ.connect();
         }

      }

      final byte[] toByteArray() {
         return this.ed.toByteArray();
      }

      final int size() {
         return this.ed.size();
      }

      public void close() throws IOException {
         if (!Protocol.g(this.aJ)) {
            this.aJ.connect();
            this.flush();
            Protocol.b(this.aJ, true);
            Protocol.i(this.aJ);
            if (Protocol.f(this.aJ) && (Protocol.b(this.aJ) || Protocol.j(this.aJ) == null) && Protocol.c(this.aJ)) {
               this.aJ.closeConnection();
            }

         }
      }
   }

   class PrivateInputStream extends InputStream {
      private int bC;
      private boolean bD;
      private boolean eof;
      private final Protocol aJ;

      PrivateInputStream(Protocol var1) throws IOException {
         this.aJ = var1;
         this.bC = 0;
         this.bD = false;
         this.eof = false;
         String var2;
         if ((var2 = (String)Protocol.a(var1).get("transfer-encoding")) != null && var2.equals("chunked")) {
            this.bD = true;
            this.bC = this.r();
            this.eof = this.bC == 0;
         }

      }

      private void checkConnection() throws IOException {
         if (Protocol.b(this.aJ)) {
            throw new IOException("connection is not open");
         } else if (!Protocol.c(this.aJ)) {
            throw new IOException("connection is not open");
         }
      }

      public int available() throws IOException {
         this.checkConnection();
         return this.bD ? this.bC : Protocol.d(this.aJ).available();
      }

      public int read() throws IOException {
         this.checkConnection();
         if (this.eof) {
            return -1;
         } else {
            if (this.bC <= 0 && this.bD) {
               this.s();
               this.bC = this.r();
               if (this.bC == 0) {
                  this.eof = true;
                  return -1;
               }
            }

            int var1 = Protocol.d(this.aJ).read();
            this.eof = var1 == -1;
            if (this.bC > 0) {
               --this.bC;
            }

            return var1;
         }
      }

      public int read(byte[] var1, int var2, int var3) throws IOException {
         this.checkConnection();
         if (var1 == null) {
            throw new NullPointerException("Buffer is null");
         } else if (var3 >= 0 && var2 >= 0 && (var2 <= 0 || var2 != var1.length) && var3 + var2 <= var1.length) {
            if (this.eof) {
               return -1;
            } else {
               if (this.bC <= 0 && this.bD) {
                  this.s();
                  this.bC = this.r();
                  if (this.bC == 0) {
                     this.eof = true;
                     return -1;
                  }
               }

               if (this.bD && var3 > this.bC) {
                  var3 = this.bC;
               }

               int var4 = Protocol.d(this.aJ).read(var1, var2, var3);
               this.bC -= var3;
               if (this.bC < 0) {
                  this.bC = 0;
               }

               return var4;
            }
         } else {
            throw new IndexOutOfBoundsException("Illegal length or offset");
         }
      }

      private int r() throws IOException {
         boolean var1 = false;

         try {
            String var5;
            if ((var5 = Protocol.a(this.aJ, Protocol.d(this.aJ))) == null) {
               throw new IOException("No Chunk Size");
            } else {
               int var2;
               for(var2 = 0; var2 < var5.length() && Character.digit(var5.charAt(var2), 16) != -1; ++var2) {
               }

               int var6 = Integer.parseInt(var5.substring(0, var2), 16);
               return var6;
            }
         } catch (NumberFormatException var4) {
            throw new IOException("Bogus chunk size");
         }
      }

      private void s() throws IOException {
         if (Protocol.d(this.aJ).read() != 13) {
            throw new IOException("missing CRLF");
         } else if (Protocol.d(this.aJ).read() != 10) {
            throw new IOException("missing CRLF");
         }
      }

      public void close() throws IOException {
         if (!Protocol.b(this.aJ)) {
            Protocol.a(this.aJ, true);
            Protocol.e(this.aJ);
            if (Protocol.f(this.aJ) && (Protocol.g(this.aJ) || Protocol.h(this.aJ) == null) && Protocol.c(this.aJ)) {
               this.aJ.closeConnection();
            }

         }
      }
   }
}
