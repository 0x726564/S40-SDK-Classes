package javax.obex;

import java.io.IOException;

public interface HeaderSet {
   int COUNT = 192;
   int NAME = 1;
   int TYPE = 66;
   int LENGTH = 195;
   int TIME_ISO_8601 = 68;
   int TIME_4_BYTE = 196;
   int DESCRIPTION = 5;
   int TARGET = 70;
   int HTTP = 71;
   int WHO = 74;
   int OBJECT_CLASS = 79;
   int APPLICATION_PARAMETER = 76;

   void setHeader(int var1, Object var2);

   Object getHeader(int var1) throws IOException;

   int[] getHeaderList() throws IOException;

   void createAuthenticationChallenge(String var1, boolean var2, boolean var3);

   int getResponseCode() throws IOException;
}
