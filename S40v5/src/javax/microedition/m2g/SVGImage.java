package javax.microedition.m2g;

import com.nokia.mid.impl.isa.m2g.DocumentImpl;
import com.nokia.mid.impl.isa.m2g.EventImpl;
import com.nokia.mid.impl.isa.m2g.SVGElementImpl;
import com.nokia.mid.impl.isa.m2g.SVGSVGElementImpl;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.StreamConnection;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.svg.SVGElement;

public class SVGImage extends ScalableImage {
   private boolean a = false;
   static Object b = new Object();
   private DocumentImpl c = null;
   private SVGElement d = null;

   protected SVGImage() {
   }

   final void a(int var1, int var2) {
      ((SVGSVGElementImpl)this.c.getDocumentElement()).refreshRenderSurface(var1, var2);
      this.a = false;
   }

   final boolean a() {
      return this.a;
   }

   public Document getDocument() {
      return this.c;
   }

   public static SVGImage createEmptyImage(ExternalResourceHandler var0) {
      SVGImage var1 = new SVGImage();
      if (var0 == null) {
         var0 = new SVGImage.DefaultExternalResourceHandler();
      }

      String var2 = "<svg version=\"1.1\" baseProfile=\"tiny\" width=\"100\" height=\"100\"></svg>";

      try {
         var1 = (SVGImage)createImage((InputStream)(new ByteArrayInputStream(var2.getBytes())), (ExternalResourceHandler)var0);
      } catch (IOException var3) {
      }

      return var1;
   }

   public void dispatchMouseEvent(String var1, int var2, int var3) throws DOMException {
      if (var1 != null && !var1.equals("")) {
         if (var2 >= 0 && var3 >= 0) {
            if (!var1.equals("click")) {
               throw new DOMException((short)9, "Only supports mouseevent click");
            } else {
               int var4 = _dispatchMouseEvent(((DocumentImpl)this.getDocument()).getDocumentHandle(), var2, var3);
               SVGElement var5 = null;
               SVGElementImpl var6 = null;
               if (var4 != 0) {
                  var5 = DocumentImpl.makeJavaElementType(this.getDocument(), var4);
               }

               if (var5 != null && ((SVGElementImpl)var5).isUsed()) {
                  var6 = (SVGElementImpl)((SVGElementImpl)var5).getUsedFromElement();
               }

               while(var5 != null) {
                  ((DocumentImpl)this.getDocument()).handleEvent(new EventImpl("click", var5, false));
                  ((DocumentImpl)this.getDocument()).handleEvent(new EventImpl("DOMActivate", var5, false));
                  var5 = (SVGElement)var5.getParentNode();
               }

               if (var6 != null) {
                  ((DocumentImpl)this.getDocument()).handleEvent(new EventImpl("click", var6, true));
                  ((DocumentImpl)this.getDocument()).handleEvent(new EventImpl("DOMActivate", var6, true));
               }

            }
         } else {
            throw new IllegalArgumentException();
         }
      } else {
         throw new NullPointerException();
      }
   }

   public void activate() {
      SVGElement var1;
      if ((var1 = this.d) != null) {
         _activate(((DocumentImpl)this.getDocument()).getDocumentHandle());

         while(var1 != null) {
            ((DocumentImpl)this.getDocument()).handleEvent(new EventImpl("DOMActivate", var1, false));
            var1 = (SVGElement)var1.getParentNode();
         }
      }

   }

   public void focusOn(SVGElement var1) throws DOMException {
      SVGElement var2 = var1;
      SVGElement var3 = this.d;
      boolean var4 = true;
      if (var1 != null) {
         SVGElement var5;
         for(var5 = var1; var5 != null && !var5.getLocalName().equals("svg"); var5 = (SVGElement)var5.getParentNode()) {
         }

         if (var5 == null) {
            throw new DOMException((short)4, " Element not belong to any document");
         }
      }

      if (var1 != null) {
         _focusOn(((DocumentImpl)this.getDocument()).getDocumentHandle(), ((SVGElementImpl)var1).getHandle());
      } else {
         _focusOn(((DocumentImpl)this.getDocument()).getDocumentHandle(), 0);
      }

      if (this.d == var1) {
         var4 = false;
      }

      if (var4) {
         while(var3 != null) {
            ((DocumentImpl)this.getDocument()).handleEvent(new EventImpl("DOMFocusOut", var3, false));
            var3 = (SVGElement)var3.getParentNode();
         }
      }

      while(var2 != null) {
         ((DocumentImpl)this.getDocument()).handleEvent(new EventImpl("DOMFocusIn", var2, false));
         var2 = (SVGElement)var2.getParentNode();
      }

      this.d = var1;
   }

   public void incrementTime(float var1) {
      ((SVGSVGElementImpl)this.c.getDocumentElement()).incrementTime(var1);
   }

   public static ScalableImage createImage(InputStream var0, ExternalResourceHandler var1) throws IOException {
      return a(var0, var1, (String)null);
   }

   private static ScalableImage a(InputStream var0, ExternalResourceHandler var1, String var2) throws IOException {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         if (var1 == null) {
            var1 = new SVGImage.DefaultExternalResourceHandler();
         }

         StringBuffer var3 = new StringBuffer();
         boolean var4 = false;

         int var11;
         while((var11 = var0.read()) != -1) {
            var3.append((char)var11);
         }

         var0.close();
         SVGImage var6 = new SVGImage();
         int var7;
         if ((var7 = loadStream(var3.toString(), var3.length(), DocumentImpl.getSvgEngineHandle(), var2)) == 0) {
            throw new IOException();
         } else {
            var6.c = new DocumentImpl(var7, var6);
            var6.c.setResourceHandler((ExternalResourceHandler)var1);
            SVGElement var8;
            String var9;
            if ((var9 = (var8 = (SVGElement)var6.getDocument().getDocumentElement()).getTrait("version")) != null && !var9.equals("1.1")) {
               throw new IOException();
            } else {
               int var10 = _getExternalListSize(var7 = ((SVGSVGElementImpl)var8).getDocumentHandle());

               for(var11 = 0; var11 < var10; ++var11) {
                  String var5 = _getExternalListItem(var7, var11);
                  if (var1 != null && var5 != null) {
                     ((ExternalResourceHandler)var1).requestResource(var6, var5);
                  }
               }

               return var6;
            }
         }
      }
   }

   public static ScalableImage createImage(String var0, ExternalResourceHandler var1) throws IOException {
      if (var0 == null) {
         throw new NullPointerException();
      } else if (var0.length() == 0) {
         throw new IllegalArgumentException();
      } else {
         ScalableImage var2 = null;
         StreamConnection var3 = null;
         InputStream var4 = null;

         try {
            if ((var3 = (StreamConnection)Connector.open(var0)) instanceof HttpConnection && ((HttpConnection)var3).getResponseCode() != 200) {
               throw new IOException();
            }

            var2 = a(var4 = var3.openInputStream(), var1, var0.substring(0, var0.lastIndexOf(47) + 1));
         } catch (IllegalArgumentException var8) {
            throw new IllegalArgumentException();
         } catch (Exception var9) {
            throw new IOException();
         } finally {
            if (var4 != null) {
               var4.close();
            }

            if (var3 != null) {
               var3.close();
            }

         }

         return var2;
      }
   }

   public void setViewportWidth(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException();
      } else {
         synchronized(b) {
            _setViewportWidth(((SVGSVGElementImpl)this.c.getDocumentElement()).getDocumentHandle(), var1);
            this.a = true;
         }
      }
   }

   public void setViewportHeight(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException();
      } else {
         synchronized(b) {
            _setViewportHeight(((SVGSVGElementImpl)this.c.getDocumentElement()).getDocumentHandle(), var1);
            this.a = true;
         }
      }
   }

   public int getViewportWidth() {
      return _getViewportWidth(this.c.getDocumentHandle());
   }

   public int getViewportHeight() {
      return _getViewportHeight(this.c.getDocumentHandle());
   }

   public void requestCompleted(String var1, InputStream var2) throws IOException {
      boolean var3 = false;
      byte[] var4 = new byte[0];
      if (var2 != null) {
         byte[] var5 = new byte[1024];

         for(int var7 = var2.read(var5); var7 != -1; var7 = var2.read(var5)) {
            byte[] var6 = var4;
            var4 = new byte[var4.length + var7];
            System.arraycopy(var6, 0, var4, 0, var6.length);
            System.arraycopy(var5, 0, var4, var6.length, var7);
         }

         var2.close();
      }

      if (0 == _requestCompleted(this.c.getDocumentHandle(), var1, var1.length(), var4, var4.length)) {
         throw new IOException();
      }
   }

   private static native int _getViewportHeight(int var0);

   private static native int _getViewportWidth(int var0);

   private static native void _setViewportHeight(int var0, int var1);

   private static native void _setViewportWidth(int var0, int var1);

   private static native int loadStream(String var0, int var1, int var2, String var3);

   private static native void _focusOn(int var0, int var1);

   private static native void _activate(int var0);

   private static native int _dispatchMouseEvent(int var0, int var1, int var2);

   private static native int _requestCompleted(int var0, String var1, int var2, byte[] var3, int var4);

   private static native String _getExternalListItem(int var0, int var1);

   private static native int _getExternalListSize(int var0);

   private static class DefaultExternalResourceHandler implements ExternalResourceHandler {
      DefaultExternalResourceHandler() {
      }

      public void requestResource(ScalableImage var1, String var2) {
         InputStream var21 = null;
         StreamConnection var3 = null;
         boolean var12 = false;

         label186: {
            label174: {
               try {
                  var12 = true;
                  if (var2.indexOf(58) < 0) {
                     var21 = Runtime.getRuntime().getClass().getResourceAsStream(var2);
                     var1.requestCompleted(var2, var21);
                     var12 = false;
                     break label186;
                  }

                  try {
                     if ((var3 = (StreamConnection)Connector.open(var2)) instanceof HttpConnection && ((HttpConnection)var3).getResponseCode() != 200) {
                        throw new IOException();
                     }

                     if (var3 != null) {
                        var21 = var3.openInputStream();
                     }
                  } catch (Exception var18) {
                  }

                  var1.requestCompleted(var2, var21);
                  var12 = false;
                  break label174;
               } catch (IOException var19) {
                  try {
                     var1.requestCompleted(var2, (InputStream)null);
                     var12 = false;
                  } catch (IOException var16) {
                     var12 = false;
                  }
               } finally {
                  if (var12) {
                     try {
                        if (var21 != null) {
                           var21.close();
                        }

                        if (var3 != null) {
                           var3.close();
                        }
                     } catch (IOException var13) {
                     }

                  }
               }

               try {
                  if (var21 != null) {
                     var21.close();
                  }

                  if (var3 != null) {
                     var3.close();
                  }

                  return;
               } catch (IOException var15) {
                  return;
               }
            }

            try {
               if (var21 != null) {
                  var21.close();
               }

               if (var3 != null) {
                  var3.close();
               }
            } catch (IOException var17) {
               return;
            }

            return;
         }

         try {
            if (var21 != null) {
               var21.close();
            }

         } catch (IOException var14) {
         }
      }
   }
}
