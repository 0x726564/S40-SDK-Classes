package com.nokia.mid.impl.isa.m2g;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGPath;

public class SVGPathImpl implements SVGPath {
   private int myPath;

   protected SVGPathImpl(int handle) {
      this.myPath = handle;
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

   public short getSegment(int index) throws DOMException {
      if (index >= 0 && index < _getNumSegs(this.myPath)) {
         return this.nativeToAscii(_getSegmentType(this.myPath, index));
      } else {
         throw new DOMException((short)1, "Index Out of Bounds");
      }
   }

   public float getSegmentParam(int cmdIndex, int paramIndex) {
      if (cmdIndex < _getNumSegs(this.myPath) && cmdIndex >= 0 && paramIndex >= 0) {
         short command = this.getSegment(cmdIndex);
         if (command == 90 && paramIndex >= 0 || command == 77 && paramIndex >= 2 || command == 76 && paramIndex >= 2 || command == 67 && paramIndex >= 6 || command == 81 && paramIndex >= 4) {
            throw new DOMException((short)1, "Index Out of Bounds for path segment type " + (char)command);
         } else {
            return _getSegmentParameter(this.myPath, cmdIndex, paramIndex);
         }
      } else {
         throw new DOMException((short)1, "Index Out of Bounds");
      }
   }

   public void moveTo(float x, float y) {
      _addMoveTo(this.myPath, x, y);
   }

   public void lineTo(float x, float y) {
      _addLineTo(this.myPath, x, y);
   }

   public void quadTo(float x1, float y1, float x2, float y2) {
      _addQuadTo(this.myPath, x1, y1, x2, y2);
   }

   public void curveTo(float x1, float y1, float x2, float y2, float x3, float y3) {
      _addCurveTo(this.myPath, x1, y1, x2, y2, x3, y3);
   }

   public void close() {
      _addClose(this.myPath);
   }

   private short nativeToAscii(int type) {
      byte retVal;
      switch(type) {
      case 600:
      case 601:
         retVal = 77;
         break;
      case 602:
         retVal = 90;
         break;
      case 603:
      case 604:
      case 605:
      case 606:
      case 607:
      case 608:
         retVal = 76;
         break;
      case 609:
      case 610:
      case 611:
      case 612:
         retVal = 67;
         break;
      case 613:
      case 614:
      case 615:
      case 616:
         retVal = 81;
         break;
      default:
         retVal = -1;
      }

      return retVal;
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
