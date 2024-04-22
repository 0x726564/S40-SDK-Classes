package com.sun.midp.io.j2me.http;

import com.nokia.mid.impl.isa.pki.NetworkCertificate;
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
import javax.microedition.pki.CertificateException;

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
   private String responseMsg;
   private Hashtable reqProperties = new Hashtable();
   private Hashtable headerFields = new Hashtable();
   private String[] headerFieldNames;
   private String[] headerFieldValues;
   private String method = "GET";
   private int mode;
   private boolean exceptionPendingFlag = false;
   private boolean hc_not_connected = true;
   private boolean in_closed = true;
   private boolean out_closed = true;
   private boolean connected = false;
   private boolean hc_close_has_been_called = false;
   private Protocol.PrivateInputStream in;
   private Protocol.PrivateOutputStream out;
   private DataInputStream appDataIn;
   private DataOutputStream appDataOut;
   private StreamConnection streamConnection;
   private DataOutputStream streamOutput;
   private DataInputStream streamInput;
   private StringBuffer stringbuffer = new StringBuffer(32);
   private String http_proxy = System.getProperty("microedition.http_proxy");
   private String http_version = "HTTP/1.1";
   private static Timer timerService;
   private static MIDletAccess timerDatabase;
   private static int http_active = 0;
   private static final long CALL_SHUTDOWN_DELAY = 10000L;

   public Protocol() {
      this.exceptionPendingFlag = false;
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
      HttpUrl var4 = new HttpUrl(this.protocolType, var1);
      if (var4.host == null) {
         throw new IllegalArgumentException("missing host in URL");
      } else {
         this.parseURL();
         this.hc_not_connected = false;
      }
   }

   public void close() throws IOException {
      if (!this.hc_not_connected) {
         this.hc_not_connected = true;
         this.hc_close_has_been_called = true;
         if ((this.in_closed || this.in == null) && (this.out_closed || this.out == null)) {
            this.closeConnection();
         }

      }
   }

   public InputStream openInputStream() throws IOException {
      if (this.hc_not_connected) {
         throw new IOException("connection is closed");
      } else if (this.in != null) {
         throw new IOException("already open");
      } else if (this.mode != 1 && this.mode != 3) {
         throw new IOException("write-only connection");
      } else {
         this.connect();
         this.in = new Protocol.PrivateInputStream();
         this.in_closed = false;
         ++this.iStreams;
         return this.in;
      }
   }

   public DataInputStream openDataInputStream() throws IOException {
      if (this.appDataIn != null) {
         throw new IOException("already open");
      } else {
         if (this.in == null) {
            this.openInputStream();
         }

         this.appDataIn = new DataInputStream(this.in);
         return this.appDataIn;
      }
   }

   public Object prepareCMSourceId() throws IOException {
      if (this.hc_not_connected) {
         throw new IOException("connection is closed");
      } else if (this.in != null) {
         throw new IOException("already open");
      } else if (this.mode != 1 && this.mode != 3) {
         throw new IOException("write-only connection");
      } else if (System.getProperty("ENABLE_HTTP_WIRE") != null) {
         throw new IOException("streaming over the bridge isn't supported");
      } else {
         synchronized(SharedObjects.getLock("com.sun.midp.io.j2me.http.Protocol.connectLock")) {
            this.connect_wap(true);
         }

         return this.streamConnection;
      }
   }

   public OutputStream openOutputStream() throws IOException {
      return this.openDataOutputStream();
   }

   public DataOutputStream openDataOutputStream() throws IOException {
      if (this.mode != 2 && this.mode != 3) {
         throw new IOException("read-only connection");
      } else if (this.hc_not_connected) {
         throw new IOException("connection is closed");
      } else if (this.out != null) {
         throw new IOException("already open");
      } else {
         this.out = new Protocol.PrivateOutputStream();
         this.out_closed = false;
         ++this.oStreams;
         return new DataOutputStream(this.out);
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
      if (this.hc_not_connected) {
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
      return (String)this.reqProperties.get(var1);
   }

   public void setRequestProperty(String var1, String var2) throws IOException {
      if (this.out_closed && this.in_closed && this.hc_not_connected) {
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
               this.setRequestPropertyInternal(var1, var2);
            }
         } else {
            throw new IllegalArgumentException("HTTP Property too large");
         }
      }
   }

   private void setRequestPropertyInternal(String var1, String var2) throws IOException {
      if (this.connected) {
         throw new IOException("connection already open");
      } else {
         this.reqProperties.put(var1, var2);
      }
   }

   public int getResponseCode() throws IOException {
      if (this.hc_not_connected) {
         throw new IOException("connection is closed");
      } else {
         this.connect();
         return this.responseCode;
      }
   }

   public String getResponseMessage() throws IOException {
      if (this.hc_not_connected) {
         throw new IOException("connection is closed");
      } else {
         this.connect();
         return this.responseMsg;
      }
   }

   public long getLength() {
      if (this.hc_not_connected) {
         return -1L;
      } else {
         try {
            return (long)this.getHeaderFieldInt("content-length", -1);
         } catch (IOException var2) {
            return -1L;
         }
      }
   }

   public String getType() {
      if (this.hc_not_connected) {
         return null;
      } else {
         try {
            return this.getHeaderField("content-type");
         } catch (IOException var2) {
            return null;
         }
      }
   }

   public String getEncoding() {
      if (this.hc_not_connected) {
         return null;
      } else {
         try {
            return this.getHeaderField("content-encoding");
         } catch (IOException var2) {
            return null;
         }
      }
   }

   public long getExpiration() throws IOException {
      if (this.hc_not_connected) {
         throw new IOException("connection is closed");
      } else {
         return this.getHeaderFieldDate("expires", 0L);
      }
   }

   public long getDate() throws IOException {
      if (this.hc_not_connected) {
         throw new IOException("connection is closed");
      } else {
         return this.getHeaderFieldDate("date", 0L);
      }
   }

   public long getLastModified() throws IOException {
      if (this.hc_not_connected) {
         throw new IOException("connection is closed");
      } else {
         return this.getHeaderFieldDate("last-modified", 0L);
      }
   }

   public String getHeaderField(String var1) throws IOException {
      if (this.hc_not_connected) {
         throw new IOException("connection is closed");
      } else {
         this.connect();
         return var1 != null ? (String)this.headerFields.get(this.toLowerCase(var1)) : null;
      }
   }

   public String getHeaderField(int var1) throws IOException {
      if (this.hc_not_connected) {
         throw new IOException("connection is closed");
      } else {
         this.connect();
         if (this.headerFieldValues == null) {
            this.makeHeaderFieldValues();
         }

         return var1 < this.headerFieldValues.length && var1 >= 0 ? this.headerFieldValues[var1] : null;
      }
   }

   public String getHeaderFieldKey(int var1) throws IOException {
      if (this.hc_not_connected) {
         throw new IOException("connection is closed");
      } else {
         this.connect();
         if (this.headerFieldNames == null) {
            this.makeHeaderFields();
         }

         return var1 < this.headerFieldNames.length && var1 >= 0 ? this.headerFieldNames[var1] : null;
      }
   }

   private void makeHeaderFields() {
      int var1 = 0;
      this.headerFieldNames = new String[this.headerFields.size()];

      for(Enumeration var2 = this.headerFields.keys(); var2.hasMoreElements(); this.headerFieldNames[var1++] = (String)var2.nextElement()) {
      }

   }

   private void makeHeaderFieldValues() {
      int var1 = 0;
      this.headerFieldValues = new String[this.headerFields.size()];

      for(Enumeration var2 = this.headerFields.keys(); var2.hasMoreElements(); this.headerFieldValues[var1++] = (String)this.headerFields.get(var2.nextElement())) {
      }

   }

   public int getHeaderFieldInt(String var1, int var2) throws IOException {
      String var3 = this.getHeaderField(var1);

      try {
         return Integer.parseInt(var3);
      } catch (Throwable var5) {
         return var2;
      }
   }

   public long getHeaderFieldDate(String var1, long var2) throws IOException {
      String var4 = this.getHeaderField(var1);

      try {
         return DateParser.parse(var4);
      } catch (Throwable var6) {
         return var2;
      }
   }

   protected void connect() throws IOException {
      if (this.hc_close_has_been_called && this.out_closed) {
         throw new IOException("connection is not open");
      } else if (!this.connected) {
         if (System.getProperty("ENABLE_HTTP_WIRE") == null) {
            synchronized(SharedObjects.getLock("com.sun.midp.io.j2me.http.Protocol.connectLock")) {
               this.connect_wap(false);
            }
         } else {
            this.connect_wire();
         }

      }
   }

   private void connect_wap(boolean var1) throws IOException {
      if (this.out != null && this.out.size() > 0 && !this.method.equals("POST")) {
         throw new IOException("GET and HEAD requests can't include an entity body");
      } else {
         this.streamConnection = (StreamConnection)InternalConnector.openInternal("wap://" + this.protocolType + "://" + this.host + (this.port == -1 ? "" : ":" + this.port) + (this.getFile() == null ? "/" : this.getFile()) + (this.getRef() == null ? "" : "#" + this.getRef()) + (this.getQuery() == null ? "" : "?" + this.getQuery()), 3, false);
         this.streamOutput = this.streamConnection.openDataOutputStream();
         if (this.getRequestProperty("Content-Length") == null) {
            this.setRequestPropertyInternal("Content-Length", "" + (this.out == null ? 0 : this.out.size()));
         }

         if (this.getRequestProperty("Accept") == null) {
            this.setRequestPropertyInternal("Accept", "*/*");
         }

         this.setRequestMethod0(this.method);
         Enumeration var2 = this.reqProperties.keys();
         String var3;
         if (this.method.equals("POST")) {
            var3 = this.getRequestProperty("Content-Type");
            if (var3 != null) {
               this.setRequestProperty0("Content-Type", var3);
            } else {
               this.setRequestProperty0("Content-Type", "text/plain");
            }
         }

         while(var2.hasMoreElements()) {
            var3 = (String)var2.nextElement();
            if (!var3.equals("Content-Type")) {
               this.setRequestProperty0(var3, (String)this.reqProperties.get(var3));
            }
         }

         this.checkIfUntrusted();
         if (this.out != null) {
            this.streamOutput.write(this.out.toByteArray());
         }

         this.streamOutput.flush();
         this.setConnected();
         if (var1) {
            ((com.nokia.mid.impl.isa.io.protocol.internal.wap.Protocol)this.streamConnection).prepareCMSourceId();
         } else {
            this.streamInput = this.streamConnection.openDataInputStream();
         }

         this.readResponseMessage_wap();
         this.readHeaders_wap();
      }
   }

   private void connect_wire() throws IOException {
      if (this.http_proxy == null) {
         this.streamConnection = (StreamConnection)InternalConnector.openInternal("tck://" + this.host + ":" + this.getPort(), 3, false);
      } else {
         this.streamConnection = (StreamConnection)InternalConnector.openInternal("tck://" + this.http_proxy, 3, false);
      }

      this.streamOutput = this.streamConnection.openDataOutputStream();
      if (this.getRequestProperty("Content-Length") == null) {
         this.setRequestPropertyInternal("Content-Length", "" + (this.out == null ? 0 : this.out.size()));
      }

      String var1;
      if (this.http_proxy == null) {
         var1 = this.method + " " + (this.getFile() == null ? "/" : this.getFile()) + (this.getRef() == null ? "" : "#" + this.getRef()) + (this.getQuery() == null ? "" : "?" + this.getQuery()) + " " + this.http_version + "\r\n";
      } else {
         var1 = this.method + " " + "http://" + this.host + ":" + this.getPort() + (this.getFile() == null ? "/" : this.getFile()) + (this.getRef() == null ? "" : "#" + this.getRef()) + (this.getQuery() == null ? "" : "?" + this.getQuery()) + " " + this.http_version + "\r\n";
      }

      this.setRequestPropertyInternal("Host", this.host + ":" + this.getPort());

      String var3;
      for(Enumeration var2 = this.reqProperties.keys(); var2.hasMoreElements(); var1 = var1 + var3 + ": " + this.reqProperties.get(var3) + "\r\n") {
         var3 = (String)var2.nextElement();
      }

      this.checkIfUntrusted();
      var1 = var1 + "\r\n";
      this.streamOutput.write(var1.getBytes());
      if (this.out != null) {
         this.streamOutput.write(this.out.toByteArray());
      }

      this.streamOutput.flush();
      this.streamInput = this.streamConnection.openDataInputStream();
      this.readResponseMessage_wire(this.streamInput);
      this.readHeaders_wire(this.streamInput);
      this.setConnected();
   }

   private void readResponseMessage_wap() throws IOException {
      this.responseMsg = null;

      while(this.responseMsg == null) {
         this.responseMsg = this.getResponseMessage0();
         this.checkForException();
      }

      this.responseCode = this.getResponseCode0();
   }

   private void readResponseMessage_wire(InputStream var1) throws IOException {
      String var2 = this.readLine(var1);
      this.responseCode = -1;
      this.responseMsg = null;
      if (var2 != null) {
         int var3 = var2.indexOf(32);
         if (var3 >= 0) {
            String var5 = var2.substring(0, var3);
            if (var5.startsWith("HTTP") && var2.length() > var3) {
               int var4 = var2.substring(var3 + 1).indexOf(32);
               if (var4 >= 0) {
                  var4 += var3 + 1;
                  if (var2.length() > var4) {
                     try {
                        this.responseCode = Integer.parseInt(var2.substring(var3 + 1, var4));
                     } catch (NumberFormatException var7) {
                        throw new IOException("malformed response message");
                     }

                     this.responseMsg = var2.substring(var4 + 1);
                     return;
                  }
               }
            }
         }
      }

      throw new IOException("malformed response message");
   }

   private void readHeaders_wap() throws IOException {
      for(String var1 = this.getFirstHeaderEntry0(); var1 != null && !var1.equals(""); var1 = this.getNextHeaderEntry0()) {
         int var4 = var1.indexOf(58);
         if (var4 < 0) {
            throw new IOException("malformed header field");
         }

         String var2 = var1.substring(0, var4);
         if (var2.length() == 0) {
            throw new IOException("malformed header field");
         }

         String var3;
         if (var1.length() <= var4 + 2) {
            var3 = "";
         } else {
            var3 = var1.substring(var4 + 2).trim();
         }

         this.headerFields.put(this.toLowerCase(var2), var3);
      }

   }

   private void readHeaders_wire(InputStream var1) throws IOException {
      while(true) {
         String var2 = this.readLine(var1);
         if (var2 == null || var2.equals("")) {
            return;
         }

         int var5 = var2.indexOf(58);
         if (var5 < 0) {
            throw new IOException("malformed header field");
         }

         String var3 = var2.substring(0, var5);
         if (var3.length() == 0) {
            throw new IOException("malformed header field");
         }

         String var4;
         if (var2.length() <= var5 + 2) {
            var4 = "";
         } else {
            var4 = var2.substring(var5 + 2).trim();
         }

         this.headerFields.put(this.toLowerCase(var3), var4);
      }
   }

   private String readLine(InputStream var1) {
      this.stringbuffer.setLength(0);

      while(true) {
         while(true) {
            int var2;
            try {
               var2 = var1.read();
               if (var2 < 0) {
                  return null;
               }

               if (var2 == 13) {
                  continue;
               }
            } catch (IOException var4) {
               return null;
            }

            if (var2 == 10) {
               return this.stringbuffer.toString();
            }

            this.stringbuffer.append((char)var2);
         }
      }
   }

   protected void closeConnection() throws IOException {
      if (this.streamOutput != null) {
         this.streamOutput.close();
         this.streamOutput = null;
      }

      if (this.streamInput != null) {
         this.streamInput.close();
         this.streamInput = null;
      }

      if (this.streamConnection != null) {
         this.streamConnection.close();
         this.streamConnection = null;
      }

      this.responseCode = -1;
      this.responseMsg = null;
      this.resetConnected();
   }

   protected String parseProtocol() throws IOException {
      int var1 = this.url.indexOf(58);
      if (var1 <= 0) {
         throw new IOException("malformed URL");
      } else {
         String var2 = this.url.substring(0, var1);
         if (!var2.equals("http")) {
            throw new IOException("protocol must be 'http'");
         } else {
            this.index = var1 + 1;
            return var2;
         }
      }
   }

   private String parseHostname() throws IOException {
      String var1 = this.url.substring(this.index);
      if (var1.startsWith("//")) {
         var1 = var1.substring(2);
         this.index += 2;
      }

      int var3 = var1.indexOf("[");
      int var4 = var1.indexOf("]");
      int var2;
      if (var3 != -1 && var4 != -1 && var3 < var4) {
         var2 = var1.indexOf(":", var4);
      } else {
         var2 = var1.indexOf(58);
      }

      if (var2 < 0) {
         var2 = var1.indexOf(47);
      }

      if (var2 < 0) {
         var2 = var1.length();
      }

      String var5 = var1.substring(0, var2);
      this.index += var2;
      return var5;
   }

   private int parsePort() throws IOException, IllegalArgumentException {
      byte var1 = -1;
      String var2 = this.url.substring(this.index);
      if (!var2.startsWith(":")) {
         return var1;
      } else {
         var2 = var2.substring(1);
         ++this.index;
         int var3 = var2.indexOf(47);
         if (var3 < 0) {
            var3 = var2.length();
         }

         int var6;
         try {
            var6 = Integer.parseInt(var2.substring(0, var3));
            if (var6 <= 0) {
               throw new NumberFormatException();
            }
         } catch (NumberFormatException var5) {
            throw new IllegalArgumentException("invalid port");
         }

         this.index += var3;
         return var6;
      }
   }

   private String parseFile() throws IOException, IllegalArgumentException {
      String var1 = "";
      String var2 = this.url.substring(this.index);
      if (var2.length() == 0) {
         return var1;
      } else if (!var2.startsWith("/")) {
         throw new IllegalArgumentException("invalid path");
      } else {
         int var3 = var2.indexOf(35);
         int var4 = var2.indexOf(63);
         if (var3 < 0 && var4 < 0) {
            var3 = var2.length();
         } else if (var3 < 0 || var4 > 0 && var4 < var3) {
            var3 = var4;
         }

         var1 = var2.substring(0, var3);
         this.index += var3;
         return var1;
      }
   }

   private String parseRef() throws IOException, IllegalArgumentException {
      String var1 = this.url.substring(this.index);
      if (var1.length() != 0 && var1.charAt(0) != '?') {
         if (!var1.startsWith("#")) {
            throw new IllegalArgumentException("invalid ref");
         } else {
            int var2 = var1.indexOf(63);
            if (var2 < 0) {
               var2 = var1.length();
            }

            this.index += var2;
            return var1.substring(1, var2);
         }
      } else {
         return "";
      }
   }

   private String parseQuery() throws IOException {
      String var1 = this.url.substring(this.index);
      if (var1.length() == 0) {
         return "";
      } else if (var1.startsWith("?")) {
         String var2 = var1.substring(1);
         int var3 = var1.indexOf(35);
         if (var3 > 0) {
            var2 = var1.substring(1, var3);
            this.index += var3;
         }

         return var2;
      } else {
         return "";
      }
   }

   protected synchronized void parseURL() throws IOException, IllegalArgumentException {
      this.index = 0;
      this.host = this.parseHostname();
      this.port = this.parsePort();
      this.file = this.parseFile();
      this.query = this.parseQuery();
      this.ref = this.parseRef();
   }

   private String toLowerCase(String var1) {
      this.stringbuffer.setLength(0);

      for(int var2 = 0; var2 < var1.length(); ++var2) {
         this.stringbuffer.append(Character.toLowerCase(var1.charAt(var2)));
      }

      return this.stringbuffer.toString();
   }

   public static void s_setTimerDatabase(MIDletAccess var0) {
      if (timerDatabase == null) {
         timerDatabase = var0;
      } else {
         throw new SecurityException();
      }
   }

   private void setConnected() {
      this.connected = true;
      this.stopTimer();
      ++http_active;
   }

   private void resetConnected() {
      this.connected = false;
      --http_active;
      if (http_active == 0) {
         this.startTimer();
      }

   }

   private void startTimer() {
      timerService = new Timer();
      timerDatabase.registerTimer((MIDlet)null, timerService);
      Protocol.TimerClient var1 = new Protocol.TimerClient();
      timerService.schedule(var1, 10000L);
   }

   private void stopTimer() {
      if (timerService != null) {
         timerService.cancel();
         timerDatabase.deregisterTimer((MIDlet)null, timerService);
         timerService = null;
      }

   }

   private void checkForException() throws IOException {
      if (this.exceptionPendingFlag) {
         int var1 = this.getBadCertificateID0();
         this.exceptionPendingFlag = false;
         if (var1 == 0) {
            throw new IOException("Error in HTTP operation");
         } else {
            byte var2 = 14;
            throw new CertificateException(new NetworkCertificate(var1), var2);
         }
      }
   }

   private native void setRequestMethod0(String var1) throws IOException;

   private native void setRequestProperty0(String var1, String var2) throws IOException;

   private native String getFirstHeaderEntry0() throws IOException;

   private native String getNextHeaderEntry0() throws IOException;

   private native int getResponseCode0() throws IOException;

   private native String getResponseMessage0() throws IOException;

   private native void loaderstop0();

   private native void checkIfUntrusted();

   private native int getBadCertificateID0();

   private class TimerClient extends TimerTask {
      private TimerClient() {
      }

      public final void run() {
         if ("pd".equals(System.getProperty("com.nokia.network.access"))) {
            Protocol.this.stopTimer();
         } else {
            Protocol.this.loaderstop0();
            Protocol.this.stopTimer();
         }
      }

      // $FF: synthetic method
      TimerClient(Object var2) {
         this();
      }
   }

   class PrivateOutputStream extends OutputStream {
      private ByteArrayOutputStream output = new ByteArrayOutputStream();

      public PrivateOutputStream() {
      }

      private void checkConnection() throws IOException {
         if (Protocol.this.out_closed) {
            throw new IOException("connection is not open");
         }
      }

      public void write(int var1) throws IOException {
         this.checkConnection();
         this.output.write(var1);
      }

      public void flush() throws IOException {
         this.checkConnection();
         if (this.output.size() > 0) {
            Protocol.this.connect();
         }

      }

      byte[] toByteArray() {
         return this.output.toByteArray();
      }

      int size() {
         return this.output.size();
      }

      public void close() throws IOException {
         if (!Protocol.this.out_closed) {
            Protocol.this.connect();
            this.flush();
            Protocol.this.out_closed = true;
            Protocol.this.oStreams--;
            if (Protocol.this.hc_not_connected && (Protocol.this.in_closed || Protocol.this.in == null) && Protocol.this.connected) {
               Protocol.this.closeConnection();
            }

         }
      }
   }

   class PrivateInputStream extends InputStream {
      int bytesleft = 0;
      boolean chunked = false;
      boolean eof = false;

      PrivateInputStream() throws IOException {
         String var2 = (String)Protocol.this.headerFields.get("transfer-encoding");
         if (var2 != null && var2.equals("chunked")) {
            this.chunked = true;
            this.bytesleft = this.readChunkSize();
            this.eof = this.bytesleft == 0;
         }

      }

      private void checkConnection() throws IOException {
         if (Protocol.this.in_closed) {
            throw new IOException("connection is not open");
         } else if (!Protocol.this.connected) {
            throw new IOException("connection is not open");
         }
      }

      public int available() throws IOException {
         this.checkConnection();
         return this.chunked ? this.bytesleft : Protocol.this.streamInput.available();
      }

      public int read() throws IOException {
         this.checkConnection();
         if (this.eof) {
            return -1;
         } else {
            if (this.bytesleft <= 0 && this.chunked) {
               this.readCRLF();
               this.bytesleft = this.readChunkSize();
               if (this.bytesleft == 0) {
                  this.eof = true;
                  return -1;
               }
            }

            int var1 = Protocol.this.streamInput.read();
            this.eof = var1 == -1;
            if (this.bytesleft > 0) {
               --this.bytesleft;
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
               if (this.bytesleft <= 0 && this.chunked) {
                  this.readCRLF();
                  this.bytesleft = this.readChunkSize();
                  if (this.bytesleft == 0) {
                     this.eof = true;
                     return -1;
                  }
               }

               if (this.chunked && var3 > this.bytesleft) {
                  var3 = this.bytesleft;
               }

               int var4 = Protocol.this.streamInput.read(var1, var2, var3);
               this.bytesleft -= var3;
               if (this.bytesleft < 0) {
                  this.bytesleft = 0;
               }

               return var4;
            }
         } else {
            throw new IndexOutOfBoundsException("Illegal length or offset");
         }
      }

      private int readChunkSize() throws IOException {
         boolean var1 = true;

         try {
            String var2 = Protocol.this.readLine(Protocol.this.streamInput);
            if (var2 == null) {
               throw new IOException("No Chunk Size");
            } else {
               int var3;
               for(var3 = 0; var3 < var2.length(); ++var3) {
                  char var4 = var2.charAt(var3);
                  if (Character.digit(var4, 16) == -1) {
                     break;
                  }
               }

               int var6 = Integer.parseInt(var2.substring(0, var3), 16);
               return var6;
            }
         } catch (NumberFormatException var5) {
            throw new IOException("Bogus chunk size");
         }
      }

      private void readCRLF() throws IOException {
         int var1 = Protocol.this.streamInput.read();
         if (var1 != 13) {
            throw new IOException("missing CRLF");
         } else {
            var1 = Protocol.this.streamInput.read();
            if (var1 != 10) {
               throw new IOException("missing CRLF");
            }
         }
      }

      public void close() throws IOException {
         if (!Protocol.this.in_closed) {
            Protocol.this.in_closed = true;
            Protocol.this.iStreams--;
            if (Protocol.this.hc_not_connected && (Protocol.this.out_closed || Protocol.this.out == null) && Protocol.this.connected) {
               Protocol.this.closeConnection();
            }

         }
      }
   }
}
