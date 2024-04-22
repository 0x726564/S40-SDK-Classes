package javax.microedition.m3g;

import java.util.Vector;

public class Group extends Node {
   Vector children;

   public Group() {
      super(_ctor(Interface.getHandle()));
   }

   Group(int handle) {
      super(handle);
      int n = _getChildCount(handle);

      while(n-- > 0) {
         this.linkChild((Node)getInstance(_getChild(handle, n)));
      }

   }

   public void addChild(Node child) {
      _addChild(this.handle, child != null ? child.handle : 0);
      if (child != null && child.getParent() != this) {
         this.linkChild(child);
      }

   }

   public void removeChild(Node child) {
      if (child != null) {
         _removeChild(this.handle, child.handle);
         this.detachChild(child);
      }

   }

   public int getChildCount() {
      return _getChildCount(this.handle);
   }

   public Node getChild(int index) {
      return (Node)getInstance(_getChild(this.handle, index));
   }

   public boolean pick(int mask, float ox, float oy, float oz, float dx, float dy, float dz, RayIntersection ri) {
      float[] result = RayIntersection.createResult();
      float[] ray = new float[]{ox, oy, oz, dx, dy, dz};
      int hIntersected = _pick3D(this.handle, mask, ray, result);
      if (hIntersected != 0) {
         if (ri != null) {
            ri.fill(hIntersected, result);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean pick(int mask, float x, float y, Camera camera, RayIntersection ri) {
      float[] result = RayIntersection.createResult();
      int hIntersected = _pick2D(this.handle, mask, x, y, camera != null ? camera.handle : 0, result);
      if (hIntersected != 0) {
         if (ri != null) {
            ri.fill(hIntersected, result);
         }

         return true;
      } else {
         return false;
      }
   }

   private void linkChild(Node child) {
      if (child == null) {
         throw new Error();
      } else {
         if (this.children == null) {
            this.children = new Vector();
         }

         this.children.addElement(child);
         child.setParent(this);
      }
   }

   private void detachChild(Node child) {
      if (this.children != null && this.children.removeElement(child)) {
         if (this.children.isEmpty()) {
            this.children = null;
         }

         child.setParent((Node)null);
      }

   }

   private static native int _ctor(int var0);

   private static native void _addChild(int var0, int var1);

   private static native void _removeChild(int var0, int var1);

   private static native int _getChildCount(int var0);

   private static native int _getChild(int var0, int var1);

   private static native int _pick3D(int var0, int var1, float[] var2, float[] var3);

   private static native int _pick2D(int var0, int var1, float var2, float var3, int var4, float[] var5);
}
