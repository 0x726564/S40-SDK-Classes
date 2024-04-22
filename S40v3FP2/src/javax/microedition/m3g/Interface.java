package javax.microedition.m3g;

import java.lang.ref.WeakReference;
import java.util.Hashtable;

class Interface {
   private static final int ANIMATION_CONTROLLER = 1;
   private static final int ANIMATION_TRACK = 2;
   private static final int APPEARANCE = 3;
   private static final int BACKGROUND = 4;
   private static final int CAMERA = 5;
   private static final int COMPOSITING_MODE = 6;
   private static final int FOG = 7;
   private static final int GROUP = 8;
   private static final int IMAGE_2D = 9;
   private static final int INDEX_BUFFER = 10;
   private static final int KEYFRAME_SEQUENCE = 11;
   private static final int LIGHT = 12;
   private static final int LOADER = 13;
   private static final int MATERIAL = 14;
   private static final int MESH = 15;
   private static final int MORPHING_MESH = 16;
   private static final int POLYGON_MODE = 17;
   private static final int RENDER_CONTEXT = 18;
   private static final int SKINNED_MESH = 19;
   private static final int SPRITE_3D = 20;
   private static final int TEXTURE_2D = 21;
   private static final int VERTEX_ARRAY = 22;
   private static final int VERTEX_BUFFER = 23;
   private static final int WORLD = 24;
   private static Interface s_instance;
   private int handle = _ctor();
   private final Hashtable liveObjects = new Hashtable();

   private Interface() {
      Platform.registerFinalizer(this);
   }

   static final Interface getInstance() {
      if (s_instance == null) {
         s_instance = new Interface();
      }

      return s_instance;
   }

   static final int getHandle() {
      return getInstance().handle;
   }

   static final void register(Object3D var0) {
      Platform.registerFinalizer(var0);
      getInstance().liveObjects.put(new Integer(var0.handle), new WeakReference(var0));
   }

   static final Object3D findObject(int var0) {
      Interface var1 = getInstance();
      Integer var2 = new Integer(var0);
      Object var3 = var1.liveObjects.get(var2);
      if (var3 != null) {
         Object3D var4 = (Object3D)((WeakReference)var3).get();
         if (var4 == null) {
            var1.liveObjects.remove(var2);
         }

         return var4;
      } else {
         return null;
      }
   }

   static final Object3D getObjectInstance(int var0) {
      if (var0 == 0) {
         return null;
      } else {
         Object3D var1 = findObject(var0);
         if (var1 != null) {
            return var1;
         } else {
            switch(_getClassID(var0)) {
            case 1:
               return new AnimationController(var0);
            case 2:
               return new AnimationTrack(var0);
            case 3:
               return new Appearance(var0);
            case 4:
               return new Background(var0);
            case 5:
               return new Camera(var0);
            case 6:
               return new CompositingMode(var0);
            case 7:
               return new Fog(var0);
            case 8:
               return new Group(var0);
            case 9:
               return new Image2D(var0);
            case 10:
               return new TriangleStripArray(var0);
            case 11:
               return new KeyframeSequence(var0);
            case 12:
               return new Light(var0);
            case 13:
            case 18:
            default:
               throw new Error();
            case 14:
               return new Material(var0);
            case 15:
               return new Mesh(var0);
            case 16:
               return new MorphingMesh(var0);
            case 17:
               return new PolygonMode(var0);
            case 19:
               return new SkinnedMesh(var0);
            case 20:
               return new Sprite3D(var0);
            case 21:
               return new Texture2D(var0);
            case 22:
               return new VertexArray(var0);
            case 23:
               return new VertexBuffer(var0);
            case 24:
               return new World(var0);
            }
         }
      }
   }

   private final void registeredFinalize() {
      Platform.finalizeInterface(this.handle);
   }

   private static native int _ctor();

   private static native int _getClassID(int var0);
}
