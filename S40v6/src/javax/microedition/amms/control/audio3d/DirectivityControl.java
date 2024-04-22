package javax.microedition.amms.control.audio3d;

public interface DirectivityControl extends OrientationControl {
   int[] getParameters();

   void setParameters(int var1, int var2, int var3);
}
