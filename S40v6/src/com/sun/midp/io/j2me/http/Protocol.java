package com.sun.midp.io.j2me.http;

import com.nokia.mid.impl.isa.pki.NetworkCertificate;
import com.nokia.mid.impl.isa.ui.MIDletAccess;
import com.nokia.mid.impl.isa.ui.MIDletState;
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
   private int exceptionPendingReason = 0;
   private boolean hc_not_connected = true;
   private boolean in_closed = true;
   private boolean out_closed = true;
   private boolean connected = false;
   private boolean hc_close_has_been_called = false;
   private Protocol.PrivateInputStream in;
   private Protocol.PrivateOutputStream out;
   private DataInputStream appDataIn;
   private StreamConnection streamConnection;
   private DataOutputStream streamOutput;
   private DataInputStream streamInput;
   private static Timer timerService;
   private static MIDletAccess timerDatabase;
   private static int http_active = 0;
   private static final long CALL_SHUTDOWN_DELAY = 10000L;
   private IOException caughtIOException = null;

   public Protocol() {
      this.exceptionPendingFlag = false;
      this.exceptionPendingReason = 0;
      this.protocol = "javax.microedition.io.Connector.http";
   }

   public int readBytes(byte[] b, int off, int len) throws IOException {
      throw new RuntimeException("Function not supported");
   }

   public int writeBytes(byte[] b, int off, int len) throws IOException {
      throw new RuntimeException("Function not supported");
   }

   public void disconnect() throws IOException {
      throw new RuntimeException("Function not supported");
   }

   public void connect(String url, int mode, boolean timeouts) throws IOException {
      this.url = url;
      this.mode = mode;
      HttpUrl testURL = new HttpUrl(this.protocolType, url);
      if (testURL.host == null) {
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
         if (this.out != null) {
            boolean chunkTransferMode = ((com.nokia.mid.impl.isa.io.protocol.internal.wap.Protocol)this.streamConnection).getChunkTransferMode();
            if (!chunkTransferMode) {
               this.out.close();
               this.out = null;
            }
         }

         this.readResponseMessage_wap();
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

   public void setRequestMethod(String method) throws IOException {
      if (this.hc_not_connected) {
         throw new IOException("connection is closed");
      } else if (this.connected) {
         throw new IOException("connection already open");
      } else if (!method.equals("HEAD") && !method.equals("GET") && !method.equals("POST")) {
         throw new IOException("unsupported method: " + method);
      } else if (this.out_closed) {
         this.method = new String(method);
      }
   }

   public String getRequestProperty(String key) {
      return (String)this.reqProperties.get(key.toLowerCase());
   }

   public void setRequestProperty(String key, String value) throws IOException {
      if (this.out_closed && this.in_closed && this.hc_not_connected) {
         throw new IOException("connection is closed");
      } else if (this.connected) {
         throw new IOException("connection already open");
      } else if (this.out_closed) {
         if (key.length() <= 39 && value.length() <= 30000) {
            char[] disallowedChars = new char[]{'\r', '\n', '\u0000'};

            for(int i = 0; i < disallowedChars.length; ++i) {
               if (key.indexOf(disallowedChars[i]) != -1 || this.requestPropertyValueCheck(value) == 0) {
                  throw new IllegalArgumentException("HTTP Property contains an unacceptable character");
               }
            }

            if (key.indexOf(58) != -1) {
               throw new IllegalArgumentException("HTTP Property contains an unacceptable character");
            } else {
               this.setRequestPropertyInternal(key, value);
            }
         } else {
            throw new IllegalArgumentException("HTTP Property too large");
         }
      }
   }

   private int requestPropertyValueCheck(String value) {
      int leng = value.length();
      char[] str = value.toCharArray();

      for(int i = 0; i < leng - 1; ++i) {
         if (str[i] == '\r') {
            if (str[i + 1] != '\n') {
               return 0;
            }
         } else if (str[i] == '\n' && str[i + 1] != '\r' && str[i + 1] != '\t' && str[i + 1] != ' ') {
            return 0;
         }
      }

      return 1;
   }

   private void setRequestPropertyInternal(String key, String value) throws IOException {
      if (this.connected) {
         throw new IOException("connection already open");
      } else {
         this.reqProperties.put(key.toLowerCase(), value);
      }
   }

   public int getResponseCode() throws IOException {
      if (this.hc_not_connected) {
         throw new IOException("connection is closed");
      } else {
         this.connect();
         if (this.out != null) {
            this.out.close();
            this.out = null;
         }

         this.readResponseMessage_wap();
         return this.responseCode;
      }
   }

   public String getResponseMessage() throws IOException {
      if (this.hc_not_connected) {
         throw new IOException("connection is closed");
      } else {
         this.connect();
         if (this.out != null) {
            this.out.close();
            this.out = null;
         }

         this.readResponseMessage_wap();
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
            this.caughtIOException = var2;
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
            this.caughtIOException = var2;
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
            this.caughtIOException = var2;
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

   public String getHeaderField(String name) throws IOException {
      if (this.hc_not_connected) {
         throw new IOException("connection is closed");
      } else {
         this.connect();
         if (this.out != null) {
            this.out.close();
            this.out = null;
         }

         this.readResponseMessage_wap();
         return name != null ? (String)this.headerFields.get(name.toLowerCase()) : null;
      }
   }

   public String getHeaderField(int index) throws IOException {
      if (this.hc_not_connected) {
         throw new IOException("connection is closed");
      } else {
         this.connect();
         if (this.out != null) {
            this.out.close();
            this.out = null;
         }

         this.readResponseMessage_wap();
         if (this.headerFieldValues == null) {
            this.makeHeaderFieldValues();
         }

         return index < this.headerFieldValues.length && index >= 0 ? this.headerFieldValues[index] : null;
      }
   }

   public String getHeaderFieldKey(int index) throws IOException {
      if (this.hc_not_connected) {
         throw new IOException("connection is closed");
      } else {
         this.connect();
         if (this.out != null) {
            this.out.close();
            this.out = null;
         }

         this.readResponseMessage_wap();
         if (this.headerFieldNames == null) {
            this.makeHeaderFields();
         }

         return index < this.headerFieldNames.length && index >= 0 ? this.headerFieldNames[index] : null;
      }
   }

   private void makeHeaderFields() {
      int i = 0;
      this.headerFieldNames = new String[this.headerFields.size()];

      for(Enumeration e = this.headerFields.keys(); e.hasMoreElements(); this.headerFieldNames[i++] = (String)e.nextElement()) {
      }

   }

   private void makeHeaderFieldValues() {
      int i = 0;
      this.headerFieldValues = new String[this.headerFields.size()];

      for(Enumeration e = this.headerFields.keys(); e.hasMoreElements(); this.headerFieldValues[i++] = (String)this.headerFields.get(e.nextElement())) {
      }

   }

   public int getHeaderFieldInt(String name, int def) throws IOException {
      String field = this.getHeaderField(name);

      try {
         return Integer.parseInt(field);
      } catch (Throwable var5) {
         return def;
      }
   }

   public long getHeaderFieldDate(String name, long def) throws IOException {
      String field = this.getHeaderField(name);

      try {
         return DateParser.parse(field);
      } catch (Throwable var6) {
         return def;
      }
   }

   protected void connect() throws IOException {
      if (this.caughtIOException != null) {
         throw this.caughtIOException;
      } else if (this.hc_close_has_been_called && this.out_closed) {
         throw new IOException("connection is not open");
      } else if (!this.connected) {
         if (System.getProperty("ENABLE_HTTP_WIRE") != null) {
            throw new IOException("TCPIP Bridge not supported");
         } else {
            synchronized(SharedObjects.getLock("com.sun.midp.io.j2me.http.Protocol.connectLock")) {
               if (this.hc_close_has_been_called && this.out_closed) {
                  throw new IOException("connection is not open");
               } else if (!this.connected) {
                  this.connect_wap(false);
               }
            }
         }
      }
   }

   private void connect_wap(boolean useCMSource) throws IOException {
      if (this.out != null && this.out.size() > 0 && !this.method.equals("POST")) {
         throw new IOException("GET and HEAD requests can't include an entity body");
      } else {
         this.streamConnection = (StreamConnection)InternalConnector.openInternal("wap://" + this.protocolType + "://" + this.host + (this.port == -1 ? "" : ":" + this.port) + (this.getFile() == null ? "/" : this.getFile()) + (this.getRef() == null ? "" : "#" + this.getRef()) + (this.getQuery() == null ? "" : "?" + this.getQuery()), 3, false);
         this.streamOutput = this.streamConnection.openDataOutputStream();
         String chunkTransferModeName = this.getRequestProperty("Transfer-Encoding");
         if ((chunkTransferModeName == null || !chunkTransferModeName.equals("chunked")) && this.getRequestProperty("Content-Length") == null) {
            this.setRequestPropertyInternal("Content-Length", "" + (this.out == null ? 0 : this.out.size()));
         }

         if (this.getRequestProperty("Accept") == null) {
            this.setRequestPropertyInternal("Accept", "*/*");
         }

         this.setRequestMethod0(this.method);
         String user_agent;
         if (this.method.equals("POST")) {
            user_agent = this.getRequestProperty("content-type");
            if (user_agent != null) {
               this.setRequestProperty0("Content-Type", user_agent);
            } else {
               this.setRequestProperty0("Content-Type", "text/plain");
            }
         }

         user_agent = (String)this.reqProperties.get("user-agent");
         if (this.checkIfUntrusted()) {
            if (user_agent == null) {
               this.reqProperties.put("user-agent", " UNTRUSTED/1.0");
            } else {
               this.reqProperties.put("user-agent", user_agent + " UNTRUSTED/1.0");
            }
         }

         Enumeration reqKeys = this.reqProperties.keys();

         while(reqKeys.hasMoreElements()) {
            String key = (String)reqKeys.nextElement();
            if (!key.equals("content-type")) {
               this.setRequestProperty0(key, (String)this.reqProperties.get(key));
            }
         }

         this.setConnected();
         if (chunkTransferModeName != null && chunkTransferModeName.equals("chunked")) {
            ((com.nokia.mid.impl.isa.io.protocol.internal.wap.Protocol)this.streamConnection).setChunkTransferMode(true);
         } else {
            ((com.nokia.mid.impl.isa.io.protocol.internal.wap.Protocol)this.streamConnection).setChunkTransferMode(false);
            if (this.out != null && this.streamOutput != null) {
               this.streamOutput.write(this.out.toByteArray());
            }

            this.streamOutput.flush();
         }

         if (useCMSource) {
            ((com.nokia.mid.impl.isa.io.protocol.internal.wap.Protocol)this.streamConnection).prepareCMSourceId();
         } else {
            this.streamInput = this.streamConnection.openDataInputStream();
         }

         if (chunkTransferModeName == null || !chunkTransferModeName.equals("chunked")) {
            this.readResponseMessage_wap();
         }

      }
   }

   private void readResponseMessage_wap() throws IOException {
      synchronized(SharedObjects.getLock("com.sun.midp.io.j2me.http.Protocol.connectLock")) {
         if (this.responseMsg == null) {
            while(this.responseMsg == null) {
               this.responseMsg = this.getResponseMessage0();
            }

            this.checkForException();
            this.responseCode = this.getResponseCode0();
            this.readHeaders_wap();
         }
      }
   }

   protected void protectedReadResponseMessage_wap() throws IOException {
      if (this.out != null) {
         this.out.close();
         this.out = null;
      }

      this.readResponseMessage_wap();
   }

   private void readHeaders_wap() throws IOException {
      for(String line = this.getFirstHeaderEntry0(); line != null && !line.equals(""); line = this.getNextHeaderEntry0()) {
         int index = line.indexOf(58);
         if (index < 0) {
            throw new IOException("malformed header field");
         }

         String key = line.substring(0, index);
         if (key.length() == 0) {
            throw new IOException("malformed header field");
         }

         String value;
         if (line.length() <= index + 2) {
            value = "";
         } else {
            value = line.substring(index + 2).trim();
         }

         this.headerFields.put(key.toLowerCase(), value);
      }

   }

   private String readLine(InputStream in) {
      StringBuffer stringbuffer = new StringBuffer();

      while(true) {
         while(true) {
            int c;
            try {
               c = in.read();
               if (c < 0) {
                  return null;
               }

               if (c == 13) {
                  continue;
               }
            } catch (IOException var5) {
               return null;
            }

            if (c == 10) {
               return stringbuffer.toString();
            }

            stringbuffer.append((char)c);
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
      int n = this.url.indexOf(58);
      if (n <= 0) {
         throw new IOException("malformed URL");
      } else {
         String token = this.url.substring(0, n);
         if (!token.equals("http")) {
            throw new IOException("protocol must be 'http'");
         } else {
            this.index = n + 1;
            return token;
         }
      }
   }

   private String parseHostname() throws IOException {
      String buf = this.url.substring(this.index);
      if (buf.startsWith("//")) {
         buf = buf.substring(2);
         this.index += 2;
      }

      int idxOpenBracket = buf.indexOf("[");
      int idxCloseBracket = buf.indexOf("]");
      int n;
      if (idxOpenBracket != -1 && idxCloseBracket != -1 && idxOpenBracket < idxCloseBracket) {
         n = buf.indexOf(":", idxCloseBracket);
      } else {
         n = buf.indexOf(58);
      }

      if (n < 0) {
         n = buf.indexOf(47);
      }

      if (n < 0) {
         n = buf.length();
      }

      String token = buf.substring(0, n);
      this.index += n;
      return token;
   }

   private int parsePort() throws IOException, IllegalArgumentException {
      int p = -1;
      String buf = this.url.substring(this.index);
      if (!buf.startsWith(":")) {
         return p;
      } else {
         buf = buf.substring(1);
         ++this.index;
         int n = buf.indexOf(47);
         if (n < 0) {
            n = buf.length();
         }

         int p;
         try {
            p = Integer.parseInt(buf.substring(0, n));
            if (p <= 0) {
               throw new NumberFormatException();
            }
         } catch (NumberFormatException var5) {
            throw new IllegalArgumentException("invalid port");
         }

         this.index += n;
         return p;
      }
   }

   private String parseFile() throws IOException, IllegalArgumentException {
      String token = "";
      String buf = this.url.substring(this.index);
      if (buf.length() == 0) {
         return token;
      } else if (!buf.startsWith("/")) {
         throw new IllegalArgumentException("invalid path");
      } else {
         int n = buf.indexOf(35);
         int m = buf.indexOf(63);
         if (n < 0 && m < 0) {
            n = buf.length();
         } else if (n < 0 || m > 0 && m < n) {
            n = m;
         }

         token = buf.substring(0, n);
         this.index += n;
         return token;
      }
   }

   private String parseRef() throws IOException, IllegalArgumentException {
      String buf = this.url.substring(this.index);
      if (buf.length() != 0 && buf.charAt(0) != '?') {
         if (!buf.startsWith("#")) {
            throw new IllegalArgumentException("invalid ref");
         } else {
            int n = buf.indexOf(63);
            if (n < 0) {
               n = buf.length();
            }

            this.index += n;
            return buf.substring(1, n);
         }
      } else {
         return "";
      }
   }

   private String parseQuery() throws IOException {
      String buf = this.url.substring(this.index);
      if (buf.length() == 0) {
         return "";
      } else if (buf.startsWith("?")) {
         String token = buf.substring(1);
         int n = buf.indexOf(35);
         if (n > 0) {
            token = buf.substring(1, n);
            this.index += n;
         }

         return token;
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

   public static void s_setTimerDatabase(MIDletAccess tdb) {
      if (timerDatabase == null) {
         timerDatabase = tdb;
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
      Protocol.TimerClient _timerClient = new Protocol.TimerClient();
      timerService.schedule(_timerClient, 10000L);
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
         int certId = this.getBadCertificateID0();
         this.exceptionPendingFlag = false;
         if (certId == 0) {
            throw new IOException(this.exceptionPendingReason + "-Error in HTTP operation");
         } else {
            byte errorReason = 14;
            throw new CertificateException(new NetworkCertificate(certId), errorReason);
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

   private native boolean checkIfUntrusted();

   private native int getBadCertificateID0();

   private class TimerClient extends TimerTask {
      private TimerClient() {
      }

      public final void run() {
         String closeContext = MIDletState.getStaticAppProperty("Nokia-MIDlet-Close-Gprs-Context");
         if ("pd".equals(System.getProperty("com.nokia.network.access")) && !"true".equals(closeContext)) {
            Protocol.this.stopTimer();
         } else {
            Protocol.this.loaderstop0();
            Protocol.this.stopTimer();
         }
      }

      // $FF: synthetic method
      TimerClient(Object x1) {
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

      public void write(int b) throws IOException {
         this.checkConnection();
         this.output.write(b);
      }

      public void flush() throws IOException {
         this.checkConnection();
         if (this.output != null && this.output.size() > 0) {
            if (!Protocol.this.connected) {
               Protocol.this.connect();
            }

            if (!Protocol.this.method.equals("POST")) {
               throw new IOException("GET and HEAD requests can't include an entity body");
            } else {
               boolean chunkTransferMode = ((com.nokia.mid.impl.isa.io.protocol.internal.wap.Protocol)Protocol.this.streamConnection).getChunkTransferMode();
               if (Protocol.this.streamOutput != null && chunkTransferMode) {
                  Protocol.this.streamOutput.write(this.output.toByteArray());
                  this.output = new ByteArrayOutputStream();
               }

            }
         }
      }

      byte[] toByteArray() {
         byte[] result = this.output.toByteArray();
         this.output = new ByteArrayOutputStream();
         return result;
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
            if (Protocol.this.streamOutput != null) {
               Protocol.this.streamOutput.close();
               Protocol.this.streamOutput = null;
            }

            Protocol.this.readResponseMessage_wap();
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
         String te = (String)Protocol.this.headerFields.get("transfer-encoding");
         if (te != null && te.equals("chunked")) {
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

            int ch = Protocol.this.streamInput.read();
            this.eof = ch == -1;
            if (this.bytesleft > 0) {
               --this.bytesleft;
            }

            return ch;
         }
      }

      public int read(byte[] b, int off, int len) throws IOException {
         this.checkConnection();
         if (b == null) {
            throw new NullPointerException("Buffer is null");
         } else if (len >= 0 && off >= 0 && (off <= 0 || off != b.length) && len + off <= b.length) {
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

               if (this.chunked && len > this.bytesleft) {
                  len = this.bytesleft;
               }

               int ret = Protocol.this.streamInput.read(b, off, len);
               this.bytesleft -= len;
               if (this.bytesleft < 0) {
                  this.bytesleft = 0;
               }

               return ret;
            }
         } else {
            throw new IndexOutOfBoundsException("Illegal length or offset");
         }
      }

      private int readChunkSize() throws IOException {
         boolean var1 = true;

         try {
            String chunk = Protocol.this.readLine(Protocol.this.streamInput);
            if (chunk == null) {
               throw new IOException("No Chunk Size");
            } else {
               int i;
               for(i = 0; i < chunk.length(); ++i) {
                  char ch = chunk.charAt(i);
                  if (Character.digit(ch, 16) == -1) {
                     break;
                  }
               }

               int size = Integer.parseInt(chunk.substring(0, i), 16);
               return size;
            }
         } catch (NumberFormatException var5) {
            throw new IOException("Bogus chunk size");
         }
      }

      private void readCRLF() throws IOException {
         int ch = Protocol.this.streamInput.read();
         if (ch != 13) {
            throw new IOException("missing CRLF");
         } else {
            ch = Protocol.this.streamInput.read();
            if (ch != 10) {
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
