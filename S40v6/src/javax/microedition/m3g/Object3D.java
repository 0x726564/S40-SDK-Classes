package javax.microedition.m3g;

import java.util.Vector;

public abstract class Object3D {
   int handle;
   private Object userObject;
   private Vector animTracks;

   Object3D(int handle) {
      if (handle != 0) {
         this.handle = handle;
         _addRef(handle);
         Interface.register(this);
         int n = _getAnimationTrackCount(handle);

         while(n-- > 0) {
            this.linkAnimTrack((AnimationTrack)getInstance(_getAnimationTrack(handle, n)));
         }
      }

   }

   public final Object3D duplicate() {
      int numRef = 1;
      if (this instanceof Node) {
         Node var10000 = (Node)this;
         numRef = Node._getSubtreeSize(this.handle);
      }

      int[] handles = new int[numRef * 2];
      Object3D obj = getInstance(_duplicate(this.handle, handles));

      for(int i = 0; i < numRef; ++i) {
         Object userObj = getInstance(handles[i * 2]).getUserObject();
         Object3D duplicateObj = getInstance(handles[i * 2 + 1]);
         if (userObj != null) {
            duplicateObj.setUserObject(userObj);
         }
      }

      return obj;
   }

   public int getReferences(Object3D[] references) {
      int[] handles = null;
      if (references != null) {
         handles = new int[references.length];
      }

      int num = _getReferences(this.handle, handles);
      if (references != null) {
         for(int i = 0; i < num; ++i) {
            references[i] = getInstance(handles[i]);
         }
      }

      return num;
   }

   public void setUserID(int userID) {
      _setUserID(this.handle, userID);
   }

   public int getUserID() {
      return _getUserID(this.handle);
   }

   public Object3D find(int userID) {
      return getInstance(_find(this.handle, userID));
   }

   public void addAnimationTrack(AnimationTrack animationTrack) {
      _addAnimationTrack(this.handle, animationTrack.handle);
      this.linkAnimTrack(animationTrack);
   }

   public AnimationTrack getAnimationTrack(int index) {
      return (AnimationTrack)getInstance(_getAnimationTrack(this.handle, index));
   }

   public void removeAnimationTrack(AnimationTrack animationTrack) {
      if (animationTrack != null) {
         _removeAnimationTrack(this.handle, animationTrack.handle);
         if (this.animTracks != null) {
            this.animTracks.removeElement(animationTrack);
            if (this.animTracks.isEmpty()) {
               this.animTracks = null;
            }
         }
      }

   }

   public int getAnimationTrackCount() {
      return _getAnimationTrackCount(this.handle);
   }

   public final int animate(int time) {
      return _animate(this.handle, time);
   }

   public void setUserObject(Object obj) {
      this.userObject = obj;
   }

   public Object getUserObject() {
      return this.userObject;
   }

   static final Object3D getInstance(int handle) {
      return Interface.getObjectInstance(handle);
   }

   private void linkAnimTrack(AnimationTrack track) {
      if (this.animTracks == null) {
         this.animTracks = new Vector();
      }

      this.animTracks.addElement(track);
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
