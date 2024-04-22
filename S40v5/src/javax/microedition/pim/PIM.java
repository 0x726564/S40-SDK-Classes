package javax.microedition.pim;

import com.nokia.mid.impl.isa.pim.PIMImp;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public abstract class PIM {
   public static final int CONTACT_LIST = 1;
   public static final int EVENT_LIST = 2;
   public static final int TODO_LIST = 3;
   public static final int READ_ONLY = 1;
   public static final int WRITE_ONLY = 2;
   public static final int READ_WRITE = 3;

   protected PIM() {
   }

   public static PIM getInstance() {
      return PIMImp.getInstance();
   }

   public abstract PIMList openPIMList(int var1, int var2) throws PIMException;

   public abstract PIMList openPIMList(int var1, int var2, String var3) throws PIMException;

   public abstract String[] listPIMLists(int var1);

   public abstract PIMItem[] fromSerialFormat(InputStream var1, String var2) throws PIMException, UnsupportedEncodingException;

   public abstract void toSerialFormat(PIMItem var1, OutputStream var2, String var3, String var4) throws PIMException, UnsupportedEncodingException;

   public abstract String[] supportedSerialFormats(int var1);
}
