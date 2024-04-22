package javax.microedition.m3g;

import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;

public class Graphics3D {
   public static final int ANTIALIAS = 2;
   public static final int DITHER = 4;
   public static final int TRUE_COLOR = 8;
   public static final int OVERWRITE = 16;
   private static Graphics3D s_instance = null;
   int handle = _ctor(Interface.getHandle());
   private Camera camera = null;
   private Vector lights = new Vector();
   private Object currentTarget = null;
   private int offsetX;
   private int offsetY;
   private int hints = 0;
   private boolean depthEnabled = true;

   public static final Graphics3D getInstance() {
      if (s_instance == null) {
         s_instance = new Graphics3D();
      }

      return s_instance;
   }

   private Graphics3D() {
      _addRef(this.handle);
      Platform.registerFinalizer(this);
   }

   public void bindTarget(Object var1) {
      this.bindTarget(var1, true, 0);
   }

   public void bindTarget(Object var1, boolean var2, int var3) {
      if (this.currentTarget != null) {
         throw new IllegalStateException();
      } else if (var1 == null) {
         throw new NullPointerException();
      } else {
         if (var1 instanceof Graphics) {
            Graphics var4 = (Graphics)var1;
            Platform.sync(var4);
            this.offsetX = var4.getTranslateX();
            this.offsetY = var4.getTranslateY();
            _bindGraphics(this.handle, var4, var4.getClipX() + this.offsetX, var4.getClipY() + this.offsetY, var4.getClipWidth(), var4.getClipHeight(), var2, var3);
            this.currentTarget = var4;
         } else {
            if (!(var1 instanceof Image2D)) {
               throw new IllegalArgumentException();
            }

            Image2D var5 = (Image2D)var1;
            this.offsetX = this.offsetY = 0;
            _bindImage(this.handle, var5.handle, var2, var3);
            this.currentTarget = var5;
         }

         this.hints = var3;
         this.depthEnabled = var2;
      }
   }

   public void releaseTarget() {
      if (this.currentTarget != null) {
         if (this.currentTarget instanceof Graphics) {
            _releaseGraphics(this.handle);
         } else {
            if (!(this.currentTarget instanceof Image2D)) {
               throw new Error();
            }

            _releaseImage(this.handle);
         }

         this.currentTarget = null;
      }

   }

   public void setViewport(int var1, int var2, int var3, int var4) {
      if (var3 > 0 && var4 > 0 && var3 <= Defs.MAX_VIEWPORT_DIMENSION && var4 <= Defs.MAX_VIEWPORT_DIMENSION) {
         _setViewport(this.handle, var1 + this.offsetX, var2 + this.offsetY, var3, var4);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void clear(Background var1) {
      _clear(this.handle, var1 != null ? var1.handle : 0);
   }

   public void render(World var1) {
      _renderWorld(this.handle, var1.handle);
   }

   public void render(VertexBuffer var1, IndexBuffer var2, Appearance var3, Transform var4) {
      this.render(var1, var2, var3, var4, -1);
   }

   public void render(VertexBuffer var1, IndexBuffer var2, Appearance var3, Transform var4, int var5) {
      _render(this.handle, var1.handle, var2.handle, var3.handle, var4 != null ? var4.matrix : null, var5);
   }

   public void render(Node var1, Transform var2) {
      if (!(var1 instanceof Mesh) && !(var1 instanceof Sprite3D) && !(var1 instanceof Group) && var1 != null) {
         throw new IllegalArgumentException();
      } else {
         _renderNode(this.handle, var1.handle, var2 != null ? var2.matrix : null);
      }
   }

   public void setCamera(Camera var1, Transform var2) {
      _setCamera(this.handle, var1 != null ? var1.handle : 0, var2 != null ? var2.matrix : null);
      this.camera = var1;
   }

   public int addLight(Light var1, Transform var2) {
      int var3 = _addLight(this.handle, var1.handle, var2 != null ? var2.matrix : null);
      if (this.lights.size() < var3 + 1) {
         this.lights.setSize(var3 + 1);
      }

      this.lights.setElementAt(var1, var3);
      return var3;
   }

   public void setLight(int var1, Light var2, Transform var3) {
      _setLight(this.handle, var1, var2 != null ? var2.handle : 0, var3 != null ? var3.matrix : null);
      this.lights.setElementAt(var2, var1);
   }

   public void resetLights() {
      _resetLights(this.handle);
      this.lights.removeAllElements();
   }

   public static final Hashtable getProperties() {
      Hashtable var0 = new Hashtable();
      var0.put("supportAntialiasing", new Boolean(Defs.supportAntialiasing));
      var0.put("supportTrueColor", new Boolean(Defs.supportTrueColor));
      var0.put("supportDithering", new Boolean(Defs.supportDithering));
      var0.put("supportMipmapping", new Boolean(Defs.supportMipmapping));
      var0.put("supportPerspectiveCorrection", new Boolean(Defs.supportPerspectiveCorrection));
      var0.put("supportLocalCameraLighting", new Boolean(Defs.supportLocalCameraLighting));
      var0.put("maxLights", new Integer(Defs.MAX_LIGHTS));
      var0.put("maxViewportWidth", new Integer(Defs.MAX_VIEWPORT_WIDTH));
      var0.put("maxViewportHeight", new Integer(Defs.MAX_VIEWPORT_HEIGHT));
      var0.put("maxViewportDimension", new Integer(Defs.MAX_VIEWPORT_DIMENSION));
      var0.put("maxTextureDimension", new Integer(Defs.MAX_TEXTURE_DIMENSION));
      var0.put("maxSpriteCropDimension", new Integer(Defs.MAX_TEXTURE_DIMENSION));
      var0.put("numTextureUnits", new Integer(Defs.NUM_TEXTURE_UNITS));
      var0.put("maxTransformsPerVertex", new Integer(Defs.MAX_TRANSFORMS_PER_VERTEX));
      return var0;
   }

   public void setDepthRange(float var1, float var2) {
      _setDepthRange(this.handle, var1, var2);
   }

   public Camera getCamera(Transform var1) {
      if (var1 != null) {
         _getViewTransform(this.handle, var1.matrix);
      }

      return (Camera)Object3D.getInstance(_getCamera(this.handle));
   }

   public float getDepthRangeFar() {
      return _getDepthRangeFar(this.handle);
   }

   public float getDepthRangeNear() {
      return _getDepthRangeNear(this.handle);
   }

   public Light getLight(int var1, Transform var2) {
      if (var1 >= 0 && var1 < _getLightCount(this.handle)) {
         return (Light)Object3D.getInstance(_getLightTransform(this.handle, var1, var2 != null ? var2.matrix : null));
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public int getLightCount() {
      return _getLightCount(this.handle);
   }

   public Object getTarget() {
      return this.currentTarget;
   }

   public int getViewportHeight() {
      return _getViewportHeight(this.handle);
   }

   public int getViewportWidth() {
      return _getViewportWidth(this.handle);
   }

   public int getViewportX() {
      return _getViewportX(this.handle) - this.offsetX;
   }

   public int getViewportY() {
      return _getViewportY(this.handle) - this.offsetY;
   }

   public int getHints() {
      return this.hints;
   }

   public boolean isDepthBufferEnabled() {
      return this.depthEnabled;
   }

   private final void registeredFinalize() {
      Platform.finalizeObject(this.handle);
   }

   private static native int _ctor(int var0);

   private static native void _addRef(int var0);

   private static native int _addLight(int var0, int var1, byte[] var2);

   private static native void _bindGraphics(int var0, Graphics var1, int var2, int var3, int var4, int var5, boolean var6, int var7);

   private static native void _bindImage(int var0, int var1, boolean var2, int var3);

   private static native void _releaseGraphics(int var0);

   private static native void _releaseImage(int var0);

   private static native void _resetLights(int var0);

   private static native void _clear(int var0, int var1);

   private static native void _render(int var0, int var1, int var2, int var3, byte[] var4, int var5);

   private static native void _renderNode(int var0, int var1, byte[] var2);

   private static native void _renderWorld(int var0, int var1);

   private static native void _setCamera(int var0, int var1, byte[] var2);

   private static native void _setViewport(int var0, int var1, int var2, int var3, int var4);

   private static native void _setLight(int var0, int var1, int var2, byte[] var3);

   private static native void _setDepthRange(int var0, float var1, float var2);

   private static native void _getViewTransform(int var0, byte[] var1);

   private static native int _getCamera(int var0);

   private static native int _getLightTransform(int var0, int var1, byte[] var2);

   private static native int _getLightCount(int var0);

   private static native float _getDepthRangeNear(int var0);

   private static native float _getDepthRangeFar(int var0);

   private static native int _getViewportX(int var0);

   private static native int _getViewportY(int var0);

   private static native int _getViewportWidth(int var0);

   private static native int _getViewportHeight(int var0);
}
