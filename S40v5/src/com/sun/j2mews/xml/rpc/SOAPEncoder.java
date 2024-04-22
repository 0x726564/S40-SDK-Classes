package com.sun.j2mews.xml.rpc;

import java.io.OutputStream;
import javax.microedition.xml.rpc.ComplexType;
import javax.microedition.xml.rpc.Element;
import javax.microedition.xml.rpc.Type;
import javax.xml.rpc.JAXRPCException;

public class SOAPEncoder {
   private StringBuffer hg;
   private String defaultNS;
   private String gy;

   public synchronized void encode(Object var1, Type var2, OutputStream var3, String var4) throws JAXRPCException {
      this.hg = new StringBuffer();
      this.gy = null;
      this.hg.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
      this.hg.append("<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n\txmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n\txmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"");

      try {
         if (var2 != null) {
            Element var5 = (Element)var2;
            this.defaultNS = var5.name.getNamespaceURI();
            this.hg.append("\n\txmlns:tns=\"" + this.defaultNS + "\">\n");
            this.hg.append("<soap:Body>\n");
            this.a((Element)var2, var1);
         } else {
            this.hg.append(">\n<soap:Body/>\n");
         }
      } catch (RuntimeException var7) {
      } catch (Throwable var8) {
         throw new JAXRPCException("Could not encode request");
      }

      if (this.gy != null) {
         throw new JAXRPCException(this.gy);
      } else {
         if (var2 != null) {
            this.hg.append("</soap:Body>\n");
         }

         this.hg.append("</soap:Envelope>\n");

         try {
            if (var4 != null) {
               var3.write(this.hg.toString().getBytes(var4));
            } else {
               var3.write(this.hg.toString().getBytes());
            }
         } catch (Exception var6) {
            this.hg = null;
            throw new JAXRPCException(var6.getMessage());
         }

         this.hg = null;
      }
   }

   private void a(Element var1, Object var2) {
      String var3;
      if (var1.name.getNamespaceURI().equals(this.defaultNS)) {
         var3 = "tns:" + var1.name.getLocalPart();
      } else {
         var3 = var1.name.getLocalPart() + " xmlns=\"" + var1.name.getNamespaceURI() + "\"";
      }

      if (var2 == null) {
         if (var1.isNillable && !var1.isArray) {
            this.hg.append("<" + var3 + " xsi:nil=\"true\"/>\n");
         } else if (!var1.isOptional) {
            this.gy = "Null value for non-nillable/optional element: " + var1.name.getLocalPart();
            throw new RuntimeException();
         }
      } else {
         int var4 = 1;
         Object[] var5 = new Object[]{var2};
         SOAPEncoder var13;
         boolean var10000;
         if (var1.isArray) {
            if (!(var2 instanceof Object[])) {
               switch(var1.contentType.value) {
               case 0:
                  var10000 = var2 instanceof boolean[];
                  break;
               case 1:
                  var10000 = var2 instanceof byte[];
                  break;
               case 2:
                  var10000 = var2 instanceof short[];
                  break;
               case 3:
                  var10000 = var2 instanceof int[];
                  break;
               case 4:
                  var10000 = var2 instanceof long[];
                  break;
               case 5:
                  var10000 = var2 instanceof float[];
                  break;
               case 6:
                  var10000 = var2 instanceof double[];
                  break;
               default:
                  var10000 = false;
               }

               if (!var10000) {
                  this.gy = "Type mismatch: elements of an array must be an array.";
                  throw new RuntimeException();
               }

               String var15 = var3;
               var13 = this;
               int var21;
               switch(var1.contentType.value) {
               case 0:
                  if (var2 instanceof boolean[]) {
                     boolean[] var26 = (boolean[])var2;
                     this.a(var1, var26.length);

                     for(var21 = 0; var21 < var26.length; ++var21) {
                        var13.hg.append("<" + var15 + ">" + var26[var21] + "</" + var15 + ">\n");
                     }

                     return;
                  }
                  break;
               case 1:
                  if (var2 instanceof byte[]) {
                     byte[] var25 = (byte[])var2;
                     this.a(var1, var25.length);

                     for(var21 = 0; var21 < var25.length; ++var21) {
                        var13.hg.append("<" + var15 + ">" + var25[var21] + "</" + var15 + ">\n");
                     }

                     return;
                  }
                  break;
               case 2:
                  if (var2 instanceof short[]) {
                     short[] var24 = (short[])var2;
                     this.a(var1, var24.length);

                     for(var21 = 0; var21 < var24.length; ++var21) {
                        var13.hg.append("<" + var15 + ">" + var24[var21] + "</" + var15 + ">\n");
                     }

                     return;
                  }
                  break;
               case 3:
                  if (var2 instanceof int[]) {
                     int[] var23 = (int[])var2;
                     this.a(var1, var23.length);

                     for(var21 = 0; var21 < var23.length; ++var21) {
                        var13.hg.append("<" + var15 + ">" + var23[var21] + "</" + var15 + ">\n");
                     }

                     return;
                  }
                  break;
               case 4:
                  if (var2 instanceof long[]) {
                     long[] var22 = (long[])var2;
                     this.a(var1, var22.length);

                     for(var21 = 0; var21 < var22.length; ++var21) {
                        var13.hg.append("<" + var15 + ">" + var22[var21] + "</" + var15 + ">\n");
                     }

                     return;
                  }
                  break;
               case 5:
                  if (var2 instanceof float[]) {
                     float[] var20 = (float[])var2;
                     this.a(var1, var20.length);

                     for(var21 = 0; var21 < var20.length; ++var21) {
                        var13.hg.append("<" + var15 + ">" + var20[var21] + "</" + var15 + ">\n");
                     }

                     return;
                  }
                  break;
               case 6:
                  if (var2 instanceof double[]) {
                     double[] var19 = (double[])var2;
                     this.a(var1, var19.length);

                     for(var21 = 0; var21 < var19.length; ++var21) {
                        var13.hg.append("<" + var15 + ">" + var19[var21] + "</" + var15 + ">\n");
                     }

                     return;
                  }
               }

               this.gy = "Invalid values for primitive array for " + var1.name.getLocalPart();
               throw new RuntimeException();
            }

            var4 = ((Object[])var2).length;
            var5 = (Object[])var2;
            this.a(var1, var5.length);
         }

         for(int var12 = 0; var12 < var4; ++var12) {
            if (var5[var12] == null) {
               if (!var1.isNillable) {
                  this.gy = "Null value for non-nillable/optional element: " + var1.name.getLocalPart();
                  throw new RuntimeException();
               }

               this.hg.append("<" + var3 + " xsi:nil=\"true\"/>\n");
            } else {
               this.hg.append("<" + var3 + ">");
               if (var1.contentType.value < 8) {
                  Object var8 = var5[var12];
                  Type var7 = var1.contentType;
                  switch(var7.value) {
                  case 0:
                     var10000 = var8 instanceof Boolean;
                     break;
                  case 1:
                     var10000 = var8 instanceof Byte;
                     break;
                  case 2:
                     var10000 = var8 instanceof Short;
                     break;
                  case 3:
                     var10000 = var8 instanceof Integer;
                     break;
                  case 4:
                     var10000 = var8 instanceof Long;
                     break;
                  case 5:
                     var10000 = var8 instanceof Float;
                     break;
                  case 6:
                     var10000 = var8 instanceof Double;
                     break;
                  case 7:
                     var10000 = var8 instanceof String;
                     break;
                  default:
                     var10000 = false;
                  }

                  if (!var10000) {
                     this.gy = "Simple Type Mismatch";
                     throw new RuntimeException();
                  }

                  if (var7.value < 7) {
                     this.hg.append(var8.toString());
                  } else {
                     String var10 = var8.toString();
                     SOAPEncoder var9 = this;
                     char[] var11 = var10.toCharArray();

                     for(int var6 = 0; var6 < var11.length; ++var6) {
                        if (var11[var6] == '<') {
                           var9.hg.append("&lt;");
                        } else if (var11[var6] == '>') {
                           var9.hg.append("&gt;");
                        } else if (var11[var6] == '&') {
                           var9.hg.append("&amp;");
                        } else if (var11[var6] == '\'') {
                           var9.hg.append("@apos;");
                        } else if (var11[var6] == '"') {
                           var9.hg.append("&quot;");
                        } else {
                           var9.hg.append(var11[var6]);
                        }
                     }
                  }
               } else if (var1.contentType.value == 8) {
                  if (!(var5[var12] instanceof Object[])) {
                     this.gy = "Type mismatch: element of ComplexType must be an array.";
                     throw new RuntimeException();
                  }

                  this.hg.append("\n");
                  ComplexType var10001 = (ComplexType)var1.contentType;
                  Object[] var16 = (Object[])var5[var12];
                  ComplexType var14 = var10001;
                  var13 = this;
                  Element[] var17;
                  if ((var17 = var14.elements).length != var16.length) {
                     this.gy = "Wrong number of values passed for complex type";
                     throw new RuntimeException();
                  }

                  for(int var18 = 0; var18 < var17.length; ++var18) {
                     var13.a(var17[var18], var16[var18]);
                  }
               } else if (var1.contentType.value == 9) {
                  this.gy = "Encoding error - unable to encode indirected Elements of Elements";
                  throw new RuntimeException();
               }

               if (var1.name.getNamespaceURI().equals(this.defaultNS)) {
                  this.hg.append("</" + var3 + ">\n");
               } else {
                  this.hg.append("</" + var1.name.getLocalPart() + ">\n");
               }
            }
         }

      }
   }

   private void a(Element var1, int var2) {
      if (var2 < var1.minOccurs) {
         this.gy = "Not enough array elements for: " + var1.name.getLocalPart();
         throw new RuntimeException();
      } else if (var1.maxOccurs > 0 && var2 > var1.maxOccurs) {
         this.gy = "Too many array elements for: " + var1.name.getLocalPart();
         throw new RuntimeException();
      }
   }
}
