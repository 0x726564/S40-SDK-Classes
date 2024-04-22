package com.nokia.mid.impl.isa.content;

import javax.microedition.content.ActionNameMap;
import javax.microedition.content.ContentHandler;

public class ContentHandlerImpl implements ContentHandler {
   private String id;
   private String appName;
   private String version;
   private String authority;
   private String[] types;
   private String[] suffixes;
   private String[] actions;
   private ActionNameMap[] actionNameMaps;
   private String className;
   private int midletId;
   private boolean nativeHandler;

   public String getAction(int index) {
      return this.actions[index];
   }

   public int getActionCount() {
      return this.actions.length;
   }

   public boolean hasAction(String action) {
      if (action == null) {
         throw new NullPointerException();
      } else {
         int pos = Util.findPositionCaseSensitive(this.actions, action);
         return pos != -1;
      }
   }

   public ActionNameMap getActionNameMap() {
      return this.getActionNameMap(System.getProperty("microedition.locale"));
   }

   public ActionNameMap getActionNameMap(String locale) {
      if (locale == null) {
         throw new NullPointerException("The given locale is null");
      } else if (!Util.isValidMIDPLocale(locale)) {
         return null;
      } else if (locale.trim().length() == 0) {
         return null;
      } else {
         ActionNameMap ret = this.matchAvailableLocales(locale);
         if (ret == null) {
            int lastdash = locale.lastIndexOf(45);
            if (lastdash >= 0) {
               ret = this.matchAvailableLocales(locale.substring(0, lastdash));
            }
         }

         return ret;
      }
   }

   private ActionNameMap matchAvailableLocales(String locale) {
      if (this.actionNameMaps == null) {
         return null;
      } else {
         for(int i = 0; i < this.actionNameMaps.length; ++i) {
            if (this.actionNameMaps[i].getLocale().equals(locale)) {
               return this.actionNameMaps[i];
            }

            if (this.actionNameMaps[i].getLocale().startsWith(locale)) {
               return this.actionNameMaps[i];
            }
         }

         return null;
      }
   }

   public ActionNameMap getActionNameMap(int i) {
      if (this.actionNameMaps == null) {
         throw new ArrayIndexOutOfBoundsException();
      } else {
         return this.actionNameMaps[i];
      }
   }

   public int getActionNameMapCount() {
      return this.actionNameMaps == null ? 0 : this.actionNameMaps.length;
   }

   public boolean hasSuffix(String suffix) {
      if (suffix == null) {
         throw new NullPointerException();
      } else {
         return Util.findPosition(this.suffixes, suffix) != -1;
      }
   }

   public String getSuffix(int i) {
      return this.suffixes[i];
   }

   public int getSuffixCount() {
      return this.suffixes.length;
   }

   public boolean hasType(String type) {
      if (type == null) {
         throw new NullPointerException();
      } else {
         return Util.findPosition(this.types, type) != -1;
      }
   }

   public String getType(int i) {
      return this.types[i];
   }

   public int getTypeCount() {
      return this.types.length;
   }

   public String getAppName() {
      return this.appName;
   }

   public String getAuthority() {
      return this.authority;
   }

   public String getID() {
      return this.id;
   }

   public String getVersion() {
      return this.version;
   }

   String getClassName() {
      return this.className;
   }

   long getMIDletId() {
      return (long)this.midletId;
   }

   String[] getActions() {
      return this.actions;
   }

   String[] getSuffixes() {
      return this.suffixes;
   }

   String[] getTypes() {
      return this.types;
   }

   boolean isNativeHandler() {
      return this.nativeHandler;
   }
}
