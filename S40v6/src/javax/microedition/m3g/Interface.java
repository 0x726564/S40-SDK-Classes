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

   static final void register(Object3D obj) {
      getInstance().liveObjects.put(new Integer(obj.handle), new WeakReference(obj));
      Platform.registerFinalizer(obj);
   }

   static final Object3D findObject(int handle) {
      Interface self = getInstance();
      Integer iHandle = new Integer(handle);
      Object ref = self.liveObjects.get(iHandle);
      if (ref != null) {
         Object3D obj = (Object3D)((WeakReference)ref).get();
         if (obj == null) {
            self.liveObjects.remove(iHandle);
         }

         return obj;
      } else {
         return null;
      }
   }

   static final Object3D getObjectInstance(int handle) {
      if (handle == 0) {
         return null;
      } else {
         Object3D obj = findObject(handle);
         if (obj != null) {
            return obj;
         } else {
            switch(_getClassID(handle)) {
            case 1:
               return new AnimationController(handle);
            case 2:
               return new AnimationTrack(handle);
            case 3:
               return new Appearance(handle);
            case 4:
               return new Background(handle);
            case 5:
               return new Camera(handle);
            case 6:
               return new CompositingMode(handle);
            case 7:
               return new Fog(handle);
            case 8:
               return new Group(handle);
            case 9:
               return new Image2D(handle);
            case 10:
               return new TriangleStripArray(handle);
            case 11:
               return new KeyframeSequence(handle);
            case 12:
               return new Light(handle);
            case 13:
            case 18:
            default:
               throw new Error();
            case 14:
               return new Material(handle);
            case 15:
               return new Mesh(handle);
            case 16:
               return new MorphingMesh(handle);
            case 17:
               return new PolygonMode(handle);
            case 19:
               return new SkinnedMesh(handle);
            case 20:
               return new Sprite3D(handle);
            case 21:
               return new Texture2D(handle);
            case 22:
               return new VertexArray(handle);
            case 23:
               return new VertexBuffer(handle);
            case 24:
               return new World(handle);
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
