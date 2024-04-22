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

   void applyViewportToSurface(int width, int height) {
      ((SVGSVGElementImpl)this.document.getDocumentElement()).refreshRenderSurface(width, height);
      this.viewPortHasChanged = false;
   }

   boolean viewportHasChanged() {
      return this.viewPortHasChanged;
   }

   public Document getDocument() {
      return this.document;
   }

   public static SVGImage createEmptyImage(ExternalResourceHandler handler) {
      SVGImage mynewImage = new SVGImage();
      if (handler == null) {
         handler = new SVGImage.DefaultExternalResourceHandler();
      }

      String myString = "<svg version=\"1.1\" baseProfile=\"tiny\" width=\"100\" height=\"100\"></svg>";

      try {
         mynewImage = (SVGImage)createImage((InputStream)(new ByteArrayInputStream(myString.getBytes())), (ExternalResourceHandler)handler);
      } catch (IOException var4) {
      }

      return mynewImage;
   }

   public void dispatchMouseEvent(String type, int x, int y) throws DOMException {
      if (type != null && !type.equals("")) {
         if (x >= 0 && y >= 0) {
            if (!type.equals("click")) {
               throw new DOMException((short)9, "Only supports mouseevent click");
            } else {
               int docHandle = ((DocumentImpl)this.getDocument()).getDocumentHandle();
               int elHandle = _dispatchMouseEvent(docHandle, x, y);
               SVGElement targetElement = null;
               SVGElement elementInstance = null;
               if (elHandle != 0) {
                  targetElement = DocumentImpl.makeJavaElementType(this.getDocument(), elHandle);
               }

               if (targetElement != null && ((SVGElementImpl)targetElement).isUsed()) {
                  elementInstance = (SVGElementImpl)((SVGElementImpl)targetElement).getUsedFromElement();
               }

               while(targetElement != null) {
                  ((DocumentImpl)this.getDocument()).handleEvent(new EventImpl("click", targetElement, false));
                  ((DocumentImpl)this.getDocument()).handleEvent(new EventImpl("DOMActivate", targetElement, false));
                  targetElement = (SVGElement)targetElement.getParentNode();
               }

               if (elementInstance != null) {
                  ((DocumentImpl)this.getDocument()).handleEvent(new EventImpl("click", elementInstance, true));
                  ((DocumentImpl)this.getDocument()).handleEvent(new EventImpl("DOMActivate", elementInstance, true));
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
      SVGElement tempNode = this.focused;
      if (tempNode != null) {
         int i = 0;
         _activate(((DocumentImpl)this.getDocument()).getDocumentHandle());

         while(tempNode != null) {
            ((DocumentImpl)this.getDocument()).handleEvent(new EventImpl("DOMActivate", tempNode, false));
            tempNode = (SVGElement)tempNode.getParentNode();
            ++i;
         }
      }

   }

   public void focusOn(SVGElement element) throws DOMException {
      SVGElement newFocus = element;
      SVGElement tempFocused = this.focused;
      boolean callFocusOut = true;
      if (element != null) {
         SVGElement root;
         for(root = element; root != null && !root.getLocalName().equals("svg"); root = (SVGElement)root.getParentNode()) {
         }

         if (root == null) {
            throw new DOMException((short)4, " Element not belong to any document");
         }
      }

      if (element != null) {
         _focusOn(((DocumentImpl)this.getDocument()).getDocumentHandle(), ((SVGElementImpl)element).getHandle());
      } else {
         _focusOn(((DocumentImpl)this.getDocument()).getDocumentHandle(), 0);
      }

      if (this.focused == element) {
         callFocusOut = false;
      }

      if (callFocusOut) {
         while(tempFocused != null) {
            ((DocumentImpl)this.getDocument()).handleEvent(new EventImpl("DOMFocusOut", tempFocused, false));
            tempFocused = (SVGElement)tempFocused.getParentNode();
         }
      }

      while(newFocus != null) {
         ((DocumentImpl)this.getDocument()).handleEvent(new EventImpl("DOMFocusIn", newFocus, false));
         newFocus = (SVGElement)newFocus.getParentNode();
      }

      this.focused = element;
   }

   public void incrementTime(float seconds) {
      ((SVGSVGElementImpl)this.document.getDocumentElement()).incrementTime(seconds);
   }

   public static ScalableImage createImage(InputStream stream, ExternalResourceHandler handler) throws IOException {
      return createImage(stream, handler, (String)null);
   }

   private static ScalableImage createImage(InputStream stream, ExternalResourceHandler handler, String documentURI) throws IOException {
      if (stream == null) {
         throw new NullPointerException();
      } else {
         if (handler == null) {
            handler = new SVGImage.DefaultExternalResourceHandler();
         }

         StringBuffer svgStringBuffer = new StringBuffer();
         boolean var4 = false;

         int ch;
         while((ch = stream.read()) != -1) {
            svgStringBuffer.append((char)ch);
         }

         stream.close();
         SVGImage returnValue = new SVGImage();
         int docHandle = loadStream(svgStringBuffer.toString(), svgStringBuffer.length(), DocumentImpl.getSvgEngineHandle(), documentURI);
         if (docHandle == 0) {
            throw new IOException();
         } else {
            returnValue.document = new DocumentImpl(docHandle, returnValue);
            returnValue.document.setResourceHandler((ExternalResourceHandler)handler);
            SVGElement root = (SVGElement)returnValue.getDocument().getDocumentElement();
            String version = root.getTrait("version");
            if (version != null && !version.equals("1.1")) {
               throw new IOException();
            } else {
               int documentHandle = ((SVGSVGElementImpl)root).getDocumentHandle();
               int listSize = _getExternalListSize(documentHandle);

               for(int i = 0; i < listSize; ++i) {
                  String tempURI = _getExternalListItem(documentHandle, i);
                  if (handler != null && tempURI != null) {
                     ((ExternalResourceHandler)handler).requestResource(returnValue, tempURI);
                  }
               }

               return returnValue;
            }
         }
      }
   }

   public static ScalableImage createImage(String url, ExternalResourceHandler handler) throws IOException {
      if (url == null) {
         throw new NullPointerException();
      } else if (url.length() == 0) {
         throw new IllegalArgumentException();
      } else {
         ScalableImage returnValue = null;
         StreamConnection con = null;
         InputStream in = null;

         try {
            con = (StreamConnection)Connector.open(url);
            if (con instanceof HttpConnection && ((HttpConnection)con).getResponseCode() != 200) {
               throw new IOException();
            }

            in = con.openInputStream();
            returnValue = createImage(in, handler, url.substring(0, url.lastIndexOf(47) + 1));
         } catch (IllegalArgumentException var11) {
            throw new IllegalArgumentException();
         } catch (Exception var12) {
            throw new IOException();
         } finally {
            if (in != null) {
               in.close();
            }

            if (con != null) {
               con.close();
            }

         }

         return returnValue;
      }
   }

   public void setViewportWidth(int width) {
      if (width < 0) {
         throw new IllegalArgumentException();
      } else {
         synchronized(svgLock) {
            _setViewportWidth(((SVGSVGElementImpl)this.document.getDocumentElement()).getDocumentHandle(), width);
            this.viewPortHasChanged = true;
         }
      }
   }

   public void setViewportHeight(int height) {
      if (height < 0) {
         throw new IllegalArgumentException();
      } else {
         synchronized(svgLock) {
            _setViewportHeight(((SVGSVGElementImpl)this.document.getDocumentElement()).getDocumentHandle(), height);
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

   public void requestCompleted(String URI, InputStream resourceData) throws IOException {
      int status = false;
      byte[] mainBuffer = new byte[0];
      if (resourceData != null) {
         byte[] buffer = new byte[1024];

         for(int status = resourceData.read(buffer); status != -1; status = resourceData.read(buffer)) {
            byte[] oldBuffer = mainBuffer;
            mainBuffer = new byte[mainBuffer.length + status];
            System.arraycopy(oldBuffer, 0, mainBuffer, 0, oldBuffer.length);
            System.arraycopy(buffer, 0, mainBuffer, oldBuffer.length, status);
         }

         resourceData.close();
      }

      if (0 == _requestCompleted(this.document.getDocumentHandle(), URI, URI.length(), mainBuffer, mainBuffer.length)) {
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

      public void requestResource(ScalableImage image, String URL) {
         InputStream in = null;
         StreamConnection con = null;

         try {
            if (URL.indexOf(58) >= 0) {
               try {
                  con = (StreamConnection)Connector.open(URL);
                  if (con instanceof HttpConnection && ((HttpConnection)con).getResponseCode() != 200) {
                     throw new IOException();
                  }

                  if (con != null) {
                     in = con.openInputStream();
                  }
               } catch (Exception var20) {
               }

               image.requestCompleted(URL, in);
               return;
            }

            in = Runtime.getRuntime().getClass().getResourceAsStream(URL);
            image.requestCompleted(URL, in);
         } catch (IOException var21) {
            try {
               image.requestCompleted(URL, (InputStream)null);
            } catch (IOException var19) {
            }

            return;
         } finally {
            try {
               if (in != null) {
                  in.close();
               }

               if (con != null) {
                  con.close();
               }
            } catch (IOException var18) {
            }

         }

      }
   }
}
