package org.w3c.dom.svg;

import org.w3c.dom.DOMException;

public interface SVGPath {
   short MOVE_TO = 77;
   short LINE_TO = 76;
   short CURVE_TO = 67;
   short QUAD_TO = 81;
   short CLOSE = 90;

   int getNumberOfSegments();

   short getSegment(int var1) throws DOMException;

   float getSegmentParam(int var1, int var2) throws DOMException;

   void moveTo(float var1, float var2);

   void lineTo(float var1, float var2);

   void quadTo(float var1, float var2, float var3, float var4);

   void curveTo(float var1, float var2, float var3, float var4, float var5, float var6);

   void close();
}
