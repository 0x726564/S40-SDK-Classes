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

   public static String[] getSupportedContentTypes(String protocol) {
      return MediaPrefs.nGetFormats(protocol);
   }

   public static String[] getSupportedProtocols(String contentType) {
      return MediaPrefs.nGetProtocols(contentType);
   }

   public static Player createPlayer(DataSource source) throws IOException, MediaException {
      _checkPermission();
      if (source == null) {
         throw new IllegalArgumentException("Param is null");
      } else {
         ParsedLocator parsedLoc = new ParsedLocator(source);
         return instantiatePlayer(parsedLoc);
      }
   }

   public static Player createPlayer(InputStream stream, String type) throws IOException, MediaException {
      _checkPermission();
      if (stream == null) {
         throw new IllegalArgumentException("Param is null");
      } else {
         ParsedLocator parsedLoc = new ParsedLocator(stream, type);
         return instantiatePlayer(parsedLoc);
      }
   }

   public static Player createPlayer(String locator) throws IOException, MediaException {
      _checkPermission();
      if (locator == null) {
         throw new IllegalArgumentException("Param is null");
      } else {
         ParsedLocator parsedLoc = new ParsedLocator(new String(locator));
         return instantiatePlayer(parsedLoc);
      }
   }

   private static void _checkPermission() throws SecurityException {
      if (PriAccess.getInt(5) == 1 && !PolicyAccess.checkPermission("javax.microedition.media.Manager", "Allow Media Access?")) {
         throw new SecurityException();
      }
   }

   public static void playTone(int note, int duration, int volume) throws MediaException {
      _checkPermission();
      if (note >= 0 && note <= 127 && duration > 0) {
         if (volume < 0) {
            volume = 0;
         }

         if (volume > 100) {
            volume = 100;
         }

         if (!AudioOutImpl.nPlayTone(note, duration, volume)) {
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

   private static Player instantiatePlayer(ParsedLocator loc) throws IOException, MediaException, SecurityException {
      String className;
      if ((className = MediaPrefs.nGetAppropriatePlayer(loc.contentType, loc.getLocatorType())) == null) {
         throw new MediaException("Can't create Player for: " + loc.contentType);
      } else {
         JavaProducerSource source = loc.connect();

         try {
            BasicPlayer pl = (BasicPlayer)Class.forName(className).newInstance();
            pl.setParsedLocator(loc);
            pl.setDataSource(source);
            return pl;
         } catch (SecurityException var4) {
            throw new SecurityException("Can't create Player for: " + var4.getMessage());
         } catch (Exception var5) {
            throw new MediaException("Can't create Player for: " + var5.getMessage());
         }
      }
   }

   private static native void nInitJSR135();

   static {
      nInitJSR135();
   }
}
