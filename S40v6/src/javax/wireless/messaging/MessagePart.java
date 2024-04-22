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

   public MessagePart(byte[] cont, int offset, int length, String mimeType, String contentId, String contentLocation, String enc) throws SizeExceededException {
      if (length >= 0 && (cont == null || length + offset <= cont.length) && offset >= 0) {
         if (length > Protocol.MAX_MULTIPART_MESSAGE_SIZE) {
            throw new SizeExceededException("MessagePart exceeds the max size");
         } else {
            this.checkValidityOfCommonParameters(mimeType, contentId, contentLocation, enc);
            if (cont != null && length >= 0) {
               try {
                  this.content = new byte[length];
                  if (length > 0) {
                     System.arraycopy(cont, offset, this.content, 0, length);
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

   public MessagePart(byte[] cont, String mimeType, String contentId, String contentLocation, String enc) throws SizeExceededException {
      this(cont, 0, cont != null ? cont.length : 0, mimeType, contentId, contentLocation, enc);
   }

   public MessagePart(InputStream is, String mimeType, String contentId, String contentLocation, String enc) throws IOException, SizeExceededException {
      this.checkValidityOfCommonParameters(mimeType, contentId, contentLocation, enc);
      if (is != null) {
         try {
            byte[] bytesToRead = new byte[1024];
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            while(true) {
               while(true) {
                  int numberOfBytesRead = is.read(bytesToRead);
                  if (numberOfBytesRead > 0) {
                     bos.write(bytesToRead, 0, numberOfBytesRead);
                  } else if (numberOfBytesRead == -1) {
                     if (bos.size() > Protocol.MAX_MULTIPART_MESSAGE_SIZE) {
                        throw new SizeExceededException("MessagePart exceeds the max size");
                     }

                     this.content = bos.toByteArray();
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
      byte[] contentToReturn = null;
      if (this.content != null) {
         contentToReturn = new byte[this.content.length];
         System.arraycopy(this.content, 0, contentToReturn, 0, this.content.length);
      }

      return contentToReturn;
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

   private void checkValidityOfCommonParameters(String mType, String contId, String contLoc, String enc) {
      if (mType != null && contId != null) {
         if (!this.isAsciiString(contId)) {
            throw new IllegalArgumentException("Invalid \"content id\"");
         } else if (contLoc != null && !this.isAsciiString(contLoc)) {
            throw new IllegalArgumentException("Invalid \"content location\"");
         } else {
            if (enc != null) {
               byte[] enc_ascii = CharsetConv.isSupportedEncoding(enc);
               if (enc_ascii == null) {
                  throw new IllegalArgumentException("Unsupported encoding");
               }
            }

            this.mimeType = mType;
            this.contentId = contId;
            this.contentLocation = contLoc;
            this.encoding = enc;
         }
      } else {
         throw new IllegalArgumentException("Invalid \"mime type\" or \"content id\" ");
      }
   }

   private boolean isAsciiString(String str) {
      for(int i = 0; i < str.length(); ++i) {
         char c = str.charAt(i);
         if (c < ' ' || c > 127) {
            return false;
         }
      }

      return true;
   }
}
