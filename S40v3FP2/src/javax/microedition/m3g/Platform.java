package javax.microedition.m3g;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

class Platform {
   static final void registerFinalizer(Object3D var0) {
      registerObjectFinalizer(var0);
   }

   static final void registerFinalizer(Graphics3D var0) {
      registerObjectFinalizer(var0);
   }

   static final void registerFinalizer(Interface var0) {
      registerInterfaceFinalizer(var0);
   }

   static final void registerFinalizer(Loader var0) {
      registerObjectFinalizer(var0);
   }

   static final void sync(Graphics var0) {
   }

   static final void sync(Image var0) {
   }

   static final void gc() {
   }

   static final void finalizeInterface(int var0) {
   }

   static final void finalizeObject(int var0) {
   }

   private static native void registerObjectFinalizer(Object var0);

   private static native void registerInterfaceFinalizer(Interface var0);
}
