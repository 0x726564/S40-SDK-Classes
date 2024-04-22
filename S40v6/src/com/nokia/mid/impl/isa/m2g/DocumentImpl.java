package com.nokia.mid.impl.isa.m2g;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.m2g.ExternalResourceHandler;
import javax.microedition.m2g.SVGImage;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.svg.SVGElement;

public class DocumentImpl implements Document {
   protected ExternalResourceHandler rHandler;
   protected SVGImage mySVGImage;
   private int hSvgDocument = 0;
   private SVGSVGElementImpl root;
   private String namespace = "http://www.w3.org/2000/svg";
   private String localName = null;
   private Hashtable activateMe = null;
   private Hashtable focusMe = null;
   private Hashtable unfocusMe = null;
   private Hashtable clickMe = null;
   private EventTarget currentProcessedTarget = null;
   private static int hSvgEngine = 0;
   private static Hashtable elementsInJava = null;
   private static String qualsCreatable = " rect circle ellipse line path use image text a g ";
   protected static String qualsLocatable;
   protected static String qualsAnimatable;

   public static int getSvgEngineHandle() {
      return hSvgEngine;
   }

   public static void setSvgEngineHandle(int newHandle) {
      hSvgEngine = newHandle;
   }

   public DocumentImpl(int docHandle, SVGImage image) {
      this.hSvgDocument = docHandle;
      this.mySVGImage = image;
      this.root = new SVGSVGElementImpl(this);
      elementsInJava = new Hashtable();
      this.activateMe = new Hashtable();
      this.focusMe = new Hashtable();
      this.unfocusMe = new Hashtable();
      this.clickMe = new Hashtable();
      this._registerToFinalize();
   }

   public int getDocumentHandle() {
      return this.hSvgDocument;
   }

   public Element createElementNS(String namespaceURI, String qualifiedName) {
      if (namespaceURI != null && qualifiedName != null && !namespaceURI.equals("") && !qualifiedName.equals("")) {
         int aType = qualsCreatable.indexOf(" " + qualifiedName + " ");
         if (namespaceURI.equals(this.namespace) && aType != -1) {
            SVGLocatableElementImpl retVal = new SVGLocatableElementImpl(this, _createElement(this.hSvgDocument, SVGElementImpl.stringToEnumElement(qualifiedName)));
            elementsInJava.put(new Integer(retVal.getHandle()), retVal);
            return retVal;
         } else {
            throw new DOMException((short)9, "Unsupported URI or element");
         }
      } else {
         throw new NullPointerException();
      }
   }

   public Element getDocumentElement() {
      return this.root;
   }

   public Element getElementById(String id) {
      if (id != null && !id.equals("")) {
         int retHandle = _getElementById(this.hSvgDocument, id);
         return retHandle == 0 ? null : makeJavaElementType(this, retHandle);
      } else {
         throw new NullPointerException();
      }
   }

   public static SVGElement makeJavaElementType(Document doc, int elHandle) {
      String aType = SVGElementImpl.enumToStringElement(SVGElementImpl._getType(elHandle));
      Object retVal;
      if (elementsInJava.containsKey(new Integer(elHandle))) {
         retVal = (SVGElement)elementsInJava.get(new Integer(elHandle));
      } else {
         if (aType.equals("svg")) {
            retVal = new SVGSVGElementImpl(doc);
         } else if (qualsLocatable.indexOf(" " + aType + " ") != -1) {
            retVal = new SVGLocatableElementImpl(doc, elHandle);
         } else if (qualsAnimatable.indexOf(" " + aType + " ") != -1) {
            retVal = new SVGAnimationElementImpl(doc, elHandle);
         } else {
            String id = SVGElementImpl._getStringTrait(elHandle, (short)90);
            if (id != null && id.equals("text_use_svg_default_font")) {
               return makeJavaElementType(doc, SVGElementImpl._getNextElementSibling(elHandle));
            }

            retVal = new SVGElementImpl(doc, elHandle);
         }

         elementsInJava.put(new Integer(elHandle), retVal);
      }

      return (SVGElement)retVal;
   }

   public String getLocalName() {
      return this == this.getDocumentElement() ? "#document" : this.localName;
   }

   public String getNamespaceURI() {
      return null;
   }

   public Node getParentNode() {
      return null;
   }

   public Node appendChild(Node newChild) throws DOMException {
      throw new DOMException((short)3, "Cannot appendChild to a Document node");
   }

   public Node removeChild(Node oldChild) throws DOMException {
      throw new DOMException((short)9, "Cannot removeChild from a Document node");
   }

   public Node insertBefore(Node newChild, Node refChild) {
      throw new DOMException((short)3, "Cannot insertBefore a Document node");
   }

   protected void register(EventTarget element, String type, EventListener listener, boolean add) {
      if (!add || this.currentProcessedTarget != element) {
         Hashtable var5;
         if (type.equals("DOMActivate")) {
            if (this.activateMe == null) {
               this.activateMe = new Hashtable();
            }

            var5 = this.activateMe;
         } else if (type.equals("DOMFocusIn")) {
            if (this.focusMe == null) {
               this.focusMe = new Hashtable();
            }

            var5 = this.focusMe;
         } else if (type.equals("DOMFocusOut")) {
            if (this.unfocusMe == null) {
               this.unfocusMe = new Hashtable();
            }

            var5 = this.unfocusMe;
         } else {
            if (!type.equals("click")) {
               return;
            }

            if (this.clickMe == null) {
               this.clickMe = new Hashtable();
            }

            var5 = this.clickMe;
         }

         Vector listenersAdd = null;
         Vector listenersRemove = null;
         if (add) {
            if (var5.containsKey(element)) {
               listenersAdd = (Vector)var5.get(element);
               if (!listenersAdd.contains(listener)) {
                  listenersAdd.addElement(listener);
               }
            } else {
               Vector newListenerList = new Vector();
               newListenerList.addElement(listener);
               var5.put(element, newListenerList);
            }
         } else if (var5.get(element) != null) {
            listenersRemove = (Vector)var5.get(element);
            listenersRemove.removeElement(listener);
         }

      }
   }

   public void handleEvent(Event evt) {
      Hashtable var2;
      if (evt.getType().equals("DOMActivate")) {
         var2 = this.activateMe;
      } else if (evt.getType().equals("DOMFocusIn")) {
         var2 = this.focusMe;
      } else if (evt.getType().equals("DOMFocusOut")) {
         var2 = this.unfocusMe;
      } else {
         if (!evt.getType().equals("click")) {
            throw new IllegalArgumentException("this type of event is not supported by svg");
         }

         var2 = this.clickMe;
      }

      Enumeration myEventTargets = var2.keys();

      while(true) {
         EventTarget target;
         do {
            if (!myEventTargets.hasMoreElements()) {
               return;
            }

            target = (EventTarget)myEventTargets.nextElement();
         } while(!((EventImpl)evt).getListenerTarget().equals(target));

         this.currentProcessedTarget = target;
         Vector listeners = (Vector)var2.get(target);
         Enumeration e = listeners.elements();

         while(e.hasMoreElements()) {
            EventListener listener = (EventListener)e.nextElement();
            listener.handleEvent(evt);
         }

         this.currentProcessedTarget = null;
      }
   }

   public void setResourceHandler(ExternalResourceHandler handler) {
      this.rHandler = handler;
   }

   public void invokeResourceHandler(String uri) {
      if (this.mySVGImage != null && this.rHandler != null && uri.length() > 4) {
         this.rHandler.requestResource(this.mySVGImage, uri);
      }

   }

   private static native int _createElement(int var0, short var1);

   private static native int _getElementById(int var0, String var1);

   private native void _registerToFinalize();

   static {
      qualsLocatable = qualsCreatable + "svg ";
      qualsAnimatable = " animate animateColor animateMotion animateTransform mpath set ";
   }
}
