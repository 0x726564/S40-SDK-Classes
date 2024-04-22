package com.nokia.mid.impl.isa.io.protocol.external.comm;

import javax.microedition.io.ConnectionNotFoundException;

class CCUriParser {
   protected static final String[] CC_SUPPORTED_PORTS = new String[]{"IR", "COM"};
   protected static final int SUPPORTED_PORT_IR = 0;
   protected static final int SUPPORTED_PORT_COM = 1;
   protected static final String[] CC_PARAMETER = new String[]{"baudrate", "bitsperchar", "stopbits", "parity", "blocking", "autocts", "autorts"};
   private static final int CC_PARAM_BAUDRATE = 0;
   private static final int CC_PARAM_BITSPRECHAR = 1;
   private static final int CC_PARAM_STOPBITS = 2;
   private static final int CC_PARAM_PARITY = 3;
   private static final int CC_PARAM_BLOCKING = 4;
   private static final int CC_PARAM_AUTOCTS = 5;
   private static final int CC_PARAM_AUTORTS = 6;
   private static final String[] CC_PARAM_BITSPRECHAR_BITVALUE = new String[]{"7", "8"};
   private static final String[] CC_PARAM_STOPBITS_STOP_VALUE = new String[]{"1", "2"};
   private static final String[] CC_PARAM_PARITY_VALUE = new String[]{"even", "odd", "none"};
   private static final String[] CC_PARAM_ON_OFF_VALUE = new String[]{"on", "off"};
   private static final char PARAMETER_SEPERATOR = ';';

   private CCUriParser() {
   }

   protected static String validateURI(String var0) throws IllegalArgumentException {
      extractParameters(var0);
      return var0;
   }

   protected static String[] extractURIParameters(String var0) throws IllegalArgumentException {
      return extractParameters(var0);
   }

   protected static int findPortIDIndex(String var0) throws ConnectionNotFoundException {
      for(int var1 = 0; var1 < CC_SUPPORTED_PORTS.length; ++var1) {
         if (var0.length() >= CC_SUPPORTED_PORTS[var1].length() && var0.substring(0, CC_SUPPORTED_PORTS[var1].length()).toUpperCase().compareTo(CC_SUPPORTED_PORTS[var1]) == 0) {
            return var1;
         }
      }

      throw new ConnectionNotFoundException("CommConnection port not recognised: '" + var0 + "'.");
   }

   private static String[] extractParameters(String var0) throws IllegalArgumentException {
      String[] var1 = new String[CC_PARAMETER.length];
      if (var0.length() == 0) {
         return var1;
      } else if (var0 == null) {
         return var1;
      } else {
         boolean var2 = false;

         while(true) {
            if (var0 != null && var0.length() != 0) {
               if (var0.charAt(0) == ';' && var0.length() != 1) {
                  var0 = var0.substring(1);

                  int var3;
                  for(var3 = 0; var3 < CC_PARAMETER.length && (CC_PARAMETER[var3].length() > var0.length() || !var0.startsWith(CC_PARAMETER[var3])); ++var3) {
                  }

                  if (var3 == CC_PARAMETER.length) {
                     throw new IllegalArgumentException("Parameter is not recognised: '" + var0 + "'.");
                  }

                  var1[var3] = CC_PARAMETER[var3] + "=";
                  var0 = var0.substring(var1[var3].length());
                  switch(var3) {
                  case 0:
                     var0 = checkAndAddnumber(var1, var3, var0);
                     continue;
                  case 1:
                     var0 = checkAndAddString(var1, var3, var0, CC_PARAM_BITSPRECHAR_BITVALUE);
                     continue;
                  case 2:
                     var0 = checkAndAddString(var1, var3, var0, CC_PARAM_STOPBITS_STOP_VALUE);
                     continue;
                  case 3:
                     var0 = checkAndAddString(var1, var3, var0, CC_PARAM_PARITY_VALUE);
                     continue;
                  case 4:
                  case 5:
                  case 6:
                     var0 = checkAndAddString(var1, var3, var0, CC_PARAM_ON_OFF_VALUE);
                     continue;
                  default:
                     throw new RuntimeException("Unexpected but found parameter while extracting parameters.");
                  }
               }

               throw new IllegalArgumentException("';' expected as seperator before start of next parameter, or missing parameter after seperator.");
            }

            return var1;
         }
      }
   }

   private static String checkAndAddnumber(String[] var0, int var1, String var2) {
      String var3 = null;

      try {
         var3 = extractNumberOnlyString(var2);
      } catch (IllegalArgumentException var5) {
         throw new IllegalArgumentException("Cannot extract number for parameter '" + CC_PARAMETER[var1] + "'.");
      }

      var0[var1] = var0[var1] + var3;
      return var2.substring(var3.length());
   }

   protected static String extractNumberOnlyString(String var0) throws IllegalArgumentException {
      if (var0 != null && var0.length() != 0) {
         boolean var1 = false;
         int var2 = 0;

         for(int var3 = 0; var3 < var0.length() && Character.isDigit(var0.charAt(var3)); ++var3) {
            ++var2;
         }

         if (var2 == 0) {
            throw new IllegalArgumentException();
         } else {
            return var0.substring(0, var2);
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   private static String checkAndAddString(String[] var0, int var1, String var2, String[] var3) {
      if (var2 != null && var2.length() != 0) {
         boolean var4 = false;

         int var5;
         for(var5 = 0; var5 < var3.length && (var3[var5].length() > var2.length() || !var2.startsWith(var3[var5])); ++var5) {
         }

         if (var5 == var3.length) {
            throw new IllegalArgumentException("Parameter value string is not recognised: '" + var2 + "'.");
         } else {
            var0[var1] = var0[var1] + var2.substring(0, var3[var5].length());
            return var2.substring(var3[var5].length());
         }
      } else {
         throw new IllegalArgumentException("Parameter value missing for parameter : '" + CC_PARAMETER[var1] + "'.");
      }
   }
}
