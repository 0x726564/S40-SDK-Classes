package javax.microedition.m3g;

import java.util.Vector;

public abstract class Object3D {
   int handle;
   private Object userObject;
   private Vector animTracks;

   Object3D(int var1) {
      if (var1 != 0) {
         this.handle = var1;
         _addRef(var1);
         Interface.register(this);
         int var2 = _getAnimationTrackCount(var1);

         while(var2-- > 0) {
            this.linkAnimTrack((AnimationTrack)getInstance(_getAnimationTrack(var1, var2)));
         }
      }

   }

   public final Object3D duplicate() {
      int var1 = 1;
      if (this instanceof Node) {
         Node var10000 = (Node)this;
         var1 = Node._getSubtreeSize(this.handle);
      }

      int[] var2 = new int[var1 * 2];
      Object3D var3 = getInstance(_duplicate(this.handle, var2));

      for(int var4 = 0; var4 < var1; ++var4) {
         Object var5 = getInstance(var2[var4 * 2]).getUserObject();
         Object3D var6 = getInstance(var2[var4 * 2 + 1]);
         if (var5 != null) {
            var6.setUserObject(var5);
         }
      }

      return var3;
   }

   public int getReferences(Object3D[] var1) {
      int[] var2 = null;
      if (var1 != null) {
         var2 = new int[var1.length];
      }

      int var3 = _getReferences(this.handle, var2);
      if (var1 != null) {
         for(int var4 = 0; var4 < var3; ++var4) {
            var1[var4] = getInstance(var2[var4]);
         }
      }

      return var3;
   }

   public void setUserID(int var1) {
      _setUserID(this.handle, var1);
   }

   public int getUserID() {
      return _getUserID(this.handle);
   }

   public Object3D find(int var1) {
      return getInstance(_find(this.handle, var1));
   }

   public void addAnimationTrack(AnimationTrack var1) {
      _addAnimationTrack(this.handle, var1.handle);
      this.linkAnimTrack(var1);
   }

   public AnimationTrack getAnimationTrack(int var1) {
      return (AnimationTrack)getInstance(_getAnimationTrack(this.handle, var1));
   }

   public void removeAnimationTrack(AnimationTrack var1) {
      if (var1 != null) {
         _removeAnimationTrack(this.handle, var1.handle);
         if (this.animTracks != null) {
            this.animTracks.removeElement(var1);
            if (this.animTracks.isEmpty()) {
               this.animTracks = null;
            }
         }
      }

   }

   public int getAnimationTrackCount() {
      return _getAnimationTrackCount(this.handle);
   }

   public final int animate(int var1) {
      return _animate(this.handle, var1);
   }

   public void setUserObject(Object var1) {
      this.userObject = var1;
   }

   public Object getUserObject() {
      return this.userObject;
   }

   static final Object3D getInstance(int var0) {
      return Interface.getObjectInstance(var0);
   }

   private void linkAnimTrack(AnimationTrack var1) {
      if (this.animTracks == null) {
         this.animTracks = new Vector();
      }

      this.animTracks.addElement(var1);
   }

   private final void registeredFinalize() {
      Platform.finalizeObject(this.handle);
   }

   private static native int _addAnimationTrack(int var0, int var1);

   private static native void _removeAnimationTrack(int var0, int var1);

   private static native int _getAnimationTrackCount(int var0);

   private static native int _animate(int var0, int var1);

   private static native void _setUserID(int var0, int var1);

   private static native int _getUserID(int var0);

   private static native void _addRef(int var0);

   private static native int _getAnimationTrack(int var0, int var1);

   private static native int _duplicate(int var0, int[] var1);

   private static native int _getReferences(int var0, int[] var1);

   private static native int _find(int var0, int var1);
}
