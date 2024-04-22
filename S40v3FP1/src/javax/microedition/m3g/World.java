package javax.microedition.m3g;

public class World extends Group {
   private Camera activeCamera;
   private Background background;

   public World() {
      super(_ctor(Interface.getHandle()));
   }

   World(int var1) {
      super(var1);
      this.background = (Background)getInstance(_getBackground(var1));
      this.activeCamera = (Camera)getInstance(_getActiveCamera(var1));
   }

   public void setBackground(Background var1) {
      _setBackground(this.handle, var1 != null ? var1.handle : 0);
      this.background = var1;
   }

   public Background getBackground() {
      return this.background;
   }

   public void setActiveCamera(Camera var1) {
      _setActiveCamera(this.handle, var1 != null ? var1.handle : 0);
      this.activeCamera = var1;
   }

   public Camera getActiveCamera() {
      return this.activeCamera;
   }

   private static native int _ctor(int var0);

   private static native void _setActiveCamera(int var0, int var1);

   private static native void _setBackground(int var0, int var1);

   private static native int _getActiveCamera(int var0);

   private static native int _getBackground(int var0);
}
