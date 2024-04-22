package com.sun.j2mews.xml.rpc;

import java.io.InputStream;
import java.rmi.MarshalException;
import java.util.Stack;
import javax.microedition.xml.rpc.ComplexType;
import javax.microedition.xml.rpc.Element;
import javax.microedition.xml.rpc.FaultDetailHandler;
import javax.microedition.xml.rpc.Type;
import javax.xml.namespace.QName;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.rpc.JAXRPCException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class SOAPDecoder extends DefaultHandler {
   private static final Object gj = new Object();
   private SAXParser gk;
   private StringBuffer gl;
   private int state;
   private int gm;
   private boolean gn;
   private boolean go;
   private String gp;
   private boolean gq;
   private ComplexType gr;
   private FaultDetailHandler gs;
   private QName gt;
   private Element gu;
   private Stack gv;
   private Stack gw;
   private Stack gx;
   private String gy;

   public SOAPDecoder() {
      try {
         SAXParserFactory var1;
         (var1 = SAXParserFactory.newInstance()).setNamespaceAware(true);
         var1.setValidating(false);
         this.gk = var1.newSAXParser();
      } catch (Exception var2) {
         throw new RuntimeException("Could not instantiate parser");
      }

      this.gl = new StringBuffer();
      this.gv = new Stack();
      this.gw = new Stack();
      this.gx = new Stack();
   }

   public synchronized Object decode(Type var1, InputStream var2, String var3, long var4) throws JAXRPCException {
      this.gs = null;
      this.gu = null;
      this.gq = false;
      this.gt = null;
      this.go = false;
      this.gm = 0;
      this.gn = false;
      this.Q();
      if (!(var1 instanceof Element)) {
         throw new JAXRPCException(new MarshalException("Type parameter not instanceof Element"));
      } else {
         this.gw.push(var1);

         try {
            this.gk.parse((InputStream)var2, this);
         } catch (RuntimeException var6) {
         } catch (SAXParseException var7) {
            this.gx.push(new Integer(4));
            this.gy = "SAXParseException in response from server";
         } catch (Throwable var8) {
            this.gx.push(new Integer(4));
            this.gy = "Unexpected Exception : " + var8.getMessage();
         }

         if (!this.gx.empty()) {
            this.state = (Integer)this.gx.pop();
         } else if (this.gm != 0) {
            this.state = 4;
            this.gy = "(1)Missing end tag for Body or Envelope";
         } else {
            this.state = 0;
         }

         if (this.state == 4) {
            throw new JAXRPCException(new MarshalException(this.gy));
         } else if (this.gv.isEmpty()) {
            Element var9;
            if (!(var9 = (Element)var1).isNillable && !var9.isOptional) {
               throw new JAXRPCException(new MarshalException("Missing return data in response from server"));
            } else {
               return null;
            }
         } else {
            return a(this.gv.pop());
         }
      }
   }

   public synchronized Object decodeFault(FaultDetailHandler var1, InputStream var2, String var3, long var4) throws JAXRPCException {
      this.gs = var1;
      this.gu = null;
      this.gq = true;
      this.gt = null;
      this.go = false;
      this.gm = 0;
      this.gn = false;
      this.Q();
      this.gr = new ComplexType();
      this.gr.elements = new Element[4];
      this.gr.elements[0] = new Element(new QName("", "faultcode"), Type.STRING);
      this.gr.elements[1] = new Element(new QName("", "faultstring"), Type.STRING);
      this.gr.elements[2] = new Element(new QName("", "faultactor"), Type.STRING, 0, 1, false);
      this.gr.elements[3] = new Element(new QName("", "detail"), Type.STRING, 0, 1, false);
      Element var9 = new Element(new QName("http://schemas.xmlsoap.org/soap/envelope/", "Fault"), this.gr);
      this.gw.push(var9);

      try {
         this.gk.parse((InputStream)var2, this);
      } catch (RuntimeException var6) {
      } catch (SAXParseException var7) {
         this.gx.push(new Integer(4));
         this.gy = "SAXParseException in response from server";
      } catch (Throwable var8) {
         var8.printStackTrace();
      }

      if (!this.gx.empty()) {
         this.state = (Integer)this.gx.pop();
      } else if (this.gm != 0) {
         this.state = 4;
         this.gy = "(2)Missing end tag for Body or Envelope";
      } else {
         this.state = 0;
      }

      if (this.state == 4) {
         throw new JAXRPCException(new MarshalException(this.gy));
      } else {
         Object[] var10 = (Object[])this.gv.pop();
         this.gl.delete(0, this.gl.length());
         if (var10[0] != null) {
            this.gl.append("\n[Code:   " + (String)var10[0] + "] ");
         }

         if (var10[1] != null) {
            this.gl.append("\n[String: " + (String)var10[1] + "] ");
         }

         if (var10[2] != null) {
            this.gl.append("\n[Actor:  " + (String)var10[2] + "] ");
         }

         if (this.gt == null) {
            if (var10[3] != null) {
               this.gl.append("\n[Detail: " + (String)var10[3] + "] ");
            }

            return this.gl.toString();
         } else {
            return new Object[]{this.gl.toString(), this.gt, a(var10[3])};
         }
      }
   }

   private void Q() {
      this.gx.removeAllElements();
      this.gw.removeAllElements();
      this.gv.removeAllElements();
   }

   private static Object a(ComplexType var0) throws JAXRPCException {
      try {
         Object[] var1 = new Object[var0.elements.length];

         for(int var2 = 0; var2 < var0.elements.length; ++var2) {
            if (var0.elements[var2].isArray) {
               var1[var2] = new TypedVector(var0.elements[var2].contentType.value, var0.elements[var2].isNillable);
            }
         }

         return var1;
      } catch (Throwable var3) {
         throw new JAXRPCException("Invalid Type for Output");
      }
   }

   private static boolean a(QName var0, String var1, String var2) {
      return var0.getNamespaceURI().equals(var1) && var0.getLocalPart().equals(var2);
   }

   private void a(Element var1, String var2, String var3) {
      if (!var3.equals(var1.name.getLocalPart())) {
         this.gy = "Invalid Element Name From Server: " + var3 + ", " + "expected: " + var1.name.getLocalPart();
         this.gx.push(new Integer(4));
         throw new RuntimeException();
      } else if (!var2.equals(var1.name.getNamespaceURI())) {
         this.gy = "Invalid Namespace URI From Server: " + var2 + ", " + "expected: " + var1.name.getNamespaceURI() + " for element: " + var3;
         this.gx.push(new Integer(4));
         throw new RuntimeException();
      } else if (this.go && !var1.isNillable) {
         this.gy = "Nillable mismatch from server for: " + var3;
         this.gx.push(new Integer(4));
         throw new RuntimeException();
      }
   }

   private static Object a(Object var0) {
      if (var0 instanceof Object[]) {
         Object[] var2 = (Object[])var0;

         for(int var3 = 0; var3 < var2.length; ++var3) {
            if (!(var2[var3] instanceof TypedVector)) {
               if (var2[var3] == gj) {
                  var2[var3] = null;
               } else if (var2[var3] instanceof Object[]) {
                  var2[var3] = a(var2[var3]);
               }
            } else {
               Object var1;
               TypedVector var4;
               int var5;
               int var7;
               switch((var4 = (TypedVector)var2[var3]).type) {
               case 0:
                  if (var4.kr) {
                     Boolean[] var22 = new Boolean[var4.size()];

                     for(var5 = 0; var5 < var22.length; ++var5) {
                        if ((var1 = var4.elementAt(var5)) != gj) {
                           var22[var5] = (Boolean)var1;
                        }
                     }

                     var2[var3] = var22;
                  } else {
                     boolean[] var21 = new boolean[var4.size()];

                     for(var7 = 0; var7 < var21.length; ++var7) {
                        var21[var7] = (Boolean)var4.elementAt(var7);
                     }

                     var2[var3] = var21;
                  }
                  break;
               case 1:
                  if (var4.kr) {
                     Byte[] var20 = new Byte[var4.size()];

                     for(var5 = 0; var5 < var20.length; ++var5) {
                        if ((var1 = var4.elementAt(var5)) != gj) {
                           var20[var5] = (Byte)var1;
                        }
                     }

                     var2[var3] = var20;
                     break;
                  }

                  byte[] var19 = new byte[var4.size()];

                  for(var7 = 0; var7 < var19.length; ++var7) {
                     var19[var7] = (Byte)var4.elementAt(var7);
                  }

                  var2[var3] = var19;
                  break;
               case 2:
                  if (var4.kr) {
                     Short[] var18 = new Short[var4.size()];

                     for(var5 = 0; var5 < var18.length; ++var5) {
                        if ((var1 = var4.elementAt(var5)) != gj) {
                           var18[var5] = (Short)var1;
                        }
                     }

                     var2[var3] = var18;
                     break;
                  }

                  short[] var17 = new short[var4.size()];

                  for(var7 = 0; var7 < var17.length; ++var7) {
                     var17[var7] = (Short)var4.elementAt(var7);
                  }

                  var2[var3] = var17;
                  break;
               case 3:
                  if (var4.kr) {
                     Integer[] var16 = new Integer[var4.size()];

                     for(var5 = 0; var5 < var16.length; ++var5) {
                        if ((var1 = var4.elementAt(var5)) != gj) {
                           var16[var5] = (Integer)var1;
                        }
                     }

                     var2[var3] = var16;
                     break;
                  }

                  int[] var15 = new int[var4.size()];

                  for(var7 = 0; var7 < var15.length; ++var7) {
                     var15[var7] = (Integer)var4.elementAt(var7);
                  }

                  var2[var3] = var15;
                  break;
               case 4:
                  if (var4.kr) {
                     Long[] var13 = new Long[var4.size()];

                     for(var5 = 0; var5 < var13.length; ++var5) {
                        if ((var1 = var4.elementAt(var5)) != gj) {
                           var13[var5] = (Long)var1;
                        }
                     }

                     var2[var3] = var13;
                     break;
                  }

                  long[] var12 = new long[var4.size()];

                  for(var7 = 0; var7 < var12.length; ++var7) {
                     var12[var7] = (Long)var4.elementAt(var7);
                  }

                  var2[var3] = var12;
                  break;
               case 5:
                  if (var4.kr) {
                     Float[] var11 = new Float[var4.size()];

                     for(var5 = 0; var5 < var11.length; ++var5) {
                        if ((var1 = var4.elementAt(var5)) != gj) {
                           var11[var5] = (Float)var1;
                        }
                     }

                     var2[var3] = var11;
                     break;
                  }

                  float[] var10 = new float[var4.size()];

                  for(var7 = 0; var7 < var10.length; ++var7) {
                     var10[var7] = (Float)var4.elementAt(var7);
                  }

                  var2[var3] = var10;
                  break;
               case 6:
                  if (var4.kr) {
                     Double[] var9 = new Double[var4.size()];

                     for(var5 = 0; var5 < var9.length; ++var5) {
                        if ((var1 = var4.elementAt(var5)) != gj) {
                           var9[var5] = (Double)var1;
                        }
                     }

                     var2[var3] = var9;
                     break;
                  }

                  double[] var8 = new double[var4.size()];

                  for(var7 = 0; var7 < var8.length; ++var7) {
                     var8[var7] = (Double)var4.elementAt(var7);
                  }

                  var2[var3] = var8;
                  break;
               case 7:
                  String[] var6 = new String[var4.size()];

                  for(var5 = 0; var5 < var6.length; ++var5) {
                     if ((var1 = var4.elementAt(var5)) != gj) {
                        var6[var5] = (String)var1;
                     }
                  }

                  var2[var3] = var6;
                  break;
               default:
                  Object[] var14 = new Object[var4.size()];
                  var4.copyInto(var14);
                  var2[var3] = a((Object)var14);
               }
            }
         }
      }

      return var0;
   }

   private Object a(Type var1, String var2) {
      if (var2 == null) {
         return null;
      } else {
         switch(var1.value) {
         case 0:
            if (!(var2 = var2.toLowerCase()).equals("true") && !var2.equals("1")) {
               if (!var2.equals("false") && !var2.equals("0")) {
                  this.gx.push(new Integer(4));
                  this.gy = "Expected Boolean, received: " + var2;
                  throw new RuntimeException();
               }

               return new Boolean(false);
            }

            return new Boolean(true);
         case 1:
            try {
               if (var2.startsWith("+")) {
                  return new Byte(Byte.parseByte(var2.substring(1)));
               }

               return new Byte(Byte.parseByte(var2));
            } catch (NumberFormatException var8) {
               this.gx.push(new Integer(4));
               this.gy = "Expected Byte, received: " + var2;
               throw new RuntimeException();
            }
         case 2:
            try {
               if (var2.startsWith("+")) {
                  return new Short(Short.parseShort(var2.substring(1)));
               }

               return new Short(Short.parseShort(var2));
            } catch (NumberFormatException var7) {
               this.gx.push(new Integer(4));
               this.gy = "Expected Short, received: " + var2;
               throw new RuntimeException();
            }
         case 3:
            try {
               if (var2.startsWith("+")) {
                  return new Integer(Integer.parseInt(var2.substring(1)));
               }

               return new Integer(Integer.parseInt(var2));
            } catch (NumberFormatException var6) {
               this.gx.push(new Integer(4));
               this.gy = "Expected Integer, received: " + var2;
               throw new RuntimeException();
            }
         case 4:
            try {
               if (var2.startsWith("+")) {
                  return new Long(Long.parseLong(var2.substring(1)));
               }

               return new Long(Long.parseLong(var2));
            } catch (NumberFormatException var5) {
               this.gx.push(new Integer(4));
               this.gy = "Expected Long, received: " + var2;
               throw new RuntimeException();
            }
         case 5:
            try {
               if (var2.startsWith("+")) {
                  return new Float(Float.parseFloat(var2.substring(1)));
               } else if (var2.startsWith("NaN")) {
                  return new Float(Float.NaN);
               } else if (var2.startsWith("INF")) {
                  return new Float(Float.POSITIVE_INFINITY);
               } else {
                  if (var2.startsWith("-INF")) {
                     return new Float(Float.NEGATIVE_INFINITY);
                  }

                  return new Float(Float.parseFloat(var2));
               }
            } catch (NumberFormatException var4) {
               this.gx.push(new Integer(4));
               this.gy = "Expected Float, received: " + var2;
               throw new RuntimeException();
            }
         case 6:
            try {
               if (var2.startsWith("+")) {
                  return new Double(Double.parseDouble(var2.substring(1)));
               } else if (var2.startsWith("NaN")) {
                  return new Double(Double.NaN);
               } else if (var2.startsWith("INF")) {
                  return new Double(Double.POSITIVE_INFINITY);
               } else {
                  if (var2.startsWith("-INF")) {
                     return new Double(Double.NEGATIVE_INFINITY);
                  }

                  return new Double(Double.parseDouble(var2));
               }
            } catch (NumberFormatException var3) {
               this.gx.push(new Integer(4));
               this.gy = "Expected Double, received: " + var2;
               throw new RuntimeException();
            }
         case 7:
            return var2;
         default:
            this.gx.push(new Integer(4));
            this.gy = "Unable to decode type: " + var1.value + ", for token: " + var2;
            throw new RuntimeException();
         }
      }
   }

   public void startElement(String var1, String var2, String var3, Attributes var4) {
      this.gp = var2;
      if (this.gp == null || this.gp.length() == 0) {
         this.gp = var3;
      }

      if ((this.gp.equals("Envelope") || this.gp.equals("Body")) && !this.gn) {
         if (this.gm < 2) {
            if (!var1.equals("http://schemas.xmlsoap.org/soap/envelope/")) {
               this.gy = "Invalid URI From Server: " + var1 + ", " + "expected: " + "http://schemas.xmlsoap.org/soap/envelope/";
               this.gx.push(new Integer(4));
               throw new RuntimeException();
            }

            ++this.gm;
            return;
         }

         ++this.gm;
      }

      if (this.gm == 1 && var1.equals("http://schemas.xmlsoap.org/soap/envelope/") && this.gp.equals("Header")) {
         this.gn = true;
      } else if (this.gn) {
         if ((var2 = var4.getValue("http://schemas.xmlsoap.org/soap/envelope/", "mustUnderstand")) != null && var2.equals("1")) {
            this.gy = "Unsupported header element with mustUnderstand";
            this.gx.push(new Integer(4));
            throw new RuntimeException();
         }
      } else if (this.gm < 2) {
         this.gy = "Missing SOAP Body or Envelope";
         this.gx.push(new Integer(4));
         throw new RuntimeException();
      } else if (this.go) {
         this.gy = "Nillable element contains value: " + this.gp;
         this.gx.push(new Integer(4));
         throw new RuntimeException();
      } else {
         var2 = var4.getValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
         this.go = var2 != null && (var2.toLowerCase().equals("true") || var2.equals("1"));
         this.gl.delete(0, this.gl.length());
         var2 = this.gp;
         var1 = var1;
         Type var6;
         if ((var6 = (Type)this.gw.peek()) instanceof Element) {
            Element var8 = (Element)var6;
            this.a(var8, var1, var2);
            if (var8.contentType.value >= 8) {
               if (var8.contentType.value == 8) {
                  this.gw.push(var8.contentType);
                  if (!this.go) {
                     this.gv.push(a((ComplexType)var8.contentType));
                  }

                  this.gx.push(new Integer(2));
               }

               return;
            }

            this.gx.push(new Integer(1));
         } else if (var6 instanceof ComplexType) {
            ComplexType var9 = (ComplexType)var6;
            int var7 = -1;

            for(int var5 = 0; var5 < var9.elements.length; ++var5) {
               if (a(var9.elements[var5].name, var1, var2)) {
                  var7 = var5;
                  break;
               }
            }

            if (var7 == -1) {
               if (this.gq && this.gs != null && this.gt == null && var9 == this.gr) {
                  this.gt = new QName(var1, var2);
                  Element var10;
                  if ((var10 = this.gs.handleFault(this.gt)) != null) {
                     this.gu = var10;
                     if (this.gu.contentType.value != 8) {
                        this.gw.push(this.gu);
                        this.gx.push(new Integer(1));
                        return;
                     }

                     this.gw.push(this.gu.contentType);
                     if (!this.go) {
                        this.gv.push(a((ComplexType)this.gu.contentType));
                     }

                     this.gx.push(new Integer(2));
                     return;
                  }
               }

               if (this.gu == null) {
                  this.gx.push(new Integer(4));
                  this.gy = "Invalid Element in Response: " + var2;
                  throw new RuntimeException();
               }
            }

            this.a(var9.elements[var7], var1, var2);
            if (var9.elements[var7].contentType.value == 8) {
               this.gw.push(var9.elements[var7].contentType);
               if (!this.go) {
                  this.gv.push(a((ComplexType)var9.elements[var7].contentType));
               }

               this.gx.push(new Integer(2));
            }
         }

      }
   }

   public void endElement(String var1, String var2, String var3) {
      this.gp = var2;
      if (this.gp == null || this.gp.length() == 0) {
         this.gp = var3;
      }

      if ((this.gp.equals("Envelope") || this.gp.equals("Body")) && !this.gn) {
         if (this.gm <= 2) {
            if (!var1.equals("http://schemas.xmlsoap.org/soap/envelope/")) {
               this.gy = "Invalid URI From Server: " + var1 + ", " + "expected: " + "http://schemas.xmlsoap.org/soap/envelope/";
               this.gx.push(new Integer(4));
               throw new RuntimeException();
            }

            --this.gm;
            return;
         }

         --this.gm;
      }

      if (this.gm == 1 && var1.equals("http://schemas.xmlsoap.org/soap/envelope/") && this.gp.equals("Header")) {
         this.gn = false;
      } else {
         if (!this.gn) {
            var3 = this.gp;
            var2 = var1;
            Type var4 = (Type)this.gw.pop();
            this.state = (Integer)this.gx.peek();
            switch(this.state) {
            case 1:
               Element var12 = (Element)var4;
               if (this.gt != null && a(this.gt, var1, var3) && this.gu != null) {
                  this.gx.pop();
                  if (!this.go) {
                     Object var11 = this.a(var12.contentType, this.gl.toString());
                     ((Object[])this.gv.peek())[3] = var11;
                  }
               } else {
                  this.a(var12, var1, var3);
                  if (!this.go) {
                     this.gv.push(this.a(var12.contentType, this.gl.toString()));
                  }

                  this.gx.pop();
               }
               break;
            case 2:
               label173: {
                  ComplexType var6 = (ComplexType)var4;
                  int var7 = -1;

                  for(int var5 = 0; var5 < var6.elements.length; ++var5) {
                     if (a(var6.elements[var5].name, var2, var3)) {
                        var7 = var5;
                        break;
                     }
                  }

                  Object var10 = null;
                  if (var7 == -1) {
                     this.gx.pop();
                     if (this.gx.isEmpty()) {
                        if (!((var4 = (Type)this.gw.pop()) instanceof Element) || !a(((Element)var4).name, var2, var3)) {
                           this.gx.push(new Integer(4));
                           this.gy = "(3):Mismatch between server response and type map";
                           throw new RuntimeException();
                        }
                        break label173;
                     }

                     this.state = (Integer)this.gx.peek();
                     if (this.state != 2) {
                        this.gx.push(new Integer(4));
                        this.gy = "(1):Mismatch between server response and type map";
                        throw new RuntimeException();
                     }

                     var6 = (ComplexType)(var4 = (Type)this.gw.pop());
                     var7 = -1;

                     for(int var8 = 0; var8 < var6.elements.length; ++var8) {
                        if (a(var6.elements[var8].name, var2, var3)) {
                           var7 = var8;
                           break;
                        }
                     }

                     if (var7 == -1) {
                        if (this.gt != null && a(this.gt, var2, var3) && this.gu != null) {
                           Object var14 = this.gv.pop();
                           ((Object[])this.gv.peek())[3] = var14;
                           this.gw.push(var4);
                           break label173;
                        }

                        this.gx.push(new Integer(4));
                        this.gy = "(2):Mismatch between server response and type map";
                        throw new RuntimeException();
                     }

                     if (!this.go) {
                        var10 = this.gv.pop();
                     }
                  }

                  Object[] var13 = (Object[])this.gv.peek();
                  this.a(var6.elements[var7], var2, var3);
                  if (this.gr != null && this.gr.elements == var6.elements && var7 == 3 && this.gu != null) {
                     var10 = var13[3];
                  } else if (var10 == null && !this.go) {
                     var10 = this.a(var6.elements[var7].contentType, this.gl.toString());
                  }

                  if (var6.elements[var7].isArray) {
                     TypedVector var9 = null;
                     if (var13[var7] == null) {
                        var9 = new TypedVector(var6.elements[var7].contentType.value, var6.elements[var7].isNillable);
                        var13[var7] = var9;
                     } else {
                        var9 = (TypedVector)var13[var7];
                     }

                     if (var10 == null) {
                        var9.addElement(gj);
                     } else {
                        var9.addElement(var10);
                     }
                  } else {
                     var13[var7] = var10;
                  }

                  this.gw.push(var4);
               }
            }

            this.go = false;
         }

      }
   }

   public void characters(char[] var1, int var2, int var3) {
      if (!this.gn) {
         this.gl.append(var1, var2, var3);
      }

   }
}
