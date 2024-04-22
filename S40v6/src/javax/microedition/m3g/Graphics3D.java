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

   public void bindTarget(Object target) {
      this.bindTarget(target, true, 0);
   }

   public void bindTarget(Object target, boolean depth, int flags) {
      if (this.currentTarget != null) {
         throw new IllegalStateException();
      } else if (target == null) {
         throw new NullPointerException();
      } else {
         if (target instanceof Graphics) {
            Graphics g = (Graphics)target;
            Platform.sync(g);
            this.offsetX = g.getTranslateX();
            this.offsetY = g.getTranslateY();
            _bindGraphics(this.handle, g, g.getClipX() + this.offsetX, g.getClipY() + this.offsetY, g.getClipWidth(), g.getClipHeight(), depth, flags);
            this.currentTarget = g;
         } else {
            if (!(target instanceof Image2D)) {
               throw new IllegalArgumentException();
            }

            Image2D img = (Image2D)target;
            this.offsetX = this.offsetY = 0;
            _bindImage(this.handle, img.handle, depth, flags);
            this.currentTarget = img;
         }

         this.hints = flags;
         this.depthEnabled = depth;
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

   public void setViewport(int x, int y, int width, int height) {
      if (width > 0 && height > 0 && width <= Defs.MAX_VIEWPORT_DIMENSION && height <= Defs.MAX_VIEWPORT_DIMENSION) {
         _setViewport(this.handle, x + this.offsetX, y + this.offsetY, width, height);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void clear(Background background) {
      _clear(this.handle, background != null ? background.handle : 0);
   }

   public void render(World world) {
      _renderWorld(this.handle, world.handle);
   }

   public void render(VertexBuffer vertices, IndexBuffer primitives, Appearance appearance, Transform transform) {
      this.render(vertices, primitives, appearance, transform, -1);
   }

   public void render(VertexBuffer vertices, IndexBuffer primitives, Appearance appearance, Transform transform, int scope) {
      _render(this.handle, vertices.handle, primitives.handle, appearance.handle, transform != null ? transform.matrix : null, scope);
   }

   public void render(Node node, Transform transform) {
      if (!(node instanceof Mesh) && !(node instanceof Sprite3D) && !(node instanceof Group) && node != null) {
         throw new IllegalArgumentException();
      } else {
         _renderNode(this.handle, node.handle, transform != null ? transform.matrix : null);
      }
   }

   public void setCamera(Camera camera, Transform transform) {
      _setCamera(this.handle, camera != null ? camera.handle : 0, transform != null ? transform.matrix : null);
      this.camera = camera;
   }

   public int addLight(Light light, Transform transform) {
      int index = _addLight(this.handle, light.handle, transform != null ? transform.matrix : null);
      if (this.lights.size() < index + 1) {
         this.lights.setSize(index + 1);
      }

      this.lights.setElementAt(light, index);
      return index;
   }

   public void setLight(int index, Light light, Transform transform) {
      _setLight(this.handle, index, light != null ? light.handle : 0, transform != null ? transform.matrix : null);
      this.lights.setElementAt(light, index);
   }

   public void resetLights() {
      _resetLights(this.handle);
      this.lights.removeAllElements();
   }

   public static final Hashtable getProperties() {
      Hashtable props = new Hashtable();
      props.put("supportAntialiasing", new Boolean(Defs.supportAntialiasing));
      props.put("supportTrueColor", new Boolean(Defs.supportTrueColor));
      props.put("supportDithering", new Boolean(Defs.supportDithering));
      props.put("supportMipmapping", new Boolean(Defs.supportMipmapping));
      props.put("supportPerspectiveCorrection", new Boolean(Defs.supportPerspectiveCorrection));
      props.put("supportLocalCameraLighting", new Boolean(Defs.supportLocalCameraLighting));
      props.put("maxLights", new Integer(Defs.MAX_LIGHTS));
      props.put("maxViewportWidth", new Integer(Defs.MAX_VIEWPORT_WIDTH));
      props.put("maxViewportHeight", new Integer(Defs.MAX_VIEWPORT_HEIGHT));
      props.put("maxViewportDimension", new Integer(Defs.MAX_VIEWPORT_DIMENSION));
      props.put("maxTextureDimension", new Integer(Defs.MAX_TEXTURE_DIMENSION));
      props.put("maxSpriteCropDimension", new Integer(Defs.MAX_TEXTURE_DIMENSION));
      props.put("numTextureUnits", new Integer(Defs.NUM_TEXTURE_UNITS));
      props.put("maxTransformsPerVertex", new Integer(Defs.MAX_TRANSFORMS_PER_VERTEX));
      return props;
   }

   public void setDepthRange(float near, float far) {
      _setDepthRange(this.handle, near, far);
   }

   public Camera getCamera(Transform transform) {
      if (transform != null) {
         _getViewTransform(this.handle, transform.matrix);
      }

      return (Camera)Object3D.getInstance(_getCamera(this.handle));
   }

   public float getDepthRangeFar() {
      return _getDepthRangeFar(this.handle);
   }

   public float getDepthRangeNear() {
      return _getDepthRangeNear(this.handle);
   }

   public Light getLight(int index, Transform transform) {
      if (index >= 0 && index < _getLightCount(this.handle)) {
         return (Light)Object3D.getInstance(_getLightTransform(this.handle, index, transform != null ? transform.matrix : null));
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
