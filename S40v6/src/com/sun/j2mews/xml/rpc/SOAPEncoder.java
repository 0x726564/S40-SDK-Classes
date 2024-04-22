package com.sun.j2mews.xml.rpc;

import java.io.OutputStream;
import javax.microedition.xml.rpc.ComplexType;
import javax.microedition.xml.rpc.Element;
import javax.microedition.xml.rpc.Type;
import javax.xml.rpc.JAXRPCException;

public class SOAPEncoder {
   StringBuffer buffer;
   String defaultNS;
   String errString;

   public synchronized void encode(Object value, Type type, OutputStream stream, String encoding) throws JAXRPCException {
      this.buffer = new StringBuffer();
      this.errString = null;
      this.buffer.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
      this.buffer.append("<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n\txmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n\txmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"");

      try {
         if (type != null) {
            Element op = (Element)type;
            this.defaultNS = op.name.getNamespaceURI();
            this.buffer.append("\n\txmlns:tns=\"" + this.defaultNS + "\">\n");
            this.buffer.append("<soap:Body>\n");
            this.encode((Element)type, value);
         } else {
            this.buffer.append(">\n<soap:Body/>\n");
         }
      } catch (RuntimeException var7) {
      } catch (Throwable var8) {
         throw new JAXRPCException("Could not encode request");
      }

      if (this.errString != null) {
         throw new JAXRPCException(this.errString);
      } else {
         if (type != null) {
            this.buffer.append("</soap:Body>\n");
         }

         this.buffer.append("</soap:Envelope>\n");

         try {
            if (encoding != null) {
               stream.write(this.buffer.toString().getBytes(encoding));
            } else {
               stream.write(this.buffer.toString().getBytes());
            }
         } catch (Exception var6) {
            this.buffer = null;
            throw new JAXRPCException(var6.getMessage());
         }

         this.buffer = null;
      }
   }

   private void encode(Element parent, Object value) {
      String id;
      if (parent.name.getNamespaceURI().equals(this.defaultNS)) {
         id = "tns:" + parent.name.getLocalPart();
      } else {
         id = parent.name.getLocalPart() + " xmlns=\"" + parent.name.getNamespaceURI() + "\"";
      }

      if (value != null) {
         int count = 1;
         Object[] values = new Object[]{value};
         if (parent.isArray) {
            if (!(value instanceof Object[])) {
               if (this.isPrimitiveArray(parent.contentType, value)) {
                  this.encodePrimitiveArray(id, parent, value);
                  return;
               }

               this.errString = "Type mismatch: elements of an array must be an array.";
               throw new RuntimeException();
            }

            count = ((Object[])value).length;
            values = (Object[])value;
            this.checkArraySize(parent, values.length);
         }

         for(int i = 0; i < count; ++i) {
            if (values[i] == null) {
               if (!parent.isNillable) {
                  this.errString = "Null value for non-nillable/optional element: " + parent.name.getLocalPart();
                  throw new RuntimeException();
               }

               this.buffer.append("<" + id + " xsi:nil=\"true\"/>\n");
            } else {
               this.buffer.append("<" + id + ">");
               if (parent.contentType.value < 8) {
                  this.encodeSimpleType(parent.contentType, values[i]);
               } else if (parent.contentType.value == 8) {
                  if (!(values[i] instanceof Object[])) {
                     this.errString = "Type mismatch: element of ComplexType must be an array.";
                     throw new RuntimeException();
                  }

                  this.buffer.append("\n");
                  this.encodeComplexType((ComplexType)parent.contentType, (Object[])values[i]);
               } else if (parent.contentType.value == 9) {
                  this.errString = "Encoding error - unable to encode indirected Elements of Elements";
                  throw new RuntimeException();
               }

               if (parent.name.getNamespaceURI().equals(this.defaultNS)) {
                  this.buffer.append("</" + id + ">\n");
               } else {
                  this.buffer.append("</" + parent.name.getLocalPart() + ">\n");
               }
            }
         }

      } else {
         if (parent.isNillable && !parent.isArray) {
            this.buffer.append("<" + id + " xsi:nil=\"true\"/>\n");
         } else if (!parent.isOptional) {
            this.errString = "Null value for non-nillable/optional element: " + parent.name.getLocalPart();
            throw new RuntimeException();
         }

      }
   }

   private void encodeSimpleType(Type contentType, Object value) {
      if (!this.checkSimpleType(contentType, value)) {
         this.errString = "Simple Type Mismatch";
         throw new RuntimeException();
      } else {
         if (contentType.value < 7) {
            this.buffer.append(value.toString());
         } else {
            this.xmlIzeString(value.toString());
         }

      }
   }

   private void encodeComplexType(ComplexType type, Object[] values) {
      Element[] elements = type.elements;
      if (elements.length != values.length) {
         this.errString = "Wrong number of values passed for complex type";
         throw new RuntimeException();
      } else {
         for(int i = 0; i < elements.length; ++i) {
            this.encode(elements[i], values[i]);
         }

      }
   }

   private void encodePrimitiveArray(String id, Element parent, Object value) {
      int i;
      switch(parent.contentType.value) {
      case 0:
         if (value instanceof boolean[]) {
            boolean[] values = (boolean[])value;
            this.checkArraySize(parent, values.length);

            for(i = 0; i < values.length; ++i) {
               this.buffer.append("<" + id + ">" + values[i] + "</" + id + ">\n");
            }

            return;
         }
         break;
      case 1:
         if (value instanceof byte[]) {
            byte[] values = (byte[])value;
            this.checkArraySize(parent, values.length);

            for(i = 0; i < values.length; ++i) {
               this.buffer.append("<" + id + ">" + values[i] + "</" + id + ">\n");
            }

            return;
         }
         break;
      case 2:
         if (value instanceof short[]) {
            short[] values = (short[])value;
            this.checkArraySize(parent, values.length);

            for(i = 0; i < values.length; ++i) {
               this.buffer.append("<" + id + ">" + values[i] + "</" + id + ">\n");
            }

            return;
         }
         break;
      case 3:
         if (value instanceof int[]) {
            int[] values = (int[])value;
            this.checkArraySize(parent, values.length);

            for(i = 0; i < values.length; ++i) {
               this.buffer.append("<" + id + ">" + values[i] + "</" + id + ">\n");
            }

            return;
         }
         break;
      case 4:
         if (value instanceof long[]) {
            long[] values = (long[])value;
            this.checkArraySize(parent, values.length);

            for(i = 0; i < values.length; ++i) {
               this.buffer.append("<" + id + ">" + values[i] + "</" + id + ">\n");
            }

            return;
         }
         break;
      case 5:
         if (value instanceof float[]) {
            float[] values = (float[])value;
            this.checkArraySize(parent, values.length);

            for(i = 0; i < values.length; ++i) {
               this.buffer.append("<" + id + ">" + values[i] + "</" + id + ">\n");
            }

            return;
         }
         break;
      case 6:
         if (value instanceof double[]) {
            double[] values = (double[])value;
            this.checkArraySize(parent, values.length);

            for(i = 0; i < values.length; ++i) {
               this.buffer.append("<" + id + ">" + values[i] + "</" + id + ">\n");
            }

            return;
         }
      }

      this.errString = "Invalid values for primitive array for " + parent.name.getLocalPart();
      throw new RuntimeException();
   }

   private void checkArraySize(Element parent, int len) {
      if (len < parent.minOccurs) {
         this.errString = "Not enough array elements for: " + parent.name.getLocalPart();
         throw new RuntimeException();
      } else if (parent.maxOccurs > 0 && len > parent.maxOccurs) {
         this.errString = "Too many array elements for: " + parent.name.getLocalPart();
         throw new RuntimeException();
      }
   }

   private void xmlIzeString(String input) {
      char[] chars = input.toCharArray();

      for(int i = 0; i < chars.length; ++i) {
         if (chars[i] == '<') {
            this.buffer.append("&lt;");
         } else if (chars[i] == '>') {
            this.buffer.append("&gt;");
         } else if (chars[i] == '&') {
            this.buffer.append("&amp;");
         } else if (chars[i] == '\'') {
            this.buffer.append("@apos;");
         } else if (chars[i] == '"') {
            this.buffer.append("&quot;");
         } else {
            this.buffer.append(chars[i]);
         }
      }

   }

   private boolean checkSimpleType(Type contentType, Object value) {
      switch(contentType.value) {
      case 0:
         return value instanceof Boolean;
      case 1:
         return value instanceof Byte;
      case 2:
         return value instanceof Short;
      case 3:
         return value instanceof Integer;
      case 4:
         return value instanceof Long;
      case 5:
         return value instanceof Float;
      case 6:
         return value instanceof Double;
      case 7:
         return value instanceof String;
      default:
         return false;
      }
   }

   private boolean isPrimitiveArray(Type contentType, Object value) {
      switch(contentType.value) {
      case 0:
         return value instanceof boolean[];
      case 1:
         return value instanceof byte[];
      case 2:
         return value instanceof short[];
      case 3:
         return value instanceof int[];
      case 4:
         return value instanceof long[];
      case 5:
         return value instanceof float[];
      case 6:
         return value instanceof double[];
      default:
         return false;
      }
   }
}
