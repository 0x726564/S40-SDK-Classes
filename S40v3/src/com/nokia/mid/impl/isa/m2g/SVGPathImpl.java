package com.nokia.mid.impl.isa.m2g;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGPath;

public class SVGPathImpl implements SVGPath {
   private int myPath;

   protected SVGPathImpl(int var1) {
      this.myPath = var1;
      this._registerToFinalize();
   }

   protected void finalize() {
      _destroySVGPath(this.myPath);
   }

   protected int getHandle() {
      return this.myPath;
   }

   public int getNumberOfSegments() {
      return _getNumSegs(this.myPath);
   }

   public short getSegment(int var1) throws DOMException {
      if (var1 >= 0 && var1 < _getNumSegs(this.myPath)) {
         return this.nativeToAscii(_getSegmentType(this.myPath, var1));
      } else {
         throw new DOMException((short)1, "Index Out of Bounds");
      }
   }

   public float getSegmentParam(int var1, int var2) {
      if (var1 < _getNumSegs(this.myPath) && var1 >= 0 && var2 >= 0) {
         short var3 = this.getSegment(var1);
         if (var3 == 90 && var2 >= 0 || var3 == 77 && var2 >= 2 || var3 == 76 && var2 >= 2 || var3 == 67 && var2 >= 6 || var3 == 81 && var2 >= 4) {
            throw new DOMException((short)1, "Index Out of Bounds for path segment type " + (char)var3);
         } else {
            return _getSegmentParameter(this.myPath, var1, var2);
         }
      } else {
         throw new DOMException((short)1, "Index Out of Bounds");
      }
   }

   public void moveTo(float var1, float var2) {
      _addMoveTo(this.myPath, var1, var2);
   }

   public void lineTo(float var1, float var2) {
      _addLineTo(this.myPath, var1, var2);
   }

   public void quadTo(float var1, float var2, float var3, float var4) {
      _addQuadTo(this.myPath, var1, var2, var3, var4);
   }

   public void curveTo(float var1, float var2, float var3, float var4, float var5, float var6) {
      _addCurveTo(this.myPath, var1, var2, var3, var4, var5, var6);
   }

   public void close() {
      _addClose(this.myPath);
   }

   private short nativeToAscii(int var1) {
      byte var2;
      switch(var1) {
      case 600:
      case 601:
         var2 = 77;
         break;
      case 602:
         var2 = 90;
         break;
      case 603:
      case 604:
      case 605:
      case 606:
      case 607:
      case 608:
         var2 = 76;
         break;
      case 609:
      case 610:
      case 611:
      case 612:
         var2 = 67;
         break;
      case 613:
      case 614:
      case 615:
      case 616:
         var2 = 81;
         break;
      default:
         var2 = -1;
      }

      return var2;
   }

   protected static native int _createSVGPath();

   protected static native void _destroySVGPath(int var0);

   private static native int _getNumSegs(int var0);

   private static native short _getSegmentType(int var0, int var1);

   private static native float _getSegmentParameter(int var0, int var1, int var2);

   private static native void _addMoveTo(int var0, float var1, float var2);

   private static native void _addLineTo(int var0, float var1, float var2);

   private static native void _addQuadTo(int var0, float var1, float var2, float var3, float var4);

   private static native void _addCurveTo(int var0, float var1, float var2, float var3, float var4, float var5, float var6);

   private static native void _addClose(int var0);

   private native void _registerToFinalize();
}
