package javax.microedition.media.control;

import javax.microedition.media.Control;

public interface MetaDataControl extends Control {
   String AUTHOR_KEY = "author";
   String COPYRIGHT_KEY = "copyright";
   String DATE_KEY = "date";
   String TITLE_KEY = "title";

   String[] getKeys();

   String getKeyValue(String var1);
}
