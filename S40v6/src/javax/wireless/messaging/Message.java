package javax.wireless.messaging;

import java.util.Date;

public interface Message {
   String getAddress();

   Date getTimestamp();

   void setAddress(String var1);
}
