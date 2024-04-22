package javax.microedition.m3g;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

class Platform {
   static final void registerFinalizer(Object3D obj) {
      registerObject3DFinalizer(obj);
   }

   static final void registerFinalizer(Graphics3D g3d) {
      registerObjectFinalizer(g3d);
   }

   static final void registerFinalizer(Interface m3g) {
      registerInterfaceFinalizer(m3g);
   }

   static final void registerFinalizer(Loader loader) {
      registerObjectFinalizer(loader);
   }

   static final void sync(Graphics g) {
   }

   static final void sync(Image img) {
   }

   static final void gc() {
   }

   static final void finalizeInterface(int handle) {
   }

   static final void finalizeObject(int handle) {
   }

   private static native void registerObjectFinalizer(Object var0);

   private static native void registerObject3DFinalizer(Object var0);

   private static native void registerInterfaceFinalizer(Interface var0);
}
