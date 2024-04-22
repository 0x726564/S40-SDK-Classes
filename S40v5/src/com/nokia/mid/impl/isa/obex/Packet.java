package com.nokia.mid.impl.isa.obex;

public class Packet {
   public static final int CONNECT = 128;
   public static final int DISCONNECT = 129;
   public static final int PUT = 2;
   public static final int GET = 3;
   public static final int SETPATH = 133;
   public static final int ABORT = 255;
   protected byte[] serializedHeaders;
   public boolean authenticationChallenge;
   public String realm;
   public boolean userIdRequired;
   public boolean isFullAccess;
   public boolean authenticationResponse;
   public byte[] userName;
   public byte[] password;
   public byte[] myNonce;
   public byte[] itsAuthChallengeNonce;
   public byte[] itsAuthResponseNonce;
   public byte[] digest;
   public int packetSort;
   public boolean isResp;
   public int respCode;
   public boolean isFinal = false;
   public boolean isSetPathBackup;
   public boolean isSetPathCreate;
   public int maxPacketLength;

   public static Packet decode(byte[] var0, int var1) {
      Packet var2;
      if ((var2 = decodePacket0(var0, var1)) == null) {
         return null;
      } else {
         if (var2.serializedHeaders != null && var2.serializedHeaders.length > 1) {
            var2.serializedHeaders[0] = var0[0];
         }

         var2.respCode = var0[0] & 255;
         if ((var0[0] & 128) == 128) {
            var2.isFinal = true;
         }

         var2.packetSort = var0[0] & 255;
         if (var2.packetSort == 130) {
            var2.packetSort = 2;
         } else if (var2.packetSort == 131) {
            var2.packetSort = 3;
         }

         return var2;
      }
   }

   public static byte[] encode(Packet var0) {
      return encodePacket0(var0);
   }

   private static native Packet decodePacket0(byte[] var0, int var1);

   private static native byte[] encodePacket0(Packet var0);

   public static native boolean IsItsDigestValid(byte[] var0, byte[] var1, byte[] var2, byte[] var3);
}
