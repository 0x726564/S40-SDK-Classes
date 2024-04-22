package org.w3c.dom.svg;

import org.w3c.dom.DOMException;

public interface SVGMatrix {
   float getComponent(int var1) throws DOMException;

   SVGMatrix mMultiply(SVGMatrix var1);

   SVGMatrix inverse() throws SVGException;

   SVGMatrix mTranslate(float var1, float var2);

   SVGMatrix mScale(float var1);

   SVGMatrix mRotate(float var1);
}
