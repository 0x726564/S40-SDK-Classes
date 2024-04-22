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
   private static String[] ir;
   private static int it;
   private String[] iu;
   private int iv;
   private SOAPEncoder iw;
   private SOAPDecoder ix;
   private Element iy;
   private Element iz;
   private FaultDetailHandler iA;

   public OperationImpl(QName var1, Element var2, Element var3) throws IllegalArgumentException {
      this.iy = var2;
      this.iz = var3;
      this.iw = new SOAPEncoder();
      this.ix = new SOAPDecoder();
   }

   public OperationImpl(QName var1, Element var2, Element var3, FaultDetailHandler var4) throws IllegalArgumentException {
      this.iy = var2;
      this.iz = var3;
      this.iA = var4;
      this.iw = new SOAPEncoder();
      this.ix = new SOAPDecoder();
   }

   public void setProperty(String var1, String var2) throws IllegalArgumentException {
      if (var1 != null && var2 != null) {
         if (!var1.equals("javax.xml.rpc.service.endpoint.address") && !var1.equals("javax.xml.rpc.security.auth.password") && !var1.equals("javax.xml.rpc.security.auth.username") && !var1.equals("javax.xml.rpc.session.maintain") && !var1.equals("javax.xml.rpc.soap.http.soapaction.uri")) {
            throw new IllegalArgumentException();
         } else {
            if (this.iu != null) {
               for(int var3 = 0; var3 < this.iv; var3 += 2) {
                  if (this.iu[var3].equals(var1)) {
                     this.iu[var3 + 1] = var2;
                     return;
                  }
               }
            }

            if (this.iu == null) {
               this.iu = new String[10];
            } else if (this.iv == this.iu.length) {
               String[] var4 = new String[this.iu.length + 10];
               System.arraycopy(this.iu, 0, var4, 0, this.iu.length);
               this.iu = null;
               this.iu = var4;
            }

            this.iu[this.iv++] = var1;
            this.iu[this.iv++] = var2;
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public Object invoke(Object var1) throws JAXRPCException {
      HttpConnection var2 = null;
      OutputStream var3 = null;
      InputStream var4 = null;

      try {
         var2 = (HttpConnection)Connector.open(this.getProperty("javax.xml.rpc.service.endpoint.address"));
         var3 = this.setupReqStream(var2);
         this.iw.encode(var1, this.iy, var3, (String)null);
         if (var3 != null) {
            var3.close();
         }

         var4 = this.setupResStream(var2);
         var1 = null;
         if (this.iz != null) {
            var1 = this.ix.decode(this.iz, var4, var2.getEncoding(), var2.getLength());
         }

         if (var2 != null) {
            var2.close();
         }

         if (var4 != null) {
            var4.close();
         }

         return var1;
      } catch (Throwable var8) {
         if (var3 != null) {
            try {
               var3.close();
            } catch (Throwable var7) {
            }
         }

         if (var4 != null) {
            try {
               var4.close();
            } catch (Throwable var6) {
            }
         }

         if (var2 != null) {
            try {
               var2.close();
            } catch (Throwable var5) {
            }
         }

         if (var8 instanceof JAXRPCException) {
            throw (JAXRPCException)var8;
         } else if (!(var8 instanceof MarshalException) && !(var8 instanceof ServerException) && !(var8 instanceof FaultDetailException)) {
            throw new JAXRPCException(var8.toString());
         } else {
            throw new JAXRPCException(var8);
         }
      }
   }

   protected OutputStream setupReqStream(HttpConnection var1) throws IOException {
      var1.setRequestMethod("POST");
      var1.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
      String var2;
      if ((var2 = this.getProperty("javax.xml.rpc.soap.http.soapaction.uri")) == null) {
         var2 = "\"\"";
      }

      if (!var2.startsWith("\"")) {
         var2 = "\"" + var2;
      }

      if (!var2.endsWith("\"")) {
         var2 = var2 + "\"";
      }

      var1.setRequestProperty("SOAPAction", var2);
      if ((var2 = this.getProperty("javax.xml.rpc.session.maintain")) != null && var2.toLowerCase().equals("true") && (var2 = N(this.getProperty("javax.xml.rpc.service.endpoint.address"))) != null) {
         var1.setRequestProperty("Cookie", var2);
      }

      var2 = this.getProperty("javax.xml.rpc.security.auth.username");
      String var3 = this.getProperty("javax.xml.rpc.security.auth.password");
      if (var2 != null && var3 != null) {
         byte[] var4 = (var2 + ":" + var3).getBytes();
         var1.setRequestProperty("Authorization", "Basic " + Base64.encode(var4, 0, var4.length));
      }

      return var1.openOutputStream();
   }

   protected InputStream setupResStream(HttpConnection var1) throws IOException, ServerException {
      InputStream var2 = var1.openInputStream();
      if (var1.getResponseCode() == 200) {
         String var7;
         if ((var7 = this.getProperty("javax.xml.rpc.session.maintain")) != null && var7.toLowerCase().equals("true") && (var7 = var1.getHeaderField("Set-Cookie")) != null) {
            e(this.getProperty("javax.xml.rpc.service.endpoint.address"), var7);
         }

         return var2;
      } else {
         Object var3;
         if ((var3 = this.ix.decodeFault(this.iA, var2, var1.getEncoding(), var1.getLength())) instanceof String) {
            if (((String)var3).indexOf("DataEncodingUnknown") != -1) {
               throw new MarshalException((String)var3);
            } else {
               throw new ServerException((String)var3);
            }
         } else {
            Object[] var6;
            String var4 = (String)(var6 = (Object[])var3)[0];
            QName var5 = (QName)var6[1];
            var3 = var6[2];
            throw new JAXRPCException(var4, new FaultDetailException(var5, var3));
         }
      }
   }

   private String getProperty(String var1) {
      if (this.iu != null) {
         for(int var2 = 0; var2 < this.iu.length - 2; var2 += 2) {
            if (this.iu[var2] == null) {
               return null;
            }

            if (this.iu[var2].equals(var1)) {
               return this.iu[var2 + 1];
            }
         }
      }

      return null;
   }

   private static synchronized void e(String var0, String var1) {
      if (var0 != null && var1 != null) {
         int var2;
         if ((var2 = var1.indexOf(";")) > 0) {
            var1 = var1.substring(0, var2);
         }

         if (ir != null) {
            for(var2 = 0; var2 < it; var2 += 2) {
               if (ir[var2].equals(var0)) {
                  ir[var2 + 1] = var1;
                  return;
               }
            }
         }

         if (ir == null) {
            ir = new String[10];
         } else if (it == ir.length) {
            String[] var3 = new String[ir.length + 10];
            System.arraycopy(ir, 0, var3, 0, ir.length);
            ir = null;
            ir = var3;
         }

         ir[it++] = var0;
         ir[it++] = var1;
      }
   }

   private static synchronized String N(String var0) {
      if (ir != null) {
         for(int var1 = 0; var1 < ir.length - 2; var1 += 2) {
            if (ir[var1] == null) {
               return null;
            }

            if (ir[var1].equals(var0)) {
               return ir[var1 + 1];
            }
         }
      }

      return null;
   }
}
