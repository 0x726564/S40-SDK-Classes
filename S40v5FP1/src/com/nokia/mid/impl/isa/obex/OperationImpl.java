package com.nokia.mid.impl.isa.obex;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.obex.HeaderSet;
import javax.obex.Operation;

public class OperationImpl implements Operation {
   private boolean isGetOperation;
   private int opcode;
   private boolean closed = false;
   private boolean userClosed = false;
   private boolean transactionClosed = false;
   private OperationImpl.ObexInputStream in;
   private OperationImpl.ObexOutputStream out;
   private AbstractObexConnection connection;
   private String contentType;
   private long contentLength = -1L;
   private static final int OBEX_HTTP_CONTINUE = 144;
   private int headerCount = 11;
   private boolean aborted = false;

   public OperationImpl(AbstractObexConnection connection) {
      this.connection = connection;
      connection.setInOperation(false);
      this.closed = true;
   }

   public OperationImpl(AbstractObexConnection connection, boolean isGetOperation) {
      if (connection != null && connection.getIncomingHeaders() != null) {
         this.connection = connection;
         this.isGetOperation = isGetOperation;
         this.opcode = isGetOperation ? 3 : 2;
         this.populateLengthAndType();
      } else {
         throw new NullPointerException();
      }
   }

   public void finish(int response) {
      if (!this.aborted) {
         HeaderSetImpl respHS = this.connection.getOutgoingHeaders();
         respHS.setResponseCode(response);
         Packet respP = new Packet();
         respP.packetSort = this.opcode;
         if (this.out != null && this.out.bufferIndex > 0) {
            byte[] bytesToSend = new byte[this.out.bufferIndex];
            System.arraycopy(this.out.buffer, 0, bytesToSend, 0, this.out.bufferIndex);
            respHS.setHeaderPrivate(73, bytesToSend);
         }

         respP.isFinal = true;

         try {
            this.sendPacket(respP);
         } catch (IOException var5) {
         }
      }

   }

   public void abort() throws IOException {
      this.ensureOpen(true);
      if (!this.connection.isClient()) {
         throw new IOException("Not allowed");
      } else {
         this.aborted = true;
         Packet reqP = new Packet();
         reqP.packetSort = 255;
         reqP.isFinal = true;
         this.sendPacket(reqP);
         this.receivePacket();
         this.close();
      }
   }

   public HeaderSet getReceivedHeaders() throws IOException {
      this.ensureOpen(false);
      return this.connection.getIncomingHeaders().clone();
   }

   public synchronized void sendHeaders(HeaderSet headers) throws IOException {
      this.ensureOpen(true);
      this.ensureNoErrorCodeReceived();
      if (headers == null) {
         throw new NullPointerException("headers cannot be null");
      } else {
         try {
            if (((HeaderSetImpl)headers).isReceivedHeaderSet()) {
               throw new IllegalArgumentException("HeaderSet not created by a call to createHeaderSet()");
            }
         } catch (ClassCastException var12) {
            throw new IllegalArgumentException("HeaderSet not created by a call to createHeaderSet()");
         }

         HeaderSetImpl headersToSend = this.connection.getOutgoingHeaders();
         int[] headersToSendList = headersToSend.getHeaderList();
         if (headersToSendList != null) {
            for(int a = 0; a < headersToSendList.length; ++a) {
               int headerID = headersToSendList[a];
               Object headerVal = headersToSend.getHeader(headerID);
               this.headerCount -= HeaderSetImpl.headerSize(headerID, HeaderSetImpl.getHeaderType(headerID, false), headerVal);
            }

            headersToSend = new HeaderSetImpl();
            this.connection.setOutgoingHeaders(headersToSend);
         }

         int[] headerList = headers.getHeaderList();
         HeaderSetImpl headersToAdd = new HeaderSetImpl();
         if (headerList != null) {
            for(int a = 0; a < headerList.length; ++a) {
               int headerID = headerList[a];
               Object headerVal = headers.getHeader(headerID);
               int i = HeaderSetImpl.headerSize(headerID, HeaderSetImpl.getHeaderType(headerID, false), headerVal);
               if (this.headerCount + i > this.connection.REMOTE_MAX_OBEX_PACKET_SIZE) {
                  headersToSend.includeHeaders(headersToAdd);
                  Packet outP = new Packet();
                  if (this.connection.isClient()) {
                     outP.isFinal = false;
                     outP.packetSort = this.opcode;
                  } else {
                     outP.isFinal = true;
                     headersToSend.setResponseCodePrivate(144);
                  }

                  this.sendPacket(outP);
                  Packet inP = this.receivePacket();
                  if (this.connection.isClient()) {
                     if (inP.respCode != 144) {
                        throw new IOException("Error while sending headers");
                     }
                  } else if (inP.packetSort != outP.packetSort) {
                     throw new IOException("Error while sending headers");
                  }

                  headersToAdd = new HeaderSetImpl();
                  headersToAdd.setHeader(headerID, headerVal);
                  headersToSend = this.connection.getOutgoingHeaders();
                  this.headerCount += i;
               } else {
                  headersToAdd.setHeader(headerID, headerVal);
                  this.headerCount += i;
               }
            }
         }

         if (headersToAdd.getHeaderList() != null) {
            headersToSend.includeHeaders(headersToAdd);
         }

      }
   }

   public int getResponseCode() throws IOException {
      if (!this.connection.isClient()) {
         throw new IOException("Not allowed");
      } else if (this.userClosed) {
         throw new IOException("operation is closed");
      } else {
         if (!this.closed) {
            this.closePutDelete();
            if (this.isGetOperation) {
               if (this.in != null && !this.in.streamClosed) {
                  this.in.close();
               }
            } else if (this.out != null && !this.out.streamClosed) {
               this.out.close();
            }

            this.transactionClosed = true;
         }

         if (this.connection.getIncomingHeaders() == null) {
            throw new IOException("Error retrieving received headers");
         } else {
            return this.connection.getIncomingHeaders().getResponseCode();
         }
      }
   }

   public String getType() {
      return this.contentType;
   }

   public String getEncoding() {
      return null;
   }

   public long getLength() {
      return this.contentLength;
   }

   public synchronized InputStream openInputStream() throws IOException {
      this.ensureOpen(true);
      if (this.in != null) {
         throw new IOException("InputStream already open");
      } else {
         HeaderSetImpl headers = this.connection.getIncomingHeaders();
         this.in = new OperationImpl.ObexInputStream();
         byte[] body = null;
         byte[] body;
         if ((body = (byte[])headers.getHeaderPrivate(72)) == null && (body = (byte[])headers.getHeaderPrivate(73)) != null) {
            this.in.endOfStream = true;
         }

         this.in.buffer = body;
         return this.in;
      }
   }

   public DataInputStream openDataInputStream() throws IOException {
      return new DataInputStream(this.openInputStream());
   }

   public synchronized OutputStream openOutputStream() throws IOException {
      this.ensureOpen(true);
      if (this.out != null) {
         throw new IOException("OutputStream already open");
      } else {
         Packet reqP;
         Packet respP;
         if (this.connection.isClient()) {
            if (!this.isGetOperation) {
               reqP = new Packet();
               reqP.packetSort = this.opcode;
               this.sendPacket(reqP);
               respP = this.connection.getPacket();
               if (respP.packetSort != this.opcode || respP.respCode != 144 || !respP.isFinal) {
                  this.connection.setInOperation(false);
                  this.closed = true;
               }
            }
         } else {
            reqP = new Packet();
            reqP.packetSort = this.opcode;
            reqP.isFinal = true;
            this.connection.getOutgoingHeaders().setResponseCodePrivate(144);
            this.sendPacket(reqP);
            respP = this.receivePacket();
            if (respP.packetSort != this.opcode) {
               throw new IOException("Unexpected packet received during operation");
            }
         }

         this.out = new OperationImpl.ObexOutputStream();
         return this.out;
      }
   }

   public DataOutputStream openDataOutputStream() throws IOException {
      return new DataOutputStream(this.openOutputStream());
   }

   private void closePutDelete() throws IOException {
      if (this.connection.isClient() && !this.closed && !this.isGetOperation && !this.aborted && this.out == null) {
         Packet reqP = new Packet();
         reqP.packetSort = 2;
         reqP.isFinal = true;
         this.sendPacket(reqP);
         Packet respP = this.receivePacket();
         if (respP.packetSort != 2 || !respP.isFinal) {
            throw new IOException("Error in DELETE operation");
         }
      }

   }

   public void close() throws IOException {
      this.userClosed = true;
      if (!this.closed) {
         this.closePutDelete();
         if (this.out != null && !this.out.streamClosed) {
            this.out.close();
         }

         this.closed = true;
         this.connection.setInOperation(false);
      }
   }

   private void ensureOpen(boolean ensureNotEnded) throws IOException {
      if (this.closed) {
         throw new IOException("Operation closed");
      } else if (ensureNotEnded && (this.transactionClosed || this.isGetOperation && this.in != null && this.in.streamClosed || !this.isGetOperation && this.out != null && this.out.streamClosed)) {
         throw new IOException("Transaction already ended");
      }
   }

   private void ensureNoErrorCodeReceived() throws IOException {
      if (this.connection.isClient() && this.connection.getIncomingHeaders() != null) {
         if (this.connection.getIncomingHeaders().getResponseCode() != 144 && this.connection.getIncomingHeaders().getResponseCode() != 160) {
            throw new IOException("Error code received");
         }
      }
   }

   private void sendPacket(Packet p) throws IOException {
      this.connection.sendPacket(p);
      this.headerCount = 11;
   }

   private Packet receivePacket() throws IOException {
      Packet p = this.connection.getPacket();
      this.populateLengthAndType();
      if (p.packetSort == 255 && !this.connection.isClient() && p.isFinal) {
         Packet respP = new Packet();
         respP.isFinal = true;
         respP.packetSort = 255;
         this.connection.getOutgoingHeaders().setResponseCode(160);
         this.sendPacket(respP);
         this.close();
         this.aborted = true;
      }

      return p;
   }

   private void populateLengthAndType() {
      try {
         Object value = this.connection.getIncomingHeaders().getHeader(195);
         if (value != null) {
            this.contentLength = (Long)value;
         }

         this.contentType = (String)this.connection.getIncomingHeaders().getHeader(66);
      } catch (IOException var2) {
      }

   }

   class ObexInputStream extends InputStream {
      private boolean endOfStream = false;
      private byte[] buffer;
      private int bufferIndex = 0;
      private boolean streamClosed;

      ObexInputStream() {
         this.bufferIndex = 0;
         this.streamClosed = false;
      }

      private void ensureStreamOpen() throws IOException {
         if (this.streamClosed) {
            throw new IOException("Stream closed");
         }
      }

      public int read(byte[] b, int off, int len) throws IOException {
         this.ensureStreamOpen();
         OperationImpl.this.ensureNoErrorCodeReceived();
         if (b == null) {
            throw new NullPointerException("The array cannot be null");
         } else if (off >= 0 && off <= b.length && len >= 0 && off + len <= b.length && off + len >= 0) {
            return len == 0 ? 0 : this.readFromBuffer(b, off, len);
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public int read(byte[] b) throws IOException {
         if (b == null) {
            throw new NullPointerException("The array cannot be null");
         } else {
            return this.read(b, 0, b.length);
         }
      }

      public int read() throws IOException {
         byte[] array = new byte[1];
         return this.read(array, 0, 1) == -1 ? -1 : (256 + array[0]) % 256;
      }

      private synchronized int readFromBuffer(byte[] b, int offset, int length) throws IOException {
         if (this.endOfStream && this.buffer != null && this.bufferIndex == this.buffer.length) {
            return -1;
         } else {
            if (this.buffer == null || this.buffer.length - this.bufferIndex == 0) {
               byte[] body = null;
               Packet dataPacket;
               HeaderSetImpl dataPacketHeaders;
               Packet resp;
               if (OperationImpl.this.connection.isClient()) {
                  resp = new Packet();
                  resp.packetSort = OperationImpl.this.opcode;
                  resp.isFinal = true;
                  OperationImpl.this.sendPacket(resp);
                  dataPacket = OperationImpl.this.receivePacket();
                  dataPacketHeaders = OperationImpl.this.connection.getIncomingHeaders();
                  switch(dataPacket.respCode) {
                  case 144:
                     body = (byte[])dataPacketHeaders.getHeaderPrivate(72);
                     break;
                  case 160:
                     if (!dataPacket.isFinal) {
                        throw new IOException("Error in operation");
                     }

                     this.endOfStream = true;
                     body = (byte[])dataPacketHeaders.getHeaderPrivate(73);
                     break;
                  default:
                     this.close();
                     throw new IOException("Error in operation - code is " + dataPacket.respCode);
                  }
               } else {
                  resp = new Packet();
                  resp.packetSort = OperationImpl.this.opcode;
                  resp.isFinal = true;

                  while(body == null) {
                     OperationImpl.this.connection.getOutgoingHeaders().setResponseCodePrivate(144);
                     OperationImpl.this.sendPacket(resp);
                     dataPacket = OperationImpl.this.receivePacket();
                     if (dataPacket.packetSort == 255) {
                        return -1;
                     }

                     dataPacketHeaders = new HeaderSetImpl(dataPacket.serializedHeaders);
                     if (dataPacket.packetSort != OperationImpl.this.opcode) {
                        this.close();
                        throw new IOException("Error in operation - packet sort " + dataPacket.packetSort);
                     }

                     if (dataPacket.isFinal) {
                        this.endOfStream = true;
                        body = (byte[])dataPacketHeaders.getHeaderPrivate(73);
                     } else {
                        body = (byte[])dataPacketHeaders.getHeaderPrivate(72);
                     }
                  }
               }

               if (body == null) {
                  return -1;
               }

               this.buffer = new byte[body.length];
               this.bufferIndex = 0;
               System.arraycopy(body, 0, this.buffer, 0, this.buffer.length);
            }

            int available = this.buffer.length - this.bufferIndex;
            int requested = length - offset;
            int actual = Math.min(available, requested);
            System.arraycopy(this.buffer, this.bufferIndex, b, offset, actual);
            this.bufferIndex += actual;
            return actual;
         }
      }

      public void close() throws IOException {
         this.ensureStreamOpen();
         this.streamClosed = true;
      }
   }

   class ObexOutputStream extends OutputStream {
      private byte[] buffer;
      private int bufferIndex = 0;
      private boolean streamClosed = false;

      private void ensureStreamOpen() throws IOException {
         if (this.streamClosed) {
            throw new IOException("Stream closed");
         }
      }

      public void close() throws IOException {
         if (!this.streamClosed) {
            if (OperationImpl.this.connection.isClient() && !OperationImpl.this.aborted) {
               this.flush(true);
            }

            this.streamClosed = true;
         }
      }

      public void flush() throws IOException {
         this.ensureStreamOpen();
         this.flush(false);
      }

      public void write(byte[] b, int off, int len) throws IOException {
         this.ensureStreamOpen();
         OperationImpl.this.ensureNoErrorCodeReceived();
         if (b == null) {
            throw new NullPointerException("The array cannot be null");
         } else if (off >= 0 && off <= b.length && len >= 0 && off + len <= b.length && off + len >= 0) {
            if (len != 0) {
               this.bufferData(b, off, len);
            }
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public void write(byte[] b) throws IOException {
         if (b == null) {
            throw new NullPointerException("The array cannot be null");
         } else {
            this.write(b, 0, b.length);
         }
      }

      public void write(int b) throws IOException {
         this.write(new byte[]{(byte)b}, 0, 1);
      }

      private synchronized void bufferData(byte[] data, int offset, int length) throws IOException {
         if (this.buffer == null) {
            this.buffer = new byte[OperationImpl.this.connection.REMOTE_MAX_OBEX_PACKET_SIZE];
            this.bufferIndex = 0;
         }

         while(length - offset > 0) {
            int available = Math.min(length - offset, this.buffer.length - this.bufferIndex - OperationImpl.this.headerCount);
            System.arraycopy(data, offset, this.buffer, this.bufferIndex, available);
            offset += available;
            this.bufferIndex += available;
            if (this.bufferIndex + OperationImpl.this.headerCount >= this.buffer.length - 1) {
               this.flush(false);
            }
         }

      }

      private synchronized void flush(boolean endOfStream) throws IOException {
         if (!endOfStream) {
            OperationImpl.this.ensureNoErrorCodeReceived();
         }

         HeaderSetImpl headersToSend = OperationImpl.this.connection.getOutgoingHeaders();
         Packet outP = new Packet();
         outP.packetSort = OperationImpl.this.opcode;
         int alreadySent = 0;
         byte[] bytesToSend;
         Packet inP;
         if (this.bufferIndex + OperationImpl.this.headerCount > OperationImpl.this.connection.REMOTE_MAX_OBEX_PACKET_SIZE) {
            alreadySent = OperationImpl.this.connection.REMOTE_MAX_OBEX_PACKET_SIZE - OperationImpl.this.headerCount;
            bytesToSend = new byte[alreadySent];
            System.arraycopy(this.buffer, 0, bytesToSend, 0, alreadySent);
            if (!OperationImpl.this.connection.isClient()) {
               headersToSend.setResponseCodePrivate(144);
            }

            headersToSend.setHeaderPrivate(72, bytesToSend);
            OperationImpl.this.sendPacket(outP);
            headersToSend = OperationImpl.this.connection.getOutgoingHeaders();
            inP = OperationImpl.this.receivePacket();
            if (OperationImpl.this.connection.isClient()) {
               if (OperationImpl.this.connection.getIncomingHeaders().getResponseCode() != 144) {
                  this.close();
                  throw new IOException("Error in operation");
               }
            } else if (inP.packetSort != OperationImpl.this.opcode && inP.packetSort != 255) {
               this.close();
               throw new IOException("Unexpected packet received during GET operation");
            }
         }

         bytesToSend = null;
         if (this.buffer != null && this.bufferIndex > 0) {
            int numBytesToSend = this.bufferIndex - alreadySent;
            bytesToSend = new byte[numBytesToSend];
            System.arraycopy(this.buffer, alreadySent, bytesToSend, 0, numBytesToSend);
            this.bufferIndex = 0;
         }

         if (endOfStream) {
            outP.isFinal = true;
            if (bytesToSend == null) {
               bytesToSend = new byte[0];
            }

            headersToSend.setHeaderPrivate(73, bytesToSend);
         } else if (bytesToSend != null) {
            headersToSend.setHeaderPrivate(72, bytesToSend);
         }

         if (!OperationImpl.this.connection.isClient()) {
            headersToSend.setResponseCodePrivate(144);
         }

         OperationImpl.this.sendPacket(outP);
         inP = OperationImpl.this.receivePacket();
         if (OperationImpl.this.connection.isClient()) {
            if (endOfStream) {
               if (!inP.isFinal) {
                  this.close();
                  throw new IOException("Error closing OBEX connection");
               }
            } else if (OperationImpl.this.connection.getIncomingHeaders().getResponseCode() != 144) {
               this.streamClosed = true;
            }
         } else if (inP.packetSort != OperationImpl.this.opcode && inP.packetSort != 255) {
            this.close();
            throw new IOException("Unexpected packet received during GET operation");
         }

      }
   }
}
