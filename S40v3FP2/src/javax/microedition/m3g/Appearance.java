package javax.microedition.m3g;

public class Appearance extends Object3D {
   private CompositingMode compositingMode;
   private Fog fog;
   private Material material;
   private PolygonMode polygonMode;
   private Texture2D[] textures;

   public Appearance() {
      super(_ctor(Interface.getHandle()));
   }

   Appearance(int var1) {
      super(var1);
      this.compositingMode = (CompositingMode)getInstance(_getCompositingMode(var1));
      this.fog = (Fog)getInstance(_getFog(var1));
      this.material = (Material)getInstance(_getMaterial(var1));
      this.polygonMode = (PolygonMode)getInstance(_getPolygonMode(var1));
      this.textures = new Texture2D[Defs.NUM_TEXTURE_UNITS];

      for(int var2 = 0; var2 < Defs.NUM_TEXTURE_UNITS; ++var2) {
         this.textures[var2] = (Texture2D)getInstance(_getTexture(var1, var2));
      }

   }

   public void setCompositingMode(CompositingMode var1) {
      _setCompositingMode(this.handle, var1 != null ? var1.handle : 0);
      this.compositingMode = var1;
   }

   public CompositingMode getCompositingMode() {
      return this.compositingMode;
   }

   public void setFog(Fog var1) {
      _setFog(this.handle, var1 != null ? var1.handle : 0);
      this.fog = var1;
   }

   public Fog getFog() {
      return this.fog;
   }

   public void setPolygonMode(PolygonMode var1) {
      _setPolygonMode(this.handle, var1 != null ? var1.handle : 0);
      this.polygonMode = var1;
   }

   public PolygonMode getPolygonMode() {
      return this.polygonMode;
   }

   public void setLayer(int var1) {
      _setLayer(this.handle, var1);
   }

   public int getLayer() {
      return _getLayer(this.handle);
   }

   public void setMaterial(Material var1) {
      _setMaterial(this.handle, var1 != null ? var1.handle : 0);
      this.material = var1;
   }

   public Material getMaterial() {
      return this.material;
   }

   public void setTexture(int var1, Texture2D var2) {
      _setTexture(this.handle, var1, var2 != null ? var2.handle : 0);
      if (this.textures == null) {
         this.textures = new Texture2D[Defs.NUM_TEXTURE_UNITS];
      }

      this.textures[var1] = var2;
   }

   public Texture2D getTexture(int var1) {
      return (Texture2D)getInstance(_getTexture(this.handle, var1));
   }

   private static native int _ctor(int var0);

   private static native int _getCompositingMode(int var0);

   private static native int _getFog(int var0);

   private static native int _getLayer(int var0);

   private static native int _getMaterial(int var0);

   private static native int _getPolygonMode(int var0);

   private static native int _getTexture(int var0, int var1);

   private static native void _setCompositingMode(int var0, int var1);

   private static native void _setFog(int var0, int var1);

   private static native void _setLayer(int var0, int var1);

   private static native void _setMaterial(int var0, int var1);

   private static native void _setPolygonMode(int var0, int var1);

   private static native void _setTexture(int var0, int var1, int var2);
}
