package com.nokia.mid.impl.jms.core;

import com.nokia.mid.impl.isa.ui.InitJALM;
import com.nokia.mid.impl.isa.ui.gdi.Pixmap;
import java.io.IOException;
import javax.microedition.lcdui.Image;

public class MIDletRegistry {
   public static final int MIDLET_ATTRIB_HASH_OF_PUBLIC_KEY = 1;
   public static final int MIDLET_ATTRIB_SECURITY_PROTECTION_DOMAIN = 2;
   public static final int MIDLET_ATTRIB_ROOT_CERTIFICATE_ORGANISATION = 3;
   public static final int MIDLET_ATTRIB_SIGNER_CERTIFICATE_ORGANISATION = 4;
   private static MIDletRegistry gf;

   private MIDletRegistry() {
   }

   public static MIDletRegistry getMIDletRegistry() {
      if (gf == null) {
         gf = new MIDletRegistry();
      }

      return gf;
   }

   public MIDletSuite findMIDletSuite(String var1, String var2) {
      if (var1 != null && var2 != null) {
         int var3;
         return (var3 = this.findMIDletSuiteByVendorSuite(var1, var2)) <= 0 ? null : MIDletSuite.getMIDletSuite(var3);
      } else {
         return null;
      }
   }

   public native String getMIDletLocation(int var1);

   public native int getMIDletStatus(int var1);

   public byte[] getMIDletIcon(int var1) {
      MIDletSuite var2;
      return (var2 = MIDletSuite.getMIDletSuite(var1)) == null ? null : var2.getIcon();
   }

   public byte[] getMIDletIcon(int var1, int var2) {
      MIDletSuite var3;
      return (var3 = MIDletSuite.getMIDletSuite(var1)) == null ? null : var3.getIcon(var2);
   }

   public Image getDefaultIcon() {
      return InitJALM.s_getMIDletAccessor().getDisplayAccessor().createImage(Pixmap.createPixmap(0));
   }

   public long getSpaceUsed(int var1) {
      MIDletSuite var2;
      return (var2 = MIDletSuite.getMIDletSuite(var1)) == null ? 0L : var2.getSpaceUsed();
   }

   public String[] getMIDletNames(int var1) {
      MIDletSuite var2;
      return (var2 = MIDletSuite.getMIDletSuite(var1)) == null ? null : var2.getMIDletNames();
   }

   public int getMIDletType(int var1) {
      MIDletSuite var2;
      return (var2 = MIDletSuite.getMIDletSuite(var1)) == null ? 0 : var2.getMIDletType();
   }

   public String getJADProperty(int var1, String var2) {
      MIDletSuite var3;
      return (var3 = MIDletSuite.getMIDletSuite(var1)) == null ? null : var3.getJADProperty(var2);
   }

   public byte[] getMIDletAttribute(int var1, int var2) throws IOException {
      return this.getMIDletAttribute0(var1, var2);
   }

   /** @deprecated */
   public void setMIDletAttribute(int var1, int var2, byte[] var3, int var4, int var5) throws IOException {
      if (var4 >= 0 && var5 >= 0 && var5 <= var3.length && var4 <= var5) {
         this.setMIDletAttribute0(var1, var2, var3, var4, var5);
      } else {
         throw new IllegalArgumentException("Offset and length parameters are illegal.");
      }
   }

   private native byte[] getMIDletAttribute0(int var1, int var2) throws IOException;

   native int findMIDletSuiteByVendorSuite(String var1, String var2);

   native int findMIDletSuiteByLocation(String var1);

   private native void setMIDletAttribute0(int var1, int var2, byte[] var3, int var4, int var5) throws IOException;
}
