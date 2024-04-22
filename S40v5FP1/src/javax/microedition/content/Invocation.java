package javax.microedition.content;

import com.nokia.mid.impl.isa.content.InvocationImpl;
import com.nokia.mid.impl.isa.content.Util;
import java.io.IOException;
import javax.microedition.io.Connection;

public final class Invocation {
   public static final int ACTIVE = 2;
   public static final int CANCELLED = 6;
   public static final int ERROR = 7;
   public static final int HOLD = 4;
   public static final int INIT = 1;
   public static final int INITIATED = 8;
   public static final int OK = 5;
   public static final int WAITING = 3;
   private InvocationImpl impl;

   public Invocation() {
      this((String)null, (String)null, (String)null, true, (String)null);
   }

   public Invocation(String url) {
      this(url, (String)null, (String)null, true, (String)null);
   }

   public Invocation(String url, String type) {
      this(url, type, (String)null, true, (String)null);
   }

   public Invocation(String url, String type, String ID) {
      this(url, type, ID, true, (String)null);
   }

   public Invocation(String url, String type, String ID, boolean responseRequired, String action) {
      this.impl = new InvocationImpl(this, url, type, ID, responseRequired, action);
   }

   public String findType() throws IOException, ContentHandlerException, SecurityException {
      return this.impl.findType();
   }

   public String getAction() {
      return this.impl.getAction();
   }

   public String[] getArgs() {
      return this.impl.getArgs();
   }

   public byte[] getData() {
      return this.impl.getData();
   }

   public String getID() {
      return this.impl.getID();
   }

   public String getInvokingAppName() {
      return this.impl.getInvokingAppName();
   }

   public String getInvokingAuthority() {
      return this.impl.getInvokingAuthority();
   }

   public String getInvokingID() {
      return this.impl.getInvokingID();
   }

   public Invocation getPrevious() {
      InvocationImpl prev = this.impl.getPrevious();
      return prev != null ? prev.getParent() : null;
   }

   public boolean getResponseRequired() {
      return this.impl.getResponseRequired();
   }

   public int getStatus() {
      return this.impl.getStatus();
   }

   public String getType() {
      return this.impl.getType();
   }

   public String getURL() {
      return this.impl.getURL();
   }

   public Connection open(boolean timeouts) throws IOException, SecurityException {
      return this.impl.open(timeouts);
   }

   public void setAction(String action) {
      this.impl.setAction(action);
   }

   public void setArgs(String[] args) {
      this.impl.setArgs(args);
   }

   public void setCredentials(String username, char[] password) {
      this.impl.setCredentials(username, password);
   }

   public void setData(byte[] data) {
      this.impl.setData(data);
   }

   public void setID(String sID) {
      this.impl.setID(sID);
   }

   public void setResponseRequired(boolean responseRequired) {
      this.impl.setResponseRequired(responseRequired);
   }

   public void setType(String type) {
      this.impl.setType(type);
   }

   public void setURL(String url) {
      this.impl.setURL(url);
   }

   InvocationImpl getImpl() {
      return this.impl;
   }

   static {
      Util.setInvocationAccessor(new InvocationAccessorImpl());
   }
}
