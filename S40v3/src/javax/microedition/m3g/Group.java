package javax.microedition.m3g;

import java.util.Vector;

public class Group extends Node {
   Vector children;

   public Group() {
      super(_ctor(Interface.getHandle()));
   }

   Group(int var1) {
      super(var1);
      int var2 = _getChildCount(var1);

      while(var2-- > 0) {
         this.linkChild((Node)getInstance(_getChild(var1, var2)));
      }

   }

   public void addChild(Node var1) {
      _addChild(this.handle, var1 != null ? var1.handle : 0);
      if (var1 != null && var1.getParent() != this) {
         this.linkChild(var1);
      }

   }

   public void removeChild(Node var1) {
      if (var1 != null) {
         _removeChild(this.handle, var1.handle);
         this.detachChild(var1);
      }

   }

   public int getChildCount() {
      return _getChildCount(this.handle);
   }

   public Node getChild(int var1) {
      return (Node)getInstance(_getChild(this.handle, var1));
   }

   public boolean pick(int var1, float var2, float var3, float var4, float var5, float var6, float var7, RayIntersection var8) {
      float[] var9 = RayIntersection.createResult();
      float[] var10 = new float[]{var2, var3, var4, var5, var6, var7};
      int var11 = _pick3D(this.handle, var1, var10, var9);
      if (var11 != 0) {
         if (var8 != null) {
            var8.fill(var11, var9);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean pick(int var1, float var2, float var3, Camera var4, RayIntersection var5) {
      float[] var6 = RayIntersection.createResult();
      int var7 = _pick2D(this.handle, var1, var2, var3, var4 != null ? var4.handle : 0, var6);
      if (var7 != 0) {
         if (var5 != null) {
            var5.fill(var7, var6);
         }

         return true;
      } else {
         return false;
      }
   }

   private void linkChild(Node var1) {
      if (var1 == null) {
         throw new Error();
      } else {
         if (this.children == null) {
            this.children = new Vector();
         }

         this.children.addElement(var1);
         var1.setParent(this);
      }
   }

   private void detachChild(Node var1) {
      if (this.children != null && this.children.removeElement(var1)) {
         if (this.children.isEmpty()) {
            this.children = null;
         }

         var1.setParent((Node)null);
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
