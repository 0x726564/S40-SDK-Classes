package com.nokia.mid.impl.jms.core;

import com.nokia.mid.impl.jms.file.File;
import com.nokia.mid.impl.jms.file.FileSystem;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class Explorer {
   public static final byte SEARCH_ALL_MIDLETS = 1;
   public static final byte SEARCH_INSTALLED_MIDLETS = 2;
   public static final byte SEARCH_BROKEN_MIDLETS = 3;
   public static final byte SEARCH_INVALID_MIDLETS = 4;
   private static Explorer m_explorer;

   protected Explorer() {
   }

   public static Explorer getExplorer() {
      if (m_explorer == null) {
         m_explorer = new Explorer();
      }

      return m_explorer;
   }

   public File[] listContents(String folderPath) throws IllegalArgumentException {
      return this.listContents(folderPath, 1);
   }

   public File[] listContents(String folderPath, int midletStatus) throws IllegalArgumentException {
      File[] items = null;
      File folder = File.getFile(folderPath);
      if (!folder.exists()) {
         throw new IllegalArgumentException("The specified file does not exist");
      } else if (midletStatus != 1 && midletStatus != 2 && midletStatus != 3 && midletStatus != 4) {
         throw new IllegalArgumentException("Unknown MIDlet Status " + midletStatus);
      } else {
         File[] children = null;

         try {
            children = folder.listContents(false, "*");
            if (children == null) {
               return null;
            }

            Hashtable tmpStore = new Hashtable(children.length);
            Vector rms_tmpStore = new Vector(children.length);

            MIDletSuite tmpSuite;
            for(int i = 0; i < children.length; ++i) {
               File jadFile = null;
               File jarFile = null;
               if (children[i] != null) {
                  if (children[i].isDirectory()) {
                     tmpStore.put(children[i].getName(), children[i]);
                  } else {
                     int fileType = children[i].getFileType(true);
                     String name = this.getFileNameNoExt(children[i].getName());
                     if (!tmpStore.containsKey(name)) {
                        String jadFileStr;
                        String tmp;
                        switch(fileType) {
                        case 1:
                           jadFile = children[i];
                           children[i] = null;
                           jadFileStr = this.findMatch(children, jadFile);
                           if (jadFileStr == null) {
                              tmp = jadFile.getPath();
                              jadFileStr = tmp.substring(0, tmp.length() - 4) + ".jar";
                           }

                           tmpSuite = MIDletSuite.getMIDletSuite(jadFileStr, jadFile.getPath());
                           tmpStore.put(name, tmpSuite);
                           break;
                        case 2:
                           jarFile = children[i];
                           children[i] = null;
                           jadFileStr = this.findMatch(children, jarFile);
                           if (jadFileStr == null) {
                              tmp = jarFile.getPath();
                              jadFileStr = tmp.substring(0, tmp.length() - 4) + ".jad";
                           }

                           tmpStore.put(name, MIDletSuite.getMIDletSuite(jarFile.getPath(), jadFileStr));
                           break;
                        case 3:
                           rms_tmpStore.addElement(children[i]);
                           children[i] = null;
                        }
                     }
                  }
               }
            }

            Enumeration keysEnum = tmpStore.keys();

            while(keysEnum.hasMoreElements()) {
               String tmpName = (String)keysEnum.nextElement();

               for(int index = rms_tmpStore.size() - 1; index >= 0; --index) {
                  File rmsFile = (File)rms_tmpStore.elementAt(index);
                  if (rmsFile.getName().startsWith(tmpName)) {
                     rms_tmpStore.removeElement(rmsFile);
                  }
               }
            }

            items = new File[tmpStore.size() + rms_tmpStore.size()];
            if (items.length > 0) {
               int i = 0;

               Enumeration enumer;
               for(enumer = tmpStore.elements(); enumer.hasMoreElements(); ++i) {
                  File suite = (File)enumer.nextElement();
                  if (suite instanceof MIDletSuite) {
                     tmpSuite = (MIDletSuite)suite;
                     if (midletStatus == 1) {
                        items[i] = suite;
                     } else if (midletStatus == 3 && tmpSuite.getMIDletStatus() == 8) {
                        items[i] = suite;
                     } else if (midletStatus == 2 && tmpSuite.getMIDletStatus() == 3) {
                        items[i] = suite;
                     } else if (midletStatus == 4 && tmpSuite.getMIDletStatus() == 6) {
                        items[i] = suite;
                     }
                  } else {
                     items[i] = suite;
                  }
               }

               for(enumer = rms_tmpStore.elements(); enumer.hasMoreElements(); ++i) {
                  items[i] = (File)enumer.nextElement();
               }
            }
         } catch (IOException var17) {
            items = null;
         }

         return items;
      }
   }

   /** @deprecated */
   public boolean renameFolder(String oldPath, String newName) throws NullPointerException, IOException, IllegalArgumentException {
      File oldFolder = File.getFile(oldPath);
      if (newName == null) {
         throw new NullPointerException();
      } else if (oldFolder.exists() && oldFolder.isDirectory()) {
         File[] files = oldFolder.listContents(false, "*");
         if (files == null) {
            return oldFolder.rename(newName);
         } else {
            String newPath = oldFolder.getParent() + "\\" + newName;
            int retVal = FileSystem.getFileSystem().rename(oldPath, newPath);
            return retVal == 0;
         }
      } else {
         throw new IllegalArgumentException("The specified folder does not exist");
      }
   }

   private String findMatch(File[] children, File file) {
      for(int i = 0; i < children.length; ++i) {
         if (children[i] != null) {
            String name = children[i].getPath();
            String ext = name.substring(name.length() - 4);
            String name2 = file.getPath();
            String ext2 = name2.substring(name2.length() - 4);
            if (this.getFileNameNoExt(name).equals(this.getFileNameNoExt(name2)) && !ext.equals(ext2)) {
               String path = children[i].getPath();
               children[i] = null;
               return path;
            }
         }
      }

      return null;
   }

   private String getFileNameNoExt(String path) {
      int last_index = path.lastIndexOf(46);
      return last_index > 0 ? path.substring(0, last_index) : path;
   }
}
