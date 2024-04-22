package javax.wireless.messaging;

import com.nokia.mid.impl.isa.i18n.CharsetConv;
import com.nokia.mid.impl.isa.io.protocol.external.mms.Protocol;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MessagePart {
   private byte[] content;
   private String mimeType;
   private String contentId;
   private String contentLocation;
   private String encoding;

   public MessagePart(byte[] var1, int var2, int var3, String var4, String var5, String var6, String var7) throws SizeExceededException {
      if (var3 >= 0 && (var1 == null || var3 + var2 <= var1.length) && var2 >= 0) {
         if (var3 > Protocol.MAX_MULTIPART_MESSAGE_SIZE) {
            throw new SizeExceededException("MessagePart exceeds the max size");
         } else {
            this.checkValidityOfCommonParameters(var4, var5, var6, var7);
            if (var1 != null && var3 >= 0) {
               try {
                  this.content = new byte[var3];
                  if (var3 > 0) {
                     System.arraycopy(var1, var2, this.content, 0, var3);
                  }
               } catch (OutOfMemoryError var9) {
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
      this.checkValidityOfCommonParameters(var2, var3, var4, var5);
      if (var1 != null) {
         try {
            byte[] var6 = new byte[1024];
            ByteArrayOutputStream var7 = new ByteArrayOutputStream();

            while(true) {
               while(true) {
                  int var8 = var1.read(var6);
                  if (var8 > 0) {
                     var7.write(var6, 0, var8);
                  } else if (var8 == -1) {
                     if (var7.size() > Protocol.MAX_MULTIPART_MESSAGE_SIZE) {
                        throw new SizeExceededException("MessagePart exceeds the max size");
                     }

                     this.content = var7.toByteArray();
                     return;
                  }
               }
            }
         } catch (OutOfMemoryError var9) {
            throw new SizeExceededException("Not enough memory to create this MessagePart");
         }
      }
   }

   public byte[] getContent() {
      byte[] var1 = null;
      if (this.content != null) {
         var1 = new byte[this.content.length];
         System.arraycopy(this.content, 0, var1, 0, this.content.length);
      }

      return var1;
   }

   public InputStream getContentAsStream() {
      return this.content != null ? new ByteArrayInputStream(this.content) : new ByteArrayInputStream(new byte[0]);
   }

   public String getContentID() {
      return this.contentId;
   }

   public String getContentLocation() {
      return this.contentLocation;
   }

   public String getEncoding() {
      return this.encoding;
   }

   public int getLength() {
      return this.content != null ? this.content.length : 0;
   }

   public String getMIMEType() {
      return this.mimeType;
   }

   private void checkValidityOfCommonParameters(String var1, String var2, String var3, String var4) {
      if (var1 != null && var2 != null) {
         if (!this.isAsciiString(var2)) {
            throw new IllegalArgumentException("Invalid \"content id\"");
         } else if (var3 != null && !this.isAsciiString(var3)) {
            throw new IllegalArgumentException("Invalid \"content location\"");
         } else {
            if (var4 != null) {
               byte[] var5 = CharsetConv.isSupportedEncoding(var4);
               if (var5 == null) {
                  throw new IllegalArgumentException("Unsupported encoding");
               }
            }

            this.mimeType = var1;
            this.contentId = var2;
            this.contentLocation = var3;
            this.encoding = var4;
         }
      } else {
         throw new IllegalArgumentException("Invalid \"mime type\" or \"content id\" ");
      }
   }

   private boolean isAsciiString(String var1) {
      for(int var2 = 0; var2 < var1.length(); ++var2) {
         char var3 = var1.charAt(var2);
         if (var3 < ' ' || var3 > 127) {
            return false;
         }
      }

      return true;
   }
}
