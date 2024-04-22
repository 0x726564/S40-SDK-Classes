package javax.wireless.messaging;

import java.io.IOException;
import java.io.InterruptedIOException;
import javax.microedition.io.Connection;

public interface MessageConnection extends Connection {
   String BINARY_MESSAGE = "binary";
   String TEXT_MESSAGE = "text";
   String MULTIPART_MESSAGE = "multipart";

   Message newMessage(String var1);

   Message newMessage(String var1, String var2);

   int numberOfSegments(Message var1);

   Message receive() throws IOException, InterruptedIOException;

   void send(Message var1) throws IOException, InterruptedIOException;

   void setMessageListener(MessageListener var1) throws IOException;
}
