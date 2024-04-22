package org.w3c.dom.svg;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.events.EventTarget;

public interface SVGElement extends Element, EventTarget {
   void setId(String var1) throws DOMException;

   String getId();

   Element getFirstElementChild();

   Element getNextElementSibling();

   String getTrait(String var1) throws DOMException;

   String getTraitNS(String var1, String var2) throws DOMException;

   float getFloatTrait(String var1) throws DOMException;

   SVGMatrix getMatrixTrait(String var1) throws DOMException;

   SVGRect getRectTrait(String var1) throws DOMException;

   SVGPath getPathTrait(String var1) throws DOMException;

   SVGRGBColor getRGBColorTrait(String var1) throws DOMException;

   void setTrait(String var1, String var2) throws DOMException;

   void setTraitNS(String var1, String var2, String var3) throws DOMException;

   void setFloatTrait(String var1, float var2) throws DOMException;

   void setMatrixTrait(String var1, SVGMatrix var2) throws DOMException;

   void setRectTrait(String var1, SVGRect var2) throws DOMException;

   void setPathTrait(String var1, SVGPath var2) throws DOMException;

   void setRGBColorTrait(String var1, SVGRGBColor var2) throws DOMException;
}
