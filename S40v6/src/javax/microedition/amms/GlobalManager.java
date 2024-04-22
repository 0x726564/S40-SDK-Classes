package javax.microedition.amms;

import com.nokia.mid.impl.isa.mmedia.ControlManager;
import javax.microedition.media.Control;
import javax.microedition.media.Controllable;
import javax.microedition.media.MediaException;

public class GlobalManager {
   private static Spectator spectatorImpl;
   private static final ControlManager controlManager = new ControlManager();
   private static final String UNSUPPORTED = "Unsupported";
   private static final String defaultPackage = "javax.microedition.media.control";
   private static final String audioEffectPackage = "javax.microedition.amms.control.audioeffect";

   public static EffectModule createEffectModule() throws MediaException {
      throw new MediaException("Unsupported");
   }

   public static MediaProcessor createMediaProcessor(String inputType) throws MediaException {
      throw new MediaException("Unsupported");
   }

   public static SoundSource3D createSoundSource3D() throws MediaException {
      try {
         getSpectator();
         Class sndClass = Class.forName("com.nokia.mid.impl.isa.amms.audio.SoundSource3DImpl");
         SoundSource3D sndSrc3D = (SoundSource3D)sndClass.newInstance();
         return sndSrc3D;
      } catch (Exception var2) {
         throw new MediaException("Unsupported");
      }
   }

   public static Control getControl(String controlType) {
      String pkgName = "javax.microedition.media.control";
      if (controlType == null) {
         throw new IllegalArgumentException();
      } else {
         if (controlType.startsWith("javax.microedition.media.control")) {
            controlType = controlType.substring("javax.microedition.media.control".length() + 1);
         } else if (controlType.startsWith("javax.microedition.amms.control.audioeffect")) {
            controlType = controlType.substring("javax.microedition.amms.control.audioeffect".length() + 1);
            pkgName = "javax.microedition.amms.control.audioeffect";
         }

         return controlManager.getControl(pkgName, controlType);
      }
   }

   public static Control[] getControls() {
      return controlManager.getControls();
   }

   public static Spectator getSpectator() throws MediaException {
      if (spectatorImpl == null) {
         try {
            Class specClass = Class.forName("com.nokia.mid.impl.isa.amms.audio.SpectatorImpl");
            spectatorImpl = new Spectator((Controllable)specClass.newInstance());
         } catch (ClassNotFoundException var1) {
            throw new MediaException("Unsupported");
         } catch (IllegalAccessException var2) {
         } catch (InstantiationException var3) {
         }
      }

      return spectatorImpl;
   }

   public static String[] getSupportedMediaProcessorInputTypes() {
      return new String[0];
   }

   public static String[] getSupportedSoundSource3DPlayerTypes() {
      return nGetSupportedSoundSource3DPlayerTypes();
   }

   private GlobalManager() {
   }

   private static native String[] nGetSupportedControls();

   private static native String[] nGetSupportedSoundSource3DPlayerTypes();

   static {
      controlManager.addControlsFromEncodedStrings(nGetSupportedControls());
   }
}
