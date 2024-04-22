package javax.microedition.m3g;

import java.lang.ref.WeakReference;
import java.util.Hashtable;

class Interface {
   private static Interface c;
   private int handle = _ctor();
   private final Hashtable d = new Hashtable();

   private Interface() {
      Platform.a(this);
   }

   static final Interface getInstance() {
      if (c == null) {
         c = new Interface();
      }

      return c;
   }

   static final int getHandle() {
      return getInstance().handle;
   }

   static final void a(Object3D var0) {
      Platform.b(var0);
      getInstance().d.put(new Integer(var0.handle), new WeakReference(var0));
   }

   static final Object3D getObjectInstance(int var0) {
      if (var0 == 0) {
         return null;
      } else {
         Interface var2 = getInstance();
         Integer var1 = new Integer(var0);
         Object3D var10000;
         Object var3;
         if ((var3 = var2.d.get(var1)) != null) {
            Object3D var5;
            if ((var5 = (Object3D)((WeakReference)var3).get()) == null) {
               var2.d.remove(var1);
            }

            var10000 = var5;
         } else {
            var10000 = null;
         }

         Object3D var4 = var10000;
         if (var10000 != null) {
            return var4;
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

   private static native int _ctor();

   private static native int _getClassID(int var0);
}
