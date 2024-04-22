package com.nokia.mid.impl.isa.content;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.ContentConnection;

public class GenericContentImpl implements ContentConnection {
   private byte[] rawData;
   private String encoding;
   private String type;

   public GenericContentImpl(byte[] rawData, String encoding, String type) {
      this.rawData = rawData;
      this.encoding = encoding;
      this.type = type;
   }

   public String getEncoding() {
      return this.encoding;
   }

   public long getLength() {
      return (long)this.rawData.length;
   }

   public String getType() {
      return this.type;
   }

   public DataInputStream openDataInputStream() throws IOException {
      if (this.rawData != null) {
         return new DataInputStream(new ByteArrayInputStream(this.rawData));
      } else {
         throw new IOException("Connection closed.");
      }
   }

   public InputStream openInputStream() throws IOException {
      if (this.rawData != null) {
         return new ByteArrayInputStream(this.rawData);
      } else {
         throw new IOException("Connection closed.");
      }
   }

   public DataOutputStream openDataOutputStream() throws IOException {
      throw new IOException("Not supported.");
   }

   public OutputStream openOutputStream() throws IOException {
      throw new IOException("Not supported.");
   }

   public void close() throws IOException {
      this.rawData = null;
   }
}
