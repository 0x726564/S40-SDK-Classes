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

   Appearance(int handle) {
      super(handle);
      this.compositingMode = (CompositingMode)getInstance(_getCompositingMode(handle));
      this.fog = (Fog)getInstance(_getFog(handle));
      this.material = (Material)getInstance(_getMaterial(handle));
      this.polygonMode = (PolygonMode)getInstance(_getPolygonMode(handle));
      this.textures = new Texture2D[Defs.NUM_TEXTURE_UNITS];

      for(int i = 0; i < Defs.NUM_TEXTURE_UNITS; ++i) {
         this.textures[i] = (Texture2D)getInstance(_getTexture(handle, i));
      }

   }

   public void setCompositingMode(CompositingMode compositingMode) {
      _setCompositingMode(this.handle, compositingMode != null ? compositingMode.handle : 0);
      this.compositingMode = compositingMode;
   }

   public CompositingMode getCompositingMode() {
      return this.compositingMode;
   }

   public void setFog(Fog fog) {
      _setFog(this.handle, fog != null ? fog.handle : 0);
      this.fog = fog;
   }

   public Fog getFog() {
      return this.fog;
   }

   public void setPolygonMode(PolygonMode polygonMode) {
      _setPolygonMode(this.handle, polygonMode != null ? polygonMode.handle : 0);
      this.polygonMode = polygonMode;
   }

   public PolygonMode getPolygonMode() {
      return this.polygonMode;
   }

   public void setLayer(int index) {
      _setLayer(this.handle, index);
   }

   public int getLayer() {
      return _getLayer(this.handle);
   }

   public void setMaterial(Material material) {
      _setMaterial(this.handle, material != null ? material.handle : 0);
      this.material = material;
   }

   public Material getMaterial() {
      return this.material;
   }

   public void setTexture(int unit, Texture2D texture) {
      _setTexture(this.handle, unit, texture != null ? texture.handle : 0);
      if (this.textures == null) {
         this.textures = new Texture2D[Defs.NUM_TEXTURE_UNITS];
      }

      this.textures[unit] = texture;
   }

   public Texture2D getTexture(int unit) {
      return (Texture2D)getInstance(_getTexture(this.handle, unit));
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
