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
   private static final Object NIL = new Object();
   private SAXParser parser;
   private StringBuffer token;
   private int state;
   private int bodyNEnvelope;
   private boolean processingHeader;
   private boolean isNill;
   private String eName;
   private boolean faultMode;
   private ComplexType faultCT;
   private FaultDetailHandler handler;
   private QName detailName;
   private Element handlerDetail;
   private Stack valueStack;
   private Stack typeStack;
   private Stack stateStack;
   private String errString;
   private static final String SOAP_URI = "http://schemas.xmlsoap.org/soap/envelope/";

   public SOAPDecoder() {
      try {
         SAXParserFactory pf = SAXParserFactory.newInstance();
         pf.setNamespaceAware(true);
         pf.setValidating(false);
         this.parser = pf.newSAXParser();
      } catch (Exception var2) {
         throw new RuntimeException("Could not instantiate parser");
      }

      this.token = new StringBuffer();
      this.valueStack = new Stack();
      this.typeStack = new Stack();
      this.stateStack = new Stack();
   }

   public synchronized Object decode(Type type, InputStream stream, String encoding, long length) throws JAXRPCException {
      this.handler = null;
      this.handlerDetail = null;
      this.faultMode = false;
      this.detailName = null;
      this.isNill = false;
      this.bodyNEnvelope = 0;
      this.processingHeader = false;
      this.clearStacks();
      if (!(type instanceof Element)) {
         throw new JAXRPCException(new MarshalException("Type parameter not instanceof Element"));
      } else {
         this.typeStack.push(type);

         try {
            this.parser.parse((InputStream)stream, this);
         } catch (RuntimeException var7) {
         } catch (SAXParseException var8) {
            this.stateStack.push(new Integer(4));
            this.errString = "SAXParseException in response from server";
         } catch (Throwable var9) {
            this.stateStack.push(new Integer(4));
            this.errString = "Unexpected Exception : " + var9.getMessage();
         }

         if (!this.stateStack.empty()) {
            this.state = (Integer)this.stateStack.pop();
         } else if (this.bodyNEnvelope != 0) {
            this.state = 4;
            this.errString = "(1)Missing end tag for Body or Envelope";
         } else {
            this.state = 0;
         }

         if (this.state == 4) {
            throw new JAXRPCException(new MarshalException(this.errString));
         } else if (this.valueStack.isEmpty()) {
            Element e = (Element)type;
            if (!e.isNillable && !e.isOptional) {
               throw new JAXRPCException(new MarshalException("Missing return data in response from server"));
            } else {
               return null;
            }
         } else {
            return vectorToArray(this.valueStack.pop());
         }
      }
   }

   public synchronized Object decodeFault(FaultDetailHandler handler, InputStream stream, String encoding, long length) throws JAXRPCException {
      this.handler = handler;
      this.handlerDetail = null;
      this.faultMode = true;
      this.detailName = null;
      this.isNill = false;
      this.bodyNEnvelope = 0;
      this.processingHeader = false;
      this.clearStacks();
      this.faultCT = new ComplexType();
      this.faultCT.elements = new Element[4];
      this.faultCT.elements[0] = new Element(new QName("", "faultcode"), Type.STRING);
      this.faultCT.elements[1] = new Element(new QName("", "faultstring"), Type.STRING);
      this.faultCT.elements[2] = new Element(new QName("", "faultactor"), Type.STRING, 0, 1, false);
      this.faultCT.elements[3] = new Element(new QName("", "detail"), Type.STRING, 0, 1, false);
      Element faultType = new Element(new QName("http://schemas.xmlsoap.org/soap/envelope/", "Fault"), this.faultCT);
      this.typeStack.push(faultType);

      try {
         this.parser.parse((InputStream)stream, this);
      } catch (RuntimeException var8) {
      } catch (SAXParseException var9) {
         this.stateStack.push(new Integer(4));
         this.errString = "SAXParseException in response from server";
      } catch (Throwable var10) {
         var10.printStackTrace();
      }

      if (!this.stateStack.empty()) {
         this.state = (Integer)this.stateStack.pop();
      } else if (this.bodyNEnvelope != 0) {
         this.state = 4;
         this.errString = "(2)Missing end tag for Body or Envelope";
      } else {
         this.state = 0;
      }

      if (this.state == 4) {
         throw new JAXRPCException(new MarshalException(this.errString));
      } else {
         Object[] fault = (Object[])this.valueStack.pop();
         this.token.delete(0, this.token.length());
         if (fault[0] != null) {
            this.token.append("\n[Code:   " + (String)fault[0] + "] ");
         }

         if (fault[1] != null) {
            this.token.append("\n[String: " + (String)fault[1] + "] ");
         }

         if (fault[2] != null) {
            this.token.append("\n[Actor:  " + (String)fault[2] + "] ");
         }

         if (this.detailName == null) {
            if (fault[3] != null) {
               this.token.append("\n[Detail: " + (String)fault[3] + "] ");
            }

            return this.token.toString();
         } else {
            return new Object[]{this.token.toString(), this.detailName, vectorToArray(fault[3])};
         }
      }
   }

   private void clearStacks() {
      this.stateStack.removeAllElements();
      this.typeStack.removeAllElements();
      this.valueStack.removeAllElements();
   }

   private Object graph(ComplexType ct) throws JAXRPCException {
      try {
         Object[] o = new Object[ct.elements.length];

         for(int i = 0; i < ct.elements.length; ++i) {
            if (ct.elements[i].isArray) {
               o[i] = new TypedVector(ct.elements[i].contentType.value, ct.elements[i].isNillable);
            }
         }

         return o;
      } catch (Throwable var4) {
         throw new JAXRPCException("Invalid Type for Output");
      }
   }

   private void startState(String uri, String name) {
      Type top = (Type)this.typeStack.peek();
      if (top instanceof Element) {
         Element e = (Element)top;
         this.validate(e, uri, name);
         if (e.contentType.value < 8) {
            this.stateStack.push(new Integer(1));
         } else if (e.contentType.value == 8) {
            this.typeStack.push(e.contentType);
            if (!this.isNill) {
               this.valueStack.push(this.graph((ComplexType)e.contentType));
            }

            this.stateStack.push(new Integer(2));
         }
      } else if (top instanceof ComplexType) {
         ComplexType ct = (ComplexType)top;
         int index = -1;

         for(int i = 0; i < ct.elements.length; ++i) {
            if (matchType(ct.elements[i].name, uri, name)) {
               index = i;
               break;
            }
         }

         if (index == -1) {
            if (this.faultMode && this.handler != null && this.detailName == null && ct == this.faultCT) {
               this.detailName = new QName(uri, name);
               Element e = this.handler.handleFault(this.detailName);
               if (e != null) {
                  this.handlerDetail = e;
                  if (this.handlerDetail.contentType.value == 8) {
                     this.typeStack.push(this.handlerDetail.contentType);
                     if (!this.isNill) {
                        this.valueStack.push(this.graph((ComplexType)this.handlerDetail.contentType));
                     }

                     this.stateStack.push(new Integer(2));
                  } else {
                     this.typeStack.push(this.handlerDetail);
                     this.stateStack.push(new Integer(1));
                  }

                  return;
               }
            }

            if (this.handlerDetail == null) {
               this.stateStack.push(new Integer(4));
               this.errString = "Invalid Element in Response: " + name;
               throw new RuntimeException();
            }
         }

         this.validate(ct.elements[index], uri, name);
         if (ct.elements[index].contentType.value == 8) {
            this.typeStack.push(ct.elements[index].contentType);
            if (!this.isNill) {
               this.valueStack.push(this.graph((ComplexType)ct.elements[index].contentType));
            }

            this.stateStack.push(new Integer(2));
         }
      }

   }

   private static boolean matchType(QName qname, String uri, String name) {
      return qname.getNamespaceURI().equals(uri) && qname.getLocalPart().equals(name);
   }

   private void validate(Element e, String uri, String name) {
      if (!name.equals(e.name.getLocalPart())) {
         this.errString = "Invalid Element Name From Server: " + name + ", " + "expected: " + e.name.getLocalPart();
         this.stateStack.push(new Integer(4));
         throw new RuntimeException();
      } else if (!uri.equals(e.name.getNamespaceURI())) {
         this.errString = "Invalid Namespace URI From Server: " + uri + ", " + "expected: " + e.name.getNamespaceURI() + " for element: " + name;
         this.stateStack.push(new Integer(4));
         throw new RuntimeException();
      } else if (this.isNill && !e.isNillable) {
         this.errString = "Nillable mismatch from server for: " + name;
         this.stateStack.push(new Integer(4));
         throw new RuntimeException();
      }
   }

   private void endState(String uri, String name) {
      Type top = (Type)this.typeStack.pop();
      this.state = (Integer)this.stateStack.peek();
      switch(this.state) {
      case 1:
         Element e = (Element)top;
         if (this.detailName != null && matchType(this.detailName, uri, name) && this.handlerDetail != null) {
            this.stateStack.pop();
            if (!this.isNill) {
               Object fd = this.tokenToObject(e.contentType, this.token.toString());
               Object[] f = (Object[])this.valueStack.peek();
               f[3] = fd;
            }

            return;
         }

         this.validate(e, uri, name);
         if (!this.isNill) {
            this.valueStack.push(this.tokenToObject(e.contentType, this.token.toString()));
         }

         this.stateStack.pop();
         return;
      case 2:
         ComplexType ct = (ComplexType)top;
         int index = -1;

         for(int i = 0; i < ct.elements.length; ++i) {
            if (matchType(ct.elements[i].name, uri, name)) {
               index = i;
               break;
            }
         }

         Object ctVal = null;
         if (index == -1) {
            this.stateStack.pop();
            if (this.stateStack.isEmpty()) {
               top = (Type)this.typeStack.pop();
               if (top instanceof Element && matchType(((Element)top).name, uri, name)) {
                  return;
               }

               this.stateStack.push(new Integer(4));
               this.errString = "(3):Mismatch between server response and type map";
               throw new RuntimeException();
            }

            this.state = (Integer)this.stateStack.peek();
            if (this.state != 2) {
               this.stateStack.push(new Integer(4));
               this.errString = "(1):Mismatch between server response and type map";
               throw new RuntimeException();
            }

            top = (Type)this.typeStack.pop();
            ct = (ComplexType)top;
            index = -1;

            for(int i = 0; i < ct.elements.length; ++i) {
               if (matchType(ct.elements[i].name, uri, name)) {
                  index = i;
                  break;
               }
            }

            if (index == -1) {
               if (this.detailName != null && matchType(this.detailName, uri, name) && this.handlerDetail != null) {
                  Object fd = this.valueStack.pop();
                  Object[] f = (Object[])this.valueStack.peek();
                  f[3] = fd;
                  this.typeStack.push(top);
                  return;
               }

               this.stateStack.push(new Integer(4));
               this.errString = "(2):Mismatch between server response and type map";
               throw new RuntimeException();
            }

            if (!this.isNill) {
               ctVal = this.valueStack.pop();
            }
         }

         Object[] els = (Object[])this.valueStack.peek();
         this.validate(ct.elements[index], uri, name);
         if (this.faultCT != null && this.faultCT.elements == ct.elements && index == 3 && this.handlerDetail != null) {
            ctVal = els[3];
         } else if (ctVal == null && !this.isNill) {
            ctVal = this.tokenToObject(ct.elements[index].contentType, this.token.toString());
         }

         if (ct.elements[index].isArray) {
            TypedVector v = null;
            if (els[index] == null) {
               v = new TypedVector(ct.elements[index].contentType.value, ct.elements[index].isNillable);
               els[index] = v;
            } else {
               v = (TypedVector)els[index];
            }

            if (ctVal == null) {
               v.addElement(NIL);
            } else {
               v.addElement(ctVal);
            }
         } else {
            els[index] = ctVal;
         }

         this.typeStack.push(top);
         return;
      default:
      }
   }

   private static Object vectorToArray(Object o) {
      if (o instanceof Object[]) {
         Object[] set = (Object[])o;

         for(int i = 0; i < set.length; ++i) {
            if (!(set[i] instanceof TypedVector)) {
               if (set[i] == NIL) {
                  set[i] = null;
               } else if (set[i] instanceof Object[]) {
                  set[i] = vectorToArray(set[i]);
               }
            } else {
               TypedVector v = (TypedVector)set[i];
               Object arrayEl;
               int j;
               int k;
               switch(v.type) {
               case 0:
                  if (v.nillable) {
                     Boolean[] tmp = new Boolean[v.size()];

                     for(j = 0; j < tmp.length; ++j) {
                        arrayEl = v.elementAt(j);
                        if (arrayEl != NIL) {
                           tmp[j] = (Boolean)arrayEl;
                        }
                     }

                     set[i] = tmp;
                  } else {
                     boolean[] tmp = new boolean[v.size()];

                     for(k = 0; k < tmp.length; ++k) {
                        tmp[k] = (Boolean)v.elementAt(k);
                     }

                     set[i] = tmp;
                  }
                  break;
               case 1:
                  if (v.nillable) {
                     Byte[] tmp = new Byte[v.size()];

                     for(j = 0; j < tmp.length; ++j) {
                        arrayEl = v.elementAt(j);
                        if (arrayEl != NIL) {
                           tmp[j] = (Byte)arrayEl;
                        }
                     }

                     set[i] = tmp;
                  } else {
                     byte[] tmp = new byte[v.size()];

                     for(k = 0; k < tmp.length; ++k) {
                        tmp[k] = (Byte)v.elementAt(k);
                     }

                     set[i] = tmp;
                  }
                  break;
               case 2:
                  if (v.nillable) {
                     Short[] tmp = new Short[v.size()];

                     for(j = 0; j < tmp.length; ++j) {
                        arrayEl = v.elementAt(j);
                        if (arrayEl != NIL) {
                           tmp[j] = (Short)arrayEl;
                        }
                     }

                     set[i] = tmp;
                  } else {
                     short[] tmp = new short[v.size()];

                     for(k = 0; k < tmp.length; ++k) {
                        tmp[k] = (Short)v.elementAt(k);
                     }

                     set[i] = tmp;
                  }
                  break;
               case 3:
                  if (v.nillable) {
                     Integer[] tmp = new Integer[v.size()];

                     for(j = 0; j < tmp.length; ++j) {
                        arrayEl = v.elementAt(j);
                        if (arrayEl != NIL) {
                           tmp[j] = (Integer)arrayEl;
                        }
                     }

                     set[i] = tmp;
                  } else {
                     int[] tmp = new int[v.size()];

                     for(k = 0; k < tmp.length; ++k) {
                        tmp[k] = (Integer)v.elementAt(k);
                     }

                     set[i] = tmp;
                  }
                  break;
               case 4:
                  if (v.nillable) {
                     Long[] tmp = new Long[v.size()];

                     for(j = 0; j < tmp.length; ++j) {
                        arrayEl = v.elementAt(j);
                        if (arrayEl != NIL) {
                           tmp[j] = (Long)arrayEl;
                        }
                     }

                     set[i] = tmp;
                  } else {
                     long[] tmp = new long[v.size()];

                     for(k = 0; k < tmp.length; ++k) {
                        tmp[k] = (Long)v.elementAt(k);
                     }

                     set[i] = tmp;
                  }
                  break;
               case 5:
                  if (v.nillable) {
                     Float[] tmp = new Float[v.size()];

                     for(j = 0; j < tmp.length; ++j) {
                        arrayEl = v.elementAt(j);
                        if (arrayEl != NIL) {
                           tmp[j] = (Float)arrayEl;
                        }
                     }

                     set[i] = tmp;
                  } else {
                     float[] tmp = new float[v.size()];

                     for(k = 0; k < tmp.length; ++k) {
                        tmp[k] = (Float)v.elementAt(k);
                     }

                     set[i] = tmp;
                  }
                  break;
               case 6:
                  if (v.nillable) {
                     Double[] tmp = new Double[v.size()];

                     for(j = 0; j < tmp.length; ++j) {
                        arrayEl = v.elementAt(j);
                        if (arrayEl != NIL) {
                           tmp[j] = (Double)arrayEl;
                        }
                     }

                     set[i] = tmp;
                  } else {
                     double[] tmp = new double[v.size()];

                     for(k = 0; k < tmp.length; ++k) {
                        tmp[k] = (Double)v.elementAt(k);
                     }

                     set[i] = tmp;
                  }
                  break;
               case 7:
                  String[] tmp = new String[v.size()];

                  for(j = 0; j < tmp.length; ++j) {
                     arrayEl = v.elementAt(j);
                     if (arrayEl != NIL) {
                        tmp[j] = (String)arrayEl;
                     }
                  }

                  set[i] = tmp;
                  break;
               default:
                  Object[] l = new Object[v.size()];
                  v.copyInto(l);
                  set[i] = vectorToArray(l);
               }

               v = null;
            }
         }
      }

      return o;
   }

   private Object tokenToObject(Type type, String token) {
      if (token == null) {
         return null;
      } else {
         switch(type.value) {
         case 0:
            token = token.toLowerCase();
            if (!token.equals("true") && !token.equals("1")) {
               if (!token.equals("false") && !token.equals("0")) {
                  this.stateStack.push(new Integer(4));
                  this.errString = "Expected Boolean, received: " + token;
                  throw new RuntimeException();
               }

               return new Boolean(false);
            }

            return new Boolean(true);
         case 1:
            try {
               if (token.startsWith("+")) {
                  return new Byte(Byte.parseByte(token.substring(1)));
               }

               return new Byte(Byte.parseByte(token));
            } catch (NumberFormatException var9) {
               this.stateStack.push(new Integer(4));
               this.errString = "Expected Byte, received: " + token;
               throw new RuntimeException();
            }
         case 2:
            try {
               if (token.startsWith("+")) {
                  return new Short(Short.parseShort(token.substring(1)));
               }

               return new Short(Short.parseShort(token));
            } catch (NumberFormatException var8) {
               this.stateStack.push(new Integer(4));
               this.errString = "Expected Short, received: " + token;
               throw new RuntimeException();
            }
         case 3:
            try {
               if (token.startsWith("+")) {
                  return new Integer(Integer.parseInt(token.substring(1)));
               }

               return new Integer(Integer.parseInt(token));
            } catch (NumberFormatException var7) {
               this.stateStack.push(new Integer(4));
               this.errString = "Expected Integer, received: " + token;
               throw new RuntimeException();
            }
         case 4:
            try {
               if (token.startsWith("+")) {
                  return new Long(Long.parseLong(token.substring(1)));
               }

               return new Long(Long.parseLong(token));
            } catch (NumberFormatException var6) {
               this.stateStack.push(new Integer(4));
               this.errString = "Expected Long, received: " + token;
               throw new RuntimeException();
            }
         case 5:
            try {
               if (token.startsWith("+")) {
                  return new Float(Float.parseFloat(token.substring(1)));
               } else if (token.startsWith("NaN")) {
                  return new Float(Float.NaN);
               } else if (token.startsWith("INF")) {
                  return new Float(Float.POSITIVE_INFINITY);
               } else {
                  if (token.startsWith("-INF")) {
                     return new Float(Float.NEGATIVE_INFINITY);
                  }

                  return new Float(Float.parseFloat(token));
               }
            } catch (NumberFormatException var5) {
               this.stateStack.push(new Integer(4));
               this.errString = "Expected Float, received: " + token;
               throw new RuntimeException();
            }
         case 6:
            try {
               if (token.startsWith("+")) {
                  return new Double(Double.parseDouble(token.substring(1)));
               } else if (token.startsWith("NaN")) {
                  return new Double(Double.NaN);
               } else if (token.startsWith("INF")) {
                  return new Double(Double.POSITIVE_INFINITY);
               } else {
                  if (token.startsWith("-INF")) {
                     return new Double(Double.NEGATIVE_INFINITY);
                  }

                  return new Double(Double.parseDouble(token));
               }
            } catch (NumberFormatException var4) {
               this.stateStack.push(new Integer(4));
               this.errString = "Expected Double, received: " + token;
               throw new RuntimeException();
            }
         case 7:
            return token;
         default:
            this.stateStack.push(new Integer(4));
            this.errString = "Unable to decode type: " + type.value + ", for token: " + token;
            throw new RuntimeException();
         }
      }
   }

   public void startElement(String uri, String localName, String name, Attributes attrs) {
      this.eName = localName;
      if (this.eName == null || this.eName.length() == 0) {
         this.eName = name;
      }

      if ((this.eName.equals("Envelope") || this.eName.equals("Body")) && !this.processingHeader) {
         if (this.bodyNEnvelope < 2) {
            if (!uri.equals("http://schemas.xmlsoap.org/soap/envelope/")) {
               this.errString = "Invalid URI From Server: " + uri + ", " + "expected: " + "http://schemas.xmlsoap.org/soap/envelope/";
               this.stateStack.push(new Integer(4));
               throw new RuntimeException();
            }

            ++this.bodyNEnvelope;
            return;
         }

         ++this.bodyNEnvelope;
      }

      if (this.bodyNEnvelope == 1 && uri.equals("http://schemas.xmlsoap.org/soap/envelope/") && this.eName.equals("Header")) {
         this.processingHeader = true;
      } else {
         String attr;
         if (this.processingHeader) {
            attr = attrs.getValue("http://schemas.xmlsoap.org/soap/envelope/", "mustUnderstand");
            if (attr != null && attr.equals("1")) {
               this.errString = "Unsupported header element with mustUnderstand";
               this.stateStack.push(new Integer(4));
               throw new RuntimeException();
            }
         } else if (this.bodyNEnvelope < 2) {
            this.errString = "Missing SOAP Body or Envelope";
            this.stateStack.push(new Integer(4));
            throw new RuntimeException();
         } else if (this.isNill) {
            this.errString = "Nillable element contains value: " + this.eName;
            this.stateStack.push(new Integer(4));
            throw new RuntimeException();
         } else {
            attr = attrs.getValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            this.isNill = attr != null && (attr.toLowerCase().equals("true") || attr.equals("1"));
            this.token.delete(0, this.token.length());
            this.startState(uri, this.eName);
         }
      }
   }

   public void endElement(String uri, String localName, String name) {
      this.eName = localName;
      if (this.eName == null || this.eName.length() == 0) {
         this.eName = name;
      }

      if ((this.eName.equals("Envelope") || this.eName.equals("Body")) && !this.processingHeader) {
         if (this.bodyNEnvelope <= 2) {
            if (!uri.equals("http://schemas.xmlsoap.org/soap/envelope/")) {
               this.errString = "Invalid URI From Server: " + uri + ", " + "expected: " + "http://schemas.xmlsoap.org/soap/envelope/";
               this.stateStack.push(new Integer(4));
               throw new RuntimeException();
            }

            --this.bodyNEnvelope;
            return;
         }

         --this.bodyNEnvelope;
      }

      if (this.bodyNEnvelope == 1 && uri.equals("http://schemas.xmlsoap.org/soap/envelope/") && this.eName.equals("Header")) {
         this.processingHeader = false;
      } else {
         if (!this.processingHeader) {
            this.endState(uri, this.eName);
            this.isNill = false;
         }

      }
   }

   public void characters(char[] chars, int start, int len) {
      if (!this.processingHeader) {
         this.token.append(chars, start, len);
      }

   }
}
