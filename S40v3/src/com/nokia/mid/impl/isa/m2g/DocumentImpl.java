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

   public static void setSvgEngineHandle(int var0) {
      hSvgEngine = var0;
   }

   public DocumentImpl(int var1, SVGImage var2) {
      this.hSvgDocument = var1;
      this.mySVGImage = var2;
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

   public Element createElementNS(String var1, String var2) {
      if (var1 != null && var2 != null && !var1.equals("") && !var2.equals("")) {
         int var3 = qualsCreatable.indexOf(" " + var2 + " ");
         if (var1.equals(this.namespace) && var3 != -1) {
            SVGLocatableElementImpl var4 = new SVGLocatableElementImpl(this, _createElement(this.hSvgDocument, SVGElementImpl.stringToEnumElement(var2)));
            SVGLocatableElementImpl var10000 = (SVGLocatableElementImpl)elementsInJava.put(new Integer(var4.getHandle()), var4);
            return var4;
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

   public Element getElementById(String var1) {
      if (var1 != null && !var1.equals("")) {
         int var2 = _getElementById(this.hSvgDocument, var1);
         return var2 == 0 ? null : makeJavaElementType(this, var2);
      } else {
         throw new NullPointerException();
      }
   }

   public static SVGElement makeJavaElementType(Document var0, int var1) {
      String var4 = SVGElementImpl.enumToStringElement(SVGElementImpl._getType(var1));
      Object var2;
      if (elementsInJava.containsKey(new Integer(var1))) {
         var2 = (SVGElement)elementsInJava.get(new Integer(var1));
      } else {
         if (var4.equals("svg")) {
            var2 = new SVGSVGElementImpl(var0);
         } else if (qualsLocatable.indexOf(" " + var4 + " ") != -1) {
            var2 = new SVGLocatableElementImpl(var0, var1);
         } else if (qualsAnimatable.indexOf(" " + var4 + " ") != -1) {
            var2 = new SVGAnimationElementImpl(var0, var1);
         } else {
            String var5 = SVGElementImpl._getStringTrait(var1, (short)90);
            if (var5 != null && var5.equals("text_use_svg_default_font")) {
               return makeJavaElementType(var0, SVGElementImpl._getNextElementSibling(var1));
            }

            var2 = new SVGElementImpl(var0, var1);
         }

         SVGElement var10000 = (SVGElement)elementsInJava.put(new Integer(var1), var2);
      }

      return (SVGElement)var2;
   }

   public String getLocalName() {
      return this == this.getDocumentElement() ? "#document" : this.localName;
   }

   public String getNamespaceURI() {
      return this.namespace;
   }

   public Node getParentNode() {
      return null;
   }

   public Node appendChild(Node var1) throws DOMException {
      throw new DOMException((short)3, "Cannot appendChild to a Document node");
   }

   public Node removeChild(Node var1) throws DOMException {
      throw new DOMException((short)9, "Cannot removeChild from a Document node");
   }

   public Node insertBefore(Node var1, Node var2) {
      throw new DOMException((short)3, "Cannot insertBefore a Document node");
   }

   protected void register(EventTarget var1, String var2, EventListener var3, boolean var4) {
      if (!var4 || this.currentProcessedTarget != var1) {
         Hashtable var5;
         if (var2.equals("DOMActivate")) {
            if (this.activateMe == null) {
               this.activateMe = new Hashtable();
            }

            var5 = this.activateMe;
         } else if (var2.equals("DOMFocusIn")) {
            if (this.focusMe == null) {
               this.focusMe = new Hashtable();
            }

            var5 = this.focusMe;
         } else if (var2.equals("DOMFocusOut")) {
            if (this.unfocusMe == null) {
               this.unfocusMe = new Hashtable();
            }

            var5 = this.unfocusMe;
         } else {
            if (!var2.equals("click")) {
               return;
            }

            if (this.clickMe == null) {
               this.clickMe = new Hashtable();
            }

            var5 = this.clickMe;
         }

         Vector var6 = null;
         Vector var7 = null;
         if (var4) {
            if (var5.containsKey(var1)) {
               var6 = (Vector)var5.get(var1);
               if (!var6.contains(var3)) {
                  var6.addElement(var3);
               }
            } else {
               Vector var8 = new Vector();
               var8.addElement(var3);
               Vector var9 = (Vector)var5.put(var1, var8);
            }
         } else if (var5.get(var1) != null) {
            var7 = (Vector)var5.get(var1);
            var7.removeElement(var3);
         }

      }
   }

   public void handleEvent(Event var1) {
      Hashtable var2;
      if (var1.getType().equals("DOMActivate")) {
         var2 = this.activateMe;
      } else if (var1.getType().equals("DOMFocusIn")) {
         var2 = this.focusMe;
      } else if (var1.getType().equals("DOMFocusOut")) {
         var2 = this.unfocusMe;
      } else {
         if (!var1.getType().equals("click")) {
            throw new IllegalArgumentException("this type of event is not supported by svg");
         }

         var2 = this.clickMe;
      }

      Enumeration var3 = var2.keys();

      while(true) {
         EventTarget var4;
         do {
            if (!var3.hasMoreElements()) {
               return;
            }

            var4 = (EventTarget)var3.nextElement();
         } while(!((EventImpl)var1).getListenerTarget().equals(var4));

         this.currentProcessedTarget = var4;
         Vector var5 = (Vector)var2.get(var4);
         Enumeration var6 = var5.elements();

         while(var6.hasMoreElements()) {
            EventListener var7 = (EventListener)var6.nextElement();
            var7.handleEvent(var1);
         }

         this.currentProcessedTarget = null;
      }
   }

   public void setResourceHandler(ExternalResourceHandler var1) {
      this.rHandler = var1;
   }

   public void invokeResourceHandler(String var1) {
      if (this.mySVGImage != null && this.rHandler != null && var1.length() > 4) {
         this.rHandler.requestResource(this.mySVGImage, var1);
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
