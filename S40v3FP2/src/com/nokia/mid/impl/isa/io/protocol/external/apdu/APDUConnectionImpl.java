package com.nokia.mid.impl.isa.io.protocol.external.apdu;

import java.io.IOException;
import java.io.InterruptedIOException;
import javax.microedition.apdu.APDUConnection;

public abstract class APDUConnectionImpl implements APDUConnection {
   private static final int APDU_MIN_LENGTH = 4;
   private static final int APDU_MAX_LENGTH = 261;
   protected static final byte INS_ENVELOPE = -62;
   protected static final int APDU_CLA = 0;
   protected static final int APDU_INS = 1;
   protected int channelId;
   private boolean isClosed = false;

   public static APDUConnection getInstance(String var0) throws IOException {
      APDUConnection var1 = null;
      boolean var2 = var0.equals("SAT");
      if (var2) {
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
            byte[] var2 = IsaSession.INSTANCE.enterPin(this.getChannelId(), var1);
            return var2;
         } catch (InterruptedIOException var3) {
            this.isClosed = true;
            throw var3;
         }
      }
   }

   public byte[] changePin(int var1) throws IOException, InterruptedIOException {
      if (this.isClosed) {
         throw new IOException();
      } else {
         try {
            byte[] var2 = IsaSession.INSTANCE.changePin(this.getChannelId(), var1);
            return var2;
         } catch (InterruptedIOException var3) {
            this.isClosed = true;
            throw var3;
         }
      }
   }

   public byte[] disablePin(int var1) throws IOException, InterruptedIOException {
      if (this.isClosed) {
         throw new IOException();
      } else {
         try {
            byte[] var2 = IsaSession.INSTANCE.disablePin(this.getChannelId(), var1);
            return var2;
         } catch (InterruptedIOException var3) {
            this.isClosed = true;
            throw var3;
         }
      }
   }

   public byte[] enablePin(int var1) throws IOException, InterruptedIOException {
      if (this.isClosed) {
         throw new IOException();
      } else {
         try {
            byte[] var2 = IsaSession.INSTANCE.enablePin(this.getChannelId(), var1);
            return var2;
         } catch (InterruptedIOException var3) {
            this.isClosed = true;
            throw var3;
         }
      }
   }

   public byte[] unblockPin(int var1, int var2) throws IOException, InterruptedIOException {
      if (this.isClosed) {
         throw new IOException();
      } else {
         try {
            byte[] var3 = IsaSession.INSTANCE.unblockPin(this.getChannelId(), var1, var2);
            return var3;
         } catch (InterruptedIOException var4) {
            this.isClosed = true;
            throw var4;
         }
      }
   }

   public byte[] getATR() {
      byte[] var1 = null;
      if (!this.isClosed) {
         var1 = IsaSession.INSTANCE.getATR(this.getChannelId());
         if (var1 == null) {
            this.isClosed = true;
         }
      }

      return var1;
   }

   public void close() throws IOException {
      if (!this.isClosed) {
         this.isClosed = true;
         IsaSession.INSTANCE.close(this.getChannelId());
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
            byte[] var2 = IsaSession.INSTANCE.exchangeAPDU(this.getChannelId(), var1);
            return var2;
         } catch (InterruptedIOException var3) {
            this.isClosed = true;
            throw var3;
         }
      }
   }

   static void checkFormatApdu(byte[] var0) {
      if (var0 == null) {
         throw new IllegalArgumentException();
      } else if (var0.length < 4 || var0.length > 261) {
         throw new IllegalArgumentException();
      }
   }

   protected static boolean isEnvelope(byte[] var0) {
      boolean var1 = var0[1] == -62;
      return var1;
   }
}
