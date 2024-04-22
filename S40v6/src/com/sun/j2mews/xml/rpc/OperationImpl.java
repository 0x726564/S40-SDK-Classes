package com.sun.j2mews.xml.rpc;

import com.nokia.mid.impl.isa.util.Base64;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.MarshalException;
import java.rmi.ServerException;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.xml.rpc.Element;
import javax.microedition.xml.rpc.FaultDetailException;
import javax.microedition.xml.rpc.FaultDetailHandler;
import javax.microedition.xml.rpc.Operation;
import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;

public class OperationImpl extends Operation {
   private static String[] cookies;
   private static int cookieIndex;
   private String[] properties;
   private int propertyIndex;
   private SOAPEncoder encoder;
   private SOAPDecoder decoder;
   private QName name;
   private Element inputType;
   private Element returnType;
   private FaultDetailHandler faultHandler;

   public OperationImpl(QName name, Element input, Element output) throws IllegalArgumentException {
      this.name = name;
      this.inputType = input;
      this.returnType = output;
      this.encoder = new SOAPEncoder();
      this.decoder = new SOAPDecoder();
   }

   public OperationImpl(QName name, Element input, Element output, FaultDetailHandler faultDetailHandler) throws IllegalArgumentException {
      this.name = name;
      this.inputType = input;
      this.returnType = output;
      this.faultHandler = faultDetailHandler;
      this.encoder = new SOAPEncoder();
      this.decoder = new SOAPDecoder();
   }

   public void setProperty(String name, String value) throws IllegalArgumentException {
      if (name != null && value != null) {
         if (!name.equals("javax.xml.rpc.service.endpoint.address") && !name.equals("javax.xml.rpc.security.auth.password") && !name.equals("javax.xml.rpc.security.auth.username") && !name.equals("javax.xml.rpc.session.maintain") && !name.equals("javax.xml.rpc.soap.http.soapaction.uri")) {
            throw new IllegalArgumentException();
         } else {
            if (this.properties != null) {
               for(int i = 0; i < this.propertyIndex; i += 2) {
                  if (this.properties[i].equals(name)) {
                     this.properties[i + 1] = value;
                     return;
                  }
               }
            }

            if (this.properties == null) {
               this.properties = new String[10];
            } else if (this.propertyIndex == this.properties.length) {
               String[] newProps = new String[this.properties.length + 10];
               System.arraycopy(this.properties, 0, newProps, 0, this.properties.length);
               this.properties = null;
               this.properties = newProps;
            }

            this.properties[this.propertyIndex++] = name;
            this.properties[this.propertyIndex++] = value;
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public Object invoke(Object params) throws JAXRPCException {
      HttpConnection http = null;
      OutputStream ostream = null;
      InputStream istream = null;

      try {
         http = (HttpConnection)Connector.open(this.getProperty("javax.xml.rpc.service.endpoint.address"));
         ostream = this.setupReqStream(http);
         this.encoder.encode(params, this.inputType, ostream, (String)null);
         if (ostream != null) {
            ostream.close();
         }

         istream = this.setupResStream(http);
         Object result = null;
         if (this.returnType != null) {
            result = this.decoder.decode(this.returnType, istream, http.getEncoding(), http.getLength());
         }

         if (http != null) {
            http.close();
         }

         if (istream != null) {
            istream.close();
         }

         return result;
      } catch (Throwable var10) {
         if (ostream != null) {
            try {
               ostream.close();
            } catch (Throwable var9) {
            }
         }

         if (istream != null) {
            try {
               istream.close();
            } catch (Throwable var8) {
            }
         }

         if (http != null) {
            try {
               http.close();
            } catch (Throwable var7) {
            }
         }

         if (var10 instanceof JAXRPCException) {
            throw (JAXRPCException)var10;
         } else if (!(var10 instanceof MarshalException) && !(var10 instanceof ServerException) && !(var10 instanceof FaultDetailException)) {
            throw new JAXRPCException(var10.toString());
         } else {
            throw new JAXRPCException(var10);
         }
      }
   }

   protected OutputStream setupReqStream(HttpConnection http) throws IOException {
      http.setRequestMethod("POST");
      http.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
      String soapAction = this.getProperty("javax.xml.rpc.soap.http.soapaction.uri");
      if (soapAction == null) {
         soapAction = "\"\"";
      }

      if (!soapAction.startsWith("\"")) {
         soapAction = "\"" + soapAction;
      }

      if (!soapAction.endsWith("\"")) {
         soapAction = soapAction + "\"";
      }

      http.setRequestProperty("SOAPAction", soapAction);
      String useSession = this.getProperty("javax.xml.rpc.session.maintain");
      String s1;
      if (useSession != null && useSession.toLowerCase().equals("true")) {
         s1 = getSessionCookie(this.getProperty("javax.xml.rpc.service.endpoint.address"));
         if (s1 != null) {
            http.setRequestProperty("Cookie", s1);
         }
      }

      s1 = this.getProperty("javax.xml.rpc.security.auth.username");
      String s2 = this.getProperty("javax.xml.rpc.security.auth.password");
      if (s1 != null && s2 != null) {
         byte[] encodeData = (s1 + ":" + s2).getBytes();
         http.setRequestProperty("Authorization", "Basic " + Base64.encode(encodeData, 0, encodeData.length));
      }

      return http.openOutputStream();
   }

   protected InputStream setupResStream(HttpConnection http) throws IOException, ServerException {
      InputStream input = http.openInputStream();
      int response = http.getResponseCode();
      if (response == 200) {
         String useSession = this.getProperty("javax.xml.rpc.session.maintain");
         if (useSession != null && useSession.toLowerCase().equals("true")) {
            String cookie = http.getHeaderField("Set-Cookie");
            if (cookie != null) {
               addSessionCookie(this.getProperty("javax.xml.rpc.service.endpoint.address"), cookie);
            }
         }

         return input;
      } else {
         Object detail = this.decoder.decodeFault(this.faultHandler, input, http.getEncoding(), http.getLength());
         if (detail instanceof String) {
            if (((String)detail).indexOf("DataEncodingUnknown") != -1) {
               throw new MarshalException((String)detail);
            } else {
               throw new ServerException((String)detail);
            }
         } else {
            Object[] wrapper = (Object[])detail;
            String message = (String)wrapper[0];
            QName name = (QName)wrapper[1];
            detail = wrapper[2];
            throw new JAXRPCException(message, new FaultDetailException(name, detail));
         }
      }
   }

   private String getProperty(String key) {
      if (this.properties != null) {
         for(int i = 0; i < this.properties.length - 2; i += 2) {
            if (this.properties[i] == null) {
               return null;
            }

            if (this.properties[i].equals(key)) {
               return this.properties[i + 1];
            }
         }
      }

      return null;
   }

   private static synchronized void addSessionCookie(String endpoint, String cookie) {
      if (endpoint != null && cookie != null) {
         int i = cookie.indexOf(";");
         if (i > 0) {
            cookie = cookie.substring(0, i);
         }

         if (cookies != null) {
            for(i = 0; i < cookieIndex; i += 2) {
               if (cookies[i].equals(endpoint)) {
                  cookies[i + 1] = cookie;
                  return;
               }
            }
         }

         if (cookies == null) {
            cookies = new String[10];
         } else if (cookieIndex == cookies.length) {
            String[] newCookies = new String[cookies.length + 10];
            System.arraycopy(cookies, 0, newCookies, 0, cookies.length);
            cookies = null;
            cookies = newCookies;
         }

         cookies[cookieIndex++] = endpoint;
         cookies[cookieIndex++] = cookie;
      }
   }

   private static synchronized String getSessionCookie(String endpoint) {
      if (cookies != null) {
         for(int i = 0; i < cookies.length - 2; i += 2) {
            if (cookies[i] == null) {
               return null;
            }

            if (cookies[i].equals(endpoint)) {
               return cookies[i + 1];
            }
         }
      }

      return null;
   }
}
