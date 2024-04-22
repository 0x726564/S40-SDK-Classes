package javax.microedition.amms;

import com.nokia.mid.impl.isa.mmedia.ControlManager;
import javax.microedition.media.Control;
import javax.microedition.media.Controllable;
import javax.microedition.media.MediaException;

public class GlobalManager {
   private static Spectator b;
   private static final ControlManager controlManager;

   public static EffectModule createEffectModule() throws MediaException {
      throw new MediaException("Unsupported");
   }

   public static MediaProcessor createMediaProcessor(String var0) throws MediaException {
      throw new MediaException("Unsupported");
   }

   public static SoundSource3D createSoundSource3D() throws MediaException {
      try {
         getSpectator();
         SoundSource3D var0 = (SoundSource3D)Class.forName("com.nokia.mid.impl.isa.amms.audio.SoundSource3DImpl").newInstance();
         return var0;
      } catch (Exception var2) {
         throw new MediaException("Unsupported");
      }
   }

   public static Control getControl(String var0) {
      String var1 = "javax.microedition.media.control";
      if (var0 == null) {
         throw new IllegalArgumentException();
      } else {
         if (var0.startsWith("javax.microedition.media.control")) {
            var0 = var0.substring("javax.microedition.media.control".length() + 1);
         } else if (var0.startsWith("javax.microedition.amms.control.audioeffect")) {
            var0 = var0.substring("javax.microedition.amms.control.audioeffect".length() + 1);
            var1 = "javax.microedition.amms.control.audioeffect";
         }

         return controlManager.getControl(var1, var0);
      }
   }

   public static Control[] getControls() {
      return controlManager.getControls();
   }

   public static Spectator getSpectator() throws MediaException {
      if (b == null) {
         try {
            Class var0 = Class.forName("com.nokia.mid.impl.isa.amms.audio.SpectatorImpl");
            b = new Spectator((Controllable)var0.newInstance());
         } catch (ClassNotFoundException var1) {
            throw new MediaException("Unsupported");
         } catch (IllegalAccessException var2) {
         } catch (InstantiationException var3) {
         }
      }

      return b;
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
      (controlManager = new ControlManager()).addControlsFromEncodedStrings(nGetSupportedControls());
   }
}
