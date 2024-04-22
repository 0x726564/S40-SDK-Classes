package org.w3c.dom.svg;

import org.w3c.dom.DOMException;

public interface SVGSVGElement extends SVGLocatableElement {
   void setCurrentScale(float var1) throws DOMException;

   float getCurrentScale();

   void setCurrentRotate(float var1);

   float getCurrentRotate();

   SVGPoint getCurrentTranslate();

   float getCurrentTime();

   void setCurrentTime(float var1);

   SVGMatrix createSVGMatrixComponents(float var1, float var2, float var3, float var4, float var5, float var6);

   SVGRect createSVGRect();

   SVGPath createSVGPath();

   SVGRGBColor createSVGRGBColor(int var1, int var2, int var3) throws SVGException;
}
