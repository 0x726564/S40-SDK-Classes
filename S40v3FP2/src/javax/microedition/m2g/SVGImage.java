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
   private boolean viewPortHasChanged = false;
   static Object svgLock = new Object();
   private DocumentImpl document = null;
   private static ExternalResourceHandler rsrcHandler;
   private SVGElement focused = null;

   protected SVGImage() {
   }

   void applyViewportToSurface(int var1, int var2) {
      ((SVGSVGElementImpl)this.document.getDocumentElement()).refreshRenderSurface(var1, var2);
      this.viewPortHasChanged = false;
   }

   boolean viewportHasChanged() {
      return this.viewPortHasChanged;
   }

   public Document getDocument() {
      return this.document;
   }

   public static SVGImage createEmptyImage(ExternalResourceHandler var0) {
      SVGImage var1 = new SVGImage();
      if (var0 == null) {
         var0 = new SVGImage.DefaultExternalResourceHandler();
      }

      String var2 = "<svg version=\"1.1\" baseProfile=\"tiny\" width=\"100\" height=\"100\"></svg>";

      try {
         var1 = (SVGImage)createImage((InputStream)(new ByteArrayInputStream(var2.getBytes())), (ExternalResourceHandler)var0);
      } catch (IOException var4) {
      }

      return var1;
   }

   public void dispatchMouseEvent(String var1, int var2, int var3) throws DOMException {
      if (var1 != null && !var1.equals("")) {
         if (var2 >= 0 && var3 >= 0) {
            if (!var1.equals("click")) {
               throw new DOMException((short)9, "Only supports mouseevent click");
            } else {
               int var4 = ((DocumentImpl)this.getDocument()).getDocumentHandle();
               int var5 = _dispatchMouseEvent(var4, var2, var3);
               SVGElement var6 = null;
               SVGElementImpl var7 = null;
               if (var5 != 0) {
                  DocumentImpl var10000 = (DocumentImpl)this.getDocument();
                  var6 = DocumentImpl.makeJavaElementType(this.getDocument(), var5);
               }

               if (var6 != null && ((SVGElementImpl)var6).isUsed()) {
                  var7 = (SVGElementImpl)((SVGElementImpl)var6).getUsedFromElement();
               }

               while(var6 != null) {
                  ((DocumentImpl)this.getDocument()).handleEvent(new EventImpl("click", var6, false));
                  ((DocumentImpl)this.getDocument()).handleEvent(new EventImpl("DOMActivate", var6, false));
                  var6 = (SVGElement)var6.getParentNode();
               }

               if (var7 != null) {
                  ((DocumentImpl)this.getDocument()).handleEvent(new EventImpl("click", var7, true));
                  ((DocumentImpl)this.getDocument()).handleEvent(new EventImpl("DOMActivate", var7, true));
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
      SVGElement var1 = this.focused;

      for(int var2 = 0; var1 != null; ++var2) {
         ((DocumentImpl)this.getDocument()).handleEvent(new EventImpl("DOMActivate", var1, false));
         var1 = (SVGElement)var1.getParentNode();
      }

   }

   public void focusOn(SVGElement var1) throws DOMException {
      SVGElement var2 = var1;
      SVGElement var3 = this.focused;
      boolean var4 = true;
      if (var1 != null) {
         SVGElement var5;
         for(var5 = var1; var5 != null && !var5.getLocalName().equals("svg"); var5 = (SVGElement)var5.getParentNode()) {
         }

         if (var5 == null) {
            throw new DOMException((short)4, " Element not belong to any document");
         }
      }

      if (this.focused == var1) {
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

      this.focused = var1;
   }

   public void incrementTime(float var1) {
      ((SVGSVGElementImpl)this.document.getDocumentElement()).incrementTime(var1);
   }

   public static ScalableImage createImage(InputStream var0, ExternalResourceHandler var1) throws IOException {
      return createImage(var0, var1, (String)null);
   }

   private static ScalableImage createImage(InputStream var0, ExternalResourceHandler var1, String var2) throws IOException {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         if (var1 == null) {
            var1 = new SVGImage.DefaultExternalResourceHandler();
         }

         StringBuffer var3 = new StringBuffer();
         boolean var4 = false;

         int var13;
         while((var13 = var0.read()) != -1) {
            var3.append((char)var13);
         }

         var0.close();
         SVGImage var5 = new SVGImage();
         int var6 = loadStream(var3.toString(), var3.length(), DocumentImpl.getSvgEngineHandle(), var2);
         if (var6 == 0) {
            throw new IOException();
         } else {
            var5.document = new DocumentImpl(var6, var5);
            var5.document.setResourceHandler((ExternalResourceHandler)var1);
            SVGElement var7 = (SVGElement)var5.getDocument().getDocumentElement();
            String var8 = var7.getTrait("version");
            if (var8 != null && !var8.equals("1.1")) {
               throw new IOException();
            } else {
               int var9 = ((SVGSVGElementImpl)var7).getDocumentHandle();
               int var10 = _getExternalListSize(var9);

               for(int var11 = 0; var11 < var10; ++var11) {
                  String var12 = _getExternalListItem(var9, var11);
                  if (var1 != null && var12 != null) {
                     ((ExternalResourceHandler)var1).requestResource(var5, var12);
                  }
               }

               return var5;
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
            var3 = (StreamConnection)Connector.open(var0);
            if (var3 instanceof HttpConnection && ((HttpConnection)var3).getResponseCode() != 200) {
               throw new IOException();
            }

            var4 = var3.openInputStream();
            var2 = createImage(var4, var1, var0.substring(0, var0.lastIndexOf(47) + 1));
         } catch (IllegalArgumentException var11) {
            throw new IllegalArgumentException();
         } catch (Exception var12) {
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
         synchronized(svgLock) {
            _setViewportWidth(((SVGSVGElementImpl)this.document.getDocumentElement()).getDocumentHandle(), var1);
            this.viewPortHasChanged = true;
         }
      }
   }

   public void setViewportHeight(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException();
      } else {
         synchronized(svgLock) {
            _setViewportHeight(((SVGSVGElementImpl)this.document.getDocumentElement()).getDocumentHandle(), var1);
            this.viewPortHasChanged = true;
         }
      }
   }

   public int getViewportWidth() {
      return _getViewportWidth(this.document.getDocumentHandle());
   }

   public int getViewportHeight() {
      return _getViewportHeight(this.document.getDocumentHandle());
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

      if (0 == _requestCompleted(this.document.getDocumentHandle(), var1, var1.length(), var4, var4.length)) {
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
         InputStream var3 = null;
         StreamConnection var4 = null;

         try {
            if (var2.indexOf(58) >= 0) {
               try {
                  var4 = (StreamConnection)Connector.open(var2);
                  if (var4 instanceof HttpConnection && ((HttpConnection)var4).getResponseCode() != 200) {
                     throw new IOException();
                  }

                  if (var4 != null) {
                     var3 = var4.openInputStream();
                  }
               } catch (Exception var20) {
               }

               var1.requestCompleted(var2, var3);
               return;
            }

            var3 = Runtime.getRuntime().getClass().getResourceAsStream(var2);
            var1.requestCompleted(var2, var3);
         } catch (IOException var21) {
            try {
               var1.requestCompleted(var2, (InputStream)null);
            } catch (IOException var19) {
            }

            return;
         } finally {
            try {
               if (var3 != null) {
                  var3.close();
               }

               if (var4 != null) {
                  var4.close();
               }
            } catch (IOException var18) {
            }

         }

      }
   }
}
