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

   public static APDUConnection getInstance(String target) throws IOException {
      APDUConnection connectionCreated = null;
      boolean boSATConenction = target.equals("SAT");
      if (boSATConenction) {
         connectionCreated = SATConnection.getInstance();
      } else {
         connectionCreated = AIDConnection.getInstance(target);
      }

      if (connectionCreated != null) {
         return connectionCreated;
      } else {
         throw new IllegalArgumentException(target);
      }
   }

   public byte[] enterPin(int pinID) throws IOException, InterruptedIOException {
      if (this.isClosed) {
         throw new IOException();
      } else {
         try {
            byte[] resp_apdu = IsaSession.INSTANCE.enterPin(this.getChannelId(), pinID);
            return resp_apdu;
         } catch (InterruptedIOException var3) {
            this.isClosed = true;
            throw var3;
         }
      }
   }

   public byte[] changePin(int pinID) throws IOException, InterruptedIOException {
      if (this.isClosed) {
         throw new IOException();
      } else {
         try {
            byte[] resp_apdu = IsaSession.INSTANCE.changePin(this.getChannelId(), pinID);
            return resp_apdu;
         } catch (InterruptedIOException var3) {
            this.isClosed = true;
            throw var3;
         }
      }
   }

   public byte[] disablePin(int pinID) throws IOException, InterruptedIOException {
      if (this.isClosed) {
         throw new IOException();
      } else {
         try {
            byte[] resp_apdu = IsaSession.INSTANCE.disablePin(this.getChannelId(), pinID);
            return resp_apdu;
         } catch (InterruptedIOException var3) {
            this.isClosed = true;
            throw var3;
         }
      }
   }

   public byte[] enablePin(int pinID) throws IOException, InterruptedIOException {
      if (this.isClosed) {
         throw new IOException();
      } else {
         try {
            byte[] resp_apdu = IsaSession.INSTANCE.enablePin(this.getChannelId(), pinID);
            return resp_apdu;
         } catch (InterruptedIOException var3) {
            this.isClosed = true;
            throw var3;
         }
      }
   }

   public byte[] unblockPin(int blockedPinID, int unblockingPinId) throws IOException, InterruptedIOException {
      if (this.isClosed) {
         throw new IOException();
      } else {
         try {
            byte[] resp_apdu = IsaSession.INSTANCE.unblockPin(this.getChannelId(), blockedPinID, unblockingPinId);
            return resp_apdu;
         } catch (InterruptedIOException var4) {
            this.isClosed = true;
            throw var4;
         }
      }
   }

   public byte[] getATR() {
      byte[] atr = null;
      if (!this.isClosed) {
         atr = IsaSession.INSTANCE.getATR(this.getChannelId());
         if (atr == null) {
            this.isClosed = true;
         }
      }

      return atr;
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

   protected byte[] commonExchangeAPDU(byte[] commandAPDU) throws IOException, InterruptedIOException {
      if (this.isClosed) {
         throw new IOException();
      } else {
         try {
            byte[] resp_apdu = IsaSession.INSTANCE.exchangeAPDU(this.getChannelId(), commandAPDU);
            return resp_apdu;
         } catch (InterruptedIOException var3) {
            this.isClosed = true;
            throw var3;
         }
      }
   }

   static void checkFormatApdu(byte[] apdu) {
      if (apdu == null) {
         throw new IllegalArgumentException();
      } else if (apdu.length < 4 || apdu.length > 261) {
         throw new IllegalArgumentException();
      }
   }

   protected static boolean isEnvelope(byte[] apdu) {
      boolean insIsEnvelope = apdu[1] == -62;
      return insIsEnvelope;
   }
}
