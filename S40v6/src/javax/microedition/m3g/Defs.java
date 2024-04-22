package javax.microedition.m3g;

class Defs {
   static boolean supportDithering;
   static boolean supportTrueColor;
   static boolean supportAntialiasing;
   static boolean supportMipmapping;
   static boolean supportPerspectiveCorrection;
   static boolean supportLocalCameraLighting;
   static int MAX_LIGHTS;
   static int MAX_TEXTURE_DIMENSION;
   static int MAX_TRANSFORMS_PER_VERTEX;
   static int MAX_VIEWPORT_WIDTH;
   static int MAX_VIEWPORT_HEIGHT;
   static int MAX_VIEWPORT_DIMENSION;
   static int NUM_TEXTURE_UNITS;
   static final int GET_POSITIONS = 0;
   static final int GET_NORMALS = 1;
   static final int GET_COLORS = 2;
   static final int GET_TEXCOORDS0 = 3;
   static final int GET_CROPX = 0;
   static final int GET_CROPY = 1;
   static final int GET_CROPWIDTH = 2;
   static final int GET_CROPHEIGHT = 3;
   static final int GET_MODEX = 0;
   static final int GET_MODEY = 1;
   static final int SETGET_COLORCLEAR = 0;
   static final int SETGET_DEPTHCLEAR = 1;
   static final int GET_NEAR = 0;
   static final int GET_FAR = 1;
   static final int SETGET_RENDERING = 0;
   static final int SETGET_PICKING = 1;
   static final int GET_CONSTANT = 0;
   static final int GET_LINEAR = 1;
   static final int GET_QUADRATIC = 2;
   static final int I2D_NO_FLAGS = 0;
   static final int I2D_DYNAMIC_TARGET = 5;

   private static native void _getDefs();

   static {
      _getDefs();
   }
}
