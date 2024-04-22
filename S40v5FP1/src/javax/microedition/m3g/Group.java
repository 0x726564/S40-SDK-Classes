package javax.microedition.m3g;

import java.util.Vector;

public class Group extends Node {
   private Vector N;

   public Group() {
      super(_ctor(Interface.getHandle()));
   }

   Group(int var1) {
      super(var1);
      int var2 = _getChildCount(var1);

      while(var2-- > 0) {
         this.a((Node)getInstance(_getChild(var1, var2)));
      }

   }

   public void addChild(Node var1) {
      _addChild(this.handle, var1 != null ? var1.handle : 0);
      if (var1 != null && var1.getParent() != this) {
         this.a(var1);
      }

   }

   public void removeChild(Node var1) {
      if (var1 != null) {
         _removeChild(this.handle, var1.handle);
         Group var2;
         if ((var2 = this).N != null && var2.N.removeElement(var1)) {
            if (var2.N.isEmpty()) {
               var2.N = null;
            }

            var1.setParent((Node)null);
         }
      }

   }

   public int getChildCount() {
      return _getChildCount(this.handle);
   }

   public Node getChild(int var1) {
      return (Node)getInstance(_getChild(this.handle, var1));
   }

   public boolean pick(int var1, float var2, float var3, float var4, float var5, float var6, float var7, RayIntersection var8) {
      float[] var9 = new float[11];
      float[] var11 = new float[]{var2, var3, var4, var5, var6, var7};
      int var10;
      if ((var10 = _pick3D(this.handle, var1, var11, var9)) != 0) {
         if (var8 != null) {
            var8.a(var10, var9);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean pick(int var1, float var2, float var3, Camera var4, RayIntersection var5) {
      float[] var6 = new float[11];
      int var7;
      if ((var7 = _pick2D(this.handle, var1, var2, var3, var4 != null ? var4.handle : 0, var6)) != 0) {
         if (var5 != null) {
            var5.a(var7, var6);
         }

         return true;
      } else {
         return false;
      }
   }

   private void a(Node var1) {
      if (var1 == null) {
         throw new Error();
      } else {
         if (this.N == null) {
            this.N = new Vector();
         }

         this.N.addElement(var1);
         var1.setParent(this);
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
