package com.nokia.mid.impl.isa.io.protocol.external.comm;

import javax.microedition.io.ConnectionNotFoundException;

class CCUriParser {
   protected static final String[] CC_SUPPORTED_PORTS = new String[]{"IR", "COM", "USB"};
   protected static final int SUPPORTED_PORT_IR = 0;
   protected static final int SUPPORTED_PORT_COM = 1;
   protected static final int SUPPORTED_PORT_USB = 2;
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
   private static final char PARAMETER_SEPARATOR = ';';

   private CCUriParser() {
   }

   protected static String validateURI(String cc_uri) throws IllegalArgumentException {
      extractParameters(cc_uri);
      return cc_uri;
   }

   protected static String[] extractURIParameters(String cc_uri) throws IllegalArgumentException {
      return extractParameters(cc_uri);
   }

   protected static int findPortIDIndex(String cc_uri) throws ConnectionNotFoundException {
      for(int i = 0; i < CC_SUPPORTED_PORTS.length; ++i) {
         if (cc_uri.length() >= CC_SUPPORTED_PORTS[i].length() && cc_uri.substring(0, CC_SUPPORTED_PORTS[i].length()).toUpperCase().compareTo(CC_SUPPORTED_PORTS[i]) == 0) {
            return i;
         }
      }

      throw new ConnectionNotFoundException("CommConnection port not recognised: '" + cc_uri + "'.");
   }

   private static String[] extractParameters(String params) throws IllegalArgumentException {
      String[] validParameters = new String[CC_PARAMETER.length];
      if (params.length() == 0) {
         return validParameters;
      } else if (params == null) {
         return validParameters;
      } else {
         boolean var2 = false;

         while(true) {
            if (params != null && params.length() != 0) {
               if (params.charAt(0) == ';' && params.length() != 1) {
                  params = params.substring(1);

                  int paramIndex;
                  for(paramIndex = 0; paramIndex < CC_PARAMETER.length && (CC_PARAMETER[paramIndex].length() > params.length() || !params.startsWith(CC_PARAMETER[paramIndex])); ++paramIndex) {
                  }

                  if (paramIndex == CC_PARAMETER.length) {
                     throw new IllegalArgumentException("Parameter is not recognised: '" + params + "'.");
                  }

                  validParameters[paramIndex] = CC_PARAMETER[paramIndex] + "=";
                  params = params.substring(validParameters[paramIndex].length());
                  switch(paramIndex) {
                  case 0:
                     params = checkAndAddnumber(validParameters, paramIndex, params);
                     continue;
                  case 1:
                     params = checkAndAddString(validParameters, paramIndex, params, CC_PARAM_BITSPRECHAR_BITVALUE);
                     continue;
                  case 2:
                     params = checkAndAddString(validParameters, paramIndex, params, CC_PARAM_STOPBITS_STOP_VALUE);
                     continue;
                  case 3:
                     params = checkAndAddString(validParameters, paramIndex, params, CC_PARAM_PARITY_VALUE);
                     continue;
                  case 4:
                  case 5:
                  case 6:
                     params = checkAndAddString(validParameters, paramIndex, params, CC_PARAM_ON_OFF_VALUE);
                     continue;
                  default:
                     throw new RuntimeException("Unexpected but found parameter while extracting parameters.");
                  }
               }

               throw new IllegalArgumentException("';' expected as separator before start of next parameter, or missing parameter after separator.");
            }

            return validParameters;
         }
      }
   }

   private static String checkAndAddnumber(String[] validParameters, int paramIndex, String params) {
      String number = null;

      try {
         number = extractNumberOnlyString(params);
      } catch (IllegalArgumentException var5) {
         throw new IllegalArgumentException("Cannot extract number for parameter '" + CC_PARAMETER[paramIndex] + "'.");
      }

      validParameters[paramIndex] = validParameters[paramIndex] + number;
      return params.substring(number.length());
   }

   protected static String extractNumberOnlyString(String s) throws IllegalArgumentException {
      if (s != null && s.length() != 0) {
         int numberEndIndex = false;
         int digCount = 0;

         for(int numberEndIndex = 0; numberEndIndex < s.length() && Character.isDigit(s.charAt(numberEndIndex)); ++numberEndIndex) {
            ++digCount;
         }

         if (digCount == 0) {
            throw new IllegalArgumentException();
         } else {
            return s.substring(0, digCount);
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   private static String checkAndAddString(String[] validParameters, int paramIndex, String params, String[] refStrings) {
      if (params != null && params.length() != 0) {
         int refIndex = false;

         int refIndex;
         for(refIndex = 0; refIndex < refStrings.length && (refStrings[refIndex].length() > params.length() || !params.startsWith(refStrings[refIndex])); ++refIndex) {
         }

         if (refIndex == refStrings.length) {
            throw new IllegalArgumentException("Parameter value string is not recognised: '" + params + "'.");
         } else {
            validParameters[paramIndex] = validParameters[paramIndex] + params.substring(0, refStrings[refIndex].length());
            return params.substring(refStrings[refIndex].length());
         }
      } else {
         throw new IllegalArgumentException("Parameter value missing for parameter : '" + CC_PARAMETER[paramIndex] + "'.");
      }
   }
}
