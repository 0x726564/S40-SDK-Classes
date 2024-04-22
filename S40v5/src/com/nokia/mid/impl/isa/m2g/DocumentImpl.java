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
   private int jD = 0;
   private SVGSVGElementImpl jE;
   private String jF = "http://www.w3.org/2000/svg";
   private String localName = null;
   private Hashtable jG = null;
   private Hashtable jH = null;
   private Hashtable jI = null;
   private Hashtable jJ = null;
   private EventTarget jK = null;
   private static int jL = 0;
   private static Hashtable jM = null;
   private static String jN = " rect circle ellipse line path use image text a g ";
   protected static String qualsLocatable;
   protected static String qualsAnimatable;

   public static int getSvgEngineHandle() {
      return jL;
   }

   public static void setSvgEngineHandle(int var0) {
      jL = var0;
   }

   public DocumentImpl(int var1, SVGImage var2) {
      this.jD = var1;
      this.mySVGImage = var2;
      this.jE = new SVGSVGElementImpl(this);
      jM = new Hashtable();
      this.jG = new Hashtable();
      this.jH = new Hashtable();
      this.jI = new Hashtable();
      this.jJ = new Hashtable();
      this._registerToFinalize();
   }

   public int getDocumentHandle() {
      return this.jD;
   }

   public Element createElementNS(String var1, String var2) {
      if (var1 != null && var2 != null && !var1.equals("") && !var2.equals("")) {
         int var3 = jN.indexOf(" " + var2 + " ");
         if (var1.equals(this.jF) && var3 != -1) {
            SVGLocatableElementImpl var4 = new SVGLocatableElementImpl(this, _createElement(this.jD, SVGElementImpl.stringToEnumElement(var2)));
            jM.put(new Integer(var4.getHandle()), var4);
            return var4;
         } else {
            throw new DOMException((short)9, "Unsupported URI or element");
         }
      } else {
         throw new NullPointerException();
      }
   }

   public Element getDocumentElement() {
      return this.jE;
   }

   public Element getElementById(String var1) {
      if (var1 != null && !var1.equals("")) {
         int var2;
         return (var2 = _getElementById(this.jD, var1)) == 0 ? null : makeJavaElementType(this, var2);
      } else {
         throw new NullPointerException();
      }
   }

   public static SVGElement makeJavaElementType(Document var0, int var1) {
      String var3 = SVGElementImpl.enumToStringElement(SVGElementImpl._getType(var1));
      Object var2;
      if (jM.containsKey(new Integer(var1))) {
         var2 = (SVGElement)jM.get(new Integer(var1));
      } else {
         if (var3.equals("svg")) {
            var2 = new SVGSVGElementImpl(var0);
         } else if (qualsLocatable.indexOf(" " + var3 + " ") != -1) {
            var2 = new SVGLocatableElementImpl(var0, var1);
         } else if (qualsAnimatable.indexOf(" " + var3 + " ") != -1) {
            var2 = new SVGAnimationElementImpl(var0, var1);
         } else {
            String var4;
            if ((var4 = SVGElementImpl._getStringTrait(var1, (short)90)) != null && var4.equals("text_use_svg_default_font")) {
               return makeJavaElementType(var0, SVGElementImpl._getNextElementSibling(var1));
            }

            var2 = new SVGElementImpl(var0, var1);
         }

         jM.put(new Integer(var1), var2);
      }

      return (SVGElement)var2;
   }

   public String getLocalName() {
      return this == this.getDocumentElement() ? "#document" : null;
   }

   public String getNamespaceURI() {
      return null;
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
      if (!var4 || this.jK != var1) {
         Hashtable var5;
         if (var2.equals("DOMActivate")) {
            if (this.jG == null) {
               this.jG = new Hashtable();
            }

            var5 = this.jG;
         } else if (var2.equals("DOMFocusIn")) {
            if (this.jH == null) {
               this.jH = new Hashtable();
            }

            var5 = this.jH;
         } else if (var2.equals("DOMFocusOut")) {
            if (this.jI == null) {
               this.jI = new Hashtable();
            }

            var5 = this.jI;
         } else {
            if (!var2.equals("click")) {
               return;
            }

            if (this.jJ == null) {
               this.jJ = new Hashtable();
            }

            var5 = this.jJ;
         }

         Vector var6 = null;
         var6 = null;
         if (var4) {
            if (!var5.containsKey(var1)) {
               (var6 = new Vector()).addElement(var3);
               var5.put(var1, var6);
               return;
            }

            if (!(var6 = (Vector)var5.get(var1)).contains(var3)) {
               var6.addElement(var3);
               return;
            }
         } else if (var5.get(var1) != null) {
            ((Vector)var5.get(var1)).removeElement(var3);
         }

      }
   }

   public void handleEvent(Event var1) {
      Hashtable var2;
      if (var1.getType().equals("DOMActivate")) {
         var2 = this.jG;
      } else if (var1.getType().equals("DOMFocusIn")) {
         var2 = this.jH;
      } else if (var1.getType().equals("DOMFocusOut")) {
         var2 = this.jI;
      } else {
         if (!var1.getType().equals("click")) {
            throw new IllegalArgumentException("this type of event is not supported by svg");
         }

         var2 = this.jJ;
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

         this.jK = var4;
         Enumeration var6 = ((Vector)var2.get(var4)).elements();

         while(var6.hasMoreElements()) {
            ((EventListener)var6.nextElement()).handleEvent(var1);
         }

         this.jK = null;
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
      qualsLocatable = jN + "svg ";
      qualsAnimatable = " animate animateColor animateMotion animateTransform mpath set ";
   }
}
