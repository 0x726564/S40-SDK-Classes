package javax.microedition.m3g;

class Platform {
   static final void b(Object3D var0) {
      registerObjectFinalizer(var0);
   }

   static final void a(Graphics3D var0) {
      registerObjectFinalizer(var0);
   }

   static final void a(Interface var0) {
      registerInterfaceFinalizer(var0);
   }

   static final void a(Loader var0) {
      registerObjectFinalizer(var0);
   }

   private static native void registerObjectFinalizer(Object var0);

   private static native void registerInterfaceFinalizer(Interface var0);
}
