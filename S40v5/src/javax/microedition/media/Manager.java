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
   public static final String TONE_DEVICE_LOCATOR = "device://tone";
   public static final String MIDI_DEVICE_LOCATOR = "device://midi";
   private static TimeBase a = null;

   private Manager() {
   }

   public static String[] getSupportedContentTypes(String var0) {
      return MediaPrefs.nGetFormats(var0);
   }

   public static String[] getSupportedProtocols(String var0) {
      return MediaPrefs.nGetProtocols(var0);
   }

   public static Player createPlayer(DataSource var0) throws IOException, MediaException {
      a();
      if (var0 == null) {
         throw new IllegalArgumentException("Param is null");
      } else {
         return a(new ParsedLocator(var0));
      }
   }

   public static Player createPlayer(InputStream var0, String var1) throws IOException, MediaException {
      a();
      if (var0 == null) {
         throw new IllegalArgumentException("Param is null");
      } else {
         return a(new ParsedLocator(var0, var1));
      }
   }

   public static Player createPlayer(String var0) throws IOException, MediaException {
      a();
      if (var0 == null) {
         throw new IllegalArgumentException("Param is null");
      } else {
         return a(new ParsedLocator(new String(var0)));
      }
   }

   private static void a() throws SecurityException {
      if (PriAccess.getInt(5) == 1 && !PolicyAccess.checkPermission("javax.microedition.media.Manager", "Allow Media Access?")) {
         throw new SecurityException();
      }
   }

   public static void playTone(int var0, int var1, int var2) throws MediaException {
      a();
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
      if (a == null) {
         a = new SystemTimeBase();
      }

      return a;
   }

   private static Player a(ParsedLocator var0) throws IOException, MediaException, SecurityException {
      String var2;
      if ((var2 = MediaPrefs.nGetAppropriatePlayer(var0.contentType, var0.getLocatorType())) == null) {
         throw new MediaException("Can't create Player for: " + var0.contentType);
      } else {
         JavaProducerSource var1 = var0.connect();

         try {
            BasicPlayer var5;
            (var5 = (BasicPlayer)Class.forName(var2).newInstance()).setParsedLocator(var0);
            var5.setDataSource(var1);
            return var5;
         } catch (SecurityException var3) {
            throw new SecurityException("Can't create Player for: " + var3.getMessage());
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
