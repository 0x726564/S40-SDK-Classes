package javax.microedition.media;

import com.nokia.mid.impl.isa.mmedia.BasicPlayer;
import com.nokia.mid.impl.isa.mmedia.MediaPrefs;
import com.nokia.mid.impl.isa.mmedia.ParsedLocator;
import com.nokia.mid.impl.isa.mmedia.audio.AudioOutImpl;
import com.nokia.mid.impl.isa.source_handling.JavaProducerSource;
import com.nokia.mid.impl.policy.PolicyAccess;
import com.nokia.mid.pri.PriAccess;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.media.protocol.DataSource;

public final class Manager {
   private static final String POLICY_MANAGER_API = "javax.microedition.media.Manager";
   public static final String TONE_DEVICE_LOCATOR = "device://tone";
   public static final String MIDI_DEVICE_LOCATOR = "device://midi";
   private static final String PL_ERR = "Can't create Player for: ";
   private static TimeBase sysTimeBase = null;

   private Manager() {
   }

   public static String[] getSupportedContentTypes(String var0) {
      return MediaPrefs.nGetFormats(var0);
   }

   public static String[] getSupportedProtocols(String var0) {
      return MediaPrefs.nGetProtocols(var0);
   }

   public static Player createPlayer(DataSource var0) throws IOException, MediaException {
      _checkPermission();
      if (var0 == null) {
         throw new IllegalArgumentException("Param is null");
      } else {
         ParsedLocator var1 = new ParsedLocator(var0);
         return instantiatePlayer(var1);
      }
   }

   public static Player createPlayer(InputStream var0, String var1) throws IOException, MediaException {
      _checkPermission();
      if (var0 == null) {
         throw new IllegalArgumentException("Param is null");
      } else {
         ParsedLocator var2 = new ParsedLocator(var0, var1);
         return instantiatePlayer(var2);
      }
   }

   public static Player createPlayer(String var0) throws IOException, MediaException {
      _checkPermission();
      if (var0 == null) {
         throw new IllegalArgumentException("Param is null");
      } else {
         ParsedLocator var1 = new ParsedLocator(new String(var0));
         return instantiatePlayer(var1);
      }
   }

   private static void _checkPermission() throws SecurityException {
      if (PriAccess.getInt(5) == 1 && !PolicyAccess.checkPermission("javax.microedition.media.Manager", "Allow Media Access?")) {
         throw new SecurityException();
      }
   }

   public static void playTone(int var0, int var1, int var2) throws MediaException {
      _checkPermission();
      if (var0 >= 0 && var0 <= 127 && var1 > 0) {
         if (var2 < 0) {
            var2 = 0;
         }

         if (var2 > 100) {
            var2 = 100;
         }

         if (!AudioOutImpl.nPlayTone(var0, var1, var2)) {
            throw new MediaException("device error");
         }
      } else {
         throw new IllegalArgumentException("bad param");
      }
   }

   public static TimeBase getSystemTimeBase() {
      if (sysTimeBase == null) {
         sysTimeBase = new SystemTimeBase();
      }

      return sysTimeBase;
   }

   private static Player instantiatePlayer(ParsedLocator var0) throws IOException, MediaException {
      String var2;
      if ((var2 = MediaPrefs.nGetPlayerForContentType(var0.contentType)) == null) {
         throw new MediaException("Can't create Player for: " + var0.contentType);
      } else {
         JavaProducerSource var1 = var0.connect();

         try {
            BasicPlayer var3 = (BasicPlayer)Class.forName(var2).newInstance();
            var3.setParsedLocator(var0);
            var3.setDataSource(var1);
            return var3;
         } catch (Exception var4) {
            throw new MediaException("Can't create Player for: " + var4.getMessage());
         }
      }
   }

   private static native void nInitJSR135();

   static {
      nInitJSR135();
   }
}
