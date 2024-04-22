package com.nokia.mid.impl.jms.core;

import com.nokia.mid.impl.isa.ui.DisplayAccess;
import com.nokia.mid.impl.isa.ui.InitJALM;
import com.nokia.mid.impl.isa.ui.MIDletAccess;
import com.nokia.mid.impl.isa.ui.gdi.Pixmap;
import java.io.IOException;
import javax.microedition.lcdui.Image;

public class MIDletRegistry {
   public static final int MIDLET_ATTRIB_HASH_OF_PUBLIC_KEY = 1;
   public static final int MIDLET_ATTRIB_SECURITY_PROTECTION_DOMAIN = 2;
   public static final int MIDLET_ATTRIB_ROOT_CERTIFICATE_ORGANISATION = 3;
   public static final int MIDLET_ATTRIB_SIGNER_CERTIFICATE_ORGANISATION = 4;
   private static MIDletRegistry m_registry;

   private MIDletRegistry() {
   }

   public static MIDletRegistry getMIDletRegistry() {
      if (m_registry == null) {
         m_registry = new MIDletRegistry();
      }

      return m_registry;
   }

   public MIDletSuite findMIDletSuite(String vendorName, String suiteName) {
      if (vendorName != null && suiteName != null) {
         int midletId = this.findMIDletSuiteByVendorSuite(vendorName, suiteName);
         return midletId <= 0 ? null : MIDletSuite.getMIDletSuite(midletId);
      } else {
         return null;
      }
   }

   public native String getMIDletLocation(int var1);

   public native int getMIDletStatus(int var1);

   public byte[] getMIDletIcon(int midletId) {
      MIDletSuite suite = MIDletSuite.getMIDletSuite(midletId);
      return suite == null ? null : suite.getIcon();
   }

   public byte[] getMIDletIcon(int midletId, int midletNum) {
      MIDletSuite suite = MIDletSuite.getMIDletSuite(midletId);
      return suite == null ? null : suite.getIcon(midletNum);
   }

   public Image getDefaultIcon() {
      MIDletAccess ma = InitJALM.s_getMIDletAccessor();
      DisplayAccess da = ma.getDisplayAccessor();
      return da.createImage(Pixmap.createPixmap(0));
   }

   public long getSpaceUsed(int midletId) {
      MIDletSuite suite = MIDletSuite.getMIDletSuite(midletId);
      return suite == null ? 0L : suite.getSpaceUsed();
   }

   public String[] getMIDletNames(int midletId) {
      MIDletSuite suite = MIDletSuite.getMIDletSuite(midletId);
      return suite == null ? null : suite.getMIDletNames();
   }

   public int getMIDletType(int midletId) {
      MIDletSuite suite = MIDletSuite.getMIDletSuite(midletId);
      return suite == null ? 0 : suite.getMIDletType();
   }

   public String getJADProperty(int midletId, String key) {
      MIDletSuite suite = MIDletSuite.getMIDletSuite(midletId);
      return suite == null ? null : suite.getJADProperty(key);
   }

   public byte[] getMIDletAttribute(int midletId, int key) throws IOException {
      return this.getMIDletAttribute0(midletId, key);
   }

   /** @deprecated */
   public void setMIDletAttribute(int midletID, int key, byte[] value, int offset, int length) throws IOException {
      if (offset >= 0 && length >= 0 && length <= value.length && offset <= length) {
         this.setMIDletAttribute0(midletID, key, value, offset, length);
      } else {
         throw new IllegalArgumentException("Offset and length parameters are illegal.");
      }
   }

   private native byte[] getMIDletAttribute0(int var1, int var2) throws IOException;

   native int findMIDletSuiteByVendorSuite(String var1, String var2);

   native int findMIDletSuiteByLocation(String var1);

   private native void setMIDletAttribute0(int var1, int var2, byte[] var3, int var4, int var5) throws IOException;
}
