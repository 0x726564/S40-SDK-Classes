package org.w3c.dom.svg;

public interface SVGLocatableElement extends SVGElement {
   SVGRect getBBox();

   SVGMatrix getScreenCTM();

   SVGRect getScreenBBox();
}
