package com.nokia.mid.impl.isa.io.protocol.external.apdu;

import java.io.IOException;
import java.io.InterruptedIOException;
import javax.microedition.apdu.APDUConnection;

public abstract class APDUConnectionImpl implements APDUConnection {
   protected static final byte INS_ENVELOPE = -62;
   protected static final int APDU_CLA = 0;
   protected static final int APDU_INS = 1;
   protected int channelId;
   private boolean isClosed = false;

   public static APDUConnection getInstance(String var0) throws IOException {
      APDUConnection var1 = null;
      if (var0.equals("SAT")) {
         var1 = SATConnection.getInstance();
      } else {
         var1 = AIDConnection.getInstance(var0);
      }

      if (var1 != null) {
         return var1;
      } else {
         throw new IllegalArgumentException(var0);
      }
   }

   public byte[] enterPin(int var1) throws IOException, InterruptedIOException {
      if (this.isClosed) {
         throw new IOException();
      } else {
         try {
            return IsaSession.f(this.getChannelId(), var1);
         } catch (InterruptedIOException var2) {
            this.isClosed = true;
            throw var2;
         }
      }
   }

   public byte[] changePin(int var1) throws IOException, InterruptedIOException {
      if (this.isClosed) {
         throw new IOException();
      } else {
         try {
            return IsaSession.c(this.getChannelId(), var1);
         } catch (InterruptedIOException var2) {
            this.isClosed = true;
            throw var2;
         }
      }
   }

   public byte[] disablePin(int var1) throws IOException, InterruptedIOException {
      if (this.isClosed) {
         throw new IOException();
      } else {
         try {
            return IsaSession.d(this.getChannelId(), var1);
         } catch (InterruptedIOException var2) {
            this.isClosed = true;
            throw var2;
         }
      }
   }

   public byte[] enablePin(int var1) throws IOException, InterruptedIOException {
      if (this.isClosed) {
         throw new IOException();
      } else {
         try {
            return IsaSession.e(this.getChannelId(), var1);
         } catch (InterruptedIOException var2) {
            this.isClosed = true;
            throw var2;
         }
      }
   }

   public byte[] unblockPin(int var1, int var2) throws IOException, InterruptedIOException {
      if (this.isClosed) {
         throw new IOException();
      } else {
         try {
            return IsaSession.a(this.getChannelId(), var1, var2);
         } catch (InterruptedIOException var3) {
            this.isClosed = true;
            throw var3;
         }
      }
   }

   public byte[] getATR() {
      byte[] var1 = null;
      if (!this.isClosed && (var1 = IsaSession.b(this.getChannelId())) == null) {
         this.isClosed = true;
      }

      return var1;
   }

   public void close() throws IOException {
      if (!this.isClosed) {
         this.isClosed = true;
         IsaSession.INSTANCE.c(this.getChannelId());
      }

   }

   int getChannelId() {
      return this.channelId;
   }

   protected boolean isConnectionOpen() {
      return !this.isClosed;
   }

   protected String getStateString() {
      return this.isConnectionOpen() ? "Open" : "Closed";
   }

   protected byte[] commonExchangeAPDU(byte[] var1) throws IOException, InterruptedIOException {
      if (this.isClosed) {
         throw new IOException();
      } else {
         try {
            return IsaSession.a(this.getChannelId(), var1);
         } catch (InterruptedIOException var2) {
            this.isClosed = true;
            throw var2;
         }
      }
   }

   static void b(byte[] var0) {
      if (var0 == null) {
         throw new IllegalArgumentException();
      } else if (var0.length < 4 || var0.length > 261) {
         throw new IllegalArgumentException();
      }
   }

   protected static boolean isEnvelope(byte[] var0) {
      return var0[1] == -62;
   }
}
