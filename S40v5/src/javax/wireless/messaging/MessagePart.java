package javax.wireless.messaging;

import com.nokia.mid.impl.isa.i18n.CharsetConv;
import com.nokia.mid.impl.isa.io.protocol.external.mms.Protocol;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MessagePart {
   private byte[] a;
   private String mimeType;
   private String b;
   private String c;
   private String encoding;

   public MessagePart(byte[] var1, int var2, int var3, String var4, String var5, String var6, String var7) throws SizeExceededException {
      if (var3 >= 0 && (var1 == null || var3 + var2 <= var1.length) && var2 >= 0) {
         if (var3 > Protocol.MAX_MULTIPART_MESSAGE_SIZE) {
            throw new SizeExceededException("MessagePart exceeds the max size");
         } else {
            this.a(var4, var5, var6, var7);
            if (var1 != null && var3 >= 0) {
               try {
                  this.a = new byte[var3];
                  if (var3 > 0) {
                     System.arraycopy(var1, var2, this.a, 0, var3);
                  }

               } catch (OutOfMemoryError var8) {
                  throw new SizeExceededException("Not enough memory to create this MessagePart");
               }
            }
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public MessagePart(byte[] var1, String var2, String var3, String var4, String var5) throws SizeExceededException {
      this(var1, 0, var1 != null ? var1.length : 0, var2, var3, var4, var5);
   }

   public MessagePart(InputStream var1, String var2, String var3, String var4, String var5) throws IOException, SizeExceededException {
      this.a(var2, var3, var4, var5);
      if (var1 != null) {
         try {
            byte[] var7 = new byte[1024];
            ByteArrayOutputStream var8 = new ByteArrayOutputStream();

            while(true) {
               int var9;
               while((var9 = var1.read(var7)) <= 0) {
                  if (var9 == -1) {
                     if (var8.size() > Protocol.MAX_MULTIPART_MESSAGE_SIZE) {
                        throw new SizeExceededException("MessagePart exceeds the max size");
                     }

                     this.a = var8.toByteArray();
                     return;
                  }
               }

               var8.write(var7, 0, var9);
            }
         } catch (OutOfMemoryError var6) {
            throw new SizeExceededException("Not enough memory to create this MessagePart");
         }
      }
   }

   public byte[] getContent() {
      byte[] var1 = null;
      if (this.a != null) {
         var1 = new byte[this.a.length];
         System.arraycopy(this.a, 0, var1, 0, this.a.length);
      }

      return var1;
   }

   public InputStream getContentAsStream() {
      return this.a != null ? new ByteArrayInputStream(this.a) : new ByteArrayInputStream(new byte[0]);
   }

   public String getContentID() {
      return this.b;
   }

   public String getContentLocation() {
      return this.c;
   }

   public String getEncoding() {
      return this.encoding;
   }

   public int getLength() {
      return this.a != null ? this.a.length : 0;
   }

   public String getMIMEType() {
      return this.mimeType;
   }

   private void a(String var1, String var2, String var3, String var4) {
      if (var1 != null && var2 != null) {
         if (!a(var2)) {
            throw new IllegalArgumentException("Invalid \"content id\"");
         } else if (var3 != null && !a(var3)) {
            throw new IllegalArgumentException("Invalid \"content location\"");
         } else if (var4 != null && CharsetConv.isSupportedEncoding(var4) == null) {
            throw new IllegalArgumentException("Unsupported encoding");
         } else {
            this.mimeType = var1;
            this.b = var2;
            this.c = var3;
            this.encoding = var4;
         }
      } else {
         throw new IllegalArgumentException("Invalid \"mime type\" or \"content id\" ");
      }
   }

   private static boolean a(String var0) {
      for(int var1 = 0; var1 < var0.length(); ++var1) {
         char var2;
         if ((var2 = var0.charAt(var1)) < ' ' || var2 > 127) {
            return false;
         }
      }

      return true;
   }
}
