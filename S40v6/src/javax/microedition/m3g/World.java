package javax.microedition.m3g;

public class World extends Group {
   private Camera activeCamera;
   private Background background;

   public World() {
      super(_ctor(Interface.getHandle()));
   }

   World(int handle) {
      super(handle);
      this.background = (Background)getInstance(_getBackground(handle));
      this.activeCamera = (Camera)getInstance(_getActiveCamera(handle));
   }

   public void setBackground(Background background) {
      _setBackground(this.handle, background != null ? background.handle : 0);
      this.background = background;
   }

   public Background getBackground() {
      return this.background;
   }

   public void setActiveCamera(Camera camera) {
      _setActiveCamera(this.handle, camera != null ? camera.handle : 0);
      this.activeCamera = camera;
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
