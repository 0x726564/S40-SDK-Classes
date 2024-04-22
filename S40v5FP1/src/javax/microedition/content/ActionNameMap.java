package javax.microedition.content;

import com.nokia.mid.impl.isa.content.Util;

public final class ActionNameMap {
   private String locale;
   private String[] localisedActions;
   private String[] actions;

   public ActionNameMap(String[] actions, String[] actionnames, String locale) {
      if (locale != null && actions != null && actionnames != null) {
         if (!Util.hasNullElements(actions) && !Util.hasNullElements(actionnames)) {
            if (actions.length == actionnames.length && actions.length != 0) {
               if (Util.hasValidArrayElements(actions, true) && Util.hasValidArrayElements(actionnames, false)) {
                  if (!Util.isValidMIDPLocale(locale)) {
                     throw new IllegalArgumentException("Locale not compliant MIDP standards");
                  } else {
                     this.locale = locale;
                     this.actions = actions;
                     this.localisedActions = actionnames;
                  }
               } else {
                  throw new IllegalArgumentException("action or actionnames arrays contains invalid values");
               }
            } else {
               throw new IllegalArgumentException("Action and its names must have the same size and it must be != of zero");
            }
         } else {
            throw new NullPointerException();
         }
      } else {
         throw new NullPointerException();
      }
   }

   public String getActionName(String action) {
      if (action == null) {
         throw new NullPointerException();
      } else {
         int pos = Util.findPosition(this.actions, action);
         return pos == -1 ? null : this.localisedActions[pos];
      }
   }

   public String getAction(String actionname) {
      if (actionname == null) {
         throw new NullPointerException();
      } else {
         int pos = Util.findPosition(this.localisedActions, actionname);
         return pos == -1 ? null : this.actions[pos];
      }
   }

   public String getLocale() {
      return this.locale;
   }

   public int size() {
      return this.actions.length;
   }

   public String getAction(int index) {
      return this.actions[index];
   }

   public String getActionName(int index) {
      return this.localisedActions[index];
   }
}
